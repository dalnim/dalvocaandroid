package com.dalread.base;

public enum EnumMessageAction {

    ADD(0),
    DELETE(1),
    EDIT(2);

    private int id;

    EnumMessageAction(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
