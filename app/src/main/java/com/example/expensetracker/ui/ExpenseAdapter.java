package com.example.expensetracker.ui;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensetracker.R;
import com.example.expensetracker.data.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder> {

    private OnItemClickListener listener;

    public ExpenseAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK = new DiffUtil.ItemCallback<Expense>() {
        @Override
        public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getAmount() == newItem.getAmount() &&
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getDate().equals(newItem.getDate());
        }
    };

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense currentExpense = getItem(position);
        holder.tvTitle.setText(currentExpense.getTitle());
        holder.tvAmount.setText(String.format("$%.2f", currentExpense.getAmount()));
        
        // Date formatting: yyyy-MM-dd to dd MMM yyyy
        String originalDate = currentExpense.getDate();
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            Date date = originalFormat.parse(originalDate);
            if (date != null) {
                holder.tvDate.setText(targetFormat.format(date));
            } else {
                holder.tvDate.setText(originalDate);
            }
        } catch (ParseException e) {
            holder.tvDate.setText(originalDate);
        }
        
        // Category styling
        setCategoryStyle(holder, currentExpense.getCategory());
    }

    private void setCategoryStyle(ExpenseViewHolder holder, String category) {
        int iconRes = R.drawable.ic_other;
        int colorRes = R.color.cat_other;

        if (category != null) {
            switch (category) {
                case "Food":
                    iconRes = R.drawable.ic_food;
                    colorRes = R.color.cat_food;
                    break;
                case "Travel":
                    iconRes = R.drawable.ic_travel;
                    colorRes = R.color.cat_travel;
                    break;
                case "Shopping":
                    iconRes = R.drawable.ic_shopping;
                    colorRes = R.color.cat_shopping;
                    break;
                case "Entertainment":
                    iconRes = R.drawable.ic_entertainment;
                    colorRes = R.color.cat_entertainment;
                    break;
                case "Health":
                    iconRes = R.drawable.ic_health;
                    colorRes = R.color.cat_health;
                    break;
                case "Education":
                    iconRes = R.drawable.ic_education;
                    colorRes = R.color.cat_education;
                    break;
                case "Bills":
                    iconRes = R.drawable.ic_bills;
                    colorRes = R.color.cat_bills;
                    break;
            }
        }

        holder.ivIcon.setImageResource(iconRes);
        int color = ContextCompat.getColor(holder.itemView.getContext(), colorRes);
        holder.ivIcon.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    public Expense getExpenseAt(int position) {
        return getItem(position);
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvAmount;
        private final TextView tvDate;
        private final ImageView ivIcon;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            ivIcon = itemView.findViewById(R.id.iv_category_icon);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
