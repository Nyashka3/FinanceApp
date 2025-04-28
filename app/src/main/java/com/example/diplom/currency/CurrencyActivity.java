package com.example.diplom.currency;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.diplom.R;
import com.example.diplom.currency.adapters.CurrencyAdapter;
import com.example.diplom.currency.CurrencyFilterDialogFragment;
import com.example.diplom.currency.CurrencySortDialogFragment;
import com.example.diplom.database.entities.Currency;
import com.example.diplom.databinding.ActivityCurrencyBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.snackbar.Snackbar;

/**
 * Активность для отображения списка валют и их курсов
 */
public class CurrencyActivity extends AppCompatActivity implements
        CurrencyAdapter.OnCurrencyClickListener,
        CurrencyFilterDialogFragment.FilterDialogListener,
        CurrencySortDialogFragment.SortDialogListener {

    private ActivityCurrencyBinding binding;
    private CurrencyViewModel viewModel;
    private CurrencyAdapter adapter;
    private MenuItem filterMenuItem;
    private BadgeDrawable filterBadge;
    private boolean isFilterActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCurrencyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.currency_rates);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);

        // Настройка RecyclerView
        setupRecyclerView();

        // Настройка SwipeRefreshLayout
        setupSwipeRefresh();

        // Наблюдение за данными
        observeData();
    }

    /**
     * Настройка RecyclerView
     */
    private void setupRecyclerView() {
        adapter = new CurrencyAdapter();
        adapter.setOnCurrencyClickListener(this);
        binding.currenciesRecyclerView.setAdapter(adapter);
        binding.currenciesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * Настройка SwipeRefreshLayout для обновления данных
     */
    private void setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.refreshCurrencyRates();
        });

        // Настройка цветов индикатора обновления
        binding.swipeRefreshLayout.setColorSchemeResources(
                R.color.primary,
                R.color.secondary,
                R.color.primary_variant
        );
    }

    /**
     * Наблюдение за данными из ViewModel
     */
    private void observeData() {
        // Наблюдение за отфильтрованными валютами
        viewModel.getFilteredCurrencies().observe(this, currencies -> {
            if (currencies != null && !currencies.isEmpty()) {
                binding.noCurrenciesText.setVisibility(View.GONE);
                binding.currenciesRecyclerView.setVisibility(View.VISIBLE);
                adapter.submitList(currencies);
            } else {
                if (viewModel.getIsLoading().getValue() != null && viewModel.getIsLoading().getValue()) {
                    // Если данные загружаются, не показываем сообщение об отсутствии валют
                    binding.noCurrenciesText.setVisibility(View.GONE);
                } else {
                    binding.noCurrenciesText.setVisibility(View.VISIBLE);
                    binding.currenciesRecyclerView.setVisibility(View.GONE);
                }
            }
        });

        // Наблюдение за состоянием загрузки
        viewModel.getIsLoading().observe(this, isLoading -> {
            binding.swipeRefreshLayout.setRefreshing(isLoading);
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

        // Наблюдение за фильтрами для обновления значка
        viewModel.getCodeFilter().observe(this, codeFilter -> {
            updateFilterBadge();
        });

        viewModel.getMinRate().observe(this, minRate -> {
            updateFilterBadge();
        });

        viewModel.getMaxRate().observe(this, maxRate -> {
            updateFilterBadge();
        });
    }

    /**
     * Обновляет значок фильтра в зависимости от активности фильтров
     */
    private void updateFilterBadge() {
        String codeFilter = viewModel.getCodeFilter().getValue();
        Double minRate = viewModel.getMinRate().getValue();
        Double maxRate = viewModel.getMaxRate().getValue();

        boolean hasActiveFilters = (codeFilter != null && !codeFilter.isEmpty()) ||
                minRate != null || maxRate != null;

        // Если состояние фильтра изменилось и меню уже создано
        if (hasActiveFilters != isFilterActive && filterMenuItem != null) {
            if (hasActiveFilters) {
                // Добавляем значок, что фильтры активны
                filterMenuItem.setIcon(R.drawable.ic_trend_up);
            } else {
                // Возвращаем обычный значок
                filterMenuItem.setIcon(R.drawable.ic_trend_down);
            }
            isFilterActive = hasActiveFilters;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_currency, menu);

        // Сохраняем ссылку на пункт меню фильтра
        filterMenuItem = menu.findItem(R.id.action_filter);

        // Обновляем значок фильтра при создании меню
        updateFilterBadge();

        // Настройка поиска
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_currency));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.setSearchQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.setSearchQuery(newText);
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_refresh) {
            viewModel.refreshCurrencyRates();
            return true;
        } else if (id == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (id == R.id.action_sort) {
            showSortDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Показывает диалог фильтрации валют
     */
    private void showFilterDialog() {
        CurrencyFilterDialogFragment dialog = CurrencyFilterDialogFragment.newInstance(
                viewModel.getCodeFilter().getValue(),
                viewModel.getMinRate().getValue(),
                viewModel.getMaxRate().getValue()
        );
        dialog.show(getSupportFragmentManager(), "CurrencyFilterDialog");
    }

    /**
     * Показывает диалог сортировки валют
     */
    private void showSortDialog() {
        CurrencySortDialogFragment dialog = CurrencySortDialogFragment.newInstance(
                viewModel.getSortOrder().getValue()
        );
        dialog.show(getSupportFragmentManager(), "CurrencySortDialog");
    }

    @Override
    public void onCurrencyClick(Currency currency) {
        // Открытие детальной информации о валюте
        Intent intent = new Intent(this, CurrencyDetailActivity.class);
        intent.putExtra(CurrencyDetailActivity.EXTRA_CURRENCY_CODE, currency.getCode());
        startActivity(intent);
    }

    /**
     * Обработка результата диалога фильтрации
     */
    @Override
    public void onFilterApplied(String codeFilter, Double minRate, Double maxRate) {
        viewModel.setCodeFilter(codeFilter);
        viewModel.setMinRate(minRate);
        viewModel.setMaxRate(maxRate);

        // Показываем сообщение, что фильтры применены
        if ((codeFilter != null && !codeFilter.isEmpty()) || minRate != null || maxRate != null) {
            Snackbar.make(binding.getRoot(), R.string.filters_active, Snackbar.LENGTH_SHORT).show();
        }
    }

    /**
     * Обработка сброса фильтров
     */
    @Override
    public void onFilterReset() {
        viewModel.resetFilters();
        Snackbar.make(binding.getRoot(), R.string.filters_reset, Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Обработка выбора способа сортировки
     */
    @Override
    public void onSortOrderSelected(int sortOrder) {
        viewModel.setSortOrder(sortOrder);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}