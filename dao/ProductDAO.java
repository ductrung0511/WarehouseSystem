package com.warehouse.dao;

import com.warehouse.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    
    // CREATE - Add new product to database
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO products (name, category, quantity, price, expiry_date, supplier_id) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setDouble(4, product.getPrice());
            
            if (product.getExpiryDate() != null) {
                pstmt.setDate(5, new java.sql.Date(product.getExpiryDate().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            
            pstmt.setInt(6, product.getSupplierId());
            
            int affectedRows = pstmt.executeUpdate();
            
            // Get the auto-generated product_id
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Product added to database: " + product.getName());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding product: " + e.getMessage());
        }
        return false;
    }
    
    // READ - Get product by ID
    public Product getProductById(int productId) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        Product product = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product = extractProductFromResultSet(rs);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting product: " + e.getMessage());
        }
        
        return product;
    }
    
    // READ - Get all products
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                products.add(product);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting all products: " + e.getMessage());
        }
        
        return products;
    }
    
    // READ - Get products by category
    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = extractProductFromResultSet(rs);
                    products.add(product);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting products by category: " + e.getMessage());
        }
        
        return products;
    }
    
    // UPDATE - Update product information
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET name = ?, category = ?, quantity = ?, price = ?, expiry_date = ?, supplier_id = ? WHERE product_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getCategory());
            pstmt.setInt(3, product.getQuantity());
            pstmt.setDouble(4, product.getPrice());
            
            if (product.getExpiryDate() != null) {
                pstmt.setDate(5, new java.sql.Date(product.getExpiryDate().getTime()));
            } else {
                pstmt.setNull(5, Types.DATE);
            }
            
            pstmt.setInt(6, product.getSupplierId());
            pstmt.setInt(7, product.getProductId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Product updated: " + product.getName());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating product: " + e.getMessage());
        }
        return false;
    }
    
    // UPDATE - Update only product stock
    public boolean updateProductStock(int productId, int newQuantity) {
        String sql = "UPDATE products SET quantity = ? WHERE product_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, productId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Stock updated for product ID " + productId + ": " + newQuantity);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating stock: " + e.getMessage());
        }
        return false;
    }
    
    // DELETE - Remove product from database
    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Product deleted: ID " + productId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting product: " + e.getMessage());
        }
        return false;
    }
    
    // Helper method to extract Product from ResultSet
    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getInt("quantity"),
            rs.getDouble("price"),
            rs.getDate("expiry_date"),
            rs.getInt("supplier_id")
        );
    }
}