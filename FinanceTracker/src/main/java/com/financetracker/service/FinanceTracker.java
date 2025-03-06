package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.util.*;

public class FinanceTracker {
    private Map<String, User> users;
    private User currentUser;

    public FinanceTracker() {
        users = new HashMap<>();
    }

    public boolean registerUser(String email, String password, String name) {
        if (users.containsKey(email)) {
            return false;
        }
        users.put(email, new User(email,password,name));
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
}
