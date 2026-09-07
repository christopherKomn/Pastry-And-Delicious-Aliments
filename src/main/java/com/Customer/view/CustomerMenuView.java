package com.customer.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;

import com.models.MenuItemsModel;

public class CustomerMenuView extends JFrame {
    private static final Color BACKGROUND = new Color(250, 247, 244);
    private static final Color TEXT = new Color(48, 40, 36);
    private static final Color MUTED = new Color(116, 103, 96);
    private static final Color ACCENT = new Color(190, 80, 50);
    private static final Color BORDER = new Color(232, 224, 218);

    private final int restaurantId;
    private final JPanel productsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
    private final JPanel cartPanel = new JPanel();
    private final JLabel cartTotalLabel = new JLabel("Total: 0.00 EUR");
    private final List<ActionListener> addToCartListeners = new ArrayList<>();
    private final Map<Integer, Integer> cartQuantities = new LinkedHashMap<>();
    private final Map<Integer, MenuItemsModel> productsById = new LinkedHashMap<>();

    public CustomerMenuView(int restaurantId, String restaurantName) {
        this.restaurantId = restaurantId;
        setTitle("Menu | " + restaurantName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));
        setContentPane(root);

        JLabel title = new JLabel(restaurantName + " - Available products");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        title.setForeground(TEXT);
        title.setBorder(new EmptyBorder(0, 0, 8, 0));
        root.add(title, BorderLayout.NORTH);

        productsPanel.setBackground(BACKGROUND);
        productsPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JScrollPane productsScrollPane = new JScrollPane(productsPanel);
        productsScrollPane.setBorder(null);
        productsScrollPane.getViewport().setBackground(BACKGROUND);
        root.add(productsScrollPane, BorderLayout.CENTER);

        JPanel cart = new JPanel(new BorderLayout(8, 8));
        cart.setBackground(Color.WHITE);
        cart.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(14, 14, 14, 14)));
        cart.setPreferredSize(new Dimension(250, 0));
        JLabel cartTitle = new JLabel("Your cart");
        cartTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        cartTitle.setForeground(TEXT);
        cart.add(cartTitle, BorderLayout.NORTH);
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(Color.WHITE);
        JScrollPane cartScrollPane = new JScrollPane(cartPanel);
        cartScrollPane.setBorder(null);
        cartScrollPane.getViewport().setBackground(Color.WHITE);
        cart.add(cartScrollPane, BorderLayout.CENTER);
        cartTotalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        cartTotalLabel.setForeground(ACCENT);
        cartTotalLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        cart.add(cartTotalLabel, BorderLayout.SOUTH);
        root.add(cart, BorderLayout.EAST);
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void addToCartListener(ActionListener listener) {
        if (listener != null) {
            addToCartListeners.add(listener);
        }
    }

    public void setProducts(List<MenuItemsModel> products) {
        productsPanel.removeAll();
        productsById.clear();
        for (MenuItemsModel product : products) {
            if (Boolean.TRUE.equals(product.getIs_available()) && product.getItem_quantity() > 0) {
                productsById.put(product.getItem_id(), product);
                productsPanel.add(createProductRow(product));
            }
        }
        if (productsById.isEmpty()) {
            productsPanel.add(new JLabel("No products available.", SwingConstants.CENTER));
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    public void setCart(Map<Integer, Integer> quantities) {
        cartQuantities.clear();
        cartQuantities.putAll(quantities);
        refreshCart();
    }

    private JPanel createProductRow(MenuItemsModel product) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 14, 12, 14)));
        JLabel details = new JLabel(product.getItem_name() + "  -  " + format(product.getItem_price()) + " EUR");
        details.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        details.setForeground(TEXT);
        JButton addButton = new JButton("Add to cart");
        styleActionButton(addButton);
        addButton.addActionListener(event -> chooseQuantity(product));
        row.add(details, BorderLayout.CENTER);
        row.add(addButton, BorderLayout.EAST);
        return row;
    }

    private void chooseQuantity(MenuItemsModel product) {
        JSpinner quantitySpinner = new JSpinner(
                new SpinnerNumberModel(1, 1, product.getItem_quantity(), 1));
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        quantityPanel.add(new JLabel("Quantity for " + product.getItem_name() + ":"));
        quantityPanel.add(quantitySpinner);

        int result = JOptionPane.showConfirmDialog(
                this,
                quantityPanel,
                "Add product",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            notifyAddToCartListeners(product, (Integer) quantitySpinner.getValue());
        }
    }

    private void notifyAddToCartListeners(MenuItemsModel product, int quantity) {
        ActionEvent event = new ActionEvent(
                this,
                ActionEvent.ACTION_PERFORMED,
                product.getItem_id() + ":" + quantity);
        for (ActionListener listener : addToCartListeners) {
            listener.actionPerformed(event);
        }
    }

    private void refreshCart() {
        cartPanel.removeAll();
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : cartQuantities.entrySet()) {
            MenuItemsModel product = productsById.get(entry.getKey());
            if (product != null) {
                BigDecimal lineTotal = product.getItem_price().multiply(BigDecimal.valueOf(entry.getValue()));
                total = total.add(lineTotal);
                JLabel cartItem = new JLabel(entry.getValue() + " x " + product.getItem_name()
                    + " - " + format(lineTotal) + " EUR");
                cartItem.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                cartItem.setForeground(MUTED);
                cartItem.setBorder(new EmptyBorder(6, 2, 6, 2));
                cartItem.setAlignmentX(LEFT_ALIGNMENT);
                cartPanel.add(cartItem);
            }
        }
        cartTotalLabel.setText("Total: " + format(total) + " EUR");
        cartPanel.revalidate();
        cartPanel.repaint();
    }

    private static void styleActionButton(JButton button) {
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        button.setOpaque(true);
    }

    private static String format(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }
}
