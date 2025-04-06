package com.example.diplom.taxes;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.diplom.R;
import com.example.diplom.database.entities.Tax;
import com.example.diplom.databinding.ActivityTaxCalculatorBinding;
import com.example.diplom.utils.CurrencyFormatter;
import com.google.android.material.snackbar.Snackbar;

import java.util.Date;
import java.util.Map;

/**
 * Активность для расчета налогов
 */
public class TaxCalculatorActivity extends AppCompatActivity {

    private ActivityTaxCalculatorBinding binding;
    private TaxViewModel viewModel;
    private String selectedTaxType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaxCalculatorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.tax_calculator);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(TaxViewModel.class);

        // Настройка спиннера для выбора типа налога
        setupTaxTypeSpinner();

        // Настройка кнопки расчета
        binding.calculateButton.setOnClickListener(v -> calculateTax());

        // Начальное скрытие специфичных полей
        updateInputFieldsVisibility();

        // Настройка кнопки сохранения
        binding.saveTaxButton.setOnClickListener(v -> saveTaxToDatabase());
    }

    /**
     * Настройка спиннера для типа налога
     */
    private void setupTaxTypeSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.tax_type_entries,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.taxTypeSpinner.setAdapter(adapter);

        binding.taxTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTaxType = parent.getItemAtPosition(position).toString();
                updateInputFieldsVisibility();
                clearResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTaxType = "";
                updateInputFieldsVisibility();
            }
        });
    }

    /**
     * Обновление видимости полей в зависимости от выбранного типа налога
     */
    private void updateInputFieldsVisibility() {
        // Сброс видимости всех специфичных полей
        binding.incomeInputLayout.setVisibility(View.GONE);
        binding.expensesInputLayout.setVisibility(View.GONE);
        binding.salaryInputLayout.setVisibility(View.GONE);
        binding.periodInputLayout.setVisibility(View.GONE);
        binding.potentialIncomeInputLayout.setVisibility(View.GONE);

        // Установка видимости в зависимости от выбранного типа налога
        if (selectedTaxType.equals(getString(R.string.usn_income))) {
            // УСН "Доходы"
            binding.incomeInputLayout.setVisibility(View.VISIBLE);
        } else if (selectedTaxType.equals(getString(R.string.usn_income_expense))) {
            // УСН "Доходы минус расходы"
            binding.incomeInputLayout.setVisibility(View.VISIBLE);
            binding.expensesInputLayout.setVisibility(View.VISIBLE);
        } else if (selectedTaxType.equals(getString(R.string.patent))) {
            // Патентная система
            binding.potentialIncomeInputLayout.setVisibility(View.VISIBLE);
            binding.periodInputLayout.setVisibility(View.VISIBLE);
        } else if (selectedTaxType.equals(getString(R.string.income_tax)) ||
                selectedTaxType.equals(getString(R.string.insurance_contributions))) {
            // НДФЛ и страховые взносы
            binding.salaryInputLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Очистка результатов расчета
     */
    private void clearResults() {
        binding.resultCard.setVisibility(View.GONE);
        binding.resultText.setText("");
        binding.resultDetailsText.setText("");
    }

    /**
     * Расчет налога
     */
    private void calculateTax() {
        clearResults();

        try {
            double result = 0;
            StringBuilder details = new StringBuilder();

            if (selectedTaxType.equals(getString(R.string.usn_income))) {
                // УСН "Доходы"
                String incomeStr = binding.incomeInput.getText().toString();
                if (incomeStr.isEmpty()) {
                    binding.incomeInputLayout.setError(getString(R.string.field_required));
                    return;
                }

                double income = Double.parseDouble(incomeStr);
                result = viewModel.calculateUsnIncomeTax(income);

                details.append(getString(R.string.usn_income))
                        .append("\n")
                        .append(getString(R.string.total_income))
                        .append(": ")
                        .append(CurrencyFormatter.format(income))
                        .append("\n")
                        .append(getString(R.string.tax_rate))
                        .append(": 6%")
                        .append("\n");

            } else if (selectedTaxType.equals(getString(R.string.usn_income_expense))) {
                // УСН "Доходы минус расходы"
                String incomeStr = binding.incomeInput.getText().toString();
                String expensesStr = binding.expensesInput.getText().toString();

                if (incomeStr.isEmpty()) {
                    binding.incomeInputLayout.setError(getString(R.string.field_required));
                    return;
                }
                if (expensesStr.isEmpty()) {
                    binding.expensesInputLayout.setError(getString(R.string.field_required));
                    return;
                }

                double income = Double.parseDouble(incomeStr);
                double expenses = Double.parseDouble(expensesStr);
                result = viewModel.calculateUsnIncomeExpenseTax(income, expenses);

                double taxBase = Math.max(0, income - expenses);

                details.append(getString(R.string.usn_income_expense))
                        .append("\n")
                        .append(getString(R.string.total_income))
                        .append(": ")
                        .append(CurrencyFormatter.format(income))
                        .append("\n")
                        .append(getString(R.string.total_expenses))
                        .append(": ")
                        .append(CurrencyFormatter.format(expenses))
                        .append("\n")
                        .append(getString(R.string.tax_base))
                        .append(": ")
                        .append(CurrencyFormatter.format(taxBase))
                        .append("\n")
                        .append(getString(R.string.tax_rate))
                        .append(": 15%");

            } else if (selectedTaxType.equals(getString(R.string.patent))) {
                // Патентная система
                String potentialIncomeStr = binding.potentialIncomeInput.getText().toString();
                String periodStr = binding.periodInput.getText().toString();

                if (potentialIncomeStr.isEmpty()) {
                    binding.potentialIncomeInputLayout.setError(getString(R.string.field_required));
                    return;
                }
                if (periodStr.isEmpty()) {
                    binding.periodInputLayout.setError(getString(R.string.field_required));
                    return;
                }

                double potentialIncome = Double.parseDouble(potentialIncomeStr);
                int period = Integer.parseInt(periodStr);

                if (period < 1 || period > 12) {
                    binding.periodInputLayout.setError(getString(R.string.period_range_error));
                    return;
                }

                result = viewModel.calculatePatentTax(potentialIncome, period);

                details.append(getString(R.string.patent))
                        .append("\n")
                        .append(getString(R.string.potential_yearly_income))
                        .append(": ")
                        .append(CurrencyFormatter.format(potentialIncome))
                        .append("\n")
                        .append(getString(R.string.period_months))
                        .append(": ")
                        .append(period)
                        .append("\n")
                        .append(getString(R.string.tax_rate))
                        .append(": 6%");

            } else if (selectedTaxType.equals(getString(R.string.income_tax))) {
                // НДФЛ
                String salaryStr = binding.salaryInput.getText().toString();

                if (salaryStr.isEmpty()) {
                    binding.salaryInputLayout.setError(getString(R.string.field_required));
                    return;
                }

                double salary = Double.parseDouble(salaryStr);
                result = viewModel.calculateIncomeTax(salary);

                details.append(getString(R.string.income_tax))
                        .append("\n")
                        .append(getString(R.string.salary))
                        .append(": ")
                        .append(CurrencyFormatter.format(salary))
                        .append("\n")
                        .append(getString(R.string.tax_rate))
                        .append(": 13%")
                        .append("\n");

            } else if (selectedTaxType.equals(getString(R.string.insurance_contributions))) {
                // Страховые взносы
                String salaryStr = binding.salaryInput.getText().toString();

                if (salaryStr.isEmpty()) {
                    binding.salaryInputLayout.setError(getString(R.string.field_required));
                    return;
                }

                double salary = Double.parseDouble(salaryStr);
                Map<String, Double> contributions = viewModel.calculateInsuranceContributions(salary);

                // Общая сумма взносов
                result = contributions.get("Всего");

                details.append(getString(R.string.insurance_contributions))
                        .append("\n")
                        .append(getString(R.string.salary))
                        .append(": ")
                        .append(CurrencyFormatter.format(salary))
                        .append("\n\n");

                // Вывод детализации по фондам
                for (Map.Entry<String, Double> entry : contributions.entrySet()) {
                    if (!entry.getKey().equals("Всего")) {
                        details.append(entry.getKey())
                                .append(": ")
                                .append(CurrencyFormatter.format(entry.getValue()))
                                .append("\n");
                    }
                }
            } else {
                Snackbar.make(binding.getRoot(), R.string.select_tax_type, Snackbar.LENGTH_SHORT).show();
                return;
            }

            // Отображение результата
            binding.resultCard.setVisibility(View.VISIBLE);
            binding.resultText.setText(CurrencyFormatter.format(result));
            binding.resultDetailsText.setText(details.toString());

            // Сохранение результата для возможного сохранения в БД
            binding.saveTaxButton.setTag(result);
            binding.saveTaxButton.setEnabled(true);

        } catch (NumberFormatException e) {
            Snackbar.make(binding.getRoot(), R.string.invalid_amount, Snackbar.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Сохранение рассчитанного налога в базу данных
     */
    private void saveTaxToDatabase() {
        Object resultObj = binding.saveTaxButton.getTag();
        if (resultObj == null) {
            return;
        }

        double amount = (double) resultObj;

        // Создание объекта налога
        Tax tax = new Tax();
        tax.setName(selectedTaxType);
        tax.setType(selectedTaxType);
        tax.setAmount(amount);
        tax.setStatus(getString(R.string.status_upcoming));
        tax.setDueDate(new Date()); // По умолчанию сегодня
        tax.setDescription(binding.resultDetailsText.getText().toString());
        tax.setCreatedAt(new Date());

        // Сохранение в базу данных
        viewModel.insert(tax);

        // Уведомление пользователя
        Toast.makeText(this, R.string.tax_saved, Toast.LENGTH_SHORT).show();

        // Деактивация кнопки, чтобы избежать повторного сохранения
        binding.saveTaxButton.setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
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
