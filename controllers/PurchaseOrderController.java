package com.warehouse.controllers;

import com.warehouse.dao.PurchaseOrderDAO;
import com.warehouse.models.PurchaseOrder;
import com.warehouse.models.Product;

import java.util.List;
import java.util.Map;

public class PurchaseOrderController {
    private final PurchaseOrderDAO purchaseOrderDAO;
    
    public PurchaseOrderController() {
        this.purchaseOrderDAO = new PurchaseOrderDAO();
    }
    
    public boolean createPurchaseOrder(int supplierId, Map<Product, Integer> productList) {
        // Validation
        if (supplierId <= 0) {
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
        
        if (productList == null || productList.isEmpty()) {
            throw new IllegalArgumentException("Product list cannot be empty");
        }
        
        // Validate quantities
        for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive for product: " + product.getName());
            }
            if (product.getPrice() < 0) {
                throw new IllegalArgumentException("Product price cannot be negative for: " + product.getName());
            }
        }
        
        return purchaseOrderDAO.createPurchaseOrder(supplierId, productList);
    }
    
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderDAO.getAllPurchaseOrders();
    }
    
    public PurchaseOrder getPurchaseOrderById(int poId) {
        if (poId <= 0) {
            throw new IllegalArgumentException("Purchase order ID must be positive");
        }
        return purchaseOrderDAO.getPurchaseOrderById(poId);
    }
    
    public boolean updatePurchaseOrderStatus(int poId, String status) {
        if (poId <= 0) {
            throw new IllegalArgumentException("Purchase order ID must be positive");
        }
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        
        // Validate status value matches your database enum
        List<String> validStatuses = List.of("Pending", "Approved", "Cancelled", "Completed");
        if (!validStatuses.contains(status)) {
            throw new IllegalArgumentException("Invalid status. Must be one of: " + validStatuses);
        }
        
        return purchaseOrderDAO.updatePurchaseOrderStatus(poId, status);
    }
    
    public boolean deletePurchaseOrder(int poId) {
        if (poId <= 0) {
            throw new IllegalArgumentException("Purchase order ID must be positive");
        }
        return purchaseOrderDAO.deletePurchaseOrder(poId);
    }
    
    public List<PurchaseOrder> getPurchaseOrdersByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }
        return purchaseOrderDAO.getPurchaseOrdersByStatus(status);
    }
    
    public List<PurchaseOrder> getPurchaseOrdersBySupplier(int supplierId) {
        if (supplierId <= 0) {
            throw new IllegalArgumentException("Supplier ID must be positive");
        }
        return purchaseOrderDAO.getPurchaseOrdersBySupplier(supplierId);
    }
    
    // Business logic methods
    public boolean approvePurchaseOrder(int poId) {
        return updatePurchaseOrderStatus(poId, "Approved");
    }
    
    public boolean cancelPurchaseOrder(int poId) {
        return updatePurchaseOrderStatus(poId, "Cancelled");
    }
    
    public boolean completePurchaseOrder(int poId) {
        return updatePurchaseOrderStatus(poId, "Completed");
    }
    
    public double getTotalPurchaseValue() {
        List<PurchaseOrder> allOrders = getAllPurchaseOrders();
        return allOrders.stream()
                .mapToDouble(PurchaseOrder::getTotalCost)
                .sum();
    }
    
    public int getPendingOrdersCount() {
        return getPurchaseOrdersByStatus("Pending").size();
    }
    
    public int getApprovedOrdersCount() {
        return getPurchaseOrdersByStatus("Approved").size();
    }
    
    public int getCompletedOrdersCount() {
        return getPurchaseOrdersByStatus("Completed").size();
    }
    

}