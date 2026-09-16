package com.customer.services;

import java.math.BigDecimal;
import java.util.List;

import com.models.CartItem;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;

public class CustomerMenuService {
    private final IStoreManagerRepository storeManagerRepository;
    private final IMenuItemsRepository menuItemsRepository;

    public CustomerMenuService(
            IStoreManagerRepository storeManagerRepository,
            IMenuItemsRepository menuItemsRepository) {
        this.storeManagerRepository = storeManagerRepository;
        this.menuItemsRepository = menuItemsRepository;
    }

    public StoreManagerModel getRestaurantById(int restaurantId) {
        return storeManagerRepository.findById(restaurantId);
    }

    public boolean canOpenMenuForRestaurant(int restaurantId) {
        return getRestaurantById(restaurantId) != null;
    }

    public List<MenuItemsModel> getAvailableProducts(int restaurantId) {
        return menuItemsRepository.findByRestaurantId(restaurantId).stream()
                .filter(product -> Boolean.TRUE.equals(product.getIs_available()))
                .filter(product -> product.getItem_quantity() > 0)
                .toList();
    }

    public String validateProductQuantity(MenuItemsModel product, CartItem cartItem, int quantity) {
        if (product == null) {
            return "Product not found.";
        }

        int currentQuantity = cartItem == null ? 0 : cartItem.getQuantity();
        int requestedQuantity = currentQuantity + quantity;
        if (requestedQuantity > product.getItem_quantity()) {
            int availableQuantity = product.getItem_quantity() - currentQuantity;
            return "Only " + Math.max(availableQuantity, 0)
                    + " more unit(s) of this product are available.";
        }
        return null;
    }

    public MenuItemsModel findProduct(List<MenuItemsModel> products, int itemId) {
        for (MenuItemsModel product : products) {
            if (product.getItem_id() == itemId) {
                return product;
            }
        }
        return null;
    }

    public CartItem findCartItem(List<CartItem> cart, int itemId) {
        for (CartItem cartItem : cart) {
            if (cartItem.getProduct().getItem_id() == itemId) {
                return cartItem;
            }
        }
        return null;
    }

    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getItem_price()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
