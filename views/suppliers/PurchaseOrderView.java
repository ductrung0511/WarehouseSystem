package com.warehouse.views.suppliers;

import com.warehouse.controllers.SupplierController;
import com.warehouse.controllers.ProductController;
import com.warehouse.models.Supplier;
import com.warehouse.models.Product;
import com.warehouse.views.components.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderView extends JPanel {
    private final SupplierController supplierController;
    private final ProductController productController;
    
    private final JComboBox<Supplier> supplierCombo = new JComboBox<>();
    private final JComboBox<Product> productCombo = new JComboBox<>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 10000.0, 0.1));
    private final DataTable orderItemsTable;
    
    private final JLabel totalAmountLabel = new JLabel("$0.00");
    private final JLabel supplierInfoLabel = new JLabel("Select a supplier");
    
    private final List<OrderItem> orderItems = new ArrayList<>();

    public PurchaseOrderView() {
        this.supplierController = new SupplierController();
        this.productController = new ProductController();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Initialize table
        String[] columns = {"Product", "Quantity", "Unit Price", "Subtotal", "Action"};
        orderItemsTable = new DataTable(columns);
        orderItemsTable.setColumnWidth(0, 200);
        orderItemsTable.setColumnWidth(1, 80);
        orderItemsTable.setColumnWidth(2, 100);
        orderItemsTable.setColumnWidth(3, 100);
        orderItemsTable.setColumnWidth(4, 100);
        
        initializeComponents();
        loadData();
    }

    private void initializeComponents() {
        // Main panel with card layout for different states
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content area
        JSplitPane contentSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        contentSplitPane.setDividerLocation(250);
        contentSplitPane.setResizeWeight(0.4);
        
        // Top: Order creation form
        JPanel orderFormPanel = createOrderFormPanel();
        contentSplitPane.setTopComponent(orderFormPanel);
        
        // Bottom: Order items table
        JPanel itemsPanel = createItemsPanel();
        contentSplitPane.setBottomComponent(itemsPanel);
        
        mainPanel.add(contentSplitPane, BorderLayout.CENTER);
        
        // Footer: Summary and actions
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(240, 245, 255));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 255)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel("CREATE PURCHASE ORDER");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 0, 128));
        
        supplierInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        supplierInfoLabel.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(supplierInfoLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }

    private JPanel createOrderFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add Products to Order"));
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 0: Supplier selection
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Supplier:*"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        inputPanel.add(supplierCombo, gbc);
        
        // Row 1: Product selection
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Product:*"), gbc);
        gbc.gridx = 1;
        inputPanel.add(productCombo, gbc);
        
        // Row 2: Quantity and Price
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Quantity:*"), gbc);
        gbc.gridx = 1;
        inputPanel.add(quantitySpinner, gbc);
        
       
        
        // Row 3: Add button
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addButton = new JButton("Add to Purchase Order");
        addButton.setBackground(new Color(0, 100, 0));
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> addProductToOrder());
        inputPanel.add(addButton, gbc);
        
        formPanel.add(inputPanel, BorderLayout.CENTER);
        return formPanel;
    }

    private JPanel createItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Purchase Order Items"));
        
        // Add custom renderer for the action column
        orderItemsTable.getTable().getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        orderItemsTable.getTable().getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        panel.add(new JScrollPane(orderItemsTable.getTable()), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.add(new JLabel("Total Order Amount:"));
        totalAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        totalAmountLabel.setForeground(Color.BLUE);
        summaryPanel.add(totalAmountLabel);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton clearButton = new JButton("Clear All");
        JButton saveDraftButton = new JButton("Save Draft");
        JButton submitButton = new JButton("Submit Purchase Order");
        
        clearButton.setBackground(Color.LIGHT_GRAY);
        saveDraftButton.setBackground(Color.ORANGE);
        saveDraftButton.setForeground(Color.BLACK);
        submitButton.setBackground(new Color(0, 128, 0));
        submitButton.setForeground(Color.WHITE);
        
        clearButton.addActionListener(e -> clearOrder());
        saveDraftButton.addActionListener(e -> saveDraft());
        submitButton.addActionListener(e -> submitPurchaseOrder());
        
        actionPanel.add(clearButton);
        actionPanel.add(saveDraftButton);
        actionPanel.add(submitButton);
        
        footerPanel.add(summaryPanel, BorderLayout.WEST);
        footerPanel.add(actionPanel, BorderLayout.EAST);
        
        return footerPanel;
    }

    private void loadData() {
        try {
            // Load suppliers
            List<Supplier> suppliers = supplierController.getAllSuppliers();
            supplierCombo.removeAllItems();
            for (Supplier supplier : suppliers) {
                supplierCombo.addItem(supplier);
            }
            
            // Load products
            List<Product> products = productController.getAllProducts();
            productCombo.removeAllItems();
            for (Product product : products) {
                productCombo.addItem(product);
            }
            
            // Add listener for supplier selection
            supplierCombo.addActionListener(e -> updateSupplierInfo());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSupplierInfo() {
        Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
        if (selectedSupplier != null) {
            supplierInfoLabel.setText(String.format(
                "Supplier: %s | Contact: %s | Address: %s",
                selectedSupplier.getName(),
                selectedSupplier.getContactInfo(),
                selectedSupplier.getAddress()
            ));
        } else {
            supplierInfoLabel.setText("Select a supplier");
        }
    }

    private void addProductToOrder() {
        Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
        Product selectedProduct = (Product) productCombo.getSelectedItem();
        
        
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a supplier first", 
                "Supplier Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a product", 
                "Product Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int quantity = (Integer) quantitySpinner.getValue();
        double unitPrice =   selectedProduct.getPrice();
        
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, 
                "Quantity must be positive", 
                "Invalid Quantity", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
  
        // Check if product already in order
        for (OrderItem item : orderItems) {
            if (item.product.getProductId() == selectedProduct.getProductId()) {
                int response = JOptionPane.showConfirmDialog(this,
                    "Product already in order. Update quantity?",
                    "Duplicate Product",
                    JOptionPane.YES_NO_OPTION);
                
                if (response == JOptionPane.YES_OPTION) {
                    item.quantity = quantity;
                    item.unitPrice = selectedProduct.getPrice() ;
                    updateOrderTable();
                }
                return;
            }
        }
        
        // Add new item
        OrderItem newItem = new OrderItem(selectedProduct, quantity, unitPrice);
        orderItems.add(newItem);
        updateOrderTable();
        
        // Reset form
        quantitySpinner.setValue(1);
        priceSpinner.setValue(0.0);
    }

    private void updateOrderTable() {
        orderItemsTable.clearData();
        
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            orderItemsTable.addRow(new Object[]{
                item.product.getName(),
                item.quantity,
                String.format("$%.2f", item.unitPrice),
                String.format("$%.2f", item.getSubtotal()),
                "Remove"
            });
        }
        
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        double total = 0.0;
        for (OrderItem item : orderItems) {
            total += item.getSubtotal();
        }
        totalAmountLabel.setText(String.format("$%.2f", total));
    }

    private void removeItem(int index) {
        if (index >= 0 && index < orderItems.size()) {
            orderItems.remove(index);
            updateOrderTable();
        }
    }

    private void clearOrder() {
        if (!orderItems.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Clear all items from this purchase order?",
                "Confirm Clear",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                orderItems.clear();
                updateOrderTable();
                supplierCombo.setSelectedIndex(-1);
                productCombo.setSelectedIndex(-1);
                quantitySpinner.setValue(1);
                priceSpinner.setValue(0.0);
            }
        }
    }

    private void saveDraft() {
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No items to save", 
                "Empty Order", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // In a real application, this would save to database
        JOptionPane.showMessageDialog(this,
            "Purchase order draft saved successfully!\n\n" +
            "Items: " + orderItems.size() + "\n" +
            "Total: " + totalAmountLabel.getText() + "\n\n" +
            "You can continue working on this order later.",
            "Draft Saved",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void submitPurchaseOrder() {
        Supplier selectedSupplier = (Supplier) supplierCombo.getSelectedItem();
        
        if (selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a supplier", 
                "Supplier Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please add at least one product to the purchase order", 
                "Empty Order", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show order summary for confirmation
        StringBuilder summary = new StringBuilder();
        summary.append("PURCHASE ORDER SUMMARY\n\n");
        summary.append("Supplier: ").append(selectedSupplier.getName()).append("\n");
        summary.append("Contact: ").append(selectedSupplier.getContactInfo()).append("\n");
        summary.append("Items: ").append(orderItems.size()).append("\n");
        summary.append("Total Amount: ").append(totalAmountLabel.getText()).append("\n\n");
        summary.append("ITEMS:\n");
        
        for (OrderItem item : orderItems) {
            summary.append(String.format("• %s x%d @ $%.2f each\n", 
                item.product.getName(), item.quantity, item.unitPrice));
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            summary.toString(),
            "Confirm Purchase Order Submission",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Prepare data for controller
                List<Product> products = new ArrayList<>();
                List<Integer> quantities = new ArrayList<>();
                List<Double> prices = new ArrayList<>();
                
                for (OrderItem item : orderItems) {
                    products.add(item.product);
                    quantities.add(item.quantity);
                    prices.add(item.unitPrice);
                }
                
                // Submit purchase order through controller
                boolean success = supplierController.createPurchaseOrder(
                    selectedSupplier.getPersonId(),
                    products,
                    quantities,
                    prices
                );
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Purchase order submitted successfully!\n\n" +
                        "Order has been sent to " + selectedSupplier.getName() + ".\n" +
                        "You can track the delivery in the Delivery Tracking section.",
                        "Order Submitted", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Clear the form
                    clearOrder();
                    
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to submit purchase order", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error submitting purchase order: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Helper class for order items
    private static class OrderItem {
        final Product product;
        int quantity;
        double unitPrice;
        
        OrderItem(Product product, int quantity, double unitPrice) {
            this.product = product;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        double getSubtotal() {
            return quantity * unitPrice;
        }
    }
    
    // Custom cell renderer for the action column
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    // Custom cell editor for the action column
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        public Object getCellEditorValue() {
            if (isPushed) {
                removeItem(row);
            }
            isPushed = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}