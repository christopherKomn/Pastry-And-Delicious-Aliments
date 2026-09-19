package com.customer.controllers;

import java.util.ArrayList;
import java.util.List;

import com.customer.services.CustomerCheckoutService;
import com.customer.services.CustomerDeliveryDetailsService;
import com.customer.services.CustomerPaymentMethodService;
import com.customer.views.CustomerCheckoutView;
import com.customer.views.CustomerMenuView;
import com.models.CartItem;

public class CustomerCheckoutController {
    private final CustomerCheckoutService service;
    private final CustomerDeliveryDetailsController deliveryDetailsController;
    private final CustomerPaymentMethodController paymentMethodController;
    private boolean deliveryDetailsSaved;
    private boolean paymentMethodSaved;

    public CustomerCheckoutController(CustomerCheckoutService service) {
        this.service = service;
        this.deliveryDetailsController = new CustomerDeliveryDetailsController(
            new CustomerDeliveryDetailsService());
        this.paymentMethodController = new CustomerPaymentMethodController(
            new CustomerPaymentMethodService());
    }

    public void openCheckout(CustomerMenuView menuView, List<CartItem> cart) {
        if (service.canOpenCheckout(cart)) {
            List<CartItem> cartSnapshot = new ArrayList<>(cart);
            CustomerCheckoutView checkoutView = new CustomerCheckoutView(
                    cartSnapshot,
                    service.calculateCartTotal(cartSnapshot));
                checkoutView.addDeliveryDetailsListener(
                    event -> deliveryDetailsController.openDeliveryDetails(
                            () -> {
                                deliveryDetailsSaved = true;
                                updatePlaceOrderVisibility(checkoutView);
                            }));
                checkoutView.addPaymentMethodListener(
                    event -> paymentMethodController.openPaymentMethod(
                            checkoutView,
                            () -> {
                                paymentMethodSaved = true;
                                updatePlaceOrderVisibility(checkoutView);
                            }));
            menuView.dispose();
            checkoutView.setVisible(true);
        }
    }

    private void updatePlaceOrderVisibility(CustomerCheckoutView checkoutView) {
        checkoutView.setPlaceOrderVisible(
            deliveryDetailsSaved && paymentMethodSaved);
    }
}