package com.store_manager;

import java.sql.Connection;

import com.models.UserModel;
import com.repository.DBCustomerRepository;
import com.repository.ICustomerRepository;
import com.store_manager.views.StoreManagerView;

public class StoreManagerMain {
    public static void SMMain(String[] args ,Connection dbConnection , UserModel user) {
        System.out.println("Hello, Store Manager!");
        
        // Repository
        ICustomerRepository crepo =
            new DBCustomerRepository(dbConnection);
        
        
        StoreManagerView mainView = 
            new StoreManagerView();

            mainView.setVisible(true);

            mainView.setCustomers(crepo.findAll());

            mainView.addCustomerDoubleClickListener(event -> {
                System.out.println("OK");
            });

    }
}
