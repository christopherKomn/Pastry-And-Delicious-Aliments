package com.customer.controllers;

import java.util.ArrayList;
import java.util.List;

import com.customer.services.CustomerCheckoutService;
import com.customer.views.CustomerCheckoutView;
import com.customer.views.CustomerMenuView;
import com.models.CartItem;

public class CustomerCheckoutController {
    private final CustomerCheckoutService service;

    public CustomerCheckoutController(CustomerCheckoutService service) {
        this.service = service;
    }

    public void openCheckout(CustomerMenuView menuView, List<CartItem> cart) {
        if (cart == null || cart.isEmpty()) {
            return;
        }

        List<CartItem> cartSnapshot = new ArrayList<>(cart);
        CustomerCheckoutView checkoutView = new CustomerCheckoutView(
                cartSnapshot,
                service.calculateCartTotal(cartSnapshot));
        menuView.dispose();
        checkoutView.setVisible(true);
    }
}