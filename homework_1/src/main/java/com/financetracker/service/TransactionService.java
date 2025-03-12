package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionService {
    private UserRepository userRepository;

    public TransactionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Добавляет транзакцию для текущего пользователя.
     * Если транзакция является доходом и у пользователя установлена финансовая цель,
     * сумма транзакции добавляется к цели.
     *
     * @param amount сумма транзакции.
     * @param category категория транзакции.
     * @param date дата транзакции.
     * @param description описание транзакции.
     * @param isIncome true, если транзакция является доходом; false, если расходом.
     * @throws IllegalStateException если текущий пользователь не аутентифицирован.
     */
    public void addTransaction(String id, double amount, String category, LocalDate date,
                               String description, boolean isIncome) {
        userRepository.addTransaction(id,new Transaction(amount, category, date, description, isIncome));
    }


    public Map<String, Transaction> getTransactions(String userId) {
        return userRepository.getTransactions(userId);
    }

    public Transaction getTransaction(String userId, String transactionId) {
        return userRepository.getTransaction(userId, transactionId);
    }

    public boolean removeTransaction(String userId, String transactionId) {
        if (userRepository.getTransaction(userId, transactionId) == null) {
            return false;
        }
        userRepository.removeTransaction(userId, transactionId);
        return true;
    }

    public boolean isTransactionThere(String userId, String transactionId) {
        return userRepository.getTransaction(userId , transactionId) != null;
    }

    public void setTransactionAmount(String userId, String transactionId, double amount) {
        userRepository.getTransaction(userId, transactionId).setAmount(amount);
    }

    public void setTransactionCategory(String userId, String transactionId, String category) {
        userRepository.getTransaction(userId, transactionId).setCategory(category);
    }
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
     * @param id уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end конечная дата периода.
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
     * @param id уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end конечная дата периода.
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
     * @param id уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end конечная дата периода.
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

    public double calculateMonthlyExpress(String id, String month) {
        return userRepository.getTransactions(id).values().stream()
                .mapToDouble(transaction -> !transaction.isIncome() && transaction.getDate().toString().substring(0,7).equals(month) ?
                        transaction.getAmount() : 0)
                .sum();

    }

    /**
     * Генерирует финансовый отчёт для пользователя за указанный период.
     * Отчёт включает текущий баланс, доходы, расходы и расходы по категориям.
     *
     * @param id уникальный идентификатор пользователя.
     * @param start начальная дата периода.
     * @param end конечная дата периода.
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

    public String viewTransactionNoFilter(String id) {
        return formatTransactions(userRepository.getTransactions(id).values().stream().toList(), "Список транзакций:");
    }
    public String viewTransactionsDateFilter(String id, LocalDate dateFilter) {
        List<Transaction> filteredTransactions = userRepository.getTransactions(id).values().stream()
                .filter(transaction -> transaction.getDate().isEqual(dateFilter))
                .toList();
        return formatTransactions(filteredTransactions, "Список транзакций по дате " + dateFilter + ":");
    }

    public String viewTransactionsCategoryFilter(String id, String categoryFilter) {
        List<Transaction> filteredTransactions = userRepository.getTransactions(id).values().stream()
                .filter(transaction -> transaction.getCategory().equals(categoryFilter))
                .toList();
        return formatTransactions(filteredTransactions, "Список транзакций по категории " + categoryFilter + ":");
    }

    public String viewTransactionsIsIncomeFilter(String id, boolean isIncomeFilter) {
        List<Transaction> filteredTransactions = userRepository.getTransactions(id).values().stream()
                .filter(transaction -> transaction.isIncome() == isIncomeFilter)
                .toList();
        String type = isIncomeFilter ? "Доходы" : "Расходы";
        return formatTransactions(filteredTransactions, "Список транзакций (" + type + "):");
    }


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
