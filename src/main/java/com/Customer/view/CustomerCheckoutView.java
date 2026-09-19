package com.customer.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.models.CartItem;

public class CustomerCheckoutView extends JFrame {
    private static final Color BACKGROUND = new Color(250, 247, 244);
    private static final Color TEXT = new Color(48, 40, 36);
    private static final Color MUTED = new Color(116, 103, 96);
    private static final Color ACCENT = new Color(190, 80, 50);
    private static final Color ACCENT_DARK = new Color(156, 61, 39);
    private static final Color BORDER = new Color(232, 224, 218);
    private static final Dimension ACTION_BUTTON_SIZE = new Dimension(510, 160);
    private final JButton deliveryDetailsButton = new JButton();
    private final JButton paymentMethodButton = new JButton();
    private final JButton placeOrderButton = new JButton();

    public CustomerCheckoutView(List<CartItem> cartItems, BigDecimal total) {
        setTitle("Checkout");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(24, 0));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(28, 32, 28, 32));
        setContentPane(root);

        root.add(createDeliveryPanel(), BorderLayout.CENTER);
        root.add(createOrderSummary(cartItems, total), BorderLayout.EAST);
    }

    public void addDeliveryDetailsListener(ActionListener listener) {
        if (listener != null) {
            deliveryDetailsButton.addActionListener(listener);
        }
    }

    public void addPaymentMethodListener(ActionListener listener) {
        if (listener != null) {
            paymentMethodButton.addActionListener(listener);
        }
    }

    public void addPlaceOrderListener(ActionListener listener) {
        if (listener != null) {
            placeOrderButton.addActionListener(listener);
        }
    }

    private JPanel createDeliveryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        upperPanel.setOpaque(false);

        styleLargeButton(
                deliveryDetailsButton,
                "Delivery Details",
                "Add your delivery address and contact information");
        styleLargeButton(
                paymentMethodButton,
                "Payment Method",
                "Choose Cash or Card");
        styleLargeButton(placeOrderButton, "Place Order", "Send your order");
        placeOrderButton.setText("Place Order");
        Dimension placeOrderSize = new Dimension(510, 60);
        placeOrderButton.setPreferredSize(placeOrderSize);
        placeOrderButton.setMaximumSize(placeOrderSize);
        placeOrderButton.setMinimumSize(placeOrderSize);
        placeOrderButton.setBackground(new Color(44, 158, 70));
        placeOrderButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(28, 112, 47), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        placeOrderButton.setVisible(false);
        deliveryDetailsButton.setToolTipText("Enter delivery details");
        paymentMethodButton.setToolTipText("Choose a payment method");
        placeOrderButton.setToolTipText("Place order");
        upperPanel.add(deliveryDetailsButton);
        upperPanel.add(javax.swing.Box.createVerticalStrut(14));
        upperPanel.add(paymentMethodButton);
        panel.add(upperPanel, BorderLayout.NORTH);
        JPanel placeOrderPanel = new JPanel(new java.awt.FlowLayout(
            java.awt.FlowLayout.LEFT, 0, 0));
        placeOrderPanel.setOpaque(false);
        placeOrderPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        placeOrderPanel.add(placeOrderButton, BorderLayout.SOUTH);
        panel.add(placeOrderPanel, BorderLayout.SOUTH);
        return panel;
    }

    public void setPlaceOrderVisible(boolean visible) {
        placeOrderButton.setVisible(visible);
        placeOrderButton.getParent().revalidate();
        placeOrderButton.getParent().repaint();
    }

    public void setPaymentMethodSummary(String paymentMethod) {
        String text = paymentMethod == null || paymentMethod.isBlank()
                ? "Choose Cash or Card"
                : "Selected: " + paymentMethod;
        styleLargeButton(paymentMethodButton, "Payment Method", text);
    }

    private void styleLargeButton(JButton button, String title, String subtitle) {
        button.setText(
                "<html><center><font size='6'><b>" + title + "</b></font><br>"
                        + "<font size='3'>" + subtitle + "</font>"
                        + "</center></html>");
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_DARK, 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(ACTION_BUTTON_SIZE);
        button.setMaximumSize(ACTION_BUTTON_SIZE);
        button.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JPanel createOrderSummary(List<CartItem> cartItems, BigDecimal total) {
        JPanel summary = new JPanel(new BorderLayout(0, 14));
        summary.setBackground(Color.WHITE);
        summary.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(18, 18, 18, 18)));
        summary.setPreferredSize(new Dimension(330, 0));

        JLabel title = new JLabel("Cart");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 21));
        title.setForeground(TEXT);
        summary.add(title, BorderLayout.NORTH);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);
        for (CartItem cartItem : cartItems) {
            BigDecimal lineTotal = cartItem.getProduct().getItem_price()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            JLabel item = new JLabel(
                    cartItem.getQuantity() + " x " + cartItem.getProduct().getItem_name()
                            + "    " + format(lineTotal) + " €");
            item.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            item.setForeground(MUTED);
            item.setBorder(new EmptyBorder(8, 0, 8, 0));
            itemsPanel.add(item);
        }
        JScrollPane itemsScrollPane = new JScrollPane(itemsPanel);
        itemsScrollPane.setBorder(null);
        itemsScrollPane.getViewport().setBackground(Color.WHITE);
        summary.add(itemsScrollPane, BorderLayout.CENTER);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        JLabel totalLabel = new JLabel("Total Cost: " + format(total) + " €");
        totalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        totalLabel.setForeground(ACCENT);
        totalLabel.setBorder(new EmptyBorder(14, 0, 0, 0));
        totalPanel.add(totalLabel, BorderLayout.NORTH);
        summary.add(totalPanel, BorderLayout.SOUTH);
        return summary;
    }

    private static String format(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, RoundingMode.HALF_UP).toString();
    }
}