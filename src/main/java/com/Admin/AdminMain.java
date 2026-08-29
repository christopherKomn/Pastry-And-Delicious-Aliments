package com.admin;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.SwingUtilities;

import com.admin.controllers.CreateRestaurantController;
import com.admin.controllers.ShowRestaurantsController;
import com.admin.services.CreateRestaurant;
import com.admin.services.ShowRestaurantService;
import com.admin.views.AdminView;
import com.admin.views.CreateRestaurantView;
import com.admin.views.ShowRestaurantsView;
import com.repository.DBStoreManagerRepository;
import com.repository.DBUserRepository;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;

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
            

            IUserRepository userRepository =
                    new DBUserRepository(dbConnection);

            IStoreManagerRepository storeRepository =
                    new DBStoreManagerRepository(dbConnection);


            CreateRestaurant service =
                    new CreateRestaurant(storeRepository, userRepository);

            ShowRestaurantService showRestaurantService =
                    new ShowRestaurantService(storeRepository , userRepository);



            AdminView adminView = new AdminView();

            ShowRestaurantsView showRestaurantsView =
                    new ShowRestaurantsView();

            CreateRestaurantView createRestaurantView =
                        new CreateRestaurantView();


            CreateRestaurantController createRestaurantController =
                    new CreateRestaurantController(createRestaurantView ,service );
 
            ShowRestaurantsController showRestaurantsController =
                    new ShowRestaurantsController(showRestaurantService , showRestaurantsView);

            

            
            createRestaurantView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent event) {
                        // Runs after the JFrame has been disposed.
                        adminView.setVisible(true);
                    }
                });
            adminView.addCreateRestaurantListener(event -> {
                

                createRestaurantView.setVisible(true);
                
                adminView.setVisible(false);
            });

            showRestaurantsView.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent event) {
                        // Runs after the JFrame has been disposed.
                        adminView.setVisible(true);
                    }
                });
            adminView.addShowRestaurantsListener(event -> {
                

                showRestaurantsView.setVisible(true);
                showRestaurantsController.RefreshRestaurantList();
                adminView.setVisible(false);
            });

            adminView.setVisible(true);
        });

    }

    
}