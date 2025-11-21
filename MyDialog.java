package com.warehouse;

import javax.swing.*;

public class MyDialog extends JDialog {

    public MyDialog() {
        setTitle("Warehouse Login Dialog");
        setSize(350, 200);
        setLocationRelativeTo(null);   // center on screen
        setModal(true);                // block until closed
        setLayout(null);               // simple absolute layout

        JLabel lblTitle = new JLabel("Warehouse System");
        lblTitle.setBounds(110, 10, 150, 25);
        add(lblTitle);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(20, 50, 100, 25);
        add(lblUser);

        JTextField txtUser = new JTextField();
        txtUser.setBounds(120, 50, 180, 25);
        add(txtUser);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(20, 90, 100, 25);
        add(lblPass);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(120, 90, 180, 25);
        add(txtPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(120, 130, 80, 25);
        add(btnLogin);
    }
}
