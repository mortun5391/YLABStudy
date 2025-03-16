package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.time.LocalDate;
import java.util.Map;

/**
 * Класс FinanceTracker представляет собой систему управления финансами,
 * которая позволяет регистрировать пользователей, управлять их транзакциями,
 * бюджетами и финансовыми целями, а также отправлять уведомления.
 */
public class FinanceTracker {
    private User currentUser;
    private NotificationService notificationService;
    private UserService userService;
    private TransactionService transactionService;
    private BudgetService budgetService;
    private GoalService goalService;

    /**
     * Результат попытки входа пользователя.
     */
    public enum LoginResult {
        SUCCESS,
        INVALID_CREDENTIALS,
        USER_BANNED
    }

    /**
     * Конструктор класса FinanceTracker.
     *
     * @param userService        сервис для работы с пользователями.
     * @param transactionService сервис для работы с транзакциями.
     * @param budgetService      сервис для работы с бюджетами.
     * @param goalService        сервис для работы с финансовыми целями.
     * @param notificationService сервис уведомлений.
     */
    public FinanceTracker(UserService userService, TransactionService transactionService,
                          BudgetService budgetService, GoalService goalService,
                          NotificationService notificationService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.goalService = goalService;
        this.notificationService = notificationService;

        // Регистрация администратора по умолчанию
        userService.registerUser("admin@example.com", "admin123", "Admin", "admin");
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param email    электронная почта пользователя.
     * @param password пароль пользователя.
     * @param name     имя пользователя.
     * @param role     роль пользователя.
     * @return true, если регистрация прошла успешно; false, если пользователь с таким email уже существует.
     */
    public boolean registerUser(String email, String password, String name, String role) {
        return userService.registerUser(email, password, name, role);
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param email    электронная почта пользователя.
     * @param password пароль пользователя.
     * @return результат попытки входа (SUCCESS, INVALID_CREDENTIALS, USER_BANNED).
     */
    public LoginResult loginUser(String email, String password) {
        try {
            if (userService.loginUser(email, password)) {
                currentUser = userService.getUserByEmail(email).orElse(null);
                return LoginResult.SUCCESS;
            }
            return LoginResult.INVALID_CREDENTIALS;
        } catch (IllegalStateException e) {
            return LoginResult.USER_BANNED;
        }
    }

    /**
     * Выход текущего пользователя из системы.
     */
    public void logoutUser() {
        currentUser = null;
    }

    /**
     * Удаляет пользователя из системы по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     */
    public void deleteUser(long id) {
        userService.deleteUser(id);
        if (currentUser != null && currentUser.getId() == id) {
            logoutUser();
        }
    }

    /**
     * Блокирует пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     */
    public void banUser(long id) {
        userService.banUser(id);
    }

    /**
     * Возвращает идентификатор текущего пользователя.
     *
     * @return идентификатор текущего пользователя.
     */
    public long getId() {
        return currentUser.getId();
    }

    /**
     * Проверяет, имеет ли пользователь доступ к определенным операциям.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если пользователь имеет доступ; false, если нет.
     */
    public boolean hasAccess(long id) {
        return userService.hasAccess(id);
    }

    /**
     * Проверяет, совпадает ли пароль пользователя с указанным паролем.
     *
     * @param password пароль для проверки.
     * @return true, если пароль совпадает; false, если нет.
     */
    public boolean isPasswordEqual(String password) {
        return userService.isPasswordEqual(currentUser.getId(), password);
    }

    /**
     * Проверяет, существует ли пользователь с указанным идентификатором.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если пользователь существует; false, если нет.
     */
    public boolean isUserExist(long id) {
        return userService.isUserExist(id);
    }

    /**
     * Добавляет транзакцию для текущего пользователя.
     *
     * @param amount      сумма транзакции.
     * @param category    категория транзакции.
     * @param date        дата транзакции.
     * @param description описание транзакции.
     * @param isIncome    true, если транзакция является доходом; false, если расходом.
     */
    public void addTransaction(double amount, String category, LocalDate date,
                               String description, boolean isIncome) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        long id = currentUser.getId();

        transactionService.addTransaction(new Transaction(id, amount, category, date, description, isIncome));
        if (budgetService.isBudgetSet(id)) {
            if (!isIncome && date.toString().substring(0, 7).equals(budgetService.getMonth(id))) {
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
     * @param transactionId уникальный идентификатор транзакции.
     * @return true, если транзакция успешно удалена; false, если транзакция не найдена.
     */
    public boolean removeTransaction(long transactionId) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return transactionService.removeTransaction(transactionId);
    }

    /**
     * Проверяет, существует ли транзакция с указанным идентификатором у текущего пользователя.
     *
     * @param transactionId уникальный идентификатор транзакции.
     * @return true, если транзакция существует; false, если нет.
     */
    public boolean isTransactionThere(long transactionId) {
        return transactionService.isTransactionThere(transactionId);
    }

    /**
     * Устанавливает новую сумму для транзакции.
     *
     * @param transactionId уникальный идентификатор транзакции.
     * @param amount        новая сумма транзакции.
     */
    public void setTransactionAmount(long transactionId, double amount) {
        transactionService.setTransactionAmount(transactionId, amount);
    }

    /**
     * Устанавливает новую категорию для транзакции.
     *
     * @param transactionId уникальный идентификатор транзакции.
     * @param category      новая категория транзакции.
     */
    public void setTransactionCategory(long transactionId, String category) {
        transactionService.setTransactionCategory(transactionId, category);
    }

    /**
     * Устанавливает новое описание для транзакции.
     *
     * @param transactionId уникальный идентификатор транзакции.
     * @param description   новое описание транзакции.
     */
    public void setTransactionDescription(long transactionId, String description) {
        transactionService.setTransactionDescription(transactionId, description);
    }

    /**
     * Возвращает список всех транзакций текущего пользователя без фильтрации.
     *
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsNoFilter(long id) {
        return transactionService.viewTransactionNoFilter(id);
    }

    /**
     * Возвращает список транзакций текущего пользователя, отфильтрованных по дате.
     *
     * @param dateFilter дата для фильтрации.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsDateFilter(long id,LocalDate dateFilter) {
        return transactionService.viewTransactionsDateFilter(id, dateFilter);
    }

    /**
     * Возвращает список транзакций текущего пользователя, отфильтрованных по категории.
     *
     * @param categoryFilter категория для фильтрации.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsCategoryFilter(long id, String categoryFilter) {
        return transactionService.viewTransactionsCategoryFilter(id, categoryFilter);
    }

    /**
     * Возвращает список транзакций текущего пользователя, отфильтрованных по типу (доход/расход).
     *
     * @param isIncomeFilter true для доходов, false для расходов.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsIsIncomeFilter(long id, boolean isIncomeFilter) {
        return transactionService.viewTransactionsIsIncomeFilter(id, isIncomeFilter);
    }

    /**
     * Изменяет email текущего пользователя.
     *
     * @param email новый email.
     */
    public void changeEmail(String email) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        userService.changeEmail(currentUser.getId(), email);
    }

    /**
     * Изменяет пароль текущего пользователя.
     *
     * @param password новый пароль.
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
     * @param name новое имя.
     */
    public void changeName(String name) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        userService.changeName(currentUser.getId(), name);
    }

    /**
     * Отображает профиль текущего пользователя.
     *
     * @return строка, содержащая информацию о профиле.
     */
    public String viewProfile() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return userService.viewProfile(currentUser.getId());
    }

