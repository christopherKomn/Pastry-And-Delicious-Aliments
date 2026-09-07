package com.store_manager;

import java.sql.Connection;

import com.models.StoreManagerModel;
import com.models.UserModel;
import com.repository.DBMenuItemsRepository;
import com.repository.DBOrderItemsRepository;
import com.repository.DBOrderRepository;
import com.repository.DBStoreManagerRepository;
import com.repository.IMenuItemsRepository;
import com.repository.IOrderItemsRepository;
import com.repository.IOrderRepository;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;
import com.store_manager.controllers.ShowItemsController;
import com.store_manager.controllers.StoreManagerController;
import com.store_manager.services.ShowItemsService;
import com.store_manager.services.StoreManagerService;
import javax.swing.JOptionPane;
import com.store_manager.views.ShowItemsView;
import com.store_manager.views.StoreManagerInfoView;
import com.store_manager.views.StoreManagerView;

public class StoreManagerMain {
    public static void SMMain(String[] args ,Connection dbConnection , UserModel user) {
        System.out.println("Hello, Store Manager!");

        
        
        // Repositories 
        
        IStoreManagerRepository srepo =
            new DBStoreManagerRepository(dbConnection);

        IMenuItemsRepository irepo =
            new DBMenuItemsRepository(dbConnection);

        IUserRepository urepo =
            new com.repository.DBUserRepository(dbConnection);

        IOrderRepository orepo =
            new DBOrderRepository(dbConnection);

        IOrderItemsRepository oirepo =
            new DBOrderItemsRepository(dbConnection);





        // get Store manager from loggin user
        StoreManagerModel ownerStore = srepo.findByOwnerId(user.getUserId());
        if (ownerStore == null) {
            JOptionPane.showMessageDialog(null, "No store is linked to your account.");
            return;
        }




        // services
        StoreManagerService storeManagerService = new StoreManagerService(srepo, user.getUserId());
        ShowItemsService showItemsService =
            new ShowItemsService(srepo , irepo , ownerStore);

        



        // views
        StoreManagerInfoView storeManagerInfoView =
            new StoreManagerInfoView(ownerStore);

        ShowItemsView showItemsView =
            new ShowItemsView();

        StoreManagerView storeManagerView =
            new StoreManagerView();


        // controllers
        ShowItemsController showItemsController =
            new ShowItemsController(showItemsService , showItemsView);
        
        StoreManagerController storeManagerController =
            new StoreManagerController(storeManagerInfoView , showItemsView , storeManagerView, storeManagerService);

        

        storeManagerView.setVisible(true);
        

    }
}
