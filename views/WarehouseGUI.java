package com.warehouse.views;

import com.warehouse.models.Customer;
import com.warehouse.models.Product;
import com.warehouse.services.InventoryService;
import com.warehouse.services.OrderService;
import com.warehouse.services.WarehouseService;
import com.warehouse.controllers.ProductController;

// using wildcard imports because it's easier
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class WarehouseGUI extends JFrame {

// backend services (created safely)
WarehouseService warehouseService;
OrderService orderService;
InventoryService inventoryService;
ProductController controller;


public WarehouseGUI() {
    try {
        System.out.println("GUI lädt");
        warehouseService = new WarehouseService();
        System.out.println("Warehouse starten");
        orderService = new OrderService();
        System.out.println("Orderservice starten");
        inventoryService = new InventoryService();
            System.out.println("Inventory Service");
    } catch (Exception ex) {
        System.out.println(" Backend unavailable. GUI running in demo mode.");
    }

    initComponents();
}

    private void initComponents() {
        // Try to set the look and feel to Nimbus if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            System.out.println("Nimbus not supported, using default.");
        }

        setTitle("Warehouse System - Admin Dashboard");
        setSize(1280, 720);
        setLocationRelativeTo(null); // center the window
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Top Header ---
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(50, 100, 200)); 
        topPanel.setPreferredSize(new Dimension(1280, 80));
        
        JLabel mainTitle = new JLabel("Warehouse Management");
        mainTitle.setFont(new Font("Arial", Font.BOLD, 30));
        mainTitle.setForeground(Color.WHITE);
        topPanel.add(mainTitle);
        
        add(topPanel, BorderLayout.NORTH);

        // --- Main Menu Area ---
        // Using a grid layout for the big buttons
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(2, 3, 20, 20)); // rows, cols, hgap, vgap
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // 1. Products Button
        JButton prodBtn = new JButton("Manage Products");
        prodBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        prodBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProductFrame().setVisible(true);
            }
        });

        // 2. Customers Button
        JButton custBtn = new JButton("Manage Customers");
        custBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        custBtn.addActionListener(e -> {
            // TODO: Create the customer frame class later
            new CustomerFrame().setVisible(true);
        });

        // 3. POS Button
        JButton orderBtn = new JButton("New Order (POS)");
        orderBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        orderBtn.setBackground(new Color(200, 230, 255)); // make this one stand out
        orderBtn.addActionListener(e -> {
            new OrderFrame().setVisible(true);
        });

        // 4. View Orders
        JButton viewOrdersBtn = new JButton("View All Orders");
        viewOrdersBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        // 5. Reports
        JButton reportBtn = new JButton("Reports");
        reportBtn.setFont(new Font("SansSerif", Font.BOLD, 16));

        // 6. Logout
        JButton exitBtn = new JButton("Exit System");
        exitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        exitBtn.setForeground(Color.RED);
        exitBtn.addActionListener(e -> System.exit(0));

        // Add them to the panel
        centerPanel.add(prodBtn);
        centerPanel.add(custBtn);
        centerPanel.add(orderBtn);
        centerPanel.add(viewOrdersBtn);
        centerPanel.add(reportBtn);
        centerPanel.add(exitBtn);

        add(centerPanel, BorderLayout.CENTER);
    }

    // ==========================================
    // Inner Class for Adding Products
    // ==========================================
    class ProductFrame extends JFrame {
        
        JTextField nameInput, catInput, qtyInput, priceInput;
        JTable productTable;
        DefaultTableModel tableModel;

        public ProductFrame() {
            setTitle("Product Management");
            setSize(900, 550);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(10, 10));

            // -- Left Side Form --
            JPanel inputPanel = new JPanel(new GridLayout(9, 1, 5, 5));
            inputPanel.setBorder(BorderFactory.createTitledBorder("New Product Info"));
            inputPanel.setPreferredSize(new Dimension(250, 0));

            inputPanel.add(new JLabel("Product Name:"));
            nameInput = new JTextField();
            inputPanel.add(nameInput);

            inputPanel.add(new JLabel("Category:"));
            catInput = new JTextField();
            inputPanel.add(catInput);

            inputPanel.add(new JLabel("Quantity:"));
            qtyInput = new JTextField();
            inputPanel.add(qtyInput);

            inputPanel.add(new JLabel("Price ($):"));
            priceInput = new JTextField();
            inputPanel.add(priceInput);

            JButton saveButton = new JButton("Save Data");
            inputPanel.add(new JLabel("")); // spacer
            inputPanel.add(saveButton);

            add(inputPanel, BorderLayout.WEST);

            // -- Right Side Table --
            String[] cols = {"ID", "Name", "Category", "Stock", "Price"};
            tableModel = new DefaultTableModel(cols, 0);
            productTable = new JTable(tableModel);
            add(new JScrollPane(productTable), BorderLayout.CENTER);

            // Load initial data
            refreshData();

            // Save Action
            saveButton.addActionListener(e -> {
                saveProduct();
            });
        }

        private void saveProduct() {
            // basic validation
            if(nameInput.getText().isEmpty() || priceInput.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields");
                return;
            }

            try {
                String name = nameInput.getText();
                String cat = catInput.getText();
                int q = Integer.parseInt(qtyInput.getText());
                double p = Double.parseDouble(priceInput.getText());

                // hardcoding supplier ID to 1 for now
//                boolean success = warehouseService.addProduct(name, cat, q, p, new Date(), 1);
                
                boolean success = controller.addProduct(name, cat, q, p,  new Date(), 1);
                if(success) {
                    JOptionPane.showMessageDialog(this, "Saved!");
                    refreshData();
                    // clear fields
                    nameInput.setText("");
                    catInput.setText("");
                    qtyInput.setText("");
                    priceInput.setText("");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Check your number format!");
            }
        }

        private void refreshData() {
            tableModel.setRowCount(0); // clear table
            List<Product> list = warehouseService.getAllProducts();
            
            // using standard for loop instead of stream
            for(int i = 0; i < list.size(); i++) {
                Product p = list.get(i);
                Object[] row = {
                    p.getProductId(), 
                    p.getName(), 
                    p.getCategory(), 
                    p.getQuantity(), 
                    p.getPrice()
                };
                tableModel.addRow(row);
            }
        }
    }

    // ==========================================
    // Inner Class for Orders (POS)
    // ==========================================
    class OrderFrame extends JFrame {
        
        JComboBox<Customer> customerBox;
        JComboBox<Product> productBox;
        JTextField quantityField;
        JTable cartTable;
        DefaultTableModel cartModel;
        JLabel totalLabel;
        
        double currentTotal = 0.0;

        public OrderFrame() {
            setTitle("Create New Order");
            setSize(1000, 650);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // --- Top Selection Area ---
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
            topPanel.setBorder(BorderFactory.createEtchedBorder());

            customerBox = new JComboBox<>();
            productBox = new JComboBox<>();
            
            // Populate combos
            List<Customer> customers = warehouseService.getAllCustomers();
            for(Customer c : customers) {
                customerBox.addItem(c);
            }
            
            List<Product> products = warehouseService.getAllProducts();
            for(Product p : products) {
                productBox.addItem(p);
            }

            quantityField = new JTextField(5);
            JButton addButton = new JButton("Add to Cart");

            topPanel.add(new JLabel("Customer:"));
            topPanel.add(customerBox);
            topPanel.add(new JLabel("Product:"));
            topPanel.add(productBox);
            topPanel.add(new JLabel("Qty:"));
            topPanel.add(quantityField);
            topPanel.add(addButton);

            add(topPanel, BorderLayout.NORTH);

            // --- Cart Table ---
            String[] headers = {"Prod ID", "Item Name", "Unit Price", "Qty", "Subtotal"};
            cartModel = new DefaultTableModel(headers, 0);
            cartTable = new JTable(cartModel);
            add(new JScrollPane(cartTable), BorderLayout.CENTER);

            // --- Bottom Total ---
            JPanel bottomPanel = new JPanel(new BorderLayout());
            bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

            totalLabel = new JLabel("Total: $0.00");
            totalLabel.setFont(new Font("Arial", Font.BOLD, 24));
            
            JButton checkoutBtn = new JButton("Complete Order");
            checkoutBtn.setPreferredSize(new Dimension(150, 40));

            bottomPanel.add(totalLabel, BorderLayout.WEST);
            bottomPanel.add(checkoutBtn, BorderLayout.EAST);
            add(bottomPanel, BorderLayout.SOUTH);

            // Add Button Logic
            addButton.addActionListener(e -> {
                addToCart();
            });

            // Checkout Logic
            checkoutBtn.addActionListener(e -> {
                if(cartModel.getRowCount() > 0) {
                    JOptionPane.showMessageDialog(this, "Order processed successfully!");
                    dispose(); // close window
                } else {
                    JOptionPane.showMessageDialog(this, "Cart is empty");
                }
            });
        }

        private void addToCart() {
            try {
                Product selectedProd = (Product) productBox.getSelectedItem();
                int qty = Integer.parseInt(quantityField.getText());

                // Simple check
                if(qty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be > 0");
                    return;
                }

                double sub = selectedProd.getPrice() * qty;
                
                Object[] row = {
                    selectedProd.getProductId(),
                    selectedProd.getName(),
                    selectedProd.getPrice(),
                    qty,
                    sub
                };
                
                cartModel.addRow(row);
                
                // update running total
                currentTotal += sub;
                totalLabel.setText("Total: $" + currentTotal);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid number");
            }
        }
    }
    
    // Placeholder class
    class CustomerFrame extends JFrame {
        public CustomerFrame() {
            setSize(400, 300);
            add(new JLabel("Customer Form goes here...", SwingConstants.CENTER));
        }
    }

  
}