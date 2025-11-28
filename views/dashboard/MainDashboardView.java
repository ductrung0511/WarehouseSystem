package com.warehouse.views.dashboard;

import com.warehouse.controllers.WarehouseController;
import com.warehouse.controllers.ProductController;
import com.warehouse.controllers.OrderController;
import com.warehouse.models.Product;
import com.warehouse.models.Order;
import com.warehouse.models.Warehouse;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainDashboardView extends JPanel {
    private final WarehouseController warehouseController;
    private final ProductController productController;
    private final OrderController orderController;
    
    // Dashboard components
    private final JLabel totalProductsLabel = new JLabel("0");
    private final JLabel totalOrdersLabel = new JLabel("0");
    private final JLabel lowStockLabel = new JLabel("0");
    private final JLabel inventoryValueLabel = new JLabel("$0.00");
    private final JLabel pendingOrdersLabel = new JLabel("0");
    
    private final JTextArea alertsArea = new JTextArea();
    private final JTextArea recentActivityArea = new JTextArea();

    public MainDashboardView() {
        this.warehouseController = new WarehouseController();
        this.productController = new ProductController();
        this.orderController = new OrderController();
        
        initializeUI();
        loadDashboardData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Main container with padding
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        
        // Top section - Key metrics
        mainPanel.add(createMetricsPanel(), BorderLayout.NORTH);
        
        // Center section - Alerts and Recent Activity
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.addActionListener(e -> refreshDashboard());
        bottomPanel.add(refreshButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createMetricsPanel() {
        JPanel metricsPanel = new JPanel(new GridLayout(1, 5, 15, 15));
        metricsPanel.setBorder(BorderFactory.createTitledBorder("Key Metrics"));
        
        metricsPanel.add(createMetricCard("Total Products", totalProductsLabel, new Color(0, 0, 0)));
        metricsPanel.add(createMetricCard("Total Orders", totalOrdersLabel, new Color(0, 0, 0)));
        metricsPanel.add(createMetricCard("Low Stock Items", lowStockLabel, new Color(0, 0, 0)));
        metricsPanel.add(createMetricCard("Inventory Value", inventoryValueLabel, new Color(0, 0, 0)));
        metricsPanel.add(createMetricCard("Pending Orders", pendingOrdersLabel, new Color(0, 0, 0)));
        
        return metricsPanel;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        
        // Alerts panel
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBorder(BorderFactory.createTitledBorder("Alerts & Notifications"));
        
        alertsArea.setEditable(false);
        alertsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        alertsArea.setBackground(new Color(255, 255, 240));
        JScrollPane alertsScroll = new JScrollPane(alertsArea);
        alertsScroll.setPreferredSize(new Dimension(300, 200));
        
        alertsPanel.add(alertsScroll, BorderLayout.CENTER);
        
        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        
        recentActivityArea.setEditable(false);
        recentActivityArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        recentActivityArea.setBackground(new Color(240, 255, 240));
        JScrollPane activityScroll = new JScrollPane(recentActivityArea);
        activityScroll.setPreferredSize(new Dimension(300, 200));
        
        activityPanel.add(activityScroll, BorderLayout.CENTER);
        
        centerPanel.add(alertsPanel);
        centerPanel.add(activityPanel);
        
        return centerPanel;
    }

    private void loadDashboardData() {
        try {
            // Load product data
            List<Product> products = productController.getAllProducts();
            totalProductsLabel.setText(String.valueOf(products.size()));
            
            // Load low stock products (threshold: 10 items)
            List<Product> lowStockProducts = warehouseController.getLowStockProducts(10);
            lowStockLabel.setText(String.valueOf(lowStockProducts.size()));
            
            // Load inventory value
            double inventoryValue = calculateInventoryValue(products);
            inventoryValueLabel.setText(String.format("$%.2f", inventoryValue));
            
            // Load order data
            List<Order> orders = orderController.getAllOrders();
            totalOrdersLabel.setText(String.valueOf(orders.size()));
            
            // Count pending orders
            long pendingOrders = orders.stream()
                .filter(order -> "Pending".equalsIgnoreCase(order.getStatus()))
                .count();
            pendingOrdersLabel.setText(String.valueOf(pendingOrders));
            
            // Generate alerts
            generateAlerts(lowStockProducts, products);
            
            // Generate recent activity
            generateRecentActivity(orders, products);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading dashboard data: " + e.getMessage(), 
                "Dashboard Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private double calculateInventoryValue(List<Product> products) {
        return products.stream()
            .mapToDouble(p -> p.getPrice() * p.getQuantity())
            .sum();
    }

    private void generateAlerts(List<Product> lowStockProducts, List<Product> allProducts) {
        StringBuilder alerts = new StringBuilder();
        
        // Low stock alerts
        if (!lowStockProducts.isEmpty()) {
            alerts.append("⚠️ LOW STOCK ALERTS:\n");
            for (Product product : lowStockProducts) {
                String severity = product.getQuantity() == 0 ? "OUT OF STOCK" : "LOW STOCK";
                alerts.append(String.format("• %s: %d left (%s)\n", 
                    product.getName(), product.getQuantity(), severity));
            }
            alerts.append("\n");
        }
        
        // Out of stock alerts
        List<Product> outOfStock = allProducts.stream()
            .filter(p -> p.getQuantity() == 0)
            .toList();
        
        if (!outOfStock.isEmpty()) {
            alerts.append("❌ OUT OF STOCK:\n");
            for (Product product : outOfStock) {
                alerts.append(String.format("• %s\n", product.getName()));
            }
            alerts.append("\n");
        }
        
        // Warehouse capacity alerts (placeholder - you can expand this)
        alerts.append("📊 Inventory Summary:\n");
        alerts.append(String.format("• Total Products: %d\n", allProducts.size()));
        alerts.append(String.format("• Low Stock Items: %d\n", lowStockProducts.size()));
        alerts.append(String.format("• Out of Stock: %d\n", outOfStock.size()));
        
        if (alerts.length() == 0) {
            alerts.append("✅ No critical alerts at this time.\n");
            alerts.append("All systems operational.");
        }
        
        alertsArea.setText(alerts.toString());
    }

    private void generateRecentActivity(List<Order> orders, List<Product> products) {
        StringBuilder activity = new StringBuilder();
        
        // Recent orders (last 5)
        activity.append("🆕 RECENT ORDERS:\n");
        orders.stream()
            .limit(5)
            .forEach(order -> {
                activity.append(String.format("• Order #%d: %s ($%.2f)\n", 
                    order.getOrderId(), 
                    order.getStatus(),
                    order.getTotalAmount()));
            });
        
        activity.append("\n");
        
        // Recent products (last 3)
        activity.append("📦 RECENT PRODUCTS:\n");
        products.stream()
            .limit(3)
            .forEach(product -> {
                activity.append(String.format("• %s: %d in stock\n", 
                    product.getName(), 
                    product.getQuantity()));
            });
        
        activity.append("\n");
        
        // Quick stats
        activity.append("📈 QUICK STATS:\n");
        activity.append(String.format("• Total Products: %d\n", products.size()));
        activity.append(String.format("• Total Orders: %d\n", orders.size()));
        
        long pendingCount = orders.stream()
            .filter(order -> "Pending".equalsIgnoreCase(order.getStatus()))
            .count();
        activity.append(String.format("• Pending Orders: %d\n", pendingCount));
        
        recentActivityArea.setText(activity.toString());
    }

    private void refreshDashboard() {
        loadDashboardData();
        JOptionPane.showMessageDialog(this, 
            "Dashboard data refreshed successfully!", 
            "Refresh Complete", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Public method to allow external refresh
    public void refresh() {
        refreshDashboard();
    }
}