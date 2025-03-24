package com.example.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Date;

/**
 * Data Transfer Object for User entity
 * Dùng cho cả hiển thị, tạo mới và cập nhật người dùng
 */
public class UserDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(max = 15, message = "Phone number should not exceed 15 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number should contain only digits")
    private String phone;

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String username;

    private String password; // Khi tạo mới cần mật khẩu, khi hiển thị sẽ bỏ qua trường này

    private String address;
    private String image;
    private String roles;
    private Date createdAt;
    private Date updatedAt;
    private int status;

    // Flag để xác định DTO được sử dụng cho mục đích gì
    private transient boolean isCreateOperation;
    private transient boolean isUpdateOperation;

    // Constructors
    public UserDTO() {
    }

    /**
     * Constructor cho việc tạo người dùng mới
     */
    public static UserDTO forCreate() {
        UserDTO dto = new UserDTO();
        dto.isCreateOperation = true;
        return dto;
    }

    /**
     * Constructor cho việc cập nhật người dùng
     */
    public static UserDTO forUpdate() {
        UserDTO dto = new UserDTO();
        dto.isUpdateOperation = true;
        return dto;
    }

    // Validation methods
    public boolean requiresPassword() {
        return isCreateOperation || (password != null && !password.isEmpty());
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isCreateOperation() {
        return isCreateOperation;
    }

    public boolean isUpdateOperation() {
        return isUpdateOperation;
    }
}