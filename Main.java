package com.warehouse;


import com.warehouse.views.MainApplicationPanel;

import javax.swing.*;
import java.awt.*;


public class Main {
       public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Warehouse Management System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);

            // main view is a JPanel from ./views
            frame.setLayout(new BorderLayout());
            frame.add(new MainApplicationPanel(), BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}