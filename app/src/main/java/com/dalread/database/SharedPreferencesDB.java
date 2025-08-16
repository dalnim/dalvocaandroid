package com.dalread.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.dalread.R;
import com.dalread.base.EnumGpt;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumTheme;
import com.dalread.base.EnumUserRole;
import com.dalread.dialog.BookStyle;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.Constant;
import com.dalread.util.LanguageUtil;
import com.dalread.util.LocaleHelper;
import com.dalread.util.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;


public class SharedPreferencesDB {
    private SharedPreferences dataStorage;
    private static SharedPreferencesDB instance;

    public static SharedPreferencesDB getInstance(Context context) {
        if (instance == null)
            instance = new SharedPreferencesDB(context);
        return instance;
    }

    public SharedPreferencesDB(Context context) {
        dataStorage = context.getSharedPreferences(Constant.BASE_SHARED_NAME,
                Context.MODE_PRIVATE);
    }

    public String getPreferenceValue(String key) {
        String value = "";
        try {
            value = dataStorage.getString(key, Constant.BASE_BLANK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public String getPreferenceValue(String key, String defaultValue) {
        String value = "";
        try {
            value = dataStorage.getString(key, defaultValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public int getPreferenceIntValue(String key, int defValue) {
        int value = defValue;
        try {
            value = dataStorage.getInt(key, defValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public long getPreferenceLongValue(String key, long defValue) {
        long value = defValue;
        try {
            value = dataStorage.getLong(key, defValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public float getPreferenceFloatValue(String key, float defValue) {
        float value = defValue;
        try {
            value = dataStorage.getFloat(key, defValue);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public boolean getPreferenceBooleanValue(String key) {
        boolean value = false;
        try {
            value = dataStorage.getBoolean(key, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public boolean getPreferenceBooleanValue(String key, boolean isDefault) {
        boolean value = false;
        try {
            value = dataStorage.getBoolean(key, isDefault);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return value;
    }

    public void setPreferenceValue(String key, String value) {
        try {
            SharedPreferences.Editor editor = dataStorage.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPreferenceIntValue(String key, int value) {
        try {
            SharedPreferences.Editor editor = dataStorage.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPreferenceLongValue(String key, long value) {
        try {
            SharedPreferences.Editor editor = dataStorage.edit();
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPreferenceFloatValue(String key, float value) {
        try {
            SharedPreferences.Editor editor = dataStorage.edit();
            editor.putFloat(key, value);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPreferenceBooleanValue(String key, boolean value) {
        try {
            SharedPreferences.Editor editor = dataStorage.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public <T> void setObjectToJson(String key, T object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);

        setPreferenceValue(key, json);
    }


    public <T> void setList(String key, List<T> list) {
        if (list == null || list.isEmpty()) {
            setPreferenceValue(key, null);
            return;
        }
        setObjectToJson(key, list);
    }

    public void setSignUpResult(final String signUpResult) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_SIGNUP_RESULT, signUpResult);
    }

    public String getSignUpResult() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_SIGNUP_RESULT);
    }

    public void setPointReading(final int pointReading) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_READING, pointReading);
    }

    public int getPointReading() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_READING, 0);
    }
    //아직 서버랑 연동안하고 앱 내부적으로만 쓴다. 광고를 보면 포인트를 추가해주는 씩으로...
    public void setPointGpt(final int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_GPT, value);
    }
    //아직 서버랑 연동안하고 앱 내부적으로만 쓴다. 광고를 보면 포인트를 추가해주는 씩으로...
    public int getPointGpt() {
        int defaultPoint = 300;
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_GPT, defaultPoint);
    }

    public void setPointMultiPlayer(final int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_MULTI_PLAYER, value);
    }
    public int getPointMultiPlayer(boolean isMultiPlayer) {
        int defaultPoint = 6;
        if (isMultiPlayer) {
            defaultPoint = 2;
        }
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_MULTI_PLAYER, defaultPoint);
    }

    public void setPointAraHanja(final int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_ARA_HANJA, value);
    }
    public int getPointAraHanja() {
        int defaultPoint = 100;
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_ARA_HANJA, defaultPoint);
    }

    public boolean isSampleVideoCopied() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_SAMPLE_VIDEO_COPIED, false);
    }

    public void setSampleVideoCopied() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_SAMPLE_VIDEO_COPIED, true);
    }

    public boolean isPointAdded() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_POINT_ADDED, false);
    }

    public void setPointAdded(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_POINT_ADDED, value);
    }

    public void setMultiPlayerScreenCount(final int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_MULTI_PLAYER_SCREEN_COUNT, value);
    }

    public int getMultiPlayerScreenCount() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_MULTI_PLAYER_SCREEN_COUNT, -1);
    }

    public void setMultiPlayerScreenOrientation(final int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_MULTI_PLAYER_SCREEN_ORIENTATION, value);
    }

    public int getMultiPlayerScreenOrientation() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_MULTI_PLAYER_SCREEN_ORIENTATION, -1);
    }

