package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для управления транзакциями пользователей.
 * Предоставляет методы для добавления, удаления, изменения и получения информации о транзакциях.
 */
public class TransactionService {
    private UserRepository userRepository;

    /**
     * Конструктор класса TransactionService.
     *
     * @param userRepository репозиторий пользователей, используемый для доступа к данным транзакций.
     */
    public TransactionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Добавляет транзакцию для указанного пользователя.
     *
     * @param id          уникальный идентификатор пользователя.
     * @param amount      сумма транзакции.
     * @param category    категория транзакции.
     * @param date        дата транзакции.
     * @param description описание транзакции.
     * @param isIncome    true, если транзакция является доходом; false, если расходом.
     */
    public void addTransaction(String id, double amount, String category, LocalDate date,
                               String description, boolean isIncome) {
        userRepository.addTransaction(id, new Transaction(amount, category, date, description, isIncome));
    }

    /**
     * Возвращает все транзакции пользователя.
     *
     * @param userId уникальный идентификатор пользователя.
     * @return Map<String, Transaction>, где ключ — идентификатор транзакции, а значение — объект транзакции.
     */
    public Map<String, Transaction> getTransactions(String userId) {
        return userRepository.getTransactions(userId);
    }

    /**
     * Возвращает транзакцию по её идентификатору.
     *
     * @param userId        уникальный идентификатор пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @return объект транзакции или null, если транзакция не найдена.
     */
    public Transaction getTransaction(String userId, String transactionId) {
        return userRepository.getTransaction(userId, transactionId);
    }

    /**
     * Удаляет транзакцию по её идентификатору.
     *
     * @param userId        уникальный идентификатор пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @return true, если транзакция успешно удалена; false, если транзакция не найдена.
     */
    public boolean removeTransaction(String userId, String transactionId) {
        if (userRepository.getTransaction(userId, transactionId) == null) {
            return false;
        }
        userRepository.removeTransaction(userId, transactionId);
        return true;
    }

    /**
     * Проверяет, существует ли транзакция с указанным идентификатором.
     *
     * @param userId        уникальный идентификатор пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @return true, если транзакция существует; false, если нет.
     */
    public boolean isTransactionThere(String userId, String transactionId) {
        return userRepository.getTransaction(userId, transactionId) != null;
    }

    /**
     * Устанавливает новую сумму для транзакции.
     *
     * @param userId        уникальный идентификатор пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @param amount        новая сумма транзакции.
     */
    public void setTransactionAmount(String userId, String transactionId, double amount) {
        userRepository.getTransaction(userId, transactionId).setAmount(amount);
    }

    /**
     * Устанавливает новую категорию для транзакции.
     *
     * @param userId        уникальный идентификатор пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @param category      новая категория транзакции.
     */
    public void setTransactionCategory(String userId, String transactionId, String category) {
        userRepository.getTransaction(userId, transactionId).setCategory(category);
    }

    /**
     * Устанавливает новое описание для транзакции.
     *
     * @param userId        уникальный идентификатор пользователя.
     * @param transactionId уникальный идентификатор транзакции.
     * @param description   новое описание транзакции.
     */
    public void setTransactionDescription(String userId, String transactionId, String description) {
        userRepository.getTransaction(userId, transactionId).setDescription(description);
    }

    /**
     * Возвращает текущий баланс пользователя.
     * Баланс рассчитывается как сумма всех доходов за вычетом всех расходов.
     *
     * @param id уникальный идентификатор пользователя.
     * @return текущий баланс пользователя.
     */
    public double getBalance(String id) {
        return userRepository.getTransactions(id).values().stream()
                .mapToDouble(transaction -> transaction.isIncome() ? transaction.getAmount() : -transaction.getAmount())
                .sum();
    }

    /**
     * Возвращает сумму доходов пользователя за указанный период.
     *
     * @param id    уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end   конечная дата периода.
     * @return сумма доходов за период.
     */
    public double getIncomeOfPeriod(String id, LocalDate start, LocalDate end) {
        return userRepository.getTransactions(id).values().stream()
                .mapToDouble(transaction -> transaction.isIncome() && transaction.getDate().isAfter(start.minusDays(1))
                        && transaction.getDate().isBefore(end.plusDays(1)) ? transaction.getAmount() : 0)
                .sum();
    }

    /**
     * Возвращает сумму расходов пользователя за указанный период.
     *
     * @param id    уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end   конечная дата периода.
     * @return сумма расходов за период.
     */
    public double getExpensesOfPeriod(String id, LocalDate start, LocalDate end) {
        return userRepository.getTransactions(id).values().stream()
                .mapToDouble(transaction -> !transaction.isIncome() && transaction.getDate().isAfter(start.minusDays(1))
                        && transaction.getDate().isBefore(end.plusDays(1)) ? transaction.getAmount() : 0)
                .sum();
    }

