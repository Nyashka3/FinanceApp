package com.example.diplom;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.example.diplom.utils.PreferenceUtils;
import com.google.android.material.color.DynamicColors;

/**
 * Класс приложения, который инициализирует глобальные настройки.
 */
public class DiplomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Применение сохраненной темы
        applyAppTheme();

        // Применение динамических цветов, если включено
        applyDynamicColors();
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

    /**
     * Этот метод вызывается перед любым другим методом при создании экземпляра.
     * Мы переопределяем его, чтобы применить настройки языка до создания контекста.
     */
    @Override
    protected void attachBaseContext(Context base) {
        // Применение языка к базовому контексту
        Context context = PreferenceUtils.applyPreferredLanguage(base);
        super.attachBaseContext(context);
    }

    /**
     * Вызывается при изменении конфигурации устройства, включая изменение языка.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Пересоздаем конфигурацию с нашими настройками
        PreferenceUtils.applyPreferredLanguage(this);
    }
}