package com.example.diplom.expenses.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplom.R;
import com.example.diplom.database.entities.Expense;
import com.example.diplom.databinding.ItemExpenseBinding;
import com.example.diplom.utils.CurrencyFormatter;
import com.example.diplom.utils.DateUtils;

/**
 * Адаптер для отображения списка расходов в RecyclerView
 */
public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder> {

    private OnExpenseClickListener listener;

    public ExpenseAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExpenseBinding binding = ItemExpenseBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ExpenseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = getItem(position);
        holder.bind(expense);
    }

    /**
     * Установка слушателя для клика по расходу
     * @param listener слушатель
     */
    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    /**
     * ViewHolder для расхода
     */
    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final ItemExpenseBinding binding;

        public ExpenseViewHolder(@NonNull ItemExpenseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Установка слушателя клика
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onExpenseClick(getItem(position));
                }
            });
        }

        /**
         * Привязка данных к View
         * @param expense расход для отображения
         */
        public void bind(Expense expense) {
            binding.expenseTitleText.setText(expense.getTitle());
            binding.expenseAmountText.setText(CurrencyFormatter.format(expense.getAmount()));

            // Отображение даты расхода
            if (expense.getExpenseDate() != null) {
                if (DateUtils.isToday(expense.getExpenseDate())) {
                    binding.expenseDateText.setText(R.string.today);
                } else {
                    binding.expenseDateText.setText(DateUtils.formatDate(expense.getExpenseDate()));
                }
            } else {
                binding.expenseDateText.setText("");
            }

            // Отображение описания, если оно есть
            if (expense.getDescription() != null && !expense.getDescription().isEmpty()) {
                binding.expenseDescriptionText.setText(expense.getDescription());
                binding.expenseDescriptionText.setVisibility(View.VISIBLE);
            } else {
                binding.expenseDescriptionText.setVisibility(View.GONE);
            }

            // Отображение иконок для материальных и трудовых затрат
            binding.materialCostIcon.setVisibility(expense.isMaterialCost() ? View.VISIBLE : View.GONE);
            binding.laborCostIcon.setVisibility(expense.isLaborCost() ? View.VISIBLE : View.GONE);

            // Отображение категории
            // Здесь должна быть логика для получения категории по ID
            // Это упрощенный вариант для примера
            binding.categoryChip.setText("Категория " + expense.getCategoryId());
        }
    }

    /**
     * Интерфейс для обработки клика по расходу
     */
    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    /**
     * DiffUtil.Callback для оптимизации обновлений списка
     */
    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK = new DiffUtil.ItemCallback<Expense>() {
        @Override
        public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getAmount() == newItem.getAmount() &&
                    equals(oldItem.getExpenseDate(), newItem.getExpenseDate()) &&
                    equals(oldItem.getDescription(), newItem.getDescription()) &&
                    equals(oldItem.getCategoryId(), newItem.getCategoryId()) &&
                    oldItem.isMaterialCost() == newItem.isMaterialCost() &&
                    oldItem.isLaborCost() == newItem.isLaborCost();
        }

        /**
         * Сравнение объектов с учетом null
         */
        private boolean equals(Object a, Object b) {
            return (a == null) ? (b == null) : a.equals(b);
        }
    };
}
