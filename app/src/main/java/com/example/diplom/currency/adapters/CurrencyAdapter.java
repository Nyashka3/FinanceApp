package com.example.diplom.currency.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplom.R;
import com.example.diplom.database.entities.Currency;
import com.example.diplom.databinding.ItemCurrencyBinding;
import com.example.diplom.utils.DateUtils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Адаптер для отображения списка валют в RecyclerView
 */
public class CurrencyAdapter extends ListAdapter<Currency, CurrencyAdapter.CurrencyViewHolder> {

    private OnCurrencyClickListener listener;

    public CurrencyAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCurrencyBinding binding = ItemCurrencyBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CurrencyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {
        Currency currency = getItem(position);
        holder.bind(currency);
    }

    /**
     * Установка слушателя для клика по валюте
     * @param listener слушатель
     */
    public void setOnCurrencyClickListener(OnCurrencyClickListener listener) {
        this.listener = listener;
    }

    /**
     * ViewHolder для валюты
     */
    class CurrencyViewHolder extends RecyclerView.ViewHolder {
        private final ItemCurrencyBinding binding;

        public CurrencyViewHolder(@NonNull ItemCurrencyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Установка слушателя клика
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCurrencyClick(getItem(position));
                }
            });
        }

        /**
         * Привязка данных к View
         * @param currency валюта для отображения
         */
        public void bind(Currency currency) {
            binding.currencyCodeText.setText(currency.getCode());
            binding.currencyNameText.setText(currency.getName());

            // Устанавливаем флаг валюты
            String iconName = currency.getIconUrl();
            int iconResourceId = binding.getRoot().getContext().getResources()
                    .getIdentifier(iconName, "drawable",
                            binding.getRoot().getContext().getPackageName());
            if (iconResourceId != 0) {
                binding.currencyFlagImage.setImageResource(iconResourceId);
            } else {
                // Если иконка не найдена, используем стандартную
                binding.currencyFlagImage.setImageResource(R.drawable.ic_currency_default);
            }

            // Форматирование и отображение курса валюты
            NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
            format.setMaximumFractionDigits(4);
            binding.currencyRateText.setText(format.format(currency.getRate()));

            // Отображение изменения курса
            if (currency.getChange() > 0) {
                binding.currencyChangeText.setText("+" + format.format(currency.getChange()));
                binding.currencyChangeText.setTextColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.positive_amount));
                binding.trendIcon.setImageResource(R.drawable.ic_trend_up);
                binding.trendIcon.setImageTintList(
                        ColorStateList.valueOf(
                                ContextCompat.getColor(binding.getRoot().getContext(), R.color.positive_amount)));
            } else if (currency.getChange() < 0) {
                binding.currencyChangeText.setText(format.format(currency.getChange()));
                binding.currencyChangeText.setTextColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.negative_amount));
                binding.trendIcon.setImageResource(R.drawable.ic_trend_down);
                binding.trendIcon.setImageTintList(
                        ColorStateList.valueOf(
                                ContextCompat.getColor(binding.getRoot().getContext(), R.color.negative_amount)));
            } else {
                binding.currencyChangeText.setText(format.format(currency.getChange()));
                binding.currencyChangeText.setTextColor(
                        ContextCompat.getColor(binding.getRoot().getContext(), R.color.neutral_amount));
                binding.trendIcon.setImageResource(R.drawable.ic_trend_neutral);
                binding.trendIcon.setImageTintList(
                        ColorStateList.valueOf(
                                ContextCompat.getColor(binding.getRoot().getContext(), R.color.neutral_amount)));
            }

            // Отображение процентного изменения
            NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.getDefault());
            percentFormat.setMaximumFractionDigits(2);
            binding.currencyPercentText.setText(percentFormat.format(currency.getChangePercentage() / 100));

            // Отображение даты обновления
            if (currency.getUpdatedAt() != null) {
                binding.currencyDateText.setText(
                        binding.getRoot().getContext().getString(R.string.last_updated) +
                                ": " +
                                DateUtils.formatDateTime(currency.getUpdatedAt()));
            }
        }
    }

    /**
     * Интерфейс для обработки клика по валюте
     */
    public interface OnCurrencyClickListener {
        void onCurrencyClick(Currency currency);
    }

    /**
     * DiffUtil.Callback для оптимизации обновлений списка
     */
    private static final DiffUtil.ItemCallback<Currency> DIFF_CALLBACK = new DiffUtil.ItemCallback<Currency>() {
        @Override
        public boolean areItemsTheSame(@NonNull Currency oldItem, @NonNull Currency newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Currency oldItem, @NonNull Currency newItem) {
            return oldItem.getCode().equals(newItem.getCode()) &&
                    oldItem.getRate() == newItem.getRate() &&
                    oldItem.getChange() == newItem.getChange() &&
                    oldItem.getChangePercentage() == newItem.getChangePercentage();
        }
    };
}
