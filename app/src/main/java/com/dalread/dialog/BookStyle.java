package com.dalread.dialog;

public enum BookStyle {
    PAGE(0),
    TABLE(1);

    private int style;

    BookStyle(int style) {
        this.style = style;
    }

    public int getValue() {
        return style;
    }
}
