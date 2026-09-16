package com.store_manager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.AbstractTableModel;

import com.models.MenuItemsModel;

/** Dialog used by a store manager to choose a menu item for an order. */
public class AddOrderItemView extends JDialog {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color ACCENT = new Color(35, 125, 70);
    private static final String[] COLUMNS = {"Name", "Unit price", "Quantity"};

    private final List<MenuItemsModel> menuItems = new ArrayList<>();
    private final List<ActionListener> dialogShownListeners = new ArrayList<>();
    private final List<ActionListener> selectionListeners = new ArrayList<>();

    private final MenuItemsTableModel tableModel = new MenuItemsTableModel();
    private final JTable menuItemsTable = new JTable(tableModel);
    private final JSpinner quantitySpinner = new JSpinner(
            new SpinnerNumberModel(1, 1, 1, 1));
    private final JLabel totalPriceValue = new JLabel("0.00 EUR");
    private final JButton createButton = new JButton("Create");

    public AddOrderItemView() {
        this(null);
    }

    public AddOrderItemView(Window owner) {
        super(owner, "Add Order Item", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setMinimumSize(new Dimension(560, 430));
        setSize(650, 500);
        setLocationRelativeTo(owner);

        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setBackground(BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        setContentPane(content);

        JLabel title = new JLabel("Add Order Item");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        content.add(title, BorderLayout.NORTH);

        menuItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        menuItemsTable.setRowHeight(28);
        menuItemsTable.setFillsViewportHeight(true);
        menuItemsTable.getTableHeader().setReorderingAllowed(false);
        menuItemsTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                updateTotalPrice();
                updateCreateButton();
                notifyMenuItemSelectionListeners();
            }
        });
        content.add(new JScrollPane(menuItemsTable), BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, 14));
        footer.setOpaque(false);

        JPanel values = new JPanel(new GridBagLayout());
        values.setOpaque(false);
        addValueRow(values, 0, "Quantity", quantitySpinner);
        totalPriceValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        totalPriceValue.setForeground(ACCENT);
        addValueRow(values, 1, "Total price", totalPriceValue);
        footer.add(values, BorderLayout.CENTER);

        createButton.setForeground(Color.WHITE);
        createButton.setBackground(ACCENT);
        createButton.setOpaque(true);
        createButton.setFocusPainted(false);
        createButton.setBorder(BorderFactory.createEmptyBorder(9, 22, 9, 22));
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(createButton);
        footer.add(actions, BorderLayout.SOUTH);
        content.add(footer, BorderLayout.SOUTH);

        quantitySpinner.addChangeListener(event -> updateTotalPrice());
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent event) {
                notifyDialogShownListeners();
            }
        });

        updateCreateButton();
    }

    /** Replaces the menu items displayed in the selection list. */
    public void setMenuItems(List<MenuItemsModel> menuItems) {
        if (menuItems == null) {
            throw new IllegalArgumentException("Menu-item list cannot be null.");
        }
        if (menuItems.stream().anyMatch(item -> item == null)) {
            throw new IllegalArgumentException("Menu-item list cannot contain null values.");
        }

        this.menuItems.clear();
        this.menuItems.addAll(menuItems);
        tableModel.fireTableDataChanged();
        menuItemsTable.clearSelection();
        updateTotalPrice();
        updateCreateButton();
    }

    /** Sets the inclusive minimum and maximum allowed order-item quantity. */
    public void setQuantityLimits(int minimum, int maximum) {
        if (minimum < 1 || maximum < minimum) {
            throw new IllegalArgumentException(
                    "Quantity limits must satisfy 1 <= minimum <= maximum.");
        }
        quantitySpinner.setModel(new SpinnerNumberModel(minimum, minimum, maximum, 1));
        updateTotalPrice();
    }

    /** Returns the currently selected menu item, or null when none is selected. */
    public MenuItemsModel getSelectedMenuItem() {
        int selectedRow = menuItemsTable.getSelectedRow();
        return selectedRow < 0
                ? null
                : menuItems.get(menuItemsTable.convertRowIndexToModel(selectedRow));
    }

    public int getOrderItemQuantity() {
        return ((Number) quantitySpinner.getValue()).intValue();
    }

    /** Adds code to run every time the dialog becomes visible. */
    public void addDialogShownListener(ActionListener listener) {
        if (listener != null) {
            dialogShownListeners.add(listener);
        }
    }

    public void addCreateListener(ActionListener listener) {
        createButton.addActionListener(listener);
    }

    /** Adds code to run whenever the selected menu item changes. */
    public void addMenuItemSelectionListener(ActionListener listener) {
        if (listener != null) {
            selectionListeners.add(listener);
        }
    }

    private void updateCreateButton() {
        createButton.setEnabled(getSelectedMenuItem() != null);
    }

    private void updateTotalPrice() {
        MenuItemsModel selectedItem = getSelectedMenuItem();
        if (selectedItem == null || selectedItem.getItem_price() == null) {
            totalPriceValue.setText("0.00 EUR");
            return;
        }
        BigDecimal total = selectedItem.getItem_price()
                .multiply(BigDecimal.valueOf(getOrderItemQuantity()));
        totalPriceValue.setText(String.format(Locale.ROOT, "%.2f EUR", total));
    }

    private void notifyDialogShownListeners() {
        notifyListeners(dialogShownListeners, "dialogShown");
    }

    private void notifyMenuItemSelectionListeners() {
        notifyListeners(selectionListeners, "menuItemSelected");
    }

    private void notifyListeners(List<ActionListener> listeners, String command) {
        ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
        for (ActionListener listener : new ArrayList<>(listeners)) {
            listener.actionPerformed(event);
        }
    }

    private static void addValueRow(JPanel panel, int row, String labelText,
            java.awt.Component value) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.LINE_START;
        labelConstraints.insets = new Insets(5, 0, 5, 16);
        JLabel label = new JLabel(labelText + ":");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        panel.add(label, labelConstraints);

        GridBagConstraints valueConstraints = new GridBagConstraints();
        valueConstraints.gridx = 1;
        valueConstraints.gridy = row;
        valueConstraints.weightx = 1.0;
        valueConstraints.anchor = GridBagConstraints.LINE_START;
        valueConstraints.insets = new Insets(5, 0, 5, 0);
        panel.add(value, valueConstraints);
    }

    private final class MenuItemsTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return menuItems.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMNS[column];
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == 2 ? Integer.class : String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Object getValueAt(int row, int column) {
            MenuItemsModel item = menuItems.get(row);
            return switch (column) {
                case 0 -> displayValue(item.getItem_name());
                case 1 -> formatMoney(item.getItem_price());
                case 2 -> item.getItem_quantity();
                default -> "";
            };
        }
    }

    private static String formatMoney(BigDecimal price) {
        return price == null ? "Not available"
                : String.format(Locale.ROOT, "%.2f EUR", price);
    }

    private static String displayValue(String value) {
        return value == null || value.isBlank() ? "Not available" : value;
    }
}
