package com.example.controller.backend;

import com.example.entity.Banner;
import com.example.service.BannerService;
import com.example.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "*")
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Banner>> index() {
        List<Banner> banners = bannerService.getAllActiveBanners();
        return ResponseEntity.ok(banners);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("name") String name,
            @RequestParam("position") String position,
            @RequestParam("link") String link,
            @RequestParam("status") int status,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            Banner banner = new Banner();
            banner.setName(name);
            banner.setPosition(position);
            banner.setLink(link);
            banner.setStatus(status);
            banner.setCreatedBy(1); // Replace with actual authentication logic for user ID

            Banner savedBanner = bannerService.createBanner(banner, imageFile);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Banner created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating banner: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Banner> banner = bannerService.getBannerById(id);
        if (banner.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Banner not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(banner.get());
    }

    /**
     * Show the form for editing the specified resource.
     */
    @GetMapping("/{id}/edit")
    public ResponseEntity<?> edit(@PathVariable Long id) {
        Optional<Banner> banner = bannerService.getBannerById(id);
        if (banner.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Banner not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        List<String> positions = bannerService.getAllBannerPositions();

        Map<String, Object> response = new HashMap<>();
        response.put("banner", banner.get());
        response.put("positions", positions);

        return ResponseEntity.ok(response);
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("position") String position,
            @RequestParam("link") String link,
            @RequestParam("status") int status,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageBase64", required = false) String imageBase64) {

        try {
            Banner bannerDetails = new Banner();
            bannerDetails.setName(name);
            bannerDetails.setPosition(position);
            bannerDetails.setLink(link);
            bannerDetails.setStatus(status);
            bannerDetails.setUpdatedBy(1); // Replace with actual authentication logic for user ID

            Banner updatedBanner;

            if (imageFile != null && !imageFile.isEmpty()) {
                updatedBanner = bannerService.updateBanner(id, bannerDetails, imageFile);
            } else if (imageBase64 != null && !imageBase64.isEmpty()) {
                updatedBanner = bannerService.updateBannerWithBase64Image(id, bannerDetails, imageBase64);
            } else {
                updatedBanner = bannerService.updateBanner(id, bannerDetails, null);
            }

            if (updatedBanner == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Banner not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Banner updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating banner: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean deleted = bannerService.deleteBanner(id);

        Map<String, String> response = new HashMap<>();
        if (deleted) {
            response.put("message", "Banner destroyed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Banner not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the banner.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        Banner banner = bannerService.toggleBannerStatus(id);

        Map<String, String> response = new HashMap<>();
        if (banner != null) {
            response.put("message", "Banner status changed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Banner not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the banner.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Banner banner = bannerService.softDeleteBanner(id);

        Map<String, String> response = new HashMap<>();
        if (banner != null) {
            response.put("message", "Banner deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Banner not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get all trashed banners.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Banner>> trash() {
        List<Banner> banners = bannerService.getAllTrashedBanners();
        return ResponseEntity.ok(banners);
    }

    /**
     * Restore a trashed banner.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Banner banner = bannerService.restoreBanner(id);

        Map<String, String> response = new HashMap<>();
        if (banner != null) {
            response.put("message", "Banner restored successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Banner not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}