    /**
     * Отображает список всех пользователей в системе.
     */
    public void viewUsersList() {
        if (currentUser == null) return;
        userService.viewUsersList(currentUser.getId());
    }

    /**
     * Возвращает месяц, связанный с бюджетом пользователя.
     *
     * @return месяц, связанный с бюджетом.
     */
    public String getMonth() {
        return budgetService.getMonth(currentUser.getId());
    }

    /**
     * Добавляет бюджет для пользователя на указанный месяц.
     *
     * @param month  месяц для которого устанавливается бюджет.
     * @param budget сумма бюджета.
     */
    public void addBudget(String month, double budget) {
        budgetService.addBudget(currentUser.getId(), month, budget);
        budgetService.addMonthlyExpress(currentUser.getId(),
                transactionService.calculateMonthlyExpress(currentUser.getId(), month));
    }

    /**
     * Проверяет, установлен ли бюджет для пользователя.
     *
     * @return true, если бюджет установлен; false, если нет.
     */
    public boolean isBudgetSet() {
        return budgetService.isBudgetSet(currentUser.getId());
    }

    /**
     * Возвращает месячный бюджет пользователя.
     *
     * @return сумма месячного бюджета.
     */
    public double getMonthlyBudget() {
        return budgetService.getMonthlyBudget(currentUser.getId());
    }

