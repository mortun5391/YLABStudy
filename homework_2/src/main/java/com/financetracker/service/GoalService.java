package com.financetracker.service;

import com.financetracker.model.Goal;
import com.financetracker.repository.GoalRepository;

import java.util.Optional;

public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    /**
     * Устанавливает финансовую цель для пользователя.
     *
     * @param userId      ID пользователя.
     * @param name        Название цели.
     * @param target      Целевая сумма.
     * @throws IllegalArgumentException если название цели или целевая сумма некорректны.
     */
    public void setGoal(long userId, String name, double target) {
        if (name == null || name.trim().isEmpty() || target < 0) {
            throw new IllegalArgumentException("Goal name cannot be null or empty, and target must be non-negative");
        }
        Goal goal = new Goal(userId, target, name);
        goalRepository.saveGoal(goal);
    }

    /**
     * Добавляет сумму к текущему прогрессу финансовой цели пользователя.
     *
     * @param userId ID пользователя.
     * @param amount Сумма для добавления.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public void addAmount(long userId, double amount) {
        Optional<Goal> goal = goalRepository.findGoal(userId);
        if (goal.isEmpty()) {
            throw new IllegalArgumentException("Goal for user with id " + userId + " is not set");
        }
        goal.get().addCurrentAmount(amount);
    }

    /**
     * Проверяет, установлена ли финансовая цель для пользователя.
     *
     * @param userId ID пользователя.
     * @return true, если цель установлена; false, если нет.
     */
    public boolean isGoalSet(long userId) {
        return goalRepository.findGoal(userId).isPresent();
    }

    /**
     * Возвращает прогресс достижения финансовой цели пользователя в процентах.
     *
     * @param userId ID пользователя.
     * @return прогресс в процентах.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public int getProgress(long userId) {
        Optional<Goal> goal = goalRepository.findGoal(userId);
        if (goal.isEmpty()) {
            throw new IllegalArgumentException("Goal for user with id " + userId + " is not set");
        }
        return goal.get().getProgress();
    }

    /**
     * Возвращает целевую сумму финансовой цели пользователя.
     *
     * @param userId ID пользователя.
     * @return целевая сумма.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public double getTargetAmount(long userId) {
        Optional<Goal> goal = goalRepository.findGoal(userId);
        if (goal.isEmpty()) {
            throw new IllegalArgumentException("Goal for user with id " + userId + " is not set");
        }
        return goal.get().getTargetAmount();
    }

    /**
     * Возвращает название финансовой цели пользователя.
     *
     * @param userId ID пользователя.
     * @return название цели.
     * @throws IllegalArgumentException если цель для пользователя не установлена.
     */
    public String getGoalName(long userId) {
        Optional<Goal> goal = goalRepository.findGoal(userId);
        if (goal.isEmpty()) {
            throw new IllegalArgumentException("Goal for user with id " + userId + " is not set");
        }
        return goal.get().getGoalName();
    }

    /**
     * Отображает информацию о финансовой цели пользователя.
     *
     * @param userId ID пользователя.
     * @return строка с отчетом о финансовой цели.
     */
    public String viewGoal(long userId) {
        StringBuilder goalReport = new StringBuilder();
        Optional<Goal> goal = goalRepository.findGoal(userId);
        if (goal.isEmpty()) {
            goalReport.append("Цель не установлена\n");
        } else {
            goalReport.append("Цель: ").append(goal.get().getGoalName()).append("\n")
                    .append("Целевая сумма: ").append(goal.get().getTargetAmount()).append("\n")
                    .append("Прогресс по цели: ").append(goal.get().getProgress()).append("%\n");
            if (goal.get().getProgress() >= 100) {
                goalReport.append("Вы достигли своей цели!\n");
            }
        }
        return goalReport.toString();
    }
}