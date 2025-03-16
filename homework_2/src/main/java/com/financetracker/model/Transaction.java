package com.financetracker.model;

import java.time.LocalDate;

/**
 * Класс, представляющий финансовую транзакцию (доход или расход).
 */
public class Transaction {
    private long id; // ID будет генерироваться базой данных
    private long userId; // ID пользователя, связанного с транзакцией
    private double amount;
    private String category;
    private LocalDate date;
    private String description;
    private boolean isIncome;

    /**
     * Создаёт новую транзакцию.
     *
     * @param userId      ID пользователя.
     * @param amount      Сумма транзакции.
     * @param category    Категория транзакции.
     * @param date        Дата транзакции.
     * @param description Описание транзакции.
     * @param isIncome    Тип транзакции (true — доход, false — расход).
     */
    public Transaction(long userId, double amount, String category, LocalDate date,
                       String description, boolean isIncome) {
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.isIncome = isIncome;
    }

    // Геттеры и сеттеры
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIncome() {
        return isIncome;
    }

    public void setIncome(boolean income) {
        isIncome = income;
    }
}