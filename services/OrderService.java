package com.warehouse.services;

import com.warehouse.dao.OrderDAO;
import com.warehouse.dao.ProductDAO;
import com.warehouse.dao.CustomerDAO;
import com.warehouse.models.Order;
import com.warehouse.models.Product;
import com.warehouse.models.Customer;
import java.util.*;

public class OrderService {
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;
    
    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();
        this.customerDAO = new CustomerDAO();
    }
    
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    public List<Order> getOrdersByStatus(String status) {
        List<Order> allOrders = orderDAO.getAllOrders();
        List<Order> filteredOrders = new ArrayList<>();
        
        for (Order order : allOrders) {
            if (order.getStatus().equalsIgnoreCase(status)) {
                filteredOrders.add(order);
            }
        }
        
        return filteredOrders;
    }
    
    public boolean processOrder(int orderId) {
        return orderDAO.updateOrderStatus(orderId, "Processed");
    }
    
    public boolean shipOrder(int orderId) {
        return orderDAO.updateOrderStatus(orderId, "Shipped");
    }
    
    public boolean completeOrder(int orderId) {
        return orderDAO.updateOrderStatus(orderId, "Completed");
    }
    
    public String getOrderSummary(int orderId) {
        // This would typically join with customer and product tables
        // For now, return basic info
        List<Order> orders = orderDAO.getAllOrders();
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return String.format("Order #%d - Customer: %d - Total: $%.2f - Status: %s",
                                   orderId, order.getCustomerId(), order.getTotalAmount(), order.getStatus());
            }
        }
        return "Order not found";
    }
}