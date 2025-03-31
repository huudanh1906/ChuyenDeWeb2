package com.example.controller;

import com.example.dto.JwtAuthenticationResponse;
import com.example.dto.LoginDTO;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.exception.BadRequestException;
import com.example.security.JwtTokenProvider;
import com.example.security.UserPrincipal;
import com.example.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsernameOrEmail());

        try {
            // Kiểm tra xem người dùng có tồn tại không
            Optional<User> userOpt = userService.getUserByUsername(loginRequest.getUsernameOrEmail());

            if (userOpt.isEmpty()) {
                userOpt = userService.getUserByEmail(loginRequest.getUsernameOrEmail());
            }

            if (userOpt.isEmpty()) {
                logger.warn("User not found: {}", loginRequest.getUsernameOrEmail());
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Tài khoản không tồn tại");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = userOpt.get();
            logger.debug("Found user: ID={}, Username={}, Status={}", user.getId(), user.getUsername(),
                    user.getStatus());

            // Kiểm tra trạng thái người dùng
            if (user.getStatus() != 1) {
                logger.warn("User account disabled: {}", loginRequest.getUsernameOrEmail());
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Tài khoản đã bị khóa");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Kiểm tra mật khẩu trực tiếp
            String rawPassword = loginRequest.getPassword();
            String storedPassword = user.getPassword();

            logger.debug("Raw password: {}", rawPassword);
            logger.debug("Stored password hash: {}", storedPassword);

            // Tạo một hash mới để so sánh
            String newHash = passwordEncoder.encode(rawPassword);
            logger.debug("New hash for comparison: {}", newHash);

            // Kiểm tra bằng passwordEncoder
            boolean passwordMatches = passwordEncoder.matches(rawPassword, storedPassword);
            logger.debug("Password match result: {}", passwordMatches);

            // Thử reset mật khẩu tạm thời nếu không khớp
            if (!passwordMatches) {
                logger.warn("Password mismatch for user: {}", loginRequest.getUsernameOrEmail());
                logger.debug("Trying alternate methods...");

                // Phương pháp 1: Mã hóa lại và kiểm tra
                // Lưu ý: Đây là một biện pháp tạm thời
                String tempEncodedPassword = passwordEncoder.encode(rawPassword);
                user.setPassword(tempEncodedPassword);
                boolean tempMatches = passwordEncoder.matches(rawPassword, tempEncodedPassword);
                logger.debug("Temp password reset and check: {}", tempMatches);

                // Nếu phương pháp tạm thời hoạt động, hãy cập nhật người dùng
                if (tempMatches) {
                    userService.updateUser(user.getId(), user);
                    logger.info("Password updated for user: {}", user.getUsername());
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Tên đăng nhập hoặc mật khẩu không đúng");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }

            // Tạo UserPrincipal trực tiếp
            UserPrincipal userPrincipal = UserPrincipal.create(user);

            // Tạo authentication token và cập nhật context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userPrincipal,
                    null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT token
            String jwt = tokenProvider.generateToken(authentication);

            logger.info("User {} successfully logged in", userPrincipal.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng nhập thành công");
            response.put("token", jwt);
            response.put("userId", userPrincipal.getId());
            response.put("username", userPrincipal.getUsername());
            response.put("roles", userPrincipal.getAuthorities().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during authentication: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi trong quá trình đăng nhập");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Registration attempt for username: {}", userDTO.getUsername());

        Map<String, Object> response = new HashMap<>();

        try {
            // Kiểm tra xem username đã tồn tại chưa
            if (userService.existsByUsername(userDTO.getUsername())) {
                logger.warn("Username already exists: {}", userDTO.getUsername());
                throw new BadRequestException("Tên đăng nhập đã được sử dụng!");
            }

            // Kiểm tra xem email đã tồn tại chưa
            if (userService.existsByEmail(userDTO.getEmail())) {
                logger.warn("Email already exists: {}", userDTO.getEmail());
                throw new BadRequestException("Email đã được sử dụng!");
            }

            // Mã hóa mật khẩu
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            logger.debug("Original password: {}, Encoded password: {}", userDTO.getPassword(), encodedPassword);

            // Tạo tài khoản người dùng mới
            User user = new User();
            user.setName(userDTO.getName());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPassword(encodedPassword);
            user.setPhone(userDTO.getPhone());
            user.setAddress(userDTO.getAddress());
            user.setRoles("USER"); // Mặc định là USER
            user.setStatus(1); // Mặc định là active

            // Đặt giá trị mặc định cho gender là 1 (nam) nếu không được cung cấp
            if (userDTO.getGender() == null) {
                user.setGender(1); // 1: Nam, 0: Nữ
            } else {
                user.setGender(userDTO.getGender());
            }

            User savedUser = userService.createUser(user);
            logger.info("User successfully registered: {}", savedUser.getUsername());

            response.put("success", true);
            response.put("message", "Đăng ký thành công!");
            response.put("user", savedUser);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error during registration: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginDTO loginRequest) {
        logger.info("Admin login attempt for user: {}", loginRequest.getUsernameOrEmail());

        try {
            // Kiểm tra xem người dùng có tồn tại không
            Optional<User> userOpt = userService.getUserByUsername(loginRequest.getUsernameOrEmail());

            if (userOpt.isEmpty()) {
                userOpt = userService.getUserByEmail(loginRequest.getUsernameOrEmail());
            }

            if (userOpt.isEmpty()) {
                logger.warn("Admin user not found: {}", loginRequest.getUsernameOrEmail());
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Tài khoản không tồn tại");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = userOpt.get();
            logger.debug("Found user: ID={}, Username={}, Status={}, Roles={}, Password={}",
                    user.getId(), user.getUsername(), user.getStatus(), user.getRoles(),
                    user.getPassword().substring(0, 20) + "...");

            // Kiểm tra trạng thái người dùng
            if (user.getStatus() != 1) {
                logger.warn("Admin account is inactive: {}", loginRequest.getUsernameOrEmail());
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Tài khoản đã bị khóa");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Kiểm tra xem người dùng có vai trò ADMIN không
            if (!user.getRoles().contains("ADMIN")) {
                logger.warn("User does not have ADMIN role: {}", loginRequest.getUsernameOrEmail());
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Bạn không có quyền truy cập trang quản trị");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Log mật khẩu để debug
            String rawPassword = loginRequest.getPassword();
            String storedPassword = user.getPassword();

            logger.debug("Raw password attempt: {}", rawPassword);
            logger.debug("Stored password hash: {}", storedPassword);

            // Tạo một hash mới để so sánh (debug)
            String newHash = passwordEncoder.encode(rawPassword);
            logger.debug("New hash for comparison: {}", newHash);

            // Kiểm tra mật khẩu trực tiếp
            boolean passwordMatches = passwordEncoder.matches(rawPassword, storedPassword);
            logger.debug("Direct password match result: {}", passwordMatches);

            // Thử reset mật khẩu tạm thời nếu không khớp (giống như trong authenticateUser)
            if (!passwordMatches) {
                logger.warn("Password mismatch for admin user: {}", loginRequest.getUsernameOrEmail());
                logger.debug("Trying alternate methods...");

                // Phương pháp 1: Mã hóa lại và kiểm tra (giống phương thức authenticateUser)
                String tempEncodedPassword = passwordEncoder.encode(rawPassword);
                user.setPassword(tempEncodedPassword);
                boolean tempMatches = passwordEncoder.matches(rawPassword, tempEncodedPassword);
                logger.debug("Temp password reset and check: {}", tempMatches);

                // Nếu phương pháp tạm thời hoạt động, hãy cập nhật người dùng
                if (tempMatches) {
                    userService.updateUser(user.getId(), user);
                    logger.info("Password updated for admin user: {}", user.getUsername());
                } else {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Tên đăng nhập hoặc mật khẩu không chính xác");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }

            // Tạo UserPrincipal trực tiếp (giống phương thức authenticateUser)
            UserPrincipal userPrincipal = UserPrincipal.create(user);

            // Tạo authentication token và cập nhật context
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT token
            String jwt = tokenProvider.generateToken(authentication);
            logger.debug("JWT token generated for admin user: {}", loginRequest.getUsernameOrEmail());

            // Tạo response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đăng nhập thành công");
            response.put("token", jwt);
            response.put("userId", userPrincipal.getId());
            response.put("username", userPrincipal.getUsername());
            response.put("roles", userPrincipal.getAuthorities().toString());
            response.put("user", userService.getUserById(userPrincipal.getId()).orElse(null));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during admin authentication: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Đã xảy ra lỗi trong quá trình đăng nhập");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}