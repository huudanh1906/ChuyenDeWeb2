package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    // Thư mục gốc cho static resources - không còn sử dụng, chỉ giữ lại cho khả
    // năng tương thích
    private final String baseUploadDir = "src/main/resources/static/imgs";

    // Thư mục lưu trữ bên ngoài cấu hình trong application.properties
    @Value("${file.storage.location:./file-storage}")
    private String fileStorageLocation;

    @PostConstruct
    public void init() {
        try {
            Path basePath = Paths.get(fileStorageLocation);
            if (fileStorageLocation.startsWith("./")) {
                // Chuyển đổi từ đường dẫn tương đối sang tuyệt đối
                Path absolutePath = Paths.get(System.getProperty("user.dir"))
                        .resolve(fileStorageLocation.substring(2));
                fileStorageLocation = absolutePath.toString();
                basePath = absolutePath;
            }

            logger.info("Initializing external file storage at: {}", fileStorageLocation);
            Files.createDirectories(basePath);
        } catch (IOException e) {
            logger.error("Could not initialize external storage location: {}", e.getMessage());
        }
    }

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

        // Chỉ lưu vào thư mục bên ngoài
        String externalUploadPath = fileStorageLocation + "/" + subdirectory;
        File externalUploadDir = new File(externalUploadPath);
        if (!externalUploadDir.exists()) {
            externalUploadDir.mkdirs();
        }

        Path externalFilePath = Paths.get(externalUploadDir.getAbsolutePath(), fileName);
        Files.write(externalFilePath, file.getBytes());

        logger.info("File saved successfully: {}", externalFilePath.toString());

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

        // Chỉ lưu vào thư mục bên ngoài
        String externalUploadPath = fileStorageLocation + "/" + subdirectory;
        File externalUploadDir = new File(externalUploadPath);
        if (!externalUploadDir.exists()) {
            externalUploadDir.mkdirs();
        }

        Path externalFilePath = Paths.get(externalUploadDir.getAbsolutePath(), fileName);
        Files.write(externalFilePath, imageBytes);

        logger.info("Base64 image saved successfully: {}", externalFilePath.toString());

        return fileName;
    }

    /**
     * Delete file from specified directory
     * 
     * @param filename   The name of the file to delete
     * @param folderName The subdirectory name (e.g., "products", "users")
     * @return true if the file was successfully deleted, false otherwise
     */
    public boolean deleteFile(String filename, String folderName) {
        try {
            logger.info("Deleting file: {} from folder: {}", filename, folderName);

            // Đường dẫn trong thư mục external
            Path externalPath = Paths
                    .get(fileStorageLocation + File.separator + folderName + File.separator + filename);
            boolean externalExists = Files.exists(externalPath);

            // Đường dẫn trong thư mục static
            Path staticPath = Paths.get(baseUploadDir + File.separator + folderName + File.separator + filename);
            boolean staticExists = Files.exists(staticPath);

            boolean deleted = false;

            // Xóa từ thư mục external trước
            if (externalExists) {
                deleted = Files.deleteIfExists(externalPath);
            }

            // Đồng thời xóa từ thư mục static nếu tồn tại
            if (staticExists) {
                boolean staticDeleted = Files.deleteIfExists(staticPath);
                deleted = deleted || staticDeleted;
            }

            if (!deleted) {
                logger.warn("File not found or could not be deleted: {} in folder {}", filename, folderName);
            } else {
                logger.info("Successfully deleted file: {} from folder {}", filename, folderName);
            }

            return deleted;
        } catch (IOException e) {
            logger.error("Error deleting file: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Get absolute file path
     * 
     * @param fileName     The name of the file
     * @param subdirectory The subdirectory name (e.g., "products", "users")
     * @return The absolute path to the file
     */
    public String getAbsoluteFilePath(String fileName, String subdirectory) {
        // Chỉ trả về đường dẫn bên ngoài
        return fileStorageLocation + "/" + subdirectory + "/" + fileName;
    }

    /**
     * Get file URL for frontend use
     * 
     * @param fileName     The name of the file
     * @param subdirectory The subdirectory name (e.g., "products", "users")
     * @return The URL path that can be used in frontend
     */
    public String getFileUrl(String fileName, String subdirectory) {
        // Luôn trả về URL cho đường dẫn bên ngoài
        return "/uploads/" + subdirectory + "/" + fileName;
    }
}