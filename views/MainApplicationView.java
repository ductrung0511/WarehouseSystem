package com.warehouse.views;

import com.warehouse.views.components.HeaderBar;
import com.warehouse.views.components.NavigationPanel;
import com.warehouse.views.dashboard.MainDashboardView;
import com.warehouse.views.products.ProductListView;
import com.warehouse.views.products.ProductSearchView;
import com.warehouse.controllers.ProductController;

import com.warehouse.views.warehouse.WarehouseListView;
import com.warehouse.views.warehouse.WarehouseReportView;

import com.warehouse.views.orders.OrderListView;
import com.warehouse.views.orders.OrderStatusView; //
import com.warehouse.views.orders.OrderCancelView;

import com.warehouse.views.suppliers.SupplierListView;
import com.warehouse.views.suppliers.PurchaseOrderView;
import com.warehouse.views.suppliers.DeliveryTrackingView;


import javax.swing.*;
import java.awt.*;

public class MainApplicationView extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    
    // Card constants
    public static final String CARD_DASHBOARD = "DASHBOARD";
    public static final String CARD_PRODUCTS = "PRODUCTS";
    public static final String CARD_SEARCH = "SEARCH";
    
    // Warehouse cards
    public static final String CARD_WAREHOUSE = "WAREHOUSE";
    public static final String CARD_WAREHOUSE_REPORT = "WAREHOUSE_REPORT";
    
    public static final String CARD_ORDERS = "ORDERS";
    public static final String CARD_ORDER_STATUS = "ORDER_STATUS";
    public static final String CARD_ORDER_CANCEL = "ORDER_CANCEL";
    
    public static final String CARD_SUPPLIERS = "SUPPLIERS";
    public static final String CARD_PURCHASE_ORDERS = "PURCHASE_ORDERS";
    public static final String CARD_DELIVERY_TRACKING = "DELIVERY_TRACKING";
    
    

    // Views
    private final MainDashboardView dashboardView;
    private final ProductListView productListView;
    private final ProductSearchView productSearchView;
    
    private final WarehouseListView warehouseListView;
    private final WarehouseReportView warehouseReportView;
    
    private final OrderListView orderListView;
    private final OrderStatusView orderStatusView;
    private final OrderCancelView orderCancelView;
    
    private final SupplierListView supplierListView;
    private final PurchaseOrderView purchaseOrderView;
    private final DeliveryTrackingView deliveryTrackingView;

    
    public MainApplicationView() {
        setTitle("Warehouse Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1200, 800));
        
        // Initialize controllers
        ProductController productController = new ProductController();
        
        // Initialize views
        dashboardView = new MainDashboardView();
        productListView = new ProductListView(productController);
        productSearchView = new ProductSearchView(productController);
        
        warehouseListView = new WarehouseListView();
        warehouseReportView = new WarehouseReportView();
        
        orderListView = new OrderListView();
        orderStatusView = new OrderStatusView();
        orderCancelView = new OrderCancelView();
        
        supplierListView = new SupplierListView();
        purchaseOrderView = new PurchaseOrderView();
        deliveryTrackingView = new DeliveryTrackingView();

        initializeUI();
        pack();
        setLocationRelativeTo(null); // Center the window
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create header
        HeaderBar headerBar = new HeaderBar("Warehouse Management System");
        add(headerBar, BorderLayout.NORTH);
        
        // Create main container with navigation and content
        JPanel container = new JPanel(new BorderLayout());
        
        // Create navigation panel
        NavigationPanel navigationPanel = new NavigationPanel(this::navigateTo);
        container.add(navigationPanel, BorderLayout.WEST);
        
        // Create main content area with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Add views to card layout
        mainPanel.add(dashboardView, CARD_DASHBOARD);
        mainPanel.add(productListView, CARD_PRODUCTS);
        mainPanel.add(productSearchView, CARD_SEARCH);
        
        // Warehouse views
        mainPanel.add(warehouseListView, CARD_WAREHOUSE);
        mainPanel.add(warehouseReportView, CARD_WAREHOUSE_REPORT);
        
        // Order views
        mainPanel.add(orderListView, CARD_ORDERS);
        mainPanel.add(orderStatusView, CARD_ORDER_STATUS);
        mainPanel.add(orderCancelView, CARD_ORDER_CANCEL);
        
        mainPanel.add(supplierListView, CARD_SUPPLIERS);
        mainPanel.add(purchaseOrderView, CARD_PURCHASE_ORDERS);
        mainPanel.add(deliveryTrackingView, CARD_DELIVERY_TRACKING);

        
        container.add(mainPanel, BorderLayout.CENTER);
        
        add(container, BorderLayout.CENTER);
        
        // Show dashboard by default
        cardLayout.show(mainPanel, CARD_DASHBOARD);
    }

    private void navigateTo(String cardName) {
        cardLayout.show(mainPanel, cardName);
        
        // Refresh dashboard when navigating to it
        if (CARD_DASHBOARD.equals(cardName)) {
            dashboardView.refresh();
        }
        
    }

    // Public method to allow external navigation
    public void showView(String cardName) {
        navigateTo(cardName);
    }
    
    // Public method to refresh dashboard
    public void refreshDashboard() {
        dashboardView.refresh();
    }
    
    // Getter methods for views (useful for testing or external access)
    public MainDashboardView getDashboardView() {
        return dashboardView;
    }
    
    public ProductListView getProductListView() {
        return productListView;
    }
    
    public ProductSearchView getProductSearchView() {
        return productSearchView;
    }
    
    public WarehouseListView getWarehouseListView() {
        return warehouseListView;
    }

    
    public WarehouseReportView getWarehouseReportView() {
        return warehouseReportView;
    }
    
    public OrderListView getOrderListView() {
        return orderListView;
    }
    
    public OrderStatusView getOrderStatusView() {
        return orderStatusView;
    }
    
    public OrderCancelView getOrderCancelView() {
        return orderCancelView;
    }
}