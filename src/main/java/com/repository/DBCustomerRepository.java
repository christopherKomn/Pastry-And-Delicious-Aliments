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
public class DBCustomerRepository implements ICustomerRepository {
    private static final Logger LOGGER =
            Logger.getLogger(DBCustomerRepository.class.getName());

    private static final String SELECT_COLUMNS = "id, user_id, fullname, city, state, postal_code, "
            + "address_line1, address_line2";

    private final Connection connection;

    public DBCustomerRepository(Connection connection) {
        if (connection == null) {
            LOGGER.severe(
                "[constructor] Cannot create DBCustomerRepository because the database connection is null."
            );
            throw new IllegalArgumentException("Connection cannot be null.");
        }
        this.connection = connection;
    }

    @Override
    public CustomerModel findById(int id) {

        String sql = "SELECT " + SELECT_COLUMNS + " FROM customer WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                CustomerModel model = resultSet.next() ? mapCustomer(resultSet) : null;

                if (model == null){
                    LOGGER.warning(() -> "[findById] customer with id  : " + id + " is not found !");
                    return null;
                }
                LOGGER.info(() -> "[findById] customer " + model + " found !");
                return model;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findById] Database error while searching customer with ID " + id + ".",
                    exception);
            return null;
        }

    }

    @Override
    public List<CustomerModel> findAll() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM customer ORDER BY id";
        List<CustomerModel> customers = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                customers.add(mapCustomer(resultSet));
            }
            if (customers.isEmpty()){
                LOGGER.warning(() -> "[findAll] customers not found !");
                return null;
            }
            LOGGER.info(() -> "[findAll] found total " + customers.size() + " customers !");
            return customers;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findAll] Database error while searching customers .",
                    exception);
            return null;
        }
    
    }

    @Override
    public ErrorCodes save(CustomerModel customer) {
        if (customer == null){
            throw new IllegalArgumentException(
                "customer cannot be null"
            );
        }

        String sql = "INSERT INTO customer (user_id, fullname, city, state, postal_code, address_line1, "
                + "address_line2) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setCustomerParameters(statement, customer, false);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    LOGGER.warning(() -> "[save] customer \"" + customer + "\" not able to saved !");
                    return ErrorCodes.FAILED_TO_WRITE;
                }
                customer.setId(generatedKeys.getInt(1));
            }
            LOGGER.info(() -> "[save] customer \"" + customer + "\" saved !");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[save] Database error while saving customer \"" + customer + "\".",
                    exception);

            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public ErrorCodes update(CustomerModel customer) {
        if (customer == null){
            throw new IllegalArgumentException(
                "customer cannot be null"
            );
        }

        String sql = "UPDATE customer SET user_id = ?, fullname = ?, city = ?, state = ?, postal_code = ?, "
                + "address_line1 = ?, address_line2 = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setCustomerParameters(statement, customer, true);
            if ( statement.executeUpdate() == 0 ){
                LOGGER.warning(() -> "[update] customer \"" + customer + "\" not found !");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.info(() -> "[update] customer \"" + customer + "\" updated !");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[save] Database error while saving customer \"" + customer + "\".",
                    exception);

            return ErrorCodes.IO_ERROR;
        }
        
    }
        

    @Override
    public ErrorCodes deleteById(int id) {
        String sql = "DELETE FROM customer WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            if ( statement.executeUpdate() == 0 ){
                LOGGER.warning(() -> "[deleteById] customer with ID : " + id + " not found !");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.info(() -> "[deleteById] customer with ID : " + id + " deleted !");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[deleteById] Database error while deleting customer with id" + id + ".",
                    exception);

             return ErrorCodes.IO_ERROR;
        }
    }
    
    private CustomerModel mapCustomer(ResultSet resultSet) throws SQLException {
        return new CustomerModel(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getString("fullname"),
                resultSet.getString("city"),
                resultSet.getString("state"),
                resultSet.getString("postal_code"),
                resultSet.getString("address_line1"),
                resultSet.getString("address_line2"));
    };

    private void setCustomerParameters(PreparedStatement statement, CustomerModel customer, boolean includeId)
            throws SQLException {
        statement.setInt(1, customer.getUser_id());
        statement.setString(2, customer.getFullname());
        statement.setString(3, customer.getCity());
        statement.setString(4, customer.getState());
        statement.setString(5, customer.getPostal_code());
        statement.setString(6, customer.getAddress_line1());
        statement.setString(7, customer.getAddress_line2());
        if (includeId) {
            statement.setInt(8, customer.getId());
        }
    }

    private RuntimeException databaseException(String operation, SQLException exception) {
        return new RuntimeException("Could not " + operation, exception);
    }

}