package com.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;

/**
 * Data Transfer Object cho Post entity
 * Dùng cho cả hiển thị, tạo mới và cập nhật bài viết
 */
public class PostDTO {
    private Long id;

    @NotNull(message = "Topic ID is required")
    private Long topicId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotBlank(message = "Detail is required")
    private String detail;

    private String image;

    @NotBlank(message = "Type is required")
    private String type;

    private String metakey;
    private String metadesc;
    private Date createdAt;
    private Date updatedAt;
    private int status = 1; // Default to active

    // Fields for related entity information
    private String topicName;

    // Flag để xác định DTO được sử dụng cho mục đích gì
    private transient boolean isCreateOperation;
    private transient boolean isUpdateOperation;

    // Constructors
    public PostDTO() {
    }

    /**
     * Constructor cho việc tạo bài viết mới
     */
    public static PostDTO forCreate() {
        PostDTO dto = new PostDTO();
        dto.isCreateOperation = true;
        return dto;
    }

    /**
     * Constructor cho việc cập nhật bài viết
     */
    public static PostDTO forUpdate() {
        PostDTO dto = new PostDTO();
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

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public boolean isCreateOperation() {
        return isCreateOperation;
    }

    public boolean isUpdateOperation() {
        return isUpdateOperation;
    }
}