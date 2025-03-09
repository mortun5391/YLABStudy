package com.financetracker.service;

/**
 * Класс ConsoleNotificationService реализует интерфейс NotificationService.
 * Отправляет уведомления через консоль (вывод в стандартный поток вывода).
 */
public class ConsoleNotificationService implements NotificationService {
    /**
     * Отправляет уведомление указанному получателю.
     *
     * @param recipient получатель уведомления.
     * @param message текст уведомления.
     */
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("Уведомление для " + recipient + ": " + message);
    }
}