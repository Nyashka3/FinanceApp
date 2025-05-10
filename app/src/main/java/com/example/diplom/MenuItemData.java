package com.example.diplom;

public class MenuItemData {
    private final int iconResId;
    private final int titleResId;
    private final int descriptionResId;
    private final Runnable clickAction;

    public MenuItemData(int iconResId, int titleResId, int descriptionResId, Runnable clickAction) {
        this.iconResId = iconResId;
        this.titleResId = titleResId;
        this.descriptionResId = descriptionResId;
        this.clickAction = clickAction;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public Runnable getClickAction() {
        return clickAction;
    }
}