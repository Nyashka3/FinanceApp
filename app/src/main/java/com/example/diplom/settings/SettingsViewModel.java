package com.example.diplom.settings;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.diplom.utils.PreferenceUtils;
// TODO : походу не используется
/**
 * ViewModel для работы с настройками приложения
 */
public class SettingsViewModel extends AndroidViewModel {

    private final MutableLiveData<String> appLanguage;
    private final MutableLiveData<String> appTheme;
    private final MutableLiveData<Boolean> useDynamicColors;
    private final MutableLiveData<String> appCurrency;

    public SettingsViewModel(@NonNull Application application) {
        super(application);

        // Инициализация LiveData настроек
        appLanguage = new MutableLiveData<>(PreferenceUtils.getAppLanguage(application));
        appTheme = new MutableLiveData<>(PreferenceUtils.getAppTheme(application));
        useDynamicColors = new MutableLiveData<>(PreferenceUtils.useDynamicColors(application));
        appCurrency = new MutableLiveData<>(PreferenceUtils.getCurrency(application));
    }

    /**
     * Устанавливает язык приложения
     * @param languageCode код языка (например, "ru", "en")
     */
    public void setAppLanguage(String languageCode) {
        PreferenceUtils.setAppLanguage(getApplication(), languageCode);
        appLanguage.setValue(languageCode);
    }

    /**
     * Устанавливает тему приложения
     * @param theme тема (THEME_LIGHT, THEME_DARK или THEME_SYSTEM)
     */
    public void setAppTheme(String theme) {
        PreferenceUtils.setAppTheme(getApplication(), theme);
        appTheme.setValue(theme);
    }

    /**
     * Устанавливает использование динамических цветов
     * @param useDynamicColors true, если нужно использовать динамические цвета
     */
    public void setUseDynamicColors(boolean useDynamicColors) {
        PreferenceUtils.setUseDynamicColors(getApplication(), useDynamicColors);
        this.useDynamicColors.setValue(useDynamicColors);
    }

    /**
     * Устанавливает валюту приложения
     * @param currencyCode код валюты (например, "RUB", "USD")
     */
    public void setAppCurrency(String currencyCode) {
        PreferenceUtils.setCurrency(getApplication(), currencyCode);
        appCurrency.setValue(currencyCode);
    }

    /**
     * Получает текущий язык приложения
     * @return LiveData с кодом языка
     */
    public LiveData<String> getAppLanguage() {
        return appLanguage;
    }

    /**
     * Получает текущую тему приложения
     * @return LiveData с кодом темы
     */
    public LiveData<String> getAppTheme() {
        return appTheme;
    }

    /**
     * Получает статус использования динамических цветов
     * @return LiveData с статусом использования динамических цветов
     */
    public LiveData<Boolean> getUseDynamicColors() {
        return useDynamicColors;
    }

    /**
     * Получает текущую валюту приложения
     * @return LiveData с кодом валюты
     */
    public LiveData<String> getAppCurrency() {
        return appCurrency;
    }
}
