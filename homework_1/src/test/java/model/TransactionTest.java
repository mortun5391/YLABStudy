package model;

import com.financetracker.model.Transaction;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    @Test
    void testTransactionGettersAndSetters() {
        // Создаем объект Transaction
        Transaction transaction = new Transaction( 1000, "Еда", LocalDate.of(2023, 10, 1), "Обед в кафе", false);

        // Проверяем геттеры
        assertEquals(1000, transaction.getAmount());
        assertEquals("Еда", transaction.getCategory());
        assertEquals(LocalDate.of(2023, 10, 1), transaction.getDate());
        assertEquals("Обед в кафе", transaction.getDescription());
        assertFalse(transaction.isIncome());

        // Проверяем сеттеры
        transaction.setAmount(2000);
        transaction.setCategory("Транспорт");
        transaction.setDescription("Такси");

        assertEquals(2000, transaction.getAmount());
        assertEquals("Транспорт", transaction.getCategory());
        assertEquals("Такси", transaction.getDescription());
    }
}
