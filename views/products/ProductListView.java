package com.warehouse.views.products;

import com.warehouse.controllers.ProductController;
import com.warehouse.models.Product;
import com.warehouse.views.components.DataTable;
import com.warehouse.views.components.SearchPanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class ProductListView extends JPanel {
    private final ProductController productController;
    private DataTable productTable;
    private SearchPanel searchPanel;
    private List<Product> products;

    public ProductListView(ProductController productController) {
        this.productController = productController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        setupContextMenu();
        loadProducts();
    }
    
    private void setupContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem editMenuItem = new JMenuItem("Edit Product");
        JMenuItem deleteMenuItem = new JMenuItem("Delete Product");
        
        editMenuItem.addActionListener(e -> {
            int selectedRow = productTable.getSelectedModelRow();
            if (selectedRow >= 0 && selectedRow < products.size()) {
                editProduct(products.get(selectedRow));
            }
        });
        
        deleteMenuItem.addActionListener(e -> {
            int selectedRow = productTable.getSelectedModelRow();
            if (selectedRow >= 0 && selectedRow < products.size()) {
                deleteProduct(products.get(selectedRow));
            }
        });
        
        contextMenu.add(editMenuItem);
        contextMenu.add(deleteMenuItem);
        
        // Add mouse listener to show context menu
        productTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = productTable.getTable().rowAtPoint(e.getPoint());
                if (row >= 0 && row < productTable.getTable().getRowCount()) {
                    productTable.getTable().setRowSelectionInterval(row, row);
                }
                
                if (e.isPopupTrigger()) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click to edit
                    int row = productTable.getTable().rowAtPoint(e.getPoint());
                    if (row >= 0 && row < products.size()) {
                        editProduct(products.get(row));
                    }
                }
            }
        });
    }

    private void initializeComponents() {
        // Top panel with buttons and search
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Product");
        JButton stockButton = new JButton("Update Stock");
        JButton refreshButton = new JButton("Refresh");
        
        // Add instruction label
        JLabel instructionLabel = new JLabel("Double click to edit - Or Right click to select Edit or Delete Product");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        
        buttonPanel.add(addButton);
        buttonPanel.add(stockButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(instructionLabel);
        
        // Search panel
        searchPanel = new SearchPanel();
        searchPanel.setSearchListener(e -> performSearch());
        
        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        // Product table
        String[] columns = {"ID", "Name", "Category", "Quantity", "Price", "Supplier"};
        productTable = new DataTable(columns);
        productTable.setColumnWidth(0, 60);  // ID
        productTable.setColumnWidth(1, 150); // Name
        productTable.setColumnWidth(2, 100); // Category
        productTable.setColumnWidth(3, 80);  // Quantity
        productTable.setColumnWidth(4, 80);  // Price
        productTable.setColumnWidth(5, 100); // Supplier
        
        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(productTable.getTable()), BorderLayout.CENTER);
        
        // Button actions
        addButton.addActionListener(e -> openProductForm());
        stockButton.addActionListener(e -> openStockUpdate());
        refreshButton.addActionListener(e -> loadProducts());
    }

    private void loadProducts() {
        try {
            products = productController.getAllProducts();
            productTable.clearData();
            
            for (Product product : products) {
                productTable.addRow(new Object[]{
                    product.getProductId(),
                    product.getName(),
                    product.getCategory(),
                    product.getQuantity(),
                    String.format("$%.2f", product.getPrice()),
                    "Supplier " + product.getSupplierId()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading products: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String searchText = searchPanel.getSearchText();
        String filterType = searchPanel.getFilterType();
        
        // Simple search implementation - can be enhanced later
        loadProducts(); // For now, just refresh
    }

    private void openProductForm() {
        ProductFormView form = new ProductFormView(
            SwingUtilities.getWindowAncestor(this),
            productController,
//            null, // No product for new form
            this::loadProducts
        );
        form.setVisible(true);
    }
    
    private void openProductForm(Product product) {
        ProductFormView form = new ProductFormView(
            SwingUtilities.getWindowAncestor(this),
            productController,
            product, // Product to edit
            this::loadProducts
        );
        form.setVisible(true);
    }

    private void openStockUpdate() {
        int selectedRow = productTable.getSelectedModelRow();
        if (selectedRow >= 0) {
            int productId = (Integer) productTable.getValueAt(selectedRow, 0);
            String productName = (String) productTable.getValueAt(selectedRow, 1);
            
            StockUpdateView stockView = new StockUpdateView(
                SwingUtilities.getWindowAncestor(this),
                productController,
                productId,
                productName,
                this::loadProducts
            );
            stockView.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a product to update stock.");
        }
    }
    
    private void editProduct(Product product) {
        openProductForm(product);
    }
    
    private void deleteProduct(Product product) {
        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete product: " + product.getName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                boolean success = productController.deleteProduct(product.getProductId());
                if (success) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Product deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    loadProducts();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to delete product.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error deleting product: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    // Method to update product from external components
    public void updateProduct(Product product) {
        try {
            boolean success = productController.updateProduct(
                product.getProductId(),
                product.getName(),
                product.getCategory(),
                product.getQuantity(),
                product.getPrice(),
                product.getExpiryDate(),
                product.getSupplierId()
            );
            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    "Product updated successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
                );
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to update product.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Error updating product: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
    
    // Method to refresh the view externally if needed
    public void refreshView() {
        loadProducts();
    }
}