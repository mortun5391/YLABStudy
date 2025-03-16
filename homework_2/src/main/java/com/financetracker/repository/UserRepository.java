package com.financetracker.repository;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Класс репозитория для управления пользователями и их данными.
 * Предоставляет методы для сохранения, поиска, удаления пользователей,
 * а также для управления их транзакциями.
 */
public class UserRepository {

    private final DataSource dataSource;

    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Сохраняет пользователя в репозитории.
     *
     * @param user объект User, который нужно сохранить.
     */
    public void saveUser(User user) {
        String sql = "INSERT INTO finance_schema.users (email, password, name, role, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getName());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getStatus());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return объект User, если пользователь найден; null, если пользователь не найден.
     */
    public Optional<User> findUserById(long id) {
        String sql = "SELECT * FROM finance_schema.users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by id", e);
        }
        return Optional.empty();
    }

    /**
     * Возвращает пользователя по его email.
     *
     * @param email email пользователя.
     * @return объект User, если пользователь найден; null, если пользователь не найден.
     */
    public Optional<User> findUserByEmail(String email) {
        String sql = "SELECT * FROM finance_schema.users WHERE email = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapUser(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find user by email", e);
        }
        return Optional.empty();
    }

    /**
     * Удаляет пользователя из репозитория по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно удалить.
     */
    public void deleteUser(long id) {
        String sql = "DELETE FROM finance_schema.users WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    /**
     * Возвращает список всех пользователей в репозитории.
     *
     * @return список объектов User.
     */
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM finance_schema.users";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get users", e);
        }
        return users;
    }

    /**
     * Маппинг ResultSet в объект User.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("name"),
                rs.getString("role")
        );
        user.setId(rs.getLong("id"));
        user.setStatus(rs.getString("status"));
        return user;
    }
}