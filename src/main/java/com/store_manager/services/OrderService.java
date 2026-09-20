package com.store_manager.services;

import com.ErrorCodes;
import com.models.OrderModel;
import com.repository.IOrderRepository;
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

    public ErrorCodes UpdateOrder(OrderModel order) {
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        return orderRepo.update(order);
    }

    public ErrorCodes DeleteOrder(OrderModel order){
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        return orderRepo.deleteById(Long.valueOf(order.getId())); 
    }

    public ErrorCodes AcceptOrder(OrderModel order){
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        order.setStatus("confirmed");

        return UpdateOrder(order);
    }

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