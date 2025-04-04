package model;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "password123", "John Doe", "user");
        transaction = new Transaction( 10000.0,"Job", LocalDate.parse("2023-10-01") , "Salary", true);
    }

    @Test
    void testGetId() {
        assertNotNull(user.getId());
        assertEquals(8, user.getId().length());
    }

    @Test
    void testGetEmail() {
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void testSetEmail() {
        user.setEmail("new@example.com");
        assertEquals("new@example.com", user.getEmail());
    }

    @Test
    void testSetEmailThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> user.setEmail(null));
        assertThrows(IllegalArgumentException.class, () -> user.setEmail(""));
    }

    @Test
    void testGetPassword() {
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testSetPassword() {
        user.setPassword("newpassword123");
        assertEquals("newpassword123", user.getPassword());
    }

    @Test
    void testSetPasswordThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> user.setPassword(null));
        assertThrows(IllegalArgumentException.class, () -> user.setPassword(""));
    }

    @Test
    void testGetName() {
        assertEquals("John Doe", user.getName());
    }

    @Test
    void testSetName() {
        user.setName("Jane Doe");
        assertEquals("Jane Doe", user.getName());
    }

    @Test
    void testSetNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> user.setName(null));
        assertThrows(IllegalArgumentException.class, () -> user.setName(""));
    }

    @Test
    void testGetStatus() {
        assertEquals("user", user.getRole());
    }

    @Test
    void testSetStatus() {
        user.setStatus("banned");
        assertEquals("banned", user.getStatus());
    }

    @Test
    void testSetStatusThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> user.setStatus(null));
        assertThrows(IllegalArgumentException.class, () -> user.setStatus(""));
    }

    @Test
    void testAddTransaction() {
        user.addTransaction(transaction);
        assertTrue(user.getTransactions().containsKey(transaction.getId()));
        assertEquals(transaction, user.getTransaction(transaction.getId()));
    }

    @Test
    void testAddTransactionThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> user.addTransaction(null));
    }

    @Test
    void testRemoveTransaction() {
        user.addTransaction(transaction);
        user.removeTransaction(transaction.getId());
        assertFalse(user.getTransactions().containsKey(transaction.getId()));
    }
    

    @Test
    void testGetTransactions() {
        user.addTransaction(transaction);
        Map<String, Transaction> transactions = user.getTransactions();
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertTrue(transactions.containsValue(transaction));
    }

    @Test
    void testGetTransaction() {
        user.addTransaction(transaction);
        Transaction retrievedTransaction = user.getTransaction(transaction.getId());
        assertEquals(transaction, retrievedTransaction);
    }

    @Test
    void testGetTransactionReturnsNull() {
        assertNull(user.getTransaction("nonexistent-id"));
    }
}