    /**
     * Возвращает сумму расходов пользователя за текущий месяц.
     *
     * @return сумма расходов.
     */
    public double getMonthlyExpress() {
        return budgetService.getMonthlyExpress(currentUser.getId());
    }

    /**
     * Возвращает оставшуюся сумму бюджета пользователя.
     *
     * @return оставшаяся сумма бюджета.
     */
    public double getRemaining() {
        return budgetService.getRemaining(currentUser.getId());
    }

    /**
     * Возвращает информацию о бюджете текущего пользователя.
     *
     * @return строка, содержащая информацию о бюджете.
     */
    public String viewBudget() {
        return budgetService.viewBudget(currentUser.getId());
    }

    /**
     * Устанавливает финансовую цель для пользователя.
     *
     * @param name   название цели.
     * @param target целевая сумма.
     */
    public void setGoal(String name, double target) {
        goalService.setGoal(currentUser.getId(), name, target);
    }

    /**
     * Проверяет, установлена ли финансовая цель для пользователя.
     *
     * @return true, если цель установлена; false, если нет.
     */
    public boolean isGoalSet() {
        return goalService.isGoalSet(currentUser.getId());
    }

    /**
     * Возвращает прогресс достижения финансовой цели пользователя в процентах.
     *
     * @return прогресс в процентах.
     */
    public int getProgress() {
        return goalService.getProgress(currentUser.getId());
    }

    /**
     * Возвращает целевую сумму финансовой цели пользователя.
     *
     * @return целевая сумма.
     */
    public double getTargetAmount() {
        return goalService.getTargetAmount(currentUser.getId());
    }

    /**
     * Возвращает название финансовой цели пользователя.
     *
     * @return название цели.
     */
    public String getGoalName() {
        return goalService.getGoalName(currentUser.getId());
    }

    /**
     * Возвращает информацию о финансовой цели текущего пользователя.
     *
     * @return строка, содержащая информацию о цели.
     */
    public String viewGoal() {
        return goalService.viewGoal(currentUser.getId());
    }

    /**
     * Возвращает текущий баланс пользователя.
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
     * @param end   конечная дата периода.
     * @return сумма доходов за период.
     */
    public double getIncomeOfPeriod(LocalDate start, LocalDate end) {
        return transactionService.getIncomeOfPeriod(currentUser.getId(), start, end);
    }

    /**
     * Возвращает сумму расходов пользователя за указанный период.
     *
     * @param start начальная дата периода.
     * @param end   конечная дата периода.
     * @return сумма расходов за период.
     */
    public double getExpensesOfPeriod(LocalDate start, LocalDate end) {
        return transactionService.getExpensesOfPeriod(currentUser.getId(), start, end);
    }

    /**
     * Возвращает строку с расходами по категориям за указанный период.
     *
     * @param start начальная дата периода.
     * @param end   конечная дата периода.
     * @return строка, содержащая расходы по категориям.
     */
    public String getExpensesByCategoryAsString(LocalDate start, LocalDate end) {
        Map<String, Double> expensesByCategory = transactionService.getExpensesByCategory(currentUser.getId(), start, end);

        StringBuilder result = new StringBuilder("Расходы по категориям за период:\n");
        expensesByCategory.forEach((category, amount) ->
                result.append(String.format("- %s: %.2f\n", category, amount)));

        return result.toString();
    }

    /**
     * Генерирует финансовый отчёт для пользователя за указанный период.
     *
     * @param start начальная дата периода.
     * @param end   конечная дата периода.
     * @return строка, содержащая финансовый отчёт.
     */
    public String generateReport(LocalDate start, LocalDate end) {
        return transactionService.generateReport(currentUser.getId(), start, end);
    }

    /**
     * Проверяет, превышен ли лимит расходов пользователя.
     *
     * @param recipient получатель уведомления.
     */
    public void checkExpenseLimit(String recipient) {
        if (!budgetService.isBudgetSet(currentUser.getId())) {
            return;
        }
        double expenseLimit = budgetService.getMonthlyBudget(currentUser.getId());
        double totalExpenses = budgetService.getMonthlyExpress(currentUser.getId());
        if (totalExpenses > expenseLimit) {
            String message = String.format("Превышен лимит расходов! Текущие расходы: %.2f, Лимит: %.2f",
                    totalExpenses, expenseLimit);
            notificationService.sendNotification(recipient, message);
        }
    }
}