package com.store_manager;

import java.sql.Connection;

import com.models.StoreManagerModel;
import com.models.UserModel;
import com.repository.DBMenuItemsRepository;
import com.repository.DBStoreManagerRepository;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;
import com.store_manager.services.ShowItemsService;

public class StoreManagerMain {
    public static void SMMain(String[] args ,Connection dbConnection , UserModel user) {
        System.out.println("Hello, Store Manager!");

        
        
        // Repositories 
        
        IStoreManagerRepository srepo =
            new DBStoreManagerRepository(dbConnection);

        IMenuItemsRepository irepo =
            new DBMenuItemsRepository(dbConnection);




        // get Store manager from loggin user
        StoreManagerModel ownerStore = srepo.findByOwnerId(user.getUserId());




        // services
        ShowItemsService showItemsService =
            new ShowItemsService(srepo , irepo , ownerStore);



        // views

        
        

    }
}
