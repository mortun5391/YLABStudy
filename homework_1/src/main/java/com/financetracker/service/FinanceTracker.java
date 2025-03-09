package com.financetracker.service;

import com.financetracker.model.BudgetRecord;
import com.financetracker.model.Goal;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Класс FinanceTracker представляет собой систему управления финансами,
 * которая позволяет регистрировать пользователей, управлять их транзакциями,
 * бюджетами и финансовыми целями, а также отправлять уведомления.
 *
 * Основные функции:
 * - Регистрация и аутентификация пользователей.
 * - Управление транзакциями, бюджетами и целями.
 * - Отправка уведомлений через сервис NotificationService.
 */
public class FinanceTracker {
    /**
     * Карта для хранения пользователей по их уникальному идентификатору.
     * Ключ: идентификатор пользователя (String).
     * Значение: объект User.
     */
    private Map<String, User> users;

    /**
     * Карта для хранения пользователей по их электронной почте.
     * Ключ: электронная почта пользователя (String).
     * Значение: объект User.
     */
    private Map<String, User> emailToUserMap;

    /**
     * Карта для хранения бюджетов пользователей.
     * Ключ: идентификатор бюджета (String).
     * Значение: объект BudgetRecord.
     */
    private Map<String, BudgetRecord> budgets;

    /**
     * Карта для хранения финансовых целей пользователей.
     * Ключ: идентификатор цели (String).
     * Значение: объект Goal.
     */
    private Map<String, Goal> goals;

    /**
     * Текущий аутентифицированный пользователь.
     * Если пользователь не аутентифицирован, значение равно null.
     */
    private User currentUser;

    /**
     * Сервис для отправки уведомлений пользователям.
     */
    private NotificationService notificationService;


