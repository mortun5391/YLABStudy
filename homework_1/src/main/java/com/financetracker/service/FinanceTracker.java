package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.util.*;

public class FinanceTracker {
    private Map<String, User> users;
    private User currentUser;

    public FinanceTracker() {
        users = new HashMap<>();
        users.put("admin@example.ru", new User("admin@example.ru","admin123", "Admin", "admin"));
    }

    public boolean registerUser(String email, String password, String name, String status) {
        if (users.containsKey(email)) {
            return false;
        }
        users.put(email, new User(email,password,name, status));
        return true;
    }

    public boolean loginUser(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logoutUser() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addTransaction(String id, double amount, String category, String date,
                               String description, boolean isIncome) {
        if (currentUser != null) {
            currentUser.addTransaction(new Transaction(id, amount, category, date, description, isIncome));
        }
    }

    public void removeTransaction(String id) {
        if (currentUser != null) {
            currentUser.getTransactions().removeIf(transaction -> transaction.getId().equals(id));
        }
    }

    public List<Transaction> getTransactions(){
        if (currentUser != null) {
            return currentUser.getTransactions();
        }
        return Collections.emptyList();
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
}
