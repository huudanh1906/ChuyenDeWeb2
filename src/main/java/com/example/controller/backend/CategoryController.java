package com.example.controller.backend;

import com.example.entity.Category;
import com.example.service.CategoryService;
import com.example.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.ConstraintViolationException;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Category>> index() {
        List<Category> categories = categoryService.getAllNonTrashedCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam(value = "slug", required = false) String slug,
            @RequestParam(value = "parent_id", required = false, defaultValue = "0") int parentId,
            @RequestParam(value = "sort_order", required = false, defaultValue = "0") int sortOrder,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false, defaultValue = "2") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication) {

        try {
            Category category = new Category();
            category.setName(name);

            // Nếu slug không được cung cấp, sẽ tự động tạo từ tên
            if (slug == null || slug.isEmpty()) {
                slug = categoryService.generateSlug(name);
            }
            category.setSlug(slug);

            category.setParentId(parentId);
            category.setSortOrder(sortOrder);
            category.setDescription(description);
            category.setStatus(status);

            // Set created_by từ người dùng đã xác thực
            if (authentication != null) {
                // Giả sử bạn có thể lấy ID người dùng từ authentication
                // Điều này phụ thuộc vào cách bạn đã cấu hình xác thực
                // Đây chỉ là một ví dụ
                category.setCreatedBy(1); // Giá trị mặc định hoặc lấy từ authentication
            } else {
                category.setCreatedBy(1); // Mặc định nếu không có authentication
            }

            Category savedCategory = categoryService.createCategory(category, imageFile);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Category created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            logger.error("Failed to upload image: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (IllegalArgumentException e) {
            // Bắt các lỗi với tham số không hợp lệ
            logger.error("Invalid argument: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Invalid argument: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (ConstraintViolationException e) {
            // Bắt các lỗi validation
            logger.error("Validation error: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Validation error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error("Error creating category: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(category.get());
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "slug", required = false) String slug,
            @RequestParam(value = "parent_id", required = false, defaultValue = "0") int parentId,
            @RequestParam(value = "sort_order", required = false, defaultValue = "0") int sortOrder,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false, defaultValue = "2") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageBase64", required = false) String imageBase64,
            Authentication authentication) {

        try {
            Optional<Category> optionalCategory = categoryService.getCategoryById(id);
            if (optionalCategory.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Category categoryDetails = new Category();
            categoryDetails.setName(name);

            // Nếu slug không được cung cấp, sẽ tự động tạo từ tên
            if (slug == null || slug.isEmpty()) {
                slug = categoryService.generateSlug(name);
            }
            categoryDetails.setSlug(slug);

            categoryDetails.setParentId(parentId);
            categoryDetails.setSortOrder(sortOrder);
            categoryDetails.setDescription(description);
            categoryDetails.setStatus(status);

            // Set updated_by từ người dùng đã xác thực
            if (authentication != null) {
                // Giả sử bạn có thể lấy ID người dùng từ authentication
                // Điều này phụ thuộc vào cách bạn đã cấu hình xác thực
                // Đây chỉ là một ví dụ
                categoryDetails.setUpdatedBy(1); // Giá trị mặc định hoặc lấy từ authentication
            }

            Category updatedCategory;

            // Use different update methods based on what's provided
            if (imageFile != null && !imageFile.isEmpty()) {
                updatedCategory = categoryService.updateCategory(id, categoryDetails, imageFile);
            } else if (imageBase64 != null && !imageBase64.isEmpty()) {
                updatedCategory = categoryService.updateCategoryWithBase64Image(id, categoryDetails, imageBase64);
            } else {
                // No image updates, use the standard method
                updatedCategory = categoryService.updateCategory(id, categoryDetails, null);
            }

            if (updatedCategory != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Category updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update category");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IOException e) {
            logger.error("Failed to upload image: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage());
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean isDeleted = categoryService.deleteCategory(id);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Category deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the specified resource between active (1) and inactive
     * (2).
     * If the category is in trashed state (0), the status won't be changed.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        // Kiểm tra xem category có tồn tại không
        Optional<Category> categoryOptional = categoryService.getCategoryById(id);
        if (categoryOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Kiểm tra status hiện tại
        Category category = categoryOptional.get();
        int currentStatus = category.getStatus();

        // Toggle status
        Category updatedCategory = categoryService.toggleCategoryStatus(id);
        if (updatedCategory != null) {
            int newStatus = updatedCategory.getStatus();
            Map<String, String> response = new HashMap<>();

            // Xử lý trường hợp category đang ở trạng thái trashed
            if (currentStatus == 0 && newStatus == 0) {
                response.put("message", "Category is in trash. Restore it first to change status.");
            } else {
                response.put("message", "Category status updated successfully");
            }

            response.put("status", String.valueOf(newStatus));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Category deletedCategory = categoryService.softDeleteCategory(id);
        if (deletedCategory != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Category moved to trash successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of trashed resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Category>> trash() {
        List<Category> trashedCategories = categoryService.getAllTrashedCategories();
        return ResponseEntity.ok(trashedCategories);
    }

    /**
     * Restore the specified resource from trash.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Category restoredCategory = categoryService.restoreCategory(id);
        if (restoredCategory != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Category restored successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}