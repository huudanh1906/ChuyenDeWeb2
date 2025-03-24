package com.example.controller.frontend;

import com.example.entity.Product;
import com.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/frontend/products")
@CrossOrigin(origins = "*")
public class FrontendProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> index(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {

        try {
            Map<String, Object> response = productService.getPaginatedActiveProducts(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to retrieve products: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<Map<String, Object>> productDetail(@PathVariable String slug) {
        Optional<Product> productOpt = productService.getProductBySlug(slug);

        Map<String, Object> response = new HashMap<>();

        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // Get related products (same category)
            List<Product> relatedProducts = productService.getRelatedProducts(product.getCategoryId(), product.getId());

            response.put("product", product);
            response.put("related_products", relatedProducts);
            response.put("message", "Product retrieved successfully");

            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/sale")
    public ResponseEntity<Map<String, Object>> saleProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {

        try {
            Map<String, Object> response = productService.getSaleProducts(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to retrieve sale products: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/best-seller")
    public ResponseEntity<Map<String, Object>> bestSellerProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {

        try {
            Map<String, Object> response = productService.getBestSellerProducts(page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to retrieve best-selling products: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/category/{slug}")
    public ResponseEntity<Map<String, Object>> productsByCategory(
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {

        try {
            Map<String, Object> response = productService.getProductsByCategory(slug, page, size);

            if (response == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Category not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to retrieve products by category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/brand/{slug}")
    public ResponseEntity<Map<String, Object>> productsByBrand(
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {

        try {
            Map<String, Object> response = productService.getProductsByBrand(slug, page, size);

            if (response == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Brand not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to retrieve products by brand: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size) {

        try {
            Map<String, Object> response = productService.searchProducts(q, page, size);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Failed to search products: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}