package com.dalread.base;

public enum EnumMessageType {

    TEXT(0),
    PHOTO(1),
    VOICE(2);

    private int id;

    EnumMessageType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
