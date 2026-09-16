package com.customer.services;

import java.math.BigDecimal;
import java.util.List;

import com.models.CartItem;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;

public class CustomerService {
    private final CustomerRestaurantService restaurantService;
    private final CustomerMenuService menuService;

    public CustomerService(
            IStoreManagerRepository storeManagerRepository,
            IMenuItemsRepository menuItemsRepository) {
        this.restaurantService = new CustomerRestaurantService(storeManagerRepository);
        this.menuService = new CustomerMenuService(storeManagerRepository, menuItemsRepository);
    }

    public CustomerRestaurantService getRestaurantService() {
        return restaurantService;
    }

    public CustomerMenuService getMenuService() {
        return menuService;
    }

    public List<StoreManagerModel> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }

    public StoreManagerModel getRestaurantById(int restaurantId) {
        return restaurantService.getRestaurantById(restaurantId);
    }

    public List<MenuItemsModel> getAvailableProducts(int restaurantId) {
        return menuService.getAvailableProducts(restaurantId);
    }

    public BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        return menuService.calculateCartTotal(cartItems);
    }
}

