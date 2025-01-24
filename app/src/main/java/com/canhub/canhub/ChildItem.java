package com.canhub.canhub;


public class ChildItem {
    private int iconResId;
    private String text;

    public ChildItem(int iconResId, String text) {
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
