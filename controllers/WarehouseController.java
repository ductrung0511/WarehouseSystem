package com.warehouse.controllers;

import com.warehouse.services.WarehouseService;
import com.warehouse.services.InventoryService;
import com.warehouse.models.Warehouse;
import com.warehouse.models.Product;
import java.util.List;
import java.util.Date;

public class WarehouseController {
    private WarehouseService warehouseService;
    private InventoryService inventoryService;
    
    public WarehouseController() {
        this.warehouseService = new WarehouseService();
        this.inventoryService = new InventoryService();
    }
    
    public List<Warehouse> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }
    
        // FIXED METHOD - Creates Product object before calling Service
    public boolean addProduct(String name, String category, int quantity, 
                             double price, Date expiryDate, int supplierId) {
        // Input validation (Controller responsibility)
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        
        // ✅ Create Product object here (Controller job)
        Product product = new Product(0, name, category, quantity, price, expiryDate, supplierId);
        
        // ✅ Call Service with Product object
        return warehouseService.addProduct(product);
    }
    
    public boolean addProductToWarehouse(int warehouseId, int productId, int quantity) {
        if (warehouseId <= 0) {
            throw new IllegalArgumentException("Warehouse ID must be positive");
        }
        if (productId <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return warehouseService.addProductToWarehouse(warehouseId, productId, quantity);
    }
    
    public List<Product> getProductsInWarehouse(int warehouseId) {
        if (warehouseId <= 0) {
            throw new IllegalArgumentException("Warehouse ID must be positive");
        }
        return warehouseService.getProductsInWarehouse(warehouseId);
    }
    
    public String generateInventoryReport() {
        return warehouseService.generateInventoryReport();
    }
    
    public String generateWarehouseReport() {
        return warehouseService.generateWarehouseReport();
    }
    
    public String getInventorySummary() {
        return inventoryService.getInventorySummary();
    }
    
    public List<Product> getLowStockProducts(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        return inventoryService.getLowStockProducts(threshold);
    }
    
    public boolean restockProduct(int productId, int quantity) {
        if (productId <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return inventoryService.restockProduct(productId, quantity);
    }
    //    public boolean addWarehouse(String name, ... )
    // Add this method to WarehouseController.java
    public boolean addWarehouse(String name, String location, int capacity) {
        // Input validation
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse name cannot be empty");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        // Create Warehouse object
        Warehouse warehouse = new Warehouse(0, name, location, capacity);

        // Call Service with Warehouse object
        return warehouseService.addWarehouse(warehouse);
    }
}