package com.example.controller.backend;

import com.example.entity.Brand;
import com.example.security.UserPrincipal;
import com.example.service.BrandService;
import com.example.service.FileStorageService;
import com.example.repository.BrandRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "*")
public class BrandController {

    private static final Logger logger = LoggerFactory.getLogger(BrandController.class);

    @Autowired
    private BrandService brandService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private BrandRepository brandRepository;

    /**
     * Display a listing of all non-trashed brands (status != 0).
     * This includes both active (status=1) and inactive (status=2) brands.
     */
    @GetMapping
    public ResponseEntity<List<Brand>> index() {
        List<Brand> brands = brandService.getAllNonTrashedBrands();
        return ResponseEntity.ok(brands);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam(value = "slug", required = false) String slug,
            @RequestParam(value = "sort_order", required = false, defaultValue = "0") int sortOrder,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "status", required = false) Integer statusParam,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            Authentication authentication) {

        try {
            Brand brand = new Brand();
            brand.setName(name);

            // If slug is null, it will be generated in the service
            brand.setSlug(slug);
            brand.setSortOrder(sortOrder);
            brand.setDescription(description);

            // Set default status if not provided
            int status = (statusParam != null) ? statusParam : 2;
            brand.setStatus(status);

            // Set created_by from authenticated user
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                brand.setCreatedBy(userPrincipal.getId().intValue());
            }

            brandService.createBrand(brand, imageFile);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Brand created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating brand: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Brand> brand = brandService.getBrandById(id);
        if (brand.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Brand not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(brand.get());
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "slug", required = false) String slug,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "sort_order", required = false, defaultValue = "0") int sortOrder,
            @RequestParam(value = "status", required = false, defaultValue = "1") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "old_image", required = false) String oldImage,
            Authentication authentication) {

        try {
            // Extract user ID from authentication object
            Long userId = 0L;
            if (authentication != null && authentication.getPrincipal() instanceof com.example.security.UserPrincipal) {
                userId = ((com.example.security.UserPrincipal) authentication.getPrincipal()).getId();
            }

            logger.info("Updating brand ID {} with old_image parameter: {}", id, oldImage);

            Optional<Brand> optionalBrand = brandService.getBrandById(id);
            if (optionalBrand.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Brand not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Brand brand = new Brand();
            brand.setName(name);

            // Generate slug if not provided
            if (slug == null || slug.isEmpty()) {
                // Sử dụng generateSlug từ brandService để tạo slug từ tên
                slug = brandService.generateSlug(name);
            }
            brand.setSlug(slug);

            brand.setDescription(description);
            brand.setSortOrder(sortOrder);
            brand.setStatus(status);
            brand.setUpdatedBy(userId.intValue());

            // Update brand using the method that handles old image deletion
            Brand updatedBrand;
            if (oldImage != null && !oldImage.isEmpty()) {
                // Use the method that accepts explicit old image information
                updatedBrand = brandService.updateBrandWithOldImage(id, brand, imageFile, oldImage);
            } else {
                // Use standard update method
                updatedBrand = brandService.updateBrand(id, brand, imageFile);
            }

            if (updatedBrand != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Brand updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update brand");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IOException e) {
            logger.error("Failed to upload image: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            logger.error("Error updating brand: {}", e.getMessage(), e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating brand: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean isDeleted = brandService.deleteBrand(id);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Brand deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Brand not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id, Authentication authentication) {
        // Get the authenticated user ID
        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            userId = userPrincipal.getId().intValue();
        }

        Brand updatedBrand = brandService.toggleBrandStatus(id, userId);
        if (updatedBrand != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Brand status updated successfully");
            response.put("status", String.valueOf(updatedBrand.getStatus()));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Brand not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id, Authentication authentication) {
        // Get the authenticated user ID
        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            userId = userPrincipal.getId().intValue();
        }

        Brand deletedBrand = brandService.softDeleteBrand(id, userId);
        if (deletedBrand != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Brand moved to trash successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Brand not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of trashed resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Brand>> trash() {
        List<Brand> trashedBrands = brandService.getAllTrashedBrands();
        return ResponseEntity.ok(trashedBrands);
    }

    /**
     * Restore the specified resource from trash.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id, Authentication authentication) {
        // Get the authenticated user ID
        Integer userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            userId = userPrincipal.getId().intValue();
        }

        Brand restoredBrand = brandService.restoreBrand(id, userId);
        if (restoredBrand != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Brand restored successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Brand not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}