    public void setPointVoca(final int pointVoca) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_VOCA, pointVoca);
    }

    public void setFirstABRepeatUse() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_AB_REPEAT_USE, false);
    }

    public boolean isFirstABRepeatUse() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_AB_REPEAT_USE, true);
    }

    public void setFirstChatGptWebUse() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_CHAT_GPT_WEB_USE, false);
    }

    public boolean isFirstChatGptWebUse() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_CHAT_GPT_WEB_USE, true);
    }

    public void setSecondChatGptWebUse() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_SECOND_CHAT_GPT_WEB_USE, false);
    }

    public boolean isSecondChatGptWebUse() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_SECOND_CHAT_GPT_WEB_USE, true);
    }

    public void setFirstHideMultiPlayerScreenTabLayout() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_HIDE_MULTI_PLAYER_SCREEN_TAB_LAYOUT, false);
    }

    public boolean isFirstHideMultiPlayerScreenTabLayout() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_HIDE_MULTI_PLAYER_SCREEN_TAB_LAYOUT, true);
    }

    public void setFirstShowGuideHowToUseMultiPlayView() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAY_VIEW, false);
    }

    public boolean isFirstShowGuideHowToUseMultiPlayView() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAY_VIEW, true);
    }

    public void setCountShowTextViewVideoFoldersLocationsInMultiPlayer(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_SHOW_TEXT_VIEW_VIDEO_FOLDERS_LOCATIONS_IN_MULTI_PLAYER, value);
    }

    public int getCountShowTextViewVideoFoldersLocationsInMultiPlayer() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_SHOW_TEXT_VIEW_VIDEO_FOLDERS_LOCATIONS_IN_MULTI_PLAYER, 0);
    }

    public boolean isFirstShowGuideHowToUseMultiPlayerSelectVideo() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_SELECT_VIDEO, true);
    }

    public void setFirstShowGuideHowToUseMultiPlayerSelectVideo() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_SELECT_VIDEO, false);
    }

    public boolean isFirstShowGuideHowToUseMultiPlayerScreenStoredLayout() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_SELECT_STORED_LAYOUT, true);
    }

    public void setFirstShowGuideHowToUseMultiPlayerScreenStoredLayout() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_SELECT_STORED_LAYOUT, false);
    }

    public boolean isFirstShowGuideHowToUsePlayButtonAfterLongClickInMultiPlayer() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_PLAY_BUTTON_AFTER_LONG_CLICK_IN_MULTI_PLAYER, true);
    }

    public void setFirstShowGuideHowToUsePlayButtonAfterLongClickInMultiPlayer() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_PLAY_BUTTON_AFTER_LONG_CLICK_IN_MULTI_PLAYER, false);
    }

    public boolean isFirstShowVideoBackDropImage() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_VIDEO_BACK_DROP_IMAGE, true);
    }

    public void setFirstShowVideoBackDropImage() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_VIDEO_BACK_DROP_IMAGE, false);
    }

    public boolean isFirstShowGuideHowToUseMultiPlayerActivity() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_ACTIVITY, true);
    }

    public void setFirstShowGuideHowToUseMultiPlayerActivity() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_ACTIVITY, false);
    }

    public boolean isFirstSelectMultiVideosFromMenu() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_MULTI_VIDEOS_FROM_MENU, true);
    }

    public void setFirstSelectMultiVideosFromMenu() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_MULTI_VIDEOS_FROM_MENU, false);
    }

    public void setFirstShowGuideScreenCount() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_SCREEN_COUNT, false);
    }

    public boolean isFirstShowGuideScreenCount() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_SCREEN_COUNT, true);
    }

    public void setFirstShowGuidePointDeduction() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_POINT_DEDUCTION, false);
    }

    public boolean isFirstShowGuidePointDeduction() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_POINT_DEDUCTION, true);
    }

    public void setFirstShowGuideGetFreePoint() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_GET_FREE_POINT, false);
    }

    public boolean isFirstShowGuideGetFreePoint() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_GET_FREE_POINT, true);
    }

    public void setFirstShowManageExternalStorage() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_MANAGE_EXTERNAL_STORAGE, false);
    }

    public boolean isFirstShowManageExternalStorage() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_MANAGE_EXTERNAL_STORAGE, true);
    }

    public void setFirstShowManageExternalStorageInstalledUser() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_MANAGE_EXTERNAL_STORAGE_INSTALLED_USER, false);
    }

    public boolean isFirstShowManageExternalStorageInstalledUser() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_MANAGE_EXTERNAL_STORAGE_INSTALLED_USER, true);
    }


    public void setFirstShowExternalStorage() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_EXTERNAL_STORAGE, false);
    }

    public boolean isFirstShowExternalStorage() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_EXTERNAL_STORAGE, true);
    }

    public void setFirstShowGuideWatchAd() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_WATCH_AD, false);
    }

    public boolean isFirstShowGuideWatchAd() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_WATCH_AD, true);
    }

    public void setFirstShowGuidePlayButton() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_PLAY_BUTTON, false);
    }

    public boolean isFirstShowGuidePlayButton() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_PLAY_BUTTON, true);
    }

    public void setShowConfirmPopupToDeleteItem() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_SHOW_CONFIRM_POPUP_TO_DELETE_ITEM, false);
    }

    public boolean isShowConfirmPopupToDeleteItem() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_SHOW_CONFIRM_POPUP_TO_DELETE_ITEM, true);
    }

    public void setFirstShowGuideChangeVocaKnow() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_CHANGE_VOCA_KNOW, false);
    }

    public boolean isFirstShowGuideChangeVocaKnow() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_GUIDE_CHANGE_VOCA_KNOW, true);
    }

    public void setFirstConversationPracticeFab() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_FIRST_CONVERSATION_PRACTICE_FAB, false);
    }

    public boolean isFirstConversationPracticeFab() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_FIRST_CONVERSATION_PRACTICE_FAB, true);
    }

    public boolean isFirstShowAlertSelectManyScreenCount() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_ALERT_SELECT_MANY_SCREEN_COUNT, true);
    }

    public void setFirstShowAlertSelectManyScreenCount() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_IS_FIRST_SHOW_ALERT_SELECT_MANY_SCREEN_COUNT, false);
    }

    public int getPointVoca() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_POINT_VOCA, 0);
    }

    public void setUserRole(final int userRole) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_USER_ROLE, userRole);
    }

    public int getUserRole() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_USER_ROLE, 0);
    }

    public void setUserType(final int userType) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_USER_TYPE, userType);
    }

    public int getUserType() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_USER_TYPE, 0);
    }

    public void setFirebaseToken(String token) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_FIREBASE_TOKEN, token);
    }

    public String getFirebaseToken() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_FIREBASE_TOKEN);
    }

    public void setUid(final int uid) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_UID, uid);
    }

    public String getUid() {
        final int uid = getPreferenceIntValue(Constant.SHARE_PREF.KEY_UID, 0);
        if (uid <= 0)
            return Constant.API.DEFAULT_UID;
        return String.valueOf(uid);
    }

    public int getUidDefault() {
        final int uid = getPreferenceIntValue(Constant.SHARE_PREF.KEY_UID, 0);
        if (uid <= 0)
            return Utils.parseInt(Constant.API.DEFAULT_UID); // 1505
        return uid;
    }

    public boolean isUID() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_UID, 0) > 0;
    }

    public boolean isLogIn() {
        return getRealUid() > 0 && getRealUid() != Utils.parseInt(Constant.API.DEFAULT_UID);
    }


    public int getRealUid() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_UID, 0);
    }

    public void setEmail(final String email) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_EMAIL, email);
    }

    public String getEmail() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_EMAIL);
    }

    public void setUserName(final String name) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_NAME, name);
    }

    public String getUserName(Context mContext) {
        String name = getPreferenceValue(Constant.SHARE_PREF.KEY_NAME);
        if (Utils.isEmpty(name)) {
            name = mContext.getString(R.string.menu_user_name);
        }
        return name;
    }

    public String getUserName() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_NAME);
    }

    public void logout() {
        setUid(0);
//		setEmail(Constant.BASE_BLANK);
//		setSessionID(Constant.BASE_BLANK);
        setPointReading(0);
        setToken(Constant.BASE_BLANK);
        setUserName(Constant.BASE_BLANK);
        setPointVoca(0);
        setUserRole(0);
        setUserType(0);
    }

    public String getSettingMotherTongue() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_SETTING_MOTHER_TONGUE);
    }

    public int getSettingMotherTongueCode() {
        return EnumLanguage.findByFormatApi(getSettingMotherTongue()).getIdApi();
    }

    public void setSettingMotherTongue(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_SETTING_MOTHER_TONGUE, value);
    }

    public void setLastPlayedMovie(String path) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_LASTPLAYEDMOVIE, path);
    }

    public String getLastPlayedMovie() {
        String val = getPreferenceValue(Constant.SHARE_PREF.KEY_LASTPLAYEDMOVIE, null);
        return val;
    }

    public int getSettingIndexMotherTongue() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SETTING_INDEX_MOTHER_TONGUE, 0);
    }

    public void setSettingIndexMotherTongue(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SETTING_INDEX_MOTHER_TONGUE, value);
    }

    public boolean getSettingWordMeaning() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SETTING_SHOW_WORD_MEANING, true);
    }

    public void setSettingWordMeaning(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SETTING_SHOW_WORD_MEANING, value);
    }

    public boolean getSettingPronounce() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SETTING_SHOW_PRONOUNCE, true);
    }

    public void setSettingPronounce(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SETTING_SHOW_PRONOUNCE, value);
    }

    public boolean getFirstLaunchApp() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_FIRST_LAUNCH_APP, true);
    }

    public void setFirstLaunchApp(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_FIRST_LAUNCH_APP, value);
    }

    public boolean isAskedIgnoreBatteryOptimization() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ASKED_TO_IGNORE_BATTERY_OPTIMIZATION, false);
    }

    public void setAskedToIgnoreBatteryOptimization(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ASKED_TO_IGNORE_BATTERY_OPTIMIZATION, value);
    }

    public boolean isShowIntroductionView() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_INTRODUCTION_VIEW, true);
    }

    public void setShowIntroductionView(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_INTRODUCTION_VIEW, value);
    }

    public String getLastUrl() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_LAST_URL);
    }

    public void setLastUrl(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_LAST_URL, value);
    }

    public String getCopiedText() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_COPIED_TEXT);
    }

    public void setCopiedText(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_COPIED_TEXT, value);
    }

    public String getLanguage() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_LANGUAGE);
    }

    public void setLanguage(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_LANGUAGE, value);
    }

    public String getCookie() {
        return getToken();
    }

    public String getToken() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_TOKEN);
    }

    public void setToken(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_TOKEN, value);
    }

