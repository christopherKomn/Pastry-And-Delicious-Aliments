package com.admin.services;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.admin.services.CreateRestaurantService;
import com.ErrorCodes;
import com.models.StoreManagerModel;
import com.models.UserModel;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class CreateRestaurantServiceTest {

    @Mock
    private IStoreManagerRepository storeManagerRepository;

    @Mock
    private IUserRepository userRepository;

    private CreateRestaurantService service;
    private UserModel user;
    private StoreManagerModel restaurant; 

    private List<UserModel> users;
    
    @BeforeEach
    void setUp() {
        service = new CreateRestaurantService(storeManagerRepository, userRepository);
        users = new ArrayList<>();

        user = new UserModel(); 
        user.setUsername("new_owner");
        user.setUserEmail("owner@example.com");

        users.add(user);
        

        restaurant = new StoreManagerModel();
        restaurant.setName("Pastry House");
        restaurant.setCity("Athens");
        restaurant.setAddress_line1("10 Baker Street");
        restaurant.setPostal_code("105583");
    }

    @Test
    void testGetAllUsers_GoodBehavior(){

        when(userRepository.findAllUsers()).thenReturn(users);

        assertEquals( users , service.getAllUsers() ); // 
        
    }

    @Test
    void testGetAllUsers_NullResult(){

        when(userRepository.findAllUsers()).thenReturn(null);

        assertEquals( null , service.getAllUsers() ); // 
    }

    
    @Test
    void testCreateRestaurant_GoodBehavior_CreateUser(){


        
        when(userRepository.saveUser(any(UserModel.class))).
        thenReturn(ErrorCodes.ALREADY_EXISTS);
        when(userRepository.saveUser(user)).thenReturn(ErrorCodes.SUCCESS);


        when(userRepository.findUserById(anyInt())).thenReturn(null);
        when(userRepository.findUserById(user.getUserId())).thenReturn(user);

        when(userRepository.findByUsername(any(String.class))).thenReturn(null);


        when(
            storeManagerRepository.save(
                any(StoreManagerModel.class)
            )
        ).thenReturn(ErrorCodes.ALREADY_EXISTS);

        when(
            storeManagerRepository.save(
                restaurant
            )
        ).thenReturn(ErrorCodes.SUCCESS); 


        when(storeManagerRepository.findByNCAP(
            any(String.class) , any(String.class) , 
            any(String.class) , any(String.class)
        )).thenReturn(null);
        
        assertEquals(ErrorCodes.SUCCESS ,
             service.createRestaurant(user, restaurant));

        


    }

    @Test
    void testCreateRestaurant_GoodBehavior_UserExistsAndIsRestaurantOwner(){
        user.setUser_type("restaurant_owner");

        
        

        when(userRepository.updateUser(any(UserModel.class))).thenReturn(ErrorCodes.FAILED_TO_WRITE);
        when(userRepository.updateUser(user)).thenReturn(ErrorCodes.SUCCESS);

        when(userRepository.findByUsername(any(String.class))).thenReturn(null);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        when(storeManagerRepository.save(any(StoreManagerModel.class))).
        thenReturn(ErrorCodes.FAILED_TO_WRITE);
        when(storeManagerRepository.save(restaurant)).
        thenReturn(ErrorCodes.FAILED_TO_WRITE);
        

        when(storeManagerRepository.findByNCAP(
            any(String.class) , any(String.class) , 
            any(String.class) , any(String.class)
        )).thenReturn(null);
        
        assertEquals(ErrorCodes.SUCCESS ,
             service.createRestaurant(user, restaurant));

        


    }

    @Test
    void testCreateRestaurant_BadBehavior_UserExistsAndIsNotRestaurantOwner(){
        user.setUser_type("customer");
        UserModel user_copy = new UserModel(
            user.getUserId(), user.getUserEmail(),user.getUserPhone() , 
            user.getUsername(),
            user.getUserPassword(), "customer",
            user.getUser_profile_image_url(), user.getUser_created_at());
        
        

        when(userRepository.findByUsername(any(String.class))).thenReturn(null);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(user);

        

        when(storeManagerRepository.findByNCAP(
            any(String.class) , any(String.class) , 
            any(String.class) , any(String.class)
        )).thenReturn(null);
        
        assertEquals(ErrorCodes.BAD_TYPE ,
             service.createRestaurant(user_copy, restaurant));

        


    }

    @Test
    void testCreateRestaurant_BadBehavior_RestaurantExists(){
        
        

        when(storeManagerRepository.findByNCAP(
            any(String.class) , any(String.class) , 
            any(String.class) , any(String.class)
        )).thenReturn(restaurant);
        
        assertEquals(ErrorCodes.ALREADY_EXISTS ,
             service.createRestaurant(user, restaurant)); 

        


    }

}
