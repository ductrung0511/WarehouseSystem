package com.warehouse.views.orders;

import com.warehouse.controllers.OrderController;
import com.warehouse.models.Order;
import com.warehouse.views.components.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;


public class OrderStatusView extends JPanel {
    private final OrderController orderController;
    private  DataTable ordersTable;
    private List<Order> orders;

    public OrderStatusView() {
        this.orderController = new OrderController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadOrders();
    }

    private void initializeComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Orders table
        String[] columns = {"Order ID", "Customer ID", "Order Date", "Total Amount", "Status", "Last Updated"};
        ordersTable = new DataTable(columns);
        ordersTable.setColumnWidth(0, 80);
        ordersTable.setColumnWidth(1, 100);
        ordersTable.setColumnWidth(2, 120);
        ordersTable.setColumnWidth(3, 100);
        ordersTable.setColumnWidth(4, 120);
        ordersTable.setColumnWidth(5, 150);
        
        mainPanel.add(new JScrollPane(ordersTable.getTable()), BorderLayout.CENTER);
        
        // Status actions panel
        JPanel actionsPanel = createStatusActionsPanel();
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{
            "All", "Pending", "Processing", "Shipped", "Delivered", "Cancelled"
        });
        
        refreshButton.addActionListener(e -> loadOrders());
        statusFilter.addActionListener(e -> filterOrdersByStatus((String) statusFilter.getSelectedItem()));
        
        controlPanel.add(refreshButton);
        controlPanel.add(new JLabel("Filter Status:"));
        controlPanel.add(statusFilter);
        
        return controlPanel;
    }

    private JPanel createStatusActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Update Order Status"));
        
        JButton processButton = createStatusButton("🔄 Process", "Processing", Color.ORANGE);
        JButton shipButton = createStatusButton("🚚 Ship", "Shipped", Color.BLUE);
        JButton deliverButton = createStatusButton("✅ Deliver", "Delivered", Color.GREEN);
        JButton cancelButton = createStatusButton("❌ Cancel", "Cancelled", Color.RED);
        
        JButton bulkUpdateButton = new JButton("Bulk Status Update");
        bulkUpdateButton.addActionListener(e -> showBulkUpdateDialog());
        
        actionsPanel.add(processButton);
        actionsPanel.add(shipButton);
        actionsPanel.add(deliverButton);
        actionsPanel.add(cancelButton);
        actionsPanel.add(bulkUpdateButton);
        
        return actionsPanel;
    }

    private JButton createStatusButton(String text, String status, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(e -> updateSelectedOrdersStatus(status));
        return button;
    }

    private void loadOrders() {
        try {
            orders = orderController.getAllOrders();
            updateOrdersTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterOrdersByStatus(String status) {
        if ("All".equals(status)) {
            loadOrders();
            return;
        }
        
        try {
            orders = orderController.getOrdersByStatus(status);
            updateOrdersTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error filtering orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOrdersTable() {
        ordersTable.clearData();
        
        for (Order order : orders) {
            ordersTable.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomerId(),
                order.getDate(),
                String.format("$%.2f", order.getTotalAmount()),
                getStatusWithIcon(order.getStatus()),
                new java.util.Date() // Should come from order object - but for demo just like this ...
            });
        }
    }

    private String getStatusWithIcon(String status) {
        return switch (status) {
            case "Pending" -> "⏳ " + status;
            case "Processing" -> "🔄 " + status;
            case "Shipped" -> "🚚 " + status;
            case "Delivered" -> "✅ " + status;
            case "Cancelled" -> "❌ " + status;
            default -> status;
        };
    }

    private void updateSelectedOrdersStatus(String newStatus) {
        int[] selectedRows = ordersTable.getTable().getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one order", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Integer> orderIds = new ArrayList<>();
        List<String> currentStatuses = new ArrayList<>();
        
        for (int viewRow : selectedRows) {
            int modelRow = ordersTable.getTable().convertRowIndexToModel(viewRow);
            if (modelRow >= 0 && modelRow < orders.size()) {
                Order order = orders.get(modelRow);
                orderIds.add(order.getOrderId());
                currentStatuses.add(order.getStatus());
            }
        }
        
        // Validate status transitions
        for (String currentStatus : currentStatuses) {
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                JOptionPane.showMessageDialog(this, 
                    String.format("Cannot change status from '%s' to '%s' for some orders", 
                        currentStatus, newStatus),
                    "Invalid Status Change", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Update %d order(s) status to '%s'?", orderIds.size(), newStatus),
            "Confirm Bulk Status Update",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            for (int orderId : orderIds) {
                try {
                    boolean success = orderController.updateOrderStatus(orderId, newStatus);
                    if (success) successCount++;
                } catch (Exception e) {
                    System.err.println("Error updating order " + orderId + ": " + e.getMessage());
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                String.format("Successfully updated %d out of %d orders", successCount, orderIds.size()),
                "Update Complete", 
                JOptionPane.INFORMATION_MESSAGE);
                
            loadOrders();
        }
    }

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        return switch (currentStatus) {
            case "Pending" -> List.of("Processing", "Cancelled").contains(newStatus);
            case "Processing" -> List.of("Shipped", "Cancelled").contains(newStatus);
            case "Shipped" -> List.of("Delivered").contains(newStatus);
            case "Delivered", "Cancelled" -> false; // Final states
            default -> true;
        };
    }

    private void showBulkUpdateDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bulk Status Update", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        contentPanel.add(new JLabel("Select new status for all filtered orders:"));
        
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{
            "Processing", "Shipped", "Delivered", "Cancelled"
        });
        contentPanel.add(statusCombo);
        
        JButton applyButton = new JButton("Apply to All Filtered Orders");
        applyButton.addActionListener(e -> {
            String selectedStatus = (String) statusCombo.getSelectedItem();
            // Implementation for bulk update would go here
            JOptionPane.showMessageDialog(dialog, 
                "Bulk update functionality would be implemented here",
                "Feature Preview",
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        contentPanel.add(applyButton);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
}