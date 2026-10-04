package com.store_manager.controllers;

import java.util.ArrayList;
import java.util.List;

import com.models.MenuItemsModel;
import com.models.OrderItemsModel;
import com.store_manager.services.OrderItemsService;
import com.store_manager.views.OrderItemsView;
import com.store_manager.views.OrderView;
import com.store_manager.views.StoreManagerView;
public class OrderItemsController {
    private OrderItemsService service;
    private OrderItemsView view;
    private OrderView orderView;
    private StoreManagerView storeManagerView;

    public OrderItemsController(
        OrderItemsService service,
        OrderItemsView view,
        OrderView orderView,
        StoreManagerView storeManagerView
    ){
        this.service = service;
        this.view = view;
        this.orderView = orderView;
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

    }
}