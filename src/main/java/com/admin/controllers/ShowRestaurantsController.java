package com.admin.controllers;

import java.util.List;

import com.ErrorCodes;
import com.admin.services.ShowRestaurantService;
import com.admin.views.ShowRestaurantsView;
import com.models.StoreManagerModel;
public class ShowRestaurantsController {

    private final ShowRestaurantService service;
    private  ShowRestaurantsView view;

    public ShowRestaurantsController(
        ShowRestaurantService showRestaurantService,
        ShowRestaurantsView showRestaurantsView) {
        this.service = showRestaurantService;
        this.view = showRestaurantsView;

        view.addSaveChangesListener(event -> {
            saveRestaurant();
            view.setSaveAllEnabled(service.getChangedRestaurants().isEmpty() == false);
        });

        view.addShowMoreListener(event -> {
            showMore();
            view.setSaveAllEnabled(service.getChangedRestaurants().isEmpty() == false);
        });

        view.addRemoveListener(event -> {
            RemoveRestaurant();
            view.setSaveAllEnabled(service.getChangedRestaurants().isEmpty() == false);
        });

        view.addViewShownListener(event -> {
            RefreshRestaurantList();
            service.clearChangedRestaurants();
            view.setSaveAllEnabled(service.getChangedRestaurants().isEmpty() == false);
        });

        view.addRefreshListener(event -> {
            RefreshRestaurantList();
            service.clearChangedRestaurants();
            view.setSaveAllEnabled(service.getChangedRestaurants().isEmpty() == false);
        });

        view.addSaveAllListener( event -> {
            if (service.getChangedRestaurants().isEmpty() == false){
                // to implemented in the future 
                service.saveChangedRestaurants();
                view.showMessage("All changes saved successfully.");
            }
            view.setSaveAllEnabled(service.getChangedRestaurants().isEmpty() == false);
        });

        view.addRestaurantItemChangeListener(restaurant -> {
            service.AddChangedRestaurant(restaurant);
            view.setSaveAllEnabled(service.getChangedRestaurants().isEmpty() == false);
        });
    }

    public ShowRestaurantService getService() {
        return service;
    }

    public ShowRestaurantsView getView() {
        return view;
    }
    
    public void RemoveRestaurant() {
        StoreManagerModel restaurant = view.getSelectedRestaurant();
        ErrorCodes  result = service.DeleteRestaurant(restaurant);
         

        switch (result) {
            case SUCCESS ->
                view.showMessage("Restaurant deleted successfully.");

            case NOT_FOUND ->
                view.showMessage("Restaurant not found.");

        }
        service.RemoveChangedRestaurant(restaurant); 
    }

    public void showMore(){
        
        StoreManagerModel restaurant = view.getSelectedRestaurant();
        ShowRestaurantsView.showRestaurantDetails(view, restaurant);
        
        
        service.AddChangedRestaurant(restaurant);
        
    }

    public void saveRestaurant(){
        StoreManagerModel restaurant = view.getSelectedRestaurant();
        ErrorCodes
         result = service.UpdateRestaurant(restaurant);
        

        switch (result) {
            case SUCCESS ->
                view.showMessage("Restaurant updated successfully.");

            case ALREADY_EXISTS ->
                view.showMessage("This restaurant already exists.");

            case NOT_FOUND ->
                view.showMessage("Restaurant not found.");

        }
        service.RemoveChangedRestaurant(restaurant);
    }

    public void RefreshRestaurantList() {
        List<StoreManagerModel> restaurants = service.getAllRestaurants();
        view.setRestaurants(restaurants);
    }


}
