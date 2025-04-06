package com.example.diplom.analytics.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.diplom.R;
import com.example.diplom.analytics.AnalyticsViewModel;
import com.example.diplom.api.ApiClient;
import com.example.diplom.api.ApiService;
import com.example.diplom.api.models.TaxRate;
import com.example.diplom.databinding.FragmentTaxAnalysisBinding;
import com.example.diplom.taxes.adapters.TaxAdapter;
import com.example.diplom.taxes.TaxViewModel;
import com.example.diplom.database.entities.Tax;
import com.example.diplom.utils.CurrencyFormatter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Фрагмент для отображения анализа налогов
 */
public class TaxAnalysisFragment extends Fragment {

    private FragmentTaxAnalysisBinding binding;
    private AnalyticsViewModel analyticsViewModel;
    private TaxViewModel taxViewModel;
    private TaxAdapter taxAdapter;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaxAnalysisBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация ViewModels
        analyticsViewModel = new ViewModelProvider(requireActivity()).get(AnalyticsViewModel.class);
        taxViewModel = new ViewModelProvider(this).get(TaxViewModel.class);

        // Инициализация API сервиса
        apiService = ApiClient.getApiService();

        // Настройка RecyclerView
        setupRecyclerView();

        // Настройка спиннера годов
        setupYearSpinner();

        // Наблюдение за данными
        observeData();

