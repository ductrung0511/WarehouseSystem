package com.warehouse.views.products;

import com.warehouse.controllers.ProductController;
import javax.swing.*;
import java.awt.*;

public class StockUpdateView extends JDialog {
    private final ProductController productController;
    private final int productId;
    private final String productName;
    private final Runnable onUpdateCallback;
    
    private final JTextField quantityField = new JTextField(10);
    private final JLabel currentStockLabel = new JLabel();

    public StockUpdateView(Window owner, ProductController controller, 
                         int productId, String productName, Runnable onUpdateCallback) {
        super(owner, "Update Stock - " + productName, ModalityType.APPLICATION_MODAL);
        this.productController = controller;
        this.productId = productId;
        this.productName = productName;
        this.onUpdateCallback = onUpdateCallback;
        initializeUI();
    }

    private void initializeUI() {
        setSize(350, 200);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        formPanel.add(new JLabel("Product:"));
        formPanel.add(new JLabel(productName));
        
        formPanel.add(new JLabel("Current Stock:"));
        formPanel.add(currentStockLabel);
        
        formPanel.add(new JLabel("New Quantity:"));
        formPanel.add(quantityField);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton updateButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(updateButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Load current stock
        loadCurrentStock();
        
        // Event handlers
        updateButton.addActionListener(e -> updateStock());
        cancelButton.addActionListener(e -> dispose());
        
        getRootPane().setDefaultButton(updateButton);
    }

    private void loadCurrentStock() {
        try {
            var product = productController.getProductById(productId);
            if (product != null) {
                currentStockLabel.setText(String.valueOf(product.getQuantity()));
                quantityField.setText(String.valueOf(product.getQuantity()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading product: " + e.getMessage());
        }
    }

    private void updateStock() {
        try {
            int newQuantity = Integer.parseInt(quantityField.getText().trim());
            
            boolean success = productController.updateProductStock(productId, newQuantity);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Stock updated successfully!");
                dispose();
                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update stock.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}