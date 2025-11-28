package com.warehouse;

import com.warehouse.views.MainApplicationView;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
//        // Set system look and feel for better appearance
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
//        } catch (Exception e) {
//            System.err.println("Error setting look and feel: " + e.getMessage());
//        }

        // Run the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🚀 Starting Warehouse Management System...");
                
                // Create and display the main application window
                MainApplicationView mainView = new MainApplicationView();
                mainView.setVisible(true);
                
                System.out.println("✅ Application started successfully!");
                System.out.println("📊 Dashboard loaded");
                System.out.println("🛠️  Navigation ready");
                System.out.println("💾 All components initialized");
                
            } catch (Exception e) {
                System.err.println("❌ Failed to start application: " + e.getMessage());
                e.printStackTrace();
                
                // Show error dialog to user
                JOptionPane.showMessageDialog(
                    null,
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}