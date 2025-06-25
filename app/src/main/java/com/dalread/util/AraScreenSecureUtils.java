package com.dalread.util;

import android.app.Activity;
import android.view.WindowManager;

import com.dalread.database.SharedPreferencesDB;

public class AraScreenSecureUtils {
  public static void enableSecureFlag(Activity activity) {
    SharedPreferencesDB preferencesDB = SharedPreferencesDB.getInstance(activity);
    if (!preferencesDB.getEnableSecureScreen()) {
      activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
              WindowManager.LayoutParams.FLAG_SECURE);
    }
  }
  public static void disableSecureFlag(Activity activity) {
    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
  }
}