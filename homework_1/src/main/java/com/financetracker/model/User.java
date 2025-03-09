package com.financetracker.model;

import java.util.*;

public class User {
    private String id;
    private String email;
    private String password;
    private String name;
    private String status; // admin or user
    private HashMap<String,Transaction> transactions;


    public User(String email, String password, String name, String status) {
        this.id = UUID.randomUUID().toString().substring(0,8);
        this.email = email;
        this.password = password;
        this.name = name;
        this.status = status;
        this.transactions = new HashMap<>();
    }

    public String getId() {
        return id;
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

    public Transaction getTransaction(String id) {
        return transactions.get(id);
    }

    public Map<String,Transaction> getTransactions() {
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
        transactions.put(transaction.getId(), transaction);
    }

    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
