package com.example.controller.frontend;

import com.example.entity.Category;
import com.example.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
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
}