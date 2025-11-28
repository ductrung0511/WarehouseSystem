package com.warehouse.models;

public class Supplier  extends Person {
    public Supplier(int pId, String n, String inf, String add) {
    super(pId, n, inf, add); // for creation of parent class
    }
    
    @Override
    public void updateInfo() {
    // Not needed now .    
    }
    
    public void deliverProduct(Product product){
    // notifify prove from delivery
    }
    public void viewPendingOrders(){
    // Queueing from database ... 
    
    }
    
    @Override 
    public String toString() {return "Supplier: " + name;}
    

}