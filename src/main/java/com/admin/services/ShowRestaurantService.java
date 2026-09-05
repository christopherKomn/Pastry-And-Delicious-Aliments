package com.admin.services;

import java.util.List;

import com.ErrorCodes;
import com.models.StoreManagerModel;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;
import java.util.ArrayList;
/**
 * @brief Service class for managing restaurant-related operations in the admin context
 * @details This class provides methods to retrieve, update, and delete restaurant information, 
 * as well as manage a list of changed or updatable restaurants. Changed restaurants are used
 * to track modifications that need to be saved or processed further. For example save multiple 
 * restaurants at once , that previusly have modified .
 */
public class ShowRestaurantService {
    private final IStoreManagerRepository storeManagerRepository;
    private final IUserRepository userRepository;
    private List<StoreManagerModel> changedRestaurants;
   


    public ShowRestaurantService(IStoreManagerRepository storeManagerRepository, IUserRepository userRepository ) {
        this.storeManagerRepository = storeManagerRepository;
        this.userRepository = userRepository;   
        this.changedRestaurants = new ArrayList<StoreManagerModel>();
    }

    /**
     * @brief Retrieves a restaurant by its ID
     * @param restaurantId The ID of the restaurant to retrieve
     * @return The restaurant if found, null otherwise
     */
    public StoreManagerModel getRestaurantById(int restaurantId) {
        return storeManagerRepository.findById(restaurantId);
    }

    /**
     * @brief Retrieves all restaurants
     * @return A list of all restaurants
     */
    public List<StoreManagerModel> getAllRestaurants() {
        return storeManagerRepository.findAll();
    } 

    /**
     * @brief Updates a restaurant
     * @param restaurant The restaurant to update
     * @return The result of the update operation
     * 1. If the restaurant updated successfully, return ErrorCodes.SUCCESS
     * 2. If the restaurant is not found, return ErrorCodes.NOT_FOUND
     * 3. If the restaurant is null, return ErrorCodes.NULL_VALUE
     * 4. Otherwise, return ErrorCodes.IO_ERROR
     */
    public ErrorCodes UpdateRestaurant(StoreManagerModel restaurant) {
        
        if (restaurant == null) 
            return ErrorCodes.NULL_VALUE;
        
        StoreManagerModel existingRestaurant = storeManagerRepository.findById(restaurant.getRestaurant_id());
        if (existingRestaurant == null) {
            return ErrorCodes.NOT_FOUND;
        }


        ErrorCodes result = storeManagerRepository.update(restaurant);
        if (result != ErrorCodes.SUCCESS) {
            return ErrorCodes.IO_ERROR;
        }
        return ErrorCodes.SUCCESS;
    }

    /**
     * @brief Deletes a restaurant
     * @param restaurant The restaurant to delete
     * @return The result of the delete operation
     * 1. If the restaurant deleted successfully, return ErrorCodes.SUCCESS
     * 2. If the restaurant is not found, return ErrorCodes.NOT_FOUND
     * 3. If the restaurant is null, return ErrorCodes.NULL_VALUE
     * 4. Otherwise, return ErrorCodes.IO_ERROR
     */
    public ErrorCodes DeleteRestaurant(StoreManagerModel restaurant) {
        
        if (restaurant == null) 
            return ErrorCodes.NULL_VALUE;
        
        StoreManagerModel existingRestaurant = storeManagerRepository.findById(restaurant.getRestaurant_id());
        if (existingRestaurant == null) {
            return ErrorCodes.NOT_FOUND;
        }

        ErrorCodes result = storeManagerRepository.deleteById(restaurant.getRestaurant_id());
        if (result != ErrorCodes.SUCCESS) {
            return ErrorCodes.IO_ERROR;
        }
        
        return ErrorCodes.SUCCESS;
    }

    /**
     * @brief Adds a restaurant to the list of changed restaurants
     * @note This methods compares the restaurant to the existing restaurants 
     * in the repository and adds it to the list of changed restaurants if it is not already present.
     * @param restaurant The restaurant to add
     * @return The result of the add operation
     * 1. If the restaurant added successfully, returns ErrorCodes.SUCCESS
     * 2. If the restaurant is not found on the repository, returns ErrorCodes.NOT_FOUND
     * 3. If the restaurant is null, returns ErrorCodes.NULL_VALUE
     * 4. If the restaurant is already in the list of changed restaurants, returns ErrorCodes.ALREADY_EXISTS
     * 
     */
    public ErrorCodes AddChangedRestaurant(StoreManagerModel restaurant) {
        
        if (restaurant == null) 
            return ErrorCodes.NULL_VALUE;
        

        StoreManagerModel existingRestaurant = storeManagerRepository.findById(restaurant.getRestaurant_id());
        if (existingRestaurant == null) 
            return ErrorCodes.NOT_FOUND;
        


        if (changedRestaurants.contains(restaurant)) 
            return ErrorCodes.ALREADY_EXISTS;
        

        changedRestaurants.add(restaurant);
        return ErrorCodes.SUCCESS;
    }

    /**
     * @brief Removes a restaurant from the list of changed restaurants
     * @param restaurant The restaurant to remove
     * @return The result of the remove operation
     * 1. If the restaurant is null, return ErrorCodes.NULL_VALUE
     * 2. If the restaurant is not found in the list of changed restaurants, return ErrorCodes.NOT_FOUND
     * 3. Otherwise, remove the restaurant and return ErrorCodes.SUCCESS
     */
    public ErrorCodes RemoveChangedRestaurant(StoreManagerModel restaurant) {
        if (restaurant == null) 
            return ErrorCodes.NULL_VALUE;

        if (!changedRestaurants.contains(restaurant)) 
            return ErrorCodes.NOT_FOUND;

        changedRestaurants.remove(restaurant);

        return ErrorCodes.SUCCESS;
    }

    /**
     * @brief Retrieves the list of changed restaurants
     * @return A list of changed restaurants
     */
    public final List<StoreManagerModel> getChangedRestaurants() {
        return changedRestaurants;
    }

    /**
     * @brief Clears the list of changed restaurants
     */
    public void clearChangedRestaurants() {
        changedRestaurants.clear();
    }

    public void saveChangedRestaurants() {
        for (StoreManagerModel restaurant : changedRestaurants) {
            storeManagerRepository.update(restaurant);
        }
        clearChangedRestaurants();
    }
    
}