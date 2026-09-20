package com.store_manager.controllers;

import java.util.ArrayList;
import java.util.List;

import com.models.MenuItemsModel;
import com.models.OrderItemsModel;
import com.store_manager.services.OrderItemsService;
import com.store_manager.views.AddOrderItemView;
import com.store_manager.views.OrderItemsView;
import com.store_manager.views.OrderView;
import com.store_manager.views.StoreManagerView;
public class OrderItemsController {
    private OrderItemsService service;
    private OrderItemsView view;
    private OrderView orderView;
    private StoreManagerView storeManagerView;
    private AddOrderItemView addOrderItemView;

    public OrderItemsController(
        OrderItemsService service,
        OrderItemsView view,
        OrderView orderView,
        AddOrderItemView addOrderItemView,
        StoreManagerView storeManagerView
    ){
        this.service = service;
        this.view = view;
        this.orderView = orderView;
        this.addOrderItemView = addOrderItemView;
        this.storeManagerView = storeManagerView;
        InitController();
    }

    private void InitController(){
        view.addViewShownListener(event -> {
            List<OrderItemsModel> orderItems = 
            service.getAllOrderItemsFromOrder(orderView.getTheOrder());
            List<MenuItemsModel> menuItems = 
            service.getAllMenuItemsOfOrder(orderView.getTheOrder());
            if (orderItems != null){
                view.setOrderItems(
                orderItems, 
                menuItems
                ); 
            }
            else {
                view.setOrderItems(
                new ArrayList<OrderItemsModel>(), 
                new ArrayList<MenuItemsModel>()
                );
            }
            
        });

        view.addBackListener(event -> {
            orderView.setVisible(true);
            storeManagerView.setMainContent(orderView);
            view.setVisible(false);
        });

        view.addOrderItemQuantityChangeListener(event ->{
            service.UpdateOrderItem(view.getSelectedOrderItem());
        });

        view.addAddListener(event -> {
            addOrderItemView.setLocationRelativeTo(view);
            addOrderItemView.setVisible(true);
        });

        view.addRemoveListener(event -> {
            service.DeleteOrderItem(view.getSelectedOrderItem());
            List<OrderItemsModel> orderItems = view.getOrderItems();
            List<MenuItemsModel> mappedMenuItems = view.getMenuItems();
            int index = orderItems.indexOf(view.getSelectedOrderItem());
            if (index >= 0){
                orderItems.remove(index);
                mappedMenuItems.remove(index);
            }
            view.setOrderItems(orderItems, mappedMenuItems);
        });

        
        
    }
}