package com.warehouse.controllers;

import com.warehouse.services.WarehouseService;
import com.warehouse.dao.SupplierDAO;
import com.warehouse.models.Supplier;
import com.warehouse.models.Product;
import java.util.*;

public class SupplierController {
    private final WarehouseService warehouseService;
    private final SupplierDAO supplierDAO;
        
    public SupplierController() {
        this.warehouseService = new WarehouseService();
        this.supplierDAO = new SupplierDAO();
    }
    
    public List<Supplier> getAllSuppliers() {
        return warehouseService.getAllSuppliers();
    }
    
    public boolean addSupplier(String name, String contactInfo, String address) {
        try {
            // Use 0 as placeholder ID - database should auto-generate
            Supplier supplier = new Supplier(0, name, contactInfo, address);
            return warehouseService.addSupplier(supplier);
        } catch (Exception e) {
            System.err.println("Error adding supplier: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateSupplier(int supplierId, String name, String contactInfo, String address) {
        try {
            Supplier supplier = new Supplier(supplierId, name, contactInfo, address);
            return warehouseService.updateSupplier(supplier);
        } catch (Exception e) {
            System.err.println("Error updating supplier: " + e.getMessage());
            return false;
        }
    }
    
    
//    public boolean createPurchaseOrder(int supplierId, List<Product> products, 
//                                  List<Integer> quantities, List<Double> prices) {
//        
//        System.out.println("In SDAO Creating purchase order  supplier ID: " + supplierId);
//
//        try {
//            // Implementation would go here
//            System.out.println("Creating purchase order for supplier ID: " + supplierId);
//            System.out.println("Items: " + products.size());
//
//            // Simulate success for demo
//            return true;
//        } catch (Exception e) {
//            System.err.println("Error creating purchase order: " + e.getMessage());
//            return false;
//        }
//    }
    // In SupplierController.java - update the createPurchaseOrder method
    public boolean createPurchaseOrder(int supplierId, List<Product> products, 
                                      List<Integer> quantities, List<Double> prices) {
        // Convert lists to Map<Product, Integer> for the PurchaseOrder model
        Map<Product, Integer> productList = new java.util.HashMap<>();
        for (int i = 0; i < products.size(); i++) {
            productList.put(products.get(i), quantities.get(i));
        }

        PurchaseOrderController poController = new PurchaseOrderController();
        return poController.createPurchaseOrder(supplierId, productList);
    }
        // Add this deleteSupplier method
    public boolean deleteSupplier(int supplierId) {
        try {
            return supplierDAO.deleteSupplier(supplierId);
        } catch (Exception e) {
            System.err.println("Error deleting supplier: " + e.getMessage());
            return false;
        }
    }
}