    /**
     * Возвращает расходы пользователя по категориям за указанный период.
     *
     * @param id    уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end   конечная дата периода.
     * @return Map<String, Double>, где ключ — категория, а значение — сумма расходов по этой категории.
     */
    public Map<String, Double> getExpensesByCategory(String id, LocalDate start, LocalDate end) {
        List<Transaction> transactions = userRepository.getTransactions(id).values().stream().toList();

        Map<String, Double> expensesByCategory = new HashMap<>();

        transactions.stream()
                .filter(transaction -> !transaction.isIncome())
                .filter(transaction -> !transaction.getDate().isBefore(start) &&
                        !transaction.getDate().isAfter(end)) // Только расходы
                .forEach(transaction -> {
                    expensesByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
                });

        return expensesByCategory;
    }

    /**
     * Рассчитывает сумму расходов пользователя за указанный месяц.
     *
     * @param id    уникальный идентификатор пользователя.
     * @param month месяц в формате "yyyy-MM".
     * @return сумма расходов за месяц.
     */
    public double calculateMonthlyExpress(String id, String month) {
        return userRepository.getTransactions(id).values().stream()
                .mapToDouble(transaction -> !transaction.isIncome() && transaction.getDate().toString().substring(0, 7).equals(month) ?
                        transaction.getAmount() : 0)
                .sum();
    }

    /**
     * Генерирует финансовый отчёт для пользователя за указанный период.
     * Отчёт включает текущий баланс, доходы, расходы и расходы по категориям.
     *
     * @param id    уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end   конечная дата периода.
     * @return строка, содержащая финансовый отчёт.
     */
    public String generateReport(String id, LocalDate start, LocalDate end) {
        double balance = getBalance(id);
        double income = getIncomeOfPeriod(id, start, end);
        double expense = getExpensesOfPeriod(id, start, end);
        Map<String, Double> expensesByCategory = getExpensesByCategory(id, start, end);

        StringBuilder report = new StringBuilder();
        report.append("=== Финансовый отчёт ===\n");
        report.append(String.format("Текущий баланс: %.2f \n", balance));
        report.append(String.format("Доход за период: %.2f \n", income));
        report.append(String.format("Расход за период: %.2f \n", expense));
        report.append("Расходы по категориям:\n");
        expensesByCategory.forEach((category, amount) ->
                report.append(String.format("- %s: %.2f \n", category, amount)));
        return report.toString();
    }

    /**
     * Возвращает список всех транзакций пользователя без фильтрации.
     *
     * @param id уникальный идентификатор пользователя.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionNoFilter(String id) {
        return formatTransactions(userRepository.getTransactions(id).values().stream().toList(), "Список транзакций:");
    }

    /**
     * Возвращает список транзакций пользователя, отфильтрованных по дате.
     *
     * @param id         уникальный идентификатор пользователя.
     * @param dateFilter дата для фильтрации.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsDateFilter(String id, LocalDate dateFilter) {
        List<Transaction> filteredTransactions = userRepository.getTransactions(id).values().stream()
                .filter(transaction -> transaction.getDate().isEqual(dateFilter))
                .toList();
        return formatTransactions(filteredTransactions, "Список транзакций по дате " + dateFilter + ":");
    }

    /**
     * Возвращает список транзакций пользователя, отфильтрованных по категории.
     *
     * @param id             уникальный идентификатор пользователя.
     * @param categoryFilter категория для фильтрации.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsCategoryFilter(String id, String categoryFilter) {
        List<Transaction> filteredTransactions = userRepository.getTransactions(id).values().stream()
                .filter(transaction -> transaction.getCategory().equals(categoryFilter))
                .toList();
        return formatTransactions(filteredTransactions, "Список транзакций по категории " + categoryFilter + ":");
    }

    /**
     * Возвращает список транзакций пользователя, отфильтрованных по типу (доход/расход).
     *
     * @param id             уникальный идентификатор пользователя.
     * @param isIncomeFilter true для доходов, false для расходов.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsIsIncomeFilter(String id, boolean isIncomeFilter) {
        List<Transaction> filteredTransactions = userRepository.getTransactions(id).values().stream()
                .filter(transaction -> transaction.isIncome() == isIncomeFilter)
                .toList();
        String type = isIncomeFilter ? "Доходы" : "Расходы";
        return formatTransactions(filteredTransactions, "Список транзакций (" + type + "):");
    }

    /**
     * Форматирует список транзакций в строку.
     *
     * @param transactions список транзакций.
     * @param header       заголовок для списка транзакций.
     * @return строка, содержащая отформатированный список транзакций.
     */
    private String formatTransactions(List<Transaction> transactions, String header) {
        StringBuilder transactionList = new StringBuilder();

        if (transactions.isEmpty()) {
            transactionList.append("Список транзакций пуст\n");
        } else {
            transactionList.append(header + "\n");
            for (Transaction transaction : transactions) {
                transactionList.append("ID: " + transaction.getId() +
                        ", Сумма: " + transaction.getAmount() +
                        ", Категория: " + transaction.getCategory() +
                        ", Дата: " + transaction.getDate().toString() +
                        ", Описание: " + transaction.getDescription() +
                        ", Тип: " + (transaction.isIncome() ? "Доход" : "Расход") + "\n");
            }
        }

        return transactionList.toString();
    }
}