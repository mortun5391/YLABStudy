package model;

import com.financetracker.model.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    void testConstructor() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);

        assertEquals(1L, transaction.getUserId());
        assertEquals(1000.0, transaction.getAmount(), 0.001);
        assertEquals("Food", transaction.getCategory());
        assertEquals(LocalDate.of(2023, 10, 1), transaction.getDate());
        assertEquals("Lunch", transaction.getDescription());
        assertFalse(transaction.isIncome());
    }

    @Test
    void testSetAmount() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transaction.setAmount(2000.0);
        assertEquals(2000.0, transaction.getAmount(), 0.001);
    }

    @Test
    void testSetAmountThrowsException() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        assertThrows(IllegalArgumentException.class, () -> transaction.setAmount(-100.0));
    }

    @Test
    void testSetCategory() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transaction.setCategory("Transport");
        assertEquals("Transport", transaction.getCategory());
    }

    @Test
    void testSetCategoryThrowsException() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        assertThrows(IllegalArgumentException.class, () -> transaction.setCategory(null));
        assertThrows(IllegalArgumentException.class, () -> transaction.setCategory(""));
    }

    @Test
    void testSetDate() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transaction.setDate(LocalDate.of(2023, 11, 1));
        assertEquals(LocalDate.of(2023, 11, 1), transaction.getDate());
    }

    @Test
    void testSetDescription() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transaction.setDescription("Dinner");
        assertEquals("Dinner", transaction.getDescription());
    }

    @Test
    void testSetIncome() {
        Transaction transaction = new Transaction(1L, 1000.0, "Food", LocalDate.of(2023, 10, 1), "Lunch", false);
        transaction.setIncome(true);
        assertTrue(transaction.isIncome());
    }
}