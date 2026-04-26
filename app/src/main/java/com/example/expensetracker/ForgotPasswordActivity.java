package com.example.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.data.User;
import com.example.expensetracker.utils.PasswordUtils;
import com.example.expensetracker.viewmodel.UserViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etNewPassword, etConfirmNewPassword;
    private TextInputLayout tilEmail, tilNewPassword, tilConfirmNewPassword;
    private LinearLayout layoutResetFields;
    private Button btnAction;
    private UserViewModel userViewModel;
    private User targetUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.et_forgot_email);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password);
        tilEmail = findViewById(R.id.til_forgot_email);
        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmNewPassword = findViewById(R.id.til_confirm_new_password);
        layoutResetFields = findViewById(R.id.layout_reset_fields);
        btnAction = findViewById(R.id.btn_reset_action);
        TextView tvBack = findViewById(R.id.tv_back_to_login);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        btnAction.setOnClickListener(v -> {
            if (targetUser == null) {
                verifyEmail();
            } else {
                updatePassword();
            }
        });

        tvBack.setOnClickListener(v -> finish());
    }

    private void verifyEmail() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Enter registered email");
            return;
        }

        new Thread(() -> {
            User user = userViewModel.getUserByEmail(email);
            runOnUiThread(() -> {
                if (user != null) {
                    targetUser = user;
                    tilEmail.setEnabled(false);
                    layoutResetFields.setVisibility(View.VISIBLE);
                    btnAction.setText("Update Password");
                } else {
                    tilEmail.setError("Email not found");
                }
            });
        }).start();
    }

    private void updatePassword() {
        String pass = etNewPassword.getText().toString().trim();
        String confirm = etConfirmNewPassword.getText().toString().trim();

        if (pass.length() < 6) {
            tilNewPassword.setError("Min 6 chars");
            return;
        } else {
            tilNewPassword.setError(null);
        }

        if (!pass.equals(confirm)) {
            tilConfirmNewPassword.setError("Match failed");
            return;
        }

        targetUser.setPasswordHash(PasswordUtils.hashPassword(pass));
        userViewModel.update(targetUser);
        Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
