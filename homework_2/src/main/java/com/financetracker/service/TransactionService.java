package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.*;

/**
 * Сервис для управления транзакциями пользователей.
 */
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Добавляет транзакцию для указанного пользователя.
     *
     * @param transaction объект Transaction, который нужно сохранить.
     */
    public void addTransaction(Transaction transaction) {
        transactionRepository.addTransaction(transaction);
    }

    /**
     * Возвращает все транзакции пользователя.
     *
     * @param userId ID пользователя.
     * @return Список транзакций.
     */
    public List<Transaction> getTransactions(long userId) {
        return transactionRepository.findTransactionsByUserId(userId);
    }

    /**
     * Возвращает транзакцию по её идентификатору.
     *
     * @param transactionId ID транзакции.
     * @return Объект транзакции или null, если транзакция не найдена.
     */
    public Optional<Transaction> getTransaction(long transactionId) {
        return transactionRepository.findTransactionById(transactionId);
    }

    /**
     * Удаляет транзакцию по её идентификатору.
     *
     * @param transactionId ID транзакции.
     * @return true, если транзакция успешно удалена; false, если транзакция не найдена.
     */
    public boolean removeTransaction(long transactionId) {
        return transactionRepository.deleteTransaction(transactionId);
    }

    /**
     * Проверяет, существует ли транзакция с указанным идентификатором.
     *
     * @param transactionId ID транзакции.
     * @return true, если транзакция существует; false, если нет.
     */
    public boolean isTransactionThere(long userId,long transactionId) {
        return transactionRepository.findTransactionByUserAndTransactionId(userId,transactionId).isPresent();
    }

    /**
     * Устанавливает новую сумму для транзакции.
     *
     * @param transactionId ID транзакции.
     * @param amount        Новая сумма транзакции.
     */
    public void setTransactionAmount(long userId, long transactionId, double amount) {
        Transaction transaction = transactionRepository.findTransactionByUserAndTransactionId(userId,transactionId)
                .orElseThrow(() -> new IllegalStateException("Transaction with id " + transactionId + " not found"));
        transaction.setAmount(amount);
        transactionRepository.updateTransaction(transaction);
    }

    /**
     * Устанавливает новую категорию для транзакции.
     *
     * @param transactionId ID транзакции.
     * @param category      Новая категория транзакции.
     */
    public void setTransactionCategory(long userId,long transactionId, String category) {
        transactionRepository.findTransactionByUserAndTransactionId(userId,transactionId).ifPresent(transaction -> {
            transaction.setCategory(category);
            transactionRepository.updateTransaction(transaction);
        });
    }

    /**
     * Устанавливает новое описание для транзакции.
     *
     * @param transactionId ID транзакции.
     * @param description   Новое описание транзакции.
     */
    public void setTransactionDescription(long userId,long transactionId, String description) {
        transactionRepository.findTransactionByUserAndTransactionId(userId,transactionId).ifPresent(transaction -> {
            transaction.setDescription(description);
            transactionRepository.updateTransaction(transaction);
        });
    }

    /**
     * Возвращает текущий баланс пользователя.
     *
     * @param userId ID пользователя.
     * @return Текущий баланс.
     */
    public double getBalance(long userId) {
        return transactionRepository.findTransactionsByUserId(userId).stream()
                .mapToDouble(transaction -> transaction.isIncome() ? transaction.getAmount() : -transaction.getAmount())
                .sum();
    }

    /**
     * Возвращает сумму доходов пользователя за указанный период.
     *
     * @param userId ID пользователя.
     * @param start  Начальная дата периода.
     * @param end    Конечная дата периода.
     * @return Сумма доходов за период.
     */
    public double getIncomeOfPeriod(long userId, LocalDate start, LocalDate end) {
        return transactionRepository.findTransactionsByUserId(userId).stream()
                .filter(transaction -> transaction.isIncome() &&
                        !transaction.getDate().isBefore(start) &&
                        !transaction.getDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Возвращает сумму расходов пользователя за указанный период.
     *
     * @param userId ID пользователя.
     * @param start  Начальная дата периода.
     * @param end    Конечная дата периода.
     * @return Сумма расходов за период.
     */
    public double getExpensesOfPeriod(long userId, LocalDate start, LocalDate end) {
        return transactionRepository.findTransactionsByUserId(userId).stream()
                .filter(transaction -> !transaction.isIncome() &&
                        !transaction.getDate().isBefore(start) &&
                        !transaction.getDate().isAfter(end))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Возвращает расходы пользователя по категориям за указанный период.
     *
     * @param userId ID пользователя.
     * @param start  Начальная дата периода.
     * @param end    Конечная дата периода.
     * @return Map<String, Double>, где ключ — категория, а значение — сумма расходов.
     */
    public Map<String, Double> getExpensesByCategory(long userId, LocalDate start, LocalDate end) {
        Map<String, Double> expensesByCategory = new HashMap<>();
        transactionRepository.findTransactionsByUserId(userId).stream()
                .filter(transaction -> !transaction.isIncome() &&
                        !transaction.getDate().isBefore(start) &&
                        !transaction.getDate().isAfter(end))
                .forEach(transaction -> {
                    expensesByCategory.merge(transaction.getCategory(), transaction.getAmount(), Double::sum);
                });
        return expensesByCategory;
    }

    /**
     * Рассчитывает сумму расходов пользователя за указанный месяц.
     *
     * @param userId ID пользователя.
     * @param month  Месяц в формате "yyyy-MM".
     * @return Сумма расходов за месяц.
     */
    public double calculateMonthlyExpress(long userId, String month) {
        return transactionRepository.findTransactionsByUserId(userId).stream()
                .filter(transaction -> !transaction.isIncome() &&
                        transaction.getDate().toString().substring(0, 7).equals(month))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Генерирует финансовый отчёт для пользователя за указанный период.
     *
     * @param userId ID пользователя.
     * @param start  Начальная дата периода.
     * @param end    Конечная дата периода.
     * @return Строка, содержащая финансовый отчёт.
     */
    public String generateReport(long userId, LocalDate start, LocalDate end) {
        double balance = getBalance(userId);
        double income = getIncomeOfPeriod(userId, start, end);
        double expense = getExpensesOfPeriod(userId, start, end);
        Map<String, Double> expensesByCategory = getExpensesByCategory(userId, start, end);

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
     * @param userId уникальный идентификатор пользователя.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionNoFilter(long userId) {
        return formatTransactions(transactionRepository.findTransactionsByUserId(userId), "Список транзакций:");
    }

    /**
     * Возвращает список транзакций пользователя, отфильтрованных по дате.
     *
     * @param userId     уникальный идентификатор пользователя.
     * @param dateFilter дата для фильтрации.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsDateFilter(long userId, LocalDate dateFilter) {
        List<Transaction> filteredTransactions = transactionRepository.findTransactionsByUserId(userId).stream()
                .filter(transaction -> transaction.getDate().isEqual(dateFilter))
                .toList();
        return formatTransactions(filteredTransactions, "Список транзакций по дате " + dateFilter + ":");
    }

    /**
     * Возвращает список транзакций пользователя, отфильтрованных по категории.
     *
     * @param userId         уникальный идентификатор пользователя.
     * @param categoryFilter категория для фильтрации.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsCategoryFilter(long userId, String categoryFilter) {
        List<Transaction> filteredTransactions = transactionRepository.findTransactionsByUserId(userId).stream()
                .filter(transaction -> transaction.getCategory().equals(categoryFilter))
                .toList();
        return formatTransactions(filteredTransactions, "Список транзакций по категории " + categoryFilter + ":");
    }

    /**
     * Возвращает список транзакций пользователя, отфильтрованных по типу (доход/расход).
     *
     * @param userId         уникальный идентификатор пользователя.
     * @param isIncomeFilter true для доходов, false для расходов.
     * @return строка, содержащая список транзакций.
     */
    public String viewTransactionsIsIncomeFilter(long userId, boolean isIncomeFilter) {
        List<Transaction> filteredTransactions = transactionRepository.findTransactionsByUserId(userId).stream()
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
                System.out.println(transaction.getId());
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