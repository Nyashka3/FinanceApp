package com.example.diplom;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.diplom.utils.NotificationUtils;
import com.example.diplom.utils.PreferenceUtils;
import com.google.android.material.color.DynamicColors;

import java.util.Locale;

/**
 * Класс приложения, который инициализирует глобальные настройки.
 */
public class DiplomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Инициализация канала уведомлений
        NotificationUtils.createNotificationChannel(this);

        // Применение сохраненного языка
        applyAppLanguage();

        // Применение сохраненной темы
        applyAppTheme();

        // Применение динамических цветов, если включено
        applyDynamicColors();
    }

    /**
     * Применяет сохраненный язык приложения
     */
    private void applyAppLanguage() {
        String languageCode = PreferenceUtils.getAppLanguage(this);
        PreferenceUtils.updateAppLanguage(this, languageCode);
    }

    /**
     * Применяет сохраненную тему приложения
     */
    private void applyAppTheme() {
        String theme = PreferenceUtils.getAppTheme(this);
        PreferenceUtils.updateAppTheme(theme);
    }

    /**
     * Применяет динамические цвета, если они включены
     */
    private void applyDynamicColors() {
        boolean useDynamicColors = PreferenceUtils.useDynamicColors(this);
        if (useDynamicColors) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        // Применение языка при создании контекста
        String languageCode = PreferenceUtils.getAppLanguage(base);
        Context localizedContext = updateBaseContextLocale(base, languageCode);
        super.attachBaseContext(localizedContext);
    }

    /**
     * Обновляет локаль базового контекста
     * @param context исходный контекст
     * @param languageCode код языка
     * @return контекст с новой локалью
     */
    private Context updateBaseContextLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }
}
