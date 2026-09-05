package com.store_manager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
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
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import com.models.CustomerModel;

public class StoreManagerView extends JFrame {

    public enum CustomerItemStatus {
        NEW,
        UPDATABLE,
        CANCELED
    }

    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(45, 50, 58);
    private static final Color TEXT_SECONDARY = new Color(105, 112, 122);

    private final DefaultListModel<CustomerModel> customerListModel = new DefaultListModel<>();
    private final JList<CustomerModel> customerList = new JList<>(customerListModel);
    private final JPanel mainContentPanel = new JPanel(new BorderLayout());
    private final JButton selfDestructButton = new JButton("Self Destruct");
    private final JMenu updateStoreMenu = new JMenu("Update Store");
    private final JMenuItem updateItemsMenuItem = new JMenuItem("Items");
    private final JMenuItem updateStatusMenuItem = new JMenuItem("Status");
    private final JMenuItem updatePricesMenuItem = new JMenuItem("Prices");
    private final JMenuItem showProfileMenuItem = new JMenuItem("Show More");
    private final JMenuItem logOutMenuItem = new JMenuItem("Log Out");
    private final List<ActionListener> customerDoubleClickListeners = new ArrayList<>();
    private final Map<Integer, CustomerItemStatus> customerItemStatuses = new HashMap<>();
    private int hoveredCustomerIndex = -1;

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

