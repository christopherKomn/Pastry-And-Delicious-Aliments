package com.repository;
import java.util.List;

import com.ErrorCodes;
import com.models.UserModel;
/**
 * 
 * @version 1.0
 * @brief The user repository interface for accessing user model data
 * from io/DB etc.
 * @note Throws are used only for programmers errors like null parameters .  
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
     * 1. SUCCESS if user saved 
     * 2. FAILED_TO_WRITE if user is invalid 
     * 3. IO_ERROR if io have not be able to do the operation
     * @throws IllegalArgumentException if user is null .
     */
    ErrorCodes saveUser(UserModel user);

    /**
     * @brief Updates a user.
     * @param user The UserModel object representing the user to update.
     * @return An ErrorCodes object indicating the result of the operation.
     * 1. SUCCESS if user updated
     * 2. NOT_FOUND if user doesn't exists
     * 3. IO_ERROR if io have not be able to do the operation
     * @throws IllegalArgumentException if user is null .
     */
    ErrorCodes updateUser(UserModel user);

    /**
     * @brief Deletes a user by their ID.
     * @param id The ID of the user to delete.
     * @return An ErrorCodes object indicating the result of the operation.
     * 1. SUCCESS if user deleted
     * 2. NOT_FOUND if user doesn't exist's
     * 3. IO_ERROR if io have not be able to do the operation
     */
    ErrorCodes deleteUserById(int id);

    /**
     * @brief Finds a user by their username.
     * @param username The username of the user to find.
     * @return The UserModel object representing the user, or null if not found.
     * @throws IllegalArgumentException if username is null .
     */
    UserModel findByUsername(String username);

    
    /**
     * @brief Finds a user by their username and password.
     * @param username The username of the user to find.
     * @param password The password of the user to find.
     * @return The UserModel object representing the user, or null if not found.
     * @throws IllegalArgumentException if username or/and password is null .
     */
    UserModel findByUsernameAndPassword(String username, String password);
    
};