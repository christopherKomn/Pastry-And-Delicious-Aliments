package com.admin.services;

import java.util.List;

import com.models.CustomerModel;
import com.models.UserModel;
import com.repository.ICustomerRepository;
import com.repository.IUserRepository;

public class ShowCustomersService{
    private final ICustomerRepository crepo;
    private final IUserRepository urepo;

    private List<CustomerModel> allCustomers;

    public ShowCustomersService(
        ICustomerRepository customerRepo ,
        IUserRepository userRepo) {
            this.crepo = customerRepo;
            this.urepo = userRepo;
            
    }

    /**
     * @brief Returns all Customers existing in the io
     * or null if there is none 
     */
    public List<CustomerModel> getAllCustomers(){
        allCustomers = crepo.findAll();
        return allCustomers;
    }

    /**
     * @brief gets the user profile from the customer profile 
     * @param customer The Customer data 
     * @return The User Model with all data related to him if the customer
     * exist's or null otherwise .
     */
    public UserModel getCustomerUserProfile(CustomerModel customer){
        CustomerModel existingCust = this.crepo.findById(customer.getId());
        if (existingCust == null) return null;
        
        return urepo.findUserById(existingCust.getUser_id());
    }



    

}