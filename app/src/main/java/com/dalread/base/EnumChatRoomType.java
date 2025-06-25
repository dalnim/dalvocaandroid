package com.dalread.base;

public enum EnumChatRoomType {

    ONE_ON_ONE(1),
    MULTI(2),
    CLASS(3);

    private int id;

    EnumChatRoomType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
