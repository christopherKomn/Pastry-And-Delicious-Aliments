package com.store_manager.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ErrorCodes;
import com.models.CustomerModel;
import com.models.MenuItemsModel;
import com.models.OrderItemsModel;
import com.models.OrderModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IOrderItemsRepository;
import com.repository.IOrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderItemsServiceTest {

    @Mock
    private IMenuItemsRepository menuItemsRepository;

    @Mock
    private IOrderItemsRepository orderItemsRepository;

    @Mock
    private IOrderRepository orderRepository;

    private OrderItemsService service;
    private CustomerModel customer;
    private StoreManagerModel restaurant;
    private List<MenuItemsModel> menuItems;
    private MenuItemsModel menuItem;
    private OrderModel order;
    private OrderItemsModel orderItem;

    @BeforeEach
    void setUp() {
        service = new OrderItemsService(
            menuItemsRepository,
            orderItemsRepository,
            orderRepository
        );

        Timestamp createdAt = Timestamp.valueOf("2026-01-01 10:00:00");

        customer = new CustomerModel(
            1, 1, "Test Customer", "Athens", "Attica", "10558",
            "10 Customer Street", ""
        );

        restaurant = new StoreManagerModel();
        restaurant.setRestaurant_id(1);
        restaurant.setOwner_id(2);
        restaurant.setName("Test Pastry House");
        restaurant.setCity("Athens");
        restaurant.setAddress_line1("20 Baker Street");
        restaurant.setPostal_code("10558");

        menuItem = new MenuItemsModel(
            100, restaurant.getRestaurant_id(), "Croissant", 10,
            "Butter croissant", new BigDecimal("10.00"), 9.0f,
            10, true, "http://example.com/croissant.jpg", 1, createdAt, createdAt
        );
        menuItems = new ArrayList<>();
        menuItems.add(menuItem);
        menuItems.add(new MenuItemsModel(
            101, restaurant.getRestaurant_id(), "Chocolate Cake", 5,
            "Chocolate cake slice", new BigDecimal("5.00"), 4.5f,
            5, true, "http://example.com/cake.jpg", 2, createdAt, createdAt
        ));

        order = new OrderModel(
            10, customer.getId(), restaurant.getRestaurant_id(), "pending",
            new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("18.00"),
            "cash", "", null, createdAt, createdAt, null, null, null, null
        );

        orderItem = new OrderItemsModel(
            1, order.getId(), menuItem.getItem_id(), 2, "", "{}"
        );
        orderItem.setCreated_at(createdAt);
        orderItem.setUpdated_at(createdAt);
    }

    @Test
    void testUpdateOrderItemWithValidOrderItem() {
        when(orderItemsRepository.update(orderItem)).thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.UpdateOrderItem(orderItem);

        assertEquals(ErrorCodes.SUCCESS, result);
        verify(orderItemsRepository).update(orderItem);
    }

    @Test
    void testUpdateOrderItemWithNotExistingOrderItem() {
        when(orderItemsRepository.update(orderItem)).thenReturn(ErrorCodes.NOT_FOUND);

        ErrorCodes result = service.UpdateOrderItem(orderItem);

        assertEquals(ErrorCodes.NOT_FOUND, result);
        verify(orderItemsRepository).update(orderItem);
    }

    @Test
    void testUpdateOrderItemWithFailedWriteRepository() {
        when(orderItemsRepository.update(orderItem)).thenReturn(ErrorCodes.FAILED_TO_WRITE);

        ErrorCodes result = service.UpdateOrderItem(orderItem);

        assertEquals(ErrorCodes.FAILED_TO_WRITE, result);
        verify(orderItemsRepository).update(orderItem);
    }

    @Test
    void testUpdateOrderItemWithIoErrorRepository() {
        when(orderItemsRepository.update(orderItem)).thenReturn(ErrorCodes.IO_ERROR);

        ErrorCodes result = service.UpdateOrderItem(orderItem);

        assertEquals(ErrorCodes.IO_ERROR, result);
        verify(orderItemsRepository).update(orderItem);
    }

    @Test
    void testUpdateOrderItemWithNotFoundErrorRepository() {
        when(orderItemsRepository.update(orderItem)).thenReturn(ErrorCodes.NOT_FOUND);

        ErrorCodes result = service.UpdateOrderItem(orderItem);

        assertEquals(ErrorCodes.NOT_FOUND, result);
        verify(orderItemsRepository).update(orderItem);
    }

    @Test
    void testUpdateOrderItemWithFailedToWriteErrorRepository() {
        when(orderItemsRepository.update(orderItem)).thenReturn(ErrorCodes.FAILED_TO_WRITE);

        ErrorCodes result = service.UpdateOrderItem(orderItem);

        assertEquals(ErrorCodes.FAILED_TO_WRITE , result);
        verify(orderItemsRepository).update(orderItem);
    }

    @Test
    void testCreateOrderItemGoodBehavior_Updated() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.findByOrderId(anyInt())).thenReturn(orderItems);

        when(orderItemsRepository.update(any(OrderItemsModel.class))).thenReturn(ErrorCodes.SUCCESS);
        
        //when(orderItemsRepository.save(any(OrderItemsModel.class))).thenReturn(ErrorCodes.SUCCESS);
        
        ErrorCodes result = service.CreateOrderItem(menuItem, 1, order);

        assertEquals(ErrorCodes.SUCCESS , result);
    }

    @Test
    void testCreateOrderItemGoodBehavior_Saved() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.findByOrderId(anyInt())).thenReturn(null);

        //when(orderItemsRepository.update(any(OrderItemsModel.class))).thenReturn(ErrorCodes.SUCCESS);
        
        when(orderItemsRepository.save(any(OrderItemsModel.class))).thenReturn(ErrorCodes.SUCCESS);
        
        ErrorCodes result = service.CreateOrderItem(menuItem, 1, order);

        assertEquals(ErrorCodes.SUCCESS , result);
    }



    @Test
    void testCreateOrderItemBadBehavior_Updated() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.findByOrderId(anyInt())).thenReturn(orderItems);

        when(orderItemsRepository.update(any(OrderItemsModel.class))).thenReturn(ErrorCodes.NOT_FOUND);
        
        //when(orderItemsRepository.save(any(OrderItemsModel.class))).thenReturn(ErrorCodes.SUCCESS);
        
        ErrorCodes result = service.CreateOrderItem(menuItem, 1, order);

        assertEquals(ErrorCodes.NOT_FOUND , result);
    }

    @Test
    void testCreateOrderItemBadBehavior_Saved() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.findByOrderId(anyInt())).thenReturn(null);

        //when(orderItemsRepository.update(any(OrderItemsModel.class))).thenReturn(ErrorCodes.SUCCESS);
        
        when(orderItemsRepository.save(any(OrderItemsModel.class))).thenReturn(ErrorCodes.IO_ERROR);
        
        ErrorCodes result = service.CreateOrderItem(menuItem, 1, order);

        assertEquals(ErrorCodes.IO_ERROR , result);
    }

    @Test
    void testConstructorWithNullMenuItemsRepository() {
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItemsService(null, orderItemsRepository, orderRepository));
    }

    @Test
    void testConstructorWithNullOrderItemsRepository() {
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItemsService(menuItemsRepository, null, orderRepository));
    }

    @Test
    void testConstructorWithNullOrderRepository() {
        assertThrows(IllegalArgumentException.class,
            () -> new OrderItemsService(menuItemsRepository, orderItemsRepository, null));
    }

    @Test
    void testGetAllOrderItemsFromOrderWithValidOrder() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.findByOrderId(order.getId())).thenReturn(orderItems);

        assertEquals(orderItems, service.getAllOrderItemsFromOrder(order));
        verify(orderItemsRepository).findByOrderId(order.getId());
    }

    @Test
    void testGetAllOrderItemsFromOrderWithEmptyResult() {
        when(orderItemsRepository.findByOrderId(order.getId())).thenReturn(List.of());

        assertEquals(List.of(), service.getAllOrderItemsFromOrder(order));
        verify(orderItemsRepository).findByOrderId(order.getId());
    }

    @Test
    void testGetAllOrderItemsFromOrderWithNullResult() {
        when(orderItemsRepository.findByOrderId(order.getId())).thenReturn(null);

        assertNull(service.getAllOrderItemsFromOrder(order));
        verify(orderItemsRepository).findByOrderId(order.getId());
    }

    @Test
    void testGetAllOrderItemsFromOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class,
            () -> service.getAllOrderItemsFromOrder(null));
        verifyNoInteractions(orderItemsRepository);
    }

    @Test
    void testGetAllMenuItemsOfOrderWithValidOrder() {
        OrderItemsModel secondOrderItem = new OrderItemsModel(
            2, order.getId(), menuItems.get(1).getItem_id(), 1, "", "{}"
        );
        List<OrderItemsModel> orderItems = List.of(orderItem, secondOrderItem);
        when(orderItemsRepository.findByOrderId(order.getId())).thenReturn(orderItems);
        when(orderItemsRepository.getMenuItemsByOrderItems(orderItems)).thenReturn(menuItems);

        assertEquals(menuItems, service.getAllMenuItemsOfOrder(order));
        verify(orderItemsRepository).findByOrderId(order.getId());
        verify(orderItemsRepository).getMenuItemsByOrderItems(orderItems);
    }

    @Test
    void testGetAllMenuItemsOfOrderWithNullOrderItems() {
        when(orderItemsRepository.findByOrderId(order.getId())).thenReturn(null);

        assertNull(service.getAllMenuItemsOfOrder(order));
        verify(orderItemsRepository).findByOrderId(order.getId());
        verifyNoMoreInteractions(orderItemsRepository);
    }

    @Test
    void testGetAllMenuItemsOfOrderWithEmptyOrderItems() {
        List<OrderItemsModel> orderItems = List.of();
        when(orderItemsRepository.findByOrderId(order.getId())).thenReturn(orderItems);
        when(orderItemsRepository.getMenuItemsByOrderItems(orderItems)).thenReturn(List.of());

        assertEquals(List.of(), service.getAllMenuItemsOfOrder(order));
        verify(orderItemsRepository).getMenuItemsByOrderItems(orderItems);
    }

    @Test
    void testGetAllMenuItemsOfOrderWithNullMenuItems() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.findByOrderId(order.getId())).thenReturn(orderItems);
        when(orderItemsRepository.getMenuItemsByOrderItems(orderItems)).thenReturn(null);

        assertNull(service.getAllMenuItemsOfOrder(order));
        verify(orderItemsRepository).getMenuItemsByOrderItems(orderItems);
    }

    @Test
    void testGetAllMenuItemsOfOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class,
            () -> service.getAllMenuItemsOfOrder(null));
        verifyNoInteractions(orderItemsRepository);
    }

    @Test
    void testUpdateOrderItemsWithValidOrderItems() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.UpdateOrderItems(orderItems)).thenReturn(ErrorCodes.SUCCESS);

        assertEquals(ErrorCodes.SUCCESS, service.UpdateOrderItems(orderItems));
        verify(orderItemsRepository).UpdateOrderItems(orderItems);
    }

    @Test
    void testUpdateOrderItemsWithNotFoundRepository() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.UpdateOrderItems(orderItems)).thenReturn(ErrorCodes.NOT_FOUND);

        assertEquals(ErrorCodes.NOT_FOUND, service.UpdateOrderItems(orderItems));
        verify(orderItemsRepository).UpdateOrderItems(orderItems);
    }

    @Test
    void testUpdateOrderItemsWithFailedWriteRepository() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.UpdateOrderItems(orderItems)).thenReturn(ErrorCodes.FAILED_TO_WRITE);

        assertEquals(ErrorCodes.FAILED_TO_WRITE, service.UpdateOrderItems(orderItems));
        verify(orderItemsRepository).UpdateOrderItems(orderItems);
    }

    @Test
    void testUpdateOrderItemsWithIoErrorRepository() {
        List<OrderItemsModel> orderItems = List.of(orderItem);
        when(orderItemsRepository.UpdateOrderItems(orderItems)).thenReturn(ErrorCodes.IO_ERROR);

        assertEquals(ErrorCodes.IO_ERROR, service.UpdateOrderItems(orderItems));
        verify(orderItemsRepository).UpdateOrderItems(orderItems);
    }

    @Test
    void testUpdateOrderItemsWithEmptyOrderItems() {
        List<OrderItemsModel> orderItems = List.of();
        when(orderItemsRepository.UpdateOrderItems(orderItems)).thenReturn(ErrorCodes.SUCCESS);

        assertEquals(ErrorCodes.SUCCESS, service.UpdateOrderItems(orderItems));
        verify(orderItemsRepository).UpdateOrderItems(orderItems);
    }

    @Test
    void testUpdateOrderItemsWithNullOrderItems() {
        assertThrows(IllegalArgumentException.class, () -> service.UpdateOrderItems(null));
        verifyNoInteractions(orderItemsRepository);
    }

    @Test
    void testDeleteOrderItemWithValidOrderItem() {
        when(orderItemsRepository.deleteById(orderItem.getId())).thenReturn(ErrorCodes.SUCCESS);

        assertEquals(ErrorCodes.SUCCESS, service.DeleteOrderItem(orderItem));
        verify(orderItemsRepository).deleteById(orderItem.getId());
    }

    @Test
    void testDeleteOrderItemWithNotFoundRepository() {
        when(orderItemsRepository.deleteById(orderItem.getId())).thenReturn(ErrorCodes.NOT_FOUND);

        assertEquals(ErrorCodes.NOT_FOUND, service.DeleteOrderItem(orderItem));
        verify(orderItemsRepository).deleteById(orderItem.getId());
    }

    @Test
    void testDeleteOrderItemWithIoErrorRepository() {
        when(orderItemsRepository.deleteById(orderItem.getId())).thenReturn(ErrorCodes.IO_ERROR);

        assertEquals(ErrorCodes.IO_ERROR, service.DeleteOrderItem(orderItem));
        verify(orderItemsRepository).deleteById(orderItem.getId());
    }

    @Test
    void testDeleteOrderItemWithNullOrderItem() {
        assertThrows(IllegalArgumentException.class, () -> service.DeleteOrderItem(null));
        verifyNoInteractions(orderItemsRepository);
    }

}
