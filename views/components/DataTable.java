package com.warehouse.views.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class DataTable extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;
    
    public DataTable(String[] columnNames) {
        setLayout(new BorderLayout());
        
        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Auto-detect column types for proper sorting
                for (int i = 0; i < getRowCount(); i++) {
                    Object value = getValueAt(i, columnIndex);
                    if (value != null) {
                        return value.getClass();
                    }
                }
                return Object.class;
            }
        };
        
        // Create table
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Status label
        statusLabel = new JLabel("No data available");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        
        updateStatus();
    }
    
    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
        updateStatus();
    }
    
    public void clearData() {
        tableModel.setRowCount(0);
        updateStatus();
    }
    
    public int getSelectedRow() {
        return table.getSelectedRow();
    }
    
    public int getSelectedModelRow() {
        int viewRow = table.getSelectedRow();
        return viewRow >= 0 ? table.convertRowIndexToModel(viewRow) : -1;
    }
    
    public Object getValueAt(int row, int column) {
        return tableModel.getValueAt(row, column);
    }
    
    public void setColumnWidth(int column, int width) {
        table.getColumnModel().getColumn(column).setPreferredWidth(width);
    }
    
    public JTable getTable() {
        return table;
    }
    
    private void updateStatus() {
        int rowCount = tableModel.getRowCount();
        statusLabel.setText(rowCount + " item(s) found");
    }
    
    public void setRowFilter(RowFilter<Object, Object> filter) {
        TableRowSorter<DefaultTableModel> sorter = 
            new TableRowSorter<>((DefaultTableModel) table.getModel());
        sorter.setRowFilter(filter);
        table.setRowSorter(sorter);
    }
}