package com.dalread.base;

public enum EnumUserType {

    NORMAL(0),
    EDIT_CONTENT_BASIC(800),
    EDIT_CONTENT_HALF(810),
    EDIT_CONTENT_FULL(820),
    SYSTEM_MANAGER(999);

    private int type;

    EnumUserType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
