package com.example.diplom.expenses;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.diplom.BaseLocaleActivity;
import com.example.diplom.R;
import com.example.diplom.database.AppDatabase;
import com.example.diplom.database.dao.ExpenseDao;
import com.example.diplom.database.entities.Category;
import com.example.diplom.database.entities.Expense;
import com.example.diplom.databinding.ActivityExpensesBinding;
import com.example.diplom.expenses.adapters.ExpenseAdapter;
import com.example.diplom.utils.CurrencyFormatter;
import com.example.diplom.utils.QRReceiptParser;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Активность для управления расходами
 */
public class ExpensesActivity extends BaseLocaleActivity implements ExpenseAdapter.OnExpenseClickListener {

    private static final String TAG = "ExpensesActivity";

    private ActivityExpensesBinding binding;
    private ExpenseViewModel viewModel;
    private ExpenseAdapter adapter;
    private Map<Integer, Category> categoriesMap = new HashMap<>();
    private SearchView searchView;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
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

            // Инициализация сканера QR-кодов
            initQRScanner();

            // Добавление слушателя для кнопки сканирования
            binding.scanReceiptFab.setOnClickListener(v -> {
                checkCameraPermissionAndScan();
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: ", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Обновляем данные при возвращении на активность
            // (например, после редактирования или добавления расхода)
            if (viewModel != null) {
                viewModel.refreshData();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: ", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_expenses, menu);

            // Настройка поиска
            MenuItem searchItem = menu.findItem(R.id.action_search);
            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();

                if (searchView != null) {
                    searchView.setQueryHint(getString(R.string.search));
                    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            if (viewModel != null) {
                                viewModel.setFilterSearch(query);
                            }
                            searchView.clearFocus();
                            return true;
                        }

                        @Override
                        public boolean onQueryTextChange(String newText) {
                            if (viewModel != null) {
                                viewModel.setFilterSearch(newText);
                            }
                            return true;
                        }
                    });

                    // Обработчик закрытия поиска
                    searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return true;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            if (viewModel != null) {
                                viewModel.setFilterSearch("");
                            }
                            return true;
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateOptionsMenu: ", e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        try {
            int id = item.getItemId();

            if (id == android.R.id.home) {
                onBackPressed();
                return true;
            } else if (id == R.id.action_search) {
                return true;
            } else if (id == R.id.action_sort) {
                showSortDialog();
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onOptionsItemSelected: ", e);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Настройка RecyclerView
     */
    private void setupRecyclerView() {
        try {
            adapter = new ExpenseAdapter();
            adapter.setOnExpenseClickListener(this);
            binding.expensesRecyclerView.setAdapter(adapter);
            binding.expensesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            Log.e(TAG, "Error in setupRecyclerView: ", e);
        }
    }

    /**
     * Настройка фильтров
     */
    private void setupFilters() {
        try {
            // Фильтр по категориям
            viewModel.getAllCategories().observe(this, categories -> {
                try {
                    if (categories != null) {
                        // Обновляем карту категорий
                        categoriesMap.clear();
                        for (Category category : categories) {
                            categoriesMap.put(category.getId(), category);
                        }

                        // Передаем категории в адаптер для отображения
                        if (adapter != null) {
                            adapter.setCategoriesMap(categoriesMap);
                        }

                        // Заполняем спиннер категорий для фильтрации
                        List<String> categoryNames = new ArrayList<>();
                        categoryNames.add(getString(R.string.all_categories));

                        // Фильтруем только категории расходов
                        List<Category> expenseCategories = new ArrayList<>();
                        for (Category category : categories) {
                            if (category.isExpense()) {
                                expenseCategories.add(category);
                            }
                        }

                        // Добавляем названия категорий в список
                        for (Category category : expenseCategories) {
                            categoryNames.add(category.getName());
                        }

                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                categoryNames
                        );
                        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        binding.categoryFilterSpinner.setAdapter(categoryAdapter);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing categories: ", e);
                }
            });

            // Обработчик выбора категории
            binding.categoryFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (parent != null && parent.getItemAtPosition(position) != null) {
                            String selectedCategory = (String) parent.getItemAtPosition(position);
                            if (getString(R.string.all_categories).equals(selectedCategory)) {
                                viewModel.setFilterCategory(null);
                            } else {
                                viewModel.setFilterCategory(selectedCategory);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in category selection: ", e);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    try {
                        viewModel.setFilterCategory(null);
                    } catch (Exception e) {
                        Log.e(TAG, "Error in onNothingSelected: ", e);
                    }
                }
            });

            // Фильтр по датам
            binding.dateFilterButton.setOnClickListener(v -> {
                try {
                    showDateRangePicker();
                } catch (Exception e) {
                    Log.e(TAG, "Error showing date picker: ", e);
                }
            });

            // Сброс фильтров
            binding.resetFiltersButton.setOnClickListener(v -> {
                try {
                    binding.categoryFilterSpinner.setSelection(0);
                    viewModel.resetFilters();
                    if (searchView != null) {
                        searchView.setQuery("", false);
                        searchView.clearFocus();
                        searchView.onActionViewCollapsed();
                    }
                    Snackbar.make(binding.getRoot(), R.string.filters_reset, Snackbar.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "Error resetting filters: ", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in setupFilters: ", e);
        }
    }

    /**
     * Показывает диалог выбора периода дат
     */
    private void showDateRangePicker() {
        try {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText(R.string.select_date);

            MaterialDatePicker<Long> picker = builder.build();
            picker.addOnPositiveButtonClickListener(selection -> {
                try {
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
                } catch (Exception e) {
                    Log.e(TAG, "Error processing date selection: ", e);
                }
            });

            picker.show(getSupportFragmentManager(), picker.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error in showDateRangePicker: ", e);
        }
    }

    /**
     * Наблюдение за данными из ViewModel
     */
    private void observeData() {
        try {
            // Наблюдение за отфильтрованными расходами
            viewModel.getFilteredExpenses().observe(this, expenses -> {
                try {
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
                } catch (Exception e) {
                    Log.e(TAG, "Error updating expenses list: ", e);
                }
            });

            // Наблюдение за общей суммой расходов
            viewModel.getTotalFilteredExpenses().observe(this, total -> {
                try {
                    if (total != null) {
                        binding.totalAmountText.setText(CurrencyFormatter.format(total));
                    } else {
                        binding.totalAmountText.setText(CurrencyFormatter.format(0));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating total amount: ", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in observeData: ", e);
        }
    }

    /**
     * Показывает диалог для выбора сортировки
     */
    private void showSortDialog() {
        try {
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
                        try {
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
                        } catch (Exception e) {
                            Log.e(TAG, "Error applying sort: ", e);
                        }
                    })
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "Error in showSortDialog: ", e);
        }
    }

    @Override
    public void onExpenseClick(Expense expense) {
        try {
            // Открытие детальной активности для редактирования
            if (expense != null) {
                Intent intent = new Intent(ExpensesActivity.this, ExpenseDetailActivity.class);
                intent.putExtra(ExpenseDetailActivity.EXTRA_EXPENSE_ID, expense.getId());
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onExpenseClick: ", e);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            binding = null;
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroy: ", e);
        }
    }

    /**
     * Инициализирует сканер QR-кодов
     */
    private void initQRScanner() {
        // Регистрация launcher для результата сканирования
        barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    if (result.getContents() == null) {
                        Snackbar.make(binding.getRoot(), "Сканирование отменено", Snackbar.LENGTH_SHORT).show();
                    } else {
                        // Обработка результата сканирования
                        handleQRResult(result.getContents());
                    }
                });

        // Регистрация launcher для запроса разрешения камеры
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startQRScanning();
                    } else {
                        Snackbar.make(binding.getRoot(),
                                "Для сканирования QR-кода необходимо разрешение камеры",
                                Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Проверяет разрешение камеры и запускает сканирование
     */
    private void checkCameraPermissionAndScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startQRScanning();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Запускает сканирование QR-кода
     */
    private void startQRScanning() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Отсканируйте QR-код чека");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    /**
     * Обрабатывает результат сканирования QR-кода
     * @param qrContent содержимое QR-кода
     */
    private void handleQRResult(String qrContent) {
        // Извлекаем сумму из QR-кода
        Double amount = QRReceiptParser.extractAmount(qrContent);

        if (amount != null) {
            // Создаем новый расход
            Expense expense = new Expense();
            expense.setTitle("Из чека");
            expense.setAmount(amount);
            expense.setCreatedAt(new Date());
            expense.setDescription("Отсканировано из QR-кода чека");
            var date = QRReceiptParser.extractDateTime(qrContent);
            if (date != null)
                expense.setExpenseDate(date);
            else
                expense.setExpenseDate(new Date());

            // Сохраняем расход в базу данных
            AppDatabase.databaseWriteExecutor.execute(() -> {
                ExpenseDao expenseDao = AppDatabase.getDatabase(this).expenseDao();
                expenseDao.insert(expense);

                runOnUiThread(() -> {
                    Snackbar.make(binding.getRoot(),
                            String.format("Добавлен расход: %.2f ₽", amount),
                            Snackbar.LENGTH_LONG).show();
                });
            });
        } else {
            Snackbar.make(binding.getRoot(),
                    "Не удалось извлечь сумму из QR-кода",
                    Snackbar.LENGTH_LONG).show();
        }
    }

}