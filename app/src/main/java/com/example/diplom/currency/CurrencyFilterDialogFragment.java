package com.example.diplom.currency;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.diplom.R;
import com.example.diplom.databinding.DialogCurrencyFilterBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Диалоговое окно для установки фильтров валют
 */
public class CurrencyFilterDialogFragment extends DialogFragment {

    private DialogCurrencyFilterBinding binding;
    private FilterDialogListener listener;

    // Текущие значения фильтров
    private String codeFilter;
    private Double minRate;
    private Double maxRate;

    /**
     * Интерфейс для обработки результатов диалога
     */
    public interface FilterDialogListener {
        void onFilterApplied(String codeFilter, Double minRate, Double maxRate);
        void onFilterReset();
    }

    /**
     * Создает новый экземпляр диалога с заданными начальными значениями
     * @param codeFilter текущий фильтр по коду
     * @param minRate минимальное значение курса
     * @param maxRate максимальное значение курса
     * @return экземпляр диалога
     */
    public static CurrencyFilterDialogFragment newInstance(String codeFilter, Double minRate, Double maxRate) {
        CurrencyFilterDialogFragment fragment = new CurrencyFilterDialogFragment();
        Bundle args = new Bundle();
        args.putString("codeFilter", codeFilter);
        args.putDouble("minRate", minRate != null ? minRate : Double.MIN_VALUE);
        args.putDouble("maxRate", maxRate != null ? maxRate : Double.MAX_VALUE);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FilterDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = DialogCurrencyFilterBinding.inflate(LayoutInflater.from(getContext()));

        // Получение текущих значений фильтров из аргументов
        if (getArguments() != null) {
            codeFilter = getArguments().getString("codeFilter", "");
            double minRateArg = getArguments().getDouble("minRate", Double.MIN_VALUE);
            double maxRateArg = getArguments().getDouble("maxRate", Double.MAX_VALUE);

            minRate = minRateArg == Double.MIN_VALUE ? null : minRateArg;
            maxRate = maxRateArg == Double.MAX_VALUE ? null : maxRateArg;

            // Установка начальных значений в поля ввода
            binding.editTextCodeFilter.setText(codeFilter);
            if (minRate != null) {
                binding.editTextMinRate.setText(String.valueOf(minRate));
            }
            if (maxRate != null) {
                binding.editTextMaxRate.setText(String.valueOf(maxRate));
            }
        }

        // Создание диалога
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setTitle(R.string.filter_currencies)
                .setView(binding.getRoot())
                .setPositiveButton(R.string.apply, null) // Устанавливаем null для предотвращения автоматического закрытия
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .setNeutralButton(R.string.reset, null); // Устанавливаем null для предотвращения автоматического закрытия

        Dialog dialog = builder.create();

        // Настройка кнопок после создания диалога для предотвращения автоматического закрытия при ошибках валидации
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = ((androidx.appcompat.app.AlertDialog) dialog).getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE);
            Button neutralButton = ((androidx.appcompat.app.AlertDialog) dialog).getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL);

            positiveButton.setOnClickListener(v -> {
                if (validateInputs()) {
                    listener.onFilterApplied(
                            binding.editTextCodeFilter.getText().toString().trim(),
                            getDoubleFromEditText(binding.editTextMinRate),
                            getDoubleFromEditText(binding.editTextMaxRate)
                    );
                    dialog.dismiss();
                }
            });

            neutralButton.setOnClickListener(v -> {
                listener.onFilterReset();
                dialog.dismiss();
            });
        });

        return dialog;
    }

    /**
     * Проверяет корректность введенных данных
     * @return true, если данные корректны
     */
    private boolean validateInputs() {
        boolean isValid = true;

        // Проверка диапазона курса
        Double minRateValue = getDoubleFromEditText(binding.editTextMinRate);
        Double maxRateValue = getDoubleFromEditText(binding.editTextMaxRate);

        if (minRateValue != null && maxRateValue != null && minRateValue > maxRateValue) {
            binding.editTextMaxRate.setError(getString(R.string.error_min_greater_max));
            isValid = false;
        }

        return isValid;
    }

    /**
     * Преобразует текст из EditText в Double
     * @param editText поле ввода
     * @return значение Double или null, если поле пустое
     */
    private Double getDoubleFromEditText(EditText editText) {
        String text = editText.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            return null;
        }

        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            editText.setError(getString(R.string.error_invalid_number));
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}