package com.financetracker.service;

import com.financetracker.model.BudgetRecord;
import com.financetracker.model.Goal;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.time.LocalDate;
import java.util.*;

/**
 * Класс FinanceTracker представляет собой систему управления финансами,
 * которая позволяет регистрировать пользователей, управлять их транзакциями,
 * бюджетами и финансовыми целями, а также отправлять уведомления.
 * <p>
 * Основные функции:
 * - Регистрация и аутентификация пользователей.
 * - Управление транзакциями, бюджетами и целями.
 * - Отправка уведомлений через сервис NotificationService.
 */
public class FinanceTracker {
    /**
     * Текущий аутентифицированный пользователь.
     * Если пользователь не аутентифицирован, значение равно null.
     */
    private User currentUser;

    /**
     * Сервис для отправки уведомлений пользователям.
     */
    private NotificationService notificationService;

    private UserService userService;
    private TransactionService transactionService;
    private BudgetService budgetService;
    private GoalService goalService;

    public enum LoginResult {
        SUCCESS,
        INVALID_CREDENTIALS,
        USER_BANNED
    }


    /**
     * Конструктор класса FinanceTracker.
     * Инициализирует необходимые структуры данных и создает администратора по умолчанию.
     *
     * @param notificationService сервис уведомлений, который будет использоваться для отправки уведомлений.
     * @throws IllegalArgumentException если notificationService равен null.
     */

