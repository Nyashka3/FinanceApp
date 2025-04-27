package com.example.diplom;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diplom.utils.PreferenceUtils;

/**
 * Базовая активность, которая применяет локализацию из настроек.
 * Все активности приложения должны наследоваться от этого класса
 * для правильной работы механизма смены языка приложения.
 */
public abstract class BaseLocaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        // Применяем предпочтительный язык к контексту активности
        Context context = PreferenceUtils.applyPreferredLanguage(newBase);
        super.attachBaseContext(context);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Если конфигурация изменилась (например, ориентация экрана),
        // применяем наши языковые настройки
        PreferenceUtils.applyPreferredLanguage(this);
    }
}
