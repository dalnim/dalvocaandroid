package com.dalread.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.base.EnumBuildType;
import com.dalread.base.EnumLanguage;
import com.dalread.base.EnumUserType;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnUpdateRubyTextListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.BookMarkModel;
import com.dalread.model.ReadingListModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import io.realm.Realm;

public class Utils {
    public interface BatteryOptimizationCallback {
        void onYes();
        void onNo();
    }
    public static final String TAG = "Utils";

    public static String DATABASE_PATH = "",
            DATABASE_NAME = "MainDalMovie.sqlite";

    public static SQLiteDatabase db;

    public static void toggleFullscreen(Activity activity) {
        toggleFullscreen(activity, true);
    }

    public static void toggleFullscreen(Activity activity, boolean fullscreen) {
        if (fullscreen) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }
    public static void toggleFullscreenMultiPlayer(Activity activity, boolean fullscreen) {
        if (fullscreen) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //이게 있으면 상단의 statusBar가 보인다.(배터리 표시, 통신사 표시)
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
    }
    public static int getNextKeyBookmarkModel(Realm realm) {
        Number maxId = realm.where(BookMarkModel.class).max("id");
        return maxId == null ? 1 : (maxId.intValue() + 1);
    }

    public static long getNextKeyReadingListModel(Realm realm) {
        return realm.where(ReadingListModel.class).max("id").intValue() + 1;
    }

    //@deprecated use {@link #Utils.isEmpty(Collection<?>)} with
    @Deprecated
    public static boolean isEmptyCollection(Collection<?> list) {
        return list == null || list.size() == 0;
    }

    //Dalnim add
    public static boolean isEmptyMap(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isEmptyWithoutTrim(CharSequence str) {
        return str == null || str.toString().length() == 0;
    }

    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.size() == 0;
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.toString().trim().length() == 0;
    }

    public static boolean isNotEmpty(Collection<?> list) {
        return !isEmpty(list);
    }
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }
    public static boolean isIndexInsideRange(Collection<?> list, long index) {
        if (list == null)
            return false;
        if ((index >= 0) && (index < list.size()))
            return true;

        return false;
    }

    public static List<Integer> getRandomIndexList(List<?> list, int newListLength) {
        List<Integer> resultListTemp = new ArrayList<>();
        if (Utils.isEmpty(list))
            return resultListTemp;

        for (int i = 0; i < list.size(); i++) {
            resultListTemp.add(i);
        }

        // shuffle list
        Collections.shuffle(resultListTemp);

        int subListTo = newListLength < resultListTemp.size() ? newListLength : resultListTemp.size();
        List<Integer> randomResult = resultListTemp.subList(0, subListTo);
        Collections.sort(randomResult);
        return randomResult;
    }

    public static boolean hasValue(CharSequence str) {
        return str != null && str.toString().trim().length() > 0;
    }

    public static int parseInt(String data) {
        int val = 0;
        try {
            val = Integer.parseInt(data);
        } catch (Exception ex) {
            val = 0;
        }
        return val;
    }

    public static long parseLong(String data) {
        long val = 0;
        try {
            val = Long.parseLong(data.trim());
        } catch (Exception ex) {
            val = 0;
        }
        return val;
    }

    public static float parseFloat(String str) {
        float i;
        try {
            i = Float.parseFloat(str);
        } catch (Exception e) {
            i = 0;
        }
        return i;
    }

    public static String checkLinkString(String url) {
        if (isEmpty(url))
            return Constant.BASE_BLANK;
        if (!url.startsWith(Constant.URL_WWW) && !url.startsWith(Constant.URL_HTTP) && !url.startsWith(Constant.URL_HTTPS)) {
            url = Constant.URL_WWW + url;
        }
        if (!url.startsWith(Constant.URL_HTTP) && !url.startsWith(Constant.URL_HTTPS)) {
            url = Constant.URL_HTTP + url;
        }
        return url;
    }

    public static void openMail(Context mContext) {
        /* Create the Intent */
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        /* Fill it with Data */
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{Constant.MAIL.DALNIM_BEST});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.send_mail_subject));
        emailIntent.putExtra(Intent.EXTRA_TEXT, mContext.getString(R.string.send_mail_body));
        /* Send it off to the Activity-Chooser */
        mContext.startActivity(Intent.createChooser(emailIntent, mContext.getString(R.string.send_mail_title)));
    }

    public static void openMail(Context mContext, String[] toEmail, String subject, String content) {
        /* Create the Intent */
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        /* Fill it with Data */
        emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, toEmail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        /* Send it off to the Activity-Chooser */
        mContext.startActivity(Intent.createChooser(emailIntent, mContext.getString(R.string.send_mail_title)));
    }

    public static boolean isValidMail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean checkLengthPassword(String password) {
        return !Utils.isEmpty(password) && password.length() >= 4;
    }

    public static boolean checkLengthUserName(String userName) {
        return !Utils.isEmpty(userName) && userName.length() >= 2;
    }

