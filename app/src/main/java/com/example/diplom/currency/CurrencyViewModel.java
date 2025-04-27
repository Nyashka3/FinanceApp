package com.example.diplom.currency;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.diplom.database.entities.Currency;
import com.example.diplom.repository.CurrencyRepository;
import com.example.diplom.utils.PreferenceUtils;

import java.util.List;

/**
 * ViewModel для работы с валютами
 */
public class CurrencyViewModel extends AndroidViewModel {

    private final CurrencyRepository repository;
    private final LiveData<List<Currency>> allCurrencies;
    private final LiveData<List<Currency>> popularCurrencies;
    private final LiveData<Currency> baseCurrency;
    private final LiveData<Boolean> isLoading;
    private final LiveData<String> errorMessage;

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MediatorLiveData<List<Currency>> filteredCurrencies = new MediatorLiveData<>();

    public CurrencyViewModel(@NonNull Application application) {
        super(application);

        // Инициализация репозитория
        repository = new CurrencyRepository(application);

        // Получение LiveData из репозитория
        allCurrencies = repository.getAllCurrencies();
        popularCurrencies = repository.getPopularCurrencies();
        baseCurrency = repository.getBaseCurrency();
        isLoading = repository.getIsLoading();
        errorMessage = repository.getErrorMessage();

        // Настройка фильтрации по поисковому запросу
        filteredCurrencies.addSource(allCurrencies, currencies -> {
            filterCurrencies(searchQuery.getValue(), currencies);
        });

        filteredCurrencies.addSource(searchQuery, query -> {
            filterCurrencies(query, allCurrencies.getValue());
        });

        // Обновление данных при создании ViewModel
        refreshCurrencyRates();
    }

    /**
     * Фильтрует список валют по поисковому запросу
     * @param query поисковый запрос
     * @param currencies исходный список валют
     */
    private void filterCurrencies(String query, List<Currency> currencies) {
        if (currencies == null) return;

        if (query == null || query.isEmpty()) {
            filteredCurrencies.setValue(currencies);
            return;
        }

        // Фильтрация списка валют по коду или названию
        List<Currency> filtered = currencies.stream()
                .filter(currency ->
                        currency.getCode().toLowerCase().contains(query.toLowerCase()) ||
                                currency.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());

        filteredCurrencies.setValue(filtered);
    }

    /**
     * Обновление курсов валют
     */
    public void refreshCurrencyRates() {
        repository.refreshCurrencyRates();
    }

    /**
     * Устанавливает поисковый запрос для фильтрации
     * @param query поисковый запрос
     */
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    /**
     * Получает валюту по коду
     * @param code код валюты
     * @return LiveData с валютой
     */
    public LiveData<Currency> getCurrencyByCode(String code) {
        return repository.getCurrencyByCode(code);
    }

    /**
     * Конвертирует сумму из одной валюты в другую
     * @param amount сумма для конвертации
     * @param fromCurrency код исходной валюты
     * @param toCurrency код целевой валюты
     * @return конвертированная сумма
     */
    public double convertCurrency(double amount, Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency == null || toCurrency == null) {
            return 0;
        }

        // Проверяем, что базовая валюта одинаковая
        if (!fromCurrency.getBaseCurrency().equals(toCurrency.getBaseCurrency())) {
            return 0;
        }

        // Конвертация: сначала переводим в базовую валюту, затем в целевую
        double amountInBaseCurrency = amount / fromCurrency.getRate();
        return amountInBaseCurrency * toCurrency.getRate();
    }

    // Геттеры для LiveData
    public LiveData<List<Currency>> getAllCurrencies() {
        return allCurrencies;
    }

    public LiveData<List<Currency>> getFilteredCurrencies() {
        return filteredCurrencies;
    }

    public LiveData<List<Currency>> getPopularCurrencies() {
        return popularCurrencies;
    }

    public LiveData<Currency> getBaseCurrency() {
        return baseCurrency;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }
}
