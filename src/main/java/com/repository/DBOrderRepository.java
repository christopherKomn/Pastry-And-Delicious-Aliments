package com.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ErrorCodes;
import com.models.CustomerModel;
import com.models.OrderItemsModel;
import com.models.OrderModel;
import com.models.StoreManagerModel;

/**
 * MySQL implementation of {@link IOrderRepository}.
 */
public class DBOrderRepository implements IOrderRepository {

    private static final String SELECT_COLUMNS =
            "id, customer_id, restaurant_id, status, subtotal, discount_amount, "
                    + "total_amount, payment_method, special_instructions, actual_delivery_time, "
                    + "created_at, updated_at, confirmed_at, prepared_at, picked_up_at, delivered_at";

    private final Connection connection;
    private final ICustomerRepository customerRepository;
    private final IStoreManagerRepository storeManagerRepository;

    public DBOrderRepository(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.customerRepository = new DBCustomerRepository(connection);
        this.storeManagerRepository = new DBStoreManagerRepository(connection);
    }

    @Override
    public ErrorCodes save(OrderModel order) {
        if (order == null) {
            return ErrorCodes.BAD_TYPE;
        }

        String sql = "INSERT INTO orders (customer_id, restaurant_id, status, subtotal, "
                + "discount_amount, total_amount, payment_method, special_instructions, "
                + "actual_delivery_time, confirmed_at, prepared_at, picked_up_at, delivered_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setOrderParameters(statement, order, false);

            if (statement.executeUpdate() == 0) {
                return ErrorCodes.FAILED_TO_WRITE;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    return ErrorCodes.FAILED_TO_WRITE;
                }
                order.setId(generatedKeys.getInt(1));
            }

            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            throw databaseException("save order", exception);
        }
    }

    @Override
    public ErrorCodes update(OrderModel order) {
        if (order == null) {
            return ErrorCodes.BAD_TYPE;
        }

        String sql = "UPDATE orders SET customer_id = ?, restaurant_id = ?, status = ?, "
                + "subtotal = ?, discount_amount = ?, total_amount = ?, payment_method = ?, "
                + "special_instructions = ?, actual_delivery_time = ?, confirmed_at = ?, "
                + "prepared_at = ?, picked_up_at = ?, delivered_at = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setOrderParameters(statement, order, true);
            return statement.executeUpdate() == 0
                    ? ErrorCodes.NOT_FOUND
                    : ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            throw databaseException("update order", exception);
        }
    }

    @Override
    public OrderModel findById(Long id) {
        if (id == null) {
            return null;
        }

        String sql = "SELECT " + SELECT_COLUMNS + " FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapOrder(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find order by ID", exception);
        }
    }

    @Override
    public CustomerModel findCustomerById(Long id) {
        Integer customerId = findRelatedId(id, "customer_id");
        return customerId == null ? null : customerRepository.findById(customerId);
    }

    @Override
    public StoreManagerModel findRestaurantById(Long id) {
        Integer restaurantId = findRelatedId(id, "restaurant_id");
        return restaurantId == null ? null : storeManagerRepository.findById(restaurantId);
    }

    @Override
    public ErrorCodes deleteById(Long id) {
        if (id == null) {
            return ErrorCodes.NOT_FOUND;
        }

        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 0
                    ? ErrorCodes.NOT_FOUND
                    : ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            throw databaseException("delete order by ID", exception);
        }
    }

    @Override
    public OrderModel findByCustomerRestaurant(
            CustomerModel customer,
            StoreManagerModel restaurant) {
        if (customer == null || restaurant == null) {
            return null;
        }

        String sql = "SELECT " + SELECT_COLUMNS + " FROM orders "
                + "WHERE customer_id = ? AND restaurant_id = ? "
                + "ORDER BY created_at DESC, id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customer.getId());
            statement.setInt(2, restaurant.getRestaurant_id());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapOrder(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find order by customer and restaurant", exception);
        }
    }

    @Override
    public List<OrderModel> findByCustomerId(Long customerId) {
        return findByRelatedId(customerId, "customer_id");
    }

    @Override
    public List<OrderModel> findByRestaurantId(Long restaurantId) {
        return findByRelatedId(restaurantId, "restaurant_id");
    }

    @Override
    public List<OrderItemsModel> findAllOrderItemsByOrderId(Long orderId) {
        List<OrderItemsModel> orderItems = new ArrayList<>();
        if (orderId == null) {
            return orderItems;
        }

        String sql = "SELECT id, order_id, menu_item_id, quantity, "
                + "special_instructions, selected_options "
                + "FROM order_items WHERE order_id = ? ORDER BY id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    OrderItemsModel item = new OrderItemsModel();
                    item.setId(resultSet.getInt("id"));
                    item.setOrder_id(resultSet.getInt("order_id"));
                    item.setMenu_item_id(resultSet.getInt("menu_item_id"));
                    item.setQuantity(resultSet.getInt("quantity"));
                    item.setSpecial_instructions(resultSet.getString("special_instructions"));
                    item.setSelected_options(resultSet.getString("selected_options"));
                    orderItems.add(item);
                }
            }
            return orderItems;
        } catch (SQLException exception) {
            throw databaseException("find order items by order ID", exception);
        }
    }

    @Override
    public List<OrderModel> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM orders ORDER BY created_at DESC, id DESC";
        List<OrderModel> orders = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                orders.add(mapOrder(resultSet));
            }
            return orders;
        } catch (SQLException exception) {
            throw databaseException("find all orders", exception);
        }
    }

    private List<OrderModel> findByRelatedId(Long relatedId, String columnName) {
        List<OrderModel> orders = new ArrayList<>();
        if (relatedId == null) {
            return orders;
        }

        // columnName is supplied only by this class, not by external input.
        String sql = "SELECT " + SELECT_COLUMNS + " FROM orders WHERE " + columnName
                + " = ? ORDER BY created_at DESC, id DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, relatedId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(mapOrder(resultSet));
                }
            }
            return orders;
        } catch (SQLException exception) {
            throw databaseException("find orders by " + columnName, exception);
        }
    }

    private Integer findRelatedId(Long orderId, String columnName) {
        if (orderId == null) {
            return null;
        }

        // columnName is supplied only by this class, not by external input.
        String sql = "SELECT " + columnName + " FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(columnName) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find " + columnName + " for order", exception);
        }
    }

    private void setOrderParameters(
            PreparedStatement statement,
            OrderModel order,
            boolean includeId) throws SQLException {
        statement.setInt(1, order.getCustomer_id());
        statement.setInt(2, order.getRestaurant_id());
        statement.setString(3, order.getStatus());
        statement.setBigDecimal(4, order.getSubtotal());
        statement.setBigDecimal(5, order.getDiscount_amount());
        statement.setBigDecimal(6, order.getTotal_amount());
        statement.setString(7, order.getPayment_method());
        statement.setString(8, order.getSpecial_instructions());
        statement.setTimestamp(9, order.getActual_delivery_time());
        statement.setTimestamp(10, order.getConfirmed_at());
        statement.setTimestamp(11, order.getPrepared_at());
        statement.setTimestamp(12, order.getPicked_up_at());
        statement.setTimestamp(13, order.getDelivered_at());

        if (includeId) {
            statement.setInt(14, order.getId());
        }
    }

    private OrderModel mapOrder(ResultSet resultSet) throws SQLException {
        return new OrderModel(
                resultSet.getInt("id"),
                resultSet.getInt("customer_id"),
                resultSet.getInt("restaurant_id"),
                resultSet.getString("status"),
                resultSet.getBigDecimal("subtotal"),
                resultSet.getBigDecimal("discount_amount"),
                resultSet.getBigDecimal("total_amount"),
                resultSet.getString("payment_method"),
                resultSet.getString("special_instructions"),
                resultSet.getTimestamp("actual_delivery_time"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("updated_at"),
                resultSet.getTimestamp("confirmed_at"),
                resultSet.getTimestamp("prepared_at"),
                resultSet.getTimestamp("picked_up_at"),
                resultSet.getTimestamp("delivered_at"));
    }

    private RuntimeException databaseException(String operation, SQLException exception) {
        return new RuntimeException("Could not " + operation, exception);
    }
}
