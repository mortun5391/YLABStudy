package com.financetracker.model;

/**
 * Класс, представляющий финансовую цель.
 */
public class Goal {
    private long id; // ID будет генерироваться базой данных
    private long userId; // ID пользователя
    private String goalName;
    private double targetAmount;
    private double currentAmount;

    /**
     * Создаёт новую финансовую цель.
     *
     * @param userId      ID пользователя.
     * @param targetAmount Целевая сумма.
     * @param goalName    Название цели.
     */
    public Goal(long userId, double targetAmount, String goalName) {
        this.userId = userId;
        this.targetAmount = targetAmount;
        this.goalName = goalName;
        this.currentAmount = 0;
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

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void addCurrentAmount(double amount) {
        this.currentAmount += amount;
    }

    public int getProgress() {
        return (int) ((currentAmount / targetAmount) * 100);
    }
}