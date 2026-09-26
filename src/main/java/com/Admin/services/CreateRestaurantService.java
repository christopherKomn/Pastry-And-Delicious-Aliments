package com.admin.services;
import java.util.List;

import com.ErrorCodes;
import com.models.StoreManagerModel;
import com.models.UserModel;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;
/**
 * @author Dimitris Smyrnakis 
 * @file CreateRestaurant.java
 * @brief This class is responsible for creating a new restaurant owner 
 * and services on it
 */
public class CreateRestaurantService {

    


    private final IStoreManagerRepository storeManagerRep;
    private final IUserRepository userRep;

    /**
     * @brief Constructor for CreateRestaurant service
     * @param storeManagerRepo The repository for managing store managers
     * @param userRepo The repository for managing users
     */
    public CreateRestaurantService(
        IStoreManagerRepository storeManagerRepo , 
        IUserRepository userRepo) {
        this.storeManagerRep = storeManagerRepo;
        this.userRep = userRepo;
    }

    /**
     * @brief Get the store manager repository
     * @return The store manager repository
     */
    public final IStoreManagerRepository getStoreManagerRep() {
        return storeManagerRep;
    }

    /**
     * @brief Get the user repository
     * @return The user repository
     */
    public final IUserRepository getUserRep() {
        return userRep;
    }

    /**
     * @brief Get all users from the user repository
     * @return A list of all users
     */
    public List<UserModel> getAllUsers(){
        return userRep.findAllUsers();
    } 



    /**
     * @brief Creates a new restaurant to a user model that is given .
     * If the user model is not existing in the io then is created and 
     * assign the restaurant otherwise if exist's then he should be a 
     * restaurant_owner type of user otherwise the restaurant can't created.
     * @param user The user model data of the new or existing user
     * @param restaurant The new Restaurant data and info .
     * @return ErrorCodes
     * 1. SUCCESS If the operation happen succesfully (the assigment)
     * 2. ALREADY_EXISTS If the store already exist's 
     * 3. BAD_TYPE If the user exist's but the type of his is not a restaurant_owner
     * 4. IO_ERROR If some io operation failed .
     * 5. FAILED_TO_WRITE If some update io operation failed .
     */
    public ErrorCodes createRestaurant(
        UserModel user,
        StoreManagerModel restaurant) {
        if (user == null || restaurant == null){
            throw new IllegalArgumentException(
                "user and / or restaurant parameter are null"
            );
        }
        
        user.setUser_type("restaurant_owner");

        // check if restaurant exists
        StoreManagerModel existingRestaurant = 
        storeManagerRep.findByNCAP(
            restaurant.getName(), restaurant.getCity(), 
            restaurant.getAddress_line1(), restaurant.getPostal_code()
            );
        if (existingRestaurant != null) {
            return ErrorCodes.ALREADY_EXISTS;
        }

        // Check if the user exists in the database
        UserModel existingUser = 
        userRep.findByUsername(user.getUsername());


        // If user not exist's create a new user in the database
        if (existingUser == null) {
            userRep.saveUser(user);
            // take back the user mainly for id
            existingUser = userRep.findUserById(user.getUserId());
        } else {
            // If the user exists,
            // check if the user type is "restaurant_owner"
            if (!existingUser.getUser_type().equals("restaurant_owner")) {
                return ErrorCodes.BAD_TYPE;
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

        return ErrorCodes.SUCCESS;
    }

    


}