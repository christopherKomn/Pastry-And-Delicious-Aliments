package com.store_manager.services;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;

public class ShowItemsService {
    private final IStoreManagerRepository storeRepository;
    private final IMenuItemsRepository itemsRepository;
    private final int ownerId;

    public ShowItemsService(IStoreManagerRepository storeRepository,
            IMenuItemsRepository itemsRepository, int ownerId) {
        this.storeRepository = storeRepository;
        this.itemsRepository = itemsRepository;
        this.ownerId = ownerId;
    }

    public List<MenuItemsModel> getItems() {
        return itemsRepository.findByRestaurantId(getStore().getRestaurant_id());
    }

    public void addItem(String nameInput, String priceInput, String quantityInput, boolean available) {
        String name = nameInput == null ? "" : nameInput.trim();
        if (name.isEmpty() || name.codePointCount(0, name.length()) > 255) {
            throw new IllegalArgumentException("Enter an item name with 1 to 255 characters.");
        }
        BigDecimal price = parsePrice(priceInput);
        int quantity = parseQuantity(quantityInput);

        MenuItemsModel item = new MenuItemsModel();
        item.setRestaurant_id(getStore().getRestaurant_id());
        item.setItem_name(name);
        item.setItem_price(price);
        item.setItem_quantity(quantity);
        item.setIs_available(available);
        itemsRepository.save(item);
    }


    private BigDecimal parsePrice(String priceInput) {
        BigDecimal price;
        try {
            String value = priceInput == null ? "" : priceInput.trim().replace(',', '.');
            if (!value.matches("[0-9]+(\\.[0-9]{1,2})?")) {
                throw new NumberFormatException();
            }
            price = new BigDecimal(value).setScale(2, RoundingMode.UNNECESSARY);
            if (price.compareTo(new BigDecimal("99999999.99")) > 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException | ArithmeticException exception) {
            throw new IllegalArgumentException("Enter a price from 0 to 99999999.99 with up to two decimal places.");
        }
        return price;
    }

    private int parseQuantity(String quantityInput) {
        int quantity;
        try {
            quantity = Integer.parseInt(quantityInput == null ? "" : quantityInput.trim());
            if (quantity < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a whole-number quantity from 0 to 2147483647.");
        }
        return quantity;
    }

    public void updatePrice(MenuItemsModel item, String input) {
        BigDecimal price = parsePrice(input);
        int storeId = getItemStore(item);
        requireUpdated(itemsRepository.updatePrice(item.getItem_id(), storeId, price));
        item.setItem_price(price);
    }

    public void updateQuantity(MenuItemsModel item, String input) {
        int quantity = parseQuantity(input);
        int storeId = getItemStore(item);
        requireUpdated(itemsRepository.updateQuantity(item.getItem_id(), storeId, quantity));
        item.setItem_quantity(quantity);
    }

    public void updateAvailability(MenuItemsModel item, boolean available) {
        int storeId = getItemStore(item);
        requireUpdated(itemsRepository.updateAvailability(item.getItem_id(), storeId, available));
        item.setIs_available(available);
    }

    public void removeItem(MenuItemsModel item) {
        int storeId = getItemStore(item);
        if (!itemsRepository.deleteUnusedItem(item.getItem_id(), storeId)) {
            throw new IllegalStateException("The item was not removed. It may no longer exist, "
                    + "or it is used in an order. Items used in orders must be marked Unavailable instead.");
        }
    }

    private int getItemStore(MenuItemsModel item) {
        if (item == null) {
            throw new IllegalArgumentException("Select an item first.");
        }
        int storeId = getStore().getRestaurant_id();
        if (item.getRestaurant_id() != storeId) {
            throw new IllegalArgumentException("This item does not belong to your store.");
        }
        return storeId;
    }

    private void requireUpdated(boolean updated) {
        if (!updated) {
            throw new IllegalStateException("The item could not be updated. Reopen Items to reload the list.");
        }
    }

    private StoreManagerModel getStore() {
        // Uses the existing single-store lookup for the signed-in owner.
        StoreManagerModel store = storeRepository.findByOwnerId(ownerId);
        if (store == null) {
            throw new IllegalStateException("No store is linked to your account.");
        }
        return store;
    }
}
