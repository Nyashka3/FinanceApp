package com.example.diplom.api;

import com.example.diplom.api.models.CurrencyRate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
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
    @GET("currency/rates")
    Call<List<CurrencyRate>> getCurrentRates();

    /**
     * Получение исторических курсов валют
     * @param days количество дней истории
     * @return список исторических курсов валют
     */
    @GET("currency/history")
    Call<List<CurrencyRate>> getHistoricalRates(@Query("days") int days);

    /**
     * Получение курса конкретной валюты
     * @param code код валюты (например, "USD", "EUR")
     * @return курс валюты
     */
    @GET("currency/rate")
    Call<CurrencyRate> getRateByCode(@Query("code") String code);
}