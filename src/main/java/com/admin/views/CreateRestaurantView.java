package com.admin.views;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import com.models.UserModel;
public class CreateRestaurantView extends JFrame {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color ACCENT = new Color(196, 92, 62);
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final JTextField usernameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JTextField phoneField = new JTextField();


    private final JTextField restaurantNameField = new JTextField();
    private final JTextField restaurantAddressField = new JTextField();
    private final JTextField restaurantCityField = new JTextField();
    private final JTextField restaurantPostalCodeField = new JTextField();
    private final JTextField restaurantPhoneField = new JTextField();
    private final JTextField restaurantEmailField = new JTextField();
    private final JTextField cuisineTypeField = new JTextField();

    private final JButton createButton = new JButton("Create");
    private final JButton browseUsersButton = new JButton("Select existing user");
    private final JButton useSelectedUserButton = new JButton("Use selected user");
    private final DefaultListModel<UserModel> userListModel = new DefaultListModel<>();
    private final JList<UserModel> userList = new JList<>(userListModel);
    private final JDialog userListDialog = new JDialog(this, "Select a user", true);
    private final List<ActionListener> showUsersListeners = new ArrayList<>();

    public CreateRestaurantView() {
        setTitle("Admin - Create Restaurant Owner");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(760, 470));
        setLocationByPlatform(true);