//	public String getSessionID() {
//		return getPreferenceValue(Constant.SHARE_PREF.KEY_SESSION);
//	}
//
//	public void setSessionID(String value) {
//		setPreferenceValue(Constant.SHARE_PREF.KEY_SESSION, value);
//	}

    public int getSettingTTSSpeed() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED, Constant.SETTINGS.SETTING_TTS_SPEED);
    }

    public void setSettingTTSSpeed(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED, value);
    }

    public int getSettingTTSSpeedChinese() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_CHINESE, Constant.SETTINGS.SETTING_TTS_SPEED_CHINESE);
    }

    //DalVoca can change Study Language
    public void setSettingTTSSpeedChinese(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_CHINESE, value);
    }

    public int getSettingTTSSpeedEnglish() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_ENGLISH, Constant.SETTINGS.SETTING_TTS_SPEED_ENGLISH);
    }

    public void setSettingTTSSpeedEnglish(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_ENGLISH, value);
    }

    public int getSettingTTSSpeedJapanese() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_JAPANESE, Constant.SETTINGS.SETTING_TTS_SPEED_JAPANESE);
    }
    //DalVoca can change Study Language
    public void setSettingTTSSpeedJapanese(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_JAPANESE, value);
    }

    public int getSettingTTSSpeedKorean() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_KOREAN, Constant.SETTINGS.SETTING_TTS_SPEED_KOREAN);
    }
    //DalVoca can change Study Language
    public void setSettingTTSSpeedKorean(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_KOREAN, value);
    }

    public int getSettingTTSSpeedHanja() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_HANJA, Constant.SETTINGS.SETTING_TTS_SPEED_HANJA);
    }
    //DalVoca can change Study Language
    public void setSettingTTSSpeedHanja(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_TTS_SPEED_HANJA, value);
    }

    public String getSettingMyLanguageLevel() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_SETTING_MY_LANGUAGE_LEVEL, "");
    }

    public void setSettingMyLanguageLevel(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_SETTING_MY_LANGUAGE_LEVEL, value);
    }

    public String getMakeRolePlayingContent() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_MAKE_ROLE_PLAYING_CONTENT, "");
    }

    public void setMakeRolePlayingContent(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_MAKE_ROLE_PLAYING_CONTENT, value);
    }

    public String getChatGptWebMenu() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_CHAT_GPT_WEB_MENU, "");
    }

    public void setConversationToStudyInGptWeb(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_CONVERSATION_TO_STUDY_IN_GPT_WEB, value);
    }

    public void setChoiceCopySentenceConvPractice(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_CHOICE_COPY_SENTENCE_CONV_PRACTICE, value);
    }

    public int getChoiceOpenGptFromAraConvConversationView() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_CHOICE_OPEN_GPT_FROM_ARA_CONV_CONVERSATION_VIEW, 0);
    }
    public void setChoiceOpenGptFromAraConvConversationView(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_CHOICE_OPEN_GPT_FROM_ARA_CONV_CONVERSATION_VIEW, value);
    }
    public int getChoiceCopySentenceConvPractice() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_CHOICE_COPY_SENTENCE_CONV_PRACTICE, 1);
    }
    public void setChoiceSubtitleVocaMeaningToCopy(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_CHOICE_SUBTITLE_VOCA_MEANING_TO_COPY, value);
    }

    public int getChoiceSubtitleVocaMeaningToCopy() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_CHOICE_SUBTITLE_VOCA_MEANING_TO_COPY, 1);
    }
    public void setChoiceAskWhatToDoWithCopiedText(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_CHOICE_ASK_WHAT_TO_DO_WITH_COPIED_TEXT, value);
    }

    public int getChoiceAskWhatToDoWithCopiedText() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_CHOICE_ASK_WHAT_TO_DO_WITH_COPIED_TEXT, 1);
    }

    public String getConversationToStudyInGptWeb() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_CONVERSATION_TO_STUDY_IN_GPT_WEB, "");
    }

    public void setChatGptWebMenu(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_CHAT_GPT_WEB_MENU, value);
    }

//    public String getChatGptWebTextShortCut() {
//        return getPreferenceValue(Constant.SHARE_PREF.KEY_CHAT_GPT_WEB_TEXT_SHORT_CUT, "");
//    }
//
//    public void setChatGptWebTextShortCut(String value) {
//        setPreferenceValue(Constant.SHARE_PREF.KEY_CHAT_GPT_WEB_TEXT_SHORT_CUT, value);
//    }
    //playbacksetting settings

    public void setSettingSeekTime(int value) {
        setPreferenceIntValue(Constant.PLAYBACK_SETTINGS.SEEK_TIME, value);
    }

    public int getSettingSeekTime() {
        return getPreferenceIntValue(Constant.PLAYBACK_SETTINGS.SEEK_TIME, 30000);
    }

    public void setSeekType(String type) {
        setPreferenceValue(Constant.PLAYBACK_SETTINGS.SEEK_TYPE, type);
    }

    public String getSeekType() {
        return getPreferenceValue(Constant.PLAYBACK_SETTINGS.SEEK_TYPE, "Fast");
    }

    public void setPlayBackground(boolean playOrNot) {
        setPreferenceBooleanValue(Constant.PLAYBACK_SETTINGS.PLAY_BACKGROUND, playOrNot);
    }

    public boolean getPlayBackground() {
        return getPreferenceBooleanValue(Constant.PLAYBACK_SETTINGS.PLAY_BACKGROUND,
                false);
    }

    public void setShowSubtitle(boolean show) {
        setPreferenceBooleanValue(Constant.GLOBAL_SUBTITLE_SETTINGS.SHOW_SUBTITLE, show);
    }

    public boolean getShowSubtitle() {
        return getPreferenceBooleanValue(Constant.GLOBAL_SUBTITLE_SETTINGS.SHOW_SUBTITLE,
                true);
    }

    public void setTextOutline(boolean outline) {
        setPreferenceBooleanValue(Constant.GLOBAL_SUBTITLE_SETTINGS.TEXT_OUTLINE, outline);
    }

    public boolean getTextOutline() {
        return getPreferenceBooleanValue(Constant.GLOBAL_SUBTITLE_SETTINGS.TEXT_OUTLINE, true);
    }

    public void setFont(String font) {
        setPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT, font);
    }

    public String getFont() {
        return getPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT, "noto.ttf");
    }

    public void setFontType(String type) {
        setPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_TYPE, type);
    }

    public String getFontType() {
        return getPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_TYPE, "Normal");
    }

    public void setFontSize(int size) {
        setPreferenceIntValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_SIZE, size);
    }

    public int getFontSize() {
        return getPreferenceIntValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_SIZE, 12);
    }

    public void setLineHeight(int height) {
        setPreferenceIntValue(Constant.GLOBAL_SUBTITLE_SETTINGS.LINE_HEIGHT, height);
    }

    public int getLineHeight() {
        return getPreferenceIntValue(Constant.GLOBAL_SUBTITLE_SETTINGS.LINE_HEIGHT, 100);
    }

    public void setUnknownWordColor(String color) {
        setPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.UNKOWN_WORD_COLOR, color);
    }

    public String getUnkownWordColor() {
        return getPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.UNKOWN_WORD_COLOR);
    }

    public void setKnownWordColor(String color) {
        setPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.KNOWN_WORD_COLOR, color);
    }

    public String getKownWordColor() {
        return getPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.KNOWN_WORD_COLOR);
    }

    public void setFontSizeMeaning(int size) {
        setPreferenceIntValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_SIZE_MEANING, size);
    }

    public int getFontSizeMeaning() {
        return getPreferenceIntValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_SIZE_MEANING, 10);
    }

    public void setFontSizePronounce(int size) {
        setPreferenceIntValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_SIZE_PRONOUNCE, size);
    }

    public int getFontSizePronounce() {
        return getPreferenceIntValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_SIZE_PRONOUNCE, 8);
    }

    public void setFontColorPronounce(String color) {
        setPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_COLOR_PRONOUNCE, color);
    }

    public String getFontColorPronounce() {
        return getPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_COLOR_PRONOUNCE);
    }

    public void setFontColorMeaning(String color) {
        setPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_COLOR_MEANING, color);
    }

    public String getFontColorMeaning() {
        return getPreferenceValue(Constant.GLOBAL_SUBTITLE_SETTINGS.FONT_COLOR_MEANING);
    }

    public void setStudyLanguage(String formatApi) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_STUDY_LANG, formatApi);
    }

    public String getStudyLanguage() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_STUDY_LANG);
    }

    public int getLangStudyCode() {
        return EnumLanguage.findByFormatApi(getStudyLanguage()).getIdApi();
    }

    //Mother Tongue?
    public void setMotherTongueLanguage(String formatApi) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_MOTHER_LANG, formatApi);
    }
    //Mother Tongue, was getDisplayLanguage
    public String getMotherTongueLanguage() {
        String language = getPreferenceValue(Constant.SHARE_PREF.KEY_MOTHER_LANG);
        if (language.isEmpty()) {
            language = EnumLanguage.findByFormatOs(LocaleHelper.getCurrentLanguage()).getFormatApi();
            setMotherTongueLanguage(language);
//            setMotherTongueLanguage(language = EnumLanguage.ENGLISH.getFormatApi());
        }
        return language;
    }
