package com.financetracker.service;

public class ConsoleNotificationService implements NotificationService{
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("Уведомление для " + recipient + ": " + message);
    }
}
