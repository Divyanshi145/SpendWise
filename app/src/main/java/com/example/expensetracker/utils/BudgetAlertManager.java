package com.example.expensetracker.utils;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.expensetracker.MainActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.receiver.DailySummaryReceiver;

import java.util.Calendar;
import java.util.Locale;

public class BudgetAlertManager {

    public static final String CHANNEL_BUDGET = "budget_warnings";
    public static final String CHANNEL_SUMMARY = "daily_summary";

    public static void initChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            
            NotificationChannel budgetChannel = new NotificationChannel(CHANNEL_BUDGET, 
                    "Budget Alerts", NotificationManager.IMPORTANCE_HIGH);
            budgetChannel.enableVibration(true);
            nm.createNotificationChannel(budgetChannel);

            NotificationChannel summaryChannel = new NotificationChannel(CHANNEL_SUMMARY, 
                    "Daily Summary", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(summaryChannel);
        }
    }

    public static void checkAndAlert(Context context, int userId, double spent, double limit) {
        if (limit <= 0) return;

        SharedPreferences prefs = context.getSharedPreferences("budget_alerts", Context.MODE_PRIVATE);
        String todayKey = new java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new java.util.Date());
        
        double percent = (spent / limit) * 100;

        if (percent >= 100) {
            if (!prefs.getString("exceeded_sent_date", "").equals(todayKey)) {
                sendNotification(context, "⛔ Daily Budget Exceeded!", 
                        String.format(Locale.getDefault(), "You've exceeded today's limit by ₹%.2f", spent - limit), 
                        NotificationCompat.PRIORITY_MAX, todayKey, "exceeded_sent_date");
            }
        } else if (percent >= 90) {
            if (!prefs.getString("critical_sent_date", "").equals(todayKey)) {
                sendNotification(context, "Budget Almost Exhausted!", 
                        String.format(Locale.getDefault(), "Only ₹%.2f left. Spent ₹%.2f of ₹%.2f.", limit - spent, spent, limit), 
                        NotificationCompat.PRIORITY_HIGH, todayKey, "critical_sent_date");
            }
        } else if (percent >= 70) {
            if (!prefs.getString("warning_sent_date", "").equals(todayKey)) {
                sendNotification(context, "Spending Warning — SpendWise", 
                        String.format(Locale.getDefault(), "You've used 70%% of today's budget. ₹%.2f remaining.", limit - spent), 
                        NotificationCompat.PRIORITY_DEFAULT, todayKey, "warning_sent_date");
            }
        }
    }

    private static void sendNotification(Context context, String title, String body, int priority, String dateKey, String prefKey) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_BUDGET)
                .setSmallIcon(R.drawable.ic_wallet)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(priority)
                .setContentIntent(pi)
                .setAutoCancel(true);

        if (priority >= NotificationCompat.PRIORITY_HIGH) {
            builder.setSound(alarmSound);
            builder.setVibrate(new long[]{500, 200, 500});
        }

        NotificationManagerCompat.from(context).notify(prefKey.hashCode(), builder.build());
        context.getSharedPreferences("budget_alerts", Context.MODE_PRIVATE).edit().putString(prefKey, dateKey).apply();
    }

    public static void scheduleDailySummaryNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, DailySummaryReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 1001, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 21);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) calendar.add(Calendar.DATE, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }
    }
}
