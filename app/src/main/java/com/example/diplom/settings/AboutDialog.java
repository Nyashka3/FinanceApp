package com.example.diplom.settings;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.diplom.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Диалог с информацией о приложении
 */
public class AboutDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());

        // Настройка заголовка и иконки
        builder.setTitle(R.string.settings_about)
                .setIcon(R.drawable.ic_launcher_foreground);

        // Получаем информацию о версии из PackageInfo вместо BuildConfig
        String versionName = "1.0";
        int versionCode = 1;

        try {
            PackageInfo packageInfo = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Формирование текста о приложении
        String aboutText = getString(R.string.app_name) + "\n" +
                "Версия: " + versionName + " (" + versionCode + ")\n\n" +
                "Приложение для контроля и оптимизации бюджетных расходов предприятия малого бизнеса.\n\n" +
                "Разработано с использованием:\n" +
                "- Java\n" +
                "- Material Design 3\n" +
                "- Room Database\n" +
                "- Retrofit API Client\n" +
                "- MPAndroidChart\n" +
                "- Математические и экономические методы анализа\n\n" +
                "© 2025. Все права защищены.";

        builder.setMessage(aboutText);

        // Кнопка закрытия
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
        });

        return builder.create();
    }
}