//Was getLangMeaningCode
    public int getMotherTongueLangCode() {
        return EnumLanguage.findByFormatApi(getMotherTongueLanguage()).getIdApi();
    }
    public boolean hasMotherTongue() {
        return !Utils.isEmpty(getPreferenceValue(Constant.SHARE_PREF.KEY_MOTHER_LANG));
    }

    public void setMenuLanguage(String formatApi) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_MENU_LANG, formatApi);
    }

    public String getMenuLanguage() {
        String language = getPreferenceValue(Constant.SHARE_PREF.KEY_MENU_LANG);
        if (language.isEmpty()) {
            setMenuLanguage(language = getMotherTongueLanguage());
        }
        return language;
    }

    public void setReadCount(String readCount) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_READ_COUNT, readCount);
    }

    public String getReadCount() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_READ_COUNT, String.valueOf(Constant.READ_COUNT_DEFAULT));
    }

    public void setIncludeMyVoice(boolean include) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_INCLUDE_MY_VOICE, include);
    }

    public boolean getIncludeMyVoice() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_INCLUDE_MY_VOICE, true);
    }

    public void setEnableSecureScreen(boolean include) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ENABLE_SECURE_SCREEN, include);
    }

    public boolean getEnableSecureScreen() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ENABLE_SECURE_SCREEN, false);
    }

    public void setIncludeMeaning(boolean include) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_INCLUDE_MEANING, include);
    }

    public boolean getIncludeMeaning() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_INCLUDE_MEANING, true);
    }
    public void setDisplayEnglishMeaningToo(boolean include) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_DISPLAY_ENGLISH_MEANING_TOO, include);
    }

    public boolean getDisplayEnglishMeaningToo() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_DISPLAY_ENGLISH_MEANING_TOO, false);
    }

    public void setKeepDisplayingBackgroundHintWhenWriting(boolean include) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_KEEP_DISPLAYING_BACKGROUND_HINT_WHEN_WRITING, include);
    }

    public boolean getKeepDisplayingBackgroundHintWhenWriting() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_KEEP_DISPLAYING_BACKGROUND_HINT_WHEN_WRITING, true);
    }

    public void setBackgroundMode(boolean backgroundMode) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_BACKGROUND_MODE, backgroundMode);
    }

    public boolean getBackgroundMode() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_BACKGROUND_MODE, true);
    }

    public void setPreferredNativeSpeakers(String speakerIds) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_PREFERRED_NATIVE_SPEAKERS, speakerIds);
    }

    public String getPreferredNativeSpeakers() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_PREFERRED_NATIVE_SPEAKERS);
    }

    public void setPreferredNativeSpeakersCount(int count) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PREFERRED_NATIVE_SPEAKERS_COUNT, count);
    }

    public int getPreferredNativeSpeakersCount() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PREFERRED_NATIVE_SPEAKERS_COUNT, 0);
    }

    public void setDisplayPronunciation(boolean display) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_DISPLAY_PRONUNCIATION, display);
    }
    //이건 달보카용인거 같다.
    public boolean getDisplayPronunciation() {
        return getPreferenceBooleanValue(
                Constant.SHARE_PREF.KEY_DISPLAY_PRONUNCIATION,
                getUserRole() == EnumUserRole.STUDENT_TUTOR_NATIVE_SPEAKER.getRole()
                        ? Constant.DISPLAY_PRONUNCIATION_DEFAULT_OTHER
                        : Constant.DISPLAY_PRONUNCIATION_DEFAULT_STUDENT
        );
    }

    public boolean getDisplayPronunciation(Context context) {
        if (AppFlavorUtil.isAraHangulApp()
            || LanguageUtil.isStudyLangChinese(context)) {
            return true;
        }

        return false;
    }

    public void setAlertAmkiGrade1(boolean alert) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALERT_AMKI_GRADE_1, alert);
    }

    public boolean getAlertAmkiGrade1() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALERT_AMKI_GRADE_1, true);
    }

    public void setAlertAmkiGrade2(boolean alert) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALERT_AMKI_GRADE_2, alert);
    }

    public boolean getAlertAmkiGrade2() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALERT_AMKI_GRADE_2, true);
    }

    public void setAlertAmkiGradeKnown(boolean alert) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALERT_AMKI_GRADE_KNOWN, alert);
    }

    public boolean getAlertAmkiGradeKnown() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALERT_AMKI_GRADE_KNOWN, true);
    }

    public void setAlertAmkiGradeUnknown(boolean alert) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALERT_AMKI_GRADE_UNKNOWN, alert);
    }

    public boolean getAlertAmkiGradeUnknown() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALERT_AMKI_GRADE_UNKNOWN, true);
    }

    public void setSex(int sex) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SEX, sex);
    }

    public int getSex() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SEX, Constant.SEX.MALE);
    }

    public void setAge(int age) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_AGE, age);
    }

    public int getAge() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_AGE, Constant.AGE.TEENAGER);
    }

    public void setMaxHomework(int maxHomework) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_MAX_HOMEWORK, maxHomework);
    }

    public int getMaxHomework() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_MAX_HOMEWORK, Constant.MAX_HOMEWORK_DEFAULT);
    }

    public void setMaxQuiz(int maxQuiz) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_MAX_QUIZ, maxQuiz);
    }

    public int getMaxQuiz() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_MAX_QUIZ, Constant.MAX_QUIZ_DEFAULT);
    }

    public void setMaxHanjaQuiz(int maxHanjaQuiz) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_MAX_HANJA_QUIZ, maxHanjaQuiz);
    }

    public int getMaxHanjaQuiz() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_MAX_HANJA_QUIZ, Constant.MAX_HANJA_QUIZ_DEFAULT);
    }

    public void setAllowChat(boolean allowChat) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALLOW_CHAT, allowChat);
    }

    public boolean getAllowChat() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ALLOW_CHAT, Constant.ALLOW_CHAT_DEFAULT);
    }

    public void setBlinkModeInWordList(boolean blinkModeInWordList) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_BLINKMODE_IN_WORDLIST, blinkModeInWordList);
    }

    public boolean getBlinkModeInWordList() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_BLINKMODE_IN_WORDLIST, Constant.BLINKMOOE_IN_WORDLIST_DEFAULT);
    }

    public void setAsteriskModeInWordList(boolean asteriskModeInWordList) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ASTERISKMODE_IN_WORDLIST, asteriskModeInWordList);
    }

    public boolean getAsteriskModeInWordList() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_ASTERISKMODE_IN_WORDLIST, Constant.ASTERISKMOOE_IN_WORDLIST_DEFAULT);
    }

    public void setShareMyRecordings(boolean shareMyRecordings) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHARE_MY_RECORDINGS, shareMyRecordings);
    }

    public boolean getShareMyRecordings() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHARE_MY_RECORDINGS, Constant.SHARE_MY_RECORDING_DEFAULT);
    }

    public void setAutoPlayInRecordingAll(boolean autoPlay) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_AUTO_PLAY_IN_RECORDING_ALL, autoPlay);
    }

    public boolean getAutoPlayInRecordingAll() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_AUTO_PLAY_IN_RECORDING_ALL, Constant.AUTO_PLAY_IN_RECORDING_ALL_DEFAULT);
    }

    public void setUseLessonNotification(boolean useLessonNotification) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_USE_LESSON_NOTIFICATION, useLessonNotification);
    }

    public boolean getUseLessonNotification() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_USE_LESSON_NOTIFICATION, Constant.USE_LESSON_NOTIFICATION_DEFAULT);
    }

    public void setTimeToNotifyBeforeLessonStart(int minutes) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_TIME_TO_NOTIFY_BEFORE_LESSON_START, minutes);
    }

    public int getTimeToNotifyBeforeLessonStart() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_TIME_TO_NOTIFY_BEFORE_LESSON_START, Constant.TIME_TO_NOTIFY_BEFORE_LESSON_START_DEFAULT);
    }

    public void setTimeToNotifyBeforeLessonFinish(int minutes) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_TIME_TO_NOTIFY_BEFORE_LESSON_FINISH, minutes);
    }

    public int getTimeToNotifyBeforeLessonFinish() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_TIME_TO_NOTIFY_BEFORE_LESSON_FINISH, Constant.TIME_TO_NOTIFY_BEFORE_LESSON_FINISH_DEFAULT);
    }

    public void setBaseUrl(String baseUrl) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_BASE_URL_VALUE, baseUrl);
    }

    public String getBaseUrl() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_BASE_URL_VALUE, Constant.BASE_API_URL);
    }

    public void setBaseCustomUrl(String baseUrl) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_BASE_CUSTOM_URL_VALUE, baseUrl);
    }

    public String getBaseCustomUrl() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_BASE_CUSTOM_URL_VALUE, Constant.BASE_API_URL_LOCAL_WORK_LAN);
    }

    public void setBaseUrlIndex(int position) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_BASE_URL_INDEX, position);
    }

    public int getBaseUrlIndex() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_BASE_URL_INDEX, Constant.BASE_API_URL_INDEX);
    }

    public void setPlayerLastPlayed(String path) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_PLAYER_LAST_PLAYED, path);
    }

    public String getPlayerLastPlayed() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_PLAYER_LAST_PLAYED, Constant.BASE_BLANK);
    }

    public void setPlayerLastPlayedHidedVideo(String path) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_PLAYER_LAST_PLAYED_HIDED_VIDEO, path);
    }

    public String getPlayerLastPlayedHidedVideo() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_PLAYER_LAST_PLAYED_HIDED_VIDEO, Constant.BASE_BLANK);
    }

    public void setPlayerFileSort(int sort) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_FILE_SORT, sort);
    }

    public int getPlayerFileSort() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_FILE_SORT, Constant.PLAYER.SORT.FILE_NAME_ASC);
    }

    public void setShowLoginWhenChangeServer(boolean showLogin) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_CHANGE_SERVER_SHOW_LOGIN, showLogin);
    }

    public boolean getShowLoginWhenChangeServer() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_CHANGE_SERVER_SHOW_LOGIN, false);
    }

    public void setPlayerSubtitleModel(String subtitleModel) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_PLAYER_SUBTITLE_MODE, subtitleModel);
    }

    public String getPlayerSubtitleModel() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_PLAYER_SUBTITLE_MODE, Constant.BASE_BLANK);
    }

    public void setAraHanjaFontSizeRatio(float fontSizeRatio) {
        setPreferenceFloatValue(Constant.SHARE_PREF.KEY_ARA_HANJA_FONT_SIZE, fontSizeRatio);
    }

    public float getAraHanjaFontSizeRatio(Context context) {
        return getPreferenceFloatValue(Constant.SHARE_PREF.KEY_ARA_HANJA_FONT_SIZE, 1.0f);
    }

    public void setPlayerSubtitleFontSize(float fontSize) {
        setPreferenceFloatValue(Constant.SHARE_PREF.KEY_PLAYER_SUBTITLE_FONT_SIZE, fontSize);
    }

    public float getPlayerSubtitleFontSize(Context context) {
        return getPreferenceFloatValue(Constant.SHARE_PREF.KEY_PLAYER_SUBTITLE_FONT_SIZE, Utils.getSPValue(context, R.dimen.font_player_subtitle_ruby));
    }

    public void setPlayerSubtitleFullScreenFontSize(float fontSize) {
        setPreferenceFloatValue(Constant.SHARE_PREF.KEY_PLAYER_SUBTITLE_FULL_SCREEN_FONT_SIZE, fontSize);
    }

    public float getPlayerSubtitleFullScreenFontSize(Context context) {
        return getPreferenceFloatValue(Constant.SHARE_PREF.KEY_PLAYER_SUBTITLE_FULL_SCREEN_FONT_SIZE, Utils.getSPValue(context, R.dimen.font_player_subtitle_ruby_fullscreen));
    }

    public void setPlayerSwipeForBackWard(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_SWIPE_FOR_BACK_WARD, value);
    }

    public int getPlayerSwipeForBackWard() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_SWIPE_FOR_BACK_WARD, Constant.PLAYER.SETTING.SWIPE_FOR_BACK_WARD_DEFAULT);
    }

    public void setPlayerSwipeForBackWardIndex(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_SWIPE_FOR_BACK_WARD_INDEX, value);
    }

    public int getPlayerSwipeForBackWardIndex() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_SWIPE_FOR_BACK_WARD_INDEX, Constant.PLAYER.SETTING.SWIPE_FOR_BACK_WARD_INDEX_DEFAULT);
    }

    public void setPlayerTapForBackWard(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_TAP_FOR_BACK_WARD, value);
    }

    public int getPlayerTapForBackWard() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_TAP_FOR_BACK_WARD, Constant.PLAYER.SETTING.TAP_FOR_BACK_WARD_DEFAULT);
    }

    public void setPlayerTapForBackWardIndex(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_TAP_FOR_BACK_WARD_INDEX, value);
    }

    public int getPlayerTapForBackWardIndex() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_TAP_FOR_BACK_WARD_INDEX, Constant.PLAYER.SETTING.TAP_FOR_BACK_WARD_INDEX_DEFAULT);
    }

    public void setIncludeMotherTongueSubtitle(boolean isEnable) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_INCLUDE_MOTHER_TONGUE_SUBTITLE, isEnable);
    }

    public boolean getIncludeMotherTongueSubtitle() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_INCLUDE_MOTHER_TONGUE_SUBTITLE, false);
    }

    public void setPlayDifficultWordsBeforePlayingSubtitles(boolean isEnable) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_PLAY_DIFFICULT_WORDS_BEFORE_PLAYING_SUBTITLES, isEnable);
    }

    public boolean getPlayDifficultWordsBeforePlayingSubtitles() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_PLAY_DIFFICULT_WORDS_BEFORE_PLAYING_SUBTITLES, false);
    }

    public void setPlayTitleByTtsBeforePlaying(boolean isEnable) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_PLAY_TITLE_BY_TTS_BEFORE_PLAYING, isEnable);
    }

    public boolean getPlayAllDifficultWordsBeforePlayingMusic() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_PLAY_ALL_DIFFICULT_WORDS_BEFORE_PLAYING_MUSIC, true);
    }

    public void setPlayAllDifficultWordsBeforePlayingMusic(boolean isEnable) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_PLAY_ALL_DIFFICULT_WORDS_BEFORE_PLAYING_MUSIC, isEnable);
    }

    public boolean getPlayTitleByTtsBeforePlaying() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_PLAY_TITLE_BY_TTS_BEFORE_PLAYING, false);
    }

    public void setDisplayStartEndTimeInSubtitleView(boolean isEnable) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_DISPLAY_START_END_TIME_IN_SUBTITLE_VIEW, isEnable);
    }

    public boolean getDisplayStartEndTimeInSubtitleView() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_DISPLAY_START_END_TIME_IN_SUBTITLE_VIEW, false);
    }

    public void setSyncKnownWithServer(boolean isEnable) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SYNC_KNOWN_WITH_SERVER, isEnable);
    }

    public boolean getSyncKnownWithServer() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SYNC_KNOWN_WITH_SERVER, true);
    }

    public void setSyncSubtitlesAtServer(boolean isEnable) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_SYNC_SUBTITLES_AT_SERVER, isEnable);
    }

    public boolean getSyncSubtitlesAtServer() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_SYNC_SUBTITLES_AT_SERVER, true);
    }

    public void setHideKnownDialogsDuringPlaying(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_HIDE_KNOWN_DIALOGS_DURING_PLAYING, value);
    }

    public boolean getHideKnownDialogsDuringPlaying() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_HIDE_KNOWN_DIALOGS_DURING_PLAYING, true);
    }

