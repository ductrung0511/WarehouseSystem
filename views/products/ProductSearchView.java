package com.warehouse.views.products;

import com.warehouse.controllers.ProductController;
import com.warehouse.models.Product;
import com.warehouse.views.components.DataTable;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ProductSearchView extends JPanel {
    private final ProductController productController;
    private  DataTable searchTable;
    private List<Product> allProducts;

    public ProductSearchView(ProductController productController) {
        this.productController = productController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadAllProducts();
    }

    private void initializeComponents() {
        // Search controls
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchType = new JComboBox<>(new String[]{
            "Name", "Category", "All"
        });
        JButton searchButton = new JButton("Search");
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("in:"));
        searchPanel.add(searchType);
        searchPanel.add(searchButton);
        
        // Results table
        String[] columns = {"ID", "Name", "Category", "Stock", "Price", "Status"};
        searchTable = new DataTable(columns);
        
        // Status label
        JLabel statusLabel = new JLabel("Enter search terms to find products");
        
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(searchTable.getTable()), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        // Search functionality
        searchButton.addActionListener(e -> performSearch(
            searchField.getText().trim(),
            (String) searchType.getSelectedItem()
        ));
        
        searchField.addActionListener(e -> performSearch(
            searchField.getText().trim(),
            (String) searchType.getSelectedItem()
        ));
    }

    private void loadAllProducts() {
        try {
            allProducts = productController.getAllProducts();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    private void performSearch(String searchTerm, String searchType) {
        if (allProducts == null || allProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products available to search.");
            return;
        }
        
        searchTable.clearData();
        
        List<Product> filteredProducts = allProducts.stream()
            .filter(product -> matchesSearch(product, searchTerm, searchType))
            .collect(Collectors.toList());
        
        for (Product product : filteredProducts) {
            String status = product.getQuantity() > 0 ? "In Stock" : "Out of Stock";
            if (product.getQuantity() < 10) status = "Low Stock";
            
            searchTable.addRow(new Object[]{
                product.getProductId(),
                product.getName(),
                product.getCategory(),
                product.getQuantity(),
                String.format("$%.2f", product.getPrice()),
                status
            });
        }
    }

    private boolean matchesSearch(Product product, String searchTerm, String searchType) {
        if (searchTerm.isEmpty()) return true;
        
        String term = searchTerm.toLowerCase();
        
        switch (searchType) {
            case "Name":
                return product.getName().toLowerCase().contains(term);
            case "Category":
                return product.getCategory().toLowerCase().contains(term);
            case "All":
                return product.getName().toLowerCase().contains(term) ||
                       product.getCategory().toLowerCase().contains(term) ||
                       String.valueOf(product.getProductId()).contains(term);
            default:
                return true;
        }
    }
}