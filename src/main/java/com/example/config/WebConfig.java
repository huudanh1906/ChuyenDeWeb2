package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Thư mục lưu trữ tệp hình ảnh bên ngoài, có thể cấu hình trong
    // application.properties
    @Value("${file.storage.location:./file-storage}")
    private String fileStorageLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Phục vụ các tệp hình ảnh từ thư mục bên ngoài
        exposeDirectory("file-storage", registry);

        // Đảm bảo các tài nguyên tĩnh khác vẫn hoạt động
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(fileStorageLocation);
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        if (dirName.startsWith("../")) {
            dirName = dirName.replace("../", "");
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}