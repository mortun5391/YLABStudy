package com.financetracker.app;

import com.financetracker.model.Transaction;
import com.financetracker.service.FinanceTracker;

import java.util.*;
import java.util.stream.Collectors;

public class FinanceTrackerApp {
    private static FinanceTracker financeTracker = new FinanceTracker();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("1. Регистрация");
            System.out.println("2. Вход");
            System.out.println("3. Выход");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    registerUser();
                    break;
                case 2:
                    loginUser();
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    private static void registerUser() {
        System.out.println("Введите email: ");
        String email = scanner.nextLine();
        String password;
        while (true) {
            System.out.println("Введите пароль: ");
            password = scanner.nextLine();
            System.out.println("Подтвердите пароль: ");
            String confirm = scanner.nextLine();
            if (password.equals(confirm)) {
                break;
            }
            System.out.println("Пароли не совпадают! Попробуйте еще раз");
        }
        System.out.println("Введите имя: ");
        String name = scanner.nextLine();
        if (financeTracker.registerUser(email, password, name, "user")) {
            System.out.println("Регистрация прошла успешно");
        }
        else {
            System.out.println("Пользователь с таким email уже существует");
        }
    }

    private static void loginUser() {
        System.out.println("Введите email: ");
        String email = scanner.nextLine();
        System.out.println("Введите пароль: ");
        String password = scanner.nextLine();

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

    private static void userMenu(){
        while (true) {
            System.out.println("1. Добавить транзакцию");
            System.out.println("2. Удалить транзакцию");
            System.out.println("3. Редактировать транзакцию");
            System.out.println("4. Просмотреть транзакции");
            System.out.println("5. Просмотреть профиль");
            System.out.println("6. Просмотреть список пользователей");
            System.out.println("7. Выход");
            System.out.println("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

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
                case 5:
                    viewProfile();
                    return;
                case 6:
                    viewUsersList();
                    break;
                case 7:
                    financeTracker.logoutUser();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }
    private static void addTransaction() {
        System.out.println("Введите ID транзакции: ");
        String id = scanner.nextLine();
        System.out.println("Введите сумму: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Введите категорию: ");
        String category = scanner.nextLine();
        System.out.println("Введите дату: ");
        String date = scanner.nextLine();
        System.out.println("Введите описание: ");
        String description = scanner.nextLine();
        System.out.println("Это доход? (true/false): ");
        boolean isIncome = scanner.nextBoolean();
        scanner.nextLine();

        financeTracker.addTransaction(id, amount, category, date, description, isIncome);
        System.out.println("Транзакция добавлена");
    }

    private static void removeTransaction() {
        System.out.println("Введите ID транзакции для удаления");
        String id = scanner.nextLine();

        financeTracker.removeTransaction(id);
        System.out.println("Транзакция удалена");
    }

    private static void editTransaction() {
        System.out.println("Введите ID транзакции для редактирования");
        String id = scanner.nextLine();
        Transaction transaction = financeTracker.getTransaction(id);
        if (transaction == null) {
            System.out.println("Транзакция не найдена");
            return;
        }
        while (true) {
            System.out.println("1. Изменить сумму");
            System.out.println("2. Изменить категорию");
            System.out.println("3. Изменить описание");
            System.out.println("4. Выход");
            System.out.println("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("Введите новую сумму: ");
                    double newAmount = scanner.nextDouble();
                    transaction.setAmount(newAmount);
                    break;
                case 2:
                    System.out.println("Введите новую категорию: ");
                    String newCategory = scanner.nextLine();
                    transaction.setCategory(newCategory);
                    break;
                case 3:
                    System.out.println("Введите новое описание: ");
                    String newDescription = scanner.nextLine();
                    transaction.setDescription(newDescription);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    private static void viewTransactions(String id) {

        while (true) {
            System.out.println("Выберите тип фильтра: ");
            System.out.println("1. Без фильтра");
            System.out.println("2. По дате");
            System.out.println("3. По категории");
            System.out.println("4. По доходу (доход/расход)");
            System.out.println("0. Выход");
            int choice = scanner.nextInt();
            scanner.nextLine();
            Map<String, Transaction> transactions = financeTracker.getTransactions(id);
            List<Transaction> filteredTransactions;
            switch (choice) {
                case 1:
                    filteredTransactions = transactions.values().stream().toList();
                    break;
                case 2:
                    System.out.println("Введите дату для фильтрации: ");
                    String dateFilter = scanner.nextLine().trim();
                    filteredTransactions = transactions.values().stream()
                            .filter(transaction -> transaction.getDate().equals(dateFilter))
                            .collect(Collectors.toList());
                    break;
                case 3:
                    System.out.println("Введите категорию для фильтрации: ");
                    String categoryFilter = scanner.nextLine().trim();
                    filteredTransactions = transactions.values().stream()
                            .filter(transaction -> transaction.getCategory().equals(categoryFilter))
                            .collect(Collectors.toList());
                    break;
                case 4:
                    System.out.println("Доход/расход? (введите true/false): ");
                    boolean isIncomeFilter = scanner.nextBoolean();
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
                            ", Дата: " + transaction.getDate() +
                            ", Описание: " + transaction.getDescription() +
                            ", Тип: " + (transaction.isIncome() ? "Доход" : "Расход"));
                }
            }
        }
    }

    private static void viewProfile() {
        financeTracker.viewProfile();
        while (true) {
            System.out.println("1. Изменить профиль" +
                             "\n2. Удалить профиль" +
                             "\n3. Выход" +
                             "\nВыберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
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
    private static void editProfile() {
        while (true) {
            System.out.println("1. Изменить email");
            System.out.println("2. Изменить пароль");
            System.out.println("3. Изменить имя");
            System.out.println("4. Выход");
            System.out.println("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
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
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    private static void changeEmail() {
        System.out.println("Введите новый email: ");
        String email = scanner.nextLine();
        System.out.println("Подтвердите изменение (введите пароль): ");
        String password = scanner.nextLine();
        if (!password.equals(financeTracker.getCurrentUser().getPassword())) {
            System.out.println("Пароль неверный! Попробуйте снова");
            editProfile();
        }
        financeTracker.changeEmail(email);
        System.out.println("Изменения сохранены. Ваш новый email: " + email);
    }

    private static void changePassword() {
        System.out.println("Введите старый пароль: ");
        String oldPassword = scanner.nextLine();
        if (!oldPassword.equals(financeTracker.getCurrentUser().getPassword())) {
            System.out.println("Пароль неверный! Попробуйте снова");
            editProfile();
        }
        System.out.println("Введите новый пароль: ");
        String newPassword = scanner.nextLine();
        System.out.println("Подтвердите пароль: ");
        String confirm = scanner.nextLine();
        if (newPassword.equals(confirm)) {
            System.out.println("Пароли не совпадают! Попробуйте снова");
            editProfile();
        }
        financeTracker.changePassword(newPassword);
        System.out.println("Изменения сохранены");
    }

    private static void changeName() {
        System.out.println("Введите новое имя: ");
        String name = scanner.nextLine();
        System.out.println("Подтвердите изменение (введите пароль): ");
        String password = scanner.nextLine();
        if (!password.equals(financeTracker.getCurrentUser().getPassword())) {
            System.out.println("Пароль неверный! Попробуйте снова");
            changeName();
            return;
        }
        financeTracker.changeName(name);
        System.out.println("Изменения сохранены. Ваше новое имя: " + name);
    }
    private static void deleteProfile() {
        System.out.println("Вы уверены что хотите удалить аккаунт? Для подтверждения введите пароль: ");
        String password = scanner.nextLine();
        if (!password.equals(financeTracker.getCurrentUser().getPassword())) {
            System.out.println("Пароль неверный! Попробуйте снова");
            viewProfile();
            return;
            // TODO: РЕАЛИЗОВАТЬ повторный ввод пароля либо выход в предыдущий шаг(просмотр профиля)
        }
        financeTracker.deleteUser(financeTracker.getCurrentUser().getId());
        System.out.println("Ваш профиль удален!");

    }

// ADMIN FEATURES
    private static void viewUsersList() {
        financeTracker.viewUsersList();
        while (true) {
            System.out.println("1. Просмотреть транзакции пользователя");
            System.out.println("2. Удалить/заблокировать пользователя");
            System.out.println("3. Выход");
            System.out.println("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    System.out.println("Введите id пользователя: ");
                    String id = scanner.nextLine();
                    viewTransactions(id);
                    break;
                case 2:
                    deleteOrBanUser();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

    private static void deleteOrBanUser() {
        System.out.println("Введите id пользователя: ");
        String id = scanner.nextLine();
        System.out.println("Выберите, удалить или заблокировать пользователя (введите delete/ban): ");
        String newStatus = scanner.nextLine();
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
