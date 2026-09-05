package com.repository;
import java.util.List;

import com.ErrorCodes;
import com.models.UserModel;
/**
 * 
 * @version 1.0
 * @brief The user repository interface for accessing user model data
 * from io . 
 */

public interface IUserRepository {

    /**
     * @brief Finds a user by their ID.
     * @param id The ID of the user to find.
     * @return The UserModel object representing the user, or null if not found.
     */
    UserModel findUserById(int id);

    /**
     * @brief Finds all users.
     * @return A list of UserModel objects representing the users.
     */
    List<UserModel> findAllUsers();

    /**
     * @brief Saves a user.
     * @param user The UserModel object representing the user to save.
     * @return An ErrorCodes object indicating the result of the operation.
     */
    ErrorCodes saveUser(UserModel user);

    /**
     * @brief Updates a user.
     * @param user The UserModel object representing the user to update.
     * @return An ErrorCodes object indicating the result of the operation.
     */
    ErrorCodes updateUser(UserModel user);

    /**
     * @brief Deletes a user by their ID.
     * @param id The ID of the user to delete.
     * @return An ErrorCodes object indicating the result of the operation.
     */
    ErrorCodes deleteUserById(int id);

    /**
     * @brief Finds a user by their username.
     * @param username The username of the user to find.
     * @return The UserModel object representing the user, or null if not found.
     */
    UserModel findByUsername(String username);

    
    /**
     * @brief Finds a user by their username and password.
     * @param username The username of the user to find.
     * @param password The password of the user to find.
     * @return The UserModel object representing the user, or null if not found.
     */
    UserModel findByUsernameAndPassword(String username, String password);
    
};