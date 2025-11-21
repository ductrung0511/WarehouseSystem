package com.warehouse;

import com.warehouse.services.*;
import com.warehouse.models.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Testing Service Layer\n");
        
        WarehouseService warehouseService = new WarehouseService();
        OrderService orderService = new OrderService();
        InventoryService inventoryService = new InventoryService();
        
        // Test 1: Inventory Service
        System.out.println("=== INVENTORY SERVICE ===");
        System.out.println(inventoryService.getInventorySummary());
        
        System.out.println("\n=== LOW STOCK PRODUCTS ===");
        List<Product> lowStock = inventoryService.getLowStockProducts(20);
        lowStock.forEach(p -> System.out.println("⚠️ " + p.getName() + " - " + p.getQuantity() + " left"));
        
        // Test 2: Warehouse Service Reports
        System.out.println("\n=== WAREHOUSE REPORTS ===");
        System.out.println(warehouseService.generateInventoryReport());
        System.out.println(warehouseService.generateWarehouseReport());
        
        // Test 3: Order Service
        System.out.println("=== ORDER SERVICE ===");
        System.out.println("Total Orders: " + orderService.getAllOrders().size());
        
        // Test 4: Business Logic - Process a Sale
        System.out.println("\n=== PROCESSING SALE ===");
        List<Customer> customers = warehouseService.getAllCustomers();
        List<Product> products = warehouseService.getAllProducts();
        
        if (!customers.isEmpty() && !products.isEmpty()) {
            boolean saleSuccess = warehouseService.processSale(
                customers.get(0).getPersonId(), 
                products.get(1).getProductId(), 
                2
            );
            System.out.println(saleSuccess ? "✅ Sale processed successfully!" : "❌ Sale failed");
        }
        
        // Test 5: Add New Product via Service
        System.out.println("\n=== ADDING NEW PRODUCT ===");
        boolean productAdded = warehouseService.addProduct(
            "Gaming Monitor", 
            "Electronics", 
            25, 
            299.99, 
            new Date(), 
            1
        );
        System.out.println(productAdded ? "✅ Product added via service!" : "❌ Product addition failed");
        
        System.out.println("\n🎉 Service Layer Testing Complete!");
        System.out.println("\n📝 Your application is now ready for GUI development!");
        System.out.println("Next: Create Swing GUI that uses these services");
    }
}