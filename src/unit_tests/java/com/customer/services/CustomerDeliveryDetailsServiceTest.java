package com.customer.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CustomerDeliveryDetailsServiceTest {

    private CustomerDeliveryDetailsService service;

    @BeforeEach
    void setUp() {
        service = new CustomerDeliveryDetailsService();
    }

    @Test
    void testAreDetailsComplete_GoodBehavior() {
        assertTrue(service.areDetailsComplete(
                "Athens", "12 Baker Street", "105 58", "2101234567"));
    }

    @Test
    void testAreDetailsComplete_BadBehaviorWithMissingValues() {
        assertFalse(service.areDetailsComplete(
                null, "12 Baker Street", "105 58", "2101234567"));
        assertFalse(service.areDetailsComplete(
                "Athens", null, "105 58", "2101234567"));
        assertFalse(service.areDetailsComplete(
                "Athens", "12 Baker Street", null, "2101234567"));
        assertFalse(service.areDetailsComplete(
                "Athens", "12 Baker Street", "105 58", null));
    }

    @Test
    void testAreDetailsComplete_BadBehaviorWithBlankValues() {
        assertFalse(service.areDetailsComplete(
                "   ", "12 Baker Street", "105 58", "2101234567"));
        assertFalse(service.areDetailsComplete(
                "Athens", "  ", "105 58", "2101234567"));
        assertFalse(service.areDetailsComplete(
                "Athens", "12 Baker Street", "", "2101234567"));
        assertFalse(service.areDetailsComplete(
                "Athens", "12 Baker Street", "105 58", "\t"));
    }

    @Test
    void testHasSavedDetails_ReflectsWhetherAllDetailsAreComplete() {
        assertTrue(service.hasSavedDetails(
                "Athens", "12 Baker Street", "105 58", "2101234567"));
        assertFalse(service.hasSavedDetails(
                "Athens", "12 Baker Street", "105 58", " "));
    }
}