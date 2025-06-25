package com.dalread.util;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.studylang.AbstractStudyLang;
import com.dalread.util.studylang.StudyLangFactory;

import java.util.Locale;

public class LanguageUtil {
    public static final String TAG = "LanguageUtil";

    public static boolean isContainStudyLang(final String text) {
        DLog.d(TAG, "isContainRightCharToTheLanguage");
        boolean isRight;
        switch (Constant.API.STUDY_LANG) {
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_JA:
                isRight = LanguageUtil.checkJapanWord(text);
                break;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_S:
                isRight = LanguageUtil.countChineseMatchRate(text) > 0;
                break;
            default:
                //containPercentage = Constant.CHECK_LANGUAGES.MAX_RATE_ENGLISH;
                isRight = LanguageUtil.countEnglishMatchRate(text) > 0;
                break;
        }
        return isRight;
    }

    public static boolean isEnglishWord(String string) {
        if (Utils.isEmpty(string))
            return false;
        final String[] temp = string.split(" ");
        for (String s : temp) {
            if (Constant.CHECK_LANGUAGES.VALID_ENGLISH_CHECK_RIGHT_LANGUAGE.matcher(s).find()) {
                return true;
            }
        }
        return false;
    }

    public static Locale convertVoiceLangToLocale(String voiceLang) {
        if (voiceLang.contains(Constant.SETTING_VOICE_LANGS.VOICELANG_JA)) {
            return Locale.JAPAN;
        } else if (voiceLang.contains(Constant.SETTING_VOICE_LANGS.VOICELANG_CH_S)) {
            return Locale.CHINA;
        } else if (voiceLang.contains(Constant.SETTING_VOICE_LANGS.VOICELANG_KO)) {
            return Locale.KOREA;
        }
        return Locale.US;
    }

    public static boolean checkJapanWord(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (isJapaneseSpecific(c) || isJapaneseKanja(c))
                return true;
            i += Character.charCount(c);
        }
        return false;
    }

    public static boolean checkHiraganaAndKatakana(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (isHiragana(c) || isKatakana(c))
                return true;
            i += Character.charCount(c);
        }
        return false;
    }

    private static boolean isHiragana(int c) {
        return (c >= 0x3040 && c <= 0x309f);
    }

    private static boolean isKatakana(int c) {
        return (c >= 0x30a0 && c <= 0x30ff);
    }

    private static boolean isJapaneseSpecific(int c) {
        return (c >= 0x3040 && c <= 0x30FF) || (c >= 0xFF01 && c <= 0xFF9F);
    }

    private static boolean isJapaneseKanja(int c) {
        return (c >= 0x2E80 && c <= 0x2EFF) || (c >= 0x2F00 && c <= 0x2FDF) || (c >= 0x4E00 && c <= 0x9FAF);
    }

    private static boolean isHiragana(final char c) {
        return (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HIRAGANA);
    }

    private static boolean isKatakana(final char c) {
        return (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.KATAKANA);
    }

    public static boolean containsChineseCharacters(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (isChineseCharacter(c)) {
                return true;
            }
            i += Character.charCount(c);
        }
        return false;
    }

    private static boolean isChineseCharacter(int codePoint) {
        return (codePoint >= 0x4E00 && codePoint <= 0x9FFF);
    }

    public static boolean countJapanMatchRate(String text) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            if (isHiragana(ch) || isKatakana(ch)) {
                return true;
            }
        }
        return false;
    }

    public static double countChineseMatchRate(String text) {
        int count = 0;
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char ch = text.charAt(i);
            Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
            if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(block) ||
                    Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS.equals(block) ||
                    Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A.equals(block)) {
                count++;
            }
        }
        if (count > 0)
            return ((double) count / text.length());
        return 0;
    }

    public static double countEnglishMatchRate(String text) {
        DLog.d(TAG, "countEnglishMatchRate");
        long count = 0;
        for (char c : text.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                count++;
            }
        }
        DLog.d(TAG, "count=" + count + " - text.length()=" + text.length());
        if (count > 0)
            return ((double) count / text.length());
        return 0;
    }

    public static String convertIndexToVoiceLang(int index) {
        DLog.d(TAG, "convertIndexToVoiceLang - index=" + index);
        if (index == 0) {
            return Constant.VOICE_LANGS[getLocalLanguage()];
        }
        return Constant.VOICE_LANGS[index - 1];
    }

    public static int getLocalLanguage() {
        final String mCurrentLang = Locale.getDefault().toString();
        int mLanguage = 4;
        DLog.d(TAG, "start mCurrentLang=" + mCurrentLang + " - mLanguage=" + mLanguage);
        if (!Utils.isEmpty(mCurrentLang)) {
            for (int i = 0; i < Constant.VOICE_LANGS.length; i++) {
                if (Constant.VOICE_LANGS[i].contains(mCurrentLang)) {
                    mLanguage = i;
                    break;
                }
            }
        }
        DLog.d(TAG, "end mLanguage=" + mLanguage);
        return mLanguage;
    }

    public static String convertIndexToLanguage(int index) {
        String value = Constant.BASE_BLANK;
        switch (index) {
            case 0:
                value = getLanguageFromApp();
                break;
            case 2: // cn
                value = Constant.LANGUAGES_SYSTEM.CN;
                break;
            case 5: // en
                value = Constant.LANGUAGES_SYSTEM.EN;
                break;
            case 9: // ja
                value = Constant.LANGUAGES_SYSTEM.JA;
                break;
            case 10: // ko
                value = Constant.LANGUAGES_SYSTEM.KO;
                break;
            case 13: // vi
                value = Constant.LANGUAGES_SYSTEM.VI;
                break;
            default:
                value = Constant.LANGUAGES_SYSTEM.EN;
                break;
        }
        return value;
    }
    //"en-US"이런 형식으로 리턴
    public static String getStudyLangLocaleString(Context context) {
        return getStudyLangLocale(context).toLanguageTag();
    }
    public static Locale getStudyLangLocale(Context context) {
        AbstractStudyLang studyLang = StudyLangFactory.create(context);
        return studyLang.getLocale();
    }
    public static String getLanguageFromApp() {
        String mLanguage = Locale.getDefault().toString();
        DLog.d(TAG, "mLanguage=" + mLanguage);
        switch (mLanguage) {
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_ZH_CN:
                return Constant.LANGUAGES_SYSTEM.CN;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_JA:
                return Constant.LANGUAGES_SYSTEM.JA;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_KO:
                return Constant.LANGUAGES_SYSTEM.KO;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_VI:
                return Constant.LANGUAGES_SYSTEM.VI;
            default:
                return Constant.LANGUAGES_SYSTEM.EN;
        }
    }
    //이게 필요하나?
