package com.example.diplom.expenses;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.diplom.R;
import com.example.diplom.database.entities.Category;
import com.example.diplom.database.entities.Expense;
import com.example.diplom.databinding.ActivityExpenseDetailBinding;
import com.example.diplom.utils.DateUtils;
import com.example.diplom.utils.ExpenseCategoryUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Активность для создания и редактирования расходов
 */
public class ExpenseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";

    private ActivityExpenseDetailBinding binding;
    private ExpenseViewModel viewModel;
    private int expenseId = -1;
    private Date selectedDate = new Date();
    private List<Category> categories;
    private boolean isEditMode = false;
    private boolean isAutoSelectingCheckboxes = false;
    private Expense currentExpense = null; // Добавляем поле для хранения текущего расхода

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExpenseDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Получение ID расхода из Intent (если есть)
        if (getIntent().hasExtra(EXTRA_EXPENSE_ID)) {
            expenseId = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
            isEditMode = true;
        }

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? R.string.edit_expense : R.string.new_expense);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);

        // Настройка выбора даты
        setupDatePicker();

        // Настройка категорий
        loadCategories();

        // Если режим редактирования, загружаем данные расхода
        if (isEditMode) {
            loadExpenseData();
        }

        // Настройка кнопки сохранения
        binding.saveButton.setOnClickListener(v -> saveExpense());
    }

    /**
     * Настройка выбора даты
     */
    private void setupDatePicker() {
        binding.expenseDateEditText.setFocusable(false);
        binding.expenseDateEditText.setClickable(true);
        binding.expenseDateEditText.setText(DateUtils.formatDate(selectedDate));

        binding.expenseDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        selectedDate = newDate.getTime();
                        binding.expenseDateEditText.setText(DateUtils.formatDate(selectedDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    /**
     * Загрузка списка категорий
     */
    private void loadCategories() {
        viewModel.getAllCategories().observe(this, categoryList -> {
            if (categoryList != null) {
                // Фильтруем только категории расходов
                categories = categoryList.stream()
                        .filter(Category::isExpense)
                        .collect(java.util.stream.Collectors.toList());

                // Создаем адаптер для спиннера категорий
                String[] categoryNames = new String[categories.size()];
                for (int i = 0; i < categories.size(); i++) {
                    categoryNames[i] = categories.get(i).getName();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        categoryNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.categorySpinner.setAdapter(adapter);

                // Устанавливаем слушатель выбора категории
                binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!isAutoSelectingCheckboxes) {
                            setCheckBoxesBasedOnCategory(categories.get(position).getName());
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Ничего не делаем
                    }
                });

                // Если мы в режиме редактирования и расход уже загружен, выбираем нужную категорию
                if (isEditMode && binding.categorySpinner.getTag() != null) {
                    int categoryId = (int) binding.categorySpinner.getTag();
                    for (int i = 0; i < categories.size(); i++) {
                        if (categories.get(i).getId() == categoryId) {
                            isAutoSelectingCheckboxes = true;
                            binding.categorySpinner.setSelection(i);
                            isAutoSelectingCheckboxes = false;
                            break;
                        }
                    }
                    binding.categorySpinner.setTag(null);
                }
            }
        });
    }

    /**
     * Устанавливает флажки в зависимости от выбранной категории
     * @param categoryName название категории
     */
    private void setCheckBoxesBasedOnCategory(String categoryName) {
        // Если пользователь уже установил флажки вручную, не изменяем их автоматически
        if (isAutoSelectingCheckboxes) return;

        // Отмечаем, что изменения происходят автоматически
        isAutoSelectingCheckboxes = true;

        // Сбрасываем все флажки
        binding.materialCostCheckBox.setChecked(false);
        binding.laborCostCheckBox.setChecked(false);
        binding.capitalCostCheckBox.setChecked(false);
        binding.energyCostCheckBox.setChecked(false);

        // Устанавливаем флажки в зависимости от категории
        if (ExpenseCategoryUtils.isMaterialIntensive(categoryName)) {
            binding.materialCostCheckBox.setChecked(true);
        } else if (ExpenseCategoryUtils.isLaborIntensive(categoryName)) {
            binding.laborCostCheckBox.setChecked(true);
        } else if (ExpenseCategoryUtils.isCapitalIntensive(categoryName)) {
            binding.capitalCostCheckBox.setChecked(true);
        } else if (ExpenseCategoryUtils.isEnergyIntensive(categoryName)) {
            binding.energyCostCheckBox.setChecked(true);
        }

        // Завершаем автоматическое изменение
        isAutoSelectingCheckboxes = false;
    }

    /**
     * Загрузка данных расхода для редактирования
     */
    private void loadExpenseData() {
        viewModel.getExpenseById(expenseId).observe(this, new Observer<Expense>() {
            @Override
            public void onChanged(Expense expense) {
                if (expense != null) {
                    // Сохраняем текущий расход
                    currentExpense = expense;

                    binding.expenseTitleEditText.setText(expense.getTitle());
                    binding.expenseAmountEditText.setText(String.valueOf(expense.getAmount()));
                    binding.expenseDescriptionEditText.setText(expense.getDescription());

                    // Установка даты
                    if (expense.getExpenseDate() != null) {
                        selectedDate = expense.getExpenseDate();
                        binding.expenseDateEditText.setText(DateUtils.formatDate(selectedDate));
                    }

                    // Установка категории (сохраняем ID в теге спиннера)
                    if (expense.getCategoryId() != null) {
                        binding.categorySpinner.setTag(expense.getCategoryId());

                        // Если категории уже загружены, выбираем нужную
                        if (categories != null && !categories.isEmpty()) {
                            for (int i = 0; i < categories.size(); i++) {
                                if (categories.get(i).getId() == expense.getCategoryId()) {
                                    isAutoSelectingCheckboxes = true;
                                    binding.categorySpinner.setSelection(i);
                                    isAutoSelectingCheckboxes = false;
                                    break;
                                }
                            }
                        }
                    }

                    // Установка чекбоксов типов затрат
                    isAutoSelectingCheckboxes = true;
                    binding.materialCostCheckBox.setChecked(expense.isMaterialCost());
                    binding.laborCostCheckBox.setChecked(expense.isLaborCost());
                    binding.capitalCostCheckBox.setChecked(expense.isCapitalCost());
                    binding.energyCostCheckBox.setChecked(expense.isEnergyCost());
                    isAutoSelectingCheckboxes = false;
                }
            }
        });
    }

    /**
     * Сохранение расхода
     */
    private void saveExpense() {
        // Проверка введенных данных
        if (binding.expenseTitleEditText.getText().toString().trim().isEmpty()) {
            binding.expenseTitleLayout.setError(getString(R.string.field_required));
            return;
        }

        if (binding.expenseAmountEditText.getText().toString().trim().isEmpty()) {
            binding.expenseAmountLayout.setError(getString(R.string.field_required));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(binding.expenseAmountEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            binding.expenseAmountLayout.setError(getString(R.string.invalid_amount));
            return;
        }

        // Получение выбранной категории
        if (binding.categorySpinner.getSelectedItemPosition() == -1 || categories == null || categories.isEmpty()) {
            Snackbar.make(binding.getRoot(), R.string.category_required, Snackbar.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = categories.get(binding.categorySpinner.getSelectedItemPosition());

        // Создание или обновление объекта расхода
        Expense expense;
        if (isEditMode && currentExpense != null) {
            // Используем текущий расход, который у нас уже есть
            expense = currentExpense;
        } else {
            expense = new Expense();
            expense.setCreatedAt(new Date());
        }

        // Заполнение данных расхода
        expense.setTitle(binding.expenseTitleEditText.getText().toString().trim());
        expense.setAmount(amount);
        expense.setDescription(binding.expenseDescriptionEditText.getText().toString().trim());
        expense.setExpenseDate(selectedDate);
        expense.setCategoryId(selectedCategory.getId());

        // Установка флагов типов затрат, соответствующих типам предприятий
        expense.setMaterialCost(binding.materialCostCheckBox.isChecked());
        expense.setLaborCost(binding.laborCostCheckBox.isChecked());
        expense.setCapitalCost(binding.capitalCostCheckBox.isChecked());
        expense.setEnergyCost(binding.energyCostCheckBox.isChecked());

        // Сохранение расхода
        if (isEditMode) {
            viewModel.update(expense);
        } else {
            viewModel.insert(expense);
        }

        // Уведомление пользователя об успешном сохранении
        Toast.makeText(this, R.string.expense_saved, Toast.LENGTH_SHORT).show();

        // Завершение активности
        finish();
    }

    /**
     * Удаление расхода
     */
    private void deleteExpense() {
        if (!isEditMode) {
            return;
        }

        // Проверяем, что у нас есть текущий расход
        if (currentExpense == null) {
            Toast.makeText(this, "Не удалось удалить расход: данные не загружены", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete_expense)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.delete(currentExpense);
                    Toast.makeText(this, R.string.expense_deleted, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEditMode) {
            getMenuInflater().inflate(R.menu.menu_expense_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_delete) {
            deleteExpense();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}