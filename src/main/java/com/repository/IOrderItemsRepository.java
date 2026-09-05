package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.OrderItemsModel;

/**
 * @brief Interface for Order Items Repository
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
     * @return A list of OrderItemsModel objects representing all order items.
     */
    List<OrderItemsModel> findAll();
    
    /**
     * @brief Finds all order items associated with a specific order ID.
     * @param orderId The ID of the order to find items for.
     * @return A list of OrderItemsModel objects representing the order items.
     */
    List<OrderItemsModel> findByOrderId(int orderId);
    

    /**
     * @brief Saves a new order item to the repository.
     * @param orderItem The OrderItemsModel object representing the order item to save.
     * @return An ErrorCodes object indicating the result of the operation.
     */
    ErrorCodes save(OrderItemsModel orderItem);

    /**
     * @brief Updates an existing order item in the repository.
     * @param orderItem The OrderItemsModel object representing the order item to update.
     * @return An ErrorCodes object indicating the result of the operation.
     */
    ErrorCodes update(OrderItemsModel orderItem);

    /**
     * @brief Deletes an order item from the repository by its ID.
     * @param id The ID of the order item to delete.
     * @return An ErrorCodes object indicating the result of the operation.
     */
    ErrorCodes deleteById(int id);

     

} 