package com.admin.controllers;

import java.util.List;

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
        });

        view.addShowMoreListener(event -> {
            showMore();
        });

        view.addRemoveListener(event -> {
            RemoveRestaurant();
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
        ShowRestaurantService.ShowRestaurantResult
         result = service.DeleteRestaurant(restaurant);
         

        switch (result) {
            case GOOD_RESULT ->
                view.showMessage("Restaurant deleted successfully.");

            case RESTAURANT_NOT_FOUND ->
                view.showMessage("Restaurant not found.");

        }
        RefreshRestaurantList();
    }

    public void showMore(){
        
        StoreManagerModel restaurant = view.getSelectedRestaurant();
        ShowRestaurantsView.showRestaurantDetails(view, restaurant);
        RefreshRestaurantList();
    }

    public void saveRestaurant(){
        StoreManagerModel restaurant = view.getSelectedRestaurant();
        ShowRestaurantService.ShowRestaurantResult
         result = service.UpdateRestaurant(restaurant);
        

        switch (result) {
            case GOOD_RESULT ->
                view.showMessage("Restaurant updated successfully.");

            case RESTAURANT_ALREADY_EXISTS ->
                view.showMessage("This restaurant already exists.");

            case RESTAURANT_NOT_FOUND ->
                view.showMessage("Restaurant not found.");

        }
        RefreshRestaurantList();
    }

    public void RefreshRestaurantList() {
        List<StoreManagerModel> restaurants = service.getAllRestaurants();
        view.setRestaurants(restaurants);
    }


}