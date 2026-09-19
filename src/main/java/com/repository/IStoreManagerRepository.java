package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.StoreManagerModel;
/**
 * @version 1.0
 * @brief This class is responsible for quick io access the store mananger 
 * stores etc. 
 * @note findByOwnerId for moment return only the first store that matches the 
 * user id (owner id) even this owner could have multiple ones . Second throws
 * always happen for programmers error as null parameters .
 */
public interface IStoreManagerRepository  {

    

    /**
     * @brief Finds and returns the StoreManagerModel based on the id
     * @param[in] id The identifier of a store
     * @return StoreManagerModel if found matching id or null otherwise
     */
    StoreManagerModel findById(int id);

    /**
     * @brief Returns a list of all Stores or null if nothing exist's
     * or io could not retreive it
     */
    List<StoreManagerModel> findAll();

    /**
     * @brief Saves to the io the store as new one
     * @return 
     * 1. SUCCESS if saved 
     * 2. FAILED_TO_WRITE if io couldn't save this store
     * 3. ALREADY_EXISTS if this store already matches other one
     * 4. IO_ERROR if io has an error .
     * @throws IllegalArgumentException if storeManager is null
     */
    ErrorCodes save(StoreManagerModel storeManager);

    /**
     * @brief Updates a already existing one store with new values
     * @return 
     * 1. SUCCESS if updated 
     * 2. FAILED_TO_WRITE if io couldn't update this store
     * 3. NOT_FOUND if this store is not found by io .
     * 3. IO_ERROR if io has an error .
     * @throws IllegalArgumentException if storeManager is null
     */
    ErrorCodes update(StoreManagerModel storeManager);

    /**
     * @brief Deletes a already existing store with matching id
     * @return 
     * 1. SUCCESS if deleted
     * 2. NOT_FOUND if there is no store with matching id
     * 3. IO_ERROR if io has an error .
     * 
     */
    ErrorCodes deleteById(int id);

    /**
     * @brief Deletes one or more stores of the same owner , based on the 
     * ownerId match.
     * @return
     * 1. SUCCESS if deleted all of them
     * 2. NOT_FOUND if there is no store of this owner
     * 3. IO_ERROR if io has error .
     */
    ErrorCodes deleteByOwnerId(int ownerId);

    /**
     * @brief returns the first matching store of this owner with 
     * matching ids . Null if this owner hasn't any store or io error
     */
    StoreManagerModel findByOwnerId(int ownerId);

    /**
     * @brief Returns a store that matches the unique
     * identifier Name , City , AddressLine , Postal code
     * (NCAP synonim) or null if nothing retreived from io 
     * that matches it or io error .
     */
    StoreManagerModel findByNCAP(
        String name , String city, 
        String addressLine1, String postalCode);
        


}
