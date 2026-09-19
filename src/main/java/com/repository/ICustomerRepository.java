package com.repository;

import java.util.List;

import com.ErrorCodes;
import com.models.CustomerModel;
public interface ICustomerRepository  {

    /**
     * @brief Returns based on the id the Customer data
     * @param id The id of the customer we ant to retreive from io 
     * @return CustomerModel data if founded or null if not founded 
     * or we have io error .
     */
    CustomerModel findById(int id);

    /**
     * @brief Returns all customers in the io 
     * @return a list of all existing customers in io
     * or null if none founded
     */
    List<CustomerModel> findAll();

    /**
     * @brief Saves a new customer
     * @param customer The customer data to save on io
     * @return A code that indicates if io succesfuly save the customer
     * 1. SUCCESS If saved
     * 2. ALREADY_EXISTS If customer exists
     * 3. FAILED_TO_WRITE If io failed to write customer
     * 4. IO_ERROR If we have more generic io errors
     * @throws IllegalArgumentException If customer is null
     */
    ErrorCodes save(CustomerModel customer);

    /**
     * @brief Updates an already existing customer 
     * @param customer The customer new version data
     * @return A error code that indicates if update happens or not
     * 1. SUCCESS If updated
     * 2. NOT_FOUND If there is no similar customer in io
     * 3. FAILED_TO_WRITE if io failed to write
     * 4. IO_ERROR If io has a more generic error
     * @throws IllegalArgumentException If customer is null
     */
    ErrorCodes update(CustomerModel customer);

    /**
     * @brief Deletes a customer in the io based on the id
     * @param id The id for the customer we want to delete from io
     * @return A code indicates if io operation was succesfull or not
     * 1. SUCCESS If deleted from io
     * 2. NOT_FOUND If there is no customer with this id 
     * 3. IO_ERROR If io has more generic error .
     */
    ErrorCodes deleteById(int id);
}