package com.warehouse.models;

import java.util.List;

public class Customer extends Person {

    public Customer(int personId, String name, String contactInfo, String address) {
        super(personId, name, contactInfo, address);
    }

    // Implement abstract method
    @Override
    public void updateInfo() {
        System.out.println("Customer info updated: " + name);
    }

    // Additional methods from UML
    public void placeOrder(Order order) {
        System.out.println("Customer " + name + " placed order #" + order.getOrderId());
    }

    public void cancelOrder(int orderId) {
        System.out.println("Customer " + name + " cancelled order #" + orderId);
    }

    public void viewOrderHistory() {
        System.out.println("Customer " + name + " is viewing order history");
    }

    @Override
    public String toString() {
        return String.format("Customer{id=%d, name='%s', contact='%s'}", 
                           personId, name, contactInfo);
    }
}
