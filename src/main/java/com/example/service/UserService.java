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
            logger.warn("Attempt to login with deactivated account: {}", usernameOrEmail);
            throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
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
            logger.warn("Attempt to authenticate with deactivated account ID: {}", id);
            throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
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
     * Get all trashed users
     */
    public List<User> getAllTrashedUsers() {
        return userRepository.findAll().stream()
                .filter(user -> user.getStatus() == 0)
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

        if (userOpt.isEmpty() || userOpt.get().getStatus() != 1) {
            logger.warn("User not found or inactive: {}", username);
            throw new RuntimeException("Tài khoản không tồn tại hoặc đã bị khóa");
        }

        User user = userOpt.get();

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
            String password, MultipartFile imageFile) throws IOException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

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
        user.setUpdatedAt(new Date());

        // Update password if provided
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = saveImage(imageFile);
            user.setImage(fileName);
        }

        return userRepository.save(user);
    }

    /**
     * Update user profile with base64 image
     */
    public User updateProfileWithBase64Image(Long id, String name, String phone, String email, String address,
            String password, String imageBase64) throws IOException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();

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
        user.setUpdatedAt(new Date());

        // Update password if provided
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        // Handle base64 image if provided
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            String fileName = saveBase64Image(imageBase64);
            user.setImage(fileName);
        }

        return userRepository.save(user);
    }

    /**
     * Delete user
     */
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete user (set status to 0)
     */
    public User softDeleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        user.setStatus(0);
        user.setUpdatedAt(new Date());

        return userRepository.save(user);
    }

    /**
     * Restore user (set status to 1)
     */
    public User restoreUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        user.setStatus(1);
        user.setUpdatedAt(new Date());

        return userRepository.save(user);
    }

    /**
     * Toggle user status
     */
    public User toggleUserStatus(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        user.setStatus(user.getStatus() == 1 ? 0 : 1);
        user.setUpdatedAt(new Date());

        return userRepository.save(user);
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        // Create uploads directory if it doesn't exist
        String uploadDir = "uploads/users";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = imageFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(imageFile.getInputStream(), filePath);

        return fileName;
    }

    private String saveBase64Image(String base64String) throws IOException {
        // Create uploads directory if it doesn't exist
        String uploadDir = "uploads/users";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Clean up the base64 string
        String[] parts = base64String.split(",");
        String imageString = parts.length > 1 ? parts[1] : parts[0];

        // Determine file extension from the data URL
        String extension = ".jpg"; // Default to jpg
        if (parts.length > 1 && parts[0].contains("image/")) {
            String mimeType = parts[0].split(":")[1].split(";")[0];
            if (mimeType.equals("image/png")) {
                extension = ".png";
            } else if (mimeType.equals("image/gif")) {
                extension = ".gif";
            }
        }

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + extension;

        // Decode and save the image
        byte[] imageBytes = Base64.getDecoder().decode(imageString);
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, imageBytes);

        return fileName;
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}