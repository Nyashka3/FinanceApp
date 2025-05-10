package com.example.diplom.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diplom.api.ApiClient;
import com.example.diplom.api.ApiService;
import com.example.diplom.api.CurrencyApiService;
import com.example.diplom.api.models.CurrencyRate;
import com.example.diplom.database.AppDatabase;
import com.example.diplom.database.dao.CurrencyDao;
import com.example.diplom.database.entities.Currency;
import com.example.diplom.utils.DateUtils;
import com.example.diplom.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Репозиторий для работы с курсами валют.
 */
public class CurrencyRepository {
    private static final String TAG = CurrencyRepository.class.getSimpleName();
    private static final String PREF_CURRENCY_CACHE = "currency_cache";
    private static final String PREF_CURRENCY_LAST_UPDATE = "currency_last_update";
    private static final long CURRENCY_CACHE_EXPIRATION = 60000; // 1 min

    private final Context context;

    private final CurrencyApiService apiService;
    private final CurrencyDao currencyDao;
    private final SharedPreferences preferences;
    private final MutableLiveData<List<Currency>> allCurrencies = new MutableLiveData<>();
    private final MutableLiveData<List<Currency>> popularCurrencies = new MutableLiveData<>();
    private final MutableLiveData<Currency> baseCurrency = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    // String baseCode;

    public CurrencyRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        currencyDao = database.currencyDao();
        apiService = ApiClient.getCurrencyApiService();
        preferences = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE);

