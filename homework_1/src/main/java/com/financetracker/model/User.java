package com.financetracker.model;

import java.util.*;

public class User {
    private String email;
    private String password;
    private String name;
    private String status; // admin or user
    private List<Transaction> transactions;


    public User(String email, String password, String name, String status) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = status;
        this.transactions = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public String getStatus() {
        return status;
    }
}
