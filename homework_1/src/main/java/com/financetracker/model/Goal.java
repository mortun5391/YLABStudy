package com.financetracker.model;

public class Goal {
    private String goalName;
    private double targetAmount;
    private double currentAmount;

    public Goal(double targetAmount, String goalName) {
        this.targetAmount = targetAmount;
        this.goalName = goalName;
        this.currentAmount = 0;
    }

    public String getGoalName() {
        return goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void addCurrentAmount(double amount) {
        this.currentAmount += amount;
    }

    public int getProgress() {
        return (int) (currentAmount * 100/targetAmount) ;
    }

}
