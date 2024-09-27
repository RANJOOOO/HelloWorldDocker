package org.helloworld;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class Database {
    private static Connection conn;
    private static final Logger log = Logger.getLogger(Database.class.getName());

    private Database() {}

    private static Connection connection;

    public static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
          log.severe("Driver class not found: " + e.getMessage());
        }


        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        }
        return connection;
    }

    public static void initDatabase() throws SQLException {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER)");
        }
    }

    public static Connection getConnection() throws SQLException {
        return connect();
    }

    public static void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
