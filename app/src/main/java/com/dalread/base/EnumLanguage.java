package com.dalread.base;

import android.content.Context;

import com.dalread.database.SharedPreferencesDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum EnumLanguage {
    KOREAN("한국어", "ko", "kor", "KOREAN", 3, Locale.KOREA, "한국어를 공부합니다.", "합계", "KR", "KOREAN", "한국어", "영어", "한국어", "일본어", "중국어"),
    ENGLISH("English", "en", "eng","ENGLISH", 13, Locale.US, "Let's study English", "Total", "US", "ENGLISH", "English", "English", "Korean", "Japanese", "Chinese"),
    CHINESE_SIMPLIFIED("简体中文", "zh", "chi","CHINESE_SIMPLIFIED", 1, Locale.SIMPLIFIED_CHINESE, "让我们学汉语", "合计", "CN", "CHINESE", "中文", "英语", "韩语", "日语", "中文"),
    JAPANESE("日本語", "ja", "jpn","JAPANESE", 4, Locale.JAPAN, "日本語を勉強しましょう", "合計", "JP", "JAPANESE", "日本語", "英語", "韓国語", "日本語", "中国語"),
    ARABIC("\u200eالعربية", "ar", "ara","ARABIC", 7, new Locale("ar", "JO"), "", "", "AR", "", "\u200eالعربية", "الإنجليزية", "الكورية", "اليابانية", "الصينية"),
    BENGALI("বাংলা", "en", "ben","BENGALI", 8, Locale.US, "", "", "BJ", "", "বাংলা", "ইংরেজি", "কোরিয়ান", "জাপানি", "চীনা"),
    CHINESE_TRADITIONAL("繁體中文", "zh", "zht","CHINESE_TRADITIONAL", 2, Locale.TRADITIONAL_CHINESE, "", "", "TW", "", "中文", "英语", "韓语", "日语", "中文"),
    CZECH("čeština", "cs", "cze","CZECH", 10, new Locale("cs", "CZ"), "", "", "CZ", "", "čeština", "Angličtina", "Korejština", "Japonština", "Čínština"),
    DANISH("Dansk", "da", "dan","DANISH", 11, new Locale("da", "DK"), "", "", "DK", "", "Dansk", "Engelsk", "Koreansk", "Japansk", "Kinesisk"),
    GERMAN("Deutsch", "de", "ger","GERMAN", 15, Locale.GERMANY, "", "", "DE", "", "Deutsch", "Englisch", "Koreanisch", "Japanisch", "Chinesisch"),
    GREEK("Ελληνικά", "el", "ell","GREEK", 16, new Locale("el", "GR"), "", "", "GR", "", "Ελληνικά", "Αγγλικά", "Κορεατικά", "Ιαπωνικά", "Κινέζικα"),
    SPANISH("Español", "es", "spa","SPANISH", 27, new Locale("es", "ES"), "", "", "ES", "", "Español", "Inglés", "Coreano", "Japonés", "Chino"),
    FINNISH("Suomi", "fi", "fin","FINNISH", 14, new Locale("fi", "FI"), "", "", "FI", "", "Suomi", "Englanti", "Korea", "Japani", "Kiina"),
    FRENCH("français", "fr", "fre","FRENCH", 6, Locale.FRANCE, "", "", "FR", "", "français", "Anglais", "Coréen", "Japonais", "Chinois"),
    HANJA("한자", "ko", "","HANJA", 33, Locale.KOREA, "한자를 공부합니다.", "", "", "", "한자", "", "", "", ""),
    HEBREW("\u200eעברית", "he", "heb","HEBREW", 17, new Locale("he", "IL"), "", "", "IL", "", "\u200eעברית", "אנגלית", "קוריאנית", "יפנית", "סינית"),
    HINDI("हिन्दी", "hi", "hin","HINDI", 18, new Locale("hi", "IN"), "", "", "IN", "", "हिन्दी", "अंग्रेज़ी", "कोरियाई", "जापानी", "चीनी"),
    CROATIAN("Hrvatski", "hr", "hrv","CROATIAN", 9, new Locale("hr", "HR"), "", "", "HR", "", "Hrvatski", "Engleski", "Korejski", "Japanski", "Kineski"),
    HUNGARIAN("Magyar", "hu", "hun","HUNGARIAN", 19, new Locale("hu", "HU"), "", "", "HU", "", "Magyar", "Angol", "Koreai", "Japán", "Kínai"),
    INDONESIAN("BahasaIndonesia", "id", "ind","INDONESIAN", 20, new Locale("id", "ID"), "", "", "ID", "", "BahasaIndonesia", "Inggris", "Korea", "Jepang", "Tionghoa"),
    ITALIAN("Italiano", "it", "ita","ITALIAN", 21, Locale.ITALY, "", "", "IT", "", "Italiano", "Inglese", "Coreano", "Giapponese", "Cinese"),
    DUTCH("Nederlands", "nl", "dut","DUTCH", 12, new Locale("nl", "NL"), "", "", "NL", "", "Nederlands", "Engels", "Koreaans", "Japans", "Chinees"),
    NORWEGIAN("Norsk", "no", "nor","NORWEGIAN", 22, new Locale("no", "NO"), "", "", "NO", "", "Norsk", "Engelsk", "Koreansk", "Japansk", "Kinesisk"),
    POLISH("Polski", "pl", "pol","POLISH", 23, new Locale("pl", "PL"), "", "", "PL", "", "Polski", "Angielski", "Koreański", "Japoński", "Chiński"),
    PORTUGUESE("Português", "pt", "por","PORTUGUESE", 24, new Locale("pt", "PT"), "", "", "PT", "", "Português", "Inglês", "Coreano", "Japonês", "Chinês"),
    ROMANIAN("Română", "ro", "rum","ROMANIAN", 25, new Locale("ro", "RO"), "", "", "RO", "", "Română", "Engleză", "Coreeană", "Japoneză", "Chineză"),
    RUSSIAN("Русский", "ru", "rus","RUSSIAN", 5, new Locale("ru", "RU"), "", "", "RU", "", "Русский", "Английский", "Корейский", "Японский", "Китайский"),
    SLOVAK("slovenčina", "sk", "slo","SLOVAK", 26, new Locale("sk", "SK"), "", "", "SK", "", "slovenčina", "Angličtina", "Kórejčina", "Japončina", "Čínština"),
    SWEDISH("Svenska", "sv", "swe","SWEDISH", 28, new Locale("sv", "SE"), "", "", "SE", "", "Svenska", "Engelska", "Koreanska", "Japanska", "Kinesiska"),
    THAI("ภาษาไทย", "th", "tha","THAI", 29, new Locale("th", "TH"), "", "", "TH", "", "ภาษาไทย", "อังกฤษ", "เกาหลี", "ญี่ปุ่น", "จีน"),
    TURKISH("Türkçe", "tr", "tur","TURKISH", 30, new Locale("tr", "TR"), "", "", "TR", "", "Türkçe", "İngilizce", "Korece", "Japonca", "Çince"),
    UKRAINIAN("українець", "uk", "ukr","UKRAINIAN", 31, Locale.US, "", "", "UA", "", "українець", "Англійська", "Корейська", "Японська", "Китайська"),
    VIETNAMESE("Tiếng Việt", "vi", "vie","VIETNAMESE", 32, new Locale("vi", "VN"), "Nào cùng học tiếng Việt", "Tổng cộng", "VN", "", "Tiếng Việt", "Tiếng Anh", "Tiếng Hàn", "Tiếng Nhật", "Tiếng Trung");

    private String formatUser;
    private String formatOs; // mlkit translater and Locale of Android, also TMDB's language parameter
    private String subLanguageId; //Can be used in opensubtitles. but opensubtitles needs more detailed info (Ex, AraApp has only PORTUGUESE "por" , but opensubtitles has "pob", "pom", "por" etc) https://www.opensubtitles.org/addons/export_languages.php
    private String formatApi;
    private int idApi;
    private Locale locale;
    private String letStudy;
    private String total;
    private String formatIso; // TMDB's Alternative titles.
    private String mediaFolder;
    private String langaugeName;
    private String studyLangEnglishByLanguage;
    private String studyLangKoreanByLanguage;
    private String studyLangJapaneseByLanguage;
    private String studyLangChineseByLanguage;
    EnumLanguage(String formatUser, String formatOs, String subLanguageId, String formatApi, int idApi, Locale locale, String letStudy, String total, String formatIso, String mediaFolder, String langaugeName, String studyLangEnglishByLanguage, String studyLangKoreanByLanguage, String studyLangJapaneseByLanguage, String studyLangChineseByLanguage) {
        this.formatUser = formatUser;
        this.formatOs = formatOs;
        this.subLanguageId = subLanguageId;
        this.formatApi = formatApi;
        this.idApi = idApi;
        this.locale = locale;
        this.letStudy = letStudy;
        this.total = total;
        this.formatIso = formatIso;
        this.mediaFolder = mediaFolder;
        this.langaugeName = langaugeName;
        this.studyLangEnglishByLanguage = studyLangEnglishByLanguage;
        this.studyLangKoreanByLanguage = studyLangKoreanByLanguage;
        this.studyLangJapaneseByLanguage = studyLangJapaneseByLanguage;
        this.studyLangChineseByLanguage = studyLangChineseByLanguage;
    }

    public String getFormatUser() {
        return formatUser;
    }

    public String getFormatOs() {
        return formatOs;
    }

    public String getSubLanguageId() {
        return subLanguageId;
    }

    public String getFormatApi() {
        return formatApi;
    }

    public int getIdApi() {
        return idApi;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getLetStudy() {
        return letStudy;
    }

    public String getTotal() {
        return total;
    }

    public String getFormatIso() {
        return formatIso;
    }

    public String getMediaFolder() {
        return mediaFolder;
    }

    public String getLangaugeName() {
        return langaugeName;
    }

    public String getStudyLangEnglishByLanguage() {
        return studyLangEnglishByLanguage;
    }

    public String getStudyLangKoreanByLanguage() {
        return studyLangKoreanByLanguage;
    }

    public String getStudyLangJapaneseByLanguage() {
        return studyLangJapaneseByLanguage;
    }

    public String getStudyLangChineseByLanguage() {
        return studyLangChineseByLanguage;
    }

    public static String[] getStudyLanguages() {
        List<EnumLanguage> enumLanguageList = getLanguageList();
        String[] languages = new String[enumLanguageList.size()];
        for (int i = 0; i < enumLanguageList.size(); i++) {
            EnumLanguage enumLanguage = enumLanguageList.get(i);
            languages[i] = enumLanguage.getFormatUser();
        }
        return languages;
//        String[] languages = new String[5];
//        languages[0] = ENGLISH.getFormatUser();
//        languages[1] = CHINESE_SIMPLIFIED.getFormatUser();// + " (" + CHINESE_SIMPLIFIED + ")"; //If I have extra text, Change Study language in DalVoca doesn't work
//        languages[2] = JAPANESE.getFormatUser();// + " (" + JAPANESE + ")";
//        languages[3] = KOREAN.getFormatUser();// + " (" + KOREAN + ")";
//        languages[4] = HANJA.getFormatUser();// + " (" + HANJA + ")";
//        return languages;
    }
//    getStudyListLanguage
    public static List<EnumLanguage> getLanguageList() {
//        final List<EnumLanguage> list = new ArrayList<>();
//        for (EnumLanguage language : values()) {
//                list.add(language);
//        }
        List<EnumLanguage> list = Arrays.asList(values());
//        list.remove(EnumLanguage.HANJA);
        return list;

//        final List<EnumLanguage> list = new ArrayList<>();
//        list.add(KOREAN);
//        list.add(ENGLISH);
//        list.add(CHINESE_SIMPLIFIED);
//        list.add(JAPANESE);
////        list.add(VIETNAMESE);
//        final List<EnumLanguage> list2 = new ArrayList<>();
//        for (EnumLanguage language : values()) {
//            if (!list.contains(language)) {
//                list2.add(language);
//            }
//        }
//        Collections.sort(list2, (obj1, obj2) ->
//                obj1.getFormatApi().toLowerCase().compareToIgnoreCase(obj2.getFormatApi().toLowerCase()));
//        list.addAll(list2);
//        return list;
    }

    public static String[] getStudyLanguagesFormatApi() {
        String[] languages = new String[5];
        languages[0] = ENGLISH.getFormatApi();
        languages[1] = CHINESE_SIMPLIFIED.getFormatApi();
        languages[2] = JAPANESE.getFormatApi();
        languages[3] = KOREAN.getFormatApi();
        languages[4] = HANJA.getFormatApi();
        return languages;
    }

    public static int[] getStudyLanguagesIdApi() {
        int[] languages = new int[5];
        languages[0] = ENGLISH.getIdApi();
        languages[1] = CHINESE_SIMPLIFIED.getIdApi();
        languages[2] = JAPANESE.getIdApi();
        languages[3] = KOREAN.getIdApi();
        languages[4] = HANJA.getIdApi();
        return languages;
    }

    public static String[] getLanguages() {
        ArrayList<String> languages = new ArrayList<>();
        for (EnumLanguage language : values()) {
            languages.add(language.getFormatUser());
        }
        languages.remove(HANJA.ordinal());
        return languages.toArray(new String[0]);
    }

    public static EnumLanguage findByFormatUser(String formatUser) {
        for (EnumLanguage language : values()) {
            if (language.getFormatUser().equalsIgnoreCase(formatUser))
                return language;
        }
        return ENGLISH;
    }

    public static EnumLanguage findByFormatApi(String formatApi) {
        for (EnumLanguage language : values()) {
            if (language.getFormatApi().equalsIgnoreCase(formatApi))
                return language;
        }
        return ENGLISH;
    }

    public static EnumLanguage findByIdApi(int idApi) {
        for (EnumLanguage language : values()) {
            if (language.getIdApi() == idApi)
                return language;
        }
        return ENGLISH;
    }

    public static EnumLanguage findByFormatOs(String formatOs) {
        for (EnumLanguage language : values()) {
            if (language.getFormatOs().equalsIgnoreCase(formatOs))
                return language;
        }
        return ENGLISH;
    }

    public static String getNameByIdApi(int idApi) {
        for (EnumLanguage language : values()) {
            if (language.getIdApi() == idApi)
                return language.name();
        }
        return ENGLISH.name();
    }

    public static int getIdApiByName(String name) {
        for (EnumLanguage language : values()) {
            if (language.name().equalsIgnoreCase(name))
                return language.getIdApi();
        }
        return ENGLISH.getIdApi();
    }

    public static String getNameByFormatIso(String formatIso) {
        for (EnumLanguage language : values()) {
            if (language.getFormatIso().equalsIgnoreCase(formatIso))
                return language.getFormatIso();
        }
        return ENGLISH.getFormatIso();
    }

    public static String getNameByFormatOs(String formatOs) {
        for (EnumLanguage language : values()) {
            if (language.getFormatApi().equalsIgnoreCase(formatOs))
                return language.getFormatOs();
        }
        return ENGLISH.getFormatOs();
    }


    private static String[] getLanguagesTranslation() {
        String[] languages = new String[5];
        languages[0] = ENGLISH.getFormatApi();
        languages[1] = CHINESE_SIMPLIFIED.getFormatApi();
        languages[2] = JAPANESE.getFormatApi();
        languages[3] = KOREAN.getFormatApi();
        languages[4] = VIETNAMESE.getFormatApi();
        return languages;
    }
    //What is this for?
    public static boolean checkSupportTranslation(String formatApi) {
        final String[] list = getLanguagesTranslation();
        for (String item : list) {
            if (item.equalsIgnoreCase(formatApi)) {
                return true;
            }
        }
        return false;
    }

    public static int getStudyLanguageCode(Context context) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        return EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()).getIdApi();
    }
    public static EnumLanguage getStudyLanguage(Context context) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        return EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
    }

    public static int getMotherTongueLanguageCode(Context context) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        return EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()).getIdApi();
    }
    public static EnumLanguage getMotherTongueLanguage(Context context) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        return EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
    }
    public static EnumLanguage getMenuLanguage(Context context) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        return EnumLanguage.findByFormatApi(sharedPreferences.getMenuLanguage());
    }
    public static int getMenuLanguageCode(Context context) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        return EnumLanguage.findByFormatApi(sharedPreferences.getMenuLanguage()).getIdApi();
    }

    public static String getTranslateLanguage(EnumLanguage language) {
        if (language == EnumLanguage.CHINESE_SIMPLIFIED || language == EnumLanguage.CHINESE_TRADITIONAL) {
            return language.getLocale().getLanguage() + "-" + language.getLocale().getCountry();
        }
        return language.getFormatOs();
    }
