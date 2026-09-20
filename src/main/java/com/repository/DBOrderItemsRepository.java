package com.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ErrorCodes;
import com.models.MenuItemsModel;
import com.models.OrderItemsModel;

/**
 * MySQL implementation of {@link IOrderItemsRepository}.
 */
public class DBOrderItemsRepository implements IOrderItemsRepository {
    private static final Logger LOGGER =
            Logger.getLogger(DBOrderItemsRepository.class.getName());
            
    private static final String SELECT_COLUMNS =
            "id, order_id, menu_item_id, quantity, special_instructions, selected_options";

    private final Connection connection;

    public DBOrderItemsRepository(Connection connection) {
        if (connection == null) {
            LOGGER.severe(
                "[constructor] Cannot create DBOrderItemsRepository because the database connection is null."
            );
            throw new IllegalArgumentException("Connection cannot be null.");
        }
        this.connection = connection;
    }

    @Override
    public OrderItemsModel findById(int id) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM order_items WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                OrderItemsModel orderItem = resultSet.next() ? mapOrderItem(resultSet) : null;
                if (orderItem == null)
                {
                    LOGGER.warning(() -> "[findById] order item with id  : " + id + " is not found !");
                    return null;
                }
                LOGGER.info(() -> "[findById] order item " + orderItem + " found !");
                return orderItem;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findById] Database error while searching order item with ID " + id + ".",
                    exception);
            return null;
        }
    }

    @Override
    public List<OrderItemsModel> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM order_items ORDER BY id";
        List<OrderItemsModel> orderItems = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orderItems.add(mapOrderItem(resultSet));
            }

            if (orderItems.isEmpty()){
                LOGGER.warning(() -> "[findAll] order items not found !");
                return null;
            }
            LOGGER.info(() -> "[findAll] found total " + orderItems.size() + " order items !");
            return orderItems;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findAll] Database error while searching order items .",
                    exception);
            return null;
        }
    }

    @Override
    public List<OrderItemsModel> findByOrderId(int orderId) {
        String sql = "SELECT " + SELECT_COLUMNS
                + " FROM order_items WHERE order_id = ? ORDER BY id";
        List<OrderItemsModel> orderItems = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orderItems.add(mapOrderItem(resultSet));
                }
            }
            if (orderItems.isEmpty()){
                LOGGER.warning(() -> "[findByOrderId] order items not found for Order with ID " + orderId + "!");
                return null;
            }
            LOGGER.info(() -> "[findByOrderId] found total " + orderItems.size() + " order items for Order with ID " + orderId + "!");
            return orderItems;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findByOrderId] Database error while searching order items for Order with ID : " + orderId + ".",
                    exception);
            return null;
        }
    }

    @Override
    public ErrorCodes save(OrderItemsModel orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException(
                "orderItem cannot be null"
            );
        }

        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity, "
                + "special_instructions, selected_options) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setOrderItemParameters(statement, orderItem, false);

            if (statement.executeUpdate() == 0) {
                LOGGER.warning(() -> "[save] order items " + orderItem + " not able to saved !");
                return ErrorCodes.FAILED_TO_WRITE;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    LOGGER.warning(() -> "[save] order items \"" + orderItem + "\" not able to saved !");
                    return ErrorCodes.FAILED_TO_WRITE;
                }
                orderItem.setId(generatedKeys.getInt(1));
            }
            LOGGER.info(() -> "[save] order items \"" + orderItem + "\" saved !");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[save] Database error while saving order item \"" + orderItem + "\".",
                    exception);

            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public ErrorCodes update(OrderItemsModel orderItem) {
        if (orderItem == null) {
            throw new IllegalArgumentException(
                "orderItem cannot be null"
            );
        }

        String sql = "UPDATE order_items SET order_id = ?, menu_item_id = ?, quantity = ?, "
                + "special_instructions = ?, selected_options = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setOrderItemParameters(statement, orderItem, true);
            
            if ( statement.executeUpdate() == 0 ){
                LOGGER.warning(() -> "[update] order item \"" + orderItem + "\" not found !");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.info(() -> "[update] order item \"" + orderItem + "\" updated !");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[update] Database error while updating order item \"" + orderItem + "\".",
                    exception);

             return ErrorCodes.IO_ERROR;
            
        }
    }

    @Override
    public ErrorCodes UpdateOrderItems(List<OrderItemsModel> orderItems) {
        if (orderItems == null) {
            LOGGER.warning("[UpdateOrderItems] orderItems cannot be null.");
            throw new IllegalArgumentException("orderItems cannot be null");
        }
        for (OrderItemsModel item : orderItems) {
            if (item == null) {
                LOGGER.warning("[UpdateOrderItems] The list contains a null item; no updates performed.");
                return ErrorCodes.FAILED_TO_WRITE;
            }
        }
        if (orderItems.isEmpty()) {
            LOGGER.info("[UpdateOrderItems] No items to update.");
            return ErrorCodes.SUCCESS;
        }

        ErrorCodes result = ErrorCodes.SUCCESS;
        boolean ownsTransaction = false;
        boolean started = false;
        boolean resolved = false;
        Savepoint savepoint = null;
        try {
            ownsTransaction = connection.getAutoCommit();
            if (ownsTransaction) {
                connection.setAutoCommit(false);
            } else {
                // Keep any work already performed by the caller in its transaction.
                savepoint = connection.setSavepoint();
            }
            started = true;

            for (OrderItemsModel item : orderItems) {
                result = update(item);
                if (result != ErrorCodes.SUCCESS) {
                    LOGGER.warning("[UpdateOrderItems] Update failed for item ID "
                            + item.getId() + ": " + result + ". Rolling back batch.");
                    break;
                }
            }

            if (result == ErrorCodes.SUCCESS) {
                if (ownsTransaction) {
                    connection.commit();
                } else {
                    connection.releaseSavepoint(savepoint);
                }
                resolved = true;
            }
        } catch (SQLException | RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "[UpdateOrderItems] Batch update failed.", exception);
            result = ErrorCodes.IO_ERROR;
        } finally {
            if (started && !resolved) {
                try {
                    if (ownsTransaction) {
                        connection.rollback();
                    } else {
                        connection.rollback(savepoint);
                    }
                    resolved = true;
                    if (!ownsTransaction) {
                        connection.releaseSavepoint(savepoint);
                    }
                } catch (SQLException exception) {
                    LOGGER.log(Level.SEVERE, "[UpdateOrderItems] Rollback or savepoint cleanup failed.", exception);
                    result = ErrorCodes.IO_ERROR;
                }
            }
            // Enabling auto-commit after a failed rollback could commit partial updates.
            if (ownsTransaction && started && resolved) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException exception) {
                    LOGGER.log(Level.SEVERE, "[UpdateOrderItems] Could not restore auto-commit.", exception);
                    result = ErrorCodes.IO_ERROR;
                }
            }
        }

        if (result == ErrorCodes.SUCCESS) {
            LOGGER.info("[UpdateOrderItems] Updated " + orderItems.size() + " items"
                    + (ownsTransaction ? " and committed batch." : "; awaiting caller transaction commit."));
        }
        return result;
    }

    @Override
    public ErrorCodes deleteById(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if ( statement.executeUpdate() == 0 ){
                LOGGER.warning(() -> "[deleteById] order item with ID : " + id + " not found !");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.info(() -> "[deleteById] order item with ID : " + id + " deleted !");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[deleteById] Database error while deleting order item with id" + id + ".",
                    exception);

             return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public List<MenuItemsModel> getMenuItemsByOrderItems(List<OrderItemsModel> orderItems) {
        List<MenuItemsModel> menuItems = new ArrayList<>();
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException(
                "orderItems cannot be null"
            );
        }

        String sql = "SELECT id, restaurant_id, name, price, quantity, is_available "
                + "FROM menu_items WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (OrderItemsModel orderItem : orderItems) {
                if (orderItem == null) {
                    menuItems.add(null);
                    continue;
                }

                statement.setInt(1, orderItem.getMenu_item_id());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        menuItems.add(null);
                        continue;
                    }
                    menuItems.add(mapMenuItem(resultSet));
                }
            }

            if (menuItems.isEmpty()){
                LOGGER.warning(() -> "[getMenuItemsByOrderItems] "+
                "no menu items matching !");
                return null;
            }
            LOGGER.info(() -> "[getMenuItemsByOrderItems] "+
            "total : " + menuItems.size() + " menu items matching !");
            return menuItems;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[update] Database error while searching for matching order items .",
                    exception);

             return null;
        }
    }

    private MenuItemsModel mapMenuItem(ResultSet resultSet) throws SQLException {
        MenuItemsModel menuItem = new MenuItemsModel();
        menuItem.setItem_id(resultSet.getInt("id"));
        menuItem.setRestaurant_id(resultSet.getInt("restaurant_id"));
        menuItem.setItem_name(resultSet.getString("name"));
        menuItem.setItem_price(resultSet.getBigDecimal("price"));
        menuItem.setItem_quantity(resultSet.getInt("quantity"));
        menuItem.setIs_available(resultSet.getBoolean("is_available"));
        return menuItem;
    }

    private OrderItemsModel mapOrderItem(ResultSet resultSet) throws SQLException {
        OrderItemsModel orderItem = new OrderItemsModel();
        orderItem.setId(resultSet.getInt("id"));
        orderItem.setOrder_id(resultSet.getInt("order_id"));
        orderItem.setMenu_item_id(resultSet.getInt("menu_item_id"));
        orderItem.setQuantity(resultSet.getInt("quantity"));
        orderItem.setSpecial_instructions(resultSet.getString("special_instructions"));
        orderItem.setSelected_options(resultSet.getString("selected_options"));
        return orderItem;
    }

    private void setOrderItemParameters(
            PreparedStatement statement,
            OrderItemsModel orderItem,
            boolean includeId) throws SQLException {
        statement.setInt(1, orderItem.getOrder_id());
        statement.setInt(2, orderItem.getMenu_item_id());
        statement.setInt(3, orderItem.getQuantity());
        statement.setString(4, orderItem.getSpecial_instructions());
        statement.setString(5, orderItem.getSelected_options());

        if (includeId) {
            statement.setInt(6, orderItem.getId());
        }
    }

    private RuntimeException databaseException(String operation, SQLException exception) {
        return new RuntimeException("Could not " + operation, exception);
    }
}
