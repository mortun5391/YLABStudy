

import com.financetracker.model.Transaction;
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

    @Test
    void testGetTargetAmount_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.setGoal(financeTracker.getCurrentUser().getId(), "Vacation", 5000.0);

        double targetAmount = financeTracker.getTargetAmount(financeTracker.getCurrentUser().getId());
        assertEquals(5000.0, targetAmount);
    }

    @Test
    void testGetTargetAmount_GoalNotSet() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> {
            financeTracker.getTargetAmount(financeTracker.getCurrentUser().getId());
        });
    }

    @Test
    void testGetProgress_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.setGoal(financeTracker.getCurrentUser().getId(), "Vacation", 5000.0);
        financeTracker.addAmount(financeTracker.getCurrentUser().getId(), 1000.0);

        int progress = financeTracker.getProgress(financeTracker.getCurrentUser().getId());
        assertEquals(20, progress); // 1000 / 5000 = 20%
    }

    @Test
    void testGetProgress_GoalNotSet() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> {
            financeTracker.getProgress(financeTracker.getCurrentUser().getId());
        });
    }

    @Test
    void testAddAmount_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.setGoal(financeTracker.getCurrentUser().getId(), "Vacation", 5000.0);
        financeTracker.addAmount(financeTracker.getCurrentUser().getId(), 1000.0);

        int progress = financeTracker.getProgress(financeTracker.getCurrentUser().getId());
        assertEquals(20, progress); // 1000 / 5000 = 20%
    }

    @Test
    void testAddAmount_GoalNotSet() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> {
            financeTracker.addAmount(financeTracker.getCurrentUser().getId(), 1000.0);
        });
    }

    @Test
    void testGetRemaining_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.addBudget(financeTracker.getCurrentUser().getId(), "2023-10", 1000.0);
        financeTracker.addMonthlyExpress(financeTracker.getCurrentUser().getId(), 400.0);

        double remaining = financeTracker.getRemaining(financeTracker.getCurrentUser().getId());
        assertEquals(600.0, remaining); // 1000 - 400 = 600
    }

    @Test
    void testGetRemaining_BudgetNotSet() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> {
            financeTracker.getRemaining(financeTracker.getCurrentUser().getId());
        });
    }

    @Test
    void testAddMonthlyExpress_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.addBudget(financeTracker.getCurrentUser().getId(), "2023-10", 1000.0);
        financeTracker.addMonthlyExpress(financeTracker.getCurrentUser().getId(), 200.0);

        double expenses = financeTracker.getMonthlyExpress(financeTracker.getCurrentUser().getId());
        assertEquals(200.0, expenses);
    }

    @Test
    void testAddMonthlyExpress_BudgetNotSet() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> {
            financeTracker.addMonthlyExpress(financeTracker.getCurrentUser().getId(), 200.0);
        });
    }

    @Test
    void testGetMonthlyExpress_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.addBudget(financeTracker.getCurrentUser().getId(), "2023-10", 1000.0);
        financeTracker.addMonthlyExpress(financeTracker.getCurrentUser().getId(), 300.0);

        double expenses = financeTracker.getMonthlyExpress(financeTracker.getCurrentUser().getId());
        assertEquals(300.0, expenses);
    }

    @Test
    void testGetMonthlyExpress_BudgetNotSet() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> {
            financeTracker.getMonthlyExpress(financeTracker.getCurrentUser().getId());
        });
    }

    @Test
    void testGetMonth_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.addBudget(financeTracker.getCurrentUser().getId(), "2023-10", 1000.0);

        String month = financeTracker.getMonth(financeTracker.getCurrentUser().getId());
        assertEquals("2023-10", month);
    }

    @Test
    void testGetMonth_BudgetNotSet() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> {
            financeTracker.getMonth(financeTracker.getCurrentUser().getId());
        });
    }

    @Test
    void testViewUsersList() {
        financeTracker.registerUser("user1@example.com", "password1", "User One", "user");
        financeTracker.registerUser("user2@example.com", "password2", "User Two", "user");

        financeTracker.loginUser("admin@example.com", "admin123");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        financeTracker.viewUsersList();

        String output = outContent.toString();
        assertTrue(output.contains("User One"));
        assertTrue(output.contains("User Two"));

        System.setOut(System.out);
    }

    @Test
    void testViewUsersList_NoCurrentUser() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        financeTracker.viewUsersList();

        String output = outContent.toString();
        assertTrue(output.isEmpty());


        System.setOut(System.out);
    }

    @Test
    void testViewProfile() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        financeTracker.viewProfile();

        String output = outContent.toString();
        assertTrue(output.contains("Test User"));
        assertTrue(output.contains("test@example.com"));

        System.setOut(System.out);
    }

    @Test
    void testViewProfile_NoCurrentUser() {
        assertThrows(IllegalStateException.class, () -> {
            financeTracker.viewProfile();
        });
    }

    @Test
    void testChangeEmail_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.changeEmail("new@example.com");

        assertEquals("new@example.com", financeTracker.getCurrentUser().getEmail());
    }

    @Test
    void testChangeEmail_NoCurrentUser() {
        assertThrows(IllegalStateException.class, () -> {
            financeTracker.changeEmail("new@example.com");
        });
    }

    @Test
    void testChangePassword_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.changePassword("newpassword");

        assertEquals("newpassword", financeTracker.getCurrentUser().getPassword());
    }

    @Test
    void testChangePassword_NoCurrentUser() {
        assertThrows(IllegalStateException.class, () -> {
            financeTracker.changePassword("newpassword");
        });
    }

    @Test
    void testChangeName_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");
        financeTracker.changeName("New Name");

        assertEquals("New Name", financeTracker.getCurrentUser().getName());
    }

    @Test
    void testChangeName_NoCurrentUser() {
        assertThrows(IllegalStateException.class, () -> {
            financeTracker.changeName("New Name");
        });
    }

    @Test
    void testRemoveTransaction_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        financeTracker.addTransaction(100.0, "Salary", LocalDate.now(), "Monthly salary", true);
        Map<String, Transaction> transactions = financeTracker.getTransactions(financeTracker.getCurrentUser().getId());
        String transactionId = transactions.keySet().iterator().next();

        financeTracker.removeTransaction(transactionId);

        assertNull(financeTracker.getTransaction(transactionId));
    }

    @Test
    void testRemoveTransaction_NoCurrentUser() {
        assertThrows(IllegalStateException.class, () -> {
            financeTracker.removeTransaction("someId");
        });
    }

    @Test
    void testGetTransaction_Success() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        financeTracker.addTransaction(100.0, "Salary", LocalDate.now(), "Monthly salary", true);
        Map<String, Transaction> transactions = financeTracker.getTransactions(financeTracker.getCurrentUser().getId());
        String transactionId = transactions.keySet().iterator().next();

        Transaction transaction = financeTracker.getTransaction(transactionId);

        assertNotNull(transaction);
        assertEquals(100.0, transaction.getAmount());
    }

    @Test
    void testGetTransaction_NoCurrentUser() {
        assertThrows(IllegalStateException.class, () -> {
            financeTracker.getTransaction("someId");
        });
    }

    @Test
    void testGetTransaction_NotFound() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        Transaction transaction = financeTracker.getTransaction("nonExistentId");

        assertNull(transaction);
    }

    @Test
    void testCheckExpenseLimit_NotExceeded() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        financeTracker.addBudget(financeTracker.getCurrentUser().getId(), "2023-10", 1000.0);

        financeTracker.addTransaction(400.0, "Shopping", LocalDate.now(), "Clothes", false);

        financeTracker.checkExpenseLimit(financeTracker.getCurrentUser().getId(), "test@example.com");

        verify(notificationService, never()).sendNotification(anyString(), anyString());
    }

    @Test
    void testCheckExpenseLimit_NoBudget() {
        financeTracker.registerUser("test@example.com", "password123", "Test User", "active");
        financeTracker.loginUser("test@example.com", "password123");

        financeTracker.checkExpenseLimit(financeTracker.getCurrentUser().getId(), "test@example.com");

        verify(notificationService, never()).sendNotification(anyString(), anyString());
    }
}