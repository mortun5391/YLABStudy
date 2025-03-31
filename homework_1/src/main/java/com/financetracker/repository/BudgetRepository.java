package com.financetracker.repository;

import com.financetracker.model.BudgetRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * Репозиторий для управления бюджетами пользователей.
 * Предоставляет методы для сохранения, поиска и удаления бюджетов.
 */
public class BudgetRepository {
    /**
     * Карта для хранения бюджетов пользователей.
     * Ключ: идентификатор бюджета (String).
     * Значение: объект BudgetRecord.
     */
    private Map<String, BudgetRecord> budgets = new HashMap<>();

    /**
     * Сохраняет бюджет пользователя.
     *
     * @param id           уникальный идентификатор бюджета.
     * @param budgetRecord объект BudgetRecord, содержащий информацию о бюджете.
     */
    public void saveBudget(String id, BudgetRecord budgetRecord) {
        budgets.put(id, budgetRecord);
    }

    /**
     * Возвращает бюджет пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор бюджета.
     * @return объект BudgetRecord, если бюджет найден; null, если бюджет не найден.
     */
    public BudgetRecord findBudget(String id) {
        return budgets.get(id);
    }

    /**
     * Удаляет бюджет пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор бюджета.
     */
    public void deleteBudget(String id) {
        budgets.remove(id);
    }
}