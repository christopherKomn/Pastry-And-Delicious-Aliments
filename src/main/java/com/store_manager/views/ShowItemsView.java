package com.store_manager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import com.models.MenuItemsModel;

public class ShowItemsView extends JPanel {
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final ItemsTableModel tableModel = new ItemsTableModel();
    private final JTable itemsTable = new JTable(tableModel);
    private final JScrollPane itemsScrollPane = new JScrollPane(itemsTable);
    private final JButton addItemButton = new JButton("Add Item");
    private final JButton toggleAvailabilityButton = new JButton("Toggle Availability");
    private final JButton removeItemButton = new JButton("Remove Item");
    private final JButton backButton = new JButton("Back to Store Manager");
    private final JLabel itemCountLabel = new JLabel("No items to display.");
    private final JPanel addItemForm = new JPanel(new BorderLayout(0, 10));
    private final JTextField itemNameInput = new JTextField();
    private final JTextField itemPriceInput = new JTextField();
    private final JTextField itemQuantityInput = new JTextField("0");
    private final JCheckBox itemAvailableInput = new JCheckBox("Available", true);
    private final JButton saveItemButton = new JButton("Save Item");
    private ItemEditListener itemEditListener;

    @FunctionalInterface
    public interface ItemEditListener {
        void itemEdited(MenuItemsModel item, int column, Object value);
    }

    public void setItemEditListener(ItemEditListener listener) {
        itemEditListener = listener;
    }

    public void refreshItemDisplay() {
        itemsTable.repaint();
        updateActions();
    }

    public void cancelItemEditing() {
        if (itemsTable.isEditing()) {
            itemsTable.getCellEditor().cancelCellEditing();
        }
    }

