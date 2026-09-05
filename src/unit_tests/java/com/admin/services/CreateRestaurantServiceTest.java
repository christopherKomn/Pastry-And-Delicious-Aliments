package com.admin.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ErrorCodes;
import static com.ErrorCodes.ALREADY_EXISTS;
import static com.ErrorCodes.BAD_TYPE;
import static com.ErrorCodes.SUCCESS;
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

    @Test
    void returnsRestaurantAlreadyExistsAndStopsWhenRestaurantIsFound() {
        when(storeManagerRepository.findByNCAP(
                "Pastry House", "Athens", "10 Baker Street", "10558"))
                .thenReturn(new StoreManagerModel());

        ErrorCodes result =
                service.createRestaurant(user, restaurant);

        assertEquals(ALREADY_EXISTS, result);
        verifyNoInteractions(userRepository);
        verify(storeManagerRepository, never()).save(restaurant);
    }

    @Test
    void rejectsExistingUserWhoIsNotARestaurantOwner() {
        UserModel existingUser = new UserModel();
        existingUser.setUsername("new_owner");
        existingUser.setUser_type("CUSTOMER");

        when(userRepository.findByUsername("new_owner")).thenReturn(existingUser);

        ErrorCodes result =
                service.createRestaurant(user, restaurant);

        assertEquals(BAD_TYPE, result);
        verify(userRepository, never()).updateUser(existingUser);
        verify(storeManagerRepository, never()).save(restaurant);
    }

    @Test
    void createsNewUserAndUsesGeneratedUserIdAsRestaurantOwnerId() {
        when(userRepository.findByUsername("new_owner")).thenReturn(null);
        doAnswer(invocation -> {
            UserModel savedUser = invocation.getArgument(0);
            savedUser.setUserId(42);
            return ErrorCodes.SUCCESS;
        }).when(userRepository).saveUser(user);
        when(userRepository.findUserById(42)).thenReturn(user);

        ErrorCodes result =
                service.createRestaurant(user, restaurant);

        assertEquals(SUCCESS, result);
        assertEquals("restaurant_owner", user.getUser_type());
        assertEquals(42, restaurant.getOwner_id());
        verify(userRepository).saveUser(user);
        verify(userRepository).findUserById(42);
        verify(storeManagerRepository).save(restaurant);
    }

    @Test
    void updatesExistingRestaurantOwnerAndCreatesRestaurantForThatOwner() {
        UserModel existingOwner = new UserModel();
        existingOwner.setUserId(84);
        existingOwner.setUsername("new_owner");
        existingOwner.setUser_type("restaurant_owner");

        when(userRepository.findByUsername("new_owner")).thenReturn(existingOwner);

        ErrorCodes result =
                service.createRestaurant(user, restaurant);

        assertEquals(SUCCESS, result);
        assertEquals(84, restaurant.getOwner_id());
        verify(userRepository).updateUser(existingOwner);
        verify(userRepository, never()).saveUser(user);
        verify(storeManagerRepository).save(restaurant);
    }
}
