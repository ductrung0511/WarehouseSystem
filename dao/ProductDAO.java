package com.warehouse.dao ;

import com.warehouse.models.Product;
import java.sql.* ;
import java.util.*;

public class ProductDAO {

public boolean addProduct(Product p) {
String sql = "insert into products (name, category, quantity, price, expiry_date, supplier_id) values (?,?,?,?,?,?)";

    try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
    ps.setString(1, p.getName());
    ps.setString(2, p.getCategory());
    ps.setInt(3, p.getQuantity());
    ps.setDouble(4, p.getPrice());
    if(p.getExpiryDate() != null) {
    ps.setDate(5, new java.sql.Date(p.getExpiryDate().getTime()));
    }
    else ps.setNull(5, Types.DATE);
    
    ps.setInt(6, p.getSupplierId());
    int result = ps.executeUpdate();
    if (result > 0) {
         try (ResultSet gk = ps.getGeneratedKeys()) {
                if(gk.next()){
                p.setProductId(gk.getInt(1));
                
                }
                return true;
               }
         
        
        
        
    }
        
    
    }
    catch (SQLException e){} // output ..
        return false;
    
    
    }

    public Product getProductById(int productId) {
        String sql = "select * from products where product_id = ? ";
        Product p = null;
        
        try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
            ps.setInt(1, productId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                p = extractPfromRs(rs);
                
                
                }}
            
        }
        catch (SQLException e ) {
            
            }
            
        return p;
    }
     // Helper function for tthe above function 

    private Product extractPfromRs(ResultSet rs) throws SQLException  {
        return new Product(
            rs.getInt("product_id"),
            rs.getString("name"),
                rs.getString("category"),
        rs.getInt("quantity"),
        rs.getDouble("price"),
        rs.getDate("expiry_date"),
        rs.getInt("supplier_id"));
    }
    
    // read all products;
    public List<Product>  getAllProducts () {
    List<Product> products = new ArrayList<>();
    String sql = "select * from products";
    
    try(Connection c = DatabaseConfig.getConnection();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql)){
    
        while(rs.next()){
            Product p= extractPfromRs(rs);
            products.add(p);
            
        
    }
        } catch(SQLException e) {
                
                }
    return products;
    }
    
    // Read - get product with the same input category
    public List<Product>  getProductsByCategory ( String cat) {
    List<Product> products = new ArrayList<>();
    String sql = "select * from products where category = ?";
    try ( Connection c = DatabaseConfig.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){
        ps.setString(1, cat);
        
        try(ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                Product p= extractPfromRs(rs);
            products.add(p);
            
        
        }
              
    }
        } catch(SQLException e) {

                    }
        return products;  
    
    
    }
    
    
    // Update eproduct information
    public boolean updateProduct(Product p){
    
        String sql = "update products set name = ?, category = ?, quantity=?, expiry_date = ?, supplier_id = ? where product_id = ?";
        try(Connection c = DatabaseConfig.getConnection();
        PreparedStatement ps = c.prepareStatement(sql);){
        
        ps.setString(1, p.getName());
        ps.setString(2, p.getCategory());
        ps.setInt(3, p.getQuantity());
        ps.setDouble(4, p.getPrice());
        
        if(p.getExpiryDate() != null ){
            ps.setDate(5, new java.sql.Date(p.getExpiryDate().getTime( ) ));
            
        }else{ps.setNull(5, Types.DATE);}
        ps.setInt(6, p.getSupplierId());
        ps.setInt(7, p.getProductId());
        
        int result = ps.executeUpdate();
            if(result > 0) {
                return true;
                // Output st here 
            }
        }
        catch (SQLException e ) {
        // ERR output
        System.err.println(e.getMessage());
        }
        return false; 
            
    }
    
        // Update eproduct information
    public boolean updateProductStock(int pId, int newQ){
    
        String sql = "update products set  quantity=?  where product_id = ?";
        try(Connection c = DatabaseConfig.getConnection();
        PreparedStatement ps = c.prepareStatement(sql);){
        
        ps.setInt(1, newQ);
        ps.setInt(2, pId);
        
        
        int result = ps.executeUpdate();
            if(result > 0) {
                return true;
                // Output st here 
            }
        }
        catch (SQLException e ) {
        // ERR output
        System.err.println(e.getMessage());
        }
        return false; 
            
    }
    
        public boolean deleteProduct(int pId){
    
        String sql = "delete from products where    where product_id = ?";
        try(Connection c = DatabaseConfig.getConnection();
        PreparedStatement ps = c.prepareStatement(sql);){
        
        ps.setInt(1, pId);
        
        
        int result = ps.executeUpdate();
            if(result > 0) {
                return true;
                // Output st here 
            }
        }
        catch (SQLException e ) {
        // ERR output
        System.err.println(e.getMessage());
        }
        return false; 
            
    }
    
    

}


