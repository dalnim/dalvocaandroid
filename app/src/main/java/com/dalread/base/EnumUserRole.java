package com.dalread.base;

public enum EnumUserRole {

    STUDENT_NATIVE_SPEAKER(4),
    STUDENT_TUTOR_NATIVE_SPEAKER(6);

    private int role;

    EnumUserRole(int role) {
        this.role = role;
    }

    public int getRole() {
        return role;
    }
}
