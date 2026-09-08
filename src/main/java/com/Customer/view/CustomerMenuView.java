package com.customer.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SpinnerNumberModel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

import com.models.CartItem;
import com.models.MenuItemsModel;

public class CustomerMenuView extends JFrame {
    private static final Color BACKGROUND = new Color(250, 247, 244);
    private static final Color TEXT = new Color(48, 40, 36);
    private static final Color MUTED = new Color(116, 103, 96);
    private static final Color ACCENT = new Color(190, 80, 50);
    private static final Color ACCENT_SOFT = new Color(253, 241, 236);
    private static final Color PRICE_TEXT = new Color(70, 70, 70);
    private static final Color BORDER = new Color(232, 224, 218);

    private final int restaurantId;
    private final JPanel productsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
    private final JPanel cartPanel = new JPanel();
    private final JLabel cartTotalLabel = new JLabel("Total: 0.00 €");
    private final JButton continueButton = new JButton("Continue");
    private final List<ActionListener> addToCartListeners = new ArrayList<>();
    private final List<ActionListener> removeFromCartListeners = new ArrayList<>();

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

        JLabel title = new JLabel(restaurantName + " - Menu");
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
        JPanel cartFooter = new JPanel(new BorderLayout(0, 10));
        cartFooter.setBackground(Color.WHITE);
        cartFooter.add(cartTotalLabel, BorderLayout.NORTH);
        styleActionButton(continueButton);
        continueButton.setVisible(false);
        cartFooter.add(continueButton, BorderLayout.SOUTH);
        cart.add(cartFooter, BorderLayout.SOUTH);
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

    public void removeFromCartListener(ActionListener listener) {
        if (listener != null) {
            removeFromCartListeners.add(listener);
        }
    }

