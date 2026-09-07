package com.store_manager.controllers;

import com.store_manager.views.ShowItemsView;
import com.store_manager.views.StoreManagerInfoView;
import com.store_manager.views.StoreManagerView;
import com.ErrorCodes;
import com.store_manager.services.StoreManagerService;

public class StoreManagerController {
    private final StoreManagerView storeManagerView;
    private final StoreManagerInfoView storeManagerInfoView;
    private final ShowItemsView showItemsView;
    private final StoreManagerService service;

    public StoreManagerController(
        StoreManagerInfoView storeManagerInfoView, 
        ShowItemsView showItemsView,
        StoreManagerView storeManagerView, StoreManagerService service) {
        this.service = service;
        this.storeManagerInfoView = storeManagerInfoView;
        this.showItemsView = showItemsView;
        this.storeManagerView = storeManagerView;
        storeManagerView.addSelfDestructListener(event -> deleteStore());

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

    private void deleteStore() {
        if (!storeManagerView.confirmStoreDeletion()) return;
        ErrorCodes result = service.deleteStore();
        if (result == ErrorCodes.SUCCESS) {
            storeManagerView.showMessage("Your store and its products and orders have been deleted. Your account has been kept.");
            storeManagerView.dispose();
        } else if (result == ErrorCodes.NOT_FOUND) {
            storeManagerView.showMessage("No store is linked to your account.");
            storeManagerView.dispose();
        } else {
            storeManagerView.showMessage("Could not delete the store. Please try again.");
        }
    }
}
