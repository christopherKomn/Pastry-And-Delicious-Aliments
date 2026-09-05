package com.admin.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ErrorCodes;
import com.models.StoreManagerModel;
import com.repository.IStoreManagerRepository;
import com.repository.IUserRepository;

@ExtendWith(MockitoExtension.class)
class ShowRestaurantServiceTest {

    @Mock
    private IStoreManagerRepository storeManagerRepository;

    @Mock
    private IUserRepository userRepository;

    private ShowRestaurantService service;
    private StoreManagerModel restaurant;

    @BeforeEach
    void setUp() {
        service = new ShowRestaurantService(storeManagerRepository, userRepository);

        restaurant = new StoreManagerModel();
        restaurant.setRestaurant_id(1);
        restaurant.setOwner_id(10);
        restaurant.setName("Test Restaurant");
        restaurant.setCity("Athens");
        restaurant.setAddress_line1("10 Test Street");
        restaurant.setPostal_code("10558");

    }

    
    @Test
    void testDeleteExistingRestaurant() {

        when(storeManagerRepository.findById(1)).thenReturn(restaurant);
        when(storeManagerRepository.deleteById(1)).thenReturn(ErrorCodes.SUCCESS);

        // Test deleting a restaurant
        ErrorCodes result = service.DeleteRestaurant(restaurant);
        // Add assertions based on the expected behavior of DeleteRestaurant
        assertEquals(ErrorCodes.SUCCESS, result);
    }

    @Test
    void testDeleteNotExistingRestaurant() {
        when(storeManagerRepository.findById(1)).thenReturn(null);
        // Test deleting a restaurant that does not exist
        ErrorCodes result = service.DeleteRestaurant(restaurant);
        assertEquals(ErrorCodes.NOT_FOUND, result);
    }

    @Test
    void testDeleteRestaurantNullParameter() {
        // Test deleting a null restaurant
        ErrorCodes result = service.DeleteRestaurant(null);
        assertEquals(ErrorCodes.NULL_VALUE, result);
    }

    @Test
    void testDeleteRestaurantWithBadBehaviorRepository() {
        when(storeManagerRepository.findById(1)).thenReturn(restaurant);
        when(storeManagerRepository.deleteById(1)).thenReturn(ErrorCodes.IO_ERROR);

        // Test deleting a restaurant that causes an IO error
        ErrorCodes result = service.DeleteRestaurant(restaurant);
        assertEquals(ErrorCodes.IO_ERROR, result);

    }

    @Test
    void testUpdateRestaurantWithExistingRestaurant() {
        when(storeManagerRepository.findById(1)).thenReturn(restaurant);
        when(storeManagerRepository.update(restaurant)).thenReturn(ErrorCodes.SUCCESS);

        // Test updating a restaurant
        ErrorCodes result = service.UpdateRestaurant(restaurant);
        assertEquals(ErrorCodes.SUCCESS, result);
    }

    @Test
    void testUpdateRestaurantWithNotExistingRestaurant() {
        when(storeManagerRepository.findById(1)).thenReturn(null);
        // Test updating a restaurant that does not exist
        ErrorCodes result = service.UpdateRestaurant(restaurant);
        assertEquals(ErrorCodes.NOT_FOUND, result); 
    }

    @Test
    void testUpdateRestaurantWithNullParameter() {
        // Test updating a null restaurant
        ErrorCodes result = service.UpdateRestaurant(null);
        assertEquals(ErrorCodes.NULL_VALUE, result);
    }

    @Test
    void testUpdateRestaurantWithBadBehaviorRepository() {
        when(storeManagerRepository.findById(1)).thenReturn(restaurant);
        when(storeManagerRepository.update(restaurant)).thenReturn(ErrorCodes.IO_ERROR);

        // Test updating a restaurant that causes an IO error
        ErrorCodes result = service.UpdateRestaurant(restaurant);
        assertEquals(ErrorCodes.IO_ERROR, result);
    }

    @Test
    void testAddChangedRestaurantWithValidRepository() {
        // mock the repository behavior if needed
        when(storeManagerRepository.findById(1)).thenReturn(restaurant);
        

        service.AddChangedRestaurant(restaurant);
        assertEquals(1, service.getChangedRestaurants().size());
        assertEquals(restaurant, service.getChangedRestaurants().get(0));
    }

