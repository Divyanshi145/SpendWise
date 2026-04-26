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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.data.Expense;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class EditExpenseDialog extends DialogFragment {

    private TextInputEditText etTitle, etAmount, etDate, etDescription;
    private AutoCompleteTextView spinnerCategory;
    private ExpenseViewModel expenseViewModel;
    private Expense expense;

    private String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Health", "Education", "Bills", "Other"};

    public static EditExpenseDialog newInstance(Expense expense) {
        EditExpenseDialog fragment = new EditExpenseDialog();
        fragment.expense = expense;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_expense, container, false);

        etTitle = view.findViewById(R.id.et_edit_title);
        etAmount = view.findViewById(R.id.et_edit_amount);
        etDate = view.findViewById(R.id.et_edit_date);
        etDescription = view.findViewById(R.id.et_edit_description);
        spinnerCategory = view.findViewById(R.id.spinner_edit_category);
        Button btnUpdate = view.findViewById(R.id.btn_update);
        Button btnCancel = view.findViewById(R.id.btn_cancel);

        expenseViewModel = new ViewModelProvider(requireActivity()).get(ExpenseViewModel.class);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(adapter);

        if (expense != null) {
            etTitle.setText(expense.getTitle());
            etAmount.setText(String.valueOf(expense.getAmount()));
            spinnerCategory.setText(expense.getCategory(), false);
            etDate.setText(expense.getDate());
            etDescription.setText(expense.getDescription());
        }

        etDate.setOnClickListener(v -> showDatePicker());

        btnCancel.setOnClickListener(v -> dismiss());

        btnUpdate.setOnClickListener(v -> {
            updateExpense();
            dismiss();
        });

        return view;
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + String.format("%02d", dayOfMonth);
                    etDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateExpense() {
        if (expense == null) return;

        expense.setTitle(etTitle.getText().toString().trim());
        expense.setAmount(Double.parseDouble(etAmount.getText().toString().trim()));
        expense.setCategory(spinnerCategory.getText().toString().trim());
        expense.setDate(etDate.getText().toString().trim());
        expense.setDescription(etDescription.getText().toString().trim());

        expenseViewModel.update(expense);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
