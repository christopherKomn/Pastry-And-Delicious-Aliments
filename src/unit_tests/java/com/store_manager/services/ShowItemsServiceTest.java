package com.store_manager.services;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ErrorCodes;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;

@ExtendWith(MockitoExtension.class)
class ShowItemsServiceTest {

    @Mock
    private IMenuItemsRepository itemsRepository;

    private ShowItemsService service;
    private StoreManagerModel store;
    private MenuItemsModel item;

    @BeforeEach
    void setUp() {
        store = new StoreManagerModel();
        store.setRestaurant_id(7);

        item = new MenuItemsModel();
        item.setItem_id(11);
        item.setRestaurant_id(7);
        item.setItem_price(new BigDecimal("3.50"));

        service = new ShowItemsService(itemsRepository, store);
    }

    @Test
    void updatePrice_success() {
        BigDecimal newPrice = new BigDecimal("4.25");

        when(itemsRepository.updatePrice(11, 7, newPrice))
                .thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.updatePrice(item, "4.25");

        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals(newPrice, item.getItem_price());

        verify(itemsRepository).updatePrice(11, 7, newPrice);
    }

    @Test
    void updatePrice_invalidPrice() {
        ErrorCodes result = service.updatePrice(item, "-5");

        assertEquals(ErrorCodes.BAD_TYPE, result);
        assertEquals(new BigDecimal("3.50"), item.getItem_price());

    }

    @Test
    void updatePrice_dbFailureKeepOldPrice() {
        when(itemsRepository.updatePrice(
             11, 7, new BigDecimal("4.25")))
            .thenReturn(ErrorCodes.IO_ERROR);

        ErrorCodes result = service.updatePrice(item, "4.25");

        assertEquals(ErrorCodes.IO_ERROR, result);
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
    void updatePrice_nullItem() {
        ErrorCodes result = service.updatePrice(null, "4.25");

        assertEquals(ErrorCodes.NULL_VALUE, result);
    }

    @Test
    void updatePrice_nullInput() {
        ErrorCodes result = service.updatePrice(item, null);

        assertEquals(ErrorCodes.NULL_VALUE, result);
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
        void updatePrice_itemFromAnotherStore() {
        
        item.setRestaurant_id(8);

     ErrorCodes result = service.updatePrice(item, "4.25");

        assertEquals(ErrorCodes.UNMATCHED_IDS, result);
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
    void updatePrice_repositoryThrowsException() {
        when(itemsRepository.updatePrice(
            11, 7, new BigDecimal("4.25")))
        .thenThrow(new RuntimeException("Database unavailable"));

        ErrorCodes result = service.updatePrice(item, "4.25");

        assertEquals(ErrorCodes.IO_ERROR, result);
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
    void updatePrice_missingStore() {
        service = new ShowItemsService(itemsRepository, null);

        ErrorCodes result = service.updatePrice(item, "4.25");

        assertEquals(ErrorCodes.NOT_FOUND, result);
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
    void updatePrice_itemNotFound() {
        when(itemsRepository.updatePrice(
            11, 7, new BigDecimal("4.25")))
        .thenReturn(ErrorCodes.NOT_FOUND);

        ErrorCodes result = service.updatePrice(item, "4.25");

        assertEquals(ErrorCodes.NOT_FOUND, result);
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
    void updatePrice_zeroAllowed() {
        BigDecimal newPrice = new BigDecimal("0.00");

        when(itemsRepository.updatePrice(11, 7, newPrice))
            .thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.updatePrice(item, "0");

        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals(newPrice, item.getItem_price());
    }

    @Test
    void updatePrice_aboveMaxRejected() {
        ErrorCodes result = service.updatePrice(item, "100000000.00");

        assertEquals(ErrorCodes.BAD_TYPE, result);
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
    void updateQuantity_success() {
        item.setItem_quantity(10);

        when(itemsRepository.updateQuantity(11, 7, 20))
        .thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.updateQuantity(item, "20");

        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals(20, item.getItem_quantity());
    }

    @Test
    void updateQuantity_negativeQuantity() {
        item.setItem_quantity(10);

        ErrorCodes result = service.updateQuantity(item, "-1");

        assertEquals(ErrorCodes.BAD_TYPE, result);
        assertEquals(10, item.getItem_quantity());
    }

@Test
void updateQuantity_dbFailureKeepsOldQuantity() {
    item.setItem_quantity(10);

    when(itemsRepository.updateQuantity(11, 7, 20))
        .thenReturn(ErrorCodes.IO_ERROR);

    ErrorCodes result = service.updateQuantity(item, "20");

    assertEquals(ErrorCodes.IO_ERROR, result);
    assertEquals(10, item.getItem_quantity());
}

    @Test
    void updateAvailability_success() {
        item.setIs_available(true);

        when(itemsRepository.updateAvailability(11, 7, false))
            .thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.updateAvailability(item, false);

        assertEquals(ErrorCodes.SUCCESS, result);
        assertEquals(Boolean.FALSE, item.getIs_available());
    }

    @Test
    void updateAvailability_dbFailureKeepsOldAvailability() {
        item.setIs_available(true);

        when(itemsRepository.updateAvailability(11, 7, false))
            .thenReturn(ErrorCodes.IO_ERROR);

        ErrorCodes result = service.updateAvailability(item, false);

        assertEquals(ErrorCodes.IO_ERROR, result);
        assertEquals(Boolean.TRUE, item.getIs_available());
    }

    @Test
    void removeItem_success() {
        when(itemsRepository.deleteItem(11, 7))
            .thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.removeItem(item);

        assertEquals(ErrorCodes.SUCCESS, result);
    
    }

    @Test
    void removeItem_deletionBlocked() {
        when(itemsRepository.deleteItem(11, 7))
            .thenReturn(ErrorCodes.FAILED_TO_WRITE);

        ErrorCodes result = service.removeItem(item);

        assertEquals(ErrorCodes.FAILED_TO_WRITE, result);
    }

    @Test
    void addItem_success() {
        when(itemsRepository.save(any(MenuItemsModel.class)))
            .thenReturn(ErrorCodes.SUCCESS);

        ErrorCodes result = service.addItem(
            "Croissant", "3.50", "10", true
        );

        assertEquals(ErrorCodes.SUCCESS, result);
    }

    @Test
    void addItem_emptyName() {
        ErrorCodes result = service.addItem(
            "", "3.50", "10", true
        );

        assertEquals(ErrorCodes.BAD_TYPE, result);
    }

    @Test
    void addItem_dbFailure() {
        when(itemsRepository.save(any(MenuItemsModel.class)))
            .thenReturn(ErrorCodes.IO_ERROR);

        ErrorCodes result = service.addItem(
            "Croissant", "3.50", "10", true
        );

        assertEquals(ErrorCodes.IO_ERROR, result);
    }   

    @Test
    void getItems_emptyList() {
        when(itemsRepository.findByRestaurantId(7))
            .thenReturn(List.of());

        List<MenuItemsModel> result = service.getItems();

        assertEquals(List.of(), result);
    }

    @Test
    void getItems_repositoryThrowsException() {
        when(itemsRepository.findByRestaurantId(7))
            .thenThrow(new RuntimeException("Database unavailable"));

        List<MenuItemsModel> result = service.getItems();

        assertNull(result);
    }

}