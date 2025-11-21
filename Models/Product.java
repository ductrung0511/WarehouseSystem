package com.warehouse.models;

import java.util.Date;

public class Product {
    private int productId;
    private String name;
    private String category;
    private int quantity;
    private double price;
    private Date expiryDate;
    private int supplierId;

    public Product(int productId, String name, String category, int quantity, 
                  double price, Date expiryDate, int supplierId) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    // Methods from UML
    public void updateStock(int amount) {
        this.quantity += amount;
        System.out.println("Stock updated for " + name + ". New quantity: " + quantity);
    }

    public void updatePrice(double newPrice) {
        this.price = newPrice;
        System.out.println("Price updated for " + name + ". New price: " + newPrice);
    }

    public void markAsExpired() {
        System.out.println("Product " + name + " has been marked as expired.");
    }

    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', category='%s', quantity=%d, price=%.2f}", 
                           productId, name, category, quantity, price);
    }
}