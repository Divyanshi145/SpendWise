package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.data.User;
import com.example.expensetracker.utils.PasswordUtils;
import com.example.expensetracker.utils.SessionManager;
import com.example.expensetracker.viewmodel.UserViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword, etBudget;
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        sessionManager = new SessionManager(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        etName = findViewById(R.id.et_reg_name);
        etEmail = findViewById(R.id.et_reg_email);
        etPassword = findViewById(R.id.et_reg_password);
        etConfirmPassword = findViewById(R.id.et_reg_confirm_password);
        etBudget = findViewById(R.id.et_reg_budget);

        tilName = findViewById(R.id.til_reg_name);
        tilEmail = findViewById(R.id.til_reg_email);
        tilPassword = findViewById(R.id.til_reg_password);
        tilConfirmPassword = findViewById(R.id.til_reg_confirm_password);

        Button btnRegister = findViewById(R.id.btn_register);
        TextView tvGoToLogin = findViewById(R.id.tv_go_to_login);

        btnRegister.setOnClickListener(v -> registerUser());
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String budgetStr = etBudget.getText().toString().trim();

        boolean isValid = true;

        if (name.length() < 2) {
            tilName.setError("Name too short");
            isValid = false;
        } else {
            tilName.setError(null);
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email format");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        if (password.length() < 6) {
            tilPassword.setError("Minimum 6 characters");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }

        double budget = 0;
        if (!budgetStr.isEmpty()) {
            try {
                budget = Double.parseDouble(budgetStr);
                if (budget <= 0) {
                    Toast.makeText(this, "Budget must be greater than 0", Toast.LENGTH_SHORT).show();
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                isValid = false;
            }
        }

        if (!isValid) return;

        final double finalBudget = budget;
        new Thread(() -> {
            if (userViewModel.getUserByEmail(email) != null) {
                runOnUiThread(() -> tilEmail.setError("Email already registered"));
                return;
            }

            String passwordHash = PasswordUtils.hashPassword(password);
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String createdAt = df.format(Calendar.getInstance().getTime());

            String initials = "";
            String[] parts = name.split(" ");
            for (String part : parts) if (!part.isEmpty()) initials += part.substring(0, 1).toUpperCase();
            if (initials.length() > 2) initials = initials.substring(0, 2);

            String[] colors = {"#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#009688", "#4CAF50"};
            String avatarColor = colors[new Random().nextInt(colors.length)];

            User user = new User(name, email, passwordHash, createdAt, initials, avatarColor);
            user.setDailyBudgetLimit(finalBudget);
            
            userViewModel.insert(user);
            
            // Wait for insert and get user to log in
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            User registeredUser = userViewModel.getUserByEmail(email);

            runOnUiThread(() -> {
                if (registeredUser != null) {
                    sessionManager.saveLoginSession(registeredUser.getId(), registeredUser.getEmail(), registeredUser.getFullName());
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }).start();
    }
}
