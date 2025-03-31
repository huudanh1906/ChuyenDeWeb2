package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller để phục vụ các file tĩnh
 */
@RestController
@CrossOrigin(origins = "*")
public class FileController {

    @Value("${file.storage.location:./file-storage}")
    private String fileStorageLocation;

    /**
     * API cung cấp hình ảnh từ thư mục lưu trữ bên ngoài
     */
    @GetMapping("/uploads/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveUploadedImage(@PathVariable String type, @PathVariable String filename) {
        Path imagePath = Paths.get(fileStorageLocation, type, filename);
        File imageFile = imagePath.toFile();

        if (!imageFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(imageFile);
        String contentType = getContentType(imagePath);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    /**
     * Cung cấp khả năng tương thích ngược cho các yêu cầu hình ảnh từ đường dẫn cũ
     */
    @GetMapping("/imgs/{type}/{filename:.+}")
    public ResponseEntity<Resource> redirectFromOldPath(@PathVariable String type, @PathVariable String filename) {
        // Chuyển hướng đến hình ảnh trong thư mục mới
        return serveUploadedImage(type, filename);
    }

    /**
     * Xác định Content-Type dựa vào phần mở rộng của file
     */
    private String getContentType(Path filePath) {
        try {
            return Files.probeContentType(filePath);
        } catch (IOException e) {
            // Mặc định về image/jpeg nếu không thể xác định
            return "image/jpeg";
        }
    }
}