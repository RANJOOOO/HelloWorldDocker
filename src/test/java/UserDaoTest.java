import org.helloworld.Database;
import org.helloworld.User;
import org.helloworld.UserDao;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    @BeforeAll
    static void setUp() throws SQLException {
        Database.connect();
        Database.initDatabase();
    }

    @AfterEach
    void cleanUp() throws SQLException {
        clearDatabase();
    }

    @AfterAll
    static void tearDown() throws SQLException {
        Database.close();
    }


    private void clearDatabase() throws SQLException {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users")) {
            stmt.executeUpdate();
        }
    }

    @Test
    void testAddUser() throws SQLException {
        User newUser = new User("Test", 22);
        User addedUser = UserDao.addUser(newUser);

        assertNotNull(addedUser);
        assertEquals(newUser.getName(), addedUser.getName());
        assertEquals(newUser.getAge(), addedUser.getAge());
        assertEquals(newUser.getId(), addedUser.getId());
    }

    @Test
    void testUpdateUser() throws SQLException {
        User newUser = new User("usama", 30);
        User existingUser = UserDao.addUser(newUser);

        existingUser.setName("abdullah");
        existingUser.setAge(22);

        User updatedUser = UserDao.updateUser(existingUser);

        assertNotNull(updatedUser);
        assertEquals("abdullah", updatedUser.getName());
        assertEquals(22, updatedUser.getAge());
    }

    @Test
    void testDeleteUser() throws SQLException {
        User newUser = new User("Usama", 40);
        User addedUser = UserDao.addUser(newUser);

        UserDao.deleteUser(addedUser.getId());

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setInt(1, addedUser.getId());
            ResultSet rs = stmt.executeQuery();
            assertFalse(rs.next());
        }
    }

    @Test
    void testReadUsers() throws SQLException {
        User user1 = new User("Ali", 25);
        User user2 = new User("Ahmed", 30);
        User user3 = new User("Abdullah", 35);

        List<User> userToSave = List.of(user1, user2, user3);

        UserDao.saveAll(userToSave);

        List<User> users = UserDao.readUsers();

        assertNotNull(users);
        assertEquals(3, users.size());
        userToSave.forEach(expectedUser ->
                assertTrue(users.stream().anyMatch(user ->
                        user.getName().equals(expectedUser.getName()) && user.getAge() == expectedUser.getAge()
                ))
        );
    }

    @Test
    void testReadUserById() throws SQLException {
        User newUser = new User("Test", 28);
        User addedUser = UserDao.addUser(newUser);

        User retrievedUser = UserDao.readUserById(addedUser.getId());

        assertNotNull(retrievedUser);
        assertEquals(addedUser.getId(), retrievedUser.getId());
        assertEquals(addedUser.getName(), retrievedUser.getName());
        assertEquals(addedUser.getAge(), retrievedUser.getAge());
    }
}
