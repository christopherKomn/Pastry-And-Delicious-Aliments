package com.admin.controllers;

import com.admin.services.ShowCustomersService;
import com.models.*;
import com.repository.*;
import com.admin.views.*;

public class ShowCustomersController{
    private final ShowCustomersView view;
    private final ShowCustomersService service;

    public ShowCustomersController(
        ShowCustomersService service ,
        ShowCustomersView view
    ){
        this.view = view;
        this.service = service;

        this.view.addViewShownListener(event ->{
            this.view.setCustomers(this.service.getAllCustomers());
        });
        this.view.addRefreshListener(event -> {
            this.view.setCustomers(this.service.getAllCustomers());
        });
        this.view.addCustomerDoubleClickListener(event -> {
            CustomerModel selctCus = this.view.getSelectedCustomer();
            UserModel cusUsr = this.service.getCustomerUserProfile(selctCus);
            if (cusUsr != null)
                this.view.showCustomerDetails(cusUsr , selctCus);

        });

    }



}