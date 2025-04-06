package com.example.diplom.analytics;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.diplom.R;
import com.example.diplom.analytics.adapters.AnalyticsPagerAdapter;
import com.example.diplom.databinding.ActivityAnalyticsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Активность для отображения аналитической информации.
 */
public class AnalyticsActivity extends AppCompatActivity {

    private ActivityAnalyticsBinding binding;
    private AnalyticsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.analytics);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(AnalyticsViewModel.class);

        // Настройка ViewPager с фрагментами аналитики
        setupViewPager();

        // Наблюдение за данными
        observeData();
    }

    /**
     * Настройка ViewPager для отображения фрагментов аналитики
     */
    private void setupViewPager() {
        AnalyticsPagerAdapter adapter = new AnalyticsPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        // Связывание TabLayout с ViewPager
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(R.string.expenses_analysis);
                            break;
                        case 1:
                            tab.setText(R.string.efficiency_analysis);
                            break;
                        case 2:
                            tab.setText(R.string.tax_analysis);
                            break;
                    }
                }).attach();

        // Установка лимита предзагрузки страниц для лучшей производительности
        binding.viewPager.setOffscreenPageLimit(2);

        // Слушатель изменения страницы
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Можно добавить дополнительную логику при выборе страницы
            }
        });
    }

    /**
     * Настройка наблюдателей данных из ViewModel
     */
    private void observeData() {
        // Пример наблюдения за общими данными статистики
        viewModel.getTotalExpenses().observe(this, totalExpenses -> {
            // Обновление UI с общей суммой расходов
            binding.totalExpensesValue.setText(getString(R.string.amount_format, totalExpenses));
        });

        viewModel.getTotalIncome().observe(this, totalIncome -> {
            // Обновление UI с общей суммой доходов
            binding.totalIncomeValue.setText(getString(R.string.amount_format, totalIncome));
        });

        viewModel.getBalance().observe(this, balance -> {
            // Обновление UI с текущим балансом
            binding.balanceValue.setText(getString(R.string.amount_format, balance));

            // Настройка цвета в зависимости от баланса
            if (balance < 0) {
                binding.balanceValue.setTextColor(getResources().getColor(R.color.negative_amount, getTheme()));
            } else {
                binding.balanceValue.setTextColor(getResources().getColor(R.color.positive_amount, getTheme()));
            }
        });
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