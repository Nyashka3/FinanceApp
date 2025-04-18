package com.example.diplom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diplom.analytics.AnalyticsActivity;
import com.example.diplom.currency.CurrencyActivity;
import com.example.diplom.databinding.ActivityMainBinding;
import com.example.diplom.expenses.ExpensesActivity;
import com.example.diplom.settings.SettingsActivity;
import com.example.diplom.taxes.TaxActivity;
import com.example.diplom.utils.ExitDialogFragment;
import com.google.android.material.snackbar.Snackbar;

/**
 * Главная активность приложения
 */
/**
 * Главная активность приложения
 */
public class MainActivity extends AppCompatActivity implements ExitDialogFragment.ExitDialogListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        setupClickListeners();
        setupBackPressHandler();
    }

    /**
     * Настройка обработчика кнопки "Назад"
     */
    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });
    }

    /**
     * Настройка обработчиков кликов
     */
    private void setupClickListeners() {
        binding.viewExpensesButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ExpensesActivity.class);
            startActivity(intent);
        });

        binding.viewAnalyticsButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        binding.viewCurrencyButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, CurrencyActivity.class);
            startActivity(intent);
        });

        binding.viewTaxesButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TaxActivity.class);
            startActivity(intent);
        });

        binding.settingsButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        binding.addExpenseFab.setOnClickListener(view -> {
            // В будущем здесь может быть переход на экран добавления расхода
            Snackbar.make(view, R.string.add_expense_coming_soon, Snackbar.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Показывает диалог подтверждения выхода
     */
    private void showExitDialog() {
        ExitDialogFragment dialog = new ExitDialogFragment();
        dialog.show(getSupportFragmentManager(), "ExitDialog");
    }

    @Override
    public void onExitConfirmed() {
        finish();
    }

    @Override
    public void onExitCancelled() {
        // Ничего не делаем, просто закрываем диалог
    }
}