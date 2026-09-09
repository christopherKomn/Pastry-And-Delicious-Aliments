package com.store_manager.services;

import com.ErrorCodes;
import com.models.OrderModel;
import com.repository.IOrderRepository;
public class OrderService {

    private final IOrderRepository orderRepo;
    
    public OrderService(IOrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public OrderService(OrderService other) {
        this.orderRepo = other.orderRepo;
    }

    public ErrorCodes UpdateOrder(OrderModel order) {
        return orderRepo.update(order);
    }

    public ErrorCodes DeleteOrder(OrderModel order){
        return orderRepo.deleteById(Long.valueOf(order.getId())); 
    }

    public ErrorCodes AcceptOrder(OrderModel order){
        order.setStatus("confirmed");

        return UpdateOrder(order);
    }

    public ErrorCodes RejectOrder(OrderModel order){
        order.setStatus("cancelled");

        return DeleteOrder(order);
    }

    


}