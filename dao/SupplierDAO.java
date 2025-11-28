package com.warehouse.dao;

import com.warehouse.models.Supplier;
import java.util.*;
import java.sql.*;


public class SupplierDAO {
    public boolean addSupplier (Supplier s){
        String sql = "insert into suppliers (name, contact_info, address) values (?,?,?)";
           try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS )){
        ps.setString(1, s.getName());
        ps.setString(2, s.getContactInfo());
        ps.setString(3, s.getAddress());

        int result = ps.executeUpdate();
        if (result > 0) {
             try (ResultSet gk = ps.getGeneratedKeys()) {
                    if(gk.next()){
                    s.setPersonId(gk.getInt(1));
                    System.out.println("Success in SDAO - New supplier ID: " + gk.getInt(1));

                    }
                    System.out.println("Success in SDAO");
                    return true;
                   }




        }


        }
        catch (SQLException e){
            System.out.println("Failed SDAO");
             System.out.println("Failed SDAO: " + e.getMessage());
        e.printStackTrace(); // This will show the exact error
        } 
            return false;
        
    }
    
    public List<Supplier> getAllSuppliers() {
        List<Supplier> ss = new ArrayList<>();
        String sql = "select * from suppliers";
    
        try(Connection c = DatabaseConfig.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)){

            while(rs.next()){
                Supplier s = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("address")
                );
                ss.add(s);


        }
            } catch(SQLException e) {

                    }
        return ss;
        }
    
     public boolean updateSupplier(Supplier supplier) {
        String sql = "UPDATE suppliers SET name = ?, contact_info = ?, address = ? WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, supplier.getName());
            pstmt.setString(2, supplier.getContactInfo());
            pstmt.setString(3, supplier.getAddress());
            pstmt.setInt(4, supplier.getPersonId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }
     
    public boolean deleteSupplier(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }
    
        public Supplier getSupplierById(int sid) {
            String sql = "select * from supplier where supplier_id = ? ";
            Supplier s = null;
            
            try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, sid);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                s = new Supplier(
                        rs.getInt("supplier_id"),
                        rs.getString("name"),
                        rs.getString("contact_info"),
                        rs.getString("address")
                );
                
                
                }}
            
        }
        catch (SQLException e ) {
            
            }
            
        return s;
                    
                    
        }

    
    
}
    
    


      









