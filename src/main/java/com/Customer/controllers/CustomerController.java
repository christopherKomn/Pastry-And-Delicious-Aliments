package com.customer.controllers;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.customer.services.CustomerService;
import com.customer.views.CustomerMenuView;
import com.customer.views.CustomerView;
import com.models.CartItem;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;

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
            List<CartItem> cart = new ArrayList<>();
            List<MenuItemsModel> products = service.getAvailableProducts(restaurantId);
            menuView.addToCartListener(
                    itemEvent -> addProductToCart(menuView, cart, products, itemEvent));
            menuView.removeFromCartListener(
                    itemEvent -> removeProductFromCart(menuView, cart, products, itemEvent));
            menuView.setProducts(products);
            menuView.setVisible(true);
        }
    }

    private void addProductToCart(
            CustomerMenuView menuView,
            List<CartItem> cart,
            List<MenuItemsModel> products,
            ActionEvent event) {
        String[] selection = event.getActionCommand().split(":", 2);
        int itemId = Integer.parseInt(selection[0]);
        int quantity = Integer.parseInt(selection[1]);
        MenuItemsModel product = findProduct(products, itemId);
        CartItem cartItem = findCartItem(cart, itemId);
        int currentQuantity = cartItem == null ? 0 : cartItem.getQuantity();
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

        if (cartItem == null) {
            cart.add(new CartItem(product, quantity));
        } else {
            cartItem.setQuantity(requestedQuantity);
        }
        updateCartView(menuView, cart);
    }

    private void removeProductFromCart(
            CustomerMenuView menuView,
            List<CartItem> cart,
            List<MenuItemsModel> products,
            ActionEvent event) {
        int itemId = Integer.parseInt(event.getActionCommand());
        cart.removeIf(cartItem -> cartItem.getProduct().getItem_id() == itemId);
        updateCartView(menuView, cart);
    }

    private void updateCartView(CustomerMenuView menuView, List<CartItem> cart) {
        menuView.setCart(cart, service.calculateCartTotal(cart));
    }

    private static MenuItemsModel findProduct(List<MenuItemsModel> products, int itemId) {
        for (MenuItemsModel product : products) {
            if (product.getItem_id() == itemId) {
                return product;
            }
        }
        return null;
    }

    private static CartItem findCartItem(List<CartItem> cart, int itemId) {
        for (CartItem cartItem : cart) {
            if (cartItem.getProduct().getItem_id() == itemId) {
                return cartItem;
            }
        }
        return null;
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
