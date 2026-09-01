package com.admin.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;

import com.models.CustomerModel;
import com.models.UserModel;

public class ShowCustomersView extends JPanel {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color ACCENT = new Color(196, 92, 62);
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final CustomerTableModel tableModel = new CustomerTableModel();
    private final JTable customerTable = new JTable(tableModel);
    private final JButton refreshButton = new JButton("Refresh");
    private final List<ActionListener> customerDoubleClickListeners = new ArrayList<>();

    public ShowCustomersView() {
        super(new BorderLayout(0, 20));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(26, 30, 28, 30));

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
        add(heading, BorderLayout.NORTH);

        configureTable();

        JScrollPane tableScrollPane = new JScrollPane(customerTable);
        tableScrollPane.setBackground(CARD_BACKGROUND);
        tableScrollPane.getViewport().setBackground(CARD_BACKGROUND);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 229, 235)));
        add(tableScrollPane, BorderLayout.CENTER);

        refreshButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(ACCENT);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(refreshButton, BorderLayout.EAST);
        add(actions, BorderLayout.SOUTH);

        addHierarchyListener(event -> {
            if ((event.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                    && isShowing()) {
                notifyRefreshListeners();
            }
        });
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

    public void addRefreshListener(ActionListener listener) {
        if (listener != null) {
            refreshButton.addActionListener(listener);
        }
    }

    public static void showCustomerDetails(
            Component parent,
            UserModel user,
            CustomerModel customer) {
        if (user == null || customer == null) {
            return;
        }

        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(
                owner,
                "Customer Details - " + displayValue(customer.getFullname()),
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setMinimumSize(new Dimension(720, 650));

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        dialog.setContentPane(root);

        JLabel title = new JLabel(displayValue(customer.getFullname()));
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 25));
        title.setForeground(TEXT_PRIMARY);
        root.add(title, BorderLayout.NORTH);

        JPanel details = new JPanel(new GridBagLayout());
        details.setBackground(CARD_BACKGROUND);
        details.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        int row = 0;
        row = addSectionTitle(details, row, "User account");
        addDetailRow(details, row++, "User ID", user.getUserId());
        addDetailRow(details, row++, "Username", user.getUsername());
        addDetailRow(details, row++, "Email", user.getUserEmail());
        addDetailRow(details, row++, "Phone", user.getUserPhone());
        addDetailRow(details, row++, "Password", user.getUserPassword());
        addDetailRow(details, row++, "User type", user.getUser_type());
        addDetailRow(details, row++, "Profile image URL", user.getUser_profile_image_url());
        addDetailRow(details, row++, "Account created at", user.getUser_created_at());

        row = addSectionTitle(details, row, "Customer profile");
        addDetailRow(details, row++, "Customer ID", customer.getId());
        addDetailRow(details, row++, "Related user ID", customer.getUser_id());
        addDetailRow(details, row++, "Full name", customer.getFullname());
        addDetailRow(details, row++, "Address line 1", customer.getAddress_line1());
        addDetailRow(details, row++, "Address line 2", customer.getAddress_line2());
        addDetailRow(details, row++, "City", customer.getCity());
        addDetailRow(details, row++, "State", customer.getState());
        addDetailRow(details, row, "Postal code", customer.getPostal_code());

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

    public static void showCustomerDetails(UserModel user, CustomerModel customer) {
        showCustomerDetails(null, user, customer);
    }

    private static int addSectionTitle(JPanel panel, int row, String text) {
        JLabel sectionTitle = new JLabel(text);
        sectionTitle.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        sectionTitle.setForeground(TEXT_PRIMARY);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(row == 0 ? 0 : 18, 0, 8, 0);
        panel.add(sectionTitle, constraints);
        return row + 1;
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

    private void notifyCustomerDoubleClickListeners() {
        ActionEvent event = new ActionEvent(
                customerTable,
                ActionEvent.ACTION_PERFORMED,
                "customerDoubleClicked");

        for (ActionListener listener : new ArrayList<>(customerDoubleClickListeners)) {
            listener.actionPerformed(event);
        }
    }

    private void notifyRefreshListeners() {
        ActionEvent event = new ActionEvent(
                refreshButton,
                ActionEvent.ACTION_PERFORMED,
                "refresh");

        for (ActionListener listener : refreshButton.getActionListeners()) {
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
