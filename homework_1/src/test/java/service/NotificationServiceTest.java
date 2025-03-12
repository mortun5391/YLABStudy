package service;

import com.financetracker.service.NotificationService;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    @Test
    void testNotificationService() {
        // Создаем мок NotificationService
        NotificationService notificationService = mock(NotificationService.class);

        // Вызываем метод sendNotification
        notificationService.sendNotification("user@example.com", "Тестовое уведомление");

        // Проверяем, что метод был вызван с правильными аргументами
        verify(notificationService, times(1)).sendNotification("user@example.com", "Тестовое уведомление");
    }
}
