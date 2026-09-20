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
        if (orderRepo == null || orderItemsRepo == null || menuItemsRepo == null){
            throw new IllegalArgumentException(
                " orderRepo/orderItemRepo/menuItemsRepo parameter is null"
            );
        }
        this.menuItemsRepo   = menuItemsRepo;
        this.orderItemsRepo = orderItemsRepo;
        this.orderRepo = orderRepo;


    }

    /**
     * @brief Returns a list of all order items if order is valid otherwise null .
     * @throws IllegalArgumentException If order is null
     */
    public List<OrderItemsModel> getAllOrderItemsFromOrder(OrderModel order){
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        return orderItemsRepo.findByOrderId(order.getId());
    } 

    /**
     * @brief Returns from this order all items of the restaurant matched
     * with the order items in the same order as the order items . 
     * @param order The Order to a store
     * @throws IllegalArgumentException If order is null
     */
    public List<MenuItemsModel> getAllMenuItemsOfOrder(OrderModel order){
        if (order == null){
            throw new IllegalArgumentException(
                " order parameter is null"
            );
        }
        List<OrderItemsModel> orderItems = orderItemsRepo.findByOrderId(order.getId());
        if (orderItems == null){

            return null;
        }
        return orderItemsRepo.getMenuItemsByOrderItems(
                orderItems 
            );
    }

    /**
     * @brief Update a list of order items only if this operation is succesfull
     * for all of them otherwise the operation is discarded .
     * @returns A code that indicates if the operation is succesfull 
     * 1. SUCCESS If everything is succesfull
     * 2. FAILED_TO_WRITE If at least one items couldn't updated
     * 3. NOT_FOUND If at least one items could not found on io 
     * 4. IO_ERROR If io has more generic error
     * @throws IllegalArgumentException If orderItems is null
     */
    public ErrorCodes UpdateOrderItems(List<OrderItemsModel> orderItems){
        if (orderItems == null){
            throw new IllegalArgumentException(
                " order items parameter is null"
            );
        }
        ErrorCodes res= orderItemsRepo.UpdateOrderItems(orderItems);
        
        return res;
    }

    /**
     * @brief Updates the order item from the order .
     * @param orderItem The Order item to update .
     * @return A code that represents if io operation happen succefully
     * 1. SUCCESS If updated .
     * 2. NOT_FOUND If orderItem could not be finded in io .
     * 3. FAILED_TO_WRITE If io failed to write the order item .
     * 4. IO_ERROR If io has a more generic error .
     * @throws IllegalArgumentException If orderItem is null
     */
    public ErrorCodes UpdateOrderItem(OrderItemsModel orderItem){
        if (orderItem == null){
            throw new IllegalArgumentException(
                " order item parameter is null"
            );
        }

        ErrorCodes res = orderItemsRepo.update(orderItem);
        if (res != ErrorCodes.SUCCESS){
            return res;
        }
        
        return ErrorCodes.SUCCESS;
    }

    /**
     * @brief Deletes the order item from the order .
     * @param orderItem The Order item to delete .
     * @return A code that represents if io operation happen succefully
     * 1. SUCCESS If deleted .
     * 2. NOT_FOUND If orderItem could not be finded in io .
     * 3. IO_ERROR If io has a more generic error .
     * @throws IllegalArgumentException If orderItem is null
     */
    public ErrorCodes DeleteOrderItem(OrderItemsModel orderItem){
        if (orderItem == null){
            throw new IllegalArgumentException(
                " order item parameter is null"
            );
        }
        ErrorCodes res = orderItemsRepo.deleteById(orderItem.getId());
        if (res != ErrorCodes.SUCCESS){
            return res;
        }

        return ErrorCodes.SUCCESS;
    }


    /**
     * @brief Creates a new order based on menu item and quantity or Updates 
     * the order that already exist's based on the quantity and menu item .
     * @param menuItem The menu item connected to this order item
     * @param OrderItemQuantity The additive quantity if equal order item already exist's
     * or the total quantity if the new order item .
     * @param order The Order that this order item will created on , basicly a way to 
     * connect the store , customer to a order item is via the order .
     * @note The order item consider equal if there is other order item with same menu item
     * id and same order id .
     * @throws  IllegalArgumentException If menuItem and/or order is null .
     */
    public ErrorCodes CreateOrderItem(
        MenuItemsModel menuItem , 
        int orderItemQuantity , 
        OrderModel order){
        
        if (order == null || menuItem == null){
            throw new IllegalArgumentException(
                " order and / or menuItem parameter is null"
            );
        }
        
        OrderItemsModel newOrderItem = new OrderItemsModel();
        newOrderItem.setMenu_item_id(menuItem.getItem_id());
        newOrderItem.setQuantity(orderItemQuantity);
        newOrderItem.setOrder_id(order.getId());
        
        
        List<OrderItemsModel> inDBOrderItems = orderItemsRepo.findByOrderId(order.getId());


        ErrorCodes res;

        boolean found = false;
        if (inDBOrderItems != null)
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
            
            res = orderItemsRepo.update(newOrderItem);
        }
           
        

        if (res != ErrorCodes.SUCCESS){
            return res;
        }
        return ErrorCodes.SUCCESS;
    }

    


}