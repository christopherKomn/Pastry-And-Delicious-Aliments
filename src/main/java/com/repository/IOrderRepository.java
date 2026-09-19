package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.CustomerModel;
import com.models.OrderItemsModel;
import com.models.OrderModel;
import com.models.StoreManagerModel;
/**
 * @brief Interface for Order Repository io access . 
 * @note Throws happen only for programmer errors like null parameters .
 */
public interface IOrderRepository {

    /**
     * @brief Create a new order in the repository
     * @param order The order to be saved
     * @return ErrorCodes indicating the result of the operation
     * 1. SUCCESS if the order is saved
     * 2. ALREADY_EXISTS if the order exist's 
     * 3. FAILED_TO_WRITE if io failed to save the order
     * 4. IO_ERROR if io has a more generic error
     * @throws IllegalArgumentException if order is null
     */
    ErrorCodes save(OrderModel order);

    /**
     * @brief Update an existing order in the repository
     * @param order The order to be updated
     * @return ErrorCodes indicating the result of the operation
     * 1. SUCCESS If the order is found and updated
     * 2. NOT_FOUND If the order is not found in the io
     * 3. FAILED_TO_WRITE if io failed to update the order
     * 4. IO_ERROR if io has a more generic error
     * @throws IllegalArgumentException if order is null
     */
    ErrorCodes update(OrderModel order);

    /**
     * @brief Find an order by its ID
     * @param id The ID of the order to find
     * @return The order if found, null otherwise
     */
    OrderModel findById(Long id);

    /**
     * @brief Find a customer by their Order ID
     * @param id The ID of the Order to find the Customer
     * @return The customer if found, null otherwise
     * @throws IllegalArgumentException if id is null
     */
    CustomerModel findCustomerById(Long id);

    /**
     * @brief Find a restaurant by its Order ID
     * @param id The ID of the Order to find the Restaurant
     * @return The StoreManager if found, null otherwise
     * @throws IllegalArgumentException if id is null
     */
    StoreManagerModel findRestaurantById(Long id);
    
    /**
     * @brief Delete an existing order by its ID
     * @param id The ID of the order to delete
     * @return ErrorCodes indicating the result of the operation
     * 1. SUCCESS If there is an order with this id and deleted succefully
     * 2. NOT_FOUND If the order doesn't exists in io
     * 3. IO_ERROR If the io has a generic error 
     * @throws IllegalArgumentException if id is null
     */
    ErrorCodes deleteById(Long id);

    /**
     * @brief Find an customer order by its associated customer and restaurant
     * @param customer The customer associated with the order
     * @param restaurant The restaurant associated with the order
     * @return The order if found, null otherwise
     * @throws IllegalArgumentException if customer or/and restaurant is null
     */
    OrderModel findByCustomerRestaurant(
        CustomerModel customer, 
        StoreManagerModel restaurant
    );

    /**
     * @brief Find all orders associated with a specific customer ID
     * @param customerId The ID of the customer to find orders for
     * @return A list of orders associated with the customer , or null if the
     * customer has no orders or io error .
     * @throws IllegalArgumentException if id is null
     */
    List<OrderModel> findByCustomerId(Long customerId);

    /**
     * @brief Find all orders associated with a specific restaurant ID
     * @param restaurantId The ID of the restaurant to find orders for
     * @return A list of orders associated with the restaurant or null 
     * if there is no order for it or io error .
     * @throws IllegalArgumentException if id is null
     */
    List<OrderModel> findByRestaurantId(Long restaurantId);

    /**
     * @brief Find all order items associated with a specific order ID
     * @param orderId The ID of the order to find items for
     * @return A list of order items associated with the order or null if there is
     * no order with this id or io error . 
     * @throws IllegalArgumentException if id is null
     */
    List<OrderItemsModel> findAllOrderItemsByOrderId(Long orderId);

    /**
     * @brief Find all orders in the repository
     * @return A list of all orders or null if there is no orders
     * or io error
     */
    List<OrderModel> findAll();


}