    /**
     * Конструктор класса FinanceTracker.
     * Инициализирует необходимые структуры данных и создает администратора по умолчанию.
     *
     * @param notificationService сервис уведомлений, который будет использоваться для отправки уведомлений.
     * @throws IllegalArgumentException если notificationService равен null.
     */
    public FinanceTracker(NotificationService notificationService) {
        users = new HashMap<>();
        emailToUserMap = new HashMap<>();
        budgets = new HashMap<>();
        goals = new HashMap<>();
        User admin = new User("admin@example.ru","admin123", "Admin", "admin");
        users.put(admin.getId(), admin);
        emailToUserMap.put(admin.getEmail(), admin);
        this.notificationService = notificationService;
    }

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param email электронная почта пользователя. Не может быть null или пустой строкой.
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @param name имя пользователя. Не может быть null или пустой строкой.
     * @param status статус пользователя. Не может быть null или пустой строкой.
     * @return true, если регистрация прошла успешно; false, если пользователь с таким email уже существует.
     * @throws IllegalArgumentException если любой из параметров равен null или пустой строке.
     */
    public boolean registerUser(String email, String password, String name, String status) {
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                name == null || name.trim().isEmpty() ||
                status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Email, password, name, and status cannot be null or empty");
        }
        if (emailToUserMap.containsKey(email)) {
            return false;
        }
        User user = new User(email, password, name, status);
        users.put(user.getId(), user);
        emailToUserMap.put(user.getEmail(), user);
        return true;
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param email электронная почта пользователя. Не может быть null или пустой строкой.
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @return true, если вход выполнен успешно; false, если пользователь не найден или пароль неверен.
     * @throws IllegalArgumentException если email или password равен null или пустой строке.
     */
    public boolean loginUser(String email, String password) {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password cannot be null or empty");
        }
        User user = emailToUserMap.get(email);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
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
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        if (currentUser != null && currentUser.getId().equals(id)) {
            logoutUser();
        }
        emailToUserMap.remove(users.get(id).getEmail());
        users.remove(id);
    }

    /**
     * Возвращает текущего аутентифицированного пользователя.
     *
     * @return объект User, представляющий текущего пользователя; null, если пользователь не аутентифицирован.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @return объект User, если пользователь найден; null, если пользователь отсутствует.
     */
    public User getUser(String id) {
        return users.get(id);
    }


    /**
     * Добавляет транзакцию для текущего пользователя.
     * Если транзакция является доходом и у пользователя установлена финансовая цель,
     * сумма транзакции добавляется к цели.
     *
     * @param id уникальный идентификатор транзакции.
     * @param amount сумма транзакции.
     * @param category категория транзакции.
     * @param date дата транзакции.
     * @param description описание транзакции.
     * @param isIncome true, если транзакция является доходом; false, если расходом.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void addTransaction(String id, double amount, String category, LocalDate date,
                               String description, boolean isIncome) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        currentUser.addTransaction(new Transaction(id, amount, category, date, description, isIncome));
        if (isGoalSet(currentUser.getId()) && isIncome) {
            addAmount(currentUser.getId(), amount);
        }
    }

    /**
     * Удаляет транзакцию у текущего пользователя по её идентификатору.
     *
     * @param id уникальный идентификатор транзакции.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void removeTransaction(String id) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        currentUser.getTransactions().remove(id);
    }

    /**
     * Возвращает транзакцию текущего пользователя по её идентификатору.
     *
     * @param id уникальный идентификатор транзакции.
     * @return объект Transaction, если транзакция найдена; null, если транзакция отсутствует.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public Transaction getTransaction(String id) {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        return currentUser.getTransaction(id);
    }

    /**
     * Возвращает все транзакции пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     * @return Map<String, Transaction>, содержащая транзакции пользователя; пустая Map, если пользователь не найден.
     */
    public Map<String, Transaction> getTransactions(String id) {
        if (users.containsKey(id)) {
            return users.get(id).getTransactions();
        }
        return Collections.emptyMap();
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
        currentUser.setEmail(email);
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
        currentUser.setPassword(password);
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
        currentUser.setName(name);
    }

    /**
     * Отображает профиль текущего пользователя.
     * Выводит статус, имя, email и скрытый пароль.
     *
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void viewProfile() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is currently logged in");
        }
        System.out.println(
                "\nСтатус: " + currentUser.getStatus() +
                        "\nИмя: " + currentUser.getName() +
                        "\nEmail: " + currentUser.getEmail() +
                        "\nПароль: ******** ");
    }

    /**
     * Отображает список всех пользователей в системе.
     * Форматированный вывод включает ID, Email, Имя и Статус пользователя.
     * Метод доступен только для аутентифицированных пользователей.
     */
    public void viewUsersList() {
        if (currentUser == null) return;
        System.out.println("Список пользователей: ");
        System.out.printf("%-10s %-20s %-15s %-10s%n", "ID", "Email", "Имя", "Статус");

        // Данные пользователей
        for (User user : users.values()) {
            System.out.printf(
                    "%-10s %-20s %-15s %-10s%n",
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getStatus()
            );
        }
    }


    /**
     * Возвращает месяц, связанный с бюджетом пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return месяц, связанный с бюджетом.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public String getMonth(String id) {
        if (!budgets.containsKey(id)) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        return budgets.get(id).getMonth();
    }

    /**
     * Добавляет бюджет для пользователя на указанный месяц.
     * Если у пользователя есть транзакции за этот месяц, они учитываются в расходах.
     *
     * @param id уникальный идентификатор пользователя.
     * @param month месяц для которого устанавливается бюджет.
     * @param budget сумма бюджета.
     * @throws IllegalArgumentException если месяц или сумма бюджета некорректны.
     */
    public void addBudget(String id, String month, double budget) {
        if (month == null || month.trim().isEmpty() || budget < 0) {
            throw new IllegalArgumentException("Month cannot be null or empty, and budget must be non-negative");
        }
        budgets.put(id, new BudgetRecord(month, budget));
        Map<String, Transaction> transactions = getTransactions(id);
        for (Transaction transaction : transactions.values()) {
            if (!transaction.isIncome() && transaction.getDate().toString().substring(0, 7).equals(month)) {
                addMonthlyExpress(id, transaction.getAmount());
            }
        }
    }

    /**
     * Проверяет, установлен ли бюджет для пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если бюджет установлен; false, если нет.
     */
    public boolean isBudgetSet(String id) {
        return budgets.containsKey(id);
    }

    /**
     * Возвращает месячный бюджет пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return сумма месячного бюджета.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getMonthlyBudget(String id) {
        if (!budgets.containsKey(id)) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        return budgets.get(id).getBudget();
    }

    /**
     * Возвращает сумму расходов пользователя за текущий месяц.
     *
     * @param id уникальный идентификатор пользователя.
     * @return сумма расходов.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getMonthlyExpress(String id) {
        if (!budgets.containsKey(id)) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        return budgets.get(id).getExpress();
    }

    /**
     * Добавляет сумму к расходам пользователя за текущий месяц.
     *
     * @param id уникальный идентификатор пользователя.
     * @param express сумма расходов.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public void addMonthlyExpress(String id, double express) {
        if (!budgets.containsKey(id)) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        budgets.get(id).addExpress(express);
    }

    /**
     * Возвращает оставшуюся сумму бюджета пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return оставшаяся сумма бюджета.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getRemaining(String id) {
        if (!budgets.containsKey(id)) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        return budgets.get(id).getBudget() - budgets.get(id).getExpress();
    }

    /**
     * Устанавливает финансовую цель для пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @param name название цели.
     * @param target целевая сумма.
     * @throws IllegalArgumentException если название цели или целевая сумма некорректны.
     */
    public void setGoal(String id, String name, double target) {
        if (name == null || name.trim().isEmpty() || target < 0) {
            throw new IllegalArgumentException("Goal name cannot be null or empty, and target must be non-negative");
        }
        goals.put(id, new Goal(target, name));
    }

    /**
     * Добавляет сумму к текущему прогрессу финансовой цели пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @param amount сумма для добавления.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public void addAmount(String id, double amount) {
        if (!goals.containsKey(id)) {
            throw new IllegalArgumentException("Goal for user with id " + id + " is not set");
        }
        goals.get(id).addCurrentAmount(amount);
    }

    /**
     * Проверяет, установлена ли финансовая цель для пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если цель установлена; false, если нет.
     */
    public boolean isGoalSet(String id) {
        return goals.containsKey(id);
    }

    /**
     * Возвращает прогресс достижения финансовой цели пользователя в процентах.
     *
     * @param id уникальный идентификатор пользователя.
     * @return прогресс в процентах.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public int getProgress(String id) {
        if (!goals.containsKey(id)) {
            throw new IllegalArgumentException("Goal for user with id " + id + " is not set");
        }
        return goals.get(id).getProgress();
    }

    /**
     * Возвращает целевую сумму финансовой цели пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return целевая сумма.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public double getTargetAmount(String id) {
        if (!goals.containsKey(id)) {
            throw new IllegalArgumentException("Goal for user with id " + id + " is not set");
        }
        return goals.get(id).getTargetAmount();
    }

    /**
     * Возвращает название финансовой цели пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return название цели.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public String getGoalName(String id) {
        if (!goals.containsKey(id)) {
            throw new IllegalArgumentException("Goal for user with id " + id + " is not set");
        }
        return goals.get(id).getGoalName();
    }

    /**
     * Возвращает текущий баланс пользователя.
     * Баланс рассчитывается как сумма всех доходов за вычетом всех расходов.
     *
     * @param id уникальный идентификатор пользователя.
     * @return текущий баланс пользователя.
     */
    public double getBalance(String id) {
        return getTransactions(id).values().stream()
                .mapToDouble(transaction -> transaction.isIncome() ? transaction.getAmount() : -transaction.getAmount())
                .sum();
    }

    /**
     * Возвращает сумму доходов пользователя за указанный период.
     *
     * @param id уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end конечная дата периода.
     * @return сумма доходов за период.
     */
    public double getIncomeOfPeriod(String id, LocalDate start, LocalDate end) {
        return getTransactions(id).values().stream()
                .mapToDouble(transaction -> transaction.isIncome() && transaction.getDate().isAfter(start.minusDays(1))
                        && transaction.getDate().isBefore(end.plusDays(1)) ? transaction.getAmount() : 0)
                .sum();
    }

    /**
     * Возвращает сумму расходов пользователя за указанный период.
     *
     * @param id уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end конечная дата периода.
     * @return сумма расходов за период.
     */
    public double getExpensesOfPeriod(String id, LocalDate start, LocalDate end) {
        return getTransactions(id).values().stream()
                .mapToDouble(transaction -> !transaction.isIncome() && transaction.getDate().isAfter(start.minusDays(1))
                        && transaction.getDate().isBefore(end.plusDays(1)) ? transaction.getAmount() : 0)
                .sum();
    }

    /**
     * Возвращает расходы пользователя по категориям за указанный период.
     *
     * @param id уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end конечная дата периода.
     * @return Map<String, Double>, где ключ — категория, а значение — сумма расходов по этой категории.
     */
    public Map<String, Double> getExpensesByCategory(String id, LocalDate start, LocalDate end) {
        List<Transaction> transactions = getTransactions(id).values().stream().toList();

        Map<String, Double> expensesByCategory = new HashMap<>();

        transactions.stream()
                .filter(transaction -> !transaction.isIncome())
                .filter(transaction -> !transaction.getDate().isBefore(start) &&
                        !transaction.getDate().isAfter(end)) // Только расходы
                .forEach(transaction -> {
                    expensesByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
                });

        return expensesByCategory;
    }

    /**
     * Генерирует финансовый отчёт для пользователя за указанный период.
     * Отчёт включает текущий баланс, доходы, расходы и расходы по категориям.
     *
     * @param id уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end конечная дата периода.
     * @return строка, содержащая финансовый отчёт.
     */
    public String generateReport(String id, LocalDate start, LocalDate end) {
        double balance = getBalance(id);
        double income = getIncomeOfPeriod(id, start, end);
        double expense = getExpensesOfPeriod(id, start, end);
        Map<String, Double> expensesByCategory = getExpensesByCategory(id, start, end);

        StringBuilder report = new StringBuilder();
        report.append("=== Финансовый отчёт ===\n");
        report.append(String.format("Текущий баланс: %.2f \n", balance));
        report.append(String.format("Доход за период: %.2f \n", income));
        report.append(String.format("Расход за период: %.2f \n", expense));
        report.append("Расходы по категориям:\n");
        expensesByCategory.forEach((category, amount) ->
                report.append(String.format("- %s: %.2f \n", category, amount)));
        return report.toString();
    }

    /**
     * Проверяет, превышен ли лимит расходов пользователя.
     * Если лимит превышен, отправляет уведомление через сервис NotificationService.
     *
     * @param id уникальный идентификатор пользователя.
     * @param recipient получатель уведомления (например, email).
     */
    public void checkExpenseLimit(String id, String recipient) {
        if (!budgets.containsKey(id)) {
            return;
        }
        double expenseLimit = budgets.get(id).getBudget();

        double totalExpenses = budgets.get(id).getExpress();
        if (totalExpenses > expenseLimit) {
            String message = String.format("Превышен лимит расходов! Текущие расходы: %.2f, Лимит: %.2f",
                    totalExpenses, expenseLimit);
            notificationService.sendNotification(recipient, message);
        }
    }
}
