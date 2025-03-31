package com.example.service;

import com.example.entity.Category;
import com.example.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

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
     * Get all non-trashed categories (status != 0)
     * Includes both active (status=1) and inactive (status=2) categories
     */
    public List<Category> getAllNonTrashedCategories() {
        return categoryRepository.findAll().stream()
                .filter(category -> category.getStatus() != 0) // Status != 0 (not trashed)
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

        // Lưu tên file ảnh cũ trước khi cập nhật
        String oldImageFileName = category.getImage();
        logger.info("Category ID {} updating. Old image file name: {}", id, oldImageFileName);

        // Cập nhật thông tin category
        category.setName(categoryDetails.getName());
        category.setSlug(categoryDetails.getSlug());
        category.setParentId(categoryDetails.getParentId());
        category.setSortOrder(categoryDetails.getSortOrder());
        category.setDescription(categoryDetails.getDescription());
        category.setStatus(categoryDetails.getStatus());
        category.setUpdatedAt(new Date());

        // Set updatedBy if provided
        if (categoryDetails.getUpdatedBy() != null) {
            category.setUpdatedBy(categoryDetails.getUpdatedBy());
        }

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            logger.info("New image file provided for category ID {}: {}", id, imageFile.getOriginalFilename());
            String fileName = fileStorageService.saveFile(imageFile, "categories");
            logger.info("New image saved with filename: {}", fileName);
            category.setImage(fileName);

            // Xóa ảnh cũ sau khi lưu ảnh mới thành công
            if (oldImageFileName != null && !oldImageFileName.isEmpty()) {
                logger.info("Attempting to delete old image: {}", oldImageFileName);
                boolean deleted = fileStorageService.deleteFile(oldImageFileName, "categories");
                logger.info("Old image deletion result: {}", deleted);
            } else {
                logger.info("No old image to delete for category ID {}", id);
            }
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

        // Lưu tên file ảnh cũ trước khi cập nhật
        String oldImageFileName = category.getImage();
        logger.info("Category ID {} updating with base64 image. Old image file name: {}", id, oldImageFileName);

        // Cập nhật thông tin category
        category.setName(categoryDetails.getName());
        category.setSlug(categoryDetails.getSlug());
        category.setParentId(categoryDetails.getParentId());
        category.setSortOrder(categoryDetails.getSortOrder());
        category.setDescription(categoryDetails.getDescription());
        category.setStatus(categoryDetails.getStatus());
        category.setUpdatedAt(new Date());

        // Set updatedBy if provided
        if (categoryDetails.getUpdatedBy() != null) {
            category.setUpdatedBy(categoryDetails.getUpdatedBy());
        }

        // Handle base64 image if provided
        if (imageBase64 != null && !imageBase64.isEmpty() && imageBase64.contains("base64")) {
            logger.info("New base64 image provided for category ID {}", id);
            String fileName = fileStorageService.saveBase64Image(imageBase64, "categories");
            logger.info("New base64 image saved with filename: {}", fileName);
            category.setImage(fileName);

            // Xóa ảnh cũ sau khi lưu ảnh mới thành công
            if (oldImageFileName != null && !oldImageFileName.isEmpty()) {
                logger.info("Attempting to delete old image: {}", oldImageFileName);
                boolean deleted = fileStorageService.deleteFile(oldImageFileName, "categories");
                logger.info("Old image deletion result: {}", deleted);
            } else {
                logger.info("No old image to delete for category ID {}", id);
            }
        }

        return categoryRepository.save(category);
    }

    /**
     * Delete category
     */
    public boolean deleteCategory(Long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            return false;
        }

        // Lấy thông tin category trước khi xóa để lấy tên file ảnh
        Category category = categoryOptional.get();
        String imageFileName = category.getImage();

        // Xóa hình ảnh liên quan nếu có
        if (imageFileName != null && !imageFileName.isEmpty()) {
            logger.info("Deleting image file for category ID {}: {}", id, imageFileName);
            boolean imageDeleted = fileStorageService.deleteFile(imageFileName, "categories");
            logger.info("Image deletion result: {}", imageDeleted);
        }

        // Xóa category khỏi database
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
     * Toggle category status between active (1) and inactive (2)
     * Won't change if status is trashed (0)
     */
    public Category toggleCategoryStatus(Long id) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return null;
        }

        Category category = optionalCategory.get();
        int oldStatus = category.getStatus();

        // Chỉ toggle giữa trạng thái active (1) và inactive (2)
        // Nếu status là 0 (trashed), sẽ không thay đổi
        if (oldStatus == 0) {
            return category; // Trả về không thay đổi nếu là trashed
        } else {
            int newStatus = oldStatus == 1 ? 2 : 1;
            category.setStatus(newStatus);
            category.setUpdatedAt(new Date());
            return categoryRepository.save(category);
        }
    }

    /**
     * Get all parent categories (parentId = 0)
     */
    public List<Category> getAllParentCategories() {
        return getAllNonTrashedCategories().stream()
                .filter(category -> category.getParentId() == 0)
                .collect(Collectors.toList());
    }

    /**
     * Get child categories by parentId
     */
    public List<Category> getChildCategoriesByParentId(int parentId) {
        return getAllNonTrashedCategories().stream()
                .filter(category -> category.getParentId() == parentId)
                .collect(Collectors.toList());
    }

    /**
     * Generate a slug from a string
     */
    public String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Chuyển đổi thành chữ thường và thay thế các ký tự đặc biệt bằng dấu gạch
        // ngang
        String slug = input.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "") // Loại bỏ các ký tự đặc biệt trừ dấu gạch ngang
                .replaceAll("\\s+", "-") // Thay thế khoảng trắng bằng dấu gạch ngang
                .replaceAll("-+", "-") // Loại bỏ nhiều dấu gạch ngang liên tiếp
                .replaceAll("^-|-$", ""); // Loại bỏ dấu gạch ngang ở đầu và cuối

        // Kiểm tra xem slug đã tồn tại chưa
        int count = 0;
        String originalSlug = slug;
        while (categoryRepository.findBySlug(slug).isPresent()) {
            count++;
            slug = originalSlug + "-" + count;
        }

        return slug;
    }
}