package com.warehouse.views.products;

import com.warehouse.controllers.ProductController;
import com.warehouse.models.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductListView extends JPanel {

    private final ProductController controller;
    private final DefaultTableModel tableModel;

    private final JTextField nameInput  = new JTextField();
    private final JTextField catInput   = new JTextField();
    private final JTextField qtyInput   = new JTextField();
    private final JTextField priceInput = new JTextField();

    public ProductListView() {
        this.controller = new ProductController();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // left form
        JPanel form = new JPanel(new GridLayout(9, 1, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("New Product"));

        form.add(new JLabel("Name:"));
        form.add(nameInput);
        form.add(new JLabel("Category:"));
        form.add(catInput);
        form.add(new JLabel("Quantity:"));
        form.add(qtyInput);
        form.add(new JLabel("Price:"));
        form.add(priceInput);

        JButton saveBtn = new JButton("Save");
        form.add(new JLabel());
        form.add(saveBtn);

        add(form, BorderLayout.WEST);

        // table
        String[] cols = {"ID", "Name", "Category", "Stock", "Price"};
        tableModel = new DefaultTableModel(cols, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        saveBtn.addActionListener(e -> saveProduct());
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        try {
            List<Product> list = controller.getAllProducts();
            for (Product p : list) {
                tableModel.addRow(new Object[]{
                    p.getProductId(),
                    p.getName(),
                    p.getCategory(),
                    p.getQuantity(),
                    p.getPrice()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void saveProduct() {
        try {
            String name = nameInput.getText().trim();
            String cat  = catInput.getText().trim();
            int qty     = Integer.parseInt(qtyInput.getText().trim());
            double price= Double.parseDouble(priceInput.getText().trim());

            boolean ok = controller.addProduct(name, cat, qty, price );
            if (ok) {
                JOptionPane.showMessageDialog(this, "Product saved.");
                nameInput.setText("");
                catInput.setText("");
                qtyInput.setText("");
                priceInput.setText("");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Save failed.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage());
        }
    }
}