package com.customer.services;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ErrorCodes;
import com.models.CartItem;
import com.models.CustomerModel;
import com.models.MenuItemsModel;
import com.models.OrderItemsModel;
import com.models.OrderModel;
import com.repository.ICustomerRepository;
import com.repository.IOrderItemsRepository;
import com.repository.IOrderRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerCheckoutServiceTest {

    //@Mock(mockMaker = org.mockito.MockMakers.PROXY)

        @Mock
        private Connection connection;

    @Mock
    private ICustomerRepository customerRepo;

    @Mock
    private IOrderRepository orderRepo;

    @Mock
    private IOrderItemsRepository orderItemsRepo;

    private CustomerCheckoutService service;
    private List<CartItem> cartItems;

    @BeforeEach
    void setUp() {
        service = new CustomerCheckoutService(
                connection, customerRepo, orderRepo, orderItemsRepo);

        MenuItemsModel pastry = new MenuItemsModel();
        pastry.setItem_id(101);
        pastry.setItem_price(new BigDecimal("4.25"));

        MenuItemsModel coffee = new MenuItemsModel();
        coffee.setItem_id(202);
        coffee.setItem_price(new BigDecimal("2.50"));

        cartItems = Arrays.asList(
                new CartItem(pastry, 2),
                new CartItem(coffee, 1));
    }

    @Test
    void testCanOpenCheckout_GoodBehavior() {
        assertTrue(service.canOpenCheckout(cartItems));
    }

    @Test
    void testCanOpenCheckout_BadBehaviorWithEmptyCart() {
        assertFalse(service.canOpenCheckout(null));
        assertFalse(service.canOpenCheckout(List.of()));
    }

    @Test
    void testCalculateCartTotal_GoodBehavior() {
        assertEquals(new BigDecimal("11.00"), service.calculateCartTotal(cartItems));
    }

    @Test
    void testPlaceOrder_BadBehaviorWithIncompleteDetails() {
        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(1, 3, null, "Card"));
        assertThrows(IllegalArgumentException.class,
                () -> service.placeOrder(1, 3, cartItems, " "));
    }

    @Test
    void testPlaceOrder_GoodBehavior() throws SQLException {
        CustomerModel customer = new CustomerModel();
        customer.setId(7);

        when(connection.getAutoCommit()).thenReturn(true);
        when(customerRepo.findByUserId(1)).thenReturn(customer);
        doAnswer(invocation -> {
            OrderModel order = invocation.getArgument(0);
            order.setId(55);
            return ErrorCodes.SUCCESS;
        }).when(orderRepo).save(any(OrderModel.class));
        when(orderItemsRepo.save(any(OrderItemsModel.class)))
                .thenReturn(ErrorCodes.SUCCESS);

        assertEquals(ErrorCodes.SUCCESS,
                service.placeOrder(1, 3, cartItems, "Card"));

        ArgumentCaptor<OrderModel> orderCaptor = ArgumentCaptor.forClass(OrderModel.class);
        verify(orderRepo).save(orderCaptor.capture());
        OrderModel savedOrder = orderCaptor.getValue();
        assertEquals(7, savedOrder.getCustomer_id());
        assertEquals(3, savedOrder.getRestaurant_id());
        assertEquals("pending", savedOrder.getStatus());
        assertEquals(new BigDecimal("11.00"), savedOrder.getSubtotal());
        assertEquals(BigDecimal.ZERO, savedOrder.getDiscount_amount());
        assertEquals("credit_card", savedOrder.getPayment_method());

        ArgumentCaptor<OrderItemsModel> itemCaptor =
                ArgumentCaptor.forClass(OrderItemsModel.class);
        verify(orderItemsRepo, times(2)).save(itemCaptor.capture());
        assertEquals(Arrays.asList(101, 202), itemCaptor.getAllValues().stream()
                .map(OrderItemsModel::getMenu_item_id).toList());
        assertEquals(Arrays.asList(2, 1), itemCaptor.getAllValues().stream()
                .map(OrderItemsModel::getQuantity).toList());
        assertEquals(Arrays.asList(55, 55), itemCaptor.getAllValues().stream()
                .map(OrderItemsModel::getOrder_id).toList());
        verify(connection).commit();
        verify(connection, never()).rollback();
        verify(connection).setAutoCommit(false);
        verify(connection).setAutoCommit(true);
    }

    @Test
    void testPlaceOrder_BadBehaviorWhenCustomerDoesNotExist() throws SQLException {
        when(connection.getAutoCommit()).thenReturn(true);
        when(customerRepo.findByUserId(1)).thenReturn(null);

        assertEquals(ErrorCodes.NOT_FOUND,
                service.placeOrder(1, 3, cartItems, "Cash"));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(orderRepo, never()).save(any(OrderModel.class));
        verify(connection).setAutoCommit(true);
    }

    @Test
    void testPlaceOrder_BadBehaviorWhenSavingOrderFails() throws SQLException {
        CustomerModel customer = new CustomerModel();
        customer.setId(7);
        when(connection.getAutoCommit()).thenReturn(true);
        when(customerRepo.findByUserId(1)).thenReturn(customer);
        when(orderRepo.save(any(OrderModel.class))).thenReturn(ErrorCodes.FAILED_TO_WRITE);

        assertEquals(ErrorCodes.FAILED_TO_WRITE,
                service.placeOrder(1, 3, cartItems, "Cash"));

        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(orderItemsRepo, never()).save(any(OrderItemsModel.class));
        verify(connection).setAutoCommit(true);
    }

    @Test
    void testPlaceOrder_BadBehaviorWhenSavingOrderItemFails() throws SQLException {
        CustomerModel customer = new CustomerModel();
        customer.setId(7);
        when(connection.getAutoCommit()).thenReturn(true);
        when(customerRepo.findByUserId(1)).thenReturn(customer);
        doAnswer(invocation -> {
            OrderModel order = invocation.getArgument(0);
            order.setId(55);
            return ErrorCodes.SUCCESS;
        }).when(orderRepo).save(any(OrderModel.class));
        when(orderItemsRepo.save(any(OrderItemsModel.class)))
                .thenReturn(ErrorCodes.SUCCESS, ErrorCodes.IO_ERROR);

        assertEquals(ErrorCodes.IO_ERROR,
                service.placeOrder(1, 3, cartItems, "Cash"));

        verify(orderItemsRepo, times(2)).save(any(OrderItemsModel.class));
        verify(connection).rollback();
        verify(connection, never()).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    void testPlaceOrder_BadBehaviorWhenCommitFails() throws SQLException {
        CustomerModel customer = new CustomerModel();
        customer.setId(7);
        when(connection.getAutoCommit()).thenReturn(true);
        when(customerRepo.findByUserId(1)).thenReturn(customer);
        doAnswer(invocation -> {
            OrderModel order = invocation.getArgument(0);
            order.setId(55);
            return ErrorCodes.SUCCESS;
        }).when(orderRepo).save(any(OrderModel.class));
        when(orderItemsRepo.save(any(OrderItemsModel.class)))
                .thenReturn(ErrorCodes.SUCCESS);
        doThrow(new SQLException("commit failed")).when(connection).commit();

        assertEquals(ErrorCodes.IO_ERROR,
                service.placeOrder(1, 3, cartItems, "Cash"));

        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
    }
}