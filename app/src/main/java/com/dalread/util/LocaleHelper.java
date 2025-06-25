package com.dalread.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;

import java.util.Locale;

public class LocaleHelper {

    public static Context onAttach(Context context) {
        return setLocale(context, getPersistedData(context));
    }

    private static String getPersistedData(Context context) {
        SharedPreferencesDB preferences = SharedPreferencesDB.getInstance(context);
        return EnumLanguage.findByFormatApi(preferences.getMenuLanguage()).getFormatOs();
    }

    public static Context setLocale(Context context, String language) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }

        return updateResourcesLegacy(context, language);
    }

    public static void setLanguage(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language);
        } else {
            updateResourcesLegacy(context, language);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        return context.createConfigurationContext(configuration);
    }

    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static String getCurrentLanguage(){
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = Resources.getSystem().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            locale = Resources.getSystem().getConfiguration().locale;
        }
        return locale.getLanguage();
    }
}
