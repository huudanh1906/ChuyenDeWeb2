package com.example.controller.frontend;

import com.example.entity.Order;
import com.example.entity.OrderDetail;
import com.example.entity.Product;
import com.example.entity.User;
import com.example.service.OrderService;
import com.example.service.ProductService;
import com.example.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestController
@RequestMapping("/api/frontend/orders")
@CrossOrigin(origins = "*")
public class FrontendOrderController {

    private static final Logger logger = LoggerFactory.getLogger(FrontendOrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    /**
     * Add a new order to the database
     */
    @PostMapping
    public ResponseEntity<?> addToOrder(
            @RequestParam(value = "note", required = false) String note,
            @RequestHeader("Authorization") String authHeader) {

        logger.info("Incoming Order Request with note: {}", note);

        // In a real application, you would extract the user ID from the auth token
        // For this example, I'm assuming a method to get the user ID from the auth
        // header
        Long userId = getUserIdFromAuthHeader(authHeader);
        if (userId == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Get user from repository
        Optional<User> optionalUser = userService.getUserById(userId);
        if (optionalUser.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        User user = optionalUser.get();

        // Create a new order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setName(user.getName());
        order.setPhone(user.getPhone());
        order.setEmail(user.getEmail());
        order.setAddress(user.getAddress());
        order.setNote(note);
        order.setStatus(1);

        try {
            Order savedOrder = orderService.createOrder(order);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Order created successfully");
            response.put("order", savedOrder);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get orders for a specific user
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<List<Order>> index(@PathVariable Long id) {
        List<Order> orders = orderService.getOrdersByUserId(id);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order details with product information
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        // Check if order exists
        Optional<Order> orderOptional = orderService.getOrderById(id);
        if (orderOptional.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Get order details with product information
        List<Map<String, Object>> orderDetails = orderService.getOrderDetailsWithProducts(id);
        if (orderDetails == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Failed to retrieve order details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(orderDetails);
    }

    /**
     * Helper method to get user ID from Authorization header
     * In a real application, this would be handled by Spring Security
     */
    private Long getUserIdFromAuthHeader(String authHeader) {
        // This is a placeholder - in a real application, you would extract the user ID
        // from the JWT token or other authentication mechanism
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extracting token
            String token = authHeader.substring(7);
            // Here you would validate the token and extract the user ID
            // For this example, returning a dummy ID
            return 1L;
        }
        return null;
    }
}