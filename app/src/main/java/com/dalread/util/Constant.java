package com.dalread.util;

import com.dalread.BuildConfig;
import com.dalread.R;
import com.dalread.base.EnumBuildType;

import java.util.Locale;
import java.util.regex.Pattern;

public class Constant {
    public static final String BASE_BLANK = "";
    public static final String BASE_ONE_SPACE = " ";
    public static final String BASE_ONE_SPACE_ESCAPE = "%20";

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String BASE_SHARED_NAME = "share_pref_dalread";

    public static final String BASE_API_URL_LOCAL_HOME = "http://192.168.0.23:8080/";
    public static final String BASE_API_URL_LOCAL_WORK_LAN = "http://192.168.0.45:8080/";
    public static final String BASE_API_URL_RELEASE = "http://araonesoft.com/"; // Don't use this anymore "http://www.dalread.com/";
    public static final String BASE_API_URL_TEST = "http://192.168.0.23:8080/"; //"http://ec2-13-124-83-199.ap-northeast-2.compute.amazonaws.com/";
    public static final String BASE_API_URL = BuildConfig.BUILD_TYPE.equals(EnumBuildType.RELEASE.getName())
            ? BASE_API_URL_RELEASE
            : BASE_API_URL_TEST;
    public static final int BASE_API_URL_INDEX = BuildConfig.BUILD_TYPE.equals(EnumBuildType.RELEASE.getName()) ? 0 : 1;
    public static final String BASE_API_URL_FIREBASE = "https://asia-northeast1-dalread-web.cloudfunctions.net/";

    public static final String DEFAULT_USER_EMAIL = "dal6@gmail.com";
    public static final String BASE_FAVICON_ICO = "/favicon.ico";
    public static final String URL_GOOGLE = "https://google.com/#q=";
    public static final String URL_YOUTUBE = "https://www.youtube.com/watch?v=";
    public static final String URL_OPEN_SUBTITLES = "https://www.opensubtitles.com/";
//    public static final String URL_CHAT_GPT = "https://chat.openai.com/chat";
    public static final String URL_ARACONV_KOREAN_KAKAOTALK_GROUPCHAT_1 = "https://open.kakao.com/o/gCmaCA3d";
    public static final String URL_ARAHANGUL_KAKAOTALK_GROUPCHAT_1 = "https://open.kakao.com/o/gnE31sae";
    public static final String URL_ARAPLAYER_ENGLISH_KAKAOTALK_GROUPCHAT = "https://open.kakao.com/o/g02ZwK9f";
    public static final String URL_ARAMULTIPLAYER_KAKAOTALK_GROUPCHAT = "https://open.kakao.com/o/gcDPqRkg";
    public static final String URL_ARAHANJA_KAKAOTALK_GROUPCHAT = "https://open.kakao.com/o/g9LeyK9f";

    public static final String URL_NAVER_CAFE_ARAPLAYER_MANUAL = "https://cafe.naver.com/dalenglish/838";
    public static final String URL_NAVER_CAFE_ARAMULTIPLAYER_MANUAL = "https://www.reddit.com/r/AraMultiPlayer/comments/1e9fk9t/about_aramultiplayer_app/";
    public static final String URL_NAVER_CAFE_DALVOCA_MANUAL = "https://cafe.naver.com/dalenglish/838";
    public static final String URL_NAVER_CAFE_ARACONV_MANUAL = "https://cafe.naver.com/dalenglish/855";
    public static final String URL_NAVER_CAFE_ARACONV_MANUAL_EN = "https://cafe.naver.com/dalenglish/876";
    public static final String URL_NAVER_CAFE_ARAHANJA_MANUAL = "https://cafe.naver.com/arahanja/3";
    public static final String URL_WEB_DIC_HANJA_KOREAN = "https://hanja.dict.naver.com/#/search?range=letter&query=";
    public static final String URL_WEB_DIC_HANJA_KOREAN_WORD = "https://hanja.dict.naver.com/#/search?range=word&query=";
    public static final String URL_WEB_DIC_HANJA_JAPANESE = "https://dic.daum.net/search.do?q=%1$s&dic=jp";
    public static final String URL_WEB_DIC_HANJA_ENGLISH = "https://dic.daum.net/search.do?q=%1$s&dic=eng";
    public static final String URL_WEB_DIC_HANJA_CHINESE = "https://zh.dict.naver.com/#/search?range=word&query=";
    public static final String URL_WEB_DIC_KOREAN = "https://dic.daum.net/search.do?q=%1$s&dic=ko";
    public static final String URL_WEB_SEARCH_GOOGLE_HANJA_KOREAN = "https://www.google.com/search?q=";
    public static final String URL_WEB_SEARCH_GOOGLE = "https://www.google.com/search?q=";
    public static final String KEY_DATA_SHARE_APP = "KEY_DATA_SHARE_APP";

    public static final String URL_WWW = "www.";
    public static final String URL_HTTP = "http://";
    public static final String URL_HTTPS = "https://";

    public static final String studyLangMarker = "[[STUDY_LANG_NAME]]";
    public static final String motherTongueLangMarker = "[[MOTHER_TONGUE_LANG_NAME]]";

    public static final long CAN_EDIT_CONTENT_AFTER_APP_USE_COUNT = 2;

    public static final float DEFAULT_PLAYER_WIDTH_PERCENT = 60;
    public static final int DEFAULT_DISP_ORDER = 999999;

    public static final String OPEN_ARA_HANJA_DATA_KEY = "OPEN_ARA_HANJA_DATA_KEY";

    public static final long TIME_DISTANCE_TO_SHOW_ADS_AFTER_CLICKED = 24 * 60 * 60 * 1000;

    public static final int vocaFilterCountToCheck = 30;
    public static class MAIL {
        public static final String DALNIM_BEST = "dalnimbest@gmail.com";
        public static final String ARA_ONE_SOFT = "araonesoft@gmail.com";
    }

    public static final String[] BASE_URL_LABELS = {
            "Release server",
            "Test server",
            "Local work",
            "Local home",
            "Custom server"
    };
    public static final String[] BASE_URL_VALUES = {
            Constant.BASE_API_URL_RELEASE,
            Constant.BASE_API_URL_TEST,
            Constant.BASE_API_URL_LOCAL_WORK_LAN,
            Constant.BASE_API_URL_LOCAL_HOME
    };

    public static final int TTS_DELAY = 500; // misecond
    public static final int MAX_LENGTH_BOOK_TITLE = 15;
    public static final String BREAK_CHARACTER = System.getProperty("line.separator");

    public static class SETTINGS {
        public static final String SETTING_SPEAK = BuildConfig.SETTING_SPEAK;
        public static final int SETTING_TTS_SPEED = BuildConfig.SETTING_TTS_SPEED;
        public static final int SETTING_TTS_SPEED_CHINESE = 40;
        public static final int SETTING_TTS_SPEED_ENGLISH = 50;
        public static final int SETTING_TTS_SPEED_JAPANESE = 50;
        public static final int SETTING_TTS_SPEED_KOREAN = 50;
        public static final int SETTING_TTS_SPEED_HANJA = 50;
    }

    public static class CHECK_LANGUAGES {
        public static final Pattern VALID_ENGLISH_CHECK_RIGHT_LANGUAGE = Pattern.compile("[a-zA-Z]+$");
        public static final double MAX_RATE_CHINESE = 0.1f;
        public static final double MAX_RATE_ENGLISH = 0.2f;
    }

    public static class BILLING {
        public static String MERCHANT_ID = "04659844840152248921";
    }

    public static class MERCURY_API {
        public static final String BASE_URL = "https://mercury.postlight.com/parser";
        public static final String KEY_API = "F2fuRr2si4M3yhhceOe95hnzvTF5B4LoZkkFaZsF";
        public static final String KEY_REPLACE = "<[^>]+>";
        public static final String HEADER_CONTENT_TYPE = "application/json";
    }

    public static class API {
        public static final String STUDY_LANG = BuildConfig.STUDY_LANG;
        public static final String VOICE_LANG = BuildConfig.VOICELANG;
        public static final String HEADER_CONTENT_TYPE = "application/x-www-form-urlencoded";
        public static final String JS_CALL_DALREAD = "/jscalldalread/";
        public static final String JS_CALL = "jscall:";
        public static final String JS_OPEN_WORD_VC = "openWordVC";
        public static final String JS_PLAY_TTS_WORD = "playTTSWord";
        public static final String JS_SHOW_SV_PROGRESS_HUD = "showSVProgressHUD";
        public static final String DEFAULT_UID = "1505";
        public static final String UTF_8 = "UTF-8";
        public static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
        public static final String COOKIE_TOKEN_START = "TOKEN=";
        public static final String COOKIE_TOKEN_END = ";";
        public static final String APP_NAME = "DalRead";
        public static final String IN_APP_TYPE = "POINT";
        public static final String CLIENT_TYPE = "CLIENT_TYPE_ANDROID";
    }

    public static class WORD_KNOW_STATUS {
        public static final String WORD_KNOWN_NOTINDIC = "-1";
        public static final String WORD_KNOWN_NOTRATED = "0";
        public static final String WORD_KNOWN_UNKNOWN = "1";
        public static final String WORD_KNOWN_NOTSURE = "2";
        public static final String WORD_KNOWN_KNOWN = "3";
        public static final String WORD_KNOWN_EXCLUDE = "99";
    }

    public static class SEGMENT_INDEX {
        public static final int SEGMENT_ALL = 0;
        public static final int SEGMENT_UNKNOWN = 1;
        public static final int SEGMENT_BOOKMARK = 2;
        public static final int SEGMENT_SELECT = 3;
        public static final int SEGMENT_USER = 0;
        public static final int SEGMENT_LEVEL_1 = 1;
        public static final int SEGMENT_LEVEL_2 = 2;
        public static final int SEGMENT_LEVEL_3 = 3;
        public static final int SEGMENT_LEVEL_4 = 4;
    }

    public static class SHARE_PREF {

        public static final String KEY_ASKED_TO_IGNORE_BATTERY_OPTIMIZATION = "KEY_ASKED_TO_IGNORE_BATTERY_OPTIMIZATION";
        public static final String KEY_FIRST_LAUNCH_APP = "key_first_launch_app";
        public static final String KEY_UID = "key_uid";
        public static final String KEY_EMAIL = "key_email";
        public static final String KEY_NAME = "key_name";
        public static final String KEY_POINT_GPT = "KEY_POINT_GPT";
        public static final String KEY_POINT_MULTI_PLAYER = "KEY_POINT_MULTI_PLAYER";
        public static final String KEY_POINT_ARA_HANJA = "KEY_POINT_ARA_HANJA";
        public static final String KEY_MULTI_PLAYER_SCREEN_COUNT = "KEY_MULTI_PLAYER_SCREEN_COUNT";
        public static final String KEY_MULTI_PLAYER_SCREEN_ORIENTATION = "KEY_MULTI_PLAYER_SCREEN_ORIENTATION";
        public static final String KEY_POINT_READING = "key_point_reading";
        public static final String KEY_POINT_VOCA = "key_point_voca";
        public static final String KEY_USER_ROLE = "KEY_USER_ROLE";
        public static final String KEY_USER_TYPE = "KEY_USER_TYPE";
        public static final String KEY_USER_SUB_ROLE = "KEY_USER_SUB_ROLE";
        public static final String KEY_SIGNUP_RESULT = "key_signup_result";
        public static final String KEY_SETTING_INDEX_MOTHER_TONGUE = "key_setting_index_mother_tongue";
        public static final String KEY_SETTING_MOTHER_TONGUE = "key_setting_mother_tongue";
        public static final String KEY_SETTING_SHOW_WORD_MEANING = "key_show_word_meaning";
        public static final String KEY_SETTING_SHOW_PRONOUNCE = "key_show_pronounce";
        public static final String KEY_LAST_URL = "key_last_url";
        public static final String KEY_COPIED_TEXT = "key_copied_text";
        public static final String KEY_LANGUAGE = "key_language";
        public static final int MAX_LENGTH_COPIED_TEXT = 100;
        public static final String KEY_TOKEN = "key_token";
        public static final String KEY_TTS_SPEED = "key_tts_speed";
        public static final String KEY_LAST_READ_BOOK = "KEY_LAST_READ_BOOK";
        public static final String KEY_SETTING_MY_LANGUAGE_LEVEL = "KEY_SETTING_MY_LANGUAGE_LEVEL";
        public static final String KEY_SESSION = "key_session";
        public static final String KEY_LASTPLAYEDMOVIE = "last_played_movie";
        public static final String KEY_STUDY_LANG = "KEY_STUDY_LANG";
        public static final String KEY_MOTHER_LANG = "KEY_MOTHER_LANG"; //Was KEY_DISPLAY_LANG
        public static final String KEY_MENU_LANG = "KEY_MENU_LANG";
        public static final String KEY_READ_COUNT = "KEY_READ_COUNT";
        public static final String KEY_INCLUDE_MY_VOICE = "KEY_INCLUDE_MY_VOICE";
        public static final String KEY_ENABLE_SECURE_SCREEN = "KEY_ENABLE_SECURE_SCREEN";
        public static final String KEY_INCLUDE_MEANING = "KEY_INCLUDE_MEANING";
        public static final String KEY_DISPLAY_ENGLISH_MEANING_TOO = "KEY_DISPLAY_ENGLISH_MEANING_TOO";
        public static final String KEY_KEEP_DISPLAYING_BACKGROUND_HINT_WHEN_WRITING = "KEY_KEEP_DISPLAYING_BACKGROUND_HINT_WHEN_WRITING";

        public static final String KEY_BACKGROUND_MODE = "KEY_BACKGROUND_MODE";
        public static final String KEY_PREFERRED_NATIVE_SPEAKERS = "KEY_PREFERRED_NATIVE_SPEAKERS";
        public static final String KEY_PREFERRED_NATIVE_SPEAKERS_COUNT = "KEY_PREFERRED_NATIVE_SPEAKERS_COUNT";
        public static final String KEY_TTS_SPEED_CHINESE = "KEY_TTS_SPEED_CHINESE";
        public static final String KEY_TTS_SPEED_ENGLISH = "KEY_TTS_SPEED_ENGLISH";
        public static final String KEY_TTS_SPEED_JAPANESE = "KEY_TTS_SPEED_JAPANESE";
        public static final String KEY_TTS_SPEED_KOREAN = "KEY_TTS_SPEED_KOREAN";
        public static final String KEY_TTS_SPEED_HANJA = "KEY_TTS_SPEED_HANJA";
        public static final String KEY_SHOW_INTRODUCTION_VIEW = "KEY_SHOW_INTRODUCTION_VIEW";
        public static final String KEY_FIREBASE_TOKEN = "KEY_FIREBASE_TOKEN";
        public static final String KEY_DISPLAY_PRONUNCIATION = "KEY_DISPLAY_PRONUNCIATION";
        public static final String KEY_ALERT_AMKI_GRADE_1 = "KEY_ALERT_AMKI_GRADE_1";
        public static final String KEY_ALERT_AMKI_GRADE_2 = "KEY_ALERT_AMKI_GRADE_2";
        public static final String KEY_ALERT_AMKI_GRADE_KNOWN = "KEY_ALERT_AMKI_GRADE_KNOWN";
        public static final String KEY_ALERT_AMKI_GRADE_UNKNOWN = "KEY_ALERT_AMKI_GRADE_UNKNOWN";
        public static final String KEY_SEX = "KEY_SEX";
        public static final String KEY_AGE = "KEY_AGE";
        public static final String KEY_MAX_HOMEWORK = "KEY_MAX_HOMEWORK";
        public static final String KEY_MAX_QUIZ = "KEY_MAX_QUIZ";
        public static final String KEY_MAX_HANJA_QUIZ = "KEY_MAX_HANJA_QUIZ";
        public static final String KEY_ALLOW_CHAT = "KEY_ALLOW_CHAT";
        public static final String KEY_BLINKMODE_IN_WORDLIST = "KEY_BLINKMODE_IN_WORDLIST";
        public static final String KEY_ASTERISKMODE_IN_WORDLIST = "KEY_ASTERISKMODE_IN_WORDLIST";
        public static final String KEY_SHARE_MY_RECORDINGS = "KEY_SHARE_MY_RECORDINGS";
        public static final String KEY_AUTO_PLAY_IN_RECORDING_ALL = "KEY_AUTO_PLAY_IN_RECORDING_ALL";
        public static final String KEY_USE_LESSON_NOTIFICATION = "KEY_USE_LESSON_NOTIFICATION";
        public static final String KEY_TIME_TO_NOTIFY_BEFORE_LESSON_START = "KEY_TIME_TO_NOTIFY_BEFORE_LESSON_START";
        public static final String KEY_TIME_TO_NOTIFY_BEFORE_LESSON_FINISH = "KEY_TIME_TO_NOTIFY_BEFORE_LESSON_FINISH";
        public static final String KEY_BASE_URL_INDEX = "KEY_BASE_URL_INDEX";
        public static final String KEY_BASE_URL_VALUE = "KEY_BASE_URL_VALUE";
        public static final String KEY_BASE_CUSTOM_URL_VALUE = "KEY_BASE_CUSTOM_URL_VALUE";
        public static final String KEY_MEDIA_SLEEP_VALUE = "KEY_MEDIA_SLEEP_VALUE";
        public static final String KEY_MEDIA_LISTEN2_REPEAT_INDEX = "KEY_MEDIA_LISTEN2_REPEAT_INDEX";
        public static final String KEY_PLAY_MOTHER_TONGUE_ONLY_FIRST_ROUND_FINISH_ON_CONVERSATION = "KEY_PLAY_MOTHER_TONGUE_ONLY_FIRST_ROUND_FINISH_ON_CONVERSATION";
        public static final String KEY_PLAYER_LAST_PLAYED = "KEY_PLAYER_LAST_PLAYED";
        public static final String KEY_PLAYER_LAST_PLAYED_HIDED_VIDEO = "KEY_PLAYER_LAST_PLAYED_HIDED_VIDEO";
        public static final String KEY_PLAYER_FILE_SORT = "KEY_PLAYER_FILE_SORT";
        public static final String KEY_CHANGE_SERVER_SHOW_LOGIN = "KEY_CHANGE_SERVER_SHOW_LOGIN";
        public static final String KEY_PAUSE_TIME_TO_REPEAT_TTS = "KEY_PAUSE_TIME_TO_REPEAT_TTS";
        public static final String KEY_PLAYER_SUBTITLE_MODE = "KEY_PLAYER_SUBTITLE_MODE";
        public static final String KEY_PLAYER_SUBTITLE_FULL_SCREEN_FONT_SIZE = "KEY_PLAYER_SUBTITLE_FULL_SCREEN_FONT_SIZE";
        public static final String KEY_PLAYER_SUBTITLE_FONT_SIZE = "KEY_PLAYER_SUBTITLE_FONT_SIZE";
        public static final String KEY_PLAYER_SWIPE_FOR_BACK_WARD = "KEY_PLAYER_SWIPE_FOR_BACK_WARD";
        public static final String KEY_PLAYER_SWIPE_FOR_BACK_WARD_INDEX = "KEY_PLAYER_SWIPE_FOR_BACK_WARD_INDEX";
        public static final String KEY_PLAYER_TAP_FOR_BACK_WARD = "KEY_PLAYER_TAP_FOR_BACK_WARD";
        public static final String KEY_PLAYER_TAP_FOR_BACK_WARD_INDEX = "KEY_PLAYER_TAP_FOR_BACK_WARD_INDEX";
        public static final String KEY_PLAYER_INCLUDE_MOTHER_TONGUE_SUBTITLE = "KEY_PLAYER_INCLUDE_MOTHER_TONGUE_SUBTITLE";
        public static final String KEY_PLAYER_PLAY_DIFFICULT_WORDS_BEFORE_PLAYING_SUBTITLES = "KEY_PLAYER_PLAY_DIFFICULT_WORDS_BEFORE_PLAYING_SUBTITLES";
        public static final String KEY_PLAYER_PLAY_ALL_DIFFICULT_WORDS_BEFORE_PLAYING_MUSIC = "KEY_PLAYER_PLAY_ALL_DIFFICULT_WORDS_BEFORE_PLAYING_MUSIC";
        public static final String KEY_PLAYER_PLAY_TITLE_BY_TTS_BEFORE_PLAYING = "KEY_PLAYER_PLAY_TITLE_BY_TTS_BEFORE_PLAYING";
        public static final String KEY_PLAYER_DISPLAY_START_END_TIME_IN_SUBTITLE_VIEW = "KEY_PLAYER_DISPLAY_START_END_TIME_IN_SUBTITLE_VIEW";
        public static final String KEY_PLAYER_SYNC_SUBTITLES_AT_SERVER = "KEY_PLAYER_SYNC_SUBTITLES_AT_SERVER";
        public static final String KEY_HIDE_KNOWN_DIALOGS_DURING_PLAYING = "KEY_HIDE_KNOWN_DIALOGS_DURING_PLAYING";
        public static final String KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_AT_ONCE = "KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_AT_ONCE";
        public static final String KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_AT_ONCE_INDEX = "KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_AT_ONCE_INDEX";
        public static final String KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_PLAY_PARTS = "KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_PLAY_PARTS";
        public static final String KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_PLAY_PARTS_INDEX = "KEY_PLAYER_LISTEN_COMPREHENSION_PLAY_SUBTITLES_PLAY_PARTS_INDEX";
//        public static final String KEY_PLAYER_HIDE_USELESS_SUBTITLES = "KEY_PLAYER_HIDE_USELESS_SUBTITLES";
        public static final String KEY_PLAYER_SHOW_NORMAL_VIDEO_FILE_LIST = "KEY_PLAYER_SHOW_NORMAL_VIDEO_FILE_LIST";
        public static final String KEY_PLAYER_SHOW_VIDEO_IN_DCIM = "KEY_PLAYER_SHOW_VIDEO_IN_DCIM";
        public static final String KEY_PLAYER_MAX_QUIZ_COUNT = "KEY_PLAYER_MAX_QUIZ_COUNT";
        public static final String KEY_PLAYER_QUIZ_TYPE = "KEY_PLAYER_QUIZ_TYPE";
        public static final String KEY_PLAYER_GET_ALL_VIDEO_FIRST_TIME = "KEY_PLAYER_GET_ALL_VIDEO_FIRST_TIME";
        public static final String KEY_PLAYER_TRANSLATE_SUBTITLE_FROM_SERVER = "KEY_PLAYER_TRANSLATE_SUBTITLE_FROM_SERVER";
        public static final String KEY_DATABASE_VERSION = "KEY_DATABASE_VERSION";
        public static final String KEY_VIDEO_THUMBNAIL_INTERVAL = "KEY_VIDEO_THUMBNAIL_INTERVAL";
        public static final String KEY_VIDEO_THUMBNAIL_COUNT_IN_A_ROW = "KEY_VIDEO_THUMBNAIL_COUNT_IN_A_ROW";
        public static final String KEY_SELECTED_VIDEO_THUMBNAIL_AUTO_SCROLL_INTERVAL = "KEY_SELECTED_VIDEO_THUMBNAIL_AUTO_SCROLL_INTERVAL";
        public static final String KEY_SELECTED_TRANSLATOR_ID = "KEY_SELECTED_TRANSLATOR_ID";
        public static final String KEY_PLAY_ONLY_DIALOGS_IN_LISTEN_COMPREHENSION = "KEY_PLAY_ONLY_DIALOGS_IN_LISTEN_COMPREHENSION";
        public static final String KEY_FREE_POINT_CALL_COUNT = "KEY_FREE_POINT_CALL_COUNT";
        public static final String KEY_COUNT_OF_1ST_AMKI_GRADE = "KEY_COUNT_OF_1ST_AMKI_GRADE";
        public static final String KEY_APP_USE_COUNT = "KEY_COUNT_OF_APP_USE";
        public static final String KEY_DELAY_SUBTILE_MIN_MAX_VALUE = "KEY_DELAY_SUBTILE_MIN_MAX_VALUE";

        public static final String KEY_SUBTITLE_TO_HIDE_CHOSEN_HIDED = "KEY_SUBTITLE_TO_HIDE_CHOSEN_HIDED";
        public static final String KEY_SUBTITLE_TO_HIDE_MUSIC_SYMBOL = "KEY_SUBTITLE_TO_HIDE_MUSIC_SYMBOL";
        public static final String KEY_SUBTITLE_TO_HIDE_PAIRED_BRACKET = "KEY_SUBTITLE_TO_HIDE_PAIRED_BRACKET";
        public static final String KEY_SUBTITLE_TO_HIDE_ALL_CAPITALS = "KEY_SUBTITLE_TO_HIDE_ALL_CAPITALS";
        public static final String KEY_SUBTITLE_TO_HIDE_INCLUDING_URL = "KEY_SUBTITLE_TO_HIDE_INCLUDING_URL";
        public static final String KEY_SUBTITLE_TO_HIDE_NO_STUDY_LANG_CHARACTER = "KEY_SUBTITLE_TO_HIDE_NO_STUDY_LANG_CHARACTER";
        public static final String KEY_SUBTITLE_TO_HIDE_EXCEPT_UNKNOWN_SUBTITLES = "KEY_SUBTITLE_TO_HIDE_EXCEPT_UNKNOWN_SUBTITLES";
        public static final String KEY_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS = "KEY_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS";
        public static final String KEY_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NUMBER = "KEY_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NUMBER";
        public static final String KEY_SUBTITLE_TO_HIDE_LONGER_20_WORDS = "KEY_SUBTITLE_TO_HIDE_LONGER_20_WORDS";
        public static final String KEY_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER = "KEY_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER";
        public static final String KEY_SUBTITLE_TO_HIDE_1_WORD = "KEY_SUBTITLE_TO_HIDE_1_WORD";
        public static final String KEY_SUBTITLE_TO_HIDE_REPEATED_1_WORD = "KEY_SUBTITLE_TO_HIDE_REPEATED_1_WORD";

