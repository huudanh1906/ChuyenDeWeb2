package com.example.controller.backend;

import com.example.entity.Order;
import com.example.entity.OrderDetail;
import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Display a listing of the resource.
     */
    @GetMapping
    public ResponseEntity<List<Order>> index() {
        List<Order> orders = orderService.getAllActiveOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Store a newly created resource in storage.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> store(
            @RequestParam("user_id") Long userId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("status") int status) {

        try {
            Order order = new Order();
            order.setUserId(userId);
            order.setName(name);
            order.setEmail(email);
            order.setPhone(phone);
            order.setAddress(address);
            order.setNote(note);
            order.setStatus(status);
            order.setCreatedBy(1); // Replace with actual authentication logic for user ID

            Order savedOrder = orderService.createOrder(order);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Order created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error creating order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Display the specified resource.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("order", order.get());

        // Get order details with product information
        List<Map<String, Object>> orderDetails = orderService.getOrderDetailsWithProducts(id);
        result.put("order_details", orderDetails);

        return ResponseEntity.ok(result);
    }

    /**
     * Update the specified resource in storage.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(
            @PathVariable Long id,
            @RequestParam("user_id") Long userId,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam("status") int status) {

        try {
            Optional<Order> optionalOrder = orderService.getOrderById(id);
            if (optionalOrder.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Order not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Order orderDetails = new Order();
            orderDetails.setUserId(userId);
            orderDetails.setName(name);
            orderDetails.setEmail(email);
            orderDetails.setPhone(phone);
            orderDetails.setAddress(address);
            orderDetails.setNote(note);
            orderDetails.setStatus(status);
            orderDetails.setUpdatedBy(1); // Replace with actual authentication logic for user ID

            Order updatedOrder = orderService.updateOrder(id, orderDetails);

            if (updatedOrder != null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Order updated successfully");
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Failed to update order");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Error updating order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Remove the specified resource from storage.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> destroy(@PathVariable Long id) {
        boolean isDeleted = orderService.deleteOrder(id);
        if (isDeleted) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Order deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Toggle the status of the specified resource.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, String>> status(@PathVariable Long id) {
        // Toggle between status 1 and 2
        Order updatedOrder = orderService.updateOrderStatus(id, 0); // Temporary value that will be changed

        if (updatedOrder != null) {
            // Get the current status and toggle it
            int currentStatus = updatedOrder.getStatus();
            int newStatus = currentStatus == 1 ? 2 : 1;

            // Update with the new status
            updatedOrder = orderService.updateOrderStatus(id, newStatus);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Order status updated successfully");
            response.put("status", String.valueOf(updatedOrder.getStatus()));
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Soft delete the specified resource.
     */
    @PutMapping("/{id}/delete")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Order deletedOrder = orderService.updateOrderStatus(id, 0);
        if (deletedOrder != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Order moved to trash successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * Display a listing of trashed resources.
     */
    @GetMapping("/trash")
    public ResponseEntity<List<Order>> trash() {
        List<Order> trashedOrders = orderService.getAllOrders().stream()
                .filter(order -> order.getStatus() == 0)
                .collect(Collectors.toList());
        return ResponseEntity.ok(trashedOrders);
    }

    /**
     * Restore the specified resource from trash.
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, String>> restore(@PathVariable Long id) {
        Order restoredOrder = orderService.updateOrderStatus(id, 2);
        if (restoredOrder != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Order restored successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Order not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}