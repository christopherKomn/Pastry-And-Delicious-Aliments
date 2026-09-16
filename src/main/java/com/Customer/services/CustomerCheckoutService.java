package com.customer.services;

import java.math.BigDecimal;
import java.util.List;

import com.models.CartItem;

public class CustomerCheckoutService {
    public boolean canOpenCheckout(List<CartItem> cartItems) {
        return cartItems != null && !cartItems.isEmpty();
    }

    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::calculateLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateLineTotal(CartItem cartItem) {
        return cartItem.getProduct().getItem_price()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}