package com.admin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AdminView extends JFrame {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final JMenuItem createRestaurantItem = new JMenuItem("Restaurant");
    private final JMenuItem showRestaurantsItem = new JMenuItem("Restaurants");
    private final JMenuItem showCustomersItem = new JMenuItem("Customers");
    private final JMenuItem showProfileItem = new JMenuItem("Show");
    private final JMenuItem logoutItem = new JMenuItem("Log out");
    private final JPanel contentPanel = new JPanel(new BorderLayout());

    public AdminView() {
        setTitle("Pastry Admin");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 550));
        setSize(new Dimension(1100, 700));
        setJMenuBar(createMenuBar());

        contentPanel.setBackground(BACKGROUND);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(34, 38, 38, 38));
        setContentPane(contentPanel);

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
        showPanel(heading);

        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu createMenu = new JMenu("Create");
        createMenu.add(createRestaurantItem);

        JMenu showMenu = new JMenu("Show");
        showMenu.add(showRestaurantsItem);
        showMenu.add(showCustomersItem);

        JMenu profileMenu = new JMenu("Profile");
        profileMenu.add(showProfileItem);
        profileMenu.addSeparator();
        profileMenu.add(logoutItem);

        menuBar.add(createMenu);
        menuBar.add(showMenu);
        menuBar.add(profileMenu);
        return menuBar;
    }

    public void showPanel(JPanel panel) {
        if (panel == null) {
            throw new IllegalArgumentException("Panel cannot be null.");
        }

        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void addCreateRestaurantListener(ActionListener listener) {
        createRestaurantItem.addActionListener(listener);
    }

    public void addShowRestaurantsListener(ActionListener listener) {
        showRestaurantsItem.addActionListener(listener);
    }

    public void addShowCustomersListener(ActionListener listener){
        showCustomersItem.addActionListener(listener);
    }

    public void addShowProfileListener(ActionListener listener) {
        showProfileItem.addActionListener(listener);
    }

    public void addLogoutListener(ActionListener listener) {
        logoutItem.addActionListener(listener);
    }
}
