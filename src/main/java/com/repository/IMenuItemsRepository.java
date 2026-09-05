package com.repository;

import java.util.List;
import com.models.MenuItemsModel;

public interface IMenuItemsRepository {
    List<MenuItemsModel> findByRestaurantId(int restaurantId);
    void save(MenuItemsModel item);
    boolean deleteUnusedItem(int itemId, int restaurantId);
    boolean updatePrice(int itemId, int restaurantId, java.math.BigDecimal price);
    boolean updateQuantity(int itemId, int restaurantId, int quantity);
    boolean updateAvailability(int itemId, int restaurantId, boolean available);
}
