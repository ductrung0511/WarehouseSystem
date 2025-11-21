package com.warehouse.models;

import java.util.List;

public class Supplier extends Person {

    public Supplier(int personId, String name, String contactInfo, String address) {
        super(personId, name, contactInfo, address);
    }

    // Implement abstract method
    @Override
    public void updateInfo() {
        System.out.println("Supplier info updated: " + name);
    }

    // Additional methods from UML
    public void deliverProduct(Product product) {
        System.out.println("Supplier " + name + " delivered product: " + product.getName());
    }

    public void viewPendingOrders() {
        System.out.println("Supplier " + name + " is viewing pending orders");
    }

    @Override
    public String toString() {
        return String.format("Supplier{id=%d, name='%s', contact='%s'}", 
                           personId, name, contactInfo);
    }
}