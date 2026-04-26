package com.example.expensetracker.data;

public class TransactionInfo {
    public double amount;
    public String bankName;
    public String merchant;
    public String date;
    public String rawSms;

    public TransactionInfo(double amount, String bankName, String merchant, String date, String rawSms) {
        this.amount = amount;
        this.bankName = bankName;
        this.merchant = merchant;
        this.date = date;
        this.rawSms = rawSms;
    }
}
