package com.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ErrorCodes;
import com.models.StoreManagerModel;

public class DBStoreManagerRepository implements IStoreManagerRepository {
    private static final String SELECT_COLUMNS = "id, owner_id, name, description, logo_url, cover_image_url, "
            + "cuisine_type, phone, email, address_line1, address_line2, city, state, postal_code, website, "
            + "is_active, is_accepting_orders, min_order_amount, delivery_fee, rating, total_reviews, "
            + "created_at, updated_at";

    private final Connection connection;

    public DBStoreManagerRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public StoreManagerModel findById(int id) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM restaurants WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapStoreManager(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find restaurant by ID", exception);
        }
    }

    @Override
    public List<StoreManagerModel> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM restaurants ORDER BY id";
        List<StoreManagerModel> storeManagers = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                storeManagers.add(mapStoreManager(resultSet));
            }
            return storeManagers;
        } catch (SQLException exception) {
            throw databaseException("find all restaurants", exception);
        }
    }

    @Override
    public ErrorCodes save(StoreManagerModel storeManager) {
        String sql = "INSERT INTO restaurants (owner_id, name, description, logo_url, cover_image_url, "
                + "cuisine_type, phone, email, address_line1, address_line2, city, state, postal_code, website, "
                + "is_active, is_accepting_orders, min_order_amount, delivery_fee) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStoreManagerParameters(statement, storeManager, false);
            if (statement.executeUpdate() == 0) {
                return ErrorCodes.IO_ERROR;
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    storeManager.setRestaurant_id(generatedKeys.getInt(1));
                }else {
                    return ErrorCodes.NOT_FOUND;
                }
            }
        } catch (SQLException exception) {
            throw databaseException("save restaurant", exception);
        }
        return ErrorCodes.SUCCESS;
    }

    @Override
    public ErrorCodes update(StoreManagerModel storeManager) {
        String sql = "UPDATE restaurants SET owner_id = ?, name = ?, description = ?, logo_url = ?, "
                + "cover_image_url = ?, cuisine_type = ?, phone = ?, email = ?, address_line1 = ?, "
                + "address_line2 = ?, city = ?, state = ?, postal_code = ?, website = ?, is_active = ?, "
                + "is_accepting_orders = ?, min_order_amount = ?, delivery_fee = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStoreManagerParameters(statement, storeManager, true);
            if (statement.executeUpdate() == 0) {
                return ErrorCodes.NOT_FOUND;
            }
        } catch (SQLException exception) {
            throw databaseException("update restaurant", exception);
        }
        return ErrorCodes.SUCCESS;
    }

    @Override
    public ErrorCodes deleteById(int id) {
        String sql = "DELETE FROM restaurants WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            if (statement.getUpdateCount() == 0) {
                return ErrorCodes.NOT_FOUND;
            }
        } catch (SQLException exception) {
            throw databaseException("delete restaurant by id " + id, exception);
        }
        return ErrorCodes.SUCCESS;
    }

    @Override
    public StoreManagerModel findByOwnerId(int ownerId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM restaurants WHERE owner_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapStoreManager(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find restaurant by owner ID", exception);
        }
    }

    @Override
    public StoreManagerModel findByNCAP(String name, String city, String addressLine1, String postalCode) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM restaurants WHERE name = ? AND city = ? AND address_line1 = ? AND postal_code = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, city);
            statement.setString(3, addressLine1);
            statement.setString(4, postalCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapStoreManager(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find restaurant by NCAP", exception);
        }
    }

    private StoreManagerModel mapStoreManager(ResultSet resultSet) throws SQLException {
        return new StoreManagerModel(
                resultSet.getInt("id"),
                resultSet.getInt("owner_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("logo_url"),
                resultSet.getString("cover_image_url"),
                resultSet.getString("cuisine_type"),
                resultSet.getString("phone"),
                resultSet.getString("email"),
                resultSet.getString("address_line1"),
                resultSet.getString("address_line2"),
                resultSet.getString("city"),
                resultSet.getString("state"),
                resultSet.getString("postal_code"),
                resultSet.getString("website"),
                resultSet.getBoolean("is_active"),
                resultSet.getBoolean("is_accepting_orders"),
                resultSet.getDouble("min_order_amount"),
                resultSet.getDouble("delivery_fee"),
                resultSet.getDouble("rating"),
                resultSet.getInt("total_reviews"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("updated_at"));
    }

    private void setStoreManagerParameters(PreparedStatement statement, StoreManagerModel storeManager,
            boolean includeId) throws SQLException {
        statement.setInt(1, storeManager.getOwner_id());
        statement.setString(2, storeManager.getName());
        statement.setString(3, storeManager.getDescription());
        statement.setString(4, storeManager.getLogo_url());
        statement.setString(5, storeManager.getCover_image_url());
        statement.setString(6, storeManager.getCuisine_type());
        statement.setString(7, storeManager.getPhone());
        statement.setString(8, storeManager.getEmail());
        statement.setString(9, storeManager.getAddress_line1());
        statement.setString(10, storeManager.getAddress_line2());
        statement.setString(11, storeManager.getCity());
        statement.setString(12, storeManager.getState());
        statement.setString(13, storeManager.getPostal_code());
        statement.setString(14, storeManager.getWebsite());
        statement.setBoolean(15, storeManager.isIs_active());
        statement.setBoolean(16, storeManager.isIs_accepting_orders());
        statement.setDouble(17, storeManager.getMin_order_amount());
        statement.setDouble(18, storeManager.getDelivery_fee());
        if (includeId) {
            statement.setInt(19, storeManager.getRestaurant_id());
        }
    }

    private RuntimeException databaseException(String operation, SQLException exception) {
        return new RuntimeException("Could not " + operation, exception);
    }   
}