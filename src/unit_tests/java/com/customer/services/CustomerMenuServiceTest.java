package com.customer.services;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.models.CartItem;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerMenuServiceTest {

    @Mock
    private IStoreManagerRepository storeManagerRepo;

    @Mock
    private IMenuItemsRepository menuItemsRepo;

    private CustomerMenuService service;
    private StoreManagerModel restaurant;
    private MenuItemsModel pastry;
    private MenuItemsModel coffee;
    private MenuItemsModel soldOutItem;

    @BeforeEach
    void setUp() {
        service = new CustomerMenuService(storeManagerRepo, menuItemsRepo);

        restaurant = new StoreManagerModel();
        restaurant.setRestaurant_id(1);

        pastry = createProduct(101, 4, true, "4.25");
        coffee = createProduct(102, 5, false, "2.50");
        soldOutItem = createProduct(103, 0, true, "3.00");
    }

    @Test
    void testGetRestaurantById_GoodBehavior() {
        when(storeManagerRepo.findById(1)).thenReturn(restaurant);

        assertSame(restaurant, service.getRestaurantById(1));
        verify(storeManagerRepo).findById(1);
    }

    @Test
    void testCanOpenMenuForRestaurant_GoodBehavior() {
        when(storeManagerRepo.findById(1)).thenReturn(restaurant);

        assertTrue(service.canOpenMenuForRestaurant(1));
    }

    @Test
    void testCanOpenMenuForRestaurant_BadBehaviorWhenRestaurantDoesNotExist() {
        when(storeManagerRepo.findById(99)).thenReturn(null);

        assertFalse(service.canOpenMenuForRestaurant(99));
    }

    @Test
    void testGetAvailableProducts_FiltersUnavailableAndOutOfStockItems() {
        when(menuItemsRepo.findByRestaurantId(1))
                .thenReturn(List.of(pastry, coffee, soldOutItem));

        assertEquals(List.of(pastry), service.getAvailableProducts(1));
        verify(menuItemsRepo).findByRestaurantId(1);
    }

    @Test
    void testValidateProductQuantity_GoodBehavior() {
        assertNull(service.validateProductQuantity(pastry, null, 2));
        assertNull(service.validateProductQuantity(pastry, new CartItem(pastry, 2), 2));
    }

    @Test
    void testValidateProductQuantity_BadBehaviorWhenProductIsMissing() {
        assertEquals("Product not found.",
                service.validateProductQuantity(null, null, 1));
    }

    @Test
    void testValidateProductQuantity_BadBehaviorWhenRequestedQuantityExceedsStock() {
        assertEquals("Only 2 more unit(s) of this product are available.",
            service.validateProductQuantity(pastry, new CartItem(pastry, 2), 3));
        assertEquals("Only 0 more unit(s) of this product are available.",
                service.validateProductQuantity(pastry, new CartItem(pastry, 5), 1));
    }

    @Test
    void testFindProduct_GoodBehavior() {
        assertSame(pastry, service.findProduct(List.of(coffee, pastry), 101));
    }

    @Test
    void testFindProduct_BadBehaviorWhenProductDoesNotExist() {
        assertNull(service.findProduct(List.of(pastry, coffee), 999));
    }

    @Test
    void testFindCartItem_GoodBehavior() {
        CartItem pastryCartItem = new CartItem(pastry, 2);

        assertSame(pastryCartItem,
                service.findCartItem(List.of(new CartItem(coffee, 1), pastryCartItem), 101));
    }

    @Test
    void testFindCartItem_BadBehaviorWhenItemDoesNotExist() {
        assertNull(service.findCartItem(List.of(new CartItem(pastry, 2)), 999));
    }

    @Test
    void testCalculateCartTotal_GoodBehavior() {
        List<CartItem> cart = List.of(
                new CartItem(pastry, 2),
                new CartItem(createProduct(104, 2, true, "2.50"), 1));

        assertEquals(new BigDecimal("11.00"), service.calculateCartTotal(cart));
    }

    private MenuItemsModel createProduct(
            int itemId, int quantity, boolean available, String price) {
        MenuItemsModel product = new MenuItemsModel();
        product.setItem_id(itemId);
        product.setItem_quantity(quantity);
        product.setIs_available(available);
        product.setItem_price(new BigDecimal(price));
        return product;
    }
}