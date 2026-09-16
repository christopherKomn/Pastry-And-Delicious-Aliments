package com.store_manager.controllers;

import java.util.List;

import com.models.MenuItemsModel;
import com.models.OrderItemsModel;
import com.store_manager.services.OrderItemsService;
import com.store_manager.services.ShowItemsService;
import com.store_manager.views.AddOrderItemView;
import com.store_manager.views.OrderItemsView;
import com.store_manager.views.OrderView;
public class AddOrderItemController{
    private final OrderItemsService service;
    private final AddOrderItemView view;
    private final OrderItemsView orderItemsView;
    private final OrderView orderView;
    private final ShowItemsService showItemsService;
    public AddOrderItemController(
        OrderItemsService service ,
        AddOrderItemView view ,
        OrderView orderView,
        OrderItemsView orderItemsView,
        ShowItemsService showItemsService
    ){
        this.service = service;
        this.view = view;
        this.orderView = orderView;
        this.showItemsService = showItemsService;
        this.orderItemsView  = orderItemsView;
        InitController();
    }

    private void InitController(){
        view.addDialogShownListener(event -> {
            List<MenuItemsModel> menuItems = 
            showItemsService.getItems();
            view.setMenuItems(menuItems);
            if (menuItems.size() == 0)
                System.out.println("NOOOOOO");

        });

        view.addCreateListener(event ->{
            service.CreateOrderItem(view.getSelectedMenuItem()
            , view.getOrderItemQuantity(), orderView.getTheOrder());
            List<OrderItemsModel> orderItems =
            service.getAllOrderItemsFromOrder(orderView.getTheOrder());
            List<MenuItemsModel> menuItems = service.getAllMenuItemsOfOrder(
                orderView.getTheOrder()
            );
            orderItemsView.setOrderItems(
                orderItems, menuItems);
            view.setVisible(false);
        });

        view.addMenuItemSelectionListener(event -> {
            MenuItemsModel item = view.getSelectedMenuItem();

            if (item != null) {
                view.setQuantityLimits(1, item.getItem_quantity());
            }
        });
    }
}