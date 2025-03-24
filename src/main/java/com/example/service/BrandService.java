package com.example.service;

import com.example.entity.Brand;
import com.example.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all active brands
     */
    public List<Brand> getAllActiveBrands() {
        return brandRepository.findAll().stream()
                .filter(brand -> brand.getStatus() == 1)
                .sorted(Comparator.comparing(Brand::getCreatedAt).reversed())
                .collect(Collectors.toList());
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
        brand.setName(brandDetails.getName());
        brand.setSlug(brandDetails.getSlug());
        brand.setStatus(brandDetails.getStatus());
        brand.setUpdatedAt(new Date());

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "brands");
            brand.setImage(fileName);
        }

        return brandRepository.save(brand);
    }

    /**
     * Update brand with base64 image
     */
    public Brand updateBrandWithBase64Image(Long id, Brand brandDetails, String imageBase64) throws IOException {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();
        brand.setName(brandDetails.getName());
        brand.setSlug(brandDetails.getSlug());
        brand.setStatus(brandDetails.getStatus());
        brand.setUpdatedAt(new Date());

        // Handle base64 image if provided
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            String fileName = fileStorageService.saveBase64Image(imageBase64, "brands");
            brand.setImage(fileName);
        }

        return brandRepository.save(brand);
    }

    /**
     * Delete brand
     */
    public boolean deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            return false;
        }

        brandRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete brand (set status to 0)
     */
    public Brand softDeleteBrand(Long id) {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();
        brand.setStatus(0);
        brand.setUpdatedAt(new Date());

        return brandRepository.save(brand);
    }

    /**
     * Restore soft-deleted brand
     */
    public Brand restoreBrand(Long id) {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();
        brand.setStatus(1);
        brand.setUpdatedAt(new Date());

        return brandRepository.save(brand);
    }

    /**
     * Toggle brand status
     */
    public Brand toggleBrandStatus(Long id) {
        Optional<Brand> optionalBrand = brandRepository.findById(id);
        if (optionalBrand.isEmpty()) {
            return null;
        }

        Brand brand = optionalBrand.get();
        brand.setStatus(brand.getStatus() == 1 ? 2 : 1);
        brand.setUpdatedAt(new Date());

        return brandRepository.save(brand);
    }
}