        JPanel root = new JPanel(new BorderLayout(0, 22));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(26, 30, 28, 30));
        setContentPane(root);

        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setOpaque(false);

        JLabel title = new JLabel("Create a restaurant owner");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Enter the account and restaurant details below.");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        root.add(heading, BorderLayout.NORTH);

        JPanel forms = new JPanel(new GridLayout(1, 2, 18, 0));
        forms.setOpaque(false);

        JPanel ownerPanel = createSection("Owner account");
        addField(ownerPanel, "Username", usernameField, 0);
        addField(ownerPanel, "Email", emailField, 1);
        addField(ownerPanel, "Password", passwordField, 2);
        addField(ownerPanel, "Phone", phoneField, 3);
        addWideButton(ownerPanel, browseUsersButton, 5);

        JPanel restaurantPanel = createSection("Restaurant details");
        addField(restaurantPanel, "Restaurant name", restaurantNameField, 0);
        addField(restaurantPanel, "Address", restaurantAddressField, 1);
        addField(restaurantPanel, "City", restaurantCityField, 2);
        addField(restaurantPanel, "Postal code", restaurantPostalCodeField, 3);
        addField(restaurantPanel, "Phone", restaurantPhoneField, 4);
        addField(restaurantPanel, "Email", restaurantEmailField, 5);
        addField(restaurantPanel, "Cuisine type", cuisineTypeField, 6);

        forms.add(ownerPanel);
        forms.add(restaurantPanel);
        root.add(forms, BorderLayout.CENTER);

        createButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        createButton.setForeground(Color.WHITE);
        createButton.setBackground(ACCENT);
        createButton.setFocusPainted(false);
        createButton.setBorder(BorderFactory.createEmptyBorder(11, 24, 11, 24));
        createButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(createButton, BorderLayout.EAST);
        root.add(actions, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(createButton);
        configureUserListDialog();
        pack();
        setLocationRelativeTo(null);
    }

    private void configureUserListDialog() {
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setFixedCellHeight(34);
        userList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        userList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                UserModel user = (UserModel) value;
                label.setText(user.getUsername());
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return label;
            }
        });

        JLabel instructions = new JLabel("Choose an existing user account");
        instructions.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        instructions.setForeground(TEXT_PRIMARY);
        instructions.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        useSelectedUserButton.setBackground(ACCENT);
        useSelectedUserButton.setForeground(Color.WHITE);
        useSelectedUserButton.setFocusPainted(false);
        useSelectedUserButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));

        JPanel dialogActions = new JPanel(new BorderLayout());
        dialogActions.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        dialogActions.add(useSelectedUserButton, BorderLayout.EAST);

        userListDialog.setLayout(new BorderLayout());
        userListDialog.add(instructions, BorderLayout.NORTH);
        userListDialog.add(new JScrollPane(userList), BorderLayout.CENTER);
        userListDialog.add(dialogActions, BorderLayout.SOUTH);
        userListDialog.setSize(380, 390);
        userListDialog.setLocationRelativeTo(this);

        browseUsersButton.addActionListener(event -> {
            ActionEvent showUsersEvent = new ActionEvent(
                    browseUsersButton,
                    ActionEvent.ACTION_PERFORMED,
                    "showUsers");
            for (ActionListener listener : new ArrayList<>(showUsersListeners)) {
                listener.actionPerformed(showUsersEvent);
            }
            userListDialog.setLocationRelativeTo(this);
            userListDialog.setVisible(true);
        });

        useSelectedUserButton.addActionListener(event -> {
            UserModel selectedUser = userList.getSelectedValue();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(userListDialog, "Please select a user first.");
                return;
            }
            setOwnerFields(selectedUser);
            userListDialog.dispose();
        });

        userList.addListSelectionListener(event ->
                useSelectedUserButton.setEnabled(!event.getValueIsAdjusting()
                        && userList.getSelectedValue() != null));
        useSelectedUserButton.setEnabled(false);
    }

    private JPanel createSection(String sectionTitle) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 235)),
                BorderFactory.createEmptyBorder(18, 20, 20, 20)));

        JLabel title = new JLabel(sectionTitle);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
        title.setForeground(TEXT_PRIMARY);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 0, 14, 0);
        panel.add(title, constraints);

        return panel;
    }

    private void addField(JPanel panel, String labelText, JTextField field, int row) {
        JLabel label = new JLabel(labelText, SwingConstants.LEFT);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        label.setForeground(TEXT_SECONDARY);

        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(220, 36));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 211, 220)),
                BorderFactory.createEmptyBorder(7, 9, 7, 9)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row + 1;
        labelConstraints.weightx = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(5, 0, 12, 12);
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row + 1;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(5, 0, 12, 0);
        panel.add(field, fieldConstraints);
    }

    private void addWideButton(JPanel panel, JButton button, int row) {
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setForeground(ACCENT);
        button.setBackground(new Color(255, 245, 241));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(232, 167, 145)),
                BorderFactory.createEmptyBorder(9, 12, 9, 12)));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(8, 0, 0, 0);
        panel.add(button, constraints);
    }

    public void addUser(UserModel user) {
        if (user != null) {
            userListModel.addElement(user);
        }
    }

    public void addShowUsersListener(ActionListener listener) {
        if (listener != null) {
            showUsersListeners.add(listener);
        }
    }

    public void clearUsers() {
        userListModel.clear();
    }

    public UserModel getSelectedUser() {
        return userList.getSelectedValue();
    }

    public void setOwnerFields(UserModel user) {
        if (user == null) {
            return;
        }
        usernameField.setText(user.getUsername());
        emailField.setText(user.getUserEmail());
        passwordField.setText(user.getUserPassword());
        phoneField.setText(user.getUserPhone());
    }

    public String getUsernameInput() {
        return usernameField.getText().trim();
    }

    public String getEmailInput() {
        return emailField.getText().trim();
    }

    public String getPasswordInput() {
        return new String(passwordField.getPassword());
    }

    public String getPhoneInput() {
        return phoneField.getText().trim();
    }




    public String getRestaurantNameInput() {
        return restaurantNameField.getText().trim();
    }

    public String getAddressInput() {
        return restaurantAddressField.getText().trim();
    }

    public String getCityInput() {
        return restaurantCityField.getText().trim();
    }

    public String getPostalCodeInput() {
        return restaurantPostalCodeField.getText().trim();
    }

    public String getRestaurantPhoneInput() {
        return restaurantPhoneField.getText().trim();
    }

    public String getRestaurantEmailInput() {
        return restaurantEmailField.getText().trim();
    }

    public String getCuisineTypeInput() {
        return cuisineTypeField.getText().trim();
    }

    public void addCreateListener(ActionListener listener) {
        createButton.addActionListener(listener);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}
