package com.customer.services;

import java.util.List;

import com.models.StoreManagerModel;
import com.repository.IStoreManagerRepository;

public class CustomerService {
    private final IStoreManagerRepository storeManagerRepository;

    public CustomerService(IStoreManagerRepository storeManagerRepository) {
        this.storeManagerRepository = storeManagerRepository;
    }

    public List<StoreManagerModel> getAllRestaurants() {
        return storeManagerRepository.findAll();
    }

    public StoreManagerModel getRestaurantById(int restaurantId) {
        return storeManagerRepository.findById(restaurantId);
    }
}
