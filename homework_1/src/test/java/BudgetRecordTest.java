

import com.financetracker.model.BudgetRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BudgetRecordTest {

    private BudgetRecord budgetRecord;

    @BeforeEach
    void setUp() {
        // Создаём объект BudgetRecord перед каждым тестом
        budgetRecord = new BudgetRecord("2023-10", 1000.0);
    }

    @Test
    void testConstructor() {
        // Проверяем, что объект создаётся корректно
        assertEquals("2023-10", budgetRecord.getMonth());
        assertEquals(1000.0, budgetRecord.getBudget());
        assertEquals(0.0, budgetRecord.getExpress(), 0.001); // Расходы должны быть 0 при создании
    }

    @Test
    void testAddExpress() {
        // Добавляем расходы и проверяем, что они корректно суммируются
        budgetRecord.addExpress(200.0);
        assertEquals(200.0, budgetRecord.getExpress(), 0.001);

        budgetRecord.addExpress(300.0);
        assertEquals(500.0, budgetRecord.getExpress(), 0.001);
    }

    @Test
    void testGetExpress() {
        // Проверяем метод getExpress
        budgetRecord.addExpress(150.0);
        assertEquals(150.0, budgetRecord.getExpress(), 0.001);
    }

    @Test
    void testGetMonth() {
        // Проверяем метод getMonth
        assertEquals("2023-10", budgetRecord.getMonth());
    }

    @Test
    void testGetBudget() {
        // Проверяем метод getBudget
        assertEquals(1000.0, budgetRecord.getBudget(), 0.001);
    }

    @Test
    void testAddExpressWithNegativeValue() {
        // Проверяем, что метод addExpress корректно обрабатывает отрицательные значения
        budgetRecord.addExpress(-100.0);
        assertEquals(-100.0, budgetRecord.getExpress(), 0.001);

        budgetRecord.addExpress(-200.0);
        assertEquals(-300.0, budgetRecord.getExpress(), 0.001);
    }

    @Test
    void testAddExpressWithZero() {
        // Проверяем, что добавление нуля не изменяет значение расходов
        budgetRecord.addExpress(0.0);
        assertEquals(0.0, budgetRecord.getExpress(), 0.001);
    }
}