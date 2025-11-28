package com.warehouse.views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConfirmationDialog extends JDialog {
    private boolean confirmed = false;
    
    public ConfirmationDialog(Window owner, String title, String message) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        initializeUI(message);
    }
    
    private void initializeUI(String message) {
        setLayout(new BorderLayout(10, 10));
        setSize(400, 200);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        // Message area
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(getBackground());
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        messageArea.setFont(UIManager.getFont("Label.font"));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton yesButton = new JButton("Yes");
        JButton noButton = new JButton("No");
        JButton cancelButton = new JButton("Cancel");
        
        yesButton.setBackground(new Color(220, 80, 80));
        yesButton.setForeground(Color.WHITE);
        yesButton.setFocusPainted(false);
        
        noButton.setBackground(new Color(70, 130, 180));
        noButton.setForeground(Color.WHITE);
        noButton.setFocusPainted(false);
        
        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        buttonPanel.add(cancelButton);
        
        add(messageArea, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Button actions
        yesButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        noButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        cancelButton.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        
        // Window close action
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmed = false;
            }
        });
        
        // Enter key for Yes, Escape for Cancel
        getRootPane().setDefaultButton(yesButton);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public static boolean showConfirmDialog(Window owner, String title, String message) {
        ConfirmationDialog dialog = new ConfirmationDialog(owner, title, message);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
}