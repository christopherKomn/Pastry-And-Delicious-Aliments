package com.customer.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.ErrorCodes;
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
    private final int userId;

    public CustomerCheckoutController(CustomerCheckoutService service) {
        this(service, 0);
    }

    public CustomerCheckoutController(CustomerCheckoutService service, int userId) {
        this.service = service;
        this.userId = userId;
        this.deliveryDetailsController = new CustomerDeliveryDetailsController(
            new CustomerDeliveryDetailsService());
        this.paymentMethodController = new CustomerPaymentMethodController(
            new CustomerPaymentMethodService());
    }

    public void openCheckout(CustomerMenuView menuView, List<CartItem> cart) {
        openCheckout(menuView, cart, 0);
    }

    public void openCheckout(CustomerMenuView menuView, List<CartItem> cart, int restaurantId) {
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
                        checkoutView.addPlaceOrderListener(event -> {
                            ErrorCodes result = service.placeOrder(
                                userId,
                                restaurantId,
                                cartSnapshot,
                                paymentMethodController.getSelectedPaymentMethod());
                            if (result == ErrorCodes.SUCCESS) {
                            JOptionPane.showMessageDialog(
                                checkoutView,
                                "Your order has been sent to the store.",
                                "Order placed",
                                JOptionPane.INFORMATION_MESSAGE);
                            checkoutView.dispose();
                            } else {
                            JOptionPane.showMessageDialog(
                                checkoutView,
                                result == ErrorCodes.NOT_FOUND
                                    ? "No customer profile is linked to this account."
                                    : "Could not place your order. Please try again.",
                                "Order failed",
                                JOptionPane.ERROR_MESSAGE);
                            }
                        });
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