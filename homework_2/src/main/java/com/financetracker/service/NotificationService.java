package com.financetracker.service;

/**
 * Интерфейс NotificationService определяет метод для отправки уведомлений.
 */
public interface NotificationService {
    /**
     * Отправляет уведомление указанному получателю.
     *
     * @param recipient получатель уведомления.
     * @param message текст уведомления.
     */
    void sendNotification(String recipient, String message);
}