    public FinanceTracker(UserService userService, TransactionService transactionService, BudgetService budgetService,
                          GoalService goalService ,NotificationService notificationService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.goalService = goalService;
        this.notificationService = notificationService;
        userService.registerUser("admin@example.com","admin123", "Admin", "admin");
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param email электронная почта пользователя. Не может быть null или пустой строкой.
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @param name имя пользователя. Не может быть null или пустой строкой.
     * @param role статус пользователя. Не может быть null или пустой строкой.
     * @return true, если регистрация прошла успешно; false, если пользователь с таким email уже существует.
     * @throws IllegalArgumentException если любой из параметров равен null или пустой строке.
     */
    public boolean registerUser(String email, String password, String name, String role) {
        return userService.registerUser(email, password, name, role);
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param email электронная почта пользователя. Не может быть null или пустой строкой.
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @return true, если вход выполнен успешно; false, если пользователь не найден или пароль неверен.
     * @throws IllegalArgumentException если email или password равен null или пустой строке.
     */
    public LoginResult loginUser(String email, String password) {
        try {
            if (userService.loginUser(email, password)) {
                currentUser = userService.getUserByEmail(email);
                return LoginResult.SUCCESS;
            }
            return LoginResult.INVALID_CREDENTIALS;
        } catch (IllegalStateException e) {
            return LoginResult.USER_BANNED;
        }
    }

    /**
     * Выход текущего пользователя из системы.
     * Устанавливает значение currentUser в null.
     */
    public void logoutUser() {
        currentUser = null;
    }


    /**
     * Удаляет пользователя из системы по его идентификатору.
     * Если удаляемый пользователь является текущим, выполняется выход из системы.
     *
     * @param id уникальный идентификатор пользователя.
     * @throws IllegalArgumentException если пользователь с указанным id не найден.
     */
    public void deleteUser(String id) {
        userService.deleteUser(id);
        if (currentUser != null && currentUser.getId().equals(id)) {
            logoutUser();
        }
    }

    public void banUser(String id) {
        userService.banUser(id);
    }

    public String getId() {
        return currentUser.getId();
    }

    public boolean hasAccess(String id) {
        return userService.hasAccess(id);
    }

    public boolean isPasswordEqual(String password) {
        return userService.isPasswordEqual(currentUser.getId(), password);
    }

    public boolean isUserExist(String id) {
        return userService.isUserExist(id);
    }

    /**
     * Добавляет транзакцию для текущего пользователя.
     * Если транзакция является доходом и у пользователя установлена финансовая цель,
     * сумма транзакции добавляется к цели.
     *
     * @param amount сумма транзакции.
     * @param category категория транзакции.
     * @param date дата транзакции.
     * @param description описание транзакции.
     * @param isIncome true, если транзакция является доходом; false, если расходом.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void addTransaction(double amount, String category, LocalDate date,
                               String description, boolean isIncome) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        String id = getId();

        transactionService.addTransaction(id, amount ,category, date, description, isIncome);
        if (budgetService.isBudgetSet(id)) {
            if (!isIncome && date.toString().substring(0,7).equals(budgetService.getMonth(id))) {
                budgetService.addMonthlyExpress(id, amount);
            }
        }
        if (goalService.isGoalSet(id) && isIncome) {
            goalService.addAmount(id, amount);
        }

    }

    /**
     * Удаляет транзакцию у текущего пользователя по её идентификатору.
     *
     * @param id уникальный идентификатор транзакции.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public boolean removeTransaction(String id) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
       return transactionService.removeTransaction(currentUser.getId(), id);
    }

    public boolean isTransactionThere(String id) {
        return transactionService.isTransactionThere(currentUser.getId(), id);
    }

    public void setTransactionAmount(String id, double amount) {
        transactionService.setTransactionAmount(currentUser.getId(), id, amount);
    }

    public void setTransactionCategory(String id, String category) {
        transactionService.setTransactionCategory(currentUser.getId(), id, category);
    }

    public void setTransactionDescription(String id, String description) {
        transactionService.setTransactionDescription(currentUser.getId(), id, description);
    }

    public String viewTransactionsNoFilter(String id) {
        return transactionService.viewTransactionNoFilter(currentUser.getId());
    }

    public String viewTransactionsDateFilter(String id, LocalDate dateFilter) {
        return transactionService.viewTransactionsDateFilter(id, dateFilter);
    }

    public String viewTransactionsCategoryFilter(String id, String categoryFilter) {
        return transactionService.viewTransactionsCategoryFilter(id, categoryFilter);
    }

    public String viewTransactionsIsIncomeFilter(String id, boolean isIncomeFilter) {
        return transactionService.viewTransactionsIsIncomeFilter(id, isIncomeFilter);
    }

    /**
     * Изменяет email текущего пользователя.
     *
     * @param email новый email. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если email равен null или пустой строке.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void changeEmail(String email) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        userService.changeEmail(currentUser.getId(),email);
    }

    /**
     * Изменяет пароль текущего пользователя.
     *
     * @param password новый пароль. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если пароль равен null или пустой строке.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void changePassword(String password) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        userService.changePassword(currentUser.getId(), password);
    }

    /**
     * Изменяет имя текущего пользователя.
     *
     * @param name новое имя. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если имя равно null или пустой строке.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void changeName(String name) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        userService.changeName(currentUser.getId(), name);
    }

    /**
     * Отображает профиль текущего пользователя.
     * Выводит статус, имя, email и скрытый пароль.
     *
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public String viewProfile() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return userService.viewProfile(currentUser.getId());
    }

    /**
     * Отображает список всех пользователей в системе.
     * Форматированный вывод включает ID, Email, Имя и Статус пользователя.
     * Метод доступен только для аутентифицированных пользователей.
     */
    public void viewUsersList() {
        if (currentUser == null) return;
        userService.viewUsersList(currentUser.getId());
    }


    /**
     * Возвращает месяц, связанный с бюджетом пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return месяц, связанный с бюджетом.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public String getMonth(String id) {
        return budgetService.getMonth(id);
    }

    /**
     * Добавляет бюджет для пользователя на указанный месяц.
     * Если у пользователя есть транзакции за этот месяц, они учитываются в расходах.
     *
     * @param month месяц для которого устанавливается бюджет.
     * @param budget сумма бюджета.
     * @throws IllegalArgumentException если месяц или сумма бюджета некорректны.
     */
    public void addBudget(String month, double budget) {
        budgetService.addBudget(currentUser.getId(), month, budget);
        budgetService.addMonthlyExpress(currentUser.getId(),
                transactionService.calculateMonthlyExpress(currentUser.getId(), month));
    }

    /**
     * Проверяет, установлен ли бюджет для пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если бюджет установлен; false, если нет.
     */
    public boolean isBudgetSet(String id) {
        return budgetService.isBudgetSet(id);
    }

    /**
     * Возвращает месячный бюджет пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return сумма месячного бюджета.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getMonthlyBudget(String id) {
        return budgetService.getMonthlyBudget(id);
    }

    /**
     * Возвращает сумму расходов пользователя за текущий месяц.
     *
     * @param id уникальный идентификатор пользователя.
     * @return сумма расходов.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getMonthlyExpress(String id) {
        return budgetService.getMonthlyExpress(id);
    }

    /**
     * Добавляет сумму к расходам пользователя за текущий месяц.
     *
     * @param id уникальный идентификатор пользователя.
     * @param express сумма расходов.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public void addMonthlyExpress(String id, double express) {
        budgetService.addMonthlyExpress(id, express);
    }

    /**
     * Возвращает оставшуюся сумму бюджета пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return оставшаяся сумма бюджета.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getRemaining(String id) {
        return budgetService.getRemaining(id);
    }

    public String viewBudget() {
        return budgetService.viewBudget(currentUser.getId());
    }
    /**
     * Устанавливает финансовую цель для пользователя.
     *
     * @param name название цели.
     * @param target целевая сумма.
     * @throws IllegalArgumentException если название цели или целевая сумма некорректны.
     */
    public void setGoal(String name, double target) {
        goalService.setGoal(currentUser.getId(), name, target);
    }

    /**
     * Добавляет сумму к текущему прогрессу финансовой цели пользователя.
     *
     * @param amount сумма для добавления.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public void addAmount(double amount) {
        goalService.addAmount(currentUser.getId(), amount);
    }

    /**
     * Проверяет, установлена ли финансовая цель для пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если цель установлена; false, если нет.
     */
    public boolean isGoalSet(String id) {
        return goalService.isGoalSet(id);
    }

    /**
     * Возвращает прогресс достижения финансовой цели пользователя в процентах.
     *
     * @param id уникальный идентификатор пользователя.
     * @return прогресс в процентах.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public int getProgress(String id) {
        return goalService.getProgress(id);
    }

    /**
     * Возвращает целевую сумму финансовой цели пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return целевая сумма.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public double getTargetAmount(String id) {
        return goalService.getTargetAmount(id);
    }

    /**
     * Возвращает название финансовой цели пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return название цели.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public String getGoalName(String id) {
        return goalService.getGoalName(id);
    }

    public String viewGoal() {
        return goalService.viewGoal(currentUser.getId());
    }

    /**
     * Возвращает текущий баланс пользователя.
     * Баланс рассчитывается как сумма всех доходов за вычетом всех расходов.
     *
     * @return текущий баланс пользователя.
     */
    public double getBalance() {
        return transactionService.getBalance(currentUser.getId());
    }

    /**
     * Возвращает сумму доходов пользователя за указанный период.
     *
     * @param start начальная дата периода.
     * @param end конечная дата периода.
     * @return сумма доходов за период.
     */
    public double getIncomeOfPeriod(LocalDate start, LocalDate end) {
        return transactionService.getIncomeOfPeriod(currentUser.getId(),start,end);
    }

    /**
     * Возвращает сумму расходов пользователя за указанный период.
     *
     * @param start начальная дата периода.
     * @param end конечная дата периода.
     * @return сумма расходов за период.
     */
    public double getExpensesOfPeriod(LocalDate start, LocalDate end) {
        return transactionService.getExpensesOfPeriod(currentUser.getId(), start, end);
    }

    /**
     * Возвращает расходы пользователя по категориям за указанный период.
     *
     * @param start начальная дата периода.
     * @param end конечная дата периода.
     * @return Map<String, Double>, где ключ — категория, а значение — сумма расходов по этой категории.
     */
    public Map<String, Double> getExpensesByCategory(LocalDate start, LocalDate end) {
        return transactionService.getExpensesByCategory(currentUser.getId(), start, end);
    }


    /**
     * Генерирует финансовый отчёт для пользователя за указанный период.
     * Отчёт включает текущий баланс, доходы, расходы и расходы по категориям.
     *
     * @param start начальная дата периода.
     * @param end конечная дата периода.
     * @return строка, содержащая финансовый отчёт.
     */
    public String generateReport(LocalDate start, LocalDate end) {
        return transactionService.generateReport(currentUser.getId(), start, end);
    }

    /**
     * Проверяет, превышен ли лимит расходов пользователя.
     * Если лимит превышен, отправляет уведомление через сервис NotificationService.
     *
     * @param id уникальный идентификатор пользователя.
     * @param recipient получатель уведомления (например, email).
     */
    public void checkExpenseLimit(String id, String recipient) {
        if (!budgetService.isBudgetSet(id)) {
            return;
        }
        double expenseLimit = budgetService.getMonthlyBudget(id);
        double totalExpenses = budgetService.getMonthlyExpress(id);
        if (totalExpenses > expenseLimit) {
            String message = String.format("Превышен лимит расходов! Текущие расходы: %.2f, Лимит: %.2f",
                    totalExpenses, expenseLimit);
            notificationService.sendNotification(recipient, message);
        }
    }
}
