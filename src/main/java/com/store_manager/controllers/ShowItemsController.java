package com.store_manager.controllers;

import java.util.List;

import com.ErrorCodes;
import com.models.MenuItemsModel;
import com.store_manager.services.ShowItemsService;
import com.store_manager.views.ShowItemsView;

public class ShowItemsController {
    private final ShowItemsService service;
    private final ShowItemsView view;

    public ShowItemsController(ShowItemsService service, ShowItemsView view) {
        this.service = service;
        this.view = view;
        view.addAddItemListener(event -> view.showAddItemForm());
        view.addSaveItemListener(event -> saveItem());
        view.addRemoveItemListener(event -> removeItem());
        view.setItemEditListener(this::updateItem);
        view.addToggleAvailabilityListener(event -> {
            view.cancelItemEditing();
            MenuItemsModel item = view.getSelectedItem();
            if (item == null) {
                view.showMessage("Select an item first.");
                return;
            }
            updateItem(item, 3, Boolean.TRUE.equals(item.getIs_available()) ? "Unavailable" : "Available");
        });
    }

    private void updateItem(MenuItemsModel item, int column, Object value) {
        String input = value == null ? null : value.toString();
        ErrorCodes result = switch (column) {
            case 1 -> service.updatePrice(item, input);
            case 2 -> service.updateQuantity(item, input);
            case 3 -> "Available".equals(input) || "Unavailable".equals(input)
                    ? service.updateAvailability(item, "Available".equals(input)) : ErrorCodes.BAD_TYPE;
            default -> ErrorCodes.BAD_TYPE;
        };
        if (result != ErrorCodes.SUCCESS) showResult(result);
        view.refreshItemDisplay();
    }

    private void removeItem() {
        view.cancelItemEditing();
        MenuItemsModel item = view.getSelectedItem();
        ErrorCodes result = service.removeItem(item);
        if (result != ErrorCodes.SUCCESS) {
            showResult(result);
            return;
        }
        view.removeItemFromList(item);
    }

    private void saveItem() {
        ErrorCodes result = service.addItem(view.getItemNameInput(), view.getItemPriceInput(),
                view.getItemQuantityInput(), view.getItemAvailableInput());
        if (result != ErrorCodes.SUCCESS) {
            showResult(result);
            return;
        }
        view.hideAddItemForm();
        refreshItems();
    }

    private void showResult(ErrorCodes result) {
        switch (result) {
            case NULL_VALUE -> view.showMessage("Select an item and fill in the required fields.");
            case BAD_TYPE -> view.showMessage("Check the item data: name 1-255 characters, price 0-99999999.99 "
                    + "with up to two decimal places, and whole-number quantity 0-2147483647.");
            case NOT_FOUND -> view.showMessage("The store or item was not found. Reopen Items to reload the list.");
            case UNMATCHED_IDS -> view.showMessage("This item does not belong to your store.");
            case FAILED_TO_WRITE -> view.showMessage("The item was not removed. It may no longer exist, "
                    + "or it is used in an order. Items used in orders must be marked Unavailable instead.");
            case IO_ERROR -> view.showMessage("The database operation failed. Your previous data has been kept. "
                    + "For price changes, check that the price is not below an existing discounted price.");
            case SUCCESS -> { }
            default -> view.showMessage("The operation could not be completed.");
        }
    }

    public void refreshItems() {
        try {
            List<MenuItemsModel> items = service.getItems();
            view.setItems(items == null ? List.of() : items);
            if (items == null) view.showMessage("No store is linked to your account.");
        } catch (RuntimeException exception) {
            view.setItems(List.of());
            view.showMessage("Could not load store items. Please try opening Items again.");
        }
    }
}
