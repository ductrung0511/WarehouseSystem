package com.warehouse.models;

import java.util.*;

public class Order {
    private int orderId;
    private int customerId;
    private Map<Product, Integer> productList;
    private double totalAmount;
    private String status;
    private Date date;

    public Order(int orderId, int customerId, Date date) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.date = date;
        this.productList = new HashMap<>();
        this.totalAmount = 0.0;
        this.status = "Pending";
    }

    // Getters and Setters
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public Map<Product, Integer> getProductList() { return productList; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public Date getDate() { return date; }

    // Methods from UML
    public void addItem(Product product, int quantity) {
        productList.put(product, productList.getOrDefault(product, 0) + quantity);
        totalAmount += product.getPrice() * quantity;
        System.out.println("Added " + quantity + " of " + product.getName() + " to order #" + orderId);
    }

    public void removeItem(int productId) {
        Product toRemove = null;
        for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
            if (entry.getKey().getProductId() == productId) {
                toRemove = entry.getKey();
                totalAmount -= entry.getKey().getPrice() * entry.getValue();
                break;
            }
        }
        if (toRemove != null) {
            productList.remove(toRemove);
            System.out.println("Removed product ID: " + productId + " from order #" + orderId);
        }
    }

    public void processOrder() {
        this.status = "Processed";
        System.out.println("Order #" + orderId + " processed.");
    }

    public void cancelOrder() {
        this.status = "Cancelled";
        System.out.println("Order #" + orderId + " cancelled.");
    }

    public void updateStatus(String status) {
        this.status = status;
        System.out.println("Order #" + orderId + " status updated to: " + status);
    }

    @Override
    public String toString() {
        return String.format("Order{id=%d, customerId=%d, total=%.2f, status='%s', items=%d}", 
                           orderId, customerId, totalAmount, status, productList.size());
    }
}