package com.warehouse.services;

import com.warehouse.dao.ProductDAO;
import com.warehouse.dao.WarehouseDAO;
import com.warehouse.models.Product;
import java.util.List;
import java.util.ArrayList;

public class InventoryService {
    private ProductDAO productDAO;
    private WarehouseDAO warehouseDAO;
    
    public InventoryService() {
        this.productDAO = new ProductDAO();
        this.warehouseDAO = new WarehouseDAO();
    }
    
    public List<Product> getLowStockProducts(int threshold) {
        List<Product> allProducts = productDAO.getAllProducts();
        List<Product> lowStockProducts = new ArrayList<>();
        
        for (Product product : allProducts) {
            if (product.getQuantity() <= threshold) {
                lowStockProducts.add(product);
            }
        }
        
        return lowStockProducts;
    }
    
    public List<Product> getOutOfStockProducts() {
        return getLowStockProducts(0);
    }
    
    public boolean restockProduct(int productId, int quantity) {
        Product product = productDAO.getProductById(productId);
        if (product != null) {
            int newQuantity = product.getQuantity() + quantity;
            return productDAO.updateProductStock(productId, newQuantity);
        }
        return false;
    }
    
    public double getTotalInventoryValue() {
        List<Product> products = productDAO.getAllProducts();
        double totalValue = 0.0;
        
        for (Product product : products) {
            totalValue += product.getPrice() * product.getQuantity();
        }
        
        return totalValue;
    }
    
    public String getInventorySummary() {
        List<Product> products = productDAO.getAllProducts();
        int totalProducts = products.size();
        int totalItems = 0;
        double totalValue = 0.0;
        int lowStockCount = 0;
        
        for (Product product : products) {
            totalItems += product.getQuantity();
            totalValue += product.getPrice() * product.getQuantity();
            if (product.getQuantity() < 10) {
                lowStockCount++;
            }
        }
        
        return String.format("Inventory Summary:\n" +
                           "Total Products: %d\n" +
                           "Total Items: %d\n" +
                           "Total Value: $%.2f\n" +
                           "Low Stock Items: %d",
                           totalProducts, totalItems, totalValue, lowStockCount);
    }
}