package com.store_manager.controllers;

import java.util.List;
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
        try {
            String input = value == null ? "" : value.toString();
            switch (column) {
                case 1 -> service.updatePrice(item, input);
                case 2 -> service.updateQuantity(item, input);
                case 3 -> {
                    if (!input.equals("Available") && !input.equals("Unavailable")) {
                        throw new IllegalArgumentException("Choose Available or Unavailable.");
                    }
                    service.updateAvailability(item, input.equals("Available"));
                }
                default -> { return; }
            }
        } catch (IllegalArgumentException | IllegalStateException exception) {
            view.showMessage(exception.getMessage());
        } catch (RuntimeException exception) {
            view.showMessage("Could not save the change. The previous value has been kept. "
                    + "For price changes, check that the price is not below an existing discounted price.");
        }
        view.refreshItemDisplay();
    }

    private void removeItem() {
        view.cancelItemEditing();
        MenuItemsModel item = view.getSelectedItem();
        try {
            service.removeItem(item);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            view.showMessage(exception.getMessage());
            return;
        } catch (RuntimeException exception) {
            view.showMessage("Could not remove the item. Please try again.");
            return;
        }
        view.removeItemFromList(item);
    }

    private void saveItem() {
        try {
            service.addItem(view.getItemNameInput(), view.getItemPriceInput(),
                    view.getItemQuantityInput(), view.getItemAvailableInput());
        } catch (IllegalArgumentException | IllegalStateException exception) {
            view.showMessage(exception.getMessage());
            return;
        } catch (RuntimeException exception) {
            view.showMessage("Could not save the item. Your inputs have been kept.");
            return;
        }
        view.hideAddItemForm();
        refreshItems();
    }

    public void refreshItems() {
        try {
            view.setItems(service.getItems());
        } catch (IllegalStateException exception) {
            view.setItems(List.of());
            view.showMessage(exception.getMessage());
        } catch (RuntimeException exception) {
            view.setItems(List.of());
            view.showMessage("Could not load store items. Please try opening Items again.");
        }
    }
}