        public static final String KEY_SUBTITLE_TO_HIDE_KNOWN_SUBTITLES = "KEY_SUBTITLE_TO_HIDE_KNOWN_SUBTITLES";
        public static final String KEY_KEEP_PLAYING_ON_BACKGROUND_MODE = "KEY_KEEP_PLAYING_ON_BACKGROUND_MODE";
        public static final String KEY_PLAY_VIDEO_AUTOMATICALLY_WHEN_OPEN_IT = "KEY_PLAY_VIDEO_AUTOMATICALLY_WHEN_OPEN_IT";
        public static final String KEY_PLAY_VIDEO_FROM_WHERE_YOU_LEFT = "KEY_PLAY_VIDEO_FROM_WHERE_YOU_LEFT";
        public static final String KEY_HIDE4_BUTTONS_ON_PLAYING_SCREEN = "KEY_HIDE4_BUTTONS_ON_PLAYING_SCREEN";
        public static final String KEY_SHOW_ADVANCED_MODE = "KEY_SHOW_ADVANCED_MODE";
        public static final String KEY_RANDOM_PLAY_MODE = "KEY_RANDOM_PLAY_MODE";
        public static final String KEY_SHOW_SUBTITLE_TABLE_WHEN_OPEN = "KEY_SHOW_SUBTITLE_TABLE_WHEN_OPEN";
        public static final String KEY_SHOW_ICT_TERMS = "KEY_SHOW_ICT_TERMS";
        public static final String KEY_SHOW_LOCAL_TERMS = "KEY_SHOW_LOCAL_TERMS";
        public static final String KEY_SELECTED_THEME = "KEY_SELECTED_THEME";
        public static final String KEY_SELECTED_GPT = "KEY_SELECTED_GPT";
        public static final String KEY_PLAYER_WIDTH_PERCENT = "KEY_PLAYER_WIDTH_PERCENT";
        public static final String KEY_HIDDEN_BUTTONS_TRANSPARENCY_ON_FULLSCREEN = "KEY_HIDDEN_BUTTONS_TRANSPARENCY_ON_FULLSCREEN";
        public static final String KEY_SUBTITLE_VIEW_TRANSPARENCY_ON_FULLSCREEN = "KEY_SUBTITLE_VIEW_TRANSPARENCY_ON_FULLSCREEN";
        public static final String KEY_COUNT_OF_NOT_RATED_WORD_BEFORE_PLAYING = "KEY_COUNT_OF_NOT_RATED_WORD_BEFORE_PLAYING";
        public static final String KEY_COUNT_OF_ANALYZE_VIDEO = "KEY_COUNT_OF_ANALYZE_VIDEO";
        public static final String KEY_COUNT_OF_WATCH_VIDEO = "KEY_COUNT_OF_WATCH_VIDEO";
        public static final String KEY_PASSWORD_HIDDEN_FILES = "KEY_PASSWORD_HIDDEN_FILES";
        public static final String KEY_SHOW_LISTEN_COMPREHNESION_1_UI_AT_FIRST_TIME = "KEY_SHOW_LISTEN_COMPREHNESION_1_UI_AT_FIRST_TIME";
        public static final String KEY_SHOW_LISTEN_COMPREHNESION_2_UI_AT_FIRST_TIME = "KEY_SHOW_LISTEN_COMPREHNESION_2_UI_AT_FIRST_TIME";
        public static final String KEY_LAST_ADS_CLICKED_TIME = "KEY_LAST_ADS_CLICKED_TIME";
        public static final String KEY_PLAY_MUSIC_BETWEEN_LYRICS_ONLY = "KEY_PLAY_MUSIC_BETWEEN_LYRICS_ONLY";
        public static final String KEY_SHOW_HURIGANA_FOR_HANJA = "KEY_SHOW_HURIGANA_FOR_HANJA";
        public static final String KEY_BOOK_STYLE = "KEY_BOOK_STYLE";
        public static final String KEY_SEARCH_IN_STUDY_LANG = "KEY_SEARCH_IN_STUDY_LANG";
        public static final String KEY_SHOW_SIMPLIFIED_CHINESE = "KEY_SHOW_SIMPLIFIED_CHINESE";
        public static final String KEY_SHOW_BEYOND_LEVEL_HANJA = "KEY_SHOW_BEYOND_LEVEL_HANJA";
        public static final String KEY_ARA_HANJA_FONT_SIZE = "KEY_ARA_HANJA_FONT_SIZE";
        public static final String KEY_FIRST_TIME_TO_ADJUST_TABLE_HEIGHT = "KEY_FIRST_TIME_TO_ADJUST_TABLE_HEIGHT";
        public static final String KEY_MUSIC_PLAYLIST = "KEY_MUSIC_PLAYLIST";
        public static final String KEY_UPLOAD_RECORDING_AUTOMATICALLY = "KEY_UPLOAD_RECORDING_AUTOMATICALLY";

        public static final String KEY_REMOVE_BANNER_ADS = "KEY_REMOVE_BANNER_ADS";
        public static final String KEY_PURCHASED_REMOVE_BANNER_ADS = "KEY_PURCHASED_REMOVE_BANNER_ADS";
        public static final String KEY_PURCHASED_CLASSIC_BOOK_THOUSAND_CHARACTER = "KEY_PURCHASED_CLASSIC_BOOK_THOUSAND_CHARACTER";

        public static final String KEY_PURCHASED_EXPORT_SUBTITLE = "KEY_PURCHASED_EXPORT_SUBTITLE";
        public static final String KEY_TO_SHOW_MERGE_SUBTITLE_POPUP = "KEY_TO_SHOW_MERGE_SUBTITLE_POPUP";
        public static final String KEY_APP_LOGIN_STATUS_OPEN_SUBTITLE = "APP_LOGIN_TOKEN_OPEN_SUBTITLE";
        public static final String KEY_CONVERSATION_PRACTICE_TYPE = "KEY_CONVERSATION_PRACTICE_TYPE";
        public static final String KEY_SHOW_MEANING = "KEY_SHOW_MEANING";
        public static final String KEY_PLAY_TTS_ON_CONVERSATION = "KEY_PLAY_TTS_ON_CONVERSATION";
        public static final String KEY_CHAT_GPT_WEB_MENU = "KEY_CHAT_GPT_WEB_MENU";
        public static final String KEY_CHAT_GPT_WEB_TEXT_SHORT_CUT = "KEY_CHAT_GPT_WEB_TEXT_SHORT_CUT";

        public static final String KEY_IS_FIRST_HIDE_MULTI_PLAYER_SCREEN_TAB_LAYOUT = "KEY_IS_FIRST_HIDE_MULTI_PLAYER_SCREEN_TAB_LAYOUT";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAY_VIEW = "KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAY_VIEW";
        public static final String KEY_COUNT_SHOW_TEXT_VIEW_VIDEO_FOLDERS_LOCATIONS_IN_MULTI_PLAYER = "KEY_COUNT_SHOW_TEXT_VIEW_VIDEO_FOLDERS_LOCATIONS_IN_MULTI_PLAYER";
        public static final String KEY_IS_FIRST_MULTI_VIDEOS_FROM_MENU = "KEY_IS_FIRST_MULTI_VIDEOS_FROM_MENU";
        public static final String KEY_IS_FIRST_SHOW_VIDEO_BACK_DROP_IMAGE = "KEY_IS_FIRST_SHOW_VIDEO_BACK_DROP_IMAGE";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_ACTIVITY = "KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_ACTIVITY";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_SELECT_VIDEO = "KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_SELECT_VIDEO";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_SELECT_STORED_LAYOUT = "KEY_IS_FIRST_SHOW_GUIDE_HOW_TO_USE_MULTI_PLAYER_SELECT_STORED_LAYOUT";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_CHANGE_VOCA_KNOW = "KEY_IS_FIRST_SHOW_GUIDE_CHANGE_VOCA_KNOW";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_SCREEN_COUNT = "KEY_IS_FIRST_SHOW_GUIDE_SCREEN_COUNT";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_GET_FREE_POINT = "KEY_IS_FIRST_SHOW_GUIDE_GET_FREE_POINT";
      public static final String KEY_IS_FIRST_SHOW_GUIDE_POINT_DEDUCTION = "KEY_IS_FIRST_SHOW_GUIDE_POINT_DEDUCTION";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_PLAY_BUTTON = "KEY_IS_FIRST_SHOW_GUIDE_PLAY_BUTTON";
        public static final String KEY_IS_SHOW_CONFIRM_POPUP_TO_DELETE_ITEM = "KEY_IS_SHOW_CONFIRM_POPUP_TO_DELETE_ITEM";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_PLAY_BUTTON_AFTER_LONG_CLICK_IN_MULTI_PLAYER = "KEY_IS_FIRST_SHOW_GUIDE_PLAY_BUTTON_AFTER_LONG_CLICK_IN_MULTI_PLAYER";
        public static final String KEY_IS_FIRST_SHOW_GUIDE_WATCH_AD = "KEY_IS_FIRST_SHOW_GUIDE_WATCH_AD";
        public static final String KEY_IS_FIRST_SHOW_MANAGE_EXTERNAL_STORAGE = "KEY_IS_FIRST_SHOW_MANAGE_EXTERNAL_STORAGE"; //이건 Android 11 (API 30)이후에 모든 파일에 대해서 권한을 가지는거
        public static final String KEY_IS_FIRST_SHOW_MANAGE_EXTERNAL_STORAGE_INSTALLED_USER = "KEY_IS_FIRST_SHOW_MANAGE_EXTERNAL_STORAGE_INSTALLED_USER"; //이건 기존에 앱을 설치한 유저들 한테도 물어볼려고 하는거임.
        public static final String KEY_IS_FIRST_SHOW_EXTERNAL_STORAGE = "KEY_IS_FIRST_SHOW_EXTERNAL_STORAGE"; //이건 MANAGE_EXTERNAL_STORAGE권한 말고 그 이전에 쓰던거
        public static final String KEY_IS_FIRST_SHOW_ALERT_SELECT_MANY_SCREEN_COUNT = "KEY_IS_FIRST_SHOW_ALERT_SELECT_MANY_SCREEN_COUNT";
        public static final String KEY_POINT_ADDED = "KEY_POINT_ADDED";
        public static final String KEY_IS_SAMPLE_VIDEO_COPIED = "KEY_IS_SAMPLE_VIDEO_COPIED";
        public static final String KEY_IS_FIRST_CHAT_GPT_WEB_USE = "KEY_IS_FIRST_CHAT_GPT_WEB_USE";
        public static final String KEY_IS_FIRST_AB_REPEAT_USE = "KEY_IS_FIRST_AB_REPEAT_USE";
        public static final String KEY_IS_SECOND_CHAT_GPT_WEB_USE = "KEY_IS_SECOND_CHAT_GPT_WEB_USE";
        public static final String KEY_CONVERSATION_TO_STUDY_IN_GPT_WEB = "KEY_CONVERSATION_TO_STUDY_IN_GPT_WEB";
        public static final String KEY_MAKE_ROLE_PLAYING_CONTENT = "KEY_MAKE_ROLE_PLAYING_CONTENT";
        public static final String KEY_CHOICE_COPY_SENTENCE_CONV_PRACTICE = "KEY_CHOICE_COPY_SENTENCE_CONV_PRACTICE";
        public static final String KEY_CHOICE_SUBTITLE_VOCA_MEANING_TO_COPY = "KEY_CHOICE_SUBTITLE_VOCA_MEANING_TO_COPY";
        public static final String KEY_CHOICE_ASK_WHAT_TO_DO_WITH_COPIED_TEXT = "KEY_CHOICE_ASK_WHAT_TO_DO_WITH_COPIED_TEXT";
        public static final String KEY_CHOICE_OPEN_GPT_FROM_ARA_CONV_CONVERSATION_VIEW = "KEY_CHOICE_OPEN_GPT_FROM_ARA_CONV_CONVERSATION_VIEW";
        public static final String KEY_SYNC_KNOWN_WITH_SERVER = "KEY_SYNC_KNOWN_WITH_SERVER";
        public static final String KEY_FIRST_CONVERSATION_PRACTICE_FAB = "KEY_FIRST_CONVERSATION_PRACTICE_FAB";
        public static final String KEY_VOCA_FILTER_VOCA_KNOWN = "KEY_VOCA_FILTER_VOCA_KNOWN";
        public static final String KEY_VOCA_FILTER_VOCA_UNKNOWN = "KEY_VOCA_FILTER_VOCA_UNKNOWN";
        public static final String KEY_VOCA_FILTER_VOCA_1ST = "KEY_VOCA_FILTER_VOCA_1ST";
        public static final String KEY_VOCA_FILTER_VOCA_2ND = "KEY_VOCA_FILTER_VOCA_2ND";
        public static final String KEY_VOCA_FILTER_VOCA_NOT_RATED = "KEY_VOCA_FILTER_VOCA_NOT_RATED";
        public static final String KEY_VOCA_FILTER_VOCA_BOOKMARKED = "KEY_VOCA_FILTER_VOCA_BOOKMARKED";


    }
    public static final Integer WordLevelNo_ENG_Beginner = 2;
    public static final Integer WordLevelNo_ENG_Pre_Intermediate = 4;
    public static final Integer WordLevelNo_ENG_Intermediate = 8;
    public static final Integer WordLevelNo_ENG_Post_Intermediate = 10;
    public static final Integer WordLevelNo_ENG_Advanced = 17;
    public static final Integer WordLevelNo_KO_Beginner = 2;
    public static final Integer WordLevelNo_KO_Pre_Intermediate = 4;
    public static final Integer WordLevelNo_KO_Intermediate = 8;
    public static final Integer WordLevelNo_KO_Post_Intermediate = 10;
    public static final Integer WordLevelNo_KO_Advanced = 14;
    public static final Integer WordLevelNo_JP_Beginner = 1;
    public static final Integer WordLevelNo_JP_Pre_Intermediate = 4;
    public static final Integer WordLevelNo_JP_Intermediate = 8;
    public static final Integer WordLevelNo_JP_Post_Intermediate = 10;
    public static final Integer WordLevelNo_JP_Advanced = 14;
    public static final Integer WordLevelNo_CH_S_Beginner = 1;
    public static final Integer WordLevelNo_CH_S_Pre_Intermediate = 4;
    public static final Integer WordLevelNo_CH_S_Intermediate = 6;
    public static final Integer WordLevelNo_CH_S_Post_Intermediate = 9;
    public static final Integer WordLevelNo_CH_S_Advanced = 12;
    public static final Integer WordLevelNo_HANJA_Beginner = 1;
    public static final Integer WordLevelNo_HANJA_Pre_Intermediate = 2;
    public static final Integer WordLevelNo_HANJA_Intermediate = 5;
    public static final Integer WordLevelNo_HANJA_Post_Intermediate = 7;
    public static final Integer WordLevelNo_HANJA_Advanced = 17;

    public static final String SIGN_UP_LEVEL_BEGINNER = "WordLevel_Beginner";
    public static final String SIGN_UP_LEVEL_PRE_INTERMEDIATE = "WordLevel_Pre_Intermediate";
    public static final String SIGN_UP_LEVEL_INTERMEDIATE = "WordLevel_Intermediate";
    public static final String SIGN_UP_LEVEL_POST_INTERMEDIATE = "WordLevel_Post_Intermediate";
    public static final String SIGN_UP_LEVEL_ADVANCED = "WordLevel_Advanced";
    public static final String SIGN_UP_LEVEL_GOD = "WordLevel_God";
    public static final String[] SIGN_UP_LEVELS = {SIGN_UP_LEVEL_BEGINNER, SIGN_UP_LEVEL_PRE_INTERMEDIATE, SIGN_UP_LEVEL_INTERMEDIATE, SIGN_UP_LEVEL_POST_INTERMEDIATE, SIGN_UP_LEVEL_ADVANCED, SIGN_UP_LEVEL_GOD};
//    public static final String[] SIGN_UP_LEVELS = {"WordLevel_Beginner", "WordLevel_Pre_Intermediate", "WordLevel_Intermediate", "WordLevel_Post_Intermediate", "WordLevel_Advanced", "WordLevel_God"};
    public static final String[] LANGUAGES_MOTHERS = {
            SETTING_LANGUAGES_MOTHERS.LANG_AR,
            SETTING_LANGUAGES_MOTHERS.LANG_CH_S,
            SETTING_LANGUAGES_MOTHERS.LANG_CH_T,
            SETTING_LANGUAGES_MOTHERS.LANG_DE,
            SETTING_LANGUAGES_MOTHERS.LANG_EN,
            SETTING_LANGUAGES_MOTHERS.LANG_ES,
            SETTING_LANGUAGES_MOTHERS.LANG_FR,
            SETTING_LANGUAGES_MOTHERS.LANG_IT,
            SETTING_LANGUAGES_MOTHERS.LANG_JA,
            SETTING_LANGUAGES_MOTHERS.LANG_KO,
            SETTING_LANGUAGES_MOTHERS.LANG_PT,
            SETTING_LANGUAGES_MOTHERS.LANG_RU,
            SETTING_LANGUAGES_MOTHERS.LANG_VI,
    };

    public static class SETTING_LANGUAGES_MOTHERS {
        public static final String LANG_AR = "ARABIC";
        public static final String LANG_CH_S = "CHINESE_SIMPLIFIED";
        public static final String LANG_CH_T = "CHINESE_TRADITIONAL";
        public static final String LANG_DE = "GERMAN";
        public static final String LANG_EN = "ENGLISH";
        public static final String LANG_ES = "SPANISH";
        public static final String LANG_FR = "FRENCH";
        public static final String LANG_IT = "ITALIAN";
        public static final String LANG_JA = "JAPANESE";
        public static final String LANG_KO = "KOREAN";
        public static final String LANG_PT = "PORTUGUESE";
        public static final String LANG_RU = "RUSSIAN";
        public static final String LANG_VI = "VIETNAMESE";
    }

    public static class BOOKMARK_DEFAULT {
        public static final int BOOKMARKED = 1;
        public static final int UNBOOKMARKED = 0;
        public static final String BOOKMARK_URL = "http://www.dalread.com/dalread/mobile_home";
        public static final String BOOKMARK_URL_FORMAT = BOOKMARK_URL + "?studyLang=" + API.STUDY_LANG + "&langDisplay=";
        public static final String BOOKMARK_TITLE = "DalRead";
        public static final String BOOKMARK_URL_1 = BuildConfig.BOOKMARK_URL;
        public static final String BOOKMARK_TITLE_1 = BuildConfig.BOOKMARK_TITLE;
        public static final String[] BOOKMARKS_DEFAULT = {BOOKMARK_URL, "http://www.yahoo.co.jp", "http://www.cnn.com", "http://www.cctv.com"};
        public static final int[] BOOKMARKS_DEFAULT_ICON = {R.mipmap.ic_dalread, R.mipmap.ic_yahoo, R.mipmap.ic_cnn, R.mipmap.ic_cctv};
    }

    public static final String[] VOICE_LANGS = {
            SETTING_VOICE_LANGS.VOICELANG_AR,
            SETTING_VOICE_LANGS.VOICELANG_CH_S,
            SETTING_VOICE_LANGS.VOICELANG_CH_T,
            SETTING_VOICE_LANGS.VOICELANG_DE,
            SETTING_VOICE_LANGS.VOICELANG_EN, // 4
            SETTING_VOICE_LANGS.VOICELANG_ES,
            SETTING_VOICE_LANGS.VOICELANG_FR,
            SETTING_VOICE_LANGS.VOICELANG_IT,
            SETTING_VOICE_LANGS.VOICELANG_JA,
            SETTING_VOICE_LANGS.VOICELANG_KO,
            SETTING_VOICE_LANGS.VOICELANG_PT,
            SETTING_VOICE_LANGS.VOICELANG_RU,
            SETTING_VOICE_LANGS.VOICELANG_VI};

    public static class SETTING_VOICE_LANGS {
        public static final String VOICELANG_AR = "ar-SA";       //Using in TTS
        public static final String VOICELANG_CH_S = "zh-CN";
        public static final String VOICELANG_CH_T = "zh-TW";     //zh-HK
        public static final String VOICELANG_DE = "de-DE";
        public static final String VOICELANG_EN = "en-US";
        public static final String VOICELANG_ES = "es-ES";
        public static final String VOICELANG_FR = "fr-FR";
        public static final String VOICELANG_IT = "it-IT";
        public static final String VOICELANG_JA = "ja-JP";
        public static final String VOICELANG_KO = "ko-KR";
        public static final String VOICELANG_PT = "pt-BR";       //Portuguese (pt-BR, pt-PT)
        public static final String VOICELANG_RU = "ru-RU";
        public static final String VOICELANG_VI = "vi-VN";
    }

    public static final String[] LANGUAGES_LOCAL = {
            SETTING_LANGUAGES_LOCAL.LANG_AR,
            SETTING_LANGUAGES_LOCAL.LANG_ZH_CN,
            SETTING_LANGUAGES_LOCAL.LANG_ZH_TW,
            SETTING_LANGUAGES_LOCAL.LANG_DE,
            SETTING_LANGUAGES_LOCAL.LANG_EN,
            SETTING_LANGUAGES_LOCAL.LANG_ES,
            SETTING_LANGUAGES_LOCAL.LANG_FR,
            SETTING_LANGUAGES_LOCAL.LANG_IT,
            SETTING_LANGUAGES_LOCAL.LANG_JA,
            SETTING_LANGUAGES_LOCAL.LANG_KO,
            SETTING_LANGUAGES_LOCAL.LANG_PT,
            SETTING_LANGUAGES_LOCAL.LANG_RU,
            SETTING_LANGUAGES_LOCAL.LANG_VI
    };

    public static class SETTING_LANGUAGES_LOCAL {
        public static final String LANG_AR = "ar_SA";
        public static final String LANG_ZH_CN = "zh_CN";
        public static final String LANG_ZH_TW = "zh_TW";
        public static final String LANG_DE = "de_DE";
        public static final String LANG_EN = "en_US";
        public static final String LANG_ES = "es_ES";
        public static final String LANG_FR = "fr_FR";
        public static final String LANG_IT = "it_IT";
        public static final String LANG_JA = "ja_JP";
        public static final String LANG_KO = "ko_KR";
        public static final String LANG_PT = "pt_BR";
        public static final String LANG_RU = "ru_RU";
        public static final String LANG_VI = "vi_VN";
    }

    public static class PASSWORD_RESPONSE_CODE {
        public static final int PASSWORD_CHANGE_OK = 1;
        public static final int PASSWORD_CHANGE_FAIL_CURRENTPASSWORD_IS_WRONG = 2;
        public static final int PASSWORD_CHANGE_FAIL_CANNOTCHANGEPASSWORD = 99;
    }

    public static class SORTS {
        public static final int SORT_NONE = 0;
        public static final int SORT_ASC = 1;
        public static final int SORT_DESC = 2;
        public static final int SORT_APPEARANCE = 3;
        public static final int SORT_FREQUENCY = 4;
        public static final int SORT_ANIMATION = 90;
    }

    public static class WORD_SPEAK {
        public static final int SPEAK_OFF = 0;
        public static final int SPEAK_SINGLE = 1;
        public static final int SPEAK_ALL = 2;
    }

    public static class SPEAKS {
        public static final int SPEAK_NONE = 0;
        public static final int SPEAK_1 = 1;
        public static final int SPEAK_3 = 3;
        public static final int SPEAK_5 = 5;
        public static final int SPEAK_10 = 10;
    }

    public static class CAB {
        public static final int CAB_COPY = 0;
        public static final int CAB_SEND_ACTIIVTY = 1;
        public static final int CAB_WORD_LIST = 2;
        public static final int CAB_CHECK_WEBVIEW = 3;
    }

    public static class CAB_CALL_BACK {
        public static final int CAB_CALL_BACK_NONE = 0;
        public static final int CAB_CALL_BACK_EDIT_TEXT = 1;
        public static final int CAB_CALL_BACK_WEB_VIEW = 2;
    }

    public static class LANGUAGES_SYSTEM {
        public static final String EN = "en";
        public static final String JA = "ja";
        public static final String VI = "vi";
        public static final String KO = "ko";
        public static final String CN = "cn";
        public static final String ZH = "zh";
        public static final String zh_CN = "zh_CN";
    }

    public static class AUDIO_SOUND {
        public static final int AUDIO_NORMAL = 0;
        public static final int AUDIO_MUTE = 1;
        public static final int AUDIO_ZERO = 2;
    }

    public static class LYRIC {
        public static final String ID_TAG_TITLE = "ti";
        public static final String ID_TAG_TITLE_TTS = "titts";
        public static final String ID_TAG_ARTIST = "ar";
        public static final String ID_TAG_ARTIST_TTS = "artts";
        public static final String ID_TAG_ALBUM = "al";
        public static final String ID_TAG_CREATOR_LRCFILE = "by";
        public static final String ID_TAG_CREATOR_SONGTEXT = "au";
        public static final String ID_TAG_LENGTH = "length";
        public static final String ID_TAG_OFFSET = "offset";
    }



    public static class ACCESS_OR_EXIT_APP {
        public static final int ACCESS = 0;
        public static final int EXIT = 1;
    }

    public static class ADD_POINT_USER {
        public static final int ADD_POINT_OK = 1;
        public static final int ADD_POINT_NG = 2;
        public static final int ADD_POINT_NG_POINT_IS_NOT_NUMBER = 3;
    }

    public static class PLAYBACK_SETTINGS {
        public static final String SEEK_TIME = "seek_time";
        public static final String SEEK_TYPE = "seek_type";
        public static final String PLAY_BACKGROUND = "bg_play";
    }

    public static class GLOBAL_SUBTITLE_SETTINGS {
        public static final String SHOW_SUBTITLE = "show_cc";
        public static final String TEXT_OUTLINE = "text_outline";
        public static final String FONT = "font";
        public static final String FONT_TYPE = "font_type";
        public static final String FONT_SIZE = "font_size";
        public static final String LINE_HEIGHT = "line_height";
        public static final String UNKOWN_WORD_COLOR = "unknown_word_color";
        public static final String KNOWN_WORD_COLOR = "known_word_color";
        public static final String FONT_SIZE_MEANING = "font_size_meaning";
        public static final String FONT_SIZE_PRONOUNCE = "font_size_pronounce";
        public static final String FONT_COLOR_PRONOUNCE = "font_color_pronounce";
        public static final String FONT_COLOR_MEANING = "font_color_meaning";
    }

