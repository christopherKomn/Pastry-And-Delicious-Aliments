package com.customer.services;

public class CustomerDeliveryDetailsService {
    public boolean areDetailsComplete(
            String city,
            String address,
            String postalCode,
            String contactPhone) {
        return isFilled(city)
                && isFilled(address)
                && isFilled(postalCode)
                && isFilled(contactPhone);
    }

    public boolean hasSavedDetails(
            String city,
            String address,
            String postalCode,
            String contactPhone) {
        return areDetailsComplete(city, address, postalCode, contactPhone);
    }

    private boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
