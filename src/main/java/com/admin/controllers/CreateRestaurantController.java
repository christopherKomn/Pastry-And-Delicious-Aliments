package com.admin.controllers;

import java.util.List;

import com.admin.services.CreateRestaurant;
import com.admin.views.CreateRestaurantView;
import com.models.StoreManagerModel;
import com.models.UserModel;

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
            List<UserModel> users = service.getUserRep().findAllUsers();
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

        CreateRestaurant.CreateRestaurantResult result =
                service.createRestaurant(user, restaurant);

        switch (result) {
            case GOOD_RESULT ->
                view.showMessage("Restaurant created successfully.");

            case RESTAURANT_ALREADY_EXISTS ->
                view.showMessage("This restaurant already exists.");

            case USER_TYPE_NOT_RESTAURANT_OWNER ->
                view.showMessage("The user type is invalid.");

            case UNMACHED_USER_ID_AND_OWNER_ID ->
                view.showMessage("The owner and user IDs do not match.");
        }
    }
}