    public void setProducts(List<MenuItemsModel> products) {
        productsPanel.removeAll();
        for (MenuItemsModel product : products) {
            productsPanel.add(createProductRow(product));
        }
        if (products.isEmpty()) {
            productsPanel.add(new JLabel("No products available.", SwingConstants.CENTER));
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    public void setCart(List<CartItem> cartItems, BigDecimal total) {
        refreshCart(cartItems, total);
    }

    private JPanel createProductRow(MenuItemsModel product) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 14, 12, 14)));
        JLabel details = new JLabel(product.getItem_name() + "  -  " + format(product.getItem_price()) + " €");
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
        quantitySpinner.setEditor(new JSpinner.NumberEditor(quantitySpinner, "0"));
        quantitySpinner.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        quantitySpinner.setPreferredSize(new Dimension(82, 36));
        quantitySpinner.setBorder(BorderFactory.createLineBorder(BORDER));
        JComponent spinnerEditor = quantitySpinner.getEditor();
        if (spinnerEditor instanceof JSpinner.DefaultEditor) {
            JTextField editor = ((JSpinner.DefaultEditor) spinnerEditor).getTextField();
            editor.setHorizontalAlignment(JTextField.CENTER);
            editor.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            editor.setEditable(false);
            editor.setFocusable(false);
            SwingUtilities.invokeLater(() -> editor.setHorizontalAlignment(SwingConstants.CENTER));
        }
        for (Component component : quantitySpinner.getComponents()) {
            if (component instanceof JButton) {
                JButton arrowButton = (JButton) component;
                arrowButton.setBackground(Color.WHITE);
                arrowButton.setForeground(ACCENT);
                arrowButton.setPreferredSize(new Dimension(28, 17));
                arrowButton.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.WHITE));
                arrowButton.setFocusPainted(false);
                arrowButton.setContentAreaFilled(true);
                arrowButton.setOpaque(true);
                arrowButton.setUI(new SpinnerArrowButtonUI());
                arrowButton.setIcon(new SpinnerArrowIcon(
                    arrowButton.getName() != null && arrowButton.getName().contains("next")));
            }
        }

        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new BoxLayout(quantityPanel, BoxLayout.Y_AXIS));
        quantityPanel.setBackground(Color.WHITE);
        quantityPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER),
            new EmptyBorder(16, 18, 16, 18)));

        JLabel productLabel = new JLabel(product.getItem_name());
        productLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        productLabel.setForeground(TEXT);
        productLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel priceLabel = new JLabel(format(product.getItem_price()) + " €");
        priceLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        priceLabel.setForeground(PRICE_TEXT);
        priceLabel.setBorder(new EmptyBorder(3, 0, 14, 0));
        priceLabel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel quantityRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quantityRow.setBackground(ACCENT_SOFT);
        quantityRow.setBorder(new EmptyBorder(10, 10, 10, 10));
        quantityRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel quantityLabel = new JLabel("Quantity");
        quantityLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        quantityLabel.setForeground(TEXT);
        quantityRow.add(quantityLabel);
        quantityRow.add(quantitySpinner);

        quantityPanel.add(productLabel);
        quantityPanel.add(priceLabel);
        quantityPanel.add(quantityRow);

        JOptionPane optionPane = new JOptionPane(
                quantityPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = optionPane.createDialog(this, "Add product");
        styleOptionPaneButtons(optionPane);
        dialog.setResizable(false);
        if (spinnerEditor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) spinnerEditor).getTextField()
            .setHorizontalAlignment(SwingConstants.CENTER);
        }
        dialog.setVisible(true);
        if (spinnerEditor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) spinnerEditor).getTextField()
                .setHorizontalAlignment(JTextField.CENTER);
        }
        Object selectedValue = optionPane.getValue();
        int result = selectedValue instanceof Integer
            ? (Integer) selectedValue
            : JOptionPane.CLOSED_OPTION;
        if (result == JOptionPane.OK_OPTION) {
            notifyAddToCartListeners(product, (Integer) quantitySpinner.getValue());
        }
    }

    private static void styleOptionPaneButtons(Component component) {
        if (component instanceof JButton
                && ("OK".equals(((JButton) component).getText())
                    || "Cancel".equals(((JButton) component).getText()))) {
            JButton button = (JButton) component;
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setForeground(Color.WHITE);
            button.setBackground(ACCENT);
            button.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        }
        if (component instanceof java.awt.Container) {
            for (Component child : ((java.awt.Container) component).getComponents()) {
                styleOptionPaneButtons(child);
            }
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

    private void notifyRemoveFromCartListeners(MenuItemsModel product) {
        ActionEvent event = new ActionEvent(
                this,
                ActionEvent.ACTION_PERFORMED,
                String.valueOf(product.getItem_id()));
        for (ActionListener listener : removeFromCartListeners) {
            listener.actionPerformed(event);
        }
    }

    private void refreshCart(List<CartItem> cartItems, BigDecimal total) {
        cartPanel.removeAll();
        boolean hasItems = false;
        for (CartItem cartItemData : cartItems) {
            MenuItemsModel product = cartItemData.getProduct();
            if (product != null && cartItemData.getQuantity() > 0) {
                hasItems = true;
                BigDecimal lineTotal = product.getItem_price().multiply(
                        BigDecimal.valueOf(cartItemData.getQuantity()));
                JPanel cartItem = new JPanel(new BorderLayout(6, 0));
                cartItem.setBackground(Color.WHITE);
                cartItem.setBorder(new EmptyBorder(6, 2, 6, 2));
                cartItem.setAlignmentX(LEFT_ALIGNMENT);
                cartItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
                JLabel itemLabel = new JLabel(cartItemData.getQuantity() + " x " + product.getItem_name()
                    + " - " + format(lineTotal) + " €");
                itemLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                itemLabel.setForeground(MUTED);
                JButton removeButton = new JButton("🗑");
                removeButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 21));
                removeButton.setForeground(ACCENT);
                removeButton.setBackground(Color.WHITE);
                removeButton.setPreferredSize(new Dimension(34, 32));
                removeButton.setFocusPainted(false);
                removeButton.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 2));
                removeButton.setToolTipText("Remove product");
                removeButton.addActionListener(event -> notifyRemoveFromCartListeners(product));
                cartItem.add(itemLabel, BorderLayout.CENTER);
                cartItem.add(removeButton, BorderLayout.EAST);
                cartPanel.add(cartItem);
            }
        }
        cartTotalLabel.setText("Total: " + format(total) + " €");
        continueButton.setVisible(hasItems);
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

    private static final class SpinnerArrowIcon implements Icon {
        private final boolean pointsUp;

        private SpinnerArrowIcon(boolean pointsUp) {
            this.pointsUp = pointsUp;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(Color.WHITE);
            int centerX = x + getIconWidth() / 2;
            int centerY = y + getIconHeight() / 2;
            int[] xPoints = {centerX - 6, centerX + 6, centerX};
            int[] yPoints = pointsUp
                ? new int[] {centerY + 4, centerY + 4, centerY - 4}
                : new int[] {centerY - 4, centerY - 4, centerY + 4};
            graphics2D.fillPolygon(xPoints, yPoints, 3);
            graphics2D.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 14;
        }
    }

    private static final class SpinnerArrowButtonUI extends BasicButtonUI {
        @Override
        public void paint(Graphics graphics, JComponent component) {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, component.getWidth(), component.getHeight());
            super.paint(graphics, component);
        }
    }

    private static String format(BigDecimal value) {
        return value == null ? "0.00" : value.setScale(2, java.math.RoundingMode.HALF_UP).toString();
    }
}
