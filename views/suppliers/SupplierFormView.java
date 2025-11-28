package com.warehouse.views.suppliers;

import com.warehouse.controllers.SupplierController;
import com.warehouse.models.Supplier;

import javax.swing.*;
import java.awt.*;

public class SupplierFormView extends JDialog {
    private final SupplierController supplierController;
    private final Supplier existingSupplier;
    private final Runnable onSuccessCallback;
    
    private final JTextField nameField = new JTextField(25);
    private final JTextField contactInfoField = new JTextField(25);
    private final JTextField addressField = new JTextField(30);

    public SupplierFormView(Window owner, SupplierController controller, 
                           Supplier existingSupplier, Runnable onSuccessCallback) {
        super(owner, existingSupplier == null ? "Add New Supplier" : "Edit Supplier", 
              ModalityType.APPLICATION_MODAL);
        this.supplierController = controller;
        this.existingSupplier = existingSupplier;
        this.onSuccessCallback = onSuccessCallback;
        
        initializeUI();
        if (existingSupplier != null) {
            populateForm();
        }
    }

    private void initializeUI() {
        setSize(400, 300);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0: Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Supplier Name:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);
        
        // Row 1: Contact Info
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Contact Info:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(contactInfoField, gbc);
        
        // Row 2: Address
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Address:*"), gbc);
        gbc.gridx = 1;
        formPanel.add(addressField, gbc);
        
        // Required fields note
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JLabel requiredNote = new JLabel("* Required fields");
        requiredNote.setForeground(Color.GRAY);
        requiredNote.setFont(requiredNote.getFont().deriveFont(Font.ITALIC, 11f));
        formPanel.add(requiredNote, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton(existingSupplier == null ? "Add Supplier" : "Update Supplier");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        saveButton.addActionListener(e -> saveSupplier());
        cancelButton.addActionListener(e -> dispose());
        
        getRootPane().setDefaultButton(saveButton);
    }

    private void populateForm() {
        if (existingSupplier != null) {
            nameField.setText(existingSupplier.getName());
            contactInfoField.setText(existingSupplier.getContactInfo());
            addressField.setText(existingSupplier.getAddress());
        }
    }

    private void saveSupplier() {
        try {
            // Validation
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Supplier name is required", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                nameField.requestFocus();
                return;
            }
            
            if (contactInfoField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Contact info is required", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                contactInfoField.requestFocus();
                return;
            }
            
            if (addressField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Address is required", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                addressField.requestFocus();
                return;
            }
            
            // Create or update supplier
            boolean success;
            if (existingSupplier == null) {
                // Create new supplier - ID will be auto-generated (use 0 as placeholder)
                success = supplierController.addSupplier(
                    nameField.getText().trim(),
                    contactInfoField.getText().trim(),
                    addressField.getText().trim()
                );
            } else {
                // Update existing supplier
                success = supplierController.updateSupplier(
                    existingSupplier.getPersonId(),
                    nameField.getText().trim(),
                    contactInfoField.getText().trim(),
                    addressField.getText().trim()
                );
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    existingSupplier == null ? "Supplier added successfully!" : "Supplier updated successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to save supplier", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error saving supplier: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}