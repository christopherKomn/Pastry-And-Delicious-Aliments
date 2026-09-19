package com.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ErrorCodes;
import com.models.CustomerModel;
import com.models.OrderItemsModel;
import com.models.OrderModel;
import com.models.StoreManagerModel;

/**
 * MySQL implementation of {@link IOrderRepository}.
 */
public class DBOrderRepository implements IOrderRepository {
    private static final Logger LOGGER =
            Logger.getLogger(DBOrderRepository.class.getName());
            
    private static final String SELECT_COLUMNS =
            "id, customer_id, restaurant_id, status, subtotal, discount_amount, "
                    + "total_amount, payment_method, special_instructions, actual_delivery_time, "
                    + "created_at, updated_at, confirmed_at, prepared_at, picked_up_at, delivered_at";

    private final Connection connection;
    private final ICustomerRepository customerRepository;
    private final IStoreManagerRepository storeManagerRepository;

    public DBOrderRepository(Connection connection) {
        if (connection == null) {
            LOGGER.severe(
                "[constructor] Cannot create DBOrderRepository because the database connection is null."
            );
            throw new IllegalArgumentException("Connection cannot be null.");
        }

        this.connection = connection;
        this.customerRepository = new DBCustomerRepository(connection);
        this.storeManagerRepository = new DBStoreManagerRepository(connection);
    }

