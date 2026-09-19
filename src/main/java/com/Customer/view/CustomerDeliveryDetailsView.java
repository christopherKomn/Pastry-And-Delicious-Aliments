package com.customer.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CustomerDeliveryDetailsView extends JFrame {
    private static final Color BACKGROUND = new Color(250, 247, 244);
    private static final Color TEXT = new Color(48, 40, 36);
    private static final Color ACCENT = new Color(190, 80, 50);
    private static final Color BORDER = new Color(232, 224, 218);
    private final JTextField cityField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField postalCodeField = new JTextField();
    private final JTextField contactPhoneField = new JTextField();
    private final JButton saveButton = new JButton("Save Details");

    public CustomerDeliveryDetailsView() {
        setTitle("Delivery Details");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(460, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(new EmptyBorder(26, 28, 26, 28));
        setContentPane(root);

        JLabel title = new JLabel("Delivery Details");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        title.setForeground(TEXT);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 12));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(16, 16, 16, 16)));
        form.add(createField("City", cityField));
        form.add(createField("Address", addressField));
        form.add(createField("Postal Code", postalCodeField));
        form.add(createField("Contact Phone", contactPhoneField));
        root.add(form, BorderLayout.CENTER);

        saveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBackground(ACCENT);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        root.add(saveButton, BorderLayout.SOUTH);
    }

    public void addSaveListener(ActionListener listener) {
        if (listener != null) {
            saveButton.addActionListener(listener);
        }
    }

    public String getCity() {
        return cityField.getText();
    }

    public String getAddress() {
        return addressField.getText();
    }

    public String getPostalCode() {
        return postalCodeField.getText();
    }

    public String getContactPhone() {
        return contactPhoneField.getText();
    }

    public void setDetails(
            String city,
            String address,
            String postalCode,
            String contactPhone) {
        cityField.setText(city);
        addressField.setText(address);
        postalCodeField.setText(postalCode);
        contactPhoneField.setText(contactPhone);
    }

    private JPanel createField(String labelText, JTextField field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(0, 5));
        fieldPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(TEXT);

        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setForeground(TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(8, 10, 8, 10)));

        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        return fieldPanel;
    }
}
