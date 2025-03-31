package com.example.controller.frontend;

import com.example.entity.User;
import com.example.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/frontend/users")
@CrossOrigin(origins = "*")
public class FrontendUserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        // Validate request
        Map<String, String> errors = validateRegistration(request);
        if (!errors.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("errors", errors);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
        }

        // Check if username or email already exists
        if (userRepository.findByUsername(request.get("username").toString()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Username already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        if (userRepository.findByEmail(request.get("email").toString()).isPresent()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            User user = new User();
            user.setName(request.get("name").toString());
            user.setPhone(request.get("phone").toString());
            user.setEmail(request.get("email").toString());
            user.setAddress(request.get("address").toString());
            user.setPassword(hashPassword(request.get("password").toString()));
            user.setUsername(request.get("username").toString());

            // Set gender with default value 1 (male) if not provided
            Integer gender = 1; // Mặc định là nam
            if (request.containsKey("gender") && request.get("gender") != null) {
                gender = Integer.parseInt(request.get("gender").toString());
            }
            user.setGender(gender);

            user.setRoles("customer");
            user.setCreatedAt(new Date());
            user.setCreatedBy(1);
            user.setStatus(1);

            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error registering user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Login a user
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, Object> request) {
        String username = request.get("username").toString();
        String password = request.get("password").toString();

        Optional<User> userOpt = userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username) && user.getStatus() == 1)
                .findFirst();

        if (userOpt.isEmpty() || !verifyPassword(password, userOpt.get().getPassword())) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = userOpt.get();

        // Generate a token (in a real app, use JWT or other token mechanism)
        String token = generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout a user
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        // In a real application, you would invalidate the token
        // Here we just return a success message

        Map<String, String> response = new HashMap<>();
        response.put("message", "User logged out successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Get user for editing
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(userOpt.get());
    }

    /**
     * Update a user
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("phone") String phone,
            @RequestParam("email") String email,
            @RequestParam("address") String address,
            @RequestParam(value = "gender", required = false) Integer gender,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "image", required = false) String imageBase64) {

        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = userOpt.get();

        // Check if email exists for another user
        Optional<User> emailUser = userRepository.findByEmail(email);
        if (emailUser.isPresent() && !emailUser.get().getId().equals(id)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email already in use by another user");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        try {
            user.setName(name);
            user.setPhone(phone);
            user.setEmail(email);
            user.setAddress(address);
            if (gender != null) {
                user.setGender(gender);
            }

            // Update password if provided
            if (password != null && !password.isEmpty()) {
                user.setPassword(hashPassword(password));
            }

            // Process image if provided
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                processImageAndUpdateUser(user, imageBase64);
            }

            user.setRoles("customer");
            user.setStatus(1);
            user.setUpdatedAt(new Date());
            user.setUpdatedBy(1);

            userRepository.save(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Update user successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Helper methods
     */
    private Map<String, String> validateRegistration(Map<String, Object> request) {
        Map<String, String> errors = new HashMap<>();

        if (request.get("name") == null || request.get("name").toString().trim().isEmpty()) {
            errors.put("name", "Name is required");
        }

        if (request.get("phone") == null || request.get("phone").toString().trim().isEmpty()) {
            errors.put("phone", "Phone is required");
        }

        if (request.get("email") == null || request.get("email").toString().trim().isEmpty()) {
            errors.put("email", "Email is required");
        } else if (!isValidEmail(request.get("email").toString())) {
            errors.put("email", "Invalid email format");
        }

        if (request.get("address") == null || request.get("address").toString().trim().isEmpty()) {
            errors.put("address", "Address is required");
        }

        if (request.get("password") == null || request.get("password").toString().trim().isEmpty()) {
            errors.put("password", "Password is required");
        } else if (request.get("password").toString().length() < 6) {
            errors.put("password", "Password must be at least 6 characters");
        }

        if (request.get("username") == null || request.get("username").toString().trim().isEmpty()) {
            errors.put("username", "Username is required");
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());

            // Convert to hex string
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

    private String generateToken(User user) {
        // In a real application, use JWT or other token mechanism
        // This is a simple placeholder
        return "token_" + user.getId() + "_" + System.currentTimeMillis();
    }

    private void processImageAndUpdateUser(User user, String imageBase64) throws Exception {
        // Remove the data:image prefix if present
        if (imageBase64.contains(",")) {
            imageBase64 = imageBase64.split(",")[1];
        }

        // Decode base64 string
        byte[] imageBytes = Base64.getDecoder().decode(imageBase64);

        // Create directory if it doesn't exist
        File uploadDir = new File("src/main/resources/static/imgs/users");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate unique filename
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".png";

        // Save the image
        Path path = Paths.get(uploadDir.getAbsolutePath(), fileName);
        Files.write(path, imageBytes);

        // Update user with new image
        user.setImage(fileName);
    }
}