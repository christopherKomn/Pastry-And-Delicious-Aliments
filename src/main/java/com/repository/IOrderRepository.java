package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.CustomerModel;
import com.models.OrderItemsModel;
import com.models.OrderModel;
import com.models.StoreManagerModel;
/**
 * @brief Interface for Order Repository
 */
public interface IOrderRepository {

    /**
     * @brief Create a new order in the repository
     * @param order The order to be saved
     * @return ErrorCodes indicating the result of the operation
     */
    ErrorCodes save(OrderModel order);

    /**
     * @brief Update an existing order in the repository
     * @param order The order to be updated
     * @return ErrorCodes indicating the result of the operation
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
     */
    CustomerModel findCustomerById(Long id);

    /**
     * @brief Find a restaurant by its Order ID
     * @param id The ID of the Order to find the Restaurant
     * @return The restaurant if found, null otherwise
     */
    StoreManagerModel findRestaurantById(Long id);
    
    /**
     * @brief Delete an existing order by its ID
     * @param id The ID of the order to delete
     * @return ErrorCodes indicating the result of the operation
     */
    ErrorCodes deleteById(Long id);

    /**
     * @brief Find an order by its associated customer and restaurant
     * @param customer The customer associated with the order
     * @param restaurant The restaurant associated with the order
     * @return The order if found, null otherwise
     */
    OrderModel findByCustomerRestaurant(
        CustomerModel customer, 
        StoreManagerModel restaurant
    );

    /**
     * @brief Find all orders associated with a specific customer ID
     * @param customerId The ID of the customer to find orders for
     * @return A list of orders associated with the customer
     */
    List<OrderModel> findByCustomerId(Long customerId);

    /**
     * @brief Find all orders associated with a specific restaurant ID
     * @param restaurantId The ID of the restaurant to find orders for
     * @return A list of orders associated with the restaurant
     */
    List<OrderModel> findByRestaurantId(Long restaurantId);

    /**
     * @brief Find all order items associated with a specific order ID
     * @param orderId The ID of the order to find items for
     * @return A list of order items associated with the order
     */
    List<OrderItemsModel> findAllOrderItemsByOrderId(Long orderId);

    /**
     * @brief Find all orders in the repository
     * @return A list of all orders
     */
    List<OrderModel> findAll();


}