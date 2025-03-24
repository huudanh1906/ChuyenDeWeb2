package com.example.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Data Transfer Object cho OrderDetail entity
 * Dùng cho cả hiển thị, tạo mới và cập nhật chi tiết đơn hàng
 */
public class OrderDetailDTO {
    private Long id;
    private Long orderId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int qty;

    // Additional product information for display
    private String productName;
    private String productImage;
    private BigDecimal itemTotal; // price * qty

    // Flag để xác định DTO được sử dụng cho mục đích gì
    private transient boolean isCreateOperation;
    private transient boolean isUpdateOperation;

    // Constructors
    public OrderDetailDTO() {
    }

    /**
     * Constructor cho việc tạo chi tiết đơn hàng mới
     */
    public static OrderDetailDTO forCreate() {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.isCreateOperation = true;
        return dto;
    }

    /**
     * Constructor cho việc tạo chi tiết đơn hàng mới với dữ liệu có sẵn
     */
    public static OrderDetailDTO forCreate(Long productId, int qty) {
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.isCreateOperation = true;
        dto.productId = productId;
        dto.qty = qty;
        return dto;
    }

    /**
     * Constructor cho việc cập nhật chi tiết đơn hàng
     */
    public static OrderDetailDTO forUpdate() {
        OrderDetailDTO dto = new OrderDetailDTO();
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public BigDecimal getItemTotal() {
        if (price != null && qty > 0) {
            return price.multiply(BigDecimal.valueOf(qty));
        }
        return BigDecimal.ZERO;
    }

    public void setItemTotal(BigDecimal itemTotal) {
        // This is mostly a placeholder as the value is calculated from price and qty
        // But we allow setting it explicitly if needed
        this.itemTotal = itemTotal;
    }

    public boolean isCreateOperation() {
        return isCreateOperation;
    }

    public boolean isUpdateOperation() {
        return isUpdateOperation;
    }
}