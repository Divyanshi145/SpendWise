package com.example.expensetracker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionHelper {

    public static final int SMS_PERMISSION_CODE = 101;

    public static boolean hasSmsPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestSmsPermissions(Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.RECEIVE_SMS)) {
            new AlertDialog.Builder(activity)
                    .setTitle("SMS Permission Needed")
                    .setMessage("This app needs SMS permissions to automatically detect bank transactions and save you time on manual entry.")
                    .setPositiveButton("Grant", (dialog, which) -> {
                        ActivityCompat.requestPermissions(activity, 
                                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 
                                SMS_PERMISSION_CODE);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            ActivityCompat.requestPermissions(activity, 
                    new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 
                    SMS_PERMISSION_CODE);
        }
    }
}
