package com.customer.controllers;

import java.util.List;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.customer.services.CustomerService;
import com.customer.views.CustomerView;
import com.models.StoreManagerModel;

public class CustomerController {
    private final CustomerService service;
    private final CustomerView view;

    public CustomerController(CustomerService service, CustomerView view) {
        this.service = service;
        this.view = view;

        view.addRestaurantListener(this::handleRestaurantSelection);
        view.addRefreshListener(event -> loadRestaurants());
        loadRestaurants();
    }

    private void handleRestaurantSelection(ActionEvent event) {
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

    public CustomerService getService() {
        return service;
    }

    public CustomerView getView() {
        return view;
    }
}
