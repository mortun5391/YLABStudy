package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.util.*;

public class FinanceTracker {
    private Map<String, User> users;
    private Map<String, User> emailToUserMap;
    private User currentUser;

    public FinanceTracker() {
        users = new HashMap<>();
        emailToUserMap = new HashMap<>();
        User admin = new User("admin@example.ru","admin123", "Admin", "admin");
        users.put(admin.getId(), admin);
        emailToUserMap.put(admin.getEmail(), admin);
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

    public void addTransaction(String id, double amount, String category, String date,
                               String description, boolean isIncome) {
        if (currentUser != null) {
            currentUser.addTransaction(new Transaction(id, amount, category, date, description, isIncome));
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
        if (!currentUser.getStatus().equals("admin")) {
            System.out.println("У вас нет права доступа!");
        }
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
}
