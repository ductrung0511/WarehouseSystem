/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ductr_u0pd9jf
 */
package com.warehouse.views.warehouse;

import com.warehouse.controllers.WarehouseController;
import com.warehouse.models.Warehouse;
import com.warehouse.models.Product;
import com.warehouse.views.components.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WarehouseListView extends JPanel {
    private final WarehouseController warehouseController;
    private  DataTable warehouseTable;
    private  DataTable productsTable;
    private List<Warehouse> warehouses;

    public WarehouseListView() {
        this.warehouseController = new WarehouseController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadWarehouses();
    }

    private void initializeComponents() {
        // Main split pane for warehouse list and products
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.5);

        // Left panel - Warehouse list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Warehouses"));
        
        // Warehouse table
        String[] warehouseColumns = {"ID", "Name", "Location", "Capacity", "Current Stock", "Utilization"};
        warehouseTable = new DataTable(warehouseColumns);
        warehouseTable.setColumnWidth(0, 50);
        warehouseTable.setColumnWidth(1, 120);
        warehouseTable.setColumnWidth(2, 100);
        warehouseTable.setColumnWidth(3, 80);
        warehouseTable.setColumnWidth(4, 80);
        warehouseTable.setColumnWidth(5, 100);
        
        // Add selection listener to show products when warehouse is selected
        warehouseTable.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = warehouseTable.getSelectedModelRow();
                if (selectedRow >= 0 && selectedRow < warehouses.size()) {
                    showWarehouseProducts(warehouses.get(selectedRow).getWarehouseId());
                }
            }
        });

        leftPanel.add(new JScrollPane(warehouseTable.getTable()), BorderLayout.CENTER);

        // Right panel - Products in selected warehouse
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Products in Warehouse"));
        
        String[] productColumns = {"Product ID", "Name", "Category", "Quantity", "Price"};
        productsTable = new DataTable(productColumns);
        rightPanel.add(new JScrollPane(productsTable.getTable()), BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = createControlPanel();
        leftPanel.add(controlPanel, BorderLayout.NORTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        JButton addWarehouseButton = new JButton("Add Warehouse"); // NEW BUTTON
        JButton addProductButton = new JButton("Add Product to Warehouse");
        JButton generateReportButton = new JButton("Generate Report");
        
        refreshButton.addActionListener(e -> loadWarehouses());
        addWarehouseButton.addActionListener(e -> showAddWarehouseDialog()); // NEW ACTION
        addProductButton.addActionListener(e -> showProductAssignmentDialog());
        generateReportButton.addActionListener(e -> generateWarehouseReport());
        
        controlPanel.add(refreshButton);
        controlPanel.add(addProductButton);
        controlPanel.add(generateReportButton);
        controlPanel.add(addWarehouseButton); // ADDED TO PANEL
        
        return controlPanel;
    }

    private void loadWarehouses() {
        try {
            warehouses = warehouseController.getAllWarehouses();
            warehouseTable.clearData();
            
            for (Warehouse warehouse : warehouses) {
                double utilization = warehouse.getCapacity() > 0 ? 
                    (double) warehouse.getCurrentStock() / warehouse.getCapacity() * 100 : 0;
                
                warehouseTable.addRow(new Object[]{
                    warehouse.getWarehouseId(),
                    warehouse.getName(),
                    warehouse.getLocation(),
                    warehouse.getCapacity(),
                    warehouse.getCurrentStock(),
                    String.format("%.1f%%", utilization)
                });
            }
            
            // Clear products table
            productsTable.clearData();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading warehouses: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showWarehouseProducts(int warehouseId) {
        try {
            List<Product> products = warehouseController.getProductsInWarehouse(warehouseId);
            productsTable.clearData();
            
            for (Product product : products) {
                productsTable.addRow(new Object[]{
                    product.getProductId(),
                    product.getName(),
                    product.getCategory(),
                    product.getQuantity(),
                    String.format("$%.2f", product.getPrice())
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading products: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProductAssignmentDialog() {
        int selectedRow = warehouseTable.getSelectedModelRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a warehouse first", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Warehouse selectedWarehouse = warehouses.get(selectedRow);
        ProductAssignmentView view = new ProductAssignmentView(SwingUtilities.getWindowAncestor(this), warehouseController, selectedWarehouse, this::loadWarehouses);
        view.setVisible(true);
    }
    
        // NEW METHOD: Show dialog to add new warehouse
    private void showAddWarehouseDialog() {
        WarehouseFormView form = new WarehouseFormView(
            SwingUtilities.getWindowAncestor(this),
            warehouseController,
            this::loadWarehouses
        );
        form.setVisible(true);
    }

    private void generateWarehouseReport() {
        try {
            String report = warehouseController.generateWarehouseReport();
            
            JTextArea reportArea = new JTextArea(20, 50);
            reportArea.setText(report);
            reportArea.setEditable(false);
            reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(reportArea);
            
            JOptionPane.showMessageDialog(this, scrollPane, 
                "Warehouse Report", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}