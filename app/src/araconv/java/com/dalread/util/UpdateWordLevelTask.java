package com.dalread.util;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.SubDatabase;

public class UpdateWordLevelTask implements Runnable {
    private Context context;
    private SharedPreferencesDB sharedPreferences;
    private SubDatabase subDatabase;
    private OnTaskCompleteListener listener;
    public interface OnTaskCompleteListener {
        void onTaskComplete();
    }

    public UpdateWordLevelTask(Context context, OnTaskCompleteListener listener) {
        this.context = context;
        this.listener = listener;
        this.sharedPreferences = SharedPreferencesDB.getInstance(context);
        String destPathWithFileName = BaseStorageUtil.getAraConvDBPathWithFileName(context);
        subDatabase = SubDatabase.getInstance(context, destPathWithFileName);
    }

    @Override
    public void run() {
        try {
            subDatabase.resetWordLevelInWord();
            subDatabase.updateWordLevelInWord(LanguageUtil.getWordLevelByLanguageLevel(context, sharedPreferences.getSettingMyLanguageLevel()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (subDatabase != null) {
                subDatabase.close();
            }
            if (listener != null) {
                listener.onTaskComplete();
            }
        }
    }
}
