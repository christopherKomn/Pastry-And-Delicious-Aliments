package com.store_manager.services;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ErrorCodes;
import com.models.OrderModel;
import com.repository.IOrderRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private IOrderRepository orderRepository;

    private OrderService service;
    private OrderModel order;

    @BeforeEach
    void setUp() {
        service = new OrderService(orderRepository);
        Timestamp createdAt = Timestamp.valueOf("2026-01-01 10:00:00");
        order = new OrderModel(
            10, 1, 1, "pending",
            new BigDecimal("20.00"), new BigDecimal("2.00"), new BigDecimal("18.00"),
            "cash", "", null, createdAt, createdAt, null, null, null, null
        );
    }

    @Test
    void testConstructorWithNullRepository() {
        assertThrows(IllegalArgumentException.class,
            () -> new OrderService((IOrderRepository) null));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void testCopyConstructorWithNullService() {
        assertThrows(IllegalArgumentException.class,
            () -> new OrderService((OrderService) null));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void testCopyConstructorUsesSameRepository() {
        OrderService copiedService = new OrderService(service);
        when(orderRepository.update(order)).thenReturn(ErrorCodes.SUCCESS);

        assertEquals(ErrorCodes.SUCCESS, copiedService.UpdateOrder(order));
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testUpdateOrderWithSuccessRepository() {
        when(orderRepository.update(order)).thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.UpdateOrder(order);

        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals("pending", order.getStatus());
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testUpdateOrderWithNotFoundRepository() {
        when(orderRepository.update(order)).thenReturn(ErrorCodes.NOT_FOUND);

        ErrorCodes result = service.UpdateOrder(order);

        assertEquals(ErrorCodes.NOT_FOUND, result);
        assertEquals("pending", order.getStatus());
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testUpdateOrderWithFailedToWriteRepository() {
        when(orderRepository.update(order)).thenReturn(ErrorCodes.FAILED_TO_WRITE);

        ErrorCodes result = service.UpdateOrder(order);

        assertEquals(ErrorCodes.FAILED_TO_WRITE, result);
        assertEquals("pending", order.getStatus());
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testUpdateOrderWithIoErrorRepository() {
        when(orderRepository.update(order)).thenReturn(ErrorCodes.IO_ERROR);

        ErrorCodes result = service.UpdateOrder(order);

        assertEquals(ErrorCodes.IO_ERROR, result);
        assertEquals("pending", order.getStatus());
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testUpdateOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> service.UpdateOrder(null));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void testDeleteOrderWithSuccessRepository() {
        when(orderRepository.deleteById(Long.valueOf(order.getId()))).thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.DeleteOrder(order);

        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals("pending", order.getStatus());
        verify(orderRepository).deleteById(Long.valueOf(order.getId()));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testDeleteOrderWithNotFoundRepository() {
        when(orderRepository.deleteById(Long.valueOf(order.getId()))).thenReturn(ErrorCodes.NOT_FOUND);

        ErrorCodes result = service.DeleteOrder(order);

        assertEquals(ErrorCodes.NOT_FOUND, result);
        assertEquals("pending", order.getStatus());
        verify(orderRepository).deleteById(Long.valueOf(order.getId()));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testDeleteOrderWithIoErrorRepository() {
        when(orderRepository.deleteById(Long.valueOf(order.getId()))).thenReturn(ErrorCodes.IO_ERROR);

        ErrorCodes result = service.DeleteOrder(order);

        assertEquals(ErrorCodes.IO_ERROR, result);
        assertEquals("pending", order.getStatus());
        verify(orderRepository).deleteById(Long.valueOf(order.getId()));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testDeleteOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> service.DeleteOrder(null));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void testAcceptOrderWithSuccessRepository() {
        when(orderRepository.update(order)).thenAnswer(invocation -> {
            assertEquals("confirmed", order.getStatus());
            return ErrorCodes.SUCCESS;
        });

        ErrorCodes result = service.AcceptOrder(order);

        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals("confirmed", order.getStatus());
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testAcceptOrderWithNotFoundRepository() {
        when(orderRepository.update(order)).thenAnswer(invocation -> {
            assertEquals("confirmed", order.getStatus());
            return ErrorCodes.NOT_FOUND;
        });

        ErrorCodes result = service.AcceptOrder(order);

        assertEquals(ErrorCodes.NOT_FOUND, result);
        assertEquals("confirmed", order.getStatus());
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testAcceptOrderWithFailedToWriteRepository() {
        when(orderRepository.update(order)).thenAnswer(invocation -> {
            assertEquals("confirmed", order.getStatus());
            return ErrorCodes.FAILED_TO_WRITE;
        });

        ErrorCodes result = service.AcceptOrder(order);

        assertEquals(ErrorCodes.FAILED_TO_WRITE, result);
        assertEquals("confirmed", order.getStatus());
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testAcceptOrderWithIoErrorRepository() {
        when(orderRepository.update(order)).thenAnswer(invocation -> {
            assertEquals("confirmed", order.getStatus());
            return ErrorCodes.IO_ERROR;
        });

        ErrorCodes result = service.AcceptOrder(order);

        assertEquals(ErrorCodes.IO_ERROR, result);
        assertEquals("confirmed", order.getStatus());
        verify(orderRepository).update(order);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testAcceptOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> service.AcceptOrder(null));
        verifyNoInteractions(orderRepository);
    }

    @Test
    void testRejectOrderWithSuccessRepository() {
        when(orderRepository.deleteById(Long.valueOf(order.getId()))).thenAnswer(invocation -> {
            assertEquals("cancelled", order.getStatus());
            return ErrorCodes.SUCCESS;
        });

        ErrorCodes result = service.RejectOrder(order);

        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals("cancelled", order.getStatus());
        verify(orderRepository).deleteById(Long.valueOf(order.getId()));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testRejectOrderWithNotFoundRepository() {
        when(orderRepository.deleteById(Long.valueOf(order.getId()))).thenAnswer(invocation -> {
            assertEquals("cancelled", order.getStatus());
            return ErrorCodes.NOT_FOUND;
        });

        ErrorCodes result = service.RejectOrder(order);

        assertEquals(ErrorCodes.NOT_FOUND, result);
        assertEquals("cancelled", order.getStatus());
        verify(orderRepository).deleteById(Long.valueOf(order.getId()));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testRejectOrderWithIoErrorRepository() {
        when(orderRepository.deleteById(Long.valueOf(order.getId()))).thenAnswer(invocation -> {
            assertEquals("cancelled", order.getStatus());
            return ErrorCodes.IO_ERROR;
        });

        ErrorCodes result = service.RejectOrder(order);

        assertEquals(ErrorCodes.IO_ERROR, result);
        assertEquals("cancelled", order.getStatus());
        verify(orderRepository).deleteById(Long.valueOf(order.getId()));
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void testRejectOrderWithNullOrder() {
        assertThrows(IllegalArgumentException.class, () -> service.RejectOrder(null));
        verifyNoInteractions(orderRepository);
    }
}

