package com.example.expensetracker.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId")})
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId; // Foreign key to User
    private String title;
    private double amount;
    private String category;
    private String date;
    private String description;

    // SMS detection fields
    private boolean isAutoDetected = false;
    private boolean isCategorized = false;
    private String rawSms;
    private String source = "manual"; // "manual" or "sms"

    public Expense(int userId, String title, double amount, String category, String date, String description) {
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAutoDetected() { return isAutoDetected; }
    public void setAutoDetected(boolean autoDetected) { isAutoDetected = autoDetected; }

    public boolean isCategorized() { return isCategorized; }
    public void setCategorized(boolean categorized) { isCategorized = categorized; }

    public String getRawSms() { return rawSms; }
    public void setRawSms(String rawSms) { this.rawSms = rawSms; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
