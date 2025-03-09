package com.financetracker.app;

import com.financetracker.model.Transaction;
import com.financetracker.service.ConsoleNotificationService;
import com.financetracker.service.FinanceTracker;
import com.financetracker.service.InputValidator;
import com.financetracker.service.NotificationService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс FinanceTrackerApp представляет собой консольное приложение для управления финансами.
 * Позволяет пользователям регистрироваться, входить в систему, управлять транзакциями,
 * бюджетами, финансовыми целями, а также просматривать статистику и аналитику.
 */
public class FinanceTrackerApp {
    private static FinanceTracker financeTracker;
    private static final Scanner scanner = new Scanner(System.in);
    private static final InputValidator inputValidator = new InputValidator(scanner);

    /**
     * Точка входа в приложение.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        NotificationService notificationService = new ConsoleNotificationService();
        financeTracker = new FinanceTracker(notificationService);
        while (true) {
            System.out.println("1. Регистрация");
            System.out.println("2. Вход");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 0:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    /**
     * Регистрирует нового пользователя в системе.
     * Запрашивает email, пароль, имя и создаёт нового пользователя.
     */
    private static void registerUser() {
        String email = inputValidator.getStringInput("Введите email: ");
        String password;
        while (true) {
            password = inputValidator.getStringInput("Введите пароль: ");
            String confirm = inputValidator.getStringInput("Подтвердите пароль: ");
            if (password.equals(confirm)) {
                break;
            }
            System.out.println("Пароли не совпадают! Попробуйте еще раз");
        }
        String name = inputValidator.getStringInput("Введите имя: ");
        if (financeTracker.registerUser(email, password, name, "user")) {
            System.out.println("Регистрация прошла успешно");
        }
        else {
            System.out.println("Пользователь с таким email уже существует");
        }
    }

    /**
     * Выполняет вход пользователя в систему.
     * Запрашивает email и пароль, проверяет их корректность.
     */
    private static void loginUser() {
        String email = inputValidator.getStringInput("Введите email: ");
        String password = inputValidator.getStringInput("Введите пароль: ");
        if (financeTracker.loginUser(email, password)) {
            if (financeTracker.getCurrentUser().getStatus().equals("banned")) {
                System.out.println("Ваш аккаунт заблокирован!");
                return;
            }
            System.out.println("Вход выполнен успешно");
            userMenu();
        }
        else {
            System.out.println("Неверный email или пароль");
        }
    }