//    public static String convertIndexToVoiceLang(int index) {
//        DLog.d(TAG, "convertIndexToVoiceLang - index=" + index);
//        if (index == 0) {
//            return Constant.VOICE_LANGS[getLocalLanguage()];
//        }
//        return Constant.VOICE_LANGS[index - 1];
//    }
//
//    public static int getLocalLanguage() {
//        final String mCurrentLang = Locale.getDefault().toString();
//        int mLanguage = 4;
//        DLog.d(TAG, "start mCurrentLang=" + mCurrentLang + " - mLanguage=" + mLanguage);
//        if (!isEmpty(mCurrentLang)) {
//            for (int i = 0; i < Constant.VOICE_LANGS.length; i++) {
//                if (Constant.VOICE_LANGS[i].contains(mCurrentLang)) {
//                    mLanguage = i;
//                    break;
//                }
//            }
//        }
//        DLog.d(TAG, "end mLanguage=" + mLanguage);
//        return mLanguage;
//    }

    public static String getHost(String url) {
        return Uri.parse(url).getHost();
    }

    /**
     * Hides the soft keyboard
     */
    /*
     @deprecated Replaced by {@link KeyboardUtil#hideSoftKeyboard(Context, View)}
     */
    @Deprecated
    public static void hideSoftKeyboard(Context mContext, View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*
     @deprecated Replaced by {@link KeyboardUtil#hideSoftKeyboard(Context)}
     */
    public static void hideSoftKeyboard(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    /**
     * Shows the soft keyboard
     */
    /*
     @deprecated Replaced by {@link KeyboardUtil#showSoftKeyboard(Context, EditText)}
     */
    public static void showSoftKeyboard(Context context, EditText editText) {
        editText.setCursorVisible(true);
        editText.requestFocus();
        if (editText.getText() != null) {
            editText.setSelection(editText.getText().length());
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
    /*
     @deprecated Replaced by {@link KeyboardUtil#showSoftKeyboard(Context)}
     */
    public static void showSoftKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
    public static boolean isConnected(Context context) {
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                }
            }
        }
        return isURL;
    }


    public static String getTextClipboard(Context mContext, boolean isCheckLength) {
        DLog.d(TAG, "getTextClipboard");
        String text = Constant.BASE_BLANK;
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        // Gets the clipboard data from the clipboard
        ClipData clip = clipboard.getPrimaryClip();
        if (clip != null) {
            // Gets the first item from the clipboard data
            ClipData.Item item = clip.getItemAt(0);
            text = coerceToText(mContext, item).toString();
        }
        if (isCheckLength) {
            if (!Utils.isEmpty(text) && text.length() > Constant.SHARE_PREF.MAX_LENGTH_COPIED_TEXT) {
                text = text.substring(0, Constant.SHARE_PREF.MAX_LENGTH_COPIED_TEXT);
            }
        }
        return text;
    }

    public static CharSequence coerceToText(Context context, ClipData.Item item) {
        // If this Item has an explicit textual value, simply return that.
        CharSequence text = item.getText();
        if (text != null) {
            return text;
        }
        // If this Item has a URI value, try using that.
        Uri uri = item.getUri();
        if (uri != null) {
            // First see if the URI can be opened as a plain text stream
            // (of any sub-type). If so, this is the best textual
            // representation for it.
            FileInputStream stream = null;
            try {
                // Ask for a stream of the desired type.
                AssetFileDescriptor descr = context.getContentResolver().openTypedAssetFileDescriptor(uri, "text/*", null);
                stream = descr.createInputStream();
                InputStreamReader reader = new InputStreamReader(stream, "UTF-8");

                // Got it... copy the stream into a local string and return it.
                StringBuilder builder = new StringBuilder(128);
                char[] buffer = new char[8192];
                int len;
                while ((len = reader.read(buffer)) > 0) {
                    builder.append(buffer, 0, len);
                }
                return builder.toString();
            } catch (FileNotFoundException e) {
                // Unable to open content URI as text... not really an
                // error, just something to ignore.
            } catch (IOException e) {
                // Something bad has happened.
                DLog.w(TAG, "Failure loading text", e);
//                return e.toString();
                return Constant.BASE_BLANK;

            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                    }
                }
            }
            // If we couldn't open the URI as a stream, then the URI itself
            // probably serves fairly well as a textual representation.
            return uri.toString();
        }
        // Finally, if all we have is an Intent, then we can just turn that
        // into text. Not the most user-friendly thing, but it's something.
        Intent intent = item.getIntent();
        if (intent != null) {
            return intent.toUri(Intent.URI_INTENT_SCHEME);
        }
        // Shouldn't get here, but just in case...
        return Constant.BASE_BLANK;
    }

    public static String cutWordJson(String json) {
        String value = "";
        try {
            if (!Utils.isEmpty(json)) {
                if (json.lastIndexOf("},") > 0) {
                    final String[] temp = json.trim().split(Pattern.quote("},"));
                    if (temp == null || temp.length <= 0) {
                        DLog.d(TAG, "temp is null");
                        return Constant.BASE_BLANK;
                    }
                    for (String t : temp) {
                        t += "}";
                        if (!Utils.isEmpty(value)) {
                            value += ",";
                        }
                        value += t.substring(t.lastIndexOf(":{") + 1, t.lastIndexOf("}") + 1);
                        DLog.d(TAG, "t=" + t);
                    }
                    value = "[" + value.substring(0, value.length() - 2) + "]";
                } else {
                    if (json.lastIndexOf(":{") > 0) {
                        value = "[" + json.substring(json.lastIndexOf(":{") + 1, json.lastIndexOf("}")) + "]";
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Constant.BASE_BLANK;
        }
        return value;
    }

    public static String cutWordListJson(String json) {
        String value = "";
        try {
            if (!Utils.isEmpty(json)) {
                if (json.indexOf(":") > 0) {
                    json = json.substring(json.indexOf(":") + 1, json.length() - 1);
                    DLog.d(TAG, "json substring : =" + json);
                    if (json.lastIndexOf(":") > 0) {
                        final String[] temp = json.trim().split(Pattern.quote("},"));
                        if (temp == null || temp.length <= 0)
                            return Constant.BASE_BLANK;
                        for (String t : temp) {
                            t += "}";
                            if (!Utils.isEmpty(value)) {
                                value += ",";
                            }
                            value += t.substring(t.lastIndexOf(":{") + 1, t.lastIndexOf("}") + 1);
                            DLog.d(TAG, "t=" + t);
                        }
                        value = "[" + value.substring(0, value.length() - 2) + "]";
                    } else {
                        if (json.lastIndexOf(":{") > 0) {
                            value = "[" + json.substring(json.lastIndexOf(":{") + 1, json.lastIndexOf("}")) + "]";
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Constant.BASE_BLANK;
        }
        return value;
    }

//    public static boolean isEnglishWord(String string) {
//        if (Utils.isEmpty(string))
//            return false;
//        final String[] temp = string.split(" ");
//        for (String s : temp) {
//            if (Constant.CHECK_LANGUAGES.VALID_ENGLISH_CHECK_RIGHT_LANGUAGE.matcher(s).find()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static Locale convertVoiceLangToLocale(String voiceLang) {
//        if (voiceLang.contains(Constant.SETTING_VOICE_LANGS.VOICELANG_JA)) {
//            return Locale.JAPAN;
//        } else if (voiceLang.contains(Constant.SETTING_VOICE_LANGS.VOICELANG_CH_S)) {
//            return Locale.CHINA;
//        }
//        return Locale.US;
//    }
//
//    public static boolean checkJapanWord(String s) {
//        for (int i = 0; i < s.length(); ) {
//            int c = s.codePointAt(i);
//            if (isJapaneseSpecific(c) || isJapaneseKanja(c))
//                return true;
//            i += Character.charCount(c);
//        }
//        return false;
//    }
//
//    public static boolean checkHiraganaAndKatakana(String s) {
//        for (int i = 0; i < s.length(); ) {
//            int c = s.codePointAt(i);
//            if (isHiragana(c) || isKatakana(c))
//                return true;
//            i += Character.charCount(c);
//        }
//        return false;
//    }
//
//    private static boolean isHiragana(int c) {
//        return (c >= 0x3040 && c <= 0x309f);
//    }
//
//    private static boolean isKatakana(int c) {
//        return (c >= 0x30a0 && c <= 0x30ff);
//    }
//
//    private static boolean isJapaneseSpecific(int c) {
//        return (c >= 0x3040 && c <= 0x30FF) || (c >= 0xFF01 && c <= 0xFF9F);
//    }
//
//    private static boolean isJapaneseKanja(int c) {
//        return (c >= 0x2E80 && c <= 0x2EFF) || (c >= 0x2F00 && c <= 0x2FDF) || (c >= 0x4E00 && c <= 0x9FAF);
//    }
//
//    private static boolean isHiragana(final char c) {
//        return (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HIRAGANA);
//    }
//
//    private static boolean isKatakana(final char c) {
//        return (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA);
//    }
//
//    public static boolean countJapanMatchRate(String text) {
//        int length = text.length();
//        for (int i = 0; i < length; i++) {
//            char ch = text.charAt(i);
//            if (isHiragana(ch) || isKatakana(ch)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static double countChineseMatchRate(String text) {
//        int count = 0;
//        int length = text.length();
//        for (int i = 0; i < length; i++) {
//            char ch = text.charAt(i);
//            Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
//            if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block) ||
//                    Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(block) ||
//                    Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(block)) {
//                count++;
//            }
//        }
//        if (count > 0)
//            return ((double) count / text.length());
//        return 0;
//    }
//
//    public static double countEnglishMatchRate(String text) {
//        DLog.d(TAG, "countEnglishMatchRate");
//        long count = 0;
//        for (char c : text.toCharArray()) {
//            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
//                count++;
//            }
//        }
//        DLog.d(TAG, "count=" + count + " - text.length()=" + text.length());
//        if (count > 0)
//            return ((double) count / text.length());
//        return 0;
//    }
//
//    public static String convertIndexToLanguage(int index) {
//        String value = Constant.BASE_BLANK;
//        switch (index) {
//            case 0:
//                value = getLanguageFromApp();
//                break;
//            case 2: // cn
//                value = Constant.LANGUAGES_SYSTEM.CN;
//                break;
//            case 5: // en
//                value = Constant.LANGUAGES_SYSTEM.EN;
//                break;
//            case 9: // ja
//                value = Constant.LANGUAGES_SYSTEM.JA;
//                break;
//            case 10: // ko
//                value = Constant.LANGUAGES_SYSTEM.KO;
//                break;
//            case 13: // vi
//                value = Constant.LANGUAGES_SYSTEM.VI;
//                break;
//            default:
//                value = Constant.LANGUAGES_SYSTEM.EN;
//                break;
//        }
//        return value;
//    }
//
//    public static String getLanguageFromApp() {
//        String mLanguage = Locale.getDefault().toString();
//        DLog.d(TAG, "mLanguage=" + mLanguage);
//        switch (mLanguage) {
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_ZH_CN:
//                return Constant.LANGUAGES_SYSTEM.CN;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_JA:
//                return Constant.LANGUAGES_SYSTEM.JA;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_KO:
//                return Constant.LANGUAGES_SYSTEM.KO;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_VI:
//                return Constant.LANGUAGES_SYSTEM.VI;
//            default:
//                return Constant.LANGUAGES_SYSTEM.EN;
//        }
//    }
//
//    public static boolean isChangeLanguages(SharedPreferencesDB sharedPreference, String mLang) {
//        final String shareLang = sharedPreference.getLanguage();
//        return !isEmpty(shareLang) && !shareLang.equalsIgnoreCase(mLang);
//    }
//
//    public static String getMotherTongueVoiceLang(String strMotherTongue) {
//        switch (strMotherTongue) {
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_AR:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_AR;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_S:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_CH_S;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_T:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_CH_T;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_DE:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_DE;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_ES:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_ES;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_FR:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_FR;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_IT:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_IT;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_JA:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_JA;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_KO:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_KO;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_PT:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_PT;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_RU:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_RU;
//            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_VI:
//                return Constant.SETTING_VOICE_LANGS.VOICELANG_VI;
//        }
//        return Constant.SETTING_VOICE_LANGS.VOICELANG_EN;
//    }
//
//    public static String getMotherTongueLangDisplay(int index) {
//        if (index == 0) {
//            // get language default
//            final String displayLang = Locale.getDefault().toString();
//            DLog.d(TAG, "displayLang=" + displayLang);
//            return getDisplayLanguage(displayLang);
//        }
//        return Constant.LANGUAGES_MOTHERS[index - 1];
//    }
//
//    public static String getDisplayLanguage(String value) {
//        if (Utils.isEmpty(value))
//            return Constant.SETTING_LANGUAGES_MOTHERS.LANG_EN;
//        switch (value) {
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_AR:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_AR;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_ZH_CN:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_S;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_ZH_TW:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_T;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_DE:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_DE;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_ES:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_ES;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_FR:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_FR;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_IT:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_IT;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_JA:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_JA;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_KO:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_KO;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_PT:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_PT;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_RU:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_RU;
//            case Constant.SETTING_LANGUAGES_LOCAL.LANG_VI:
//                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_VI;
//        }
//        return Constant.SETTING_LANGUAGES_MOTHERS.LANG_EN;
//    }

    @SuppressWarnings("deprecation")
    public static String fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result.toString();
    }

    public static String encodeString(String value) {
        return Uri.encode(value, Constant.API.UTF_8);
    }

    public static int getMessageResponsePassword(String data) {
        final int value = parseInt(data);
        switch (value) {
            case Constant.PASSWORD_RESPONSE_CODE.PASSWORD_CHANGE_OK:
                return R.string.msg_change_password_success;
            case Constant.PASSWORD_RESPONSE_CODE.PASSWORD_CHANGE_FAIL_CURRENTPASSWORD_IS_WRONG:
                return R.string.msg_change_password_current_password_wrong;
            default:
                return R.string.msg_change_password_fail;
        }
    }

    public static boolean appInstalledOrNot(Context mContext, String uri) {
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public static void openGooglePlay(Context mContext, final String pkName) {
        try {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkName)));
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + pkName)));
        }
    }

    public static String getCurrentDateTimeWithUnderline() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getCurrentReadingList() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String getDurationStringForDisplay(long durationMs, boolean negativePrefix) {
        durationMs = Math.abs(durationMs);
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs);

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%s%02d:%02d:%02d", "",
                    hours,
                    minutes - TimeUnit.HOURS.toMinutes(hours),
                    seconds - TimeUnit.MINUTES.toSeconds(minutes));
        }
        return String.format(Locale.getDefault(), "%s%02d:%02d", "",
                minutes,
                seconds - TimeUnit.MINUTES.toSeconds(minutes)
        );
    }

    public static String getDurationString(long durationMs, boolean negativePrefix) {
        long hours = TimeUnit.MILLISECONDS.toHours(durationMs);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs);
        // if(hours > 0) {
        return String.format(Locale.getDefault(), "%s%02d:%02d:%02d",
                negativePrefix ? "-" : "",
                hours,
                minutes - TimeUnit.HOURS.toMinutes(hours),
                seconds - TimeUnit.MINUTES.toSeconds(minutes));
