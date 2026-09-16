package com.customer.controllers;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.customer.services.CustomerCheckoutService;
import com.customer.services.CustomerMenuService;
import com.customer.services.CustomerRestaurantService;
import com.customer.views.CustomerMenuView;
import com.customer.views.CustomerView;
import com.models.CartItem;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;

public class CustomerMenuController {
    private final CustomerMenuService service;
    private final CustomerRestaurantService restaurantService;
    private final CustomerCheckoutController checkoutController;

    public CustomerMenuController(CustomerMenuService service, CustomerRestaurantService restaurantService) {
        this.service = service;
        this.restaurantService = restaurantService;
        this.checkoutController = new CustomerCheckoutController(new CustomerCheckoutService());
    }

    public void handleMenuSelection(ActionEvent event, CustomerView view) {
        int restaurantId = Integer.parseInt(event.getActionCommand());
        if (service.canOpenMenuForRestaurant(restaurantId)) {
            StoreManagerModel restaurant = service.getRestaurantById(restaurantId);
            CustomerMenuView menuView = new CustomerMenuView(restaurantId, restaurant.getName());
            List<CartItem> cart = new ArrayList<>();
            List<MenuItemsModel> products = service.getAvailableProducts(restaurantId);
            menuView.addToCartListener(
                    itemEvent -> addProductToCart(menuView, cart, products, itemEvent));
            menuView.removeFromCartListener(
                    itemEvent -> removeProductFromCart(menuView, cart, itemEvent));
                menuView.addContinueListener(
                    continueEvent -> checkoutController.openCheckout(menuView, cart));
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
        MenuItemsModel product = service.findProduct(products, itemId);
        CartItem cartItem = service.findCartItem(cart, itemId);
        String validationMessage = service.validateProductQuantity(product, cartItem, quantity);

        if (validationMessage == null) {
            int currentQuantity = cartItem == null ? 0 : cartItem.getQuantity();
            int requestedQuantity = currentQuantity + quantity;
            if (cartItem == null) {
                cart.add(new CartItem(product, quantity));
            } else {
                cartItem.setQuantity(requestedQuantity);
            }
            updateCartView(menuView, cart);
        } else {
            JOptionPane.showMessageDialog(
                menuView,
                validationMessage,
                "Quantity unavailable",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void removeProductFromCart(
            CustomerMenuView menuView,
            List<CartItem> cart,
            ActionEvent event) {
        int itemId = Integer.parseInt(event.getActionCommand());
        cart.removeIf(cartItem -> cartItem.getProduct().getItem_id() == itemId);
        updateCartView(menuView, cart);
    }

    private void updateCartView(CustomerMenuView menuView, List<CartItem> cart) {
        menuView.setCart(cart, service.calculateCartTotal(cart));
    }
}
