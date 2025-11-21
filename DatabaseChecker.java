package com.warehouse;  // Or adjust package as needed

import java.sql.*;

public class DatabaseChecker {
    
    public static void checkDatabaseSetup() {
        String[] requiredTables = {"products", "suppliers", "warehouses", "employees", "customers", "orders"};
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            
            System.out.println("🔍 Checking database setup...");
            System.out.println("Connected to: " + conn.getMetaData().getURL());
            
            boolean allTablesExist = true;
            
            for (String table : requiredTables) {
                ResultSet tables = meta.getTables(null, null, table, null);
                if (tables.next()) {
                    System.out.println("✅ Table found: " + table);
                } else {
                    System.out.println("❌ Table missing: " + table);
                    allTablesExist = false;
                }
            }
            
            if (allTablesExist) {
                System.out.println("\n🎉 All tables are ready! Database setup is complete.");
            } else {
                System.out.println("\n💡 Some tables are missing. Please run the SQL script to create them.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking database: " + e.getMessage());
            System.err.println("💡 Make sure:");
            System.err.println("   1. MySQL is running");
            System.err.println("   2. Database 'warehouse' exists");
            System.err.println("   3. Connection settings are correct");
        }
    }
    
    // Additional method to test basic connection
    public static void testConnection() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            System.out.println("✅ Database connection successful!");
            System.out.println("   Database: " + conn.getCatalog());
            System.out.println("   MySQL Version: " + conn.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
        }
    }
}