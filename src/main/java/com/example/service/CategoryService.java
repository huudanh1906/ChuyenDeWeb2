package com.example.service;

import com.example.entity.Category;
import com.example.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all active categories
     */
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getStatus() == 1)
                .sorted(Comparator.comparing(Category::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all trashed categories
     */
    public List<Category> getAllTrashedCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getStatus() == 0)
                .sorted(Comparator.comparing(Category::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get category by ID
     */
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Get category by slug
     */
    public Optional<Category> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    /**
     * Create a new category
     */
    public Category createCategory(Category category, MultipartFile imageFile) throws IOException {
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "categories");
            category.setImage(fileName);
        }

        category.setCreatedAt(new Date());
        return categoryRepository.save(category);
    }

    /**
     * Update an existing category
     */
    public Category updateCategory(Long id, Category categoryDetails, MultipartFile imageFile) throws IOException {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return null;
        }

        Category category = optionalCategory.get();
        category.setName(categoryDetails.getName());
        category.setSlug(categoryDetails.getSlug());
        category.setParentId(categoryDetails.getParentId());
        category.setStatus(categoryDetails.getStatus());
        category.setUpdatedAt(new Date());

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "categories");
            category.setImage(fileName);
        }

        return categoryRepository.save(category);
    }

    /**
     * Update category with base64 image
     */
    public Category updateCategoryWithBase64Image(Long id, Category categoryDetails, String imageBase64)
            throws IOException {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return null;
        }

        Category category = optionalCategory.get();
        category.setName(categoryDetails.getName());
        category.setSlug(categoryDetails.getSlug());
        category.setParentId(categoryDetails.getParentId());
        category.setStatus(categoryDetails.getStatus());
        category.setUpdatedAt(new Date());

        // Handle base64 image if provided
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            String fileName = fileStorageService.saveBase64Image(imageBase64, "categories");
            category.setImage(fileName);
        }

        return categoryRepository.save(category);
    }

    /**
     * Delete category
     */
    public boolean deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }

        categoryRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete category (set status to 0)
     */
    public Category softDeleteCategory(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return null;
        }

        Category category = optionalCategory.get();
        category.setStatus(0);
        category.setUpdatedAt(new Date());

        return categoryRepository.save(category);
    }

    /**
     * Restore soft-deleted category
     */
    public Category restoreCategory(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return null;
        }

        Category category = optionalCategory.get();
        category.setStatus(1);
        category.setUpdatedAt(new Date());

        return categoryRepository.save(category);
    }

    /**
     * Toggle category status
     */
    public Category toggleCategoryStatus(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return null;
        }

        Category category = optionalCategory.get();
        category.setStatus(category.getStatus() == 1 ? 2 : 1);
        category.setUpdatedAt(new Date());

        return categoryRepository.save(category);
    }

    /**
     * Get all parent categories (parentId = 0)
     */
    public List<Category> getAllParentCategories() {
        return getAllActiveCategories().stream()
                .filter(category -> category.getParentId() == 0)
                .collect(Collectors.toList());
    }

    /**
     * Get child categories by parentId
     */
    public List<Category> getChildCategoriesByParentId(Long parentId) {
        return getAllActiveCategories().stream()
                .filter(category -> category.getParentId().equals(parentId))
                .collect(Collectors.toList());
    }
}