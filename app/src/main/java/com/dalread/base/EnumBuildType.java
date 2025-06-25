package com.dalread.base;

public enum EnumBuildType {

    DEBUG("debug"),
    RELEASE("release");

    private String name;

    EnumBuildType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
