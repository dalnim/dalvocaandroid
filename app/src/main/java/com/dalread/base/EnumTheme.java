package com.dalread.base;

import android.content.Context;

import com.dalread.R;

public enum EnumTheme {

    LIGHT(0, R.string.light_theme),
    DARK(1, R.string.dark_theme),
    SYSTEM_DEFAULT(2, R.string.system_default_theme);

    private int id;
    private int nameId;

    EnumTheme(int id, int nameId) {
        this.id = id;
        this.nameId = nameId;
    }

    public int getId() {
        return id;
    }

    public String getName(Context context) {
        return context.getString(nameId);
    }

    public static String[] getNames(Context context) {
        int count = values().length;
        String[] names = new String[count];
        for (int i = 0; i < count; i++) {
            names[i] = values()[i].getName(context);
        }
        return names;
    }

    public static String getNameFromId(Context context, int id) {
        int nameId = LIGHT.nameId;
        for (EnumTheme enumTheme : EnumTheme.values()) {
            if (enumTheme.id == id) {
                nameId = enumTheme.nameId;
                break;
            }
        }
        return context.getString(nameId);
    }
}