//    public boolean getSubtitleToHideChosenHided() {
//        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_CHOSEN_HIDED, true);
//    }
//
//    public void setSubtitleToHideChosenHided(boolean value) {
//        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_CHOSEN_HIDED, value);
//    }

    public boolean getSubtitleToHideMusicSymbol() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_MUSIC_SYMBOL, true);
    }

    public void setSubtitleToHideMusicSymbol(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_MUSIC_SYMBOL, value);
    }

    public boolean getSubtitleToHidePairedBracket() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_PAIRED_BRACKET, true);
    }

    public void setSubtitleToHidePairedBracket(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_PAIRED_BRACKET, value);
    }

    public boolean getSubtitleToHideAllCapitals() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_ALL_CAPITALS, true);
    }

    public void setSubtitleToHideAllCapitals(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_ALL_CAPITALS, value);
    }

    public boolean getSubtitleToHideIncludingUrl() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_INCLUDING_URL, true);
    }

    public void setSubtitleToHideIncludingUrl(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_INCLUDING_URL, value);
    }

    public boolean getSubtitleToHideNoStudyLangCharacter() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_NO_STUDY_LANG_CHARACTER, true);
    }

    public void setSubtitleToHideNoStudyLangCharacter(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_NO_STUDY_LANG_CHARACTER, value);
    }

    public boolean getSubtitleToHideShorter3KnownWords() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS, true);
    }

    public void setSubtitleToHideShorter3KnownWords(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS, value);
    }

    public int getSubtitleToHideShorter3KnownWordsNumber() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NUMBER, Constant.PLAYER.DEFAULT_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NUMBER_DEFAULT);
    }

    public void setSubtitleToHideShorter3KnownWordsNumbere(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NUMBER, value);
    }

    public boolean getSubtitleToHide1Word() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_1_WORD, true);
    }

    public void setSubtitleToHide1Word(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_1_WORD, value);
    }

    public boolean getSubtitleToHideRepeated1Word() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_REPEATED_1_WORD, true);
    }

    public void setSubtitleToHideRepeated1Word(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_REPEATED_1_WORD, value);
    }

    public boolean getSubtitleToHideLonger20Words() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_LONGER_20_WORDS, true);
    }

    public void setSubtitleToHideLonger20Words(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_LONGER_20_WORDS, value);
    }

    public int getSubtitleToHideLonger20WordsNumber() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER, Constant.PLAYER.DEFAULT_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER_DEFAULT);
    }

    public void setSubtitleToHideLonger20WordsNumbere(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER, value);
    }

    public boolean getSubtitleToHideExceptUnknownSubtitles() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_EXCEPT_UNKNOWN_SUBTITLES, true);
    }

    public void setSubtitleToHideExceptUnknownSubtitles(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_EXCEPT_UNKNOWN_SUBTITLES, value);
    }

    public boolean getSubtitleToHideKnownSubtitles() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_KNOWN_SUBTITLES, true);
    }

    public void setSubtitleToHideKnownSubtitles(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SUBTITLE_TO_HIDE_KNOWN_SUBTITLES, value);
    }

    public int getPlayerListenComprehensionPlaySubtitlesAtOnce() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_AT_ONCE, Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_AT_ONCE_DEFAULT_INDEX);
    }

    public void setPlayerListenComprehensionPlaySubtitlesAtOnce(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_AT_ONCE, value);
    }

    public int getPlayerListenComprehensionPlaySubtitlesAtOnceIndex() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_AT_ONCE_INDEX, Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_AT_ONCE_DEFAULT_INDEX);
    }

    public void setPlayerListenComprehensionPlaySubtitlesAtOnceIndex(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_AT_ONCE_INDEX, value);
    }

    public int getPlayerListenComprehensionPlaySubtitlesPlayParts() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_PLAY_PARTS, Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_PLAY_PARTS_DEFAULT_INDEX);
    }

    public void setPlayerListenComprehensionPlaySubtitlesPlayParts(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_PLAY_PARTS, value);
    }

    public int getPlayerListenComprehensionPlaySubtitlesPlayPartsIndex() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_PLAY_PARTS_INDEX, Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.PLAY_SUBTITLES_PLAY_PARTS_DEFAULT_INDEX);
    }

    public void setPlayerListenComprehensionPlaySubtitlesPlayPartsIndex(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_PLAY_PARTS_INDEX, value);
    }

    public void setShowNormalVideoFileList(boolean isEnable) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_SHOW_NORMAL_VIDEO_FILE_LIST, isEnable);
    }

    public boolean getShowNormalVideoFileList() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_SHOW_NORMAL_VIDEO_FILE_LIST, true);
    }

