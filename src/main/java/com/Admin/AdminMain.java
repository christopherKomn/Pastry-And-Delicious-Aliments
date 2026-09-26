package com.admin;

import java.sql.Connection;

import javax.swing.SwingUtilities;

import com.admin.controllers.AdminController;
import com.admin.controllers.AdminInfoController;
import com.admin.controllers.CreateRestaurantController;
import com.admin.controllers.ShowCustomersController;
import com.admin.controllers.ShowRestaurantsController;
import com.admin.controllers.AdminEditController;
import com.admin.services.CreateRestaurantService;
import com.admin.services.ShowCustomersService;
import com.admin.services.AdminEditService;
import com.admin.services.ShowRestaurantService;
import com.admin.views.AdminInfoView;
import com.admin.views.AdminView;
import com.admin.views.CreateRestaurantView;
import com.admin.views.ShowCustomersView;
import com.admin.views.ShowRestaurantsView;
import com.admin.views.EditUserView;
import com.models.UserModel;
import com.repository.DBCustomerRepository;
import com.repository.DBStoreManagerRepository;
import com.repository.DBUserRepository;
import com.repository.ICustomerRepository;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;

public class AdminMain {
    public static void AMain(String[] args ,Connection dbConnection , UserModel user) {
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
            CreateRestaurantService service =
                    new CreateRestaurantService(storeRepository, userRepository);

            ShowRestaurantService showRestaurantService =
                    new ShowRestaurantService(storeRepository , userRepository);

            ShowCustomersService showCustomersService = 
                    new ShowCustomersService(customerRepository , userRepository);

            AdminEditService adminEditService = 
                        new AdminEditService(userRepository);

            // Views
            AdminView adminView = new AdminView();

            AdminInfoView adminInfoView = new AdminInfoView();

            ShowRestaurantsView showRestaurantsView =
                    new ShowRestaurantsView();

            CreateRestaurantView createRestaurantView =
                        new CreateRestaurantView();
            ShowCustomersView showCustomersView = 
                        new ShowCustomersView();
            
            EditUserView editUserView = 
                        new EditUserView();

            // Controller
            CreateRestaurantController createRestaurantController =
                    new CreateRestaurantController(createRestaurantView ,service );
 
            ShowRestaurantsController showRestaurantsController =
                    new ShowRestaurantsController(showRestaurantService , showRestaurantsView);

            ShowCustomersController showCustomersController = 
                    new ShowCustomersController(showCustomersService , showCustomersView);
            
            AdminInfoController adminInfoController = 
                    new AdminInfoController( adminInfoView , user );

            AdminEditController adminEditController = 
                    new AdminEditController( user , editUserView , adminEditService);

            AdminController adminController = 
            new AdminController(
                user,
                adminInfoView,
                createRestaurantView, 
                showRestaurantsView, 
                adminView, 
                showCustomersView,
                editUserView);
            

            adminView.setVisible(true);
        });

    }

    
}
