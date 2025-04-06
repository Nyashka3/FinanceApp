package com.example.diplom.analytics.fragments;

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
import com.example.diplom.databinding.FragmentEfficiencyAnalysisBinding;
import com.example.diplom.utils.CurrencyFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Фрагмент для отображения анализа эффективности (материалоемкость, трудоемкость)
 */
public class EfficiencyAnalysisFragment extends Fragment {

    private FragmentEfficiencyAnalysisBinding binding;
    private AnalyticsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEfficiencyAnalysisBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(AnalyticsViewModel.class);

        // Настройка диаграммы
        setupChart();

        // Наблюдение за данными
        observeData();
    }

    /**
     * Настройка диаграммы
     */
    private void setupChart() {
        BarChart chart = binding.efficiencyChart;

        // Основные настройки
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // Настройка легенды
        chart.getLegend().setEnabled(true);

        // Настройка оси X
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Материалоемкость", "Трудоемкость"}));

        // Настройка левой оси Y
        chart.getAxisLeft().setDrawGridLines(true);
        chart.getAxisLeft().setAxisMinimum(0f);

        // Настройка правой оси Y
        chart.getAxisRight().setEnabled(false);

        // Анимация
        chart.animateY(1000);
    }

    /**
     * Наблюдение за данными из ViewModel
     */
    private void observeData() {
        // Наблюдение за материалоемкостью
        viewModel.getMaterialIntensity().observe(getViewLifecycleOwner(), materialIntensity -> {
            if (materialIntensity != null) {
                binding.materialIntensityValue.setText(CurrencyFormatter.formatPercentage(materialIntensity));
                updateChart();
            }
        });

        // Наблюдение за трудоемкостью
        viewModel.getLaborIntensity().observe(getViewLifecycleOwner(), laborIntensity -> {
            if (laborIntensity != null) {
                binding.laborIntensityValue.setText(CurrencyFormatter.formatPercentage(laborIntensity));
                updateChart();
            }
        });

        // Наблюдение за типом производства
        viewModel.getProductionType().observe(getViewLifecycleOwner(), type -> {
            if (type != null) {
                binding.productionTypeValue.setText(type);
            }
        });

        // Наблюдение за материальными затратами
        viewModel.getMaterialCosts().observe(getViewLifecycleOwner(), costs -> {
            if (costs != null) {
                binding.materialCostsValue.setText(CurrencyFormatter.format(costs));
            }
        });

        // Наблюдение за трудовыми затратами
        viewModel.getLaborCosts().observe(getViewLifecycleOwner(), costs -> {
            if (costs != null) {
                binding.laborCostsValue.setText(CurrencyFormatter.format(costs));
            }
        });

        // Наблюдение за выручкой от продукции
        viewModel.getProductRevenue().observe(getViewLifecycleOwner(), revenue -> {
            if (revenue != null) {
                binding.productRevenueValue.setText(CurrencyFormatter.format(revenue));
            }
        });
    }

    /**
     * Обновление данных диаграммы
     */
    private void updateChart() {
        Double materialIntensity = viewModel.getMaterialIntensity().getValue();
        Double laborIntensity = viewModel.getLaborIntensity().getValue();

        if (materialIntensity == null || laborIntensity == null) {
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, materialIntensity.floatValue()));
        entries.add(new BarEntry(1f, laborIntensity.floatValue()));

        BarDataSet dataSet = new BarDataSet(entries, "Показатели эффективности (%)");
        dataSet.setColors(getResources().getColor(R.color.material_intensity, null),
                getResources().getColor(R.color.labor_intensity, null));

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        binding.efficiencyChart.setData(data);
        binding.efficiencyChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
