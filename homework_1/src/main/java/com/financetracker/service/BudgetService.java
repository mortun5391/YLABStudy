package com.financetracker.service;

import com.financetracker.model.BudgetRecord;
import com.financetracker.model.Transaction;
import com.financetracker.repository.BudgetRepository;

import java.util.Map;

public class BudgetService {

    private BudgetRepository budgetRepository;

    public BudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }
    /**
     * Возвращает месяц, связанный с бюджетом пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return месяц, связанный с бюджетом.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public String getMonth(String id) {
        if (budgetRepository.findBudget(id) == null) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        return budgetRepository.findBudget(id).getMonth();
    }

    /**
     * Добавляет бюджет для пользователя на указанный месяц.
     * Если у пользователя есть транзакции за этот месяц, они учитываются в расходах.
     *
     * @param id уникальный идентификатор пользователя.
     * @param month месяц для которого устанавливается бюджет.
     * @param budget сумма бюджета.
     * @throws IllegalArgumentException если месяц или сумма бюджета некорректны.
     */
    public void addBudget(String id, String month, double budget) {
        if (month == null || month.trim().isEmpty() || budget < 0) {
            throw new IllegalArgumentException("Month cannot be null or empty, and budget must be non-negative");
        }
        budgetRepository.saveBudget(id, new BudgetRecord(month, budget));
    }

    /**
     * Проверяет, установлен ли бюджет для пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если бюджет установлен; false, если нет.
     */
    public boolean isBudgetSet(String id) {
        return budgetRepository.findBudget(id) != null;
    }

    /**
     * Возвращает месячный бюджет пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return сумма месячного бюджета.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getMonthlyBudget(String id) {
        if (budgetRepository.findBudget(id) == null) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        return budgetRepository.findBudget(id).getBudget();
    }

    /**
     * Возвращает сумму расходов пользователя за текущий месяц.
     *
     * @param id уникальный идентификатор пользователя.
     * @return сумма расходов.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getMonthlyExpress(String id) {
        if (budgetRepository.findBudget(id) == null) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        return budgetRepository.findBudget(id).getExpress();
    }

    /**
     * Добавляет сумму к расходам пользователя за текущий месяц.
     *
     * @param id уникальный идентификатор пользователя.
     * @param express сумма расходов.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public void addMonthlyExpress(String id, double express) {
        if (budgetRepository.findBudget(id) == null) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        budgetRepository.findBudget(id).addExpress(express);
    }

    /**
     * Возвращает оставшуюся сумму бюджета пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return оставшаяся сумма бюджета.
     * @throws IllegalArgumentException если бюджет для пользователя не установлен.
     */
    public double getRemaining(String id) {
        if (budgetRepository.findBudget(id) == null) {
            throw new IllegalArgumentException("Budget for user with id " + id + " is not set");
        }
        return budgetRepository.findBudget(id).getBudget() - budgetRepository.findBudget(id).getExpress();
    }

    /**
     * Отображает информацию о месячном бюджете пользователя.
     * Формирует отчет, содержащий установленный месячный бюджет, расходы за месяц,
     * остаток бюджета и предупреждение, если бюджет превышен.
     *
     * @param id уникальный идентификатор пользователя.
     * @return строка с отчетом о месячном бюджете. Если бюджет не установлен, возвращается сообщение об этом.
     */
    public String viewBudget(String id) {
        StringBuilder budgetReport = new StringBuilder();
        if (!isBudgetSet(id)) {
            budgetReport.append("Месячный бюджет не установлен\n");
        } else {
            budgetReport.append("Месячный бюджет: ").append(getMonthlyBudget(id)).append("\nРасходы за месяц: ")
                    .append(getMonthlyExpress(id)).append("\nОстаток бюджета: ").append(getRemaining(id)).append("\n");
            if (getRemaining(id) < 0) {
                budgetReport.append("Внимание! Вы превысили месячный бюджет на ").append(Math.abs(getRemaining(id))).append("\n");
            }
        }
        return budgetReport.toString();
    }
}
