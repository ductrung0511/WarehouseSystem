package com.warehouse.views.orders;

import com.warehouse.controllers.OrderController;
import com.warehouse.controllers.ProductController;
import com.warehouse.models.Product;
import com.warehouse.models.Customer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrderCreationView extends JDialog {
    private final OrderController orderController;
    private final ProductController productController;
    private final Runnable onSuccessCallback;
    
    private final JComboBox<Customer> customerCombo = new JComboBox<>();
    private final JComboBox<Product> productCombo = new JComboBox<>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final DefaultListModel<OrderItem> orderItemsModel = new DefaultListModel<>();
    private final JList<OrderItem> orderItemsList = new JList<>(orderItemsModel);
    
    private final JLabel totalAmountLabel = new JLabel("$0.00");
    private final JLabel availableStockLabel = new JLabel("0");

    public OrderCreationView(Window owner, OrderController controller, Runnable onSuccessCallback) {
        super(owner, "Create New Order", ModalityType.APPLICATION_MODAL);
        this.orderController = controller;
        this.productController = new ProductController();
        this.onSuccessCallback = onSuccessCallback;
        
        initializeUI();
        loadData();
    }

    private void initializeUI() {
        setSize(1000, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Main form panel
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Customer selection
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.add(new JLabel("Customer:"));
        customerPanel.add(customerCombo);
        formPanel.add(customerPanel, BorderLayout.NORTH);
        
        // Product selection panel
        JPanel productSelectionPanel = createProductSelectionPanel();
        formPanel.add(productSelectionPanel, BorderLayout.CENTER);
        
        // Order items list
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items"));
        itemsPanel.add(new JScrollPane(orderItemsList), BorderLayout.CENTER);
        
        JPanel itemsControlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton removeItemButton = new JButton("Remove Selected Item");
        removeItemButton.addActionListener(e -> removeSelectedItem());
        itemsControlPanel.add(removeItemButton);
        itemsPanel.add(itemsControlPanel, BorderLayout.SOUTH);
        
        // Summary panel
        JPanel summaryPanel = createSummaryPanel();
        
        // Main layout
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplit.setTopComponent(formPanel);
        mainSplit.setBottomComponent(itemsPanel);
        mainSplit.setDividerLocation(200);
        
        add(mainSplit, BorderLayout.CENTER);
        add(summaryPanel, BorderLayout.SOUTH);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createButton = new JButton("Create Order");
        JButton cancelButton = new JButton("Cancel");
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Event handlers
        productCombo.addActionListener(e -> updateProductInfo());
        createButton.addActionListener(e -> createOrder());
        cancelButton.addActionListener(e -> dispose());
        
        getRootPane().setDefaultButton(createButton);
    }

    private JPanel createProductSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Add Product to Order"));
        
        panel.add(new JLabel("Product:"));
        panel.add(productCombo);
        
        panel.add(new JLabel("Available Stock:"));
        panel.add(availableStockLabel);
        
        panel.add(new JLabel("Quantity:"));
        panel.add(quantitySpinner);
        
        JButton addButton = new JButton("Add to Order");
        addButton.addActionListener(e -> addProductToOrder());
        panel.add(addButton);
        
        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Total Amount:"));
        totalAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalAmountLabel.setForeground(Color.BLUE);
        panel.add(totalAmountLabel);
        
        return panel;
    }

    private void loadData() {
        try {
            // Load customers
            List<Customer> customers = orderController.getAllCustomers();
            customerCombo.removeAllItems();
            for (Customer customer : customers) {
                customerCombo.addItem(customer);
            }
            
            // Load products
            List<Product> products = orderController.getAllProducts();
            productCombo.removeAllItems();
            for (Product product : products) {
                productCombo.addItem(product);
            }
            
            if (!products.isEmpty()) {
                updateProductInfo();
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProductInfo() {
        Product selectedProduct = (Product) productCombo.getSelectedItem();
        if (selectedProduct != null) {
            availableStockLabel.setText(String.valueOf(selectedProduct.getQuantity()));
            
            // Update spinner maximum based on available stock
            SpinnerNumberModel model = (SpinnerNumberModel) quantitySpinner.getModel();
            model.setMaximum(selectedProduct.getQuantity());
            
            if ((Integer) quantitySpinner.getValue() > selectedProduct.getQuantity()) {
                quantitySpinner.setValue(selectedProduct.getQuantity());
            }
        }
    }

    private void addProductToOrder() {
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
        
        if (quantity > selectedProduct.getQuantity()) {
            JOptionPane.showMessageDialog(this, 
                "Not enough stock available", 
                "Insufficient Stock", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if product already in order
        for (int i = 0; i < orderItemsModel.size(); i++) {
            OrderItem item = orderItemsModel.getElementAt(i);
            if (item.product.getProductId() == selectedProduct.getProductId()) {
                JOptionPane.showMessageDialog(this, 
                    "Product already in order. Remove it first or update quantity.", 
                    "Duplicate Product", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        OrderItem newItem = new OrderItem(selectedProduct, quantity);
        orderItemsModel.addElement(newItem);
        updateTotalAmount();
        
        // Reset quantity
        quantitySpinner.setValue(1);
    }

    private void removeSelectedItem() {
        int selectedIndex = orderItemsList.getSelectedIndex();
        if (selectedIndex >= 0) {
            orderItemsModel.remove(selectedIndex);
            updateTotalAmount();
        }
    }

    private void updateTotalAmount() {
        double total = 0.0;
        for (int i = 0; i < orderItemsModel.size(); i++) {
            OrderItem item = orderItemsModel.getElementAt(i);
            total += item.getSubtotal();
        }
        totalAmountLabel.setText(String.format("$%.2f", total));
    }

    private void createOrder() {
        try {
            Customer selectedCustomer = (Customer) customerCombo.getSelectedItem();
            if (selectedCustomer == null) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a customer", 
                    "Selection Required", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (orderItemsModel.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please add at least one product to the order", 
                    "Empty Order", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Prepare products and quantities
            List<Product> products = new ArrayList<>();
            List<Integer> quantities = new ArrayList<>();
            
            for (int i = 0; i < orderItemsModel.size(); i++) {
                OrderItem item = orderItemsModel.getElementAt(i);
                products.add(item.product);
                quantities.add(item.quantity);
            }
            
            // Create order
            boolean success = orderController.createOrder(
                selectedCustomer.getPersonId(), 
                products, 
                quantities
            );
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Order created successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSuccessCallback != null) {
                    onSuccessCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create order", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error creating order: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper class for order items in the list
    private static class OrderItem {
        final Product product;
        final int quantity;
        
        OrderItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
        
        double getSubtotal() {
            return product.getPrice() * quantity;
        }
        
        @Override
        public String toString() {
            return String.format("%s x%d - $%.2f (Subtotal: $%.2f)", 
                product.getName(), quantity, product.getPrice(), getSubtotal());
        }
    }
}