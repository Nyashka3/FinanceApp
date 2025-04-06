package com.example.diplom.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.diplom.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Диалог подтверждения выхода из приложения
 */
public class ExitDialogFragment extends DialogFragment {

    private ExitDialogListener listener;

    /**
     * Интерфейс для обработки действий в диалоге
     */
    public interface ExitDialogListener {
        /**
         * Вызывается при подтверждении выхода
         */
        void onExitConfirmed();

        /**
         * Вызывается при отмене выхода
         */
        void onExitCancelled();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Убедимся, что контекст реализует интерфейс слушателя
        try {
            listener = (ExitDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ExitDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.exit_dialog_title)
                .setMessage(R.string.exit_dialog_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (listener != null) {
                        listener.onExitConfirmed();
                    }
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {
                    if (listener != null) {
                        listener.onExitCancelled();
                    }
                })
                .create();
    }
}
