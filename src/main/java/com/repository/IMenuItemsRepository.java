package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.MenuItemsModel;

/**
 * @brief Defines the operations for reading and changing store products.
 */
public interface IMenuItemsRepository {

    /**
     * @brief Gets the products of a store.
     * @param restaurantId The store ID.
     * @return The products, or an empty list if none are found.
     * @throws RuntimeException if a database error occurs.
     */
    List<MenuItemsModel> findByRestaurantId(int restaurantId);

    /**
     * @brief Saves a new product in the database.
     * @param item The product to save.
     * @return The result:
     * 1. SUCCESS if the product is saved.
     * 2. NULL_VALUE if item is null.
     * 3. FAILED_TO_WRITE if one product was not inserted.
     * 4. IO_ERROR if a database error occurs.
     */
    ErrorCodes save(MenuItemsModel item);

    /**
     * @brief Deletes a product only if it is not used in any order.
     * @param itemId The product ID.
     * @param restaurantId The ID of the store that owns the product.
     * @return The result:
     * 1. SUCCESS if the product is deleted.
     * 2. FAILED_TO_WRITE if the product is missing from this store or used in an order.
     * 3. IO_ERROR if a database error occurs.
     */
    ErrorCodes deleteItem(int itemId, int restaurantId);

    /**
     * @brief Changes only the price of a product.
     * @param itemId The product ID.
     * @param restaurantId The ID of the store that owns the product.
     * @param price The new price.
     * @return The result:
     * 1. SUCCESS if the database reports one product updated.
     * 2. NOT_FOUND if the database does not report one product updated.
     * 3. IO_ERROR if a database error occurs.
     */
    ErrorCodes updatePrice(int itemId, int restaurantId, java.math.BigDecimal price);

    /**
     * @brief Changes only the stock quantity of a product.
     * @param itemId The product ID.
     * @param restaurantId The ID of the store that owns the product.
     * @param quantity The new stock quantity.
     * @return The result:
     * 1. SUCCESS if the database reports one product updated.
     * 2. NOT_FOUND if the database does not report one product updated.
     * 3. IO_ERROR if a database error occurs.
     */
    ErrorCodes updateQuantity(int itemId, int restaurantId, int quantity);

    /**
     * @brief Changes whether a product is available.
     * @param itemId The product ID.
     * @param restaurantId The ID of the store that owns the product.
     * @param available True for available, false for unavailable.
     * @return The result:
     * 1. SUCCESS if the database reports one product updated.
     * 2. NOT_FOUND if the database does not report one product updated.
     * 3. IO_ERROR if a database error occurs.
     */
    ErrorCodes updateAvailability(int itemId, int restaurantId, boolean available);
}
