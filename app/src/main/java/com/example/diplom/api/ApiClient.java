package com.example.diplom.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Класс для создания и настройки API клиента.
 */
public class ApiClient {
    private static final String BASE_URL = "https://api.example.com/v1/"; // Базовый URL для API
    private static final String CURRENCY_BASE_URL = "https://api.freecurrencyapi.com/"; // Базовый URL для API валют
    public static final String SUPPORTED_CURRENCIES = "EUR,USD,JPY,CNY,CZK,INR,HUF,PLN,RON,TRY,CAD,ILS,KRW,SGD,BGN,DKK,GBP,SEK,CHF,ISK,NOK,HRK,RUB,AUD,BRL,HKD,IDR,MXN,MYR,NZD,PHP,THB,ZAR";
    public static final String API_KEY = "fca_live_BPwqxb36ONW52WvZNI7bTVIEIiQTBrPcFcI8aBow";

    private static Retrofit retrofit = null;
    private static Retrofit currencyRetrofit = null;

    /**
     * Получение экземпляра Retrofit клиента.
     *
     * @return настроенный Retrofit клиент
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Добавляем логирование HTTP запросов всегда или используйте константу
            // Вместо проверки BuildConfig.DEBUG
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    /**
     * Получение экземпляра Retrofit клиента для валют.
     *
     * @return настроенный Retrofit клиент для валют
     */
    public static Retrofit getCurrencyClient() {
        if (currencyRetrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Добавляем логирование HTTP запросов
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);

            currencyRetrofit = new Retrofit.Builder()
                    .baseUrl(CURRENCY_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return currencyRetrofit;
    }

    /**
     * Получение экземпляра API сервиса.
     *
     * @return настроенный API сервис
     */
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }

    /**
     * Получение экземпляра API сервиса для валют.
     *
     * @return настроенный API сервис для валют
     */
    public static CurrencyApiService getCurrencyApiService() {
        return getCurrencyClient().create(CurrencyApiService.class);
    }
}
