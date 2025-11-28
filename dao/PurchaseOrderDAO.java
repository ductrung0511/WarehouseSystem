package com.warehouse.dao;

import com.warehouse.models.PurchaseOrder;
import com.warehouse.models.Product;
import com.warehouse.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PurchaseOrderDAO {
    
    public boolean createPurchaseOrder(int supplierId, Map<Product, Integer> productList) {
        
        
        
        Connection conn = null;
        PreparedStatement poStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet generatedKeys = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Insert purchase order header
            String poSql = "INSERT INTO warehouse.purchase_orders (supplier_id, total_cost, status) VALUES (?, ?, 'Pending')";
            poStmt = conn.prepareStatement(poSql, Statement.RETURN_GENERATED_KEYS);
            
            // Calculate total cost using product prices
            double totalCost = 0.0;
            for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                System.out.println("quantity : " + quantity + " product: " + product.getName());
                totalCost += product.getPrice() * quantity;
            }
            
            poStmt.setInt(1, supplierId);
            poStmt.setDouble(2, totalCost);
            
            int poRows = poStmt.executeUpdate();
            if (poRows == 0) {
                throw new SQLException("Creating purchase order failed, no rows affected.");
            }
            
            // Get the generated purchase order ID
            int purchaseOrderId;
            generatedKeys = poStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                purchaseOrderId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating purchase order failed, no ID obtained.");
            }
            
            // 2. Insert purchase order items
            String itemSql = "INSERT INTO warehouse.purchase_order_items (po_id, product_id, quantity, unit_cost) VALUES (?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);
            
            for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                
                itemStmt.setInt(1, purchaseOrderId);
                itemStmt.setInt(2, product.getProductId());
                itemStmt.setInt(3, quantity);
                itemStmt.setDouble(4, product.getPrice()); // Use product's price as unit cost
                itemStmt.addBatch();
            }
            
            int[] itemResults = itemStmt.executeBatch();
            
            // Verify all items were inserted
            for (int result : itemResults) {
                if (result == PreparedStatement.EXECUTE_FAILED) {
                    throw new SQLException("Failed to insert one or more purchase order items.");
                }
            }
            
            conn.commit(); // Commit transaction
            System.out.println("Successfully created purchase order #" + purchaseOrderId + " with " + productList.size() + " items");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // Rollback on error
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("Error creating purchase order: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (itemStmt != null) itemStmt.close();
                if (poStmt != null) poStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
//    public List<PurchaseOrder> getAllPurchaseOrders() {
//        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
//        String sql = "SELECT po_id, supplier_id, total_cost, order_date, status FROM warehouse.purchase_orders ORDER BY order_date DESC";
//        
//        try (Connection conn = DatabaseConfig.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//            
//            while (rs.next()) {
//                PurchaseOrder po = new PurchaseOrder(
//                    rs.getInt("po_id"),
//                    rs.getInt("supplier_id"),
//                    new java.util.Date(rs.getTimestamp("order_date").getTime())
//                );
//                po.updateTotalCost(rs.getDouble("total_cost"));
//                po.updateStatus(rs.getString("status"));
//                
//                // Load products for this purchase order
//                Map<Product, Integer> productList = getPurchaseOrderProducts(rs.getInt("po_id"));
//                for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
//                    po.getProductList().put(entry.getKey(), entry.getValue());
//                }
//                
//                purchaseOrders.add(po);
//            }
//            
//        } catch (SQLException e) {
//            System.err.println("Error getting all purchase orders: " + e.getMessage());
//        }
//        
//        return purchaseOrders;
//    }
//   
    
//    public List<PurchaseOrder> getAllPurchaseOrders() {
//    List<PurchaseOrder> purchaseOrders = new ArrayList<>();
//    String sql = "SELECT " +
//            "FROM warehouse.purchase_order_items poi  join warehouse.purchase_orders po ON poi.po_id = po.po_id " +
//            "WHERE poi.po_id = ?;";
//    
//    try (Connection conn = DatabaseConfig.getConnection();
//         PreparedStatement pstmt = conn.prepareStatement(sql);
//         ResultSet rs = pstmt.executeQuery()) {
//        
//        while (rs.next()) {
//            PurchaseOrder po = new PurchaseOrder(
//                rs.getInt("po_id"),
//                rs.getInt("supplier_id"),
//                new java.util.Date(rs.getTimestamp("order_date").getTime())
//            );
//            po.updateStatus(rs.getString("status"));
//            po.updateTotalCost(rs.getDouble("total_cost"));
//            
//            // Load products for this purchase order
//            Map<Product, Integer> productList = getPurchaseOrderProducts(rs.getInt("po_id"));
//            for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
//                po.getProductList().put(entry.getKey(), entry.getValue());
//            }
//            
//            purchaseOrders.add(po);
//        }
//        
//    } catch (SQLException e) {
//        System.err.println("Error getting all purchase orders: " + e.getMessage());
//    }
//    
//    return purchaseOrders;
//} 
    
    
    public List<PurchaseOrder> getAllPurchaseOrders() {
    List<PurchaseOrder> purchaseOrders = new ArrayList<>();
    String sql = "SELECT po.po_id, po.supplier_id, po.total_cost, po.order_date, po.status " +
                "FROM purchase_orders po join purchase_order_items poi on poi.po_id = po.po_id " +
                "ORDER BY po.order_date DESC";
    
    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            PurchaseOrder po = new PurchaseOrder(
                rs.getInt("po_id"),
                rs.getInt("supplier_id"),
                new java.util.Date(rs.getTimestamp("order_date").getTime())
            );
            po.updateStatus(rs.getString("status"));
            
            
            
            
            // Load products for this purchase order
            Map<Product, Integer> productList = getPurchaseOrderProducts(rs.getInt("po_id"));
            for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
//                po.getProductList().put(entry.getKey(), entry.getValue());
                    po.addProduct(entry.getKey(), entry.getValue());
            }
            purchaseOrders.add(po);
            
        }
        
    } catch (SQLException e) {
        System.err.println("Error getting all purchase orders: " + e.getMessage());
    }
    
    return purchaseOrders;
}
    public PurchaseOrder getPurchaseOrderById(int poId) {
        String sql = "SELECT po_id, supplier_id, total_cost, order_date, status FROM warehouse.purchase_orders WHERE po_id = ?";
        PurchaseOrder purchaseOrder = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, poId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                purchaseOrder = new PurchaseOrder(
                    rs.getInt("po_id"),
                    rs.getInt("supplier_id"),
                    new java.util.Date(rs.getTimestamp("order_date").getTime())
                );
                purchaseOrder.updateStatus(rs.getString("status"));
                
                // Load products for this purchase order
                Map<Product, Integer> productList = getPurchaseOrderProducts(poId);
                for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
                    purchaseOrder.getProductList().put(entry.getKey(), entry.getValue());
                }
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting purchase order by ID: " + e.getMessage());
        }
        
        return purchaseOrder;
    }
    
    private Map<Product, Integer> getPurchaseOrderProducts(int poId) {
        Map<Product, Integer> productList = new java.util.HashMap<>();
//        String sql = "SELECT p.*, poi.quantity " +
//                    "FROM warehouse.purchase_order_items poi " +
//                    "JOIN products p ON poi.product_id = p.product_id " +
//                    "WHERE poi.po_id = ?";
//        
          String sql = "SELECT p.*, poi.quantity as order_quantity " +  // ← ALIAS the quantity
                "FROM purchase_order_items poi " +
                "JOIN products p ON poi.product_id = p.product_id " +
                "WHERE poi.po_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, poId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("price")
                );
                int quantity = rs.getInt("order_quantity");
                productList.put(product, quantity);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting purchase order products: " + e.getMessage());
        }
        
        return productList;
    }
    
    public boolean updatePurchaseOrderStatus(int poId, String status) {
        String sql = "UPDATE warehouse.purchase_orders SET status = ? WHERE po_id = ?";

        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, poId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating purchase order status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deletePurchaseOrder(int poId) {
        Connection conn = null;
        PreparedStatement deleteItemsStmt = null;
        PreparedStatement deletePOStmt = null;
        
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // First delete items
            String deleteItemsSql = "DELETE FROM warehouse.purchase_order_items WHERE po_id = ?";
            deleteItemsStmt = conn.prepareStatement(deleteItemsSql);
            deleteItemsStmt.setInt(1, poId);
            deleteItemsStmt.executeUpdate();
            
            // Then delete the purchase order
            String deletePOSql = "DELETE FROM warehouse.purchase_orders WHERE po_id = ?";
            deletePOStmt = conn.prepareStatement(deletePOSql);
            deletePOStmt.setInt(1, poId);
            int affectedRows = deletePOStmt.executeUpdate();
            
            conn.commit();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            System.err.println("Error deleting purchase order: " + e.getMessage());
            return false;
        } finally {
            try {
                if (deleteItemsStmt != null) deleteItemsStmt.close();
                if (deletePOStmt != null) deletePOStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = "SELECT po_id, supplier_id, total_cost, order_date, status FROM warehouse.purchase_orders WHERE status = ? ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PurchaseOrder po = new PurchaseOrder(
                    rs.getInt("po_id"),
                    rs.getInt("supplier_id"),
                    new java.util.Date(rs.getTimestamp("order_date").getTime())
                );
                po.updateStatus(rs.getString("status"));
                
                // Load products for this purchase order
                Map<Product, Integer> productList = getPurchaseOrderProducts(rs.getInt("po_id"));
                for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
                    po.getProductList().put(entry.getKey(), entry.getValue());
                }
                
                purchaseOrders.add(po);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting purchase orders by status: " + e.getMessage());
        }
        
        return purchaseOrders;
    }
    
    public List<PurchaseOrder> getPurchaseOrdersBySupplier(int supplierId) {
        List<PurchaseOrder> purchaseOrders = new ArrayList<>();
        String sql = "SELECT po_id, supplier_id, total_cost, order_date, status FROM warehouse.purchase_orders WHERE supplier_id = ? ORDER BY order_date DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PurchaseOrder po = new PurchaseOrder(
                    rs.getInt("po_id"),
                    rs.getInt("supplier_id"),
                    new java.util.Date(rs.getTimestamp("order_date").getTime())
                );
                po.updateStatus(rs.getString("status"));
                
                // Load products for this purchase order
                Map<Product, Integer> productList = getPurchaseOrderProducts(rs.getInt("po_id"));
                for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
                    po.getProductList().put(entry.getKey(), entry.getValue());
                }
                
                purchaseOrders.add(po);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error getting purchase orders by supplier: " + e.getMessage());
        }
        
        return purchaseOrders;
    }
}