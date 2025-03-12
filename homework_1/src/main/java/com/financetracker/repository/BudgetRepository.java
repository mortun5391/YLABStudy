package com.financetracker.repository;

import com.financetracker.model.BudgetRecord;
import com.financetracker.model.Goal;
import com.financetracker.model.User;

import java.util.HashMap;
import java.util.Map;

public class BudgetRepository {
    /**
     * Карта для хранения бюджетов пользователей.
     * Ключ: идентификатор бюджета (String).
     * Значение: объект BudgetRecord.
     */
    private Map<String, BudgetRecord> budgets = new HashMap<>();

    public void saveBudget(String id, BudgetRecord budgetRecord) {
        budgets.put(id, budgetRecord);
    }

    public BudgetRecord findBudget(String id) {
        return budgets.get(id);
    }

    public void deleteBudget(String id) {
        budgets.remove(id);
    }

}