//        }
//        return String.format(Locale.getDefault(), "%s%02d:%02d",
//                negativePrefix ? "-" : "",
//                minutes,
//                seconds - TimeUnit.MINUTES.toSeconds(minutes)
//        );
    }

    public static SQLiteDatabase openDb(Context c, String dbName) {
        try {
            DATABASE_PATH = c.getPackageManager().
                    getPackageInfo(c.getPackageName(), 0).
                    applicationInfo.dataDir + "/";
            DATABASE_NAME = dbName;

            db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME,
                    null, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return db;
    }

    public static void closeDb() {
        if (db != null)
            db.close();

        db = null;
    }

    public static HashMap queryAllDetails(String table, String filePath) {

        Cursor cursor = db.query(table, null,
                "FilePath = " + "\'" + filePath + "\'", null, null, null, null);
        HashMap<String, String> values = new HashMap<>();

        if (cursor.moveToFirst()) {
            do {
                values.put("FileName", cursor.getString(cursor.getColumnIndexOrThrow("FileName")));
                values.put("FilePath", cursor.getString(cursor.getColumnIndexOrThrow("FilePath")));
                values.put("ImagePath", cursor.getString(cursor.getColumnIndexOrThrow("ImagePath")));
                values.put("Title", cursor.getString(cursor.getColumnIndexOrThrow("Title")));
                values.put("Level", cursor.getString(cursor.getColumnIndexOrThrow("Level")));
                values.put("IsXmlBook", cursor.getString(cursor.getColumnIndexOrThrow("IsXmlBook")));
                values.put("AllWordsCount", cursor.getString(cursor.getColumnIndexOrThrow("AllWordsCount")));
                values.put("KnownWordsCount", cursor.getString(cursor.getColumnIndexOrThrow("KnownWordsCount")));
                values.put("BookmarkWordsCount", cursor.getString(cursor.getColumnIndexOrThrow("BookmarkWordsCount")));
                values.put("SubtitleEncoding", cursor.getString(cursor.getColumnIndexOrThrow("SubtitleEncoding")));
            } while (cursor.moveToNext());
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return values;
    }

    public static int getIconDefault(final String url) {
        DLog.d(TAG, "getIconDefault - url=" + url);
        if (isEmpty(url))
            return -1;
        final String host1 = getHost(url);
        DLog.d(TAG, "host1=" + host1);
        for (int i = 0; i < Constant.BOOKMARK_DEFAULT.BOOKMARKS_DEFAULT.length; i++) {
            final String h = getHost(Constant.BOOKMARK_DEFAULT.BOOKMARKS_DEFAULT[i]);
            DLog.d(TAG, "h=" + h);
            if (!isEmpty(host1) && !isEmpty(h) && host1.equalsIgnoreCase(h))
                return Constant.BOOKMARK_DEFAULT.BOOKMARKS_DEFAULT_ICON[i];
        }
        return -1;
    }
    /**
     * @deprecated use {@link CopyTextUtil#copyToClipboard(Context, String, int)} instead
     */
    public static void copyToClipboard(Context mContext, final String text, final int id) {
        copyToClipboard(mContext, text, mContext.getString(id));
    }

    /**
     * @deprecated use {@link CopyTextUtil#copyToClipboard(Context, String, String)} instead
     */
    @Deprecated
    public static void copyToClipboard(Context mContext, final String text, final String toastText) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText("");
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        ToastUtil.getInstance(mContext).show(toastText);
    }
    /**
     * @deprecated use {@link CopyTextUtil#copyToClipboard(Context, String)} instead
     */
    public static void setTextClipboard(Context mContext, final String text) {
        DLog.d(TAG, "setTextClipboard - text=" + text);
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
    }

    public static String getTextClipboard(Context mContext) {
        return getTextClipboard(mContext, false);
    }

    public static String getMobileHomeURL(SharedPreferencesDB mSharePref, String url) {
        if (url.equalsIgnoreCase(Constant.BOOKMARK_DEFAULT.BOOKMARK_URL)) {
            return Constant.BOOKMARK_DEFAULT.BOOKMARK_URL_FORMAT + mSharePref.getSettingMotherTongue();
        }
        return url;
    }

    public static void loadFragment(AppCompatActivity activity, Fragment fragment, int fragmentcontainer) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(fragmentcontainer, fragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
    }

    public static void loadFragment(AppCompatActivity activity, Fragment fragment, int fragmentContainer, boolean addToBackStack) {
        loadFragment(activity, fragment, fragmentContainer, addToBackStack, fragment.getClass().getSimpleName());
    }

    public static void loadFragment(AppCompatActivity activity, Fragment fragment, int fragmentContainer, boolean addToBackStack, String tag) {
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(fragmentContainer, fragment, tag);
        if (addToBackStack) {
            ft.addToBackStack(tag);
        }
        ft.commitAllowingStateLoss();
    }

    public static int getDPsFromPixels(Context context, int pixels) {
        /*
            public abstract Resources getResources ()
                Return a Resources instance for your application's package.
        */
        Resources r = context.getResources();
        int dps = Math.round(pixels / (r.getDisplayMetrics().densityDpi / 160f));
        return dps;
    }

    public static int convertDpToPx(Context context, int dp) {
        return Math.round(dp * (context.getResources().getDisplayMetrics().ydpi / DisplayMetrics.DENSITY_DEFAULT));

    }

    static public boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                return true;
            } else {
            }
        } else {
        }
        return false;
    }

    public static boolean hasWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
            }
        }
        return false;
    }

    public static boolean hasMobileConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return mobile != null && mobile.isConnected();
        }
        return false;
    }

    public static String getAppVersion() {
        String appVersion = BuildConfig.VERSION_NAME;
        if (BuildConfig.DEBUG) {
            int i = appVersion.lastIndexOf(".");
            appVersion = appVersion.substring(0, i);
        }
        return appVersion;
    }

    public static String getApplicationId() {
        return BuildConfig.APPLICATION_ID;
    }

    public static void changeVolume(AudioManager audioManager, int value) {
        changeVolume(audioManager, value, true);
    }

    public static void changeVolume(AudioManager audioManager, int value, boolean isUI) {
        DLog.d(TAG, "commonVolume - value=" + value);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, isUI ? (0 | AudioManager.FLAG_SHOW_UI) : 0);
    }

    public static int checkBrightnessRange(int value) {
        return checkRange(value, Constant.PLAYER.SWIPE.BRIGHTNESS.MAX, Constant.PLAYER.SWIPE.BRIGHTNESS.MIN);
    }

    public static int checkVolumeRange(int value) {
        return checkRange(value, Constant.PLAYER.SWIPE.VOLUME.MAX, Constant.PLAYER.SWIPE.VOLUME.MIN);
    }

    //Dalnim added
    public static int checkRange(int value, int max, int min) {
        if (value > max) {
            value = max;
        } else if (value < min) {
            value = min;
        }
        return value;
    }

    public static int getSystemBrightness(Activity activity) {
        int brightness = Constant.PLAYER.SWIPE.BRIGHTNESS.MAX;
        try {
            brightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            DLog.d(TAG, "brightness = " + brightness);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    public static int changeBrightness(Activity activity, int value) {
        float percent = (float) value / Constant.PLAYER.SWIPE.BRIGHTNESS.MAX;
        DLog.d(TAG, "changeBrightness - percent=" + percent);
        WindowManager.LayoutParams layout = activity.getWindow().getAttributes();
        layout.screenBrightness = percent;
        activity.getWindow().setAttributes(layout);
        setupLight(activity, value);
        return (int) (percent * 100);
    }

    private static void setupLight(Context context, int progress) {
        if (progress < 1) {
            progress = 1;
        }
        try {
            int brightnessMode = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
            if (brightnessMode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                Settings.System.putInt(
                        context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            }
            Settings.System.putInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    progress);
        } catch (Exception ex) {
            DLog.e(TAG, "Error setupLight");
        }
    }

    public static boolean updateRubyText(String rubyText, int vocaId, String key, int newValue, OnUpdateRubyTextListener listener) {
        if (!TextUtils.isEmpty(rubyText) && listener != null) {
            try {
                String wordId = Constant.RUBY.KEY.VOCA_ID + "=" + vocaId;
                int startIndex = rubyText.indexOf(wordId);
                int endIndex = rubyText.indexOf(key, startIndex) + (key.length() + 1);
                if (startIndex >= 0 && endIndex > 0) {
                    while (endIndex < rubyText.length() - 1) {
                        if (String.valueOf(rubyText.charAt(++endIndex)).equals(" "))
                            break;
                    }
                    String oldString = rubyText.substring(startIndex, endIndex);
                    String oldValueString = oldString.substring(oldString.indexOf(key));
                    String newValueString = key + "=" + newValue;
                    String newString = oldString.replace(oldValueString, newValueString);
                    listener.onUpdated(rubyText.replace(oldString, newString));
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void removeAllNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static float getDistance(float startX, float startY, MotionEvent ev) {
        float distanceSum = 0;
        final int historySize = ev.getHistorySize();
        for (int h = 0; h < historySize; h++) {
            float hx = ev.getHistoricalX(0, h);
            float hy = ev.getHistoricalY(0, h);
            float dx = (hx - startX);
            float dy = (hy - startY);
            distanceSum += Math.sqrt(dx * dx + dy * dy);
            startX = hx;
            startY = hy;
        }
        float dx = (ev.getX(0) - startX);
        float dy = (ev.getY(0) - startY);
        distanceSum += Math.sqrt(dx * dx + dy * dy);
        return distanceSum;
    }

    public static int getIndexFromArray(String[] array, String value, int indexDefault) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value))
                return i;
        }
        return indexDefault;
    }

    public static void openWeb(Context mContext, final String url) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    public static void openAraHanjaApp(Context context, final String value) {
        String packageName = "com.dalnimsoft.arahanja";
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            // Make users to install AraHanja app.
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + packageName));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constant.OPEN_ARA_HANJA_DATA_KEY, value);
        context.startActivity(intent);
    }


    public static void openWebDictionaryForHanja(Context mContext, final String voca) {
//        if (!Utils.isEmpty(voca)) {
            String uriString = Constant.URL_WEB_DIC_HANJA_KOREAN_WORD + voca;
            if (voca.trim().length() == 1) {
                uriString = Constant.URL_WEB_DIC_HANJA_KOREAN + voca;
            }
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
//        }
    }

    public static void openWebDictionaryForHanja_ch_s(Context mContext, final String voca) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_WEB_DIC_HANJA_CHINESE + voca)));
    }

    public static void openWebDictionaryForKorean(Context mContext, final String voca) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constant.URL_WEB_DIC_KOREAN, voca))));
    }

    public static void openWebDictionaryForHanja_jp(Context mContext, final String voca) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constant.URL_WEB_DIC_HANJA_JAPANESE, voca))));
    }

    public static void openWebDictionaryForHanja_en(Context mContext, final String voca) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Constant.URL_WEB_DIC_HANJA_ENGLISH, voca))));
    }

    public static void openWebSearchForHanja(Context mContext, final String voca) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_WEB_SEARCH_GOOGLE_HANJA_KOREAN + voca + " 유래 뜻")));
    }

    public static void openWebSearch(Context mContext, final String voca) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_WEB_SEARCH_GOOGLE + voca + " 뜻")));
    }
    public static void openOpenSubtitleWebSite(Context mContext) {
        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.URL_OPEN_SUBTITLES)));
    }

    public static int getScreenRotation(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getRotation();
    }

    public static void setDialogSizeWider(Context context, Dialog dialog) {
        int marginLeftRight = (int) context.getResources().getDimension(R.dimen.dialog_horizontal_margin);
        int marginTopBottom = (int) context.getResources().getDimension(R.dimen.dialog_vertical_margin);
        setDialogSizeWider(dialog, marginLeftRight, marginTopBottom, marginLeftRight, marginTopBottom);
    }

    private static void setDialogSizeWider(Dialog dialog, int marginLeft, int marginTop, int marginRight, int marginBottom) {
        Window window = dialog.getWindow();
        if (window == null) return;

        window.setGravity(Gravity.CENTER);

        Point displaySize = getDisplayDimensions(dialog.getContext());
        int width = displaySize.x - marginLeft - marginRight;
        int height = displaySize.y - marginTop - marginBottom;
        window.setLayout(width, height);
    }

    public static Point getDisplayDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        return new Point(screenWidth, screenHeight);
    }

    public static int getSPValue(Context context, int valueId) {
        return (int) (context.getResources().getDimension(valueId) / context.getResources().getDisplayMetrics().scaledDensity);
    }

    public static boolean isDebug() {
        if (BuildConfig.BUILD_TYPE.equals(EnumBuildType.DEBUG.getName())) {  //        if (BuildConfig.DEBUG)
            return true;
        }
        return false;
    }
    //이거 안되는줄 알았는데, ios가 안되고 안드로이드는 잘 되네...
    public static boolean isReleaseMode() {
        if (BuildConfig.BUILD_TYPE.equals(EnumBuildType.RELEASE.getName())) {  //        if (BuildConfig.DEBUG)
            return true;
        }
        return false;
    }

    /*
     * @deprecated use same method in UserUtil.java
     */
    @Deprecated
    public static boolean isAdminUser(Context context) {
        if (SharedPreferencesDB.getInstance(context).getUserType() == EnumUserType.SYSTEM_MANAGER.getType()) {
            return true;
        }
        return false;
    }

    /*
     * @deprecated use same method in UserUtil.java
     */
    @Deprecated
    public static boolean isAdminUser(int userType) {
        if (userType == EnumUserType.SYSTEM_MANAGER.getType()) {
            return true;
        }
        return false;
    }

