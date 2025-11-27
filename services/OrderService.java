package com.warehouse.services;

import com.warehouse.dao.OrderDAO;

import com.warehouse.models.Order;

import java.util.*;


public class OrderService {
    private OrderDAO orderDAO; // object of this class
    
    public OrderService() {
        this.orderDAO = new OrderDAO();
        
    }
    
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    public List<Order> getOrdersByStatus(String s) {
        List<Order> allO = orderDAO.getAllOrders();
        List<Order> fO = new ArrayList<>();
        
        for(Order o : allO){
            if(o.getStatus().equalsIgnoreCase(s)){
                fO.add(o);
            }
        }
        return fO;
    }
    
    
    
    public boolean updateOrderStatus(int orderId, String s) {
        return orderDAO.updateOrderStatus(orderId, s);
    }
    
    public boolean processOrder(int orderId){
        return orderDAO.updateOrderStatus(orderId, "Processed");
        // do some checking if needed
    }
    
    public boolean shipOrder(int oId){
        return orderDAO.updateOrderStatus(oId, "shipped");
        
    }
    
    
}