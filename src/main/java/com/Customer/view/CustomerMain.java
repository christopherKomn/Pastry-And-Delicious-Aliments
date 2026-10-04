package com.customer;

import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.customer.controllers.CustomerController;
import com.customer.services.CustomerService;
import com.customer.views.CustomerView;
import com.models.UserModel;
import com.customer.services.CustomerCheckoutService;
import com.repository.DBCustomerRepository;
import com.repository.DBStoreManagerRepository;
import com.repository.DBMenuItemsRepository;
import com.repository.DBOrderItemsRepository;
import com.repository.DBOrderRepository;

public final class CustomerMain {

    private CustomerMain() {
    }

    public static void CMain(String[] args, Connection dbConnection , UserModel user) {
        runCustomerModule(args, dbConnection, user);
    }

    public static void runCustomerModule(String[] args, Connection dbConnection) {
        runCustomerModule(args, dbConnection, null);
    }

    public static void runCustomerModule(String[] args, Connection dbConnection, UserModel user) {
        System.out.println("Hello, Customer!");

        SwingUtilities.invokeLater(() -> {
            CustomerService customerService =
                new CustomerService(
                    new DBStoreManagerRepository(dbConnection),
                    new DBMenuItemsRepository(dbConnection));
            CustomerCheckoutService checkoutService = new CustomerCheckoutService(
                    dbConnection,
                    new DBCustomerRepository(dbConnection),
                    new DBOrderRepository(dbConnection),
                    new DBOrderItemsRepository(dbConnection));
            CustomerView customerView = new CustomerView(new ArrayList<>());
            CustomerController customerController =
                new CustomerController(
                        customerService,
                        customerView,
                        checkoutService,
                        user == null ? 0 : user.getUserId());

            customerView.setVisible(true);
        });
    }
}