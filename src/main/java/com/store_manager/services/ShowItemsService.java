package com.store_manager.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.ErrorCodes;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;
import com.repository.StoreManagerRepository;

/**
 * @brief Service class for managing the signed-in owner's menu items
 * The model is changed only after a successful repository operation.
 */
public class ShowItemsService {
    private final IStoreManagerRepository storeRepository;
    private final IMenuItemsRepository itemsRepository;
    private final StoreManagerModel ownerStore;
    private final int ownerId;

    /**
     * @brief Constructor for the show items service
     * @param storeRepository The repository for retrieving the owner's store
     * @param itemsRepository The repository for managing menu items
     * @param ownerId The ID of the signed-in owner
     */
    public ShowItemsService(IStoreManagerRepository storeRepository,
            IMenuItemsRepository itemsRepository , StoreManagerModel ownerStore ) {
        this.storeRepository = storeRepository;
        this.itemsRepository = itemsRepository;
        this.ownerStore = ownerStore;
        this.ownerId = ownerStore.getOwner_id();
    }

    /**
     * @brief Retrieves items belonging to the owner's store
     * @return The items, or null if no store is linked to the owner
     * @throws RuntimeException If the repository cannot retrieve the data
     */
    public List<MenuItemsModel> getItems() {
        StoreManagerModel store = storeRepository.findByOwnerId(ownerId);
        return store == null ? null : itemsRepository.findByRestaurantId(store.getRestaurant_id());
    }

    /**
     * @brief Creates an item in the owner's store
     * @param nameInput The item name
     * @param priceInput The price text
     * @param quantityInput The quantity text
     * @param available The item availability
     * @return SUCCESS on creation, NULL_VALUE for null inputs, BAD_TYPE for invalid
     * data, NOT_FOUND for a missing store, or IO_ERROR for a repository failure
     */
    public ErrorCodes addItem(String nameInput, String priceInput, String quantityInput, boolean available) {
        if (nameInput == null || priceInput == null || quantityInput == null)
            return ErrorCodes.NULL_VALUE;
        String name = nameInput.trim();
        BigDecimal price = parsePrice(priceInput);
        Integer quantity = parseQuantity(quantityInput);
        if (name.isEmpty() || name.codePointCount(0, name.length()) > 255 || price == null || quantity == null)
            return ErrorCodes.BAD_TYPE;
        try {
            StoreManagerModel store = storeRepository.findByOwnerId(ownerId);
            if (store == null)
                return ErrorCodes.NOT_FOUND;
            MenuItemsModel item = new MenuItemsModel();
            item.setRestaurant_id(store.getRestaurant_id());
            item.setItem_name(name);
            item.setItem_price(price);
            item.setItem_quantity(quantity);
            item.setIs_available(available);
            itemsRepository.save(item);
            return ErrorCodes.SUCCESS;
        } catch (RuntimeException exception) {
            return ErrorCodes.IO_ERROR;
        }
    }

    /**
     * @brief Validates and converts a price to the database's decimal format
     * @param priceInput The price text, accepting a decimal point or comma
     * @return A price from 0 to 99999999.99
     * @note Returns null if the price format or range is invalid
     */
    private BigDecimal parsePrice(String priceInput) {
        BigDecimal price;
        try {
            String value = priceInput == null ? "" : priceInput.trim().replace(',', '.');
            if (!value.matches("[0-9]+(\\.[0-9]{1,2})?")) {
                return null;
            }
            price = new BigDecimal(value).setScale(2, RoundingMode.UNNECESSARY);
            if (price.compareTo(new BigDecimal("99999999.99")) > 0) {
                return null;
            }
        } catch (NumberFormatException | ArithmeticException exception) {
            return null;
        }
        return price;
    }

    /**
     * @brief Validates and converts the quantity to an integer
     * @param quantityInput The quantity text
     * @return A quantity from 0 to 2147483647
     * @note Returns null if the quantity is invalid
     */
    private Integer parseQuantity(String quantityInput) {
        int quantity;
        try {
            quantity = Integer.parseInt(quantityInput == null ? "" : quantityInput.trim());
            if (quantity < 0) {
                return null;
            }
        } catch (NumberFormatException exception) {
            return null;
        }
        return quantity;
    }

