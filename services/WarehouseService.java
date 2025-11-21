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
    
    public boolean addProduct(String name, String category, int quantity, double price, Date expiryDate, int supplierId) {
        Product product = new Product(0, name, category, quantity, price, expiryDate, supplierId);
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
        // Check if warehouse has capacity
        Warehouse warehouse = warehouseDAO.getWarehouseById(warehouseId);
        if (warehouse != null) {
            int currentStock = warehouse.getCurrentStock();
            int capacity = warehouse.getCapacity();
            
            if (currentStock + quantity <= capacity) {
                return warehouseDAO.addProductToWarehouse(warehouseId, productId, quantity);
            } else {
                System.err.println("❌ Cannot add product: Warehouse capacity exceeded");
                return false;
            }
        }
        return false;
    }
    
    public List<Product> getProductsInWarehouse(int warehouseId) {
        return warehouseDAO.getProductsInWarehouse(warehouseId);
    }
    
    // ===== EMPLOYEE SERVICES =====
    public List<Employee> getAllEmployees() {
        return employeeDAO.getAllEmployees();
    }
    
    public boolean addEmployee(String name, String position, String shift, double salary) {
        Employee employee = new Employee(0, name, "", "", position, shift, salary);
        return employeeDAO.addEmployee(employee);
    }
    
    // ===== CUSTOMER SERVICES =====
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }
    
    public boolean addCustomer(String name, String contactInfo, String address) {
        Customer customer = new Customer(0, name, contactInfo, address);
        return customerDAO.addCustomer(customer);
    }
    
    // ===== ORDER SERVICES =====
    public List<Order> getAllOrders() {
        return orderDAO.getAllOrders();
    }
    
    public boolean createOrder(int customerId, List<Product> products, List<Integer> quantities) {
        if (products.size() != quantities.size()) {
            System.err.println("❌ Products and quantities lists must be the same size");
            return false;
        }
        
        Order order = new Order(0, customerId, new Date());
        
        // Add items to order
        for (int i = 0; i < products.size(); i++) {
            order.addItem(products.get(i), quantities.get(i));
        }
        
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
        
        // Products by category
        report.append("Products by Category:\n");
        // You could group by category here
        
        // Low stock alert
        report.append("\nLow Stock Alert:\n");
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
    
    public boolean processSale(int customerId, int productId, int quantity) {
        Product product = getProductById(productId);
        if (product == null) {
            System.err.println("❌ Product not found");
            return false;
        }
        
        if (product.getQuantity() < quantity) {
            System.err.println("❌ Insufficient stock. Available: " + product.getQuantity());
            return false;
        }
        
        // Create order with single product
        Order order = new Order(0, customerId, new Date());
        order.addItem(product, quantity);
        
        boolean orderCreated = orderDAO.createOrder(order);
        if (orderCreated) {
            // Update stock
            int newQuantity = product.getQuantity() - quantity;
            return updateProductStock(productId, newQuantity);
        }
        
        return false;
    }
}