package com.example.config;

import com.example.exception.RestExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình xử lý Exception cho ứng dụng
 */
@Configuration
public class ExceptionConfig {

    @Bean
    public RestExceptionHandler restExceptionHandler() {
        return new RestExceptionHandler();
    }
}