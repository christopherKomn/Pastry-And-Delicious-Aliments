package com.customer.services;

import java.util.List;

import com.models.StoreManagerModel;
import com.models.MenuItemsModel;
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
        return menuItemsRepository.findByRestaurantId(restaurantId);
    }
}
