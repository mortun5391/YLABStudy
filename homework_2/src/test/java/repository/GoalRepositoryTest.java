package repository;

import com.financetracker.model.Goal;
import com.financetracker.repository.GoalRepository;
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
public class GoalRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    private static DataSource dataSource;
    private GoalRepository goalRepository;

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
                CREATE TABLE finance_schema.goals (
                    id SERIAL PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    goal_name VARCHAR(255) NOT NULL,
                    target_amount DECIMAL(19, 2) NOT NULL,
                    current_amount DECIMAL(19, 2) NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create test schema and tables", e);
        }
    }

    @BeforeEach
    void setup() {
        goalRepository = new GoalRepository(dataSource);

        // Очистка таблицы goals перед каждым тестом
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM finance_schema.goals");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up goals table", e);
        }
    }

    @AfterAll
    static void cleanup() {
        // Очистка БД после всех тестов
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS finance_schema.goals");
            stmt.execute("DROP SCHEMA IF EXISTS finance_schema CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test schema and tables", e);
        }
    }

    @Test
    void testSaveGoal() {
        Goal goal = new Goal(1L, 1000.0, "Vacation");
        goalRepository.saveGoal(goal);

        assertNotNull(goal.getId(), "Goal ID should be generated after saving");
    }

    @Test
    void testFindGoal() {
        Goal goal = new Goal(1L, 1000.0, "Vacation");
        goalRepository.saveGoal(goal);

        Optional<Goal> foundGoal = goalRepository.findGoal(goal.getUserId());
        assertTrue(foundGoal.isPresent(), "Goal should be found by user ID");
        assertEquals(goal.getGoalName(), foundGoal.get().getGoalName(), "Goal names should match");
    }

    @Test
    void testDeleteGoal() {
        Goal goal = new Goal(1L, 1000.0, "Vacation");
        goalRepository.saveGoal(goal);

        goalRepository.deleteGoal(goal.getUserId());

        Optional<Goal> deletedGoal = goalRepository.findGoal(goal.getUserId());
        assertFalse(deletedGoal.isPresent(), "Goal should be deleted");
    }
}