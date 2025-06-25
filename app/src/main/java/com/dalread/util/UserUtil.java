package com.dalread.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dalread.R;
import com.dalread.base.EnumUserType;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.AlertDialog;

public class UserUtil {

    public static final String TAG = "UserUtil";

    public static boolean isLoggedIn(Context context, boolean isShowLoginView) {
        //Don't check log in AraPlayer and AraMusci
        if (AppFlavorUtil.isAraPlayerApp() || AppFlavorUtil.isAraMusicApp() || AppFlavorUtil.isAraHanjaApp())
            return true;

        AlertDialog alertDialog = new AlertDialog(context);
        if (!isNetworkConnected(context)) {
            alertDialog.showNoInternet();
            return false;
        }

        if (!SharedPreferencesDB.getInstance(context).isLogIn()) {
            if (isShowLoginView) {
                alertDialog.showLogInRequired(isShowLoginView);
            }
            return false;
        }
        return true;
    }

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

    public static int getVocabooksUsedByUserType(Context context) {
        int result = Constant.VOCABOOKS.USED.USE_FOR_LOGIN_USER;
        int userType = SharedPreferencesDB.getInstance(context).getUserType();
        if (userType == EnumUserType.SYSTEM_MANAGER.getType()) {
            result = Constant.VOCABOOKS.USED.USE_FOR_ADMIN;
        } else if (userType == EnumUserType.EDIT_CONTENT_BASIC.getType()) {
            result = Constant.VOCABOOKS.USED.USE_FOR_LOGIN_USER;
        } else if (userType == EnumUserType.EDIT_CONTENT_HALF.getType()
                || userType == EnumUserType.EDIT_CONTENT_FULL.getType()) {
            result = Constant.VOCABOOKS.USED.USE_FOR_TEST;
        }
        return result;
    }

    public static boolean isAdminUser(Context context) {
        if (SharedPreferencesDB.getInstance(context).getUserType() == EnumUserType.SYSTEM_MANAGER.getType()) {
            return true;
        }
        return false;
    }

    public static boolean isAdminUser(int userType) {
        if (userType == EnumUserType.SYSTEM_MANAGER.getType()) {
            return true;
        }
        return false;
    }

    public static boolean isAdminUserByUserType(int userType) {
        if (userType == EnumUserType.SYSTEM_MANAGER.getType()) {
            return true;
        }
        return false;
    }

    public static boolean isNormalUser(Context context) {
        int userType = SharedPreferencesDB.getInstance(context).getUserType();
        return userType == EnumUserType.NORMAL.getType();
    }

    public static boolean isEditContentFullOrAdminUser(Context context) {
        int userType = SharedPreferencesDB.getInstance(context).getUserType();
        if ((userType == EnumUserType.EDIT_CONTENT_FULL.getType())
                || (userType == EnumUserType.SYSTEM_MANAGER.getType())){
            return true;
        }
        return false;
    }

    public static boolean canEditContentAfterAppUseCount(Context context) {
        return SharedPreferencesDB.getInstance(context).getAppUseCount() > Constant.CAN_EDIT_CONTENT_AFTER_APP_USE_COUNT;
    }
    public static boolean isEditContentUser(Context context) {
        if (UserUtil.isDebugOrAdminUser(context)) {
            return true;
        }
        int userType = SharedPreferencesDB.getInstance(context).getUserType();
        if (userType == EnumUserType.NORMAL.getType()) {
            return false;
        }
        return true;
    }

    public static int getUserID(Context context) {
        return SharedPreferencesDB.getInstance(context).getUidDefault();
    }

    public static boolean isLoggedIn(Context context) {
        return SharedPreferencesDB.getInstance(context).isUID();
    }

    public static boolean isDebugOrAdminUser(Context context) {
        if (Utils.isDebug() || isAdminUser(context)) {
            return true;
        }
        return false;
    }

    public static String getLangLevel(Context context, String languageLevel) {
        String result = context.getString(R.string.sign_up_word_level_beginner);
        switch (languageLevel) {
            case Constant.SIGN_UP_LEVEL_PRE_INTERMEDIATE:
                result = context.getString(R.string.sign_up_word_level_pre_intermediate);
                break;
            case Constant.SIGN_UP_LEVEL_INTERMEDIATE:
                result = context.getString(R.string.sign_up_word_level_intermediate);
                break;
            case Constant.SIGN_UP_LEVEL_POST_INTERMEDIATE:
                result = context.getString(R.string.sign_up_word_level_post_intermediate);
                break;
            case Constant.SIGN_UP_LEVEL_ADVANCED:
                result = context.getString(R.string.sign_up_word_level_advanced);
                break;
        }
        return result;
    }
}
