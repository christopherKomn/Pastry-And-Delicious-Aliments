package com.customer.services;

import java.math.BigDecimal;
import java.util.List;

import com.models.CartItem;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;

public class CustomerService {
    private final IStoreManagerRepository storeManagerRepository;
    private final IMenuItemsRepository menuItemsRepository;

    public CustomerService(
            IStoreManagerRepository storeManagerRepository,
            IMenuItemsRepository menuItemsRepository) {
        this.storeManagerRepository = storeManagerRepository;
        this.menuItemsRepository = menuItemsRepository;
    }

    public List<StoreManagerModel> getAllRestaurants() {
        return storeManagerRepository.findAll();
    }

    public StoreManagerModel getRestaurantById(int restaurantId) {
        return storeManagerRepository.findById(restaurantId);
    }

    public List<MenuItemsModel> getAvailableProducts(int restaurantId) {
        return menuItemsRepository.findByRestaurantId(restaurantId).stream()
                .filter(product -> Boolean.TRUE.equals(product.getIs_available()))
                .filter(product -> product.getItem_quantity() > 0)
                .toList();
    }

    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getItem_price()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