    public static final int QUESTION_TYPE_NAME = 0;
    public static final int QUESTION_TYPE_MEANING = QUESTION_TYPE_NAME + 1;
    public static final int QUESTION_TYPE_COUNT = QUESTION_TYPE_MEANING + 1;
    public static final int QUESTION_SIZE = 10; // number of questions in a test
    public static final int ANSWER_SIZE = 4; // number of answers in a question
    public static final int NEXT_QUESTION_TIME = 1000;
    public static final int TEST_MAX_SCORE = 100;
    public static final int DRAWER_CLOSE_TIME = 300;
    public static final int NEXT_SCREEN_TIME = 1000;
    public static final int LOADING_MAX_ITEM = 500;
    public static final int SPLASH_TIME = 1000;
    public static final String BREAK_SIGN = "#";
    public static final int SILENCE_TIME = 600;
    public static final int VOICE_MAX_SIZE = 5;
    public static final int READ_COUNT_MIN = 1;
    public static final int READ_COUNT_MAX = 100;
    public static final int READ_COUNT_DEFAULT = 3;
    public static final long RECORD_TIME_MAX = 60000;
    public static final long RECORD_TIME_TICK = 1000;
    public static final long ON_RESUME_DELAY = 500;
    public static final String FIREBASE_EMAIL = "test@gmail.com";
    public static final String FIREBASE_PASSWORD = "test!1";
    public static final int PRACTICE_COUNT_MAX = 5;
    public static final String URL_NAVER_DICTIONARY_EN = "https://endic.naver.com/search.nhn?searchOption=all&query=%s";
    public static final String URL_NAVER_DICTIONARY_ZH = "https://zh.dict.naver.com/#/search?query=%s";
    public static final int DOWNLOAD_CONFIRM_SIZE = 1024 * 1024;
    public static final String ENGLISH_PRONUNCIATION = "à á ɑ̀ ɑ́ α ὰ ά æ æ̀ ǽ ə ə̀ ə́ è é ɛ έ ɪ ì í ò ó ɔ ɔ̀ ɔ́ ù ú ʌ ʌ̀ ʌ́ ŋ ʊ ʒ θ ð ∫ʤ ʧ ː";
    public static final String CHINESE_PRONUNCIATION = "ā á ǎ à ē é ě è ī í ǐ ì ō ó ǒ ò ū ú ǔ ù";
    public static final int GRADE_1_MAX = 9999;
    public static final boolean DISPLAY_PRONUNCIATION_DEFAULT_STUDENT = true;
    public static final boolean DISPLAY_PRONUNCIATION_DEFAULT_OTHER = true;
    public static final long AUTO_START_SELF_PRACTICE_DELAY = 1000;
    public static final long START_SELF_PRACTICE_NEXT_ROUND_DELAY = 2000;
    public static final long SELF_PRACTICE_DISPLAY_VOCA_TIME = 1000;
    public static final long START_SELF_PRACTICE_COUNT_DOWN_TIME = 3100;
    public static final long START_SELF_PRACTICE_COUNT_DOWN_INTERVAL = 1000;
    public static final String SELF_PRACTICE_A = "A";
    public static final String SELF_PRACTICE_B = "B";
    public static final int SELF_PRACTICE_ROUND_MAX = 6;
    public static final long RECORD_SELF_PRACTICE_COUNT_DOWN_TIME = 10100;
    public static final long RECORD_SELF_PRACTICE_COUNT_DOWN_INTERVAL = 1000;
    public static final long STOP_NATIVE_SPEAKER_RECORDER_DELAY = 200;
    public static final String CLIENT_TYPE_ANDROID = "ANDROID";
    public static final int PRACTICE_HANGUL_SENTENCE_MAX = 10;
    public static final long START_PRACTICE_HANGUL_COUNT_DOWN_TIME = 3100;
    public static final long CORRECT_PRACTICE_HANGUL_COUNT_DOWN_TIME = 1100;
    public static final long INCORRECT_PRACTICE_HANGUL_COUNT_DOWN_TIME = 2100;
    public static final long PRACTICE_HANGUL_COUNT_DOWN_INTERVAL = 1000;
    public static final long PRACTICE_HANGUL_BASE_TIME = 10000;
    public static final long PLAY_STUDENT_VOICE_DELAY_TIME = 1000;
    public static final long PLAY_STUDENT_VOICE_RETRY_MAX_TIME = 3;
    public static final int MAX_HOMEWORK_MIN = 1;
    public static final int MAX_HOMEWORK_MAX = 100;
    public static final int ORDER_MIN = 1;
    public static final int ORDER_MAX = 10;
    public static final int MAX_HOMEWORK_DEFAULT = 20;
    public static final int MAX_QUIZ_DEFAULT = 20;
    public static final int MAX_HANJA_QUIZ_DEFAULT = 5;
    public static final boolean ALLOW_CHAT_DEFAULT = true;
    public static final boolean BLINKMOOE_IN_WORDLIST_DEFAULT = false;
    public static final boolean ASTERISKMOOE_IN_WORDLIST_DEFAULT = false;
    public static final boolean SHARE_MY_RECORDING_DEFAULT = true;
    public static final boolean AUTO_PLAY_IN_RECORDING_ALL_DEFAULT = true;
    public static final boolean USE_LESSON_NOTIFICATION_DEFAULT = true;
    public static final int TIME_TO_NOTIFY_BEFORE_LESSON_START_DEFAULT = 3;
    public static final int TIME_TO_NOTIFY_BEFORE_LESSON_FINISH_DEFAULT = 1;
    public static final long PLAY_VOICE_INTERVAL = 500;
    public static final long STOP_CHAT_VOICE_DELAY_TIME = 200;
    public static final long STOP_BLINK_DELAY_TIME = 2000;
    public static final long TOAST_SHORT_TIME = 2000;
    public static final long ANSWER_QUIZ_TIME = 200000;
    public static final long ANSWER_HANJA_QUIZ_TIME = 30000;
    public static final int ANSWER_HANJA_QUIZ_REPEAT_COUNT = 3;
    public static final int ANSWER_HANJA_QUIZ_START_INDEX = 1;
    public static final int NUMBER_OF_RECORDINGS_PREREQUISITE = 2;
    public static final int STUDY_ROLE_STUDENT = 0;
    public static final int STUDY_ROLE_TUTOR = 1;
    public static final int STUDY_ROLE_OBSERVER = 2;
//    public static final int SHOW_ASTERISK_OFF = 0;  // don't show asterisk = show sentence
//    public static final int SHOW_ASTERISK_ON = 1;  // show difficult words only
//    public static final int SHOW_ASTERISK_ALL = 2;  // completely hide sentence
    public static class SHOW_ASTERISK {
        public static final int SHOW_SENTENCE = 0;
        public static final int SHOW_DIFFICULT_WORD_ONLY = 1;
        public static final int HIDE_SENTENCE = 2;
    }
    public static final int SHOW_FURIGANA_OFF = 0;  // don't show furigana at all
    public static final int SHOW_FURIGANA_DIFFICULT_WORDS_ONLY = 1;  // show furigana for difficult words only
    public static final int SHOW_FURIGANA_ALL = 2;  // show furigana for all words

    public static final int SHOW_ASTERISK_ALL_QUESTIONS = 3;
    public static final int STUDY_ORDER_ROLE_PLAYING_COUNT = 4;
    public static final int SHOW_SENTENCE_ALL = 1;
    public static final int HIDE_SENTENCE_KNOWN = 2;
    public static final int HIDE_SENTENCE_EXCELLENT = 3;
    public static final int HIDE_SENTENCE_KNOWN_EXCELLENT = 4;
    public static final String PATTERN_SPECIAL_CHARACTERS = "^[\\sㄱ-ㅎㅏ-ㅣ.,<>?/'\"~*&(){}|_`:;!@#$%^*+=\\-\\[\\]\\\\ㆍ]*$";
    public static final String NOTIFICATION_CHANNEL_LESSON_ID = "200518";
    private static final int VIBRATE_ON_MS = 1000;
    private static final int VIBRATE_OFF_MS = 300;
    public static final long[] VIBRATION_PATTERN = {0, VIBRATE_ON_MS, VIBRATE_OFF_MS, VIBRATE_ON_MS};
    public static final int SHOW_TOAST_NO = 0;
    public static final int SHOW_TOAST_YES = 1;
    public static final int REPEAT_TOPICS_MIN = 1;
    public static final int REPEAT_TOPICS_MAX = 5;
    public static final int REPEAT_COUNT_MIN = 1;
    public static final int REPEAT_COUNT_MAX = 5;
    public static final long SAVE_STUDIED_WORKBOOK_ID_DELAY_TIME = 60000;
    public static final int EXAM_WORD_COUNT_MAX = 30;
    public static final int EXAM_SOURCE_CURRENT_WORKBOOK = 1;
    public static final int EXAM_SOURCE_MEMORIZATION_TARGETS = 2;
    public static final int EXAM_TYPE_NORMAL = 0;
    public static final int EXAM_TYPE_QUESTION_WORD = 1;
    public static final int EXAM_TYPE_QUESTION_MEANING = 2;
    public static final int EXAM_TYPE_QUESTION_RANDOM = 3;
    public static final int LESSON_MINUTES_DEFAULT = 30;
    public static final int DUPLICATE_LESSON_DAY_MIN = 1;
    public static final int DUPLICATE_LESSON_DAY_MAX = 31;
    public static final int DUPLICATE_LESSON_DAY_DEFAULT = 7;
    public static final boolean MAKE_RUBY_TEXT = true;
    public static final int CHAT_MESSAGE_LIMIT = 25;
    public static final int COUNT_OF_PHRASES_TO_STUDY_AT_ONCE_MIN = 1;
    public static final int COUNT_OF_PHRASES_TO_STUDY_AT_ONCE_MAX = 10;
    public static final int COUNT_OF_PHRASES_TO_STUDY_AT_ONCE_DEFAULT = 5;
    public static final int READ_COUNT_LISTEN_COMPREHENSION_MIN = 0;
    public static final int READ_COUNT_LISTEN_COMPREHENSION_MAX = 10;
    public static final int MAX_VOCA_COUNT_TO_USE_WORD_HOLDER = 4;
    public static final int ITEM_COUNT_TO_DISPALY_FAST_SCROLLER_TO_RECYCLER_VIEW = 30;
    public static final int VOCA_LEVEL_INDIC_MAX = 999;
    public static final int VOCA_LEVEL_INDIC_MIN = 1;
    public static final int COUNT_OF_CANDIDATE_FOR_TODAY_HANJA = 15;

    public static class MULTIPLE_CHOICE_QUESTION_TYPE {
        public static final int NONE = 0;
        public static final int VOCA = 1;
        public static final int MEANING = 2;
        public static final int RANDOM = 3;
    }
    public static class BUNDLE {
        public static final String KEY_ASTERISK = "KEY_ASTERISK";
        public static final String KEY_AUTO_MOVE = "KEY_AUTO_MOVE";
        public static final String KEY_BOOK_NAME = "KEY_BOOK_NAME";
        public static final String KEY_CHAT_ROOM = "KEY_CHAT_ROOM";
        public static final String KEY_CHAT_ROOM_INFO = "KEY_CHAT_ROOM_INFO";
        public static final String KEY_DATA = "KEY_DATA";
        public static final String KEY_DIC_HANJA = "KEY_DIC_HANJA";
        public static final String KEY_EVENT_DATA = "KEY_EVENT_DATA";
        public static final String KEY_FROM_MENU = "KEY_FROM_MENU";
        public static final String KEY_GRAMMAR_ID = "KEY_GRAMMAR_ID";
        public static final String KEY_GET_DATA_FROM_NETWORK = "KEY_GET_DATA_FROM_NETWORK";
        public static final String KEY_HANJA_DATA = "KEY_HANJA_DATA";
        public static final String KEY_HANJA_GROUP_TYPE_MODEL = "KEY_HANJA_GROUP_TYPE_MODEL";
        public static final String KEY_HANJA_GROUP_VIEW_TYPE = "KEY_HANJA_GROUP_VIEW_TYPE";
        public static final String KEY_ID = "KEY_ID";
        public static final String KEY_INPUT_STATE = "KEY_INPUT_STATE";
        public static final String KEY_IS_OPEN = "KEY_IS_OPEN";
        public static final String KEY_ITEM_CLICK_ACTION = "KEY_ITEM_CLICK_ACTION";
        public static final String KEY_LESSON = "KEY_LESSON";
        public static final String KEY_LESSON_ID = "KEY_LESSON_ID";
        public static final String KEY_LESSON_LIST_ACTION = "KEY_LESSON_LIST_ACTION";
        public static final String KEY_LESSON_MODE = "KEY_LESSON_MODE";
        public static final String KEY_LESSON_OPTION = "KEY_LESSON_OPTION";
        public static final String KEY_LESSON_READING_ID = "KEY_LESSON_READING_ID";
        public static final String KEY_LESSON_TYPE = "KEY_LESSON_TYPE";
        public static final String KEY_LIST_STATE = "KEY_LIST_STATE";
        public static final String KEY_METHOD_NAME = "KEY_METHOD_NAME";
        public static final String KEY_OPEN_FROM_LAUNCHER = "KEY_OPEN_FROM_LAUNCHER";
        public static final String KEY_OPPONENT_ID = "KEY_OPPONENT_ID";
        public static final String KEY_OTHER_USER_ID = "KEY_OTHER_USER_ID";
        public static final String KEY_OTHER_USER_NAME = "KEY_OTHER_USER_NAME";
        public static final String KEY_PARENT_BOOK_ID_LIST = "KEY_PARENT_BOOK_ID_LIST";
        public static final String KEY_PRACTICE_ONLY = "KEY_PRACTICE_ONLY";
        public static final String KEY_PROFILE = "KEY_PROFILE";
        public static final String KEY_QUIZ_HANJA_LIST = "KEY_QUIZ_HANJA_LIST";
        public static final String KEY_QUIZ_LIST = "KEY_QUIZ_LIST";
        public static final String KEY_READ_ONLY = "KEY_READ_ONLY";
        public static final String KEY_RECORDING_ALL = "KEY_RECORDING_ALL";
        public static final String KEY_REFRESH_CLICK_ACTION = "KEY_REFRESH_CLICK_ACTION";
        public static final String KEY_RUBY_TYPE = "KEY_RUBY_TYPE";
        public static final String KEY_SCHEDULE_CLICK_ACTION = "KEY_SCHEDULE_CLICK_ACTION";
        public static final String KEY_SELECT_MULTI = "KEY_SELECT_MULTI";
        public static final String KEY_SELF_STUDY = "KEY_SELF_STUDY";
        public static final String KEY_SHOW_4_BUTTONS = "KEY_SHOW_4_BUTTONS";
        public static final String KEY_SHOW_FAB_BUTTON = "KEY_SHOW_FAB_BUTTON";
        public static final String KEY_SHOW_SENTENCE = "KEY_SHOW_SENTENCE";
        public static final String KEY_SHOW_STUDENT_MEANING = "KEY_SHOW_STUDENT_MEANING";
        public static final String KEY_SLIDE_RIGHT_TO_LEFT_IN_ANIMATION = "KEY_SLIDE_RIGHT_TO_LEFT_IN_ANIMATION";
        public static final String KEY_SLIDE_LEFT_TO_RIGHT_ANIMATION = "KEY_SLIDE_LEFT_TO_RIGHT_ANIMATION";
        public static final String KEY_START_TIME = "KEY_START_TIME";
        public static final String KEY_STUDENT_ID = "KEY_STUDENT_ID";
        public static final String KEY_STUDIED_BOOKS = "KEY_STUDIED_BOOKS";
        public static final String KEY_STUDY_INFO = "KEY_STUDY_INFO";
        public static final String KEY_STUDY_LANG = "KEY_STUDY_LANG";
        public static final String KEY_STUDY_ORDER_POS = "KEY_STUDY_ORDER_POS";
        public static final String KEY_STUDY_ORDER_ROLE_PLAYING_POS = "KEY_STUDY_ORDER_ROLE_PLAYING_POS";
        public static final String KEY_STUDY_ROLE_PLAYING_CONTENT = "KEY_STUDY_ROLE_PLAYING_CONTENT";
        public static final String KEY_STUDY_VOCA_EXAM_LIST = "KEY_STUDY_VOCA_EXAM_LIST";
        public static final String KEY_STUDY_VOCA_LIST = "KEY_STUDY_VOCA_LIST";
        public static final String KEY_SUB_DATABASE_PATH = "KEY_SUB_DATABASE_PATH";
        public static final String KEY_TUTOR_ID = "KEY_TUTOR_ID";
        public static final String KEY_URL = "KEY_URL";
        public static final String KEY_USER_LIST_TYPE = "KEY_USER_LIST_TYPE";
        public static final String KEY_WORDLIST_TYPE = "KEY_WORDLIST_TYPE";
        public static final String KEY_VOCA = "KEY_VOCA";
        public static final String KEY_VOCA_BOOK = "KEY_VOCA_BOOK";
        public static final String KEY_VOCA_BOOKS = "KEY_VOCA_BOOKS";
        public static final String KEY_VOCA_BOOK_ID = "KEY_VOCA_BOOK_ID";
        public static final String KEY_VOCA_EXPRESSION_BOOK_ID = "KEY_VOCA_EXPRESSION_BOOK_ID";
        public static final String KEY_VOCA_BOOK_NAME = "KEY_VOCA_BOOK_NAME";
        public static final String KEY_VOCA_FROM_ALL_VOCA_BOOK = "KEY_VOCA_FROM_ALL_VOCA_BOOK";
        public static final String KEY_VOCA_FILTER_TYPE = "KEY_VOCA_FILTER_TYPE";
        public static final String KEY_VOCA_ID = "KEY_VOCA_ID";
        public static final String KEY_VOCA_PLAYLIST = "KEY_VOCA_PLAYLIST";
        public static final String KEY_VOCA_PRACTICE_LIST = "KEY_VOCA_PRACTICE_LIST";
        public static final String KEY_VOCA_READING = "KEY_VOCA_READING";
        public static final String KEY_VOCA_STUDY_CHAT_EXAM = "KEY_VOCA_STUDY_CHAT_EXAM";
        public static final String KEY_VOCA_TYPE = "KEY_VOCA_TYPE";
        public static final String KEY_VOCA_TYPE_ID = "KEY_VOCA_TYPE_ID";
        public static final String KEY_VOCABOOK_TYPE_CODE_SERVER = "KEY_VOCABOOK_TYPE_CODE_SERVER";
        public static final String KEY_VOICE_DATA = "KEY_VOICE_DATA";
        public static final String KEY_VOCA_USER_BOOK_LIST = "KEY_VOCA_USER_BOOK_LIST";
        public static final String KEY_NUMBER_OF_SCREENS_MULTIPLE_PLAYER = "KEY_NUMBER_OF_SCREENS_MULTIPLE_PLAYER";
        public static final String KEY_LOAD_LAST_WATCHED_VIDEO_MULTIPLE_PLAYER = "KEY_LOAD_LAST_WATCHED_VIDEO_MULTIPLE_PLAYER";
        public static final String KEY_SELECTED_VIDEO_FILE = "KEY_SELECTED_VIDEO_FILE";
        public static final String KEY_SELECTED_VIDEO_FILES = "KEY_SELECTED_VIDEO_FILES";
        public static final String KEY_ALL_VIDEO_FILES_IN_LIST = "KEY_ALL_VIDEO_FILES_IN_LIST";
        public static final String KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ID = "KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ID";
        public static final String KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ROTATE_LAYOUT = "KEY_MULTI_PLAYER_SCREEN_STORED_LAYOUT_ROTATE_LAYOUT";

//        public static final String KEY_VOCA_USER_BOOK_LIST = "KEY_VOCA_USER_BOOK_LIST";
//        public static final String KEY_VOCA_BOOK = "KEY_VOCA_BOOK";
//        public static final String KEY_VOCA_BOOK_ID = "KEY_VOCA_BOOK_ID";
//        public static final String KEY_VOCA_BOOK_NAME = "KEY_VOCA_BOOK_NAME";
//        public static final String KEY_VOCA_PLAYLIST = "KEY_VOCA_PLAYLIST";
//        public static final String KEY_VOCA_STUDY_CHAT_EXAM = "KEY_VOCA_STUDY_CHAT_EXAM";
//        public static final String KEY_VOCA = "KEY_VOCA";
//        public static final String KEY_VOCA_ID = "KEY_VOCA_ID";
//        public static final String KEY_VOCA_TYPE = "KEY_VOCA_TYPE";
//        public static final String KEY_SELF_STUDY = "KEY_SELF_STUDY";
//        public static final String KEY_FROM_MENU = "KEY_FROM_MENU";
//        public static final String KEY_PRACTICE_ONLY = "KEY_PRACTICE_ONLY";
//        public static final String KEY_VOCA_PRACTICE_LIST = "KEY_VOCA_PRACTICE_LIST";
//        public static final String KEY_METHOD_NAME = "KEY_METHOD_NAME";
//        public static final String KEY_PROFILE = "KEY_PROFILE";
//        public static final String KEY_READ_ONLY = "KEY_READ_ONLY";
//        public static final String KEY_USER_LIST_TYPE = "KEY_USER_LIST_TYPE";
//        public static final String KEY_OPPONENT_ID = "KEY_OPPONENT_ID";
//        public static final String KEY_STUDENT_ID = "KEY_STUDENT_ID";
//        public static final String KEY_TUTOR_ID = "KEY_TUTOR_ID";
//        public static final String KEY_OTHER_USER_ID = "KEY_OTHER_USER_ID";
//        public static final String KEY_OTHER_USER_NAME = "KEY_OTHER_USER_NAME";
//        public static final String KEY_CHAT_ROOM = "KEY_CHAT_ROOM";
//        public static final String KEY_CHAT_ROOM_INFO = "KEY_CHAT_ROOM_INFO";
//        public static final String KEY_RECORDING_ALL = "KEY_RECORDING_ALL";
//        public static final String KEY_LIST_STATE = "KEY_LIST_STATE";
//        public static final String KEY_INPUT_STATE = "KEY_INPUT_STATE";
//        public static final String KEY_STUDY_INFO = "KEY_STUDY_INFO";
//        public static final String KEY_VOCA_FROM_ALL_VOCA_BOOK = "KEY_VOCA_FROM_ALL_VOCA_BOOK";
//        public static final String KEY_STUDY_VOCA_LIST = "KEY_STUDY_VOCA_LIST";
//        public static final String KEY_STUDY_VOCA_EXAM_LIST = "KEY_STUDY_VOCA_EXAM_LIST";
//        public static final String KEY_STUDY_ROLE_PLAYING_CONTENT = "KEY_STUDY_ROLE_PLAYING_CONTENT";
//        public static final String KEY_ASTERISK = "KEY_ASTERISK";
//        public static final String KEY_SHOW_SENTENCE = "KEY_SHOW_SENTENCE";
//        public static final String KEY_LESSON_ID = "KEY_LESSON_ID";
//        public static final String KEY_LESSON_TYPE = "KEY_LESSON_TYPE";
//        public static final String KEY_LESSON = "KEY_LESSON";
//        public static final String KEY_LESSON_OPTION = "KEY_LESSON_OPTION";
//        public static final String KEY_START_TIME = "KEY_START_TIME";
//        public static final String KEY_STUDIED_BOOKS = "KEY_STUDIED_BOOKS";
//        public static final String KEY_EVENT_DATA = "KEY_EVENT_DATA";
//        public static final String KEY_LESSON_LIST_ACTION = "KEY_LESSON_LIST_ACTION";
//        public static final String KEY_ITEM_CLICK_ACTION = "KEY_ITEM_CLICK_ACTION";
//        public static final String KEY_REFRESH_CLICK_ACTION = "KEY_REFRESH_CLICK_ACTION";
//        public static final String KEY_OPEN_FROM_LAUNCHER = "KEY_OPEN_FROM_LAUNCHER";
//        public static final String KEY_VOICE_DATA = "KEY_VOICE_DATA";
//        public static final String KEY_SCHEDULE_CLICK_ACTION = "KEY_SCHEDULE_CLICK_ACTION";
//        public static final String KEY_PARENT_BOOK_ID_LIST = "KEY_PARENT_BOOK_ID_LIST";
//        public static final String KEY_AUTO_MOVE = "KEY_AUTO_MOVE";
//        public static final String KEY_STUDY_LANG = "KEY_STUDY_LANG";
//        public static final String KEY_RUBY_TYPE = "KEY_RUBY_TYPE";
//        public static final String KEY_QUIZ_LIST = "KEY_QUIZ_LIST";
//        public static final String KEY_QUIZ_HANJA_LIST = "KEY_QUIZ_HANJA_LIST";
//        public static final String KEY_LESSON_MODE = "KEY_LESSON_MODE";
//        public static final String KEY_GRAMMAR_ID = "KEY_GRAMMAR_ID";
//        public static final String KEY_LESSON_READING_ID = "KEY_LESSON_READING_ID";
//        public static final String KEY_IS_OPEN = "KEY_IS_OPEN";
//        public static final String KEY_SELECT_MULTI = "KEY_SELECT_MULTI";
//        public static final String KEY_VOCA_BOOKS = "KEY_VOCA_BOOKS";
//        public static final String KEY_VOCA_READING = "KEY_VOCA_READING";
//        public static final String KEY_STUDY_ORDER_POS = "KEY_STUDY_ORDER_POS";
//        public static final String KEY_STUDY_ORDER_ROLE_PLAYING_POS = "KEY_STUDY_ORDER_ROLE_PLAYING_POS";
//        public static final String KEY_SHOW_STUDENT_MEANING = "KEY_SHOW_STUDENT_MEANING";
//        public static final String KEY_URL = "KEY_URL";
//        public static final String KEY_DIC_HANJA = "KEY_DIC_HANJA";
//        public static final String KEY_ID = "KEY_ID";
//        public static final String KEY_BOOK_NAME = "KEY_BOOK_NAME";
//        public static final String KEY_HANJA_DATA = "KEY_HANJA_DATA";
//        public static final String KEY_VOCA_TYPE_ID = "KEY_VOCA_TYPE_ID";
//        public static final String KEY_SLIDE_RIGHT_TO_LEFT_IN_ANIMATION = "KEY_SLIDE_RIGHT_TO_LEFT_IN_ANIMATION";
//        public static final String KEY_HANJA_GROUP_VIEW_TYPE = "KEY_HANJA_GROUP_VIEW_TYPE";
//        public static final String KEY_HANJA_GROUP_TYPE_MODEL = "KEY_HANJA_GROUP_TYPE_MODEL";
//        public static final String KEY_OPEN_FROM_APP = "KEY_OPEN_FROM_APP";

    }

