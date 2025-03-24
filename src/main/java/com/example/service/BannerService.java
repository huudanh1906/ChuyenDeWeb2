package com.example.service;

import com.example.entity.Banner;
import com.example.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all active banners
     */
    public List<Banner> getAllActiveBanners() {
        return bannerRepository.findAll().stream()
                .filter(banner -> banner.getStatus() != 0)
                .sorted(Comparator.comparing(Banner::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get all trashed banners (status = 0)
     */
    public List<Banner> getAllTrashedBanners() {
        return bannerRepository.findAll().stream()
                .filter(banner -> banner.getStatus() == 0)
                .sorted(Comparator.comparing(Banner::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get banner by ID
     */
    public Optional<Banner> getBannerById(Long id) {
        return bannerRepository.findById(id);
    }

    /**
     * Create a new banner
     */
    public Banner createBanner(Banner banner, MultipartFile imageFile) throws IOException {
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "banners");
            banner.setImage(fileName);
        }

        banner.setCreatedAt(new Date());
        return bannerRepository.save(banner);
    }

    /**
     * Update an existing banner
     */
    public Banner updateBanner(Long id, Banner bannerDetails, MultipartFile imageFile) throws IOException {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isEmpty()) {
            return null;
        }

        Banner banner = optionalBanner.get();
        banner.setName(bannerDetails.getName());
        banner.setPosition(bannerDetails.getPosition());
        banner.setLink(bannerDetails.getLink());
        banner.setStatus(bannerDetails.getStatus());
        banner.setUpdatedAt(new Date());

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "banners");
            banner.setImage(fileName);
        }

        return bannerRepository.save(banner);
    }

    /**
     * Update banner with base64 image
     */
    public Banner updateBannerWithBase64Image(Long id, Banner bannerDetails, String imageBase64) throws IOException {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isEmpty()) {
            return null;
        }

        Banner banner = optionalBanner.get();
        banner.setName(bannerDetails.getName());
        banner.setPosition(bannerDetails.getPosition());
        banner.setLink(bannerDetails.getLink());
        banner.setStatus(bannerDetails.getStatus());
        banner.setUpdatedAt(new Date());

        // Handle base64 image if provided
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            String fileName = fileStorageService.saveBase64Image(imageBase64, "banners");
            banner.setImage(fileName);
        }

        return bannerRepository.save(banner);
    }

    /**
     * Delete banner
     */
    public boolean deleteBanner(Long id) {
        if (!bannerRepository.existsById(id)) {
            return false;
        }

        bannerRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete banner (set status to 0)
     */
    public Banner softDeleteBanner(Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isEmpty()) {
            return null;
        }

        Banner banner = optionalBanner.get();
        banner.setStatus(0);
        banner.setUpdatedAt(new Date());

        return bannerRepository.save(banner);
    }

    /**
     * Restore soft-deleted banner
     */
    public Banner restoreBanner(Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isEmpty()) {
            return null;
        }

        Banner banner = optionalBanner.get();
        banner.setStatus(1);
        banner.setUpdatedAt(new Date());

        return bannerRepository.save(banner);
    }

    /**
     * Toggle banner status
     */
    public Banner toggleBannerStatus(Long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isEmpty()) {
            return null;
        }

        Banner banner = optionalBanner.get();
        banner.setStatus(banner.getStatus() == 1 ? 2 : 1);
        banner.setUpdatedAt(new Date());

        return bannerRepository.save(banner);
    }

    /**
     * Get all distinct banner positions
     */
    public List<String> getAllBannerPositions() {
        return getAllActiveBanners().stream()
                .map(Banner::getPosition)
                .distinct()
                .collect(Collectors.toList());
    }
}