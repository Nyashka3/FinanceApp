package com.example.diplom.currency;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.diplom.R;
import com.example.diplom.database.entities.Currency;
import com.example.diplom.databinding.ActivityCurrencyDetailBinding;
import com.example.diplom.utils.DateUtils;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Активность для отображения детальной информации о валюте
 */
public class CurrencyDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENCY_CODE = "extra_currency_code";

    private ActivityCurrencyDetailBinding binding;
    private CurrencyViewModel viewModel;
    private String currencyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCurrencyDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Получение кода валюты из Intent
        currencyCode = getIntent().getStringExtra(EXTRA_CURRENCY_CODE);
        if (currencyCode == null) {
            Snackbar.make(binding.getRoot(), R.string.error_currency_not_found, Snackbar.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(currencyCode);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        // Настройка конвертера
        setupCurrencyConverter();

        // Наблюдение за данными
        observeData();

        // Обновление данных
        viewModel.refreshCurrencyRates();
    }

    /**
     * Настройка конвертера валют
     */
    private void setupCurrencyConverter() {
        binding.convertButton.setOnClickListener(v -> {
            try {
                double amount = Double.parseDouble(binding.amountInput.getText().toString());
                String fromCurrencyCode = binding.fromCurrencySpinner.getSelectedItem().toString();
                String toCurrencyCode = binding.toCurrencySpinner.getSelectedItem().toString();

                // Получение объектов валют
                viewModel.getCurrencyByCode(fromCurrencyCode).observe(this, fromCurrency -> {
                    viewModel.getCurrencyByCode(toCurrencyCode).observe(this, toCurrency -> {
                        if (fromCurrency != null && toCurrency != null) {
                            // Конвертация валюты
                            double result = viewModel.convertCurrency(amount, fromCurrency, toCurrency);

                            // Отображение результата
                            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
                            format.setMaximumFractionDigits(4);
                            binding.resultText.setText(format.format(result));
                        } else {
                            Snackbar.make(binding.getRoot(), R.string.error_currency_not_found, Snackbar.LENGTH_SHORT).show();
                        }
                    });
                });
            } catch (NumberFormatException e) {
                Snackbar.make(binding.getRoot(), R.string.error_invalid_amount, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Заполнение спиннеров валют
        viewModel.getAllCurrencies().observe(this, currencies -> {
            if (currencies != null && !currencies.isEmpty()) {
                String[] currencyCodes = new String[currencies.size()];
                for (int i = 0; i < currencies.size(); i++) {
                    currencyCodes[i] = currencies.get(i).getCode();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        currencyCodes
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                binding.fromCurrencySpinner.setAdapter(adapter);
                binding.toCurrencySpinner.setAdapter(adapter);

                // Устанавливаем выбранную валюту
                for (int i = 0; i < currencyCodes.length; i++) {
                    if (currencyCodes[i].equals(currencyCode)) {
                        binding.fromCurrencySpinner.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    /**
     * Наблюдение за данными валюты
     */
    private void observeData() {
        viewModel.getCurrencyByCode(currencyCode).observe(this, currency -> {
            if (currency != null) {
                updateUI(currency);
            } else {
                Snackbar.make(binding.getRoot(), R.string.error_currency_not_found, Snackbar.LENGTH_SHORT).show();
            }
        });

        // Наблюдение за состоянием загрузки
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
            }
        });

        // Наблюдение за ошибками
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Snackbar.make(binding.getRoot(), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Обновление UI данными о валюте
     * @param currency объект валюты
     */
    private void updateUI(Currency currency) {
        binding.currencyCodeText.setText(currency.getCode());
        binding.currencyNameText.setText(currency.getName());

        // Форматирование и отображение курса валюты
        NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
        format.setMaximumFractionDigits(4);
        binding.currencyRateText.setText(format.format(currency.getRate()));

        // Отображение изменения курса
        if (currency.getChange() > 0) {
            binding.currencyChangeText.setText("+" + format.format(currency.getChange()));
            binding.currencyChangeText.setTextColor(
                    ContextCompat.getColor(this, R.color.positive_amount));
            binding.trendIcon.setImageResource(R.drawable.ic_trend_up);
            binding.trendIcon.setImageTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.positive_amount)));
        } else if (currency.getChange() < 0) {
            binding.currencyChangeText.setText(format.format(currency.getChange()));
            binding.currencyChangeText.setTextColor(
                    ContextCompat.getColor(this, R.color.negative_amount));
            binding.trendIcon.setImageResource(R.drawable.ic_trend_down);
            binding.trendIcon.setImageTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.negative_amount)));
        } else {
            binding.currencyChangeText.setText(format.format(currency.getChange()));
            binding.currencyChangeText.setTextColor(
                    ContextCompat.getColor(this, R.color.neutral_amount));
            binding.trendIcon.setImageResource(R.drawable.ic_trend_neutral);
            binding.trendIcon.setImageTintList(
                    ColorStateList.valueOf(
                            ContextCompat.getColor(this, R.color.neutral_amount)));
        }

        // Отображение процентного изменения
        NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.getDefault());
        percentFormat.setMaximumFractionDigits(2);
        binding.currencyPercentText.setText(percentFormat.format(currency.getChangePercentage() / 100));

        // Отображение базовой валюты
        binding.baseCurrencyText.setText(getString(R.string.base_currency, currency.getBaseCurrency()));

        // Отображение даты обновления
        if (currency.getUpdatedAt() != null) {
            binding.lastUpdatedText.setText(getString(R.string.last_updated, DateUtils.formatDateTime(currency.getUpdatedAt())));
        }
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