package com.example.diplom.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.diplom.MainActivity;
import com.example.diplom.R;
import com.example.diplom.utils.PreferenceUtils;

/**
 * Фрагмент настроек для отображения и управления настройками приложения
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Настройка предпочтения языка
        ListPreference languagePreference = findPreference("pref_language");
        if (languagePreference != null) {
            // Установим текущее значение
            languagePreference.setValue(PreferenceUtils.getAppLanguage(requireContext()));

            languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String languageCode = newValue.toString();

                // Сохраняем новое значение языка
                PreferenceUtils.setAppLanguage(requireContext(), languageCode);

                // Показываем сообщение о необходимости перезапуска приложения
                Toast.makeText(requireContext(),
                        R.string.settings_saved,
                        Toast.LENGTH_SHORT).show();

                // Перезапускаем приложение для применения нового языка
                restartApp();

                return true;
            });
        }

        // Настройка предпочтения темы
        ListPreference themePreference = findPreference("pref_theme");
        if (themePreference != null) {
            themePreference.setValue(PreferenceUtils.getAppTheme(requireContext()));

            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String theme = newValue.toString();
                PreferenceUtils.setAppTheme(requireContext(), theme);
                return true;
            });
        }

        // Настройка предпочтения динамических цветов
        SwitchPreferenceCompat dynamicColorsPreference = findPreference("pref_dynamic_colors");
        if (dynamicColorsPreference != null) {
            dynamicColorsPreference.setChecked(PreferenceUtils.useDynamicColors(requireContext()));

            dynamicColorsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean useDynamicColors = (boolean) newValue;
                PreferenceUtils.setUseDynamicColors(requireContext(), useDynamicColors);

                // Перезапуск для применения новых цветов
                restartApp();

                return true;
            });
        }

        // Настройка предпочтения валюты
        ListPreference currencyPreference = findPreference("pref_currency");
        if (currencyPreference != null) {
            currencyPreference.setValue(PreferenceUtils.getCurrency(requireContext()));

            currencyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                String currencyCode = newValue.toString();
                PreferenceUtils.setCurrency(requireContext(), currencyCode);

                Toast.makeText(requireContext(),
                        R.string.settings_saved,
                        Toast.LENGTH_SHORT).show();

                return true;
            });
        }

        // Настройка предпочтения "О приложении"
        Preference aboutPreference = findPreference("pref_about");
        if (aboutPreference != null) {
            aboutPreference.setOnPreferenceClickListener(preference -> {
                // Показ диалога с информацией о приложении
                showAboutDialog();
                return true;
            });
        }
    }

    /**
     * Показывает диалог с информацией о приложении
     */
    private void showAboutDialog() {
        new AboutDialog().show(getParentFragmentManager(), "about_dialog");
    }

    /**
     * Метод для перезапуска приложения
     */
    private void restartApp() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        requireActivity().startActivity(intent);
        requireActivity().finish();

        // Добавляем анимацию для плавного перехода
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
