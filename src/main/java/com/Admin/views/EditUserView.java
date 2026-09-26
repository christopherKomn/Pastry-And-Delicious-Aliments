package com.admin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.models.UserModel;

/** User editor. Use on the Swing event dispatch thread; persistence belongs to the controller. */
public class EditUserView extends JPanel {
    private static final int IMAGE_SIZE = 160;
    private final JTextField usernameField = new JTextField(24);
    private final JTextField emailField = new JTextField(24);
    private final JTextField phoneField = new JTextField(24);
    private final JPasswordField passwordField = new JPasswordField(24);
    private final JTextField idField = new JTextField(24);
    private final JTextField typeField = new JTextField(24);
    private final JTextField createdField = new JTextField(24);
    private final JTextField imagePathField = new JTextField(24);
    private final JButton profileButton = new JButton("Choose profile image");
    private final JButton saveButton = new JButton("Save");
    private final FileNameExtensionFilter imageFilter = new FileNameExtensionFilter(
            "Images (*.jpg, *.jpeg, *.png, *.gif, *.bmp)", "jpg", "jpeg", "png", "gif", "bmp");
    private UserModel user;

    public EditUserView() {
        super(new BorderLayout());
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setBackground(new Color(245, 247, 250));
        content.setBorder(BorderFactory.createEmptyBorder(26, 30, 28, 30));
        JLabel title = new JLabel("Edit user");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        content.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(18, 20, 20, 20));
        idField.setEditable(false);
        typeField.setEditable(false);
        createdField.setEditable(false);
        imagePathField.setEditable(false);
        addField(form, "User ID", idField, 0);
        addField(form, "Username", usernameField, 1);
        addField(form, "Email", emailField, 2);
        addField(form, "Phone", phoneField, 3);
        addField(form, "Password", passwordField, 4);
        addField(form, "Account type", typeField, 5);
        addField(form, "Created", createdField, 6);
        addField(form, "Profile image path", imagePathField, 7);

        profileButton.setPreferredSize(new Dimension(190, 190));
        profileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        profileButton.setToolTipText("Click to select a profile image");
        profileButton.getAccessibleContext().setAccessibleName("Choose profile image");
        profileButton.addActionListener(event -> chooseProfileImage());
        GridBagConstraints picture = new GridBagConstraints();
        picture.gridx = 0;
        picture.gridy = 8;
        picture.gridwidth = 2;
        picture.insets = new Insets(15, 0, 0, 0);
        form.add(profileButton, picture);
        content.add(form, BorderLayout.CENTER);

        saveButton.setBackground(new Color(196, 92, 62));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(saveButton, BorderLayout.EAST);
        content.add(actions, BorderLayout.SOUTH);
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        setUser(null);
    }

    private void addField(JPanel panel, String text, JTextField field, int row) {
        JLabel label = new JLabel(text);
        label.setLabelFor(field);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(6, 0, 6, 14);
        panel.add(label, constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        field.setPreferredSize(new Dimension(250, 34));
        panel.add(field, constraints);
    }

    /** Loads an editable copy. Passing null clears and disables the editor. */
    public void setUser(UserModel user) {
        this.user = user == null ? null : new UserModel(user);
        usernameField.setText(user == null ? "" : user.getUsername());
        emailField.setText(user == null ? "" : user.getUserEmail());
        phoneField.setText(user == null ? "" : user.getUserPhone());
        passwordField.setText(user == null ? "" : user.getUserPassword());
        idField.setText(user == null ? "" : String.valueOf(user.getUserId()));
        typeField.setText(user == null ? "" : user.getUser_type());
        createdField.setText(user == null || user.getUser_created_at() == null
                ? "" : user.getUser_created_at().toString());
        imagePathField.setText(user == null ? "" : user.getUser_profile_image_url());
        for (JTextField field : new JTextField[] {usernameField, emailField, phoneField, passwordField}) {
            field.setEnabled(user != null);
        }
        saveButton.setEnabled(user != null);
        profileButton.setEnabled(user != null);
        showProfileImage(null);
        String path = imagePathField.getText();
        if (!path.isBlank()) {
            try {
                showProfileImage(ImageIO.read(new File(path)));
            } catch (IOException | SecurityException exception) {
                // Missing or unreadable images leave a usable placeholder.
            }
        }
    }

    /** Returns edited values, preserving ID, type and creation date, or null when no user is loaded. */
    public UserModel getUser() {
        if (user == null) {
            return null;
        }
        UserModel edited = new UserModel(user);
        edited.setUsername(usernameField.getText().trim());
        edited.setUserEmail(emailField.getText().trim());
        edited.setUserPhone(phoneField.getText().trim());
        edited.setUserPassword(new String(passwordField.getPassword()));
        return edited;
    }

    /** The listener can call getUser() and persist the edited values. */
    public void addSaveListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    private void chooseProfileImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose profile image");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(imageFilter);
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        try {
            if (!file.isFile() || !imageFilter.accept(file)) {
                throw new IOException("Unsupported image file");
            }
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new IOException("Unreadable image");
            }
            user.setUser_profile_image_url(file.getAbsolutePath());
            imagePathField.setText(file.getAbsolutePath());
            showProfileImage(image);
        } catch (IOException | SecurityException exception) {
            JOptionPane.showMessageDialog(this,
                    "Please select a readable JPG, JPEG, PNG, GIF or BMP image.",
                    "Cannot open image", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProfileImage(BufferedImage image) {
        profileButton.setIcon(null);
        profileButton.setText("Choose profile image");
        if (image != null) {
            double scale = Math.min((double) IMAGE_SIZE / image.getWidth(),
                    (double) IMAGE_SIZE / image.getHeight());
            profileButton.setIcon(new ImageIcon(image.getScaledInstance(
                    Math.max(1, (int) (image.getWidth() * scale)),
                    Math.max(1, (int) (image.getHeight() * scale)), Image.SCALE_SMOOTH)));
            profileButton.setText("");
        }
    }
}
