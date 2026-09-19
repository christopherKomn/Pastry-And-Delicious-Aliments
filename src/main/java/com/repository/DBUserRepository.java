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
import com.models.UserModel;

public class DBUserRepository implements IUserRepository {
    private static final Logger LOGGER =
            Logger.getLogger(DBUserRepository.class.getName());

    private static final String SELECT_COLUMNS = 
    "id, email, phone, username, password, user_type, "
            + "profile_image_url, created_at";

    private final Connection connection;

    

    public DBUserRepository(Connection connection) {
        if (connection == null) {
            LOGGER.severe(
                "[constructor] Cannot create DBUserRepository: database connection is null"
            );

            throw new IllegalArgumentException(
                "Database connection cannot be null"
            );
        }

        this.connection = connection;
    }

    @Override
    public UserModel findUserById(int id) {
        String sql = "SELECT " + SELECT_COLUMNS + " FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    LOGGER.fine(() -> "[findUserById] User not found for ID: " + id);
                    return null;
                }

                LOGGER.fine(() -> "[findUserById] User found for ID: " + id);
                return mapUser(resultSet);
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findUserById] Could not find user by ID: " + id,
                    exception);
            return null;
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
            if (users.size() == 0){
                LOGGER.fine(() -> "[findAllUser] could not find any user!");
                return null;
            }
            LOGGER.fine(() -> "[findAllUser] found " + users.size() + " users!");
            return users;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findAllUser] Database io Could not retreive users",
                    exception);
            return null;
        }
    }

    @Override
    public ErrorCodes saveUser(UserModel user) {
        if (user == null) {
            throw new IllegalArgumentException(
                "user parameter cannot be null"
            );
        }

        String sql = "INSERT INTO users (email, phone, username, password, user_type, profile_image_url) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = 
        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setUserParameters(statement, user, false);
            if (statement.executeUpdate() == 0) {
                LOGGER.fine(() -> "[saveUser] could not saved user \"" + user.toString() + " \"");
                return ErrorCodes.FAILED_TO_WRITE;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    LOGGER.fine(() -> "[saveUser] could not saved user \"" + user.toString() + " \"");
                    return ErrorCodes.FAILED_TO_WRITE;
                }
                
                user.setUserId(generatedKeys.getInt(1));
                    LOGGER.fine(() -> "[saveUser] saved user \"" + user.toString() + " \"");
                return ErrorCodes.SUCCESS;
                
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[saveUser] Database io failed save user \" " + user.toString() + " \"",
                    exception);
            return ErrorCodes.IO_ERROR;
        }
        
    }

    @Override
    public ErrorCodes updateUser(UserModel user) {
        if (user == null){
            throw new IllegalArgumentException(
                "user parameter cannot be null"
            );
        }
        String sql = "UPDATE users SET email = ?, phone = ?, username = ?, password = ?, "
                + "user_type = ?, profile_image_url = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setUserParameters(statement, user, true);
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                LOGGER.fine(() -> "[updateUser] could not find user \"" + user.toString() + " \"");
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.fine(() -> "[updateUser] user \"" + user.toString() + " \" updated !");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[updateUser] Could not write user \" " + user.toString() + " \"",
                    exception);
            return ErrorCodes.IO_ERROR;
        }
    }

    @Override
    public ErrorCodes deleteUserById(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0){
                LOGGER.fine(() -> "[deleteUserById] could not find user with ID" + id);
                return ErrorCodes.NOT_FOUND;
            }
            LOGGER.fine(() -> "[deleteUserById] user with id " + id + " deleted!");
            return ErrorCodes.SUCCESS;
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[deleteUserById] Could not delete user ID " + id ,
                    exception);

            return ErrorCodes.IO_ERROR;
        }
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
        if (username == null){
            throw new IllegalArgumentException(
                "username parameter cannot be null"
            );
        }
        String sql = "SELECT " + SELECT_COLUMNS + " FROM users WHERE username = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                UserModel user = resultSet.next() ? mapUser(resultSet) : null;
                if (user == null){
                    LOGGER.fine(() -> 
                    "[findByUsername] could not find user with username \"" + username 
                     + " \"");
                    return null;
                }
                LOGGER.fine(() -> 
                "[findByUsername] found user with username \"" + username 
                 + " \"");
                return user;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findByUsername] failed to retreive user with username \"" + username + "\"",
                    exception);

            return null;
        }
    }

    @Override
    public UserModel findByUsernameAndPassword(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException(
                "username or/and password parameters are null"
            );
        }
        String sql = "SELECT " + SELECT_COLUMNS + " FROM users WHERE username = ? AND password = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                UserModel user = resultSet.next() ? mapUser(resultSet) : null;
                if (user == null){
                    LOGGER.fine(() -> 
                    "[findByUsernameAndPassword] could not find user with username \"" + username 
                     + " \"");
                    return null;
                }
                LOGGER.fine(() -> 
                "[findByUsernameAndPassword] found user with username \"" + username 
                 + " \"");
                return user;
            }
        } catch (SQLException exception) {
            LOGGER.log(
                    Level.SEVERE,
                    "[findByUsernameAndPassword] failed to retreive user with username \"" + username + "\"",
                    exception);

            return null;
        }
    }

    

}
