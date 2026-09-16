package com.store_manager.controllers;

import com.store_manager.services.OrderService;
import com.store_manager.views.OrderItemsView;
import com.store_manager.views.OrderView;
import com.store_manager.views.StoreManagerView;
public class OrderController{
    private final OrderService service;
    private final OrderView    view;
    private final OrderItemsView orderItemsview;
    private final StoreManagerView storeManagerView;

    public OrderController(
        OrderService orderService , 
        OrderView orderView ,
        OrderItemsView orderItemsView,
        StoreManagerView storeManagerView
        ){

        this.service = orderService;
        this.view  = orderView;
        this.orderItemsview = orderItemsView;
        this.storeManagerView = storeManagerView;
        InitController();
        
    }

    public OrderController(OrderController controller){
        
        this.view = controller.view;
        this.service = controller.service;
        this.orderItemsview = controller.orderItemsview;
        this.storeManagerView = controller.storeManagerView;
        InitController();
    }



    private void InitController(){

        view.addAcceptListener(event -> {

            service.AcceptOrder( view.getTheOrder() );
            view.setTheOrder(view.getTheOrder());

        });

        view.addRejectListener(event -> {
            
            service.RejectOrder( view.getTheOrder() );
            
        });

        view.addOrderItemsListener(event -> {
            view.setVisible(false);
            orderItemsview.setVisible(true);
            storeManagerView.setMainContent(orderItemsview);
        });

        
    }


}