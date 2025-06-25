package com.dalread.util;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

import com.dalread.base.EnumTheme;
import com.dalread.database.SharedPreferencesDB;

public class DarkThemeUtil {
    public static void setDarkModeOnCreate(Context context) {
        SharedPreferencesDB mSharedPref = SharedPreferencesDB.getInstance(context);
        int darkLightTheme = getDarkLightThemeFromStorage(mSharedPref);
        AppCompatDelegate.setDefaultNightMode(darkLightTheme);
    }
    public static int getDarkLightThemeFromStorage(SharedPreferencesDB sharedPref) {
        int selectedTheme = sharedPref.getSelectedTheme();
        if (selectedTheme == EnumTheme.SYSTEM_DEFAULT.getId()) {
            return AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        } else if (selectedTheme == EnumTheme.DARK.getId()) {
            return AppCompatDelegate.MODE_NIGHT_YES;
        } else {
            return AppCompatDelegate.MODE_NIGHT_NO;
        }
    }

    public static boolean isDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }
}
