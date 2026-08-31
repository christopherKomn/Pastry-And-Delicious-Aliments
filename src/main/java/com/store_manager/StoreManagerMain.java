package com.store_manager;

import java.sql.Connection;
import com.models.*;
import com.repository.*;
import com.store_manager.views.*;

public class StoreManagerMain {
    public static void SMMain(String[] args ,Connection dbConnection) {
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