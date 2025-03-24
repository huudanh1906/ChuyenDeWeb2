package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

@Service
public class FileStorageService {

    private final String baseUploadDir = "src/main/resources/static/imgs";

    /**
     * Save multipart file to specified directory
     * 
     * @param file         The MultipartFile to save
     * @param subdirectory The subdirectory name (e.g., "products", "users")
     * @return The file name that was generated and saved
     * @throws IOException If an I/O error occurs
     */
    public String saveFile(MultipartFile file, String subdirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        // Generate unique file name
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String fileName = timestamp + fileExtension;

        // Create upload directory path
        String uploadDirPath = baseUploadDir + "/" + subdirectory;
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Save file
        Path filePath = Paths.get(uploadDir.getAbsolutePath(), fileName);
        Files.write(filePath, file.getBytes());

        return fileName;
    }

    /**
     * Save base64 encoded image to specified directory
     * 
     * @param base64String The base64 encoded image string
     * @param subdirectory The subdirectory name (e.g., "products", "users")
     * @return The file name that was generated and saved
     * @throws IOException If an I/O error occurs
     */
    public String saveBase64Image(String base64String, String subdirectory) throws IOException {
        if (base64String == null || base64String.isEmpty()) {
            throw new IllegalArgumentException("Base64 string cannot be null or empty");
        }

        // Remove data:image/jpeg;base64, prefix if present
        if (base64String.contains(",")) {
            base64String = base64String.split(",")[1];
        }

        // Decode base64 string
        byte[] imageBytes = Base64.getDecoder().decode(base64String);

        // Generate unique file name
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = timestamp + ".png";

        // Create upload directory path
        String uploadDirPath = baseUploadDir + "/" + subdirectory;
        File uploadDir = new File(uploadDirPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Save file
        Path filePath = Paths.get(uploadDir.getAbsolutePath(), fileName);
        Files.write(filePath, imageBytes);

        return fileName;
    }

    /**
     * Delete file from specified directory
     * 
     * @param fileName     The name of the file to delete
     * @param subdirectory The subdirectory name (e.g., "products", "users")
     * @return true if the file was successfully deleted, false otherwise
     */
    public boolean deleteFile(String fileName, String subdirectory) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        String filePath = baseUploadDir + "/" + subdirectory + "/" + fileName;
        File file = new File(filePath);

        return file.exists() && file.delete();
    }

    /**
     * Get absolute file path
     * 
     * @param fileName     The name of the file
     * @param subdirectory The subdirectory name (e.g., "products", "users")
     * @return The absolute path to the file
     */
    public String getAbsoluteFilePath(String fileName, String subdirectory) {
        return baseUploadDir + "/" + subdirectory + "/" + fileName;
    }

    /**
     * Get file URL for frontend use
     * 
     * @param fileName     The name of the file
     * @param subdirectory The subdirectory name (e.g., "products", "users")
     * @return The URL path that can be used in frontend
     */
    public String getFileUrl(String fileName, String subdirectory) {
        return "/imgs/" + subdirectory + "/" + fileName;
    }
}