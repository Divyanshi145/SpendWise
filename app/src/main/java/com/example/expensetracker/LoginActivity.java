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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        sessionManager = new SessionManager(this);
        if (sessionManager.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        tilEmail = findViewById(R.id.til_login_email);
        tilPassword = findViewById(R.id.til_login_password);
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnRegister = findViewById(R.id.btn_go_to_register);
        TextView tvForgotPassword = findViewById(R.id.tv_forgot_password);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        tvForgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            return;
        } else {
            tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setError("Password is required");
            return;
        } else {
            tilPassword.setError(null);
        }

        new Thread(() -> {
            User user = userViewModel.getUserByEmail(email);
            String hashedPassword = PasswordUtils.hashPassword(password);

            runOnUiThread(() -> {
                if (user != null && user.getPasswordHash().equals(hashedPassword)) {
                    sessionManager.saveLoginSession(user.getId(), user.getEmail(), user.getFullName());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Invalid email or password", Snackbar.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}
