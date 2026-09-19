package com.repository;

import java.util.List;

import com.models.MenuItemsModel;

public interface IMenuItemsRepository {

    /**
     * @brief Returns a list of all menu items of the store 
     * @param restaurantId The unique id of the restaurant/store
     * @return List<MenuItemsModel> : A list of menu items of the store
     * or null if the restaurantId is not valid or io error . 
     */
    List<MenuItemsModel> findByRestaurantId(int restaurantId);
    
    /**
     * @brief Saves a new item for the store to io
     * @param item The new item model to be saved in io
     * @return A code that idicates if this operation happen or not 
     * 1. SUCCESS If saved to io
     * 2. ALREADY_EXISTS If the item is already exists
     * 3. FAILED_TO_WRITE If io failed to write/save it
     * 4. IO_ERROR If io has more generic error
     * @throws IllegalArgumentException If item is null.
     */ // to be fix in future
    void save(MenuItemsModel item);
    

    boolean deleteUnusedItem(int itemId, int restaurantId);
    
    boolean updatePrice(int itemId, int restaurantId, java.math.BigDecimal price);
    
    boolean updateQuantity(int itemId, int restaurantId, int quantity);
    
    boolean updateAvailability(int itemId, int restaurantId, boolean available);


}
