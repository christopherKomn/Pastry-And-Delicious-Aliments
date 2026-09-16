package com.customer.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
    private static final Color BORDER = new Color(232, 224, 218);

    public CustomerCheckoutView(List<CartItem> cartItems, BigDecimal total) {
        setTitle("Checkout");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 620);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(24, 0));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(28, 32, 28, 32));
        setContentPane(root);

        JPanel futurePanel = new JPanel();
        futurePanel.setOpaque(false);
        root.add(futurePanel, BorderLayout.CENTER);
        root.add(createOrderSummary(cartItems, total), BorderLayout.EAST);
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