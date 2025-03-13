package com.financetracker.repository;

import com.financetracker.model.Goal;


import java.util.HashMap;
import java.util.Map;

/**
 * Класс репозитория для управления финансовыми целями пользователей.
 * Предоставляет методы для сохранения, поиска и удаления целей.
 */
public class GoalRepository {
    /**
     * Карта для хранения финансовых целей пользователей.
     * Ключ: идентификатор цели (String).
     * Значение: объект Goal.
     */
    private Map<String, Goal> goals = new HashMap<>();

    /**
     * Сохраняет финансовую цель в репозитории.
     *
     * @param id   идентификатор цели, который будет использоваться как ключ для хранения.
     * @param goal объект Goal, который нужно сохранить.
     */
    public void saveGoal(String id, Goal goal) {
        goals.put(id, goal);
    }

    /**
     * Возвращает финансовую цель по её идентификатору.
     *
     * @param id идентификатор цели.
     * @return объект Goal, если цель найдена; null, если цель не найдена.
     */
    public Goal findGoal(String id) {
        return goals.get(id);
    }

    /**
     * Удаляет финансовую цель из репозитория по её идентификатору.
     *
     * @param id идентификатор цели, которую нужно удалить.
     */
    public void deleteGoal(String id) {
        goals.remove(id);
    }
}