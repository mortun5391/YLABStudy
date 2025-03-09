package com.financetracker.model;

import java.time.LocalDate;

public class Transaction {
    private String id;
    private double amount;
    private String category;
    private LocalDate date;
    private String description;
    private boolean isIncome;

    public Transaction(String id, double amount, String category, LocalDate date, String description, boolean isIncome) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.isIncome = isIncome;
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
