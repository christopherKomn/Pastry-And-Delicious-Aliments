package com.store_manager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.models.OrderModel;

/** Displays an order and allows its status to be changed. */
public class OrderView extends JPanel {

    private static final String[] ORDER_STATUSES = {
        "pending",
        "confirmed",
        "preparing",
        "ready_for_pickup",
        "on_the_way",
        "delivered",
        "cancelled"
    };

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color TEXT_SECONDARY = new Color(100, 108, 118);

    private final JLabel subtotalValue = new JLabel();
    private final JLabel discountValue = new JLabel();
    private final JLabel totalValue = new JLabel();
    private final JLabel paymentMethodValue = new JLabel();
    private final JTextArea specialInstructionsValue = new JTextArea();
    private final JComboBox<String> statusComboBox = new JComboBox<>(ORDER_STATUSES);
    private final JButton orderItemsButton = new JButton("Order Items");

    private OrderModel order;
    private boolean settingOrder;

    public OrderView() {
        super(new BorderLayout());
        setBackground(BACKGROUND);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)));

        int row = 0;
        addReadOnlyField(content, row++, "Subtotal", subtotalValue);
        addReadOnlyField(content, row++, "Discount", discountValue);
        addReadOnlyField(content, row++, "Total amount", totalValue);
        addField(content, row++, "Status", statusComboBox);
        addReadOnlyField(content, row++, "Payment method", paymentMethodValue);

        specialInstructionsValue.setEditable(false);
        specialInstructionsValue.setFocusable(false);
        specialInstructionsValue.setLineWrap(true);
        specialInstructionsValue.setWrapStyleWord(true);
        specialInstructionsValue.setRows(4);
        specialInstructionsValue.setBackground(new Color(248, 249, 251));
        specialInstructionsValue.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        addField(content, row++, "Special instructions", new JScrollPane(specialInstructionsValue));

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 1;
        buttonConstraints.gridy = row;
        buttonConstraints.anchor = GridBagConstraints.LINE_END;
        buttonConstraints.insets = new Insets(18, 0, 0, 0);
        content.add(orderItemsButton, buttonConstraints);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        wrapper.add(content, BorderLayout.NORTH);
        add(new JScrollPane(wrapper), BorderLayout.CENTER);

        statusComboBox.addActionListener(event -> {
            if (!settingOrder && order != null) {
                order.setStatus((String) statusComboBox.getSelectedItem());
            }
            updateOrderItemsButton();
        });

        setTheOrder(null);
    }

    /** Replaces the order displayed by this panel. */
    public void setTheOrder(OrderModel order) {
        this.order = order;
        settingOrder = true;
        try {
            boolean hasOrder = order != null;
            subtotalValue.setText(hasOrder ? formatMoney(order.getSubtotal()) : "-");
            discountValue.setText(hasOrder ? formatMoney(order.getDiscount_amount()) : "-");
            totalValue.setText(hasOrder ? formatMoney(order.getTotal_amount()) : "-");
            paymentMethodValue.setText(hasOrder ? displayValue(order.getPayment_method()) : "-");
            specialInstructionsValue.setText(
                    hasOrder ? displayValue(order.getSpecial_instructions()) : "No order selected");
            statusComboBox.setSelectedItem(hasOrder ? order.getStatus() : null);
            statusComboBox.setEnabled(hasOrder);
        } finally {
            settingOrder = false;
        }
        updateOrderItemsButton();
    }

    /** Returns the displayed order, including any status selected in the view. */
    public OrderModel getTheOrder() {
        if (order != null) {
            order.setStatus((String) statusComboBox.getSelectedItem());
        }
        return order;
    }

    /** Adds code to run whenever the selected order status changes. */
    public void addStatusChangeListener(ActionListener listener) {
        statusComboBox.addActionListener(listener);
    }

    /** Adds code to run when the Order Items button is clicked. */
    public void addOrderItemsListener(ActionListener listener) {
        orderItemsButton.addActionListener(listener);
    }

    private void updateOrderItemsButton() {
        orderItemsButton.setEnabled(order != null
                && "pending".equals(statusComboBox.getSelectedItem()));
    }

    private static void addReadOnlyField(JPanel panel, int row, String label, JLabel value) {
        value.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        addField(panel, row, label, value);
    }

    private static void addField(JPanel panel, int row, String labelText,
            java.awt.Component component) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
        labelConstraints.insets = new Insets(8, 0, 8, 24);
        JLabel label = new JLabel(labelText + ":");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(TEXT_SECONDARY);
        panel.add(label, labelConstraints);

        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = row;
        valueConstraints.weightx = 1.0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.insets = new Insets(8, 0, 8, 0);
        panel.add(component, valueConstraints);
    }

    private static String formatMoney(BigDecimal amount) {
        return amount == null ? "Not available"
                : String.format(Locale.ROOT, "%.2f EUR", amount);
    }

    private static String displayValue(String value) {
        return value == null || value.isBlank() ? "Not available" : value;
    }
}
