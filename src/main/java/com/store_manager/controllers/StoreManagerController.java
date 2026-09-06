package com.store_manager.controllers;

import com.store_manager.views.StoreManagerInfoView;
import com.store_manager.views.StoreManagerView;
import com.models.StoreManagerModel;

public class StoreManagerController {
    private final StoreManagerView storeManagerView;
    private final StoreManagerInfoView storeManagerInfoView;
    private final StoreManagerModel restaurant;

    public StoreManagerConstroller(StoreManagerModel restaurant, StoreManagerInfoView storeManagerInfoView, StoreManagerView storeManagerView) {
        this.restaurant = restaurant;
        this.storeManagerInfoView = storeManagerInfoView;
        this.storeManagerView = storeManagerView;

        // Add action listeners for the menu items
        storeManagerView.addShowItemsListener(event -> {
            storeManagerView.showPanel(storeManagerInfoView);
        });

        storeManagerView.addMainPageListener(event -> {
            storeManagerView.showPanel(storeManagerInfoView);
        });

        // Show the main page by default
        storeManagerView.showPanel(storeManagerInfoView);
    }
}