package com.example.diplom.taxes;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diplom.R;
import com.example.diplom.database.entities.Tax;
import com.example.diplom.databinding.ActivityTaxDetailBinding;
import com.example.diplom.utils.DateUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

/**
 * Активность для создания и редактирования налогов
 */
public class TaxDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TAX_ID = "extra_tax_id";

    private ActivityTaxDetailBinding binding;
    private TaxViewModel viewModel;
    private int taxId = -1;
    private Date dueDate = new Date();
    private Date paymentDate = null;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaxDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Получение ID налога из Intent (если есть)
        if (getIntent().hasExtra(EXTRA_TAX_ID)) {
            taxId = getIntent().getIntExtra(EXTRA_TAX_ID, -1);
            isEditMode = true;
        }

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(isEditMode ? R.string.edit_tax : R.string.new_tax);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(TaxViewModel.class);

        // Настройка спиннера для типа налога
        setupTypeSpinner();

        // Настройка спиннера для статуса налога
        setupStatusSpinner();

        // Настройка выбора дат
        setupDatePickers();

        // Если режим редактирования, загружаем данные налога
        if (isEditMode) {
            loadTaxData();
        }

        // Настройка кнопки сохранения
        binding.saveButton.setOnClickListener(v -> saveTax());
    }

    /**
     * Настройка спиннера для типа налога
     */
    private void setupTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tax_type_entries,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.taxTypeSpinner.setAdapter(adapter);
    }

    /**
     * Настройка спиннера для статуса налога
     */
    private void setupStatusSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tax_status_entries,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.taxStatusSpinner.setAdapter(adapter);
    }

    /**
     * Настройка выбора дат
     */
    private void setupDatePickers() {
        // Настройка выбора срока уплаты
        binding.taxDueDateEditText.setFocusable(false);
        binding.taxDueDateEditText.setClickable(true);
        binding.taxDueDateEditText.setText(DateUtils.formatDate(dueDate));

        binding.taxDueDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dueDate);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        dueDate = newDate.getTime();
                        binding.taxDueDateEditText.setText(DateUtils.formatDate(dueDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Настройка выбора даты оплаты
        binding.taxPaymentDateEditText.setFocusable(false);
        binding.taxPaymentDateEditText.setClickable(true);
        if (paymentDate != null) {
            binding.taxPaymentDateEditText.setText(DateUtils.formatDate(paymentDate));
        }

        binding.taxPaymentDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            if (paymentDate != null) {
                calendar.setTime(paymentDate);
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, dayOfMonth);
                        paymentDate = newDate.getTime();
                        binding.taxPaymentDateEditText.setText(DateUtils.formatDate(paymentDate));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Настройка кнопки очистки даты оплаты
        binding.clearPaymentDateButton.setOnClickListener(v -> {
            paymentDate = null;
            binding.taxPaymentDateEditText.setText("");
        });
    }

    /**
     * Загрузка данных налога для редактирования
     */
    private void loadTaxData() {
        viewModel.getTaxById(taxId).observe(this, tax -> {
            if (tax != null) {
                binding.taxNameEditText.setText(tax.getName());
                binding.taxAmountEditText.setText(String.valueOf(tax.getAmount()));
                binding.taxDescriptionEditText.setText(tax.getDescription());

                // Установка типа налога
                String[] taxTypes = getResources().getStringArray(R.array.tax_type_entries);
                for (int i = 0; i < taxTypes.length; i++) {
                    if (taxTypes[i].equals(tax.getType())) {
                        binding.taxTypeSpinner.setSelection(i);
                        break;
                    }
                }

                // Установка статуса налога
                String[] taxStatuses = getResources().getStringArray(R.array.tax_status_entries);
                for (int i = 0; i < taxStatuses.length; i++) {
                    if (taxStatuses[i].equals(tax.getStatus())) {
                        binding.taxStatusSpinner.setSelection(i);
                        break;
                    }
                }

                // Установка срока оплаты
                if (tax.getDueDate() != null) {
                    dueDate = tax.getDueDate();
                    binding.taxDueDateEditText.setText(DateUtils.formatDate(dueDate));
                }

                // Установка даты оплаты
                if (tax.getPaymentDate() != null) {
                    paymentDate = tax.getPaymentDate();
                    binding.taxPaymentDateEditText.setText(DateUtils.formatDate(paymentDate));
                } else {
                    binding.taxPaymentDateEditText.setText("");
                }
            }
        });
    }

    /**
     * Сохранение налога
     */
    private void saveTax() {
        // Проверка введенных данных
        if (binding.taxNameEditText.getText().toString().trim().isEmpty()) {
            binding.taxNameLayout.setError(getString(R.string.field_required));
            return;
        }

        if (binding.taxAmountEditText.getText().toString().trim().isEmpty()) {
            binding.taxAmountLayout.setError(getString(R.string.field_required));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(binding.taxAmountEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            binding.taxAmountLayout.setError(getString(R.string.invalid_amount));
            return;
        }

        // Создание или обновление объекта налога
        Tax tax;
        if (isEditMode) {
            // Получаем существующий налог
            tax = viewModel.getTaxById(taxId).getValue();
            if (tax == null) {
                tax = new Tax();
                tax.setCreatedAt(new Date());
            }
        } else {
            tax = new Tax();
            tax.setCreatedAt(new Date());
        }

        // Заполнение данных налога
        tax.setId(taxId);
        tax.setName(binding.taxNameEditText.getText().toString().trim());
        tax.setAmount(amount);
        tax.setType(binding.taxTypeSpinner.getSelectedItem().toString());
        tax.setStatus(binding.taxStatusSpinner.getSelectedItem().toString());
        tax.setDueDate(dueDate);
        tax.setPaymentDate(paymentDate);
        tax.setDescription(binding.taxDescriptionEditText.getText().toString().trim());

        // Сохранение налога
        if (isEditMode) {
            viewModel.update(tax);
        } else {
            viewModel.insert(tax);
        }

        // Уведомление пользователя об успешном сохранении
        Toast.makeText(this, R.string.tax_saved, Toast.LENGTH_SHORT).show();

        // Завершение активности
        finish();
    }

    /**
     * Удаление налога
     */
    private void deleteTax() {
        if (!isEditMode) {
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete_tax)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    viewModel.getTaxById(taxId).observe(this, tax -> {
                        if (tax != null) {
                            viewModel.delete(tax);
                            Toast.makeText(this, R.string.tax_deleted, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEditMode) {
            getMenuInflater().inflate(R.menu.menu_tax_detail, menu);
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
            deleteTax();
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
