package com.store_manager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import com.models.MenuItemsModel;
import com.models.OrderItemsModel;

/** Displays menu information alongside the corresponding order items. */
public class OrderItemsView extends JPanel {

    @FunctionalInterface
    public interface OrderItemQuantityChangeListener {
        void quantityChanged(OrderItemsModel orderItem);
    }

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final String[] COLUMNS = {
        "Menu item",
        "Unit price",
        "Total quantity",
        "Availability",
        "Ordered quantity",
        "Special instructions"
    };

    private final List<OrderItemsModel> orderItems = new ArrayList<>();
    private final List<MenuItemsModel> menuItems = new ArrayList<>();
    private final List<OrderItemQuantityChangeListener> quantityChangeListeners = new ArrayList<>();

    private final OrderItemsTableModel tableModel = new OrderItemsTableModel();
    private final JTable itemsTable = new JTable(tableModel);
    private final JButton addButton = new JButton("Add");
    private final JButton removeButton = new JButton("Remove");

    public OrderItemsView() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public OrderItemsView(List<OrderItemsModel> orderItems,
            List<MenuItemsModel> menuItems) {
        super(new BorderLayout(0, 14));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel title = new JLabel("Order Items");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setRowHeight(27);
        itemsTable.setFillsViewportHeight(true);
        itemsTable.getTableHeader().setReorderingAllowed(false);
        itemsTable.getSelectionModel().addListSelectionListener(event -> updateRemoveButton());

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(215, 220, 228)));
        add(scrollPane, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(removeButton);
        actions.add(addButton);
        add(actions, BorderLayout.SOUTH);

        setOrderItems(orderItems, menuItems);
    }

    /**
     * Sets the rows displayed by the view. The item at each index in one list
     * must correspond to the item at the same index in the other list.
     */
    public void setOrderItems(List<OrderItemsModel> orderItems,
            List<MenuItemsModel> menuItems) {
        if (orderItems == null || menuItems == null) {
            throw new IllegalArgumentException("Order-item and menu-item lists cannot be null.");
        }
        if (orderItems.size() != menuItems.size()) {
            throw new IllegalArgumentException("The two lists must have the same size.");
        }

        for (int index = 0; index < orderItems.size(); index++) {
            OrderItemsModel orderItem = orderItems.get(index);
            MenuItemsModel menuItem = menuItems.get(index);
            if (orderItem == null || menuItem == null) {
                throw new IllegalArgumentException("The lists cannot contain null items.");
            }
            if (orderItem.getMenu_item_id() != menuItem.getItem_id()) {
                throw new IllegalArgumentException(
                        "Order item and menu item do not correspond at index " + index + ".");
            }
        }

        this.orderItems.clear();
        this.orderItems.addAll(orderItems);
        this.menuItems.clear();
        this.menuItems.addAll(menuItems);
        tableModel.fireTableDataChanged();
        itemsTable.clearSelection();
        updateRemoveButton();
    }

    /** Returns the displayed order items, including edited quantities. */
    public List<OrderItemsModel> getOrderItems() {
        return new ArrayList<>(orderItems);
    }

    /** Returns the order item selected in the table, or null if none is selected. */
    public OrderItemsModel getSelectedOrderItem() {
        int row = itemsTable.getSelectedRow();
        return row < 0 ? null : orderItems.get(itemsTable.convertRowIndexToModel(row));
    }

    /** Returns the menu item paired with the selected order item. */
    public MenuItemsModel getSelectedMenuItem() {
        int row = itemsTable.getSelectedRow();
        return row < 0 ? null : menuItems.get(itemsTable.convertRowIndexToModel(row));
    }

    public void addOrderItemQuantityChangeListener(OrderItemQuantityChangeListener listener) {
        if (listener != null) {
            quantityChangeListeners.add(listener);
        }
    }

    public void addAddListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }

    public void addRemoveListener(ActionListener listener) {
        removeButton.addActionListener(listener);
    }

    private void updateRemoveButton() {
        removeButton.setEnabled(itemsTable.getSelectedRow() >= 0);
    }

    private void notifyQuantityChanged(OrderItemsModel orderItem) {
        for (OrderItemQuantityChangeListener listener :
                new ArrayList<>(quantityChangeListeners)) {
            listener.quantityChanged(orderItem);
        }
    }

    private final class OrderItemsTableModel extends AbstractTableModel {

        @Override
        public int getRowCount() {
            return orderItems.size();
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
            return switch (column) {
                case 2, 4 -> Integer.class;
                default -> String.class;
            };
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4;
        }

        @Override
        public Object getValueAt(int row, int column) {
            OrderItemsModel orderItem = orderItems.get(row);
            MenuItemsModel menuItem = menuItems.get(row);
            return switch (column) {
                case 0 -> displayValue(menuItem.getItem_name());
                case 1 -> formatMoney(menuItem.getItem_price());
                case 2 -> menuItem.getItem_quantity();
                case 3 -> Boolean.TRUE.equals(menuItem.getIs_available())
                        ? "Available" : "Unavailable";
                case 4 -> orderItem.getQuantity();
                case 5 -> displayValue(orderItem.getSpecial_instructions());
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column != 4) {
                return;
            }

            Integer quantity = parseQuantity(value);
            if (quantity == null || quantity < 1) {
                fireTableCellUpdated(row, column);
                return;
            }

            OrderItemsModel orderItem = orderItems.get(row);
            if (quantity == orderItem.getQuantity()) {
                return;
            }
            orderItem.setQuantity(quantity);
            fireTableCellUpdated(row, column);
            notifyQuantityChanged(orderItem);
        }

        private Integer parseQuantity(Object value) {
            if (value instanceof Number number) {
                return number.intValue();
            }
            try {
                return Integer.valueOf(String.valueOf(value).trim());
            } catch (NumberFormatException exception) {
                return null;
            }
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
