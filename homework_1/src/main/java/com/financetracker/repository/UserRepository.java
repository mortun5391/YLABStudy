package com.financetracker.repository;

import com.financetracker.model.BudgetRecord;
import com.financetracker.model.Goal;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserRepository {

    /**
     * Карта для хранения пользователей по их уникальному идентификатору.
     * Ключ: идентификатор пользователя (String).
     * Значение: объект User.
     */
    private Map<String, User> users = new HashMap<>();

    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    public User findUserById(String id) {
        return users.get(id);
    }

    public User findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public void deleteUser(String id) {
        users.remove(id);
    }

    public List<User> getUsers() {
        return users.values().stream().toList();
    }

    public void addTransaction(String userId, Transaction transaction) {
        if (users.containsKey(userId)) {
            users.get(userId).addTransaction(transaction);
        }
    }

    public Map<String, Transaction> getTransactions(String userId) {
        if (users.containsKey(userId)) {
            return users.get(userId).getTransactions();
        }
        return Collections.emptyMap();
    }

    public Transaction getTransaction(String userId, String transactionId) {
        return users.get(userId).getTransaction(transactionId);
    }

    public void removeTransaction(String userId, String transactionId) {
        if (users.containsKey(userId)) {
            users.get(userId).removeTransaction(transactionId);
        }
    }
}