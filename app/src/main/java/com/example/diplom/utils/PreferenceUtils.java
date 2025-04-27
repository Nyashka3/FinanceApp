package com.example.diplom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.Locale;

/**
 * Утилитный класс для работы с настройками приложения.
 */
public class PreferenceUtils {

    // Ключи для настроек
    private static final String PREF_APP_LANGUAGE = "app_language";
    private static final String PREF_APP_THEME = "app_theme";
    private static final String PREF_USE_DYNAMIC_COLORS = "use_dynamic_colors";
    private static final String PREF_FIRST_RUN = "first_run";
    private static final String PREF_CURRENCY = "currency";

    // Значения для темы
    public static final String THEME_LIGHT = "light";
    public static final String THEME_DARK = "dark";
    public static final String THEME_SYSTEM = "system";

    /**
     * Получает предпочтительный язык приложения
     * @param context контекст приложения
     * @return код языка (например, "ru", "en")
     */
    public static String getAppLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_APP_LANGUAGE, "ru");
    }

    /**
     * Устанавливает предпочтительный язык приложения
     * @param context контекст приложения
     * @param languageCode код языка (например, "ru", "en")
     */
    public static void setAppLanguage(Context context, String languageCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_APP_LANGUAGE, languageCode).apply();
    }

    /**
     * Обновляет язык приложения
     * @param context контекст приложения
     * @param languageCode код языка (например, "ru", "en")
     * @return обновленный контекст с новой локалью
     */
    public static Context updateAppLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
            return context.createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
            return context;
        }
    }

    /**
     * Применяет сохраненный язык к активности
     * @param context контекст приложения
     * @return обновленный контекст с локалью из настроек
     */
    public static Context applyPreferredLanguage(Context context) {
        String language = getAppLanguage(context);
        return updateAppLanguage(context, language);
    }

    /**
     * Получает предпочтительную тему приложения
     * @param context контекст приложения
     * @return тема (THEME_LIGHT, THEME_DARK или THEME_SYSTEM)
     */
    public static String getAppTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_APP_THEME, THEME_SYSTEM);
    }

    /**
     * Устанавливает предпочтительную тему приложения
     * @param context контекст приложения
     * @param theme тема (THEME_LIGHT, THEME_DARK или THEME_SYSTEM)
     */
    public static void setAppTheme(Context context, String theme) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_APP_THEME, theme).apply();
        updateAppTheme(theme);
    }

    /**
     * Обновляет тему приложения
     * @param theme тема (THEME_LIGHT, THEME_DARK или THEME_SYSTEM)
     */
    public static void updateAppTheme(String theme) {
        switch (theme) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    /**
     * Получает статус использования динамических цветов (Material You)
     * @param context контекст приложения
     * @return true, если динамические цвета включены
     */
    public static boolean useDynamicColors(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_USE_DYNAMIC_COLORS, true);
    }

    /**
     * Устанавливает статус использования динамических цветов
     * @param context контекст приложения
     * @param useDynamicColors true, если динамические цвета должны быть включены
     */
    public static void setUseDynamicColors(Context context, boolean useDynamicColors) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(PREF_USE_DYNAMIC_COLORS, useDynamicColors).apply();
    }

    /**
     * Проверяет, является ли это первым запуском приложения
     * @param context контекст приложения
     * @return true, если это первый запуск
     */
    public static boolean isFirstRun(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_FIRST_RUN, true);
    }

    /**
     * Устанавливает статус первого запуска
     * @param context контекст приложения
     * @param isFirstRun true, если это первый запуск
     */
    public static void setFirstRun(Context context, boolean isFirstRun) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(PREF_FIRST_RUN, isFirstRun).apply();
    }

    /**
     * Получает предпочтительную валюту
     * @param context контекст приложения
     * @return код валюты (например, "RUB", "USD")
     */
    public static String getCurrency(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREF_CURRENCY, "RUB");
    }

    /**
     * Устанавливает предпочтительную валюту
     * @param context контекст приложения
     * @param currencyCode код валюты (например, "RUB", "USD")
     */
    public static void setCurrency(Context context, String currencyCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_CURRENCY, currencyCode).apply();
    }
}