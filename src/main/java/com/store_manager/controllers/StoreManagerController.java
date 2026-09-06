package com.store_manager.controllers;

import com.store_manager.views.ShowItemsView;
import com.store_manager.views.StoreManagerInfoView;
import com.store_manager.views.StoreManagerView;

public class StoreManagerController {
    private final StoreManagerView storeManagerView;
    private final StoreManagerInfoView storeManagerInfoView;
    private final ShowItemsView showItemsView;

    public StoreManagerController(
        StoreManagerInfoView storeManagerInfoView, 
        ShowItemsView showItemsView,
        StoreManagerView storeManagerView) {
        this.storeManagerInfoView = storeManagerInfoView;
        this.showItemsView = showItemsView;
        this.storeManagerView = storeManagerView;

        // Add action listeners for the menu items
        storeManagerView.addUpdateItemsListener(event -> {
            storeManagerView.setMainContent(showItemsView);
        });

        storeManagerView.addMainPageListener(event -> {
            storeManagerView.setMainContent(storeManagerInfoView);
        });

        // Show the main page by default
        storeManagerView.setMainContent(storeManagerInfoView);
    }
}