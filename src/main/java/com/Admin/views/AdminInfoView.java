package com.admin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.models.UserModel;

/**
 * Displays the currently connected administrator's profile information.
 */
public class AdminInfoView extends JPanel {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);
    private static final int IMAGE_SIZE = 180;

    private final JLabel profileImageLabel = new JLabel();
    private final JLabel usernameValue = createValueLabel();
    private final JLabel emailValue = createValueLabel();
    private final JLabel phoneValue = createValueLabel();
    private final JLabel userTypeValue = createValueLabel();
    private final JLabel createdAtValue = createValueLabel();
    private final List<ActionListener> viewShownListeners = new ArrayList<>();
    private final List<ActionListener> profileImageDoubleClickListeners = new ArrayList<>();

    private UserModel user;

    public AdminInfoView() {
        super(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(26, 30, 28, 30));

        JLabel title = new JLabel("Administrator profile");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);
        add(title, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(0, 24));
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 235)),
                BorderFactory.createEmptyBorder(28, 28, 28, 28)));

        configureProfileImage();
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        imagePanel.setOpaque(false);
        imagePanel.add(profileImageLabel);
        card.add(imagePanel, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridBagLayout());
        details.setOpaque(false);
        int row = 0;
        addDetail(details, row++, "Username", usernameValue);
        addDetail(details, row++, "Email", emailValue);
        addDetail(details, row++, "Phone", phoneValue);
        addDetail(details, row++, "User type", userTypeValue);
        addDetail(details, row, "Created at", createdAtValue);
        card.add(details, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        addHierarchyListener(event -> {
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                    && isShowing()) {
                notifyListeners(viewShownListeners, "viewShown");
            }
        });

        showEmptyProfile();
    }

    public AdminInfoView(UserModel user) {
        this();
        setUser(user);
    }

    public void setUser(UserModel user) {
        this.user = user;

        if (user == null) {
            showEmptyProfile();
            return;
        }

        usernameValue.setText(displayValue(user.getUsername()));
        emailValue.setText(displayValue(user.getUserEmail()));
        phoneValue.setText(displayValue(user.getUserPhone()));
        userTypeValue.setText(displayValue(user.getUser_type()));
        createdAtValue.setText(user.getUser_created_at() == null
                ? "Not available"
                : user.getUser_created_at().toString());
        setProfileImage(user.getUser_profile_image_url());
    }

    public UserModel getUser() {
        return user;
    }

    public void addViewShownListener(ActionListener listener) {
        if (listener != null) {
            viewShownListeners.add(listener);
        }
    }

    public void addProfileImageDoubleClickListener(ActionListener listener) {
        if (listener != null) {
            profileImageDoubleClickListeners.add(listener);
        }
    }

    private void configureProfileImage() {
        profileImageLabel.setPreferredSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        profileImageLabel.setMinimumSize(new Dimension(IMAGE_SIZE, IMAGE_SIZE));
        profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profileImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        profileImageLabel.setOpaque(true);
        profileImageLabel.setBackground(new Color(237, 240, 244));
        profileImageLabel.setForeground(TEXT_SECONDARY);
        profileImageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileImageLabel.setToolTipText("Double-click the profile image");
        profileImageLabel.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 222)));
        profileImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    notifyListeners(profileImageDoubleClickListeners, "profileImageDoubleClicked");
                }
            }
        });
    }

    private void setProfileImage(String imagePath) {
        profileImageLabel.setIcon(null);

        if (imagePath == null || imagePath.isBlank()) {
            profileImageLabel.setText("No profile image");
            return;
        }

        File imageFile = new File(imagePath);
        ImageIcon sourceIcon = new ImageIcon(imageFile.getAbsolutePath());
        if (!imageFile.isFile() || sourceIcon.getIconWidth() <= 0) {
            profileImageLabel.setText("Image unavailable");
            return;
        }

        double scale = Math.min(
                (double) IMAGE_SIZE / sourceIcon.getIconWidth(),
                (double) IMAGE_SIZE / sourceIcon.getIconHeight());
        int scaledWidth = Math.max(1, (int) Math.round(sourceIcon.getIconWidth() * scale));
        int scaledHeight = Math.max(1, (int) Math.round(sourceIcon.getIconHeight() * scale));
        Image scaledImage = sourceIcon.getImage().getScaledInstance(
                scaledWidth,
                scaledHeight,
                Image.SCALE_SMOOTH);
        profileImageLabel.setText("");
        profileImageLabel.setIcon(new ImageIcon(scaledImage));
    }

    private void showEmptyProfile() {
        usernameValue.setText("Not available");
        emailValue.setText("Not available");
        phoneValue.setText("Not available");
        userTypeValue.setText("Not available");
        createdAtValue.setText("Not available");
        setProfileImage(null);
    }

    private void notifyListeners(List<ActionListener> listeners, String command) {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
        for (ActionListener listener : new ArrayList<>(listeners)) {
            listener.actionPerformed(event);
        }
    }

    private static void addDetail(JPanel panel, int row, String name, JLabel value) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.LINE_START;
        labelConstraints.insets = new Insets(8, 0, 8, 20);

        JLabel nameLabel = new JLabel(name + ":");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        nameLabel.setForeground(TEXT_SECONDARY);
        panel.add(nameLabel, labelConstraints);

        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = row;
        valueConstraints.weightx = 1.0;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.anchor = GridBagConstraints.LINE_START;
        valueConstraints.insets = new Insets(8, 0, 8, 0);
        panel.add(value, valueConstraints);
    }

    private static JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private static String displayValue(String value) {
        return value == null || value.isBlank() ? "Not available" : value;
    }
}
