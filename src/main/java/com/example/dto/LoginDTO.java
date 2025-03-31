package com.example.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object cho đăng nhập
 */
public class LoginDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String usernameOrEmail;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    // Constructors
    public LoginDTO() {
    }

    public LoginDTO(String usernameOrEmail, String password) {
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
    }

    // Getters and Setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }

    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "usernameOrEmail='" + usernameOrEmail + '\'' +
                ", password='" + "[PROTECTED]" + '\'' +
                '}';
    }
}