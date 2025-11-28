package com.warehouse.views.orders;

import com.warehouse.controllers.OrderController;
import com.warehouse.models.Order;
import com.warehouse.views.components.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderListView extends JPanel {
    private final OrderController orderController;
    private  DataTable ordersTable;
    private  DataTable orderItemsTable;
    private List<Order> orders;

    public OrderListView() {
        this.orderController = new OrderController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadOrders();
    }

    private void initializeComponents() {
        // Main split pane for orders and order items
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.6);

        // Left panel - Orders list
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Order History"));
        
        // Orders table
        String[] orderColumns = {"Order ID", "Customer ID", "Order Date", "Total Amount", "Status", "Items Count"};
        ordersTable = new DataTable(orderColumns);
        ordersTable.setColumnWidth(0, 80);
        ordersTable.setColumnWidth(1, 100);
        ordersTable.setColumnWidth(2, 120);
        ordersTable.setColumnWidth(3, 100);
        ordersTable.setColumnWidth(4, 100);
        ordersTable.setColumnWidth(5, 80);
        
        // Add selection listener to show order items when order is selected
        ordersTable.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedModelRow();
                if (selectedRow >= 0 && selectedRow < orders.size()) {
                    showOrderItems(orders.get(selectedRow));
                }
            }
        });

        leftPanel.add(new JScrollPane(ordersTable.getTable()), BorderLayout.CENTER);

        // Right panel - Order items for selected order
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));
        
        String[] itemColumns = {"Product ID", "Product Name", "Quantity", "Unit Price", "Subtotal"};
        orderItemsTable = new DataTable(itemColumns);
        rightPanel.add(new JScrollPane(orderItemsTable.getTable()), BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = createControlPanel();
        leftPanel.add(controlPanel, BorderLayout.NORTH);

        // Order actions panel
        JPanel actionsPanel = createActionsPanel();
        rightPanel.add(actionsPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        JButton createOrderButton = new JButton("Create New Order");
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Pending", "Processing", "Shipped", "Delivered", "Cancelled"});
        
        refreshButton.addActionListener(e -> loadOrders());
        createOrderButton.addActionListener(e -> createNewOrder());
        statusFilter.addActionListener(e -> filterOrdersByStatus((String) statusFilter.getSelectedItem()));
        
        controlPanel.add(refreshButton);
        controlPanel.add(createOrderButton);
        controlPanel.add(new JLabel("Filter by Status:"));
        controlPanel.add(statusFilter);
        
        return controlPanel;
    }

    private JPanel createActionsPanel() {
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton processButton = new JButton("Process Order");
        JButton shipButton = new JButton("Ship Order");
        JButton cancelButton = new JButton("Cancel Order");
        JButton viewDetailsButton = new JButton("View Full Details");
        
        processButton.addActionListener(e -> updateOrderStatus("Processing"));
        shipButton.addActionListener(e -> updateOrderStatus("Shipped"));
        cancelButton.addActionListener(e -> updateOrderStatus("Cancelled"));
        viewDetailsButton.addActionListener(e -> showOrderDetails());
        
        actionsPanel.add(processButton);
        actionsPanel.add(shipButton);
        actionsPanel.add(cancelButton);
        actionsPanel.add(viewDetailsButton);
        
        return actionsPanel;
    }

    private void loadOrders() {
        try {
            orders = orderController.getAllOrders();
            ordersTable.clearData();
            
            for (Order order : orders) {
                System.out.println(order);
                ordersTable.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getDate(),
                    String.format("$%.2f", order.getTotalAmount()),
                    order.getStatus(),
                    order.getProductList().size()
                });
            }
            
            // Clear order items table
            orderItemsTable.clearData();
            
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
            List<Order> filteredOrders = orderController.getOrdersByStatus(status);
            ordersTable.clearData();
            
            for (Order order : filteredOrders) {
                ordersTable.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getDate(),
                    String.format("$%.2f", order.getTotalAmount()),
                    order.getStatus(),
                    order.getProductList().size()
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error filtering orders: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOrderItems(Order order) {
        try {
            orderItemsTable.clearData();
            
            for (var entry : order.getProductList().entrySet()) {
                var product = entry.getKey();
                int quantity = entry.getValue();
                double subtotal = product.getPrice() * quantity;
                
                orderItemsTable.addRow(new Object[]{
                    product.getProductId(),
                    product.getName(),
                    quantity,
                    String.format("$%.2f", product.getPrice()),
                    String.format("$%.2f", subtotal)
                });
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading order items: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewOrder() {
        // Get the parent window (JFrame) that contains this OrderListView
    Window parentWindow = SwingUtilities.getWindowAncestor(this);
    OrderCreationView view = new OrderCreationView(parentWindow, orderController, this::loadOrders);
    view.setVisible(true);
//        new OrderCreationView(this, orderController, this::loadOrders);
    }

    private void updateOrderStatus(String newStatus) {
        int selectedRow = ordersTable.getSelectedModelRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select an order first", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Order selectedOrder = orders.get(selectedRow);
        String currentStatus = selectedOrder.getStatus();
        
        // Validate status transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            JOptionPane.showMessageDialog(this, 
                String.format("Cannot change status from '%s' to '%s'", currentStatus, newStatus),
                "Invalid Status Change", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Change order #%d status from '%s' to '%s'?", 
                selectedOrder.getOrderId(), currentStatus, newStatus),
            "Confirm Status Change",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = orderController.updateOrderStatus(selectedOrder.getOrderId(), newStatus);
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Order status updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadOrders();
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

    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid status transitions
        return switch (currentStatus) {
            case "Pending" -> List.of("Processing", "Cancelled").contains(newStatus);
            case "Processing" -> List.of("Shipped", "Cancelled").contains(newStatus);
            case "Shipped" -> List.of("Delivered").contains(newStatus);
            case "Delivered", "Cancelled" -> false; // Final states
            default -> true;
        };
    }

    private void showOrderDetails() {
        int selectedRow = ordersTable.getSelectedModelRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select an order first", 
                "Selection Required", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Order selectedOrder = orders.get(selectedRow);
        // Get the parent window (JFrame) that contains this OrderListView
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        OrderDetailsView view = new OrderDetailsView(parentWindow, selectedOrder);
        view.setVisible(true);
    }
}