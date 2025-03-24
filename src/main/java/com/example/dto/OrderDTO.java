package com.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object cho Order entity
 * Dùng cho cả hiển thị, tạo mới và cập nhật đơn hàng
 */
public class OrderDTO {
    private Long id;
    private Long userId; // Optional for guest checkout

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 15, message = "Phone number should not exceed 15 characters")
    @Pattern(regexp = "^[0-9]*$", message = "Phone number should contain only digits")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    private String note;
    private Date createdAt;
    private Date updatedAt;
    private int status = 1; // Default to active

    // Information about the user
    private String username;

    // For order details
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderDetailDTO> orderDetails;

    // Order total (calculated from order details)
    private Double orderTotal;

    // Flag để xác định DTO được sử dụng cho mục đích gì
    private transient boolean isCreateOperation;
    private transient boolean isUpdateOperation;

    // Constructors
    public OrderDTO() {
    }

    /**
     * Constructor cho việc tạo đơn hàng mới
     */
    public static OrderDTO forCreate() {
        OrderDTO dto = new OrderDTO();
        dto.isCreateOperation = true;
        return dto;
    }

    /**
     * Constructor cho việc cập nhật đơn hàng
     */
    public static OrderDTO forUpdate() {
        OrderDTO dto = new OrderDTO();
        dto.isUpdateOperation = true;
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<OrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public boolean isCreateOperation() {
        return isCreateOperation;
    }

    public boolean isUpdateOperation() {
        return isUpdateOperation;
    }
}