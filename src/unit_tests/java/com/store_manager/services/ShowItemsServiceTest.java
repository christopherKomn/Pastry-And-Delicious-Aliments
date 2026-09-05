package com.store_manager.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.math.BigDecimal;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;

class ShowItemsServiceTest {
    private final IStoreManagerRepository stores = mock(IStoreManagerRepository.class);
    private final IMenuItemsRepository items = mock(IMenuItemsRepository.class);
    private final ShowItemsService service = new ShowItemsService(stores, items, 3);

    @Test
    void removesOnlyFromSignedInOwnersStore() {
        MenuItemsModel item = editableItem();
        when(items.deleteUnusedItem(11, 7)).thenReturn(true);
        service.removeItem(item);
        verify(items).deleteUnusedItem(11, 7);
        verifyNoMoreInteractions(items);
    }

    @Test
    void rejectsRemovalWithoutSelection() {
        assertThrows(IllegalArgumentException.class, () -> service.removeItem(null));
        verifyNoInteractions(stores, items);
    }

    @Test
    void rejectsRemovalFromAnotherStore() {
        MenuItemsModel item = editableItem();
        item.setRestaurant_id(8);
        assertThrows(IllegalArgumentException.class, () -> service.removeItem(item));
        verifyNoInteractions(items);
    }

    @Test
    void reportsBlockedOrMissingItemAndDatabaseFailure() {
        MenuItemsModel item = editableItem();
        assertThrows(IllegalStateException.class, () -> service.removeItem(item));
        when(items.deleteUnusedItem(11, 7)).thenThrow(new RuntimeException("Disconnected"));
        assertThrows(RuntimeException.class, () -> service.removeItem(item));
    }

    private MenuItemsModel editableItem() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);
        when(stores.findByOwnerId(3)).thenReturn(store);
        MenuItemsModel item = new MenuItemsModel();
        item.setItem_id(11);
        item.setRestaurant_id(7);
        item.setItem_price(new BigDecimal("3.50"));
        item.setItem_quantity(25);
        item.setIs_available(true);
        return item;
    }

    @Test
    void priceUpdatePreservesQuantityAndAvailability() {
        MenuItemsModel item = editableItem();
        when(items.updatePrice(11, 7, new BigDecimal("4.25"))).thenReturn(true);
        service.updatePrice(item, "4,25");
        assertEquals(new BigDecimal("4.25"), item.getItem_price());
        assertEquals(25, item.getItem_quantity());
        assertTrue(item.getIs_available());
        verify(items).updatePrice(11, 7, new BigDecimal("4.25"));
        verifyNoMoreInteractions(items);
    }

    @Test
    void failedWriteKeepsPreviousPrice() {
        MenuItemsModel item = editableItem();
        doThrow(new RuntimeException("Disconnected")).when(items).updatePrice(anyInt(), anyInt(), any());
        assertThrows(RuntimeException.class, () -> service.updatePrice(item, "4.25"));
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
    void missingItemKeepsPreviousQuantity() {
        MenuItemsModel item = editableItem();
        assertThrows(IllegalStateException.class, () -> service.updateQuantity(item, "10"));
        assertEquals(25, item.getItem_quantity());
    }

    @Test
    void cannotUpdateAnotherStoresItem() {
        MenuItemsModel item = editableItem();
        item.setRestaurant_id(8);
        assertThrows(IllegalArgumentException.class, () -> service.updateAvailability(item, false));
        verifyNoInteractions(items);
        assertTrue(item.getIs_available());
    }

    @Test
    void quantityAndAvailabilityUpdatesPreservePrice() {
        MenuItemsModel item = editableItem();
        when(items.updateQuantity(11, 7, 0)).thenReturn(true);
        when(items.updateAvailability(11, 7, false)).thenReturn(true);
        service.updateQuantity(item, "0");
        service.updateAvailability(item, false);
        assertEquals(0, item.getItem_quantity());
        assertFalse(item.getIs_available());
        assertEquals(new BigDecimal("3.50"), item.getItem_price());
    }

    @Test
    void invalidEditsDoNotReachRepository() {
        MenuItemsModel item = new MenuItemsModel();
        assertThrows(IllegalArgumentException.class, () -> service.updatePrice(item, "-1"));
        assertThrows(IllegalArgumentException.class, () -> service.updateQuantity(item, "1.5"));
        verifyNoInteractions(items);
    }

    @Test
    void createsItemForSignedInOwnersStoreWithExactPrice() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);
        when(stores.findByOwnerId(3)).thenReturn(store);
        service.addItem("  Croissant  ", "3,50", "25", false);
        ArgumentCaptor<MenuItemsModel> saved = ArgumentCaptor.forClass(MenuItemsModel.class);
        verify(items).save(saved.capture());
        assertEquals(7, saved.getValue().getRestaurant_id());
        assertEquals("Croissant", saved.getValue().getItem_name());
        assertEquals(new BigDecimal("3.50"), saved.getValue().getItem_price());
        assertEquals(25, saved.getValue().getItem_quantity());
        assertFalse(saved.getValue().getIs_available());
    }

    @ParameterizedTest
    @CsvSource({"'',3.50,2", "Cake,-1,2", "Cake,1.234,2", "Cake,NaN,2",
            "Cake,100000000,2", "Cake,3.50,-1", "Cake,3.50,1.5", "Cake,3.50,2147483648"})
    void rejectsInvalidInputBeforeSaving(String name, String price, String quantity) {
        assertThrows(IllegalArgumentException.class, () -> service.addItem(name, price, quantity, true));
        verifyNoInteractions(items);
    }

    @Test
    void cannotCreateWithoutLinkedStore() {
        assertThrows(IllegalStateException.class, () -> service.addItem("Cake", "2", "1", true));
        verifyNoInteractions(items);
    }

    @Test
    void savingFailureIsPropagatedToController() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);
        when(stores.findByOwnerId(3)).thenReturn(store);
        doThrow(new RuntimeException("Database unavailable")).when(items).save(any());
        assertThrows(RuntimeException.class, () -> service.addItem("Cake", "2", "0", true));
    }

    @Test
    void loadsUsingRestaurantIdRatherThanOwnerIdIncludingUnavailableItems() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);
        when(stores.findByOwnerId(3)).thenReturn(store);
        MenuItemsModel unavailable = new MenuItemsModel();
        unavailable.setIs_available(false);
        when(items.findByRestaurantId(7)).thenReturn(List.of(unavailable));

        assertEquals(List.of(unavailable), service.getItems());
        verify(items).findByRestaurantId(7);
        verifyNoMoreInteractions(items);
    }

    @Test
    void missingStoreDoesNotQueryItems() {
        assertThrows(IllegalStateException.class, service::getItems);
        verifyNoInteractions(items);
    }

    @Test
    void storeWithoutItemsReturnsEmptyList() {
        StoreManagerModel store = new StoreManagerModel();
        store.setRestaurant_id(7);
        when(stores.findByOwnerId(3)).thenReturn(store);
        when(items.findByRestaurantId(7)).thenReturn(List.of());
        assertTrue(service.getItems().isEmpty());
    }
}
