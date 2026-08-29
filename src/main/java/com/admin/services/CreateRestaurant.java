package com.admin.services;
import com.models.StoreManagerModel;
import com.models.UserModel;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;

/**
 * @author Dimitris Smyrnakis and Xristoforos
 * @file CreateRestaurant.java
 * @brief This class is responsible for creating a new restaurant owner 
 * and a new user if not exist's 
 */
public class CreateRestaurant {

    public enum CreateRestaurantResult {
        GOOD_RESULT,
        RESTAURANT_ALREADY_EXISTS,
        USER_TYPE_NOT_RESTAURANT_OWNER ,
        UNMACHED_USER_ID_AND_OWNER_ID
    };


    private final IStoreManagerRepository storeManagerRep;
    private final IUserRepository userRep;

    public CreateRestaurant(
        IStoreManagerRepository storeManagerRepo , 
        IUserRepository userRepo) {
        this.storeManagerRep = storeManagerRepo;
        this.userRep = userRepo;
    }

    public final IStoreManagerRepository getStoreManagerRep() {
        return storeManagerRep;
    }

    public final IUserRepository getUserRep() {
        return userRep;
    }




    public CreateRestaurantResult createRestaurant(
        UserModel user,
        StoreManagerModel restaurant) {

        
        user.setUser_type("restaurant_owner");

        // check if restaurant exists
        StoreManagerModel existingRestaurant = 
        storeManagerRep.findByNCAP(
            restaurant.getName(), restaurant.getCity(), 
            restaurant.getAddress_line1(), restaurant.getPostal_code()
            );
        if (existingRestaurant != null) {
            return CreateRestaurantResult.RESTAURANT_ALREADY_EXISTS;
        }

        // Check if the user exists in the database
        UserModel existingUser = userRep.findByUsername(user.getUsername());


        // If user not exist's create a new user in the database
        if (existingUser == null) {
            userRep.saveUser(user);
            // take back the user mainly for id
            existingUser = userRep.findUserById(user.getUserId());
        } else {
            // If the user exists,
            // check if the user type is "restaurant_owner"
            if (!existingUser.getUser_type().equals("restaurant_owner")) {
                return CreateRestaurantResult.USER_TYPE_NOT_RESTAURANT_OWNER;
            }
            // else
            // update the user information in the database
            // so he becames a restaurant owner
            userRep.updateUser(existingUser);
            user = existingUser;
        }

        
        // match the restaurant owner id with the user id
        restaurant.setOwner_id(existingUser.getUserId());

    
        

        // save the restaurant in the database
        storeManagerRep.save(restaurant);

        return CreateRestaurantResult.GOOD_RESULT;
    }

    


}