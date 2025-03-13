package com.financetracker.repository;


import com.financetracker.model.Transaction;
import com.financetracker.model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс репозитория для управления пользователями и их данными.
 * Предоставляет методы для сохранения, поиска, удаления пользователей,
 * а также для управления их транзакциями.
 */
public class UserRepository {

    /**
     * Карта для хранения пользователей по их уникальному идентификатору.
     * Ключ: идентификатор пользователя (String).
     * Значение: объект User.
     */
    private Map<String, User> users = new HashMap<>();

    /**
     * Сохраняет пользователя в репозитории.
     *
     * @param user объект User, который нужно сохранить.
     */
    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя.
     * @return объект User, если пользователь найден; null, если пользователь не найден.
     */
    public User findUserById(String id) {
        return users.get(id);
    }

    /**
     * Возвращает пользователя по его email.
     *
     * @param email email пользователя.
     * @return объект User, если пользователь найден; null, если пользователь не найден.
     */
    public User findUserByEmail(String email) {
        return users.values().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    /**
     * Удаляет пользователя из репозитория по его идентификатору.
     *
     * @param id идентификатор пользователя, которого нужно удалить.
     */
    public void deleteUser(String id) {
        users.remove(id);
    }

    /**
     * Возвращает список всех пользователей в репозитории.
     *
     * @return список объектов User.
     */
    public List<User> getUsers() {
        return users.values().stream().toList();
    }

    /**
     * Добавляет транзакцию пользователю по его идентификатору.
     *
     * @param userId      идентификатор пользователя.
     * @param transaction объект Transaction, который нужно добавить.
     */
    public void addTransaction(String userId, Transaction transaction) {
        if (users.containsKey(userId)) {
            users.get(userId).addTransaction(transaction);
        }
    }

    /**
     * Возвращает карту транзакций пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return карта транзакций, если пользователь найден; пустая карта, если пользователь не найден.
     */
    public Map<String, Transaction> getTransactions(String userId) {
        if (users.containsKey(userId)) {
            return users.get(userId).getTransactions();
        }
        return Collections.emptyMap();
    }

    /**
     * Возвращает транзакцию пользователя по её идентификатору.
     *
     * @param userId        идентификатор пользователя.
     * @param transactionId идентификатор транзакции.
     * @return объект Transaction, если транзакция найдена; null, если транзакция не найдена.
     */
    public Transaction getTransaction(String userId, String transactionId) {
        return users.get(userId).getTransaction(transactionId);
    }

    /**
     * Удаляет транзакцию пользователя по её идентификатору.
     *
     * @param userId        идентификатор пользователя.
     * @param transactionId идентификатор транзакции, которую нужно удалить.
     */
    public void removeTransaction(String userId, String transactionId) {
        if (users.containsKey(userId)) {
            users.get(userId).removeTransaction(transactionId);
        }
    }
}