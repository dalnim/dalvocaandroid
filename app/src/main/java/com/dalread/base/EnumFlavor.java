package com.dalread.base;

public enum EnumFlavor {

    ENGLISH_FREE("english_free"),
    DALVOCA("dalvoca"),
    ARAPLAYER("araplayer"),
    ARAHANJA("arahanja"),
    ARAKOICA("arakoica");

    private String name;

    EnumFlavor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