    /**
     * Отображает меню пользователя после успешного входа в систему.
     * Позволяет управлять транзакциями, бюджетом, целями, просматривать статистику и профиль.
     */
    private static void userMenu() {
        while (true) {

            System.out.println("1. Управление транзакциями");
            System.out.println("2. Управление бюджетом");
            System.out.println("3. Управление целями");
            System.out.println("4. Статистика и аналитика");
            System.out.println("5. Просмотреть профиль");
            System.out.println("6. Просмотреть список пользователей");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");

            switch (choice) {
                case 1:
                    manageTransactions();
                    break;
                case 2:
                    manageBudget();
                    break;
                case 3:
                    manageGoals();
                    break;
                case 4:
                    manageStatistic();
                    break;
                case 5:
                    viewProfile();
                    return;
                case 6:
                    viewUsersList();
                    break;
                case 0:
                    financeTracker.logoutUser();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    /**
     * Управление транзакциями пользователя.
     * Позволяет добавлять, удалять, редактировать и просматривать транзакции.
     */
    private static void manageTransactions() {
        while (true) {
            System.out.println("1. Добавить транзакцию");
            System.out.println("2. Удалить транзакцию");
            System.out.println("3. Редактировать транзакцию");
            System.out.println("4. Просмотреть транзакции");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    removeTransaction();
                    break;
                case 3:
                    editTransaction();
                    break;
                case 4:
                    viewTransactions(financeTracker.getCurrentUser().getId());
                    break;
                case 0:
                    return;
            }

        }
    }

    /**
     * Добавляет новую транзакцию для текущего пользователя.
     * Запрашивает ID, сумму, категорию, дату, описание и тип транзакции (доход/расход).
     */
    private static void addTransaction() {
        double amount = inputValidator.getDoubleInput("Введите сумму: ");
        String category = inputValidator.getStringInput("Введите категорию: ");
        LocalDate date = inputValidator.getDateInput("Введите дату (гггг-мм-дд): ");
        String description = inputValidator.getStringInput("Введите описание: ");
        boolean isIncome = inputValidator.getBooleanInput("Это доход? (true/false): ");

        financeTracker.addTransaction(amount, category, date, description, isIncome);
        System.out.println("Транзакция добавлена");
    }

    /**
     * Удаляет транзакцию по её ID.
     * Запрашивает ID транзакции для удаления.
     */
    private static void removeTransaction() {
        String id = inputValidator.getStringInput("Введите ID транзакции для удаления");

        financeTracker.removeTransaction(id);
        System.out.println("Транзакция удалена");
    }

    /**
     * Редактирует существующую транзакцию.
     * Позволяет изменить сумму, категорию или описание транзакции.
     */
    private static void editTransaction() {
        String id = inputValidator.getStringInput("Введите ID транзакции для редактирования: ");
        Transaction transaction = financeTracker.getTransaction(id);
        if (transaction == null) {
            System.out.println("Транзакция не найдена");
            return;
        }
        while (true) {

            System.out.println("1. Изменить сумму");
            System.out.println("2. Изменить категорию");
            System.out.println("3. Изменить описание");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    double newAmount = inputValidator.getDoubleInput("Введите новую сумму: ");;
                    transaction.setAmount(newAmount);
                    break;
                case 2:
                    String newCategory = inputValidator.getStringInput("Введите новую категорию: ");
                    transaction.setCategory(newCategory);
                    break;
                case 3:
                    String newDescription = inputValidator.getStringInput("Введите новое описание: ");
                    transaction.setDescription(newDescription);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }

        }
    }

    /**
     * Просматривает транзакции пользователя с возможностью фильтрации.
     *
     * @param id уникальный идентификатор пользователя.
     */
    private static void viewTransactions(String id) {
        while (true) {

            System.out.println("Выберите тип фильтра: ");
            System.out.println("1. Без фильтра");
            System.out.println("2. По дате");
            System.out.println("3. По категории");
            System.out.println("4. По доходу (доход/расход)");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("");
            Map<String, Transaction> transactions = financeTracker.getTransactions(id);
            List<Transaction> filteredTransactions;
            switch (choice) {
                case 1:
                    filteredTransactions = transactions.values().stream().toList();
                    break;
                case 2:
                    LocalDate dateFilter = inputValidator.getDateInput("Введите дату для фильтрации(гггг-мм-дд): ");
                    filteredTransactions = transactions.values().stream()
                            .filter(transaction -> transaction.getDate().isEqual(dateFilter))
                            .collect(Collectors.toList());
                    break;
                case 3:
                    String categoryFilter = inputValidator.getStringInput("Введите категорию для фильтрации: ");
                    filteredTransactions = transactions.values().stream()
                            .filter(transaction -> transaction.getCategory().equals(categoryFilter))
                            .collect(Collectors.toList());
                    break;
                case 4:
                    boolean isIncomeFilter = inputValidator.getBooleanInput("Доход/расход? (введите true/false): ");
                    filteredTransactions = transactions.values().stream()
                            .filter(transaction -> transaction.isIncome() == isIncomeFilter)
                            .collect(Collectors.toList());
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
                    continue;
            }

            if (filteredTransactions.isEmpty()) {
                System.out.println("Список транзакций пуст");
            } else {
                System.out.println("Список транзакций: ");
                for (Transaction transaction : filteredTransactions) {
                    System.out.println("ID: " + transaction.getId() +
                            ", Сумма: " + transaction.getAmount() +
                            ", Категория: " + transaction.getCategory() +
                            ", Дата: " + transaction.getDate().toString() +
                            ", Описание: " + transaction.getDescription() +
                            ", Тип: " + (transaction.isIncome() ? "Доход" : "Расход"));
                }
            }

        }
    }

    /**
     * Отображает профиль текущего пользователя и предоставляет меню для его изменения или удаления.
     */
    private static void viewProfile() {
        financeTracker.viewProfile();
        while (true) {
            System.out.println("1. Изменить профиль" +
                             "\n2. Удалить профиль" +
                             "\n3. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    editProfile();
                    break;
                case 2:
                    deleteProfile();
                    return;
                case 3:
                    userMenu();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }


    // PROFILE SETTINGS
    /**
     * Меню для редактирования профиля пользователя.
     * Позволяет изменить email, пароль или имя.
     */
    private static void editProfile() {
        while (true) {
            System.out.println("1. Изменить email");
            System.out.println("2. Изменить пароль");
            System.out.println("3. Изменить имя");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    changeEmail();
                    break;
                case 2:
                    changePassword();
                    break;
                case 3:
                    changeName();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    /**
     * Изменяет email текущего пользователя.
     * Запрашивает новый email и подтверждение пароля.
     */
    private static void changeEmail() {
        String email = inputValidator.getStringInput("Введите новый email: ");
        String password = inputValidator.getStringInput("Подтвердите изменение (введите пароль): ");
        if (!password.equals(financeTracker.getCurrentUser().getPassword())) {
            System.out.println("Пароль неверный! Попробуйте снова");
            return;
        }
        financeTracker.changeEmail(email);
        System.out.println("Изменения сохранены. Ваш новый email: " + email);
    }

    /**
     * Изменяет имя текущего пользователя.
     * Запрашивает новое имя и подтверждение пароля.
     */
    private static void changePassword() {
        String oldPassword = inputValidator.getStringInput("Введите старый пароль: ");
        if (!oldPassword.equals(financeTracker.getCurrentUser().getPassword())) {
            System.out.println("Пароль неверный! Попробуйте снова");
            return;
        }
        String newPassword = inputValidator.getStringInput("Введите новый пароль: ");
        String confirm = inputValidator.getStringInput("Подтвердите пароль: ");
        if (newPassword.equals(confirm)) {
            System.out.println("Пароли не совпадают! Попробуйте снова");
            return;
        }
        financeTracker.changePassword(newPassword);
        System.out.println("Изменения сохранены");
    }

    /**
     * Удаляет профиль текущего пользователя.
     * Запрашивает подтверждение пароля для удаления.
     */
    private static void changeName() {
        String name = inputValidator.getStringInput("Введите новое имя: ");
        String password = inputValidator.getStringInput("Подтвердите изменение (введите пароль): ");
        if (!password.equals(financeTracker.getCurrentUser().getPassword())) {
            System.out.println("Пароль неверный! Попробуйте снова");
            return;
        }
        financeTracker.changeName(name);
        System.out.println("Изменения сохранены. Ваше новое имя: " + name);
    }

    /**
     * Удаляет профиль текущего пользователя.
     * Запрашивает подтверждение пароля для удаления.
     */
    private static void deleteProfile() {

        String password = inputValidator.getStringInput("Вы уверены что хотите удалить аккаунт? Для подтверждения введите пароль: ");
        if (!password.equals(financeTracker.getCurrentUser().getPassword())) {
            System.out.println("Пароль неверный! Попробуйте снова");
            viewProfile();
            return;
        }
        financeTracker.deleteUser(financeTracker.getCurrentUser().getId());
        System.out.println("Ваш профиль удален!");

    }

    // BUDGET MANAGE
    /**
     * Управление месячным бюджетом пользователя.
     * Позволяет установить бюджет и просмотреть текущие расходы и остаток.
     */
    private static void manageBudget() {
        while (true) {
            String id = financeTracker.getCurrentUser().getId();
            if (!financeTracker.isBudgetSet(id)) {
                System.out.println("Месячный бюджет не установлен");
            } else {
                System.out.println("Месячный бюджет: " + financeTracker.getMonthlyBudget(id));
                System.out.println("Расходы за месяц: " + financeTracker.getMonthlyExpress(id));
                System.out.println("Остаток бюджета: " + financeTracker.getRemaining(id));
                if (financeTracker.getRemaining(id) < 0) {
                    System.out.println("Внимание! Вы превысили месячный бюджет на " + Math.abs(financeTracker.getRemaining(id)));
                }
            }
            System.out.println("1. Установить месячный бюджет");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    String month = inputValidator.getStringInput("Введите месяц (гггг-мм) :");
                    double budget = inputValidator.getDoubleInput("Введите сумму бюджета: ");
                    financeTracker.addBudget(id, month, budget);
                    System.out.println("Месячный бюджет установлен!");
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    // GOAL MANAGE
    /**
     * Управление финансовыми целями пользователя.
     * Позволяет установить новую цель и просмотреть прогресс по текущей цели.
     */
    private static void manageGoals() {
        String id = financeTracker.getCurrentUser().getId();
        while (true) {
            if (!financeTracker.isGoalSet(id)) {
                System.out.println("Цель не установлена");
            } else {
                System.out.println("Цель: " + financeTracker.getGoalName(id));
                System.out.println("Целевая сумма: " + financeTracker.getTargetAmount(id));
                System.out.println("Прогресс по цели: " + financeTracker.getProgress(id) + "%");
                if (financeTracker.getProgress(id) >= 100) {
                    System.out.println("Вы достигли своей цели!");
                }
            }

            System.out.println("1. Установить новую цель");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    String name = inputValidator.getStringInput("Введите название цели: ");
                    double target = inputValidator.getDoubleInput("Введите целевую сумму: ");
                    financeTracker.setGoal(id, name, target);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    // STATISTICS
    /**
     * Управление статистикой и аналитикой пользователя.
     * Позволяет просмотреть баланс, доходы и расходы за период, расходы по категориям и сформировать отчёт.
     */
    private static void manageStatistic() { //TODO: add move 2-4 (DONE)
        while (true) {
            System.out.println("1. Показать текущий баланс");
            System.out.println("2. Показать доходы и расходы за период");
            System.out.println("3. Показать расходы по категориям");
            System.out.println("4. Сформировать отчёт");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            String id = financeTracker.getCurrentUser().getId();
            switch (choice) {
                case 1:
                    System.out.println("Текущий баланс: " + financeTracker.getBalance(id));
                    break;
                case 2:
                    LocalDate start = inputValidator.getDateInput("Введите дату начала периода (гггг-мм-дд): ");
                    LocalDate end = inputValidator.getDateInput("Введите дату конца периода (гггг-мм-дд): ");
                    System.out.println("Суммарный доход за период: " + financeTracker.getIncomeOfPeriod(id, start, end));
                    System.out.println("Суммарный расход за период: " + financeTracker.getExpensesOfPeriod(id, start, end));
                    break;
                case 3:
                    start = inputValidator.getDateInput("Введите дату начала периода (гггг-мм-дд): ");
                    end = inputValidator.getDateInput("Введите дату конца периода (гггг-мм-дд): ");
                    System.out.println("Расходы по категориям за период: ");
                    financeTracker.getExpensesByCategory(id, start, end).forEach((category, amount) ->
                            System.out.printf("- %s: %.2f\n", category, amount));
                    break;
                case 4:
                    start = inputValidator.getDateInput("Введите дату начала периода (гггг-мм-дд): ");
                    end = inputValidator.getDateInput("Введите дату конца периода (гггг-мм-дд): ");
                    System.out.println(financeTracker.generateReport(id, start, end));
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    // TODO: add notification

// ADMIN FEATURES
    /**
     * Просмотр списка пользователей (доступно только администратору).
     * Позволяет просматривать транзакции пользователей, удалять или блокировать их.
     */
    private static void viewUsersList() {
        if (!financeTracker.getCurrentUser().getStatus().equals("admin")) {
            System.out.println("У вас нет права доступа!");
            return;
        }
        while (true) {
            financeTracker.viewUsersList();
            System.out.println("1. Просмотреть транзакции пользователя");
            System.out.println("2. Удалить/заблокировать пользователя");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    String id = inputValidator.getStringInput("Введите id пользователя: ");
                    viewTransactions(id);
                    break;
                case 2:
                    deleteOrBanUser();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    /**
     * Удаляет или блокирует пользователя (доступно только администратору).
     * Запрашивает ID пользователя и действие (удаление или блокировка).
     */
    private static void deleteOrBanUser() {
        String id = inputValidator.getStringInput("Введите id пользователя: ");
        if (financeTracker.getUser(id).getStatus().equals("admin")) {
            System.out.println("Вы не можете блокировать или удалять аккаунт администратора!");
            return;
        }
        String newStatus = inputValidator.getStringInput("Выберите, удалить или заблокировать пользователя (введите delete/ban): ");
        if (newStatus.equals("ban")) {
            financeTracker.getUser(id).setStatus("banned");
        } else if (newStatus.equals("delete")) {
            financeTracker.deleteUser(id);
        }
        else {
            System.out.println("Неверный ввод. Попробуйте снова");
        }
    }


}
