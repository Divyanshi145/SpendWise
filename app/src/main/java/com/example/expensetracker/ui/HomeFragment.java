package com.example.expensetracker.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.LoginActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.data.Expense;
import com.example.expensetracker.utils.SessionManager;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.example.expensetracker.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ExpenseViewModel expenseViewModel;
    private UserViewModel userViewModel;
    private ExpenseAdapter adapter;

    private TextView tvTotalAmount, tvBudgetSummary;
    private ProgressBar pbBudget;
    private LinearLayout layoutEmpty;

    private double dailyLimit = 0;
    private int currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());
        if (!sessionManager.isLoggedIn()) {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
            return view;
        }

        currentUserId = sessionManager.getLoggedInUserId();
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        CardView cardPendingBanner = view.findViewById(R.id.card_pending_banner);
        
        // Budget UI
        CardView cardBudgetProgress = view.findViewById(R.id.card_budget_progress);
        pbBudget = view.findViewById(R.id.pb_budget);
        tvBudgetSummary = view.findViewById(R.id.tv_budget_summary);

        RecyclerView recyclerView = view.findViewById(R.id.rv_expenses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter();
        recyclerView.setAdapter(adapter);

        // Load Data
        loadExpenses(cardPendingBanner);
        loadBudget();

        View fabAdd = view.findViewById(R.id.fab_add);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> 
                    Navigation.findNavController(v).navigate(R.id.navigation_add));
        }

        if (cardBudgetProgress != null) {
            cardBudgetProgress.setOnClickListener(v -> 
                    Navigation.findNavController(v).navigate(R.id.navigation_profile));
        }
        
        if (cardPendingBanner != null) {
            cardPendingBanner.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.navigation_pending));
        }

        setupSwipeToDelete(recyclerView);

        return view;
    }

    private void loadExpenses(CardView cardPendingBanner) {
        expenseViewModel.getAllExpenses(currentUserId).observe(getViewLifecycleOwner(), expenses -> {
            if (expenses == null || expenses.isEmpty()) {
                if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
            } else {
                if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
                adapter.submitList(expenses);
            }
            updateTotalSpending(expenses);
            updateBudgetProgress();
        });

        expenseViewModel.getUncategorizedExpenses(currentUserId).observe(getViewLifecycleOwner(), list -> {
            if (cardPendingBanner != null) {
                cardPendingBanner.setVisibility(list != null && !list.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadBudget() {
        userViewModel.getUserById(currentUserId).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                dailyLimit = user.getDailyBudgetLimit();
                updateBudgetProgress();
            }
        });
    }

    private void updateBudgetProgress() {
        if (pbBudget == null || tvBudgetSummary == null) return;
        
        if (dailyLimit <= 0) {
            tvBudgetSummary.setText("Tap to set daily budget limit →");
            pbBudget.setProgress(0);
            return;
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String today = df.format(Calendar.getInstance().getTime());

        new Thread(() -> {
            double todaySpent = expenseViewModel.getTodayTotal(currentUserId, today);
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                int progress = (int) ((todaySpent / dailyLimit) * 100);
                pbBudget.setProgress(Math.min(progress, 100));

                if (progress >= 100) pbBudget.getProgressDrawable().setTint(Color.RED);
                else if (progress >= 70) pbBudget.getProgressDrawable().setTint(Color.YELLOW);
                else pbBudget.getProgressDrawable().setTint(ContextCompat.getColor(requireContext(), R.color.primary));

                tvBudgetSummary.setText(String.format(Locale.getDefault(), 
                        "₹%.2f spent of ₹%.2f limit", todaySpent, dailyLimit));
            });
        }).start();
    }

    private void updateTotalSpending(List<Expense> expenses) {
        if (tvTotalAmount == null) return;
        double total = 0;
        if (expenses != null) for (Expense e : expenses) total += e.getAmount();
        tvTotalAmount.setText(String.format(Locale.getDefault(), "₹%.2f", total));
    }

    private void setupSwipeToDelete(RecyclerView rv) {
        if (rv == null) return;
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder v, @NonNull RecyclerView.ViewHolder t) { return false; }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder v, int dir) {
                int pos = v.getAbsoluteAdapterPosition();
                Expense e = adapter.getExpenseAt(pos);
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete?")
                        .setMessage("Permanent action.")
                        .setPositiveButton("Delete", (d, w) -> expenseViewModel.delete(e))
                        .setNegativeButton("Cancel", (d, w) -> adapter.notifyItemChanged(pos))
                        .show();
            }
        }).attachToRecyclerView(rv);
    }
}
