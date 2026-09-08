package com.models;

public class CartItem {
    private final MenuItemsModel product;
    private int quantity;

    public CartItem(MenuItemsModel product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public MenuItemsModel getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
