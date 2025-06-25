package com.dalread.model;

import com.stfalcon.chatkit.commons.models.IUser;

public class ChatUser implements IUser {

    private String id;
    private String name;

    public ChatUser(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return null;
    }
}
