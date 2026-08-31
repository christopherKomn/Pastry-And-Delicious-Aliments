package com.admin;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.SwingUtilities;

import com.admin.controllers.*;
import com.admin.services.*;
import com.admin.views.*;
import com.repository.*;
import com.models.*;

import java.util.List;

public class AdminMain {
    public static void AMain(String[] args ,Connection dbConnection) {
        System.out.println("Hello, Admin!");
        // check if the database connection is valid
        try {
            if (dbConnection.isClosed()) {
                System.err.println("Database connection is closed. Exiting application.");
                return;
            }
        } catch (Exception e) {
            System.err.println("Error checking database connection: " + e.getMessage());
            return;
        }
        
        

        SwingUtilities.invokeLater(() -> {
            
            // Repositories
            IUserRepository userRepository =
                    new DBUserRepository(dbConnection);

            IStoreManagerRepository storeRepository =
                    new DBStoreManagerRepository(dbConnection);

            ICustomerRepository customerRepository =
                    new DBCustomerRepository(dbConnection);

            // services
            CreateRestaurant service =
                    new CreateRestaurant(storeRepository, userRepository);

            ShowRestaurantService showRestaurantService =
                    new ShowRestaurantService(storeRepository , userRepository);

            ShowCustomersService showCustomersService = 
                    new ShowCustomersService(customerRepository , userRepository);

            // Views
            AdminView adminView = new AdminView();

            ShowRestaurantsView showRestaurantsView =
                    new ShowRestaurantsView();

            CreateRestaurantView createRestaurantView =
                        new CreateRestaurantView();
            ShowCustomersView showCustomersView = 
                        new ShowCustomersView();
            

            // Controller
            CreateRestaurantController createRestaurantController =
                    new CreateRestaurantController(createRestaurantView ,service );
 
            ShowRestaurantsController showRestaurantsController =
                    new ShowRestaurantsController(showRestaurantService , showRestaurantsView);

            ShowCustomersController showCustomersController = 
                    new ShowCustomersController(showCustomersService , showCustomersView);
            

            // Window views close listeners
            createRestaurantView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent event) {
                        // Runs after the JFrame has been disposed.
                        adminView.setVisible(true);
                    }
                });
            showRestaurantsView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent event) {
                        // Runs after the JFrame has been disposed.
                        adminView.setVisible(true);
                    }
                });
            showCustomersView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent event) {
                        // Runs after the JFrame has been disposed.
                        adminView.setVisible(true);
                    }
                });


            // admin buttons action listeners
            adminView.addCreateRestaurantListener(event -> {
                

                createRestaurantView.setVisible(true);
                adminView.setVisible(false);
            });

            adminView.addShowRestaurantsListener(event -> {
                

                showRestaurantsView.setVisible(true);
                showRestaurantsController.RefreshRestaurantList();
                adminView.setVisible(false);
            });

            
            adminView.addShowCustomersListener(event -> {
                showCustomersView.setVisible(true);
                adminView.setVisible(false);
            });

            adminView.setVisible(true);
        });

    }

    
}