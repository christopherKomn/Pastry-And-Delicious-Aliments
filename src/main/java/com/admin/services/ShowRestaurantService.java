package com.admin.services;

import java.util.List;

import com.models.StoreManagerModel;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;
public class ShowRestaurantService {
    private final IStoreManagerRepository storeManagerRepository;
    private final IUserRepository userRepository;

    public enum ShowRestaurantResult {
        GOOD_RESULT,
        RESTAURANT_NOT_FOUND,
        RESTAURANT_ALREADY_EXISTS
    };


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

    public ShowRestaurantResult UpdateRestaurant(StoreManagerModel restaurant) {
        
        return (storeManagerRepository.update(restaurant) 
        != IStoreManagerRepository.ErrorType.NOT_FOUND ? 
        ShowRestaurantResult.GOOD_RESULT : ShowRestaurantResult.RESTAURANT_NOT_FOUND);
    }

    public  ShowRestaurantResult DeleteRestaurant(StoreManagerModel restaurant) {
        

        IStoreManagerRepository.ErrorType result = storeManagerRepository.deleteById(restaurant.getRestaurant_id());
        if (result != IStoreManagerRepository.ErrorType.SUCCESS) {
            return ShowRestaurantResult.RESTAURANT_NOT_FOUND;
        }
        
        return ShowRestaurantResult.GOOD_RESULT;
    }

    
}