package com.warehouse.views.products;

import com.warehouse.controllers.ProductController;
import com.warehouse.controllers.SupplierController;
import com.warehouse.models.Product;
import com.warehouse.models.Supplier;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductFormView extends JDialog {
    private final ProductController productController;
    private final SupplierController supplierController;
    private final Product existingProduct;
    private final Runnable onSaveCallback;
    
    private final JTextField nameField = new JTextField(20);
    private final JTextField categoryField = new JTextField(20);
    private final JTextField quantityField = new JTextField(10);
    private final JTextField priceField = new JTextField(10);
    private final JComboBox<Supplier> supplierCombo = new JComboBox<>();

    // Constructor for adding new product
    public ProductFormView(Window owner, ProductController productController, Runnable onSaveCallback) {
        this(owner, productController, null, onSaveCallback);
    }

    // Constructor for both add and edit
    public ProductFormView(Window owner, ProductController productController, Product product, Runnable onSaveCallback) {
        super(owner, product == null ? "Add New Product" : "Edit Product", ModalityType.APPLICATION_MODAL);
        this.productController = productController;
        this.supplierController = new SupplierController(); // Initialize supplier controller
        this.existingProduct = product;
        this.onSaveCallback = onSaveCallback;
        initializeUI();
        loadSuppliers(); // Load real suppliers from database
        if (product != null) {
            populateForm(product);
        }
    }

    private void initializeUI() {
        setSize(400, 350);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add ID field for edit mode (read-only)
        if (existingProduct != null) {
            formPanel.add(new JLabel("Product ID:"));
            JTextField idField = new JTextField(String.valueOf(existingProduct.getProductId()));
            idField.setEditable(false);
            idField.setBackground(Color.LIGHT_GRAY);
            formPanel.add(idField);
        }
        
        formPanel.add(new JLabel("Product Name:"));
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);
        
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);
        
        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);
        
        formPanel.add(new JLabel("Supplier:"));
        formPanel.add(supplierCombo);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton(existingProduct == null ? "Save" : "Update");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set default values for new product
        if (existingProduct == null) {
            quantityField.setText("0");
            priceField.setText("0.00");
        }
        
        // Event handlers
        saveButton.addActionListener(e -> saveProduct());
        cancelButton.addActionListener(e -> dispose());
        
        getRootPane().setDefaultButton(saveButton);
    }

    private void loadSuppliers() {
        try {
            // Get real suppliers from database
            List<Supplier> suppliers = supplierController.getAllSuppliers();
            
            // Clear existing demo items
            supplierCombo.removeAllItems();
            
            if (suppliers.isEmpty()) {
                supplierCombo.addItem(new Supplier(0, "No suppliers available", "", ""));
                supplierCombo.setEnabled(false);
            } else {
                for (Supplier supplier : suppliers) {
                    supplierCombo.addItem(supplier);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading suppliers: " + e.getMessage());
            supplierCombo.removeAllItems();
            supplierCombo.addItem(new Supplier(0, "Error loading suppliers", "", ""));
            supplierCombo.setEnabled(false);
        }
    }

    private void populateForm(Product product) {
        nameField.setText(product.getName());
        categoryField.setText(product.getCategory());
        quantityField.setText(String.valueOf(product.getQuantity()));
        priceField.setText(String.valueOf(product.getPrice()));
        
        // Set the correct supplier in the combo box
        int supplierId = product.getSupplierId();
        for (int i = 0; i < supplierCombo.getItemCount(); i++) {
            Supplier supplier = supplierCombo.getItemAt(i);
            if (supplier.getPersonId() == supplierId) {
                supplierCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void saveProduct() {
        try {
            // Validate inputs
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            
            // Get selected supplier
            Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
            int supplierId = selectedSupplier != null ? selectedSupplier.getPersonId() : 1;

            // Basic validation
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Product name cannot be empty.");
                nameField.requestFocus();
                return;
            }
            
            if (category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Category cannot be empty.");
                categoryField.requestFocus();
                return;
            }
            
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative.");
                quantityField.requestFocus();
                return;
            }
            
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative.");
                priceField.requestFocus();
                return;
            }

            if (supplierCombo.getSelectedIndex() == -1 || supplierId == 0) {
                JOptionPane.showMessageDialog(this, "Please select a valid supplier.");
                supplierCombo.requestFocus();
                return;
            }

            boolean success;
            
            if (existingProduct != null) {
                // Update existing product
                success = productController.updateProduct(
                    existingProduct.getProductId(),
                    name,
                    category,
                    quantity,
                    price,
                    existingProduct.getExpiryDate(), // Keep existing expiry date
                    supplierId
                );
            } else {
                // Add new product
                success = productController.addProduct(name, category, quantity, price, null, supplierId);
            }
            
            if (success) {
                String message = existingProduct == null ? 
                    "Product added successfully!" : "Product updated successfully!";
                JOptionPane.showMessageDialog(this, message);
                dispose();
                if (onSaveCallback != null) {
                    onSaveCallback.run();
                }
            } else {
                String message = existingProduct == null ? 
                    "Failed to save product." : "Failed to update product.";
                JOptionPane.showMessageDialog(this, message);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for quantity and price.\n" +
                "Quantity must be a whole number.\n" +
                "Price must be a decimal number.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Validation Error: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            e.printStackTrace(); // For debugging
        }
    }

    // Custom renderer for supplier combo box to display supplier names nicely
    static class SupplierComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                                                     boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Supplier) {
                Supplier supplier = (Supplier) value;
                // Display supplier in format: "ID - Name" or just "Name"
                setText(supplier.getPersonId() + " - " + supplier.getName());
            }
            
            return this;
        }
    }
}