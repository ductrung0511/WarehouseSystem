package com.warehouse.views.warehouse;

import com.warehouse.controllers.WarehouseController;
import com.warehouse.services.InventoryService;
import com.warehouse.models.Warehouse;
import com.warehouse.models.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WarehouseReportView extends JPanel {
    private final WarehouseController warehouseController;
    private final InventoryService inventoryService;
    
    private final JTextArea reportArea = new JTextArea();
    private final JComboBox<String> reportTypeCombo = new JComboBox<>();
    private final JSpinner thresholdSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 100, 1));

    public WarehouseReportView() {
        this.warehouseController = new WarehouseController();
        this.inventoryService = new InventoryService();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        generateDefaultReport();
    }

    private void initializeComponents() {
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        reportTypeCombo.addItem("Inventory Summary");
        reportTypeCombo.addItem("Warehouse Status");
        reportTypeCombo.addItem("Low Stock Alert");
        reportTypeCombo.addItem("Inventory Value");
        
        JButton generateButton = new JButton("Generate Report");
        JButton exportButton = new JButton("Export to File");
        
        controlPanel.add(new JLabel("Report Type:"));
        controlPanel.add(reportTypeCombo);
        controlPanel.add(new JLabel("Low Stock Threshold:"));
        controlPanel.add(thresholdSpinner);
        controlPanel.add(generateButton);
        controlPanel.add(exportButton);
        
        // Report area
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setBackground(new Color(248, 248, 248));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Report Output"));
        
        add(controlPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Event handlers
        generateButton.addActionListener(e -> generateReport());
        exportButton.addActionListener(e -> exportReport());
    }

    private void generateDefaultReport() {
        try {
            String inventorySummary = warehouseController.getInventorySummary();
            reportArea.setText("=== DEFAULT INVENTORY REPORT ===\n\n");
            reportArea.append(inventorySummary);
            reportArea.append("\n\nUse the controls above to generate specific reports.");
            
        } catch (Exception e) {
            reportArea.setText("Error generating default report: " + e.getMessage());
        }
    }

    private void generateReport() {
        try {
            String reportType = (String) reportTypeCombo.getSelectedItem();
            int threshold = (Integer) thresholdSpinner.getValue();
            
            StringBuilder report = new StringBuilder();
            report.append("=== ").append(reportType).append(" ===\n\n");
            report.append("Generated on: ").append(new java.util.Date()).append("\n\n");
            
            switch (reportType) {
                case "Inventory Summary":
                    generateInventorySummary(report);
                    break;
                case "Warehouse Status":
                    generateWarehouseStatus(report);
                    break;
                case "Low Stock Alert":
                    generateLowStockAlert(report, threshold);
                    break;
                case "Inventory Value":
                    generateInventoryValue(report);
                    break;
            }
            
            reportArea.setText(report.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating report: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateInventorySummary(StringBuilder report) {
        String summary = warehouseController.getInventorySummary();
        report.append(summary);
        
        report.append("\n\n--- Warehouse Details ---\n");
        List<Warehouse> warehouses = warehouseController.getAllWarehouses();
        for (Warehouse warehouse : warehouses) {
            double utilization = warehouse.getCapacity() > 0 ? 
                (double) warehouse.getCurrentStock() / warehouse.getCapacity() * 100 : 0;
            
            report.append(String.format("\n%s (%s)\n", warehouse.getName(), warehouse.getLocation()));
            report.append(String.format("  Capacity: %d | Current: %d | Utilization: %.1f%%\n", 
                warehouse.getCapacity(), warehouse.getCurrentStock(), utilization));
            
            if (utilization > 90) {
                report.append("  ⚠️  HIGH UTILIZATION\n");
            } else if (utilization < 20) {
                report.append("  💤 LOW UTILIZATION\n");
            }
        }
    }

    private void generateWarehouseStatus(StringBuilder report) {
        List<Warehouse> warehouses = warehouseController.getAllWarehouses();
        
        report.append("Warehouse Status Overview:\n");
        report.append("==========================\n\n");
        
        for (Warehouse warehouse : warehouses) {
            report.append(String.format("🏭 %s\n", warehouse.getName()));
            report.append(String.format("   Location: %s\n", warehouse.getLocation()));
            report.append(String.format("   Capacity: %d units\n", warehouse.getCapacity()));
            report.append(String.format("   Current Stock: %d units\n", warehouse.getCurrentStock()));
            
            double utilization = warehouse.getCapacity() > 0 ? 
                (double) warehouse.getCurrentStock() / warehouse.getCapacity() * 100 : 0;
            report.append(String.format("   Utilization: %.1f%%\n", utilization));
            
            // Status indicator
            if (utilization >= 95) {
                report.append("   🚨 CRITICAL - Near full capacity!\n");
            } else if (utilization >= 80) {
                report.append("   ⚠️  WARNING - High utilization\n");
            } else if (utilization >= 50) {
                report.append("   ✅ OPTIMAL - Good utilization\n");
            } else {
                report.append("   💤 LOW - Underutilized\n");
            }
            
            report.append("\n");
        }
    }

    private void generateLowStockAlert(StringBuilder report, int threshold) {
        List<Product> lowStockProducts = warehouseController.getLowStockProducts(threshold);
        
        report.append(String.format("Low Stock Alert (Threshold: %d)\n", threshold));
        report.append("================================\n\n");
        
        if (lowStockProducts.isEmpty()) {
            report.append("✅ No low stock items found.\n");
            report.append("All products are above the threshold.\n");
        } else {
            report.append(String.format("Found %d product(s) below threshold:\n\n", lowStockProducts.size()));
            
            for (Product product : lowStockProducts) {
                String status = product.getQuantity() == 0 ? "OUT OF STOCK" : "LOW STOCK";
                Color color = product.getQuantity() == 0 ? Color.RED : Color.ORANGE;
                
                report.append(String.format("• %s (ID: %d)\n", product.getName(), product.getProductId()));
                report.append(String.format("  Category: %s\n", product.getCategory()));
                report.append(String.format("  Current Stock: %d\n", product.getQuantity()));
                report.append(String.format("  Status: %s\n", status));
                report.append(String.format("  Price: $%.2f\n\n", product.getPrice()));
            }
            
            report.append("⚠️  Consider restocking these items soon.\n");
        }
    }

    private void generateInventoryValue(StringBuilder report) {
        double totalValue = inventoryService.getTotalInventoryValue();
        List<Product> products = warehouseController.getAllWarehouses().get(0).getProducts(); // Simplified
        
        report.append("Inventory Value Report\n");
        report.append("======================\n\n");
        report.append(String.format("Total Inventory Value: $%.2f\n\n", totalValue));
        
        report.append("Product Value Breakdown:\n");
        report.append("------------------------\n");
        
        // This would normally come from a proper service method
        // For now, we'll show a simplified version
        report.append("(Detailed product value breakdown would be shown here)\n");
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new java.io.File("warehouse_report.txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.PrintWriter writer = new java.io.PrintWriter(file);
                writer.write(reportArea.getText());
                writer.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Report exported successfully to: " + file.getAbsolutePath(), 
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting report: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}