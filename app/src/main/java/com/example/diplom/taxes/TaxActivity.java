package com.example.diplom.taxes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.diplom.R;
import com.example.diplom.database.entities.Tax;
import com.example.diplom.databinding.ActivityTaxBinding;
import com.example.diplom.taxes.adapters.TaxAdapter;
import com.example.diplom.utils.CurrencyFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

/**
 * Активность для управления налогами
 */
public class TaxActivity extends AppCompatActivity implements TaxAdapter.OnTaxClickListener {

    private ActivityTaxBinding binding;
    private TaxViewModel viewModel;
    private TaxAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.taxes);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(TaxViewModel.class);

        // Настройка RecyclerView
        setupRecyclerView();

        // Настройка фильтров
        setupFilters();

        // Настройка FAB
        binding.addTaxFab.setOnClickListener(v -> {
            Intent intent = new Intent(TaxActivity.this, TaxDetailActivity.class);
            startActivity(intent);
        });

        // Наблюдение за данными
        observeData();
    }

    /**
     * Настройка RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new TaxAdapter();
        adapter.setOnTaxClickListener(this);
        binding.taxesRecyclerView.setAdapter(adapter);
        binding.taxesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Настройка фильтров
     */
    private void setupFilters() {
        // Фильтр по типу налога
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tax_type_entries,
                android.R.layout.simple_spinner_item
        );
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.typeFilterSpinner.setAdapter(typeAdapter);

        // Добавляем "Все типы" в начало
        binding.typeFilterSpinner.setSelection(0);

        binding.typeFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) parent.getItemAtPosition(position);
                if (position == 0) {
                    viewModel.setFilterType("ALL");
                } else {
                    viewModel.setFilterType(selectedType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setFilterType("ALL");
            }
        });

        // Фильтр по статусу
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.tax_status_entries,
                android.R.layout.simple_spinner_item
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.statusFilterSpinner.setAdapter(statusAdapter);

        // Добавляем "Все статусы" в начало
        binding.statusFilterSpinner.setSelection(0);

        binding.statusFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = (String) parent.getItemAtPosition(position);
                if (position == 0) {
                    viewModel.setFilterStatus("ALL");
                } else {
                    viewModel.setFilterStatus(selectedStatus);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setFilterStatus("ALL");
            }
        });

        // Фильтр по датам
        binding.dateFilterButton.setOnClickListener(v -> showDateRangePicker());

        // Сброс фильтров
        binding.resetFiltersButton.setOnClickListener(v -> {
            binding.typeFilterSpinner.setSelection(0);
            binding.statusFilterSpinner.setSelection(0);
            viewModel.setFilterType("ALL");
            viewModel.setFilterStatus("ALL");
            viewModel.setCurrentDate(new Date());
            binding.dateFilterButton.setText(R.string.select_date);
            Snackbar.make(binding.getRoot(), R.string.filters_reset, Snackbar.LENGTH_SHORT).show();
        });
    }

    /**
     * Показ диалога выбора даты
     */
    private void showDateRangePicker() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.select_date)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            Date selectedDate = calendar.getTime();
            viewModel.setCurrentDate(selectedDate);
            binding.dateFilterButton.setText(getString(R.string.date_filter_active));
        });

        picker.show(getSupportFragmentManager(), picker.toString());
    }

    /**
     * Наблюдение за данными
     */
    private void observeData() {
        viewModel.getFilteredTaxes().observe(this, taxes -> {
            if (taxes != null && !taxes.isEmpty()) {
                binding.noTaxesText.setVisibility(View.GONE);
                binding.taxesRecyclerView.setVisibility(View.VISIBLE);
                adapter.submitList(taxes);

                // Обновление счетчика количества налогов
                binding.taxesCountText.setText(getString(R.string.taxes_count, taxes.size()));
            } else {
                binding.noTaxesText.setVisibility(View.VISIBLE);
                binding.taxesRecyclerView.setVisibility(View.GONE);
                binding.taxesCountText.setText(getString(R.string.taxes_count, 0));
            }
        });

        // Наблюдение за общей суммой налогов
        viewModel.getTotalTaxesForCurrentPeriod().observe(this, total -> {
            if (total != null) {
                binding.totalAmountText.setText(CurrencyFormatter.format(total));
            } else {
                binding.totalAmountText.setText(CurrencyFormatter.format(0));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_taxes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_calculate) {
            Intent intent = new Intent(this, TaxCalculatorActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTaxClick(Tax tax) {
        Intent intent = new Intent(this, TaxDetailActivity.class);
        intent.putExtra(TaxDetailActivity.EXTRA_TAX_ID, tax.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
