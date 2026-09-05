package com.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.models.MenuItemsModel;

public class DBMenuItemsRepository implements IMenuItemsRepository {
    private final Connection connection;

    public DBMenuItemsRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean deleteUnusedItem(int itemId, int restaurantId) {
        // Guard against cascading deletion of existing order lines.
        String sql = "DELETE FROM menu_items WHERE id = ? AND restaurant_id = ? "
                + "AND NOT EXISTS (SELECT 1 FROM order_items WHERE menu_item_id = menu_items.id)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, itemId);
            statement.setInt(2, restaurantId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new RuntimeException("Could not remove the item.", exception);
        }
    }

    @Override
    public boolean updatePrice(int itemId, int restaurantId, java.math.BigDecimal price) {
        return updateField("price", price, itemId, restaurantId);
    }

    @Override
    public boolean updateQuantity(int itemId, int restaurantId, int quantity) {
        return updateField("quantity", quantity, itemId, restaurantId);
    }

    @Override
    public boolean updateAvailability(int itemId, int restaurantId, boolean available) {
        return updateField("is_available", available, itemId, restaurantId);
    }

    // Column names come only from the three methods above, never from user input.
    private boolean updateField(String column, Object value, int itemId, int restaurantId) {
        String sql = "UPDATE menu_items SET " + column + " = ? WHERE id = ? AND restaurant_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, value);
            statement.setInt(2, itemId);
            statement.setInt(3, restaurantId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new RuntimeException("Could not update the item.", exception);
        }
    }

    @Override
    public List<MenuItemsModel> findByRestaurantId(int restaurantId) {
        // Only the fields needed for the items list are loaded at this stage.
        String sql = "SELECT id, restaurant_id, name, price, quantity, is_available "
                + "FROM menu_items WHERE restaurant_id = ? ORDER BY display_order, id";
        List<MenuItemsModel> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, restaurantId);
            try (ResultSet rows = statement.executeQuery()) {
                while (rows.next()) {
                    MenuItemsModel item = new MenuItemsModel();
                    item.setItem_id(rows.getInt("id"));
                    item.setRestaurant_id(rows.getInt("restaurant_id"));
                    item.setItem_name(rows.getString("name"));
                    item.setItem_price(rows.getBigDecimal("price"));
                    item.setItem_quantity(rows.getInt("quantity"));
                    item.setIs_available(rows.getBoolean("is_available"));
                    items.add(item);
                }
            }
            return items;
        } catch (SQLException exception) {
            throw new RuntimeException("Could not load store items.", exception);
        }
    }

    @Override
    public void save(MenuItemsModel item) {
        // Optional fields, including category_id, keep their database defaults.
        String sql = "INSERT INTO menu_items (restaurant_id, name, price, quantity, is_available) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, item.getRestaurant_id());
            statement.setString(2, item.getItem_name());
            statement.setBigDecimal(3, item.getItem_price());
            statement.setInt(4, item.getItem_quantity());
            statement.setBoolean(5, item.getIs_available());
            if (statement.executeUpdate() != 1) {
                throw new SQLException("The item was not saved.");
            }
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    item.setItem_id(keys.getInt(1));
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Could not save the item.", exception);
        }
    }
}
