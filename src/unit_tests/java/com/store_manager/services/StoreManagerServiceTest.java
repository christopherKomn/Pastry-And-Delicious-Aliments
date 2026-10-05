package com.store_manager.services;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ErrorCodes;
import com.models.CustomerModel;
import com.models.OrderModel;
import com.models.StoreManagerModel;
import com.repository.ICustomerRepository;
import com.repository.IOrderRepository;
import com.repository.IStoreManagerRepository;

@ExtendWith(MockitoExtension.class)
class StoreManagerServiceTest {

    @Mock
    private IStoreManagerRepository storeRepository;

    @Mock
    private IOrderRepository orderRepository;

    @Mock
    private ICustomerRepository customerRepository;

    private StoreManagerService service;
    private StoreManagerModel store;

    @BeforeEach
    void setUp() {
        service = new StoreManagerService(
            storeRepository, orderRepository, customerRepository
        );

        store = new StoreManagerModel();
        store.setRestaurant_id(7);
        store.setOwner_id(3);
    }

    @Test
    void getAllCustomersOrders_success() {
        CustomerModel customer = new CustomerModel();
        customer.setId(5);

        OrderModel order = new OrderModel();
        order.setCustomer_id(5);

        when(orderRepository.findByRestaurantId(7L))
            .thenReturn(List.of(order));

        when(customerRepository.findById(5))
            .thenReturn(customer);

        List<CustomerModel> result =
            service.getAllCustomersOrders(store);

        assertEquals(List.of(customer), result);
    }

    @Test
    void getAllCustomersOrders_noOrders() {
        when(orderRepository.findByRestaurantId(7L))
            .thenReturn(List.of());

        List<CustomerModel> result =
            service.getAllCustomersOrders(store);

        assertNull(result);
    }
    
    @Test
    void getAllCustomersOrders_nullStore() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.getAllCustomersOrders(null)
        );
    }

    @Test
    void getOrderByCustomer_success() {
        CustomerModel customer = new CustomerModel();
        customer.setId(5);

        OrderModel otherOrder = new OrderModel();
        otherOrder.setCustomer_id(9);

        OrderModel matchingOrder = new OrderModel();
        matchingOrder.setCustomer_id(5);

        when(orderRepository.findByRestaurantId(7L))
            .thenReturn(List.of(otherOrder, matchingOrder));

        OrderModel result = service.getOrderByCustomer(store, customer);

        assertEquals(matchingOrder, result);
    }

    @Test
    void getOrderByCustomer_notFound() {
        CustomerModel customer = new CustomerModel();
        customer.setId(5);

        OrderModel otherOrder = new OrderModel();
        otherOrder.setCustomer_id(9);

        when(orderRepository.findByRestaurantId(7L))
            .thenReturn(List.of(otherOrder));

        OrderModel result = service.getOrderByCustomer(store, customer);

        assertNull(result);
    }

    @Test
    void deleteStore_success() {
        when(storeRepository.deleteByOwnerId(3))
            .thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.deleteStore(store);

        assertEquals(ErrorCodes.SUCCESS, result);
    }

    @Test
    void deleteStore_notFound() {
        when(storeRepository.deleteByOwnerId(3))
            .thenReturn(ErrorCodes.NOT_FOUND);

        ErrorCodes result = service.deleteStore(store);

        assertEquals(ErrorCodes.NOT_FOUND, result);
    }

    @Test
    void deleteStore_dbFailure() {
        when(storeRepository.deleteByOwnerId(3))
            .thenReturn(ErrorCodes.IO_ERROR);

        ErrorCodes result = service.deleteStore(store);

        assertEquals(ErrorCodes.IO_ERROR, result);
    }

    @Test
    void deleteStore_nullStore() {
        assertThrows(
            IllegalArgumentException.class,
            () -> service.deleteStore(null)
        );
    }

    @Test
    void copyConstructor_keepsSameRepositories() {
        StoreManagerService copy = new StoreManagerService(service);

        assertSame(storeRepository, copy.getStoreManagerRepository());
        assertSame(orderRepository, copy.getOrderRepository());
        assertSame(customerRepository, copy.getCustomerRepository());
}

}