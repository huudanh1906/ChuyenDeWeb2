package com.example.service;

import com.example.entity.Brand;
import com.example.repository.BrandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrandService {

    private static final Logger logger = LoggerFactory.getLogger(BrandService.class);

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all non-trashed brands (status != 0)
     * Includes both active (status=1) and inactive (status=2) brands
     */
    public List<Brand> getAllNonTrashedBrands() {
        return brandRepository.findAll().stream()
                .filter(brand -> brand.getStatus() != 0) // Status != 0 (not trashed)
                .sorted(Comparator.comparing(Brand::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    // Keep the old method name as a wrapper for backward compatibility
    /**
     * @deprecated Use getAllNonTrashedBrands() instead
     */
    @Deprecated
    public List<Brand> getAllActiveBrands() {
        return getAllNonTrashedBrands();
    }

    /**
     * Get all trashed brands
     */
    public List<Brand> getAllTrashedBrands() {
        return brandRepository.findAll().stream()
                .filter(brand -> brand.getStatus() == 0)
                .sorted(Comparator.comparing(Brand::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get brand by ID
     */
    public Optional<Brand> getBrandById(Long id) {
        return brandRepository.findById(id);
    }

    /**
     * Get brand by slug
     */
    public Optional<Brand> getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug);
    }

    /**
     * Create a new brand
     */
    public Brand createBrand(Brand brand, MultipartFile imageFile) throws IOException {
        // Generate slug from name if not provided
        if (brand.getSlug() == null || brand.getSlug().isEmpty()) {
            brand.setSlug(generateSlug(brand.getName()));
        }

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "brands");
            brand.setImage(fileName);
        }

        brand.setCreatedAt(new Date());
        return brandRepository.save(brand);
    }

    /**
     * Update an existing brand
     */
    public Brand updateBrand(Long id, Brand brandDetails, MultipartFile imageFile) throws IOException {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();

        // Lưu tên file ảnh cũ trước khi cập nhật
        String oldImageFileName = brand.getImage();
        logger.info("Brand ID {} updating. Old image file name: {}", id, oldImageFileName);

        // Cập nhật thông tin brand
        brand.setName(brandDetails.getName());
        brand.setSlug(brandDetails.getSlug());
        brand.setDescription(brandDetails.getDescription());
        brand.setSortOrder(brandDetails.getSortOrder());
        brand.setStatus(brandDetails.getStatus());
        brand.setUpdatedAt(new Date());

        // Set updatedBy if provided
        if (brandDetails.getUpdatedBy() > 0) {
            brand.setUpdatedBy(brandDetails.getUpdatedBy());
        }

        // Xử lý tải lên hình ảnh nếu được cung cấp
        if (imageFile != null && !imageFile.isEmpty()) {
            logger.info("New image file provided for brand ID {}: {}", id, imageFile.getOriginalFilename());
            String fileName = fileStorageService.saveFile(imageFile, "brands");
            logger.info("New image saved with filename: {}", fileName);
            brand.setImage(fileName);

            // Xóa ảnh cũ sau khi lưu ảnh mới thành công
            if (oldImageFileName != null && !oldImageFileName.isEmpty()) {
                logger.info("Attempting to delete old image: {}", oldImageFileName);
                boolean deleted = fileStorageService.deleteFile(oldImageFileName, "brands");
                logger.info("Old image deletion result: {}", deleted);
            } else {
                logger.info("No old image to delete for brand ID {}", id);
            }
        }

        return brandRepository.save(brand);
    }

    /**
     * Update an existing brand with explicit old image information
     */
    public Brand updateBrandWithOldImage(Long id, Brand brandDetails, MultipartFile imageFile, String oldImage)
            throws IOException {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();

        // Lưu tên file ảnh cũ
        String oldImageFileName = oldImage != null ? oldImage : brand.getImage();
        logger.info("Brand ID {} updating with explicit old image. Old image file name: {}", id, oldImageFileName);

        // Cập nhật thông tin brand
        brand.setName(brandDetails.getName());
        brand.setSlug(brandDetails.getSlug());
        brand.setDescription(brandDetails.getDescription());
        brand.setSortOrder(brandDetails.getSortOrder());
        brand.setStatus(brandDetails.getStatus());
        brand.setUpdatedAt(new Date());

        // Set updatedBy if provided
        if (brandDetails.getUpdatedBy() > 0) {
            brand.setUpdatedBy(brandDetails.getUpdatedBy());
        }

        // Xử lý tải lên hình ảnh nếu được cung cấp
        if (imageFile != null && !imageFile.isEmpty()) {
            logger.info("New image file provided for brand ID {}: {}", id, imageFile.getOriginalFilename());
            String fileName = fileStorageService.saveFile(imageFile, "brands");
            logger.info("New image saved with filename: {}", fileName);
            brand.setImage(fileName);

            // Xóa ảnh cũ sau khi lưu ảnh mới thành công
            if (oldImageFileName != null && !oldImageFileName.isEmpty()) {
                logger.info("Attempting to delete old image: {}", oldImageFileName);
                boolean deleted = fileStorageService.deleteFile(oldImageFileName, "brands");
                logger.info("Old image deletion result: {}", deleted);
            } else {
                logger.info("No old image to delete for brand ID {}", id);
            }
        }

        return brandRepository.save(brand);
    }

    /**
     * Update brand with base64 encoded image
     */
    public Brand updateBrandWithBase64Image(Long id, Brand brandDetails, String imageBase64) throws IOException {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();

        // Lưu tên file ảnh cũ trước khi cập nhật
        String oldImageFileName = brand.getImage();
        logger.info("Brand ID {} updating with base64 image. Old image file name: {}", id, oldImageFileName);

        // Cập nhật thông tin brand
        brand.setName(brandDetails.getName());
        brand.setSlug(brandDetails.getSlug());
        brand.setDescription(brandDetails.getDescription());
        brand.setSortOrder(brandDetails.getSortOrder());
        brand.setStatus(brandDetails.getStatus());
        brand.setUpdatedAt(new Date());

        // Set updatedBy if provided
        if (brandDetails.getUpdatedBy() > 0) {
            brand.setUpdatedBy(brandDetails.getUpdatedBy());
        }

        // Xử lý ảnh base64 nếu được cung cấp
        if (imageBase64 != null && !imageBase64.isEmpty() && imageBase64.contains("base64")) {
            logger.info("New base64 image provided for brand ID {}", id);
            String fileName = fileStorageService.saveBase64Image(imageBase64, "brands");
            logger.info("New base64 image saved with filename: {}", fileName);
            brand.setImage(fileName);

            // Xóa ảnh cũ sau khi lưu ảnh mới thành công
            if (oldImageFileName != null && !oldImageFileName.isEmpty()) {
                logger.info("Attempting to delete old image: {}", oldImageFileName);
                boolean deleted = fileStorageService.deleteFile(oldImageFileName, "brands");
                logger.info("Old image deletion result: {}", deleted);
            } else {
                logger.info("No old image to delete for brand ID {}", id);
            }
        }

        return brandRepository.save(brand);
    }

    /**
     * Delete brand
     */
    public boolean deleteBrand(Long id) {
        Optional<Brand> brandOptional = brandRepository.findById(id);
        if (brandOptional.isEmpty()) {
            return false;
        }

        // Lấy thông tin brand trước khi xóa để lấy tên file ảnh
        Brand brand = brandOptional.get();
        String imageFileName = brand.getImage();

        // Xóa hình ảnh liên quan nếu có
        if (imageFileName != null && !imageFileName.isEmpty()) {
            logger.info("Deleting image file for brand ID {}: {}", id, imageFileName);
            boolean imageDeleted = fileStorageService.deleteFile(imageFileName, "brands");
            logger.info("Image deletion result: {}", imageDeleted);
        }

        // Xóa brand khỏi database
        brandRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete brand (set status to 0)
     */
    public Brand softDeleteBrand(Long id, Integer userId) {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();
        brand.setStatus(0);
        brand.setUpdatedAt(new Date());

        // Set updated_by if userId is provided
        if (userId != null) {
            brand.setUpdatedBy(userId);
        }

        return brandRepository.save(brand);
    }

    /**
     * Restore soft-deleted brand
     */
    public Brand restoreBrand(Long id, Integer userId) {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();
        brand.setStatus(1);
        brand.setUpdatedAt(new Date());

        // Set updated_by if userId is provided
        if (userId != null) {
            brand.setUpdatedBy(userId);
        }

        return brandRepository.save(brand);
    }

    /**
     * Toggle brand status between active (1) and inactive (2)
     * Won't change if status is trashed (0)
     */
    public Brand toggleBrandStatus(Long id, Integer userId) {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();

        // Only toggle between active (1) and inactive (2)
        // If status is 0 (trashed), don't change it
        if (brand.getStatus() != 0) {
            brand.setStatus(brand.getStatus() == 1 ? 2 : 1);
            brand.setUpdatedAt(new Date());

            // Set updated_by if userId is provided
            if (userId != null) {
                brand.setUpdatedBy(userId);
            }
        }

        return brandRepository.save(brand);
    }

    /**
     * Generate a slug from a string
     * Public method for controllers to use
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
        while (brandRepository.findBySlug(slug).isPresent()) {
            count++;
            slug = originalSlug + "-" + count;
        }

        return slug;
    }

    /**
     * Get only truly active brands (status = 1)
     * For frontend use
     */
    public List<Brand> getOnlyActiveBrands() {
        return brandRepository.findAll().stream()
                .filter(brand -> brand.getStatus() == 1) // Only status = 1 (active)
                .sorted(Comparator.comparing(Brand::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}