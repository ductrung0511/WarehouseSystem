package com.warehouse.views.components;

import javax.swing.*;
import java.awt.*;

public class HeaderBar extends JPanel {
    
    public HeaderBar(String appTitle) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        setBackground(new Color(70, 130, 180)); // Steel blue background
        
        // App title
        JLabel titleLabel = new JLabel(appTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // Timestamp or status
        JLabel statusLabel = new JLabel("Warehouse Management System");
        statusLabel.setForeground(Color.WHITE);
        
        add(titleLabel, BorderLayout.WEST);
        add(statusLabel, BorderLayout.EAST);
    }
}