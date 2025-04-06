package com.example.diplom.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Утилитный класс для форматирования валютных значений.
 */
public class CurrencyFormatter {

    private static final NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));

    /**
     * Форматирует сумму в валютный формат (руб.)
     * @param amount сумма для форматирования
     * @return отформатированная сумма с символом валюты
     */
    public static String format(double amount) {
        return numberFormat.format(amount);
    }

    /**
     * Форматирует сумму в валютный формат без символа валюты
     * @param amount сумма для форматирования
     * @return отформатированная сумма без символа валюты
     */
    public static String formatWithoutSymbol(double amount) {
        NumberFormat format = (NumberFormat) numberFormat.clone();
        format.setCurrency(null);
        return format.format(amount);
    }

    /**
     * Форматирует сумму с указанием знака (+ или -)
     * @param amount сумма для форматирования
     * @return отформатированная сумма со знаком
     */
    public static String formatWithSign(double amount) {
        String formatted = format(Math.abs(amount));
        return (amount >= 0 ? "+" : "-") + formatted;
    }

    /**
     * Форматирует процентное значение
     * @param percentage процентное значение
     * @return отформатированное процентное значение
     */
    public static String formatPercentage(double percentage) {
        NumberFormat percentFormat = NumberFormat.getPercentInstance(new Locale("ru", "RU"));
        percentFormat.setMaximumFractionDigits(2);
        return percentFormat.format(percentage / 100);
    }
}
