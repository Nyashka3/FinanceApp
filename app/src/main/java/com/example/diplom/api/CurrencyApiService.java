package com.example.diplom.api;

import com.example.diplom.api.models.CurrencyRate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Интерфейс для API запросов к сервису курсов валют.
 * Использует Retrofit для работы с REST API.
 */
public interface CurrencyApiService {

    /**
     * Получение текущих курсов валют
     * @return список текущих курсов валют
     */
    @GET("v1/latest")
    Call<CurrencyRate> getCurrentRates(
            @Query("apikey") String apiKey,
            @Query("currencies") String currencies,
            @Query("base_currency") String baseCurrency
    );
}