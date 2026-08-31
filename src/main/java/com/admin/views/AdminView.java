package com.admin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AdminView extends JFrame {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color ACCENT = new Color(196, 92, 62);
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final JButton createRestaurantButton = new JButton("Create Restaurant");
    private final JButton showRestaurantsButton = new JButton("Show Restaurants");
    private final JButton showCustomersButton = new JButton("Show Customers");

    public AdminView() {
        setTitle("Pastry Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(650, 390));

        JPanel root = new JPanel(new BorderLayout(0, 24));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(34, 38, 38, 38));
        setContentPane(root);

        JPanel heading = new JPanel(new BorderLayout(0, 6));
        heading.setOpaque(false);

        JLabel title = new JLabel("Admin dashboard", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Choose an administration option", SwingConstants.CENTER);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        root.add(heading, BorderLayout.NORTH);

        JPanel menuCard = new JPanel(new GridLayout(3, 1, 0, 14));
        menuCard.setBackground(CARD_BACKGROUND);
        menuCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 235)),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        styleActiveButton(createRestaurantButton);
        styleActiveButton(showRestaurantsButton);
        styleActiveButton(showCustomersButton);

        showRestaurantsButton.setEnabled(true);
        showCustomersButton.setEnabled(true);

        menuCard.add(createRestaurantButton);
        menuCard.add(showRestaurantsButton);
        menuCard.add(showCustomersButton);
        root.add(menuCard, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void styleActiveButton(JButton button) {
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(13, 18, 13, 18));
    }

    private void styleDisabledButton(JButton button) {
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        button.setForeground(new Color(145, 150, 158));
        button.setBackground(new Color(235, 237, 241));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(13, 18, 13, 18));
    }

    public void addCreateRestaurantListener(ActionListener listener) {
        createRestaurantButton.addActionListener(listener);
    }

    public void addShowRestaurantsListener(ActionListener listener) {
        showRestaurantsButton.addActionListener(listener);
    }

    public void addShowCustomersListener(ActionListener listener){
        showCustomersButton.addActionListener(listener);
    }
}
