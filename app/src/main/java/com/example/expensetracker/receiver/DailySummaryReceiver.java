package com.example.expensetracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.expensetracker.data.ExpenseDatabase;
import com.example.expensetracker.utils.BudgetAlertManager;
import com.example.expensetracker.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DailySummaryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (!prefs.getBoolean("daily_summary", true)) return;

        SessionManager sessionManager = new SessionManager(context);
        if (!sessionManager.isLoggedIn()) return;

        int userId = sessionManager.getLoggedInUserId();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = df.format(Calendar.getInstance().getTime());

        ExpenseDatabase.databaseWriteExecutor.execute(() -> {
            // This is a simplified total query for the receiver
            // In a full implementation, you'd add a DAO method for this
            // double total = ExpenseDatabase.getDatabase(context).expenseDao().getTodayTotal(userId, today);
            // int count = ExpenseDatabase.getDatabase(context).expenseDao().getTodayCount(userId, today);
            
            // For now, we'll use a placeholder logic or trigger the notification helper
            // BudgetAlertManager.sendDailySummary(context, total, count, limit);
        });
    }
}
