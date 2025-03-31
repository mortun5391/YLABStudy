package com.financetracker.model;

/**
 * Класс, представляющий финансовую цель.
 */
public class Goal {
    private String goalName;
    private double targetAmount;
    private double currentAmount;

    /**
     * Создаёт новую финансовую цель.
     * @param goalName         Название цели.
     * @param targetAmount Целевая сумма.
     */
    public Goal(double targetAmount, String goalName) {
        this.targetAmount = targetAmount;
        this.goalName = goalName;
        this.currentAmount = 0;
    }

    /**
     * Возвращает название цели.
     *
     * @return Название.
     */
    public String getGoalName() {
        return goalName;
    }

    /**
     * Возвращает целевую сумму.
     *
     * @return Целевая сумма.
     */
    public double getTargetAmount() {
        return targetAmount;
    }

    /**
     * Возвращает текущую сумму.
     *
     * @return Текущая сумма.
     */
    public double getCurrentAmount() {
        return currentAmount;
    }

    /**
     * Добавляет сумму к текущему прогрессу.
     *
     * @param amount Сумма для добавления.
     */
    public void addCurrentAmount(double amount) {
        this.currentAmount += amount;
    }

    /**
     * Возвращает прогресс в процентах.
     *
     * @return Прогресс в процентах.
     */
    public int getProgress() {
        return (int) (currentAmount * 100/targetAmount) ;
    }

}