    public static class MSG_WHAT {
        public static final int SAVE_STUDIED_WORKBOOK_ID = 1000;
    }

    public static class STUDENT {
        public static final String KEY_ID = "studentId";
    }

    public static class VOCA {
        public static final String KEY_VOCA_KNOW = "vocaKnow";
        public static final String KEY_VOCA = "voca";
        public static final String KEY_MEANING = "meaning";
    }

    public static class API_KEY {
        public static final String DEFAULT_UID = "1686";
        public static final String DEFAULT_STUDY_LANG = "ENGLISH";
        public static final String DEFAULT_LANG_DISPLAY = "KOREAN";
        public static final String KEY_AFTER_TIME = "afterTime";
        public static final String KEY_UID = "UID";
        public static final String KEY_OPPONENT_UID = "OPPONENT_UID";
        public static final String KEY_ACCESS_OR_EXIT_APP = "ACCESS_OR_EXIT_APP";
        public static final String KEY_CLIENT_TYPE = "CLIENT_TYPE";
        public static final String KEY_APP_NAME = "appName";
        public static final String KEY_EMAIL = "EMAIL";
        public static final String KEY_WORDBOOK_NAME = "WORDBOOK_NAME";
        public static final String KEY_WORDBOOK_ID = "VOCABOOKS_ID";
        public static final String KEY_WORDBOOK_TYPE = "VOCABOOK_TYPE";
        public static final String KEY_START_NO = "START_NO";
        public static final String KEY_COUNT_RECORD = "COUNT_RECORD";
        public static final String KEY_VOCA_TYPE = "VOCA_TYPE";
        public static final String KEY_VOCA_DISPLAY = "VOCA";
        public static final String KEY_SEARCH_STRING = "STR_TO_FIND";
        public static final String KEY_SEARCH_IN = "SEARCH_IN_WHERE";
        public static final String KEY_SEARCH_WITH_REGEX = "SEARCH_WITH_REGULAREXPRESSION";
        public static final String KEY_VOCA_ID = "VOCA_ID";
        //        public static final String KEY_AMKI_GRADE = "AMKI_GRADE";
//        public static final String KEY_KNOWPRONOUNCE = "KNOWPRONOUNCE";
        public static final String KEY_VOCA_KNOW = "VOCA_KNOW";
        public static final String KEY_VOCA_KNOWPRONOUNCE = "VOCA_KNOWPRONOUNCE";
        public static final String KEY_STUDENT_ID = "STUDENT_ID";
        public static final String KEY_TUTOR_ID = "TUTOR_ID";
        public static final String KEY_STUDENT_STUDY_VOCA_ID = "STUDENT_STUDY_VOCA_ID";
        public static final String KEY_EXTENSION = "EXTENSION";
        public static final String KEY_FILE_SIZE = "FILESIZE";
        public static final String KEY_NATIVE_SPEAKER_ID = "NATIVE_SPEAKER_ID";
        public static final String KEY_FILE_NAME = "FILE_NAME";
        public static final String KEY_DISP_ORDER = "DISP_ORDER";
        public static final String KEY_VOCABOOK_PARENT_ID = "VOCABOOK_PARENT_ID";
        public static final String KEY_VOCA = "VOCA";
        public static final String KEY_VOCAORI = "VOCAORI";
        public static final String KEY_VOCA_CH_S = "VOCA_CH_S";
        public static final String KEY_VOCA_JP = "VOCA_JP";

        public static final String KEY_HANJA_KOREA = "HANJA_KOREA";
        public static final String KEY_HANJA_JAPAN = "HANJA_JAPAN";
        public static final String KEY_HANJA_SIMPLIFIED = "HANJA_SIMPLIFIED";
        public static final String KEY_HANJA_TAIWAN = "HANJA_TAIWAN";
        public static final String KEY_HANJA_SHORT_FORM = "HANJA_SHORT_FORM";
        public static final String KEY_HANJA_VARIANT_1 = "HANJA_VARIANT_1";
        public static final String KEY_HANJA_VARIANT_2 = "HANJA_VARIANT_2";
        public static final String KEY_HANJA_SOKJA = "HANJA_SOKJA";

        public static final String KEY_DELETED = "DELETED";
        public static final String KEY_MEANING = "MEANING";
        public static final String KEY_MEANING_DETAILED = "MEANING_DETAILED";
        public static final String KEY_MEANING_ENG_DETAILED = "MEANING_ENG_DETAILED";
        public static final String KEY_PRONOUNCE = "PRONOUNCE";
        public static final String KEY_PRONOUNCE_CH_S = "PRONOUNCE_CH_S";
        public static final String KEY_PRONOUNCE_JP = "PRONOUNCE_JP";


        public static final String KEY_MEANING1 = "MEANING1";
        public static final String KEY_PRONOUNCE1 = "PRONOUNCE1";
        public static final String KEY_PRONOUNCE1_FIRST = "PRONOUNCE1_FIRST";
        public static final String KEY_MEANING2 = "MEANING2";
        public static final String KEY_PRONOUNCE2 = "PRONOUNCE2";
        public static final String KEY_PRONOUNCE2_FIRST = "PRONOUNCE2_FIRST";
        public static final String KEY_MEANING3 = "MEANING3";
        public static final String KEY_PRONOUNCE3 = "PRONOUNCE3";
        public static final String KEY_PRONOUNCE3_FIRST = "PRONOUNCE3_FIRST";
        public static final String KEY_LEFTCOMPONENT = "LEFTCOMPONENT";
        public static final String KEY_RIGHTCOMPONENT = "RIGHTCOMPONENT";
        public static final String KEY_STROKES = "STROKES";
        public static final String KEY_RADICAL = "RADICAL";
        public static final String KEY_PINYIN = "PINYIN";
        public static final String KEY_KUNYOMI = "KUNYOMI";
        public static final String KEY_ONYOMI = "ONYOMI";
        public static final String KEY_MEANING_ENG = "MEANING_ENG";

        public static final String KEY_FIREBASE_TOKEN = "FIREBASE_TOKEN";
        public static final String KEY_VOCABOOK_PRACTICE_ONLY = "VOCABOOK_PRACTICE_ONLY";
        public static final String KEY_VOCABOOK_ALPHABET_ONLY = "VOCABOOK_ALPHABET_ONLY";
        public static final String KEY_NAME = "NAME";
        public static final String KEY_NATION = "NATION";
        public static final String KEY_CITY = "CITY";
        public static final String KEY_SEX = "SEX";
        public static final String KEY_AGE = "AGE";
        public static final String KEY_BIO = "BIO";
        public static final String KEY_USER_LIST_BY_CATEGORY = "USER_LIST_BY_CATEGORY";
        public static final String KEY_FOLLOWING_ETC_TYPE = "FOLLOWING_ETC_TYPE";
        public static final String KEY_MANAGE_RECORD = "MANAGE_RECORD";
        public static final String KEY_OPPONENT_NAME_BY_ME = "OPPONENT_NAME_BY_ME";
        public static final String KEY_OPPONENT_DESC_BY_ME = "OPPONENT_DESC_BY_ME";
        public static final String KEY_ID = "ID";
        public static final String KEY_MAX_HOMEWORK = "MAX_HOMEWORK";
        public static final String KEY_MAX_QUIZ = "MAX_QUIZ";
        public static final String KEY_MAX_HANJA_QUIZ = "MAX_HANJA_QUIZ";
        public static final String KEY_ALLOW_CHAT = "ALLOW_CHAT";
        public static final String KEY_SHARE_MY_RECORDING = "SHARE_MY_RECORDING";
        public static final String KEY_CLASS_LIST_ID = "CLASS_LIST_ID";
        public static final String KEY_CHATROOM_TYPE = "CHATROOM_TYPE";
        public static final String KEY_CHATROOM_ID = "CHATROOM_ID";
        public static final String KEY_EVALUATE_VOCA_GRADE = "EVALUATE_VOCA_GRADE";
        public static final String KEY_FEEDBACK_MESSAGE = "FEEDBACK_MESSAGE";
        public static final String KEY_FILE_VERSION = "FILE_VERSION";
        public static final String KEY_SENDER_ID = "SENDER_ID";
        public static final String KEY_TBL_NAME = "TBL_NAME";
        public static final String KEY_SHOW_ASTERISK = "SHOW_ASTERISK";
        public static final String KEY_MESSAGE_ID = "MESSAGE_ID";
        public static final String KEY_MESSAGE = "MESSAGE";
        public static final String KEY_INDEX = "INDEX";
        public static final String KEY_LESSON_FOR_WHOM = "LESSON_FOR_WHOM";
        public static final String KEY_STUDY_ROLE = "STUDY_ROLE";
        public static final String KEY_OPPONENT_STUDY_ROLE = "OPPONENT_STUDY_ROLE";
        public static final String KEY_LESSON_ID = "LESSON_ID";
        public static final String KEY_START_TIME = "START_TIME";
        public static final String KEY_FINISH_TIME = "FINISH_TIME";
        public static final String KEY_LAST_VOCABOOKS_ID = "LAST_VOCABOOKS_ID";
        public static final String KEY_LAST_VOCABOOK_TYPE = "LAST_VOCABOOK_TYPE";
        public static final String KEY_CONFIRM = "CONFIRM";
        public static final String KEY_ENABLE_LESSON_PN = "ENABLE_LESSON_PN";
        public static final String KEY_PN_MINUTES_BEFORE_BEGIN_LESSON = "PN_MINUTES_BEFORE_BEGIN_LESSON";
        public static final String KEY_PN_MINUTES_BEFORE_FINISH_LESSON = "PN_MINUTES_BEFORE_FINISH_LESSON";
        public static final String KEY_LEAD_LESSON = "LEAD_LESSON";
        public static final String KEY_LANGUAGE_LEVEL = "LANGUAGE_LEVEL";
        public static final String KEY_FREE_TALKING = "FREE_TALKING";
        public static final String KEY_SMALL_TALKING = "SMALL_TALKING";
        public static final String KEY_SPEAKING_SPEED = "SPEAKING_SPEED";
        public static final String KEY_READING_SPEED = "READING_SPEED";
        public static final String KEY_CORRECT_PRONUNCIATION = "CORRECT_PRONUNCIATION";
        public static final String KEY_LESSON_REPEAT_TOPICS = "LESSON_REPEAT_TOPICS";
        public static final String KEY_LESSON_REPEAT_COUNT = "LESSON_REPEAT_COUNT";
        public static final String KEY_LESSON_REPEAT_MAX_PHRASE_COUNT = "LESSON_REPEAT_MAX_PHRASE_COUNT";
        public static final String KEY_TOPIC_BEGIN_INDEX = "TOPIC_BEGIN_INDEX";
        public static final String KEY_TOPIC_REPEATED_COUNT = "TOPIC_REPEATED_COUNT";
        public static final String KEY_VOCABOOKS_CELL_INDEX = "VOCABOOKS_CELL_INDEX";
        public static final String KEY_SHOW_TOAST = "SHOW_TOAST";
        public static final String KEY_SORT_TYPE = "SORT_TYPE";
        public static final String KEY_APP_TYPE = "APP_TYPE";
        public static final String KEY_VERSION = "VERSION";
        public static final String KEY_DEVICE_TOKEN = "DEVICE_TOKEN";
        public static final String KEY_OS_TYPE = "OS_TYPE";
        public static final String KEY_APPNAME = "APP_NAME";
        public static final String KEY_LOG_MESSAGE = "LOG_MESSAGE";
        public static final String KEY_CALL_UID = "UID";
        public static final String KEY_CALL_OPPONENT_UID = "OPPONENT_UID";
        public static final String KEY_CALL_ROOMNAME = "CALL_ROOMNAME";
        public static final String KEY_CALL_TYPE = "CALL_TYPE";
        public static final String KEY_EXAM_SOURCE = "EXAM_SOURCE";
        public static final String KEY_ANSWER_CORRECT = "ANSWER_CORRECT";
        public static final String KEY_SOLVING_TIME = "SOLVING_TIME";
        public static final String KEY_SELECTED_ANSWER_NUMBER = "SELECTED_ANSWER_NUMBER";
        public static final String KEY_APP_VERSION = "APP_VERSION";
        public static final String KEY_VOIP_TOKEN = "VOIP_TOKEN";
        public static final String KEY_REPLY_CALL_TYPE = "REPLY_CALL_TYPE";
        public static final String KEY_UUID = "UUID";
        public static final String KEY_DEVICE_TYPE = "DEVICE_TYPE";
        public static final String KEY_DEVICE_OS_VERSION = "DEVICE_OS_VERSION";
        public static final String KEY_ROLE_PLAYING_ID = "ROLE_PLAYING_ID";
        public static final String KEY_ROLE_PLAYING_CATEGORY_ID = "ROLE_PLAYING_CATEGORY_ID";
        public static final String KEY_USER_WORD_EXAM_TYPE = "USER_WORD_EXAM_TYPE";
        public static final String KEY_INPUT_TEXT = "INPUT_TEXT";
        public static final String KEY_ROLE_PLAYING_PARENT_ID = "ROLE_PLAYING_PARENT_ID";
        public static final String KEY_ROLE_PLAYING_TYPE = "ROLE_PLAYING_TYPE";
        public static final String KEY_TBL_SECTION = "TBL_SECTION";
        public static final String KEY_TBL_ROW = "TBL_ROW";
        public static final String KEY_TBL_TYPE_SYNC = "TBL_TYPE_SYNC";
        public static final String KEY_IS_CELL_CHECKED = "IS_CELL_CHECKED";
        public static final String KEY_COUNT_OF_ORDER_FROM_CHECKED_MENU = "COUNT_OF_ORDER_FROM_CHECKED_MENU";
        public static final String KEY_WORD = "VOCA";
        public static final String KEY_STR_POS = "strPOS";
        public static final String KEY_STR_WORD_ID = "strWordID";
        public static final String KEY_DISP_MEANING_LANG = "dispMeaningLang";
        public static final String KEY_PRONOUNCE_LOW = "pronounce";
        public static final String KEY_MEANING_LOW = "meaning";
        public static final String KEY_COOKIE = "Cookie";

        public static final String KEY_CONTENT_TYPE = "Content-Type";
        public static final String KEY_BRANCH_SELECTED = "BRANCH_SELECTED";
        public static final String KEY_LESSON_TYPE = "LESSON_TYPE";
        public static final String KEY_COUNT_OF_QUIZ = "COUNT_OF_QUIZ";
        public static final String KEY_QUIZ_STATUS_ID = "QUIZ_STATUS_ID";
        public static final String KEY_IS_FINISH = "IS_FINISH";
        public static final String KEY_LESSON_MODE = "LESSON_MODE";
        public static final String KEY_MAKE_RUBY_TEXT = "MAKE_RUBY_TEXT";
        public static final String KEY_MAKE_NEW_EXAM = "MAKE_NEW_EXAM";
        public static final String KEY_PN_TYPE = "PN_TYPE";
        public static final String KEY_RECORD_LESSON = "RECORD_LESSON";
        public static final String KEY_ID_IN_SERVER_VOCABOOK = "ID_IN_SERVER_VOCABOOK";
        public static final String KEY_TBL_PARENT_SECTION = "TBL_PARENT_SECTION";
        public static final String KEY_TBL_PARENT_ROW = "TBL_PARENT_ROW";
        public static final String KEY_COUNT_OF_PHRASES_TO_STUDY_AT_ONCE = "COUNT_OF_PHRASES_TO_STUDY_AT_ONCE";
        public static final String KEY_STUDY_ORDER_HIDE_PART_OF_WORDS = "STUDY_ORDER_HIDE_PART_OF_WORDS";
        public static final String KEY_STUDY_ORDER_HIDE_ALL_PHRASES = "STUDY_ORDER_HIDE_ALL_PHRASES";
        public static final String KEY_STUDY_ORDER_RANDOM_QUESTIONS = "STUDY_ORDER_RANDOM_QUESTIONS";
        public static final String KEY_GRAMMAR_ID = "GRAMMAR_ID";
        public static final String KEY_SEND_PN = "SEND_PN";
        public static final String KEY_USE_SERVER_VOCABOOK_DATA = "USE_SERVER_VOCABOOK_DATA";
        public static final String KEY_READING_ID = "READING_ID";
        public static final String KEY_LESSON_READING_ID = "LESSON_READING_ID";
        public static final String KEY_SHOW_MEANING_AT_STUDENT = "SHOW_MEANING_AT_STUDENT";
        public static final String KEY_APP_STATE_MODE = "APP_STATE_MODE";
        public static final String KEY_LANG_STUDY = "LANG_STUDY";
        public static final String KEY_LANG_MEANING = "LANG_MEANING";
        public static final String KEY_ENCODING = "encoding";
        public static final String KEY_WITH_FIXED_NAME = "WithFixedName";
        public static final String KEY_PARAM = "Key_Param";
        public static final String KEY_SYNC_SUBTITLE_AT_SERVER = "SYNC_SUBTITLE_AT_SERVER";
        public static final String KEY_LANG_STUDY_CODE = "LANG_STUDY_CODE";
        public static final String KEY_LANG_MEANING_CODE = "LANG_MEANING_CODE";
        public static final String KEY_SEARCH_HANJA_TYPE = "SEARCH_HANJA_TYPE";
        public static final String KEY_TRANSLATE_INPUT_TEXT = "TRANSLATE_INPUT_TEXT";
        public static final String KEY_STUDENT_LANG_MEANING_CODE = "STUDENT_LANG_MEANING_CODE";
        public static final String KEY_STR_WORDS = "strWords";
        public static final String KEY_PASSWORD = "PASSWORD";
        public static final String KEY_NEW_PASSWORD = "NEW_PASSWORD";
        public static final String KEY_ID_MAIL = "idMail";
        public static final String KEY_LANG_NATIVE = "langNative";
        public static final String KEY_VOCA_LEVEL = "VOCA_LEVEL";
        public static final String KEY_VOCALEVEL = "VOCALEVEL";
        public static final String KEY_COMMENT = "comment";
        public static final String KEY_IN_APP_TYPE = "inAppType";
        public static final String KEY_POINT = "POINT";
        public static final String KEY_CLIENTTYPE = "clientType";
        public static final String KEY_STR_VOCA_ID_LIST = "STR_VOCA_ID_LIST";
        public static final String KEY_STR_VOCA_TYPE_LIST = "STR_VOCA_TYPE_LIST";
        public static final String KEY_MULTIPLE_CHOICE_QUESTION_TYPE = "MULTIPLE_CHOICE_QUESTION_TYPE";
        public static final String KEY_UPDATE_RESULT = "UPDATE_RESULT";
        public static final String KEY_NEW_VOCA_ID_LIST = "NEW_VOCA_ID_LIST";
        public static final String KEY_UPDATE_RESULT_LIST = "UPDATE_RESULT_LIST";
        public static final String KEY_STR_FILE_NAME_LIST = "STR_FILE_NAME_LIST";
        public static final String KEY_POSALL = "POSALL";
        public static final String KEY_DIC_HANJA = "DIC_HANJA";
        public static final String KEY_ID_IN_VOCABOOK = "ID_IN_VOCABOOK";
        public static final String KEY_VOCABOOK_TYPE_CODE = "VOCABOOK_TYPE_CODE";


        public static final String KEY_TERM_ENG_ABBR = "TERM_ENG_ABBR";
        public static final String KEY_TERM_ENG_FULL = "TERM_ENG_FULL";
        public static final String KEY_TERM_GROUP_ETC = "GROUP_ETC";
        public static final String KEY_TERM_GROUP_GIS = "GROUP_GIS";
        public static final String KEY_TERM_GROUP_HW = "GROUP_HW";
        public static final String KEY_TERM_GROUP_ICT = "GROUP_ICT";
        public static final String KEY_TERM_GROUP_NATION = "GROUP_NATION";
        public static final String KEY_TERM_KO_TITLE = "TERM_KO_TITLE";
        public static final String KEY_TERM_HANJA_TITLE = "TERM_HANJA_TITLE";
        public static final String KEY_TERM_KO_SHORT = "TERM_KO_SHORT";
        public static final String KEY_TERM_KO_FULL = "TERM_KO_FULL";
        public static final String KEY_TERM_LEVEL = "TERM_LEVEL";
        public static final String KEY_URL = "URL";
        public static final String KEY_MEMO = "MEMO";
        public static final String KEY_ORIGINAL_ID = "ORIGINAL_ID";

        public static final String KEY_Api_Key = "Api-Key";
        public static final String KEY_Authorization = "Authorization";
        public static final String KEY_file_id = "file_id";
        public static final String KEY_languages = "languages";
        public static final String KEY_query = "query";
//        public static final String KEY_PRONOUNCE1_FIRST = "PRONOUNCE1_FIRST";
//        public static final String KEY_PRONOUNCE1 = "PRONOUNCE1";
//        public static final String KEY_MEANING1 = "MEANING1";
//        public static final String KEY_MEANING1_PRONOUNCE1_FIRST = "MEANING1_PRONOUNCE1_FIRST";
//        public static final String KEY_MEANING1_PRONOUNCE1 = "MEANING1_PRONOUNCE1";
    }

    public static class REALMDB {
        public static final String KEY_BOOKMARK = "BOOKMARK";
        public static final String KEY_MEANING_KO = "MEANING_KO";
        public static final String KEY_MEANING1 = "MEANING1";
        public static final String KEY_MEANING1_PRONOUNCE1_FIRST = "MEANING1_PRONOUNCE1_FIRST";
        public static final String KEY_MEANING1_PRONOUNCE1 = "MEANING1_PRONOUNCE1";
        public static final String KEY_PRONOUNCE = "PRONOUNCE";
        public static final String KEY_PRONOUNCE1_FIRST = "PRONOUNCE1_FIRST";
        public static final String KEY_PRONOUNCE1 = "PRONOUNCE1";
        public static final String KEY_PRONOUNCE2_FIRST = "PRONOUNCE2_FIRST";
        public static final String KEY_PRONOUNCE2 = "PRONOUNCE2";
        public static final String KEY_PRONOUNCE3_FIRST = "PRONOUNCE3_FIRST";
        public static final String KEY_PRONOUNCE3 = "PRONOUNCE3";
        public static final String KEY_VOCA = "VOCA";
        public static final String KEY_VOCA_LEVEL = "VOCA_LEVEL";
        public static final String KEY_VOCAORI = "VOCAORI";
        public static final String KEY_GROUP_ID = "GROUP_ID";
        public static final String KEY_DISP_ORDER = "DISP_ORDER";
        public static final String KEY_PARENT_ID = "PARENT_ID";
        public static final String KEY_USED = "USED";
        public static final String KEY_RADICAL = "RADICAL";
        public static final String KEY_STROKES = "STROKES";
        public static final String KEY_VOCABOOKS_ID = "VOCABOOKS_ID";
        public static final String KEY_ID = "ID";
        public static final String KEY_NAME = "NAME";
        public static final String KEY_NAME_KO = "NAME_KO";
        public static final String KEY_name = "name";
        public static final String KEY_VOCA_ID = "VOCA_ID";
        public static final String KEY_VOCA_TYPE = "VOCA_TYPE";
        public static final String KEY_VOCA_KNOW = "VOCA_KNOW";
        public static final String KEY_VOCA_KNOWPRONOUNCE = "VOCA_KNOWPRONOUNCE";
        public static final String KEY_LEVEL_KOREA_TYPE_1 = "LEVEL_KOREA_TYPE_1";
        public static final String KEY_LEVEL_KOREA_TYPE_2 = "LEVEL_KOREA_TYPE_2";
        public static final String KEY_LEVEL_KOREA_TYPE_3 = "LEVEL_KOREA_TYPE_3";
        public static final String KEY_HANJA_KOREA = "HANJA_KOREA";
        public static final String KEY_HANJA_JAPAN = "HANJA_JAPAN";
        public static final String KEY_HANJA_TAIWAN = "HANJA_TAIWAN";
        public static final String KEY_HANJA_SIMPLIFIED = "HANJA_SIMPLIFIED";
        public static final String KEY_HANJA_SHORT_FORM = "HANJA_SHORT_FORM";
        public static final String KEY_HANJA_VARIANT_1 = "HANJA_VARIANT_1";
        public static final String KEY_HANJA_VARIANT_2 = "HANJA_VARIANT_2";
        public static final String KEY_HANJA_SOKJA = "HANJA_SOKJA";
        public static final String KEY_LEFTCOMPONENT = "LEFTCOMPONENT";
        public static final String KEY_RIGHTCOMPONENT = "RIGHTCOMPONENT";
        public static final String KEY_COMMON_USE_KOREA = "COMMON_USE_KOREA";
        public static final String KEY_COMMON_USE_JAPAN = "COMMON_USE_JAPAN";
        public static final String KEY_LEVEL_HSK = "LEVEL_HSK";

        public static final String KEY_UNICODE_DEC = "UNICODE_DEC";
        public static final String KEY_IMAGE_ID = "IMAGE_ID";

    }

