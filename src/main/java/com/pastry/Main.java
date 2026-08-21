package com.pastry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.SwingUtilities;

public class Main {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/pastry_db";

    public static void main(String[] args) {
        String username = System.getenv().getOrDefault("MYSQL_USER", "root");
        String password = System.getenv().getOrDefault("MYSQL_PWD", "");

        try {
            Connection connection = DriverManager.getConnection(DATABASE_URL, username, password);
            showTables(connection);
            SwingUtilities.invokeLater(() -> new LoginFrame(connection, args).setVisible(true));
        } catch (SQLException exception) {
            System.err.println("Could not connect to pastry_db: " + exception.getMessage());
        }
    }

    private static void showTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement();
                ResultSet tables = statement.executeQuery(
                        "SELECT TABLE_NAME FROM information_schema.tables "
                                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE' "
                                + "ORDER BY TABLE_NAME")) {
            System.out.println("Tables in pastry_db:");
            while (tables.next()) {
                System.out.println("- " + tables.getString("TABLE_NAME"));
            }
        }
    }
}
