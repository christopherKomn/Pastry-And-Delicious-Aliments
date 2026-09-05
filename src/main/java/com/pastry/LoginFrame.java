package com.pastry;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.admin.AdminMain;
import com.customer.CustomerMain;
import com.models.UserModel;
import com.repository.DBUserRepository;
import com.repository.IUserRepository;
import com.store_manager.StoreManagerMain;

public class LoginFrame extends JFrame {
    private final Connection dbConnection;
    private final String[] applicationArgs;
    private final IUserRepository userRepository;
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginFrame(Connection dbConnection, String[] applicationArgs) {
        super("Pastry login");
        this.dbConnection = dbConnection;
        this.applicationArgs = applicationArgs;
        this.userRepository = new DBUserRepository(dbConnection);
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

        try {
            UserModel user = userRepository.findByUsernameAndPassword(username, password);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
                return;
            }

            dispatch(user);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(this, "Login failed: " + exception.getMessage());
        }
    }

    private void dispatch(UserModel user) {
        setVisible(false);
        try {
            switch (user.getUser_type()) {
                case "admin" -> AdminMain.AMain(applicationArgs, dbConnection, user);
                case "restaurant_owner" -> StoreManagerMain.SMMain(applicationArgs, dbConnection, user);
                case "customer" -> CustomerMain.CMain(applicationArgs, dbConnection, user);
                default -> JOptionPane.showMessageDialog(
                        null,
                        "Unknown user role: " + user.getUser_type());
            }
        } finally {
            
            dispose();
        }
    }

}
