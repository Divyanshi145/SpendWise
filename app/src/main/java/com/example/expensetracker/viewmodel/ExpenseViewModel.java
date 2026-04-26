package com.example.expensetracker.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expensetracker.data.CategorySum;
import com.example.expensetracker.data.DateSum;
import com.example.expensetracker.data.Expense;
import com.example.expensetracker.data.ExpenseRepository;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private ExpenseRepository mRepository;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ExpenseRepository(application);
    }

    public LiveData<List<Expense>> getAllExpenses(int userId) { 
        return mRepository.getAllExpenses(userId); 
    }
    
    public LiveData<List<CategorySum>> getCategorySums(int userId) { 
        return mRepository.getCategorySums(userId); 
    }
    
    public LiveData<List<DateSum>> getDateSums(int userId) { 
        return mRepository.getDateSums(userId); 
    }
    
    public LiveData<List<Expense>> getUncategorizedExpenses(int userId) { 
        return mRepository.getUncategorizedExpenses(userId); 
    }

    public void insert(Expense expense) { mRepository.insert(expense); }
    public void update(Expense expense) { mRepository.update(expense); }
    public void delete(Expense expense) { mRepository.delete(expense); }
    public void markAsCategorized(int id, String title, String category) {
        mRepository.markAsCategorized(id, title, category);
    }
    
    public double getTodayTotal(int userId, String date) {
        return mRepository.getTodayTotal(userId, date);
    }
}