    @Test
    void testAddChangedRestaurantWithNullParameter() {
        service.AddChangedRestaurant(null);
        assertEquals(0, service.getChangedRestaurants().size());
    }

    @Test
    void testAddChangedRestaurantWithDuplicateRestaurant() {
        // mock the repository behavior if needed
        when(storeManagerRepository.findById(1)).thenReturn(restaurant);
        


        service.AddChangedRestaurant(restaurant);
        assertEquals(ErrorCodes.ALREADY_EXISTS, service.AddChangedRestaurant(restaurant)); // Adding the same restaurant again
        assertEquals(1, service.getChangedRestaurants().size()); // Should still be 1
        assertEquals(restaurant, service.getChangedRestaurants().get(0));
    }

    @Test
    void testAddChangedRestaurantWithNotExistingRestaurant() {
        StoreManagerModel nonExistingRestaurant = new StoreManagerModel();
        nonExistingRestaurant.setRestaurant_id(2);
        nonExistingRestaurant.setOwner_id(20);
        nonExistingRestaurant.setName("Non-Existing Restaurant");
        nonExistingRestaurant.setCity("Athens");
        nonExistingRestaurant.setAddress_line1("20 Test Street");
        nonExistingRestaurant.setPostal_code("10558");

        when(storeManagerRepository.findById(2)).thenReturn(null);

        ErrorCodes result = service.AddChangedRestaurant(nonExistingRestaurant);
        assertEquals(ErrorCodes.NOT_FOUND, result);
        assertEquals(0, service.getChangedRestaurants().size());

    }

    @Test
    void testClearChangedRestaurants() {
        // Add a restaurant to the changed list
        when(storeManagerRepository.findById(1)).thenReturn(restaurant);
        service.AddChangedRestaurant(restaurant);
        assertEquals(1, service.getChangedRestaurants().size());

        // Clear the changed restaurants
        service.clearChangedRestaurants();
        assertEquals(0, service.getChangedRestaurants().size());

    }

    @Test
    void testRemoveChangedRestaurantWithValidRestaurant() {
        // Create a second restaurant for testing
        StoreManagerModel restaurant2 = new StoreManagerModel();
        restaurant2.setRestaurant_id(2);
        restaurant2.setOwner_id(20);
        restaurant2.setName("Test Restaurant 2");
        restaurant2.setCity("Athens");
        restaurant2.setAddress_line1("20 Test Street");
        restaurant2.setPostal_code("10558");
        
        // Add a restaurant to the changed list
        when(storeManagerRepository.findById(1)).thenReturn(restaurant);
        // Add a restaurant to the changed list
        when(storeManagerRepository.findById(2)).thenReturn(restaurant2);
        
        service.AddChangedRestaurant(restaurant);
        assertEquals(1, service.getChangedRestaurants().size());

        service.AddChangedRestaurant(restaurant2);
        assertEquals(2, service.getChangedRestaurants().size());

        // Remove the restaurant from the changed list
        ErrorCodes result = service.RemoveChangedRestaurant(restaurant);
        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals(1, service.getChangedRestaurants().size());

        assertTrue(service.getChangedRestaurants().contains(restaurant2));
        assertFalse(service.getChangedRestaurants().contains(restaurant));
    }

    @Test
    void testRemoveChangedRestaurantWithNullParameter() {

        when(storeManagerRepository.findById(1)).thenReturn(restaurant);

        // Add a restaurant to the changed list
        service.AddChangedRestaurant(restaurant);
        assertEquals(1, service.getChangedRestaurants().size());

        // Remove a null restaurant from the changed list
        ErrorCodes result = service.RemoveChangedRestaurant(null);
        assertEquals(ErrorCodes.NULL_VALUE, result);
        assertEquals(1, service.getChangedRestaurants().size());

        assertTrue(service.getChangedRestaurants().contains(restaurant));
    }

    @Test
    void testRemoveChangedRestaurantWithNotExistingRestaurant() {
        

        // with nothing in the list check if restaurant is not found
        ErrorCodes result = service.RemoveChangedRestaurant(restaurant);
        assertEquals(ErrorCodes.NOT_FOUND, result);
        assertEquals(0, service.getChangedRestaurants().size());



    }


}
