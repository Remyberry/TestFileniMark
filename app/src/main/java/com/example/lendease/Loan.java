package com.example.lendease;

public class Loan {
    private String borrowerName;
    private String dueDate;
    private double amount;

    public Loan(String borrowerName, String dueDate, double amount) {
        this.borrowerName = borrowerName;
        this.dueDate = dueDate;
        this.amount = amount;
    }

    // Getters and setters (optional, but good practice)

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
