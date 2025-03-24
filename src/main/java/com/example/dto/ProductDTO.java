package com.example.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Data Transfer Object cho Product entity
 * Dùng cho cả hiển thị, tạo mới và cập nhật sản phẩm
 */
public class ProductDTO {
    private Long id;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Brand ID is required")
    private Long brandId;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    private BigDecimal priceSale;

    private String image;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int qty;

    private String detail;
    private String metakey;
    private String metadesc;
    private Date createdAt;
    private int status = 1; // Default to active

    // Fields for related entity information (useful for list views)
    private String categoryName;
    private String brandName;

    // Flag để xác định DTO được sử dụng cho mục đích gì
    private transient boolean isCreateOperation;
    private transient boolean isUpdateOperation;

    // Constructors
    public ProductDTO() {
    }

    /**
     * Constructor cho việc tạo sản phẩm mới
     */
    public static ProductDTO forCreate() {
        ProductDTO dto = new ProductDTO();
        dto.isCreateOperation = true;
        return dto;
    }

    /**
     * Constructor cho việc cập nhật sản phẩm
     */
    public static ProductDTO forUpdate() {
        ProductDTO dto = new ProductDTO();
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceSale() {
        return priceSale;
    }

    public void setPriceSale(BigDecimal priceSale) {
        this.priceSale = priceSale;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getMetakey() {
        return metakey;
    }

    public void setMetakey(String metakey) {
        this.metakey = metakey;
    }

    public String getMetadesc() {
        return metadesc;
    }

    public void setMetadesc(String metadesc) {
        this.metadesc = metadesc;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public boolean isCreateOperation() {
        return isCreateOperation;
    }

    public boolean isUpdateOperation() {
        return isUpdateOperation;
    }
}