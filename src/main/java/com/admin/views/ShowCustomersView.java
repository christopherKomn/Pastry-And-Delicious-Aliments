package com.admin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

import com.models.CustomerModel;

public class ShowCustomersView extends JFrame {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final CustomerTableModel tableModel = new CustomerTableModel();
    private final JTable customerTable = new JTable(tableModel);
    private final List<ActionListener> customerDoubleClickListeners = new ArrayList<>();

    public ShowCustomersView() {
        setTitle("Admin - Customers");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(900, 520));

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(26, 30, 28, 30));
        setContentPane(root);

        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setOpaque(false);

        JLabel title = new JLabel("Customers");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Double-click a customer to view the complete information.");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        root.add(heading, BorderLayout.NORTH);

        configureTable();

        JScrollPane tableScrollPane = new JScrollPane(customerTable);
        tableScrollPane.setBackground(CARD_BACKGROUND);
        tableScrollPane.getViewport().setBackground(CARD_BACKGROUND);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 229, 235)));
        root.add(tableScrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    private void configureTable() {
        customerTable.setRowHeight(36);
        customerTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        customerTable.setForeground(TEXT_PRIMARY);
        customerTable.setBackground(CARD_BACKGROUND);
        customerTable.setSelectionBackground(new Color(250, 222, 212));
        customerTable.setSelectionForeground(TEXT_PRIMARY);
        customerTable.setGridColor(new Color(230, 233, 238));
        customerTable.setShowVerticalLines(true);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = customerTable.getTableHeader();
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        header.setForeground(TEXT_PRIMARY);
        header.setBackground(new Color(237, 240, 244));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));

        customerTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(220);

        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() != 2) {
                    return;
                }

                Point clickPoint = event.getPoint();
                int row = customerTable.rowAtPoint(clickPoint);
                if (row < 0) {
                    return;
                }

                customerTable.setRowSelectionInterval(row, row);
                notifyCustomerDoubleClickListeners();
            }
        });
    }

    public void setCustomers(List<CustomerModel> customers) {
        tableModel.setCustomers(customers);
    }

    public List<CustomerModel> getCustomers() {
        return tableModel.getCustomers();
    }

    public CustomerModel getSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        return selectedRow < 0 ? null : tableModel.getCustomerAt(selectedRow);
    }

    public void addCustomerDoubleClickListener(ActionListener listener) {
        if (listener != null) {
            customerDoubleClickListeners.add(listener);
        }
    }

    private void notifyCustomerDoubleClickListeners() {
        ActionEvent event = new ActionEvent(
                customerTable,
                ActionEvent.ACTION_PERFORMED,
                "customerDoubleClicked");

        for (ActionListener listener : new ArrayList<>(customerDoubleClickListeners)) {
            listener.actionPerformed(event);
        }
    }

    private static final class CustomerTableModel extends AbstractTableModel {

        private static final String[] COLUMN_NAMES = {
                "Full Name", "City", "State", "Postal Code", "Address"
        };

        private final List<CustomerModel> customers = new ArrayList<>();

        public void setCustomers(List<CustomerModel> newCustomers) {
            customers.clear();
            if (newCustomers != null) {
                customers.addAll(newCustomers);
            }
            fireTableDataChanged();
        }

        public List<CustomerModel> getCustomers() {
            return new ArrayList<>(customers);
        }

        public CustomerModel getCustomerAt(int row) {
            return customers.get(row);
        }

        @Override
        public int getRowCount() {
            return customers.size();
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
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Object getValueAt(int row, int column) {
            CustomerModel customer = customers.get(row);
            return switch (column) {
                case 0 -> customer.getFullname();
                case 1 -> customer.getCity();
                case 2 -> customer.getState();
                case 3 -> customer.getPostal_code();
                case 4 -> customer.getAddress_line1();
                default -> null;
            };
        }
    }
}
