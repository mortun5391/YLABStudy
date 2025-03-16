package com.financetracker.repository;

import com.financetracker.model.BudgetRecord;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

/**
 * Репозиторий для управления бюджетами пользователей.
 */
public class BudgetRepository {

    private final DataSource dataSource;

    public BudgetRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Сохраняет бюджет пользователя.
     *
     * @param budgetRecord объект BudgetRecord, содержащий информацию о бюджете.
     */
    public void saveBudget(BudgetRecord budgetRecord) {
        String sql = "INSERT INTO finance_schema.budgets (user_id, month, budget, express) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, budgetRecord.getUserId());
            stmt.setString(2, budgetRecord.getMonth());
            stmt.setDouble(3, budgetRecord.getBudget());
            stmt.setDouble(4, budgetRecord.getExpress());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    budgetRecord.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save budget", e);
        }
    }

    /**
     * Возвращает бюджет пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор бюджета.
     * @return объект BudgetRecord, если бюджет найден; null, если бюджет не найден.
     */
    public Optional<BudgetRecord> findBudget(long id) {
        String sql = "SELECT * FROM finance_schema.budgets WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapBudget(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find budget", e);
        }
        return Optional.empty();
    }

    /**
     * Удаляет бюджет пользователя по его идентификатору.
     *
     * @param id уникальный идентификатор пользователя.
     */
    public void deleteBudget(long id) {
        String sql = "DELETE FROM finance_schema.budgets WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete budget", e);
        }
    }

    /**
     * Маппинг ResultSet в объект BudgetRecord.
     */
    private BudgetRecord mapBudget(ResultSet rs) throws SQLException {
        BudgetRecord budgetRecord = new BudgetRecord(
                rs.getLong("user_id"),
                rs.getString("month"),
                rs.getDouble("budget")
        );
        budgetRecord.setId(rs.getLong("id"));
        budgetRecord.addExpress(rs.getDouble("express"));
        return budgetRecord;
    }
}