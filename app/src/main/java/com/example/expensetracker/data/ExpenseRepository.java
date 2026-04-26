package com.example.expensetracker.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import java.util.concurrent.Future;

public class ExpenseRepository {
    private ExpenseDao mExpenseDao;

    public ExpenseRepository(Application application) {
        ExpenseDatabase db = ExpenseDatabase.getDatabase(application);
        mExpenseDao = db.expenseDao();
    }

    public LiveData<List<Expense>> getAllExpenses(int userId) {
        return mExpenseDao.getAllExpenses(userId);
    }

    public LiveData<List<CategorySum>> getCategorySums(int userId) {
        return mExpenseDao.getExpensesByCategory(userId);
    }

    public LiveData<List<DateSum>> getDateSums(int userId) {
        return mExpenseDao.getExpensesByDate(userId);
    }

    public LiveData<List<Expense>> getUncategorizedExpenses(int userId) {
        return mExpenseDao.getUncategorizedExpenses(userId);
    }

    public void insert(Expense expense) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> mExpenseDao.insertExpense(expense));
    }

    public void update(Expense expense) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> mExpenseDao.updateExpense(expense));
    }

    public void delete(Expense expense) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> mExpenseDao.deleteExpense(expense));
    }

    public void markAsCategorized(int id, String title, String category) {
        ExpenseDatabase.databaseWriteExecutor.execute(() -> mExpenseDao.markAsCategorized(id, title, category));
    }

    public double getTodayTotal(int userId, String date) {
        try {
            Future<Double> future = ExpenseDatabase.databaseWriteExecutor.submit(() -> mExpenseDao.getTodayTotal(userId, date));
            return future.get();
        } catch (Exception e) {
            return 0.0;
        }
    }
}
