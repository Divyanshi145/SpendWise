package com.example.expensetracker.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.R;
import com.example.expensetracker.data.TransactionInfo;
import com.example.expensetracker.receiver.SmsReceiver;
import com.example.expensetracker.utils.PermissionHelper;
import com.example.expensetracker.utils.SessionManager;
import com.example.expensetracker.utils.TransactionParser;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        SwitchMaterial switchSms = view.findViewById(R.id.switch_sms_detection);
        SwitchMaterial switchNotif = view.findViewById(R.id.switch_notifications);
        Button btnTest = view.findViewById(R.id.btn_test_sms);
        Button btnSync = view.findViewById(R.id.btn_sync_sms);

        switchSms.setChecked(prefs.getBoolean("sms_detection", true));
        switchNotif.setChecked(prefs.getBoolean("show_notifications", true));

        switchSms.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sms_detection", isChecked).apply();
            if (isChecked && !PermissionHelper.hasSmsPermissions(requireContext())) {
                PermissionHelper.requestSmsPermissions(requireActivity());
            }
        });

        switchNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("show_notifications", isChecked).apply();
        });

        btnTest.setOnClickListener(v -> simulateSms());
        btnSync.setOnClickListener(v -> syncSmsInbox());

        return view;
    }

    private void simulateSms() {
        SessionManager sessionManager = new SessionManager(requireContext());
        int userId = sessionManager.getLoggedInUserId();
        if (userId == -1) return;

        String fakeSms = "Alert: Your Acct XX123 is debited by Rs.1500.00 on 2023-10-27 at STARBUCKS. Ref: 123456";
        TransactionInfo info = TransactionParser.parseTransaction(fakeSms, "BANK-SMS");
        
        if (info != null) {
            SmsReceiver.saveAndNotify(requireContext(), info);
            Toast.makeText(getContext(), "Test SMS processed", Toast.LENGTH_SHORT).show();
        }
    }

    private void syncSmsInbox() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.requestSmsPermissions(requireActivity());
            return;
        }

        int syncCount = 0;
        ContentResolver cr = requireContext().getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://sms/inbox"), null, null, null, "date DESC LIMIT 50");

        if (cursor != null && cursor.moveToFirst()) {
            int bodyIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY);
            int addressIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS);

            do {
                String body = cursor.getString(bodyIndex);
                String address = cursor.getString(addressIndex);
                TransactionInfo info = TransactionParser.parseTransaction(body, address);
                
                if (info != null) {
                    SmsReceiver.saveAndNotify(requireContext(), info);
                    syncCount++;
                }
            } while (cursor.moveToNext());
            cursor.close();
        }

        Toast.makeText(getContext(), "Sync complete. Found " + syncCount + " transactions.", Toast.LENGTH_LONG).show();
    }
}
