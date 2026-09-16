package com.customer;

import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.customer.controllers.CustomerController;
import com.customer.services.CustomerService;
import com.customer.views.CustomerView;
import com.models.UserModel;
import com.repository.DBStoreManagerRepository;
import com.repository.DBMenuItemsRepository;

public final class CustomerMain {

    private CustomerMain() {
    }

    public static void CMain(String[] args, Connection dbConnection , UserModel user) {
        runCustomerModule(args, dbConnection);
    }

    public static void runCustomerModule(String[] args, Connection dbConnection) {
        System.out.println("Hello, Customer!");

        SwingUtilities.invokeLater(() -> {
            CustomerService customerService =
                new CustomerService(
                    new DBStoreManagerRepository(dbConnection),
                    new DBMenuItemsRepository(dbConnection));
            CustomerView customerView = new CustomerView(new ArrayList<>());
            CustomerController customerController =
                new CustomerController(customerService, customerView);

            customerView.setVisible(true);
        });
    }
}