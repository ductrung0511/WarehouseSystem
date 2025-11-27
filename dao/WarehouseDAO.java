package com.warehouse.dao;

import com.warehouse.models.Warehouse;
import com.warehouse.models.Product;
import java.util.*;
import java.sql.*;


public class WarehouseDAO{
    public boolean addWarehouse(Warehouse w) {
        String sql = " insert into warehouses (name, location, capacity, current_stock ) values (?,?,?,?)";

    try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
    ps.setString(1, w.getLocation());
    ps.setString(2, w.getLocation());
    ps.setInt(3, w.getCapacity());
    ps.setInt(4, w.getCurrentStock());
    
    int result = ps.executeUpdate();
    if (result > 0) {
         try (ResultSet gk = ps.getGeneratedKeys()) {
                if(gk.next()){
                w.setWarehouseId(gk.getInt(1));
                
                }
                return true;
               }
         
        
    }
        
    
    }
    catch (SQLException e){} // output ..
        return false;
    }
    
    
    
    
    
    // Read all warehouses
    public List<Warehouse> getAllWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        String sql = "select * from warehouses";
        
        try(Connection c = DatabaseConfig.getConnection();
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)){

            while(rs.next()){
                Warehouse s = new Warehouse(
                        rs.getInt("warehouse_id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getInt("capacity")
                );
                warehouses.add(s);
        }
            } catch(SQLException e) {

                    }
        return warehouses ;

        
    }
    
    public Warehouse getWarehouseById(int id){
        
        String sql = "select * from warehouses where wahouse_id = ? ";
            Warehouse w = null;
            
            try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                w = new Warehouse(
                        rs.getInt("warehouse_id"),
                        rs.getString("name"),
                        rs.getString("location"),
                        rs.getInt("capacity")
                );
                
                
                }}
            
        }
        catch (SQLException e ) {
            
            }
            
        return w;
                    
                    
        }
    
    
    // Product inserted to warehouse with ids and quantity to warehouse-p relationship
    public boolean addProductToWarehouse( int wId, int pId, int q) {
        String sql = "insert into warehouse_prroducts (warehouse_id, product_id, quantity) values (?, ?, ?) "
                + "on duplicate key update quantity = quantity + ?";
        
           try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
    ps.setInt(1, wId);
    ps.setInt(2, pId);
    ps.setInt(3, q);
    ps.setInt(4, q);
    
    int result = ps.executeUpdate();
    if (result > 0) {
        updateWarehouseStock(wId); // helper function belowe
        return true;
    }
        
    
    }
    catch (SQLException e){} // output ..
    return false;
    }
    
    public List<Product> getProductsInWarehouse(int wId) {
        List<Product> products = new ArrayList<>();
        String sql = "select p.*, wp.quantity from products p " +
                "join warehouse_products wp on p.product_id = wp.product_id " + "where wp.warehouse_id = ?";
          
        try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
             ps.setInt(1, wId);

         try (ResultSet rs = ps.executeQuery()){
         
             while (rs.next()){
                 Product p = new Product(
                         rs.getInt("product_id"),
                         rs.getString(("name")),
                         rs.getString("category"),
                         rs.getInt("quantity"),
                         rs.getDouble("price"),
                         rs.getDate("expiry_date"),
                         rs.getInt("supplier_id")
                 );
                 products.add(p);
             }
         }
        


        }
        catch (SQLException e){} // output ..
        return products;
                

    }
    
    
    private void updateWarehouseStock(int wId){
        String sql = "update warehouses set current_stock = "
                + "(select coalesce(sum(quantity), 0) from warehouse_products where warehouse_id ?)"
                // Coalesce to get either sum or 0 ...
                + "where warehouse_id = ?";
        
        try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
             ps.setInt(1, wId);
        ps.setInt(2, wId);

         ps.executeUpdate();
        


        }
        catch (SQLException e){} // output ..
                
        
    }
    
    
    
}

