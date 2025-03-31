package com.financetracker.model;
/**
 * Класс, представляющий запись о бюджете за определённый месяц.
 */
public class BudgetRecord {
    private String month;
    private double budget;
    private double express;

    /**
     * Создаёт новую запись о бюджете.
     *
     * @param month  Месяц и год в формате "yyyy-MM".
     * @param budget Установленный бюджет.
     */
    public BudgetRecord(String month, double budget) {
        this.month = month;
        this.budget = budget;
        this.express = 0;

    }

    /**
     * Добавляет расходы
     * @param express - новый расход
     */

    public void addExpress(double express) {
        this.express += express;
    }

    /**
     * Возвращает расходы за установленный месяц.
     *
     * @return Расходы.
     */
    public double getExpress() {
        return express;
    }

    /**
     * Возвращает месяц и год.
     *
     * @return Месяц и год в формате "yyyy-MM".
     */
    public String getMonth() {
        return month;
    }

    /**
     * Возвращает установленный бюджет.
     *
     * @return Бюджет.
     */
    public double getBudget() {
        return budget;
    }
}