    public ShowItemsView() {
        super(new BorderLayout(0, 22));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel heading = new JPanel(new BorderLayout(0, 8));
        heading.setOpaque(false);
        JLabel title = new JLabel("Store Items");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("View your menu items and their availability.");
        subtitle.setForeground(TEXT_SECONDARY);
        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        add(heading, BorderLayout.NORTH);

        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setRowHeight(38);
        itemsTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        itemsTable.setForeground(TEXT_PRIMARY);
        itemsTable.setSelectionBackground(new Color(250, 222, 212));
        itemsTable.setSelectionForeground(TEXT_PRIMARY);
        itemsTable.setShowGrid(false);
        itemsTable.setFillsViewportHeight(true);
        itemsTable.getTableHeader().setReorderingAllowed(false);
        // Text editors commit on Enter; Escape cancels without saving.
        itemsTable.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
        itemsTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));
        itemsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(
                new JComboBox<String>(new String[] {"Available", "Unavailable"})));
        itemsTable.getSelectionModel().addListSelectionListener(event -> updateActions());

        itemsScrollPane.getViewport().setBackground(Color.WHITE);
        itemsScrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 229, 235)));
        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setOpaque(false);
        content.add(itemsScrollPane, BorderLayout.CENTER);
        configureAddItemForm();
        content.add(addItemForm, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(0, 14));
        footer.setOpaque(false);
        itemCountLabel.setForeground(TEXT_SECONDARY);
        footer.add(itemCountLabel, BorderLayout.NORTH);

        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navigation.setOpaque(false);
        navigation.add(backButton);
        footer.add(navigation, BorderLayout.SOUTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);
        actions.add(addItemButton);
        actions.add(toggleAvailabilityButton);
        actions.add(removeItemButton);
        footer.add(actions, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        updateActions();
    }

    public void addBackListener(ActionListener listener) {
        backButton.addActionListener(event -> {
            cancelItemEditing();
            listener.actionPerformed(event);
        });
    }

    private void configureAddItemForm() {
        addItemForm.setBackground(Color.WHITE);
        addItemForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 235)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        addItemForm.add(new JLabel("Add Item"), BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(4, 2, 8, 8));
        fields.setOpaque(false);
        fields.add(new JLabel("Name"));
        fields.add(itemNameInput);
        fields.add(new JLabel("Price"));
        fields.add(itemPriceInput);
        fields.add(new JLabel("Quantity"));
        fields.add(itemQuantityInput);
        fields.add(new JLabel("Availability"));
        itemAvailableInput.setOpaque(false);
        fields.add(itemAvailableInput);
        addItemForm.add(fields, BorderLayout.CENTER);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> hideAddItemForm());
        buttons.add(saveItemButton);
        buttons.add(cancelButton);
        addItemForm.add(buttons, BorderLayout.SOUTH);
        addItemForm.setVisible(false);
    }

    public void showAddItemForm() {
        cancelItemEditing();
        itemsTable.clearSelection();
        itemsScrollPane.setVisible(false);
        addItemForm.setVisible(true);
        revalidate();
        repaint();
        itemNameInput.requestFocusInWindow();
    }

    public void hideAddItemForm() {
        addItemForm.setVisible(false);
        itemsScrollPane.setVisible(true);
        itemNameInput.setText("");
        itemPriceInput.setText("");
        itemQuantityInput.setText("0");
        itemAvailableInput.setSelected(true);
        revalidate();
        repaint();
    }

    public void addSaveItemListener(ActionListener listener) {
        saveItemButton.addActionListener(listener);
    }

    public String getItemNameInput() { return itemNameInput.getText(); }
    public String getItemPriceInput() { return itemPriceInput.getText(); }
    public String getItemQuantityInput() { return itemQuantityInput.getText(); }
    public boolean getItemAvailableInput() { return itemAvailableInput.isSelected(); }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public void setItems(List<MenuItemsModel> items) {
        cancelItemEditing();
        tableModel.items.clear();
        if (items != null) {
            for (MenuItemsModel item : items) {
                if (item != null) {
                    tableModel.items.add(item);
                }
            }
        }
        tableModel.fireTableDataChanged();
        itemCountLabel.setText(tableModel.items.isEmpty()
                ? "No items to display." : "Items: " + tableModel.items.size());
        updateActions();
    }

    public MenuItemsModel getSelectedItem() {
        int row = itemsTable.getSelectedRow();
        return row < 0 ? null : tableModel.items.get(itemsTable.convertRowIndexToModel(row));
    }

    public void removeItemFromList(MenuItemsModel item) {
        cancelItemEditing();
        int row = tableModel.items.indexOf(item);
        if (row < 0) {
            return;
        }
        tableModel.items.remove(row);
        tableModel.fireTableRowsDeleted(row, row);
        if (!tableModel.items.isEmpty()) {
            int nextRow = Math.min(row, tableModel.items.size() - 1);
            itemsTable.setRowSelectionInterval(nextRow, nextRow);
        }
        itemCountLabel.setText(tableModel.items.isEmpty()
                ? "No items to display." : "Items: " + tableModel.items.size());
        updateActions();
    }

    public void addAddItemListener(ActionListener listener) {
        addItemButton.addActionListener(listener);
    }

    public void addToggleAvailabilityListener(ActionListener listener) {
        toggleAvailabilityButton.addActionListener(listener);
        updateActions();
    }

    public void addRemoveItemListener(ActionListener listener) {
        removeItemButton.addActionListener(listener);
        updateActions();
    }

    private void updateActions() {
        MenuItemsModel selected = getSelectedItem();
        toggleAvailabilityButton.setText(selected == null ? "Toggle Availability"
                : Boolean.TRUE.equals(selected.getIs_available()) ? "Mark Unavailable" : "Mark Available");
    }

    private class ItemsTableModel extends AbstractTableModel {
        private final String[] columns = {"Item", "Price", "Quantity", "Availability"};
        private final List<MenuItemsModel> items = new ArrayList<>();

        @Override
        public int getRowCount() { return items.size(); }

        @Override
        public int getColumnCount() { return columns.length; }

        @Override
        public String getColumnName(int column) { return columns[column]; }

        @Override
        public boolean isCellEditable(int row, int column) {
            return itemEditListener != null && column >= 1 && column <= 3;
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (!isCellEditable(row, column)) {
                return;
            }
            // The service updates the model only after a successful database write.
            itemEditListener.itemEdited(items.get(row), column, value);
            fireTableCellUpdated(row, column);
            updateActions();
        }

        @Override
        public Object getValueAt(int row, int column) {
            MenuItemsModel item = items.get(row);
            return switch (column) {
                case 0 -> item.getItem_name();
                case 1 -> String.format(java.util.Locale.ROOT, "%.2f", item.getItem_price());
                case 2 -> item.getItem_quantity();
                case 3 -> Boolean.TRUE.equals(item.getIs_available()) ? "Available" : "Unavailable";
                default -> "";
            };
        }
    }
}
