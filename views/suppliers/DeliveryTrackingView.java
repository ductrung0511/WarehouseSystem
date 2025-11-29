package com.warehouse.views.suppliers;

import com.warehouse.controllers.PurchaseOrderController;
import com.warehouse.models.PurchaseOrder;
import com.warehouse.models.Product;
import com.warehouse.views.components.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DeliveryTrackingView extends JPanel {
    private final PurchaseOrderController purchaseOrderController;
    private  DataTable deliveriesTable;
    private List<PurchaseOrder> purchaseOrders;

    public DeliveryTrackingView() {
        this.purchaseOrderController = new PurchaseOrderController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadRealData();
    }

    private void initializeComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header with stats
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        try {
            int totalOrders = purchaseOrderController.getAllPurchaseOrders().size();
            int pendingOrders = purchaseOrderController.getPendingOrdersCount();
            int approvedOrders = purchaseOrderController.getApprovedOrdersCount();
            int completedOrders = purchaseOrderController.getCompletedOrdersCount();
            
            headerPanel.add(createStatCard("Total Orders", String.valueOf(totalOrders), Color.BLACK));
            headerPanel.add(createStatCard("Pending", String.valueOf(pendingOrders), Color.BLACK));
            headerPanel.add(createStatCard("Approved", String.valueOf(approvedOrders), Color.BLACK));
            headerPanel.add(createStatCard("Completed", String.valueOf(completedOrders), Color.BLACK));
            
        } catch (Exception e) {
            headerPanel.add(createStatCard("Total Orders", "0", Color.BLACK));
            headerPanel.add(createStatCard("Pending", "0", Color.BLACK));
            headerPanel.add(createStatCard("Approved", "0", Color.BLACK));
            headerPanel.add(createStatCard("Completed", "0", Color.BLACK));
        }
        
        return headerPanel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createControlPanel() {
    JPanel controlPanel = new JPanel(new BorderLayout());
    
    // Filter controls
    JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
    JComboBox<String> statusFilter = new JComboBox<>(new String[]{
        "All", "Pending", "Approved", "Cancelled", "Completed"
    });
    JButton refreshButton = new JButton("Refresh");
    JButton viewDetailsButton = new JButton("View Details");
    JButton updateStatusButton = new JButton("Update Status");
    
    statusFilter.addActionListener(e -> filterByStatus((String) statusFilter.getSelectedItem()));
    refreshButton.addActionListener(e -> loadRealData());
    viewDetailsButton.addActionListener(e -> viewOrderDetails());
    updateStatusButton.addActionListener(e -> updateOrderStatus());
    
    filterPanel.add(new JLabel("Filter by Status:"));
    filterPanel.add(statusFilter);
    filterPanel.add(refreshButton);
    filterPanel.add(viewDetailsButton);
    filterPanel.add(updateStatusButton);
    
    // Deliveries table - CHANGED: "Items Count" to "Total Quantity"
    String[] columns = {"PO ID", "Supplier ID", "Order Date", "Total Cost", "Status", "Total Quantity"};
    deliveriesTable = new DataTable(columns);
    deliveriesTable.setColumnWidth(0, 80);
    deliveriesTable.setColumnWidth(1, 100);
    deliveriesTable.setColumnWidth(2, 150);
    deliveriesTable.setColumnWidth(3, 100);
    deliveriesTable.setColumnWidth(4, 100);
    deliveriesTable.setColumnWidth(5, 100);
    
    controlPanel.add(filterPanel, BorderLayout.NORTH);
    controlPanel.add(new JScrollPane(deliveriesTable.getTable()), BorderLayout.CENTER);
    
    return controlPanel;
}

private void updateDeliveriesTable() {
    deliveriesTable.clearData();
    
    for (PurchaseOrder order : purchaseOrders) {
        // Calculate total quantity from all items
        int totalQuantity = order.getProductList().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
        
        deliveriesTable.addRow(new Object[]{
            "PO-" + order.getPoId(),
            order.getSupplierId(),
            order.getOrderDate(),
            String.format("$%.2f", order.getTotalCost()),

            getStatusWithIcon(order.getStatus()),
            totalQuantity  // Show total quantity instead of item count
        });
    }
    
    applyStatusStyling();
}

    private void loadRealData() {
        try {
            purchaseOrders = purchaseOrderController.getAllPurchaseOrders();
            updateDeliveriesTable();
            updateHeaderStats();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading purchase orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }


    private void updateHeaderStats() {
        // Header stats are automatically updated when we reload data
        // since they're calculated fresh each time
    }

    private String getStatusWithIcon(String status) {
        return switch (status) {
            case "Pending" -> "⏳ " + status;
            case "Approved" -> "✅ " + status;
            case "Cancelled" -> "❌ " + status;
            case "Completed" -> "🏁 " + status;
            default -> status;
        };
    }

    private void applyStatusStyling() {
        // Apply color coding based on status (you can enhance your DataTable component for this)
        // For now, we'll just log the status for debugging
        System.out.println("Applied status styling to " + purchaseOrders.size() + " orders");
    }

    private void filterByStatus(String status) {
        if ("All".equals(status)) {
            loadRealData();
            return;
        }
        
        try {
            purchaseOrders = purchaseOrderController.getPurchaseOrdersByStatus(status);
            updateDeliveriesTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error filtering orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewOrderDetails() {
        int selectedRow = deliveriesTable.getSelectedModelRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to view details", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PurchaseOrder selectedOrder = purchaseOrders.get(selectedRow);
        showOrderDetailsDialog(selectedOrder);
    }

    private void showOrderDetailsDialog(PurchaseOrder order) {
        JDialog detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Purchase Order Details - PO-" + order.getPoId(), true);
        detailsDialog.setSize(600, 500);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout(10, 10));
        
        // Header info
        JPanel headerPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        headerPanel.add(new JLabel("PO ID:"));
        headerPanel.add(new JLabel("PO-" + order.getPoId()));
        headerPanel.add(new JLabel("Supplier ID:"));
        headerPanel.add(new JLabel(String.valueOf(order.getSupplierId())));
        headerPanel.add(new JLabel("Order Date:"));
        headerPanel.add(new JLabel(order.getOrderDate().toString()));
        headerPanel.add(new JLabel("Status:"));
        JLabel statusLabel = new JLabel(order.getStatus());
        statusLabel.setForeground(getStatusColor(order.getStatus()));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));
        headerPanel.add(statusLabel);
        
        // Items table
        String[] columns = {"Product", "Quantity", "Unit Price", "Subtotal"};
        DataTable itemsTable = new DataTable(columns);
        
        Map<Product, Integer> productList = order.getProductList();
        for (Map.Entry<Product, Integer> entry : productList.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double subtotal = product.getPrice() * quantity;
            
            itemsTable.addRow(new Object[]{
                product.getName(),
                quantity,
                String.format("$%.2f", product.getPrice()),
                String.format("$%.2f", subtotal)
            });
        }
        
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items (" + productList.size() + " items)"));
        itemsPanel.add(new JScrollPane(itemsTable.getTable()), BorderLayout.CENTER);
        
        // Total summary
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total Cost: " + String.format("$%.2f", order.getTotalCost()));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(Color.BLUE);
        summaryPanel.add(totalLabel);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(itemsPanel, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        detailsDialog.add(contentPanel, BorderLayout.CENTER);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        detailsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        detailsDialog.setVisible(true);
    }

    private void updateOrderStatus() {
        int selectedRow = deliveriesTable.getSelectedModelRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase order to update", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PurchaseOrder selectedOrder = purchaseOrders.get(selectedRow);
        
        // Create status update dialog
        String[] statusOptions = {"Pending", "Approved", "Cancelled", "Completed"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Update status for PO-" + selectedOrder.getPoId() + ":",
            "Update Order Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            selectedOrder.getStatus());
        
        if (newStatus != null && !newStatus.equals(selectedOrder.getStatus())) {
            try {
                boolean success = purchaseOrderController.updatePurchaseOrderStatus(selectedOrder.getPoId(), newStatus);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Order status updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadRealData(); // Refresh the data
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to update order status", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error updating order status: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Color getStatusColor(String status) {
        return switch (status) {
            case "Pending" -> Color.ORANGE;
            case "Approved" -> Color.BLUE;
            case "Cancelled" -> Color.RED;
            case "Completed" -> Color.GREEN;
            default -> Color.BLACK;
        };
    }
}