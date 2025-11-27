package com.warehouse.views;

import com.warehouse.views.components.HeaderBar;
import com.warehouse.views.components.NavigationPanel;
import com.warehouse.views.orders.OrderFormView;
import com.warehouse.views.products.ProductListView;

import javax.swing.*;
import java.awt.*;

public class MainApplicationPanel extends JPanel {

    public static final String CARD_DASHBOARD = "dashboard";
    public static final String CARD_PRODUCTS  = "products";
    public static final String CARD_ORDERS    = "orders";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public MainApplicationPanel() {
        setLayout(new BorderLayout());

        // top header
        add(new HeaderBar("Warehouse Management System"), BorderLayout.NORTH);

        // left navigation
        NavigationPanel nav = new NavigationPanel(target -> {
            cardLayout.show(contentPanel, target);
        });
        add(nav, BorderLayout.WEST);

        // center: card layout with views
        contentPanel.add(new JLabel("Dashboard coming soon", SwingConstants.CENTER), CARD_DASHBOARD);
        contentPanel.add(new ProductListView(), CARD_PRODUCTS);
        contentPanel.add(new OrderFormView(), CARD_ORDERS);

        add(contentPanel, BorderLayout.CENTER);

        // default view
        cardLayout.show(contentPanel, CARD_DASHBOARD);
    }
}