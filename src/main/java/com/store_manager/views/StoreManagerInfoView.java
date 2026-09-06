package com.store_manager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.models.StoreManagerModel;

/** Read-only restaurant profile panel for a store manager. */
public class StoreManagerInfoView extends JPanel {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);
    private static final Color ACTIVE = new Color(35, 125, 70);
    private static final Color INACTIVE = new Color(175, 45, 45);
    private static final int IMAGE_SIZE = 400;

    private final StoreManagerModel restaurant;

    public StoreManagerInfoView(StoreManagerModel restaurant) {
        super(new BorderLayout());
        if (restaurant == null) {
            throw new IllegalArgumentException("Restaurant cannot be null.");
        }
        this.restaurant = restaurant;
        setBackground(BACKGROUND);

        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(26, 30, 28, 30));

        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setOpaque(false);
        JLabel title = new JLabel(displayValue(restaurant.getName()));
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 27));
        title.setForeground(TEXT_PRIMARY);
        JLabel cuisine = new JLabel(displayValue(restaurant.getCuisine_type()));
        cuisine.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cuisine.setForeground(TEXT_SECONDARY);
        heading.add(title, BorderLayout.NORTH);
        heading.add(cuisine, BorderLayout.SOUTH);
        content.add(heading, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(0, 24));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 235)),
                BorderFactory.createEmptyBorder(26, 28, 28, 28)));

        JPanel imageArea = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        imageArea.setOpaque(false);
        imageArea.add(createRestaurantImage());
        card.add(imageArea, BorderLayout.NORTH);

        JPanel information = new JPanel(new GridBagLayout());
        information.setOpaque(false);
        int row = addDescription(information, 0);
        addDetail(information, row++, "Status", restaurant.isIs_active() ? "Active" : "Inactive",
                restaurant.isIs_active() ? ACTIVE : INACTIVE);
        addDetail(information, row++, "Orders",
                restaurant.isIs_accepting_orders() ? "Accepting orders" : "Not accepting orders",
                restaurant.isIs_accepting_orders() ? ACTIVE : INACTIVE);
        addDetail(information, row++, "Phone", restaurant.getPhone(), TEXT_PRIMARY);
        addDetail(information, row++, "Email", restaurant.getEmail(), TEXT_PRIMARY);
        addDetail(information, row++, "Website", restaurant.getWebsite(), TEXT_PRIMARY);
        addDetail(information, row++, "Address", buildAddress(restaurant), TEXT_PRIMARY);
        addDetail(information, row++, "Minimum order", money(restaurant.getMin_order_amount()), TEXT_PRIMARY);
        addDetail(information, row++, "Delivery fee", money(restaurant.getDelivery_fee()), TEXT_PRIMARY);
        addDetail(information, row, "Rating",
                String.format(Locale.ROOT, "%.2f / 5 (%d reviews)",
                        restaurant.getRating(), restaurant.getTotal_reviews()), TEXT_PRIMARY);
        card.add(information, BorderLayout.CENTER);
        content.add(card, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public StoreManagerModel getRestaurant() {
        return restaurant;
    }

    private JLabel createRestaurantImage() {
        JLabel imageLabel = new JLabel("No restaurant image", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        imageLabel.setMinimumSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(237, 240, 244));
        imageLabel.setForeground(TEXT_SECONDARY);
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 222)));

        String imagePath = restaurant.getLogo_url();
        if (imagePath == null || imagePath.isBlank()) {
            imagePath = restaurant.getCover_image_url();
        }
        if (imagePath == null || imagePath.isBlank()) {
            return imageLabel;
        }

        File imageFile = new File(imagePath);
        if (!imageFile.isFile()) {
            imageLabel.setText("Image unavailable");
            return imageLabel;
        }

        ImageIcon sourceIcon = new ImageIcon(imageFile.getAbsolutePath());
        if (sourceIcon.getIconWidth() <= 0 || sourceIcon.getIconHeight() <= 0) {
            imageLabel.setText("Image unavailable");
            return imageLabel;
        }

        double scale = Math.min((double) IMAGE_SIZE / sourceIcon.getIconWidth(),
                (double) IMAGE_SIZE / sourceIcon.getIconHeight());
        int width = Math.max(1, (int) Math.round(sourceIcon.getIconWidth() * scale));
        int height = Math.max(1, (int) Math.round(sourceIcon.getIconHeight() * scale));
        Image image = sourceIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        imageLabel.setText("");
        imageLabel.setIcon(new ImageIcon(image));
        return imageLabel;
    }

    private int addDescription(JPanel panel, int row) {
        JTextArea description = new JTextArea(displayValue(restaurant.getDescription()));
        description.setEditable(false);
        description.setFocusable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setRows(3);
        description.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        description.setForeground(TEXT_PRIMARY);
        description.setBackground(CARD_BACKGROUND);
        description.setBorder(BorderFactory.createTitledBorder("Description"));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 12, 0);
        panel.add(description, constraints);
        return row + 1;
    }

    private static void addDetail(JPanel panel, int row, String labelText, String valueText, Color valueColor) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.LINE_START;
        labelConstraints.insets = new Insets(7, 0, 7, 24);
        JLabel label = new JLabel(labelText + ":");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(TEXT_SECONDARY);
        panel.add(label, labelConstraints);

        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = row;
        valueConstraints.weightx = 1.0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.anchor = GridBagConstraints.LINE_START;
        valueConstraints.insets = new Insets(7, 0, 7, 0);
        JLabel value = new JLabel(displayValue(valueText));
        value.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        value.setForeground(valueColor);
        panel.add(value, valueConstraints);
    }

    private static String buildAddress(StoreManagerModel restaurant) {
        StringBuilder address = new StringBuilder();
        appendPart(address, restaurant.getAddress_line1());
        appendPart(address, restaurant.getAddress_line2());
        appendPart(address, restaurant.getCity());
        appendPart(address, restaurant.getState());
        appendPart(address, restaurant.getPostal_code());
        return address.length() == 0 ? "Not available" : address.toString();
    }

    private static void appendPart(StringBuilder output, String value) {
        if (value == null || value.isBlank()) return;
        if (output.length() > 0) output.append(", ");
        output.append(value.trim());
    }

    private static String money(double amount) {
        return String.format(Locale.ROOT, "%.2f EUR", amount);
    }

    private static String displayValue(String value) {
        return value == null || value.isBlank() ? "Not available" : value;
    }
}
