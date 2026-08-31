package com.admin.services;

import com.repository.*;
import com.models.*;

import java.util.*;

public class ShowCustomersService{
    private final ICustomerRepository crepo;
    private final IUserRepository urepo;

    private List<CustomerModel> allCustomers;

    public ShowCustomersService(
        ICustomerRepository customerRepo ,
        IUserRepository userRepo) {
            this.crepo = customerRepo;
            this.urepo = userRepo;
            allCustomers = crepo.findAll();
    }


    public List<CustomerModel> getAllCustomers(){
        allCustomers = crepo.findAll();
        return allCustomers;
    }

    public UserModel getCustomerUserProfile(CustomerModel customer){
        CustomerModel existingCust = this.crepo.findById(customer.getId());
        if (existingCust == null) return null;

        return urepo.findUserById(existingCust.getUser_id());
    }



    

}