package com.store_manager;

import java.sql.Connection;

import javax.swing.JPanel;

import com.models.*;
import com.repository.*;

import com.store_manager.controllers.*;
import com.store_manager.services.*;
import com.store_manager.views.*;

public class StoreManagerMain {
    public static void SMMain(String[] args ,Connection dbConnection , UserModel user) {
        System.out.println("Hello, Store Manager!");
        
        // Repositories 
        
        IStoreManagerRepository srepo =
            new DBStoreManagerRepository(dbConnection);

        IMenuItemsRepository irepo =
            new DBMenuItemsRepository(dbConnection);

        // services
        ShowItemsService showItemsService =
            new ShowItemsService(srepo , irepo);

        
        

    }
}
