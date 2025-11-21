package com.warehouse.dao;

import com.warehouse.models.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    
    // CREATE - Add new supplier
    public boolean addSupplier(Supplier supplier) {
        String sql = "INSERT INTO suppliers (name, contact_info, address) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContactInfo());
            pstmt.setString(3, supplier.getAddress());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        supplier.setPersonId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Supplier added: " + supplier.getName());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding supplier: " + e.getMessage());
        }
        return false;
    }
    
    // READ - Get all suppliers
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM suppliers";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Supplier supplier = new Supplier(
                    rs.getInt("supplier_id"),
                    rs.getString("name"),
                    rs.getString("contact_info"),
                    rs.getString("address")
                );
                suppliers.add(supplier);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting suppliers: " + e.getMessage());
        }
        
        return suppliers;
    }
    
    // READ - Get supplier by ID
    public Supplier getSupplierById(int supplierId) {
        String sql = "SELECT * FROM suppliers WHERE supplier_id = ?";
        Supplier supplier = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    supplier = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("address")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting supplier: " + e.getMessage());
        }
        
        return supplier;
    }
}