    /**
     * @brief Updates only the item's price
     * @param item The item to update
     * @param input The new price
     * @return SUCCESS on update, NULL_VALUE for missing input, BAD_TYPE for invalid data,
     * NOT_FOUND for a missing store or item, UNMATCHED_IDS for another store's item,
     * or IO_ERROR for a repository failure
     */
    public ErrorCodes updatePrice(MenuItemsModel item, String input) {
        if (item == null || input == null) return ErrorCodes.NULL_VALUE;
        BigDecimal value = parsePrice(input);
        if (value == null) return ErrorCodes.BAD_TYPE;
        try {
            ErrorCodes result = checkItemStore(item);
            if (result != ErrorCodes.SUCCESS) return result;
            if (!itemsRepository.updatePrice(item.getItem_id(), item.getRestaurant_id(), value))
                return ErrorCodes.NOT_FOUND;
            item.setItem_price(value);
            return ErrorCodes.SUCCESS;
        } catch (RuntimeException exception) {
            return ErrorCodes.IO_ERROR;
        }
    }

    /**
     * @brief Updates only the item's quantity
     * @param item The item to update
     * @param input The new quantity
     * @return SUCCESS on update, NULL_VALUE for missing input, BAD_TYPE for invalid data,
     * NOT_FOUND for a missing store or item, UNMATCHED_IDS for another store's item,
     * or IO_ERROR for a repository failure
     */
    public ErrorCodes updateQuantity(MenuItemsModel item, String input) {
        if (item == null || input == null) return ErrorCodes.NULL_VALUE;
        Integer value = parseQuantity(input);
        if (value == null) return ErrorCodes.BAD_TYPE;
        try {
            ErrorCodes result = checkItemStore(item);
            if (result != ErrorCodes.SUCCESS) return result;
            if (!itemsRepository.updateQuantity(item.getItem_id(), item.getRestaurant_id(), value))
                return ErrorCodes.NOT_FOUND;
            item.setItem_quantity(value);
            return ErrorCodes.SUCCESS;
        } catch (RuntimeException exception) {
            return ErrorCodes.IO_ERROR;
        }
    }

    /**
     * @brief Updates only the item's availability
     * @param item The item to update
     * @param available The new availability
     * @return SUCCESS on update, NULL_VALUE for missing input, BAD_TYPE for invalid data,
     * NOT_FOUND for a missing store or item, UNMATCHED_IDS for another store's item,
     * or IO_ERROR for a repository failure
     */
    public ErrorCodes updateAvailability(MenuItemsModel item, boolean available) {
        if (item == null) return ErrorCodes.NULL_VALUE;
        boolean value = available;
        try {
            ErrorCodes result = checkItemStore(item);
            if (result != ErrorCodes.SUCCESS) return result;
            if (!itemsRepository.updateAvailability(item.getItem_id(), item.getRestaurant_id(), value))
                return ErrorCodes.NOT_FOUND;
            item.setIs_available(value);
            return ErrorCodes.SUCCESS;
        } catch (RuntimeException exception) {
            return ErrorCodes.IO_ERROR;
        }
    }

    /**
     * @brief Removes an item without deleting existing order lines
     * @param item The item to remove
     * @return SUCCESS on deletion, NULL_VALUE for no selection, NOT_FOUND for a missing
     * store, UNMATCHED_IDS for another store's item, FAILED_TO_WRITE when deletion
     * is blocked or the item is absent, or IO_ERROR for a repository failure
     */
    public ErrorCodes removeItem(MenuItemsModel item) {
        if (item == null) return ErrorCodes.NULL_VALUE;
        try {
            ErrorCodes result = checkItemStore(item);
            if (result != ErrorCodes.SUCCESS) return result;
            // The boolean repository result cannot distinguish blocked and missing items.
            if (!itemsRepository.deleteUnusedItem(item.getItem_id(), item.getRestaurant_id()))
                return ErrorCodes.FAILED_TO_WRITE;
            return ErrorCodes.SUCCESS;
        } catch (RuntimeException exception) {
            return ErrorCodes.IO_ERROR;
        }
    }

    /**
     * @brief Checks ownership before a write operation
     * @param item The non-null item to check
     * @return SUCCESS, NOT_FOUND if the store is missing, or UNMATCHED_IDS
     */
    private ErrorCodes checkItemStore(MenuItemsModel item) {
        StoreManagerModel store = storeRepository.findByOwnerId(ownerId);
        if (store == null) return ErrorCodes.NOT_FOUND;
        if (item.getRestaurant_id() != store.getRestaurant_id()) return ErrorCodes.UNMATCHED_IDS;
        return ErrorCodes.SUCCESS;
    }
}