//        String language = PreferenceUtils.getAppLanguage(context);
//        baseCode = language.equals("en") ? "USD" : "RUB";

        // Загрузка данных из локальной базы данных
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Currency> currencies = currencyDao.getAllCurrenciesByBaseSync(PreferenceUtils.getCurrency(context));

            //currencies.sort(Comparator.comparing(Currency::getCode).reversed()); через например клик листенер

            allCurrencies.postValue(currencies);
            loadPopularCurrencies();
            loadBaseCurrency();
        });
        this.context = context;
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

            apiService.getCurrentRates(
                    ApiClient.API_KEY,
                    ApiClient.SUPPORTED_CURRENCIES,
                    PreferenceUtils.getCurrency(context)
            ).enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<CurrencyRate> call, Response<CurrencyRate> response) {
                    isLoading.setValue(false);
                    if (response.isSuccessful() && response.body() != null) {
                        // Устанавливаем базовую валюту в полученном объекте
                        CurrencyRate currencyRate = response.body();
                        currencyRate.setBaseCurrency(PreferenceUtils.getCurrency(context));

                        // Сохраняем полученные данные в базу данных
                        saveToDatabase(currencyRate);

                        // Обновляем время последнего обновления
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putLong(PREF_CURRENCY_LAST_UPDATE, System.currentTimeMillis());
                        editor.apply();
                    } else {
                        // Если ответ неуспешный, выводим ошибку
                        errorMessage.setValue("Ошибка получения данных: " +
                                (response.errorBody() != null ? response.errorBody().toString() : "Неизвестная ошибка"));
                        Log.e(TAG, "API error: " + (response.errorBody() != null ? response.errorBody().toString() : "Unknown error"));
                    }
                }

                @Override
                public void onFailure(Call<CurrencyRate> call, Throwable t) {
                    isLoading.setValue(false);
                    errorMessage.setValue("Ошибка подключения: " + t.getMessage());
                    Log.e(TAG, "API call failed", t);
                }
            });
        } else {
            // Данные свежие, загружаем из базы данных
            AppDatabase.databaseWriteExecutor.execute(() -> {
                List<Currency> currencies = currencyDao.getAllCurrenciesByBaseSync(PreferenceUtils.getCurrency(context));
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
//    private void loadFromCache() {
//        String cacheJson = preferences.getString(PREF_CURRENCY_CACHE, null);
//        if (cacheJson != null) {
//            Gson gson = new Gson();
//            Type type = new TypeToken<List<CurrencyRate>>() {}.getType();
//            List<CurrencyRate> cachedRates = gson.fromJson(cacheJson, type);
//            saveToDatabase(cachedRates);
//        }
//    }

    /**
     * Сохраняет курсы валют в локальную базу данных
     * @param currencyRate список курсов валют
     */
    private void saveToDatabase(CurrencyRate currencyRate) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                Map<String, Double> rates = currencyRate.getRates();
                String baseCurrencyCode = currencyRate.getBaseCurrency();
                Date currentDate = new Date();

                // Обновляем данные для базовой валюты (курс = 1.0)
                updateCurrencyInDatabase(baseCurrencyCode, 1.0, "0", 0.0, 0.0, baseCurrencyCode, currentDate);

                // Обновляем данные для остальных валют
                for (Map.Entry<String, Double> entry : rates.entrySet()) {
                    String code = entry.getKey();
                    double rate = entry.getValue();

                    // Если базовая валюта, пропускаем (уже обновили выше)
                    if (code.equals(baseCurrencyCode)) continue;

                    // Получаем существующую валюту для определения изменения курса
                    Currency existingCurrency = currencyDao.getCurrencyByCodeSync(code, baseCurrencyCode);
                    String trend = "stable";
                    double change = 0.0;
                    double changePercentage = 0.0;

                    if (existingCurrency != null) {
                        double oldRate = existingCurrency.getRate();
                        change = rate - oldRate;
                        if (oldRate != 0) {
                            changePercentage = (change / oldRate) * 100;
                        }

                        if (change > 0) {
                            trend = "up";
                        } else if (change < 0) {
                            trend = "down";
                        }
                    }

                    // Обновляем валюту в базе данных
                    updateCurrencyInDatabase(code, rate, trend, change, changePercentage, baseCurrencyCode, currentDate);
                }

                // Загружаем обновленные данные
                List<Currency> updatedCurrencies = currencyDao.getAllCurrenciesByBaseSync(baseCurrencyCode);
                allCurrencies.postValue(updatedCurrencies);
                loadPopularCurrencies();
                loadBaseCurrency();
            } catch (Exception e) {
                Log.e(TAG, "Error saving currency data to database", e);
                errorMessage.postValue("Ошибка сохранения данных: " + e.getMessage());
            }
        });

    }

    /**
     * Загружает список популярных валют (RUB, USD, EUR)
     */
    private void loadPopularCurrencies() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<Currency> popularCurrenciesList = new ArrayList<>();

            // Добавляем базовую валюту и популярные валюты в список
            Currency rubCurrency = currencyDao.getCurrencyByCodeSync("RUB", PreferenceUtils.getCurrency(context));
            Currency usdCurrency = currencyDao.getCurrencyByCodeSync("USD", PreferenceUtils.getCurrency(context));
            Currency eurCurrency = currencyDao.getCurrencyByCodeSync("EUR", PreferenceUtils.getCurrency(context));

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
            Currency currency = currencyDao.getCurrencyByCodeSync("RUB", PreferenceUtils.getCurrency(context));
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
            Currency currency = currencyDao.getCurrencyByCodeSync(code, PreferenceUtils.getCurrency(context));
            result.postValue(currency);
        });
        return result;
    }

    private void updateCurrencyInDatabase(String code, double rate, String trend, double change,
                                          double changePercentage, String baseCurrencyCode, Date updateDate) {
        Currency currency = currencyDao.getCurrencyByCodeSync(code, PreferenceUtils.getCurrency(context));
        if (currency != null) {
            // Обновляем существующую валюту
            var oldRate = currency.getRate();
            currency.setRate(rate);
            currency.setTrend(trend);
            if (oldRate != currency.getRate())
            {
                currency.setChange(change); // Меняем только если были изменения в цене
                currency.setChangePercentage(changePercentage);
            }
            currency.setBaseCurrency(baseCurrencyCode);
            currency.setUpdatedAt(updateDate);
            currencyDao.update(currency);
        } else {
            // Создаем новую валюту
            currency = new Currency();
            currency.setCode(code);
            currency.setName(getNameForCurrencyCode(code, baseCurrencyCode));
            currency.setRate(rate);
            currency.setTrend(trend);
            currency.setChange(change);
            currency.setChangePercentage(changePercentage);
            currency.setBaseCurrency(baseCurrencyCode);
            currency.setUpdatedAt(updateDate);
            currency.setIconUrl("ic_currency_" + code.toLowerCase());
            currencyDao.insert(currency);
        }
    }

    private String getNameForCurrencyCode(String currencyCode, String baseCode) {
        // Validate input
        if (currencyCode == null) {
            return currencyCode;
        }

        if ("RUB".equals(baseCode)) {
            switch (currencyCode) {
                case "RUB": return "Российский рубль";
                case "USD": return "Доллар США";
                case "EUR": return "Евро";
                case "JPY": return "Японская йена";
                case "CNY": return "Китайский юань";
                case "INR": return "Индийская рупия";
                case "HUF": return "Венгерский форинт";
                case "PLN": return "Польский злотый";
                case "RON": return "Румынский лей";
                case "TRY": return "Турецкая лира";
                case "CAD": return "Канадский доллар";
                case "ILS": return "Израильский новый шекель";
                case "KRW": return "Южнокорейская вона";
                case "SGD": return "Сингапурский доллар";
                case "BGN": return "Болгарский лев";
                case "CZK": return "Чешская крона";
                case "DKK": return "Датская крона";
                case "GBP": return "Британский фунт стерлингов";
                case "SEK": return "Шведская крона";
                case "CHF": return "Швейцарский франк";
                case "ISK": return "Исландская крона";
                case "NOK": return "Норвежская крона";
                case "HRK": return "Хорватская куна";
                case "AUD": return "Австралийский доллар";
                case "BRL": return "Бразильский реал";
                case "HKD": return "Гонконгский доллар";
                case "IDR": return "Индонезийская рупия";
                case "MXN": return "Мексиканское песо";
                case "MYR": return "Малайзийский ринггит";
                case "NZD": return "Новозеландский доллар";
                case "PHP": return "Филиппинское песо";
                case "THB": return "Тайский бат";
                case "ZAR": return "Южноафриканский рэнд";
                default: return currencyCode;
            }
        }
        else {
            switch (currencyCode) {
                case "RUB": return "Russian Ruble";
                case "USD": return "US Dollar";
                case "EUR": return "Euro";
                case "JPY": return "Japanese Yen";
                case "CNY": return "Chinese Yuan";
                case "INR": return "Indian Rupee";
                case "HUF": return "Hungarian Forint";
                case "PLN": return "Polish Zloty";
                case "RON": return "Romanian Leu";
                case "TRY": return "Turkish Lira";
                case "CAD": return "Canadian Dollar";
                case "ILS": return "Israeli New Shekel";
                case "KRW": return "South Korean Won";
                case "SGD": return "Singapore Dollar";
                case "BGN": return "Bulgarian Lev";
                case "CZK": return "Czech Koruna";
                case "DKK": return "Danish Krone";
                case "GBP": return "British Pound Sterling";
                case "SEK": return "Swedish Krona";
                case "CHF": return "Swiss Franc";
                case "ISK": return "Icelandic Krona";
                case "NOK": return "Norwegian Krone";
                case "HRK": return "Croatian Kuna";
                case "AUD": return "Australian Dollar";
                case "BRL": return "Brazilian Real";
                case "HKD": return "Hong Kong Dollar";
                case "IDR": return "Indonesian Rupiah";
                case "MXN": return "Mexican Peso";
                case "MYR": return "Malaysian Ringgit";
                case "NZD": return "New Zealand Dollar";
                case "PHP": return "Philippine Peso";
                case "THB": return "Thai Baht";
                case "ZAR": return "South African Rand";
                default: return currencyCode;
            }
        }
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
