package com.store_manager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.models.OrderModel;

/** Displays an order and exposes actions for handling a pending order. */
public class OrderView extends JPanel {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color TEXT_SECONDARY = new Color(100, 108, 118);
    private static final Color PENDING_COLOR = new Color(230, 126, 34);
    private static final Color CONFIRMED_COLOR = new Color(52, 152, 219);
    private static final Color CANCELLED_COLOR = new Color(192, 57, 43);
    private static final Color DELIVERED_COLOR = new Color(46, 160, 67);
    private static final Color IN_PROGRESS_COLOR = new Color(25, 100, 55);

    private final JLabel subtotalValue = new JLabel();
    private final JLabel discountValue = new JLabel();
    private final JLabel totalValue = new JLabel();
    private final JLabel statusValue = new JLabel();
    private final JLabel paymentMethodValue = new JLabel();
    private final JTextArea specialInstructionsValue = new JTextArea();
    private final JButton acceptButton = new JButton("Accept \u2713");
    private final JButton rejectButton = new JButton("Reject \u2715");
    private final JButton orderItemsButton = new JButton("Order Items");

    private OrderModel order;

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
        statusValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        addField(content, row++, "Status", statusValue);
        addReadOnlyField(content, row++, "Payment method", paymentMethodValue);

        specialInstructionsValue.setEditable(false);
        specialInstructionsValue.setFocusable(false);
        specialInstructionsValue.setLineWrap(true);
        specialInstructionsValue.setWrapStyleWord(true);
        specialInstructionsValue.setRows(4);
        specialInstructionsValue.setBackground(new Color(248, 249, 251));
        specialInstructionsValue.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));
        addField(content, row++, "Special instructions", new JScrollPane(specialInstructionsValue));

        acceptButton.setForeground(new Color(30, 130, 60));
        acceptButton.setFont(acceptButton.getFont().deriveFont(Font.BOLD));
        rejectButton.setForeground(CANCELLED_COLOR);
        rejectButton.setFont(rejectButton.getFont().deriveFont(Font.BOLD));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(orderItemsButton);
        buttons.add(rejectButton);
        buttons.add(acceptButton);

        GridBagConstraints buttonConstraints = new GridBagConstraints();
        buttonConstraints.gridx = 1;
        buttonConstraints.gridy = row;
        buttonConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonConstraints.anchor = GridBagConstraints.LINE_END;
        buttonConstraints.insets = new Insets(18, 0, 0, 0);
        content.add(buttons, buttonConstraints);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND);
        wrapper.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        wrapper.add(content, BorderLayout.NORTH);
        add(new JScrollPane(wrapper), BorderLayout.CENTER);

        setTheOrder(null);
    }

    /** Replaces the order displayed by this panel. */
    public void setTheOrder(OrderModel order) {
        this.order = order;
        boolean hasOrder = order != null;
        subtotalValue.setText(hasOrder ? formatMoney(order.getSubtotal()) : "-");
        discountValue.setText(hasOrder ? formatMoney(order.getDiscount_amount()) : "-");
        totalValue.setText(hasOrder ? formatMoney(order.getTotal_amount()) : "-");
        paymentMethodValue.setText(hasOrder ? displayValue(order.getPayment_method()) : "-");
        specialInstructionsValue.setText(
                hasOrder ? displayValue(order.getSpecial_instructions()) : "No order selected");
        updateStatus();
        updateButtons();
    }

    /** Returns the order currently displayed by the view. */
    public OrderModel getTheOrder() {
        return order;
    }

    /** Adds code to run when the Accept button is clicked. */
    public void addAcceptListener(ActionListener listener) {
        acceptButton.addActionListener(listener);
    }

    /** Adds code to run when the Reject button is clicked. */
    public void addRejectListener(ActionListener listener) {
        rejectButton.addActionListener(listener);
    }

    /** Adds code to run when the Order Items button is clicked. */
    public void addOrderItemsListener(ActionListener listener) {
        orderItemsButton.addActionListener(listener);
    }

    private void updateButtons() {
        boolean pending = order != null && "pending".equalsIgnoreCase(order.getStatus());
        boolean cancelled = order != null && order.getStatus() != null
                && ("cancelled".equalsIgnoreCase(order.getStatus())
                    || "canceled".equalsIgnoreCase(order.getStatus()));
        orderItemsButton.setEnabled(pending);
        acceptButton.setEnabled(pending);
        rejectButton.setEnabled(order != null && !cancelled);
    }

    private void updateStatus() {
        if (order == null || order.getStatus() == null || order.getStatus().isBlank()) {
            statusValue.setText("-");
            statusValue.setForeground(TEXT_SECONDARY);
            return;
        }

        String status = order.getStatus();
        statusValue.setText(status.replace('_', ' '));
        statusValue.setForeground(switch (status.toLowerCase(Locale.ROOT)) {
            case "pending" -> PENDING_COLOR;
            case "confirmed" -> CONFIRMED_COLOR;
            case "cancelled", "canceled" -> CANCELLED_COLOR;
            case "delivered" -> DELIVERED_COLOR;
            default -> IN_PROGRESS_COLOR;
        });
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
