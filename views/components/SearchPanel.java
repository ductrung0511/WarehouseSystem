package com.warehouse.views.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SearchPanel extends JPanel {
    private final JTextField searchField;
    private final JButton searchButton;
    private final JComboBox<String> filterComboBox;
    
    public SearchPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Search field
        searchField = new JTextField(20);
        searchField.setToolTipText("Enter search term...");
        
        // Filter combo box
        filterComboBox = new JComboBox<>(new String[]{
            "All", "Name", "Category", "ID", "Supplier"
        });
        
        // Search button
        searchButton = new JButton("Search");
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        add(new JLabel("Search:"));
        add(searchField);
        add(new JLabel("Filter by:"));
        add(filterComboBox);
        add(searchButton);
    }
    
    public String getSearchText() {
        return searchField.getText().trim();
    }
    
    public String getFilterType() {
        return (String) filterComboBox.getSelectedItem();
    }
    
    public void setSearchListener(ActionListener listener) {
        searchButton.addActionListener(listener);
        searchField.addActionListener(listener); // Enter key support
    }
    
    public void clearSearch() {
        searchField.setText("");
    }
    
    public void setPlaceholder(String placeholder) {
        searchField.setText(placeholder);
    }
}