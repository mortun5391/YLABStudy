package com.financetracker.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Класс для обработки ввода данных с проверкой корректности.
 */
public class InputValidator {
    private Scanner scanner;

    public InputValidator(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Запрашивает у пользователя ввод целого числа и проверяет его корректность.
     *
     * @param prompt Сообщение, которое будет показано пользователю.
     * @return Целое число, введённое пользователем.
     */
    public int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите целое число.");
            } finally {
                scanner.nextLine(); // Очистка буфера после ввода
            }
        }
    }

    /**
     * Запрашивает у пользователя ввод числа с плавающей точкой и проверяет его корректность.
     *
     * @param prompt Сообщение, которое будет показано пользователю.
     * @return Число с плавающей точкой, введённое пользователем.
     */
    public double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите число.");
            } finally {
                scanner.nextLine(); // Очистка буфера после ввода
            }
        }
    }

    /**
     * Запрашивает у пользователя ввод даты и проверяет её корректность.
     *
     * @param prompt Сообщение, которое будет показано пользователю.
     * @return Объект LocalDate, представляющий введённую дату.
     */
    public LocalDate getDateInput(String prompt) {
        LocalDate date = null;
        while (date == null) {
            System.out.print(prompt);
            String dateInput = scanner.nextLine();
            try {
                date = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверный формат даты. Используйте формат гггг-мм-дд.");
            }
        }
        return date;
    }

    /**
     * Запрашивает у пользователя ввод булевого значения (true/false) и проверяет его корректность.
     *
     * @param prompt Сообщение, которое будет показано пользователю.
     * @return Булево значение, введённое пользователем.
     */
    public boolean getBooleanInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                return Boolean.parseBoolean(input);
            } else {
                System.out.println("Ошибка: введите 'true' или 'false'.");
            }
        }
    }

    /**
     * Запрашивает у пользователя ввод строки и проверяет, что она не пустая.
     *
     * @param prompt Сообщение, которое будет показано пользователю.
     * @return Строка, введённая пользователем.
     */
    public String getStringInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Ошибка: ввод не может быть пустым.");
            }
        }
    }
}