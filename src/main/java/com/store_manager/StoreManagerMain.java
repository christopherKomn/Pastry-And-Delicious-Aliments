package com.store_manager;

import java.sql.Connection;

import javax.swing.JPanel;

import com.models.UserModel;
import com.repository.DBCustomerRepository;
import com.repository.DBMenuItemsRepository;
import com.repository.DBStoreManagerRepository;
import com.repository.ICustomerRepository;
import com.store_manager.controllers.ShowItemsController;
import com.store_manager.services.ShowItemsService;
import com.store_manager.views.ShowItemsView;
import com.store_manager.views.StoreManagerView;

public class StoreManagerMain {
    public static void SMMain(String[] args ,Connection dbConnection , UserModel user) {
        System.out.println("Hello, Store Manager!");
        
        // Repository
        ICustomerRepository crepo =
            new DBCustomerRepository(dbConnection);
        
        
        StoreManagerView mainView =
            new StoreManagerView();
        JPanel homePanel = StoreManagerView.createTestPanel();
        ShowItemsView itemsView = new ShowItemsView();
        ShowItemsService itemsService = new ShowItemsService(
                new DBStoreManagerRepository(dbConnection),
                new DBMenuItemsRepository(dbConnection), user.getUserId());
        ShowItemsController itemsController = new ShowItemsController(itemsService, itemsView);
        mainView.setMainContent(homePanel);
        mainView.addUpdateItemsListener(event -> {
            mainView.setMainContent(itemsView);
            itemsController.refreshItems();
        });
        itemsView.addBackListener(event -> mainView.setMainContent(homePanel));

        mainView.setVisible(true);

        mainView.setCustomers(crepo.findAll());

        mainView.addCustomerDoubleClickListener(event -> {
            System.out.println("OK");
            mainView.setCustomerItemStatus(mainView.getSelectedCustomer().getId(), StoreManagerView.CustomerItemStatus.NEW);
        });

    }
}