//    public static boolean isChangeLanguages(SharedPreferencesDB sharedPreference, String mLang) {
//        final String shareLang = sharedPreference.getLanguage();
//        return !Utils.isEmpty(shareLang) && !shareLang.equalsIgnoreCase(mLang);
//    }

    public static String getMotherTongueVoiceLang(String strMotherTongue) {
        switch (strMotherTongue) {
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_AR:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_AR;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_S:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_CH_S;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_T:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_CH_T;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_DE:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_DE;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_ES:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_ES;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_FR:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_FR;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_IT:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_IT;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_JA:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_JA;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_KO:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_KO;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_PT:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_PT;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_RU:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_RU;
            case Constant.SETTING_LANGUAGES_MOTHERS.LANG_VI:
                return Constant.SETTING_VOICE_LANGS.VOICELANG_VI;
        }
        return Constant.SETTING_VOICE_LANGS.VOICELANG_EN;
    }

    public static String getMotherTongueLangDisplay(int index) {
        if (index == 0) {
            // get language default
            final String displayLang = Locale.getDefault().toString();
            DLog.d(TAG, "displayLang=" + displayLang);
            return getDisplayLanguage(displayLang);
        }
        return Constant.LANGUAGES_MOTHERS[index - 1];
    }

    public static String getDisplayLanguage(String value) {
        if (Utils.isEmpty(value))
            return Constant.SETTING_LANGUAGES_MOTHERS.LANG_EN;
        switch (value) {
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_AR:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_AR;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_ZH_CN:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_S;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_ZH_TW:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_CH_T;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_DE:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_DE;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_ES:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_ES;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_FR:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_FR;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_IT:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_IT;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_JA:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_JA;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_KO:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_KO;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_PT:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_PT;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_RU:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_RU;
            case Constant.SETTING_LANGUAGES_LOCAL.LANG_VI:
                return Constant.SETTING_LANGUAGES_MOTHERS.LANG_VI;
        }
        return Constant.SETTING_LANGUAGES_MOTHERS.LANG_EN;
    }

    public static boolean isStudyLangKorean(Context context) {
//        EnumLanguage studyLanguage = EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getStudyLanguage());
        return EnumLanguage.getStudyLanguage(context) == EnumLanguage.KOREAN;
    }
    public static boolean isStudyLangHanja(Context context) {
        return EnumLanguage.getStudyLanguage(context) == EnumLanguage.HANJA;
    }
    public static boolean isStudyLangEnglish(Context context) {
        return EnumLanguage.getStudyLanguage(context) == EnumLanguage.ENGLISH;
    }
    public static boolean isStudyLangChinese(Context context) {
        return EnumLanguage.getStudyLanguage(context) == EnumLanguage.CHINESE_SIMPLIFIED;
    }
    public static boolean isStudyLangJapanese(Context context) {
        return EnumLanguage.getStudyLanguage(context) == EnumLanguage.JAPANESE;
    }

    public static boolean isMotherTongueLangJapanese(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.JAPANESE;
    }
    public static boolean isMotherTongueLangKorean(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.KOREAN;
    }
    public static boolean isMotherTongueLangEnglish(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.ENGLISH;
    }
    public static boolean isMotherTongueLangCH_S(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.CHINESE_SIMPLIFIED;
    }
    public static boolean isMotherTongueLangCH_T(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.CHINESE_TRADITIONAL;
    }
    public static boolean isMotherTongueLangArabic(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.ARABIC;
    }
    public static boolean isMotherTongueLangBengali(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.BENGALI;
    }
    public static boolean isMotherTongueLangCzech(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.CZECH;
    }
    public static boolean isMotherTongueLangDanish(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.DANISH;
    }
    public static boolean isMotherTongueLangGreek(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.GREEK;
    }
    public static boolean isMotherTongueLangSpanish(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.SPANISH;
    }
    public static boolean isMotherTongueLangFinnish(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.FINNISH;
    }
    public static boolean isMotherTongueLangFrench(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.FRENCH;
    }
    public static boolean isMotherTongueLangHebrew(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.HEBREW;
    }
    public static boolean isMotherTongueLangHindi(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.HINDI;
    }
    public static boolean isMotherTongueLangCroatian(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.CROATIAN;
    }
    public static boolean isMotherTongueLangIndonesian(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.INDONESIAN;
    }
    public static boolean isMotherTongueLangItalian(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.ITALIAN;
    }
    public static boolean isMotherTongueLangDutch(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.DUTCH;
    }
    public static boolean isMotherTongueLangNorwegian(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.NORWEGIAN;
    }
    public static boolean isMotherTongueLangPolish(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.POLISH;
    }
    public static boolean isMotherTongueLangProtuguese(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.PORTUGUESE;
    }
    public static boolean isMotherTongueLangRomanian(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.ROMANIAN;
    }
    public static boolean isMotherTongueLangRussian(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.RUSSIAN;
    }
    public static boolean isMotherTongueLangSlovak(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.SLOVAK;
    }
    public static boolean isMotherTongueLangSwedish(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.SWEDISH;
    }
    public static boolean isMotherTongueLangThai(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.THAI;
    }
    public static boolean isMotherTongueLangTurksih(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.TURKISH;
    }
