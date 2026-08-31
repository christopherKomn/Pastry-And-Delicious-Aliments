package com.store_manager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import com.models.CustomerModel;

public class StoreManagerView extends JFrame {

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final DefaultListModel<CustomerModel> customerListModel = new DefaultListModel<>();
    private final JList<CustomerModel> customerList = new JList<>(customerListModel);
    private final JButton selfDestructButton = new JButton("Self Destruct");
    private final JMenuItem updateStoreMenuItem = new JMenuItem("Update Store");
    private final JMenuItem logOutMenuItem = new JMenuItem("Log Out");
    private final List<ActionListener> customerDoubleClickListeners = new ArrayList<>();

    public StoreManagerView() {
        setTitle("Store Manager");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(950, 580));
        setJMenuBar(createMenuBar());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));
        setContentPane(root);

        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBackground(BACKGROUND);
        workspace.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 18));

        selfDestructButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        selfDestructButton.setForeground(Color.WHITE);
        selfDestructButton.setBackground(new Color(190, 38, 38));
        selfDestructButton.setFocusPainted(false);
        selfDestructButton.setOpaque(true);
        selfDestructButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        selfDestructButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JPanel dangerActions = new JPanel(new BorderLayout());
        dangerActions.setOpaque(false);
        dangerActions.add(selfDestructButton, BorderLayout.WEST);
        workspace.add(dangerActions, BorderLayout.SOUTH);

        JPanel customersPanel = createCustomersPanel();

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                workspace,
                customersPanel);
        splitPane.setResizeWeight(0.80);
        splitPane.setDividerLocation(0.80);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        splitPane.setBackground(BACKGROUND);
        root.add(splitPane, BorderLayout.CENTER);

        configureCustomerList();
        setSize(1050, 650);
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu storeMenu = new JMenu("Store");

        storeMenu.add(updateStoreMenuItem);
        storeMenu.addSeparator();
        storeMenu.add(logOutMenuItem);
        menuBar.add(storeMenu);

        return menuBar;
    }

    private JPanel createCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 235)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JPanel heading = new JPanel(new BorderLayout(0, 4));
        heading.setOpaque(false);

        JLabel title = new JLabel("Customers");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Double-click to select a customer");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitle.setForeground(TEXT_SECONDARY);

        heading.add(title, BorderLayout.NORTH);
        heading.add(subtitle, BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(customerList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 238)));
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void configureCustomerList() {
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.setFixedCellHeight(52);
        customerList.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        customerList.setBackground(CARD_BACKGROUND);
        customerList.setSelectionBackground(new Color(250, 222, 212));
        customerList.setSelectionForeground(TEXT_PRIMARY);
        customerList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                CustomerModel customer = (CustomerModel) value;
                String city = customer.getCity() == null || customer.getCity().isBlank()
                        ? "No city"
                        : customer.getCity();
                label.setText("<html><b>" + html(customer.getFullname())
                        + "</b><br><span style='color:#69707a'>" + html(city) + "</span></html>");
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });

        customerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() != 2) {
                    return;
                }

                int index = customerList.locationToIndex(event.getPoint());
                if (index < 0) {
                    return;
                }

                Rectangle bounds = customerList.getCellBounds(index, index);
                if (bounds == null || !bounds.contains(event.getPoint())) {
                    return;
                }

                customerList.setSelectedIndex(index);
                notifyCustomerDoubleClickListeners();
            }
        });
    }

    public void setCustomers(List<CustomerModel> customers) {
        customerListModel.clear();
        if (customers != null) {
            for (CustomerModel customer : customers) {
                if (customer != null) {
                    customerListModel.addElement(customer);
                }
            }
        }
    }

    public CustomerModel getSelectedCustomer() {
        return customerList.getSelectedValue();
    }

    public void addCustomerDoubleClickListener(ActionListener listener) {
        if (listener != null) {
            customerDoubleClickListeners.add(listener);
        }
    }

    public void addSelfDestructListener(ActionListener listener) {
        selfDestructButton.addActionListener(listener);
    }

    public void addUpdateStoreListener(ActionListener listener) {
        updateStoreMenuItem.addActionListener(listener);
    }

    public void addLogOutListener(ActionListener listener) {
        logOutMenuItem.addActionListener(listener);
    }

    private void notifyCustomerDoubleClickListeners() {
        ActionEvent event = new ActionEvent(
                customerList,
                ActionEvent.ACTION_PERFORMED,
                "customerDoubleClicked");

        for (ActionListener listener : new ArrayList<>(customerDoubleClickListeners)) {
            listener.actionPerformed(event);
        }
    }

    private static String html(String value) {
        if (value == null) {
            return "—";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
};
