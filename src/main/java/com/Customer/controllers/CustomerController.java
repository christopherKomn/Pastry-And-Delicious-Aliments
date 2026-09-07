package com.customer.controllers;

import java.util.List;
import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import com.customer.services.CustomerService;
import com.customer.views.CustomerView;
import com.customer.views.CustomerMenuView;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomerController {
    private final CustomerService service;
    private final CustomerView view;

    public CustomerController(CustomerService service, CustomerView view) {
        this.service = service;
        this.view = view;

        view.addRestaurantListener(this::handleRestaurantSelection);
        view.addMenuListener(this::handleMenuSelection);
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

    private void handleMenuSelection(ActionEvent event) {
        int restaurantId = Integer.parseInt(event.getActionCommand());
        StoreManagerModel restaurant = service.getRestaurantById(restaurantId);
        if (restaurant != null) {
            CustomerMenuView menuView = new CustomerMenuView(restaurantId, restaurant.getName());
            Map<Integer, Integer> cart = new LinkedHashMap<>();
            menuView.addToCartListener(itemEvent -> addProductToCart(menuView, cart, itemEvent));
            menuView.setProducts(service.getAvailableProducts(restaurantId));
            menuView.setVisible(true);
        }
    }

    private void addProductToCart(
            CustomerMenuView menuView,
            Map<Integer, Integer> cart,
            ActionEvent event) {
        String[] selection = event.getActionCommand().split(":", 2);
        int itemId = Integer.parseInt(selection[0]);
        int quantity = Integer.parseInt(selection[1]);
        cart.merge(itemId, quantity, Integer::sum);
        menuView.setCart(cart);
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
