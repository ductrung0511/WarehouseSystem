package com.warehouse.dao;

import com.warehouse.models.Order;
import com.warehouse.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    
    // CREATE - Create new order
    public boolean createOrder(Order order) {
        String orderSql = "INSERT INTO orders (customer_id, total_amount, status) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
            
            orderStmt.setInt(1, order.getCustomerId());
            orderStmt.setDouble(2, order.getTotalAmount());
            orderStmt.setString(3, order.getStatus());
            
            int affectedRows = orderStmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the generated order ID
                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int orderId = generatedKeys.getInt(1);
                        
                        // Add order items
                        addOrderItems(orderId, order, conn);
                        
                        System.out.println("✅ Order created: #" + orderId);
                        return true;
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
        }
        return false;
    }
    
    // Helper method to add order items
    private void addOrderItems(int orderId, Order order, Connection conn) throws SQLException {
        String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement itemStmt = conn.prepareStatement(itemSql)) {
            for (var entry : order.getProductList().entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, product.getProductId());
                itemStmt.setInt(3, quantity);
                itemStmt.setDouble(4, product.getPrice());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();
        }
    }
    
    // READ - Get all orders
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Order order = new Order(
                    rs.getInt("order_id"),
                    rs.getInt("customer_id"),
                    rs.getDate("order_date")
                );
                order.updateStatus(rs.getString("status"));
                orders.add(order);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting orders: " + e.getMessage());
        }
        
        return orders;
    }
    
    // UPDATE - Update order status
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Order status updated: #" + orderId + " to " + status);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating order status: " + e.getMessage());
        }
        return false;
    }
}