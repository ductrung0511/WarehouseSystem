package com.warehouse.views.components;

import com.warehouse.views.MainApplicationPanel;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class NavigationPanel extends JPanel {

    public NavigationPanel(Consumer<String> onNavigate) {
        setLayout(new GridLayout(0, 1, 0, 10));
        setPreferredSize(new Dimension(220, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        add(navButton("Dashboard", MainApplicationPanel.CARD_DASHBOARD, onNavigate));
        add(navButton("Products",  MainApplicationPanel.CARD_PRODUCTS, onNavigate));
        add(navButton("Orders (POS)", MainApplicationPanel.CARD_ORDERS, onNavigate));
        add(Box.createVerticalGlue());
    }

    private JButton navButton(String text, String card, Consumer<String> onNavigate) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.addActionListener(e -> onNavigate.accept(card));
        return btn;
    }
}