        mainContentPanel.setBackground(BACKGROUND);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 18, 8));
        workspace.add(mainContentPanel, BorderLayout.CENTER);

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
        setMainContent(createTestPanel());
        setSize(1050, 650);
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu storeMenu = new JMenu("Store");
        JMenu profileMenu = new JMenu("Profile");

        updateStoreMenu.add(updateItemsMenuItem);
        updateStoreMenu.add(updateStatusMenuItem);
        updateStoreMenu.add(updatePricesMenuItem);

        storeMenu.add(updateStoreMenu);
        profileMenu.add(showProfileMenuItem);
        profileMenu.addSeparator();
        profileMenu.add(logOutMenuItem);

        menuBar.add(storeMenu);
        menuBar.add(profileMenu);

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
        customerList.setCellRenderer(new ListCellRenderer<CustomerModel>() {
            @Override
            public Component getListCellRendererComponent(JList<? extends CustomerModel> list,
                    CustomerModel customer, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JPanel itemPanel = new JPanel(new BorderLayout(8, 0));
                itemPanel.setOpaque(true);

                String city = customer.getCity() == null || customer.getCity().isBlank()
                        ? "No city"
                        : customer.getCity();

                String customerName = html(customer.getFullname());
                if (index == hoveredCustomerIndex) {
                    customerName = "<b>" + customerName + "</b>";
                }
                JLabel customerDetails = new JLabel("<html>" + customerName
                        + "<br><span style='color:#69707a'>" + html(city) + "</span></html>");
                customerDetails.setForeground(TEXT_PRIMARY);

                CustomerItemStatus status = customerItemStatuses.get(customer.getId());
                JLabel statusMarker = createStatusMarker(status);

                itemPanel.add(customerDetails, BorderLayout.CENTER);
                if (statusMarker != null) {
                    itemPanel.add(statusMarker, BorderLayout.EAST);
                }

                itemPanel.setBackground(isSelected
                        ? selectedBackground(status)
                        : statusBackground(status));
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                        isSelected
                                ? BorderFactory.createLineBorder(new Color(196, 92, 62), 2)
                                : BorderFactory.createMatteBorder(0, 0, 1, 0,
                                        new Color(230, 233, 238)),
                        BorderFactory.createEmptyBorder(5, 10, 5, 8)));
                return itemPanel;
            }
        });

        customerList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent event) {
                if (hoveredCustomerIndex != -1) {
                    hoveredCustomerIndex = -1;
                    customerList.repaint();
                }
            }

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

        customerList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent event) {
                int index = customerList.locationToIndex(event.getPoint());
                Rectangle bounds = index < 0 ? null : customerList.getCellBounds(index, index);
                int newHoveredIndex = bounds != null && bounds.contains(event.getPoint())
                        ? index
                        : -1;

                if (newHoveredIndex != hoveredCustomerIndex) {
                    hoveredCustomerIndex = newHoveredIndex;
                    customerList.repaint();
                }
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

    /**
     * Changes the visual status of one customer entry.
     * Passing {@code null} as the status restores the normal appearance.
     */
    public void setCustomerItemStatus(int customerId, CustomerItemStatus status) {
        if (status == null) {
            customerItemStatuses.remove(customerId);
        } else {
            customerItemStatuses.put(customerId, status);
        }
        customerList.repaint();
    }

    public void setCustomerItemStatus(CustomerModel customer, CustomerItemStatus status) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null.");
        }
        setCustomerItemStatus(customer.getId(), status);
    }

    public CustomerItemStatus getCustomerItemStatus(int customerId) {
        return customerItemStatuses.get(customerId);
    }

    public void clearCustomerItemStatus(int customerId) {
        setCustomerItemStatus(customerId, null);
    }

    public void setMainContent(JPanel panel) {
        mainContentPanel.removeAll();
        if (panel != null) {
            mainContentPanel.add(panel, BorderLayout.CENTER);
        }
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    public static JPanel createTestPanel() {
        JPanel testPanel = new JPanel(new BorderLayout(0, 18));
        testPanel.setBackground(CARD_BACKGROUND);
        testPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 235)),
                BorderFactory.createEmptyBorder(24, 26, 24, 26)));

        JLabel title = new JLabel("Store Manager Test Panel");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);

        JLabel description = new JLabel(
                "This temporary panel demonstrates the main content area.");
        description.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        description.setForeground(TEXT_SECONDARY);

        JPanel heading = new JPanel(new BorderLayout(0, 5));
        heading.setOpaque(false);
        heading.add(title, BorderLayout.NORTH);
        heading.add(description, BorderLayout.SOUTH);
        testPanel.add(heading, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(2, 2, 12, 12));
        buttons.setOpaque(false);
        buttons.add(new JButton("Test Button 1"));
        buttons.add(new JButton("Test Button 2"));
        buttons.add(new JButton("Test Button 3"));
        buttons.add(new JButton("Test Button 4"));
        testPanel.add(buttons, BorderLayout.CENTER);

        JLabel footer = new JLabel("Replace this panel from the controller when ready.");
        footer.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        footer.setForeground(TEXT_SECONDARY);
        testPanel.add(footer, BorderLayout.SOUTH);

        return testPanel;
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

    public void addUpdateItemsListener(ActionListener listener) {
        updateItemsMenuItem.addActionListener(listener);
    }

    public void addUpdateStatusListener(ActionListener listener) {
        updateStatusMenuItem.addActionListener(listener);
    }

    public void addUpdatePricesListener(ActionListener listener) {
        updatePricesMenuItem.addActionListener(listener);
    }

    public void addLogOutListener(ActionListener listener) {
        logOutMenuItem.addActionListener(listener);
    }

    public void addShowProfileListener(ActionListener listener) {
        showProfileMenuItem.addActionListener(listener);
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

    private JLabel createStatusMarker(CustomerItemStatus status) {
        if (status == null) {
            return null;
        }

        JLabel marker = new JLabel();
        marker.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        marker.setHorizontalAlignment(JLabel.RIGHT);
        marker.setVerticalAlignment(JLabel.BOTTOM);

        switch (status) {
            case NEW -> {
                marker.setText("!");
                marker.setForeground(new Color(35, 125, 70));
                marker.setToolTipText("New");
            }
            case UPDATABLE -> {
                marker.setText("\u26A0");
                marker.setForeground(new Color(160, 110, 0));
                marker.setToolTipText("Updatable");
            }
            case CANCELED -> {
                marker.setText("\u2298");
                marker.setForeground(new Color(175, 45, 45));
                marker.setToolTipText("Canceled");
            }
        }
        return marker;
    }

    private Color statusBackground(CustomerItemStatus status) {
        if (status == null) {
            return CARD_BACKGROUND;
        }

        return switch (status) {
            case NEW -> new Color(220, 245, 229);
            case UPDATABLE -> new Color(255, 246, 204);
            case CANCELED -> new Color(253, 226, 226);
        };
    }

    private Color selectedBackground(CustomerItemStatus status) {
        if (status == null) {
            return new Color(250, 222, 212);
        }

        return switch (status) {
            case NEW -> new Color(194, 232, 208);
            case UPDATABLE -> new Color(244, 227, 157);
            case CANCELED -> new Color(241, 198, 198);
        };
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
