package com.example.diplom.api;

import com.example.diplom.api.models.EconomicIndicator;
import com.example.diplom.api.models.MarketPrice;
import com.example.diplom.api.models.TaxRate;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Интерфейс для API запросов.
 * Использует Retrofit для работы с REST API.
 */
public interface ApiService {

    /**
     * Получение текущих налоговых ставок
     * @return список налоговых ставок
     */
    @GET("taxes/rates")
    Call<List<TaxRate>> getTaxRates();

    /**
     * Получение истории налоговых ставок
     * @param year год
     * @return список налоговых ставок за указанный год
     */
    @GET("taxes/history/{year}")
    Call<List<TaxRate>> getTaxHistory(@Path("year") int year);

    /**
     * Получение рыночных цен на материалы
     * @param category категория материалов
     * @return список материалов с ценами
     */
    @GET("market/prices")
    Call<List<MarketPrice>> getMarketPrices(@Query("category") String category);

    /**
     * Получение экономических индикаторов
     * @return список экономических индикаторов
     */
    @GET("economy/indicators")
    Call<List<EconomicIndicator>> getEconomicIndicators();

    /**
     * Получение прогноза экономических индикаторов
     * @param months количество месяцев для прогноза
     * @return список прогнозных экономических индикаторов
     */
    @GET("economy/forecast")
    Call<List<EconomicIndicator>> getEconomicForecast(@Query("months") int months);
}
