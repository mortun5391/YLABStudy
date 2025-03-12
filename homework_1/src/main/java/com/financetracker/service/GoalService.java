package com.financetracker.service;

import com.financetracker.model.Goal;
import com.financetracker.repository.GoalRepository;

public class GoalService {
    private GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    /**
     * Устанавливает финансовую цель для пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @param name название цели.
     * @param target целевая сумма.
     * @throws IllegalArgumentException если название цели или целевая сумма некорректны.
     */
    public void setGoal(String id, String name, double target) {
        if (name == null || name.trim().isEmpty() || target < 0) {
            throw new IllegalArgumentException("Goal name cannot be null or empty, and target must be non-negative");
        }
        goalRepository.saveGoal(id, new Goal(target, name));
    }


    /**
     * Добавляет сумму к текущему прогрессу финансовой цели пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @param amount сумма для добавления.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public void addAmount(String id, double amount) {
        if (goalRepository.findGoal(id) == null) {
            throw new IllegalArgumentException("Goal for user with id " + id + " is not set");
        }
        goalRepository.findGoal(id).addCurrentAmount(amount);
    }

    /**
     * Проверяет, установлена ли финансовая цель для пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return true, если цель установлена; false, если нет.
     */
    public boolean isGoalSet(String id) {
        return goalRepository.findGoal(id) != null;
    }

    /**
     * Возвращает прогресс достижения финансовой цели пользователя в процентах.
     *
     * @param id уникальный идентификатор пользователя.
     * @return прогресс в процентах.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public int getProgress(String id) {
        if (goalRepository.findGoal(id) == null) {
            throw new IllegalArgumentException("Goal for user with id " + id + " is not set");
        }
        return goalRepository.findGoal(id).getProgress();
    }

    /**
     * Возвращает целевую сумму финансовой цели пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return целевая сумма.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public double getTargetAmount(String id) {
        if (goalRepository.findGoal(id) == null) {
            throw new IllegalArgumentException("Goal for user with id " + id + " is not set");
        }
        return goalRepository.findGoal(id).getTargetAmount();
    }

    /**
     * Возвращает название финансовой цели пользователя.
     *
     * @param id уникальный идентификатор пользователя.
     * @return название цели.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public String getGoalName(String id) {
        if (goalRepository.findGoal(id) == null) {
            throw new IllegalArgumentException("Goal for user with id " + id + " is not set");
        }
        return goalRepository.findGoal(id).getGoalName();
    }

    public String viewGoal(String id) {
        StringBuilder goalReport = new StringBuilder();
        if (!isGoalSet(id)) {
            System.out.println("Цель не установлена\n");
        } else {
            goalReport.append("Цель: ").append(getGoalName(id)).append("\n");
            goalReport.append("Целевая сумма: ").append(getTargetAmount(id)).append("\n");
            goalReport.append("Прогресс по цели: ").append(getProgress(id)).append("%\n");
            if (getProgress(id) >= 100) {
                goalReport.append("Вы достигли своей цели!\n");
            }
        }
        return goalReport.toString();
    }
}
