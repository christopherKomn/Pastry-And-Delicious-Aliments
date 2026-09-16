package com.customer.controllers;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import com.customer.services.CustomerRestaurantService;
import com.customer.views.CustomerView;
import com.models.StoreManagerModel;

public class CustomerRestaurantController {
    private final CustomerRestaurantService service;
    private final CustomerView view;

    public CustomerRestaurantController(CustomerRestaurantService service, CustomerView view) {
        this.service = service;
        this.view = view;
    }

    public void handleRestaurantSelection(ActionEvent event) {
        int restaurantId = Integer.parseInt(event.getActionCommand());
        StoreManagerModel restaurant = service.getRestaurantById(restaurantId);
        if (restaurant != null) {
            view.showRestaurantDetails(restaurant);
        }
    }

    public void loadRestaurants() {
        try {
            List<StoreManagerModel> restaurants = service.getAllRestaurants();
            view.setRestaurants(restaurants);
        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    view,
                    "Could not load restaurants: " + exception.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
