package repository;

import com.financetracker.model.BudgetRecord;
import com.financetracker.repository.BudgetRepository;
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
public class BudgetRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    private static DataSource dataSource;
    private BudgetRepository budgetRepository;

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
                CREATE TABLE finance_schema.budgets (
                    id SERIAL PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    month VARCHAR(255) NOT NULL,
                    budget DECIMAL(19, 2) NOT NULL,
                    express DECIMAL(19, 2) NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create test schema and tables", e);
        }
    }

    @BeforeEach
    void setup() {
        budgetRepository = new BudgetRepository(dataSource);

        // Очистка таблицы budgets перед каждым тестом
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM finance_schema.budgets");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up budgets table", e);
        }
    }

    @AfterAll
    static void cleanup() {
        // Очистка БД после всех тестов
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS finance_schema.budgets");
            stmt.execute("DROP SCHEMA IF EXISTS finance_schema CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test schema and tables", e);
        }
    }

    @Test
    void testSaveBudget() {
        BudgetRecord budgetRecord = new BudgetRecord(1L, "2023-10", 1000.0);
        budgetRepository.saveBudget(budgetRecord);

        assertNotNull(budgetRecord.getId(), "Budget ID should be generated after saving");
    }

    @Test
    void testFindBudget() {
        BudgetRecord budgetRecord = new BudgetRecord(1L, "2023-10", 1000.0);
        budgetRepository.saveBudget(budgetRecord);

        Optional<BudgetRecord> foundBudget = budgetRepository.findBudget(budgetRecord.getUserId());
        assertTrue(foundBudget.isPresent(), "Budget should be found by user ID");
        assertEquals(budgetRecord.getMonth(), foundBudget.get().getMonth(), "Months should match");
    }

    @Test
    void testDeleteBudget() {
        BudgetRecord budgetRecord = new BudgetRecord(1L, "2023-10", 1000.0);
        budgetRepository.saveBudget(budgetRecord);

        budgetRepository.deleteBudget(budgetRecord.getUserId());

        Optional<BudgetRecord> deletedBudget = budgetRepository.findBudget(budgetRecord.getUserId());
        assertFalse(deletedBudget.isPresent(), "Budget should be deleted");
    }
}