package com.store_manager.controllers;

import com.store_manager.services.OrderService;
import com.store_manager.views.OrderView;


public class OrderController{
    private final OrderService service;
    private final OrderView    view;

    public OrderController(
        OrderService orderService , 
        OrderView orderView 
        ){

        this.service = orderService;
        this.view  = orderView;

        InitController();
        
    }

    public OrderController(OrderController controller){
        
        this.view = controller.view;
        this.service = controller.service;

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

        
    }


}