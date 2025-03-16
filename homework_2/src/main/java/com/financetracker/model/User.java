package com.financetracker.model;

import java.util.*;

/**
 * Класс, представляющий пользователя приложения.
 */
public class User {
    private long id;
    private String email;
    private String password;
    private String name;
    private String role; // admin or user
    private String status; // active or banned
    private HashMap<Long,Transaction> transactions;

    /**
     * Создаёт нового пользователя.
     *
     *
     * @param email Email пользователя.
     * @param password Пароль пользователя
     * @param name  Имя пользователя.
     * @param role Роль пользователя (admin/user)
     */
    public User(String email, String password, String name, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = "active";
        this.transactions = new HashMap<>();
    }

    /**
     * Возвращает уникальный идентификатор пользователя.
     *
     * @return Идентификатор.
     */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Возвращает email пользователя.
     *
     * @return Email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return Пароль.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return Имя.
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает транзакцию по её идентификатору.
     *
     * @param id уникальный идентификатор транзакции.
     * @return объект Transaction, если транзакция найдена; null, если транзакция отсутствует.
     */
    public Transaction getTransaction(long id) {
        return transactions.get(id);
    }

    /**
     * Возвращает все транзакции пользователя.
     *
     * @return Map<String, Transaction>, где ключом является идентификатор транзакции,
     * а значением — объект Transaction.
     */
    public Map<Long,Transaction> getTransactions() {
        return transactions;
    }

    /**
     * Устанавливает адрес электронной почты пользователя.
     *
     * @param email адрес электронной почты. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если email равен null или пустой строке.
     */
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        this.email = email;
    }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password пароль пользователя. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если пароль равен null или пустой строке.
     */
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        this.password = password;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name имя пользователя. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если имя равно null или пустой строке.
     */
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    /**
     * Добавляет транзакцию в список транзакций пользователя.
     *
     * @param transaction объект Transaction для добавления. Не может быть null.
     * @throws IllegalArgumentException если transaction равен null.
     */
    public void addTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        transactions.put(transaction.getId(), transaction);
    }

    /**
     * Удаляет транзакцию из списка транзакций пользователя.
     *
     * @param id объект Transaction для удаления. Не может быть null.
     * @throws IllegalArgumentException если transaction равен null.
     */
    public void removeTransaction(long id) {
        transactions.remove(id);
    }

    /**
     * Возвращает текущий статус пользователя.
     *
     * @return строка, представляющая статус пользователя.
     */
    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Устанавливает статус пользователя.
     *
     * @param status новый статус пользователя. Не может быть null или пустой строкой.
     * @throws IllegalArgumentException если статус равен null или пустой строке.
     */
    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be null or empty");
        }
        this.status = status;
    }
}
