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
    private static final String CURRENCY_BASE_URL = "https://api.exchangerate.host/"; // Базовый URL для API валют
    private static Retrofit retrofit = null;
    private static Retrofit currencyRetrofit = null;

    /**
     * Получение экземпляра Retrofit клиента.
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
     * Получение экземпляра API сервиса.
     * @return настроенный API сервис
     */
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
