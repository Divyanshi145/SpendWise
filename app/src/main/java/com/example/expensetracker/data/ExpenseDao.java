package com.example.expensetracker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Insert
    void insertExpense(Expense expense);

    @Update
    void updateExpense(Expense expense);

    @Delete
    void deleteExpense(Expense expense);

    @Query("SELECT * FROM expense WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<Expense>> getAllExpenses(int userId);

    @Query("SELECT category, SUM(amount) as total FROM expense WHERE userId = :userId GROUP BY category")
    LiveData<List<CategorySum>> getExpensesByCategory(int userId);

    @Query("SELECT date, SUM(amount) as total FROM expense WHERE userId = :userId GROUP BY date ORDER BY date")
    LiveData<List<DateSum>> getExpensesByDate(int userId);

    @Query("SELECT * FROM expense WHERE userId = :userId AND isCategorized = 0 AND isAutoDetected = 1 ORDER BY date DESC")
    LiveData<List<Expense>> getUncategorizedExpenses(int userId);

    @Query("UPDATE expense SET category = :category, title = :title, isCategorized = 1 WHERE id = :id")
    void markAsCategorized(int id, String title, String category);

    @Query("SELECT SUM(amount) FROM expense WHERE userId = :userId AND date = :date")
    double getTodayTotal(int userId, String date);
}
