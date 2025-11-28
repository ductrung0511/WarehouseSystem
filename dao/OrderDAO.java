package com.warehouse.dao;
        import com.warehouse.models.Order;
import com.warehouse.models.Product;

import java.sql.*;
//import java.util.List;
import java.util.*;

public class OrderDAO{
//    public boolean createOrder(Order o){
//    String orderSql = "insert into orders (customer_id, total_amount, status) values (?, ?, ?)";
//    try (Connection c = DatabaseConfig.getConnection();
//            PreparedStatement ps = c.prepareStatement(orderSql, statement.GET)){
//        ps.setInt(1, o.getCustomerId());
//        ps.setDouble(2, o.getTotalAmount());
//        ps.setString(3, o.getStatus());
//        
//        int result = ps.executeUpdate();
//        
//        if(result > 0) {
//            try (ResultSet gk = ps.getGeneratedKeys()) {
//                 if(gk.next()){
//                 int id = gk.getInt(1);
//                 addOrder(id, o, c);
//                         System.out.println("Created order in DAO");
//
//                 return true;
//                 }
//                }
//                
//        }
//        
//    }
//    catch (SQLException e) {
//        System.out.println(e.getMessage());
//            }
//        return false; 
//    
//    }
//    
    
    public boolean createOrder(Order o){
        String orderSql = "insert into orders (customer_id, total_amount, status) values (?, ?, ?)";
        try (Connection c = DatabaseConfig.getConnection();
            PreparedStatement ps = c.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) { // Fixed: Statement.RETURN_GENERATED_KEYS
            ps.setInt(1, o.getCustomerId());
            ps.setDouble(2, o.getTotalAmount());
            ps.setString(3, o.getStatus());

            int result = ps.executeUpdate();

            if(result > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                     if(gk.next()){
                     int id = gk.getInt(1);
                     o.setOrderId(id); // Set the generated ID back to the Order object
                     boolean itemsAdded = addOrder(id, o, c); // Assuming this method adds order items
                     if (itemsAdded) {
                         System.out.println("Created order in DAO with ID: " + id);
                         return true;
                     }
                     }
                }                
            }

        }
        catch (SQLException e) {
            System.out.println("Error creating order: " + e.getMessage());
            e.printStackTrace();
        }
        return false; 
    }
    
    // After insert Order, only then can we insert individual item itself
    private boolean addOrder(int id, Order o, Connection conn) throws SQLException {
    
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
            return true;
        }
        catch(SQLException e) {
            System.err.println(e.getMessage());
                return false;
    }
        
    }
    
//    public List<Order> getAllOrders(){
//    List<Order> orders  = new ArrayList<>();
//    String sql = "select * from orders";
//    
//    try (Connection conn = DatabaseConfig.getConnection();
//               Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery(sql)){
//        while(rs.next()){
//            Order o = new Order(
//            rs.getInt("order_id"),
//                   rs.getInt("customer_id"),
//                    rs.getDate("order_date")
//            );
//            
//            o.updateStatus(rs.getString("status"));
//            
//            Map<Product, Integer> oderItems = queryOrderItems();
//            
//            ... o.addItem(p, quantity);
//            
//           
//            System.out.println(o);
//            orders.add(o);
//        }
//        
//
//        
//    }
//    catch(SQLException e) {
//        System.out.println(e.getMessage());
//    }
//    return orders;
//    }
//    
//    private Map<Product, Integer> queryOrderItems()
//    
    
    public List<Order> getAllOrders() {
    List<Order> orders = new ArrayList<>();
    String sql = "SELECT * FROM orders ORDER BY order_date DESC";
    
    try (Connection conn = DatabaseConfig.getConnection();
         Statement st = conn.createStatement();
         ResultSet rs = st.executeQuery(sql)) {
        
        while (rs.next()) {
            Order o = new Order(
                rs.getInt("order_id"),
                rs.getInt("customer_id"),
                rs.getDate("order_date")
            );
            
            // Set order properties
            o.updateStatus(rs.getString("status"));
            
            // Get order items
            Map<Product, Integer> orderItems = queryOrderItems(conn, o.getOrderId());
            
            // Add items to order  and calculate total amount (auto in class)
            for (Map.Entry<Product, Integer> entry : orderItems.entrySet()) {
                o.addItem(entry.getKey(), entry.getValue());
            }

            System.out.println("Loaded order: " + o);
            orders.add(o);
        }
        
    } catch (SQLException e) {
        System.out.println("Error getting all orders: " + e.getMessage());
        e.printStackTrace();
    }
    return orders;
}

private Map<Product, Integer> queryOrderItems(Connection conn, int orderId) {
    Map<Product, Integer> orderItems = new HashMap<>();
    String sql = "SELECT oi.product_id, oi.quantity, oi.unit_price, p.product_id, p.name, p.category, p.expiry_date, p.supplier_id, p.quantity as stock_quantity " +
                 " FROM order_items oi " +
                 "JOIN products p ON oi.product_id = p.product_id " +
                 "WHERE oi.order_id = ?";
    
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Create Product object from order_items and products table data
                Product product = new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("stock_quantity"), // quantity in stock - not needed for order display
                    rs.getDouble("unit_price"),
                    rs.getDate("expiry_date"), // expiry date
                    rs.getInt("supplier_id") // supplier id - not needed for order display
                );
                
                int quantity = rs.getInt("quantity");
                orderItems.put(product, quantity);
            }
        }
        
    } catch (SQLException e) {
        System.out.println("Error querying order items for order ID " + orderId + ": " + e.getMessage());
        e.printStackTrace();
    }
    
    return orderItems;
}


    // Update - update order status
    public boolean updateOrderStatus (int orderId, String status){
        String sql = "update orders set status = ? where order_id = ?" ;
        
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