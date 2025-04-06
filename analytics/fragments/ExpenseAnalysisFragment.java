package com.example.diplom.analytics.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.diplom.R;
import com.example.diplom.analytics.AnalyticsViewModel;
import com.example.diplom.databinding.FragmentExpenseAnalysisBinding;
import com.example.diplom.utils.CurrencyFormatter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Фрагмент для отображения анализа расходов
 */
public class ExpenseAnalysisFragment extends Fragment {

    private FragmentExpenseAnalysisBinding binding;
    private AnalyticsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExpenseAnalysisBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(AnalyticsViewModel.class);

        // Настройка диаграммы
        setupCharts();

        // Наблюдение за данными
        observeData();
    }

    /**
     * Настройка диаграммы
     */
    private void setupCharts() {
        // Настройка круговой диаграммы расходов
        PieChart pieChart = binding.expensesPieChart;
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Расходы по категориям");
        pieChart.setCenterTextSize(16f);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextSize(12f);
        pieChart.setDrawSlicesUnderHole(false);
        pieChart.setExtraOffsets(20, 20, 20, 20);
    }

    /**
     * Наблюдение за данными из ViewModel
     */
    private void observeData() {
        // Наблюдение за общими расходами
        viewModel.getTotalExpenses().observe(getViewLifecycleOwner(), expenses -> {
            if (expenses != null) {
                binding.totalExpensesValue.setText(CurrencyFormatter.format(expenses));
            }
        });

        // Наблюдение за прогнозом расходов
        viewModel.getExpensesForecast().observe(getViewLifecycleOwner(), forecast -> {
            if (forecast != null) {
                binding.forecastExpensesValue.setText(CurrencyFormatter.format(forecast));
            }
        });

        // Наблюдение за расходами по категориям
        viewModel.getExpensesByCategory().observe(getViewLifecycleOwner(), this::updatePieChart);
    }

    /**
     * Обновление данных круговой диаграммы
     * @param data данные для диаграммы
     */
    private void updatePieChart(Map<String, Double> data) {
        if (data == null || data.isEmpty()) {
            binding.expensesPieChart.setVisibility(View.GONE);
            binding.noDataText.setVisibility(View.VISIBLE);
            return;
        }

        binding.expensesPieChart.setVisibility(View.VISIBLE);
        binding.noDataText.setVisibility(View.GONE);

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            if (entry.getValue() > 0) {
                entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "Категории");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // Настройка цветов
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.MATERIAL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new PercentFormatter(binding.expensesPieChart));
        pieData.setValueTextSize(12f);
        pieData.setValueTextColor(Color.WHITE);

        binding.expensesPieChart.setData(pieData);
        binding.expensesPieChart.highlightValues(null);
        binding.expensesPieChart.invalidate();
        binding.expensesPieChart.animateY(1000);

        // Создание таблицы расходов
        StringBuilder tableBuilder = new StringBuilder();
        tableBuilder.append("Категория\t\tСумма\t\t%\n");
        tableBuilder.append("--------------------------------\n");

        double total = 0;
        for (Double value : data.values()) {
            total += value;
        }

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            double percentage = (entry.getValue() / total) * 100;
            tableBuilder.append(String.format("%s\t\t%s\t\t%.1f%%\n",
                    entry.getKey(),
                    CurrencyFormatter.format(entry.getValue()),
                    percentage));
        }

        binding.expensesTableText.setText(tableBuilder.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
