package com.financetracker.model;

public class BudgetRecord {
    private String month;
    private double budget;
    private double express;

    public BudgetRecord(String month, double budget) {
        this.month = month;
        this.budget = budget;
        this.express = 0;

    }

    public void addExpress(double express) {
        this.express += express;
    }

    public double getExpress() {
        return express;
    }

    public void setExpress(double express) {
        this.express = express;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }
}
