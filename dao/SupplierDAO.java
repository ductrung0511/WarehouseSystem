package com.warehouse.dao;

import com.warehouse.models.Supplier;
import java.util.*;
import java.sql.*;


public class SupplierDAO {
    public boolean addSupplier (Supplier s){
        String sql = "insert into suppliers (name, contact_info, address values (?,?,?)";
           try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
        ps.setString(1, s.getName());
        ps.setString(2, s.getContactInfo());
        ps.setString(3, s.getAddress());

        int result = ps.executeUpdate();
        if (result > 0) {
             try (ResultSet gk = ps.getGeneratedKeys()) {
                    if(gk.next()){
                    s.setPersonId(gk.getInt(1));

                    }
                    return true;
                   }




        }


        }
        catch (SQLException e){} // output ..
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
    
    


      