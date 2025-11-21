package com.warehouse.models;

import java.util.*;

public class PurchaseOrder {
    private int poId;
    private int supplierId;
    private List<Product> productList;
    private double totalCost;
    private Date orderDate;
    private String status;

    public PurchaseOrder(int poId, int supplierId, Date orderDate) {
        this.poId = poId;
        this.supplierId = supplierId;
        this.orderDate = orderDate;
        this.productList = new ArrayList<>();
        this.totalCost = 0.0;
        this.status = "Pending";
    }

    // Getters and Setters
    public int getPoId() { return poId; }
    public int getSupplierId() { return supplierId; }
    public List<Product> getProductList() { return productList; }
    public double getTotalCost() { return totalCost; }
    public Date getOrderDate() { return orderDate; }
    public String getStatus() { return status; }

    // Methods from UML
    public void createPO() {
        System.out.println("Purchase Order #" + poId + " created for supplier ID: " + supplierId);
    }

    public void approvePO() {
        this.status = "Approved";
        System.out.println("Purchase Order #" + poId + " approved.");
    }

    public void cancelPO() {
        this.status = "Cancelled";
        System.out.println("Purchase Order #" + poId + " cancelled.");
    }

    public void updateStatus(String status) {
        this.status = status;
        System.out.println("Purchase Order #" + poId + " status updated to: " + status);
    }

    // Helper method to add product
    public void addProduct(Product product) {
        productList.add(product);
        totalCost += product.getPrice();
    }

    @Override
    public String toString() {
        return String.format("PurchaseOrder{id=%d, supplierId=%d, totalCost=%.2f, status='%s', items=%d}", 
                           poId, supplierId, totalCost, status, productList.size());
    }
}