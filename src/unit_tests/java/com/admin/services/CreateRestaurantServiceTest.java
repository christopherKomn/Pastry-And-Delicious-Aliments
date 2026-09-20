package com.admin.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @BeforeEach
    void setUp() {
        service = new CreateRestaurantService(storeManagerRepository, userRepository);

        user = new UserModel();
        user.setUsername("new_owner");
        user.setUserEmail("owner@example.com");

        restaurant = new StoreManagerModel();
        restaurant.setName("Pastry House");
        restaurant.setCity("Athens");
        restaurant.setAddress_line1("10 Baker Street");
        restaurant.setPostal_code("10558");
    }

}
