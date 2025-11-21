package com.warehouse.models;

import java.util.ArrayList;
import java.util.List;

public class Warehouse {
    private int warehouseId;
    private String name;
    private String location;
    private int capacity;
    private int currentStock;
    private List<Product> products;

    public Warehouse(int warehouseId, String name, String location, int capacity) {
        this.warehouseId = warehouseId;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.currentStock = 0;
        this.products = new ArrayList<>();
    }

    // Getters and Setters
    public int getWarehouseId() { return warehouseId; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public int getCurrentStock() { return currentStock; }
    public List<Product> getProducts() { return products; }

    // Methods from UML
    public void addProduct(Product product, int quantity) {
        if (currentStock + quantity <= capacity) {
            // Check if product already exists
            boolean productExists = false;
            for (Product p : products) {
                if (p.getProductId() == product.getProductId()) {
                    p.updateStock(quantity);
                    productExists = true;
                    break;
                }
            }
            
            if (!productExists) {
                Product newProduct = new Product(
                    product.getProductId(), product.getName(), product.getCategory(),
                    quantity, product.getPrice(), product.getExpiryDate(), product.getSupplierId()
                );
                products.add(newProduct);
            }
            
            currentStock += quantity;
            System.out.println("Added " + quantity + " of " + product.getName() + " to warehouse " + name);
        } else {
            System.out.println("Cannot add product. Warehouse capacity exceeded.");
        }
    }

    public void removeProduct(int productId, int quantity) {
        for (Product product : products) {
            if (product.getProductId() == productId) {
                if (product.getQuantity() >= quantity) {
                    product.updateStock(-quantity);
                    currentStock -= quantity;
                    System.out.println("Removed " + quantity + " of product ID " + productId);
                    
                    // Remove product if quantity becomes zero
                    if (product.getQuantity() == 0) {
                        products.remove(product);
                    }
                } else {
                    System.out.println("Not enough stock to remove. Available: " + product.getQuantity());
                }
                return;
            }
        }
        System.out.println("Product not found in warehouse: ID " + productId);
    }

    public int checkAvailability(int productId) {
        for (Product product : products) {
            if (product.getProductId() == productId) {
                return product.getQuantity();
            }
        }
        return 0;
    }

    public void generateReport() {
        System.out.println("\n=== Warehouse Report ===");
        System.out.println("Warehouse: " + name);
        System.out.println("Location: " + location);
        System.out.println("Capacity: " + capacity);
        System.out.println("Current Stock: " + currentStock);
        System.out.println("Utilization: " + (currentStock * 100.0 / capacity) + "%");
        System.out.println("Products:");
        for (Product product : products) {
            System.out.println("  - " + product.getName() + " (ID: " + product.getProductId() + 
                             ", Qty: " + product.getQuantity() + ")");
        }
    }

    @Override
    public String toString() {
        return String.format("Warehouse{id=%d, name='%s', location='%s', capacity=%d, stock=%d}", 
                           warehouseId, name, location, capacity, currentStock);
    }
}