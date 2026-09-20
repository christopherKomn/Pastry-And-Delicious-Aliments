package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.MenuItemsModel;
import com.models.OrderItemsModel;
/**
 * @version 1.0
 * @brief Interface for Order Items for saving , updating , deleting or retreive specific
 * info relate to them . 
 * @note Throws happen only for programmers error's like null parameters 
 */

public interface IOrderItemsRepository {

    /**
     * @brief Finds an order item by its ID.
     * @param id The ID of the order item to find.
     * @return The OrderItemsModel object representing the order item, or null if not found.
     */
    OrderItemsModel findById(int id);

    /**
     * @brief Finds all order items in the repository.
     * @return A list of OrderItemsModel objects representing all order items or null if nothing found or io error.
     */
    List<OrderItemsModel> findAll();
    
    /**
     * @brief Finds all order items associated with a specific order ID.
     * @param orderId The ID of the order to find items for.
     * @return A list of OrderItemsModel objects representing the order items. Null
     * if nothing found or io error .
     */
    List<OrderItemsModel> findByOrderId(int orderId);
    

    /**
     * @brief Saves a new order item to the repository.
     * @param orderItem The OrderItemsModel object representing the order item to save.
     * @return An ErrorCodes object indicating the result of the operation.
     * 1. SUCCESS If saved
     * 2. ALREADY_EXISTS if this order item exist's 
     * 3. FAILED_TO_WRITE if io failed to write
     * 4. IO_ERROR for more generic io error
     * @throws IllegalArgumentException if orderItem is null
     */
    ErrorCodes save(OrderItemsModel orderItem);

    /**
     * @brief Updates an existing order item in the repository.
     * @param orderItem The OrderItemsModel object representing the order item to update.
     * @return An ErrorCodes object indicating the result of the operation.
     * 1. SUCCESS If updated .
     * 2. NOT_FOUND If not found in io
     * 3. FAILED_TO_WRITE if io failed to write
     * 4. IO_ERROR for more generic io error
     * @throws IllegalArgumentException if orderItem is null 
     */
    ErrorCodes update(OrderItemsModel orderItem);

    /**
     * @brief Deletes an order item from the repository by its ID.
     * @param id The ID of the order item to delete.
     * @return An ErrorCodes object indicating the result of the operation.
     * 1. SUCCESS if deleted .
     * 2. NOT_FOUND if there is no OrderItem with this id
     * 3. IO_ERROR If io has error (failed to delete that)
     */
    ErrorCodes deleteById(int id);

    /**
     * @brief Returns the represented menu items of these orderItems
     * @param orderItems The list of order items that we want to retreive the menu items
     * @return A list of the menu items as noted above , some of them may or may not be null
     * depends if the order item is valid or not . In the case of io error returns null instead 
     * of a list .
     */
    List<MenuItemsModel> getMenuItemsByOrderItems(List<OrderItemsModel> orderItems);

    /**
     * @brief Updates all the order items only and only if there is no a single 
     * error , otherwise returns the failed code for it .
     * @param orderItems The order items list we want to update 
     * @return A code represents if io operation happen or not and why not
     * 1. SUCCESS If succesfully updated
     * 2. FAILED_TO_WRITE If failed to write on io
     * 3. NOT_FOUND If a single Item is not found in io
     * 4. IO_ERROR If io has a more generic error
     * @throws IllegalArgumentException If orderItems is null
     */
    ErrorCodes UpdateOrderItems(List<OrderItemsModel> orderItems);

} 