        // Загрузка данных с API
        loadTaxRates();
    }

    /**
     * Настройка RecyclerView
     */
    private void setupRecyclerView() {
        taxAdapter = new TaxAdapter();
        binding.upcomingTaxesRecyclerView.setAdapter(taxAdapter);
        binding.upcomingTaxesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    /**
     * Настройка спиннера для выбора года
     */
    private void setupYearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int i = currentYear - 2; i <= currentYear + 1; i++) {
            years.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.yearSpinner.setAdapter(adapter);
        binding.yearSpinner.setSelection(years.indexOf(String.valueOf(currentYear)));

        binding.yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = (String) parent.getItemAtPosition(position);
                loadTaxHistory(Integer.parseInt(selectedYear));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Не делаем ничего
            }
        });
    }

    /**
     * Наблюдение за данными из ViewModel
     */
    private void observeData() {
        // Наблюдение за предстоящими налогами
        taxViewModel.getUpcomingTaxes().observe(getViewLifecycleOwner(), taxes -> {
            if (taxes != null && !taxes.isEmpty()) {
                binding.noUpcomingTaxesText.setVisibility(View.GONE);
                binding.upcomingTaxesRecyclerView.setVisibility(View.VISIBLE);
                taxAdapter.submitList(taxes);
            } else {
                binding.noUpcomingTaxesText.setVisibility(View.VISIBLE);
                binding.upcomingTaxesRecyclerView.setVisibility(View.GONE);
            }
        });

        // Наблюдение за общей суммой налогов
        taxViewModel.getTotalTaxesForCurrentPeriod().observe(getViewLifecycleOwner(), total -> {
            if (total != null) {
                binding.totalTaxesValue.setText(CurrencyFormatter.format(total));
            } else {
                binding.totalTaxesValue.setText(CurrencyFormatter.format(0));
            }
        });
    }

    /**
     * Загрузка текущих налоговых ставок с API
     */
    private void loadTaxRates() {
        binding.taxRatesProgressBar.setVisibility(View.VISIBLE);
        binding.taxRatesErrorText.setVisibility(View.GONE);

        apiService.getTaxRates().enqueue(new Callback<List<TaxRate>>() {
            @Override
            public void onResponse(Call<List<TaxRate>> call, Response<List<TaxRate>> response) {
                binding.taxRatesProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    displayTaxRates(response.body());
                } else {
                    binding.taxRatesErrorText.setVisibility(View.VISIBLE);
                    binding.taxRatesErrorText.setText(R.string.error_loading_tax_rates);
                }
            }

            @Override
            public void onFailure(Call<List<TaxRate>> call, Throwable t) {
                binding.taxRatesProgressBar.setVisibility(View.GONE);
                binding.taxRatesErrorText.setVisibility(View.VISIBLE);
                binding.taxRatesErrorText.setText(getString(R.string.error_api_connection, t.getMessage()));

                // Если API недоступен, попробуем загрузить данные из локальной базы данных
                loadTaxRatesFromLocalDatabase();
            }
        });
    }

    /**
     * Загрузка налоговых ставок из локальной базы данных
     */
    private void loadTaxRatesFromLocalDatabase() {
        // Здесь должна быть логика загрузки из локальной базы данных
        // Это заглушка для примера
        Snackbar.make(binding.getRoot(), R.string.using_cached_data, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Отображение налоговых ставок
     * @param taxRates список налоговых ставок
     */
    private void displayTaxRates(List<TaxRate> taxRates) {
        if (taxRates.isEmpty()) {
            binding.taxRatesErrorText.setVisibility(View.VISIBLE);
            binding.taxRatesErrorText.setText(R.string.no_tax_rates_found);
            return;
        }

        StringBuilder ratesBuilder = new StringBuilder();
        ratesBuilder.append("Текущие налоговые ставки:\n\n");

        for (TaxRate rate : taxRates) {
            ratesBuilder.append(rate.getName())
                    .append(": ")
                    .append(rate.getRate())
                    .append("%");

            if (rate.getDescription() != null && !rate.getDescription().isEmpty()) {
                ratesBuilder.append(" (")
                        .append(rate.getDescription())
                        .append(")");
            }

            ratesBuilder.append("\n");
        }

        binding.currentTaxRatesText.setText(ratesBuilder.toString());
    }

    /**
     * Загрузка истории налоговых ставок с API
     * @param year год, за который нужно загрузить историю
     */
    private void loadTaxHistory(int year) {
        binding.taxHistoryProgressBar.setVisibility(View.VISIBLE);
        binding.taxHistoryErrorText.setVisibility(View.GONE);

        apiService.getTaxHistory(year).enqueue(new Callback<List<TaxRate>>() {
            @Override
            public void onResponse(Call<List<TaxRate>> call, Response<List<TaxRate>> response) {
                binding.taxHistoryProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    displayTaxHistory(response.body(), year);
                } else {
                    binding.taxHistoryErrorText.setVisibility(View.VISIBLE);
                    binding.taxHistoryErrorText.setText(R.string.error_loading_tax_history);
                }
            }

            @Override
            public void onFailure(Call<List<TaxRate>> call, Throwable t) {
                binding.taxHistoryProgressBar.setVisibility(View.GONE);
                binding.taxHistoryErrorText.setVisibility(View.VISIBLE);
                binding.taxHistoryErrorText.setText(getString(R.string.error_api_connection, t.getMessage()));
            }
        });
    }

    /**
     * Отображение истории налоговых ставок
     * @param taxRates список налоговых ставок
     * @param year год, за который отображается история
     */
    private void displayTaxHistory(List<TaxRate> taxRates, int year) {
        if (taxRates.isEmpty()) {
            binding.taxHistoryErrorText.setVisibility(View.VISIBLE);
            binding.taxHistoryErrorText.setText(getString(R.string.no_tax_history_found, year));
            return;
        }

        StringBuilder historyBuilder = new StringBuilder();
        historyBuilder.append("История налоговых ставок за ")
                .append(year)
                .append(" год:\n\n");

        // Группировка по налогам и сортировка по дате
        Map<String, List<TaxRate>> taxesByCode = new HashMap<>();
        for (TaxRate rate : taxRates) {
            if (!taxesByCode.containsKey(rate.getCode())) {
                taxesByCode.put(rate.getCode(), new ArrayList<>());
            }
            taxesByCode.get(rate.getCode()).add(rate);
        }

        for (Map.Entry<String, List<TaxRate>> entry : taxesByCode.entrySet()) {
            List<TaxRate> rates = entry.getValue();
            rates.sort((r1, r2) -> r1.getEffectiveDate().compareTo(r2.getEffectiveDate()));

            TaxRate firstRate = rates.get(0);
            historyBuilder.append(firstRate.getName())
                    .append(" (")
                    .append(firstRate.getCode())
                    .append("):\n");

            for (TaxRate rate : rates) {
                historyBuilder.append("  - ")
                        .append(rate.getEffectiveDate())
                        .append(": ")
                        .append(rate.getRate())
                        .append("%\n");
            }

            historyBuilder.append("\n");
        }

        binding.taxHistoryText.setText(historyBuilder.toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
