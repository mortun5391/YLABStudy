package com.financetracker.service;

import java.util.Scanner;

public class ScannerInputProvider implements InputProvider {
    private Scanner scanner;

    public ScannerInputProvider(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public int nextInt() {
        return scanner.nextInt();
    }

    @Override
    public double nextDouble() {
        return scanner.nextDouble();
    }

    @Override
    public String nextLine() {
        return scanner.nextLine();
    }
}