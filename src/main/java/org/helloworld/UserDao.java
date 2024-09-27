package org.helloworld;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UserDao {

    private static final Logger log = Logger.getLogger(UserDao.class.getName());
    private static final String INSERT_USER = "INSERT INTO users (name, age) VALUES (?, ?)";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String UPDATE_USER = "UPDATE users SET name = ?, age = ? WHERE id = ?";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";

    public static User addUser(User user) throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getAge());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    log.info("User added: " + user.getName() + ", Age: " + user.getAge() + ", ID: " + user.getId());
                } else {
                    throw new SQLException("Unable to attach ID to return user.");
                }
            }
        } catch (SQLException e) {
            log.severe("Error adding user: " + e.getMessage());
        }

        return user;
    }

    public static void deleteUser(int id) throws SQLException {
        Connection conn = Database.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("User with ID " + id + " deleted.");
            } else {
                log.info("User with ID " + id + " not found.");
            }
        } catch (SQLException e) {
            log.severe("Error deleting user: " + e.getMessage());
        }
    }

    public static List<User> readUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        Connection conn = Database.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_USERS)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setAge(rs.getInt("age"));
                userList.add(user);
            }
            log.info("Retrieved Users: ");
            for (User user : userList) {
                log.info("ID: " + user.getId() + ", Name: " + user.getName() + ", Age: " + user.getAge());
            }
        } catch (SQLException e) {
            log.severe("Error reading users: " + e.getMessage());
        }
        return userList;
    }


    public static User updateUser(User user) throws SQLException {
        Connection conn = Database.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_USER)) {
            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getAge());
            stmt.setInt(3, user.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("User with ID " + user.getId() + " updated to: " + user.getName() + ", Age: " + user.getAge());
            } else {
                log.info("User with ID " + user.getId() + " not found.");
            }
        } catch (SQLException e) {
            log.severe("Error updating user: " + e.getMessage());
        }
        return user;
    }


    public static User readUserById(int userId) throws SQLException {
        User user = null;
        Connection conn = Database.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_ID)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setAge(rs.getInt("age"));
                    log.info("Retrieved User: ID: " + user.getId() + ", Name: " + user.getName() + ", Age: " + user.getAge());
                } else {
                    log.info("User with ID " + userId + " not found.");
                }
            }
        } catch (SQLException e) {
            log.severe("Error reading user by ID: " + e.getMessage());
        }
        return user;
    }

    public static void saveAll(List<User> users) throws SQLException {
        if (users == null || users.isEmpty()) {
            log.info("No users to save.");
            return;
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS)) {

            for (User user : users) {
                stmt.setString(1, user.getName());
                stmt.setInt(2, user.getAge());
                stmt.addBatch();
            }

            int[] affectedRows = stmt.executeBatch();
            log.info("Inserted " + affectedRows.length + " users.");


            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                for (int i = 0; i < affectedRows.length; i++) {
                    if (affectedRows[i] == Statement.SUCCESS_NO_INFO) {
                        log.info("User added without ID.");
                    } else if (affectedRows[i] == Statement.EXECUTE_FAILED) {
                        log.warning("Failed to insert user at index " + i);
                    } else {
                        if (generatedKeys.next()) {
                            users.get(i).setId(generatedKeys.getInt(1));
                            log.info("User added: " + users.get(i).getName() + ", Age: " + users.get(i).getAge() + ", ID: " + users.get(i).getId());
                        }
                    }
                }
            }
        } catch (SQLException e) {
            log.severe("Error saving users: " + e.getMessage());
        }
    }


}