    public static class API_VALUE {
        public static final int VALUE_USER_BOOK = 1;
        public static final int VALUE_SERVER_BOOK = 2;
        public static final String VALUE_SEARCH_IN_VOCA = "SEARCH_IN_WHERE_VOCA";
        public static final String VALUE_SEARCH_IN_MEANING = "SEARCH_IN_WHERE_MEANING";
        public static final int VALUE_VOCA_TYPE_NONE = -1;
        public static final int VALUE_VOCA_TYPE_WORD = 1;
        public static final int VALUE_VOCA_TYPE_SENTENCE = 2;
        public static final int VALUE_VOCA_TYPE_SUBTITLE = 3;
        public static final int VALUE_VOCA_TYPE_SUBTITLE_UNTUNED = 4;
        public static final int VALUE_VOCA_TYPE_BOOK = 5;
        public static final int VOCA_TYPE_USER_VOCABOOK_LOCAL = 10; //앱내에서만 유저 개인별로 사용. 서버로 전송은 안함.
        public static final int VALUE_VOCA_TYPE_KOICA = 80;
        public static final int VOCABOOK_PRACTICE_ONLY_NO = 0;
        public static final int VOCABOOK_PRACTICE_ONLY_YES = 1;
        public static final int VOCABOOK_ALPHABET_ONLY_NO = 0;
        public static final int VOCABOOK_ALPHABET_ONLY_YES = 1;

        public static final Integer VOCABOOK_TYPE_CODE_NONE = 0; //
        public static final Integer VOCABOOK_TYPE_CODE_USER = 1; // DB담을 값
        public static final Integer VOCABOOK_TYPE_CODE_SERVER = 2;
//        public static final String VOCABOOK_TYPE_SERVER = "VOCABOOK_TYPE_SERVER"; //
//        public static final String VOCABOOK_TYPE_USER = "VOCABOOK_TYPE_USER";

        public static final String FOLLOWING_ETC_TYPE_BLOCK = "FOLLOWING_ETC_TYPE_BLOCK";
        public static final String FOLLOWING_ETC_TYPE_FOLLOWING = "FOLLOWING_ETC_TYPE_FOLLOWING";
        public static final String FOLLOWING_ETC_TYPE_FOLLOWER = "FOLLOWING_ETC_TYPE_FOLLOWER";
        public static final String FOLLOWING_ETC_TYPE_FAVORITE = "FOLLOWING_ETC_TYPE_FAVORITE";
        public static final String FOLLOWING_ETC_TYPE_TUTOR_STUDENTS = "FOLLOWING_ETC_TYPE_TUTOR_STUDENTS";
        public static final int LIST_LESSON_FOR_STUDENT = 1;
        public static final int LIST_LESSON_FOR_TUTOR = 2;
        public static final int LIST_LESSON_FOR_ADMIN = 3;
        public static final int ENABLE_LESSON_PN_NO = 0;
        public static final int ENABLE_LESSON_PN_YES = 1;
        public static final int LEAD_LESSON_NO = 0;
        public static final int LEAD_LESSON_YES = 1;
        public static final int LANGUAGE_LEVEL_LOW_BEGINNER = 1;
        public static final int LANGUAGE_LEVEL_BEGINNER = 2;
        public static final int LANGUAGE_LEVEL_LOW_INTERMEDIATE = 3;
        public static final int LANGUAGE_LEVEL_INTERMEDIATE = 4;
        public static final int LANGUAGE_LEVEL_LOW_ADVANCED = 5;
        public static final int LANGUAGE_LEVEL_ADVANCED = 6;
        public static final int FREE_TALKING_NO = 0;
        public static final int FREE_TALKING_YES = 1;
        public static final int SMALL_TALKING_NO = 0;
        public static final int SMALL_TALKING_YES = 1;
        public static final int SPEAKING_SPEED_SLOW = 1;
        public static final int SPEAKING_SPEED_NORMAL = 2;
        public static final int SPEAKING_SPEED_FAST = 3;
        public static final int READING_SPEED_SLOW = 1;
        public static final int READING_SPEED_NORMAL = 2;
        public static final int READING_SPEED_FAST = 3;
        public static final int CORRECT_PRONUNCIATION_NEVER = 0;
        public static final int CORRECT_PRONUNCIATION_SOMETIMES = 1;
        public static final int CORRECT_PRONUNCIATION_FREQUENTLY = 2;
        public static final int USER_SORT_TYPE_NAME = 1;
        public static final int USER_SORT_TYPE_LAST_ACCESS_DATE = 2;
        public static final int APP_TYPE_APP = 1;
        public static final int APP_TYPE_WIDGET = 2;
        public static final int TBL_TYPE_SYNC_TOPIC = 1;
        public static final int TBL_TYPE_SYNC_EXAMPLE_SENTENCE = 2;
        public static final int TBL_TYPE_SYNC_CONTENT_ID_1 = 3;
        public static final int TBL_TYPE_SYNC_CONVERSATION_LIST = 4;
        public static final int TBL_TYPE_SYNC_RUBY_VOCA_LIST = 5;
        public static final int TBL_TYPE_SYNC_RUBY_VOCA_LIST_CONTENT = 6;
        public static final int TBL_TYPE_SYNC_RUBY_VOCA_LIST_CONVERSATION = 7;
        public static final int TBL_TYPE_SYNC_RUBY_VOCA_LIST_ROLE_PLAYING_ALL = 8;
        public static final int TBL_TYPE_SYNC_VOCA_LIST_BY_CATEGORY = 9;
        public static final int TBL_TYPE_SYNC_ALL_SENTENCE_LIST = 10;
        public static final int TBL_TYPE_SYNC_TOPIC_REVIEW_MODE = 11;
        public static final int TBL_TYPE_SYNC_RL_ALL_VOCAS_IN_MAIN_VIEW = 12;
        public static final int TBL_TYPE_SYNC_RL_ALL_SENTENCES_IN_MAIN_VIEW = 13;
        public static final int TBL_TYPE_SYNC_MSG_VOCA_LIST = 14;
        public static final int TBL_TYPE_SYNC_READING_VOCA_LIST = 15;
        public static final int IS_CELL_CHECKED_NO = 0;
        public static final int IS_CELL_CHECKED_YES = 1;
        public static final int LESSON_TYPE_PHONE = 1;
        public static final int LESSON_TYPE_TEXT_CHAT = 2;
        public static final int LESSON_MODE_NORMAL = 1;
        public static final int LESSON_MODE_EXAM = 2;
        public static final int LESSON_MODE_ROLE_PLAYING = 3;
        public static final int LESSON_MODE_REVIEW = 4;
        public static final int LESSON_MODE_GRAMMAR = 5;
        public static final int LESSON_MODE_READING = 6;
        public static final int PN_TYPE_NORMAL = 0;
        public static final int PN_TYPE_RUBY_TEXT = 1;
        public static final int PN_TYPE_RUBY_TEXT_CONTENT = 2;
        public static final int PN_TYPE_RUBY_TEXT_CONVERSATION = 3;
        public static final int PN_TYPE_RUBY_TEXT_ALL = 4;
        public static final int PN_TYPE_STUDY_WORDS = 5;
        public static final int PN_TYPE_STUDY_SENTENCES = 6;
        public static final int PN_TYPE_RL_ALL_VOCAS = 7;
        public static final int PN_TYPE_RL_ALL_SENTENCES = 8;
        public static final int PN_TYPE_MSG_VOCA_LIST = 9;
        public static final int PN_TYPE_READING_VOCA_LIST = 10;
        public static final int RECORD_LESSON_NO = 0;
        public static final int RECORD_LESSON_YES = 1;
        public static final int APP_STATE_MODE_BACKGROUND = 0;
        public static final int APP_STATE_MODE_FOREGROUND = 1;
        public static final int IS_NO = 0;
        public static final int IS_YES = 1;
    }

    public static class NAVIGATION {
        public static final int LOG_IN = 0;
        public static final int LOG_OUT = LOG_IN + 1;
        public static final int DELETE_ACCOUNT = LOG_OUT + 1;
        public static final int SETTINGS = DELETE_ACCOUNT + 1;
        public static final int NAVER_CAFE = SETTINGS + 1;
        public static final int NAVER_CAFE_ARAHANGUL_WRITING_PRACTICE_PAPER_DOWNLOAD = NAVER_CAFE + 1;
        public static final int KAKAOTALK_GROUPCHAT = NAVER_CAFE_ARAHANGUL_WRITING_PRACTICE_PAPER_DOWNLOAD + 1;
        public static final int MAIL = KAKAOTALK_GROUPCHAT + 1;
        public static final int APP_DOWNLOAD = MAIL + 1;
        public static final int SHARE_APP = APP_DOWNLOAD + 1;
        public static final int RATE_APP = SHARE_APP + 1;
        public static final int IN_APP_PURCHASE = RATE_APP + 1;
        public static final int REMOVE_BANNER_ADS = IN_APP_PURCHASE + 1;
        public static final int RESTORE_BANNER_ADS = REMOVE_BANNER_ADS + 1;
        public static final int HELP = RESTORE_BANNER_ADS + 1;
        public static final int SERVER = HELP + 1;
        public static final int BACKUP = SERVER + 1;
    }

    public static class FOLDER_APP {
        public static final String TEMP = "temp";
    }

    public static class FOLDER_ARAPLAYER {
        public static final String TMDB = "tmdb";
    }

    public static class FOLDER_IN_APP {
        public static final String ROOT = "araone";
//        public static final String CHANNELS_PATH = ROOT + "/" + CHANNELS;
//        public static final String IMAGE_PATH = ROOT + "/" + IMAGE;
//        public static final String SOUND_PATH = ROOT + "/" + SOUND;
//        public static final String TEMP_PATH = ROOT + "/" + TEMP;
//        //        public static final String TMDB_PATH = ROOT + "/" + TMDB;
//        public static final String VIDEO_PATH = ROOT + "/" + VIDEO;
        public static final String VOICE_PATH = ROOT + "/" + "voice";
    }

    public static class FOLDER_ARAONE {
        public static final String DOWNLOAD = "Download";
        public static final String DOCUMENTS = "Documents";
        public static final String ROOT = "Download/araone";
//        public static final String APP_ROOT = DOCUMENTS + File.separator + BuildConfig.APP_NAME;
        public static final String CHANNELS = "channels";
        public static final String IMAGE = "image";
        public static final String SOUND = "sound";
        public static final String TEMP = "temp";
//        public static final String TMDB = "tmdb";
        public static final String VIDEO = "video";
        public static final String VOICE = "voice";

        public static final String CHANNELS_PATH = ROOT + "/" + CHANNELS;
        public static final String IMAGE_PATH = ROOT + "/" + IMAGE;
        public static final String SOUND_PATH = ROOT + "/" + SOUND;
        public static final String TEMP_PATH = ROOT + "/" + TEMP;
//        public static final String TMDB_PATH = ROOT + "/" + TMDB;
        public static final String VIDEO_PATH = ROOT + "/" + VIDEO;
        public static final String VOICE_PATH = ROOT + "/" + VOICE;

//        public static final String APP_ROOT_VOICE_PATH = ROOT + "/" + VOICE;
    }

    public static class FILE {
//        public static final String FOLDER_ARAONE = "Documents/araone";
        public static final String FOLDER_SOUND = "sound";
        public static final String FOLDER_POWERPOINT = "powerpoint";
        public static final String FOLDER_VOICE = "voice";
        public static final String FOLDER_PRACTICE_SPEAKING = "voice/practice";
        public static final String FOLDER_BACKUP = "backup";
        public static final String FOLDER_BACKUP_SPEAKING = "voice/backup";
        public static final String EXTENTION_SPEAKING = "m4a";
        public static final String SUFFIX_TEMP = "_temp";
        public static final String YOUTUBE_PPTM_ASSET_PATH = "pptm";
        //        public static final String YOUTUBE_PPTM_FILE_NAME = "YoutubeSlide.pptm";
        public static final String SILENT_SHORT_FILE_NAME = "silent_0.5second.m4a";
        public static final String SILENT_LONG_FILE_NAME = "silent_1second.m4a";
        public static final String SENTENCES_FILE_NAME = "sentences.txt";
        public static final String END_LIST_FILE_NAME = "end_list_sound.mp3";
        public static final String END_MUSIC_FILE_NAME = "end_list_sound.mp3";
        public static final String BEEP_FILE_NAME = "beep_sound.mp3";
        public static final String FOLDER_CHAT = "channels";
        public static final String FOLDER_IMAGE = "image";
        public static final String EXTENTION_IMAGE = "jpeg";
        public static final String SUFFIX_THUMBNAIL = "_thumbnail";
        public static final String CAPTURED_IMAGE = "temp.jpeg";
        public static final String FOLDER_INTRODUCTION = "voice/introduction";
        public static final String PREFIX_INTRODUCTION = "introduction_";
        public static final String FOLDER_TEMP = "temp";
        public static final String FOLDER_NETWORK = "Network";
        public static final String FOLDER_LOCAL = "Local";
    }

    public static class UCROP {
        public static final int CROP_MAX_WIDTH = 1024;
        public static final int CROP_MAX_HEIGHT = 1024;
        public static final int CROP_COMPRESSION_QUALITY = 100;
        public static final boolean CROP_FREE_STYLE = true;
        public static final int THUMBNAIL_MAX_WIDTH = 320;
        public static final int THUMBNAIL_COMPRESSION_QUALITY = 100;
    }

    public static class FONT {
        public static final String KANJI_STROKE_ORDERS = "KanjiStrokeOrders_v4.002.ttf";
    }

    public static class AMKI_GRADE {
        //        public static final int VALUE_KNOWN = 0;
//        public static final int VALUE_1 = 1;
//        public static final int VALUE_2 = 2;
//        public static final int VALUE_UNKNOWN = 99;
        public static final int VALUE_BLINK = -1;
        public static final int VALUE_ASTERISK = -2;
        public static final int VALUE_BACK_TO_HOME = -3;
        public static final int VALUE_QUESTION = -99;
        public static final int VALUE_NONE = -999;
        public static final String DISPLAY_KNOWN = "!";
        public static final String DISPLAY_A = "1";
        public static final String DISPLAY_B = "2";
        public static final String DISPLAY_UNKNOWN = "X";
        public static final String DISPLAY_NOT_RATED = "?";
    }

    public static class KNOW {
//        public static final int VALUE_0 = 0;
//        public static final int VALUE_1 = 1;
//        public static final int VALUE_2 = 2;
//        public static final int VALUE_3 = 3;

//        public static final int NOTRATED = 0;
//        public static final int UNKNOWN = 1;
//        public static final int NOTSURE = 2;
//        public static final int KNOWN = 3;
//        public static final int EXCLUDE = 99;
    }

    public static class VOCA_KNOW {
        public static final int VOCA_KNOW_NULL = -1;
        public static final int VOCA_KNOW_NOTRATED = 0;
        public static final int VOCA_KNOW_AMKI_GRADE_1 = 10;
        public static final int VOCA_KNOW_AMKI_GRADE_2 = 20;
        public static final int VOCA_KNOW_UNKNOWN = 90;
        public static final int VOCA_KNOW_KNOWN = 100;
        public static final int VOCA_KNOW_EXCLUDE = 110;
        public static final int VOCA_KNOW_NOTINDIC = 120;
    }

    public static class NOTIFICATION_KEY {
        public static final String METHOD_NAME = "METHOD_NAME";
        public static final String CHATROOM_ID = "CHATROOM_ID";
        public static final String VOCA_ID = "VOCA_ID";
        public static final String VOCA_TYPE = "VOCA_TYPE";
        //        public static final String AMKI_GRADE = "AMKI_GRADE";
//        public static final String KNOWPRONOUNCE = "KNOWPRONOUNCE";
        public static final String VOCA_KNOW = "VOCA_KNOW";
        public static final String VOCA_KNOWPRONOUNCE = "VOCA_KNOWPRONOUNCE";
        public static final String EVALUATE_VOCA_GRADE = "EVALUATE_VOCA_GRADE";
        public static final String FEEDBACK_MESSAGE = "FEEDBACK_MESSAGE";
        public static final String SHOW_ASTERISK = "SHOW_ASTERISK";
        public static final String CALLER_UID = "CALLER_UID";
        public static final String CALLER_NUMBER = "CALLER_NUMBER";
        public static final String CALLER_USERNAME = "CALLER_USERNAME";
        public static final String CALL_ROOMNAME = "CALL_ROOMNAME";
        public static final String CALLER_SCREEN = "CALLER_SCREEN";
        public static final String LESSON_ID = "LESSON_ID";
        public static final String OPPONENT_STUDY_ROLE = "OPPONENT_STUDY_ROLE";
        public static final String AGE = "AGE";
        public static final String SEX = "SEX";
        public static final String MESSAGE_ID = "MESSAGE_ID";
        public static final String MESSAGE = "MESSAGE";
        public static final String MESSAGE_MODIFIED = "MESSAGE_MODIFIED";
        public static final String VOCABOOKS_ID = "VOCABOOKS_ID";
        public static final String VOCABOOK_TYPE = "VOCABOOK_TYPE";
        public static final String LAST_VOCABOOKS_ID = "LAST_VOCABOOKS_ID";
        public static final String LAST_VOCABOOK_TYPE = "LAST_VOCABOOK_TYPE";
        public static final String START_TIME = "START_TIME";
        public static final String FINISH_TIME = "FINISH_TIME";
        public static final String CONFIRM = "CONFIRM";
        public static final String SHOW_TOAST = "SHOW_TOAST";
        public static final String VOCABOOKS_CELL_INDEX = "VOCABOOKS_CELL_INDEX";
        public static final String TOPIC_BEGIN_INDEX = "TOPIC_BEGIN_INDEX";
        public static final String TOPIC_REPEATED_COUNT = "TOPIC_REPEATED_COUNT";
        public static final String SELECTED_ANSWER_NUMBER = "SELECTED_ANSWER_NUMBER";
        public static final String REPLY_CALL_TYPE = "REPLY_CALL_TYPE";
        public static final String ROLE_PLAYING_ID = "ROLE_PLAYING_ID";
        public static final String STUDENT_ID = "STUDENT_ID";
        public static final String TBL_PARENT_SECTION = "TBL_PARENT_SECTION";
        public static final String TBL_PARENT_ROW = "TBL_PARENT_ROW";
        public static final String TBL_SECTION = "TBL_SECTION";
        public static final String TBL_ROW = "TBL_ROW";
        public static final String TBL_TYPE_SYNC = "TBL_TYPE_SYNC";
        public static final String IS_CELL_CHECKED = "IS_CELL_CHECKED";
        public static final String COUNT_OF_ORDER_FROM_CHECKED_MENU = "COUNT_OF_ORDER_FROM_CHECKED_MENU";
        public static final String BRANCH_SELECTED = "BRANCH_SELECTED";
        public static final String NOTIFICATION_BODY = "gcm.notification.body";
        public static final String PN_TYPE = "PN_TYPE";
        public static final String OPPONENT_UID = "OPPONENT_UID";
        public static final String LESSON_MODE = "LESSON_MODE";
        public static final String VOCA_IDs = "VOCA_IDs";
        public static final String GRAMMAR_ID = "GRAMMAR_ID";
        public static final String LESSON_READING_ID = "LESSON_READING_ID";
        public static final String CALL_TYPE = "CALL_TYPE";
        public static final String SHOW_MEANING_AT_STUDENT = "SHOW_MEANING_AT_STUDENT";
    }

    public static class NOTIFICATION_VALUE {
        public static final String DO_YOU_KNOW_THIS_VOCA = "DoYouKnowThisVoca.ajax";
        public static final String SEND_VOCA_KNOW_TO_OTHERS_IN_STUDY_MODE = "SendVocaKnowToOthersInStudyMode.ajax";
        public static final String SEND_PRONOUNCE_FEEDBACK_TO_OTHERS_IN_STUDY_MODE = "SendPronounceFeedbackToOthersInStudyMode.ajax";
        public static final String SEND_VOCA_BOOK_IN_STUDY_MODE = "SendVocabookInStudyMode.ajax";
        public static final String SHOW_ASTERISK_IN_STUDY_MODE = "ShowAsteriskInStudyMode.ajax";
        public static final String JOIN_STUDY_MODE_IN_CHAT_ROOM = "JoinStudyModeInChatroom.ajax";
        public static final String EXIT_STUDY_MODE_IN_CHAT_ROOM = "ExitStudyModeInTheChatroom.ajax";
        public static final String SEND_MESSAGE_IN_STUDY_MODE = "SendMessageInStudyMode.ajax";
        public static final String SYNC_CELL_WITH_OTHER_USER_IN_STUDY_MODE = "SyncCellWithOtherUsersInStudyMode.ajax";
        public static final String SIDE_GLANCE_SENTENCE_IN_STUDY_MODE = "SideGlanceSentenceInStudyMode.ajax";
        public static final String SEND_PUSH_TO_STUDENT_FINISH_STUDY_IN_STUDY_MODE = "SendPushToStudentFinishStudyInStudyMode.ajax";
        public static final String CONFIRM_FINISH_STUDY_IN_STUDY_MODE = "ConfirmFinishStudyInStudyMode.ajax";
        public static final String SAVE_VOCABOOK_ID_IN_STUDY_MODE = "SaveVocabookIDInStudyMode.ajax";
        public static final String GET_EXAM_IN_STUDY_MODE = "GetAnExamInStudyMode.ajax";
        public static final String SEND_SELECT_ANSWER_AT_EXAM_IN_STUDY_MODE = "SendSelectAnswerAtExamInStudyMode.ajax";
        public static final String REQUEST_CALL_TO_OPPONENT = "requestCallToOpponent.ajax";
        public static final String REPLY_CALL_FROM_OPPONENT = "replyCallFromOpponent.ajax";
        public static final String MAKE_ROLE_PLAYING_CONTENTS_JSON = "MakeRolePlayingContentsJson.ajax";
        public static final String REPLY_CALL_FROM_OPPONENT_ADMIN = "replyCallFromOpponent.ajax.ForAdmin";
        public static final String REQUEST_CALL_TO_OPPONENT_ADMIN = "requestCallToOpponent.ajax.ForAdmin";
        public static final String SEND_CHAT_MESSAGE_IN_STUDY_MODE = "SendChatMessageInStudyMode.ajax";
        public static final String SEND_GRAMMAR_WORKBOOK_IN_STUDY_MODE = "SendGrammarWorkbookInStudyMode.ajax";
        public static final String GET_STUDY_USER_INFO_IN_CHAT_ROOM = "getStudyUserInfoInChatroom.ajax";
        public static final String MAKE_READING_CONTENTS_JSON_IN_STUDY_MODE = "MakeReadingContentsJsonInStudyMode.ajax";
        public static final String SHOW_MEANING_ON_STUDENT_VIEW_IN_STUDY_MODE = "showMeaningOnStudentViewInStudyMode.ajax";
    }

    public static class SEX {
        public static final int MALE = 1;
        public static final int FEMALE = 2;
    }

    public static class AGE {
        public static final int TEENAGER = 1;
        public static final int ADULT = 2;
    }

    public static class USER_LIST {
        public static final int ALL = 1;
        public static final int FOLLOWING = 2;
        public static final int FOLLOWER = 3;
        public static final int FAVORITE = 4;
        public static final int BLOCK = 5;
        public static final int STUDY_LANG = 6;
        public static final int FILTER_NAME = 7;
        public static final int UID_DESC = 8;
        public static final int LAST_ACCESS_DATE_DESC = 9;
    }

