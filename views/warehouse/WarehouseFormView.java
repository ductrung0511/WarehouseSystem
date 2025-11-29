package com.warehouse.views.warehouse;

import com.warehouse.controllers.WarehouseController;
import javax.swing.*;
import java.awt.*;

public class WarehouseFormView extends JDialog {
    private final WarehouseController warehouseController;
    private final Runnable onSaveCallback;
    
    private final JTextField nameField = new JTextField(20);
    private final JTextField locationField = new JTextField(20);
    private final JTextField capacityField = new JTextField(10);

    public WarehouseFormView(Window owner, WarehouseController controller, Runnable onSaveCallback) {
        super(owner, "Add New Warehouse", ModalityType.APPLICATION_MODAL);
        this.warehouseController = controller;
        this.onSaveCallback = onSaveCallback;
        initializeUI();
    }

    private void initializeUI() {
        setSize(400, 250);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        formPanel.add(new JLabel("Warehouse Name:"));
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Location:"));
        formPanel.add(locationField);
        
        formPanel.add(new JLabel("Capacity:"));
        formPanel.add(capacityField);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default values
        capacityField.setText("1000");
        
        // Event handlers
        saveButton.addActionListener(e -> saveWarehouse());
        cancelButton.addActionListener(e -> dispose());
        
        getRootPane().setDefaultButton(saveButton);
    }

    private void saveWarehouse() {
        try {
            String name = nameField.getText().trim();
            String location = locationField.getText().trim();
            int capacity = Integer.parseInt(capacityField.getText().trim());

            // Basic validation
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Warehouse name cannot be empty.");
                nameField.requestFocus();
                return;
            }
            
            if (location.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Location cannot be empty.");
                locationField.requestFocus();
                return;
            }
            
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.");
                capacityField.requestFocus();
                return;
            }

            // You'll need to add this method to your WarehouseController
            boolean success = warehouseController.addWarehouse(name, location, capacity);
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Warehouse added successfully!");
                dispose();
                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save warehouse.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for capacity.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}