//    public static boolean isMotherTongueLangUkrainian(Context context) {
//        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.UKRAINIAN;
//    }
    public static boolean isMotherTongueLangGerman(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.GERMAN;
    }
    public static boolean isMotherTongueLangVietnamese(Context context) {
        return EnumLanguage.getMotherTongueLanguage(context) == EnumLanguage.VIETNAMESE;
    }

    public static String getSQLFileNameForAraKoicaInAsset(Context context) {
        return Constant.ARAKOICA.DATABASE_NAME;
    }

    public static String getSQLFileNameForAraConvInAsset(Context context) {
        if (isStudyLangChinese(context))
            return Constant.ARACONV.ASSET_DATABASE_NAME_CH_S;
        else if (isStudyLangJapanese(context))
            return Constant.ARACONV.ASSET_DATABASE_NAME_JP;
        else if (isStudyLangKorean(context))
            return Constant.ARACONV.ASSET_DATABASE_NAME_KO;
        return Constant.ARACONV.ASSET_DATABASE_NAME_ENG;
    }

    public static String getVoiceFileNameForAraConvInAsset(Context context) {
        if (isStudyLangChinese(context))
            return Constant.ARACONV.ASSET_ZIP_VOICE_CH_S;
        else if (isStudyLangJapanese(context))
            return Constant.ARACONV.ASSET_ZIP_VOICE_JP;
        else if (isStudyLangKorean(context))
            return Constant.ARACONV.ASSET_ZIP_VOICE_KO;
        return Constant.ARACONV.ASSET_ZIP_VOICE_ENG;
    }

    public static EnumLanguage getStudyLanguage(Context context) {
        return EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getStudyLanguage());
    }
    public static int getStudyLanguageCode(Context context) {
        return EnumLanguage.getStudyLanguageCode(context);
    }
    public static EnumLanguage getMotherTongueLanguage(Context context) {
        return EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getMotherTongueLanguage());
    }
    public static int getMotherTongueLanguageCode(Context context) {
        return EnumLanguage.getMotherTongueLanguageCode(context);
    }

    public static EnumLanguage getMenuLanguage(Context context) {
        return EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getMenuLanguage());
    }
    public static int getMenuLanguageCode(Context context) {
        return EnumLanguage.getMenuLanguageCode(context);
    }

    public static int getWordLevelByLanguageLevel(Context context, String strLanguageLevel) {
        int wordLevel = getWordLevel_Beginner(context);
        switch (strLanguageLevel) {
            case Constant.SIGN_UP_LEVEL_BEGINNER:
               wordLevel = getWordLevel_Beginner(context);
                break;
            case Constant.SIGN_UP_LEVEL_PRE_INTERMEDIATE:
                wordLevel = getWordLevel_Pre_Intermediate(context);
                break;
            case Constant.SIGN_UP_LEVEL_INTERMEDIATE:
                wordLevel = getWordLevel_Intermediate(context);
                break;
            case Constant.SIGN_UP_LEVEL_POST_INTERMEDIATE:
                wordLevel = getWordLevel_Post_Intermediate(context);
                break;
            case Constant.SIGN_UP_LEVEL_ADVANCED:
                wordLevel = getWordLevel_Advanced(context);
                break;
        }
        return wordLevel;
    }

    private static int getWordLevel_Beginner(Context context) {
        int level = Constant.WordLevelNo_ENG_Beginner;
        if (LanguageUtil.isStudyLangChinese(context) || LanguageUtil.isStudyLangJapanese(context) || LanguageUtil.isStudyLangHanja(context)) {
            level = Constant.WordLevelNo_CH_S_Beginner;
        }
        return level;
    }

    private static int getWordLevel_Pre_Intermediate(Context context) {
        int level = Constant.WordLevelNo_ENG_Pre_Intermediate;
        if (LanguageUtil.isStudyLangHanja(context)) {
            level = Constant.WordLevelNo_HANJA_Pre_Intermediate;
        }
        return level;
    }

    private static int getWordLevel_Intermediate(Context context) {
        int level = Constant.WordLevelNo_ENG_Intermediate;
        if (LanguageUtil.isStudyLangChinese(context)) {
            level = Constant.WordLevelNo_CH_S_Intermediate;
        } else if (LanguageUtil.isStudyLangHanja(context)) {
            level = Constant.WordLevelNo_HANJA_Intermediate;
        }
        return level;
    }

    private static int getWordLevel_Post_Intermediate(Context context) {
        int level = Constant.WordLevelNo_ENG_Post_Intermediate;
        if (LanguageUtil.isStudyLangChinese(context)) {
            level = Constant.WordLevelNo_CH_S_Post_Intermediate;
        } else if (LanguageUtil.isStudyLangHanja(context)) {
            level = Constant.WordLevelNo_HANJA_Post_Intermediate;
        }
        return level;
    }

    private static int getWordLevel_Advanced(Context context) {
        int level = Constant.WordLevelNo_ENG_Advanced;
        if (LanguageUtil.isStudyLangKorean(context) || LanguageUtil.isStudyLangJapanese(context)) {
            level = Constant.WordLevelNo_JP_Advanced;
        } else if (LanguageUtil.isStudyLangChinese(context)) {
            level = Constant.WordLevelNo_CH_S_Advanced;
        }
        return level;
    }
