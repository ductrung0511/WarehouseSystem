package com.warehouse.views.products;

import com.warehouse.controllers.ProductController;

import javax.swing.*;
import java.awt.*;

public class ProductFormView extends JPanel {

    private final ProductController controller;
    private final Runnable onSavedCallback;

    private final JTextField nameField  = new JTextField();
    private final JTextField catField   = new JTextField();
    private final JTextField qtyField   = new JTextField();
    private final JTextField priceField = new JTextField();

    public ProductFormView(ProductController controller, Runnable onSavedCallback) {
        this.controller = controller;
        this.onSavedCallback = onSavedCallback;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(catField);
        form.add(new JLabel("Quantity:"));
        form.add(qtyField);
        form.add(new JLabel("Price:"));
        form.add(priceField);

        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton save   = new JButton("Save");
        JButton clear  = new JButton("Clear");

        buttons.add(clear);
        buttons.add(save);

        add(buttons, BorderLayout.SOUTH);

        save.addActionListener(e -> onSave());
        clear.addActionListener(e -> clearFields());
    }

    private void onSave() {
        try {
            String name = nameField.getText().trim();
            String cat  = catField.getText().trim();
            int qty     = Integer.parseInt(qtyField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());

            boolean ok = controller.addProduct(name, cat, qty, price);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Product saved.");
                clearFields();
                if (onSavedCallback != null) {
                    onSavedCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save product.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }

    private void clearFields() {
        nameField.setText("");
        catField.setText("");
        qtyField.setText("");
        priceField.setText("");
    }
}