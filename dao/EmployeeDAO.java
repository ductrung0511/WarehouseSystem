

package com.warehouse.dao;

import com.warehouse.models.Employee;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
// Createnew employee; 
    
    public boolean addEmployee(Employee emp) {
        String sql = "   insert into employees (name, position, shift, salary) VALUES ( ?, ? , ? , ? )";
        
        try (
                
                Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                
                )
            
        {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getPosition());
            ps.setString(3, emp.getShift());
            ps.setDouble(4, emp.getSalary());
            
            int returnRows = ps.executeUpdate();
            
            if(returnRows > 0 ) {
                try(ResultSet gk = ps.getGeneratedKeys()){
                    if (gk.next()){
                        emp.setPersonId(gk.getInt(1));}
                }
                return true;
            }
            
        } catch(SQLException e) {
         // Output error
        }
        return false;
    }
    
    public List<Employee> getAllEmployees(){
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * from employees";
        
        try(Connection conn = DatabaseConfig.getConnection(); 
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql))
        {
            while(rs.next()) {
                Employee emp = new Employee(
                    rs.getInt("employee_id"),
                      rs.getString("name"),
                    "",
                    "",
                    rs.getString("position"),
                    rs.getString("shift"),
                     rs.getDouble("salary")
                );
                employees.add(emp);
            }
        } 
        catch(SQLException e){
        System.err.println(" ERR in GETal emp");
        }
        
        return employees;
    }
    
    // Read by Id of emp
    public Employee getEmployeeById(int empId){
        String sql = "Select * from employees where employee_id = ? ";
        Employee employee = null;
        
        try( Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
                
                ps.setInt(1, empId);
                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()){
                    employee = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("name"),
                        "",
                        "", // address
                           rs.getString("position"),
                            rs.getString("shift"),
                            rs.getDouble("salary")
                            
                            
                        );
                    }
                }
            }
        catch  (SQLException e) {System.err.println("SQL Error in get empt by id");}
        
        return employee;
        
    }
    
    // Update the emp info from the input Object
    
    public boolean updateEmployee (Employee employee) {
        
    
        String sql = "update employees set name = ?, position = ?, shift = ?, salary = ? where employee_id = ? ";
        try( Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, employee.getName());
            ps.setString(2, employee.getPosition()) ;
            ps.setString(3, employee.getShift());
            ps.setDouble(4, employee.getSalary());
            ps.setInt(5, employee.getPersonId());
            
            int result = ps.executeUpdate();
            if(result > 0) {
                return true; // Scucess
            }
            
        }
        catch(SQLException e) {
        
        }
        return false;
        }
    
    
        public boolean deleteEmployee(int empId) {
            String sql = "delete from employees where employee_id = ?"; 
            
            try (Connection c = DatabaseConfig.getConnection();
                    PreparedStatement ps = c.prepareStatement(sql)){
                ps.setInt(1, empId);
                int result = ps.executeUpdate();
                if (result > 0) {
                return true;
                }
                
            
            } 
            catch (SQLException e) {
            }
                        return false;

        }
    }

    
    
    

