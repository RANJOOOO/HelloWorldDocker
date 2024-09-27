package org.helloworld;

import java.sql.SQLException;
import java.util.logging.Logger;

public class Main {

    final static Logger log = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            Database.connect();
            Database.initDatabase();

            UserDao.addUser(new User("Huzaifa", 30));

            UserDao.readUsers();

            UserDao.readUserById(116);

            UserDao.updateUser(new User(116, "Rizwan", 40));

            UserDao.deleteUser(120);

        } catch (SQLException e) {
            log.severe("Database error: " + e.getMessage());
        } catch (Exception e) {
            log.severe("Unexpected error: " + e.getMessage());
        }
    }
}