    @Override
    public ErrorCodes save(OrderModel order) {
        if (order == null) {
            throw new IllegalArgumentException(
                "order cannot be null"
            );
        }

        String sql = "INSERT INTO orders (customer_id, restaurant_id, status, subtotal, "
                + "discount_amount, total_amount, payment_method, special_instructions, "
                + "actual_delivery_time, confirmed_at, prepared_at, picked_up_at, delivered_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setOrderParameters(statement, order, false);

            if (statement.executeUpdate() == 0) {
                LOGGER.warning(() -> "[save] io write error , can't write order : " + order);
                return ErrorCodes.FAILED_TO_WRITE;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    LOGGER.warning(() -> "[save] io write error , can't write order : " + order);
                    return ErrorCodes.FAILED_TO_WRITE;
                }
                LOGGER.info(() -> "[save] order : " + order + " is saved !");
                order.setId(generatedKeys.getInt(1));
                return ErrorCodes.SUCCESS;
            }

            
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[save] Database error while saving order " + order + ".",
                    exception);
            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public ErrorCodes update(OrderModel order) {
        if (order == null) {
            throw new IllegalArgumentException(
                "order cannot be null"
            );
        }

        String sql = "UPDATE orders SET customer_id = ?, restaurant_id = ?, status = ?, "
                + "subtotal = ?, discount_amount = ?, total_amount = ?, payment_method = ?, "
                + "special_instructions = ?, actual_delivery_time = ?, confirmed_at = ?, "
                + "prepared_at = ?, picked_up_at = ?, delivered_at = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setOrderParameters(statement, order, true);
            if (statement.executeUpdate() == 0){
                LOGGER.warning(() -> "[update] can't find order : " + order);
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.info(() -> "[update]  order : " + order + " is updated! ");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[update] Database error while updating order " + order + ".",
                    exception);
            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public OrderModel findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                "id cannot be null"
            );
        }

        String sql = "SELECT " + SELECT_COLUMNS + " FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                OrderModel order = resultSet.next() ? mapOrder(resultSet) : null;
                if (order == null){
                    LOGGER.warning(() -> "[findById]  cannot find by id : " + id + " order ");
                    return null;
                }
                LOGGER.info(() -> "[findById] order : " + order + " retreived !");
                return order;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findById] Database error while searching order with id " + id + ".",
                    exception);
            return null;
        }
    }

    // To change on the future , should not use other repos
    @Override
    public CustomerModel findCustomerById(Long id) {
        Integer customerId = findRelatedId(id, "customer_id");
        return customerId == null ? null : customerRepository.findById(customerId);
    }

    // To change on the future , should not use other repos
    @Override
    public StoreManagerModel findRestaurantById(Long id) {
        Integer restaurantId = findRelatedId(id, "restaurant_id");
        return restaurantId == null ? null : storeManagerRepository.findById(restaurantId);
    }

    @Override
    public ErrorCodes deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                "id cannot be null"
            );
        }

        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            if (statement.executeUpdate() == 0){
                LOGGER.warning(() -> "[deleteById]  cannot find by id : " + id + " order ");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.info(() -> "[findById] order with id " + id + " deleted !");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[deleteById] Database error while searching order with id " + id + ".",
                    exception);
            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public OrderModel findByCustomerRestaurant(
            CustomerModel customer,
            StoreManagerModel restaurant) {
        if (customer == null || restaurant == null) {
            throw new IllegalArgumentException(
                "restaurant or/and customer are null"
            );
        }

        String sql = "SELECT " + SELECT_COLUMNS + " FROM orders "
                + "WHERE customer_id = ? AND restaurant_id = ? "
                + "ORDER BY created_at DESC, id DESC LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customer.getId());
            statement.setInt(2, restaurant.getRestaurant_id());
            try (ResultSet resultSet = statement.executeQuery()) {
                OrderModel order =  resultSet.next() ? mapOrder(resultSet) : null;
                if (order == null){
                    LOGGER.warning(() -> "[findByCustomerRestaurant] "+
                    "no order found related with customer \""+customer + 
                    "\" and store manager \"" + restaurant + ".");
                    return null;
                }
                LOGGER.info(() -> "[findByCustomerRestaurant] "+
                "order \"" + order + "\" found related with customer \""+customer + 
                "\" and store manager \"" + restaurant + ".");
                return order;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findByCustomerRestaurant] Database error while searching for order related with "
                     + "customer \"" + customer + "\" and restaurant \"" + restaurant + ".",
                    exception);
            return null;
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
            throw new IllegalArgumentException(
                "orderId is null"
            );
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
            if (orderItems.isEmpty()){
                LOGGER.warning(() -> "[findAllOrderItemsByOrderId]  cannot find order items or order with related " +
                " order Id : " + orderId + " . ");
                return null;
            }
            LOGGER.info(() -> "[findAllOrderItemsByOrderId]  found total order items " + 
            orderItems.size() +" with related " +
            " order Id : " + orderId + " . ");
            return orderItems;
        } catch (SQLException exception) {
            LOGGER.log(
                Level.SEVERE,
                "[findAllOrderItemsByOrderId] Database error while searching order items of order id " + orderId + ".",
                exception
            );
            return null;
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
            if (orders.isEmpty()){
                LOGGER.warning(() -> "[findAll]  cannot find orders . ");
                return null;
            }
            LOGGER.info(() -> "[findAll]  found total " + orders.size() + " orders . ");
            return orders;
        } catch (SQLException exception) {
            LOGGER.log(
                Level.SEVERE,
                "[findAll] Database error while searching orders .",
                exception
            );
            return null;
        }
    }

















    private List<OrderModel> findByRelatedId(Long relatedId, String columnName) {
        List<OrderModel> orders = new ArrayList<>();
        if (relatedId == null || columnName == null) {
            throw new IllegalArgumentException(
                "relatedId or columnName is null"
            );
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
            if (orders.isEmpty()){
                LOGGER.warning(() -> "[findByRelatedId]  cannot find orders . ");
                return null;
            }
            LOGGER.info(() -> "[findByRelatedId]  found total " + orders.size() + " orders . ");
            return orders;
        } catch (SQLException exception) {
            LOGGER.log(
                Level.SEVERE,
                "[findByRelatedId] Database error while searching orders .",
                exception
            );
            return null;
        }
    }

    private Integer findRelatedId(Long orderId, String columnName) {
        if (orderId == null || columnName == null) {
            throw new IllegalArgumentException(
                "relatedId or columnName is null"
            );
        }

        // columnName is supplied only by this class, not by external input.
        String sql = "SELECT " + columnName + " FROM orders WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                Integer res = resultSet.next() ? resultSet.getInt(columnName) : null;
                if (res == null){
                    LOGGER.warning(() -> "[findRelatedId]  could not find  a \"" + columnName + " from order id " + 
                orderId);
                    return null;
                }
                LOGGER.info(() -> "[findRelatedId]  found  a \"" + columnName + " from order id " + 
                orderId);
                return res;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                Level.SEVERE,
                "[findRelatedId] Database error while searching orders .",
                exception
            );
            return null;
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
