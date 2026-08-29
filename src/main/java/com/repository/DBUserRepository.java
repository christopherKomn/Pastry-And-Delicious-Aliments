package com.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.models.UserModel;

public class DBUserRepository implements IUserRepository {
    private static final String SELECT_COLUMNS = "id, email, phone, username, password, user_type, "
            + "profile_image_url, created_at";

    private final Connection connection;

    

    public DBUserRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public UserModel findUserById(int id) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapUser(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find user by ID", exception);
        }
    }

    @Override
    public List<UserModel> findAllUsers() {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM users ORDER BY id";
        List<UserModel> users = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                users.add(mapUser(resultSet));
            }
            return users;
        } catch (SQLException exception) {
            throw databaseException("find all users", exception);
        }
    }

    @Override
    public ErrorType saveUser(UserModel user) {
        String sql = "INSERT INTO users (email, phone, username, password, user_type, profile_image_url) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setUserParameters(statement, user, false);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException exception) {
            throw databaseException("save user", exception);
        }
        return ErrorType.SUCCESS;
    }

    @Override
    public ErrorType updateUser(UserModel user) {
        String sql = "UPDATE users SET email = ?, phone = ?, username = ?, password = ?, "
                + "user_type = ?, profile_image_url = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setUserParameters(statement, user, true);
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                return ErrorType.NOT_FOUND;
            }
            return ErrorType.SUCCESS;
        } catch (SQLException exception) {
            throw databaseException("update user", exception);
        }
    }

    @Override
    public ErrorType deleteUserById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw databaseException("delete user", exception);
        }
        return ErrorType.SUCCESS;
    }

    private UserModel mapUser(ResultSet resultSet) throws SQLException {
        return new UserModel(
                resultSet.getInt("id"),
                resultSet.getString("email"),
                resultSet.getString("phone"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("user_type"),
                resultSet.getString("profile_image_url"),
                resultSet.getTimestamp("created_at"));
    }

    private void setUserParameters(PreparedStatement statement, UserModel user, boolean includeId)
            throws SQLException {
        statement.setString(1, user.getUserEmail());
        statement.setString(2, user.getUserPhone());
        statement.setString(3, user.getUsername());
        statement.setString(4, user.getUserPassword());
        statement.setString(5, user.getUser_type());
        statement.setString(6, user.getUser_profile_image_url());
        if (includeId) {
            statement.setInt(7, user.getUserId());
        }
    }

    @Override
    public UserModel findByUsername(String username) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapUser(resultSet) : null;
            }
        } catch (SQLException exception) {
            throw databaseException("find user by username", exception);
        }
    }

    

    private RuntimeException databaseException(String operation, SQLException exception) {
        return new RuntimeException("Could not " + operation, exception);
    }
}