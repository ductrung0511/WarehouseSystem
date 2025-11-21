package com.warehouse.dao;

import com.warehouse.models.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    
    // CREATE - Add new employee
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO employees (name, position, shift, salary) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getPosition());
            pstmt.setString(3, employee.getShift());
            pstmt.setDouble(4, employee.getSalary());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setPersonId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("✅ Employee added: " + employee.getName());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding employee: " + e.getMessage());
        }
        return false;
    }
    
    // READ - Get all employees
    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Employee employee = new Employee(
                    rs.getInt("employee_id"),
                    rs.getString("name"),
                    "", // contactInfo - you might want to add this to your table
                    "", // address - you might want to add this to your table
                    rs.getString("position"),
                    rs.getString("shift"),
                    rs.getDouble("salary")
                );
                employees.add(employee);
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting employees: " + e.getMessage());
        }
        
        return employees;
    }
    
    // READ - Get employee by ID
    public Employee getEmployeeById(int employeeId) {
        String sql = "SELECT * FROM employees WHERE employee_id = ?";
        Employee employee = null;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        "", // contactInfo
                        "", // address
                        rs.getString("position"),
                        rs.getString("shift"),
                        rs.getDouble("salary")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error getting employee: " + e.getMessage());
        }
        
        return employee;
    }
    
    // UPDATE - Update employee information
    public boolean updateEmployee(Employee employee) {
        String sql = "UPDATE employees SET name = ?, position = ?, shift = ?, salary = ? WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getPosition());
            pstmt.setString(3, employee.getShift());
            pstmt.setDouble(4, employee.getSalary());
            pstmt.setInt(5, employee.getPersonId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Employee updated: " + employee.getName());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating employee: " + e.getMessage());
        }
        return false;
    }
    
    // DELETE - Remove employee
    public boolean deleteEmployee(int employeeId) {
        String sql = "DELETE FROM employees WHERE employee_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, employeeId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Employee deleted: ID " + employeeId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting employee: " + e.getMessage());
        }
        return false;
    }
}