package com.example.diplom.expenses.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diplom.database.entities.Category;
import com.example.diplom.databinding.ItemCategoryBinding;

/**
 * Адаптер для отображения категорий в RecyclerView.
 */
public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private OnCategoryClickListener listener;

    public CategoryAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = getItem(position);
        holder.bind(category);
    }

    /**
     * Установка слушателя для клика по категории
     * @param listener слушатель
     */
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    /**
     * ViewHolder для категории
     */
    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding binding;

        public CategoryViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Установка слушателя клика
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onCategoryClick(getItem(position));
                }
            });
        }

        /**
         * Привязка данных к View
         * @param category категория для отображения
         */
        public void bind(Category category) {
            binding.categoryNameText.setText(category.getName());
            binding.categoryIconImage.setImageResource(
                    binding.getRoot().getContext().getResources().getIdentifier(
                            category.getIcon(),
                            "drawable",
                            binding.getRoot().getContext().getPackageName()
                    )
            );

            try {
                binding.categoryColorIndicator.setBackgroundColor(
                        android.graphics.Color.parseColor(category.getColor())
                );
            } catch (Exception e) {
                // Если цвет некорректный, устанавливаем серый цвет по умолчанию
                binding.categoryColorIndicator.setBackgroundColor(android.graphics.Color.GRAY);
            }

            binding.categoryTypeText.setText(category.isExpense() ? "Расход" : "Доход");
        }
    }

    /**
     * Интерфейс для обработки клика по категории
     */
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    /**
     * DiffUtil.Callback для оптимизации обновлений списка
     */
    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getIcon().equals(newItem.getIcon()) &&
                    oldItem.getColor().equals(newItem.getColor()) &&
                    oldItem.isExpense() == newItem.isExpense();
        }
    };
}
