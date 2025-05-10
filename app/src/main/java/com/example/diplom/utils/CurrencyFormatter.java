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
}
