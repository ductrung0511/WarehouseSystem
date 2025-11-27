package com.warehouse.dao;
        import com.warehouse.models.Order;
import com.warehouse.models.Product;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class OrderDAO{
    public boolean createOrder(Order o){
    String orderSql = "insert into orders (customer_id, total_amount, status) values (?, ?, ?)";
    try (Connection c = DatabaseConfig.getConnection();
            PreparedStatement ps = c.prepareStatement(orderSql)){
        ps.setInt(1, o.getCustomerId());
        ps.setDouble(2, o.getTotalAmount());
        ps.setString(3, o.getStatus());
        
        int result = ps.executeUpdate();
        
        if(result > 0) {
            try (ResultSet gk = ps.getGeneratedKeys()) {
                 if(gk.next()){
                 int id = gk.getInt(1);
                 addOrder(id, o, c);
                 return true;
                 }
                }
                
        }
        
    }
    catch (SQLException e) {
            }
        return false; 
    
    }
    
    
    // After insert Order, only then can we insert individual item itself
    private void addOrder(int id, Order o, Connection conn) throws SQLException {
    
        String itemSql = "Insert into order_items (order_id, product_id, quantity, unit_price ) values (?, ?, ?, ?)";
        try (PreparedStatement st = conn.prepareStatement(itemSql)){
            for (var entry : o.getProductList().entrySet()){
                Product p = entry.getKey();
                int quantity = entry.getValue();
                
                
                                st.setInt(1, id );
                st.setInt(2, p.getProductId());
                st.setInt(3, quantity);
                st.setDouble(4, p.getPrice());
                st.addBatch();
                
            }
            st.executeBatch();
        }
        
    }
    
    public List<Order> getAllOrders(){
    List<Order> orders  = new ArrayList<>();
    String sql = "select * from orders";
    
    try (Connection conn = DatabaseConfig.getConnection();
               Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql)){
        while(rs.next()){
            Order o = new Order(
            rs.getInt("order_id"),
                   rs.getInt("customer_id"),
                    rs.getDate("order_date"));
            o.updateStatus(rs.getString("status"));
            orders.add(o);
        }
    }
    catch(SQLException e) {
    }
    return orders;
    }
    
    // Update - update order status
    public boolean updateOrderStatus (int orderId, String status){
        String sql = "update orders set astatus = ? where order_id = ?" ;
        
        try(Connection c = DatabaseConfig.getConnection();
                PreparedStatement ps = c.prepareStatement(sql)){
            ps.setString(1, status );
            ps.setInt(2, orderId);
            
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