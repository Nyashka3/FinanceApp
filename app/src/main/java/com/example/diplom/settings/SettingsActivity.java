package com.example.diplom.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.example.diplom.MainActivity;
import com.example.diplom.R;
import com.example.diplom.databinding.ActivitySettingsBinding;
import com.example.diplom.utils.PreferenceUtils;

/**
 * Активность для управления настройками приложения
 */
public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SettingsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        // Настройка кнопки "Назад" в тулбаре
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings_title);
        }

        // Инициализация ViewModel
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // Загрузка фрагмента настроек
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Фрагмент настроек
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // Настройка предпочтения языка
            ListPreference languagePreference = findPreference("pref_language");
            if (languagePreference != null) {
                languagePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String languageCode = newValue.toString();
                    PreferenceUtils.setAppLanguage(requireContext(), languageCode);

                    // Перезапуск активности для применения нового языка
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();

                    return true;
                });
            }

            // Настройка предпочтения темы
            ListPreference themePreference = findPreference("pref_theme");
            if (themePreference != null) {
                themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String theme = newValue.toString();
                    PreferenceUtils.setAppTheme(requireContext(), theme);
                    return true;
                });
            }

            // Настройка предпочтения динамических цветов
            SwitchPreferenceCompat dynamicColorsPreference = findPreference("pref_dynamic_colors");
            if (dynamicColorsPreference != null) {
                dynamicColorsPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean useDynamicColors = (boolean) newValue;
                    PreferenceUtils.setUseDynamicColors(requireContext(), useDynamicColors);

                    // Перезапуск активности для применения новых цветов
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    requireActivity().finish();

                    return true;
                });
            }

            // Настройка предпочтения валюты
            ListPreference currencyPreference = findPreference("pref_currency");
            if (currencyPreference != null) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
