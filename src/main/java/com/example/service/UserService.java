package com.example.service;

import com.example.entity.User;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.UserRepository;
import com.example.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Phương thức từ UserDetailsService - dùng cho Spring Security
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        logger.debug("Loading user by username or email: {}", usernameOrEmail);

        // Let users login with either username or email
        Optional<User> userOpt = userRepository.findByUsername(usernameOrEmail);

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(usernameOrEmail);
        }

        if (userOpt.isEmpty()) {
            logger.error("User not found with username or email: {}", usernameOrEmail);
            throw new UsernameNotFoundException(
                    "Người dùng không tồn tại với tên đăng nhập hoặc email: " + usernameOrEmail);
        }

        User user = userOpt.get();

        logger.debug("User found: ID={}, Username={}, Status={}, Password={}",
                user.getId(), user.getUsername(), user.getStatus(), user.getPassword().substring(0, 10) + "...");

        if (user.getStatus() != 1) {
            if (user.getStatus() == 0) {
                logger.warn("Attempt to login with deleted account: {}", usernameOrEmail);
                throw new UsernameNotFoundException("Tài khoản đã bị xóa");
            } else if (user.getStatus() == 2) {
                logger.warn("Attempt to login with deactivated account: {}", usernameOrEmail);
                throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
            } else {
                logger.warn("Attempt to login with account having unknown status: {}", usernameOrEmail);
                throw new UsernameNotFoundException("Tài khoản có trạng thái không hợp lệ");
            }
        }

        return UserPrincipal.create(user);
    }

    /**
     * Phương thức dùng cho JWTAuthenticationFilter
     */
    @Transactional
    public UserDetails loadUserById(Long id) {
        logger.debug("Loading user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User", "id", id);
                });

        logger.debug("User found by ID: Username={}, Status={}", user.getUsername(), user.getStatus());

        if (user.getStatus() != 1) {
            if (user.getStatus() == 0) {
                logger.warn("Attempt to authenticate with deleted account ID: {}", id);
                throw new UsernameNotFoundException("Tài khoản đã bị xóa");
            } else if (user.getStatus() == 2) {
                logger.warn("Attempt to authenticate with deactivated account ID: {}", id);
                throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
            } else {
                logger.warn("Attempt to authenticate with account having unknown status ID: {}", id);
                throw new UsernameNotFoundException("Tài khoản có trạng thái không hợp lệ");
            }
        }

        return UserPrincipal.create(user);
    }

    /**
     * Get all active users
     */
    public List<User> getAllActiveUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == 1)
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all active and inactive users (không bao gồm users trong thùng rác)
     */
    public List<User> getAllNonTrashedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() != 0) // status 1 (active) hoặc 2 (inactive)
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all users regardless of status
     */
    public List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all trashed users
     */
    public List<User> getAllTrashedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == 0) // chỉ lấy users có status = 0 (trashed)
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all inactive (but not trashed) users
     */
    public List<User> getAllInactiveUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == 2) // chỉ lấy users có status = 2 (inactive)
                .sorted(Comparator.comparing(User::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Get user by username
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Get user by email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Create a new user
     */
    public User createUser(User user) {
        user.setCreatedAt(new Date());
        user.setStatus(1);

        // Encode password using Spring Security's PasswordEncoder
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Đặt giá trị mặc định cho gender nếu chưa có
        if (user.getGender() == null) {
            user.setGender(1); // 1: Nam, 0: Nữ
        }

        return userRepository.save(user);
    }

    /**
     * Register a new user
     */
    public User registerUser(String name, String username, String email, String password, String phone,
            String address) {
        // Check if username or email already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setAddress(address);
        user.setRoles("customer");
        user.setStatus(1);
        user.setGender(1); // Mặc định là 1 (nam)
        user.setCreatedAt(new Date());
        user.setCreatedBy(1); // Default system user

        return userRepository.save(user);
    }

    /**
     * Login with username and password
     */
    public User login(String username, String password) {
        logger.debug("Login attempt for username: {}", username);

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(username);
        }

        if (userOpt.isEmpty()) {
            logger.warn("User not found: {}", username);
            throw new RuntimeException("Tài khoản không tồn tại");
        }

        User user = userOpt.get();

        // Kiểm tra trạng thái user
        if (user.getStatus() != 1) {
            if (user.getStatus() == 0) {
                logger.warn("Attempt to login with deleted account: {}", username);
                throw new RuntimeException("Tài khoản đã bị xóa");
            } else if (user.getStatus() == 2) {
                logger.warn("Attempt to login with inactive account: {}", username);
                throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
            } else {
                logger.warn("Attempt to login with account having unknown status: {}", username);
                throw new RuntimeException("Tài khoản có trạng thái không hợp lệ");
            }
        }

        logger.debug("Found user: {}, checking password", user.getUsername());

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Invalid password for user: {}", username);
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        logger.info("User logged in successfully: {}", username);
        return user;
    }

    /**
     * Update user
     */
    public User updateUser(Long id, User userDetails) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // Check if email is being used by another user
        Optional<User> emailUser = userRepository.findByEmail(userDetails.getEmail());
        if (emailUser.isPresent() && !emailUser.get().getId().equals(id)) {
            throw new RuntimeException("Email already in use by another user");
        }

        // Update user fields
        user.setName(userDetails.getName());
        user.setPhone(userDetails.getPhone());
        user.setEmail(userDetails.getEmail());
        user.setAddress(userDetails.getAddress());
        user.setRoles(userDetails.getRoles());
        user.setStatus(userDetails.getStatus());
        user.setUpdatedAt(new Date());

        // Update password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        return userRepository.save(user);
    }

    /**
     * Update user profile
     */
    public User updateProfile(Long id, String name, String phone, String email, String address,
            String password, Integer gender, MultipartFile imageFile) throws IOException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // Lưu tên ảnh cũ trước khi cập nhật
        String oldImageFileName = user.getImage();
        logger.info("User ID {} updating profile. Old image file name: {}", id, oldImageFileName);

        // Check if email is being used by another user
        Optional<User> emailUser = userRepository.findByEmail(email);
        if (emailUser.isPresent() && !emailUser.get().getId().equals(id)) {
            throw new RuntimeException("Email already in use by another user");
        }

        // Update user fields
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAddress(address);
        user.setGender(gender);
        user.setUpdatedAt(new Date());

        // Update password if provided
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            logger.info("New image file provided for user ID {}: {}", id, imageFile.getOriginalFilename());
            String fileName = saveImage(imageFile);
            logger.info("New image saved with filename: {}", fileName);
            user.setImage(fileName);

            // Xóa ảnh cũ sau khi lưu ảnh mới thành công
            if (oldImageFileName != null && !oldImageFileName.isEmpty()) {
                logger.info("Attempting to delete old image: {}", oldImageFileName);
                boolean deleted = fileStorageService.deleteFile(oldImageFileName, "users");
                logger.info("Old image deletion result: {}", deleted);
            } else {
                logger.info("No old image to delete for user ID {}", id);
            }
        } else {
            logger.info("No new image file provided for user ID {}", id);
        }

        User savedUser = userRepository.save(user);
        logger.info("User profile updated successfully for ID {}", id);
        return savedUser;
    }

    /**
     * Update user profile with base64 image
     */
    public User updateProfileWithBase64Image(Long id, String name, String phone, String email, String address,
            String password, Integer gender, String imageBase64) throws IOException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // Lưu tên ảnh cũ trước khi cập nhật
        String oldImageFileName = user.getImage();
        logger.info("User ID {} updating profile with base64 image. Old image file name: {}", id, oldImageFileName);

        // Check if email is being used by another user
        Optional<User> emailUser = userRepository.findByEmail(email);
        if (emailUser.isPresent() && !emailUser.get().getId().equals(id)) {
            throw new RuntimeException("Email already in use by another user");
        }

        // Update user fields
        user.setName(name);
        user.setPhone(phone);
        user.setEmail(email);
        user.setAddress(address);
        user.setGender(gender);
        user.setUpdatedAt(new Date());

        // Update password if provided
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        // Handle base64 image if provided
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            logger.info("New base64 image provided for user ID {}", id);
            String fileName = saveBase64Image(imageBase64);
            logger.info("New base64 image saved with filename: {}", fileName);
            user.setImage(fileName);

            // Xóa ảnh cũ sau khi lưu ảnh mới thành công
            if (oldImageFileName != null && !oldImageFileName.isEmpty()) {
                logger.info("Attempting to delete old image: {}", oldImageFileName);
                boolean deleted = fileStorageService.deleteFile(oldImageFileName, "users");
                logger.info("Old image deletion result: {}", deleted);
            } else {
                logger.info("No old image to delete for user ID {}", id);
            }
        } else {
            logger.info("No new base64 image provided for user ID {}", id);
        }

        User savedUser = userRepository.save(user);
        logger.info("User profile updated successfully for ID {}", id);
        return savedUser;
    }

    /**
     * Delete user
     */
    public boolean deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            return false;
        }

        // Lấy thông tin user trước khi xóa để lấy tên file ảnh
        User user = userOptional.get();
        String imageFileName = user.getImage();

        // Xóa hình ảnh liên quan nếu có
        if (imageFileName != null && !imageFileName.isEmpty()) {
            logger.info("Deleting image file for user ID {}: {}", id, imageFileName);
            boolean imageDeleted = fileStorageService.deleteFile(imageFileName, "users");
            logger.info("Image deletion result: {}", imageDeleted);
        }

        // Xóa user khỏi database
        userRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete user (set status to 0 - trashed)
     */
    public User softDeleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        user.setStatus(0); // 0: Trashed
        user.setUpdatedAt(new Date());

        return userRepository.save(user);
    }

    /**
     * Restore user (set status to 1 - active)
     */
    public User restoreUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        user.setStatus(1); // 1: Active
        user.setUpdatedAt(new Date());

        return userRepository.save(user);
    }

    /**
     * Toggle user status between active (1) and inactive (2)
     */
    public User toggleUserStatus(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

        // Chỉ toggle giữa trạng thái active (1) và inactive (2)
        // Nếu user đang ở trạng thái trashed (0), thì sẽ đặt là active (1)
        if (user.getStatus() == 1) {
            user.setStatus(2); // 2: Inactive
        } else {
            user.setStatus(1); // 1: Active
        }

        user.setUpdatedAt(new Date());

        return userRepository.save(user);
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        // Sử dụng FileStorageService thay vì lưu trực tiếp
        logger.info("Delegating image save to FileStorageService from UserService.saveImage()");
        return fileStorageService.saveFile(imageFile, "users");
    }

    private String saveBase64Image(String base64String) throws IOException {
        // Sử dụng FileStorageService thay vì lưu trực tiếp
        logger.info("Delegating base64 image save to FileStorageService from UserService.saveBase64Image()");
        return fileStorageService.saveBase64Image(base64String, "users");
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}