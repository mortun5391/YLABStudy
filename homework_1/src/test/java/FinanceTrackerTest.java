

import com.financetracker.model.BudgetRecord;
import com.financetracker.model.Goal;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.service.FinanceTracker;
import com.financetracker.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceTrackerTest {

    private FinanceTracker financeTracker;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        // Мокируем NotificationService
        notificationService = Mockito.mock(NotificationService.class);
        financeTracker = new FinanceTracker(notificationService);
    }

    @Test
    void testRegisterUser_Success() {
        boolean result = financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        assertTrue(result);
        assertNotNull(financeTracker.getUser(financeTracker.getCurrentUser().getId()));
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        boolean result = financeTracker.registerUser("test@example.com", "password456", "Another User", "active");
        assertFalse(result);
    }

    @Test
    void testLoginUser_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        boolean result = financeTracker.loginUser("test@example.com", "password123");
        assertTrue(result);
        assertNotNull(financeTracker.getCurrentUser());
    }

    @Test
    void testLoginUser_WrongPassword() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        boolean result = financeTracker.loginUser("test@example.com", "wrongpassword");
        assertFalse(result);
        assertNull(financeTracker.getCurrentUser());
    }

    @Test
    void testAddTransaction_Income() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.addTransaction(100.0, "Salary", LocalDate.now(), "Monthly salary", true);

        Map<String, Transaction> transactions = financeTracker.getTransactions(financeTracker.getCurrentUser().getId());
        assertEquals(1, transactions.size());
        assertTrue(transactions.values().iterator().next().isIncome());
    }

    @Test
    void testAddTransaction_Expense() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.addTransaction(50.0, "Groceries", LocalDate.now(), "Weekly groceries", false);

        Map<String, Transaction> transactions = financeTracker.getTransactions(financeTracker.getCurrentUser().getId());
        assertEquals(1, transactions.size());
        assertFalse(transactions.values().iterator().next().isIncome());
    }

    @Test
    void testAddBudget() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.addBudget(financeTracker.getCurrentUser().getId(), "2023-10", 1000.0);

        assertTrue(financeTracker.isBudgetSet(financeTracker.getCurrentUser().getId()));
        assertEquals(1000.0, financeTracker.getMonthlyBudget(financeTracker.getCurrentUser().getId()));
    }

    @Test
    void testSetGoal() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.setGoal(financeTracker.getCurrentUser().getId(), "Vacation", 5000.0);

        assertTrue(financeTracker.isGoalSet(financeTracker.getCurrentUser().getId()));
        assertEquals("Vacation", financeTracker.getGoalName(financeTracker.getCurrentUser().getId()));
    }

    @Test
    void testGenerateReport() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.addTransaction(1000.0, "Salary", LocalDate.now(), "Monthly salary", true);
        financeTracker.addTransaction(500.0, "Groceries", LocalDate.now(), "Weekly groceries", false);

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);
        String report = financeTracker.generateReport(financeTracker.getCurrentUser().getId(), start, end);

        assertTrue(report.contains("Текущий баланс: 500,00 "));
        assertTrue(report.contains("Доход за период: 1000,00 "));
        assertTrue(report.contains("Расход за период: 500,00 "));
    }

   @Test
    void testDeleteUser() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "user");
        financeTracker.loginUser("test@example.com", "password123");
        String userId = financeTracker.getCurrentUser().getId();
        financeTracker.deleteUser(userId);

        assertNull(financeTracker.getUser(userId));
        assertNull(financeTracker.getCurrentUser());
    }

}