//    public void setShowVideoInDCIM(boolean isEnable) {
//        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_SHOW_VIDEO_IN_DCIM, isEnable);
//    }
//
//    public boolean getShowVideoInDCIM() {
//        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_SHOW_VIDEO_IN_DCIM, true);
//    }
//
    public void setPlayerMaxQuizCount(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_MAX_QUIZ_COUNT, value);
    }

    public int getPlayerMaxQuizCount() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_MAX_QUIZ_COUNT, Constant.PLAYER.QUIZ.MAX_QUIZ_COUNT_DEFAULT);
    }

    public void setPlayerQuizType(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_QUIZ_TYPE, value);
    }

    public int getPlayerQuizType() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_PLAYER_QUIZ_TYPE, Constant.PLAYER.QUIZ.QUIZ_TYPE_DEFAULT);
    }

    public void setPlayerFetchAllVideoFirstTime(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_GET_ALL_VIDEO_FIRST_TIME, value);
    }

    public boolean isPlayerFetchAllVideoFirstTime() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_GET_ALL_VIDEO_FIRST_TIME, true);
    }

    public void setTranslateSubtitleFromServer(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_TRANSLATE_SUBTITLE_FROM_SERVER, value);
    }

    public boolean isUploadRecordingAutomatically() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_UPLOAD_RECORDING_AUTOMATICALLY, false);
    }

    public void setUploadRecordingAutomatically(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_UPLOAD_RECORDING_AUTOMATICALLY, value);
    }



    public boolean getTranslateSubtitleFromServer() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAYER_TRANSLATE_SUBTITLE_FROM_SERVER, false);
    }

    public String getTranslateSubtitleFromServerValue() {
        return String.valueOf(getTranslateSubtitleFromServer() ? 1 : 0);
    }

    public int getDatabaseVersion() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_DATABASE_VERSION, 0);
    }

    public void setDatabaseVersion(int version) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_DATABASE_VERSION, version);
    }

    public int getVideoThumbnailInterval() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_VIDEO_THUMBNAIL_INTERVAL, 10000);
    }

    public void setVideoThumbnailInterval(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_VIDEO_THUMBNAIL_INTERVAL, value);
    }

    public int getSelectedVideoThumbailAutoScrollIntervalCountValue() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SELECTED_VIDEO_THUMBNAIL_AUTO_SCROLL_INTERVAL, 19);
    }

    public void setSelectedVideoThumbailAutoScrollIntervalCountValue(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SELECTED_VIDEO_THUMBNAIL_AUTO_SCROLL_INTERVAL, value);
    }

    public int getVideoThumbnailCountInARow() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_VIDEO_THUMBNAIL_COUNT_IN_A_ROW, 1);
    }

    public void setVideoThumbnailCountInARow(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_VIDEO_THUMBNAIL_COUNT_IN_A_ROW, value);
    }

    public void setPlayOnlyDialogsInListenComprehension(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_ONLY_DIALOGS_IN_LISTEN_COMPREHENSION, value);
    }

    public boolean getPlayOnlyDialogsInListenComprehension() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_ONLY_DIALOGS_IN_LISTEN_COMPREHENSION, false);
    }

    public int getFreePointCallCount() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_FREE_POINT_CALL_COUNT, 0);
    }

    public void setFreePointCallCount(int count) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_FREE_POINT_CALL_COUNT, count);
    }

    public int getCountOf1stAmkiGrade() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_OF_1ST_AMKI_GRADE, 0);
    }

    public void setCountOf1stAmkiGrade(int count) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_OF_1ST_AMKI_GRADE, count);
    }

    public int getDelaySubtitleMinMaxValue() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_DELAY_SUBTILE_MIN_MAX_VALUE, Constant.PLAYER.DELAY_SUBTITLE.MAX_TIME);
    }

    public void setDelaySubtitleMinMaxValue(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_DELAY_SUBTILE_MIN_MAX_VALUE, value);
    }

    public void setShowSubtitleTableWhenOpen(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_SUBTITLE_TABLE_WHEN_OPEN, value);
    }

    public boolean getShowSubtitleTableWhenOpen() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_SUBTITLE_TABLE_WHEN_OPEN, true);
    }

    public void setKeepPlayingOnBackgroundMode(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_KEEP_PLAYING_ON_BACKGROUND_MODE, value);
    }

    public boolean getKeepPlayingOnBackgroundMode() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_KEEP_PLAYING_ON_BACKGROUND_MODE, false);
    }

    public void setPlayVideoAutomaticallyWhenOpenIt(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_VIDEO_AUTOMATICALLY_WHEN_OPEN_IT, value);
    }

    public boolean getPlayVideoAutomaticallyWhenOpenIt() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_VIDEO_AUTOMATICALLY_WHEN_OPEN_IT, true);
    }

    public void setPlayVideoFromWhereYouLeft(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_VIDEO_FROM_WHERE_YOU_LEFT, value);
    }

    public boolean getPlayVideoFromWhereYouLeft() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_VIDEO_FROM_WHERE_YOU_LEFT, true);
    }

    public void setPlayMusicBetweenLyricsOnly(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_MUSIC_BETWEEN_LYRICS_ONLY, value);
    }
    public boolean getHide4ButtonsOnPlayingScreen() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_HIDE4_BUTTONS_ON_PLAYING_SCREEN, false);
    }

    public void setHide4ButtonsOnPlayingScreen(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_HIDE4_BUTTONS_ON_PLAYING_SCREEN, value);
    }

    public boolean isShowAdvancedMode() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_ADVANCED_MODE, true);
    }

    public void setShowAdvancedMode(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_ADVANCED_MODE, value);
    }

    public boolean isRandomPlayMode() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_RANDOM_PLAY_MODE, true);
    }

    public void setRandomPlayMode(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_RANDOM_PLAY_MODE, value);
    }

    public boolean getPlayMusicBetweenLyricsOnly() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_MUSIC_BETWEEN_LYRICS_ONLY, false);
    }

    public void setSelectedTheme(int themeId) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SELECTED_THEME, themeId);
    }

    public int getSelectedTheme() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SELECTED_THEME, EnumTheme.LIGHT.getId());
    }

    public void setSelectedGpt(int gptId) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SELECTED_GPT, gptId);
    }

    public int getSelectedGpt() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SELECTED_GPT, EnumGpt.CHATGPT.getId());
    }

    public void setPlayerWidthPercent(float percent) {
        setPreferenceFloatValue(Constant.SHARE_PREF.KEY_PLAYER_WIDTH_PERCENT, percent);
    }

    public float getPlayerWidthPercent() {
        return getPreferenceFloatValue(Constant.SHARE_PREF.KEY_PLAYER_WIDTH_PERCENT, Constant.DEFAULT_PLAYER_WIDTH_PERCENT);
    }

    public void setHiddenButtonsTransparencyOnFullScreen(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_HIDDEN_BUTTONS_TRANSPARENCY_ON_FULLSCREEN, value);
    }

    public int getHiddenButtonsTransparencyOnFullScreen() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_HIDDEN_BUTTONS_TRANSPARENCY_ON_FULLSCREEN, 85);
    }

    public void setSubtitleViewTransparencyOnFullScreen(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SUBTITLE_VIEW_TRANSPARENCY_ON_FULLSCREEN, value);
    }

    public int getSubtitleViewTransparencyOnFullScreen() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SUBTITLE_VIEW_TRANSPARENCY_ON_FULLSCREEN, 100);
    }

    public void setCountOfNotRatedWordBeforePlaying(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_OF_NOT_RATED_WORD_BEFORE_PLAYING, value);
    }

    public int getCountOfNotRatedWordBeforePlaying() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_OF_NOT_RATED_WORD_BEFORE_PLAYING, 5);
    }

    public void setCountOfAnalyzeVideo(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_OF_ANALYZE_VIDEO, value);
    }

    public int getCountOfAnalyzeVideo() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_OF_ANALYZE_VIDEO, 0);
    }

    public void setCountOfWatchVideo(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_OF_WATCH_VIDEO, value);
    }

    public int getCountOfWatchVideo() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_COUNT_OF_WATCH_VIDEO, 0);
    }

    public void setPasswordHiddenFiles(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_PASSWORD_HIDDEN_FILES, value);
    }

    public String getPasswordHiddenFiles() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_PASSWORD_HIDDEN_FILES);
    }

    public boolean isShowListenComprehnesion1UIAtFirstTime() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_LISTEN_COMPREHNESION_1_UI_AT_FIRST_TIME, true);
    }

    public void setDontShowListenComprehnesion1UIAtFirstTime() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_LISTEN_COMPREHNESION_1_UI_AT_FIRST_TIME, false);
    }

    public boolean isShowListenComprehnesion2UIAtFirstTime() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_LISTEN_COMPREHNESION_2_UI_AT_FIRST_TIME, true);
    }

    public void setDontShowListenComprehnesion2UIAtFirstTime() {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_LISTEN_COMPREHNESION_2_UI_AT_FIRST_TIME, false);
    }

    public void setLastAdsClickedTime(long value) {
        setPreferenceLongValue(Constant.SHARE_PREF.KEY_LAST_ADS_CLICKED_TIME, value);
    }

    public long getLastAdsClickedTime() {
        return getPreferenceLongValue(Constant.SHARE_PREF.KEY_LAST_ADS_CLICKED_TIME, 0);
    }

    public void setShowHuriganaForHanja(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SHOW_HURIGANA_FOR_HANJA, value);
    }

    public int getShowHuriganaForHanja() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SHOW_HURIGANA_FOR_HANJA, Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY);
    }

    public void setBookStyle(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_BOOK_STYLE, value);
    }

    public int getBookStyle() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_BOOK_STYLE, BookStyle.PAGE.getValue());
    }

    public void setShowSimplifiedChinese(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_SIMPLIFIED_CHINESE, value);
    }

    public boolean getShowSimplifiedChinese() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_SIMPLIFIED_CHINESE, true);
    }

    public void setSearchInStudyLang(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SEARCH_IN_STUDY_LANG, value);
    }

    public boolean getSearchInStudyLang() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SEARCH_IN_STUDY_LANG, true);
    }

    public void setShowBeyondLevelHanja(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_BEYOND_LEVEL_HANJA, value);
    }

    public boolean getShowBeyondLevelHanja() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_BEYOND_LEVEL_HANJA, true);
    }

    public void setFirstTimeToAdjustTableHeight(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_FIRST_TIME_TO_ADJUST_TABLE_HEIGHT, value);
    }

    public boolean isFirstTimeToAdjustTableHeight() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_FIRST_TIME_TO_ADJUST_TABLE_HEIGHT, true);
    }

    public void setPauseTimeToRepeatTts(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PAUSE_TIME_TO_REPEAT_TTS, value);
    }

    public boolean isPlayMotherTongueOnlyFirstRoundFinishOnConversation() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_MOTHER_TONGUE_ONLY_FIRST_ROUND_FINISH_ON_CONVERSATION, true);
    }

    public void setPlayMotherTongueOnlyFirstRoundFinishOnConversation(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_MOTHER_TONGUE_ONLY_FIRST_ROUND_FINISH_ON_CONVERSATION, value);
    }

    public boolean isPauseTimeToRepeatTts() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PAUSE_TIME_TO_REPEAT_TTS, false);
    }


    public void setRemoveBannerAds(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_REMOVE_BANNER_ADS, value);
    }

    public boolean isRemoveBannerAds() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_REMOVE_BANNER_ADS, false);
    }

    public void setPurchasedBannerAds(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PURCHASED_REMOVE_BANNER_ADS, value);
    }

    public boolean isPurchasedRemoveBannerAds() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PURCHASED_REMOVE_BANNER_ADS, false);
    }

    public void setPurchasedClassicBookThousandCharacter(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PURCHASED_CLASSIC_BOOK_THOUSAND_CHARACTER, value);
    }

    public boolean isPurchasedClassicBookThousandCharacter() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PURCHASED_CLASSIC_BOOK_THOUSAND_CHARACTER, false);
    }

    public void setPurchasedExportSubtitle(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PURCHASED_EXPORT_SUBTITLE, value);
    }

    public boolean isPurchasedExportSubtitle() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PURCHASED_EXPORT_SUBTITLE, false);
    }

    public void setToShowMergeSubtitlePopup(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_TO_SHOW_MERGE_SUBTITLE_POPUP, value);
    }

    public boolean isToShowMergeSubtitlePopup() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_TO_SHOW_MERGE_SUBTITLE_POPUP, true);
    }

    //마지막 Read Book Id가 없으면 -1을 리턴한다.
    public long getLastReadBookIdReal() {
        return getPreferenceLongValue(Constant.SHARE_PREF.KEY_LAST_READ_BOOK, -1);
    }

    public long getLastReadBookId() {
        long bookId = getLastReadBookIdReal();
        //영어가 아닐때도 대비해야 한다.
        if (bookId == -1) {
            bookId = 112;
        }
        return bookId;
    }

    public void setLastReadBookId(long value) {
        setPreferenceLongValue(Constant.SHARE_PREF.KEY_LAST_READ_BOOK, value);
    }

    public void setShowIctTerms(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_ICT_TERMS, value);
    }

    public boolean getShowIctTerms() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_ICT_TERMS, true);
    }

    public void setShowLocalTerms(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_LOCAL_TERMS, value);
    }

    public boolean getShowLocalTerms() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_LOCAL_TERMS, true);
    }

    public void setSelectedTranslatorId(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_SELECTED_TRANSLATOR_ID, value);
    }

    public int getSelectedTranslatorId() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_SELECTED_TRANSLATOR_ID, 0);
    }

    public void setMediaSleepValue(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_MEDIA_SLEEP_VALUE, value);
    }

    public int getMediaSleepValue() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_MEDIA_SLEEP_VALUE, 24);
    }

    public void setMediaListen2RepeatIndex(int value) {
        setPreferenceIntValue(Constant.SHARE_PREF.KEY_MEDIA_LISTEN2_REPEAT_INDEX, value);
    }

    public int getMediaListen2RepeatIndex() {
        return getPreferenceIntValue(Constant.SHARE_PREF.KEY_MEDIA_LISTEN2_REPEAT_INDEX, 1);
    }

    public void setOpenSubtitleLoginToken(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_APP_LOGIN_STATUS_OPEN_SUBTITLE, value);
    }

    public String getOpenSubtitleLoginToken() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_APP_LOGIN_STATUS_OPEN_SUBTITLE);
    }

    public void setConversationPracticeType(String value) {
        setPreferenceValue(Constant.SHARE_PREF.KEY_CONVERSATION_PRACTICE_TYPE, value);
    }

    public String getConversationPracticeType() {
        return getPreferenceValue(Constant.SHARE_PREF.KEY_CONVERSATION_PRACTICE_TYPE);
    }

    public boolean isShowMeaning() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_MEANING, true);
    }

    public void setShowMeaning(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_SHOW_MEANING, value);
    }

    public boolean isPlayTtsOnConversation() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_TTS_ON_CONVERSATION, false);
    }

    public void setPlayTtsOnConversation(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_PLAY_TTS_ON_CONVERSATION, value);
    }

    public boolean isVocaFilterVocaKnown() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_KNOWN, false);
    }

    public void setVocaFilterVocaKnown(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_KNOWN, value);
    }
    public boolean isVocaFilterVoca1st() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_1ST, true);
    }

    public void setVocaFilterVoca1st(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_1ST, value);
    }
    public boolean isVocaFilterVoca2nd() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_2ND, true);
    }

    public void setVocaFilterVoca2nd(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_2ND, value);
    }
    public boolean isVocaFilterVocaUnknown() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_UNKNOWN, true);
    }

    public void setVocaFilterVocaUnknown(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_UNKNOWN, value);
    }
    public boolean isVocaFilterVocaNotRated() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_NOT_RATED, true);
    }

    public void setVocaFilterVocaNotRated(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_NOT_RATED, value);
    }
    public boolean isVocaFilterVocaBookmarked() {
        return getPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_BOOKMARKED, true);
    }

    public void setVocaFilterVocaBookmarked(boolean value) {
        setPreferenceBooleanValue(Constant.SHARE_PREF.KEY_VOCA_FILTER_VOCA_BOOKMARKED, value);
    }
    public long getAppUseCount() {
        return getPreferenceLongValue(Constant.SHARE_PREF.KEY_APP_USE_COUNT, 0);
    }

    public void setAppUseCount(long count) {
        setPreferenceLongValue(Constant.SHARE_PREF.KEY_APP_USE_COUNT, count);
    }

    public static <T> T fromJson(String jsonString, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, type);
    }

    public static <T> String toJson(T object, Type type) {
        Gson gson = new Gson();
        return gson.toJson(object, type);
    }

    public <T> T getDataFromJson(String key) {
        String serializedObject = getPreferenceValue(key, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<T>() {
            }.getType();
            return gson.fromJson(serializedObject, type);
        }
        return null;
    }

    public <T> List<T> getListDataFromJson(String key, Class<T[]> clazz) {
        String serializedObject = getPreferenceValue(key, null);
        if (serializedObject != null) {
            T[] arr = new Gson().fromJson(serializedObject, clazz);
            return Arrays.asList(arr);
        }
        return null;
    }
}