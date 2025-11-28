/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ductr_u0pd9jf
 */
package com.warehouse.views.warehouse;

import com.warehouse.controllers.WarehouseController;
import com.warehouse.controllers.ProductController;
import com.warehouse.models.Warehouse;
import com.warehouse.models.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductAssignmentView extends JDialog {
    private final WarehouseController warehouseController;
    private final ProductController productController;
    private final Warehouse warehouse;
    private final Runnable onSuccessCallback;
    
    private final JComboBox<Product> productCombo = new JComboBox<>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JLabel capacityLabel = new JLabel();
    private final JLabel availableStockLabel = new JLabel();

    public ProductAssignmentView(Window owner, WarehouseController controller, 
                               Warehouse warehouse, Runnable onSuccessCallback) {
        super(owner, "Add Product to Warehouse", ModalityType.APPLICATION_MODAL);
        this.warehouseController = controller;
        this.productController = new ProductController();
        this.warehouse = warehouse;
        this.onSuccessCallback = onSuccessCallback;
        
        initializeUI();
        loadProducts();
        updateCapacityInfo();
    }

    private void initializeUI() {
        setSize(500, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        formPanel.add(new JLabel("Warehouse:"));
        JLabel warehouseLabel = new JLabel(warehouse.getName() + " - " + warehouse.getLocation());
        warehouseLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        formPanel.add(warehouseLabel);
        
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityLabel);
        
        formPanel.add(new JLabel("Product:"));
        formPanel.add(productCombo);
        
        formPanel.add(new JLabel("Available Stock:"));
        formPanel.add(availableStockLabel);
        
        formPanel.add(new JLabel("Quantity to Add:"));
        formPanel.add(quantitySpinner);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton assignButton = new JButton("Assign Product");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(assignButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        productCombo.addActionListener(e -> updateProductInfo());
        assignButton.addActionListener(e -> assignProduct());
        cancelButton.addActionListener(e -> dispose());
        
        getRootPane().setDefaultButton(assignButton);
    }

    private void loadProducts() {
        try {
            List<Product> products = productController.getAllProducts();
            productCombo.removeAllItems();
            
            for (Product product : products) {
                productCombo.addItem(product);
            }
            
            if (products.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No products available. Please add products first.", 
                    "No Products", 
                    JOptionPane.WARNING_MESSAGE);
                dispose();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading products: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCapacityInfo() {
        int availableCapacity = warehouse.getCapacity() - warehouse.getCurrentStock();
        capacityLabel.setText(warehouse.getCurrentStock() + " / " + warehouse.getCapacity() + 
                            " (Available: " + availableCapacity + ")");
        
        if (availableCapacity <= 0) {
            capacityLabel.setForeground(Color.RED);
        } else if (availableCapacity < 10) {
            capacityLabel.setForeground(Color.ORANGE);
        } else {
            capacityLabel.setForeground(Color.GREEN);
        }
    }

    private void updateProductInfo() {
        Product selectedProduct = (Product) productCombo.getSelectedItem();
        if (selectedProduct != null) {
            availableStockLabel.setText(String.valueOf(selectedProduct.getQuantity()));
            
            // Update spinner maximum based on available stock and warehouse capacity
            int maxQuantity = Math.min(
                selectedProduct.getQuantity(),
                warehouse.getCapacity() - warehouse.getCurrentStock()
            );
            
            SpinnerNumberModel model = (SpinnerNumberModel) quantitySpinner.getModel();
            model.setMaximum(maxQuantity);
            
            if ((Integer) quantitySpinner.getValue() > maxQuantity) {
                quantitySpinner.setValue(maxQuantity);
            }
        }
    }

    private void assignProduct() {
        try {
            Product selectedProduct = (Product) productCombo.getSelectedItem();
            if (selectedProduct == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a product", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int quantity = (Integer) quantitySpinner.getValue();
            
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Quantity must be positive", 
                    "Invalid Quantity", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check warehouse capacity
            if (warehouse.getCurrentStock() + quantity > warehouse.getCapacity()) {
                JOptionPane.showMessageDialog(this, 
                    "Not enough capacity in warehouse. Available: " + 
                    (warehouse.getCapacity() - warehouse.getCurrentStock()), 
                    "Capacity Exceeded", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check product availability
            if (selectedProduct.getQuantity() < quantity) {
                JOptionPane.showMessageDialog(this, 
                    "Not enough product stock. Available: " + selectedProduct.getQuantity(), 
                    "Insufficient Stock", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Assign product to warehouse
            boolean success = warehouseController.addProductToWarehouse(
                warehouse.getWarehouseId(), 
                selectedProduct.getProductId(), 
                quantity
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Product successfully assigned to warehouse!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to assign product to warehouse", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error assigning product: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
