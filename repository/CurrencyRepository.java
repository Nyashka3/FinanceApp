package com.example.diplom.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diplom.api.ApiClient;
import com.example.diplom.api.CurrencyApiService;
import com.example.diplom.api.models.CurrencyRate;
import com.example.diplom.database.AppDatabase;
import com.example.diplom.database.dao.CurrencyDao;
import com.example.diplom.database.entities.Currency;
import com.example.diplom.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Репозиторий для работы с курсами валют.
 * Обеспечивает получение данных как из локальной базы данных, так и из сетевого API.
 */
public class CurrencyRepository {
    private static final String TAG = CurrencyRepository.class.getSimpleName();
    private static final String PREF_CURRENCY_CACHE = "currency_cache";
    private static final String PREF_CURRENCY_LAST_UPDATE = "currency_last_update";
    private static final long CURRENCY_CACHE_EXPIRATION = 3600000; // 1 час

    private final CurrencyApiService apiService;
    private final CurrencyDao currencyDao;
    private final SharedPreferences preferences;
    private final MutableLiveData<List<Currency>> allCurrencies = new MutableLiveData<>();
    private final MutableLiveData<List<Currency>> popularCurrencies = new MutableLiveData<>();
    private final MutableLiveData<Currency> baseCurrency = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CurrencyRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        currencyDao = database.currencyDao();
        apiService = ApiClient.getCurrencyApiService();
        preferences = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE);

        // Загрузка данных из локальной базы данных
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Currency> currencies = currencyDao.getAllCurrenciesSync();
            allCurrencies.postValue(currencies);
            loadPopularCurrencies();
            loadBaseCurrency();
        });
    }

    /**
     * Загружает курсы валют из API и обновляет локальную базу данных
     */
    public void refreshCurrencyRates() {
        long lastUpdate = preferences.getLong(PREF_CURRENCY_LAST_UPDATE, 0);
        long currentTime = System.currentTimeMillis();

        // Проверяем, нужно ли обновлять данные из API
        if (currentTime - lastUpdate > CURRENCY_CACHE_EXPIRATION) {
            isLoading.setValue(true);
            apiService.getCurrentRates().enqueue(new Callback<List<CurrencyRate>>() {
                @Override
                public void onResponse(Call<List<CurrencyRate>> call, Response<List<CurrencyRate>> response) {
                    isLoading.setValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        // Сохраняем полученные данные в кэш и базу данных
                        List<CurrencyRate> currencyRates = response.body();
                        saveToCache(currencyRates);
                        saveToDatabase(currencyRates);
                    } else {
                        // Если ответ неуспешный, загружаем данные из кэша
                        errorMessage.setValue("Ошибка получения данных: " + response.message());
                        loadFromCache();
                    }
                }

                @Override
                public void onFailure(Call<List<CurrencyRate>> call, Throwable t) {
                    isLoading.setValue(false);
                    errorMessage.setValue("Ошибка подключения: " + t.getMessage());
                    // Загружаем данные из кэша при ошибке
                    loadFromCache();
                }
            });
        } else {
            // Данные свежие, загружаем из базы данных
            AppDatabase.databaseWriteExecutor.execute(() -> {
                List<Currency> currencies = currencyDao.getAllCurrenciesSync();
                allCurrencies.postValue(currencies);
                loadPopularCurrencies();
            });
        }
    }

    /**
     * Сохраняет курсы валют в кэш (SharedPreferences)
     * @param currencyRates список курсов валют
     */
    private void saveToCache(List<CurrencyRate> currencyRates) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        editor.putString(PREF_CURRENCY_CACHE, gson.toJson(currencyRates));
        editor.putLong(PREF_CURRENCY_LAST_UPDATE, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Загружает курсы валют из кэша
     */
    private void loadFromCache() {
        String cacheJson = preferences.getString(PREF_CURRENCY_CACHE, null);
        if (cacheJson != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<CurrencyRate>>() {}.getType();
            List<CurrencyRate> cachedRates = gson.fromJson(cacheJson, type);
            saveToDatabase(cachedRates);
        }
    }

    /**
     * Сохраняет курсы валют в локальную базу данных
     * @param currencyRates список курсов валют
     */
    private void saveToDatabase(List<CurrencyRate> currencyRates) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Currency> currencies = new ArrayList<>();

            for (CurrencyRate rate : currencyRates) {
                Currency currency = new Currency();
                currency.setCode(rate.getCode());
                currency.setName(rate.getName());
                currency.setRate(rate.getRate());
                currency.setBaseCurrency(rate.getBaseCurrency());
                currency.setTrend(rate.getTrend());
                currency.setChange(rate.getChange());
                currency.setChangePercentage(rate.getChangePercentage());
                currency.setIconUrl(rate.getIconUrl());
                currency.setUpdatedAt(DateUtils.parseDate(rate.getDate()));
                currencies.add(currency);
            }

            // Обновляем или вставляем валюты в базу данных
            for (Currency currency : currencies) {
                Currency existingCurrency = currencyDao.getCurrencyByCodeSync(currency.getCode());
                if (existingCurrency != null) {
                    currency.setId(existingCurrency.getId());
                    currencyDao.update(currency);
                } else {
                    currencyDao.insert(currency);
                }
            }

            // Загружаем обновленные данные
            List<Currency> updatedCurrencies = currencyDao.getAllCurrenciesSync();
            allCurrencies.postValue(updatedCurrencies);
            loadPopularCurrencies();
        });
    }

    /**
     * Загружает список популярных валют (RUB, USD, EUR)
     */
    private void loadPopularCurrencies() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Currency> popularCurrenciesList = new ArrayList<>();

            // Добавляем базовую валюту и популярные валюты в список
            Currency rubCurrency = currencyDao.getCurrencyByCodeSync("RUB");
            Currency usdCurrency = currencyDao.getCurrencyByCodeSync("USD");
            Currency eurCurrency = currencyDao.getCurrencyByCodeSync("EUR");

            if (rubCurrency != null) popularCurrenciesList.add(rubCurrency);
            if (usdCurrency != null) popularCurrenciesList.add(usdCurrency);
            if (eurCurrency != null) popularCurrenciesList.add(eurCurrency);

            popularCurrencies.postValue(popularCurrenciesList);
        });
    }

    /**
     * Загружает базовую валюту (обычно RUB)
     */
    private void loadBaseCurrency() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Currency currency = currencyDao.getCurrencyByCodeSync("RUB");
            baseCurrency.postValue(currency);
        });
    }

    /**
     * Получает валюту по коду
     * @param code код валюты
     * @return LiveData с валютой
     */
    public LiveData<Currency> getCurrencyByCode(String code) {
        MutableLiveData<Currency> result = new MutableLiveData<>();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Currency currency = currencyDao.getCurrencyByCodeSync(code);
            result.postValue(currency);
        });
        return result;
    }

    // Геттеры LiveData объектов
    public LiveData<List<Currency>> getAllCurrencies() {
        return allCurrencies;
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
}
