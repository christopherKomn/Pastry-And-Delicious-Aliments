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
import com.models.StoreManagerModel;

public class DBStoreManagerRepository implements IStoreManagerRepository {
    private static final Logger LOGGER =
            Logger.getLogger(DBStoreManagerRepository.class.getName());

    private static final String SELECT_COLUMNS = "id, owner_id, name, description, logo_url, cover_image_url, "
            + "cuisine_type, phone, email, address_line1, address_line2, city, state, postal_code, website, "
            + "is_active, is_accepting_orders, min_order_amount, delivery_fee, rating, total_reviews, "
            + "created_at, updated_at";

    private final Connection connection;

    public DBStoreManagerRepository(Connection connection) {
        if (connection == null) {
            LOGGER.severe(
                "[constructor] Cannot create DBStoreManagerRepository because the database connection is null."
            );

            throw new IllegalArgumentException(
                "Database connection cannot be null"
            );
        }

        this.connection = connection;
    }

    @Override
    public StoreManagerModel findById(int id) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM restaurants WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                StoreManagerModel store = 
                resultSet.next() ? mapStoreManager(resultSet) : null;
                if (store == null){
                    LOGGER.fine(() -> "[findById] No restaurant was found with ID " + id + ".");
                    return null;
                }
                LOGGER.fine(() -> "[findById] Found restaurant: " + store);
                return store;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findById] Database error while retrieving restaurant with ID " + id + ".",
                    exception);
            return null;
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

            if (storeManagers.isEmpty() ){
                LOGGER.fine("[findAll] No restaurants were found.");
                return null;
            }

            LOGGER.fine(() -> "[findAll] Retrieved "
                    + storeManagers.size() + " restaurant(s).");
            return storeManagers;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findAll] Database error while retrieving all restaurants.",
                    exception);

            return null;
        }
    }

    @Override
    public ErrorCodes save(StoreManagerModel storeManager) {
        if (storeManager == null){
            throw new IllegalArgumentException(
                "storeManager parameter cannot be null"
            );
        }
        String sql = "INSERT INTO restaurants (owner_id, name, description, logo_url, cover_image_url, "
                + "cuisine_type, phone, email, address_line1, address_line2, city, state, postal_code, website, "
                + "is_active, is_accepting_orders, min_order_amount, delivery_fee) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStoreManagerParameters(statement, storeManager, false);
            if (statement.executeUpdate() == 0) {
                LOGGER.warning("[save] Restaurant insert affected no rows: " + storeManager);
                return ErrorCodes.FAILED_TO_WRITE;
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    storeManager.setRestaurant_id(generatedKeys.getInt(1));
                    LOGGER.fine(() -> "[save] Restaurant saved successfully: " + storeManager);
                    return ErrorCodes.SUCCESS;
                }else {
                    LOGGER.warning("[save] Restaurant was inserted, but no generated ID was returned: "
                            + storeManager);
                    return ErrorCodes.FAILED_TO_WRITE;
                }
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[save] Database error while saving restaurant: " + storeManager,
                    exception);
            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public ErrorCodes update(StoreManagerModel storeManager) {
        if (storeManager == null){
            throw new IllegalArgumentException(
                "storeManager parameter cannot be null"
            );
        }

        String sql = "UPDATE restaurants SET owner_id = ?, name = ?, description = ?, logo_url = ?, "
                + "cover_image_url = ?, cuisine_type = ?, phone = ?, email = ?, address_line1 = ?, "
                + "address_line2 = ?, city = ?, state = ?, postal_code = ?, website = ?, is_active = ?, "
                + "is_accepting_orders = ?, min_order_amount = ?, delivery_fee = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStoreManagerParameters(statement, storeManager, true);
            if (statement.executeUpdate() == 0) {
                LOGGER.fine(() -> "[update] No restaurant was found with ID "
                        + storeManager.getRestaurant_id() + ".");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.fine(() -> "[update] Restaurant updated successfully: " + storeManager);
            return ErrorCodes.SUCCESS;
            
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[update] Database error while updating restaurant: " + storeManager,
                    exception);
            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public ErrorCodes deleteById(int id) {
        String sql = "DELETE FROM restaurants WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            if (statement.getUpdateCount() == 0) {
                LOGGER.fine(() -> "[deleteById] No restaurant was found with ID " + id + ".");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.fine(() -> "[deleteById] Deleted restaurant with ID " + id + ".");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[deleteById] Database error while deleting restaurant with ID " + id + ".",
                    exception);
            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public StoreManagerModel findByOwnerId(int ownerId) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM restaurants WHERE owner_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                StoreManagerModel store = resultSet.next() ? mapStoreManager(resultSet) : null;
                if (store == null){
                    LOGGER.fine(() -> "[findByOwnerId] No restaurant was found for owner ID "
                            + ownerId + ".");
                    return null;
                }
                LOGGER.fine(() -> "[findByOwnerId] Found restaurant for owner ID "
                        + ownerId + ": " + store);
                return store;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findByOwnerId] Database error while retrieving restaurant for owner ID "
                            + ownerId + ".",
                    exception);
            return null;
        }
    }

    /** InnoDB deletes the restaurant and its dependent rows atomically via ON DELETE CASCADE. */
    @Override
    public ErrorCodes deleteByOwnerId(int ownerId) {
        String sql = "DELETE FROM restaurants WHERE owner_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId);
            int updateCount = statement.executeUpdate();
            if (updateCount == 0){
                LOGGER.fine(() -> "[deleteByOwnerId] No restaurants were found for owner ID "
                        + ownerId + ".");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.fine(() -> "[deleteByOwnerId] Deleted " + updateCount
                    + " restaurant(s) for owner ID " + ownerId + ".");
            return  ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[deleteByOwnerId] Database error while deleting restaurants for owner ID "
                            + ownerId + ".",
                    exception);

            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public StoreManagerModel findByNCAP(String name, String city, String addressLine1, String postalCode) {
        if ( (name == null) || (city == null) || (addressLine1 == null) || (postalCode == null)){
            throw new IllegalArgumentException(
                "name or/and city or/and address or/and postalCode parameters are null"
            );
        }

        String sql = "SELECT " + SELECT_COLUMNS + " FROM restaurants WHERE name = ? AND city = ? AND address_line1 = ? AND postal_code = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, city);
            statement.setString(3, addressLine1);
            statement.setString(4, postalCode);

            try (ResultSet resultSet = statement.executeQuery()) {
                StoreManagerModel store = resultSet.next() ? mapStoreManager(resultSet) : null;
                if (store == null){
                    LOGGER.fine(() -> "[findByNCAP] No restaurant matched name='" + name
                            + "', city='" + city + "', address='" + addressLine1
                            + "', postalCode='" + postalCode + "'.");
                    return null;
                }
                LOGGER.fine(() -> "[findByNCAP] Found restaurant: " + store);
                return store;
            }
        } catch (SQLException exception) {
           LOGGER.log(
                    Level.SEVERE,
                    "[findByNCAP] Database error while retrieving restaurant with name='"
                            + name + "', city='" + city + "', address='" + addressLine1
                            + "', postalCode='" + postalCode + "'.",
                    exception);

            return null;
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

}
