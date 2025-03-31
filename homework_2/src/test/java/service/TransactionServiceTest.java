package service;

import com.financetracker.model.Transaction;
import com.financetracker.repository.TransactionRepository;
import com.financetracker.service.TransactionService;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class TransactionServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    private static DataSource dataSource;
    private TransactionService transactionService;
    private TransactionRepository transactionRepository;

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
                CREATE TABLE IF NOT EXISTS finance_schema.transactions (
                    id SERIAL PRIMARY KEY,
                    user_id BIGINT NOT NULL,
                    amount DOUBLE PRECISION NOT NULL,
                    category VARCHAR(255) NOT NULL,
                    date DATE NOT NULL,
                    description VARCHAR(255),
                    is_income BOOLEAN NOT NULL
                )
            """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create test schema and tables", e);
        }
    }

    @BeforeEach
    void setup() {
        transactionRepository = new TransactionRepository(dataSource);
        transactionService = new TransactionService(transactionRepository);

        // Очистка таблицы перед каждым тестом
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM finance_schema.transactions");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clean up transactions", e);
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
        Transaction transaction = new Transaction(1L, 100.0, "Salary", LocalDate.now(), "Monthly salary", true);
        transactionService.addTransaction(transaction);

        Optional<Transaction> foundTransaction = transactionService.getTransaction(transaction.getId());
        assertTrue(foundTransaction.isPresent());
        assertEquals(100.0, foundTransaction.get().getAmount());
    }

    @Test
    void testGetTransactions() {
        Transaction transaction1 = new Transaction(1L, 100.0, "Salary", LocalDate.now(), "Monthly salary", true);
        Transaction transaction2 = new Transaction(1L, 50.0, "Groceries", LocalDate.now(), "Weekly groceries", false);
        transactionService.addTransaction(transaction1);
        transactionService.addTransaction(transaction2);

        List<Transaction> transactions = transactionService.getTransactions(1L);
        assertEquals(2, transactions.size());
    }

    @Test
    void testRemoveTransaction() {

        Transaction transaction = new Transaction(1L, 100.0, "Salary", LocalDate.now(), "Monthly salary", true);
        transactionService.addTransaction(transaction);

        boolean isDeleted = transactionService.removeTransaction(transaction.getId());
        assertTrue(isDeleted);
        assertFalse(transactionService.isTransactionThere(1,transaction.getId()));
    }

    @Test
    void testUpdateTransaction() {
        Transaction transaction = new Transaction(1L, 100.0, "Salary", LocalDate.now(), "Monthly salary", true);

        transactionService.addTransaction(transaction);

        transactionService.setTransactionAmount(1,transaction.getId(), 200.0);
        transactionService.setTransactionCategory(1,transaction.getId(), "Bonus");
        transactionService.setTransactionDescription(1,transaction.getId(), "Annual bonus");

        Optional<Transaction> updatedTransaction = transactionService.getTransaction(transaction.getId());
        assertTrue(updatedTransaction.isPresent());
        assertEquals(200.0, updatedTransaction.get().getAmount());
        assertEquals("Bonus", updatedTransaction.get().getCategory());
        assertEquals("Annual bonus", updatedTransaction.get().getDescription());
    }

    @Test
    void testGetBalance() {
        Transaction income = new Transaction(1L, 100.0, "Salary", LocalDate.now(), "Monthly salary", true);
        Transaction expense = new Transaction(1L, 50.0, "Groceries", LocalDate.now(), "Weekly groceries", false);
        transactionService.addTransaction(income);
        transactionService.addTransaction(expense);

        double balance = transactionService.getBalance(1L);
        assertEquals(50.0, balance);
    }

    @Test
    void testGetIncomeOfPeriod() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();

        Transaction income1 = new Transaction(1L, 100.0, "Salary", LocalDate.now(), "Monthly salary", true);
        Transaction income2 = new Transaction(1L, 200.0, "Bonus", LocalDate.now().minusDays(3), "Annual bonus", true);
        transactionService.addTransaction(income1);
        transactionService.addTransaction(income2);

        double income = transactionService.getIncomeOfPeriod(1L, start, end);
        assertEquals(300.0, income);
    }

    @Test
    void testGetExpensesOfPeriod() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();

        Transaction expense1 = new Transaction(1L, 50.0, "Groceries", LocalDate.now(), "Weekly groceries", false);
        Transaction expense2 = new Transaction(1L, 30.0, "Transport", LocalDate.now().minusDays(3), "Bus fare", false);
        transactionService.addTransaction(expense1);
        transactionService.addTransaction(expense2);

        double expenses = transactionService.getExpensesOfPeriod(1L, start, end);
        assertEquals(80.0, expenses);
    }

    @Test
    void testGetExpensesByCategory() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();

        Transaction expense1 = new Transaction(1L, 50.0, "Groceries", LocalDate.now(), "Weekly groceries", false);
        Transaction expense2 = new Transaction(1L, 30.0, "Groceries", LocalDate.now().minusDays(3), "More groceries", false);
        transactionService.addTransaction(expense1);
        transactionService.addTransaction(expense2);

        Map<String, Double> expensesByCategory = transactionService.getExpensesByCategory(1L, start, end);
        assertEquals(1, expensesByCategory.size());
        assertEquals(80.0, expensesByCategory.get("Groceries"));
    }

    @Test
    void testGenerateReport() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();

        Transaction income = new Transaction(1L, 100.0, "Salary", LocalDate.now(), "Monthly salary", true);
        Transaction expense = new Transaction(1L, 50.0, "Groceries", LocalDate.now(), "Weekly groceries", false);
        transactionService.addTransaction(income);
        transactionService.addTransaction(expense);

        String report = transactionService.generateReport(1L, start, end);
        assertTrue(report.contains("Текущий баланс: 50,00"));
        assertTrue(report.contains("Доход за период: 100,00"));
        assertTrue(report.contains("Расход за период: 50,00"));
        assertTrue(report.contains("Расходы по категориям:"));
        assertTrue(report.contains("- Groceries: 50,00"));
    }
}