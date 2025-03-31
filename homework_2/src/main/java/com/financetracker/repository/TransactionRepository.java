package com.financetracker.repository;

import com.financetracker.model.Transaction;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionRepository {

    private final DataSource dataSource;

    public TransactionRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Добавляет транзакцию в базу данных.
     */
    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO finance_schema.transactions (user_id, amount, category, date, description, is_income) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, transaction.getUserId());
            stmt.setDouble(2, transaction.getAmount());
            stmt.setString(3, transaction.getCategory());
            stmt.setDate(4, Date.valueOf(transaction.getDate()));
            stmt.setString(5, transaction.getDescription());
            stmt.setBoolean(6, transaction.isIncome());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    transaction.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add transaction", e);
        }
    }

    /**
     * Возвращает транзакцию по её идентификатору.
     */
    public Optional<Transaction> findTransactionById(long transactionId) {
        String sql = "SELECT * FROM finance_schema.transactions WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, transactionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transaction", e);
        }
        return Optional.empty();
    }

    /**
     * Возвращает транзакцию по её идентификатору.
     */
    public Optional<Transaction> findTransactionByUserAndTransactionId(long userId, long transactionId) {
        String sql = "SELECT * FROM finance_schema.transactions WHERE id = ? AND user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, transactionId);
            stmt.setLong(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find transaction", e);
        }
        return Optional.empty();
    }

    /**
     * Возвращает все транзакции пользователя.
     */
    public List<Transaction> findTransactionsByUserId(long userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM finance_schema.transactions WHERE user_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapTransaction(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get transactions", e);
        }
        return transactions;
    }

    /**
     * Удаляет транзакцию по её идентификатору.
     */
    public boolean deleteTransaction(long transactionId) {
        String sql = "DELETE FROM finance_schema.transactions WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, transactionId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete transaction", e);
        }
    }

    /**
     * Обновляет существующую транзакцию в базе данных.
     */
    public void updateTransaction(Transaction transaction) {
        String sql = "UPDATE finance_schema.transactions SET amount = ?, category = ?, date = ?, description = ?, is_income = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, transaction.getAmount());
            stmt.setString(2, transaction.getCategory());
            stmt.setDate(3, Date.valueOf(transaction.getDate()));
            stmt.setString(4, transaction.getDescription());
            stmt.setBoolean(5, transaction.isIncome());
            stmt.setLong(6, transaction.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update transaction", e);
        }
    }

    /**
     * Маппинг ResultSet в объект Transaction.
     */
    private Transaction mapTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction(
                rs.getLong("user_id"),
                rs.getDouble("amount"),
                rs.getString("category"),
                rs.getDate("date").toLocalDate(),
                rs.getString("description"),
                rs.getBoolean("is_income")
        );
        transaction.setId(rs.getLong("id"));
        return transaction;
    }
}