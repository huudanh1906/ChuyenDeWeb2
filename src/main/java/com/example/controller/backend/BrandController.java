package com.example.controller.backend;

import com.example.entity.Brand;
import com.example.service.BrandService;
import com.example.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "*")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Brand>> index() {
        List<Brand> brands = brandService.getAllActiveBrands();
        return ResponseEntity.ok(brands);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam("slug") String slug,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Brand brand = new Brand();
            brand.setName(name);
            brand.setSlug(slug);
            brand.setMetakey(metakey);
            brand.setMetadesc(metadesc);
            brand.setStatus(status);

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
            @RequestParam("slug") String slug,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageBase64", required = false) String imageBase64) {

        try {
            Optional<Brand> optionalBrand = brandService.getBrandById(id);
            if (optionalBrand.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Brand not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Brand brandDetails = new Brand();
            brandDetails.setName(name);
            brandDetails.setSlug(slug);
            brandDetails.setMetakey(metakey);
            brandDetails.setMetadesc(metadesc);
            brandDetails.setStatus(status);

            Brand updatedBrand;

            // Use different update methods based on what's provided
            if (imageFile != null && !imageFile.isEmpty()) {
                updatedBrand = brandService.updateBrand(id, brandDetails, imageFile);
            } else if (imageBase64 != null && !imageBase64.isEmpty()) {
                updatedBrand = brandService.updateBrandWithBase64Image(id, brandDetails, imageBase64);
            } else {
                // No image updates, use the standard method
                updatedBrand = brandService.updateBrand(id, brandDetails, null);
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
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
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
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        Brand updatedBrand = brandService.toggleBrandStatus(id);
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
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Brand deletedBrand = brandService.softDeleteBrand(id);
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
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Brand restoredBrand = brandService.restoreBrand(id);
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