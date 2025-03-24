package com.example.service;

import com.example.entity.Order;
import com.example.entity.OrderDetail;
import com.example.entity.Product;
import com.example.entity.User;
import com.example.repository.OrderRepository;
import com.example.repository.OrderDetailRepository;
import com.example.repository.ProductRepository;
import com.example.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get all orders
     */
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * Get order by ID
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Create a new order
     */
    public Order createOrder(Order order) {
        order.setCreatedAt(new Date());
        return orderRepository.save(order);
    }

    /**
     * Update an existing order
     */
    public Order updateOrder(Long id, Order orderDetails) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return null;
        }

        Order order = optionalOrder.get();
        order.setUserId(orderDetails.getUserId());
        order.setName(orderDetails.getName());
        order.setEmail(orderDetails.getEmail());
        order.setPhone(orderDetails.getPhone());
        order.setAddress(orderDetails.getAddress());
        order.setNote(orderDetails.getNote());
        order.setStatus(orderDetails.getStatus());
        order.setUpdatedAt(new Date());

        return orderRepository.save(order);
    }

    /**
     * Delete order
     */
    public boolean deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            return false;
        }

        orderRepository.deleteById(id);
        return true;
    }

    /**
     * Update order status
     */
    public Order updateOrderStatus(Long id, int status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isEmpty()) {
            return null;
        }

        Order order = optionalOrder.get();
        order.setStatus(status);
        order.setUpdatedAt(new Date());

        return orderRepository.save(order);
    }

    /**
     * Get orders by user ID
     */
    public List<Order> getOrdersByUserId(Long userId) {
        return getAllOrders().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Get all active orders
     */
    public List<Order> getAllActiveOrders() {
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == 1)
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Get order details with products
     */
    public List<Map<String, Object>> getOrderDetailsWithProducts(Long orderId) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        if (orderDetails.isEmpty()) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> orderDetailsWithProducts = new ArrayList<>();

        for (OrderDetail detail : orderDetails) {
            Optional<Product> productOptional = productRepository.findById(detail.getProductId());
            if (productOptional.isPresent()) {
                Product product = productOptional.get();

                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("price", detail.getPrice());
                detailMap.put("qty", detail.getQty());
                detailMap.put("product_name", product.getName());
                detailMap.put("product_image", "http://localhost:8080/imgs/products/" + product.getImage());

                orderDetailsWithProducts.add(detailMap);
            }
        }

        return orderDetailsWithProducts;
    }

    /**
     * Add order details to an order
     */
    public List<OrderDetail> addOrderDetails(Long orderId, List<OrderDetail> orderDetails) {
        // Check if order exists
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found");
        }

        List<OrderDetail> savedOrderDetails = new ArrayList<>();

        for (OrderDetail detail : orderDetails) {
            detail.setOrderId(orderId);
            savedOrderDetails.add(orderDetailRepository.save(detail));
        }

        return savedOrderDetails;
    }

    /**
     * Get order statistics
     */
    public Map<String, Object> getOrderStatistics() {
        List<Order> allOrders = getAllActiveOrders();

        // Total orders
        int totalOrders = allOrders.size();

        // Total revenue
        double totalRevenue = 0;
        for (Order order : allOrders) {
            List<OrderDetail> details = orderDetailRepository.findByOrderId(order.getId());
            for (OrderDetail detail : details) {
                totalRevenue += detail.getPrice().doubleValue() * detail.getQty();
            }
        }

        // Orders by status
        Map<Integer, Long> ordersByStatus = allOrders.stream()
                .collect(Collectors.groupingBy(Order::getStatus, Collectors.counting()));

        // Orders by month (for current year)
        Map<Integer, Long> ordersByMonth = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        for (Order order : allOrders) {
            calendar.setTime(order.getCreatedAt());
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1; // Calendar months are 0-based

            if (year == currentYear) {
                ordersByMonth.put(month, ordersByMonth.getOrDefault(month, 0L) + 1);
            }
        }

        // Create response
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("total_orders", totalOrders);
        statistics.put("total_revenue", totalRevenue);
        statistics.put("orders_by_status", ordersByStatus);
        statistics.put("orders_by_month", ordersByMonth);

        return statistics;
    }

    /**
     * Get recent orders (last 10)
     */
    public List<Order> getRecentOrders() {
        return getAllActiveOrders().stream()
                .limit(10)
                .collect(Collectors.toList());
    }
}