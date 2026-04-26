package com.example.expensetracker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.expensetracker.data.Expense;
import com.example.expensetracker.data.ExpenseDatabase;
import com.example.expensetracker.data.TransactionInfo;
import com.example.expensetracker.utils.NotificationHelper;
import com.example.expensetracker.utils.SessionManager;
import com.example.expensetracker.utils.TransactionParser;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("sms_detection", true);
        if (!enabled) return;

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                String format = bundle.getString("format");
                for (Object pdu : pdus) {
                    SmsMessage smsMessage;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
                    } else {
                        smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    }
                    
                    String body = smsMessage.getMessageBody();
                    String sender = smsMessage.getOriginatingAddress();

                    TransactionInfo info = TransactionParser.parseTransaction(body, sender);
                    if (info != null) {
                        saveAndNotify(context, info);
                    }
                }
            }
        }
    }

    public static void saveAndNotify(Context context, TransactionInfo info) {
        SessionManager sessionManager = new SessionManager(context);
        int userId = sessionManager.getLoggedInUserId();
        
        if (userId == -1) {
            Log.e("SmsReceiver", "No user logged in, ignoring transaction");
            return;
        }

        Expense expense = new Expense(userId, info.merchant, info.amount, "Uncategorized", info.date, "Auto-detected from SMS");
        expense.setAutoDetected(true);
        expense.setCategorized(false);
        expense.setRawSms(info.rawSms);
        expense.setSource("sms");

        ExpenseDatabase.databaseWriteExecutor.execute(() -> 
            ExpenseDatabase.getDatabase(context).expenseDao().insertExpense(expense)
        );

        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        if (prefs.getBoolean("show_notifications", true)) {
            NotificationHelper.sendTransactionNotification(context, info.amount, info.merchant);
        }
    }
}
