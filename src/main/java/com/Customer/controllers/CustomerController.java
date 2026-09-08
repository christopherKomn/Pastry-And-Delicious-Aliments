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
            List<MenuItemsModel> products = service.getAvailableProducts(restaurantId);
            Map<Integer, MenuItemsModel> productsById = new LinkedHashMap<>();
            for (MenuItemsModel product : products) {
                productsById.put(product.getItem_id(), product);
            }
            menuView.addToCartListener(
                    itemEvent -> addProductToCart(menuView, cart, productsById, itemEvent));
                menuView.removeFromCartListener(
                    itemEvent -> removeProductFromCart(menuView, cart, itemEvent));
            menuView.setProducts(products);
            menuView.setVisible(true);
        }
    }

    private void addProductToCart(
            CustomerMenuView menuView,
            Map<Integer, Integer> cart,
            Map<Integer, MenuItemsModel> productsById,
            ActionEvent event) {
        String[] selection = event.getActionCommand().split(":", 2);
        int itemId = Integer.parseInt(selection[0]);
        int quantity = Integer.parseInt(selection[1]);
        MenuItemsModel product = productsById.get(itemId);
        int currentQuantity = cart.getOrDefault(itemId, 0);
        int requestedQuantity = currentQuantity + quantity;

        if (product == null || requestedQuantity > product.getItem_quantity()) {
            int availableQuantity = product == null
                ? 0
                : product.getItem_quantity() - currentQuantity;
            JOptionPane.showMessageDialog(
                menuView,
                "Only " + Math.max(availableQuantity, 0)
                    + " more unit(s) of this product are available.",
                "Quantity unavailable",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        cart.put(itemId, requestedQuantity);
        menuView.setCart(cart);
    }

    private void removeProductFromCart(
            CustomerMenuView menuView,
            Map<Integer, Integer> cart,
            ActionEvent event) {
        cart.remove(Integer.parseInt(event.getActionCommand()));
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
