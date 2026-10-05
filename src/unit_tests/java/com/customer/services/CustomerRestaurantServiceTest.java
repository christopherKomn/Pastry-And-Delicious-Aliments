package com.customer.services;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.models.StoreManagerModel;
import com.repository.IStoreManagerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerRestaurantServiceTest {

    @Mock
    private IStoreManagerRepository storeRepository;

    private CustomerRestaurantService service;

    @BeforeEach
    void setUp() {
        service = new CustomerRestaurantService(storeRepository);
    }

    @Test
    void getAllRestaurants_success() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);
        store.setName("Test Bakery");
        List<StoreManagerModel> stores = List.of(store);

        when(storeRepository.findAll()).thenReturn(stores);

        List<StoreManagerModel> result = service.getAllRestaurants();

        assertEquals(stores, result);
    }

    @Test
    void getAllRestaurants_emptyList() {
        when(storeRepository.findAll()).thenReturn(List.of());

        List<StoreManagerModel> result = service.getAllRestaurants();

        assertEquals(List.of(), result);
    }

    @Test
    void getRestaurantById_success() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);

        when(storeRepository.findById(7)).thenReturn(store);

        StoreManagerModel result = service.getRestaurantById(7);

        assertEquals(store, result);
    }

    @Test
    void getRestaurantById_notFound() {
        when(storeRepository.findById(7)).thenReturn(null);

        StoreManagerModel result = service.getRestaurantById(7);

        assertNull(result);
    }
}