    public static final Locale[] LOCALES = {
            new Locale("", "AD"),
            new Locale("", "AE"),
            new Locale("", "AF"),
            new Locale("", "AG"),
            new Locale("", "AI"),
            new Locale("", "AL"),
            new Locale("", "AO"),
            new Locale("", "AR"),
            new Locale("", "AS"),
            new Locale("", "AT"),
            new Locale("", "AU"),
            new Locale("", "AW"),
            new Locale("", "AX"),
            new Locale("", "AZ"),
            new Locale("", "BA"),
            new Locale("", "BB"),
            new Locale("", "BD"),
            new Locale("", "BE"),
            new Locale("", "BF"),
            new Locale("", "BG"),
            new Locale("", "BH"),
            new Locale("", "BI"),
            new Locale("", "BJ"),
            new Locale("", "BL"),
            new Locale("", "BM"),
            new Locale("", "BN"),
            new Locale("", "BO"),
            new Locale("", "BQ"),
            new Locale("", "BR"),
            new Locale("", "BS"),
            new Locale("", "BT"),
            new Locale("", "BW"),
            new Locale("", "BY"),
            new Locale("", "BZ"),
            new Locale("", "CA"),
            new Locale("", "CC"),
            new Locale("", "CD"),
            new Locale("", "CF"),
            new Locale("", "CG"),
            new Locale("", "CH"),
            new Locale("", "CI"),
            new Locale("", "CK"),
            new Locale("", "CL"),
            new Locale("", "CM"),
            new Locale("", "CN"),
            new Locale("", "CO"),
            new Locale("", "CR"),
            new Locale("", "CU"),
            new Locale("", "CV"),
            new Locale("", "CW"),
            new Locale("", "CX"),
            new Locale("", "CY"),
            new Locale("", "CZ"),
            new Locale("", "DE"),
            new Locale("", "DG"),
            new Locale("", "DJ"),
            new Locale("", "DK"),
            new Locale("", "DM"),
            new Locale("", "DO"),
            new Locale("", "DZ"),
            new Locale("", "EA"),
            new Locale("", "EC"),
            new Locale("", "EE"),
            new Locale("", "EG"),
            new Locale("", "EH"),
            new Locale("", "ER"),
            new Locale("", "ES"),
            new Locale("", "ET"),
            new Locale("", "FI"),
            new Locale("", "FJ"),
            new Locale("", "FK"),
            new Locale("", "FM"),
            new Locale("", "FO"),
            new Locale("", "FR"),
            new Locale("", "GA"),
            new Locale("", "GB"),
            new Locale("", "GD"),
            new Locale("", "GE"),
            new Locale("", "GF"),
            new Locale("", "GG"),
            new Locale("", "GH"),
            new Locale("", "GI"),
            new Locale("", "GL"),
            new Locale("", "GM"),
            new Locale("", "GN"),
            new Locale("", "GP"),
            new Locale("", "GQ"),
            new Locale("", "GR"),
            new Locale("", "GT"),
            new Locale("", "GU"),
            new Locale("", "GW"),
            new Locale("", "GY"),
            new Locale("", "HK"),
            new Locale("", "HN"),
            new Locale("", "HR"),
            new Locale("", "HT"),
            new Locale("", "HU"),
            new Locale("", "IC"),
            new Locale("", "ID"),
            new Locale("", "IE"),
            new Locale("", "IL"),
            new Locale("", "IM"),
            new Locale("", "IN"),
            new Locale("", "IO"),
            new Locale("", "IQ"),
            new Locale("", "IR"),
            new Locale("", "IS"),
            new Locale("", "IT"),
            new Locale("", "JE"),
            new Locale("", "JM"),
            new Locale("", "JO"),
            new Locale("", "JP"),
            new Locale("", "KE"),
            new Locale("", "KG"),
            new Locale("", "KH"),
            new Locale("", "KI"),
            new Locale("", "KM"),
            new Locale("", "KN"),
            new Locale("", "KP"),
            new Locale("", "KR"),
            new Locale("", "KW"),
            new Locale("", "KY"),
            new Locale("", "KZ"),
            new Locale("", "LA"),
            new Locale("", "LB"),
            new Locale("", "LC"),
            new Locale("", "LI"),
            new Locale("", "LK"),
            new Locale("", "LR"),
            new Locale("", "LS"),
            new Locale("", "LT"),
            new Locale("", "LU"),
            new Locale("", "LV"),
            new Locale("", "LY"),
            new Locale("", "MA"),
            new Locale("", "MC"),
            new Locale("", "MD"),
            new Locale("", "ME"),
            new Locale("", "MF"),
            new Locale("", "MG"),
            new Locale("", "MH"),
            new Locale("", "MK"),
            new Locale("", "ML"),
            new Locale("", "MM"),
            new Locale("", "MN"),
            new Locale("", "MO"),
            new Locale("", "MP"),
            new Locale("", "MQ"),
            new Locale("", "MR"),
            new Locale("", "MS"),
            new Locale("", "MT"),
            new Locale("", "MU"),
            new Locale("", "MW"),
            new Locale("", "MX"),
            new Locale("", "MY"),
            new Locale("", "MZ"),
            new Locale("", "NA"),
            new Locale("", "NC"),
            new Locale("", "NE"),
            new Locale("", "NF"),
            new Locale("", "NG"),
            new Locale("", "NI"),
            new Locale("", "NL"),
            new Locale("", "NO"),
            new Locale("", "NP"),
            new Locale("", "NR"),
            new Locale("", "NU"),
            new Locale("", "NZ"),
            new Locale("", "OM"),
            new Locale("", "PA"),
            new Locale("", "PE"),
            new Locale("", "PF"),
            new Locale("", "PG"),
            new Locale("", "PH"),
            new Locale("", "PK"),
            new Locale("", "PL"),
            new Locale("", "PM"),
            new Locale("", "PN"),
            new Locale("", "PR"),
            new Locale("", "PS"),
            new Locale("", "PT"),
            new Locale("", "PW"),
            new Locale("", "PY"),
            new Locale("", "QA"),
            new Locale("", "RE"),
            new Locale("", "RO"),
            new Locale("", "RS"),
            new Locale("", "RU"),
            new Locale("", "RW"),
            new Locale("", "SA"),
            new Locale("", "SB"),
            new Locale("", "SC"),
            new Locale("", "SD"),
            new Locale("", "SE"),
            new Locale("", "SG"),
            new Locale("", "SH"),
            new Locale("", "SI"),
            new Locale("", "SJ"),
            new Locale("", "SK"),
            new Locale("", "SL"),
            new Locale("", "SM"),
            new Locale("", "SN"),
            new Locale("", "SO"),
            new Locale("", "SR"),
            new Locale("", "SS"),
            new Locale("", "ST"),
            new Locale("", "SV"),
            new Locale("", "SX"),
            new Locale("", "SY"),
            new Locale("", "SZ"),
            new Locale("", "TC"),
            new Locale("", "TD"),
            new Locale("", "TG"),
            new Locale("", "TH"),
            new Locale("", "TK"),
            new Locale("", "TL"),
            new Locale("", "TN"),
            new Locale("", "TO"),
            new Locale("", "TR"),
            new Locale("", "TT"),
            new Locale("", "TV"),
            new Locale("", "TW"),
            new Locale("", "TZ"),
            new Locale("", "UA"),
            new Locale("", "UG"),
            new Locale("", "UM"),
            new Locale("", "US"),
            new Locale("", "UY"),
            new Locale("", "UZ"),
            new Locale("", "VC"),
            new Locale("", "VE"),
            new Locale("", "VG"),
            new Locale("", "VI"),
            new Locale("", "VN"),
            new Locale("", "VU"),
            new Locale("", "WF"),
            new Locale("", "WS"),
            new Locale("", "XK"),
            new Locale("", "YE"),
            new Locale("", "YT"),
            new Locale("", "ZA"),
            new Locale("", "ZM"),
            new Locale("", "ZW")
    };

    public static class EVALUATE {
        public static final String GRADE_A = "A";
        public static final String GRADE_B = "B";
        public static final String GRADE_C = "C";
    }

    public static class PERSON_AB {
        public static final String A = "A";
        public static final String B = "B";
    }
    public static class TERM_LEVEL {
        public static final Integer DEFAULT = 999;
    }
    public static class ATTACHMENT {
        public static final int PHOTO = 0;
        public static final int CAMERA = 1;
        public static final int RECORDING = 2;
    }

    public static class INCOME_TEXT_MESSAGE_OPTIONS {
        public static final int COPY = 0;
        public static final int REPLY = 1;
    }

    public static class OUTCOME_TEXT_MESSAGE_OPTIONS {
        public static final int COPY = 0;
        public static final int REPLY = 1;
        public static final int EDIT = 2;
        public static final int DELETE = 3;
    }

    public static class INCOME_PHOTO_MESSAGE_OPTIONS {
        public static final int OPEN = 0;
        public static final int DOWNLOAD = 1;
    }

    public static class OUTCOME_PHOTO_MESSAGE_OPTIONS {
        public static final int DELETE = 0;
        public static final int OPEN = 1;
        public static final int DOWNLOAD = 2;
    }

    public static class INCOME_VOICE_MESSAGE_OPTIONS {
        public static final int DOWNLOAD = 0;
    }

    public static class OUTCOME_VOICE_MESSAGE_OPTIONS {
        public static final int DELETE = 0;
        public static final int DOWNLOAD = 1;
    }

    public static class REQUEST_CODE {
        public static final int SELECT_PHOTO = 2910;
        public static final int CROP_PHOTO = SELECT_PHOTO + 1;
        public static final int CAMERA = CROP_PHOTO + 1;
        public static final int RECORDING = CAMERA + 1;
        public static final int SELECT_BOOK = RECORDING + 1;
        public static final int SELECT_TUTOR = SELECT_BOOK + 1;
        public static final int SELECT_STUDENT = SELECT_TUTOR + 1;
        public static final int STUDY_OPTION = SELECT_STUDENT + 1;
    }

    public static class TABLE_NAME {
        public static final String KEY = "TBL_NAME";
        public static final String TBL_SERVER_VOCABOOKS = "SERVER_VOCABOOKS";
        public static final String TBL_MESSAGE = "TBL_MESSAGE";
        public static final String TBL_GRAMMAR = "GRAMMAR";
        public static final String TBL_READING = "READING";
    }

    public static class JITSI {
        public static final String SERVER_URL = "https://meet.jit.si/";
        public static final String ROOM = "dalnim_dalvoca_";

        public static class TYPE {
            public static final int VOICE = 1;
            public static final int VIDEO = 2;
            public static final int TEXT = 3;
        }

        public static class STATUS {
            public static final int NONE = 1;
            public static final int CALL = 2;
            public static final int END = 3;
            public static final int JOIN_CALL = 4;
        }

        public static class SCREEN_RECEIVE_DATA {
            public static final int NONE = 0;
            public static final int CHAT_DETAILS = 1;
            public static final int LESSON = 2;
            public static final int ADMIN = 3;
            public static final int MAIN = 4;
        }

        public static class REPLY_CALL_TYPE {
            public static final int DECLINE = 0;
            public static final int ACCEPT = 1;
            public static final int END = 2;

        }

        public static class CALl_TYPE {
            public static final int HIDE = -1;
            public static final int NONE = 0;
            public static final int CALLING = 1;
            public static final int CALLED = 2;
        }
    }

    public static class RUBY {
        public static final String TEXT_DEFAULT =
                "<span wordid=\"43944\" word=\"i\" wordori=\"i\" pronounce=\"ai\" pronounce-wordori=\"ai\" know=\"3\" knowPronounce=\"2\" wordlevel=\"1\" bookmark=\"true\" meaning=\"나, 나는\" pos-word=\"\" pos-wordbaseform=\"\"><ruby><rb>I</rb><rt>나, 나는</rt></ruby></span>" +
                        "<span wordid=\"2162\" word=\"am\" wordori=\"be\" pronounce=\"ǽm\" pronounce-wordori=\"bi:\" know=\"3\" knowPronounce=\"2\" wordlevel=\"2\" bookmark=\"false\" meaning=\"이다, 있다\" pos-word=\"\" pos-wordbaseform=\"\"><ruby><rb>am</rb><rt>이다, 있다</rt></ruby></span>" +
                        "<span wordid=\"2\" word=\"a\" wordori=\"a\" pronounce=\"ə\" pronounce-wordori=\"ə\" know=\"3\" knowPronounce=\"3\" wordlevel=\"1\" bookmark=\"false\" meaning=\"하나의\" pos-word=\"\" pos-wordbaseform=\"\"><ruby><rb>a</rb><rt>하나의</rt></ruby></span>" +
                        "<span wordid=\"86116\" word=\"student\" wordori=\"student\" pronounce=\"stjú:dənt\" pronounce-wordori=\"stjú:dənt\" know=\"3\" knowPronounce=\"2\" wordlevel=\"5\" bookmark=\"false\" meaning=\"학생\" pos-word=\"\" pos-wordbaseform=\"\"><ruby><rb>student</rb><rt>학생</rt></ruby></span>";

        public static final String CHARACTER_U2028 = "\u2028";
        public static final String BREAK_CHARACTER = "\n";
        public static final int DEFAULT_LINE_SPACE = 25;
        public static final int DEFAULT_LINE_SPACE_FOR_HANJA = 15;
        public static final int DEFAULT_LINE_SPACE_FOR_HIDE_RUBY = 50;
        public static class TYPE {
            public static final int TABLE = 0;
            public static final int TEXT = 1;
        }

        public static class KEY {
            public static final String SPAN = "span";
            public static final String VOCA_ID = "VOCA_ID";
            public static final String VOCA_TYPE = "VOCA_TYPE";
            public static final String VOCA_ORI_ID = "VOCA_ORI_ID";
            public static final String VOCA = "VOCA";
            public static final String VOCA_DISPLAY = "VOCA_DISPLAY";
            public static final String PRONOUNCE = "PRONOUNCE";
            public static final String VOCA_KNOW = "VOCA_KNOW";
            public static final String VOCA_KNOWPRONOUNCE = "VOCA_KNOWPRONOUNCE";
            public static final String AMKI_GRADE = "AMKI_GRADE";
            public static final String WORD_LEVEL = "WORD_LEVEL";
            public static final String BOOKMARK = "BOOKMARK";
            public static final String POS = "POS";
            public static final String MEANING = "MEANING";
            public static final String MEANING_TTS = "MEANING_TTS";
            public static final int BOOKMARK_SHOW = 1;
            public static final int BOOKMARK_HIDE = 0;
            public static final String BOOKMARK_STR_SHOW = BOOKMARK + "=" + BOOKMARK_SHOW;
            public static final String BOOKMARK_STR_HIDE = BOOKMARK + "=" + BOOKMARK_HIDE;
            public static final String KNOW_STR_KNOWN = VOCA_KNOW + "=" + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
            public static final String KNOW_STR_UNKNOWN = VOCA_KNOW + "=" + Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
            public static final String KNOW_PRONOUNCE_STR_KNOW = VOCA_KNOWPRONOUNCE + "=" + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
            public static final String KNOW_PRONOUNCE_STR_UNKNOWN = VOCA_KNOWPRONOUNCE + "=" + Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
            public static final String SEMICOLON = ";";
            public static final String BRACKET_LEFT = "[";
            public static final String BRACKET_RIGHT = "]";
            public static final String RB_TAG = "rb";
            public static final String RB_OPEN = "<rb>";
            public static final String RB_CLOSE = "</rb>";
            public static final String RT_TAG = "rt";
            public static final String RT_OPEN = "<rt>";
            public static final String RT_CLOSE = "</rt>";
            public static final String RUBY_OPEN = "<ruby>";
            public static final String RUBY_CLOSE = "</ruby>";
            public static final String SPAN_OPEN = "<span>";
            public static final String SPAN_CLOSE = "</span>";
            public static final String SPACE = " ";
            public static final String KEY_REPLACE = "#";
            public static final String RB_OPEN_CHECK = "<rb";
            public static final String RB_CLOSE_CHECK = "</rb";
            public static final String RT_OPEN_CHECK = "<rt";
            public static final String RT_CLOSE_CHECK = "</rt";
            public static final String RUBY_OPEN_CHECK = "<ruby";
            public static final String RUBY_CLOSE_CHECK = "</ruby";
            public static final String SPAN_OPEN_CHECK = "<span";
            public static final String BREAK_REGEX = "(<br ?\\/?>)";
            public static final String BREAK_BR_START = "<br>";
            public static final String BREAK_BR_END = "<br/>";
            public static final String BREAK_BR_END_2 = "<br />";
            public static final String CHARACTER_N = "\\u000A";
        }
    }

    public static class SEARCH {
        public static class TYPE {
            public static int MATCHED = 0;
            public static int START = 1;
            public static int END = 2;
        }

        public static class VALUE {
            public static int TITLE = 0;
            public static int MEANING = 1;
            public static int ALL = 2;
        }

        public static final String KEY_PERCENT = "%";
    }

    public static class INTENT {
        public static final String KEY_PATH = "KEY_PATH";
        public static final String KEY_DATA = "KEY_DATA";
        public static final String KEY_QUIZ_SOURCE = "KEY_QUIZ_SOURCE";
        public static final String KEY_MULTIPLE_CHOICE_QUESTION_TYPE = "KEY_MULTIPLE_CHOICE_QUESTION_TYPE";
        public static class QUIZ_SOURCE {
            public static final String LOCAL = "LOCAL";
            public static final String SERVER = "SERVER";
        }
    }

    public static class PLAYER {
        public static final String ZIP_NAME = "dalplayer_db_sub.zip";
        public static final String SAMPLE_FILE_VIDEO = "Sample.mp4";
        public static final String DEFAULT_SUBTITLE_RUBY_SQLITE = "DalPlayer_Ruby.sqlite";
        public static final String DATABASE_NAME_ENG = "araonesoft_eng.sqlite";
        public static final String ASSET_DATABASE_NAME_ENG_ZIP = "araonesoft_eng.sqlite.zip";
        public static final String ASSET_SAMPLE_FILE_MULTI_VIDEOS_ZIP = "MultiPlayer_Sample.zip";
//        public static final String SAMPLE_FILE_SUB = "Sample.srt";
        public static final String SAMPLE_VOICES_FOLDER = FILE.FOLDER_VOICE;
//        public static final String DEFAULT_SUBTITLE_RUBY_SQLITE = "subtitle_ruby.sqlite";
        public static final String FILE_EXT_PNG = ".png";

        public static final double THUMBNAIL_HEIGHT_RATIO_BY_WIDTH = (double)9/16;
        public static final int MAX_SMOOTH_SCROLL_POSITION = 3;
        public static final int VIDEO_POSITION_BONUS = 0;
        public static final float TTS_VOLUME_PERCENTAGE = 0.6f;
        public static final String SPECIAL_CHARACTERS = "[-_+.^:,]";
        public static final int DEFAULT_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NUMBER_DEFAULT = 3;
        public static final int DEFAULT_SUBTITLE_TO_HIDE_SHORTER_3_KNOWN_WORDS_NONE_NUMBER = 0;
        public static final int DEFAULT_SUBTITLE_TO_HIDE_LONGER_20_WORDS_NUMBER_DEFAULT = 20;


        public static class INTENT {
            public static final String KEY_VIDEO_FILE = "KEY_VIDEO_FILE";
            public static final String KEY_PATH = "KEY_PATH";
            public static final String KEY_TIME = "KEY_TIME";
//            public static final String KEY_IS_WORD_LIST = "KEY_IS_WORD_LIST";
            public static final String KEY_TYPE = "KEY_TYPE";
            public static final String KEY_DATA = "KEY_DATA";
            public static final String KEY_WORD = "KEY_WORD";
            public static final String KEY_URL = "KEY_URL";
            public static final String KEY_INDEX = "KEY_INDEX";
            public static final String KEY_VIDEO_TIME_TO_START = "KEY_VIDEO_TIME_TO_START";
            public static final String KEY_IS_VIDEO_FROM_NETWORK = "KEY_IS_VIDEO_FROM_NETWORK";
            public static final String KEY_SUBPATH_INDEX = "KEY_SUBPATH_INDEX";
            public static final int SUBPATH_INDEX_1 = 1;
            public static final int SUBPATH_INDEX_2 = 2;
            public static final String KEY_MUSIC_PLAYLIST = "KEY_MUSIC_PLAYLIST";
        }

        public static class RECOMMENDED_CODEC {
            public static final String VIDEO = "H264";
            public static final String AUDIO = "AAC";
        }
        public static class SUB_TITLE {

//            public static final int MIN_TIME_TO_KEEP_PLAY_BETWEEN_SUBTITLES = 1000;

            public static class DISPLAY_LANG {
                public static final int COUNT_OF_TYPE = 3; // count (both, study, meaning = 3)
                public static final int BOTH = 0;
                public static final int STUDY = BOTH + 1;
                public static final int MEAING = STUDY + 1;
            }
            public static class GROUP_TYPE {
//                public static final int NONE = -1; //Dalnim commented this out. Wonder where we use NONE in the AraPlaeyr.
//                public static final int ALL_SUBTITLES = NONE + 1;
                public static final int ALL_SUBTITLES = 0;
                public static final int ALL_SUBTITLES_EXCEPT_AUTO_HIDED = ALL_SUBTITLES + 1;
//                public static final int BOOKMARK = ALL_SUBTITLES_EXCEPT_HIDED + 1;
                public static final int DIFFICULTY_SUBTITLE = ALL_SUBTITLES_EXCEPT_AUTO_HIDED + 1;
                public static final int DIFFICULTY_WORD = DIFFICULTY_SUBTITLE + 1;
                public static final int SEARCH = DIFFICULTY_WORD + 1;
                public static final int RANGE = SEARCH + 1;
                public static final int MANUALLY_HIDED_SUBTITLES = RANGE + 1;
            }

            public static class TO_HIDE {
                public static final int CHECKED_HIDED = 0;
                public static final int ALL_CAPITAL = CHECKED_HIDED + 1;
                public static final int WITH_MUSIC_TEXT = ALL_CAPITAL + 1;
                public static final int ALL_KNOWN_WORD = ALL_CAPITAL + 1;
                public static final int HAS_URL = ALL_KNOWN_WORD + 1;
            }

            public static class DISPLAY {
                public static final int NONE = 0;
                public static final int SUBTITLE_LIST = NONE + 1;
                public static final int LISTEN_COMPREHENSION_1 = SUBTITLE_LIST + 1;
                public static final int LISTEN_COMPREHENSION_2 = LISTEN_COMPREHENSION_1 + 1;
            }

            public static class DIFFICULT {
//                public static final int NONE = 0;
                public static final int ALL_1ST_AMKI = 0;
                public static final int ALL_2ND_AMKI = ALL_1ST_AMKI + 1;
                public static final int ALL_DIFFICULT = ALL_2ND_AMKI + 1;
                public static final int ALL_DIFFICULT_WITHOUT = ALL_DIFFICULT + 1;
                public static final int ALL_DIFFICULT_PRONUNCIATION = ALL_DIFFICULT_WITHOUT + 1;
            }

            public static class REPEAT_COUNT {
                public static final int COUNT_OF_SMART_REPEAT = 3;
                public static final int SMART_FEWER = -3;
                public static final int SMART_FEW = -2;
                public static final int SMART_MANY = -1;
                public static final int ZERO = 0;
                public static final int DEAFULT_VALUE = 1;
                public static final int SMART_FEWER_PLUS = 3;
                public static final int SMART_FEW_PLUS = 5;
                public static final int SMART_MANY_PLUS = 10;

                public static final int JUST_PRACTICE_ONE_IS_ENOUGH = 1;
                public static final int DONT_KNOW_PRONOUNCE_SO_NEED_TO_PRACTICE_MORE = 2;

                public static final float MIN_TIME_KEEP_PLAY_BEFORE_SUBTITLE = -0.3f;
                public static final float MIN_TIME_KEEP_PLAY_AFTER_SUBTITLE = 0.3f;
                public static final float MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE = 1.0f;
                public static final float MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE = 1.5f;
                public static final float MIN_TIME_KEEP_PLAY_BEFORE_AB_REPEAT = -0.8f;
                public static final float MIN_TIME_KEEP_PLAY_AFTER_AB_REPEAT = 0f;
                public static final int MIN_TIME_VALUE = 1000;
                public static final int MIN_TIME_INDEX = 10;
                public static final int REPEAT_COUNT_ONE = 1;

                public static class TYPE {
                    public static final int BEFORE = 0;
                    public static final int AFTER = 1;
                    public static final int BETWEEN = 2;
                }

                public static final String KEY_PLAY_DONE = "KEY_PLAY_DONE";
                public static final int PREVIEW_REPEAT_TOTAL = 2;
                public static final float PREVIEW_REPEAT_TIME = 1.0f * TIMER.SECOND;
            }

            public static class LISTEN_COMPREHENSION_1 {
                public static class TYPE {
                    public static final int HIDE_THE_SUBTITLE = 0;
                    public static final int SHOW_THE_SUBTITLE = 1;
                    public static final int SHOW_DIFFICULT_WORDS_ONLY = 2;
                }

                public static final int PLAY_SUBTITLES_AT_ONCE_DEFAULT_INDEX = 2;
                public static final int PLAY_SUBTITLES_AT_ONCE_MIN = 1;
                public static final int PLAY_SUBTITLES_AT_ONCE_MAX = 50;
                public static final int PLAY_SUBTITLES_PLAY_PARTS_DEFAULT_INDEX = 21;
                public static final int PLAY_SUBTITLES_PLAY_PARTS_MIN = 1;
                public static final int PLAY_SUBTITLES_PLAY_PARTS_MAX = 999;
            }
            //여기서 자막을 숨긴다는 뜻은. 자막 리스트에서 빼버린다는 뜻이다.(자막을 지우는건 아님) 자막을 빈문자로 보여주는것이 아님.
            //자막 삭제 뷰에서는 HIDE_AUTO를 삭제할 자막으로 사용함. (로직 코드를 공통으로 쓰고 있음)
            public static class USED {
                public static final int SHOW = 1;
//                public static final int HIDE_MANUAL = SHOW - 1; //유저가 자막을 숨기고자 일부러 지정한것 //이건 빼버리자. 코드가 너무 복잡해진다.
                public static final int HIDE_AUTO = SHOW + 1; //특수문자 들어간 자막을 자동으로 숨기고자 하는것
            }

        }

        public static class DATABASE {
            public static final String SQLITE = ".sqlite";

            public static final class TABLE {
                public static final String FILE = "tb_file";
            }

            public static final class FIELD {
                public static final String ID = "id";
                public static final String PATH = "path";
                public static final String DURATION = "duration";
                public static final String INDEX = "index";
                public static final String SELECTED = "isSelected";
                public static final String LANGUAGE = "language";
                public static final String STATUS = "status";
                public static final String CREATE_DATE = "createDate";
                public static final String COUNT = "count";
                public static final String MOTHER_TONGUE = "motherTongue";
                public static final String NAME = "name";
                public static final String SEASON_NAME_VIDEO_FILE = "seasonNameVideoFile";
                public static final String SEASON_NUMBER = "seasonNumber";
                public static final String STUDY_LANGUAGE = "studyLanguage";
                public static final String SUB_PATH1 = "subPath1";
                public static final String SUB_PATH2 = "subPath2";
                public static final String SUB_PATH_ORIGINAL = "subPathOriginal";
                public static final String TRASH = "trash";
//                public static final String TMDB_SEASON_ID = "tmdbSeasonId";
                public static final String HIDE = "hide";
                public static final String VOCA_KNOW_ALL = "vocaKnowAll";
                public static final String VIDEO_FROM_NETWORK = "videoFromNetwork";
                public static final String MEDIA_TYPE = "mediaType";
                public static final String ARTIST = "artist";
                public static final String PLAYLIST_ID = "playListId";
            }
        }

        //TODO : need to remove this after NETWORK(FTP, WebDAV). Don't need to check this.
        public static class MEANING {
            public static final int NONE = -1;
            public static final int REFRESH = 0;
            public static final int WITH = 1;
            public static final int WITHOUT = 2;
        }

        public static class TIMER {
            public static final int SECOND = 1000;
            public static final int MIN = 0;
            public static final int HIDE_TEXT_CENTER = 500;
//            public static final int BACKWARD_5_SECOND = 5000;
        }

        public static class SLEEP {
//            public static final String[] RANGE = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "", "20", "25", "30", "40", "50", "60"};
//            public static final int RANGE_DEFAULT = 9;
            public static final int ONE_SECOND = 1000;
            public static final int ONE_MINUTE = ONE_SECOND * 60;
            public static final int COUNTDOWN_INTERVAL = ONE_SECOND;
            public static final int SHOW_CONFIRM_POPUP = ONE_SECOND * 30; //ONE_MINUTE;
        }

