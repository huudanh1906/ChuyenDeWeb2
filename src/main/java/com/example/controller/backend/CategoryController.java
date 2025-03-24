package com.example.controller.backend;

import com.example.entity.Category;
import com.example.service.CategoryService;
import com.example.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Category>> index() {
        List<Category> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam("slug") String slug,
            @RequestParam("parent_id") Long parentId,
            @RequestParam("sort_order") Integer sortOrder,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Category category = new Category();
            category.setName(name);
            category.setSlug(slug);
            category.setParentId(parentId);
            category.setSortOrder(sortOrder);
            category.setMetakey(metakey);
            category.setMetadesc(metadesc);
            category.setStatus(status);

            categoryService.createCategory(category, imageFile);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Category created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
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
            @RequestParam("slug") String slug,
            @RequestParam("parent_id") Long parentId,
            @RequestParam("sort_order") Integer sortOrder,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageBase64", required = false) String imageBase64) {

        try {
            Optional<Category> optionalCategory = categoryService.getCategoryById(id);
            if (optionalCategory.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Category categoryDetails = new Category();
            categoryDetails.setName(name);
            categoryDetails.setSlug(slug);
            categoryDetails.setParentId(parentId);
            categoryDetails.setSortOrder(sortOrder);
            categoryDetails.setMetakey(metakey);
            categoryDetails.setMetadesc(metadesc);
            categoryDetails.setStatus(status);

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
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
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
     * Toggle the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        Category updatedCategory = categoryService.toggleCategoryStatus(id);
        if (updatedCategory != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Category status updated successfully");
            response.put("status", String.valueOf(updatedCategory.getStatus()));
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