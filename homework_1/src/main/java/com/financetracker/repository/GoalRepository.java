package com.financetracker.repository;

import com.financetracker.model.BudgetRecord;
import com.financetracker.model.Goal;
import com.financetracker.model.User;

import java.util.HashMap;
import java.util.Map;

public class GoalRepository {
    /**
     * Карта для хранения финансовых целей пользователей.
     * Ключ: идентификатор цели (String).
     * Значение: объект Goal.
     */
    private Map<String, Goal> goals = new HashMap<>();

    public void saveGoal(String id, Goal goal) {
        goals.put(id, goal);
    }

    public Goal findGoal(String id) {
        return goals.get(id);
    }

    public void deleteGoal(String id) {
        goals.remove(id);
    }
}