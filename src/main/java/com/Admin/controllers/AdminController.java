package com.admin.controllers;

import com.admin.views.AdminView;
import com.admin.views.CreateRestaurantView;
import com.admin.views.ShowCustomersView;
import com.admin.views.*;;
import com.models.UserModel;
public class AdminController {
    private final CreateRestaurantView createRestaurantView;
    private final ShowRestaurantsView showRestaurantsView;
    private final AdminView adminView;
    private final ShowCustomersView showCustomersView;
    private final AdminInfoView adminInfoView;
    private final UserModel user;
    public AdminController(
        UserModel user, AdminInfoView adminInfoView, CreateRestaurantView createRestaurantView, ShowRestaurantsView showRestaurantsView, AdminView adminView, ShowCustomersView showCustomersView) {
        this.user = user;
        this.adminInfoView = adminInfoView;
        this.createRestaurantView = createRestaurantView;
        this.showRestaurantsView = showRestaurantsView;
        this.adminView = adminView;
        this.showCustomersView = showCustomersView;

        // Admin menu action listeners
            adminView.addCreateRestaurantListener(event -> {
                adminView.showPanel(createRestaurantView);
            });

            adminView.addShowRestaurantsListener(event -> {
                //showRestaurantsController.RefreshRestaurantList();
                adminView.showPanel(showRestaurantsView);
            });

            adminView.addShowCustomersListener(event -> {
                adminView.showPanel(showCustomersView);
            });

            adminView.addMainPageListener(event -> {
                adminView.showPanel(adminInfoView);
            });

            adminView.showPanel(adminInfoView);

    }

}