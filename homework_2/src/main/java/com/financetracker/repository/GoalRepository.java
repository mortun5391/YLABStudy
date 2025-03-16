package com.financetracker.repository;

import com.financetracker.model.Goal;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

/**
 * Репозиторий для управления финансовыми целями пользователей.
 */
public class GoalRepository {

    private final DataSource dataSource;

    public GoalRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Сохраняет финансовую цель.
     *
     * @param goal объект Goal, который нужно сохранить.
     */
    public void saveGoal(Goal goal) {
        String sql = "INSERT INTO finance_schema.goals (user_id, goal_name, target_amount, current_amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, goal.getUserId());
            stmt.setString(2, goal.getGoalName());
            stmt.setDouble(3, goal.getTargetAmount());
            stmt.setDouble(4, goal.getCurrentAmount());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    goal.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save goal", e);
        }
    }

    /**
     * Возвращает финансовую цель по её идентификатору.
     *
     * @param id уникальный идентификатор цели.
     * @return объект Goal, если цель найдена; null, если цель не найдена.
     */
    public Optional<Goal> findGoal(long id) {
        String sql = "SELECT * FROM finance_schema.goals WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapGoal(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find goal", e);
        }
        return Optional.empty();
    }

    /**
     * Удаляет финансовую цель по её идентификатору.
     *
     * @param id уникальный идентификатор цели.
     */
    public void deleteGoal(long id) {
        String sql = "DELETE FROM finance_schema.goals WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete goal", e);
        }
    }

    /**
     * Маппинг ResultSet в объект Goal.
     */
    private Goal mapGoal(ResultSet rs) throws SQLException {
        Goal goal = new Goal(
                rs.getLong("user_id"),
                rs.getDouble("target_amount"),
                rs.getString("goal_name")
        );
        goal.setId(rs.getLong("id"));
        goal.addCurrentAmount(rs.getDouble("current_amount"));
        return goal;
    }
}