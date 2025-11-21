package com.warehouse.dao;

import com.warehouse.models.Warehouse;
import com.warehouse.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDAO {
    
    // CREATE - Add new warehouse
    public boolean addWarehouse(Warehouse warehouse) {
        String sql = "INSERT INTO warehouses (name, location, capacity, current_stock) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, warehouse.getName());
            pstmt.setString(2, warehouse.getLocation());
            pstmt.setInt(3, warehouse.getCapacity());
            pstmt.setInt(4, warehouse.getCurrentStock());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // If you add setId method to Warehouse model
                        // warehouse.setWarehouseId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Warehouse added: " + warehouse.getName());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding warehouse: " + e.getMessage());
        }
        return false;
    }
    
    // READ - Get all warehouses
    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = "SELECT * FROM warehouses";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Warehouse warehouse = new Warehouse(
                    rs.getInt("warehouse_id"),
                    rs.getString("name"),
                    rs.getString("location"),
                    rs.getInt("capacity")
                );
                // Note: You'll need to add setCurrentStock method to Warehouse model
                warehouses.add(warehouse);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting warehouses: " + e.getMessage());
        }
        
        return warehouses;
    }
    
    // READ - Get warehouse by ID
    public Warehouse getWarehouseById(int warehouseId) {
        String sql = "SELECT * FROM warehouses WHERE warehouse_id = ?";
        Warehouse warehouse = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, warehouseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    warehouse = new Warehouse(
                        rs.getInt("warehouse_id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getInt("capacity")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting warehouse: " + e.getMessage());
        }
        
        return warehouse;
    }
    
    // Add product to warehouse (warehouse_products table)
    public boolean addProductToWarehouse(int warehouseId, int productId, int quantity) {
        String sql = "INSERT INTO warehouse_products (warehouse_id, product_id, quantity) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, warehouseId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.setInt(4, quantity);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                updateWarehouseStock(warehouseId);
                System.out.println("✅ Product added to warehouse");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding product to warehouse: " + e.getMessage());
        }
        return false;
    }
    
    // Get products in a specific warehouse
    public List<Product> getProductsInWarehouse(int warehouseId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.*, wp.quantity FROM products p " +
                    "JOIN warehouse_products wp ON p.product_id = wp.product_id " +
                    "WHERE wp.warehouse_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, warehouseId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"), // This is from warehouse_products
                        rs.getDouble("price"),
                        rs.getDate("expiry_date"),
                        rs.getInt("supplier_id")
                    );
                    products.add(product);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting warehouse products: " + e.getMessage());
        }
        
        return products;
    }
    
    // Update warehouse stock count
    private void updateWarehouseStock(int warehouseId) {
        String sql = "UPDATE warehouses SET current_stock = " +
                    "(SELECT COALESCE(SUM(quantity), 0) FROM warehouse_products WHERE warehouse_id = ?) " +
                    "WHERE warehouse_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, warehouseId);
            pstmt.setInt(2, warehouseId);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating warehouse stock: " + e.getMessage());
        }
    }
}