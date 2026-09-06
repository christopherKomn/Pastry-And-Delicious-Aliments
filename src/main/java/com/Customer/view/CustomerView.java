package com.customer.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.models.StoreManagerModel;

/** Customer-facing catalogue displayed after a customer logs in. */
public class CustomerView extends JFrame {
    private static final Color BACKGROUND = new Color(250, 247, 244);
    private static final Color TEXT = new Color(48, 40, 36);
    private static final Color MUTED = new Color(116, 103, 96);
    private static final Color ACCENT = new Color(190, 80, 50);
    private static final Color OPEN = new Color(38, 137, 86);

    private final List<StoreManagerModel> restaurants = new ArrayList<>();
    private final List<ActionListener> restaurantListeners = new ArrayList<>();
    private final JPanel cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 18));
    private final JTextField searchField = new JTextField();

    public CustomerView(List<StoreManagerModel> restaurants) {
        setTitle("Pastry | Restaurants");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setSize(1120, 720);
        setLocationRelativeTo(null);
        if (restaurants != null) this.restaurants.addAll(restaurants);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(26, 34, 30, 34));
        setContentPane(root);
        root.add(createHeader(), BorderLayout.NORTH);

        cardsPanel.setBackground(BACKGROUND);
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        root.add(scrollPane, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent event) { triggerRefresh(); }
            @Override public void removeUpdate(DocumentEvent event) { triggerRefresh(); }
            @Override public void changedUpdate(DocumentEvent event) { triggerRefresh(); }
        });
        refreshCards();
    }

    public void setRestaurants(List<StoreManagerModel> restaurants) {
        Runnable update = () -> {
            this.restaurants.clear();
            if (restaurants != null) {
                this.restaurants.addAll(restaurants);
            }
            refreshCards();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            update.run();
        } else {
            SwingUtilities.invokeLater(update);
        }
    }

    public void addRestaurantListener(ActionListener listener) {
        if (listener != null) {
            restaurantListeners.add(listener);
        }
    }

    private void triggerRefresh() {
        // Ensures Swing components are mutated strictly on the Event Dispatch Thread
        SwingUtilities.invokeLater(this::refreshCards);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(20, 10));
        header.setOpaque(false);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        JLabel title = new JLabel("Find something delicious");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setForeground(TEXT);
        JLabel subtitle = new JLabel("Choose from the restaurants available in your area.");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitle.setForeground(MUTED);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.WEST);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 218, 210)), new EmptyBorder(8, 12, 8, 12)));
        JLabel icon = new JLabel("⌕");
        icon.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        icon.setForeground(MUTED);
        searchField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        searchField.setForeground(TEXT);
        searchField.setBorder(null);
        searchField.setOpaque(false);
        searchField.setToolTipText("Search by name, cuisine, or city");
        searchPanel.add(icon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.setPreferredSize(new Dimension(305, 43));
        header.add(searchPanel, BorderLayout.EAST);
        return header;
    }

    private void refreshCards() {
        cardsPanel.removeAll();
        String query = searchField.getText().trim().toLowerCase(Locale.ROOT);
        int visible = 0;

        for (StoreManagerModel restaurant : restaurants) {
            if (restaurant.isIs_active() && matches(restaurant, query)) {
                cardsPanel.add(createCard(restaurant));
                visible++;
            }
        }

        if (visible == 0) {
            JLabel empty = new JLabel("No restaurants found for this search.", SwingConstants.CENTER);
            empty.setForeground(MUTED);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            empty.setPreferredSize(new Dimension(700, 150));
            cardsPanel.add(empty);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private boolean matches(StoreManagerModel restaurant, String query) {
        if (query.isEmpty()) return true;
        return text(restaurant.getName()).contains(query) 
                || text(restaurant.getCuisine_type()).contains(query)
                || text(restaurant.getCity()).contains(query);
    }

    private JPanel createCard(StoreManagerModel restaurant) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(325, 250));
        card.setBorder(normalBorder());
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel cardHeader = new JPanel(new BorderLayout(12, 0));
        cardHeader.setOpaque(false);
        JLabel badge = new JLabel(initials(restaurant.getName()), SwingConstants.CENTER);
        badge.setPreferredSize(new Dimension(48, 48));
        badge.setOpaque(true);
        badge.setBackground(new Color(249, 224, 211));
        badge.setForeground(ACCENT);
        badge.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        badge.setBorder(BorderFactory.createLineBorder(new Color(243, 205, 187), 1, true));
        cardHeader.add(badge, BorderLayout.WEST);

        JPanel namePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        namePanel.setOpaque(false);
        JLabel name = new JLabel(value(restaurant.getName(), "Restaurant"));
        name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        name.setForeground(TEXT);
        JLabel cuisine = new JLabel(value(restaurant.getCuisine_type(), "Various cuisines"));
        cuisine.setForeground(MUTED);
        cuisine.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        namePanel.add(name); 
        namePanel.add(cuisine);
        cardHeader.add(namePanel, BorderLayout.CENTER);
        card.add(cardHeader, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 8));
        info.setOpaque(false);
        info.add(infoLabel("★ " + String.format(Locale.US, "%.1f", restaurant.getRating()) + "  ("
                + restaurant.getTotal_reviews() + " reviews)"));
        info.add(infoLabel("⌖ " + value(restaurant.getCity(), "Location unavailable")));
        info.add(infoLabel("Minimum order: " + String.format(Locale.US, "%.2f €", restaurant.getMin_order_amount())
                + "  ·  Delivery: " + String.format(Locale.US, "%.2f €", restaurant.getDelivery_fee())));
        card.add(info, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JLabel status = new JLabel(restaurant.isIs_accepting_orders() ? "● Open for orders" : "● Temporarily closed");
        status.setForeground(restaurant.isIs_accepting_orders() ? OPEN : MUTED);
        status.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        footer.add(status, BorderLayout.WEST);

        JButton details = new JButton("View ›");
        details.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        details.setForeground(ACCENT);
        details.setBackground(Color.WHITE);
        details.setFocusPainted(false);
        details.setBorder(new EmptyBorder(5, 7, 5, 0));
        details.setActionCommand(String.valueOf(restaurant.getRestaurant_id()));
        details.addActionListener(event -> showRestaurantDetails(this, restaurant));
        for (ActionListener listener : restaurantListeners) {
            details.addActionListener(listener);
        }
        footer.add(details, BorderLayout.EAST);
        card.add(footer, BorderLayout.SOUTH);

        makeClickable(card, restaurant);
        return card;
    }

    private void makeClickable(JComponent component, StoreManagerModel restaurant) {
        component.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseClicked(MouseEvent event) {
                showRestaurantDetails(CustomerView.this, restaurant);
            }
            @Override 
            public void mouseEntered(MouseEvent event) { 
                component.setBorder(hoverBorder()); 
            }
            @Override 
            public void mouseExited(MouseEvent event) { 
                component.setBorder(normalBorder()); 
            }
        });
    }

    private static void showRestaurantDetails(Component parent, StoreManagerModel restaurant) {
        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Restaurant details", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));
        root.setBackground(Color.WHITE);

        JLabel title = new JLabel("Restaurant details");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        title.setForeground(TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel(new GridBagLayout());
        fields.setBackground(Color.WHITE);
        addDetailField(fields, 0, "Restaurant name", restaurant.getName());
        addDetailField(fields, 1, "Address", restaurant.getAddress_line1());
        addDetailField(fields, 2, "City", restaurant.getCity());
        addDetailField(fields, 3, "Postal code", restaurant.getPostal_code());
        addDetailField(fields, 4, "Phone", restaurant.getPhone());
        addDetailField(fields, 5, "Email", restaurant.getEmail());
        addDetailField(fields, 6, "Cuisine type", restaurant.getCuisine_type());
        root.add(fields, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(390, 390));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static void addDetailField(JPanel panel, int row, String labelText, String value) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(7, 0, 7, 14);

        JLabel label = new JLabel(labelText);
        label.setForeground(MUTED);
        panel.add(label, labelConstraints);

        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = row;
        valueConstraints.weightx = 1;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.insets = new Insets(7, 0, 7, 0);

        JLabel valueLabel = new JLabel(value(value, "-"));
        valueLabel.setForeground(TEXT);
        panel.add(valueLabel, valueConstraints);
    }

    private static javax.swing.border.Border normalBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 224, 218)),
                new EmptyBorder(18, 18, 16, 18)
        );
    }

    private static javax.swing.border.Border hoverBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 139, 111), 2),
                new EmptyBorder(17, 17, 15, 17)
        );
    }

    private JLabel infoLabel(String text) {
        JLabel label = new JLabel(text); 
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13)); 
        label.setForeground(MUTED); 
        return label;
    }

    private static String text(String value) { 
        return value == null ? "" : value.toLowerCase(Locale.ROOT); 
    }

    private static String value(String value, String fallback) { 
        return value == null || value.isBlank() ? fallback : value; 
    }

    private static String initials(String name) {
        String[] words = value(name, "R").trim().split("\\s+");
        return ("" + words[0].charAt(0) + (words.length > 1 ? words[1].charAt(0) : "")).toUpperCase(Locale.ROOT);
    }
}



