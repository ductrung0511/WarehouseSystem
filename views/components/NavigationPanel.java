package com.warehouse.views.components;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class NavigationPanel extends JPanel {

    public NavigationPanel(Consumer<String> onNavigate) {
        setLayout(new GridLayout(0, 1, 0, 10));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        setBackground(new Color(240, 240, 240));

        add(navButton("📊 Dashboard", "DASHBOARD", onNavigate));
        add(navButton("📦 Products", "PRODUCTS", onNavigate));
//        add(navButton("🔍 Search Products", "SEARCH", onNavigate));
        add(navButton("🛒 Orders (POS)", "ORDERS", onNavigate));
        
        add(navButton("🏭 Warehouses", "WAREHOUSE", onNavigate));
//        add(navButton("🔄 Transfers", "TRANSFER", onNavigate));
        add(navButton("📋 Warehouse Reports", "WAREHOUSE_REPORT", onNavigate));
        
//        add(navButton("🔄 Order Status", "ORDER_STATUS", onNavigate));
        add(navButton("❌ Cancel Orders", "ORDER_CANCEL", onNavigate));
        
        
//        add(createSectionLabel("Suppliers"));
        add(navButton("🏢 Supplier List", "SUPPLIERS", onNavigate));
        add(navButton("📦 Purchase Orders", "PURCHASE_ORDERS", onNavigate));
        add(navButton("🚚 PO Tracking", "DELIVERY_TRACKING", onNavigate));
        add(Box.createVerticalGlue());
        
        // Add some bottom buttons
        add(navButton("⚙️ Settings", "SETTINGS", onNavigate));
        add(navButton("❓ Help", "HELP", onNavigate));
    }

    private JButton navButton(String text, String card, Consumer<String> onNavigate) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        
        btn.addActionListener(e -> onNavigate.accept(card));
        return btn;
    }
}