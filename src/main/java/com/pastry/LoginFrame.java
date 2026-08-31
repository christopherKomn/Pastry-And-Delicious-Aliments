package com.pastry;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.admin.AdminMain;
import com.customer.CustomerMain;
import com.store_manager.StoreManagerMain;

public class LoginFrame extends JFrame {
    private final Connection dbConnection;
    private final String[] applicationArgs;
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginFrame(Connection dbConnection, String[] applicationArgs) {
        super("Pastry login");
        this.dbConnection = dbConnection;
        this.applicationArgs = applicationArgs;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(320, 160);
        setLocationRelativeTo(null);

        JPanel fields = new JPanel(new GridLayout(2, 2, 8, 8));
        fields.add(new JLabel("Username:"));
        fields.add(usernameField);
        fields.add(new JLabel("Password:"));
        fields.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(event -> login());
        getRootPane().setDefaultButton(loginButton);

        add(fields, BorderLayout.CENTER);
        add(loginButton, BorderLayout.SOUTH);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                //lol

            }
        });
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a username and password.");
            return;
        }

        String query = "SELECT user_type FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement statement = dbConnection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    JOptionPane.showMessageDialog(this, "Invalid username or password.");
                    return;
                }
                dispatch(result.getString("user_type"));
            }
        } catch (SQLException exception) {
            JOptionPane.showMessageDialog(this, "Login failed: " + exception.getMessage());
        }
    }

    private void dispatch(String userType) {
        setVisible(false);
        try {
            switch (userType) {
                case "admin" -> AdminMain.AMain(applicationArgs, dbConnection);
                case "restaurant_owner" -> StoreManagerMain.SMMain(applicationArgs, dbConnection);
                case "customer" -> CustomerMain.CMain(applicationArgs, dbConnection);
                default -> JOptionPane.showMessageDialog(null, "Unknown user role: " + userType);
            }
        } finally {
            
            dispose();
        }
    }

}