package com.admin.controllers;

import java.util.List;

import com.admin.services.CreateRestaurant;
import com.admin.views.CreateRestaurantView;
import com.models.StoreManagerModel;
import com.models.UserModel;
import com.ErrorCodes;

public class CreateRestaurantController {

    private final CreateRestaurantView view;
    private final CreateRestaurant service;

    public CreateRestaurantController(
            CreateRestaurantView view,
            CreateRestaurant service) {
        this.view = view;
        this.service = service;
        
        view.addShowUsersListener(event -> {
            view.clearUsers();
            List<UserModel> users = service.getAllUsers();
            for (UserModel user : users) {
                view.addUser(user);
            }
        });
        view.addCreateListener(event -> createRestaurant());
    }

    private void createRestaurant() {

        
        UserModel user = new UserModel();
        user.setUsername(view.getUsernameInput());
        user.setUserEmail(view.getEmailInput());
        user.setUserPassword(view.getPasswordInput());
        user.setUserPhone(view.getPhoneInput());
        user.setUser_type("restaurant_owner");

        StoreManagerModel restaurant = new StoreManagerModel();
        restaurant.setName(view.getRestaurantNameInput());
        restaurant.setAddress_line1(view.getAddressInput());
        restaurant.setCity(view.getCityInput());
        restaurant.setPostal_code(view.getPostalCodeInput());
        restaurant.setPhone(view.getRestaurantPhoneInput());
        restaurant.setEmail(view.getRestaurantEmailInput());
        restaurant.setCuisine_type(view.getCuisineTypeInput());

        ErrorCodes result =
                service.createRestaurant(user, restaurant);

        switch (result) {
            case SUCCESS ->
                view.showMessage("Restaurant created successfully.");

            case ALREADY_EXISTS ->
                view.showMessage("This restaurant already exists.");

            case BAD_TYPE ->
                view.showMessage("The user type is invalid.");

            case UNMATCHED_IDS ->
                view.showMessage("The owner and user IDs do not match.");

            case NOT_FOUND ->
                view.showMessage("The specified user was not found.");

            case UNKNOWN_ERROR ->
                view.showMessage("An unknown error occurred.");
        }
    }
}