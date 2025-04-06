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
import com.example.diplom.database.entities.Currency;
import com.example.diplom.databinding.ActivityCurrencyBinding;
import com.google.android.material.snackbar.Snackbar;

/**
 * Активность для отображения списка валют и их курсов
 */
public class CurrencyActivity extends AppCompatActivity implements CurrencyAdapter.OnCurrencyClickListener {

    private ActivityCurrencyBinding binding;
    private CurrencyViewModel viewModel;
    private CurrencyAdapter adapter;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_currency, menu);

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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCurrencyClick(Currency currency) {
        // Открытие детальной информации о валюте
        Intent intent = new Intent(this, CurrencyDetailActivity.class);
        intent.putExtra(CurrencyDetailActivity.EXTRA_CURRENCY_CODE, currency.getCode());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
