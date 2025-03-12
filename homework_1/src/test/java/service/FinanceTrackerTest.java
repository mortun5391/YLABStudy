package service;

import com.financetracker.model.*;
import com.financetracker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceTrackerTest {

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private BudgetService budgetService;

    @Mock
    private GoalService goalService;

    @Mock
    private NotificationService notificationService;

    private FinanceTracker financeTracker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        financeTracker = new FinanceTracker(userService, transactionService, budgetService, goalService, notificationService);
    }

    @Test
    void testRegisterUser() {
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);

        boolean result = financeTracker.registerUser("test@example.com", "password", "Test Name", "user");

        assertTrue(result);
        verify(userService, times(1)).registerUser("test@example.com", "password", "Test Name", "user");
    }

    @Test
    void testLoginUserSuccess() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        FinanceTracker.LoginResult result = financeTracker.loginUser("test@example.com", "password");

        assertEquals(FinanceTracker.LoginResult.SUCCESS, result);
        assertNotNull(financeTracker.getId());
    }

    @Test
    void testLoginUserInvalidCredentials() {
        when(userService.loginUser("test@example.com", "wrongpassword")).thenReturn(false);

        FinanceTracker.LoginResult result = financeTracker.loginUser("test@example.com", "wrongpassword");

        assertEquals(FinanceTracker.LoginResult.INVALID_CREDENTIALS, result);
    }

    @Test
    void testLoginUserBanned() {
        when(userService.loginUser("banned@example.com", "password")).thenThrow(new IllegalStateException("User is banned"));

        FinanceTracker.LoginResult result = financeTracker.loginUser("banned@example.com", "password");

        assertEquals(FinanceTracker.LoginResult.USER_BANNED, result);
    }

    @Test
    void testAddTransaction() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        when(budgetService.isBudgetSet(user.getId())).thenReturn(true);
        when(budgetService.getMonth(user.getId())).thenReturn(LocalDate.now().toString().substring(0,7));
        when(goalService.isGoalSet(user.getId())).thenReturn(true);

        financeTracker.addTransaction(100.0, "Food", LocalDate.now(), "Groceries", false);

        verify(transactionService, times(1)).addTransaction(user.getId(), 100.0, "Food", LocalDate.now(), "Groceries", false);
        verify(budgetService, times(1)).addMonthlyExpress(user.getId(), 100.0);
    }

    @Test
    void testRemoveTransaction() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        when(transactionService.removeTransaction(user.getId(), transactionId)).thenReturn(true);

        boolean result = financeTracker.removeTransaction(transactionId);

        assertTrue(result);
        verify(transactionService, times(1)).removeTransaction(user.getId(), transactionId);
    }

    @Test
    void testGetBalance() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        when(transactionService.getBalance(user.getId())).thenReturn(500.0);

        double balance = financeTracker.getBalance();

        assertEquals(500.0, balance);
        verify(transactionService, times(1)).getBalance(user.getId());
    }

    @Test
    void testGenerateReport() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 12, 31);
        when(transactionService.generateReport(user.getId(), start, end)).thenReturn("Financial Report");

        String report = financeTracker.generateReport(start, end);

        assertEquals("Financial Report", report);
        verify(transactionService, times(1)).generateReport(user.getId(), start, end);
    }

    @Test
    void testViewProfile() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(userService.viewProfile(user.getId())).thenReturn("User Profile");

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        String profile = financeTracker.viewProfile();

        assertEquals("User Profile", profile);
        verify(userService, times(1)).viewProfile(user.getId());
    }

    @Test
    void testViewBudget() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(budgetService.viewBudget(user.getId())).thenReturn("Budget Details");

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        String budget = financeTracker.viewBudget();

        assertEquals("Budget Details", budget);
        verify(budgetService, times(1)).viewBudget(user.getId());
    }

    @Test
    void testViewGoal() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(goalService.viewGoal(user.getId())).thenReturn("Goal Details");

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        String goal = financeTracker.viewGoal();

        assertEquals("Goal Details", goal);
        verify(goalService, times(1)).viewGoal(user.getId());
    }

    @Test
    void testGetGoalName() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(goalService.getGoalName(user.getId())).thenReturn("Save for a car");

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        String goalName = financeTracker.getGoalName(user.getId());

        assertEquals("Save for a car", goalName);
        verify(goalService, times(1)).getGoalName(user.getId());
    }

    @Test
    void testGetTargetAmount() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(goalService.getTargetAmount(user.getId())).thenReturn(10000.0);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        double targetAmount = financeTracker.getTargetAmount(user.getId());

        assertEquals(10000.0, targetAmount);
        verify(goalService, times(1)).getTargetAmount(user.getId());
    }

    @Test
    void testGetProgress() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(goalService.getProgress(user.getId())).thenReturn(50);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        int progress = financeTracker.getProgress(user.getId());

        assertEquals(50, progress);
        verify(goalService, times(1)).getProgress(user.getId());
    }

    @Test
    void testIsGoalSet() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(goalService.isGoalSet(user.getId())).thenReturn(true);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        boolean isGoalSet = financeTracker.isGoalSet(user.getId());

        assertTrue(isGoalSet);
        verify(goalService, times(1)).isGoalSet(user.getId());
    }

    @Test
    void testGetRemaining() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(budgetService.getRemaining(user.getId())).thenReturn(500.0);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        double remaining = financeTracker.getRemaining(user.getId());

        assertEquals(500.0, remaining);
        verify(budgetService, times(1)).getRemaining(user.getId());
    }

    @Test
    void testGetMonthlyExpress() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(budgetService.getMonthlyExpress(user.getId())).thenReturn(300.0);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        double monthlyExpress = financeTracker.getMonthlyExpress(user.getId());

        assertEquals(300.0, monthlyExpress);
        verify(budgetService, times(1)).getMonthlyExpress(user.getId());
    }

    @Test
    void testGetMonthlyBudget() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(budgetService.getMonthlyBudget(user.getId())).thenReturn(1000.0);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        double monthlyBudget = financeTracker.getMonthlyBudget(user.getId());

        assertEquals(1000.0, monthlyBudget);
        verify(budgetService, times(1)).getMonthlyBudget(user.getId());
    }

    @Test
    void testIsBudgetSet() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(budgetService.isBudgetSet(user.getId())).thenReturn(true);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        boolean isBudgetSet = financeTracker.isBudgetSet(user.getId());

        assertTrue(isBudgetSet);
        verify(budgetService, times(1)).isBudgetSet(user.getId());
    }

    @Test
    void testGetMonth() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(budgetService.getMonth(user.getId())).thenReturn("2023-10");

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        String month = financeTracker.getMonth(user.getId());

        assertEquals("2023-10", month);
        verify(budgetService, times(1)).getMonth(user.getId());
    }

    @Test
    void testBanUser() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        financeTracker.banUser(user.getId());

        verify(userService, times(1)).banUser(user.getId());
    }

    @Test
    void testAddMonthlyExpress() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        financeTracker.addMonthlyExpress(user.getId(), 200.0);

        verify(budgetService, times(1)).addMonthlyExpress(user.getId(), 200.0);
    }

    @Test
    void testAddAmount() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        financeTracker.addAmount(500.0);

        verify(goalService, times(1)).addAmount(user.getId(), 500.0);
    }

    @Test
    void testIsTransactionThere() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);
        when(transactionService.isTransactionThere(user.getId(), "txn123")).thenReturn(true);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        boolean isTransactionThere = financeTracker.isTransactionThere("txn123");

        assertTrue(isTransactionThere);
        verify(transactionService, times(1)).isTransactionThere(user.getId(), "txn123");
    }

    @Test
    void testSetTransactionDescription() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        financeTracker.setTransactionDescription("txn123", "Updated description");

        verify(transactionService, times(1)).setTransactionDescription(user.getId(), "txn123", "Updated description");
    }

    @Test
    void testSetTransactionCategory() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        financeTracker.setTransactionCategory("txn123", "Updated category");

        verify(transactionService, times(1)).setTransactionCategory(user.getId(), "txn123", "Updated category");
    }

    @Test
    void testSetTransactionAmount() {
        User user = new User("test@example.com", "password", "Test Name", "user");
        when(userService.registerUser("test@example.com", "password", "Test Name", "user")).thenReturn(true);
        when(userService.loginUser("test@example.com", "password")).thenReturn(true);
        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        financeTracker.registerUser("test@example.com", "password", "Test Name", "user");
        financeTracker.loginUser("test@example.com", "password");

        financeTracker.setTransactionAmount("txn123", 150.0);

        verify(transactionService, times(1)).setTransactionAmount(user.getId(), "txn123", 150.0);
    }

}