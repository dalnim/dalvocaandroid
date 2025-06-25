package com.dalread.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Base64;

import com.dalread.BaseApplication;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.AlertDialog;

public class NetworkUtil {

    public static final String TAG = "NetworkUtil";

    public static String getProtocol(int type) {
        switch (type) {
            case Constant.PLAYER.SERVER.TYPE.FTP:
            case Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD:
                return Constant.PLAYER.SERVER.PROTOCOL.FTP;
            case Constant.PLAYER.SERVER.TYPE.WEBDAV:
                return Constant.PLAYER.SERVER.PROTOCOL.WEBDAV;
        }
        return Constant.BASE_BLANK;
    }

    public static String generateBasicAuth(String value) {
        return "Basic " + Base64.encodeToString(value.getBytes(), Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public static String getDefaultUserAgent() {
        StringBuilder result = new StringBuilder(64);
        result.append("Dalvik/");
        result.append(System.getProperty("java.vm.version")); // such as 1.1.0
        result.append(" (Linux; U; Android ");

        String version = Build.VERSION.RELEASE; // "1.0" or "3.4b5"
        result.append(version.length() > 0 ? version : "1.0");

        // add the model for the release build
        if ("REL".equals(Build.VERSION.CODENAME)) {
            String model = Build.MODEL;
            if (model.length() > 0) {
                result.append("; ");
                result.append(model);
            }
        }
        String id = Build.ID; // "MASTER" or "M4-rc20"
        if (id.length() > 0) {
            result.append(" Build/");
            result.append(id);
        }
        result.append(")");
        return result.toString();
    }

    //TODO : Need to move Utils.isConnected here too.
    /**
     * Check if there is any connectivity
     *
     * @param context It's mean activity
     * @return true if has connected to internet, false if no connection
     */
    public static boolean isNetworkConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Get the network info
     *
     * @param context It's mean activity
     * @return a networkInfo
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context It's mean activity
     * @return true if has connected to internet, false if no connection
     */
//    public static boolean isConnected(Context context) {
//        NetworkInfo info = getNetworkInfo(context);
//        return (info != null && info.isConnected());
//    }

    public static boolean isNetworkConnetedIfNotDontShowWarning(Context context) {
        if (isNetworkConnected(context)) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkConnetedIfNotShowWarningAsToast(Context context) {
        if (isNetworkConnected(context)) {
            return true;
        }
        ToastUtil.getInstance(context).showErrorNetwork();
        return false;
    }

    public static boolean isNetworkConnetedIfNotShowWarningAsPopup(Context context) {
        if (isNetworkConnected(context)) {
            return true;
        }
        AlertDialog alertDialog = new AlertDialog(context);
        alertDialog.showNoInternet();
        return false;
    }

    public static void updateLastAccessDate(BaseApplication application, int accessOrExitApp) {
        if (Utils.isReleaseMode()) {
//            ToastUtil.getInstance(application.getBaseContext()).show("updateLastAccessDate is executed");
            Context context = application.getBaseContext();
            SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
            int uid = UserUtil.getUserID(context);
            if (uid > 0 && Utils.isConnected(context)) {
                application.getDalAiImpl().updateLastAccessDate(
                        String.valueOf(uid),
                        sharedPreferences.getLangStudyCode(),
                        sharedPreferences.getEmail(),
                        accessOrExitApp
                );
            }
        } else {
//            ToastUtil.getInstance(application.getBaseContext()).show("updateLastAccessDate is skipped");
        }
    }

}
