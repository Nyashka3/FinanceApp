package com.example.diplom.expenses;

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
import com.example.diplom.database.entities.Expense;
import com.example.diplom.databinding.ActivityExpensesBinding;
import com.example.diplom.expenses.adapters.ExpenseAdapter;
import com.example.diplom.utils.CurrencyFormatter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Активность для управления расходами
 */
public class ExpensesActivity extends AppCompatActivity implements ExpenseAdapter.OnExpenseClickListener {

    private ActivityExpensesBinding binding;
    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpensesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.expenses);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Настройка RecyclerView
        setupRecyclerView();

        // Настройка фильтров
        setupFilters();

        // Настройка FAB
        binding.addExpenseFab.setOnClickListener(v -> {
            Intent intent = new Intent(ExpensesActivity.this, ExpenseDetailActivity.class);
            startActivity(intent);
        });

        // Наблюдение за данными
        observeData();
    }

    /**
     * Настройка RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new ExpenseAdapter();
        adapter.setOnExpenseClickListener(this);
        binding.expensesRecyclerView.setAdapter(adapter);
        binding.expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Настройка фильтров
     */
    private void setupFilters() {
        // Фильтр по категориям
        viewModel.getAllCategories().observe(this, categories -> {
            List<String> categoryNames = new ArrayList<>();
            categoryNames.add(getString(R.string.all_categories));

            for (Category category : categories) {
                if (category.isExpense()) {
                    categoryNames.add(category.getName());
                }
            }

            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    categoryNames
            );
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.categoryFilterSpinner.setAdapter(categoryAdapter);
        });

        binding.categoryFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = (String) parent.getItemAtPosition(position);
                if (getString(R.string.all_categories).equals(selectedCategory)) {
                    viewModel.setFilterCategory(null);
                } else {
                    viewModel.setFilterCategory(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.setFilterCategory(null);
            }
        });

        // Фильтр по датам
        binding.dateFilterButton.setOnClickListener(v -> showDateRangePicker());

        // Сброс фильтров
        binding.resetFiltersButton.setOnClickListener(v -> {
            binding.categoryFilterSpinner.setSelection(0);
            viewModel.resetFilters();
            Snackbar.make(binding.getRoot(), R.string.filters_reset, Snackbar.LENGTH_SHORT).show();
        });
    }

    /**
     * Показ диалога выбора периода дат
     */
    private void showDateRangePicker() {
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText(R.string.select_date);

        MaterialDatePicker<Long> picker = builder.build();
        picker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            Date selectedDate = calendar.getTime();

            // Устанавливаем день как начало периода
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startDate = calendar.getTime();

            // Конец дня как конец периода
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date endDate = calendar.getTime();

            viewModel.setFilterDateRange(startDate, endDate);
            binding.dateFilterButton.setText(getString(R.string.date_filter_active));
        });

        picker.show(getSupportFragmentManager(), picker.toString());
    }

    /**
     * Наблюдение за данными из ViewModel
     */
    private void observeData() {
        // Наблюдение за отфильтрованными расходами
        viewModel.getFilteredExpenses().observe(this, expenses -> {
            if (expenses != null && !expenses.isEmpty()) {
                binding.noExpensesText.setVisibility(View.GONE);
                binding.expensesRecyclerView.setVisibility(View.VISIBLE);
                adapter.submitList(expenses);

                // Обновление счетчика количества расходов
                binding.expensesCountText.setText(getString(R.string.expenses_count, expenses.size()));
            } else {
                binding.noExpensesText.setVisibility(View.VISIBLE);
                binding.expensesRecyclerView.setVisibility(View.GONE);
                binding.expensesCountText.setText(getString(R.string.expenses_count, 0));
            }
        });

        // Наблюдение за общей суммой расходов
        viewModel.getTotalFilteredExpenses().observe(this, total -> {
            if (total != null) {
                binding.totalAmountText.setText(CurrencyFormatter.format(total));
            } else {
                binding.totalAmountText.setText(CurrencyFormatter.format(0));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_expenses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_search) {
            // Показ поиска
            // Реализация будет в будущем
            return true;
        } else if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Показывает диалог для выбора сортировки
     */
    private void showSortDialog() {
        String[] sortOptions = {
                getString(R.string.sort_date_newest),
                getString(R.string.sort_date_oldest),
                getString(R.string.sort_amount_highest),
                getString(R.string.sort_amount_lowest),
                getString(R.string.sort_name_az),
                getString(R.string.sort_name_za)
        };

        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.sort_by)
                .setItems(sortOptions, (dialog, which) -> {
                    switch (which) {
                        case 0: // Сортировка по дате (новые сначала)
                            viewModel.setSortOrder(ExpenseViewModel.SORT_DATE_DESC);
                            break;
                        case 1: // Сортировка по дате (старые сначала)
                            viewModel.setSortOrder(ExpenseViewModel.SORT_DATE_ASC);
                            break;
                        case 2: // Сортировка по сумме (по убыванию)
                            viewModel.setSortOrder(ExpenseViewModel.SORT_AMOUNT_DESC);
                            break;
                        case 3: // Сортировка по сумме (по возрастанию)
                            viewModel.setSortOrder(ExpenseViewModel.SORT_AMOUNT_ASC);
                            break;
                        case 4: // Сортировка по названию (A-Z)
                            viewModel.setSortOrder(ExpenseViewModel.SORT_TITLE_ASC);
                            break;
                        case 5: // Сортировка по названию (Z-A)
                            viewModel.setSortOrder(ExpenseViewModel.SORT_TITLE_DESC);
                            break;
                    }
                })
                .show();
    }

    @Override
    public void onExpenseClick(Expense expense) {
        // Открытие детальной активности для редактирования
        Intent intent = new Intent(ExpensesActivity.this, ExpenseDetailActivity.class);
        intent.putExtra(ExpenseDetailActivity.EXTRA_EXPENSE_ID, expense.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
