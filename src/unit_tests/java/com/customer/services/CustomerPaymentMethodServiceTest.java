package com.customer.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CustomerPaymentMethodServiceTest {

    private CustomerPaymentMethodService service;

    @BeforeEach
    void setUp() {
        service = new CustomerPaymentMethodService();
    }

    @Test
    void isSupported_cash() {
        assertTrue(service.isSupported("Cash"));
    }

    @Test
    void isSupported_card() {
        assertTrue(service.isSupported("Card"));
    }

    @Test
    void isSupported_null() {
        assertFalse(service.isSupported(null));
    }

    @Test
    void isSupported_emptyString() {
        assertFalse(service.isSupported(""));
    }


    @Test
    void hasSelectedPaymentMethod_selected() {
        assertTrue(service.hasSelectedPaymentMethod("Cash"));
        assertTrue(service.hasSelectedPaymentMethod("Card"));
    }

    @Test
    void hasSelectedPaymentMethod_null() {
        assertFalse(service.hasSelectedPaymentMethod(null));
    }

    @Test
    void hasSelectedPaymentMethod_emptyString() {
        assertFalse(service.hasSelectedPaymentMethod(""));
    }
}
