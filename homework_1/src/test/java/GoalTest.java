
import com.financetracker.model.Goal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GoalTest {

    private Goal goal;

    @BeforeEach
    void setUp() {
        // Создаём объект Goal перед каждым тестом
        goal = new Goal(1000.0, "Накопить на отпуск");
    }

    @Test
    void testConstructor() {
        // Проверяем, что объект создаётся корректно
        assertEquals("Накопить на отпуск", goal.getGoalName());
        assertEquals(1000.0, goal.getTargetAmount(), 0.001);
        assertEquals(0.0, goal.getCurrentAmount(), 0.001); // Текущая сумма должна быть 0 при создании
    }

    @Test
    void testGetGoalName() {
        // Проверяем метод getGoalName
        assertEquals("Накопить на отпуск", goal.getGoalName());
    }

    @Test
    void testGetTargetAmount() {
        // Проверяем метод getTargetAmount
        assertEquals(1000.0, goal.getTargetAmount(), 0.001);
    }

    @Test
    void testGetCurrentAmount() {
        // Проверяем метод getCurrentAmount
        assertEquals(0.0, goal.getCurrentAmount(), 0.001);
    }

    @Test
    void testAddCurrentAmount() {
        // Добавляем сумму и проверяем, что текущая сумма обновляется корректно
        goal.addCurrentAmount(200.0);
        assertEquals(200.0, goal.getCurrentAmount(), 0.001);

        goal.addCurrentAmount(300.0);
        assertEquals(500.0, goal.getCurrentAmount(), 0.001);
    }

    @Test
    void testAddCurrentAmountWithNegativeValue() {
        // Проверяем, что метод addCurrentAmount корректно обрабатывает отрицательные значения
        goal.addCurrentAmount(-100.0);
        assertEquals(-100.0, goal.getCurrentAmount(), 0.001);

        goal.addCurrentAmount(-200.0);
        assertEquals(-300.0, goal.getCurrentAmount(), 0.001);
    }

    @Test
    void testAddCurrentAmountWithZero() {
        // Проверяем, что добавление нуля не изменяет текущую сумму
        goal.addCurrentAmount(0.0);
        assertEquals(0.0, goal.getCurrentAmount(), 0.001);
    }

    @Test
    void testGetProgress() {
        // Проверяем метод getProgress
        goal.addCurrentAmount(250.0);
        assertEquals(25, goal.getProgress()); // 250 / 1000 = 25%

        goal.addCurrentAmount(500.0);
        assertEquals(75, goal.getProgress()); // 750 / 1000 = 75%

        goal.addCurrentAmount(250.0);
        assertEquals(100, goal.getProgress()); // 1000 / 1000 = 100%
    }

    @Test
    void testGetProgressWithZeroTarget() {
        // Проверяем, что метод getProgress корректно обрабатывает случай, когда целевая сумма равна 0
        Goal zeroTargetGoal = new Goal(0.0, "Бесплатная цель");
        assertEquals(0, zeroTargetGoal.getProgress()); // Прогресс должен быть 0, если целевая сумма равна 0
    }

    @Test
    void testGetProgressWithNegativeCurrentAmount() {
        // Проверяем, что метод getProgress корректно обрабатывает отрицательную текущую сумму
        goal.addCurrentAmount(-500.0);
        assertEquals(-50, goal.getProgress()); // -500 / 1000 = -50%
    }
}