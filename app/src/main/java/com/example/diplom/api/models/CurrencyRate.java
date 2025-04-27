package com.example.diplom.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Модель данных для курса валюты.
 */
public class CurrencyRate {
    @SerializedName("data")
    private Map<String, Double> rates;

    private String baseCurrency;

    // Геттеры и сеттеры
    public Map<String, Double> getRates() {
        return rates;
    }

    public void setRates(Map<String, Double> rates) {
        this.rates = rates;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }
}
