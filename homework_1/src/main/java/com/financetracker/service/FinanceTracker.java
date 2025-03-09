package com.financetracker.service;

import com.financetracker.model.BudgetRecord;
import com.financetracker.model.Goal;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class FinanceTracker {
    private Map<String, User> users;
    private Map<String, User> emailToUserMap;
    private Map<String, BudgetRecord> budgets;
    private Map<String, Goal> goals;
    private User currentUser;
    private NotificationService notificationService;


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

    public boolean registerUser(String email, String password, String name, String status) {
        if (emailToUserMap.containsKey(email)) {
            return false;
        }
        User user = new User(email,password,name, status);

        users.put(user.getId(), user);
        emailToUserMap.put(user.getEmail(), user);
        return true;
    }

    public boolean loginUser(String email, String password) {
        User user = emailToUserMap.get(email);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logoutUser() {
        currentUser = null;
    }

    public void deleteUser(String id) {
        if (currentUser.getEmail().equals(id)) {
            logoutUser();
        }

        emailToUserMap.remove(users.get(id).getEmail());
        users.remove(id);
    }


    public User getCurrentUser() {
        return currentUser;
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public void addTransaction(String id, double amount, String category, LocalDate date,
                               String description, boolean isIncome) {
        if (currentUser != null) {
            currentUser.addTransaction(new Transaction(id, amount, category, date, description, isIncome));
            if (isGoalSet(currentUser.getId()) && isIncome) {
                addAmount(currentUser.getId(), amount);
            }
        }
    }

    public void removeTransaction(String id) {
        if (currentUser != null) {
            currentUser.getTransactions().remove(id);
        }
    }



    public Transaction getTransaction(String id){

        return currentUser.getTransaction(id);
    }

    public Map<String,Transaction> getTransactions(String id){
        if (users.containsKey(id)) {
            return users.get(id).getTransactions();
        }
        return Collections.emptyMap();
    }

    public void changeEmail(String email) {
        currentUser.setEmail(email);
    }

    public void changePassword(String password) {
        currentUser.setPassword(password);
    }

    public void changeName(String name) {
        currentUser.setName(name);
    }

    public void viewProfile() {
        if (currentUser != null) {
            System.out.println(
                            "\nСтатус: " + currentUser.getStatus() +
                            "\nИмя: " + currentUser.getName() +
                            "\nEmail: " + currentUser.getEmail() +
                            "\nПароль: ******** ");
        }
    }

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

    public String getMonth(String id) {
        return budgets.get(id).getMonth();
    }

    public void addBudget(String id, String month ,double budget) {
        budgets.put(id, new BudgetRecord(month, budget));
        Map<String, Transaction> transactions = getTransactions(id);
        for (Transaction transaction : transactions.values()) {
            if (!transaction.isIncome() && transaction.getDate().toString().substring(0,7).equals(month)) {
                addMonthlyExpress(id, transaction.getAmount());
            }
        }
    }

    public boolean isBudgetSet(String id) {
        return budgets.containsKey(id);
    }

    public double getMonthlyBudget(String id) {
        return budgets.get(id).getBudget();
    }

    public double getMonthlyExpress(String id) {
        return budgets.get(id).getExpress();
    }

    public void addMonthlyExpress(String id, double express) {
        budgets.get(id).addExpress(express);
    }

    public double getRemaining(String id) {
        return budgets.get(id).getBudget() - budgets.get(id).getExpress();
    }

    public void setGoal(String id, String name, double target) {
        goals.put(id, new Goal(target, name));
    }

    public void addAmount(String id, double amount) {
        goals.get(id).addCurrentAmount(amount);
    }

    public boolean isGoalSet(String id) {
        return goals.containsKey(id);
    }
    public int getProgress(String id) {
        return goals.get(id).getProgress();
    }

    public double getTargetAmount(String id) {
        return goals.get(id).getTargetAmount();
    }

    public String getGoalName(String id) {
        return goals.get(id).getGoalName();
    }


    public double getBalance(String id) {
        return getTransactions(id).values().stream()
                .mapToDouble(transaction -> transaction.isIncome() ? transaction.getAmount() : -transaction.getAmount())
                .sum();

    }

    public double getIncomeOfPeriod(String id, LocalDate start, LocalDate end) {
        return getTransactions(id).values().stream()
                .mapToDouble(transaction -> transaction.isIncome() && transaction.getDate().isAfter(start.minusDays(1))
                                        && transaction.getDate().isBefore(end.plusDays(1)) ? transaction.getAmount() : 0)
                .sum();
    }

    public double getExpensesOfPeriod(String id, LocalDate start, LocalDate end) {
        return getTransactions(id).values().stream()
                .mapToDouble(transaction -> !transaction.isIncome() && transaction.getDate().isAfter(start.minusDays(1))
                        && transaction.getDate().isBefore(end.plusDays(1)) ? transaction.getAmount() : 0)
                .sum();
    }

    public Map<String, Double> getExpensesByCategory(String id, LocalDate start, LocalDate end) {
        List<Transaction> transactions = getTransactions(id).values().stream().toList();

        Map<String, Double> expensesByCategory = new HashMap<>();


        transactions.stream()
                .filter(transaction -> !transaction.isIncome())
                .filter(transaction -> !transaction.getDate().isBefore(start) &&
                        !transaction.getDate().isAfter(end))// Только расходы
                .forEach(transaction -> {
                    expensesByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
                });

        return expensesByCategory;
    }

    public String generateReport(String id, LocalDate start, LocalDate end) {
        double balance = getBalance(id);
        double income = getExpensesOfPeriod(id, start, end);
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
