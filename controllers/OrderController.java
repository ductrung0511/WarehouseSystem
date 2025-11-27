package com.warehouse.controllers;

import com.warehouse.services.WarehouseService;
import com.warehouse.services.OrderService;
import com.warehouse.models.Order;
import com.warehouse.models.Product;
import com.warehouse.models.Customer;
import java.util.List;
import java.util.Date;

public class OrderController {
    private WarehouseService warehouseService;
    private OrderService orderService;
    
    public OrderController() {
        this.warehouseService = new WarehouseService();
        this.orderService = new OrderService();
    }
    
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }
    
    public List<Order> getOrdersByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        return orderService.getOrdersByStatus(status);
    }
    
    public boolean createOrder(int customerId, List<Product> products, List<Integer> quantities) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one product");
        }
        if (quantities == null || quantities.size() != products.size()) {
            throw new IllegalArgumentException("Products and quantities must match");
        }
        
        // Check stock availability
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = quantities.get(i);
            if (product.getQuantity() < quantity) {
                throw new IllegalStateException("Insufficient stock for: " + product.getName() + 
                                              ". Available: " + product.getQuantity() + ", Requested: " + quantity);
            }
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive for: " + product.getName());
            }
        }
        
        Order order = new Order(0, customerId, new Date());
        for (int i = 0; i < products.size(); i++) {
            order.addItem(products.get(i), quantities.get(i));
        }
        
        return warehouseService.createOrder(order);
    }
    
    public boolean processSale(int customerId, int productId, int quantity) {
        if (customerId <= 0) throw new IllegalArgumentException("Customer ID must be positive");
        if (productId <= 0) throw new IllegalArgumentException("Product ID must be positive");
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        
        Product product = warehouseService.getProductById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }
        if (product.getQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock. Available: " + product.getQuantity());
        }
        
        Order order = new Order(0, customerId, new Date());
        order.addItem(product, quantity);
        return warehouseService.processSale(order);
    }
    
    public boolean updateOrderStatus(int orderId, String status) {
        if (orderId <= 0) throw new IllegalArgumentException("Order ID must be positive");
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        return orderService.updateOrderStatus(orderId, status);
    }
    
    public List<Customer> getAllCustomers() {
        return warehouseService.getAllCustomers();
    }
    
    public List<Product> getAllProducts() {
        return warehouseService.getAllProducts();
    }
}