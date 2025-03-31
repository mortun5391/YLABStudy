package model;

import com.financetracker.model.BudgetRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BudgetRecordTest {

    private BudgetRecord budgetRecord;

    @BeforeEach
    void setUp() {
        budgetRecord = new BudgetRecord(1L, "2023-10", 1000.0);
    }

    @Test
    void testConstructor() {
        assertEquals(1L, budgetRecord.getUserId());
        assertEquals("2023-10", budgetRecord.getMonth());
        assertEquals(1000.0, budgetRecord.getBudget(), 0.001);
        assertEquals(0.0, budgetRecord.getExpress(), 0.001);
    }

    @Test
    void testAddExpress() {
        budgetRecord.addExpress(200.0);
        assertEquals(200.0, budgetRecord.getExpress(), 0.001);

        budgetRecord.addExpress(300.0);
        assertEquals(500.0, budgetRecord.getExpress(), 0.001);
    }

    @Test
    void testAddExpressWithNegativeValue() {
        budgetRecord.addExpress(-100.0);
        assertEquals(-100.0, budgetRecord.getExpress(), 0.001);

        budgetRecord.addExpress(-200.0);
        assertEquals(-300.0, budgetRecord.getExpress(), 0.001);
    }

    @Test
    void testAddExpressWithZero() {
        budgetRecord.addExpress(0.0);
        assertEquals(0.0, budgetRecord.getExpress(), 0.001);
    }

    @Test
    void testSetBudget() {
        budgetRecord.setBudget(2000.0);
        assertEquals(2000.0, budgetRecord.getBudget(), 0.001);
    }

    @Test
    void testSetMonth() {
        budgetRecord.setMonth("2023-11");
        assertEquals("2023-11", budgetRecord.getMonth());
    }

    @Test
    void testSetUserId() {
        budgetRecord.setUserId(2L);
        assertEquals(2L, budgetRecord.getUserId());
    }
}