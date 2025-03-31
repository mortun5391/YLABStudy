package repository;

import com.financetracker.model.Transaction;
import com.financetracker.repository.TransactionRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class TransactionRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    private static DataSource dataSource;
    private TransactionRepository transactionRepository;

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
                CREATE TABLE finance_schema.transactions (
                    id SERIAL PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    amount DECIMAL(19, 2) NOT NULL,
                    category VARCHAR(255),
                    date DATE,
                    description TEXT,
                    is_income BOOLEAN
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create test schema and tables", e);
        }
    }

    @BeforeEach
    void setup() {
        transactionRepository = new TransactionRepository(dataSource);

        // Очистка таблицы transactions перед каждым тестом
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM finance_schema.transactions");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up transactions table", e);
        }
    }

    @AfterAll
    static void cleanup() {
        // Очистка БД после всех тестов
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS finance_schema.transactions");
            stmt.execute("DROP SCHEMA IF EXISTS finance_schema CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up test schema and tables", e);
        }
    }

    @Test
    void testAddTransaction() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transactionRepository.addTransaction(transaction);

        assertNotNull(transaction.getId(), "Transaction ID should be generated after saving");
    }

    @Test
    void testFindTransactionById() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transactionRepository.addTransaction(transaction);

        Optional<Transaction> foundTransaction = transactionRepository.findTransactionById(transaction.getId());
        assertTrue(foundTransaction.isPresent(), "Transaction should be found by ID");
        assertEquals(transaction.getAmount(), foundTransaction.get().getAmount(), "Amounts should match");
    }

    @Test
    void testFindTransactionsByUserId() {
        Transaction transaction1 = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        Transaction transaction2 = new Transaction(1L, 2000.0, "Transport", LocalDate.of(2023, 10, 2), "Taxi", false);
        transactionRepository.addTransaction(transaction1);
        transactionRepository.addTransaction(transaction2);

        List<Transaction> transactions = transactionRepository.findTransactionsByUserId(1L);
        assertEquals(2, transactions.size(), "There should be 2 transactions for user 1");
    }

    @Test
    void testDeleteTransaction() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transactionRepository.addTransaction(transaction);

        boolean isDeleted = transactionRepository.deleteTransaction(transaction.getId());
        assertTrue(isDeleted, "Transaction should be deleted");

        Optional<Transaction> deletedTransaction = transactionRepository.findTransactionById(transaction.getId());
        assertFalse(deletedTransaction.isPresent(), "Transaction should not exist after deletion");
    }

    @Test
    void testUpdateTransaction() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transactionRepository.addTransaction(transaction);

        transaction.setAmount(1500.0);
        transaction.setCategory("Groceries");
        transactionRepository.updateTransaction(transaction);

        Optional<Transaction> updatedTransaction = transactionRepository.findTransactionById(transaction.getId());
        assertTrue(updatedTransaction.isPresent(), "Transaction should be found after update");
        assertEquals(1500.0, updatedTransaction.get().getAmount(), "Amount should be updated");
        assertEquals("Groceries", updatedTransaction.get().getCategory(), "Category should be updated");
    }
}