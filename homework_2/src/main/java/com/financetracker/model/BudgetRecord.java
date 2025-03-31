package com.financetracker.model;

/**
 * Класс, представляющий запись о бюджете за определённый месяц.
 */
public class BudgetRecord {
    private long id; // ID будет генерироваться базой данных
    private long userId; // ID пользователя
    private String month;
    private double budget;
    private double express;

    /**
     * Создаёт новую запись о бюджете.
     *
     * @param userId ID пользователя.
     * @param month  Месяц и год в формате "yyyy-MM".
     * @param budget Установленный бюджет.
     */
    public BudgetRecord(long userId, String month, double budget) {
        this.userId = userId;
        this.month = month;
        this.budget = budget;
        this.express = 0;
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getExpress() {
        return express;
    }

    public void addExpress(double express) {
        this.express += express;
    }
}