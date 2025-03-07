package com.financetracker.app;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.service.FinanceTracker;

import java.util.*;
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
            System.out.println("3. Просмотреть транзакции");
            System.out.println("4. Просмотреть профиль");
            System.out.println("5. Выход");
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
                    viewTransactions();
                    break;
                case 4:
                    viewProfile();
                    break;
                case 5:
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

    private static void viewTransactions() {
        List<Transaction> transactions = financeTracker.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("Список транзакций пуст");
        }
        else {
            for (Transaction transaction : transactions) {
                System.out.println("ID: " + transaction.getId() +
                        ", Сумма: " + transaction.getAmount() +
                        ", Категория: " + transaction.getCategory() +
                        ", Дата: " + transaction.getDate() +
                        ", Описание: " + transaction.getDescription() +
                        ", Тип: " + (transaction.isIncome() ? "Доход" : "Расход"));
            }
        }
    }

    private static void viewProfile() {
        financeTracker.viewProfile();
        while (true) {
            System.out.println("1. Изменить профиль" +
                             "\n2. Выход" +
                             "\nВыберите действие: ");
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    editProfile();
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова");
            }
        }
    }

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
}
