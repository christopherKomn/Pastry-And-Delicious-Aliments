package com.customer.controllers;

import com.customer.services.CustomerPaymentMethodService;
import com.customer.views.CustomerCheckoutView;
import com.customer.views.CustomerPaymentMethodView;

public class CustomerPaymentMethodController {
    private final CustomerPaymentMethodService service;
    private String selectedPaymentMethod = "";

    public CustomerPaymentMethodController(CustomerPaymentMethodService service) {
        this.service = service;
    }

    public void openPaymentMethod(CustomerCheckoutView checkoutView, Runnable onSaved) {
        CustomerPaymentMethodView view = new CustomerPaymentMethodView(selectedPaymentMethod);
        view.addPaymentMethodListener(
                event -> savePaymentMethod(event.getActionCommand(), view, checkoutView, onSaved));
        view.setVisible(true);
    }

    private void savePaymentMethod(
            String paymentMethod,
            CustomerPaymentMethodView view,
            CustomerCheckoutView checkoutView,
            Runnable onSaved) {
        if (service.isSupported(paymentMethod)) {
            selectedPaymentMethod = paymentMethod;
            checkoutView.setPaymentMethodSummary(selectedPaymentMethod);
            view.dispose();
            if (onSaved != null) {
                onSaved.run();
            }
        }
    }

}