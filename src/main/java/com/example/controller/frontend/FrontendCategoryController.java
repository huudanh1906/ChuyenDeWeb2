package com.example.controller.frontend;

import com.example.entity.Category;
import com.example.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/frontend/categories")
@CrossOrigin(origins = "*")
public class FrontendCategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Get all active categories
     */
    @GetMapping
    public ResponseEntity<List<Category>> index() {
        List<Category> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get parent categories (categories with parentId = 0)
     */
    @GetMapping("/parents")
    public ResponseEntity<List<Category>> getParentCategories() {
        List<Category> parentCategories = categoryService.getAllParentCategories();
        return ResponseEntity.ok(parentCategories);
    }

    /**
     * Get child categories by parent ID
     */
    @GetMapping("/parent/{parentId}/children")
    public ResponseEntity<List<Category>> getChildCategories(@PathVariable int parentId) {
        List<Category> childCategories = categoryService.getChildCategoriesByParentId(parentId);
        return ResponseEntity.ok(childCategories);
    }

    /**
     * Get category by slug
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getCategoryBySlug(@PathVariable String slug) {
        Optional<Category> category = categoryService.getCategoryBySlug(slug);
        if (category.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Category not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(category.get());
    }
}