//    public static boolean isDebugOrAdminUser(int userType) {
//        if (isDebug() || isAdminUser(userType)) {
//            return true;
//        }
//        return false;
//    }

    /*
     * @deprecated use same method in UserUtil.java
     */
    @Deprecated
    public static boolean isDebugOrAdminUser(Context context) {
        if (isDebug() || UserUtil.isAdminUser(context)) {
            return true;
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        return getResourceHeightDimen(context, "status_bar_height");
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (!hasSoftNavigationBar(activity)) return 0;

        return getResourceHeightDimen(activity, "navigation_bar_height");
    }

    private static int getResourceHeightDimen(Context context, String resourceName) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(resourceName, "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static boolean hasSoftNavigationBar(Activity activity) {
        Display d = activity.getWindowManager().getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        boolean hasSoftwareKeys = (realWidth - displayWidth) > 0 ||
                (realHeight - displayHeight) > 0;
        return hasSoftwareKeys;
    }

    public static float pxToSp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static boolean isFirstTimeAdjustTableHeight(Context context) {
        if (SharedPreferencesDB.getInstance(context).isFirstTimeToAdjustTableHeight()) {
            SharedPreferencesDB.getInstance(context).setFirstTimeToAdjustTableHeight(false);
            return true;
        } else {
            return false;
        }
    }

    public static void initStudyLanguage(Context context) {
        if (Utils.isEmpty(SharedPreferencesDB.getInstance(context).getStudyLanguage())) {
            BaseVoca.setStudyLanguage(SharedPreferencesDB.getInstance(context), EnumLanguage.findByFormatApi(BuildConfig.STUDY_LANG));
        }
    }

    public static boolean needToDisplayMeaningEnglish(Context context) {
        return SharedPreferencesDB.getInstance(context).getDisplayEnglishMeaningToo();
    }

    public static void checkAndAskIgnoreBatteryOptimization(Activity activity, BatteryOptimizationCallback callback) {
        if (!SharedPreferencesDB.getInstance(activity).isAskedIgnoreBatteryOptimization()) {
            SharedPreferencesDB.getInstance(activity).setAskedToIgnoreBatteryOptimization(true);
            Utils.askIgnoreBatteryOptimization(activity, callback);
        }
    }

    //Not to go to sleep (or stop playing media) when I turn off the screen while I'm listening music or video
    public static void askIgnoreBatteryOptimization(Activity activity, BatteryOptimizationCallback callback) {
        PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        if ((activity.getPackageName() != null)
                && (powerManager.isIgnoringBatteryOptimizations(activity.getPackageName()) == false)) {
            final YesNoDialog dialog = new YesNoDialog(activity, R.string.info, R.string.messsage_ask_to_ignore_battery_optimiszations, null, new OnYesNoClickListener() {
                @Override
                public void onYesClick(View view, Object object) {
                    String fileNameWithoutExt = (String) object;
                    Intent intent = new Intent();

                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivityForResult(intent, GlobalActivityRequestCodeUtil.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    if (callback != null) {
                        callback.onYes();
                    }
                }

                @Override
                public void onNoClick(View view, Object object) {
                    if (callback != null) {
                        callback.onNo();
                    }
                }
            });
            dialog.show();
        }
    }
}
