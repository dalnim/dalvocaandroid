package com.dalread.base;

import android.content.Context;

import com.dalread.R;

public enum EnumWorkbookType {

    WORD(1, R.string.wordbook_type_word),
    SENTENCE(2, R.string.wordbook_type_sentence);

    private int id;
    private int nameId;

    EnumWorkbookType(int id, int nameId) {
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
}
