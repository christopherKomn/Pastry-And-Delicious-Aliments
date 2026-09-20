package com.store_manager.services;

import com.ErrorCodes;
import com.models.OrderModel;
import com.repository.IOrderRepository;
/**
 * @version 1.0
 * @brief Order Service for accepting , rejecting orders etc. 
 * @note At the moment the order in accept is just set it's status to 
 * confirmed + updates the order to io . Reject actually deletes the order 
 * from io . May later make the order in reject just set to canceled .
 */
public class OrderService {

    private final IOrderRepository orderRepo;
    
    public OrderService(IOrderRepository orderRepo) {
        if (orderRepo == null){
            throw new IllegalArgumentException(
                " orderRepo parameter is null"
            );
        }
        this.orderRepo = orderRepo;
    }

    public OrderService(OrderService other) {
        if (other == null){
            throw new IllegalArgumentException(
                " other parameter is null"
            );
        }
        this.orderRepo = other.orderRepo;
    }

    /**
     * @brief Updates the order
     * @param order The order to be updated
     * @return 
     * 1. SUCCESS if updated 
     * 2. NOT_FOUND If there is no order that match this one
     * 3. FAILED_TO_WRITE If io failed to write the order
     * 4. IO_ERROR If io has more generic error .
     * @throws IllegalArgumentException If order is null
     */
    public ErrorCodes UpdateOrder(OrderModel order) {
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        return orderRepo.update(order);
    }

    /**
     * @brief Deletes the order
     * @param order The order to be deleted
     * @return 
     * 1. SUCCESS if deleted 
     * 2. NOT_FOUND If there is no order that match this one
     * 3. IO_ERROR If io has more generic error .
     * @throws IllegalArgumentException If order is null
     */
    public ErrorCodes DeleteOrder(OrderModel order){
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        return orderRepo.deleteById(Long.valueOf(order.getId())); 
    }

    /**
     * @brief Accepts the order , basicly set it's status to confirmed and then
     * updates it .
     * @param order The order to be updated
     * @return 
     * 1. SUCCESS if updated 
     * 2. NOT_FOUND If there is no order that match this one
     * 3. FAILED_TO_WRITE If io failed to write the order
     * 4. IO_ERROR If io has more generic error .
     * @throws IllegalArgumentException If order is null
     */
    public ErrorCodes AcceptOrder(OrderModel order){
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        order.setStatus("confirmed");

        return UpdateOrder(order);
    }

    /**
     * @brief Rejects the order , basicly deletes it .
     * @param order The order to be rejected
     * @return 
     * 1. SUCCESS if deleted 
     * 2. NOT_FOUND If there is no order that match this one
     * 3. IO_ERROR If io has more generic error .
     * @throws IllegalArgumentException If order is null
     */
    public ErrorCodes RejectOrder(OrderModel order){
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        order.setStatus("cancelled");

        return DeleteOrder(order);
    }
    

    


}