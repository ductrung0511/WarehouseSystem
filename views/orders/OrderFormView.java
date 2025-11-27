package com.warehouse.views.orders;

import com.warehouse.controllers.OrderController;
import com.warehouse.models.Customer;
import com.warehouse.models.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class OrderFormView extends JPanel {

    private final OrderController controller;

    private final JComboBox<Customer> customerBox = new JComboBox<>();
    private final JComboBox<Product>  productBox  = new JComboBox<>();
    private final JTextField qtyField             = new JTextField(5);
    private final DefaultTableModel cartModel;
    private final JLabel totalLabel = new JLabel("Total: $0.00");

    private double currentTotal = 0.0;

    public OrderFormView() {
        this.controller = new OrderController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top selection area
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        top.setBorder(BorderFactory.createEtchedBorder());

        top.add(new JLabel("Customer:"));
        top.add(customerBox);
        top.add(new JLabel("Product:"));
        top.add(productBox);
        top.add(new JLabel("Qty:"));
        top.add(qtyField);

        JButton addButton = new JButton("Add to Cart");
        top.add(addButton);

        add(top, BorderLayout.NORTH);

        // Cart table area
        String[] headers = {"Prod ID", "Item Name", "Unit Price", "Qty", "Subtotal"};
        cartModel = new DefaultTableModel(headers, 0);
        JTable cartTable = new JTable(cartModel);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Bottom total + checkout area
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        totalLabel.setFont(new Font("Arial", Font.BOLD, 20));
        bottom.add(totalLabel, BorderLayout.WEST);

        JButton checkoutBtn = new JButton("Complete Order");
        checkoutBtn.setPreferredSize(new Dimension(160, 40));
        bottom.add(checkoutBtn, BorderLayout.EAST);

        add(bottom, BorderLayout.SOUTH);

        // Load data
        loadCombos();

        // Actions
        addButton.addActionListener(e -> addToCart());
        checkoutBtn.addActionListener(e -> {
            if (cartModel.getRowCount() > 0) {
                JOptionPane.showMessageDialog(this, "Order processed (demo).");
                clearCart();
            } else {
                JOptionPane.showMessageDialog(this, "Cart is empty");
            }
        });
    }

    private void loadCombos() {
        try {
            List<Customer> customers = controller.getAllCustomers();
            for (Customer c : customers) customerBox.addItem(c);
        } catch (Exception ignored) {}

        try {
            List<Product> products = controller.getAllProducts();
            for (Product p : products) productBox.addItem(p);
        } catch (Exception ignored) {}
    }

    private void addToCart() {
        try {
            Product p = (Product) productBox.getSelectedItem();
            int qty   = Integer.parseInt(qtyField.getText().trim());

            if (p == null || qty <= 0) {
                JOptionPane.showMessageDialog(this, "Select product and quantity > 0");
                return;
            }

            double sub = p.getPrice() * qty;
            cartModel.addRow(new Object[]{
                p.getProductId(),
                p.getName(),
                p.getPrice(),
                qty,
                sub
            });

            currentTotal += sub;
            totalLabel.setText("Total: $" + currentTotal);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid number");
        }
    }

    private void clearCart() {
        cartModel.setRowCount(0);
        currentTotal = 0.0;
        totalLabel.setText("Total: $0.00");
    }
}