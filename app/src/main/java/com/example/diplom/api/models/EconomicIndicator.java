package com.example.diplom.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Модель данных для экономического индикатора.
 */
public class EconomicIndicator {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("code")
    private String code;

    @SerializedName("value")
    private double value;

    @SerializedName("unit")
    private String unit;

    @SerializedName("date")
    private String date;

    @SerializedName("previous_value")
    private double previousValue;

    @SerializedName("change")
    private double change;

    @SerializedName("change_percentage")
    private double changePercentage;

    @SerializedName("forecast")
    private double forecast;

    @SerializedName("source")
    private String source;

    // Геттеры и сеттеры
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(double previousValue) {
        this.previousValue = previousValue;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(double changePercentage) {
        this.changePercentage = changePercentage;
    }

    public double getForecast() {
        return forecast;
    }

    public void setForecast(double forecast) {
        this.forecast = forecast;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
