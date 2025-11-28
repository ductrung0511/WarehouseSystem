package com.warehouse.services;

import com.warehouse.dao.*;
import com.warehouse.models.*;
import java.util.List;
import java.util.Date;

public class WarehouseService {
    private ProductDAO productDAO;
    private WarehouseDAO warehouseDAO;
    private EmployeeDAO employeeDAO;
    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;
    private SupplierDAO supplierDAO;
    
    public WarehouseService() {
        this.productDAO = new ProductDAO();
        this.warehouseDAO = new WarehouseDAO();
        this.employeeDAO = new EmployeeDAO();
        this.customerDAO = new CustomerDAO();
        this.orderDAO = new OrderDAO();
        this.supplierDAO = new SupplierDAO();
    }
    
    // ===== PRODUCT SERVICES =====
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productDAO.getProductsByCategory(category);
    }
    
    public boolean addProduct(Product product) {
        return productDAO.addProduct(product);
    }
    
    public boolean updateProductStock(int productId, int newQuantity) {
        return productDAO.updateProductStock(productId, newQuantity);
    }
    
    public Product getProductById(int productId) {
        return productDAO.getProductById(productId);
    }
    
    // ===== WAREHOUSE SERVICES =====
    public List<Warehouse> getAllWarehouses() {
        return warehouseDAO.getAllWarehouses();
    }
    
    public boolean addProductToWarehouse(int warehouseId, int productId, int quantity) {
        // Pure business logic - check warehouse capacity
        Warehouse warehouse = warehouseDAO.getWarehouseById(warehouseId);

        if (warehouse != null) {
            int currentStock = warehouse.getCurrentStock();
            int capacity = warehouse.getCapacity();
            return (currentStock + quantity <= capacity) && 
                   warehouseDAO.addProductToWarehouse(warehouseId, productId, quantity);
        }
        return false;
    }
    
    public List<Product> getProductsInWarehouse(int warehouseId) {
        return warehouseDAO.getProductsInWarehouse(warehouseId);
    }
    
    public Warehouse getWarehouseById(int warehouseId) {
        return warehouseDAO.getWarehouseById(warehouseId);
    }
    
    // ===== EMPLOYEE SERVICES =====
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
    
    public boolean addEmployee(Employee employee) {
        return employeeDAO.addEmployee(employee);
    }
    
    // ===== CUSTOMER SERVICES =====
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public boolean addCustomer(Customer customer) {
        return customerDAO.addCustomer(customer);
    }
    
    // ===== ORDER SERVICES =====
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    public boolean createOrder(Order order) {
        return orderDAO.createOrder(order);
    }
    
    public boolean updateOrderStatus(int orderId, String status) {
        return orderDAO.updateOrderStatus(orderId, status);
    }
    
    // ===== SUPPLIER SERVICES =====
    public List<Supplier> getAllSuppliers() {
        return supplierDAO.getAllSuppliers();
    }
    
    // ===== BUSINESS LOGIC METHODS =====
    public String generateInventoryReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== INVENTORY REPORT ===\n");
        
        List<Product> products = getAllProducts();
        report.append("Total Products: ").append(products.size()).append("\n\n");
        
        // Low stock alert
        report.append("Low Stock Alert:\n");
        for (Product product : products) {
            if (product.getQuantity() < 10) {
                report.append("⚠️ ").append(product.getName())
                      .append(" - Only ").append(product.getQuantity()).append(" left!\n");
            }
        }
        
        return report.toString();
    }
    
    public String generateWarehouseReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== WAREHOUSE REPORT ===\n");
        
        List<Warehouse> warehouses = getAllWarehouses();
        for (Warehouse warehouse : warehouses) {
            report.append("\n").append(warehouse.getName()).append(" (").append(warehouse.getLocation()).append(")\n");
            report.append("Capacity: ").append(warehouse.getCapacity())
                  .append(" | Current Stock: ").append(warehouse.getCurrentStock()).append("\n");
            
            List<Product> products = getProductsInWarehouse(warehouse.getWarehouseId());
            report.append("Products: ").append(products.size()).append("\n");
        }
        
        return report.toString();
    }
    
    public boolean processSale(Order order) {
        boolean orderCreated = orderDAO.createOrder(order);
        if (orderCreated) {
            // Update stock for each product in the order
            for (var entry : order.getProductList().entrySet()) {
                Product product = entry.getKey();
                int quantitySold = entry.getValue();
                int newQuantity = product.getQuantity() - quantitySold;
                productDAO.updateProductStock(product.getProductId(), newQuantity);
            }
            return true;
        }
        return false;
    }


    public boolean addSupplier(Supplier supplier) {
        System.out.print("IN WAREHOUSE SERVICES");
        return supplierDAO.addSupplier(supplier);
    }

    public boolean updateSupplier(Supplier supplier) {
        return supplierDAO.updateSupplier(supplier);
    }

    public Supplier getSupplierById(int supplierId) {
        return supplierDAO.getSupplierById(supplierId);
}
    
    public boolean updateProduct(Product p){
        return productDAO.updateProduct(p);
    }
    public boolean deleteProduct(int Pid){
        return productDAO.deleteProduct(Pid);
    }
}