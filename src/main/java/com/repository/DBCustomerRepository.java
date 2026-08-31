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
public class DBCustomerRepository implements ICustomerRepository {
    private static final String SELECT_COLUMNS = "id, user_id, fullname, city, state, postal_code, "
            + "address_line1, address_line2";

    private final Connection connection;

    public DBCustomerRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CustomerModel findById(int id) {

        String sql = "SELECT " + SELECT_COLUMNS + " FROM customer WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapCustomer(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find customer by ID", exception);
        }

       // return null;
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
            return customers;
        } catch (SQLException exception) {
            throw databaseException("find all customers", exception);
        }
    
       // return null;
    }

    @Override
    public ErrorCodes save(CustomerModel customer) {
        String sql = "INSERT INTO customer (user_id, fullname, city, state, postal_code, address_line1, "
                + "address_line2) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setCustomerParameters(statement, customer, false);
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException exception) {
            throw databaseException("save customer", exception);
        }
        return ErrorCodes.SUCCESS;
    }

    @Override
    public ErrorCodes update(CustomerModel customer) {
        String sql = "UPDATE customer SET user_id = ?, fullname = ?, city = ?, state = ?, postal_code = ?, "
                + "address_line1 = ?, address_line2 = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setCustomerParameters(statement, customer, true);
            if (statement.executeUpdate() == 0) {
                return ErrorCodes.NOT_FOUND;
            }
        } catch (SQLException exception) {
            throw databaseException("update customer", exception);
        }
        return ErrorCodes.SUCCESS;
    }
        

    @Override
    public ErrorCodes deleteById(int id) {
        String sql = "DELETE FROM customer WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw databaseException("delete customer", exception);
        }
        return ErrorCodes.SUCCESS;
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