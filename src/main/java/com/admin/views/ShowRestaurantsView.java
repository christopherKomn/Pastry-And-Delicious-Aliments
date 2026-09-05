package com.admin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.models.StoreManagerModel;

public class ShowRestaurantsView extends JPanel {

    @FunctionalInterface
    public interface RestaurantItemChangeListener {
        void restaurantItemChanged(StoreManagerModel restaurant);
    }

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color ACCENT = new Color(196, 92, 62);
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final RestaurantTableModel tableModel = new RestaurantTableModel();
    private final JTable restaurantTable = new JTable(tableModel);
    private final JMenuItem saveChangesItem = new JMenuItem("Save Changes");
    private final JMenuItem showMoreItem = new JMenuItem("Show More");
    private final JMenuItem removeItem = new JMenuItem("Remove");
    private final JButton refreshButton = new JButton("\u21BB");
    private final JButton saveAllButton = new JButton();
    private final List<ActionListener> viewShownListeners = new ArrayList<>();
    private final List<RestaurantItemChangeListener> restaurantItemChangeListeners =
            new ArrayList<>();

    public ShowRestaurantsView() {
        super(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(26, 30, 28, 30));

        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setOpaque(false);

        JLabel title = new JLabel("Restaurants");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Double-click a cell to edit its value.");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        refreshButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        refreshButton.setForeground(TEXT_PRIMARY);
        refreshButton.setBackground(CARD_BACKGROUND);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.setPreferredSize(new Dimension(42, 36));
        refreshButton.setToolTipText("Refresh restaurants");
        refreshButton.getAccessibleContext().setAccessibleName("Refresh restaurants");
        refreshButton.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 222)));

        saveAllButton.setFocusPainted(false);
        saveAllButton.setOpaque(true);
        saveAllButton.setContentAreaFilled(true);
        saveAllButton.setPreferredSize(new Dimension(42, 36));
        saveAllButton.setToolTipText("Save all restaurant changes");
        saveAllButton.getAccessibleContext().setAccessibleName("Save all restaurant changes");
        setSaveAllEnabled(true);

        JPanel headingActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        headingActions.setOpaque(false);
        headingActions.add(refreshButton);
        headingActions.add(saveAllButton);

        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        heading.add(headingActions, BorderLayout.EAST);
        add(heading, BorderLayout.NORTH);

        configureTable();

        JScrollPane tableScrollPane = new JScrollPane(restaurantTable);
        tableScrollPane.setBackground(CARD_BACKGROUND);
        tableScrollPane.getViewport().setBackground(CARD_BACKGROUND);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 229, 235)));
        add(tableScrollPane, BorderLayout.CENTER);

        addHierarchyListener(event -> {
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                    && isShowing()) {
                notifyViewShownListeners();
            }
        });
    }

    private void configureTable() {
        restaurantTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        restaurantTable.setRowHeight(34);
        restaurantTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        restaurantTable.setForeground(TEXT_PRIMARY);
        restaurantTable.setBackground(CARD_BACKGROUND);
        restaurantTable.setSelectionBackground(new Color(250, 222, 212));
        restaurantTable.setSelectionForeground(TEXT_PRIMARY);
        restaurantTable.setGridColor(new Color(230, 233, 238));
        restaurantTable.setShowVerticalLines(true);
        restaurantTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = restaurantTable.getTableHeader();
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        header.setForeground(TEXT_PRIMARY);
        header.setBackground(new Color(237, 240, 244));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));

        restaurantTable.setDefaultRenderer(Boolean.class, restaurantTable.getDefaultRenderer(Boolean.class));

        int[] widths = {170, 190, 110, 95, 125, 115, 165, 100, 110, 115, 100, 55};
        for (int column = 0; column < widths.length; column++) {
            restaurantTable.getColumnModel().getColumn(column).setPreferredWidth(widths[column]);
        }

        RestaurantActionsCell actionsCell = new RestaurantActionsCell();
        restaurantTable.getColumnModel().getColumn(11).setCellRenderer(actionsCell);
        restaurantTable.getColumnModel().getColumn(11).setCellEditor(actionsCell);
    }

    public void setRestaurants(List<StoreManagerModel> restaurants) {
        tableModel.setRestaurants(restaurants);
    }

    public List<StoreManagerModel> getRestaurants() {
        stopCellEditing();
        return tableModel.getRestaurants();
    }

    public StoreManagerModel getSelectedRestaurant() {
        stopCellEditing();
        int selectedRow = restaurantTable.getSelectedRow();
        return selectedRow < 0 ? null : tableModel.getRestaurantAt(selectedRow);
    }

    public void addSaveChangesListener(ActionListener listener) {
        saveChangesItem.addActionListener(listener);
    }

    public void addShowMoreListener(ActionListener listener) {
        showMoreItem.addActionListener(listener);
    }

    public void addRemoveListener(ActionListener listener) {
        removeItem.addActionListener(listener);
    }

    public void addRefreshListener(ActionListener listener) {
        if (listener != null) {
            refreshButton.addActionListener(listener);
        }
    }

    public void addSaveAllListener(ActionListener listener) {
        if (listener != null) {
            saveAllButton.addActionListener(listener);
        }
    }

    public void setSaveAllEnabled(boolean enabled) {
        Color inactiveIcon = new Color(155, 160, 168);

        saveAllButton.setEnabled(enabled);
        saveAllButton.setBackground(enabled
                ? Color.WHITE
                : new Color(232, 235, 239));
        saveAllButton.setIcon(new SaveAllIcon(enabled ? TEXT_PRIMARY : inactiveIcon));
        saveAllButton.setDisabledIcon(new SaveAllIcon(inactiveIcon));
        saveAllButton.setBorder(BorderFactory.createLineBorder(enabled
                ? new Color(200, 205, 212)
                : new Color(220, 224, 229)));
        saveAllButton.setCursor(enabled
                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : Cursor.getDefaultCursor());
    }

    public void addRestaurantItemChangeListener(RestaurantItemChangeListener listener) {
        if (listener != null) {
            restaurantItemChangeListeners.add(listener);
        }
    }

    public void addViewShownListener(ActionListener listener) {
        if (listener != null) {
            viewShownListeners.add(listener);
        }
    }

    private void notifyViewShownListeners() {
        ActionEvent event = new ActionEvent(
                this,
                ActionEvent.ACTION_PERFORMED,
                "viewShown");

        for (ActionListener listener : new ArrayList<>(viewShownListeners)) {
            listener.actionPerformed(event);
        }
    }

    private void notifyRestaurantItemChangeListeners(StoreManagerModel restaurant) {
        for (RestaurantItemChangeListener listener
                : new ArrayList<>(restaurantItemChangeListeners)) {
            listener.restaurantItemChanged(restaurant);
        }
    }

    public static void showRestaurantDetails(Component parent, StoreManagerModel restaurant) {
        if (restaurant == null) {
            return;
        }

        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(
                owner,
                "Restaurant Details - " + displayValue(restaurant.getName()),
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setMinimumSize(new Dimension(720, 650));

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        dialog.setContentPane(root);

        JLabel title = new JLabel(displayValue(restaurant.getName()));
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        title.setForeground(TEXT_PRIMARY);
        root.add(title, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridBagLayout());
        details.setBackground(CARD_BACKGROUND);
        details.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        int row = 0;
        addDetailRow(details, row++, "Restaurant ID", restaurant.getRestaurant_id());
        addDetailRow(details, row++, "Owner ID", restaurant.getOwner_id());
        addDetailRow(details, row++, "Name", restaurant.getName());
        addDetailRow(details, row++, "Description", restaurant.getDescription());
        addDetailRow(details, row++, "Logo URL", restaurant.getLogo_url());
        addDetailRow(details, row++, "Cover image URL", restaurant.getCover_image_url());
        addDetailRow(details, row++, "Cuisine type", restaurant.getCuisine_type());
        addDetailRow(details, row++, "Phone", restaurant.getPhone());
        addDetailRow(details, row++, "Email", restaurant.getEmail());
        addDetailRow(details, row++, "Address line 1", restaurant.getAddress_line1());
        addDetailRow(details, row++, "Address line 2", restaurant.getAddress_line2());
        addDetailRow(details, row++, "City", restaurant.getCity());
        addDetailRow(details, row++, "State", restaurant.getState());
        addDetailRow(details, row++, "Postal code", restaurant.getPostal_code());
        addDetailRow(details, row++, "Website", restaurant.getWebsite());
        addDetailRow(details, row++, "Active", restaurant.isIs_active() ? "Yes" : "No");
        addDetailRow(details, row++, "Accepting orders",
                restaurant.isIs_accepting_orders() ? "Yes" : "No");
        addDetailRow(details, row++, "Minimum order amount", restaurant.getMin_order_amount());
        addDetailRow(details, row++, "Delivery fee", restaurant.getDelivery_fee());
        addDetailRow(details, row++, "Rating", restaurant.getRating());
        addDetailRow(details, row++, "Total reviews", restaurant.getTotal_reviews());
        addDetailRow(details, row++, "Created at", restaurant.getCreated_at());
        addDetailRow(details, row, "Updated at", restaurant.getUpdated_at());

        JScrollPane scrollPane = new JScrollPane(details);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 229, 235)));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(ACCENT);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        closeButton.addActionListener(event -> dialog.dispose());

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(closeButton, BorderLayout.EAST);
        root.add(actions, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(closeButton);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showRestaurantDetails(StoreManagerModel restaurant) {
        showRestaurantDetails(null, restaurant);
    }

    private static void addDetailRow(JPanel panel, int row, String fieldName, Object value) {
        JLabel nameLabel = new JLabel(fieldName);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        nameLabel.setForeground(TEXT_SECONDARY);

        JTextArea valueText = new JTextArea(displayValue(value));
        valueText.setEditable(false);
        valueText.setFocusable(false);
        valueText.setLineWrap(true);
        valueText.setWrapStyleWord(true);
        valueText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        valueText.setForeground(TEXT_PRIMARY);
        valueText.setBackground(CARD_BACKGROUND);
        valueText.setBorder(null);

        GridBagConstraints nameConstraints = new GridBagConstraints();
        nameConstraints.gridx = 0;
        nameConstraints.gridy = row;
        nameConstraints.weightx = 0;
        nameConstraints.anchor = GridBagConstraints.NORTHWEST;
        nameConstraints.insets = new Insets(7, 0, 7, 22);
        panel.add(nameLabel, nameConstraints);

        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = row;
        valueConstraints.weightx = 1;
        valueConstraints.fill = GridBagConstraints.HORIZONTAL;
        valueConstraints.anchor = GridBagConstraints.NORTHWEST;
        valueConstraints.insets = new Insets(7, 0, 7, 0);
        panel.add(valueText, valueConstraints);
    }

    private static String displayValue(Object value) {
        return value == null || value.toString().isBlank() ? "—" : value.toString();
    }

    private void stopCellEditing() {
        if (restaurantTable.isEditing()) {
            restaurantTable.getCellEditor().stopCellEditing();
        }
    }

    private final class RestaurantTableModel extends AbstractTableModel {

        private static final String[] COLUMN_NAMES = {
                "Name", "Address", "City", "Postal Code", "Cuisine",
                "Phone", "Email", "Active", "Accepting Orders",
                "Minimum Order", "Delivery Fee", ""
        };

        private final List<StoreManagerModel> restaurants = new ArrayList<>();

        public void setRestaurants(List<StoreManagerModel> newRestaurants) {
            restaurants.clear();
            if (newRestaurants != null) {
                restaurants.addAll(newRestaurants);
            }
            fireTableDataChanged();
        }

        public List<StoreManagerModel> getRestaurants() {
            return new ArrayList<>(restaurants);
        }

        public StoreManagerModel getRestaurantAt(int row) {
            return restaurants.get(row);
        }

        @Override
        public int getRowCount() {
            return restaurants.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return switch (column) {
                case 7, 8 -> Boolean.class;
                case 9, 10 -> Double.class;
                default -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return true;
        }

        @Override
        public Object getValueAt(int row, int column) {
            StoreManagerModel restaurant = restaurants.get(row);
            return switch (column) {
                case 0 -> restaurant.getName();
                case 1 -> restaurant.getAddress_line1();
                case 2 -> restaurant.getCity();
                case 3 -> restaurant.getPostal_code();
                case 4 -> restaurant.getCuisine_type();
                case 5 -> restaurant.getPhone();
                case 6 -> restaurant.getEmail();
                case 7 -> restaurant.isIs_active();
                case 8 -> restaurant.isIs_accepting_orders();
                case 9 -> restaurant.getMin_order_amount();
                case 10 -> restaurant.getDelivery_fee();
                case 11 -> "...";
                default -> null;
            };
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            StoreManagerModel restaurant = restaurants.get(row);
            Object previousValue = getValueAt(row, column);

            switch (column) {
                case 0 -> restaurant.setName(stringValue(value));
                case 1 -> restaurant.setAddress_line1(stringValue(value));
                case 2 -> restaurant.setCity(stringValue(value));
                case 3 -> restaurant.setPostal_code(stringValue(value));
                case 4 -> restaurant.setCuisine_type(stringValue(value));
                case 5 -> restaurant.setPhone(stringValue(value));
                case 6 -> restaurant.setEmail(stringValue(value));
                case 7 -> restaurant.setIs_active((Boolean) value);
                case 8 -> restaurant.setIs_accepting_orders((Boolean) value);
                case 9 -> restaurant.setMin_order_amount(doubleValue(value));
                case 10 -> restaurant.setDelivery_fee(doubleValue(value));
                case 11 -> {
                    return;
                }
                default -> {
                    return;
                }
            }

            fireTableCellUpdated(row, column);
            Object currentValue = getValueAt(row, column);
            if (!Objects.equals(previousValue, currentValue)) {
                restaurantTable.setRowSelectionInterval(row, row);
                notifyRestaurantItemChangeListeners(restaurant);
            }
        }

        private static String stringValue(Object value) {
            return value == null ? "" : value.toString().trim();
        }

        private static double doubleValue(Object value) {
            if (value instanceof Number number) {
                return number.doubleValue();
            }
            try {
                return Double.parseDouble(stringValue(value));
            } catch (NumberFormatException exception) {
                return 0.0;
            }
        }
    }

    private final class RestaurantActionsCell extends AbstractCellEditor
            implements TableCellRenderer, TableCellEditor {

        private final JButton button = new JButton("•••");
        private final JPopupMenu menu = new JPopupMenu();

        private RestaurantActionsCell() {
            button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
            button.setForeground(TEXT_PRIMARY);
            button.setBackground(CARD_BACKGROUND);
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            removeItem.setForeground(new Color(180, 45, 45));
            menu.add(saveChangesItem);
            menu.add(showMoreItem);
            menu.addSeparator();
            menu.add(removeItem);

            button.addActionListener(event -> menu.show(button, 0, button.getHeight()));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            button.setBackground(isSelected ? table.getSelectionBackground() : CARD_BACKGROUND);
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            table.setRowSelectionInterval(row, row);
            button.setBackground(table.getSelectionBackground());
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "...";
        }

        
    }

    public void showMessage(String message) {
            JOptionPane.showMessageDialog(this, message);
    }

    private static final class SaveAllIcon implements Icon {
        private final Color color;

        private SaveAllIcon(Color color) {
            this.color = color;
        }

        @Override
        public int getIconWidth() {
            return 20;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }

        @Override
        public void paintIcon(Component component, Graphics graphics, int x, int y) {
            Graphics2D graphics2D = (Graphics2D) graphics.create();
            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setColor(color);

            // Two overlapping document sheets: the conventional "save all" mark.
            graphics2D.drawRoundRect(x + 2, y + 1, 11, 14, 2, 2);
            graphics2D.drawRoundRect(x + 6, y + 5, 11, 13, 2, 2);
            graphics2D.drawLine(x + 9, y + 10, x + 14, y + 10);
            graphics2D.drawLine(x + 9, y + 13, x + 14, y + 13);
            graphics2D.dispose();
        }
    }
}
