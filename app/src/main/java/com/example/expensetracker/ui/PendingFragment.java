package com.example.expensetracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.Expense;
import com.example.expensetracker.utils.SessionManager;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class PendingFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;
    private ExpenseAdapter adapter;
    private TextView tvNoPending;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        sessionManager = new SessionManager(requireContext());
        tvNoPending = view.findViewById(R.id.tv_no_pending);
        RecyclerView recyclerView = view.findViewById(R.id.rv_pending);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        
        int userId = sessionManager.getLoggedInUserId();
        expenseViewModel.getUncategorizedExpenses(userId).observe(getViewLifecycleOwner(), expenses -> {
            if (expenses == null || expenses.isEmpty()) {
                tvNoPending.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                tvNoPending.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.submitList(expenses);
            }
        });

        adapter.setOnItemClickListener(this::showCategorizeDialog);

        return view;
    }

    private void showCategorizeDialog(Expense expense) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_categorize, null);

        TextView tvAmount = view.findViewById(R.id.tv_dialog_amount);
        TextView tvSms = view.findViewById(R.id.tv_dialog_sms);
        TextInputEditText etTitle = view.findViewById(R.id.et_dialog_title);
        AutoCompleteTextView spinnerCategory = view.findViewById(R.id.spinner_dialog_category);
        Button btnSave = view.findViewById(R.id.btn_dialog_save);
        Button btnDelete = view.findViewById(R.id.btn_dialog_delete);

        tvAmount.setText(String.format(Locale.getDefault(), "Rs. %.2f", expense.getAmount()));
        tvSms.setText(expense.getRawSms());
        etTitle.setText(expense.getTitle());

        String[] categories = {"Food", "Travel", "Shopping", "Entertainment", "Health", "Education", "Bills", "Other"};
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, categories);
        spinnerCategory.setAdapter(catAdapter);

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String category = spinnerCategory.getText().toString().trim();
            if (!title.isEmpty() && !category.isEmpty()) {
                expenseViewModel.markAsCategorized(expense.getId(), title, category);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(v -> {
            expenseViewModel.delete(expense);
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }
}
