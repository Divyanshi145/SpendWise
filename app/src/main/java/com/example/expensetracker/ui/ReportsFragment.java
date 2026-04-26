package com.example.expensetracker.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.expensetracker.R;
import com.example.expensetracker.data.CategorySum;
import com.example.expensetracker.data.DateSum;
import com.example.expensetracker.utils.SessionManager;
import com.example.expensetracker.viewmodel.ExpenseViewModel;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private PieChart pieChart;
    private BarChart barChart;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        sessionManager = new SessionManager(requireContext());
        pieChart = view.findViewById(R.id.pie_chart);
        barChart = view.findViewById(R.id.bar_chart);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        ExpenseViewModel expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        int userId = sessionManager.getLoggedInUserId();

        setupPieChart();
        setupBarChart();

        expenseViewModel.getCategorySums(userId).observe(getViewLifecycleOwner(), this::updatePieChart);
        expenseViewModel.getDateSums(userId).observe(getViewLifecycleOwner(), this::updateBarChart);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    pieChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);
                } else {
                    pieChart.setVisibility(View.GONE);
                    barChart.setVisibility(View.VISIBLE);
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        return view;
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextSize(18f);
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
    }

    private void updatePieChart(List<CategorySum> categorySums) {
        List<PieEntry> entries = new ArrayList<>();
        double total = 0;
        if (categorySums != null) {
            for (CategorySum sum : categorySums) {
                entries.add(new PieEntry((float) sum.total, sum.category));
                total += sum.total;
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Categories");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.setCenterText("Total:\n₹" + String.format(Locale.getDefault(), "%.2f", total));
        pieChart.animateY(1400);
        pieChart.invalidate();
    }

    private void updateBarChart(List<DateSum> dateSums) {
        List<BarEntry> entries = new ArrayList<>();
        final List<String> dates = new ArrayList<>();

        if (dateSums != null) {
            for (int i = 0; i < dateSums.size(); i++) {
                DateSum sum = dateSums.get(i);
                entries.add(new BarEntry(i, (float) sum.total));
                dates.add(sum.date);
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Daily Spending");
        dataSet.setColors(ColorTemplate.LIBERTY_COLORS);

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < dates.size()) {
                    return dates.get(index);
                }
                return "";
            }
        });
        
        barChart.animateY(1000);
        barChart.invalidate();
    }
}
