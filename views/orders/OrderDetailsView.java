package com.warehouse.views.orders;

import com.warehouse.models.Order;
import com.warehouse.models.Product;

import javax.swing.*;
import java.awt.*;

public class OrderDetailsView extends JDialog {
    private final Order order;

    public OrderDetailsView(Window owner, Order order) {
        super(owner, "Order Details - #" + order.getOrderId(), ModalityType.APPLICATION_MODAL);
        this.order = order;
        
        initializeUI();
    }

    private void initializeUI() {
        setSize(600, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        
        // Header with order summary
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Order items
        JPanel itemsPanel = createItemsPanel();
        add(new JScrollPane(itemsPanel), BorderLayout.CENTER);
        
        // Footer with actions
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Order Summary"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        headerPanel.add(new JLabel("Order ID:"));
        headerPanel.add(new JLabel(String.valueOf(order.getOrderId())));
        
        headerPanel.add(new JLabel("Customer ID:"));
        headerPanel.add(new JLabel(String.valueOf(order.getCustomerId())));
        
        headerPanel.add(new JLabel("Order Date:"));
        headerPanel.add(new JLabel(String.valueOf(order.getDate())));
        
        headerPanel.add(new JLabel("Status:"));
        JLabel statusLabel = new JLabel(order.getStatus());
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        statusLabel.setForeground(getStatusColor(order.getStatus()));
        headerPanel.add(statusLabel);
        
        headerPanel.add(new JLabel("Total Amount:"));
        JLabel amountLabel = new JLabel(String.format("$%.2f", order.getTotalAmount()));
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountLabel.setForeground(Color.BLUE);
        headerPanel.add(amountLabel);
        
        headerPanel.add(new JLabel("Number of Items:"));
        headerPanel.add(new JLabel(String.valueOf(order.getProductList().size())));
        
        return headerPanel;
    }

    private JPanel createItemsPanel() {
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items"));
        
        // Create header
        JPanel headerPanel = new JPanel(new GridLayout(1, 5, 10, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createHeaderLabel("Product"));
        headerPanel.add(createHeaderLabel("Quantity"));
        headerPanel.add(createHeaderLabel("Unit Price"));
        headerPanel.add(createHeaderLabel("Subtotal"));
        headerPanel.add(createHeaderLabel("Stock Status"));
        itemsPanel.add(headerPanel);
        itemsPanel.add(new JSeparator());
        
        // Add items
        for (var entry : order.getProductList().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            System.out.println(product + "in OrderDetailsView - q : " + product.getQuantity());

            double subtotal = product.getPrice() * quantity;
            
            JPanel itemPanel = new JPanel(new GridLayout(1, 5, 10, 5));
            itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            itemPanel.add(new JLabel(product.getName()));
            itemPanel.add(new JLabel(String.valueOf(quantity)));
            itemPanel.add(new JLabel(String.format("$%.2f", product.getPrice())));
            itemPanel.add(new JLabel(String.format("$%.2f", subtotal)));
            
//            JLabel stockLabel = new JLabel(getStockStatus(product.getQuantity()));  
            JLabel stockLabel = new JLabel(product.getQuantity() + " ");
            

            stockLabel.setForeground(getStockColor(product.getQuantity()));
            itemPanel.add(stockLabel);
            
            itemsPanel.add(itemPanel);
            itemsPanel.add(new JSeparator());
        }
        
        return itemsPanel;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private String getStockStatus(int quantity) {
        if (quantity == 0) return "Out of Stock";
        if (quantity < 10) return "Low Stock";
        return "In Stock";
    }

    private Color getStockColor(int quantity) {
        if (quantity == 0) return Color.RED;
        if (quantity < 10) return Color.ORANGE;
        return Color.GREEN;
    }

    private Color getStatusColor(String status) {
        return switch (status) {
            case "Pending" -> Color.ORANGE;
            case "Processing" -> Color.BLUE;
            case "Shipped" -> new Color(75, 0, 130); // Purple
            case "Delivered" -> Color.GREEN;
            case "Cancelled" -> Color.RED;
            default -> Color.BLACK;
        };
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton closeButton = new JButton("Close");
        JButton printButton = new JButton("Print Details");
        
        closeButton.addActionListener(e -> dispose());
        printButton.addActionListener(e -> printOrderDetails());
        
        footerPanel.add(printButton);
        footerPanel.add(closeButton);
        
        return footerPanel;
    }

    private void printOrderDetails() {
        // In a real application, this would generate a printable report
        JOptionPane.showMessageDialog(this,
            "Print functionality would be implemented here.\n" +
            "Order details would be formatted for printing.",
            "Print Feature",
            JOptionPane.INFORMATION_MESSAGE);
    }
}