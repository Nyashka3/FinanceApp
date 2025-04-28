package com.example.diplom.currency;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.diplom.R;
import com.example.diplom.currency.CurrencyViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Диалоговое окно для выбора способа сортировки валют
 */
public class CurrencySortDialogFragment extends DialogFragment {

    private SortDialogListener listener;
    private int currentSortOrder;

    /**
     * Интерфейс для обработки выбора сортировки
     */
    public interface SortDialogListener {
        void onSortOrderSelected(int sortOrder);
    }

    /**
     * Создает новый экземпляр диалога с заданным текущим порядком сортировки
     * @param currentSortOrder текущий порядок сортировки
     * @return экземпляр диалога
     */
    public static CurrencySortDialogFragment newInstance(int currentSortOrder) {
        CurrencySortDialogFragment fragment = new CurrencySortDialogFragment();
        Bundle args = new Bundle();
        args.putInt("currentSortOrder", currentSortOrder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SortDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SortDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Получение текущего порядка сортировки из аргументов
        if (getArguments() != null) {
            currentSortOrder = getArguments().getInt("currentSortOrder", CurrencyViewModel.SORT_CODE_ASC);
        } else {
            currentSortOrder = CurrencyViewModel.SORT_CODE_ASC;
        }

        // Создание элементов диалога
        final String[] sortOptions = {
                getString(R.string.sort_code_asc),
                getString(R.string.sort_code_desc),
                getString(R.string.sort_rate_asc),
                getString(R.string.sort_rate_desc)
        };

        // Определение индекса выбранного элемента
        int selectedIndex = 0;
        switch (currentSortOrder) {
            case CurrencyViewModel.SORT_CODE_ASC:
                selectedIndex = 0;
                break;
            case CurrencyViewModel.SORT_CODE_DESC:
                selectedIndex = 1;
                break;
            case CurrencyViewModel.SORT_RATE_ASC:
                selectedIndex = 2;
                break;
            case CurrencyViewModel.SORT_RATE_DESC:
                selectedIndex = 3;
                break;
        }

        // Создание диалога
        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.sort_by)
                .setSingleChoiceItems(sortOptions, selectedIndex, (dialog, which) -> {
                    int selectedSortOrder;
                    switch (which) {
                        case 0:
                            selectedSortOrder = CurrencyViewModel.SORT_CODE_ASC;
                            break;
                        case 1:
                            selectedSortOrder = CurrencyViewModel.SORT_CODE_DESC;
                            break;
                        case 2:
                            selectedSortOrder = CurrencyViewModel.SORT_RATE_ASC;
                            break;
                        case 3:
                            selectedSortOrder = CurrencyViewModel.SORT_RATE_DESC;
                            break;
                        default:
                            selectedSortOrder = CurrencyViewModel.SORT_CODE_ASC;
                    }
                    listener.onSortOrderSelected(selectedSortOrder);
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();
    }
}