        public static class SORT {
            public static final int CREATE_DATE_ASC = 0;
            public static final int CREATE_DATE_DESC = 1;
            public static final int FILE_SIZE_ASC = 2;
            public static final int FILE_SIZE_DESC = 3;
            public static final int FILE_NAME_ASC = 4;
            public static final int FILE_NAME_DESC = 5;
            public static final int DIFFICULTY_ASC = 6;
            public static final int DIFFICULTY_DESC = 7;
        }

        public static class SWIPE {
            public static final int NONE = 0;
            public static final int VERTICALLY_SCREEN = NONE + 1;
            public static final int VERTICALLY_LEFT_SCREEN = VERTICALLY_SCREEN + 1;
            public static final int VERTICALLY_RIGHT_SCREEN = VERTICALLY_LEFT_SCREEN + 1;
            public static final int PLAYING_AREA = VERTICALLY_RIGHT_SCREEN + 1; //Was TOP
            public static final int SUBTITLE_VIEW = PLAYING_AREA + 1;  //Was BOTTOM
            public static final int RUBY = SUBTITLE_VIEW + 1;
            public static final int BOTTOM_EDGE = RUBY + 1;  // swipe bottom area to move next/prev dialog (Use same function with SUBTITLE_VIEW)
            public static final int BOTTOM_EDGE_IN_SUBTITLE_TABLE = BOTTOM_EDGE + 1;  // swipe bottom area in the subtitle table view to move next/prev dialog (Use same function with SUBTITLE_VIEW)
            public static final int TOP_EDGE = BOTTOM_EDGE_IN_SUBTITLE_TABLE + 1;  // swipe top area to move forward/backward quickly (Use same function with PLAYING_AREA)

            public static final int DISTANCE_MIN = 100;
            public static final int HEIGHT_OF_EDGE = 130;
            public static final int MULTIPLIER_TO_MOVE_FORWARD_BACKWARD_QUICKLY = 10;
            public static final float PERCENTAGE_HEIGHT_OF_TOP_SCREEN_AREA_FOR_SWIPING_FORWARD_BACKWARD = 0.2f; //When Swipe on this area move forward/backward with MULTIPLIER_FOR_TOUCHDOWN_ON_TOP_SCREEN_AREA times more.
//            public static final float PERCENTAGE_HEIGHT_OF_TOP_SCREEN_AREA_FOR_BRIGHTNESS_VOLUME = 0.2f; //Don't want to adjust brightness or volume when I touch on the top of the screen.

            public static class BRIGHTNESS {
                public static final int MIN = 1;
                public static final int MAX = 255;
            }

            public static class VOLUME {
                public static final int MIN = 0;
                public static final int MAX = 100;
            }

        }

        public static class AUDIO {
            public static final float AUDIO_SPEED_DEFAULT = 1.0f;
            public static final float AUDIO_SPEED_ADJUST = 0.1f;
            public static final float AUDIO_SPEED_MIN = 0.1f;
            public static final float AUDIO_SPEED_MAX = 3.0f;
        }

        public static class DELAY_SUBTITLE {
            public static final String[] MIN_MAX_DELAY_RANGE = new String[]{"5", "10", "15", "20", "30", "60", "180", "300", "600", "1200"}; //Dalnim add
            public static final int DEFAULT_MIN_MAX_INDEX = 4;
            public static final int MAX_TIME = 1 * 30 * 1000;
//            public static final int MIN_TIME = MAX_TIME * -1; //Dalnim comment out
            public static final int ADJUST = 100;
            public static final int RESET = 0;
        }

        public static class REPEAT {
            public static class TIMEBASE {
                public static final int NONE = 0;
                public static final int A = 1;
                public static final int B = 2;
            }

            public static class TYPE {
                public static final int NONE = 0;
//                public static final int WORD = 1; //Don't use this anymore.
                public static final int SUBTITLE = 1;
                public static final int DICTATION = 2;
            }
        }

        public static class BOOKMARK {
            public static class SELECT {
                public static final int ALL = 0;
                public static final int NONE = 1;
            }

            public static class REPEAT {
                public static final int TIME_1 = 1;
                public static final int TIME_3 = 3;
                public static final int TIME_5 = 5;
                public static final int TIME_10 = 10;
                public static final int TIME_20 = 20;
            }
        }

        public static class OPTION {
            public static class SUBTITLE {
                public static class ENCODING {
                    public static final String[] GROUP = {"DEFAULT", "KOREAN", "OTHER", "JAPANESE"};
                    public static final String AUTO = "AUTO";
                    public static final String[] ITEM_DEFAULT = {AUTO};
                    public static final String[] ITEM_KOREAN = {"EUC-KR", "CP949", "IOS-2022-KR", "JOBHAB"};
                    public static final String[] ITEM_OTHER = {"UTF-8", "UTF-16", "UTF-32"};
                    public static final String[] ITEM_JAPANESE = {"EUC-JP", "SHIFT_JIS", "CP932", "ISO-2022-JP", "ISO-2022-JP-2", "ISO-2022-JP-1"};
                    public static final String[][] ITEMS = {ITEM_DEFAULT, ITEM_KOREAN, ITEM_OTHER, ITEM_JAPANESE};
                }
            }
        }

        public static class SQL {
            public static class TABLE {

                public static final String DIC = "DIC";
                public static final String DIC_CH_S = "DIC_CH_S";
                public static final String DIC_CH_S_SENTENCE = "DIC_CH_S_SENTENCE";
                public static final String DIC_ENG = "DIC_ENG";
                public static final String DIC_ENG_SENTENCE = "DIC_ENG_SENTENCE";
                public static final String DIC_JP = "DIC_JP";
                public static final String DIC_JP_SENTENCE = "DIC_JP_SENTENCE";
                public static final String DIC_KO = "DIC_KO";
                public static final String DIC_KO_SENTENCE = "DIC_KO_SENTENCE";
                public static final String DIC_PLAYER_SCREEN = "DIC_PLAYER_SCREEN";
                public static final String DIC_PLAYER_SCREEN_BACKUP = "DIC_PLAYER_SCREEN_BACKUP"; //이미 한번 로드한적이 있는 비디오는 AB반복을 재사용할려고
                public static final String DIC_PLAYER_SCREEN_AB_REPEAT = "DIC_PLAYER_SCREEN_AB_REPEAT"; //AB반복을 여러개 하기 위해서 DIC_PLAYER_SCREEN에 있는 AB반복은 안쓴다.
                public static final String DIC_PLAYER_SCREEN_STORED_LAYOUT = "DIC_PLAYER_SCREEN_STORED_LAYOUT"; //4화면에 로드된 비디오들을 저장하여 다시 일일히 고르지 않게 할려고
                public static final String DIC_PLAYER_VIDEO_LIST_IN_SCREEN = "DIC_PLAYER_VIDEO_LIST_IN_SCREEN"; //각화면에서 앞뒤로 이동가능한 비디오 파일명을 저장함.

                public static final String GPT_CHAT_MESSAGE = "GPT_CHAT_MESSAGE";
                public static final String GPT_TEXT_SHORT_CUT = "GPT_TEXT_SHORT_CUT";

                public static final String MEDIA_INFO = "MEDIA_INFO";
                public static final String SERVER_VOCABOOK = "SERVER_VOCABOOK";
                public static final String SERVER_VOCABOOKS = "SERVER_VOCABOOKS";
                public static final String USER_DIC_ENG = "USER_DIC_ENG";
                public static final String USER_VOCABOOK_LOCAL = "USER_VOCABOOK_LOCAL";
                public static final String USER_VOCABOOKS_LOCAL = "USER_VOCABOOKS_LOCAL";
                public static final String SUBTITLE = "SUBTITLE";
                public static final String SUBTITLE_WORDLIST = "SUBTITLE_WORDLIST";
                public static final String DIC_ICT_TERM = "DIC_ICT_TERM";
                public static final String TBL_VERSION = "TBL_VERSION";
            }

            public static class COLUMN {
                public static final String ALBUM = "ALBUM";
                public static final String AB_A = "AB_A";
                public static final String AB_B = "AB_B";
                public static final String ARTIST = "ARTIST";
                public static final String ARTIST_TTS = "ARTIST_TTS";
                public static final String BOOKMARK = "BOOKMARK";
                public static final String COMPLETION_TOKENS = "COMPLETION_TOKENS";
                public static final String COUNT = "COUNT";
                public static final String DESC = "DESC";
                public static final String DISP_ORDER = "DISP_ORDER";
                public static final String EDITED = "EDITED";
                public static final String END_TIME = "END_TIME";
                public static final String END_TIME_ORIGINAL = "END_TIME_ORIGINAL";
                public static final String EXAMPLE_SENTENCES = "EXAMPLE_SENTENCES";
                public static final String FREQUENCY = "FREQUENCY";
                public static final String FILE_PATH = "FILE_PATH";
                public static final String CREATE_DATE = "CREATE_DATE";
                public static final String GROUP_ICT = "GROUP_ICT";
                public static final String GROUP_ETC = "GROUP_ETC";
                public static final String HANJA = "HANJA";
                public static final String HAS_VOICE_FILE = "HAS_VOICE_FILE";
                public static final String ID = "ID";
                public static final String ID_IN_SERVER_VOCA_BOOK = "ID_IN_SERVER_VOCA_BOOK";
                public static final String ID_IN_USER_VOCA_BOOK = "ID_IN_USER_VOCA_BOOK";
                public static final String IMAGE_LIST = "IMAGE_LIST";
                public static final String JMDICT_MEANING = "JMDICT_MEANING";
                public static final String JMDICT_MEANING_ENG = "JMDICT_MEANING_ENG";
                public static final String LANG_STUDY = "LANG_STUDY";
                public static final String LAST_TIME = "LAST_TIME";
                public static final String LAYOUT_NAME = "LAYOUT_NAME";
                public static final String LENGTH = "LENGTH";
                public static final String LYRIC_CREATOR = "LYRIC_CREATOR";
                public static final String LYRIC_FILE_CREATOR = "LYRIC_FILE_CREATOR";
                public static final String LYRIC_FILE_EDITOR = "LYRIC_FILE_EDITOR";
                public static final String MEANING = "MEANING";
                public static final String MEANING_TTS = "MEANING_TTS";
                public static final String MEANING_DETAILED = "MEANING_DETAILED";

                public static final String MEANING_AR = "MEANING_AR";
                public static final String MEANING_AR_DETAILED = "MEANING_AR_DETAILED";
                public static final String MEANING_AR_FOR_HIDE_ALL = "MEANING_AR_FOR_HIDE_ALL";
                public static final String MEANING_AR_TTS = "MEANING_AR_TTS";
                public static final String MEANING_BN = "MEANING_BN";
                public static final String MEANING_BN_DETAILED = "MEANING_BN_DETAILED";
                public static final String MEANING_BN_FOR_HIDE_ALL = "MEANING_BN_FOR_HIDE_ALL";
                public static final String MEANING_BN_TTS = "MEANING_BN_TTS";
                public static final String MEANING_CH_S = "MEANING_CH_S";
                public static final String MEANING_CH_S_DETAILED = "MEANING_CH_S_DETAILED";
                public static final String MEANING_CH_S_FOR_HIDE_ALL = "MEANING_CH_S_FOR_HIDE_ALL";
                public static final String MEANING_CH_S_TTS = "MEANING_CH_S_TTS";
                public static final String MEANING_CH_T = "MEANING_CH_T";
                public static final String MEANING_CH_T_DETAILED = "MEANING_CH_T_DETAILED";
                public static final String MEANING_CH_T_FOR_HIDE_ALL = "MEANING_CH_T_FOR_HIDE_ALL";
                public static final String MEANING_CH_T_TTS = "MEANING_CH_T_TTS";
                public static final String MEANING_CS = "MEANING_CS";
                public static final String MEANING_CS_DETAILED = "MEANING_CS_DETAILED";
                public static final String MEANING_CS_FOR_HIDE_ALL = "MEANING_CS_FOR_HIDE_ALL";
                public static final String MEANING_CS_TTS = "MEANING_CS_TTS";
                public static final String MEANING_DA = "MEANING_DA";
                public static final String MEANING_DA_DETAILED = "MEANING_DA_DETAILED";
                public static final String MEANING_DA_FOR_HIDE_ALL = "MEANING_DA_FOR_HIDE_ALL";
                public static final String MEANING_DA_TTS = "MEANING_DA_TTS";
                public static final String MEANING_DE = "MEANING_DE";
                public static final String MEANING_DE_DETAILED = "MEANING_DE_DETAILED";
                public static final String MEANING_DE_FOR_HIDE_ALL = "MEANING_DE_FOR_HIDE_ALL";
                public static final String MEANING_DE_TTS = "MEANING_DE_TTS";
                public static final String MEANING_EL = "MEANING_EL";
                public static final String MEANING_EL_DETAILED = "MEANING_EL_DETAILED";
                public static final String MEANING_EL_FOR_HIDE_ALL = "MEANING_EL_FOR_HIDE_ALL";
                public static final String MEANING_EL_TTS = "MEANING_EL_TTS";
                public static final String MEANING_ENG = "MEANING_ENG";
                public static final String MEANING_ENG_DETAILED = "MEANING_ENG_DETAILED";
                public static final String MEANING_ENG_FOR_HIDE_ALL = "MEANING_ENG_FOR_HIDE_ALL";
                public static final String MEANING_ENG_TTS = "MEANING_ENG_TTS";
                public static final String MEANING_ES = "MEANING_ES";
                public static final String MEANING_ES_DETAILED = "MEANING_ES_DETAILED";
                public static final String MEANING_ES_FOR_HIDE_ALL = "MEANING_ES_FOR_HIDE_ALL";
                public static final String MEANING_ES_TTS = "MEANING_ES_TTS";
                public static final String MEANING_FI = "MEANING_FI";
                public static final String MEANING_FI_DETAILED = "MEANING_FI_DETAILED";
                public static final String MEANING_FI_FOR_HIDE_ALL = "MEANING_FI_FOR_HIDE_ALL";
                public static final String MEANING_FI_TTS = "MEANING_FI_TTS";
                public static final String MEANING_FR = "MEANING_FR";
                public static final String MEANING_FR_DETAILED = "MEANING_FR_DETAILED";
                public static final String MEANING_FR_FOR_HIDE_ALL = "MEANING_FR_FOR_HIDE_ALL";
                public static final String MEANING_FR_TTS = "MEANING_FR_TTS";
                public static final String MEANING_HE = "MEANING_HE";
                public static final String MEANING_HE_DETAILED = "MEANING_HE_DETAILED";
                public static final String MEANING_HE_FOR_HIDE_ALL = "MEANING_HE_FOR_HIDE_ALL";
                public static final String MEANING_HE_TTS = "MEANING_HE_TTS";
                public static final String MEANING_HI = "MEANING_HI";
                public static final String MEANING_HI_DETAILED = "MEANING_HI_DETAILED";
                public static final String MEANING_HI_FOR_HIDE_ALL = "MEANING_HI_FOR_HIDE_ALL";
                public static final String MEANING_HI_TTS = "MEANING_HI_TTS";
                public static final String MEANING_HR = "MEANING_HR";
                public static final String MEANING_HR_DETAILED = "MEANING_HR_DETAILED";
                public static final String MEANING_HR_FOR_HIDE_ALL = "MEANING_HR_FOR_HIDE_ALL";
                public static final String MEANING_HR_TTS = "MEANING_HR_TTS";
                public static final String MEANING_HU = "MEANING_HU";
                public static final String MEANING_HU_DETAILED = "MEANING_HU_DETAILED";
                public static final String MEANING_HU_FOR_HIDE_ALL = "MEANING_HU_FOR_HIDE_ALL";
                public static final String MEANING_HU_TTS = "MEANING_HU_TTS";
                public static final String MEANING_ID = "MEANING_ID";
                public static final String MEANING_ID_DETAILED = "MEANING_ID_DETAILED";
                public static final String MEANING_ID_FOR_HIDE_ALL = "MEANING_ID_FOR_HIDE_ALL";
                public static final String MEANING_ID_TTS = "MEANING_ID_TTS";
                public static final String MEANING_IT = "MEANING_IT";
                public static final String MEANING_IT_DETAILED = "MEANING_IT_DETAILED";
                public static final String MEANING_IT_FOR_HIDE_ALL = "MEANING_IT_FOR_HIDE_ALL";
                public static final String MEANING_IT_TTS = "MEANING_IT_TTS";
                public static final String MEANING_JP = "MEANING_JP";
                public static final String MEANING_JP_DETAILED = "MEANING_JP_DETAILED";
                public static final String MEANING_JP_FOR_HIDE_ALL = "MEANING_JP_FOR_HIDE_ALL";
                public static final String MEANING_JP_TTS = "MEANING_JP_TTS";
                public static final String MEANING_KO = "MEANING_KO";
                public static final String MEANING_KO_DETAILED = "MEANING_KO_DETAILED";
                public static final String MEANING_KO_FOR_HIDE_ALL = "MEANING_KO_FOR_HIDE_ALL";
                public static final String MEANING_KO_TTS = "MEANING_KO_TTS";
                public static final String MEANING_NL = "MEANING_NL";
                public static final String MEANING_NL_DETAILED = "MEANING_NL_DETAILED";
                public static final String MEANING_NL_FOR_HIDE_ALL = "MEANING_NL_FOR_HIDE_ALL";
                public static final String MEANING_NL_TTS = "MEANING_NL_TTS";
                public static final String MEANING_NO = "MEANING_NO";
                public static final String MEANING_NO_DETAILED = "MEANING_NO_DETAILED";
                public static final String MEANING_NO_FOR_HIDE_ALL = "MEANING_NO_FOR_HIDE_ALL";
                public static final String MEANING_NO_TTS = "MEANING_NO_TTS";
                public static final String MEANING_PL = "MEANING_PL";
                public static final String MEANING_PL_DETAILED = "MEANING_PL_DETAILED";
                public static final String MEANING_PL_FOR_HIDE_ALL = "MEANING_PL_FOR_HIDE_ALL";
                public static final String MEANING_PL_TTS = "MEANING_PL_TTS";
                public static final String MEANING_PT = "MEANING_PT";
                public static final String MEANING_PT_DETAILED = "MEANING_PT_DETAILED";
                public static final String MEANING_PT_FOR_HIDE_ALL = "MEANING_PT_FOR_HIDE_ALL";
                public static final String MEANING_PT_TTS = "MEANING_PT_TTS";
                public static final String MEANING_RO = "MEANING_RO";
                public static final String MEANING_RO_DETAILED = "MEANING_RO_DETAILED";
                public static final String MEANING_RO_FOR_HIDE_ALL = "MEANING_RO_FOR_HIDE_ALL";
                public static final String MEANING_RO_TTS = "MEANING_RO_TTS";
                public static final String MEANING_RU = "MEANING_RU";
                public static final String MEANING_RU_DETAILED = "MEANING_RU_DETAILED";
                public static final String MEANING_RU_FOR_HIDE_ALL = "MEANING_RU_FOR_HIDE_ALL";
                public static final String MEANING_RU_TTS = "MEANING_RU_TTS";
                public static final String MEANING_SK = "MEANING_SK";
                public static final String MEANING_SK_DETAILED = "MEANING_SK_DETAILED";
                public static final String MEANING_SK_FOR_HIDE_ALL = "MEANING_SK_FOR_HIDE_ALL";
                public static final String MEANING_SK_TTS = "MEANING_SK_TTS";
                public static final String MEANING_SV = "MEANING_SV";
                public static final String MEANING_SV_DETAILED = "MEANING_SV_DETAILED";
                public static final String MEANING_SV_FOR_HIDE_ALL = "MEANING_SV_FOR_HIDE_ALL";
                public static final String MEANING_SV_TTS = "MEANING_SV_TTS";
                public static final String MEANING_TH = "MEANING_TH";
                public static final String MEANING_TH_DETAILED = "MEANING_TH_DETAILED";
                public static final String MEANING_TH_FOR_HIDE_ALL = "MEANING_TH_FOR_HIDE_ALL";
                public static final String MEANING_TH_TTS = "MEANING_TH_TTS";
                public static final String MEANING_TR = "MEANING_TR";
                public static final String MEANING_TR_DETAILED = "MEANING_TR_DETAILED";
                public static final String MEANING_TR_FOR_HIDE_ALL = "MEANING_TR_FOR_HIDE_ALL";
                public static final String MEANING_TR_TTS = "MEANING_TR_TTS";
                public static final String MEANING_UK = "MEANING_UK";
                public static final String MEANING_UK_DETAILED = "MEANING_UK_DETAILED";
                public static final String MEANING_UK_FOR_HIDE_ALL = "MEANING_UK_FOR_HIDE_ALL";
                public static final String MEANING_UK_TTS = "MEANING_UK_TTS";
                public static final String MEANING_VI = "MEANING_VI";
                public static final String MEANING_VI_DETAILED = "MEANING_VI_DETAILED";
                public static final String MEANING_VI_FOR_HIDE_ALL = "MEANING_VI_FOR_HIDE_ALL";
                public static final String MEANING_VI_TTS = "MEANING_VI_TTS";



                public static final String MEMO = "MEMO";
                public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
                public static final String MESSAGE_CONTENT = "MESSAGE_CONTENT";

                public static final String NAME_AR = "NAME_AR";
                public static final String NAME_BN = "NAME_BN";
                public static final String NAME_KO = "NAME_KO";
                public static final String NAME_CH_S = "NAME_CH_S";
                public static final String NAME_CH_T = "NAME_CH_T";
                public static final String NAME_CS = "NAME_CS";
                public static final String NAME_DA = "NAME_DA";
                public static final String NAME_DE = "NAME_DE";
                public static final String NAME_EL = "NAME_EL";
                public static final String NAME_ENG = "NAME_ENG";
                public static final String NAME_ES = "NAME_ES";
                public static final String NAME_FI = "NAME_FI";
                public static final String NAME_FR = "NAME_FR";
                public static final String NAME_HE = "NAME_HE";
                public static final String NAME_HI = "NAME_HI";
                public static final String NAME_HR = "NAME_HR";
                public static final String NAME_HU = "NAME_HU";
                public static final String NAME_ID = "NAME_ID";
                public static final String NAME_IT = "NAME_IT";
                public static final String NAME_JP = "NAME_JP";
                public static final String NAME_NL = "NAME_NL";
                public static final String NAME_NO = "NAME_NO";
                public static final String NAME_PL = "NAME_PL";
                public static final String NAME_PT = "NAME_PT";
                public static final String NAME_RO = "NAME_RO";
                public static final String NAME_RU = "NAME_RU";
                public static final String NAME_SK = "NAME_SK";
                public static final String NAME_SV = "NAME_SV";
                public static final String NAME_TH = "NAME_TH";
                public static final String NAME_TR = "NAME_TR";
                public static final String NAME_UK = "NAME_UK";
                public static final String NAME_VI = "NAME_VI";
                public static final String PERSON_AB = "PERSON_AB";
                public static final String PARENT_ID = "PARENT_ID";
                public static final String POSALL = "POSALL";
                public static final String PROMPT_TOKENS = "PROMPT_TOKENS";
                public static final String PRONOUNCE = "PRONOUNCE";
                public static final String RECORDING_PATH = "RECORDING_PATH";
                public static final String REPEAT = "REPEAT";
                public static final String REQUEST_RESONPSE_TYPE = "REQUEST_RESONPSE_TYPE";
                public static final String RESIZE_MODE = "RESIZE_MODE";
                public static final String ROLE = "ROLE";
                public static final String ROOM_ID = "ROOM_ID";
                public static final String ROTATE = "ROTATE";
                public static final String ROTATE_LAYOUT = "ROTATE_LAYOUT";
                public static final String RUBY = "RUBY";
                public static final String SCREEN_ID = "SCREEN_ID";
                public static final String SEARCH_HISTORY = "SEARCH_HISTORY";
                public static final String START_TIME = "START_TIME";
                public static final String START_TIME_ORIGINAL = "START_TIME_ORIGINAL";
                public static final String STORED_ID = "STORED_ID";
                public static final String SUBTITLE_ID = "SUBTITLE_ID";
                public static final String SYSTEM_DEFAULT = "SYSTEM_DEFAULT";
                public static final String TERM_ENG_ABBR = "TERM_ENG_ABBR";
                public static final String TERM_ENG_FULL = "TERM_ENG_FULL";
                public static final String TERM_GROUP_ETC = "GROUP_ETC";
                public static final String TERM_GROUP_GIS = "GROUP_GIS";
                public static final String TERM_GROUP_HW = "GROUP_HW";
                public static final String TERM_GROUP_ICT = "GROUP_ICT";
                public static final String TERM_GROUP_NATION = "GROUP_NATION";
                public static final String TERM_KO_TITLE = "TERM_KO_TITLE";
                public static final String TERM_HANJA_TITLE = "TERM_HANJA_TITLE";
                public static final String TERM_KO_SHORT = "TERM_KO_SHORT";
                public static final String TERM_KO_FULL = "TERM_KO_FULL";
                public static final String TERM_LEVEL = "TERM_LEVEL";
                public static final String TITLE = "TITLE";
                public static final String TITLE_TTS = "TITLE_TTS";
                public static final String TOTAL_TOKENS = "TOTAL_TOKENS";
                public static final String TYPE = "TYPE";
                public static final String UPDATE_DATE = "UPDATE_DATE";
                public static final String URL_LIST = "URL_LIST";
                public static final String USE_AB = "USE_AB";
                public static final String USE_ALPHABET = "USE_ALPHABET";

                public static final String USE_POPUP_1_MENU = "USE_POPUP_1_MENU";
                public static final String USE_POPUP_2_MENU = "USE_POPUP_2_MENU";
                public static final String USE_POPUP_3_MENU = "USE_POPUP_3_MENU";
                public static final String USE_PROMPT = "USE_PROMPT";
                public static final String USE_RECORDING = "USE_RECORDING";
                public static final String USE_VOCABOOK = "USE_VOCABOOK";

