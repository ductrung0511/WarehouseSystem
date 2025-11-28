package com.warehouse.views.warehouse;

import com.warehouse.controllers.WarehouseController;
import com.warehouse.models.Warehouse;
import com.warehouse.models.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TransferView extends JPanel {
    private final WarehouseController warehouseController;
    private List<Warehouse> warehouses;
    private List<Product> products;
    
    private final JComboBox<Warehouse> fromWarehouseCombo = new JComboBox<>();
    private final JComboBox<Warehouse> toWarehouseCombo = new JComboBox<>();
    private final JComboBox<Product> productCombo = new JComboBox<>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    
    private final JLabel fromStockLabel = new JLabel("0");
    private final JLabel toCapacityLabel = new JLabel("0");

    public TransferView() {
        this.warehouseController = new WarehouseController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadData();
    }

    private void initializeComponents() {
        // Main form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Transfer Products Between Warehouses"));
        
        // From warehouse
        formPanel.add(new JLabel("From Warehouse:"));
        formPanel.add(fromWarehouseCombo);
        
        formPanel.add(new JLabel("Available Stock:"));
        formPanel.add(fromStockLabel);
        
        // To warehouse
        formPanel.add(new JLabel("To Warehouse:"));
        formPanel.add(toWarehouseCombo);
        
        formPanel.add(new JLabel("Available Capacity:"));
        formPanel.add(toCapacityLabel);
        
        // Product and quantity
        formPanel.add(new JLabel("Product:"));
        formPanel.add(productCombo);
        
        formPanel.add(new JLabel("Transfer Quantity:"));
        formPanel.add(quantitySpinner);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton transferButton = new JButton("Transfer Products");
        JButton refreshButton = new JButton("Refresh");
        
        controlPanel.add(refreshButton);
        controlPanel.add(transferButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Event handlers
        fromWarehouseCombo.addActionListener(e -> updateFromWarehouseInfo());
        toWarehouseCombo.addActionListener(e -> updateToWarehouseInfo());
        productCombo.addActionListener(e -> updateProductInfo());
        refreshButton.addActionListener(e -> loadData());
        transferButton.addActionListener(e -> executeTransfer());
    }

    private void loadData() {
        try {
            warehouses = warehouseController.getAllWarehouses();
            
            fromWarehouseCombo.removeAllItems();
            toWarehouseCombo.removeAllItems();
            
            for (Warehouse warehouse : warehouses) {
                fromWarehouseCombo.addItem(warehouse);
                toWarehouseCombo.addItem(warehouse);
            }
            
            loadProducts();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProducts() {
        try {
            // For transfer, we need products that exist in the source warehouse
            // This is a simplified version - in a real app, you'd filter by warehouse
            // products = warehouseController.getProductsInWarehouse(selectedFromWarehouseId);
            
            // For now, using all products
            // products = productController.getAllProducts();
            
            productCombo.removeAllItems();
            // Note: In a real implementation, you'd populate this based on selected warehouse
            // This is a placeholder for the concept
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading products: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateFromWarehouseInfo() {
        Warehouse fromWarehouse = (Warehouse) fromWarehouseCombo.getSelectedItem();
        if (fromWarehouse != null) {
            fromStockLabel.setText(String.valueOf(fromWarehouse.getCurrentStock()));
        }
    }

    private void updateToWarehouseInfo() {
        Warehouse toWarehouse = (Warehouse) toWarehouseCombo.getSelectedItem();
        if (toWarehouse != null) {
            int availableCapacity = toWarehouse.getCapacity() - toWarehouse.getCurrentStock();
            toCapacityLabel.setText(String.valueOf(availableCapacity));
            
            if (availableCapacity <= 0) {
                toCapacityLabel.setForeground(Color.RED);
            } else if (availableCapacity < 10) {
                toCapacityLabel.setForeground(Color.ORANGE);
            } else {
                toCapacityLabel.setForeground(Color.GREEN);
            }
        }
    }

    private void updateProductInfo() {
        // Update available quantity for selected product in source warehouse
        // This would require additional service methods in a real implementation
    }

    private void executeTransfer() {
        try {
            Warehouse fromWarehouse = (Warehouse) fromWarehouseCombo.getSelectedItem();
            Warehouse toWarehouse = (Warehouse) toWarehouseCombo.getSelectedItem();
            
            if (fromWarehouse == null || toWarehouse == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select both source and destination warehouses", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (fromWarehouse.getWarehouseId() == toWarehouse.getWarehouseId()) {
                JOptionPane.showMessageDialog(this, 
                    "Source and destination warehouses must be different", 
                    "Invalid Selection", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int quantity = (Integer) quantitySpinner.getValue();
            
            // In a real implementation, you would:
            // 1. Check if product exists in source warehouse
            // 2. Check quantity availability
            // 3. Check destination capacity
            // 4. Execute the transfer transaction
            
            JOptionPane.showMessageDialog(this, 
                "Transfer functionality would be implemented here.\n" +
                "From: " + fromWarehouse.getName() + "\n" +
                "To: " + toWarehouse.getName() + "\n" +
                "Quantity: " + quantity, 
                "Transfer Simulation", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error executing transfer: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}