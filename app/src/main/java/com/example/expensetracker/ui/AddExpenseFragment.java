package com.example.expensetracker.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.expensetracker.R;
import com.example.expensetracker.data.Expense;
import com.example.expensetracker.utils.BudgetAlertManager;
import com.example.expensetracker.utils.SessionManager;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.example.expensetracker.viewmodel.UserViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddExpenseFragment extends Fragment {

    private TextInputEditText etTitle, etAmount, etDate, etDescription;
    private TextInputLayout tilTitle, tilAmount, tilDate;
    private AutoCompleteTextView spinnerCategory;
    private ExpenseViewModel expenseViewModel;
    private UserViewModel userViewModel;
    private SessionManager sessionManager;

    private final String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Health", "Education", "Bills", "Other"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        sessionManager = new SessionManager(requireContext());
        etTitle = view.findViewById(R.id.et_title);
        etAmount = view.findViewById(R.id.et_amount);
        etDate = view.findViewById(R.id.et_date);
        etDescription = view.findViewById(R.id.et_description);
        spinnerCategory = view.findViewById(R.id.spinner_category);
        
        tilTitle = view.findViewById(R.id.til_title_parent);
        tilAmount = view.findViewById(R.id.til_amount_parent);
        tilDate = view.findViewById(R.id.til_date_parent);
        
        Button btnSave = view.findViewById(R.id.btn_save);

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(adapter);

        setCurrentDate();
        etDate.setOnClickListener(v -> showDatePicker());
        btnSave.setOnClickListener(v -> saveExpense());

        return view;
    }

    private void setCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(df.format(c.getTime()));
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + String.format(Locale.getDefault(), "%02d", (monthOfYear + 1)) + "-" + String.format(Locale.getDefault(), "%02d", dayOfMonth);
                    etDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void saveExpense() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String category = spinnerCategory.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        boolean isValid = true;
        if (title.isEmpty()) { tilTitle.setError("Title is required"); isValid = false; } else tilTitle.setError(null);
        
        double amount = 0;
        if (amountStr.isEmpty()) { tilAmount.setError("Amount is required"); isValid = false; }
        else {
            try {
                amount = Double.parseDouble(amountStr);
                if (amount <= 0) { tilAmount.setError("Amount must be > 0"); isValid = false; }
                else tilAmount.setError(null);
            } catch (Exception e) { isValid = false; }
        }

        if (!isValid) return;

        int userId = sessionManager.getLoggedInUserId();
        Expense expense = new Expense(userId, title, amount, category, date, description);
        expenseViewModel.insert(expense);

        // Budget Alert Check
        final double currentExpenseAmount = amount;
        new Thread(() -> {
            try { Thread.sleep(500); } catch (Exception ignored) {} // Wait for DB
            double todayTotal = expenseViewModel.getTodayTotal(userId, date);
            userViewModel.getUserById(userId).observe(getViewLifecycleOwner(), user -> {
                if (user != null && user.getDailyBudgetLimit() > 0) {
                    BudgetAlertManager.checkAndAlert(requireContext(), userId, todayTotal, user.getDailyBudgetLimit());
                }
            });
        }).start();

        Snackbar.make(requireView(), "Expense saved to SpendWise", Snackbar.LENGTH_SHORT).show();
        Navigation.findNavController(requireView()).navigateUp();
    }
}
