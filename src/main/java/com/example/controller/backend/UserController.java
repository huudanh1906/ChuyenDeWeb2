package com.example.controller.backend;

import com.example.entity.User;
import com.example.service.UserService;
import com.example.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<User>> index() {
        List<User> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("roles") String roles,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Check if username already exists
            Optional<User> existingUsername = userService.getUserByUsername(username);
            if (existingUsername.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Username already exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Check if email already exists
            Optional<User> existingEmail = userService.getUserByEmail(email);
            if (existingEmail.isPresent()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Email already exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            User user = new User();
            user.setName(name);
            user.setUsername(username);
            user.setPassword(password); // Will be hashed in the service
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            user.setRoles(roles);
            user.setStatus(status);
            user.setCreatedBy(1); // Default to system user (replace with actual authentication)

            // Handle image upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = fileStorageService.saveFile(imageFile, "users");
                user.setImage(fileName);
            }

            userService.createUser(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(user.get());
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("roles") String roles,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageBase64", required = false) String imageBase64) {

        try {
            Optional<User> optionalUser = userService.getUserById(id);
            if (optionalUser.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Check if email already exists for another user
            Optional<User> existingEmail = userService.getUserByEmail(email);
            if (existingEmail.isPresent() && !existingEmail.get().getId().equals(id)) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Email already exists for another user");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            User userDetails = new User();
            userDetails.setName(name);
            userDetails.setUsername(username);
            userDetails.setPassword(password);
            userDetails.setEmail(email);
            userDetails.setPhone(phone);
            userDetails.setAddress(address);
            userDetails.setRoles(roles);
            userDetails.setStatus(status);
            userDetails.setUpdatedBy(1); // Replace with actual authentication logic for user ID

            // Handle image upload
            String imageName = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                imageName = fileStorageService.saveFile(imageFile, "users");
            } else if (imageBase64 != null && !imageBase64.isEmpty() && imageBase64.contains("base64")) {
                imageName = fileStorageService.saveBase64Image(imageBase64, "users");
            }

            if (imageName != null) {
                userDetails.setImage(imageName);
            }

            User updatedUser = userService.updateUser(id, userDetails);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User updated successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean isDeleted = userService.deleteUser(id);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        User updatedUser = userService.toggleUserStatus(id);
        if (updatedUser != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User status updated successfully");
            response.put("status", String.valueOf(updatedUser.getStatus()));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        User deletedUser = userService.softDeleteUser(id);
        if (deletedUser != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User moved to trash successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of trashed resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<User>> trash() {
        List<User> trashedUsers = userService.getAllTrashedUsers();
        return ResponseEntity.ok(trashedUsers);
    }

    /**
     * Restore the specified resource from trash.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        User restoredUser = userService.restoreUser(id);
        if (restoredUser != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "User restored successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get the profile of the authenticated user.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> profile() {
        // Mock getting the authenticated user - replace with actual authentication
        Optional<User> user = userService.getUserById(1L);
        if (user.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(user.get());
    }

    /**
     * Update the profile of the authenticated user.
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Mock getting the authenticated user ID - replace with actual authentication
            Long userId = 1L;

            User updatedUser = userService.updateProfile(userId, name, phone, email, address, password, imageFile);

            if (updatedUser != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "Profile updated successfully");
                response.put("user", updatedUser);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update profile");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}