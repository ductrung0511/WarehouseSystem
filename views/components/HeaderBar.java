package com.warehouse.views.components;

import javax.swing.*;
import java.awt.*;

public class HeaderBar extends JPanel {

    public HeaderBar(String title) {
        setBackground(new Color(50, 100, 200));
        setPreferredSize(new Dimension(0, 70));
        setLayout(new BorderLayout());

        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.BOLD, 28));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        add(label, BorderLayout.WEST);
    }
}