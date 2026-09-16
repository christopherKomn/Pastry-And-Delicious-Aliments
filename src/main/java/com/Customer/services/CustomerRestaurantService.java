package com.customer.services;

import java.util.List;

import com.models.StoreManagerModel;
import com.repository.IStoreManagerRepository;

public class CustomerRestaurantService {
    private final IStoreManagerRepository storeManagerRepository;

    public CustomerRestaurantService(IStoreManagerRepository storeManagerRepository) {
        this.storeManagerRepository = storeManagerRepository;
    }

    public List<StoreManagerModel> getAllRestaurants() {
        return storeManagerRepository.findAll();
    }

    public StoreManagerModel getRestaurantById(int restaurantId) {
        return storeManagerRepository.findById(restaurantId);
    }
}
