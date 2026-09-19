package com.customer.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class CustomerPaymentMethodView extends JFrame {
    private static final Color BACKGROUND = new Color(250, 247, 244);
    private static final Color TEXT = new Color(48, 40, 36);
    private static final Color ACCENT = new Color(190, 80, 50);
    private static final Color BORDER = new Color(232, 224, 218);
    private final JButton cashButton = new JButton("Cash");
    private final JButton cardButton = new JButton("Card");

    public CustomerPaymentMethodView(String selectedPaymentMethod) {
        setTitle("Payment Method");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(420, 260);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(24, 26, 24, 26));
        setContentPane(root);

        JLabel title = new JLabel("Choose Payment Method");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        title.setForeground(TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel options = new JPanel(new GridLayout(1, 2, 12, 0));
        options.setBackground(Color.WHITE);
        options.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        styleOption(cashButton, "Cash".equals(selectedPaymentMethod));
        styleOption(cardButton, "Card".equals(selectedPaymentMethod));
        options.add(cashButton);
        options.add(cardButton);
        root.add(options, BorderLayout.CENTER);
    }

    public void addPaymentMethodListener(ActionListener listener) {
        if (listener != null) {
            cashButton.addActionListener(listener);
            cardButton.addActionListener(listener);
        }
    }

    private void styleOption(JButton button, boolean selected) {
        button.setActionCommand(button.getText());
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        button.setForeground(selected ? Color.WHITE : ACCENT);
        button.setBackground(selected ? ACCENT : Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
        button.setPreferredSize(new Dimension(140, 70));
    }
}