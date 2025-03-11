//import com.financetracker.app.FinanceTrackerApp;
//import com.financetracker.service.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.PrintStream;
//import java.util.Scanner;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//class FinanceTrackerAppTest {
//
//    private FinanceTracker financeTracker;
//    private NotificationService notificationService;
//    private ByteArrayOutputStream outputStream;
//
//    @BeforeEach
//    void setUp() {
//        notificationService = new ConsoleNotificationService();
//        financeTracker = new FinanceTracker(notificationService);
//        outputStream = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(outputStream)); // Перехватываем вывод в консоль
//        FinanceTrackerApp.financeTracker = financeTracker;
//    }
//
//    // Вспомогательный метод для подготовки ввода
//    private void prepareInput(String input) {
//        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
//        Scanner scanner = new Scanner(inputStream);
//        InputProvider inputProvider = new ScannerInputProvider(scanner);
//        InputValidator inputValidator = new InputValidator(inputProvider);
//        FinanceTrackerApp.inputValidator = inputValidator;
//    }
//
//    // Вспомогательный метод для регистрации пользователя
//    private void registerUser(String email, String password, String name) {
//        String input = email + "\n" + password + "\n" + password + "\n" + name + "\n";
//        prepareInput(input);
//        FinanceTrackerApp.registerUser();
//    }
//
//    // Вспомогательный метод для входа пользователя
//    private void loginUser(String email, String password) {
//        String input = email + "\n" + password + "\n";
//        prepareInput(input);
//        FinanceTrackerApp.loginUser();
//    }
//
//    // Вспомогательный метод для выполнения команды меню и выхода
//    private void executeMenuCommand(String command) {
//        String input = command + "\n0\n"; // Выполняем команду и выходим
//        prepareInput(input);
//        FinanceTrackerApp.userMenu();
//    }
//
//    @Test
//    void testRegisterUser_Success() {
//        String input = "test@example.com\npassword123\nTest User";
//        prepareInput(input);
//        FinanceTrackerApp.registerUser();
//        assertTrue(outputStream.toString().contains("Регистрация прошла успешно"));
//    }
//
//    @Test
//    void testRegisterUser_DuplicateEmail() {
//        String input = "test@example.com\npassword123\nTest User";
//        prepareInput(input);
//        FinanceTrackerApp.registerUser();
//        prepareInput(input);
//        FinanceTrackerApp.registerUser();
//        assertTrue(outputStream.toString().contains("Пользователь с таким email уже существует"));
//    }
//
//    @Test
//    void testLoginUser_Success() {
//        String input = "test@example.com\npassword123\nTest User";
//        prepareInput(input);
//        FinanceTrackerApp.registerUser();
//        prepareInput("test@example.com\npassword123\n0");
//        FinanceTrackerApp.loginUser();
//        assertTrue(outputStream.toString().contains("Вход выполнен успешно"));
//        assertNotNull(FinanceTrackerApp.financeTracker.getCurrentUser(), "Пользователь не вошел в систему");
//    }
//
//    @Test
//    void testLoginUser_Failure() {
//        registerUser("test@example.com", "password123", "Test User"); // Регистрация
//        loginUser("test@example.com", "wrongpassword"); // Вход с неверным паролем
//        assertTrue(outputStream.toString().contains("Неверный email или пароль"));
//    }
//    @Test
//    void testAddTransaction() {
//        String input = "test@example.com\npassword123\nTest User\n" +
//                        "test@example.com\npassword123\n"+
//
//                        "1\n100,0\nFood\n2023-10-01\nLunch\nfalse\n0";
//        prepareInput(input);
//        // Вызов метода управления транзакциями
//        FinanceTrackerApp.main(new String[]{});
//
//        // Проверка вывода
//        assertTrue(outputStream.toString().contains("Транзакция добавлена"));
//    }
//
//    @Test
//    void testViewProfile() {
//        registerUser("test@example.com", "password123", "Test User"); // Регистрация
//        loginUser("test@example.com", "password123"); // Вход
//
//        // Подготовка ввода для просмотра профиля
//        String input = "5\n0\n";
//        prepareInput(input);
//
//        // Вызов метода меню пользователя
//        FinanceTrackerApp.userMenu();
//
//        // Проверка вывода
//        assertTrue(outputStream.toString().contains("Статус: user"));
//    }
//
//    @Test
//    void testManageBudget() {
//        registerUser("test@example.com", "password123", "Test User"); // Регистрация
//        loginUser("test@example.com", "password123"); // Вход
//
//        // Подготовка ввода для управления бюджетом
//        String input = "2\n1\n2023-10\n1000.0\n0\n";
//        prepareInput(input);
//
//        // Вызов метода управления бюджетом
//        FinanceTrackerApp.manageBudget();
//
//        // Проверка вывода
//        assertTrue(outputStream.toString().contains("Месячный бюджет установлен!"));
//    }
//
//    @Test
//    void testManageGoals() {
//        registerUser("test@example.com", "password123", "Test User"); // Регистрация
//        loginUser("test@example.com", "password123"); // Вход
//
//        // Подготовка ввода для управления целями
//        String input = "3\n1\nНовая цель\n5000.0\n0\n";
//        prepareInput(input);
//
//        // Вызов метода управления целями
//        FinanceTrackerApp.manageGoals();
//
//        // Проверка вывода
//        assertTrue(outputStream.toString().contains("Цель установлена"));
//    }
//
//    @Test
//    void testManageStatistic() {
//        registerUser("test@example.com", "password123", "Test User"); // Регистрация
//        loginUser("test@example.com", "password123"); // Вход
//
//        // Подготовка ввода для управления статистикой
//        String input = "4\n1\n0\n";
//        prepareInput(input);
//
//        // Вызов метода управления статистикой
//        FinanceTrackerApp.manageStatistic();
//
//        // Проверка вывода
//        assertTrue(outputStream.toString().contains("Текущий баланс: 0.00"));
//    }
//
//    @Test
//    void testViewUsersList() {
//        registerUser("admin@example.ru", "admin123", "Admin"); // Регистрация администратора
//        loginUser("admin@example.ru", "admin123"); // Вход администратора
//
//        // Подготовка ввода для просмотра списка пользователей
//        String input = "6\n0\n";
//        prepareInput(input);
//
//        // Вызов метода меню пользователя
//        FinanceTrackerApp.userMenu();
//
//        // Проверка вывода
//        assertTrue(outputStream.toString().contains("Список пользователей:"));
//    }
//}