//
//    private int getWordLevel_Beginner(Context context) {
//        int level = Constant.WordLevelNo_ENG_Beginner;
//        if (LanguageUtil.isStudyLangChinese(context) ||
//            LanguageUtil.isStudyLangJapanese(context) ||
//            LanguageUtil.isStudyLangHanja(context)) {
//            level = 1;
//        }
//        return level;
//    }
//    private int getWordLevel_Pre_Intermediate(Context context) {
//        int level = 4;
//        if (LanguageUtil.isStudyLangHanja(context)) {
//            level = 2;
//        }
//        return level;
//    }
//    private int getWordLevel_Intermediate(Context context) {
//        int level = 8;
//        if (LanguageUtil.isStudyLangChinese(context)) {
//            level = 6;
//        } else if (LanguageUtil.isStudyLangHanja(context)) {
//            level = 5;
//        }
//        return level;
//    }
//    private int getWordLevel_Post_Intermediate(Context context) {
//        int level = 10;
//        if (LanguageUtil.isStudyLangChinese(context)) {
//            level = 9;
//        } else if (LanguageUtil.isStudyLangHanja(context)) {
//            level = 7;
//        }
//        return level;
//    }
//    private int getWordLevel_Advanced(Context context) {
//        int level = 17;
//        if (LanguageUtil.isStudyLangKorean(context) || LanguageUtil.isStudyLangJapanese(context)) {
//            level = 14;
//        } else if (LanguageUtil.isStudyLangChinese(context)) {
//            level = 12;
//        }
//        return level;
//    }
}
