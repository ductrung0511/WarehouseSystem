package com.warehouse.views.suppliers;

import com.warehouse.controllers.SupplierController;
import com.warehouse.models.Supplier;
import com.warehouse.views.components.DataTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SupplierListView extends JPanel {
    private final SupplierController supplierController;
    private DataTable supplierTable;
    private List<Supplier> suppliers;

    public SupplierListView() {
        this.supplierController = new SupplierController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        initializeComponents();
        loadSuppliers();
    }

    private void initializeComponents() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Supplier table
        String[] columns = {"ID", "Name", "Contact Info", "Address"};
        supplierTable = new DataTable(columns);
        supplierTable.setColumnWidth(0, 80);
        supplierTable.setColumnWidth(1, 150);
        supplierTable.setColumnWidth(2, 200);
        supplierTable.setColumnWidth(3, 250);
        
        // Add right-click context menu
        setupContextMenu();
        
        mainPanel.add(new JScrollPane(supplierTable.getTable()), BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupContextMenu() {
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem editMenuItem = new JMenuItem("Edit Supplier");
        JMenuItem deleteMenuItem = new JMenuItem("Delete Supplier");
        
        editMenuItem.addActionListener(e -> {
            int selectedRow = supplierTable.getTable().getSelectedRow();
            if (selectedRow >= 0 && selectedRow < suppliers.size()) {
                editSupplier(suppliers.get(selectedRow));
            }
        });
        
        deleteMenuItem.addActionListener(e -> {
            int selectedRow = supplierTable.getTable().getSelectedRow();
            if (selectedRow >= 0 && selectedRow < suppliers.size()) {
                deleteSupplier(suppliers.get(selectedRow));
            }
        });
        
        contextMenu.add(editMenuItem);
        contextMenu.add(deleteMenuItem);
        
        // Add mouse listener to show context menu
        supplierTable.getTable().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = supplierTable.getTable().rowAtPoint(e.getPoint());
                if (row >= 0 && row < supplierTable.getTable().getRowCount()) {
                    supplierTable.getTable().setRowSelectionInterval(row, row);
                }
                
                if (e.isPopupTrigger()) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click to edit
                    int row = supplierTable.getTable().rowAtPoint(e.getPoint());
                    if (row >= 0 && row < suppliers.size()) {
                        editSupplier(suppliers.get(row));
                    }
                }
            }
        });
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton refreshButton = new JButton("Refresh");
        JButton addSupplierButton = new JButton("Add New Supplier");
        
        refreshButton.addActionListener(e -> loadSuppliers());
        addSupplierButton.addActionListener(e -> showSupplierForm(null));
        
        controlPanel.add(refreshButton);
        controlPanel.add(addSupplierButton);
        
        // Add instruction label
        JLabel instructionLabel = new JLabel("Double click to edit - Or Right click to select Edit or Delete Supplier");
        instructionLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        instructionLabel.setForeground(Color.GRAY);
        controlPanel.add(instructionLabel);

        return controlPanel;
    }

    private void loadSuppliers() {
        try {
            suppliers = supplierController.getAllSuppliers();
            updateSupplierTable();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading suppliers: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSupplierTable() {
        supplierTable.clearData();
        
        for (Supplier supplier : suppliers) {
            supplierTable.addRow(new Object[]{
                supplier.getPersonId(),
                supplier.getName(),
                supplier.getContactInfo(),
                supplier.getAddress()
            });
        }
    }

    private void showSupplierForm(Supplier supplier) {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        SupplierFormView form = new SupplierFormView(
            parentWindow, 
            supplierController, 
            supplier, 
            this::loadSuppliers
        );
        form.setVisible(true);
    }

    private void editSupplier(Supplier supplier) {
        showSupplierForm(supplier);
    }

    private void deleteSupplier(Supplier supplier) {
        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete supplier: " + supplier.getName() + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                boolean success = supplierController.deleteSupplier(supplier.getPersonId());
                if (success) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Supplier deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    loadSuppliers();
                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Failed to delete supplier.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                    this,
                    "Error deleting supplier: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    public void refreshView() {
        loadSuppliers();
    }
}