                public static final String USED = "USED";
                public static final String USED_ARACONV = "USED_ARACONV";
                public static final String USED_ARAHANGUL = "USED_ARAHANGUL";
                public static final String USED_ARAHANJA = "USED_ARAHANJA";
                public static final String USED_ARAKOICA = "USED_ARAKOICA";
                public static final String USED_ARAPLAYER = "USED_ARAPLAYER";
                public static final String USED_ARAVOCA = "USED_ARAVOCA";
                public static final String UID = "UID";
                public static final String URL = "URL";
                public static final String VERSION = "VERSION";
                public static final String VOCA = "VOCA";
                public static final String VOCABOOKS_ID = "VOCABOOKS_ID";
                public static final String VOCA_APPEARANCE_ORDER = "VOCA_APPEARANCE_ORDER";
                public static final String VOCA_COUNT = "VOCA_COUNT";
                public static final String VOCA_DISPLAY = "VOCA_DISPLAY";
                public static final String VOCA_ID = "VOCA_ID";
                public static final String VOCA_ID_BASE = "BASE_VOCA_ID";
                public static final String VOCA_ID_TO_SEND_SERVER = "VOCA_ID_TO_SEND_SERVER";
                public static final String VOCA_KNOW = "VOCA_KNOW";
                public static final String VOCA_KNOWPRONOUNCE = "VOCA_KNOWPRONOUNCE";
                public static final String VOCA_LEVEL = "VOCA_LEVEL";
                public static final String VOCA_ORIGINAL = "VOCA_ORIGINAL";
                public static final String VOCA_ORI_ID = "VOCA_ORI_ID";
                public static final String VOCA_RUBY = "VOCA_RUBY";
                public static final String VOCA_TTS = "VOCA_TTS";
                public static final String VOCA_TYPE = "VOCA_TYPE";
                public static final String VOCA_TYPE_BASE = "BASE_VOCA_TYPE";
                public static final String VOLUME = "VOLUME";
                public static final String WORD = "WORD";
                public static final String WORDORI = "WORDORI";
                public static final String WORDORI_ID = "WORDORI_ID";
                public static final String WORD_COUNT = "WORD_COUNT";
                public static final String WORD_DISPLAY = "WORD_DISPLAY";
                public static final String WORDLEVEL = "WORDLEVEL";
                public static final String WORD_TTS = "WORD_TTS";
            }

            public static class QUERY {
                public static final String AND = " AND ";
                public static final String AS = " AS ";
                public static final String ASC = " ASC ";
                public static final String BETWEEN = " BETWEEN ";
                public static final String COMMA = ",";
                public static final String DESC = " DESC ";
                public static final String EQUAL = " = ";
                public static final String FINISH = " ;";
                public static final String FROM = " FROM ";
                public static final String GREATER = " > ";
                public static final String GREATER_THAN_OR_EQUAL = " >= ";
                public static final String GROUP_BY = " GROUP BY ";
                public static final String GROUP_CONCAT = " GROUP_CONCAT ";
                public static final String HAVING = " HAVING ";
                public static final String IN_CLOSE = ") ";
                public static final String IN_OPEN = " IN (";
                public static final String LEFT_JOIN = " LEFT JOIN ";
                public static final String LESS = " < ";
                public static final String LESS_THAN_OR_EQUAL = " <= ";
                public static final String LIKE = " LIKE ";
                public static final String LIMIT = " LIMIT ";
                public static final String LIMIT_1 = " LIMIT 1 ";
                public static final String MAX = " MAX ";
                public static final String MIN = " MIN ";
                public static final String NOT = " NOT ";
                public static final String NOT_EMPTY = " != \"\" ";
                public static final String NOT_EQUAL = " != ";
                public static final String OFFSET = " OFFSET ";
                public static final String OR = " OR ";
                public static final String ORDER_BY = " ORDER BY ";
                public static final String PARENTHESIS_CLOSE = " ) ";
                public static final String PARENTHESIS_OPEN = " ( ";
                public static final String REPLACE = " REPLACE ";
                public static final String RANDOM = " ORDER BY RANDOM() ";
                public static final String SELECT_ALL = "SELECT * FROM ";
                public static final String SELECT_ALL_A = "SELECT A.* FROM ";
                public static final String SELECT_ALL_A_PERSON_AB = "SELECT A.*, PERSON_AB FROM ";
                public static final String SELECT_ALL_COUNT = "SELECT *, COUNT(*) as COUNT FROM ";
                public static final String SELECT_COUNT = "SELECT COUNT (*) FROM ";
                public static final String SET = " SET ";
                public static final String UNION = " UNION ";
                public static final String UPDATE = "UPDATE ";
                public static final String WHERE = " WHERE ";
                public static final String SELECT = "SELECT ";

                public static final String GET_DIC = SELECT_ALL + TABLE.DIC;
                public static final String GET_DIC_SIZE = SELECT_COUNT + TABLE.DIC;
                public static final String GET_SUBTITLE = SELECT_ALL + TABLE.SUBTITLE;
                public static final String GET_SUBTITLE_SIZE = SELECT_COUNT + TABLE.SUBTITLE;
            }
        }

        public static class SETTING {
//            public static final int SUBTITLE_FONT_SIZE_DEFAULT = 56; // No one uses this
            public static final int SUBTITLE_FONT_SIZE_MIN = 10; //Dalnim add.
            public static final int SUBTITLE_FULL_SCREEN_FONT_SIZE_MIN = 12; //Dalnim add.
//            public static final int SUBTITLE_FONT_SIZE_MAX = 2; //Dalnim add.
            public static final float SUBTITLE_FONT_SIZE_ADJUST_BY_PINCH = 0.6f; //Dalnim add.
            public static final int SUBTITLE_FONT_SIZE_ADJUST = 2; //Dalnim add.
            public static final int SWIPE_FOR_BACK_WARD_DEFAULT = 180;
            public static final int SWIPE_FOR_BACK_WARD_INDEX_DEFAULT = 11;
            public static final int TAP_FOR_BACK_WARD_DEFAULT = 3;
            public static final int TAP_FOR_BACK_WARD_INDEX_DEFAULT = 2;
            public static final String[] TAP_FOR_BACK_WARD_RANGE = new String[]{"1", "2", "3", "4", "5", "7", "10", "15", "20", "30", "60", "180", "300", "600", "1800", "3000", "6000"};
        }

        public static class SERVER {
            public static class TYPE {
                public static final int FREE_FTP_DOWNLOAD = -3;
                public static final int DOWNLOAD = -2;
                public static final int NONE = -1;
                public static final int FTP = 0;
                public static final int WEBDAV = 1;
                public static final int SMB = 2;
                public static final int DROPBOX = 3;
            }

            public static class PROTOCOL {
                public static final String FTP = "ftp://";
                public static final String WEBDAV = "https://";
            }

            public static class DOWNLOAD {
                public static class STATUS {
                    public static final int WAIT = 0;
                    public static final int DOWNLOAD = 1;
                    public static final int PAUSE = 2;
                    public static final int COMPLETE = 3;
                    public static final int ERROR = 4;
                }
            }

            public static final int TIMEOUT = 7200000;
        }

        public static class WEB_DICTIONARY {
            public static final String URL_DEFAULT = "https://m.dic.daum.net/search.do?dic=eng&q=";
            public static String WORD_REPLACE = "%word%";
            public static final int TAB_MAX = 4;
        }

        public static class TRANSLATOR {
            public static String STUDY_REPLACE = "%study%";
            public static String MOTHER_REPLACE = "%mother%";
            public static String WORD_REPLACE = "%word%";
            public static String VIEW_REPLACE = "%view%";

            public static class SERVER {
                public static final String DEEPL = "https://www.deepl.com/translator#%study%/%mother%/%word%";
                public static final String GOOGLE = "https://translate.google.com/?hl=%view%#view=home&op=translate&sl=%study%&tl=%mother%&text=%word%";
                public static final String PAPAGO = "https://papago.naver.com/?sk=%study%&tk=%mother%&hn=0&st=%word%";
            }
        }

        public static class QUIZ {
            public static final String[] MAX_QUIZ_COUNT_RANGE = new String[]{"5", "10", "20", "30", "50", "100", "200", "300", "500", "1000"};
            public static final int MAX_QUIZ_COUNT_DEFAULT = 2;
            public static final int QUIZ_TYPE_DEFAULT = 0;
            public static final int QUIZ_ANSWERS = 4;
        }

//        public static class STORAGE {
//            public static class COUNT_OF_FILES {
//                public static final int VIDEO = 1;
//                public static final int SUBTITLE = 2;
//            }
//        }

        public static class THE_MOVIE_DB {
            public static final String BASE_URL = "https://api.themoviedb.org/3/";
            public static final String KEY_MOVIE = "movie";
            public static final String KEY_SEASON = "season";
            public static final String KEY_EPISODE = "episode";
            public static final String KEY_TV = "tv";
            public static final String KEY_PERSON = "person";
            public static final String SEARCH_MULTI_URL = BASE_URL + "search/multi";
            public static final String SEARCH_SEASON_URL = BASE_URL + "tv/";
            public static final String API_KEY = "35ae1e18660f31a75780bbe2d05be147";
            public static final String APPEND_CREDITS = "credits";
            public static final String APPEND_VIDEOS = "videos";
            public static final String APPEND_ALTERNATIVE_TITLES = "alternative_titles";

            // TODO : Dalnim - need to call https://api.themoviedb.org/3/configuration?api_key=35ae1e18660f31a75780bbe2d05be147 and get the available file size.
            // Ref : https://developers.themoviedb.org/3/configuration/get-api-configuration
            public static final String BASE_IMAGE_URL_SMALL_SIZE = "https://image.tmdb.org/t/p/w154";
            public static final String BASE_IMAGE_URL_MEDIUM_SIZE = "https://image.tmdb.org/t/p/w500";
            public static final String BASE_IMAGE_URL_BIG_SIZE = "https://image.tmdb.org/t/p/w1280";
            public static final String BASE_IMAGE_URL_ORIGINAL_SIZE = "https://image.tmdb.org/t/p/original";

            public static final String BASE_FILE_SEASON = "season";
            public static final String BASE_FILE_POSTER = "_poster.";
            public static final String BASE_FILE_BACKDROP = "_backdrop.";
            public static final String BASE_FILE_STILL = "_still.";
            public static final String BASE_FILE_JSON = "_tmdb.json";
//            public static final String BASE_IMAGE_PEOPLE_URL = "https://image.tmdb.org/t/p/w138_and_h175_face";
            public static final String BASE_IMAGE_PEOPLE_URL_ORIGINAL = "https://image.tmdb.org/t/p/original";
            public static final String BASE_IMAGE_PEOPLE_URL = "https://image.tmdb.org/t/p/w185";
            public static final String KEY_DIRECTOR = "Director";
            public static final String KEY_WRITER = "Writer";
            public static final int MAX_CAST_DISPLAY = 5;
            public static final int MAX_CAST_LIST = 100;
            public static final String BASE_PERSON_URL = "https://www.themoviedb.org/person/";
            public static final String WEB_SITE = "https://www.themoviedb.org/";
            public static final String KEY_TRAILER = "Trailer";
            public static final String KEY_YOUTUBE = "Youtube";

            public static class TYPE {
                public static final int NONE = 0;
                public static final int SEASON = 1;
                public static final int EPISODE = 2;
            }
        }
    }

    public static class OPENSUBTITLES {
        public static final String USER_AGENT = "araplayer/v1";
        public static final String TEMP_UA = "TemporaryUserAgent";
    }

    public static class ARAHANJA {
//        public static final String DATABASE_NAME = "arahanja.realm";
        public static final String DATABASE_FOLDER = "database";
        public static final String TESSDATA_FOLDER = "tessdata";
        public static final String ASSET_DATABASE_NAME = "araonesoft.realm";
    }

    public static class ARAMULTIPLAYER {
        //        public static final String DATABASE_NAME = "arahanja.realm";
        public static final String ASSET_DATABASE_NAME = "araonesoft.multiplayer.sqlite";
        public static final String DATABASE_NAME = "araonesoft.multiplayer.sqlite";
    }
    public static class ARAKOICA {
        public static final String DATABASE_FOLDER = "database";
        public static final String DATABASE_NAME = "araonesoft.sqlite";
    }
    public static class ARACONV {
        public static final String DATABASE_FOLDER = "database";
        public static final String DATABASE_NAME = "araonesoft.sqlite";
        public static final String ASSET_DATABASE_NAME_ENG = "araonesoft_eng.sqlite";
        public static final String ASSET_DATABASE_NAME_CH_S = "araonesoft_ch_s.sqlite";
        public static final String ASSET_DATABASE_NAME_JP = "araonesoft_jp.sqlite";
        public static final String ASSET_DATABASE_NAME_KO = "araonesoft_ko.sqlite";
        public static final String ASSET_ZIP_VOICE_ENG = "13.zip";
        public static final String ASSET_ZIP_VOICE_CH_S = "1.zip";
        public static final String ASSET_ZIP_VOICE_JP = "4.zip";
        public static final String ASSET_ZIP_VOICE_KO = "3.zip";
        public static class KO {
            public static final String ASSET_VOICE_FILE_LIST_TEXT = "voice_file_list_3.txt";
        }
    }

    public static class ARAPLAYER {
        public static final int numberOfFileGroupToDisplayAdsBanner = 10;
    }
    public static class INT_BOOLEAN {
        public static final int FASLE = 0;
        public static final int TRUE = 1;
    }

    public static class HANJA {
        public static class WORKBOOK_ID {
            public static final int level = 95;
            public static final int commonMiddleSchool = 581;
            public static final int commonHighSchool = 582;
            public static final int radical = 583;
            public static final int bookIdIdiom = 797;
            public static final int bookIdLevelKoreaType1 = 798;
            public static final int bookIdLevelKoreaType2 = 799;
            public static final int bookIdLevelKoreaType3 = 800;
        }
        public static class COMMON_USE {
            public static final String commonMiddleSchool = "중학교";
            public static final String commonHighSchool = "고등학교";
        }
    }

    public static class TODAY_EXPRESSION_VOCA {
        public static class ENG {
            public static final int VOCA_ID_IN_SETENCE_START = 266;
            public static final int VOCA_ID_IN_SETENCE_END = 1312;
        }
        public static class CH_S {
            public static final int VOCA_ID_IN_SETENCE_START = 266;
            public static final int VOCA_ID_IN_SETENCE_END = 1312;
        }
        public static class KO {
            public static final int VOCA_ID_IN_SETENCE_START = 266;
            public static final int VOCA_ID_IN_SETENCE_END = 1312;
        }
        public static class JP {
            public static final int VOCA_ID_IN_SETENCE_START = 266;
            public static final int VOCA_ID_IN_SETENCE_END = 1312;
        }
    }

    public static class BOOK {
        public static class ENG {
            public static class CONVERSATION { //이건 전체 문장이 들어있다.
                public static final int BASIC_EXPRESSION = 16;
                public static final int TRAVEL_AIRPORT = 17;
                public static final int TRAVEL_TRANSPORT = 18;
                public static final int TRAVEL_RESTAURANT = 19;
                public static final int TRAVEL_HOTEL = 20;
                public static final int TRAVEL_SHOPPING = 21;
                public static final int TRAVEL_TOUR = 22;
                public static final int TRAVEL_TELEPHONE = 23;
                public static final int TRAVEL_EMERGENCY = 24;
            }
            public static class CONVERSATION_GROUP { //이건 기초 밑에 인사1, 인사2등의 그룹을 가져온다. 근데 112와 257은 왜 동떨어 져 있을까?
                public static final int BASIC_EXPRESSION = 103;
                public static final int BASIC_EXPRESSION_1 = 112;
                public static final int BASIC_EXPRESSION_2 = 113;
                public static final int BASIC_EXPRESSION_3 = 114;
                public static final int BASIC_EXPRESSION_4 = 257;
                public static final int BASIC_EXPRESSION_5 = 258;
                public static final int BASIC_EXPRESSION_6 = 259;
                public static final int BASIC_EXPRESSION_LEARN_ENGLISH = 282;
                public static final int BASIC_EXPRESSION_LEARN_KOREAN = 283;
                public static final int TRAVEL_AIRPORT = 108;
                public static final int TRAVEL_AIRPORT_COUNTER_1 = 145;
                public static final int TRAVEL_AIRPORT_COUNTER_2 = 146;
                public static final int TRAVEL_AIRPORT_COUNTER_3 = 147;
                public static final int TRAVEL_AIRPORT_COUNTER_4 = 148;
                public static final int TRAVEL_AIRPORT_ON_AIRPLANE_1 = 150;
                public static final int TRAVEL_AIRPORT_ON_AIRPLANE_2 = 149;
                public static final int TRAVEL_AIRPORT_ON_AIRPLANE_3 = 151;
                public static final int TRAVEL_AIRPORT_ON_AIRPLANE_4 = 152;
                public static final int TRAVEL_AIRPORT_ON_AIRPLANE_5 = 153;
                public static final int TRAVEL_AIRPORT_IMMIGRATION_1 = 154;
                public static final int TRAVEL_AIRPORT_IMMIGRATION_2 = 155;
                public static final int TRAVEL_AIRPORT_IMMIGRATION_3 = 156;
                public static final int TRAVEL_TRANSPORT = 105;
                public static final int TRAVEL_TRANSPORT_ASK_DIRECTION_1 = 267;
                public static final int TRAVEL_TRANSPORT_ASK_DIRECTION_2 = 268;
                public static final int TRAVEL_TRANSPORT_ASK_DIRECTION_3 = 129;
                public static final int TRAVEL_TRANSPORT_ASK_DIRECTION_4 = 130;
                public static final int TRAVEL_TRANSPORT_ASK_DIRECTION_5 = 131;
                public static final int TRAVEL_TRANSPORT_SUBWAY_1 = 268;
                public static final int TRAVEL_TRANSPORT_SUBWAY_2 = 269;
                public static final int TRAVEL_TRANSPORT_SUBWAY_3 = 270;
                public static final int TRAVEL_TRANSPORT_SUBWAY_4 = 271;
                public static final int TRAVEL_TRANSPORT_SUBWAY_5 = 132;
                public static final int TRAVEL_TRANSPORT_SUBWAY_6 = 133;
                public static final int TRAVEL_TRANSPORT_SUBWAY_7 = 134;
                public static final int TRAVEL_TRANSPORT_RENT_1 = 135;
                public static final int TRAVEL_TRANSPORT_RENT_2 = 136;
                public static final int TRAVEL_TRANSPORT_RENT_3 = 137;
                public static final int TRAVEL_RESTAURANT = 104;
                public static final int TRAVEL_RESTAURANT_ORDER_1 = 273;
                public static final int TRAVEL_RESTAURANT_ORDER_2 = 274;
                public static final int TRAVEL_RESTAURANT_ORDER_3 = 275;
                public static final int TRAVEL_RESTAURANT_ORDER_4 = 125;
                public static final int TRAVEL_RESTAURANT_ORDER_5 = 126;
                public static final int TRAVEL_RESTAURANT_ORDER_6 = 127;
                public static final int TRAVEL_RESTAURANT_ORDER_7 = 128;
                public static final int TRAVEL_RESTAURANT_PAYING_1 = 276;
                public static final int TRAVEL_RESTAURANT_PAYING_2 = 277;
                public static final int TRAVEL_RESTAURANT_PAYING_3 = 278;
                public static final int TRAVEL_RESTAURANT_PAYING_4 = 122;
                public static final int TRAVEL_RESTAURANT_PAYING_5 = 123;
                public static final int TRAVEL_RESTAURANT_PAYING_6 = 124;
                public static final int TRAVEL_HOTEL = 109;
                public static final int TRAVEL_HOTEL_CHECK_IN_1 = 118;
                public static final int TRAVEL_HOTEL_CHECK_IN_2 = 119;
                public static final int TRAVEL_HOTEL_CHECK_IN_3 = 120;
                public static final int TRAVEL_HOTEL_CHECK_IN_4 = 121;
                public static final int TRAVEL_HOTEL_CHECK_OUT_1 = 122;
                public static final int TRAVEL_HOTEL_CHECK_OUT_2 = 123;
                public static final int TRAVEL_HOTEL_CHECK_OUT_3 = 124;
                public static final int TRAVEL_SHOPPING = 111;
                public static final int TRAVEL_SHOPPING_1 = 160;
                public static final int TRAVEL_SHOPPING_2 = 161;
                public static final int TRAVEL_SHOPPING_3 = 162;
                public static final int TRAVEL_SHOPPING_4 = 163;
                public static final int TRAVEL_SHOPPING_REFUND_1 = 164;
                public static final int TRAVEL_SHOPPING_REFUND_2 = 165;
                public static final int TRAVEL_SHOPPING_REFUND_3 = 166;
                public static final int TRAVEL_TOUR = 107;
                public static final int TRAVEL_TOUR_PHOTO_1 = 279;
                public static final int TRAVEL_TOUR_PHOTO_2 = 280;
                public static final int TRAVEL_TOUR_PHOTO_3 = 281;
                public static final int TRAVEL_TOUR_PHOTO_4 = 142;
                public static final int TRAVEL_TOUR_PHOTO_5 = 143;
                public static final int TRAVEL_TOUR_PHOTO_6 = 144;
                public static final int TRAVEL_TELEPHONE = 106;
                public static final int TRAVEL_TELEPHONE_1 = 260;
                public static final int TRAVEL_TELEPHONE_2 = 261;
                public static final int TRAVEL_TELEPHONE_3 = 262;
                public static final int TRAVEL_TELEPHONE_4 = 263;
                public static final int TRAVEL_TELEPHONE_5 = 264;
                public static final int TRAVEL_TELEPHONE_6 = 265;
                public static final int TRAVEL_TELEPHONE_7 = 266;
                public static final int TRAVEL_TELEPHONE_8 = 138;
                public static final int TRAVEL_TELEPHONE_9 = 139;
                public static final int TRAVEL_TELEPHONE_10 = 140;
                public static final int TRAVEL_EMERGENCY = 110;
                public static final int TRAVEL_EMERGENCY_1 = 157;
                public static final int TRAVEL_EMERGENCY_2 = 158;
                public static final int TRAVEL_EMERGENCY_3 = 159;
            }
        }
        public static class CH_S {
            public static class CONVERSATION {
                public static final int BASIC_EXPRESSION = 25;
                public static final int TRAVEL_AIRPORT = 26;
                public static final int TRAVEL_TRANSPORT = 27;
                public static final int TRAVEL_RESTAURANT =28;
                public static final int TRAVEL_HOTEL = 29;
                public static final int TRAVEL_SHOPPING = 30;
                public static final int TRAVEL_TOUR = 31;
                public static final int TRAVEL_TELEPHONE = 32;
                public static final int TRAVEL_EMERGENCY = 33;
            }
        }
        public static class JP {
            public static class CONVERSATION {
                public static final int BASIC_EXPRESSION = 34;
                public static final int TRAVEL_AIRPORT = 35;
                public static final int TRAVEL_TRANSPORT = 36;
                public static final int TRAVEL_RESTAURANT =37;
                public static final int TRAVEL_HOTEL = 38;
                public static final int TRAVEL_SHOPPING = 39;
                public static final int TRAVEL_TOUR = 40;
                public static final int TRAVEL_TELEPHONE = 41;
                public static final int TRAVEL_EMERGENCY = 42;
            }
        }
        public static class KO {
            public static class CONVERSATION {
                public static final int BASIC_EXPRESSION = 57;
                public static final int TRAVEL_AIRPORT = 70;
                public static final int TRAVEL_TRANSPORT = 71;
                public static final int TRAVEL_RESTAURANT =72;
                public static final int TRAVEL_HOTEL = 73;
                public static final int TRAVEL_SHOPPING = 74;
                public static final int TRAVEL_TOUR = 75;
                public static final int TRAVEL_TELEPHONE = 76;
                public static final int TRAVEL_EMERGENCY = 77;
            }
            public static class HANGUL {
                public static final int CONSONANTS = 168;
                public static final int DOUBLE_CONSONANTS = 844;
                public static final int VOWELS = 169;
                public static final int COMBINATION_VOWELS =845;
                public static final int COMBINE_CONSONANTS_AND_VOWELS = 170;
                public static final int BATCHIM_FINAL_CONSTONANTS = 171;
            }
        }
    }


    public static class VOCABOOKS {
        public static class USED {
            public static final int NOT_USE = 0;
            public static final int USE_FOR_ADMIN = 1;
            public static final int USE_FOR_TEST = 10;
            public static final int USE_FOR_BUY = 90;
            public static final int USE_FOR_LOGIN_USER = 100;
            public static final int USE_FOR_FREE = 1000;

        }
    }

    public static class DIFFICULT {
        public static final int NOT_DETERMINED = 0;
        public static final int AMKI_1ST = NOT_DETERMINED + 1;
        public static final int AMKI_2ND = AMKI_1ST + 1;
        public static final int UNKNOWN = AMKI_2ND + 1;
        public static final int KNOWN = UNKNOWN + 1;

    }

    public enum MenuButtonInPlayer {
        // TODO: Change enum name to make clearly.
        BUTTON_EXPAND,
        ROTATE_SCREEN,
        NIGHT_MODE,
        PLAYER_OPTION,
        PLAYER_SETTING,
        EMBEDDED_TRACKS,
        DELAY_SUBTITLE,
        SLEEP,
        ONE_HAND_MODE,
        SUBTITLE_GROUP,
//        WEB_DICTIONARY,
        MIRROR_MODE,
        FLIP_VERTICALLY,
        SCREEN_RESIZE,
        LISTEN_COMPREHENSION_OPTION,
    }

    public static class AppMediaType {
        public static final int VIDEO = 0;
        public static final int SUBTITLE = VIDEO + 1;
        public static final int MUSIC = SUBTITLE + 1;
        public static final int LYRIC = MUSIC + 1;
    }
}