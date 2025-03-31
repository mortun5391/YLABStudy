package repository;

import com.financetracker.model.User;
import com.financetracker.repository.UserRepository;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    private static DataSource dataSource;
    private UserRepository userRepository;

    @BeforeAll
    static void setupDataSource() {
        // Настройка HikariCP для подключения к контейнеру PostgreSQL
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());
        config.setMaximumPoolSize(5);
        dataSource = new HikariDataSource(config);

        // Создание таблиц в тестовой БД
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS finance_schema");
            stmt.execute("""
                CREATE TABLE finance_schema.users (
                    id SERIAL PRIMARY KEY,
                    email VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    name VARCHAR(255),
                    role VARCHAR(50),
                    status VARCHAR(50) DEFAULT 'active'
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create test schema and tables", e);
        }
    }

    @BeforeEach
    void setup() {
        userRepository = new UserRepository(dataSource);
    }

    @AfterAll
    static void cleanup() {
        // Очистка БД после всех тестов
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS finance_schema.users");
            stmt.execute("DROP SCHEMA IF EXISTS finance_schema CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test schema and tables", e);
        }
    }

    @Test
    void testSaveUser() {
        User user = new User("test@example.com", "password123", "John Doe", "user");
        userRepository.saveUser(user);

        assertNotNull(user.getId(), "User ID should be generated after saving");
    }

    @Test
    void testFindUserById() {
        User user = new User("test@example.com", "password123", "John Doe", "user");
        userRepository.saveUser(user);

        Optional<User> foundUser = userRepository.findUserById(user.getId());
        assertTrue(foundUser.isPresent(), "User should be found by ID");
        assertEquals(user.getEmail(), foundUser.get().getEmail(), "Emails should match");
    }

    @Test
    void testFindUserByEmail() {
        User user = new User("test@example.com", "password123", "John Doe", "user");
        userRepository.saveUser(user);

        Optional<User> foundUser = userRepository.findUserByEmail(user.getEmail());
        assertTrue(foundUser.isPresent(), "User should be found by email");
        assertEquals(user.getName(), foundUser.get().getName(), "Names should match");
    }

    @Test
    void testDeleteUser() {
        User user = new User("test@example.com", "password123", "John Doe", "user");
        userRepository.saveUser(user);

        userRepository.deleteUser(user.getId());

        Optional<User> deletedUser = userRepository.findUserById(user.getId());
        assertFalse(deletedUser.isPresent(), "User should be deleted");
    }

    @Test
    void testGetUsers() {
        User user1 = new User("test1@example.com", "password123", "John Doe", "user");
        User user2 = new User("test2@example.com", "password123", "Jane Doe", "admin");
        userRepository.saveUser(user1);
        userRepository.saveUser(user2);

        List<User> users = userRepository.getUsers();
        assertEquals(2, users.size(), "There should be 2 users in the repository");
    }
}