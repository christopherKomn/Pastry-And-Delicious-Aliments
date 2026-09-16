package com.customer.controllers;

import java.awt.event.ActionEvent;

import com.customer.services.CustomerService;
import com.customer.views.CustomerView;

public class CustomerController {
    private final CustomerService service;
    private final CustomerView view;
    private final CustomerRestaurantController restaurantController;
    private final CustomerMenuController menuController;

    public CustomerController(CustomerService service, CustomerView view) {
        this.service = service;
        this.view = view;
        this.restaurantController = new CustomerRestaurantController(service.getRestaurantService(), view);
        this.menuController = new CustomerMenuController(service.getMenuService(), service.getRestaurantService());

        view.addRestaurantListener(this::handleRestaurantSelection);
        view.addMenuListener(this::handleMenuSelection);
        view.addRefreshListener(event -> restaurantController.loadRestaurants());
        restaurantController.loadRestaurants();
    }

    private void handleRestaurantSelection(ActionEvent event) {
        restaurantController.handleRestaurantSelection(event);
    }

    private void handleMenuSelection(ActionEvent event) {
        menuController.handleMenuSelection(event, view);
    }

    public void loadRestaurants() {
        restaurantController.loadRestaurants();
    }
}
