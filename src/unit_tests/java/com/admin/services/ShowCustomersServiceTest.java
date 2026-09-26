package com.admin.services;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyInt;

import com.ErrorCodes;
import com.admin.services.*;
import com.models.CustomerModel;
import com.models.UserModel;
import com.repository.ICustomerRepository;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class ShowCustomersServiceTest {

    @Mock 
    private ICustomerRepository customerRepo;

    @Mock 
    private IUserRepository userRepo;

    private ShowCustomersService service;
    private CustomerModel customer;
    private UserModel user;
    private List<CustomerModel> customers;

    @BeforeEach
    void setUp() {
        service = new ShowCustomersService(customerRepo, userRepo);

        customer = new CustomerModel(
            1 , 1 , "mitsos" , "athens" ,
            "attiki" , "11145" , "address_line1" , 
            "address_line2"
        );
        Timestamp created_at = new Timestamp(10L);

        user = new UserModel(
            1 , "email" , "phone" , "username" , "password" , "customer" , "profile" ,  created_at);
        
        customers = new ArrayList<CustomerModel>();
    }

    
    @Test
    void testGetAllCustomers_GoodBehavior() {
        
        customers.add(customer);
        when(customerRepo.findAll()).thenReturn(customers);

        assertEquals(customers , service.getAllCustomers());
        
    }

    @Test
    void testGetAllCustomers_NullBehavior() {
        
        
        when(customerRepo.findAll()).thenReturn(null);

        assertEquals(null , service.getAllCustomers());
        
    }


    @Test
    void testGetCustomerUserProfile_GoodBehavior() {
        
        
        when(customerRepo.findById(anyInt())).thenReturn(null);
        when(customerRepo.findById(customer.getId())).thenReturn(customer);

        when(userRepo.findUserById(anyInt())).thenReturn(null);
        when(userRepo.findUserById(customer.getUser_id())).thenReturn(user);

        assertEquals(user , service.getCustomerUserProfile(customer));
        
    }


    @Test
    void testGetCustomerUserProfile_BadBehavior_WithNoCustomer() {
        
        
        when(customerRepo.findById(anyInt())).thenReturn(null);

        assertEquals(null , service.getCustomerUserProfile(customer));
        
    }

}
