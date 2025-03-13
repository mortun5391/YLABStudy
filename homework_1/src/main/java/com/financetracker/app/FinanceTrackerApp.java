package com.financetracker.app;

import com.financetracker.repository.BudgetRepository;
import com.financetracker.repository.GoalRepository;
import com.financetracker.repository.UserRepository;
import com.financetracker.service.*;
import com.financetracker.utils.InputValidator;
import com.financetracker.utils.ScannerInputProvider;

import java.time.LocalDate;
import java.util.*;

/**
 * Класс FinanceTrackerApp представляет собой консольное приложение для управления финансами.
 * Позволяет пользователям регистрироваться, входить в систему, управлять транзакциями,
 * бюджетами, финансовыми целями, а также просматривать статистику и аналитику.
 */
public class FinanceTrackerApp {
    private static FinanceTracker financeTracker;
    public static InputValidator inputValidator = new InputValidator( new ScannerInputProvider(new Scanner(System.in)));



    /**
     * Точка входа в приложение.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();

        NotificationService notificationService = new ConsoleNotificationService();
        UserService userService = new UserService(userRepository);
        TransactionService transactionService = new TransactionService(userRepository);
        BudgetService budgetService = new BudgetService(new BudgetRepository());
        GoalService goalService = new GoalService(new GoalRepository());

        financeTracker = new FinanceTracker(userService, transactionService,
                budgetService, goalService, notificationService);
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
                    return;
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
        FinanceTracker.LoginResult result = financeTracker.loginUser(email, password);

        switch (result) {
            case SUCCESS:
                System.out.println("Вход выполнен успешно");
                userMenu();
                break;
            case INVALID_CREDENTIALS:
                System.out.println("Неверный email или пароль");
                break;
            case USER_BANNED:
                System.out.println("Ваш аккаунт заблокирован!");
                break;
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
                    viewTransactions(financeTracker.getId()); // ??
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
        String id = inputValidator.getStringInput("Введите ID транзакции для удаления: ");
        if (financeTracker.removeTransaction(id)) {
            System.out.println("Транзакция удалена");
        }
        else {
            System.out.println("Транзакция не найдена");
        }
    }

    /**
     * Редактирует существующую транзакцию.
     * Позволяет изменить сумму, категорию или описание транзакции.
     */
    private static void editTransaction() {
        String id = inputValidator.getStringInput("Введите ID транзакции для редактирования: ");
        if (financeTracker.isTransactionThere(id)) {
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
                    financeTracker.setTransactionAmount(id, newAmount);
                    break;
                case 2:
                    String newCategory = inputValidator.getStringInput("Введите новую категорию: ");
                    financeTracker.setTransactionCategory(id, newCategory);
                    break;
                case 3:
                    String newDescription = inputValidator.getStringInput("Введите новое описание: ");
                    financeTracker.setTransactionDescription(id, newDescription);
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
            switch (choice) {
                case 1:
                    System.out.println(financeTracker.viewTransactionsNoFilter(id));
                    break;
                case 2:
                    LocalDate dateFilter = inputValidator.getDateInput("Введите дату для фильтрации(гггг-мм-дд): ");
                    System.out.println(financeTracker.viewTransactionsDateFilter(id, dateFilter));
                    break;
                case 3:
                    String categoryFilter = inputValidator.getStringInput("Введите категорию для фильтрации: ");
                    System.out.println(financeTracker.viewTransactionsCategoryFilter(id, categoryFilter));
                    break;
                case 4:
                    boolean isIncomeFilter = inputValidator.getBooleanInput("Доход/расход? (введите true/false): ");
                    System.out.println(financeTracker.viewTransactionsIsIncomeFilter(id, isIncomeFilter));
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    /**
     * Отображает профиль текущего пользователя и предоставляет меню для его изменения или удаления.
     */
    private static void viewProfile() {
        while (true) {
            System.out.println(financeTracker.viewProfile());
            System.out.println("1. Изменить профиль" +
                             "\n2. Удалить профиль" +
                             "\n0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    editProfile();
                    break;
                case 2:
                    deleteProfile();
                    return;
                case 0:
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
        if (!financeTracker.isPasswordEqual(password)) {
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
        if (!financeTracker.isPasswordEqual(oldPassword)) {
            System.out.println("Пароль неверный! Попробуйте снова");
            return;
        }
        String newPassword = inputValidator.getStringInput("Введите новый пароль: ");
        String confirm = inputValidator.getStringInput("Подтвердите пароль: ");
        if (!newPassword.equals(confirm)) {
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
        if (!financeTracker.isPasswordEqual(password)) {
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
        if (!financeTracker.isPasswordEqual(password)) {
            System.out.println("Пароль неверный! Попробуйте снова");
            viewProfile();
            return;
        }
        financeTracker.deleteUser(financeTracker.getId());
        System.out.println("Ваш профиль удален!");

    }

    // BUDGET MANAGE
    /**
     * Управление месячным бюджетом пользователя.
     * Позволяет установить бюджет и просмотреть текущие расходы и остаток.
     */
    private static void manageBudget() {
        while (true) {
            System.out.println(financeTracker.viewBudget());
            System.out.println("1. Установить месячный бюджет");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    String month = inputValidator.getStringInput("Введите месяц (гггг-мм) :");
                    double budget = inputValidator.getDoubleInput("Введите сумму бюджета: ");
                    financeTracker.addBudget(month, budget);
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
        while (true) {
            System.out.println(financeTracker.viewGoal());
            System.out.println("1. Установить новую цель");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    String name = inputValidator.getStringInput("Введите название цели: ");
                    double target = inputValidator.getDoubleInput("Введите целевую сумму: ");
                    financeTracker.setGoal(name, target);
                    System.out.println("Цель установлена!");
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
            switch (choice) {
                case 1:
                    System.out.println("Текущий баланс: " + financeTracker.getBalance());
                    break;
                case 2:
                    LocalDate start = inputValidator.getDateInput("Введите дату начала периода (гггг-мм-дд): ");
                    LocalDate end = inputValidator.getDateInput("Введите дату конца периода (гггг-мм-дд): ");
                    System.out.println("Суммарный доход за период: " + financeTracker.getIncomeOfPeriod(start, end));
                    System.out.println("Суммарный расход за период: " + financeTracker.getExpensesOfPeriod(start, end));
                    break;
                case 3:
                    start = inputValidator.getDateInput("Введите дату начала периода (гггг-мм-дд): ");
                    end = inputValidator.getDateInput("Введите дату конца периода (гггг-мм-дд): ");
                    System.out.println(financeTracker.getExpensesByCategoryAsString(start, end));
                    break;
                case 4:
                    start = inputValidator.getDateInput("Введите дату начала периода (гггг-мм-дд): ");
                    end = inputValidator.getDateInput("Введите дату конца периода (гггг-мм-дд): ");
                    System.out.println(financeTracker.generateReport(start, end));
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }


// ADMIN FEATURES
    /**
     * Просмотр списка пользователей (доступно только администратору).
     * Позволяет просматривать транзакции пользователей, удалять или блокировать их.
     */
    private static void viewUsersList() {
        if (!financeTracker.hasAccess(financeTracker.getId())) {
            System.out.println("У вас нет права доступа!");
            return;
        }
        while (true) {
            financeTracker.viewUsersList();
            System.out.println("1. Просмотреть транзакции пользователя");
            System.out.println("2. Заблокировать пользователя");
            System.out.println("3. Удалить пользователя");
            System.out.println("0. Выход");
            int choice = inputValidator.getIntInput("Выберите действие: ");
            switch (choice) {
                case 1:
                    String id = inputValidator.getStringInput("Введите id пользователя: ");
                    if (financeTracker.isUserExist(id)) viewTransactions(id);
                    else System.out.println("Пользователь с таким id не найден!");

                    break;
                case 2:
                    id = inputValidator.getStringInput("Введите id пользователя: ");
                    if (financeTracker.isUserExist(id)) {
                        if (!financeTracker.hasAccess(id)) financeTracker.banUser(id);
                        else System.out.println("Вы не можете заблокировать аккаунт администратора!");
                    }
                    else System.out.println("Пользователь с таким id не найден!");
                    break;
                case 3:
                    id = inputValidator.getStringInput("Введите id пользователя: ");
                    if (financeTracker.isUserExist(id)) {
                        if (!financeTracker.hasAccess(id)) financeTracker.deleteUser(id);
                        else System.out.println("Вы не можете удалить аккаунт администратора!");
                    }
                    else System.out.println("Пользователь с таким id не найден!");
                case 0:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

}
