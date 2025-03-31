package com.example.service;

import com.example.entity.Brand;
import com.example.entity.Category;
import com.example.entity.OrderDetail;
import com.example.entity.Product;
import com.example.repository.BrandRepository;
import com.example.repository.CategoryRepository;
import com.example.repository.OrderDetailRepository;
import com.example.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * Get all active products
     */
    public List<Product> getAllActiveProducts() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStatus() == 1)
                .collect(Collectors.toList());
    }

    /**
     * Get all trashed products
     */
    public List<Product> getAllTrashedProducts() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStatus() == 0)
                .collect(Collectors.toList());
    }

    /**
     * Get product by ID
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Get products by category ID
     */
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return getAllActiveProducts().stream()
                .filter(product -> product.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }

    /**
     * Get products by brand ID
     */
    public List<Product> getProductsByBrandId(Long brandId) {
        return getAllActiveProducts().stream()
                .filter(product -> product.getBrandId().equals(brandId))
                .collect(Collectors.toList());
    }

    /**
     * Get filtered products
     */
    public Map<String, Object> getFilteredProducts(String categorySlug, String brandSlug,
            String priceRange, Boolean onPromotion,
            String sortOrder, int page, int size) {
        // Start with all active products
        List<Product> filteredProducts = getAllActiveProducts();

        // Filter by category if specified
        if (categorySlug != null && !categorySlug.isEmpty()) {
            Optional<Category> category = categoryRepository.findBySlug(categorySlug);
            if (category.isPresent()) {
                Long categoryId = category.get().getId();
                filteredProducts = filteredProducts.stream()
                        .filter(product -> product.getCategoryId().equals(categoryId))
                        .collect(Collectors.toList());
            }
        }

        // Filter by brand if specified
        if (brandSlug != null && !brandSlug.isEmpty()) {
            Optional<Brand> brand = brandRepository.findBySlug(brandSlug);
            if (brand.isPresent()) {
                Long brandId = brand.get().getId();
                filteredProducts = filteredProducts.stream()
                        .filter(product -> product.getBrandId().equals(brandId))
                        .collect(Collectors.toList());
            }
        }

        // Filter by price range if specified
        if (priceRange != null && !priceRange.isEmpty()) {
            String[] range = priceRange.split("-");
            if (range.length == 2) {
                try {
                    BigDecimal min = new BigDecimal(range[0].trim());
                    BigDecimal max = new BigDecimal(range[1].trim());

                    filteredProducts = filteredProducts.stream()
                            .filter(product -> {
                                BigDecimal effectivePrice = product.getPricesale() != null ? product.getPricesale()
                                        : product.getPrice();
                                return effectivePrice.compareTo(min) >= 0 && effectivePrice.compareTo(max) <= 0;
                            })
                            .collect(Collectors.toList());
                } catch (NumberFormatException e) {
                    // Invalid price range format, ignore this filter
                }
            }
        }

        // Filter promotion products if specified
        if (onPromotion != null && onPromotion) {
            filteredProducts = filteredProducts.stream()
                    .filter(product -> product.getPricesale() != null)
                    .collect(Collectors.toList());
        }

        // Sort products based on sort_order parameter
        sortProducts(filteredProducts, sortOrder);

        // Paginate results
        return paginateResults(filteredProducts, page, size);
    }

    /**
     * Get sale products (with pricesale > 0)
     */
    public Map<String, Object> getSaleProducts(int page, int size) {
        List<Product> allSaleProducts = getAllActiveProducts().stream()
                .filter(product -> product.getPricesale() != null
                        && product.getPricesale().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        // Paginate
        int totalItems = allSaleProducts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Product> paginatedProducts = (fromIndex < totalItems) ? allSaleProducts.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedProducts);
        response.put("totalElements", totalItems);
        response.put("totalPages", totalPages);
        response.put("size", size);
        response.put("number", page - 1); // 0-based page index for API consistency

        return response;
    }

    /**
     * Get best selling products
     */
    public List<Map<String, Object>> getBestSellingProducts() {
        Map<Long, Integer> productSales = getBestSellingProductMap();

        // Get top 8 product IDs based on sales
        List<Long> topProductIds = productSales.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(8)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Get product details for these IDs
        List<Map<String, Object>> bestSellingProducts = new ArrayList<>();

        for (Long productId : topProductIds) {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("product", product);
                productInfo.put("total_qty_sold", productSales.get(productId));
                bestSellingProducts.add(productInfo);
            }
        }

        return bestSellingProducts;
    }

    /**
     * Get products by category
     */
    public Map<String, Object> getProductsByCategory(String slug, int page, int size) {
        Optional<Category> categoryOpt = categoryRepository.findBySlug(slug);
        if (categoryOpt.isEmpty()) {
            return null;
        }

        Category category = categoryOpt.get();
        Long categoryId = category.getId();

        List<Product> allCategoryProducts = getProductsByCategoryId(categoryId);

        // Paginate
        int totalItems = allCategoryProducts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Product> paginatedProducts = (fromIndex < totalItems) ? allCategoryProducts.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("category", category);
        response.put("content", paginatedProducts);
        response.put("totalElements", totalItems);
        response.put("totalPages", totalPages);
        response.put("size", size);
        response.put("number", page - 1); // 0-based page index for API consistency

        return response;
    }

    /**
     * Get products by brand
     */
    public Map<String, Object> getProductsByBrand(String slug, int page, int size) {
        Optional<Brand> brandOpt = brandRepository.findBySlug(slug);
        if (brandOpt.isEmpty()) {
            return null;
        }

        Brand brand = brandOpt.get();
        Long brandId = brand.getId();

        List<Product> allBrandProducts = getProductsByBrandId(brandId);

        // Paginate
        int totalItems = allBrandProducts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Product> paginatedProducts = (fromIndex < totalItems) ? allBrandProducts.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("brand", brand);
        response.put("content", paginatedProducts);
        response.put("totalElements", totalItems);
        response.put("totalPages", totalPages);
        response.put("size", size);
        response.put("number", page - 1); // 0-based page index for API consistency

        return response;
    }

    /**
     * Get product by slug
     */
    public Optional<Product> getProductBySlug(String slug) {
        return getAllActiveProducts().stream()
                .filter(product -> product.getSlug().equals(slug))
                .findFirst();
    }

    /**
     * Search products by keyword
     */
    public Map<String, Object> searchProducts(String query, int page, int size) {
        String searchTerm = query.toLowerCase();

        List<Product> matchingProducts = getAllActiveProducts().stream()
                .filter(product -> product.getName().toLowerCase().contains(searchTerm) ||
                        product.getDetail().toLowerCase().contains(searchTerm) ||
                        (product.getDescription() != null &&
                                product.getDescription().toLowerCase().contains(searchTerm)))
                .collect(Collectors.toList());

        // Paginate
        int totalItems = matchingProducts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Product> paginatedProducts = (fromIndex < totalItems) ? matchingProducts.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedProducts);
        response.put("totalElements", totalItems);
        response.put("totalPages", totalPages);
        response.put("size", size);
        response.put("number", page - 1); // 0-based page index for API consistency
        response.put("searchTerm", query);

        return response;
    }

    /**
     * Create a new product
     */
    public Product createProduct(Product product, MultipartFile imageFile) throws IOException {
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "products");
            product.setImage(fileName);
        }

        product.setCreatedAt(new Date());
        return productRepository.save(product);
    }

    /**
     * Update an existing product
     */
    public Product updateProduct(Long id, Product productDetails, MultipartFile imageFile) throws IOException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return null;
        }

        Product product = optionalProduct.get();

        // Update product fields
        product.setName(productDetails.getName());
        product.setSlug(productDetails.getSlug());
        product.setDetail(productDetails.getDetail());
        product.setPrice(productDetails.getPrice());
        product.setPricesale(productDetails.getPricesale());
        product.setCategoryId(productDetails.getCategoryId());
        product.setBrandId(productDetails.getBrandId());
        product.setStatus(productDetails.getStatus());
        product.setUpdatedAt(new Date());
        product.setQty(productDetails.getQty());
        product.setDescription(productDetails.getDescription());

        // Set updatedBy if provided
        if (productDetails.getUpdatedBy() > 0) {
            product.setUpdatedBy(productDetails.getUpdatedBy());
        }

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "products");
            product.setImage(fileName);
        }

        return productRepository.save(product);
    }

    /**
     * Update an existing product with file or base64 image
     */
    public Product updateProduct(Long id, Product productDetails, MultipartFile imageFile, String imageBase64)
            throws IOException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return null;
        }

        Product product = optionalProduct.get();

        // Update product fields
        product.setName(productDetails.getName());
        product.setSlug(productDetails.getSlug());
        product.setDetail(productDetails.getDetail());
        product.setPrice(productDetails.getPrice());
        product.setPricesale(productDetails.getPricesale());
        product.setCategoryId(productDetails.getCategoryId());
        product.setBrandId(productDetails.getBrandId());
        product.setStatus(productDetails.getStatus());
        product.setUpdatedAt(new Date());
        product.setQty(productDetails.getQty());
        product.setDescription(productDetails.getDescription());

        // Set updatedBy if provided
        if (productDetails.getUpdatedBy() > 0) {
            product.setUpdatedBy(productDetails.getUpdatedBy());
        }

        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile, "products");
            product.setImage(fileName);
        } else if (imageBase64 != null && !imageBase64.isEmpty()) {
            String fileName = fileStorageService.saveBase64Image(imageBase64, "products");
            product.setImage(fileName);
        }

        return productRepository.save(product);
    }

    /**
     * Delete product
     */
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            return false;
        }

        productRepository.deleteById(id);
        return true;
    }

    /**
     * Soft delete product (set status to 0)
     */
    public Product softDeleteProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return null;
        }

        Product product = optionalProduct.get();
        product.setStatus(0);
        product.setUpdatedAt(new Date());

        return productRepository.save(product);
    }

    /**
     * Restore soft-deleted product (set status to 1)
     */
    public Product restoreProduct(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return null;
        }

        Product product = optionalProduct.get();
        product.setStatus(1);
        product.setUpdatedAt(new Date());

        return productRepository.save(product);
    }

    /**
     * Toggle product status between 1 and 2
     */
    public Product toggleProductStatus(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            return null;
        }

        Product product = optionalProduct.get();
        product.setStatus(product.getStatus() == 1 ? 2 : 1);
        product.setUpdatedAt(new Date());

        return productRepository.save(product);
    }

    /**
     * Helper method to sort products
     */
    private void sortProducts(List<Product> products, String sortOrder) {
        switch (sortOrder) {
            case "high_to_low":
                products.sort((p1, p2) -> {
                    BigDecimal price1 = p1.getPricesale() != null ? p1.getPricesale() : p1.getPrice();
                    BigDecimal price2 = p2.getPricesale() != null ? p2.getPricesale() : p2.getPrice();
                    return price2.compareTo(price1);
                });
                break;
            case "low_to_high":
                products.sort((p1, p2) -> {
                    BigDecimal price1 = p1.getPricesale() != null ? p1.getPricesale() : p1.getPrice();
                    BigDecimal price2 = p2.getPricesale() != null ? p2.getPricesale() : p2.getPrice();
                    return price1.compareTo(price2);
                });
                break;
            case "best_seller":
                // Get best selling products
                Map<Long, Integer> productSales = getBestSellingProductMap();

                products.sort((p1, p2) -> {
                    Integer sales1 = productSales.getOrDefault(p1.getId(), 0);
                    Integer sales2 = productSales.getOrDefault(p2.getId(), 0);
                    return sales2.compareTo(sales1);
                });
                break;
            default: // newest
                products.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
                break;
        }
    }

    /**
     * Helper method to paginate results
     */
    private Map<String, Object> paginateResults(List<Product> products, int page, int size) {
        int totalItems = products.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Product> paginatedProducts = (fromIndex < totalItems) ? products.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("current_page", page);
        response.put("per_page", size);
        response.put("total", totalItems);
        response.put("last_page", totalPages);
        response.put("data", paginatedProducts);

        return response;
    }

    /**
     * Helper method to get product sales
     */
    private Map<Long, Integer> getBestSellingProductMap() {
        List<OrderDetail> allOrderDetails = orderDetailRepository.findAll();

        // Group by product ID and sum quantities
        Map<Long, Integer> productSales = new HashMap<>();

        for (OrderDetail detail : allOrderDetails) {
            Long productId = detail.getProductId();
            int qty = detail.getQty();

            productSales.put(productId, productSales.getOrDefault(productId, 0) + qty);
        }

        return productSales;
    }

    /**
     * Get paginated active products
     */
    public Map<String, Object> getPaginatedActiveProducts(int page, int size) {
        List<Product> allActiveProducts = getAllActiveProducts();

        // Paginate
        int totalItems = allActiveProducts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Product> paginatedProducts = (fromIndex < totalItems) ? allActiveProducts.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedProducts);
        response.put("totalElements", totalItems);
        response.put("totalPages", totalPages);
        response.put("size", size);
        response.put("number", page - 1); // 0-based page index for API consistency

        return response;
    }

    /**
     * Get related products (same category, excluding current product)
     */
    public List<Product> getRelatedProducts(Long categoryId, Long currentProductId) {
        return getAllActiveProducts().stream()
                .filter(product -> product.getCategoryId().equals(categoryId)
                        && !product.getId().equals(currentProductId))
                .limit(8)
                .collect(Collectors.toList());
    }

    /**
     * Get best seller products
     */
    public Map<String, Object> getBestSellerProducts(int page, int size) {
        // In a real application, this would be based on order statistics
        // For now, just return active products
        List<Product> allProducts = getAllActiveProducts();

        // Paginate
        int totalItems = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, totalItems);

        List<Product> paginatedProducts = (fromIndex < totalItems) ? allProducts.subList(fromIndex, toIndex)
                : new ArrayList<>();

        Map<String, Object> response = new HashMap<>();
        response.put("content", paginatedProducts);
        response.put("totalElements", totalItems);
        response.put("totalPages", totalPages);
        response.put("size", size);
        response.put("number", page - 1); // 0-based page index for API consistency

        return response;
    }
}