package com.example.diplom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {
    private final List<MenuItemData> menuItems;

    public MainMenuAdapter(List<MenuItemData> menuItems) {
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(menuItems.get(position));
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconView;
        private final TextView titleView;
        private final TextView descriptionView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconView = itemView.findViewById(R.id.menu_item_icon);
            titleView = itemView.findViewById(R.id.menu_item_title);
            descriptionView = itemView.findViewById(R.id.menu_item_description);
        }

        void bind(MenuItemData item) {
            iconView.setImageResource(item.getIconResId());
            titleView.setText(item.getTitleResId());
            descriptionView.setText(item.getDescriptionResId());
            itemView.setOnClickListener(v -> item.getClickAction().run());
        }
    }
}