package com.financetracker.model;

import java.time.LocalDate;

/**
 * Класс, представляющий финансовую транзакцию (доход или расход).
 */
public class Transaction {
    private String id;
    private double amount;
    private String category;
    private LocalDate date;
    private String description;
    private boolean isIncome;

    /**
     * Создаёт новую транзакцию.
     *
     * @param id          Уникальный идентификатор транзакции.
     * @param amount      Сумма транзакции.
     * @param category    Категория транзакции.
     * @param date        Дата транзакции.
     * @param description Описание транзакции.
     * @param isIncome    Тип транзакции (true — доход, false — расход).
     */
    public Transaction(String id, double amount, String category, LocalDate date,
                       String description, boolean isIncome) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.isIncome = isIncome;
    }

    /**
     * Возвращает уникальный идентификатор транзакции.
     *
     * @return Идентификатор.
     */
    public String getId() {
        return id;
    }

    /**
     * Возвращает сумму транзакции.
     *
     * @return Сумма.
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Возвращает категорию транзакции.
     *
     * @return Категория.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Возвращает дату транзакции.
     *
     * @return Дата.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Возвращает описание транзакции.
     *
     * @return Описание.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Проверяет, является ли транзакция доходом.
     *
     * @return true, если это доход, иначе false.
     */
    public boolean isIncome() {
        return isIncome;
    }

    /**
     * Устанавливает сумму транзакции.
     *
     * @param amount сумма транзакции. Должна быть положительным числом.
     * @throws IllegalArgumentException если передано отрицательное значение.
     */
    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        this.amount = amount;
    }

    /**
     * Устанавливает категорию транзакции.
     *
     * @param category категория транзакции. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если передана пустая строка или null.
     */
    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category;
    }

    /**
     * Устанавливает описание транзакции.
     *
     * @param description описание транзакции. Может быть null, если описание отсутствует.
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
