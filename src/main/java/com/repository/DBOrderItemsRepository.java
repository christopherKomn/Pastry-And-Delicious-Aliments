package com.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ErrorCodes;
import com.models.OrderItemsModel;

/**
 * MySQL implementation of {@link IOrderItemsRepository}.
 */
public class DBOrderItemsRepository implements IOrderItemsRepository {

    private static final String SELECT_COLUMNS =
            "id, order_id, menu_item_id, quantity, special_instructions, selected_options";

    private final Connection connection;

    public DBOrderItemsRepository(Connection connection) {
        if (connection == null) {
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
                return resultSet.next() ? mapOrderItem(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find order item by ID", exception);
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
            return orderItems;
        } catch (SQLException exception) {
            throw databaseException("find all order items", exception);
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
            return orderItems;
        } catch (SQLException exception) {
            throw databaseException("find order items by order ID", exception);
        }
    }

    @Override
    public ErrorCodes save(OrderItemsModel orderItem) {
        if (orderItem == null) {
            return ErrorCodes.BAD_TYPE;
        }

        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity, "
                + "special_instructions, selected_options) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setOrderItemParameters(statement, orderItem, false);

            if (statement.executeUpdate() == 0) {
                return ErrorCodes.FAILED_TO_WRITE;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    return ErrorCodes.FAILED_TO_WRITE;
                }
                orderItem.setId(generatedKeys.getInt(1));
            }

            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            throw databaseException("save order item", exception);
        }
    }

    @Override
    public ErrorCodes update(OrderItemsModel orderItem) {
        if (orderItem == null) {
            return ErrorCodes.BAD_TYPE;
        }

        String sql = "UPDATE order_items SET order_id = ?, menu_item_id = ?, quantity = ?, "
                + "special_instructions = ?, selected_options = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setOrderItemParameters(statement, orderItem, true);
            return statement.executeUpdate() == 0
                    ? ErrorCodes.NOT_FOUND
                    : ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            throw databaseException("update order item", exception);
        }
    }

    @Override
    public ErrorCodes deleteById(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            return statement.executeUpdate() == 0
                    ? ErrorCodes.NOT_FOUND
                    : ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            throw databaseException("delete order item by ID", exception);
        }
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
