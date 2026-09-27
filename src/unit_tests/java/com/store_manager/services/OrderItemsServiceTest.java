package com.store_manager.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ErrorCodes;
import com.models.OrderItemsModel;
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
    private OrderItemsModel orderItem;

    @BeforeEach
    void setUp() {
        service = new OrderItemsService(
            menuItemsRepository,
            orderItemsRepository,
            orderRepository
        );

        orderItem = new OrderItemsModel();
        orderItem.setId(1);
        orderItem.setOrder_id(10);
        orderItem.setMenu_item_id(100);
        orderItem.setQuantity(2);
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

}
