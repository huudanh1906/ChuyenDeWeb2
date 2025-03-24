package com.example.controller.backend;

import com.example.entity.Product;
import com.example.entity.Category;
import com.example.entity.Brand;
import com.example.service.ProductService;
import com.example.service.CategoryService;
import com.example.service.BrandService;
import com.example.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Product>> index() {
        List<Product> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("category_id") Long categoryId,
            @RequestParam("brand_id") Long brandId,
            @RequestParam("name") String name,
            @RequestParam("slug") String slug,
            @RequestParam("price") BigDecimal price,
            @RequestParam("price_sale") BigDecimal priceSale,
            @RequestParam("qty") int qty,
            @RequestParam("detail") String detail,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Validate category and brand
            Optional<Category> category = categoryService.getCategoryById(categoryId);
            if (category.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Category not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Optional<Brand> brand = brandService.getBrandById(brandId);
            if (brand.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Brand not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Product product = new Product();
            product.setCategoryId(categoryId);
            product.setBrandId(brandId);
            product.setName(name);
            product.setSlug(slug);
            product.setPrice(price);
            product.setPriceSale(priceSale);
            product.setQty(qty);
            product.setDetail(detail);
            product.setMetakey(metakey);
            product.setMetadesc(metadesc);
            product.setStatus(status);
            product.setCreatedBy(1); // Replace with actual authentication logic for user ID

            productService.createProduct(product, imageFile);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Product created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(product.get());
    }

    /**
     * Show form for editing the specified resource.
     */
    @GetMapping("/{id}/edit")
    public ResponseEntity<?> edit(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("product", product.get());
        result.put("categories", categoryService.getAllActiveCategories());
        result.put("brands", brandService.getAllActiveBrands());

        return ResponseEntity.ok(result);
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("category_id") Long categoryId,
            @RequestParam("brand_id") Long brandId,
            @RequestParam("name") String name,
            @RequestParam("slug") String slug,
            @RequestParam("price") BigDecimal price,
            @RequestParam("price_sale") BigDecimal priceSale,
            @RequestParam("qty") int qty,
            @RequestParam("detail") String detail,
            @RequestParam("metakey") String metakey,
            @RequestParam("metadesc") String metadesc,
            @RequestParam("status") int status,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(value = "imageBase64", required = false) String imageBase64) {

        try {
            Optional<Product> optionalProduct = productService.getProductById(id);
            if (optionalProduct.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Product not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Validate category and brand
            Optional<Category> category = categoryService.getCategoryById(categoryId);
            if (category.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Category not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Optional<Brand> brand = brandService.getBrandById(brandId);
            if (brand.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Brand not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Product productDetails = new Product();
            productDetails.setCategoryId(categoryId);
            productDetails.setBrandId(brandId);
            productDetails.setName(name);
            productDetails.setSlug(slug);
            productDetails.setPrice(price);
            productDetails.setPriceSale(priceSale);
            productDetails.setQty(qty);
            productDetails.setDetail(detail);
            productDetails.setMetakey(metakey);
            productDetails.setMetadesc(metadesc);
            productDetails.setStatus(status);
            productDetails.setUpdatedBy(1); // Replace with actual authentication logic for user ID

            Product updatedProduct;

            if (imageFile != null && !imageFile.isEmpty()) {
                // Update with file image
                updatedProduct = productService.updateProduct(id, productDetails, imageFile, null);
            } else if (imageBase64 != null && !imageBase64.isEmpty()) {
                // Update with base64 image
                updatedProduct = productService.updateProduct(id, productDetails, null, imageBase64);
            } else {
                // Update without changing image
                updatedProduct = productService.updateProduct(id, productDetails, null, null);
            }

            if (updatedProduct != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Product updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update product");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean isDeleted = productService.deleteProduct(id);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        Product updatedProduct = productService.toggleProductStatus(id);
        if (updatedProduct != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product status updated successfully");
            response.put("status", String.valueOf(updatedProduct.getStatus()));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Product deletedProduct = productService.softDeleteProduct(id);
        if (deletedProduct != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product moved to trash successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of trashed resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Product>> trash() {
        List<Product> trashedProducts = productService.getAllTrashedProducts();
        return ResponseEntity.ok(trashedProducts);
    }

    /**
     * Restore the specified resource from trash.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Product restoredProduct = productService.restoreProduct(id);
        if (restoredProduct != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product restored successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Get products by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    /**
     * Get products by brand
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<Product>> getByBrandId(@PathVariable Long brandId) {
        List<Product> products = productService.getProductsByBrandId(brandId);
        return ResponseEntity.ok(products);
    }

    /**
     * Search products
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam("keyword") String keyword) {
        Map<String, Object> result = productService.searchProducts(keyword, 1, 10);
        return ResponseEntity.ok(result);
    }
}