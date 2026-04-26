package com.example.expensetracker.data;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = {"email"}, unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String currency = "₹";
    private double dailyBudgetLimit = 0.0;
    private String createdAt;
    private String profileInitials;
    private String avatarColor;

    public User(String fullName, String email, String passwordHash, String createdAt, String profileInitials, String avatarColor) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.profileInitials = profileInitials;
        this.avatarColor = avatarColor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public double getDailyBudgetLimit() { return dailyBudgetLimit; }
    public void setDailyBudgetLimit(double dailyBudgetLimit) { this.dailyBudgetLimit = dailyBudgetLimit; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getProfileInitials() { return profileInitials; }
    public void setProfileInitials(String profileInitials) { this.profileInitials = profileInitials; }

    public String getAvatarColor() { return avatarColor; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }
}
