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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ViewModel для работы с валютами
 */
public class CurrencyViewModel extends AndroidViewModel {

    // Константы для сортировки
    public static final int SORT_CODE_ASC = 0;
    public static final int SORT_CODE_DESC = 1;
    public static final int SORT_RATE_ASC = 2;
    public static final int SORT_RATE_DESC = 3;

    private final CurrencyRepository repository;
    private final LiveData<List<Currency>> allCurrencies;
    private final LiveData<List<Currency>> popularCurrencies;
    private final LiveData<Currency> baseCurrency;
    private final LiveData<Boolean> isLoading;
    private final LiveData<String> errorMessage;

    private final MutableLiveData<String> searchQuery = new MutableLiveData<>("");
    private final MutableLiveData<Integer> sortOrder = new MutableLiveData<>(SORT_CODE_ASC);
    private final MutableLiveData<Double> minRate = new MutableLiveData<>(null);
    private final MutableLiveData<Double> maxRate = new MutableLiveData<>(null);
    private final MutableLiveData<String> codeFilter = new MutableLiveData<>("");

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

        // Настройка фильтрации по поисковому запросу и другим фильтрам
        filteredCurrencies.addSource(allCurrencies, currencies -> {
            applyFiltersAndSort();
        });

        filteredCurrencies.addSource(searchQuery, query -> {
            applyFiltersAndSort();
        });

        filteredCurrencies.addSource(sortOrder, order -> {
            applyFiltersAndSort();
        });

        filteredCurrencies.addSource(minRate, min -> {
            applyFiltersAndSort();
        });

        filteredCurrencies.addSource(maxRate, max -> {
            applyFiltersAndSort();
        });

        filteredCurrencies.addSource(codeFilter, code -> {
            applyFiltersAndSort();
        });

        // Обновление данных при создании ViewModel
        refreshCurrencyRates();
    }

    /**
     * Применяет фильтры и сортировку к списку валют
     */
    private void applyFiltersAndSort() {
        List<Currency> currencies = allCurrencies.getValue();
        if (currencies == null) return;

        List<Currency> result = new ArrayList<>(currencies);

        // Применение фильтра по коду
        String code = codeFilter.getValue();
        if (code != null && !code.isEmpty()) {
            String lowerCode = code.toLowerCase();
            result = result.stream()
                    .filter(currency ->
                            currency.getCode().toLowerCase().contains(lowerCode))
                    .collect(Collectors.toList());
        }

        // Применение фильтра по диапазону курса
        Double min = minRate.getValue();
        Double max = maxRate.getValue();

        if (min != null) {
            result = result.stream()
                    .filter(currency -> currency.getRate() >= min)
                    .collect(Collectors.toList());
        }

        if (max != null) {
            result = result.stream()
                    .filter(currency -> currency.getRate() <= max)
                    .collect(Collectors.toList());
        }

        // Применение поискового фильтра
        String query = searchQuery.getValue();
        if (query != null && !query.isEmpty()) {
            String lowerQuery = query.toLowerCase();
            result = result.stream()
                    .filter(currency ->
                            currency.getCode().toLowerCase().contains(lowerQuery) ||
                                    currency.getName().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }

        // Применение сортировки
        Integer order = sortOrder.getValue();
        if (order != null) {
            switch (order) {
                case SORT_CODE_ASC:
                    Collections.sort(result, Comparator.comparing(Currency::getCode));
                    break;
                case SORT_CODE_DESC:
                    Collections.sort(result, Comparator.comparing(Currency::getCode).reversed());
                    break;
                case SORT_RATE_ASC:
                    Collections.sort(result, Comparator.comparing(Currency::getRate));
                    break;
                case SORT_RATE_DESC:
                    Collections.sort(result, Comparator.comparing(Currency::getRate).reversed());
                    break;
            }
        }

        filteredCurrencies.setValue(result);
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
     * Устанавливает фильтр по коду валюты
     * @param code код валюты для фильтрации
     */
    public void setCodeFilter(String code) {
        codeFilter.setValue(code);
    }

    /**
     * Устанавливает минимальное значение курса для фильтрации
     * @param min минимальное значение
     */
    public void setMinRate(Double min) {
        minRate.setValue(min);
    }

    /**
     * Устанавливает максимальное значение курса для фильтрации
     * @param max максимальное значение
     */
    public void setMaxRate(Double max) {
        maxRate.setValue(max);
    }

    /**
     * Устанавливает порядок сортировки
     * @param order порядок сортировки
     */
    public void setSortOrder(int order) {
        sortOrder.setValue(order);
    }

    /**
     * Сбрасывает все фильтры
     */
    public void resetFilters() {
        codeFilter.setValue("");
        minRate.setValue(null);
        maxRate.setValue(null);
        searchQuery.setValue("");
        sortOrder.setValue(SORT_CODE_ASC);
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
     * @param fromCurrency исходная валюта
     * @param toCurrency целевая валюта
     * @return конвертированная сумма
     */
    public double convertCurrency(double amount, Currency fromCurrency, Currency toCurrency) {
        if (fromCurrency == null || toCurrency == null) {
            return 0;
        }

        // Проверяем, что базовая валюта одинаковая
        if (!fromCurrency.getBaseCurrency().equals(toCurrency.getBaseCurrency())) {
            // Если базовые валюты разные, нужно использовать кросс-курс
            // Преобразуем через соотношение их курсов к базовым валютам
            return amount * (toCurrency.getRate() / fromCurrency.getRate());
        }

        // Базовые валюты совпадают
        // Преобразуем сначала в базовую валюту, затем в целевую
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

    public LiveData<Integer> getSortOrder() {
        return sortOrder;
    }

    public LiveData<Double> getMinRate() {
        return minRate;
    }

    public LiveData<Double> getMaxRate() {
        return maxRate;
    }

    public LiveData<String> getCodeFilter() {
        return codeFilter;
    }
}