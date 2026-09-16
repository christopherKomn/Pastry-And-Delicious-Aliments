package com.store_manager.services;

import java.util.List;

import com.ErrorCodes;
import com.models.MenuItemsModel;
import com.models.OrderItemsModel;
import com.models.OrderModel;
import com.repository.IMenuItemsRepository;
import com.repository.IOrderItemsRepository;
import com.repository.IOrderRepository;
/**
 * @brief Order Items Service for getting and updating existing orders
 */
public class OrderItemsService {
    private final IMenuItemsRepository menuItemsRepo;
    private final IOrderItemsRepository orderItemsRepo;
    private final IOrderRepository orderRepo;

    public OrderItemsService(
        IMenuItemsRepository menuItemsRepo,
        IOrderItemsRepository orderItemsRepo,
        IOrderRepository orderRepo
    ){

        this.menuItemsRepo   = menuItemsRepo;
        this.orderItemsRepo = orderItemsRepo;
        this.orderRepo = orderRepo;


    }

    /**
     * @brief Returns a list of all order items if order is valid otherwise null .
     */
    public List<OrderItemsModel> getAllOrderItemsFromOrder(OrderModel order){
        return orderItemsRepo.findByOrderId(order.getId());
    } 

    /**
     * @brief Returns from this order all items of the restaurant matched
     * with the order items in the same order as the order items . If 
     * no order item exists return's null .
     */
    public List<MenuItemsModel> getAllMenuItemsOfOrder(OrderModel order){
        return orderItemsRepo.getMenuItemsByOrderItems(
            orderItemsRepo.findByOrderId(order.getId())
            );
    }

    public ErrorCodes UpdateOrderItems(List<OrderItemsModel> orderItems){

        for( OrderItemsModel orderItem : orderItems){
            orderItemsRepo.update(orderItem);
        }

        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes UpdateOrderItem(OrderItemsModel orderItem){
        ErrorCodes res = orderItemsRepo.update(orderItem);
        if (res != ErrorCodes.SUCCESS)
            return res;
        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes DeleteOrderItem(OrderItemsModel orderItem){

        ErrorCodes res = orderItemsRepo.deleteById(orderItem.getId());
        if (res != ErrorCodes.SUCCESS){
            return res;
        }

        return ErrorCodes.SUCCESS;
    }

    public ErrorCodes CreateOrderItem(
        MenuItemsModel menuItem , 
        int orderItemQuantity , 
        OrderModel order){
        
        OrderItemsModel newOrderItem = new OrderItemsModel();
        newOrderItem.setMenu_item_id(menuItem.getItem_id());
        newOrderItem.setQuantity(orderItemQuantity);
        newOrderItem.setOrder_id(order.getId());

        List<OrderItemsModel> inDBOrderItems = orderItemsRepo.findByOrderId(order.getId());


        ErrorCodes res;

        boolean found = false;
        for (OrderItemsModel orderItem : inDBOrderItems){
            if (
                (newOrderItem.getOrder_id() == orderItem.getOrder_id() ) &&
                (newOrderItem.getMenu_item_id() == orderItem.getMenu_item_id())
            ){
                found = true;
                orderItem.setQuantity(orderItem.getQuantity() + newOrderItem.getQuantity());
                newOrderItem = orderItem;
            }
            
            
        }
        if (!found){
            res = orderItemsRepo.save(newOrderItem);
        }
        else{
            System.out.println("Common!!!");
            res = orderItemsRepo.update(newOrderItem);
        }
           
        

        if (res != ErrorCodes.SUCCESS){
            return res;
        }
        return ErrorCodes.SUCCESS;
    }

    


}