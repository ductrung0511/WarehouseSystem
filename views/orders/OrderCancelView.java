package com.warehouse.views.orders;

import com.warehouse.controllers.OrderController;
import com.warehouse.models.Order;
import com.warehouse.views.components.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderCancelView extends JPanel {
    private final OrderController orderController;
    private  DataTable ordersTable;
    private List<Order> orders;

    public OrderCancelView() {
        this.orderController = new OrderController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadCancellableOrders();
    }

    private void initializeComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header with instructions
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Orders table
        String[] columns = {"Order ID", "Customer ID", "Order Date", "Total Amount", "Status", "Items Count"};
        ordersTable = new DataTable(columns);
        ordersTable.setColumnWidth(0, 80);
        ordersTable.setColumnWidth(1, 100);
        ordersTable.setColumnWidth(2, 120);
        ordersTable.setColumnWidth(3, 100);
        ordersTable.setColumnWidth(4, 100);
        ordersTable.setColumnWidth(5, 80);
        
        mainPanel.add(new JScrollPane(ordersTable.getTable()), BorderLayout.CENTER);
        
        // Cancel actions panel
        JPanel actionsPanel = createCancelActionsPanel();
        mainPanel.add(actionsPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 245, 245));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.RED, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel("Order Cancellation Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.RED);
        
        JLabel infoLabel = new JLabel(
            "<html>Only orders with status 'Pending' or 'Processing' can be cancelled. " +
            "Cancelled orders cannot be restored.</html>"
        );
        infoLabel.setForeground(Color.DARK_GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(infoLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }

    private JPanel createCancelActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBorder(BorderFactory.createTitledBorder("Cancellation Actions"));
        
        JButton cancelOrderButton = new JButton("Cancel Selected Order");
        JButton bulkCancelButton = new JButton("Bulk Cancel Orders");
        JButton refreshButton = new JButton("Refresh List");
        
        cancelOrderButton.setBackground(Color.RED);
        cancelOrderButton.setForeground(Color.WHITE);
        bulkCancelButton.setBackground(new Color(220, 100, 100));
        bulkCancelButton.setForeground(Color.WHITE);
        
        cancelOrderButton.addActionListener(e -> cancelSelectedOrder());
        bulkCancelButton.addActionListener(e -> showBulkCancelDialog());
        refreshButton.addActionListener(e -> loadCancellableOrders());
        
        actionsPanel.add(cancelOrderButton);
        actionsPanel.add(bulkCancelButton);
        actionsPanel.add(refreshButton);
        
        return actionsPanel;
    }

    private void loadCancellableOrders() {
        try {
            // Load only orders that can be cancelled (Pending or Processing)
            List<Order> allOrders = orderController.getAllOrders();
            orders = allOrders.stream()
                .filter(order -> isCancellable(order.getStatus()))
                .toList();
                
            updateOrdersTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isCancellable(String status) {
        return "Pending".equals(status) || "Processing".equals(status);
    }

    private void updateOrdersTable() {
        ordersTable.clearData();
        
        for (Order order : orders) {
            ordersTable.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomerId(),
                order.getDate(),
                String.format("$%.2f", order.getTotalAmount()),
                order.getStatus(),
                order.getProductList().size()
            });
        }
        
        // Update table appearance for cancellable orders
//        ordersTable.getTable().setDefaultRenderer(Object.class, new CancellableOrderRenderer());
    }

    private void cancelSelectedOrder() {
        int selectedRow = ordersTable.getSelectedModelRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select an order to cancel", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Order selectedOrder = orders.get(selectedRow);
        showCancellationConfirmation(selectedOrder);
    }

    private void showCancellationConfirmation(Order order) {
        // Create detailed cancellation dialog
        JDialog confirmDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Confirm Order Cancellation", true);
        confirmDialog.setSize(500, 300);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Warning message
        JPanel warningPanel = new JPanel(new BorderLayout());
        warningPanel.setBackground(new Color(255, 230, 230));
        warningPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        
        JLabel warningIcon = new JLabel("⚠️");
        warningIcon.setFont(new Font("SansSerif", Font.BOLD, 24));
        warningIcon.setHorizontalAlignment(SwingConstants.CENTER);
        warningIcon.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel warningText = new JLabel(
            "<html><center><b>You are about to cancel this order!</b><br>" +
            "This action cannot be undone.</center></html>"
        );
        warningText.setHorizontalAlignment(SwingConstants.CENTER);
        
        warningPanel.add(warningIcon, BorderLayout.WEST);
        warningPanel.add(warningText, BorderLayout.CENTER);
        
        // Order details
        JTextArea orderDetails = new JTextArea();
        orderDetails.setEditable(false);
        orderDetails.setText(buildOrderDetailsText(order));
        orderDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Reason for cancellation (optional)
        JPanel reasonPanel = new JPanel(new BorderLayout());
        reasonPanel.add(new JLabel("Reason for cancellation (optional):"), BorderLayout.NORTH);
        JTextField reasonField = new JTextField();
        reasonPanel.add(reasonField, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Confirm Cancellation");
        JButton cancelButton = new JButton("Keep Order");
        
        confirmButton.setBackground(Color.RED);
        confirmButton.setForeground(Color.WHITE);
        
        confirmButton.addActionListener(e -> {
            executeCancellation(order, reasonField.getText().trim());
            confirmDialog.dispose();
        });
        cancelButton.addActionListener(e -> confirmDialog.dispose());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(confirmButton);
        
        contentPanel.add(warningPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(orderDetails), BorderLayout.CENTER);
        contentPanel.add(reasonPanel, BorderLayout.SOUTH);
        
        confirmDialog.add(contentPanel, BorderLayout.CENTER);
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);
        confirmDialog.setVisible(true);
    }

    private String buildOrderDetailsText(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append("Order ID: ").append(order.getOrderId()).append("\n");
        sb.append("Customer ID: ").append(order.getCustomerId()).append("\n");
        sb.append("Order Date: ").append(order.getDate()).append("\n");
        sb.append("Total Amount: $").append(String.format("%.2f", order.getTotalAmount())).append("\n");
        sb.append("Status: ").append(order.getStatus()).append("\n\n");
        sb.append("Items:\n");
        sb.append("------\n");
        
        for (var entry : order.getProductList().entrySet()) {
            var product = entry.getKey();
            int quantity = entry.getValue();
            sb.append(String.format("- %s x%d @ $%.2f each\n", 
                product.getName(), quantity, product.getPrice()));
        }
        
        return sb.toString();
    }

    private void executeCancellation(Order order, String reason) {
        try {
            boolean success = orderController.updateOrderStatus(order.getOrderId(), "Cancelled");
            
            if (success) {
                // Log cancellation reason (in real app, this would be saved to database)
                if (!reason.isEmpty()) {
                    System.out.println("Order #" + order.getOrderId() + " cancelled. Reason: " + reason);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Order #" + order.getOrderId() + " has been cancelled successfully.",
                    "Cancellation Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                loadCancellableOrders();
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to cancel order #" + order.getOrderId(),
                    "Cancellation Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error cancelling order: " + e.getMessage(),
                "Cancellation Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showBulkCancelDialog() {
        int[] selectedRows = ordersTable.getTable().getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select orders to cancel", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Cancel %d selected order(s)? This action cannot be undone.", selectedRows.length),
            "Confirm Bulk Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            int successCount = 0;
            for (int viewRow : selectedRows) {
                int modelRow = ordersTable.getTable().convertRowIndexToModel(viewRow);
                if (modelRow >= 0 && modelRow < orders.size()) {
                    Order order = orders.get(modelRow);
                    try {
                        boolean success = orderController.updateOrderStatus(order.getOrderId(), "Cancelled");
                        if (success) successCount++;
                    } catch (Exception e) {
                        System.err.println("Error cancelling order " + order.getOrderId() + ": " + e.getMessage());
                    }
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                String.format("Successfully cancelled %d out of %d orders", successCount, selectedRows.length),
                "Bulk Cancellation Complete", 
                JOptionPane.INFORMATION_MESSAGE);
                
            loadCancellableOrders();
        }
    }

//    // Custom renderer for cancellable orders table
//    private static class CancellableOrderRenderer extends DefaultTableCellRenderer {
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, 
//                                                     boolean isSelected, boolean hasFocus, 
//                                                     int row, int column) {
//            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//            
//            if (!isSelected) {
//                String status = (String) table.getModel().getValueAt(row, 4); // Status column
//                if ("Pending".equals(status)) {
//                    c.setBackground(new Color(255, 255, 200)); // Light yellow for pending
//                } else if ("Processing".equals(status)) {
//                    c.setBackground(new Color(255, 230, 230)); // Light red for processing
//                } else {
//                    c.setBackground(Color.WHITE);
//                }
//            }
//            
//            return c;
//        }
//    }
}