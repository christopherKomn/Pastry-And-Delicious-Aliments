package com.customer.services;

public class CustomerPaymentMethodService {
    public boolean isSupported(String paymentMethod) {
        return "Cash".equals(paymentMethod) || "Card".equals(paymentMethod);
    }

    public boolean hasSelectedPaymentMethod(String paymentMethod) {
        return paymentMethod != null && !paymentMethod.isEmpty();
    }
}