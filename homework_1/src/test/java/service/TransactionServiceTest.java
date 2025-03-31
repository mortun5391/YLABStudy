package service;

import com.financetracker.model.Transaction;
import com.financetracker.repository.UserRepository;
import com.financetracker.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionService = new TransactionService(userRepository);
    }

    @Test
    void testAddTransaction() {
        String userId = "user123";

        transactionService.addTransaction(userId, 100.0, "Food", LocalDate.now(), "Groceries", false);

        verify(userRepository, times(1)).addTransaction(eq(userId), any(Transaction.class));
    }

    @Test
    void testGetTransactions() {
        String userId = "user123";
        Map<String, Transaction> transactions = new HashMap<>();
        transactions.put("transaction1", new Transaction(100.0, "Food", LocalDate.now(), "Groceries", false));
        transactions.put("transaction2", new Transaction(200.0, "Salary", LocalDate.now(), "Monthly salary", true));

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        Map<String, Transaction> result = transactionService.getTransactions(userId);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("transaction1"));
        assertTrue(result.containsKey("transaction2"));
    }

    @Test
    void testGetTransaction() {
        String userId = "user123";
        String transactionId = "transaction1";
        Transaction transaction = new Transaction(100.0, "Food", LocalDate.now(), "Groceries", false);

        when(userRepository.getTransaction(userId, transactionId)).thenReturn(transaction);

        Transaction result = transactionService.getTransaction(userId, transactionId);

        assertNotNull(result);
        assertEquals(100.0, result.getAmount());
        assertEquals("Food", result.getCategory());
    }

    @Test
    void testRemoveTransaction() {
        String userId = "user123";
        String transactionId = "transaction1";
        Transaction transaction = new Transaction(100.0, "Food", LocalDate.now(), "Groceries", false);

        when(userRepository.getTransaction(userId, transactionId)).thenReturn(transaction);

        boolean result = transactionService.removeTransaction(userId, transactionId);

        assertTrue(result);
        verify(userRepository, times(1)).removeTransaction(userId, transactionId);
    }

    @Test
    void testRemoveTransactionNotFound() {
        String userId = "user123";
        String transactionId = "transaction1";

        when(userRepository.getTransaction(userId, transactionId)).thenReturn(null);

        boolean result = transactionService.removeTransaction(userId, transactionId);

        assertFalse(result);
        verify(userRepository, never()).removeTransaction(userId, transactionId);
    }

    @Test
    void testIsTransactionThere() {
        String userId = "user123";
        String transactionId = "transaction1";
        Transaction transaction = new Transaction(100.0, "Food", LocalDate.now(), "Groceries", false);

        when(userRepository.getTransaction(userId, transactionId)).thenReturn(transaction);

        boolean result = transactionService.isTransactionThere(userId, transactionId);

        assertTrue(result);
    }

    @Test
    void testIsTransactionThereNotFound() {
        String userId = "user123";
        String transactionId = "transaction1";

        when(userRepository.getTransaction(userId, transactionId)).thenReturn(null);

        boolean result = transactionService.isTransactionThere(userId, transactionId);

        assertFalse(result);
    }

    @Test
    void testSetTransactionAmount() {
        String userId = "user123";
        String transactionId = "transaction1";
        Transaction transaction = new Transaction(100.0, "Food", LocalDate.now(), "Groceries", false);

        when(userRepository.getTransaction(userId, transactionId)).thenReturn(transaction);

        transactionService.setTransactionAmount(userId, transactionId, 200.0);

        assertEquals(200.0, transaction.getAmount());
    }

    @Test
    void testSetTransactionCategory() {
        String userId = "user123";
        String transactionId = "transaction1";
        Transaction transaction = new Transaction(100.0, "Food", LocalDate.now(), "Groceries", false);

        when(userRepository.getTransaction(userId, transactionId)).thenReturn(transaction);

        transactionService.setTransactionCategory(userId, transactionId, "Transport");

        assertEquals("Transport", transaction.getCategory());
    }

    @Test
    void testSetTransactionDescription() {
        String userId = "user123";
        String transactionId = "transaction1";
        Transaction transaction = new Transaction(100.0, "Food", LocalDate.now(), "Groceries", false);

        when(userRepository.getTransaction(userId, transactionId)).thenReturn(transaction);

        transactionService.setTransactionDescription(userId, transactionId, "Weekly groceries");

        assertEquals("Weekly groceries", transaction.getDescription());
    }

    @Test
    void testGetBalance() {
        String userId = "user123";
        Map<String, Transaction> transactions = new HashMap<>();
        transactions.put("transaction1", new Transaction(100.0, "Food", LocalDate.now(), "Groceries", false));
        transactions.put("transaction2", new Transaction(200.0, "Salary", LocalDate.now(), "Monthly salary", true));

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        double balance = transactionService.getBalance(userId);

        assertEquals(100.0, balance); // 200 (доход) - 100 (расход) = 100
    }

    @Test
    void testGetIncomeOfPeriod() {
        String userId = "user123";
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);

        Map<String, Transaction> transactions = new HashMap<>();
        transactions.put("transaction1", new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false));
        transactions.put("transaction2", new Transaction(200.0, "Salary", LocalDate.of(2023, 6, 1), "Monthly salary", true));

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        double income = transactionService.getIncomeOfPeriod(userId, start, end);

        assertEquals(200.0, income); // Только доходы за период
    }

    @Test
    void testGetExpensesOfPeriod() {
        String userId = "user123";
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);

        Map<String, Transaction> transactions = new HashMap<>();
        transactions.put("transaction1", new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false));
        transactions.put("transaction2", new Transaction(200.0, "Salary", LocalDate.of(2023, 6, 1), "Monthly salary", true));

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        double expenses = transactionService.getExpensesOfPeriod(userId, start, end);

        assertEquals(100.0, expenses); // Только расходы за период
    }

    @Test
    void testGetExpensesByCategory() {
        String userId = "user123";
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);

        Map<String, Transaction> transactions = new HashMap<>();
        transactions.put("transaction1", new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false));
        transactions.put("transaction2", new Transaction(200.0, "Transport", LocalDate.of(2023, 6, 1), "Bus fare", false));

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        Map<String, Double> expensesByCategory = transactionService.getExpensesByCategory(userId, start, end);

        assertEquals(2, expensesByCategory.size());
        assertEquals(100.0, expensesByCategory.get("Food"));
        assertEquals(200.0, expensesByCategory.get("Transport"));
    }

    @Test
    void testCalculateMonthlyExpress() {
        String userId = "user123";
        String month = "2023-05";

        Map<String, Transaction> transactions = new HashMap<>();
        transactions.put("transaction1", new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false));
        transactions.put("transaction2", new Transaction(200.0, "Transport", LocalDate.of(2023, 6, 1), "Bus fare", false));

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        double monthlyExpenses = transactionService.calculateMonthlyExpress(userId, month);

        assertEquals(100.0, monthlyExpenses); // Только расходы за май 2023
    }

    @Test
    void testGenerateReport() {
        String userId = "user123";
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);

        Map<String, Transaction> transactions = new HashMap<>();
        transactions.put("transaction1", new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false));
        transactions.put("transaction2", new Transaction(200.0, "Salary", LocalDate.of(2023, 6, 1), "Monthly salary", true));

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        String report = transactionService.generateReport(userId, start, end);

        assertNotNull(report);
        assertTrue(report.contains("Текущий баланс: 100,00"));
        assertTrue(report.contains("Доход за период: 200,00"));
        assertTrue(report.contains("Расход за период: 100,00"));
    }

    @Test
    void testViewTransactionNoFilter() {
        String userId = "user123";

        Map<String, Transaction> transactions = new HashMap<>();
        Transaction transaction1 = new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false);
        Transaction transaction2 = new Transaction(200.0, "Salary", LocalDate.of(2023, 6, 1), "Monthly salary", true);
        transactions.put(transaction1.getId(), transaction1);
        transactions.put(transaction2.getId(), transaction2);

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        String result = transactionService.viewTransactionNoFilter(userId);

        assertNotNull(result);
        assertTrue(result.contains("Список транзакций:"));
        assertTrue(result.contains("ID: " + transaction1.getId()));
        assertTrue(result.contains("ID: " + transaction2.getId()));
    }

    @Test
    void testViewTransactionsDateFilter() {
        String userId = "user123";
        LocalDate dateFilter = LocalDate.of(2023, 5, 15);

        Map<String, Transaction> transactions = new HashMap<>();
        Transaction transaction1 = new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false);
        Transaction transaction2 = new Transaction(200.0, "Salary", LocalDate.of(2023, 6, 1), "Monthly salary", true);
        transactions.put(transaction1.getId(), transaction1);
        transactions.put(transaction2.getId(), transaction2);
        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        String result = transactionService.viewTransactionsDateFilter(userId, dateFilter);

        assertNotNull(result);
        assertTrue(result.contains("Список транзакций по дате 2023-05-15:"));
        assertTrue(result.contains("ID: " + transaction1.getId()));
        assertFalse(result.contains("ID: " + transaction2.getId()));
    }

    @Test
    void testViewTransactionsCategoryFilter() {
        String userId = "user123";
        String categoryFilter = "Food";

        Map<String, Transaction> transactions = new HashMap<>();
        Transaction transaction1 = new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false);
        Transaction transaction2 = new Transaction(200.0, "Salary", LocalDate.of(2023, 6, 1), "Monthly salary", true);
        transactions.put(transaction1.getId(), transaction1);
        transactions.put(transaction2.getId(), transaction2);
        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        String result = transactionService.viewTransactionsCategoryFilter(userId, categoryFilter);

        assertNotNull(result);
        assertTrue(result.contains("Список транзакций по категории Food:"));
        assertTrue(result.contains("ID: " + transaction1.getId()));
        assertFalse(result.contains("ID: " + transaction2.getId()));
    }

    @Test
    void testViewTransactionsIsIncomeFilter() {
        String userId = "user123";
        boolean isIncomeFilter = true;

        Map<String, Transaction> transactions = new HashMap<>();
        Transaction transaction1 = new Transaction(100.0, "Food", LocalDate.of(2023, 5, 15), "Groceries", false);
        Transaction transaction2 = new Transaction(200.0, "Salary", LocalDate.of(2023, 6, 1), "Monthly salary", true);
        transactions.put(transaction1.getId(), transaction1);
        transactions.put(transaction2.getId(), transaction2);

        when(userRepository.getTransactions(userId)).thenReturn(transactions);

        String result = transactionService.viewTransactionsIsIncomeFilter(userId, isIncomeFilter);

        assertNotNull(result);
        assertTrue(result.contains("Список транзакций (Доходы):"));
        assertFalse(result.contains("ID: " + transaction1.getId()));
        assertTrue(result.contains("ID: " + transaction2.getId()));
    }
}