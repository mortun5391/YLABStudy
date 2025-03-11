import com.financetracker.app.FinanceTrackerApp;
import com.financetracker.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinanceTrackerAppTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        // Перехватываем вывод в консоль
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    // Вспомогательный метод для подготовки ввода
    private void prepareInput(String input) {
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        Scanner scanner = new Scanner(inputStream);
        InputProvider inputProvider = new ScannerInputProvider(scanner);
        InputValidator inputValidator = new InputValidator(inputProvider);
        FinanceTrackerApp.inputValidator = inputValidator;
    }

    @Test
    void testRegisterAndLogin() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Регистрация прошла успешно"));
        assertTrue(output.contains("Вход выполнен успешно"));
    }

    @Test
    void testAddTransaction() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "1\n1\n100\nFood\n2023-10-01\nLunch\nfalse\n0\n" + // Добавление транзакции
                "0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Транзакция добавлена"));
    }

    @Test
    void testViewTransactions() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "1\n1\n100\nFood\n2023-10-01\nLunch\nfalse\n" + // Добавление транзакции
                "4\n1\n0\n" + // Просмотр транзакций
                "0\n0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Список транзакций:"));
        assertTrue(output.contains("Food"));
        assertTrue(output.contains("100"));
    }

    @Test
    void testViewTransactionsDateFilter() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "1\n1\n100\nFood\n2023-10-01\nLunch\nfalse\n" + // Добавление транзакции
                "4\n2\n2023-10-01\n0\n" + // Просмотр транзакций
                "0\n0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Список транзакций:"));
        assertTrue(output.contains("Food"));
        assertTrue(output.contains("100"));
    }

    @Test
    void testViewTransactionsCategoryFilter() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "1\n1\n100\nFood\n2023-10-01\nLunch\nfalse\n" + // Добавление транзакции
                "4\n3\nFood\n0\n" + // Просмотр транзакций
                "0\n0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Список транзакций:"));
        assertTrue(output.contains("Food"));
        assertTrue(output.contains("100"));
    }

    @Test
    void testViewTransactionsIncomeFilter() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "1\n1\n100\nFood\n2023-10-01\nLunch\nfalse\n" + // Добавление транзакции
                "4\n4\ntrue\n0\n" + // Просмотр транзакций
                "0\n0\n0\n"; // Выход
        prepareInput(input);
        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Список транзакций пуст"));
        assertFalse(output.contains("Food"));
        assertFalse(output.contains("100"));

        input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "1\n1\n100\nFood\n2023-10-01\nLunch\nfalse\n" + // Добавление транзакции
                "4\n4\nfalse\n0\n" + // Просмотр транзакций
                "0\n0\n0\n"; // Выход
        prepareInput(input);
        FinanceTrackerApp.main(new String[]{});

        output = outputStream.toString();
        assertTrue(output.contains("Список транзакций:"));
        assertTrue(output.contains("Food"));
        assertTrue(output.contains("100"));
    }

    @Test
    void testManageBudget() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "2\n1\n2023-10\n1000\n0\n" + // Установка бюджета
                "0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Месячный бюджет установлен!"));
    }

    @Test
    void testManageGoals() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "3\n1\nНовая цель\n5000\n0\n" + // Установка цели
                "0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Цель установлена!"));
    }

    @Test
    void testManageStatistic() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "1\n1\n100\nFood\n2023-10-01\nLunch\nfalse\n0\n" + // Добавление транзакции
                "4\n1\n0\n" + // Просмотр баланса
                "0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Текущий баланс: -100.0"));
    }

    @Test
    void testEditProfile() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "5\n1\n" + // Просмотр профиля
                "1\nnewemail@test.com\npassword123\n" +
                "2\npassword123\nnewpassword123\nnewpassword123\n" +
                "3\nNew Name\nnewpassword123\n" +
                "0\n0\n0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Статус: user"));
        assertTrue(output.contains("Email: newemail@test.com"));
        assertTrue(output.contains("Имя: New Name"));
    }

    @Test
    void testViewProfile() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "5\n0\n" + // Просмотр профиля
                "0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Статус: user"));
        assertTrue(output.contains("Email: test@example.com"));
        assertTrue(output.contains("Имя: Test User"));
    }

    @Test
    void testDeleteProfile() {
        String input = "1\ntest@example.com\npassword123\npassword123\nTest User\n" + // Регистрация
                "2\ntest@example.com\npassword123\n" + // Вход
                "5\n2\npassword123\n" + // Удаление профиля
                "0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Ваш профиль удален!"));
    }

    @Test
    void testAdminViewUsersList() {
        String input =
                "2\nadmin@example.ru\nadmin123\n" + // Вход администратора
                "6\n0\n" + // Просмотр списка пользователей
                "0\n0\n"; // Выход
        prepareInput(input);

        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Список пользователей:"));
        assertTrue(output.contains("admin@example.ru"));
    }

    @Test
    void testAdminDeleteOrBanUser() {

        String input =
                "2\nadmin@example.ru\nadmin123\n" + // Вход администратора
                "6\n2\nwrongID\n0\n0\n0\n"; // Выход
        prepareInput(input);
        FinanceTrackerApp.main(new String[]{});

        String output = outputStream.toString();
        assertTrue(output.contains("Пользователя с таким id не существует!"));
    }


}