package com.example.diplom.taxes.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplom.R;
import com.example.diplom.database.entities.Tax;
import com.example.diplom.databinding.ItemTaxBinding;
import com.example.diplom.utils.CurrencyFormatter;
import com.example.diplom.utils.DateUtils;

/**
 * Адаптер для отображения списка налогов в RecyclerView
 */
public class TaxAdapter extends ListAdapter<Tax, TaxAdapter.TaxViewHolder> {

    private OnTaxClickListener listener;

    public TaxAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public TaxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaxBinding binding = ItemTaxBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new TaxViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxViewHolder holder, int position) {
        Tax tax = getItem(position);
        holder.bind(tax);
    }

    /**
     * Установка слушателя для клика по налогу
     * @param listener слушатель
     */
    public void setOnTaxClickListener(OnTaxClickListener listener) {
        this.listener = listener;
    }

    /**
     * ViewHolder для налога
     */
    class TaxViewHolder extends RecyclerView.ViewHolder {
        private final ItemTaxBinding binding;

        public TaxViewHolder(@NonNull ItemTaxBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Установка слушателя клика
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onTaxClick(getItem(position));
                }
            });
        }

        /**
         * Привязка данных к View
         * @param tax налог для отображения
         */
        public void bind(Tax tax) {
            binding.taxNameText.setText(tax.getName());
            binding.taxAmountText.setText(CurrencyFormatter.format(tax.getAmount()));
            binding.taxDueDateText.setText(DateUtils.formatDate(tax.getDueDate()));

            // Настройка статуса
            switch (tax.getStatus()) {
                case "PAID":
                    binding.taxStatusText.setText(R.string.status_paid);
                    binding.taxStatusText.setTextColor(
                            binding.getRoot().getContext().getResources().getColor(
                                    R.color.status_paid, null));
                    break;
                case "UPCOMING":
                    binding.taxStatusText.setText(R.string.status_upcoming);
                    binding.taxStatusText.setTextColor(
                            binding.getRoot().getContext().getResources().getColor(
                                    R.color.status_upcoming, null));
                    break;
                case "OVERDUE":
                    binding.taxStatusText.setText(R.string.status_overdue);
                    binding.taxStatusText.setTextColor(
                            binding.getRoot().getContext().getResources().getColor(
                                    R.color.status_overdue, null));
                    break;
                default:
                    binding.taxStatusText.setText(tax.getStatus());
                    binding.taxStatusText.setTextColor(
                            binding.getRoot().getContext().getResources().getColor(
                                    R.color.text_primary, null));
                    break;
            }

            // Отображение типа налога
            binding.taxTypeText.setText(tax.getType());

            // Отображение иконки в зависимости от типа налога
            switch (tax.getType()) {
                case "УСН":
                    binding.taxTypeIcon.setImageResource(R.drawable.ic_tax_usn);
                    break;
                case "НДФЛ":
                    binding.taxTypeIcon.setImageResource(R.drawable.ic_tax_ndfl);
                    break;
                case "НДС":
                    binding.taxTypeIcon.setImageResource(R.drawable.ic_tax_nds);
                    break;
                case "Страховые взносы":
                    binding.taxTypeIcon.setImageResource(R.drawable.ic_tax_insurance);
                    break;
                default:
                    binding.taxTypeIcon.setImageResource(R.drawable.ic_tax_general);
                    break;
            }
        }
    }

    /**
     * Интерфейс для обработки клика по налогу
     */
    public interface OnTaxClickListener {
        void onTaxClick(Tax tax);
    }

    /**
     * DiffUtil.Callback для оптимизации обновлений списка
     */
    private static final DiffUtil.ItemCallback<Tax> DIFF_CALLBACK = new DiffUtil.ItemCallback<Tax>() {
        @Override
        public boolean areItemsTheSame(@NonNull Tax oldItem, @NonNull Tax newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Tax oldItem, @NonNull Tax newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getAmount() == newItem.getAmount() &&
                    oldItem.getStatus().equals(newItem.getStatus()) &&
                    oldItem.getType().equals(newItem.getType()) &&
                    oldItem.getDueDate().equals(newItem.getDueDate());
        }
    };
}
