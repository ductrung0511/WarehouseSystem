package com.warehouse.views.dashboard;

import com.warehouse.services.InventoryService;
import com.warehouse.services.OrderService;
import com.warehouse.services.WarehouseService;

import javax.swing.*;
import java.awt.*;

public class MainDashboardView extends JPanel {

    private final WarehouseService warehouseService;
    private final InventoryService inventoryService;
    private final OrderService orderService;

    private final JLabel productCount = new JLabel("Products: -");
    private final JLabel orderCount   = new JLabel("Orders: -");
    private final JLabel warehouseCount = new JLabel("Warehouses: -");

    public MainDashboardView(WarehouseService warehouseService,
                             InventoryService inventoryService,
                             OrderService orderService) {
        this.warehouseService = warehouseService;
        this.inventoryService = inventoryService;
        this.orderService = orderService;

        initUI();
        loadStats();
    }

    private void initUI() {
        setLayout(new GridLayout(1, 3, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        add(statCard("Inventory", productCount));
        add(statCard("Orders", orderCount));
        add(statCard("Warehouses", warehouseCount));
    }

    private JPanel statCard(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    private void loadStats() {
        try {
            productCount.setText("Products: " + warehouseService.getAllProducts().size()); // function of warehouse ... 
        } catch (Exception e) {
            productCount.setText("Products: n/a");
        }

        try {
            orderCount.setText("Orders: " + orderService.getAllOrders().size());
        } catch (Exception e) {
            orderCount.setText("Orders: n/a");
        }

        try {
            warehouseCount.setText("Warehouses: " + warehouseService.getAllWarehouses().size());
        } catch (Exception e) {
            warehouseCount.setText("Warehouses: n/a");
        }
    }
}