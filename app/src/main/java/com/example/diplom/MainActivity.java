package com.example.diplom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.example.diplom.currency.CurrencyActivity;
import com.example.diplom.databinding.ActivityMainBinding;
import com.example.diplom.expenses.ExpensesActivity;
import com.example.diplom.settings.SettingsActivity;
import com.example.diplom.utils.ExitDialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Главная активность приложения
 */
public class MainActivity extends BaseLocaleActivity implements ExitDialogFragment.ExitDialogListener {

    private ActivityMainBinding binding;
    private MainMenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        setupRecyclerView();
        setupBackPressHandler();
    }

    /**
     * Настройка RecyclerView с элементами меню
     */
    private void setupRecyclerView() {
        List<MenuItemData> menuItems = createMenuItems();
        menuAdapter = new MainMenuAdapter(menuItems);
        binding.menuRecyclerView.setAdapter(menuAdapter);
    }

    /**
     * Создание списка элементов меню
     */
    private List<MenuItemData> createMenuItems() {
        List<MenuItemData> items = new ArrayList<>();

        items.add(new MenuItemData(
                R.drawable.ic_money,
                R.string.view_expenses,
                R.string.expenses_menu_description,
                () -> {
                    Intent intent = new Intent(MainActivity.this, ExpensesActivity.class);
                    startActivity(intent);
                }
        ));

        items.add(new MenuItemData(
                R.drawable.ic_currency,
                R.string.currency_rates,
                R.string.currency_menu_description,
                () -> {
                    Intent intent = new Intent(MainActivity.this, CurrencyActivity.class);
                    startActivity(intent);
                }
        ));

        items.add(new MenuItemData(
                R.drawable.ic_settings,
                R.string.settings,
                R.string.settings_menu_description,
                () -> {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
        ));

        return items;
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