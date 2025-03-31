package com.financetracker.service;

import com.financetracker.model.BudgetRecord;
import com.financetracker.repository.BudgetRepository;

import java.util.Optional;

public class BudgetService {

    private final BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    /**
     * Возвращает месяц, связанный с бюджетом пользователя.
     *
     * @param userId ID пользователя.
     * @return месяц, связанный с бюджетом.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public String getMonth(long userId) {
        Optional<BudgetRecord> budgetRecord = budgetRepository.findBudget(userId);
        if (budgetRecord.isEmpty()) {
            throw new IllegalArgumentException("Budget for user with id " + userId + " is not set");
        }
        return budgetRecord.get().getMonth();
    }

    /**
     * Добавляет бюджет для пользователя на указанный месяц.
     *
     * @param userId ID пользователя.
     * @param month  Месяц в формате "yyyy-MM".
     * @param budget Сумма бюджета.
     * @throws IllegalArgumentException если месяц или сумма бюджета некорректны.
     */
    public void addBudget(long userId, String month, double budget) {
        if (month == null || month.trim().isEmpty() || budget < 0) {
            throw new IllegalArgumentException("Month cannot be null or empty, and budget must be non-negative");
        }
        BudgetRecord budgetRecord = new BudgetRecord(userId, month, budget);
        budgetRepository.saveBudget(budgetRecord);
    }

    /**
     * Проверяет, установлен ли бюджет для пользователя.
     *
     * @param userId ID пользователя.
     * @return true, если бюджет установлен; false, если нет.
     */
    public boolean isBudgetSet(long userId) {
        return budgetRepository.findBudget(userId).isPresent();
    }

    /**
     * Возвращает месячный бюджет пользователя.
     *
     * @param userId ID пользователя.
     * @return сумма месячного бюджета.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getMonthlyBudget(long userId) {
        Optional<BudgetRecord> budgetRecord = budgetRepository.findBudget(userId);
        if (budgetRecord.isEmpty()) {
            throw new IllegalArgumentException("Budget for user with id " + userId + " is not set");
        }
        return budgetRecord.get().getBudget();
    }

    /**
     * Возвращает сумму расходов пользователя за текущий месяц.
     *
     * @param userId ID пользователя.
     * @return сумма расходов.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getMonthlyExpress(long userId) {
        Optional<BudgetRecord> budgetRecord = budgetRepository.findBudget(userId);
        if (budgetRecord.isEmpty()) {
            throw new IllegalArgumentException("Budget for user with id " + userId + " is not set");
        }
        return budgetRecord.get().getExpress();
    }

    /**
     * Добавляет сумму к расходам пользователя за текущий месяц.
     *
     * @param userId ID пользователя.
     * @param express Сумма расходов.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public void addMonthlyExpress(long userId, double express) {
        Optional<BudgetRecord> budgetRecord = budgetRepository.findBudget(userId);
        if (budgetRecord.isEmpty()) {
            throw new IllegalArgumentException("Budget for user with id " + userId + " is not set");
        }
        budgetRecord.get().addExpress(express);
    }

    /**
     * Возвращает оставшуюся сумму бюджета пользователя.
     *
     * @param userId ID пользователя.
     * @return оставшаяся сумма бюджета.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getRemaining(long userId) {
        Optional<BudgetRecord> budgetRecord = budgetRepository.findBudget(userId);
        if (budgetRecord.isEmpty()) {
            throw new IllegalArgumentException("Budget for user with id " + userId + " is not set");
        }
        return budgetRecord.get().getBudget() - budgetRecord.get().getExpress();
    }

    /**
     * Отображает информацию о месячном бюджете пользователя.
     *
     * @param userId ID пользователя.
     * @return строка с отчетом о месячном бюджете.
     */
    public String viewBudget(long userId) {
        StringBuilder budgetReport = new StringBuilder();
        Optional<BudgetRecord> budgetRecord = budgetRepository.findBudget(userId);
        if (budgetRecord.isEmpty()) {
            budgetReport.append("Месячный бюджет не установлен\n");
        } else {
            budgetReport.append("Месячный бюджет: ").append(budgetRecord.get().getBudget()).append("\n")
                    .append("Расходы за месяц: ").append(budgetRecord.get().getExpress()).append("\n")
                    .append("Остаток бюджета: ").append(getRemaining(userId)).append("\n");
            if (getRemaining(userId) < 0) {
                budgetReport.append("Внимание! Вы превысили месячный бюджет на ")
                        .append(Math.abs(getRemaining(userId))).append("\n");
            }
        }
        return budgetReport.toString();
    }
}