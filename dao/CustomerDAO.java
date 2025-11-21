package com.warehouse.dao;

import com.warehouse.models.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    // CREATE - Add new customer
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, contact_info, address) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getContactInfo());
            pstmt.setString(3, customer.getAddress());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setPersonId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Customer added: " + customer.getName());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding customer: " + e.getMessage());
        }
        return false;
    }
    
    // READ - Get all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("contact_info"),
                    rs.getString("address")
                );
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting customers: " + e.getMessage());
        }
        
        return customers;
    }
}