//
//    public static String getStudyLangNameByMotherLanguage(Context context) {
//        String result = "";
//        EnumLanguage studyLanguage = getStudyLanguage(context);
//        EnumLanguage motherTongueLanguage = getMotherTongueLanguage(context);
//        switch (studyLanguage) {
//            case ENGLISH:
//                result = motherTongueLanguage.getStudyLangEnglishByMotherTongueName();
//                break;
//            case KOREAN:
//                result = motherTongueLanguage.getStudyLangKoreanByMotherTongueName();
//                break;
//            case JAPANESE:
//                result = motherTongueLanguage.getStudyLangJapaneseByMotherTongueName();
//                break;
//            case CHINESE_SIMPLIFIED:
//            case CHINESE_TRADITIONAL:
//                result = motherTongueLanguage.getStudyLangChineseByMotherTongueName();
//                break;
//        }
//        return result;
//    }

    public static String getLangNameByLanguage(EnumLanguage studyLanguage, EnumLanguage language) {
        String result = "";
        switch (studyLanguage) {
            case ENGLISH:
                result = language.getStudyLangEnglishByLanguage();
                break;
            case KOREAN:
                result = language.getStudyLangKoreanByLanguage();
                break;
            case JAPANESE:
                result = language.getStudyLangJapaneseByLanguage();
                break;
            case CHINESE_SIMPLIFIED:
            case CHINESE_TRADITIONAL:
                result = language.getStudyLangChineseByLanguage();
                break;
        }
        return result;
    }
}
