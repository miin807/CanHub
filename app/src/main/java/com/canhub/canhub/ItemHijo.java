package com.canhub.canhub;


public class ItemHijo {
    private int iconResId;
    private String text;

    public ItemHijo(int iconResId, String text) {
        this.iconResId = iconResId;
        this.text = text;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getText() {
        return text;
    }
}
