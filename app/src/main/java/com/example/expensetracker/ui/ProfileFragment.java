package com.example.expensetracker.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.LoginActivity;
import com.example.expensetracker.R;
import com.example.expensetracker.data.User;
import com.example.expensetracker.utils.SessionManager;
import com.example.expensetracker.viewmodel.UserViewModel;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private SessionManager sessionManager;
    private User currentUser;

    private TextView tvName, tvEmail, tvInitials, tvMemberSince, tvBudget;
    private View viewAvatarBg;
    private SwitchMaterial switchBudgetNotifs, switchDailySummary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvInitials = view.findViewById(R.id.tv_profile_initials);
        tvMemberSince = view.findViewById(R.id.tv_member_since);
        tvBudget = view.findViewById(R.id.tv_profile_budget);
        viewAvatarBg = view.findViewById(R.id.view_avatar_bg);
        switchBudgetNotifs = view.findViewById(R.id.switch_budget_notifs);
        switchDailySummary = view.findViewById(R.id.switch_daily_summary);
        
        ImageView ivEditBudget = view.findViewById(R.id.iv_edit_budget);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        SharedPreferences settings = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        switchBudgetNotifs.setChecked(settings.getBoolean("budget_notifications", true));
        switchDailySummary.setChecked(settings.getBoolean("daily_summary", true));

        switchBudgetNotifs.setOnCheckedChangeListener((b, isChecked) -> settings.edit().putBoolean("budget_notifications", isChecked).apply());
        switchDailySummary.setOnCheckedChangeListener((b, isChecked) -> settings.edit().putBoolean("daily_summary", isChecked).apply());

        userViewModel.getUserById(sessionManager.getLoggedInUserId()).observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                currentUser = user;
                tvName.setText(user.getFullName());
                tvEmail.setText(user.getEmail());
                tvInitials.setText(user.getProfileInitials());
                tvMemberSince.setText("Member Since: " + user.getCreatedAt());
                tvBudget.setText(String.format(Locale.getDefault(), "₹%.2f", user.getDailyBudgetLimit()));

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.OVAL);
                shape.setColor(Color.parseColor(user.getAvatarColor()));
                viewAvatarBg.setBackground(shape);
            }
        });

        ivEditBudget.setOnClickListener(v -> showEditBudgetDialog());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void showEditBudgetDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_budget, null);
        TextInputEditText etBudget = dialogView.findViewById(R.id.et_edit_budget);
        if (currentUser != null) etBudget.setText(String.valueOf(currentUser.getDailyBudgetLimit()));

        new AlertDialog.Builder(requireContext())
                .setTitle("Edit Daily Budget")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String val = etBudget.getText().toString();
                    if (!val.isEmpty()) {
                        double budget = Double.parseDouble(val);
                        userViewModel.updateDailyBudget(sessionManager.getLoggedInUserId(), budget);
                        Toast.makeText(getContext(), "Budget updated", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void logout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    sessionManager.clearSession();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
