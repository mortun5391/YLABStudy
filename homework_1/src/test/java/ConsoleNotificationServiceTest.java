import com.financetracker.service.ConsoleNotificationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ConsoleNotificationServiceTest {
    @Test
    void testSendNotification() {
        ConsoleNotificationService notificationService = new ConsoleNotificationService();

        // Проверяем, что метод не выбрасывает исключений
        assertDoesNotThrow(() -> notificationService.sendNotification("user@example.com", "Тестовое уведомление"));
    }
}
