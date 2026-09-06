package com.customer;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.customer.views.CustomerView;
import com.models.StoreManagerModel;
import com.models.UserModel;
import com.repository.DBStoreManagerRepository;
public final class CustomerMain {

    private CustomerMain() {
        // Prevent instantiation
    }

    public static void CMain(String[] args, Connection dbConnection , UserModel user) {
        runCustomerModule(args, dbConnection);
    }

    public static void runCustomerModule(String[] args, Connection dbConnection) {
        System.out.println("Hello, Customer!");

        // Open the panel immediately; database loading must not block its creation.
        CustomerView customerView = new CustomerView(new ArrayList<>());
        SwingUtilities.invokeLater(() -> customerView.setVisible(true));

        new Thread(() -> {
            try {
                DBStoreManagerRepository repository = new DBStoreManagerRepository(dbConnection);
                List<StoreManagerModel> restaurants = repository.findAll();
                SwingUtilities.invokeLater(() -> customerView.setRestaurants(restaurants));

            } catch (Exception exception) {
                SwingUtilities.invokeLater(() -> 
                    JOptionPane.showMessageDialog(
                        customerView,
                        "Could not load restaurants: " + exception.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE
                    )
                );
            }
        }).start();
    }
}