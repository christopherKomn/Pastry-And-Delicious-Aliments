package com.customer.services;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.models.CartItem;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private IStoreManagerRepository storeRepository;

    @Mock
    private IMenuItemsRepository menuItemsRepository;

    private CustomerService service;

    @BeforeEach
    void setUp() {
        service = new CustomerService(
            storeRepository, menuItemsRepository
        );
    }

    @Test
    void getAllRestaurants_success() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);
        store.setName("Test Bakery");

        List<StoreManagerModel> stores = List.of(store);

        when(storeRepository.findAll()).thenReturn(stores);

        List<StoreManagerModel> result =
            service.getAllRestaurants();

        assertEquals(stores, result);
    }

    @Test
    void getRestaurantById_success() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);

        when(storeRepository.findById(7))
            .thenReturn(store);

        StoreManagerModel result = service.getRestaurantById(7);

        assertEquals(store, result);
    }

    @Test
    void getRestaurantById_notFound() {
        when(storeRepository.findById(7))
            .thenReturn(null);

        StoreManagerModel result = service.getRestaurantById(7);

        assertNull(result);
    }

    @Test
    void getAllRestaurants_emptyList() {
        when(storeRepository.findAll()).thenReturn(List.of());

        assertEquals(List.of(), service.getAllRestaurants());
    }

    @Test
    void getAvailableProducts_onlyAvailableWithStock() {
        MenuItemsModel available = new MenuItemsModel();
        available.setItem_id(11);
        available.setIs_available(true);
        available.setItem_quantity(5);

        MenuItemsModel unavailable = new MenuItemsModel();
        unavailable.setItem_id(12);
        unavailable.setIs_available(false);
        unavailable.setItem_quantity(5);

        MenuItemsModel soldOut = new MenuItemsModel();
        soldOut.setItem_id(13);
        soldOut.setIs_available(true);
        soldOut.setItem_quantity(0);

        when(menuItemsRepository.findByRestaurantId(7))
            .thenReturn(List.of(available, unavailable, soldOut));

        List<MenuItemsModel> result = service.getAvailableProducts(7);

        assertEquals(List.of(available), result);
    }

    @Test
    void getAvailableProducts_emptyList() {
        when(menuItemsRepository.findByRestaurantId(7)).thenReturn(List.of());

        assertEquals(List.of(), service.getAvailableProducts(7));
    }

    @Test
    void calculateCartTotal_multipleProducts() {
        MenuItemsModel pastry = new MenuItemsModel();
        pastry.setItem_price(new BigDecimal("4.25"));

        MenuItemsModel coffee = new MenuItemsModel();
        coffee.setItem_price(new BigDecimal("2.50"));

        List<CartItem> cart = List.of(
            new CartItem(pastry, 2),
            new CartItem(coffee, 3)
        );

        BigDecimal result = service.calculateCartTotal(cart);

        // 2 * 4.25 + 3 * 2.50 = 16.00
        assertEquals(new BigDecimal("16.00"), result);
    }

    @Test
    void calculateCartTotal_emptyCart() {
        assertEquals(BigDecimal.ZERO, service.calculateCartTotal(List.of()));
    }

    

    

}
