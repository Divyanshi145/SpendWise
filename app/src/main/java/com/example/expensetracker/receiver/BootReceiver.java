package com.example.expensetracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.expensetracker.utils.BudgetAlertManager;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            BudgetAlertManager.scheduleDailySummaryNotification(context);
        }
    }
}
