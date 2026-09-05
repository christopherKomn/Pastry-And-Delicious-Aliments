package com.store_manager.views;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Component;
import java.awt.Container;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import org.junit.jupiter.api.Test;
import com.models.MenuItemsModel;
import com.models.StoreManagerModel;
import com.repository.IMenuItemsRepository;
import com.repository.IStoreManagerRepository;
import com.store_manager.controllers.ShowItemsController;
import com.store_manager.services.ShowItemsService;

class ShowItemsViewTest {
    @Test
    void removalClosesGapAndLastRemovalClearsSelection() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            MenuItemsModel first = new MenuItemsModel();
            MenuItemsModel middle = new MenuItemsModel();
            MenuItemsModel last = new MenuItemsModel();
            ShowItemsView view = new ShowItemsView();
            view.setItems(List.of(first, middle, last));
            JTable table = findTable(view);
            table.setRowSelectionInterval(1, 1);
            view.removeItemFromList(middle);
            assertEquals(2, table.getRowCount());
            assertSame(last, view.getSelectedItem());
            assertEquals(1, table.getSelectedRow());
            view.removeItemFromList(last);
            assertSame(first, view.getSelectedItem());
            view.removeItemFromList(first);
            assertEquals(0, table.getRowCount());
            assertNull(view.getSelectedItem());
        });
    }
    @Test
    void textEditorEnterSavesAndEscapeStyleCancellationDoesNotSave() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            IStoreManagerRepository stores = mock(IStoreManagerRepository.class);
            IMenuItemsRepository repository = mock(IMenuItemsRepository.class);
            StoreManagerModel store = new StoreManagerModel();
            store.setRestaurant_id(7);
            when(stores.findByOwnerId(3)).thenReturn(store);
            when(repository.updatePrice(11, 7, new BigDecimal("4.50"))).thenReturn(true);
            MenuItemsModel item = new MenuItemsModel();
            item.setItem_id(11);
            item.setRestaurant_id(7);
            item.setItem_price(new BigDecimal("3.50"));
            item.setIs_available(true);
            ShowItemsView view = new ShowItemsView();
            new ShowItemsController(new ShowItemsService(stores, repository, 3), view);
            view.setItems(List.of(item));
            JTable table = findTable(view);
            assertNotNull(table);
            assertFalse(table.isCellEditable(0, 0));
            assertTrue(table.editCellAt(0, 1));
            JTextField field = (JTextField) table.getEditorComponent();
            field.setText("4,50");
            field.postActionEvent();
            verify(repository).updatePrice(11, 7, new BigDecimal("4.50"));
            assertEquals("4.50", table.getValueAt(0, 1));
            assertTrue(table.editCellAt(0, 2));
            ((JTextField) table.getEditorComponent()).setText("50");
            view.cancelItemEditing();
            verifyNoMoreInteractions(repository);
            assertEquals(0, table.getValueAt(0, 2));

            when(repository.updateAvailability(11, 7, false)).thenReturn(true);
            assertTrue(table.editCellAt(0, 3));
            ((JComboBox<?>) table.getEditorComponent()).setSelectedItem("Unavailable");
            verify(repository).updateAvailability(11, 7, false);
            assertEquals("Unavailable", table.getValueAt(0, 3));
        });
    }

    private JTable findTable(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTable table) return table;
            if (component instanceof Container child) {
                JTable table = findTable(child);
                if (table != null) return table;
            }
        }
        return null;
    }
}
