package com.admin.services;

import java.util.List;

import com.models.StoreManagerModel;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;
import com.ErrorCodes;
public class ShowRestaurantService {
    private final IStoreManagerRepository storeManagerRepository;
    private final IUserRepository userRepository;

   


    public ShowRestaurantService(IStoreManagerRepository storeManagerRepository, IUserRepository userRepository ) {
        this.storeManagerRepository = storeManagerRepository;
        this.userRepository = userRepository;   
    }

    public StoreManagerModel getRestaurantById(int restaurantId) {
        return storeManagerRepository.findById(restaurantId);
    }

    public List<StoreManagerModel> getAllRestaurants() {
        return storeManagerRepository.findAll();
    } 

    public ErrorCodes UpdateRestaurant(StoreManagerModel restaurant) {
        
        return storeManagerRepository.update(restaurant);
    }

    public ErrorCodes DeleteRestaurant(StoreManagerModel restaurant) {
        
        if (restaurant == null) 
            return ErrorCodes.NOT_FOUND;
        
        ErrorCodes result = storeManagerRepository.deleteById(restaurant.getRestaurant_id());
        if (result != ErrorCodes.SUCCESS) {
            return result;
        }
        
        return ErrorCodes.SUCCESS;
    }

    
}