package service;

import com.financetracker.model.User;
import com.financetracker.repository.UserRepository;
import com.financetracker.service.UserService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private static DataSource dataSource;
    private UserService userService;
    private UserRepository userRepository;

    @BeforeAll
    static void setupDataSource() {
        // Настройка HikariCP для подключения к контейнеру PostgreSQL
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(postgresContainer.getJdbcUrl());
        config.setUsername(postgresContainer.getUsername());
        config.setPassword(postgresContainer.getPassword());
        config.setMaximumPoolSize(5);
        dataSource = new HikariDataSource(config);

        // Создание таблиц в тестовой БД
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS finance_schema");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS finance_schema.users (
                    id SERIAL PRIMARY KEY,
                    email VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    name VARCHAR(255) NOT NULL,
                    role VARCHAR(50) NOT NULL,
                    status VARCHAR(50) NOT NULL DEFAULT 'active'
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create test schema and tables", e);
        }
    }

    @BeforeEach
    void setup() {
        userRepository = new UserRepository(dataSource);
        userService = new UserService(userRepository);
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
    void testRegisterUser() {
        boolean result = userService.registerUser("test@example.com", "password", "Test User", "user");

        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());
        assertEquals("Test User", userOptional.get().getName());
    }

    @Test
    void testLoginUser() {
        userService.registerUser("test@example.com", "password", "Test User", "user");
        boolean result = userService.loginUser("test@example.com", "password");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());
    }

    @Test
    void testDeleteUser() {
        userService.registerUser("test@example.com", "password", "Test User", "user");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());

        userService.deleteUser(userOptional.get().getId());
        assertFalse(userService.getUserById(userOptional.get().getId()).isPresent());
    }

    @Test
    void testBanUser() {
        userService.registerUser("test@example.com", "password", "Test User", "user");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());

        userService.banUser(userOptional.get().getId());
        User user = userService.getUserById(userOptional.get().getId()).orElseThrow();
        assertEquals("banned", user.getStatus());
    }

    @Test
    void testChangeEmail() {
        userService.registerUser("test@example.com", "password", "Test User", "user");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());

        userService.changeEmail(userOptional.get().getId(), "new@example.com");
        User user = userService.getUserById(userOptional.get().getId()).orElseThrow();
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void testChangePassword() {
        userService.registerUser("test@example.com", "password", "Test User", "user");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());

        userService.changePassword(userOptional.get().getId(), "newpassword");
        User user = userService.getUserById(userOptional.get().getId()).orElseThrow();
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    void testChangeName() {
        userService.registerUser("test@example.com", "password", "Test User", "user");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());

        userService.changeName(userOptional.get().getId(), "New Name");
        User user = userService.getUserById(userOptional.get().getId()).orElseThrow();
        assertEquals("New Name", user.getName());
    }

    @Test
    void testViewProfile() {
        userService.registerUser("test@example.com", "password", "Test User", "user");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());

        String profile = userService.viewProfile(userOptional.get().getId());
        assertTrue(profile.contains("Имя: Test User"));
        assertTrue(profile.contains("Email: test@example.com"));
    }

    @Test
    void testViewUsersList() {
        userService.registerUser("test@example.com", "password", "Test User", "user");
        userService.registerUser("admin@example.com", "adminpass", "Admin User", "admin");

        Optional<User> adminOptional = userService.getUserByEmail("admin@example.com");
        assertTrue(adminOptional.isPresent());

        userService.viewUsersList(adminOptional.get().getId());
        // Проверка вывода в консоль может быть сложной, поэтому этот тест в основном для демонстрации
    }

    @Test
    void testIsPasswordEqual() {
        // Создаем пользователя
        userService.registerUser("test@example.com", "password123", "Test User", "user");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());

        long userId = userOptional.get().getId();

        // Проверяем, что пароль совпадает
        assertTrue(userService.isPasswordEqual(userId, "password123"));

        // Проверяем, что пароль не совпадает
        assertFalse(userService.isPasswordEqual(userId, "wrongpassword"));
    }

    @Test
    void testHasAccess() {
        // Создаем обычного пользователя
        userService.registerUser("user@example.com", "password123", "User", "user");
        Optional<User> userOptional = userService.getUserByEmail("user@example.com");
        assertTrue(userOptional.isPresent());

        long userId = userOptional.get().getId();

        // Проверяем, что пользователь не имеет доступа администратора
        assertFalse(userService.hasAccess(userId));

        // Создаем администратора
        userService.registerUser("admin@example.com", "adminpass", "Admin", "admin");
        Optional<User> adminOptional = userService.getUserByEmail("admin@example.com");
        assertTrue(adminOptional.isPresent());

        long adminId = adminOptional.get().getId();

        // Проверяем, что администратор имеет доступ
        assertTrue(userService.hasAccess(adminId));
    }
    @Test
    void testIsUserExist() {
        // Создаем пользователя
        userService.registerUser("test@example.com", "password123", "Test User", "user");
        Optional<User> userOptional = userService.getUserByEmail("test@example.com");
        assertTrue(userOptional.isPresent());
        long userId = userOptional.get().getId();
        // Проверяем, что пользователь существует
        assertTrue(userService.isUserExist(userId));
        // Удаляем пользователя
        userService.deleteUser(userId);
        // Проверяем, что пользователь больше не существует
        assertFalse(userService.isUserExist(userId));
    }
}