package com.dalread.util.arasubtitle;

import java.util.regex.Pattern;

public class Constants {
    public static class VOCA_KNOW {
        public static final Integer NULL = -1;
        public static final Integer NOTRATED = 0;
        public static final Integer AMKI_GRADE_1 = 10;
        public static final Integer AMKI_GRADE_2 = 20;
        public static final Integer UNKNOWN = 90;
        public static final Integer KNOWN = 100;
        public static final Integer EXCLUDE = 110;
        public static final Integer NOTINDIC = 120;
    }
    public static final Integer VOCA_KNOW_NULL = -1;
    public static final Integer VOCA_KNOW_NOTRATED = 0;
    public static final Integer VOCA_KNOW_AMKI_GRADE_1 = 10;
    public static final Integer VOCA_KNOW_AMKI_GRADE_2 = 20;
    public static final Integer VOCA_KNOW_UNKNOWN = 90;
    public static final Integer VOCA_KNOW_KNOWN = 100;
    public static final Integer VOCA_KNOW_EXCLUDE = 110;
    public static final Integer VOCA_KNOW_NOTINDIC = 120;

    // 북마크정보는 다른유저와 같이쓰는 TBL_DIC_ENG_BOOKMARK등에만 들어간다. 각자 사전 DB에 있는 북마크필드는 현재
    // 안쓴다.
    public static final Integer WORD_UNBOOKMARKED = 0;
    public static final Integer WORD_BOOKMARKED = 1;
    public static final int COOKIES_MAXAGE = 60 * 60 * 24 * 7; // set expire time to for 7 days; 60*60*24*7
    // public static final int COOKIES_MAXAGE = 60 * 5;

    public static final Integer DIALOGUE_HIDE = 0;
    public static final Integer DIALOGUE_SHOW = 1;
    /** 영화의 자막중 개별 대사를 보여줄지 여부 */

    /** DB 명 */
    public static final String DB_NAME = "test";
    // public static final String DB_NAME = "daltalk";

    /** 테이블 명 */

    public static final String TBL_APP_INFO = "APP_INFO";

    public static final String TBL_CHATROOM_LIST = "CHATROOM_LIST";
    public static final String TBL_CHATROOM_USER = "CHATROOM_USER";

    public static final String TBL_CLASS = "TBL_CLASS";
    public static final String TBL_CLASS_LIST= "TBL_CLASS_LIST";
    public static final String TBL_DIC_CH_S = "DIC_CH_S";
    public static final String TBL_DIC_CH_S_SENTENCE = "DIC_CH_S_SENTENCE";
    public static final String TBL_DIC_ENG = "DIC_ENG";
    public static final String TBL_DIC_ENG_SENTENCE = "DIC_ENG_SENTENCE";
    public static final String TBL_DIC_ENG_SPECIAL_WORDS = "DIC_ENG_SPECIAL_WORDS";
    public static final String TBL_DIC_JP = "DIC_JP";
    public static final String TBL_DIC_JP_JMDICT = "DIC_JP_JMDICT";
    public static final String TBL_DIC_JP_SENTENCE = "DIC_JP_SENTENCE";
    public static final String TBL_DIC_KO = "DIC_KO";
    public static final String TBL_DIC_KO_SENTENCE = "DIC_KO_SENTENCE";
    public static final String TBL_DIC_HANJA = "DIC_HANJA";
    public static final String TBL_DIC_HANJA_DECOMPOSITION = "DIC_HANJA_DECOMPOSITION";
    public static final String TBL_DIC_HANJA_SENTENCE = "DIC_HANJA_SENTENCE";
    public static final String TBL_DIC_HANJA_BOOK = "DIC_HANJA_BOOK";
    public static final String TBL_VO_NLP_PARSED = "VO_NLP_PARSED";
    public static final String TBL_DIC_VOCA_GROUP_CONFUSED = "DIC_VOCA_GROUP_CONFUSED";
    public static final String TBL_DIC_VOCA_GROUP_SPELLING_DIFFERENCES = "DIC_VOCA_GROUP_SPELLING_DIFFERENCES";

    //////이건 옛날꺼다...
    public static final String TBL_DIC_CH_S_BOOKMARK = "TBL_DIC_CH_S_BOOKMARK";
    public static final String TBL_DIC_ENG_BOOKMARK = "TBL_DIC_ENG_BOOKMARK";
    public static final String TBL_DIC_JP_BOOKMARK = "TBL_DIC_JP_BOOKMARK";
    public static final String TBL_DIC_HANJA_BOOKMARK = "TBL_DIC_HANJA_BOOKMARK";
    public static final String TBL_DIC_KO_BOOKMARK = "TBL_DIC_KO_BOOKMARK";
    //// 이걸 새로 사용함.
    public static final String DIC_CH_S_BOOKMARK = "DIC_CH_S_BOOKMARK";
    public static final String DIC_ENG_BOOKMARK = "DIC_ENG_BOOKMARK";
    public static final String DIC_JP_BOOKMARK = "DIC_JP_BOOKMARK";
    public static final String DIC_HANJA_BOOKMARK = "DIC_HANJA_BOOKMARK";
    public static final String DIC_KO_BOOKMARK = "DIC_KO_BOOKMARK";

    public static final String TBL_DIC_CH_S_HISTORY = "TBL_DIC_CH_S_HISTORY";
    public static final String TBL_DIC_ENG_HISTORY = "TBL_DIC_ENG_HISTORY";
    public static final String TBL_DIC_JP_HISTORY = "TBL_DIC_JP_HISTORY";
    public static final String TBL_DIC_HANJA_HISTORY = "TBL_DIC_HANJA_HISTORY";
    public static final String TBL_DIC_KO_HISTORY = "TBL_DIC_KO_HISTORY";
    public static final String TBL_SERVER_VOCABOOK_HISTORY = "TBL_SERVER_VOCABOOK_HISTORY";
    public static final String TBL_USER_VOCABOOK_HISTORY = "TBL_USER_VOCABOOK_HISTORY";

    public static final String TBL_GRAMMAR = "GRAMMAR";
    public static final String TBL_GRAMMAR_QUESTIONS = "GRAMMAR_QUESTIONS";
    public static final String TBL_READING = "READING";

    public static final String TBL_DIC_ICT_TERM = "DIC_ICT_TERM";
    public static final String TBL_DIC_ICT_TERM_HISTORY = "TBL_DIC_ICT_TERM_HISTORY";


    public static final String TBL_NLP_PARSED_CH_S = "NLP_PARSED_CH_S";
    public static final String TBL_NLP_PARSED_JP = "NLP_PARSED_JP";
    public static final String TBL_NLP_PARSED_ENG = "NLP_PARSED_ENG";
    public static final String TBL_NLP_PARSED_CH_S_UNTUNED = "NLP_PARSED_CH_S_UNTUNED";
    public static final String TBL_NLP_PARSED_JP_UNTUNED = "NLP_PARSED_JP_UNTUNED";
    public static final String TBL_NLP_PARSED_ENG_UNTUNED = "NLP_PARSED_ENG_UNTUNED";

    public static final String TBL_VIDEOS_DEFAULT = "VIDEOS_DEFAULT";
    public static final String TBL_VOICE_RE_RECORD_LIST = "VOICE_RE_RECORD_LIST";
    public static final String TBL_VOCABOOKS_REAL_SITUATION = "VOCABOOKS_REAL_SITUATION";
    public static final String TBL_VOCA_QUIZ_RESULT_ENG = "VOCA_QUIZ_RESULT_ENG";
    public static final String TBL_VOCA_QUIZ_RESULT_CH_S = "VOCA_QUIZ_RESULT_CH_S";
    public static final String TBL_VOCA_QUIZ_RESULT_JP = "VOCA_QUIZ_RESULT_JP";
    public static final String TBL_VOCA_QUIZ_RESULT_KO = "VOCA_QUIZ_RESULT_KO";
    public static final String TBL_VOCA_QUIZ_RESULT_HANJA = "VOCA_QUIZ_RESULT_HANJA";
    public static final String TBL_VOCA_QUIZ_RESULT_EACH_QUESTION_ENG = "VOCA_QUIZ_RESULT_EACH_QUESTION_ENG";
    public static final String TBL_VOCA_QUIZ_RESULT_EACH_QUESTION_CH_S = "VOCA_QUIZ_RESULT_EACH_QUESTION_CH_S";
    public static final String TBL_VOCA_QUIZ_RESULT_EACH_QUESTION_JP = "VOCA_QUIZ_RESULT_EACH_QUESTION_JP";
    public static final String TBL_VOCA_QUIZ_RESULT_EACH_QUESTION_KO = "VOCA_QUIZ_RESULT_EACH_QUESTION_KO";
    public static final String TBL_VOCA_QUIZ_RESULT_EACH_QUESTION_HANJA = "VOCA_QUIZ_RESULT_EACH_QUESTION_HANJA";


    public static final String TBL_FILES_VOICE = "FILES_VOICE";
    public static final String TBL_FILES_IMAGE = "FILES_IMAGE";

    public static final String TBL_LESSON_EXAM = "LESSON_EXAM";
    public static final String TBL_LOG_POINT_CHARGE = "LOG_POINT_CHARGE";
    public static final String TBL_LANGUAGE = "TBL_LANGUAGE";


    public static final String TBL_NATIVE_SPEAKERS = "NATIVE_SPEAKERS";
    public static final String TBL_MEMBER = "TBL_MEMBER";
    public static final String TBL_MESSAGE = "TBL_MESSAGE";
    public static final String TBL_POINT_SET = "TBL_POINT_SET";
    public static final String TBL_PRACTICE_ROOM = "PRACTICE_ROOM";
    public static final String TBL_PREFERRED_NATIVESPEAKERS = "PREFERRED_NATIVESPEAKERS";

    public static final String TBL_QUIZ_VOCABOOK = "QUIZ_VOCABOOK";
    public static final String TBL_QUIZ_VOCABOOKS = "QUIZ_VOCABOOKS";

    public static final String TBL_SERVER_VOCABOOK = "SERVER_VOCABOOK";
    public static final String TBL_SERVER_VOCABOOKS = "SERVER_VOCABOOKS";
    public static final String TBL_STUDENT_COUNT = "STUDENT_COUNT";
    public static final String TBL_STUDENT_DO_YOU_KNOW_THIS_VOCA = "STUDENT_DO_YOU_KNOW_THIS_VOCA";
    public static final String TBL_STUDENT_LIST = "STUDENT_LIST";
    public static final String TBL_STUDENT_STUDIED_HISTORY = "STUDENT_STUDIED_HISTORY";
    public static final String TBL_TMDB_DATA = "TMDB_DATA";

    public static final String TBL_VIDEO_FILE_LIST = "VIDEO_FILE_LIST";
    public static final String TBL_SUBTITLE_FILE_LIST = "SUBTITLE_FILE_LIST";
    public static final String TBL_VIDEO_SUBTITLE_FILE_MATCH = "VIDEO_SUBTITLE_FILE_MATCH";
    public static final String TBL_VIDEO_LIST = "VIDEO_LIST";
    public static final String TBL_SUBTITLE_ENG = "SUBTITLE_ENG";
    public static final String TBL_SUBTITLE_JP = "SUBTITLE_JP";
    public static final String TBL_SUBTITLE_CH_S = "SUBTITLE_CH_S";
    public static final String TBL_SUBTITLE_KO = "SUBTITLE_KO";
    public static final String TBL_SUBTITLE_ENG_UNTUNED = "SUBTITLE_ENG_UNTUNED";
    public static final String TBL_SUBTITLE_JP_UNTUNED = "SUBTITLE_JP_UNTUNED";
    public static final String TBL_SUBTITLE_CH_S_UNTUNED = "SUBTITLE_CH_S_UNTUNED";
    public static final String TBL_SUBTITLE_KO_UNTUNED = "SUBTITLE_KO_UNTUNED";


    public static final String TBL_LESSON_HISTORY = "LESSON_HISTORY";
    public static final String TBL_LESSON_HISTORY_WORKBOOKS = "LESSON_HISTORY_WORKBOOKS";
    public static final String TBL_LESSON_ROLE_PLAYING = "LESSON_ROLE_PLAYING";
    public static final String TBL_LESSON_READING = "LESSON_READING";
    public static final String TBL_LESSON_SCHEDULE = "LESSON_SCHEDULE";
    public static final String TBL_LESSON_STUDENT = "LESSON_STUDENT";
    public static final String TBL_LESSON_TUTOR = "LESSON_TUTOR";
    public static final String TBL_LESSON_USER = "LESSON_USER";

    public static final String TBL_QUIZ_LIST_FOR_QUIZ_BATTLE = "QUIZ_LIST_FOR_QUIZ_BATTLE";

    public static final String TBL_ROLE_PLAYING_CATEGORY = "ROLE_PLAYING_CATEGORY";
    public static final String TBL_ROLE_PLAYING_CATEGORYNAME_TO_DISPLAY = "ROLE_PLAYING_CATEGORYNAME_TO_DISPLAY";

    //	public static final String TBL_ROLE_PLAYING_FAKE_DATA = "ROLE_PLAYING_FAKE_DATA";
    public static final String TBL_ROLE_PLAYING_FAKE_DATA_NOUN = "ROLE_PLAYING_FAKE_DATA_NOUN";
    public static final String TBL_ROLE_PLAYING_MISSION = "ROLE_PLAYING_MISSION";
    public static final String TBL_ROLE_PLAYING_SHOPNAME = "ROLE_PLAYING_SHOPNAME";
    //	public static final String TBL_ROLE_PLAYING_TITLE_NAME = "ROLE_PLAYING_TITLE_NAME";
    public static final String TBL_ROLE_PLAYING_SENTENCES = "ROLE_PLAYING_SENTENCES";
    public static final String TBL_ROLE_PLAYING_CONVERSATION = "ROLE_PLAYING_CONVERSATION";
    public static final String TBL_ROLE_PLAYING_USER_DATA = "ROLE_PLAYING_USER_DATA";

    public static final String TBL_STUDENT_STUDY_VOCA = "STUDENT_STUDY_VOCA";
    public static final String TBL_STUDENT_PRONOUNCE_FEEDBACK = "STUDENT_PRONOUNCE_FEEDBACK";
    public static final String TBL_STUDENT_TARGET_VOCA = "STUDENT_TARGET_VOCA";
    public static final String TBL_TUTOR_EVALUATE_VOCA = "TUTOR_EVALUATE_VOCA";
    public static final String TBL_TUTOR_SEND_STUDY = "TUTOR_SEND_STUDY";
    public static final String TBL_TUTOR_STUDENTS = "TUTOR_STUDENTS";

    public static final String TBL_USER_ACCESS_HISTORY = "USER_ACCESS_HISTORY";
    public static final String TBL_USER_ANALYZE_SUBTITLE_HISTORY = "USER_ANALYZE_SUBTITLE_HISTORY";
    public static final String TBL_USER_BLOCK = "USER_BLOCK";
    public static final String TBL_USER_FAVORITE= "USER_FAVORITE";
    //	public static final String TBL_USER_FOLLOWER = "USER_FOLLOWER";
    public static final String TBL_USER_FOLLOWING = "USER_FOLLOWING";

    public static final String TBL_USER_DIC_CH_S_PREFIX = "TBL_USER_DIC_CH_S";
    public static final String TBL_USER_DIC_ENG_PREFIX = "TBL_USER_DIC_ENG";
    public static final String TBL_USER_DIC_JP_PREFIX = "TBL_USER_DIC_JP";
    public static final String TBL_USER_DIC_HANJA_PREFIX = "TBL_USER_DIC_HANJA";
    public static final String TBL_USER_DIC_KO_PREFIX = "TBL_USER_DIC_KO";

    public static final String TBL_USER_DIC_CH_S_SENTENCE_PREFIX = "TBL_USER_DIC_CH_S_SENTENCE";
    public static final String TBL_USER_DIC_ENG_SENTENCE_PREFIX = "TBL_USER_DIC_ENG_SENTENCE";
    public static final String TBL_USER_DIC_JP_SENTENCE_PREFIX = "TBL_USER_DIC_JP_SENTENCE";
    public static final String TBL_USER_DIC_HANJA_SENTENCE_PREFIX = "TBL_USER_DIC_HANJA_SENTENCE";
    public static final String TBL_USER_DIC_KO_SENTENCE_PREFIX = "TBL_USER_DIC_KO_SENTENCE";

    public static final String TBL_USER_DIC_CH_S_SUBTITLE_PREFIX = "TBL_USER_DIC_CH_S_SUBTITLE";
    public static final String TBL_USER_DIC_ENG_SUBTITLE_PREFIX = "TBL_USER_DIC_ENG_SUBTITLE";
    public static final String TBL_USER_DIC_JP_SUBTITLE_PREFIX = "TBL_USER_DIC_JP_SUBTITLE";
    public static final String TBL_USER_DIC_HANJA_SUBTITLE_PREFIX = "TBL_USER_DIC_HANJA_SUBTITLE";
    public static final String TBL_USER_DIC_KO_SUBTITLE_PREFIX = "TBL_USER_DIC_KO_SUBTITLE";

    public static final String TBL_USER_DIC_CH_S_SUBTITLE_UNTUNED_PREFIX = "TBL_USER_DIC_CH_S_SUBTITLE_UNTUNED";
    public static final String TBL_USER_DIC_ENG_SUBTITLE_UNTUNED_PREFIX = "TBL_USER_DIC_ENG_SUBTITLE_UNTUNED";
    public static final String TBL_USER_DIC_JP_SUBTITLE_UNTUNED_PREFIX = "TBL_USER_DIC_JP_SUBTITLE_UNTUNED";
    public static final String TBL_USER_DIC_HANJA_SUBTITLE_UNTUNED_PREFIX = "TBL_USER_DIC_HANJA_SUBTITLE_UNTUNED";
    public static final String TBL_USER_DIC_KO_SUBTITLE_UNTUNED_PREFIX = "TBL_USER_DIC_KO_SUBTITLE_UNTUNED";

    public static final String TBL_USER_USER = "USER_USER";
    public static final String TBL_USER_VOCABOOK = "USER_VOCABOOK";
    public static final String TBL_USER_VOCABOOKS = "USER_VOCABOOKS";
    public static final String TBL_VERSION = "TBL_VERSION";

    /** 필드 명 */
    public static final String FLD_ACCESS_COUNT = "ACCESS_COUNT";
    public static final String FLD_ACCESS_DATE = "ACCESS_DATE";
    //	public static final String FLD_ACCESS_DATE_TS = "ACCESS_DATE_TS";
    public static final String FLD_AGE = "AGE";
    //	public static final String FLD_AMKI_GRADE = "AMKI_GRADE";
    public static final String FLD_AMKI_REG_COUNT = "AMKI_REG_COUNT";
    public static final String FLD_APP_NAME = "APP_NAME";
    public static final String FLD_APP_VERSION = "APP_VERSION";
    public static final String FLD_ALLOW_CHAT = "ALLOW_CHAT";
    public static final String FLD_ALLOW_PUSH = "ALLOW_PUSH";

    public static final String FLD_QUIZ_APP = "QUIZ_APP";

    public static final String FLD_QUESTION = "QUESTION";
    public static final String FLD_ANSWER_1 = "ANSWER_1";
    public static final String FLD_ANSWER_2 = "ANSWER_2";
    public static final String FLD_ANSWER_3 = "ANSWER_3";
    public static final String FLD_ANSWER_4 = "ANSWER_4";



    public static final String FLD_BIO = "BIO";
    public static final String FLD_BOOKMARK = "BOOKMARK";
    public static final String FLD_BOOKMARK_PREVIOUS = "BOOKMARK_PREVIOUS";
    public static final String FLD_BRANCH = "BRANCH";
    public static final String FLD_CATEGORY_A = "CATEGORY_A";
    public static final String FLD_CATEGORY_B = "CATEGORY_B";
    public static final String FLD_CATEGORY_C = "CATEGORY_C";
    public static final String FLD_CATEGORY_D = "CATEGORY_D";
    public static final String FLD_CATEGORY_ID = "CATEGORY_ID";

    public static final String FLD_CHANNEL_ID = "CHANNEL_ID";
    public static final String FLD_CHATROOM_ID = "CHATROOM_ID";
    public static final String FLD_CHATROOM_NAME = "CHATROOM_NAME";
    public static final String FLD_CHARGE_DATE = "CHARGE_DATE";
    public static final String FLD_CITY = "CITY";
    public static final String FLD_CLASS_NUMBER = "CLASS_NUMBER";

    public static final String FLD_CONTENT_TYPE = "CONTENT_TYPE";
    public static final String FLD_CONTENT_VIEW_IDS = "CONTENT_VIEW_IDS";
    public static final String FLD_CONTENTS = "CONTENTS";
    public static final String FLD_CONTENTS_JSON = "CONTENTS_JSON";
    public static final String FLD_CORRECT_PRONUNCIATION = "CORRECT_PRONUNCIATION";
    public static final String FLD_CORRECT_ANSWER = "CORRECT_ANSWER";
    public static final String FLD_CORRECT_ANSWER_NUMBER = "CORRECT_ANSWER_NUMBER";

    public static final String FLD_CREATOR_UID = "CREATOR_UID";
    public static final String FLD_CREATOR_TYPE = "CREATOR_TYPE";

    public static final String FLD_CURRENCY = "CURRENCY";
    public static final String FLD_CURRENT_STUDENTS = "CURRENT_STUDENTS";
    public static final String FLD_MAX_STUDENTS = "MAX_STUDENTS";
    public static final String FLD_MISSION_ID = "MISSION_ID";
    public static final String FLD_MD5 = "MD5";

    public static final String FLD_CLASS_LIST_ID = "CLASS_LIST_ID";
    public static final String FLD_CLASS_MEMBER_TYPE = "CLASS_MEMBER_TYPE";
    public static final String FLD_CLASS_GRADE = "CLASS_GRADE";

    public static final String FLD_CLIENT_TYPE = "CLIENT_TYPE";
    public static final String FLD_DIALOG = "DIALOG";
    public static final String FLD_DEAFULT_NATIVESPEAKER = "DEAFULT_NATIVESPEAKER";
    public static final String FLD_DEFAULT_PHRASE = "DEFAULT_PHRASE";
    public static final String FLD_DEFAULT_PHRASE_L1 = "DEFAULT_PHRASE_L1";
    public static final String FLD_DEVICE_OS_VERSION = "DEVICE_OS_VERSION";
    public static final String FLD_DEVICE_TYPE = "DEVICE_TYPE";

    public static final String FLD_DISPLAY = "DISPLAY";
    public static final String FLD_DISP_ORDER = "DISP_ORDER";
    public static final String FLD_DISP_ORDER_NAME_ITEM = "DISP_ORDER_NAME_ITEM";
    public static final String FLD_CREATE_DATE = "CREATE_DATE";
    public static final String FLD_EDITED = "EDITED";
    public static final String FLD_ENABLE_LESSON_PN = "ENABLE_LESSON_PN";
    public static final String FLD_EMAIL = "EMAIL";
    public static final String FLD_EVALUATE_VOCA_GRADE = "EVALUATE_VOCA_GRADE";
    public static final String FLD_EXAM_TYPE = "EXAM_TYPE";

    public static final String FLD_CORRECT_ANSWER_COUNT = "CORRECT_ANSWER_COUNT";
    public static final String FLD_CORRECT_ANSWER_COUNT_TOTAL = "CORRECT_ANSWER_COUNT_TOTAL";
    public static final String FLD_WRONG_ANSWER_COUNT = "WRONG_ANSWER_COUNT";
    public static final String FLD_WRONG_ANSWER_COUNT_TOTAL = "WRONG_ANSWER_COUNT_TOTAL";

    public static final String FLD_VOCA_QUIZ_STATUS_ID = "VOCA_QUIZ_STATUS_ID";
    public static final String FLD_VERIFICATION_TOKEN = "verification_token";
    //	public static final String FLD_VO_KEY = "VO_KEY";
    public static final String FLD_VOCABOOK_TYPE = "VOCABOOK_TYPE";
    public static final String FLD_VOCA_WITHOUT_PUNCTUATION = "VOCA_WITHOUT_PUNCTUATION";
    public static final String FLD_PARENT_ID = "PARENT_ID";
    public static final String FLD_PATH = "PATH";
    public static final String FLD_PERSON_A_ID = "PERSON_A_ID";
    public static final String FLD_PERSON_AB = "PERSON_AB";
    public static final String FLD_PERSON_B_ID = "PERSON_B_ID";
    public static final String FLD_PN_MINUTES_BEFORE_BEGIN_LESSON = "PN_MINUTES_BEFORE_BEGIN_LESSON";
    public static final String FLD_PN_MINUTES_BEFORE_FINISH_LESSON = "PN_MINUTES_BEFORE_FINISH_LESSON";


    public static final String FLD_PRI_POINT = "PRI_POINT";
    public static final String FLD_PWDRESET_TOKEN = "pwdreset_token";
    //	public static final String FLD_KNOW = "KNOW";
    public static final String FLD_VOCA_KNOW = "VOCA_KNOW";
    public static final String FLD_VOCA_KNOW_PREVIOUS = "VOCA_KNOW_PREVIOUS";
    //	public static final String FLD_KNOWPRONOUNCE = "KNOWPRONOUNCE";
    public static final String FLD_VOCA_KNOWPRONOUNCE = "VOCA_KNOWPRONOUNCE";
    public static final String FLD_VOCA_KNOWPRONOUNCE_PREVIOUS = "VOCA_KNOWPRONOUNCE_PREVIOUS";

    public static final String FLD_EXTENSION = "EXTENSION";
    public static final String FLD_FULL_WORD = "FULL_WORD";
    public static final String FLD_START_WORD = "START_WORD";
    public static final String FLD_SUBTITLE_FILE_ID = "SUBTITLE_FILE_ID";
    public static final String FLD_SUBTITLE = "SUBTITLE";

    public static final String FLD_EACH_WORD = "EACH_WORD";

    public static final String FLD_FAKE_DATA_SOURCE = "FAKE_DATA_SOURCE";
    public static final String FLD_FAKE_DATA_SOURCE_MIN_LVL_FOR_SQL = "FAKE_DATA_SOURCE_MIN_LVL_FOR_SQL";
    public static final String FLD_FAKER_CATEGORY_A = "FAKER_CATEGORY_A";
    public static final String FLD_FAKER_CATEGORY_B = "FAKER_CATEGORY_B";
    public static final String FLD_FAKER_CATEGORY_C = "FAKER_CATEGORY_C";
    public static final String FLD_FAKER_INPUT_TYPES = "FAKER_INPUT_TYPES";
    public static final String FLD_FAKER_INPUT_PARAMETERS = "FAKER_INPUT_PARAMETERS";
    public static final String FLD_FAKER_OUTPUT_COUNT = "FAKER_OUTPUT_COUNT";

    public static final String FLD_FAKER_RETURN_TYPE = "FAKER_RETURN_TYPE";

    public static final String FLD_FILE_VERSION = "FILE_VERSION";
    public static final String FLD_FILENAME = "FILENAME";
    public static final String FLD_FINISHED = "FINISHED";
    public static final String FLD_FINISH_TIME = "FINISH_TIME";
    public static final String FLD_FILESIZE = "FILESIZE";
    public static final String FLD_FOLLOWING_COUNT = "FOLLOWING_COUNT";
    public static final String FLD_FOLLOWER_COUNT = "FOLLOWER_COUNT";
    public static final String FLD_FREE_TALKING = "FREE_TALKING";
    public static final String FLD_GRAMMAR_ID = "GRAMMAR_ID";
    public static final String FLD_GROUP_ID = "GROUP_ID";
    public static final String FLD_GROUP_ICT = "GROUP_ICT";
    public static final String FLD_GROUP_ICT_BEFORE = "GROUP_ICT_BEFORE";
    public static final String FLD_GROUP_ICT_AFTER = "GROUP_ICT_AFTER";
    public static final String FLD_GROUP_ETC = "GROUP_ETC";
    public static final String FLD_GROUP_ETC_BEFORE = "GROUP_ETC_BEFORE";
    public static final String FLD_GROUP_ETC_AFTER = "GROUP_ETC_AFTER";
    public static final String FLD_GROUP_HW = "GROUP_HW";
    public static final String FLD_GROUP_HW_BEFORE = "GROUP_HW_BEFORE";
    public static final String FLD_GROUP_HW_AFTER = "GROUP_HW_AFTER";
    public static final String FLD_GROUP_GIS = "GROUP_GIS";
    public static final String FLD_GROUP_GIS_BEFORE = "GROUP_GIS_BEFORE";
    public static final String FLD_GROUP_GIS_AFTER = "GROUP_GIS_AFTER";
    public static final String FLD_GROUP_NATION = "GROUP_NATION";
    public static final String FLD_GROUP_NATION_BEFORE = "GROUP_NATION_BEFORE";
    public static final String FLD_GROUP_NATION_AFTER = "GROUP_NATION_AFTER";

    //	public static final String FLD_READING_ID = "READING_ID";
    public static final String FLD_LESSON_READING_ID = "LESSON_READING_ID";


//	public static final String FLD_HANJA = "HANJA";
//	public static final String FLD_HANJA_ORI = "HANJA_ORI";

    public static final String FLD_HANJA_KOREA = "HANJA_KOREA";
    public static final String FLD_HANJA_JAPAN = "HANJA_JAPAN";
    public static final String FLD_HANJA_SIMPLIFIED = "HANJA_SIMPLIFIED";
    public static final String FLD_HANJA_TAIWAN = "HANJA_TAIWAN";
    public static final String FLD_HANJA_SHORT_FORM = "HANJA_SHORT_FORM";
    public static final String FLD_HANJA_VARIANT_1 = "HANJA_VARIANT_1";
    public static final String FLD_HANJA_VARIANT_2 = "HANJA_VARIANT_2";
    public static final String FLD_HANJA_SOKJA = "HANJA_SOKJA";

    public static final String FLD_HANJA_KANCHE = "HANJA_KANCHE";
    public static final String FLD_HANJA_TYPE = "HANJA_TYPE";
    public static final String FLD_HANJA_YAKJA = "HANJA_YAKJA";
    public static final String FLD_HAS_SUB_LIST = "HAS_SUB_LIST";
    public static final String FLD_ICT_TERM_ID = "ICT_TERM_ID";


    public static final String FLD_JMDICT_ENT_SEQ = "JMDICT_ENT_SEQ";
    public static final String FLD_KANJI = "KANJI";
    public static final String FLD_ID = "ID";
    public static final String FLD_ID_IN_VOCABOOK = "ID_IN_VOCABOOK";
    public static final String FLD_ID_LIST = "ID_LIST";
    public static final String FLD_IMAGE_ID = "IMAGE_ID";
    //	public static final String FLD_ITEM_TYPE = "ITEM_TYPE";
    public static final String FLD_JOIN_COUNT = "JOIN_COUNT";
    public static final String FLD_JOIN_DATE = "JOIN_DATE";
    //	public static final String FLD_JOIN_DATE_TS = "JOIN_DATE_TS";
    public static final String FLD_FEEDBACK_MESSAGE = "FEEDBACK_MESSAGE";
    public static final String FLD_FEEDBACK_DATE = "FEEDBACK_DATE";
    //	public static final String FLD_FEEDBACK_SCORE = "FEEDBACK_SCORE";
    public static final String FLD_LANG_DISPLAY = "LANG_DISPLAY";
    public static final String FLD_LANG_ENG = "LANG_ENG"; // 언어명을 그나라 언어가 아니고
    // 영어로 표시한것
    public static final String FLD_LANG_ID = "LANG_ID";
    public static final String FLD_LANG_NATIVE = "LANG_NATIVE";
    public static final String FLD_LANG_STUDY = "LANG_STUDY";
    public static final String FLD_LANGUAGE_LEVEL = "LANGUAGE_LEVEL";
    public static final String FLD_LANGUAGE_LEVEL_TO = "LANGUAGE_LEVEL_TO";
    public static final String FLD_LANGUAGE_LEVEL_FROM = "LANGUAGE_LEVEL_FROM";

    public static final String FLD_APP_STATE_MODE = "APP_STATE_MODE";
    public static final String FLD_LAST_ACCESS_DATE = "LAST_ACCESS_DATE";
    //	public static final String FLD_LAST_ACCESS_DATE_TS = "LAST_ACCESS_DATE_TS";
    public static final String FLD_LEAD_LESSON = "LEAD_LESSON";
    public static final String FLD_LESSON_SCHEDULE_ID = "LESSON_SCHEDULE_ID";
    public static final String FLD_LESSON_REPEAT_TOPICS = "LESSON_REPEAT_TOPICS";
    public static final String FLD_LESSON_REPEAT_COUNT = "LESSON_REPEAT_COUNT";

    public static final String FLD_JOIN_STUDYMODE_DATE = "JOIN_STUDYMODE_DATE";
    public static final String FLD_LAST_EXIT_DATE = "LAST_EXIT_DATE";
    //	public static final String FLD_LAST_EXIT_DATE_TS = "LAST_EXIT_DATE_TS";
    public static final String FLD_LAST_STUDY_PUSH_DATE = "LAST_STUDY_PUSH_DATE";
    public static final String FLD_LAST_VOCABOOK_TYPE = "LAST_VOCABOOK_TYPE";
    public static final String FLD_LAST_VOCABOOKS_ID = "LAST_VOCABOOKS_ID";
    public static final String FLD_LESSON_TYPE = "LESSON_TYPE";
    public static final String FLD_LESSON_MODE = "LESSON_MODE";

    public static final String FLD_MAX_HANJA_QUIZ = "MAX_HANJA_QUIZ";
    public static final String FLD_MAX_HOMEWORK = "MAX_HOMEWORK";
    public static final String FLD_MAX_QUIZ = "MAX_QUIZ";
    public static final String FLD_MEANING = "MEANING";
    public static final String FLD_MEANING_FOR_STUDENT = "MEANING_FOR_STUDENT";

    public static final String FLD_MEANING_DETAILED = "MEANING_DETAILED";
    public static final String FLD_MEANING_FOR_HIDE_ALL = "MEANING_FOR_HIDE_ALL";
    public static final String FLD_MEANING_FOR_HIDE_ALL_BEFORE = "MEANING_FOR_HIDE_ALL_BEFORE";
    public static final String FLD_MEANING_FOR_HIDE_ALL_AFTER = "MEANING_FOR_HIDE_ALL_AFTER";
    public static final String FLD_MEANING_TTS = "MEANING_TTS";
    public static final String FLD_MEANING_TTS_BEFORE = "MEANING_TTS_BEFORE";
    public static final String FLD_MEANING_TTS_AFTER = "MEANING_TTS_AFTER";

    public static final String FLD_MEANING_PREVIOUS = "MEANING_PREVIOUS";

    public static final String FLD_MEANING1 = "MEANING1"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_MEANING2 = "MEANING2"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_MEANING3 = "MEANING3"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_MEANING1_PRONOUNCE1_FIRST = "MEANING1_PRONOUNCE1_FIRST";
    public static final String FLD_MEANING1_PRONOUNCE1 = "MEANING1_PRONOUNCE1";

    public static final String FLD_MEANING_AFTER = "MEANING_AFTER";
    public static final String FLD_MEANING_BEFORE = "MEANING_BEFORE";
    public static final String FLD_MEANING_DETAILED_AFTER = "MEANING_DETAILED_AFTER";
    public static final String FLD_MEANING_DETAILED_BEFORE = "MEANING_DETAILED_BEFORE";

    public static final String FLD_MEANING_AR = "MEANING_AR";
    public static final String FLD_MEANING_AR_DETAILED = "MEANING_AR_DETAILED";
    public static final String FLD_MEANING_AR_FOR_HIDE_ALL = "MEANING_AR_FOR_HIDE_ALL";
    public static final String FLD_MEANING_AR_TTS = "MEANING_AR_TTS";
    public static final String FLD_MEANING_BN = "MEANING_BN";
    public static final String FLD_MEANING_BN_DETAILED = "MEANING_BN_DETAILED";
    public static final String FLD_MEANING_BN_FOR_HIDE_ALL = "MEANING_BN_FOR_HIDE_ALL";
    public static final String FLD_MEANING_BN_TTS = "MEANING_BN_TTS";
    public static final String FLD_MEANING_CH_S = "MEANING_CH_S";
    public static final String FLD_MEANING_CH_S_DETAILED = "MEANING_CH_S_DETAILED";
    public static final String FLD_MEANING_CH_S_FOR_HIDE_ALL = "MEANING_CH_S_FOR_HIDE_ALL";
    public static final String FLD_MEANING_CH_S_TTS = "MEANING_CH_S_TTS";
    public static final String FLD_MEANING_CH_T = "MEANING_CH_T";
    public static final String FLD_MEANING_CH_T_DETAILED = "MEANING_CH_T_DETAILED";
    public static final String FLD_MEANING_CH_T_FOR_HIDE_ALL = "MEANING_CH_T_FOR_HIDE_ALL";
    public static final String FLD_MEANING_CH_T_TTS = "MEANING_CH_T_TTS";
    public static final String FLD_MEANING_CS = "MEANING_CS";
    public static final String FLD_MEANING_CS_DETAILED = "MEANING_CS_DETAILED";
    public static final String FLD_MEANING_CS_FOR_HIDE_ALL = "MEANING_CS_FOR_HIDE_ALL";
    public static final String FLD_MEANING_CS_TTS = "MEANING_CS_TTS";
    public static final String FLD_MEANING_DA = "MEANING_DA";
    public static final String FLD_MEANING_DA_DETAILED = "MEANING_DA_DETAILED";
    public static final String FLD_MEANING_DA_FOR_HIDE_ALL = "MEANING_DA_FOR_HIDE_ALL";
    public static final String FLD_MEANING_DA_TTS = "MEANING_DA_TTS";
    public static final String FLD_MEANING_DE = "MEANING_DE";
    public static final String FLD_MEANING_DE_DETAILED = "MEANING_DE_DETAILED";
    public static final String FLD_MEANING_DE_FOR_HIDE_ALL = "MEANING_DE_FOR_HIDE_ALL";
    public static final String FLD_MEANING_DE_TTS = "MEANING_DE_TTS";
    public static final String FLD_MEANING_EL = "MEANING_EL";
    public static final String FLD_MEANING_EL_DETAILED = "MEANING_EL_DETAILED";
    public static final String FLD_MEANING_EL_FOR_HIDE_ALL = "MEANING_EL_FOR_HIDE_ALL";
    public static final String FLD_MEANING_EL_TTS = "MEANING_EL_TTS";
    public static final String FLD_MEANING_ENG = "MEANING_ENG";
    public static final String FLD_MEANING_ENG_DETAILED = "MEANING_ENG_DETAILED";
    public static final String FLD_MEANING_ENG_FOR_HIDE_ALL = "MEANING_ENG_FOR_HIDE_ALL";
    public static final String FLD_MEANING_ENG_TTS = "MEANING_ENG_TTS";
    public static final String FLD_MEANING_ES = "MEANING_ES";
    public static final String FLD_MEANING_ES_DETAILED = "MEANING_ES_DETAILED";
    public static final String FLD_MEANING_ES_FOR_HIDE_ALL = "MEANING_ES_FOR_HIDE_ALL";
    public static final String FLD_MEANING_ES_TTS = "MEANING_ES_TTS";
    public static final String FLD_MEANING_FI = "MEANING_FI";
    public static final String FLD_MEANING_FI_DETAILED = "MEANING_FI_DETAILED";
    public static final String FLD_MEANING_FI_FOR_HIDE_ALL = "MEANING_FI_FOR_HIDE_ALL";
    public static final String FLD_MEANING_FI_TTS = "MEANING_FI_TTS";
    public static final String FLD_MEANING_FR = "MEANING_FR";
    public static final String FLD_MEANING_FR_DETAILED = "MEANING_FR_DETAILED";
    public static final String FLD_MEANING_FR_FOR_HIDE_ALL = "MEANING_FR_FOR_HIDE_ALL";
    public static final String FLD_MEANING_FR_TTS = "MEANING_FR_TTS";
    public static final String FLD_MEANING_HE = "MEANING_HE";
    public static final String FLD_MEANING_HE_DETAILED = "MEANING_HE_DETAILED";
    public static final String FLD_MEANING_HE_FOR_HIDE_ALL = "MEANING_HE_FOR_HIDE_ALL";
    public static final String FLD_MEANING_HE_TTS = "MEANING_HE_TTS";
    public static final String FLD_MEANING_HI = "MEANING_HI";
    public static final String FLD_MEANING_HI_DETAILED = "MEANING_HI_DETAILED";
    public static final String FLD_MEANING_HI_FOR_HIDE_ALL = "MEANING_HI_FOR_HIDE_ALL";
    public static final String FLD_MEANING_HI_TTS = "MEANING_HI_TTS";
    public static final String FLD_MEANING_HR = "MEANING_HR";
    public static final String FLD_MEANING_HR_DETAILED = "MEANING_HR_DETAILED";
    public static final String FLD_MEANING_HR_FOR_HIDE_ALL = "MEANING_HR_FOR_HIDE_ALL";
    public static final String FLD_MEANING_HR_TTS = "MEANING_HR_TTS";
    public static final String FLD_MEANING_HU = "MEANING_HU";
    public static final String FLD_MEANING_HU_DETAILED = "MEANING_HU_DETAILED";
    public static final String FLD_MEANING_HU_FOR_HIDE_ALL = "MEANING_HU_FOR_HIDE_ALL";
    public static final String FLD_MEANING_HU_TTS = "MEANING_HU_TTS";
    public static final String FLD_MEANING_ID = "MEANING_ID";
    public static final String FLD_MEANING_ID_DETAILED = "MEANING_ID_DETAILED";
    public static final String FLD_MEANING_ID_FOR_HIDE_ALL = "MEANING_ID_FOR_HIDE_ALL";
    public static final String FLD_MEANING_ID_TTS = "MEANING_ID_TTS";
    public static final String FLD_MEANING_IT = "MEANING_IT";
    public static final String FLD_MEANING_IT_DETAILED = "MEANING_IT_DETAILED";
    public static final String FLD_MEANING_IT_FOR_HIDE_ALL = "MEANING_IT_FOR_HIDE_ALL";
    public static final String FLD_MEANING_IT_TTS = "MEANING_IT_TTS";
    public static final String FLD_MEANING_JP = "MEANING_JP";
    public static final String FLD_MEANING_JP_DETAILED = "MEANING_JP_DETAILED";
    public static final String FLD_MEANING_JP_FOR_HIDE_ALL = "MEANING_JP_FOR_HIDE_ALL";
    public static final String FLD_MEANING_JP_TTS = "MEANING_JP_TTS";
    public static final String FLD_MEANING_KO = "MEANING_KO";
    public static final String FLD_MEANING_KO_DETAILED = "MEANING_KO_DETAILED";
    public static final String FLD_MEANING_KO_FOR_HIDE_ALL = "MEANING_KO_FOR_HIDE_ALL";
    public static final String FLD_MEANING_KO_TTS = "MEANING_KO_TTS";
    public static final String FLD_MEANING_NL = "MEANING_NL";
    public static final String FLD_MEANING_NL_DETAILED = "MEANING_NL_DETAILED";
    public static final String FLD_MEANING_NL_FOR_HIDE_ALL = "MEANING_NL_FOR_HIDE_ALL";
    public static final String FLD_MEANING_NL_TTS = "MEANING_NL_TTS";
    public static final String FLD_MEANING_NO = "MEANING_NO";
    public static final String FLD_MEANING_NO_DETAILED = "MEANING_NO_DETAILED";
    public static final String FLD_MEANING_NO_FOR_HIDE_ALL = "MEANING_NO_FOR_HIDE_ALL";
    public static final String FLD_MEANING_NO_TTS = "MEANING_NO_TTS";
    public static final String FLD_MEANING_PL = "MEANING_PL";
    public static final String FLD_MEANING_PL_DETAILED = "MEANING_PL_DETAILED";
    public static final String FLD_MEANING_PL_FOR_HIDE_ALL = "MEANING_PL_FOR_HIDE_ALL";
    public static final String FLD_MEANING_PL_TTS = "MEANING_PL_TTS";
    public static final String FLD_MEANING_PT = "MEANING_PT";
    public static final String FLD_MEANING_PT_DETAILED = "MEANING_PT_DETAILED";
    public static final String FLD_MEANING_PT_FOR_HIDE_ALL = "MEANING_PT_FOR_HIDE_ALL";
    public static final String FLD_MEANING_PT_TTS = "MEANING_PT_TTS";
    public static final String FLD_MEANING_RO = "MEANING_RO";
    public static final String FLD_MEANING_RO_DETAILED = "MEANING_RO_DETAILED";
    public static final String FLD_MEANING_RO_FOR_HIDE_ALL = "MEANING_RO_FOR_HIDE_ALL";
    public static final String FLD_MEANING_RO_TTS = "MEANING_RO_TTS";
    public static final String FLD_MEANING_RU = "MEANING_RU";
    public static final String FLD_MEANING_RU_DETAILED = "MEANING_RU_DETAILED";
    public static final String FLD_MEANING_RU_FOR_HIDE_ALL = "MEANING_RU_FOR_HIDE_ALL";
    public static final String FLD_MEANING_RU_TTS = "MEANING_RU_TTS";
    public static final String FLD_MEANING_SK = "MEANING_SK";
    public static final String FLD_MEANING_SK_DETAILED = "MEANING_SK_DETAILED";
    public static final String FLD_MEANING_SK_FOR_HIDE_ALL = "MEANING_SK_FOR_HIDE_ALL";
    public static final String FLD_MEANING_SK_TTS = "MEANING_SK_TTS";
    public static final String FLD_MEANING_SV = "MEANING_SV";
    public static final String FLD_MEANING_SV_DETAILED = "MEANING_SV_DETAILED";
    public static final String FLD_MEANING_SV_FOR_HIDE_ALL = "MEANING_SV_FOR_HIDE_ALL";
    public static final String FLD_MEANING_SV_TTS = "MEANING_SV_TTS";
    public static final String FLD_MEANING_TH = "MEANING_TH";
    public static final String FLD_MEANING_TH_DETAILED = "MEANING_TH_DETAILED";
    public static final String FLD_MEANING_TH_FOR_HIDE_ALL = "MEANING_TH_FOR_HIDE_ALL";
    public static final String FLD_MEANING_TH_TTS = "MEANING_TH_TTS";
    public static final String FLD_MEANING_TR = "MEANING_TR";
    public static final String FLD_MEANING_TR_DETAILED = "MEANING_TR_DETAILED";
    public static final String FLD_MEANING_TR_FOR_HIDE_ALL = "MEANING_TR_FOR_HIDE_ALL";
    public static final String FLD_MEANING_TR_TTS = "MEANING_TR_TTS";
    public static final String FLD_MEANING_UK = "MEANING_UK";
    public static final String FLD_MEANING_UK_DETAILED = "MEANING_UK_DETAILED";
    public static final String FLD_MEANING_UK_FOR_HIDE_ALL = "MEANING_UK_FOR_HIDE_ALL";
    public static final String FLD_MEANING_UK_TTS = "MEANING_UK_TTS";
    public static final String FLD_MEANING_VI = "MEANING_VI";
    public static final String FLD_MEANING_VI_DETAILED = "MEANING_VI_DETAILED";
    public static final String FLD_MEANING_VI_FOR_HIDE_ALL = "MEANING_VI_FOR_HIDE_ALL";
    public static final String FLD_MEANING_VI_TTS = "MEANING_VI_TTS";

    public static final String FLD_MEMO = "MEMO";
    public static final String FLD_MEMO_BEFORE = "MEMO_BEFORE";
    public static final String FLD_MEMO_AFTER = "MEMO_AFTER";
    public static final String FLD_MESSAGE = "MESSAGE";
    public static final String FLD_MUST_INCLUDE = "MUST_INCLUDE";

    public static final String FLD_NATION = "NATION";
    public static final String FLD_NATIVE_SPEAKER_ID = "NATIVE_SPEAKER_ID";
    public static final String FLD_NAME = "NAME";
    public static final String FLD_NAME_STUDY_LANG = "NAME_STUDY_LANG";
    public static final String FLD_NAME_ITEM = "NAME_ITEM";
    public static final String FLD_NAME_ITEM_SUB = "NAME_ITEM_SUB";
    public static final String FLD_NAME_ITEM_SUB_DESC = "NAME_ITEM_SUB_DESC";
    public static final String FLD_NAME_SHOP = "NAME_SHOP";

    public static final String FLD_NAME_AR = "NAME_AR";
    public static final String FLD_NAME_BN = "NAME_BN";
    public static final String FLD_NAME_CH_S = "NAME_CH_S";
    public static final String FLD_NAME_CH_T = "NAME_CH_T";
    public static final String FLD_NAME_CS = "NAME_CS";
    public static final String FLD_NAME_DA = "NAME_DA";
    public static final String FLD_NAME_DE = "NAME_DE";
    public static final String FLD_NAME_EL = "NAME_EL";
    public static final String FLD_NAME_ENG = "NAME_ENG";
    public static final String FLD_NAME_ES = "NAME_ES";
    public static final String FLD_NAME_FI = "NAME_FI";
    public static final String FLD_NAME_FR = "NAME_FR";
    public static final String FLD_NAME_HE = "NAME_HE";
    public static final String FLD_NAME_HI = "NAME_HI";
    public static final String FLD_NAME_HR = "NAME_HR";
    public static final String FLD_NAME_HU = "NAME_HU";
    public static final String FLD_NAME_ID = "NAME_ID";
    public static final String FLD_NAME_IT = "NAME_IT";
    public static final String FLD_NAME_JP = "NAME_JP";
    public static final String FLD_NAME_KO = "NAME_KO";
    public static final String FLD_NAME_NL = "NAME_NL";
    public static final String FLD_NAME_NO = "NAME_NO";
    public static final String FLD_NAME_PL = "NAME_PL";
    public static final String FLD_NAME_PT = "NAME_PT";
    public static final String FLD_NAME_RO = "NAME_RO";
    public static final String FLD_NAME_RU = "NAME_RU";
    public static final String FLD_NAME_SK = "NAME_SK";
    public static final String FLD_NAME_SV = "NAME_SV";
    public static final String FLD_NAME_TH = "NAME_TH";
    public static final String FLD_NAME_TR = "NAME_TR";
    public static final String FLD_NAME_UK = "NAME_UK";
    public static final String FLD_NAME_VI = "NAME_VI";
    public static final String FLD_OPPONENT_UID = "OPPONENT_UID";
    public static final String FLD_OPPONENT_NAME = "OPPONENT_NAME";
    public static final String FLD_OPPONENT_DESC = "OPPONENT_DESC";
    public static final String FLD_ORIGINAL_ID = "ORIGINAL_ID";
    public static final String FLD_ORIGINAL_ID_BEFORE = "ORIGINAL_ID_BEFORE";
    public static final String FLD_ORIGINAL_ID_AFTER = "ORIGINAL_ID_AFTER";

    public static final String FLD_PASSWORD = "PWD";
    public static final String FLD_POINT_AMKI = "POINT_AMKI";
    public static final String FLD_POINT_READING = "POINT_READING";
    public static final String FLD_POINT = "POINT";
    public static final String FLD_POINT_TYPE = "POINT_TYPE";
    public static final String FLD_POSOFWORD = "POSOFWORD";
    public static final String FLD_PRICE = "PRICE";
    public static final String FLD_PRICE_FIXED = "PRICE_FIXED";
    public static final String FLD_PRICE_RANGE = "PRICE_RANGE";
    public static final String FLD_PRODUCT_ID = "PRODUCT_ID";
    public static final String FLD_POSALL = "POSALL";
    // public static final String FLD_POS = "POS";
    // public static final String FLD_POS2 = "POS2";
    // public static final String FLD_POS3 = "POS3";
    // public static final String FLD_POS4 = "POS4";
    // public static final String FLD_POS5 = "POS5"; //일본어는 Conjugation_Type을 저장
    // public static final String FLD_POS6 = "POS6"; //일본어는 Conjugation_Form을 저장
    public static final String FLD_PRONOUNCE = "PRONOUNCE";
    public static final String FLD_PRONOUNCE_CH_S = "PRONOUNCE_CH_S";
    public static final String FLD_PRONOUNCE_JP = "PRONOUNCE_JP";
    public static final String FLD_PRONOUNCE_PREVIOUS = "PRONOUNCE_PREVIOUS";
    public static final String FLD_PRONOUNCE_USE = "PRONOUNCE_USE"; // 0이면 형태소분석기의 발음을 사용. 1이면 DB의 발음을 사용.
    public static final String FLD_PRONOUNCE1 = "PRONOUNCE1"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_PRONOUNCE1_FIRST = "PRONOUNCE1_FIRST"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_PRONOUNCE2 = "PRONOUNCE2"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_PRONOUNCE2_FIRST = "PRONOUNCE2_FIRST"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_PRONOUNCE3 = "PRONOUNCE3"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_PRONOUNCE3_FIRST = "PRONOUNCE3_FIRST"; // HANJA사전에서 사용하는 필드..
    public static final String FLD_PRONOUNCE_AFTER = "PRONOUNCE_AFTER";
    public static final String FLD_PRONOUNCE_BEFORE = "PRONOUNCE_BEFORE";

    public static final String FLD_LEFTCOMPONENT = "LEFTCOMPONENT";
    public static final String FLD_RIGHTCOMPONENT = "RIGHTCOMPONENT";
    public static final String FLD_STROKES = "STROKES";
    public static final String FLD_RADICAL = "RADICAL";
    public static final String FLD_PINYIN = "PINYIN";
    public static final String FLD_KUNYOMI = "KUNYOMI";
    public static final String FLD_ONYOMI = "ONYOMI";


    public static final String FLD_PWD = "PWD";
    public static final String FLD_RANK = "RANK";
    public static final String FLD_RE_RECORDED = "RE_RECORDED";
    public static final String FLD_RECORD_LESSON = "RECORD_LESSON";

    public static final String FLD_ROLEPLAYING_TYPE = "ROLEPLAYING_TYPE"; //이거는 TBL_MESSAGE에 있는건데 향후 안쓸 예정임.
    public static final String FLD_ROLE_PLAYING_ID = "ROLE_PLAYING_ID";

    //	public static final String FLD_ROLE_PLAYING_CATEGORY_ID = "ROLE_PLAYING_CATEGORY_ID";
//	public static final String FLD_ROLE_PLAYING_CATEGORY_PARENT_ID = "ROLE_PLAYING_CATEGORY_PARENT_ID";
    public static final String FLD_CONVERSATION_ID = "CONVERSATION_ID";
    public static final String FLD_ROLE_PLAYING_TYPE = "ROLE_PLAYING_TYPE";
    public static final String FLD_ROLE_PLAYING_TYPE_MINOR = "ROLE_PLAYING_TYPE_MINOR";
    public static final String FLD_ROOM_EXPIRED = "ROOM_EXPIRED";
    public static final String FLD_ROOM_OPEN = "ROOM_OPEN";
    public static final String FLD_PRACTICE_DIALOG_TYPE = "PRACTICE_DIALOG_TYPE";
    public static final String FLD_REG_DATE = "REG_DATE";

    public static final String FLD_SAME_MOTHER_TONGUE_LIMIT = "SAME_MOTHER_TONGUE_LIMIT";

    public static final String FLD_SEARCH_HISTORY = "SEARCH_HISTORY";
    public static final String FLD_SEND_DATE = "SEND_DATE";
    public static final String FLD_SENDER_ID = "SENDER_ID";
    public static final String FLD_SENTENCE_ID_LIST = "SENTENCE_ID_LIST";
    public static final String FLD_SEX = "SEX";

    public static final String FLD_SHARE_MY_RECORDING = "SHARE_MY_RECORDING";
    public static final String FLD_SHOW_MEANING = "SHOW_MEANING";
    public static final String FLD_SHOW_PRONOUNCE = "SHOW_PRONOUNCE";
    public static final String FLD_SMALL_TALKING = "SMALL_TALKING";
    public static final String FLD_SOLVING_TIME = "SOLVING_TIME";
    public static final String FLD_SPEAKING_SPEED = "SPEAKING_SPEED";
    public static final String FLD_COUNT = "COUNT";

    public static final String FLD_COUNT_OF_PHRASES_TO_STUDY_AT_ONCE = "COUNT_OF_PHRASES_TO_STUDY_AT_ONCE";
    public static final String FLD_ID_IN_SERVER_VOCABOOK = "ID_IN_SERVER_VOCABOOK";
    public static final String FLD_STUDY_ORDER_HIDE_PART_OF_WORDS = "STUDY_ORDER_HIDE_PART_OF_WORDS";
    public static final String FLD_STUDY_ORDER_HIDE_ALL_PHRASES = "STUDY_ORDER_HIDE_ALL_PHRASES";
    public static final String FLD_STUDY_ORDER_RANDOM_QUESTIONS = "STUDY_ORDER_RANDOM_QUESTIONS";

    public static final String FLD_READING_SPEED = "READING_SPEED";


    public static final String FLD_STATUS = "STATUS";
    public static final String FLD_STARTED = "STARTED";
    public static final String FLD_START_TIME = "START_TIME";
    public static final String FLD_STUDIED = "STUDIED";
    public static final String FLD_STUDY_COUNT = "STUDY_COUNT";
    public static final String FLD_STUDY_DATE = "STUDY_DATE";
    public static final String FLD_STUDY_ROLE = "STUDY_ROLE";
    public static final String FLD_STUDY_ROLE_MAIN = "STUDY_ROLE_MAIN";

    public static final String FLD_VOCA_NLP_PARSED = "VOCA_NLP_PARSED";

    public static final String FLD_STUDY_MODE = "STUDY_MODE";
    public static final String FLD_STUDENT_ID = "STUDENT_ID";
    public static final String FLD_STUDENT_NAME = "STUDENT_NAME";
    public static final String FLD_STUDENT_JOINED = "STUDENT_JOINED";
    public static final String FLD_STUDENT_JOINED_PHONE = "STUDENT_JOINED_PHONE";
    public static final String FLD_STUDENT_JOINED_TIME = "STUDENT_JOINED_TIME";
    public static final String FLD_STUDENT_JOINED_PHONE_TIME = "STUDENT_JOINED_PHONE_TIME";
    public static final String FLD_STUDENT_FINISHED_TIME = "STUDENT_FINISHED_TIME";
    public static final String FLD_STUDENT_FINISHED_PHONE_TIME = "STUDENT_FINISHED_PHONE_TIME";

    public static final String FLD_STUDENT_CHECK_DATE = "STUDENT_CHECK_DATE";
    public static final String FLD_STUDENT_STUDYMODE_HISTORY_ID = "STUDENT_STUDYMODE_HISTORY_ID";
    public static final String FLD_TBL_NAME = "TBL_NAME";
    public static final String FLD_TERM_ENG_ABBR = "TERM_ENG_ABBR";
    public static final String FLD_TERM_ENG_ABBR_BEFORE = "TERM_ENG_ABBR_BEFORE";
    public static final String FLD_TERM_ENG_ABBR_AFTER = "TERM_ENG_ABBR_AFTER";
    public static final String FLD_TERM_ENG_FULL = "TERM_ENG_FULL";
    public static final String FLD_TERM_ENG_FULL_BEFORE = "TERM_ENG_FULL_BEFORE";
    public static final String FLD_TERM_ENG_FULL_AFTER = "TERM_ENG_FULL_AFTER";
    public static final String FLD_TERM_KO_TITLE = "TERM_KO_TITLE";
    public static final String FLD_TERM_KO_TITLE_BEFORE = "TERM_KO_TITLE_BEFORE";
    public static final String FLD_TERM_KO_TITLE_AFTER = "TERM_KO_TITLE_AFTER";
    public static final String FLD_TERM_HANJA_TITLE = "TERM_HANJA_TITLE";
    public static final String FLD_TERM_HANJA_TITLE_BEFORE = "TERM_HANJA_TITLE_BEFORE";
    public static final String FLD_TERM_HANJA_TITLE_AFTER = "TERM_HANJA_TITLE_AFTER";
    public static final String FLD_TERM_KO_SHORT = "TERM_KO_SHORT";
    public static final String FLD_TERM_KO_SHORT_BEFORE = "TERM_KO_SHORT_BEFORE";
    public static final String FLD_TERM_KO_SHORT_AFTER = "TERM_KO_SHORT_AFTER";
    public static final String FLD_TERM_KO_FULL = "TERM_KO_FULL";
    public static final String FLD_TERM_KO_FULL_BEFORE = "TERM_KO_FULL_BEFORE";
    public static final String FLD_TERM_KO_FULL_AFTER = "TERM_KO_FULL_AFTER";

    public static final String FLD_TITLE = "TITLE";
    public static final String FLD_TMDB_ID = "TMDB_ID";
    public static final String FLD_TMDB_TYPE = "TMDB_TYPE";


    public static final String FLD_ONLINE_DB_SOURCE = "ONLINE_DB_SOURCE";
    public static final String FLD_ONLINE_DB_ID = "ONLINE_DB_ID";
    public static final String FLD_ONLINE_DB_MEDIA_TYPE = "ONLINE_DB_MEDIA_TYPE";
    public static final String FLD_ONLINE_DB_TITLE = "ONLINE_DB_TITLE";
    public static final String FLD_RELEASE_DATE = "RELEASE_DATE";
    public static final String FLD_ONLINE_DB_POSTER_FILENAME = "ONLINE_DB_POSTER_FILENAME";

    public static final String FLD_TERM_LEVEL = "TERM_LEVEL";
    public static final String FLD_TERM_LEVEL_BEFORE = "TERM_LEVEL_BEFORE";
    public static final String FLD_TERM_LEVEL_AFTER = "TERM_LEVEL_AFTER";
    public static final String FLD_TOKEN_FIREBASE = "TOKEN_FIREBASE";
    public static final String FLD_TOKEN_VOIP = "TOKEN_VOIP";
    public static final String FLD_TOPIC_BEGIN_INDEX = "TOPIC_BEGIN_INDEX";
    public static final String FLD_TOPIC_REPEATED_COUNT = "TOPIC_REPEATED_COUNT";

    public static final String FLD_TUTOR_ID = "TUTOR_ID";
    public static final String FLD_TUTOR_JOINED = "TUTOR_JOINED";
    public static final String FLD_TUTOR_JOINED_PHONE = "TUTOR_JOINED_PHONE";
    public static final String FLD_TUTOR_JOINED_TIME = "TUTOR_JOINED_TIME";
    public static final String FLD_TUTOR_JOINED_PHONE_TIME = "TUTOR_JOINED_PHONE_TIME";
    public static final String FLD_TUTOR_FINISHED_TIME = "TUTOR_FINISHED_TIME";
    public static final String FLD_TUTOR_FINISHED_PHONE_TIME = "TUTOR_FINISHED_PHONE_TIME";
    public static final String FLD_TUTOR_NAME = "TUTOR_NAME";
    public static final String FLD_TYPE = "TYPE";
    public static final String FLD_UPDATABLE = "UPDATABLE";
    public static final String FLD_URL_TYPE = "URL_TYPE";
    public static final String FLD_USED = "USED";
    public static final String FLD_USE_ALPHABET = "USE_ALPHABET";
    public static final String FLD_USE_RECORDING = "USE_RECORDING";
    public static final String FLD_USE_SERVER_VOCABOOK_DATA = "USE_SERVER_VOCABOOK_DATA";

    public static final String FLD_USE_SHOP_NAME_FOR_RANDOM_VALUE = "USE_SHOP_NAME_FOR_RANDOM_VALUE";
    public static final String FLD_USE_PRACTICE = "USE_PRACTICE";
    public static final String FLD_USE_TODAY_EXPRESSION = "USE_TODAY_EXPRESSION";
    public static final String FLD_USE_ROLE_PLAYING_RANDOM_VALUE = "USE_ROLE_PLAYING_RANDOM_VALUE";
    public static final String FLD_USE_VOCABOOK = "USE_VOCABOOK";
    public static final String FLD_USER_COUNT = "USER_COUNT";
    public static final String FLD_USER_NAME = "USER_NAME";
    public static final String FLD_USER_ROLE = "USER_ROLE";
    public static final String FLD_USER_TYPE = "USER_TYPE";
    public static final String FLD_USER_ID = "USER_ID";
    public static final String FLD_USER_WORD_EXAM_TYPE = "USER_WORD_EXAM_TYPE";
    public static final String FLD_UUID = "UUID";
    public static final String FLD_VALUE = "VALUE";
    public static final String FLD_VALUE_PREFIX = "VALUE_PREFIX";
    public static final String FLD_VALUE_SUFFIX = "VALUE_SUFFIX";
    public static final String FLD_VALUE_DESC = "VALUE_DESC";
    public static final String FLD_VERSION = "VERSION";
    public static final String FLD_VOCA = "VOCA";
    public static final String FLD_VOCAORI = "VOCAORI";
    public static final String FLD_VOCAORI_ID = "VOCAORI_ID";
    public static final String FLD_VOCABOOKS_CELL_INDEX = "VOCABOOKS_CELL_INDEX";
    public static final String FLD_VOCABOOKS_ID = "VOCABOOKS_ID";
    public static final String FLD_VOCA_CH_S = "VOCA_CH_S";
    public static final String FLD_VOCA_JP = "VOCA_JP";
    public static final String FLD_VOCA_ID = "VOCA_ID";
    public static final String FLD_VOCA_ID_ORI = "VOCA_ID_ORI";
    public static final String FLD_VOCA_INFOONLY = "VOCA_INFOONLY";
    public static final String FLD_VOCA_COUNT = "VOCA_COUNT";
    public static final String FLD_VOCA_DISPLAY = "VOCA_DISPLAY";
    public static final String FLD_VOCA_TTS = "VOCA_TTS";
    public static final String FLD_VOCA_TYPE = "VOCA_TYPE";
    public static final String FLD_VOCA_TYPE_ORI = "VOCA_TYPE_ORI";
    public static final String FLD_WORD = "WORD";
    public static final String FLD_WORD_COUNT = "WORD_COUNT";
    public static final String FLD_WORD_DISPLAY = "WORD_DISPLAY";
    public static final String FLD_WORD_ID = "WORD_ID";
    public static final String FLD_WORD_ID_LIST = "WORD_ID_LIST";
    public static final String FLD_WORD_TTS = "WORD_TTS";
    public static final String FLD_WORDORI = "WORDORI";
    public static final String FLD_WORDORI_ID = "WORDORI_ID";
    public static final String FLD_WORDORI_ID_LIST = "WORDORI_ID_LIST";
    public static final String FLD_WORDLEVEL = "WORDLEVEL";
    public static final String FLD_VOCA_LEVEL = "VOCA_LEVEL";
    public static final String FLD_WORD_SAME_ID = "WORD_SAME_ID";
    public static final String FLD_WORDWITHPOS = "WORDWITHPOS"; // dalbook에서 사용하는 필드

    public static final String FLD_UID = "UID";
    public static final String FLD_UPDATE_DATE = "UPDATE_DATE";
    public static final String FLD_URL = "URL";
    public static final String FLD_URL_BEFORE = "URL_BEFORE";
    public static final String FLD_URL_AFTER = "URL_AFTER";

    public static final String FLD_USE_ALL_QUIZ = "USE_ALL_QUIZ";
    public static final String FLD_USE_BOOK_QUIZ = "USE_BOOK_QUIZ";
    public static final String FLD_USE_VIDEO_QUIZ = "USE_VIDEO_QUIZ";



    public static final String FLD_TEMP_countTemp = "countTemp";
    /** 언어들 */
    public static final String LOCAL_SMI_EN = "EN";
    public static final String LOCAL_SMI_KR = "KR";
    // public static final String LOCAL_SMI_EN = "EN";
    // public static final String LOCAL_SMI_EN = "EN";
    // local..
    public static final String LOCAL_LANG_EN = "en";
    public static final String LOCAL_LANG_JP = "jp";
    public static final String LOCAL_LANG_KO = "ko";
    public static final String LOCAL_LANG_ZH_CN = "zh_CN";
    public static final String LOCAL_LANG_VI = "vi";

    public static final String LANG_AR = "ARABIC";
    public static final String LANG_BN = "BENGALI";
    public static final String LANG_CH = "CHINESE";
    public static final String LANG_CH_S = "CHINESE_SIMPLIFIED";
    public static final String LANG_CH_T = "CHINESE_TRADITIONAL";
    public static final String LANG_CS = "CZECH";
    public static final String LANG_DA = "DANISH";
    public static final String LANG_DE = "GERMAN";
    public static final String LANG_EL = "GREEK";
    public static final String LANG_EN = "ENGLISH";
    public static final String LANG_ES = "SPANISH";
    public static final String LANG_FI = "FINNISH";
    public static final String LANG_FR = "FRENCH";
    public static final String LANG_HANJA = "HANJA";
    public static final String LANG_HE = "HEBREW";
    public static final String LANG_HI = "HINDI";
    public static final String LANG_HR = "CROATIAN";
    public static final String LANG_HU = "HUNGARIAN";
    public static final String LANG_ID = "INDONESIAN";
    public static final String LANG_IT = "ITALIAN";
    public static final String LANG_JP = "JAPANESE";
    public static final String LANG_KO = "KOREAN";
    public static final String LANG_NL = "DUTCH";
    public static final String LANG_NO = "NORWEGIAN";
    public static final String LANG_PL = "POLISH";
    public static final String LANG_PT = "PORTUGUESE";
    public static final String LANG_RO = "ROMANIAN";
    public static final String LANG_RU = "RUSSIAN";
    public static final String LANG_SK = "SLOVAK";
    public static final String LANG_SV = "SWEDISH";
    public static final String LANG_TH = "THAI";
    public static final String LANG_TR = "TURKISH";
    public static final String LANG_UK = "UKRAINIAN";
    public static final String LANG_VI = "VIETNAMESE";

    public static final String LANG_JSP_AR = "العربية"; // 영어가 아니고 그나라 문자로 표기한것
    public static final String LANG_JSP_BN = "বাংলা";
    public static final String LANG_JSP_CH_S = "简体中文";
    public static final String LANG_JSP_CH_T = "繁體中文";
    public static final String LANG_JSP_CS = "čeština";
    public static final String LANG_JSP_DA = "Dansk";
    public static final String LANG_JSP_DE = "Deutsch";
    public static final String LANG_JSP_EL = "Ελληνικά";
    public static final String LANG_JSP_EN = "ENGLISH";
    public static final String LANG_JSP_ES = "Español";
    public static final String LANG_JSP_FI = "Suomi";
    public static final String LANG_JSP_FR = "français";
    public static final String LANG_JSP_HE = "עברית";
    public static final String LANG_JSP_HI = "हिन्दी";
    public static final String LANG_JSP_HR = "Hrvatski";
    public static final String LANG_JSP_HU = "Magyar";
    public static final String LANG_JSP_ID = "BahasaIndonesia";
    public static final String LANG_JSP_IT = "Italiano";
    public static final String LANG_JSP_JP = "日本語";
    public static final String LANG_JSP_KO = "한국어";
    public static final String LANG_JSP_NL = "Nederlands";
    public static final String LANG_JSP_NO = "Norsk";
    public static final String LANG_JSP_PL = "Polski";
    public static final String LANG_JSP_PT = "Português";
    public static final String LANG_JSP_RO = "Română";
    public static final String LANG_JSP_RU = "Русский";
    public static final String LANG_JSP_SK = "slovenčina";
    public static final String LANG_JSP_SV = "Svenska";
    public static final String LANG_JSP_TH = "ภาษาไทย";
    public static final String LANG_JSP_TR = "Türkçe";
    public static final String LANG_JSP_UK = "українець";
    public static final String LANG_JSP_VI = "TiếngViệt";

    public static final Integer LANGCODE_NONE = 0;
    public static final Integer LANGCODE_CH_S = 1;
    public static final Integer LANGCODE_CH_T = 2;
    public static final Integer LANGCODE_KO = 3;
    public static final Integer LANGCODE_JP = 4;
    public static final Integer LANGCODE_RU = 5;
    public static final Integer LANGCODE_FR = 6;
    public static final Integer LANGCODE_AR = 7;
    public static final Integer LANGCODE_BN = 8;
    public static final Integer LANGCODE_HR = 9;
    public static final Integer LANGCODE_CS = 10;
    public static final Integer LANGCODE_DA = 11;
    public static final Integer LANGCODE_NL = 12;
    public static final Integer LANGCODE_EN = 13;
    public static final Integer LANGCODE_FI = 14;
    public static final Integer LANGCODE_DE = 15;
    public static final Integer LANGCODE_EL = 16;
    public static final Integer LANGCODE_HE = 17;
    public static final Integer LANGCODE_HI = 18;
    public static final Integer LANGCODE_HU = 19;
    public static final Integer LANGCODE_ID = 20;
    public static final Integer LANGCODE_IT = 21;
    public static final Integer LANGCODE_NO = 22;
    public static final Integer LANGCODE_PL = 23;
    public static final Integer LANGCODE_PT = 24;
    public static final Integer LANGCODE_RO = 25;
    public static final Integer LANGCODE_SK = 26;
    public static final Integer LANGCODE_ES = 27;
    public static final Integer LANGCODE_SV = 28;
    public static final Integer LANGCODE_TH = 29;
    public static final Integer LANGCODE_TR = 30;
    public static final Integer LANGCODE_UK = 31;
    public static final Integer LANGCODE_VI = 32;
    public static final Integer LANGCODE_HANJA = 33;

    public static final String LANG_SMI_CLASS_DEFAULT = "DEFAULT";
    public static final String LANG_SMI_CLASS_ENGLISH_EN = "EN";
    public static final String LANG_SMI_CLASS_ENGLISH_ENCC = "ENCC";
    public static final String LANG_SMI_CLASS_ENGLISH_EGCC = "EGCC";
    public static final String LANG_SMI_CLASS_KOREAN_KR = "KR";
    public static final String LANG_SMI_CLASS_KOREAN_KOR = "KOR";
    public static final String LANG_SMI_CLASS_KOREAN_KRCC = "KRCC";
    public static final String LANG_SMI_CLASS_JAPANESE_JP = "JP";
    public static final String LANG_SMI_CLASS_JAPANESE_JPCC = "JPCC";
    public static final String LANG_SMI_CLASS_JAPANESE_JA = "JA";
    public static final String LANG_SMI_CLASS_JAPANESE_JACC = "JACC";
    public static final String LANG_SMI_CLASS_CHINESE_CH_S = "CH";
    public static final String LANG_SMI_CLASS_CHINESE_CHCC = "CHCC";

    /** KEY값들 */
    public static final String KEY_CORRECT_ANSWER_COUNT = "CORRECT_ANSWER_COUNT";
    public static final String KEY_CORRECT_ANSWER_COUNT_TOTAL = "CORRECT_ANSWER_COUNT_TOTAL";
    public static final String KEY_WRONG_ANSWER_COUNT = "WRONG_ANSWER_COUNT";
    public static final String KEY_WRONG_ANSWER_COUNT_TOTAL = "WRONG_ANSWER_COUNT_TOTAL";


    public static final String KEY_ACCESS_COUNT = "ACCESS_COUNT";
    public static final String KEY_ACCESS_DATE = "ACCESS_DATE";
    //	public static final String KEY_ACCESS_DATE_TS = "ACCESS_DATE_TS";
    public static final String KEY_AGE = "AGE";
    //	public static final String KEY_ANSWERS = "ANSWERS";
    public static final String KEY_ANSWER_1 = "ANSWER_1";
    public static final String KEY_ANSWER_2 = "ANSWER_2";
    public static final String KEY_ANSWER_3 = "ANSWER_3";
    public static final String KEY_ANSWER_4 = "ANSWER_4";
    public static final String KEY_APP_TYPE = "APP_TYPE";
    public static final String KEY_SHOW_TOAST = "SHOW_TOAST";
    public static final String KEY_SHOW_ORIGINAL_VOCA = "SHOW_ORIGINAL_VOCA";

    public static final String KEY_LIST_IS_RIGHT_SUBTITLE = "LIST_IS_RIGHT_SUBTITLE";
    public static final String KEY_SUBTITLE_FILE_LANG = "SUBTITLE_FILE_LANG";

    public static final String KEY_SOLVING_TIME = "SOLVING_TIME";
    public static final String KEY_IS_FINISH = "IS_FINISH";

    public static final String KEY_STUDENT_AGE = "STUDENT_AGE";
    public static final String KEY_STUDENT_langNative = "STUDENT_langNative";

    public static final String KEY_COUNT_OF_PHRASES_TO_STUDY_AT_ONCE = "COUNT_OF_PHRASES_TO_STUDY_AT_ONCE";
    public static final String KEY_STUDY_ORDER_HIDE_PART_OF_WORDS = "STUDY_ORDER_HIDE_PART_OF_WORDS";
    public static final String KEY_STUDY_ORDER_HIDE_ALL_PHRASES = "STUDY_ORDER_HIDE_ALL_PHRASES";
    public static final String KEY_STUDY_ORDER_RANDOM_QUESTIONS = "STUDY_ORDER_RANDOM_QUESTIONS";


    public static final String KEY_TUTOR_AGE = "TUTOR_AGE";
    public static final String KEY_TUTOR_langNative = "TUTOR_langNative";

    public static final String KEY_APP_NAME = "APP_NAME";
    public static final String KEY_ACCESS_OR_EXIT_APP = "ACCESS_OR_EXIT_APP";
    public static final String KEY_ALL_NATIVE_SPEAKERS = "ALL_NATIVE_SPEAKERS";
    public static final String KEY_ALLOW_CHAT = "ALLOW_CHAT";
    public static final String KEY_ALLOW_PUSH = "ALLOW_PUSH";
//	public static final String KEY_AMKI_GRADE = "AMKI_GRADE";
//	public static final String KEY_ORDER_KNOW_AND_AMKI_GRADE = "ORDER_KNOW_AND_AMKI_GRADE";

    public static final String KEY_BELONG_TO_VOCABOOK = "BELONG_TO_VOCABOOK";
    public static final String KEY_BIO = "BIO";
    public static final String KEY_BRANCH = "BRANCH";
    public static final String KEY_BRANCH_SELECTED = "BRANCH_SELECTED";

    public static final String KEY_CHATROOM_ID = "CHATROOM_ID";
    public static final String KEY_CHATROOM_NAME = "CHATROOM_NAME";
    public static final String KEY_CHATROOM_TYPE = "CHATROOM_TYPE";
    //	public static final String KEY_CHATROOM_ALL_USER_COUNT = "CHATROOM_ALL_USER_COUNT";
//	public static final String KEY_CHATROOM_ALL_USER_INFO = "CHATROOM_ALL_USER_INFO";
    public static final String KEY_CLASS_LIST = "CLASS_LIST";
    public static final String KEY_CLASS_LIST_COUNT = "CLASS_LIST_COUNT";
    public static final String KEY_CLASS_LIST_ID = "CLASS_LIST_ID";
    public static final String KEY_CLASS_GRADE = "CLASS_GRADE";
    public static final String KEY_CLASS_NUMBER = "CLASS_NUMBER";
    public static final String KEY_CLASS_PRESIDENTS_COUNT = "CLASS_PRESIDENTS_COUNT";
    public static final String KEY_CLASS_STUDENTS_COUNT = "CLASS_STUDENTS_COUNT";
    public static final String KEY_CLASS_PRESIDENTS = "CLASS_PRESIDENTS";
    public static final String KEY_CLASS_STUDENTS = "CLASS_STUDENTS";

    public static final String KEY_CLIENT_TYPE = "CLIENT_TYPE";
    public static final String KEY_OS_TYPE = "OS_TYPE";
    public static final String KEY_CONFIRM = "CONFIRM";
    public static final String KEY_CORRECT_ANSWER_NUMBER = "CORRECT_ANSWER_NUMBER";
    public static final String KEY_COUNT_OF_ORDER_FROM_CHECKED_MENU = "COUNT_OF_ORDER_FROM_CHECKED_MENU";

    public static final String KEY_MAKE_NEW_EXAM = "MAKE_NEW_EXAM";
    public static final String KEY_MANAGE_USER_IN_LESSON = "MANAGE_USER_IN_LESSON";
    public static final String KEY_MESSAGE = "MESSAGE";
    public static final String KEY_MESSAGE_MODIFIED = "MESSAGE_MODIFIED";
    public static final String KEY_MESSAGE_ID = "MESSAGE_ID";
    public static final String KEY_MANAGE_RECORD = "MANAGE_RECORD";

    public static final String KEY_PARENT_WORDBOOK_ID = "PARENT_WORDBOOK_ID";
    public static final String KEY_PRACTICE_ROOM_OPEN_TYPE = "PRACTICE_ROOM_OPEN_TYPE";

    public static final String KEY_PN_TYPE = "PN_TYPE";
    public static final String KEY_PN_MINUTES_BEFORE_BEGIN_LESSON = "PN_MINUTES_BEFORE_BEGIN_LESSON";
    public static final String KEY_PN_MINUTES_BEFORE_FINISH_LESSON = "PN_MINUTES_BEFORE_FINISH_LESSON";

    public static final String KEY_PUSH_apns = "apns";
    public static final String KEY_PUSH_aps= "aps";
    public static final String KEY_PUSH_apns_priority = "apns-priority";
    public static final String KEY_PUSH_apns_push_type = "apns-push-type";
    public static final String KEY_PUSH_alert = "alert";
    public static final String KEY_PUSH_background = "background";
    public static final String KEY_PUSH_body = "body";
    public static final String KEY_PUSH_content_available = "content_available";
    public static final String KEY_PUSH_data = "data";
    public static final String KEY_PUSH_headers = "headers";
    public static final String KEY_PUSH_notification = "notification";
    public static final String KEY_PUSH_payload = "payload";
    public static final String KEY_PUSH_priority = "priority";
    public static final String KEY_PUSH_title = "title";
    public static final String KEY_PUSH_to = "to";


    public static final String KEY_PUSH_CALL_ROOMNAME = "CALL_ROOMNAME";
    public static final String KEY_PUSH_CALL_TYPE = "CALL_TYPE";
    public static final String KEY_PUSH_CALLER_UID = "CALLER_UID";
    public static final String KEY_PUSH_CALLER_USERNAME= "CALLER_USERNAME";
    public static final String KEY_PUSH_OPPONENT_UID = "OPPONENT_UID";
    public static final String KEY_PUSH_OPPONENT_USERNAME= "OPPONENT_USERNAME";
    //	public static final String KEY_IS_CALLER_FIRST_JOINED_USER= "IS_CALLER_FIRST_JOINED_USER";
    public static final String KEY_IS_FIRST_JOINED_USER= "IS_FIRST_JOINED_USER";

//	public static final String KEY_PUSH_CONTENT_AVAILABLE= "content_available";

    public static final String KEY_PUSH_METHOD_NAME = "METHOD_NAME";

    public static final String KEY_HAS_MEANING = "HAS_MEANING";
    public static final String KEY_HAS_VOICE_FILE = "HAS_VOICE_FILE";
    public static final String KEY_HAS_INTRODUCTION_FILE = "HAS_INTRODUCTION_FILE";
    public static final String KEY_SHOW_SENTENCE = "SHOW_SENTENCE";
    public static final String KEY_SHOW_ASTERISK = "SHOW_ASTERISK";

    public static final String KEY_FAKE_DATA_SOURCE = "FAKE_DATA_SOURCE";
    public static final String KEY_FAKE_DATA_SOURCE_MIN_LVL_FOR_SQL = "FAKE_DATA_SOURCE_MIN_LVL_FOR_SQL";

    public static final String KEY_FAKER_CATEGORY_A = "FAKER_CATEGORY_A";
    public static final String KEY_FAKER_CATEGORY_B = "FAKER_CATEGORY_B";
    public static final String KEY_FAKER_CATEGORY_C = "FAKER_CATEGORY_C";
    public static final String KEY_FAKER_INPUT_TYPES = "FAKER_INPUT_TYPES";
    public static final String KEY_FAKER_INPUT_PARAMETERS = "FAKER_INPUT_PARAMETERS";
    public static final String KEY_FAKER_OUTPUT_COUNT = "FAKER_OUTPUT_COUNT";
    public static final String KEY_FAKER_RETURN_TYPE = "FAKER_RETURN_TYPE";


    public static final String KEY_FILE_NAME = "FILE_NAME";
    public static final String KEY_FILE_VERSION = "FILE_VERSION";
    public static final String KEY_FILESIZE = "FILESIZE";
    public static final String KEY_FILE_NAME_VOICE = "FILE_NAME_VOICE";
    public static final String KEY_FINISH_TIME = "FINISH_TIME";
    public static final String KEY_FIRST_DAY_OF_MONTH = "FIRST_DAY_OF_MONTH";
    public static final String KEY_YEAR_MONTH = "YEAR_MONTH";
    public static final String KEY_FOLLOWING_COUNT = "FOLLOWING_COUNT";
    public static final String KEY_FOLLOWING_ETC_TYPE = "FOLLOWING_ETC_TYPE";
    public static final String KEY_FOLLOWER_COUNT = "FOLLOWER_COUNT";
    public static final String KEY_FREE_TALKING = "FREE_TALKING";

    public static final String KEY_SCORE = "SCORE";
    public static final String KEY_SUBTITLE_CORRECT_FORMAT_TO_INSERT_DIC_SENTENCE = "SUBTITLE_CORRECT_FORMAT";
    public static final String KEY_SHARE_MY_RECORDING = "SHARE_MY_RECORDING";
    public static final String KEY_SMALL_TALKING = "SMALL_TALKING";
    public static final String KEY_SORT_TYPE = "SORT_TYPE";
    public static final String KEY_SPEAKING_SPEED = "SPEAKING_SPEED";
    public static final String KEY_READING_SPEED = "READING_SPEED";
    public static final String KEY_RECORD_LESSON = "RECORD_LESSON";
    public static final String KEY_RECORDED_DATE_TS = "RECORDED_DATE_TS";

    public static final String KEY_STUDY_ROLE = "STUDY_ROLE";
    public static final String KEY_STUDY_ROLE_MAIN = "STUDY_ROLE_MAIN";

    public static final String KEY_HAS_IMAGE_FILE = "HAS_IMAGE_FILE";
    public static final String KEY_DEVICE_OS_VERSION = "DEVICE_OS_VERSION";
    public static final String KEY_DEVICE_TYPE = "DEVICE_TYPE";
    public static final String KEY_DIALOG = "DIALOG";
    public static final String KEY_DISPLAY = "DISPLAY";
    public static final String KEY_HIDE = "HIDE";
    public static final String KEY_DISP_ORDER = "DISP_ORDER";
    public static final String KEY_DISP_ORDER_NAME_ITEM = "DISP_ORDER_NAME_ITEM";

    public static final String KEY_DALVOCA_USER_TYPE = "DALVOCA_USER_TYPE";

    public static final String KEY_QUESTION = "QUESTION";
//	public static final String KEY_QUESTIONS = "QUESTIONS";

    public static final String KEY_SEND_PN = "SEND_PN";
    public static final String KEY_SENDER_ID = "SENDER_ID";
    public static final String KEY_SENDER_NAME = "SENDER_NAME";


    public static final String KEY_STUDENT_ID = "STUDENT_ID";
    public static final String KEY_STUDENT_JOINED = "STUDENT_JOINED";
    public static final String KEY_STUDENT_JOINED_PHONE = "STUDENT_JOINED_PHONE";
    public static final String KEY_STUDENT_NAME = "STUDENT_NAME";
    public static final String KEY_STUDENT_STUDY_VOCA_ID = "STUDENT_STUDY_VOCA_ID";

    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_TITLE_RUBY_TEXT = "TITLE_RUBY_TEXT";
    public static final String KEY_TITLE_RUBY_VOCA_LIST = "TITLE_RUBY_VOCA_LIST";


    public static final String KEY_TUTOR_LIST = "TUTOR_LIST";
    public static final String KEY_TUTOR_LIST_COUNT = "TUTOR_LIST_COUNT";
    public static final String KEY_SEARCH_IN_WHERE = "SEARCH_IN_WHERE";
    // public static final String KEY_SEARCH_VOCA_TYPE = "SEARCH_VOCA_TYPE";
    // public static final String KEY_SEARCH_IN_SENTENCE = "SEARCH_IN_SENTENCE";
    public static final String KEY_STR_TO_FIND = "STR_TO_FIND";
    // public static final String KEY_SEARCH_VOCA_EXACT_MATCH =
    // "SEARCH_VOCA_EXACT_MATCH";
    // public static final String KEY_SEARCH_VOCA_FROM_START =
    // "SEARCH_VOCA_FROM_START";
    // public static final String KEY_SEARCH_VOCA_FROM_END = "SEARCH_VOCA_FROM_END";
    public static final String KEY_SEARCH_WITH_REGULAREXPRESSION = "SEARCH_WITH_REGULAREXPRESSION";
    public static final String KEY_SENTENCES = "SENTENCES";
    public static final String KEY_SEND_DATE = "SEND_DATE";
    public static final String KEY_SEND_DATE_TS = "SEND_DATE_TS";
    public static final String KEY_STARTED = "STARTED";
    public static final String KEY_TUTOR_ID = "TUTOR_ID";
    public static final String KEY_TUTOR_JOINED = "TUTOR_JOINED";
    public static final String KEY_TUTOR_JOINED_PHONE = "TUTOR_JOINED_PHONE";
    public static final String KEY_TUTOR_NAME = "TUTOR_NAME";
    public static final String KEY_VALUE = "VALUE";
    public static final String KEY_VALUE_LIST = "VALUE_LIST";
    public static final String KEY_VALUE_RUBY_TEXT = "VALUE_RUBY_TEXT";
    public static final String KEY_VALUE_RUBY_VOCA_LIST = "VALUE_RUBY_VOCA_LIST";
    public static final String KEY_VALUE_PREFIX = "VALUE_PREFIX";
    public static final String KEY_VALUE_SUFFIX = "VALUE_SUFFIX";
    public static final String KEY_VALUE_DESC = "VALUE_DESC";
    public static final String KEY_VOCA_DISP_ORDER = "VOCA_DISP_ORDER";

    public static final String KEY_VOCA_TYPE = "VOCA_TYPE";
    public static final String KEY_VOCA_COUNT_TO_RECORD_ALL = "VOCA_COUNT_TO_RECORD_ALL";
    public static final String KEY_VOCA_COUNT_TO_RECORD_ONLY = "VOCA_COUNT_TO_RECORD_ONLY";
    public static final String KEY_VOCABOOK = "VOCABOOK";
    public static final String KEY_VOCABOOKS_CELL_INDEX = "VOCABOOKS_CELL_INDEX";
    public static final String KEY_VOCABOOK_PARENT_ID = "VOCABOOK_PARENT_ID";
    public static final String KEY_VOCABOOK_PRACTICE_ONLY = "VOCABOOK_PRACTICE_ONLY";
    public static final String KEY_VOCABOOK_ALPHABET_ONLY = "VOCABOOK_ALPHABET_ONLY";
    public static final String KEY_VOICEFILE_TYPE = "VOICEFILE_TYPE";


    public static final String KEY_addInfoAtWord_strResultHTMLTag = "addInfoAtWord_strResultHTMLTag";
    public static final String KEY_addInfoAtWord_existindic = "addInfoAtWord_existindic";
    public static final String KEY_appName = "appName";
    public static final String KEY_APP_VERSION = "APP_VERSION";
    public static final String KEY_BOOKMARK = "BOOKMARK";
    public static final String KEY_blnShowMeaningAsRubyText = "blnShowMeaningAsRubyText";
    public static final String KEY_showMeaningAsRubyText = "showMeaningAsRubyText";

    public static final String KEY_CAN_JOIN_PRACTICE_ROOM = "CAN_JOIN_PRACTICE_ROOM";
    public static final String KEY_CALL_ROOMNAME = "CALL_ROOMNAME";
    public static final String KEY_CALL_TYPE = "CALL_TYPE";
    public static final String KEY_CATEGORY_ID = "CATEGORY_ID";

    public static final String KEY_CELL_ORDER = "CELL_ORDER";
    public static final String KEY_CHANNEL_ID = "CHANNEL_ID";
    public static final String KEY_CITY = "CITY";
    public static final String KEY_clientType = "clientType";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_INPUT_TEXT = "INPUT_TEXT";
    public static final String KEY_RUBY_TEXT = "RUBY_TEXT";
    public static final String KEY_LIST_RUBY_TEXT = "LIST_RUBY_TEXT";

    public static final String KEY_RUBY_VOCA_LIST = "RUBY_VOCA_LIST";

    public static final String KEY_TEXT = "TEXT";
    public static final String KEY_CORRECT_PRONUNCIATION = "CORRECT_PRONUNCIATION";
    public static final String KEY_COUNT_RECORD = "COUNT_RECORD";
    public static final String KEY_CURRENT_URL = "CURRENT_URL";
    public static final String KEY_CURRENCY = "CURRENCY";

    public static final String KEY_CSSSTRING = "CSSString";
    public static final String KEY_DETAIL_INFO_KO = "DETAIL_INFO_KO";
    public static final String KEY_DISPLAY_CONVERSATION = "DISPLAY_CONVERSATION";
    public static final String KEY_ENABLE_LESSON_PN = "ENABLE_LESSON_PN";
    public static final String KEY_ENCODING = "encoding";
    // public static final String KEY_END_NO = "END_NO";
    public static final String KEY_EPUBFILENAMETOBE = "EPUBFILENAMETOBE";
    public static final String KEY_EVALUATE_VOCA_GRADE = "EVALUATE_VOCA_GRADE";
    public static final String KEY_EVALUATE_VOCA_GRADE_TUTORS = "EVALUATE_VOCA_GRADE_TUTORS";

    public static final String KEY_EXAM_TYPE = "EXAM_TYPE";
    public static final String KEY_EXAM_SOURCE = "EXAM_SOURCE";
    public static final String KEY_EXAMPLE_SENTECE_ID = "EXAMPLE_SENTENCE_ID";
    public static final String KEY_EXAMPLE_SENTECE_WORD_DISPLAY = "EXAMPLE_SENTECE_WORD_DISPLAY";
    public static final String KEY_EXAMPLE_SENTECE_MEANING = "EXAMPLE_SENTECE_MEANING";
    public static final String KEY_EXAMPLE_SENTECE_MEANING_DETAILED = "EXAMPLE_SENTECE_MEANING_DETAILED";
    public static final String KEY_EXAMPLE_SENTECE_MEANING_TTS = "EXAMPLE_SENTECE_MEANING_TTS";
    public static final String KEY_EXAMPLE_SENTENCES = "EXAMPLE_SENTENCES";
    public static final String KEY_EXISTIN_SERVERDIC = "EXISTIN_SERVERDIC";
    public static final String KEY_EXTENSION = "EXTENSION";
    //	public static final String KEY_FEEDBACK_SCORE = "FEEDBACK_SCORE";
    public static final String KEY_FEEDBACK_MESSAGE = "FEEDBACK_MESSAGE";
    public static final String KEY_FEEDBACK_DATE = "FEEDBACK_DATE";
    public static final String KEY_FIREBASE_TOKEN = "FIREBASE_TOKEN";
    public static final String KEY_IS_CELL_CHECKED = "IS_CELL_CHECKED";
    public static final String KEY_FINISHED = "FINISHED";
    public static final String KEY_STUDENT_CHECK_DATE = "STUDENT_CHECK_DATE";
    public static final String KEY_SYNC_SUBTITLE_AT_SERVER  = "SYNC_SUBTITLE_AT_SERVER";

    public static final String KEY_SUBTITLE  = "SUBTITLE";
    public static final String KEY_SUBTITLE_CONTENTSINSTYLETAG  = "SUBTITLE_CONTENTSINSTYLETAG";
    public static final String KEY_SUBTITLE_ALL = "SUBTITLE_ALL";
    public static final String KEY_SUBTITLE_STUDYLANG = "SUBTITLE_STUDYLANG";
    public static final String KEY_SUBTITLE_MOTHER_TONGUE = "SUBTITLE_MOTHER_TONGUE";

    //	public static final String KEY_SUBTITLE_ID = "SUBTITLE_ID";
//	public static final String KEY_SUBTITLE_ID_STUDYLANG = "SUBTITLE_ID_STUDYLANG";
//	public static final String KEY_SUBTITLE_ID_MOTHER_TONGUE = "SUBTITLE_ID_MOTHER_TONGUE";
//	public static final String KEY_SUBTITLE_LANGUAGECODE  = "SUBTITLE_LANGUAGECODE";
    public static final String KEY_SUBTITLE_LISTLANGUAGESMIFORM  = "SUBTITLE_LISTLANGUAGESMIFORM";
    public static final String KEY_SUBTITLE_DIALOGUE  = "SUBTITLE_DIALOGUE";
    //	public static final String KEY_SUBTITLE_STARTTIME  = "SUBTITLE_STARTTIME";
//	public static final String KEY_SUBTITLE_ENDTIME  = "SUBTITLE_ENDTIME";
//	public static final String KEY_SUBTITLE_DIALOGUE_STUDYLANG = "SUBTITLE_DIALOGUE_STUDYLANG";
//	public static final String KEY_SUBTITLE_STARTTIME_STUDYLANG = "SUBTITLE_STARTTIME_STUDYLANG";
//	public static final String KEY_SUBTITLE_ENDTIME_STUDYLANG = "SUBTITLE_ENDTIME_STUDYLANG";
//	public static final String KEY_SUBTITLE_DIALOGUE_MOTHER_TONGUE  = "SUBTITLE_DIALOGUE_MOTHER_TONGUE";
//	public static final String KEY_SUBTITLE_STARTTIME_MOTHER_TONGUE  = "SUBTITLE_STARTTIME_MOTHER_TONGUE";
//	public static final String KEY_SUBTITLE_ENDTIME_MOTHER_TONGUE  = "SUBTITLE_ENDTIME_MOTHER_TONGUE";
    public static final String KEY_SUBTITLE_LANG  = "SUBTITLE_LANG";


    public static final String KEY_SUBTITLE_DIALOGUE_STUDYLANGUAGE  = "SUBTITLE_DIALOGUE_STUDYLANGUAGE";
    public static final String KEY_mapSubtitleVocaID  = "mapSubtitleAllBySubtitle";
    public static final String KEY_mapSubtitleAllByTime  = "mapSubtitleAllByTime";
    //	public static final String KEY_mapSubtitleStudyLangByTime  = "mapSubtitleStudyLangByTime";
    public static final String KEY_mapSubtitleMotherTongueByTime  = "mapSubtitleMotherTongueByTime";

    public static final String KEY_FREQUENCY = "FREQUENCY";

    public static final String KEY_GROUP_MANAGER = "GROUP_MANAGER";
    public static final String KEY_HAS_RECORD_VOICE = "HAS_RECORD_VOICE";
    public static final String KEY_HAS_SUB_CATEGORY = "HAS_SUB_CATEGORY";
    public static final String KEY_HAS_SUB_LIST = "HAS_SUB_LIST";

    public static final String KEY_HANJA_COUNT = "HANJA_COUNT";
    public static final String KEY_HANJA_LIST = "HANJA_LIST";
    public static final String KEY_HANJA_COMPONENTS = "HANJA_COMPONENTS";
    public static final String KEY_HANJA_KANCHE = "HANJA_KANCHE";
    public static final String KEY_HANJA_ORI = "HANJA_ORI";
    public static final String KEY_HANJA_TYPE = "HANJA_TYPE";
    public static final String KEY_HANJA_YAKJA = "HANJA_KANCHE";
    public static final String KEY_HANJA_EXAMPLE_SENTENCES = "HANJA_EXAMPLE_SENTENCES";
    public static final String KEY_HTMLWithMeaningOfWord = "HTMLWithMeaningOfWord";
    public static final String KEY_HTMLWithMeaningOfWordMap = "HTMLWithMeaningOfWordMAP";

    public static final String KEY_QUIZ_STATUS_ID = "QUIZ_STATUS_ID";

    public static final String KEY_ID = "ID";
    public static final String KEY_ID_EMAIL = "EMAIL";

    public static final String KEY_ID_IN_SERVER_VOCABOOK = "ID_IN_SERVER_VOCABOOK";
    public static final String KEY_IMAGE_ID = "IMAGE_ID";


    public static final String KEY_INDEX = "INDEX";
    public static final String KEY_INDEX_HANJA = "INDEX_HANJA";
    public static final String KEY_IS_PREFFERRED = "IS_PREFFERRED";
    public static final String KEY_IS_DEFAULT_PREFFERRED = "IS_DEFAULT_PREFFERRED";
    public static final String KEY_IS_BLOCK_USER = "IS_BLOCK_USER";
    public static final String KEY_ANSWER_CORRECT = "ANSWER_CORRECT";

    public static final String KEY_READING_ID = "READING_ID";
    public static final String KEY_LESSON_READING_ID = "LESSON_READING_ID";

    public static final String KEY_GRAMMAR_ID = "GRAMMAR_ID";
    public static final String KEY_GRAMMAR_CONTENT = "GRAMMAR_CONTENT";
    public static final String KEY_GRAMMAR_PARENT_ID = "GRAMMAR_PARENT_ID";

    public static final String KEY_IS_FAVORITE_USER = "IS_FAVORITE_USER";
    public static final String KEY_IS_FOLLOWING_USER = "IS_FOLLOWING_USER";
    public static final String KEY_IS_MY_STUDENT = "IS_MY_STUDENT";
    public static final String KEY_IS_USER_ONLINE = "IS_USER_ONLINE";


    public static final String KEY_JOIN_DATE = "JOIN_DATE";
    public static final String KEY_JOIN_DATE_TS = "JOIN_DATE_TS";
    public static final String KEY_JSESSIONID = "JSESSIONID";

    //	public static final String KEY_KNOW = "KNOW";
    public static final String KEY_VOCA_KNOW = "VOCA_KNOW";
    public static final String KEY_KNOW_FOR_WHAT = "KNOW_FOR_WHAT";
    //	public static final String KEY_KNOWPRONOUNCE = "KNOWPRONOUNCE";
    public static final String KEY_VOCA_KNOWPRONOUNCE = "VOCA_KNOWPRONOUNCE";
    public static final String KEY_KNOWN_VOCA_COUNT = "KNOWN_VOCA_COUNT";

    public static final String KEY_langService = "langService";
    public static final String KEY_langDisplay = "langDisplay";
    public static final String KEY_USE_SERVER_TRANSLATTION= "USE_SERVER_TRANSLATTION";
    public static final String KEY_STUDENT_langDisplay = "STUDENT_langDisplay";
    public static final String KEY_GRAMMAR_langDisplay = "GRAMMAR_langDisplay";
    public static final String KEY_STUDENT_LANGUAGE_LEVEL = "STUDENT_LANGUAGE_LEVEL";
    public static final String KEY_langNative = "langNative";
    public static final String KEY_langNativeCode = "langNativeCode";

    public static final String KEY_LANGUAGE_LEVEL = "LANGUAGE_LEVEL";
    public static final String KEY_LAST_ACCESS_DATE = "LAST_ACCESS_DATE";
    public static final String KEY_LAST_ACCESS_DATE_TS = "LAST_ACCESS_DATE_TS";
    public static final String KEY_LAST_GRAMMAR_ID = "LAST_GRAMMAR_ID";
    public static final String KEY_LAST_READING_ID = "LAST_READING_ID";

    public static final String KEY_LAST_MESSAGE = "LAST_MESSAGE";
    public static final String KEY_LAST_VOCABOOK_TYPE = "LAST_VOCABOOK_TYPE";
    public static final String KEY_LAST_VOCABOOKS_ID = "LAST_VOCABOOKS_ID";

    public static final String KEY_LEAD_LESSON = "LEAD_LESSON";
    public static final String KEY_LESSON_ID = "LESSON_ID";
    public static final String KEY_LESSON_TIME_LENGTH = "LESSON_TIME_LENGTH";
    //	public static final String KEY_LESSON_START_TIME = "LESSON_START_TIME";
    public static final String KEY_LESSON_START_TIME_TS = "LESSON_START_TIME_TS";
    //	public static final String KEY_LESSON_FINISH_TIME = "LESSON_FINISH_TIME";
    public static final String KEY_LESSON_FINISH_TIME_TS = "LESSON_FINISH_TIME_TS";

    public static final String KEY_LESSON_FOR_WHOM = "LESSON_FOR_WHOM";
    public static final String KEY_LESSON_REPEAT_TOPICS = "LESSON_REPEAT_TOPICS";
    public static final String KEY_LESSON_REPEAT_COUNT = "LESSON_REPEAT_COUNT";
    public static final String KEY_LESSON_REPEAT_MAX_PHRASE_COUNT = "LESSON_REPEAT_MAX_PHRASE_COUNT";
    public static final String KEY_LESSON_TYPE = "LESSON_TYPE";
    public static final String KEY_LESSON_MODE = "LESSON_MODE";

    public static final String KEY_RE_RECORDED = "RE_RECORDED";
    public static final String KEY_RE_RECORD_STATUS = "RE_RECORD_STATUS";
    public static final String KEY_RE_RECORD_LIST = "RE_RECORD_LIST";
    public static final String KEY_REG_DATE = "REG_DATE";
    public static final String KEY_REG_DATE_TS = "REG_DATE_TS";
    // public static final String KEY_RECORD_VOICE_LIST_TYPE=
    // "RECORD_VOICE_LIST_TYPE";

    public static final String KEY_RERECORD_LIST_ACTION = "RERECORD_LIST_ACTION";
    public static final String KEY_RECORDED_START_DATE = "RECORDED_START_DATE";
    public static final String KEY_RECORDED_END_DATE = "RECORDED_END_DATE";
    public static final String KEY_RECORDED_DATE = "RECORDED_DATE";

    //	public static final String KEY_CONTENTS= "CONTENTS";
    public static final String KEY_ROLE_PLAYING_CONTENT = "CONTENT";
    public static final String KEY_CONTENT_VIEW_ID = "CONTENT_VIEW_ID";
    public static final String KEY_COUNT_OF_QUIZ = "COUNT_OF_QUIZ";
    public static final String KEY_ROLE_PLAYING_CONTENT_TITLE = "CONTENT_TITLE";
    public static final String KEY_ROLE_PLAYING_CONTENT_TITLE_RUBY_TEXT = "CONTENT_TITLE_RUBY_TEXT";
    public static final String KEY_ROLE_PLAYING_CONTENT_TITLE_RUBY_VOCA_LIST = "CONTENT_TITLE_RUBY_VOCA_LIST";
    public static final String KEY_ROLE_PLAYING_CONTENT_MAIN = "CONTENT_MAIN";
    public static final String KEY_ROLE_PLAYING_CONTENT_SUB_TITLE = "CONTENT_SUB_TITLE";
    public static final String KEY_ROLE_PLAYING_CONTENT_SUB_TITLE_RUBY_TEXT = "CONTENT_SUB_TITLE_RUBY_TEXT";
    public static final String KEY_ROLE_PLAYING_CONTENT_SUB_TITLE_RUBY_VOCA_LIST = "CONTENT_SUB_TITLE_RUBY_VOCA_LIST";
    public static final String KEY_ROLE_PLAYING_CONTENT_SUB_DETAILS = "CONTENT_SUB_DETAILS";
    public static final String KEY_ROLE_PLAYING_CONVERSATION = "CONVERSATION";
    public static final String KEY_ROLE_PLAYING_CATEGORY_ID = "ROLE_PLAYING_CATEGORY_ID";
    //	public static final String KEY_ROLE_PLAYING_CATEGORY_PARENT_ID = "ROLE_PLAYING_CATEGORY_PARENT_ID";
    public static final String KEY_ROLE_PLAYING_CONVERSATION_ID = "CONVERSATION_ID";
    public static final String KEY_ROLE_PLAYING_PARENT_ID = "ROLE_PLAYING_PARENT_ID";
    public static final String KEY_ROLE_PLAYING_TYPE = "ROLE_PLAYING_TYPE"; //여행회화 안에서 공항, 식당등...
    public static final String KEY_ROLE_PLAYING_TYPE_MINOR = "ROLE_PLAYING_TYPE_MINOR";  //여행회화 - 식당 밑에서 주문, 계산등...
    public static final String KEY_RANDOM_ROLE_PLAYING_TYPE_MINOR = "RANDOM_ROLE_PLAYING_TYPE_MINOR";
    public static final String KEY_ROLE_PLAYING_MISSION_ID = "MISSON_ID";

    public static final String KEY_ROLE_PLAYING_MISSION= "MISSION";

    public static final String KEY_ROLE_PLAYING_CONTENT_TYPE = "ROLE_PLAYING_CONTENT_TYPE";
    public static final String KEY_ROLE_PLAYING_CONTENT_VIEW_IDS = "ROLE_PLAYING_CONTENT_VIEW_IDS";
    //	public static final String KEY_ROLE_PLAYING_RUBY_VOCA_LIST = "ROLE_PLAYING_RUBY_VOCA_LIST";
    public static final String KEY_ALL_VOCA_LIST = "ALL_VOCA_LIST";
    public static final String KEY_EXAM_VOCA_LIST = "EXAM_VOCA_LIST";
    public static final String KEY_STUDY_VOCA_LIST = "STUDY_VOCA_LIST";
    //	public static final String KEY_ALL_VOCA_LIST_SHOW_GUIDE = "ALL_VOCA_LIST_SHOW_GUIDE";
    public static final String KEY_ALL_SENTENCE_LIST = "ALL_SENTENCE_LIST";
    public static final String KEY_STUDY_SENTENCE_LIST = "STUDY_SENTENCE_LIST";
//	public static final String KEY_ALL_SENTENCE_LIST_SHOW_GUIDE = "ALL_SENTENCE_LIST_SHOW_GUIDE";

//	public static final String KEY_ROLE_PLAYING_TYPE_BIG = "ROLE_PLAYING_TYPE_MINOR"; //여행회화, 비지니스 회화등...
//	public static final String KEY_ROLE_PLAYING_TYPE_MIDDLE = "ROLE_PLAYING_TYPE_MINOR";  //여행회화 안에서 공항, 식당등...
//	public static final String KEY_ROLE_PLAYING_TYPE_SMALL = "ROLE_PLAYING_TYPE_SMALL";  //여행회화 - 식당 밑에서 주문, 계산등...
//	public static final String KEY_RANDOM_ROLE_PLAYING_TYPE_SMALL = "RANDOM_ROLE_PLAYING_TYPE_SMALL";

    public static final String KEY_ROLE_PLAYING_ID = "ROLE_PLAYING_ID";

    public static final String KEY_listUinqueWordNameLowcase = "listUinqueWordNameLowcase";
    public static final String KEY_listWordsFromText = "listWordsFromText";
    public static final String KEY_mapWordsFromText = "mapWordsFromText";

    public static final String KEY_MAKE_RUBY_TEXT = "MAKE_RUBY_TEXT";
    public static final String KEY_MAX_HANJA_QUIZ = "MAX_HANJA_QUIZ";
    public static final String KEY_MAX_HOMEWORK = "MAX_HOMEWORK";
    public static final String KEY_MAX_QUIZ = "MAX_QUIZ";
    public static final String KEY_MEANING = "MEANING";
//	public static final String KEY_MEANING1 = "MEANING1";
//	public static final String KEY_MEANING2 = "MEANING2";
//	public static final String KEY_MEANING3 = "MEANING3";

    public static final String KEY_MEANING_ENG = "MEANING_ENG";
    public static final String KEY_MEANING_ENG_DETAILED = "MEANING_ENG_DETAILED";
    public static final String KEY_MEANING_TTS = "MEANING_TTS";
    public static final String KEY_MEANING_DETAILED = "MEANING_DETAILED";
    public static final String KEY_MEANING_FOR_HIDE_ALL = "MEANING_FOR_HIDE_ALL";
    public static final String KEY_MEANING_FOR_STUDENT = "MEANING_FOR_STUDENT";

    public static final String KEY_NAME_FOR_LANG_STUDY = "NAME_FOR_LANG_STUDY";
    public static final String KEY_NAME_FOR_STUDENT_LANG_MEANING = "NAME_FOR_STUDENT_LANG_MEANING";

    // public static final String KEY_MEANING_LONG = "MEANING_LONG";


    public static final String KEY_HANJA_MEANING_PRONOUNCE_FOR_KOREAN = "HANJA_MEANING_PRONOUNCE_FOR_KOREAN";
    public static final String KEY_HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN = "HANJA_FIRST_MEANING_PRONOUNCE_FOR_KOREAN";
    public static final String KEY_MEANING_WITH_PRONOUNCE_FOR_HANJA = "MEANING_WITH_PRONOUNCE_FOR_HANJA";

    public static final String KEY_MENU = "MENU";
    public static final String KEY_NATION = "NATION";
    public static final String KEY_NATIVE_SPEAKER_ID = "NATIVE_SPEAKER_ID";

    public static final String KEY_titleName = "titleName";

    public static final String KEY_NAME = "NAME";
    public static final String KEY_NAME_RUBY_TEXT = "NAME_RUBY_TEXT";
    public static final String KEY_NAME_RUBY_VOCA_LIST = "NAME_RUBY_VOCA_LIST";
    public static final String KEY_NAME_ITEM = "NAME_ITEM";
    public static final String KEY_NAME_ITEM_SUB = "NAME_ITEM_SUB";
    public static final String KEY_NAME_ITEM_SUB_DESC = "NAME_ITEM_SUB_DESC";
    public static final String KEY_NAME_SHOP = "NAME_SHOP";

    public static final String KEY_NAME_DISPLMEANINGLANG = "NAME_DISPLMEANINGLANG";
    public static final String KEY_NAME_STUDYLANG = "NAME_STUDYLANG";

//	public static final String KEY_OTHER_USER_UID = "OTHER_USER_UID";

    public static final String KEY_OPPONENT_UID = "OPPONENT_UID";
    public static final String KEY_OPPONENT_NAME_BY_ME = "OPPONENT_NAME_BY_ME";
    public static final String KEY_OPPONENT_DESC_BY_ME = "OPPONENT_DESC_BY_ME";
    public static final String KEY_OPPONENT_STUDY_ROLE = "OPPONENT_STUDY_ROLE";

    public static final String KEY_PARTOFSPEECH = "PARTOFSPEECH";
    public static final String KEY_PASSWORD = "PASSWORD";
    public static final String KEY_PERSON_AB = "PERSON_AB";
    public static final String KEY_PERSON_A = "PERSON_A";
    public static final String KEY_PERSON_B = "PERSON_B";
    public static final String KEY_POINT_AMKI = "POINT_AMKI";
    public static final String KEY_POINT_READING = "POINT_READING";
    public static final String KEY_POINT = "POINT";
    public static final String KEY_PRACTICE_DIALOG_ROOM_LIST_ID = "PRACTICE_DIALOG_ROOM_LIST_ID";
    public static final String KEY_PRACTICE_DIALOG_ROOM_NAME = "PRACTICE_DIALOG_ROOM_NAME";
    public static final String KEY_PRACTICE_DIALOG_RESPONSE = "PRACTICE_DIALOG_RESPONSE";
    public static final String KEY_PRACTICE_DIALOG_TYPE = "PRACTICE_DIALOG_TYPE";
    public static final String KEY_PRACTICE_REAL_TIME_TYPE = "PRACTICE_REAL_TIME_TYPE";

    public static final String KEY_PREFERRED_NATIVE_SPEAKERS = "PREFERRED_NATIVE_SPEAKERS";
    public static final String KEY_PRICE = "PRICE";
    public static final String KEY_PRICE_FIXED = "PRICE_FIXED";
    public static final String KEY_PRICE_RANGE = "PRICE_RANGE";

    public static final String KEY_PRODUCT_ID = "PRODUCT_ID";
    public static final String KEY_POSITION = "POSITION";
    public static final String KEY_POS_WORD = "ALL_POS_CURRENT_WORD";
    public static final String KEY_POS_WORD_BASEFORM = "ALL_POS_CURRENT_WORD_BASEFORM";
    public static final String KEY_PRONOUNCE = "PRONOUNCE";
    public static final String KEY_PRONOUNCE_BASEFORM = "PRONOUNCE_BASEFORM";


    //	public static final String KEY_PRONOUNCE1_FIRST = "PRONOUNCE1_FIRST";
//	public static final String KEY_PRONOUNCE2_FIRST = "PRONOUNCE2_FIRST";
//	public static final String KEY_PRONOUNCE3_FIRST = "PRONOUNCE3_FIRST";
    public static final String KEY_PRONOUNCE_From_Analyzer = "PRONOUNCE_Analyzer";
    public static final String KEY_PRONOUNCE_WORDORI = "PRONOUNCEWORDORI";
    public static final String KEY_RANK = "RANK";
    public static final String KEY_RANK_REPLACEMENT = "RANK_REPLACEMENT";
    public static final String KEY_REFRESH_KNOW_VALUE = "REFRESH_KNOW_VALUE";
    public static final String KEY_REPLACEMENT_NAME = "REPLACEMENT_NAME";
    public static final String KEY_REPLACEMENT_VERSION = "REPLACEMENT_VERSION";
    public static final String KEY_REPLY_CALL_TYPE = "REPLY_CALL_TYPE";
    public static final String KEY_REQUESTEDPASSWORDRESETEMAIL = "REQUESTEDPASSWORDRESETEMAIL";
    public static final String KEY_REQUESTED_USER_PERSON_AB = "REQUESTED_USER_PERSON_AB";

    public static final String KEY_SELECTED_ANSWER_NUMBER = "SELECTED_ANSWER_NUMBER";
    public static final String KEY_SERIAL_ID_FOR_WORDS = "SERIAL_ID_FOR_WORDS";
    public static final String KEY_SERVER_VOCA_BOOKLIST = "SERVER_VOCA_BOOKLIST";
    public static final String KEY_SEX = "SEX";
    public static final String KEY_TUTOR_SEX = "TUTOR_SEX";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_STUDENT_SEX = "STUDENT_SEX";

    //	public static final String KEY_SHOW_USERNAME = "SHOW_USERNAME";
    public static final String KEY_SIGNUP_RESULT = "SIGNUP_RESULT";
    public static final String KEY_START_NO = "START_NO";
    public static final String KEY_START_TIME = "START_TIME";
    public static final String KEY_STUDIED = "STUDIED";
    public static final String KEY_STUDY_COUNT = "STUDY_COUNT";
    public static final String KEY_STUDY_DATE = "STUDY_DATE";
    public static final String KEY_STUDY_DATE_TS = "STUDY_DATE_TS";
    //	public static final String KEY_STUDY_TYPE = "STUDY_TYPE";
    public static final String KEY_studyLang = "studyLang";
//	public static final String KEY_LANG_STUDY_CODE= "LANG_STUDY_CODE";

    public static final String KEY_STUDYLANG_IN_LESSONS_CHEDULE= "STUDYLANG_IN_LESSONS_CHEDULE";
    public static final String KEY_STUDYLANG_CODE_IN_LESSONS_CHEDULE= "STUDYLANG_CODE_IN_LESSONS_CHEDULE";
    public static final String KEY_strOri = "strOri";
    public static final String KEY_strPOS = "strPOS";
    public static final String KEY_POS = "POS";
    public static final String KEY_TBL_NAME = "TBL_NAME";
    public static final String KEY_TBL_NAME2 = "TBL_NAME2";
    public static final String KEY_TBL_NAME3 = "TBL_NAME3";
    public static final String KEY_TBL_NAME4 = "TBL_NAME4";
    public static final String KEY_TBL_SECTION = "TBL_SECTION";
    public static final String KEY_TBL_PARENT_ROW = "TBL_PARENT_ROW";
    public static final String KEY_TBL_PARENT_SECTION = "TBL_PARENT_SECTION";
    public static final String KEY_TBL_TYPE_SYNC = "TBL_TYPE_SYNC";
    public static final String KEY_TBL_ROW = "TBL_ROW";

    public static final String KEY_Time_StartDate = "StartDate";
    //	public static final String KEY_Time_StartParsingDate = "StartParsingDate";
//	public static final String KEY_Time_StartTimeOfExtractWords = "StartTimeOfExtractWords";
//	public static final String KEY_Time_StartTimeOfGetUniqueWords = "StartTimeOfGetUniqueWords";
//	public static final String KEY_Time_StartTimeOfMakeHTMLWithMeaning = "StartTimeOfMakeHTMLWithMeaning";
//	public static final String KEY_Time_EndParsingDate = "EndParsingDate";
    public static final String KEY_Time_SpentTime = "SpentTime";
    public static final String KEY_titleOfTooltip = "titleOfTooltip";
    public static final String KEY_TOKEN = "TOKEN";
    public static final String KEY_DEFAULT_PHRASE_FOR_STUDENT = "DEFAULT_PHRASE_FOR_STUDENT";
    public static final String KEY_DEFAULT_PHRASE_FOR_STUDENT_RUBY_TEXT = "DEFAULT_PHRASE_FOR_STUDENT_RUBY_TEXT";
    public static final String KEY_DEFAULT_PHRASE_FOR_STUDENT_RUBY_VOCA_LIST = "DEFAULT_PHRASE_FOR_STUDENT_RUBY_VOCA_LIST";
    //	public static final String KEY_DEFAULT_PHRASE_L1 = "DEFAULT_PHRASE_L1";
//	public static final String KEY_DEFAULT_PHRASE_L2_6 = "DEFAULT_PHRASE_L2_6";
    public static final String KEY_DEFAULT_PHRASE_LANGUAGE_LEVEL = "DEFAULT_PHRASE_LANGUAGE_LEVEL";
    public static final String KEY_DESC = "DESC";
    public static final String KEY_DETAIL = "DETAIL";
    public static final String KEY_DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String KEY_SHOW_MEANING_AT_STUDENT = "SHOW_MEANING_AT_STUDENT";

    public static final String KEY_TOPIC_BEGIN_INDEX = "TOPIC_BEGIN_INDEX";
    public static final String KEY_TOPIC_REPEATED_COUNT = "TOPIC_REPEATED_COUNT";
    public static final String KEY_UID = "UID";
    public static final String KEY_UID_LIST = "UID_LIST";
    public static final String KEY_USED = "USED";
    public static final String KEY_USE_SERVER_VOCABOOK_DATA = "USE_SERVER_VOCABOOK_DATA";
    public static final String KEY_USE_SHOP_NAME_FOR_RANDOM_VALUE = "USE_SHOP_NAME_FOR_RANDOM_VALUE";

    public static final String KEY_CHATROOM_MESSAGE_UPDATE_TYPE = "CHATROOM_MESSAGE_UPDATE_TYPE";
    public static final String KEY_CHATROOM_MESSAGE_ID = "CHATROOM_MESSAGE_ID";
    public static final String KEY_USER_LIST = "USER_LIST";
    public static final String KEY_USER_LIST_TOTAL = "USER_LIST_TOTAL";
    public static final String KEY_USER_LIST_BY_CATEGORY = "USER_LIST_BY_CATEGORY";
    public static final String KEY_USER_LIST_BY_LESSON = "USER_LIST_BY_LESSON";
    public static final String KEY_USER_ROLE = "USER_ROLE";
    public static final String KEY_USER_TYPE = "USER_TYPE";
    public static final String KEY_USER_VOCA_BOOKLIST = "USER_VOCA_BOOKLIST";
    public static final String KEY_USER_WORD_EXAM_TYPE = "USER_WORD_EXAM_TYPE";
    public static final String KEY_QUIZ_TYPE = "QUIZ_TYPE";
    public static final String KEY_UUID = "UUID";
    public static final String KEY_VERSION = "VERSION";
    public static final String KEY_VOCA = "VOCA";
    public static final String KEY_VOCABOOK_NAME = "VOCABOOK_NAME";
    public static final String KEY_VOCABOOK_TYPE = "VOCABOOK_TYPE";
    public static final String KEY_VOCABOOKS_ID = "VOCABOOKS_ID";
    public static final String KEY_VOCA_ID = "VOCA_ID";
    public static final String KEY_VOCA_IDs = "VOCA_IDs";
    public static final String KEY_VOCA_ID_VOCA_TYPE = "VOCA_ID_VOCA_TYPE";
    public static final String KEY_VOCA_COUNT = "VOCA_COUNT";
    public static final String KEY_VOCA_DISPLAY = "VOCA_DISPLAY";
    public static final String KEY_VOCA_DISPLAY_RUBY_TEXT = "VOCA_DISPLAY_RUBY_TEXT";
    public static final String KEY_VOCA_DISPLAY_RUBY_VOCA_LIST = "VOCA_DISPLAY_RUBY_VOCA_LIST";
    public static final String KEY_VOCA_TOTAL = "VOCA_TOTAL";
    public static final String KEY_VOCA_CURRENT_INDEX = "VOCA_CURRENT_INDEX";
    public static final String KEY_VOCA_LIST = "VOCA_LIST";
    public static final String KEY_VOCA_RECORDED_TOTAL = "VOCA_RECORDED_TOTAL";
    public static final String KEY_VOCA_TTS = "VOCA_TTS";
    public static final String KEY_VOCA_LIST_BY_CATEGORY = "VOCA_LIST_BY_CATEGORY";

    public static final String KEY_VOIP_TOKEN = "VOIP_TOKEN";
    public static final String KEY_WORD = "WORD";
    public static final String KEY_WORD_TTS = "WORD_TTS";
    public static final String KEY_WORDBOOK_NAME = "WORDBOOK_NAME";
    public static final String KEY_WORDID = "WORDID";
    public static final String KEY_WORD_COUNT = "WORD_COUNT";
    public static final String KEY_IS_STRING_OVER_MAX_WORD_COUNT = "IS_STRING_OVER_MAX_WORD_COUNT";
    public static final String KEY_WORDLIST_KEY = "WORDLIST_KEY";
    public static final String KEY_WORDORI = "WORDORI";
    public static final String KEY_WORDORI_ID = "WORDORI_ID";
    public static final String KEY_WORDLEVEL = "WORDLEVEL";
    public static final String KEY_WORD_DISPLAY = "WORD_DISPLAY";
    public static final String KEY_WORD_LIST = "WORD_LIST";
    public static final String KEY_JMDICT_WORD_LIST = "JMDICT_WORD_LIST";
    public static final String KEY_JMDICT_MEANING = "JMDICT_MEANING";
    public static final String KEY_JMDICT_MEANING_ENG = "JMDICT_MEANING_ENG";
    public static final String KEY_WORD_SAME_ID= "WORD_SAME_ID";
    public static final String KEY_WORD_WordWitMeaning = "WORD_WordWitMeaning";
    public static final String KEY_WORD_WithConjugation = "WORD_WithConjugation";

    public static final String REQPARAMKEY_PASSWORD = "settingpassword";
    public static final String REQPARAMKEY_NEWPASSWORD = "settingnewpassword";
    public static final String REQPARAMKEY_REPASSWORD = "settingrepassword";
    // public static final String TOKEN_PREFIX = "token_prefix";

    /** 각종 값들 */
//	public static final Integer STUDY_TYPE_SPEAKING = 1;
//	public static final Integer STUDY_TYPE_WRITING_CHOOSE_WORD = 2;
//	public static final Integer STUDY_TYPE_WRITING_HANDWRITING = 3;

    public static final String DEFAULT_DATE_1970 = "1970-01-01 00:00:00";
    public static final Integer DEFAULT_DATE_2000 = 946684800; //946684800 is Date and time (GMT): Saturday, January 1, 2000 12:00:00 AM
    public static final Integer TBL_TYPE_SYNC_TOPIC_LESSON_MODE = 1;
    public static final Integer TBL_TYPE_SYNC_EXAMPLE_SENTENCE= 2;
    public static final Integer TBL_TYPE_SYNC_CONTENT_ID_1 = 3;
    public static final Integer TBL_TYPE_SYNC_CONVERSATION_LIST= 4;
    public static final Integer TBL_TYPE_SYNC_RUBY_VOCA_LIST= 5;
    public static final Integer TBL_TYPE_SYNC_RUBY_VOCA_LIST_CONTENT= 6;
    public static final Integer TBL_TYPE_SYNC_RUBY_VOCA_LIST_CONVERSATION= 7;
    public static final Integer TBL_TYPE_SYNC_RUBY_VOCA_LIST_ROLE_PLAYING_ALL= 8;
    public static final Integer TBL_TYPE_SYNC_VOCA_LIST_BY_CATEGORY = 9;
    public static final Integer TBL_TYPE_SYNC_ALL_SENTENCE_LIST = 10;
    public static final Integer TBL_TYPE_SYNC_TOPIC_REVIEW_MODE = 11;
    public static final Integer TBL_TYPE_SYNC_RL_ALL_VOCAS_IN_MAIN_VIEW = 12; // Role Playing -> All vocas in main view
    public static final Integer TBL_TYPE_SYNC_RL_ALL_SENTENCES_IN_MAIN_VIEW = 13; // Role Playing -> Study sentences in main view
    public static final Integer TBL_TYPE_SYNC_MSG_VOCA_LIST = 14;
    public static final Integer TBL_TYPE_SYNC_READING_VOCA_LIST = 15;

    public static final Integer SELECT_ONE_SENTENCE_NO = 0;
    public static final Integer SELECT_ONE_SENTENCE_YES = 1;


    public static final Integer TMDB_TYPE_NONE = 0;
    public static final Integer TMDB_TYPE_MOVIE = 1;
    public static final Integer TMDB_TYPE_TV_SERIES = 2;

    public static final Integer MAKE_RUBY_TEXT_NO = 0;
    public static final Integer MAKE_RUBY_TEXT_YES = 1;

    public static final Integer PN_TYPE_NORMAL = 0;
    public static final Integer PN_TYPE_RUBY_TEXT = 1;
    public static final Integer PN_TYPE_RUBY_TEXT_CONTENT = 2;
    public static final Integer PN_TYPE_RUBY_TEXT_CONVERSATION = 3;
    public static final Integer PN_TYPE_RUBY_TEXT_ALL = 4;
    public static final Integer PN_TYPE_STUDY_WORDS = 5;
    public static final Integer PN_TYPE_STUDY_SENTENCES = 6;

    public static final Integer PN_MINUTES_BEFORE_BEGIN_LESSON_DEFAULT = 3;
    public static final Integer PN_MINUTES_BEFORE_FINISH_LESSON_DEFAULT = 1;
    public static final Integer ENABLE_LESSON_PN_NO = 0;
    public static final Integer ENABLE_LESSON_PN_YES = 1;


    //	public static final Integer EXAM_MAX_QUESTIONS = 30;
    public static final Integer EXAM_SOURCE_CURRENT_WORKBOOK = 1;
    public static final Integer EXAM_SOURCE_MEMORIZATION_TARGETS = 2;

    public static final Integer QUIZ_SOURCE_CURRENT_WORKBOOK = 1;
    public static final Integer QUIZ_SOURCE_MEMORIZATION_TARGETS = 2;

    public static final Integer QUIZ_TYPE_MULTIPLE_CHOICE = 1;
    public static final Integer QUIZ_TYPE_FILL_GAP = 2;
    public static final Integer QUIZ_TYPE_MULTIPLE_CHOICE_PRONOUNCE = 3;
    public static final Integer QUIZ_TYPE_CHOOSE_LETTERS = 4;
    public static final Integer QUIZ_TYPE_WRITING = 5;


    public static final Integer MULTIPLE_CHOICE_QUESTION_NONE = 0;
    public static final Integer MULTIPLE_CHOICE_QUESTION_TYPE_VOCA = 1;
    public static final Integer MULTIPLE_CHOICE_QUESTION_TYPE_MEANING = 2;
    public static final Integer MULTIPLE_CHOICE_QUESTION_TYPE_RANDOM = 3;

    public static final Integer EXAM_TYPE_NORMAL = 0;
    public static final Integer EXAM_TYPE_QUESTION_WORD = 1;
    public static final Integer EXAM_TYPE_QUESTION_MEANING = 2; //뜻이나 발음을 말함.
    public static final Integer EXAM_TYPE_QUESTION_RANDOM = 3;



    public static final Integer RESULT_OF_EACH_QUESTION_ERROR = 0;
    public static final Integer RESULT_OF_EACH_QUESTION_OK = 1;
    public static final Integer RESULT_OF_EACH_QUESTION_TO_KNOWN = 2;
    public static final Integer RESULT_OF_EACH_QUESTION_TO_UNKNOWN = 3;

    public static final Integer STUDY_ROLE_MAIN_NO = 0;
    public static final Integer STUDY_ROLE_MAIN_YES = 1;

    public static final Integer JOINED_LESSON_NO = 0;
    public static final Integer JOINED_LESSON_YES = 1;

    public static final Integer CORRECT_FORMAT_VOCA_TO_INSERT_DB_NO = 0;
    public static final Integer CORRECT_FORMAT_VOCA_TO_INSERT_DB_YES = 1;

    public static final Integer SHOW_ASTERISK_OFF = 0;
    public static final Integer SHOW_ASTERISK_ON = 1;

    public static final Integer SHOW_MEANING_AT_STUDENT_OFF = 0;
    public static final Integer SHOW_MEANING_AT_STUDENT_ON = 1;



    public static final Integer SHOW_ORIGINAL_VOCA_OFF = 0;
    public static final Integer SHOW_ORIGINAL_VOCA_ON = 1;



    public static final Integer SHOW_SENTENCE_ALL = 1;
    public static final Integer HIDE_SENTENCE_KNOWN = 2;
    public static final Integer HIDE_SENTENCE_EXCELLENT = 3;
    public static final Integer HIDE_SENTENCE_KNOWN_EXCELLENT = 4;

    public static final Integer STUDY_ROLE_NONE = -1;
    public static final Integer STUDY_ROLE_STUDENT = 0;
    public static final Integer STUDY_ROLE_TUTOR = 1;
    public static final Integer STUDY_ROLE_OBSERVER = 2;

    public static final Integer USER_SORT_TYPE_NAME = 1;
    public static final Integer USER_SORT_TYPE_LAST_ACCESS_DATE = 2;

    public static final Integer APP_TYPE_APP = 1;
    public static final Integer APP_TYPE_WIDGET = 2;

    public static final Integer LESSON_COUNT_FOR_WIDGET_MIN = 0;
    public static final Integer LESSON_COUNT_FOR_WIDGET_MAX = 8;

//	public static final Integer USED_NO = 0;
//	public static final Integer USED_YES = 1;

    public static final Integer SHOW_TOAST_NO = 0;
    public static final Integer SHOW_TOAST_YES = 1;

    public static final Integer CONFIRM_NO = 0;
    public static final Integer CONFIRM_YES = 1;

    public static final Integer USE_SHOP_NAME_FOR_RANDOM_VALUE_NO = 0;
    public static final Integer USE_SHOP_NAME_FOR_RANDOM_VALUE_YES = 1;



    public static final Integer QUIZ_APP_ALL = 1;
    public static final Integer QUIZ_APP_VIDEO = 2;
    public static final Integer QUIZ_APP_BOOK = 3;

    public static final Integer CHATROOM_TYPE_1ON1 = 1;
    public static final Integer CHATROOM_TYPE_MULTI = 2;
    public static final Integer CHATROOM_TYPE_CLASS = 3;
    public static final Integer CHATROOM_TYPE_LESSON = 4;

    public static final Integer COUNT_OF_PHRASES_TO_STUDY_AT_ONCE_DEFAULT = 5;

    public static final Integer CLASS_MEMBER_TYPE_STUDENT = 0;
    public static final Integer CLASS_MEMBER_TYPE_PRESIDENT = 1;

    public static final Integer IS_QUIZ_FINISH = 1;

    public static final Integer IS_STARTED_NO = 0;
    public static final Integer IS_STARTED_YES = 1;
    public static final Integer IS_FINISHED_NO = 0;
    public static final Integer IS_FINISHED_YES = 1;

    public static final Integer FREE_TALKING_NO = 0;
    public static final Integer FREE_TALKING_YES = 1;

    public static final Integer SMALL_TALKING_NO = 0;
    public static final Integer SMALL_TALKING_YES = 1;

    public static final Integer RECORD_LESSON_NO = 0;
    public static final Integer RECORD_LESSON_YES = 1;

    public static final Integer LEAD_LESSON_NO = 0;
    public static final Integer LEAD_LESSON_YES = 1;

    public static final Integer IS_ANSWER_CORRECT_NO = 0;
    public static final Integer IS_ANSWER_CORRECT_YES = 1;

//	public static final Integer KNOWPRONOUNCE_NULL = -1;
//	public static final Integer KNOWPRONOUNCE_NO = 0;
//	public static final Integer KNOWPRONOUNCE_YES = 1;


    public static final Integer LANGUAGE_LEVEL_ABSOLUTE_BEGINNER = 0;
    public static final Integer LANGUAGE_LEVEL_LOW_BEGINNER = 1;
    public static final Integer LANGUAGE_LEVEL_BEGINNER = 2;
    public static final Integer LANGUAGE_LEVEL_LOW_INTERMEDIATE= 3;
    public static final Integer LANGUAGE_LEVEL_INTERMEDIATE = 4;
    public static final Integer LANGUAGE_LEVEL_LOW_ADVANCED = 5;
    public static final Integer LANGUAGE_LEVEL_ADVANCED = 6;

    public static final Integer CORRECT_PRONUNCIATION_NEVER = 0;
    public static final Integer CORRECT_PRONUNCIATION_SOMETIMES = 1;
    public static final Integer CORRECT_PRONUNCIATION_FREQUENTLY = 2;

    public static final Integer SPEAKING_SPEED_SLOW = 1;
    public static final Integer SPEAKING_SPEED_NORMAL = 2;
    public static final Integer SPEAKING_SPEED_FAST = 3;


    public static final Integer IS_CELL_CHECKED_NO = 0;
    public static final Integer IS_CELL_CHECKED_YES = 1;

    public static final Integer READING_SPEED_SLOW = 1;
    public static final Integer READING_SPEED_NORMAL = 2;
    public static final Integer READING_SPEED_FAST = 3;

    public static final Integer MAKE_NEW_EXAM_NO = 0;
    public static final Integer MAKE_NEW_EXAM_YES = 1;

    public static final Integer ALLOW_CHAT_NO = 0;
    public static final Integer ALLOW_CHAT_YES = 1;

    public static final Integer ALLOW_PUSH_NO = 0;
    public static final Integer ALLOW_PUSH_YES = 1;

    public static final Integer HAS_MEANING_NO = 0;
    public static final Integer HAS_MEANING_YES = 1;

    public static final Integer RE_RECORD_LIST_ADD = 1;
    public static final Integer RE_RECORD_LIST_REMOVE = 0;

    public static final Integer RE_RECORDED_NO = 0;
    public static final Integer RE_RECORDED_YES = 1;

    public static final Integer USE_SERVER_VOCABOOK_DATA_NO = 0;
    public static final Integer USE_SERVER_VOCABOOK_DATA_YES = 1;


    public static final Integer RE_RECORD_STATUS_NONE = 0; //Hide small re-record icon
    public static final Integer RE_RECORD_STATUS_TUTOR_ADDED = 1; //display small re-record icon
    public static final Integer RE_RECORD_STATUS_STUDENT_RECORDED = 2; //Hide small re-record icon

    public static final Integer REPLY_CALL_TYPE_DECLINE= 0;
    public static final Integer REPLY_CALL_TYPE_ACCEPT = 1;
    public static final Integer REPLY_CALL_TYPE_END = 2;
    public static final Integer REPLY_CALL_TYPE_NOT_RESPONSE = 3;

    public static final Integer ROLE_PLAYING_TYPE_BASIC = 50;
    public static final Integer ROLE_PLAYING_TYPE_GREETING = 100;
    public static final Integer ROLE_PLAYING_TYPE_TRAFFIC = 200;
    public static final Integer ROLE_PLAYING_TYPE_RESTAURANT = 300;
    public static final Integer ROLE_PLAYING_TYPE_AIRPORT = 400;
    public static final Integer ROLE_PLAYING_TYPE_HOTEL = 500;
    public static final Integer ROLE_PLAYING_TYPE_SHOPPING = 600;
    public static final Integer ROLE_PLAYING_TYPE_SIGHTSEEING = 700;
    public static final Integer ROLE_PLAYING_TYPE_CLASS = 800;
    public static final Integer ROLE_PLAYING_TYPE_EMERGENCY = 900;
    public static final Integer ROLE_PLAYING_TYPE_PHONE = 900;
//	public static final Integer ROLE_PLAYING_TYPE_PAYING = 600;

    public static final Integer CREATOR_TYPE_USER = 1;
    public static final Integer CREATOR_TYPE_ADMIN = 2;

    public static final Integer ROLE_PLAYING_CUSTOMOIZE_ID_GREETING_1 = 1;
    public static final Integer ROLE_PLAYING_CUSTOMOIZE_ID_GREETING_2 = 2;
    public static final Integer ROLE_PLAYING_CUSTOMOIZE_ID_MENU_1 = 21;
    public static final Integer ROLE_PLAYING_CUSTOMOIZE_ID_MENU_2 = 2;
    public static final Integer ROLE_PLAYING_CUSTOMOIZE_ID_AIRPORT_1 = 31;
    public static final Integer ROLE_PLAYING_CUSTOMOIZE_ID_AIRPORT_2 = 41;

    public static final Integer SHARE_MY_RECORDING_NO = 0;
    public static final Integer SHARE_MY_RECORDING_YES = 1;

    public static final Integer MANAGE_RECORD_REMOVE = 0;
    public static final Integer MANAGE_RECORD_ADD = 1;

    public static final Integer SEX_MALE = 1;
    public static final Integer SEX_FEMALE = 2;

    public static final Integer AGE_TEENAGER = 1;
    public static final Integer AGE_ADULT = 2;

    public static final Integer CHATROOM_MESSAGE_UPDATE_TYPE_DELETE = 1;
    public static final Integer CHATROOM_MESSAGE_UPDATE_TYPE_EDIT = 2;

    public static final Integer DIC_VOCA_GROUP_TYPE_CONFUSED = 1;
    public static final Integer DIC_VOCA_GROUP_TYPE_SPELLING_DIFFERENCES = 2;

    public static final Integer FILES_VOICE_VERSION_DELETED_ON_SERVER = -1;
    public static final Integer FILES_VOICE_VERSION_NOT_UPLOADED_TO_SERVER = -10;

    public static final Integer LESSON_MODE_NORMAL = 1;
    public static final Integer LESSON_MODE_EXAM = 2;
    public static final Integer LESSON_MODE_ROLE_PLAYING = 3;
    public static final Integer LESSON_MODE_REVIEW = 4;
    public static final Integer LESSON_MODE_GRAMMAR = 5;
    public static final Integer LESSON_MODE_READING = 6;
    public static final Integer LESSON_MODE_PRONOUNCE = 7;

    public static final Integer LESSON_TYPE_PHONE = 1;
    public static final Integer LESSON_TYPE_TEXT_CHAT = 2;

    public static final Integer REFRESH_KNOW_VALUE_NO = 0;
    public static final Integer REFRESH_KNOW_VALUE_YES = 1;

//	public static final Integer IS_CALLER_FIRST_JOINED_USER_NO = 0;
//	public static final Integer IS_CALLER_FIRST_JOINED_USER_YES = 1;

    public static final Integer IS_FIRST_JOINED_USER_NO = 0;
    public static final Integer IS_FIRST_JOINED_USER_YES = 1;

    public static final Integer ACCESS_OR_EXIT_APP_ACCESS = 0;
    public static final Integer ACCESS_OR_EXIT_APP_EXIT = 1;

    public static final Integer USER_LIST_BY_CATEGORY_ALL = 1;
    public static final Integer USER_LIST_BY_CATEGORY_FOLLOWING = 2;
    public static final Integer USER_LIST_BY_CATEGORY_FOLLOWER = 3;
    public static final Integer USER_LIST_BY_CATEGORY_FAVORITE = 4;
    public static final Integer USER_LIST_BY_CATEGORY_BLOCK = 5;
    public static final Integer USER_LIST_BY_CATEGORY_STUDYLANG = 6;
    public static final Integer USER_LIST_BY_CATEGORY_FILTER_NAME = 7;
    public static final Integer USER_LIST_BY_CATEGORY_UID_DESC = 8;
    public static final Integer USER_LIST_BY_CATEGORY_LAST_ACCESS_DATE_DESC = 9;
    public static final Integer USER_LIST_BY_LESSON_NONE = 0;
    public static final Integer USER_LIST_BY_LESSON_STUDENT = 1;
    public static final Integer USER_LIST_BY_LESSON_TUTOR = 2;

    public static final Integer VOCABOOK_PRACTICE_ONLY_NO = 0;
    public static final Integer VOCABOOK_PRACTICE_ONLY_YES = 1;

    public static final Integer DISP_ORDER_MAX = 999999;
    public static final Integer VOCA_DISP_ORDER_NONE = 0;
    public static final Integer VOCA_DISP_ORDER_ALPHABET = 1;

    public static final Integer VOCABOOK_ALPHABET_ONLY_NO = 0;
    public static final Integer VOCABOOK_ALPHABET_ONLY_YES = 1;
    public static final Integer VOCABOOK_ALPHABET_ONLY_PRACTICE = 2;

    public static final Integer PRACTICE_ROOM_OPEN_TYPE_CLOSE = 0;
    public static final Integer PRACTICE_ROOM_OPEN_TYPE_FOR_OPPONENT = 1;
    public static final Integer PRACTICE_ROOM_OPEN_TYPE_FOR_ANYONE = 2;
    public static final Integer PRACTICE_ROOM_OPEN_TYPE_FOR_SELF_PRACTICE = 3;

    public static final String MSG_SHOW_ASTERISK_OFF = "Student’s sentences are displayed as ***";
    public static final String MSG_SHOW_ASTERISK_ON = "Student’s sentences are not displayed as *** anymore";

    public static final String MSG_SHOW_SENTENCE_ALL = "Student’s all sentences are displayed";
    public static final String MSG_HIDE_SENTENCE_KNOWN = "Student’s all Known(!) sentences are hided";
    public static final String MSG_HIDE_SENTENCE_EXCELLENT = "Student’s all Excellent(A) sentences are hided";
    public static final String MSG_HIDE_SENTENCE_KNOWN_EXCELLENT = "Student’s all Known(!) and Excellent(A) sentences are hided";

    public static final String SOURCETYPE_WEB = "SOURCETYPE_WEB";
    public static final String SOURCETYPE_MOBILE = "SOURCETYPE_MOBILE";
    public static final String SOURCETYPE_EPUB = "SOURCETYPE_EPUB";


    public static final Integer RUBY_OUTPUT_TYPE_WEB = 1;
    public static final Integer RUBY_OUTPUT_TYPE_SQLITE = 2;
    public static final Integer RUBY_OUTPUT_TYPE_EPUB = 3;
    public static final Integer RUBY_OUTPUT_TYPE_APP = 4;


    public static final String STR_KNOW = "KNOW";
    public static final String STR_FREQUENCY = "FREQUENCY";
    public static final String STR_MEANING = "MEANING";
    public static final String STR_PRONOUNCE = "PRONOUNCE";
    public static final String STR_WORD = "WORD";
    public static final String STR_WORDORI = "WORDORI";

    public static final String MESSAGE_TABLE_REQUEST = "Request";
    public static final String MESSAGE_TABLE_DO_YOU_WANT_TO_JOIN_TO_STUDY = "Do you want to join to study?";
    public static final String MESSAGE_TABLE_REQUEST_PRACTICE_WITH_OPPONENT = "requestPracticeWithOpponent";
    // public static final String STR_WORDCOUNT_MIN = "WORDCOUNT_MIN";
    // public static final String STR_WORDCOUNT_MAX = "WORDCOUNT_MAX";

    public static final Integer VOCA_TYPE_NONE = 0;
    public static final Integer VOCA_TYPE_WORD = 1;
    public static final Integer VOCA_TYPE_SENTENCE = 2;
    public static final Integer VOCA_TYPE_SUBTITLE = 3;
    public static final Integer VOCA_TYPE_SUBTITLE_UNTUNED = 4;
    public static final Integer VOCA_TYPE_BOOK= 5; //책의 내용은 현재는 DIC_HANJA_PHRASES만 존재함.
    public static final Integer VOCA_TYPE_USER_VOCABOOK_LOCAL= 10; //앱내에서만 유저 개인적으로 씀.
    public static final Integer VALUE_VOCA_TYPE_KOICA = 80;

    public static final Integer KNOW_FOR_WHAT_MEANING = 1;
    public static final Integer KNOW_FOR_WHAT_PRONOUNCE = 2;

    public static final String APP_NAME_DALVOCA = "DALVOCA";
    public static final String APP_NAME_DALVOCAPRO = "DALVOCAPRO";


    public static final Integer SEARCH_HANJA_TYPE_TEXT = 1;
    public static final Integer SEARCH_HANJA_TYPE_RADICAL = 2;
    public static final Integer SEARCH_HANJA_TYPE_STROKES = 3;

    public static final String SEARCH_IN_WHERE_VOCA = "SEARCH_IN_WHERE_VOCA";
    public static final String SEARCH_IN_WHERE_MEANING = "SEARCH_IN_WHERE_MEANING";

    public static final String FOLLOWING_ETC_TYPE_BLOCK = "FOLLOWING_ETC_TYPE_BLOCK";
    public static final String FOLLOWING_ETC_TYPE_FOLLOWING = "FOLLOWING_ETC_TYPE_FOLLOWING";
    public static final String FOLLOWING_ETC_TYPE_FOLLOWER = "FOLLOWING_ETC_TYPE_FOLLOWER";
    public static final String FOLLOWING_ETC_TYPE_FAVORITE = "FOLLOWING_ETC_TYPE_FAVORITE";
    public static final String FOLLOWING_ETC_TYPE_TUTOR_STUDENTS = "FOLLOWING_ETC_TYPE_TUTOR_STUDENTS";

    public static final String URL_TYPE_YOUTUBE = "YOUTUBE";

    public static final Integer MANAGE_USER_IN_LESSON_ADD_AS_STUDENT = 1;
    public static final Integer MANAGE_USER_IN_LESSON_REMOVE_FROM_STUDENT = 2;
    public static final Integer MANAGE_USER_IN_LESSON_ADD_AS_TUTOR = 3;
    public static final Integer MANAGE_USER_IN_LESSON_REMOVE_FROM_TUTOR= 4;


    public static final Integer SUBTITLE_LANG_AUTO = 1;
    public static final Integer SUBTITLE_LANG_STUDY_LANG = 2;
    public static final Integer SUBTITLE_LANG_MOTHER_TONGUE = 3;

    public static final Integer FAKE_DATA_SOURCE_FAKER = 0;
    public static final Integer FAKE_DATA_SOURCE_SQL = 1;
    public static final Integer FAKE_DATA_SOURCE_USER_STORED_DATA = 2;

    public static final Integer STUDIED_NO = 0;
    public static final Integer STUDIED_YES = 1;


    public static final Integer APP_STATE_MODE_BACKGROUND = 0;
    public static final Integer APP_STATE_MODE_FOREGROUND = 1;

    public static final Integer VOCABOOK_TYPE_CODE_USER = 1; // DB담을 값
    public static final Integer VOCABOOK_TYPE_CODE_SERVER = 2;
    public static final String VOCABOOK_TYPE_SERVER = "VOCABOOK_TYPE_SERVER"; //API의 파라미터로 전달할 값
    public static final String VOCABOOK_TYPE_USER = "VOCABOOK_TYPE_USER";

    public static final Integer SHOW_PRONOUNCE_SHOW = 1;
    public static final Integer SHOW_MEANING_SHOW = 1;
    public static final Integer VOCA_LEVEL_INDIC_MIN = 0;
    public static final Integer VOCA_LEVEL_INDIC_MAX = 999;

    public static final Integer VOCA_ID_ZERO = 0;

    public static final Integer WORDLEVEL_INDIC_MIN = 0;
    public static final Integer WORDLEVEL_INDIC_MAX = 999;
    public static final Integer WORDLEVEL_MAX_INSERT_USER_TABLE_ONLY_HANJA = 998;
    public static final Integer WORDLEVEL_MAX_INSERT_USER_TABLE_ONLY_ENG = 10;
    public static final Integer WORDLEVEL_MAX_INSERT_USER_TABLE_ONLY_JP = 10;
    public static final Integer WORDLEVEL_MAX_INSERT_USER_TABLE_ONLY_CH_S = 998;
    public static final Integer WORDLEVEL_MAX_INSERT_USER_TABLE_ONLY_KO = 998;

    public static final Integer DEFAULT_START_NO_USER_LIST= 0;
    public static final Integer DEFAULT_COUNT_RECORD_USER_LIST = 50;

    public static final Integer WORDCOUNTNO_MIN_DEFAULT = 0;
    public static final Integer WORDCOUNTNO_MAX_DEFAULT = 999999;
    public static final Integer WORDCOUNTNO_DEFAULT = 200;

    public static final Integer PEOPLE_COUNT_MIN_DEFAULT = 0;
    public static final Integer PEOPLE_COUNT_MAX_DEFAULT = 999999;
    public static final Integer PEOPLE_COUNT_DEFAULT = 200;

    public static final Integer VOICE_FILE_TYPE_VOCA = 1;
    public static final Integer VOICE_FILE_TYPE_REAL_SITUATION = 2;

    public static final Integer BOOKMARKED_NO = 0;
    public static final Integer BOOKMARKED_YES = 1;



    public static final String WORDLISTTYPE_KNOWN = "1";
    public static final String WORDLISTTYPE_UNKNOWN = "2";
    public static final String WORDLISTTYPE_BOOKMARK = "3";
    public static final String WordLevel_None = "WordLevel_None";
    public static final String WordLevel_Beginner = "WordLevel_Beginner";
    public static final String WordLevel_Pre_Intermediate = "WordLevel_Pre_Intermediate";
    public static final String WordLevel_Intermediate = "WordLevel_Intermediate";
    public static final String WordLevel_Post_Intermediate = "WordLevel_Post_Intermediate";
    public static final String WordLevel_Advanced = "WordLevel_Advanced";
    public static final String WordLevel_God = "WordLevel_God";
    public static final Integer WordLevelNo_None = 0;
    public static final Integer WordLevelNo_God = 999;
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

    public static final String DATAINDIC_YES = "YES";
    public static final String DATAINDIC_NO = "NO";

    public static final String STRING_YES = "YES";
    public static final String STRING_NO = "NO";

    public static final String POS_JP_KUROMOJI_POS_NOUN = "名詞";
    public static final String POS_JP_KUROMOJI_POS_VERB = "動詞";
    public static final String POS_JP_KUROMOJI_POS2_PROPERNOUN = "固有名詞";
    public static final String POS_JP_KUROMOJI_POS2_NUMBER = "数";
    public static final String POS_JP_KUROMOJI_POS3_PEOPLE = "人名";
    public static final String POS_JP_KUROMOJI_POS3_PLACE = "地域";
    public static final String POS_JP_KUROMOJI_POS4_NATION = "国";

    // 은전한잎형태소 분석기의 품사 종류 POS(Part of Speech)
    public static final String POS_KO_EUNJEON_MM = "MM"; // 실질형태소 - 수식언 - 관형사
    public static final String POS_KO_EUNJEON_MAG = "MAG"; // 실질형태소 - 수식언 - 일반 부사
    public static final String POS_KO_EUNJEON_MAJ = "MAJ"; // 실질형태소 - 수식언 - 접속 부사
    public static final String POS_KO_EUNJEON_NOUN_NORMAL_NNG = "NNG"; // 실질형태소 - 체언 - 일반 명사
    public static final String POS_KO_EUNJEON_NOUN_PROPERONOUN_NNP = "NNP"; // 실질형태소 - 체언 - 고유 명사
    public static final String POS_KO_EUNJEON_NOUN_NNB = "NNB"; // 실질형태소 - 체언 - 의존 명사
    public static final String POS_KO_EUNJEON_NOUN_NNBC = "NNBC"; // 실질형태소 - 체언 - 단위를 나타내는 명사
    public static final String POS_KO_EUNJEON_NOUN_NR = "NR"; // 실질형태소 - 체언 - 수사
    public static final String POS_KO_EUNJEON_VERB_VV = "VV"; // 실질형태소 - 용언 - 동사
    public static final String POS_KO_EUNJEON_ADVERB_VA = "VA"; // 실질형태소 - 용언 - 형용사
    public static final String POS_KO_EUNJEON_VX = "VX"; // 실질형태소 - 용언 - 보조 용언
    public static final String POS_KO_EUNJEON_VCP = "VCP"; // 실질형태소 - 용언 - 긍정 지정사
    public static final String POS_KO_EUNJEON_VCN = "VCN"; // 실질형태소 - 용언 - 부정 지정사
    public static final String POS_KO_EUNJEON_PROPERNOUN_NP = "NP";

    // Twitter형태소 분석기의 품사 종류
    public static final String PartOfSpeech_KO_EOMI = "EOMI";
    public static final String PartOfSpeech_KO_FOREIGN = "FOREIGN";
    public static final String PartOfSpeech_KO_JOSA = "JOSA";
    public static final String PartOfSpeech_KO_KOREANPARTICLE = "KOREANPARTICLE";
    public static final String PartOfSpeech_KO_NOUN = "NOUN";
    public static final String PartOfSpeech_KO_NUMBER = "NUMBER";
    public static final String PartOfSpeech_KO_PREEOMI = "PREEOMI";
    public static final String PartOfSpeech_KO_PROPERNOUN = "PROPERNOUN";

    public static final String PartOfSpeech_KO_PUNCTUATION = "PUNCTUATION";
    public static final String PartOfSpeech_KO_SUFFIX = "SUFFIX";
    public static final String PartOfSpeech_KO_VERB = "VERB";

    public static final String CLIENT_TYPE_IOS = "IOS";
    public static final String CLIENT_TYPE_ANDROID = "ANDROID";
    public static final String CLIENT_TYPE_WEB = "WEB";
    public static final String OS_TYPE_SERVER = "SERVER";

    public static final String TRANSLATE_TYPE_MOVIE = "TRANSLATE_TYPE_MOVIE";
    public static final String TRANSLATE_TYPE_SUBTITLE_TO_RUBY = "TRANSLATE_TYPE_SUBTITLE_TO_RUBY";
    public static final String TRANSLATE_TYPE_BOOK = "TRANSLATE_TYPE_BOOK";

    public static final Integer TERM_LEVEL_DEFAULT = 999;
    /** Movie 관련 */
    public static final String MOVIE_SUFFIX_DALMOVIE = "_DalMovie";

    /** ePub 관련 */
    public static final String EPUB_SUFFIX_DALBOOK = "_DalBook";
    public static final String EPUB_SPLITTEDFILE_MAP = "EPUB_SPLITTEDFILE_MAP";
    public static final String EPUB_SPLITTEDFILE_LIST = "EPUB_SPLITTEDFILE_LIST";
    public static final String EPUB_FILENAMEANDCONTENT = "EPUB_FILENAMEANDCONTENT";
    public static final Integer MAX_CHAR_COUNT_TO_SPLIT_INCLUDE_TAG = 10000; // 영어, 한개의 xml파일(Chapter)를 나눌 기준, 파일내의
    // 문자수(HTML Tag포함)
    public static final Integer MAX_CHAR_COUNT_TO_SPLIT_INCLUDE_TAG_JP = 400; // 일본어, 한개의 xml파일(Chapter)를 나눌 기준, 파일내의
    // 문자수(HTML Tag포함)
    public static final Integer MAX_CHAR_COUNT_TO_SPLIT_INCLUDE_TAG_CH_S = 400; // 중국어, 한개의 xml파일(Chapter)를 나눌 기준, 파일내의
    // 문자수(HTML Tag포함)
    public static final Integer MAX_CHAR_COUNT_TO_SPLIT_TEXTNODE = 2000; // 영어, 한개의 textNode를 나눌 기준 (text파일이 아님)
    public static final Integer MAX_CHAR_COUNT_TO_SPLIT_TEXTNODE_JP = 200; // 일본어, 한개의 textNode를 나눌 기준
    public static final Integer MAX_CHAR_COUNT_TO_SPLIT_TEXTNODE_CH_S = 200; // 중국어, 한개의 textNode를 나눌 기준

    public static final Integer MAX_CHAR_COUNT_TO_SPLIT_TEXTFILE = 2000; // 한개의 text파일를 나눌 기준, 파일내의 문자수(Tag없음)
    public static final String UPDATEWORDSINFO_ToKnown = "ToKnown";
    public static final String UPDATEWORDSINFO_ToUnknown = "ToUnknown";



    public static final String UPDATEWORDSINFO_ToKnownPronounce = "ToKnownPronounce";
    public static final String UPDATEWORDSINFO_ToUnknownPronounce = "ToUnknownPronounce";
    public static final String UPDATEWORDSINFO_ToBookmark = "ToBookmark";
    public static final String UPDATEWORDSINFO_ToUnbookmark = "ToUnbookmark";
    public static final String UPDATEWORDSINFO_ChangeMeanings = "ChangeMeanings";
    public static final String UPDATEWORDSINFO_ChangePronounces = "ChangePronounces";

    /** 로그인 관련 */

    public static final Integer LOGIN_OK = 1;
    public static final Integer LOGIN_ERROR = 0;

    public static final Integer ADDPOINT_OK = 1;
    public static final Integer ADDPOINT_NG = 2;
    public static final Integer ADDPOINT_NG_POINTISNOTNUMBER = 3;

    public static final Integer PASSWORD_LENGTH_MIN = 4;
    public static final Integer CHANGE_PASSWORD_OK = 1;
    public static final Integer CHANGE_PASSWORD_FAIL_CURRENTPASSWORD_IS_WRONG = 2;
    public static final Integer CHANGE_PASSWORD_FAIL_CANNOTCHANGEPASSWORD = 99;


    public static final Integer LESSON_FOR_STUDENT = 1;
    public static final Integer LESSON_FOR_TUTOR = 2;
    public static final Integer LESSON_FOR_ADMIN = 3;

    public static final Integer USER_ROLE_STUDENT = 0;
    public static final Integer USER_ROLE_TUTOR = 1;
    public static final Integer USER_ROLE_NATIVESPEAKER = 2;
    public static final Integer USER_ROLE_STUDENT_TUTOR = 3;
    public static final Integer USER_ROLE_STUDENT_NATIVESPEAKER = 4;
    public static final Integer USER_ROLE_TUTOR_NATIVESPEAKER = 5;
    public static final Integer USER_ROLE_STUDENT_TUTOR_NATIVESPEAKER = 6;

    /** 기타 값들 */
    public static final Integer HalfDayByMilliSeconds = 1000 * 60 * 60 * 12; //1000밀리세컨드 * 60초 * 60분
    public static final Integer OneHourByMilliSeconds = 1000 * 60 * 60; //1000밀리세컨드 * 60초 * 60분
    public static final Integer ThreeHoursByMilliSeconds = 1000 * 60 * 60; //1000밀리세컨드 * 60초 * 60분 * 3
    public static final Integer MilliSecondsToCheckUserOnline = 1000 * 60 * 10; //1000밀리세컨드 * 60초 * 10분


    public static final Integer SQL_EXACT_MATCH_NO = 0;
    public static final Integer SQL_EXACT_MATCH_YES = 1;

    public static final Integer CALL_TYPE_VIDEO = 1;
    public static final Integer CALL_TYPE_AUDIO = 2;
    public static final Integer CALL_TYPE_TEXT = 3;

    public static final Integer PRACTICE_DIALOG_TYPE_SPEKAING = 1;
    public static final Integer PRACTICE_DIALOG_TYPE_WRITING = 2;

    public static final Integer PRACTICE_REAL_TIME_TYPE_NO = 0;
    public static final Integer PRACTICE_REAL_TIME_TYPE_YES = 1;

    public static final Integer SEND_PN_NO = 0;
    public static final Integer SEND_PN_YES = 1;

    public static final Integer PRACTICE_DIALOG_RESPONSE_REFUSE = 0;
    public static final Integer PRACTICE_DIALOG_RESPONSE_ACCEPT = 1;

    public static final Integer IN_APP_POINT_TYPE_READING = 0;
    public static final Integer IN_APP_POINT_TYPE_AMKI = 1;

    public static final Integer IS_PREFFERRED_FALSE = 0;
    public static final Integer IS_PREFFERRED_TRUE = 1;

    public static final Integer VOCA_UPDATE_FAIL = -999;
    public static final Integer VOCA_UPDATE_NO_VOCA = -20;
    public static final Integer VOCA_UPDATE_NOT_ALLOWED = -10;
    public static final Integer VOCA_UPDATE_DO_NOTHING = 0;
    public static final Integer VOCA_UPDATE_SUCCESS = 1;
    public static final Integer VOCA_INSERT_NEW_VOCA = 2;

    public static final Integer LESSON_REPEAT_TOPICS_DEFAULT = 2;
    public static final Integer LESSON_REPEAT_COUNT_DEFAULT = 3;
    public static final Integer LESSON_REPEAT_MAX_PHRASE_COUNT_DEFAULT = 50;

    public static final Integer TOPIC_BEGIN_INDEX_DEFAULT = 0;
    public static final Integer TOPIC_REPEATED_COUNT_DEFAULT = 0;


    public static final Integer ROLE_PLAYING_CONTENT_TYPE_NONE = 0;
    public static final Integer ROLE_PLAYING_CONTENT_TYPE_TEXT = 1;
    public static final Integer ROLE_PLAYING_CONTENT_TYPE_VIEW = 2;
    public static final Integer ROLE_PLAYING_CONTENT_TYPE_IMAGE = 3;

    public static final Integer SIGNUP_RESULT_ERROR = 0;
    public static final Integer SIGNUP_RESULT_ID_EXISTS = 1;
    public static final Integer SIGNUP_RESULT_ID_INVALID = 2;
    public static final Integer SIGNUP_RESULT_OK = 200;


    public static final Integer IS_NO = 0;
    public static final Integer IS_YES = 1;

    public static final Integer SHOW_GUIDE_NO = 0;
    public static final Integer SHOW_GUIDE_YES = 1;

//	public static final boolean BOOL_SHOW_GUIDE_NO = false;
//	public static final boolean BOOL_SHOW_GUIDE_YES = true;

    public static final boolean BOOL_NEED_TO_STUDY_NO = false;
    public static final boolean BOOL_NEED_TO_STUDY_YES = true;
    public static final Integer MAX_COUNT_TO_STUDY_SENTENCE_IN_ROLE_PLAY = 10;
    public static final Integer MAX_COUNT_TO_STUDY_WORD_IN_ROLE_PLAY = 15;
    public static final Integer MAX_COUNT_TO_STUDY_WORD_IN_NORMAL_LESSON = 20;
    public static final Integer MAX_COUNT_TO_STUDY_WORD_IN_READING = 100;

    public static final Integer IS_BLOCK_USER_NO = 0;
    public static final Integer IS_BLOCK_USER_YES = 1;
    public static final Integer IS_FAVORITE_USER_NO = 0;
    public static final Integer IS_FAVORITE_USER_YES = 1;
    public static final Integer IS_FOLLOWING_USER_NO = 0;
    public static final Integer IS_FOLLOWING_USER_YES = 1;
    public static final Integer IS_MY_STUDENT_NO = 0;
    public static final Integer IS_MY_STUDENT_YES = 1;


    public static final Integer IS_USER_ONLINE_NO = 0;
    public static final Integer IS_USER_ONLINE_YES = 1;


    public static final Integer IS_DEFAULT_PREFFERRED_FALSE = 0;
    public static final Integer IS_DEFAULT_PREFFERRED_TRUE = 1;

//	public static final Integer SUBTITLE_FILE_LANG_BOTH = 1;
//	public static final Integer SUBTITLE_FILE_LANG_STUDYLANG = 2;
//	public static final Integer SUBTITLE_FILE_LANG_MOTHER_TONGUE = 3;

    public static final Integer IS_RIGHT_SUBTITLE_NO = 0;
    public static final Integer IS_RIGHT_SUBTITLE_YES = 1;


    public static final String TITLE_PARAM1 = "PARAM1";
    public static final String TITLE_PARAM2 = "PARAM2";


    public static final String PERSON_AB_A = "A";
    public static final String PERSON_AB_B = "B";

    public static final String STRING_BRANCH = "BRANCH";


    public static final String PUSH_TYPE_DATA = "PUSH_TYPE_DATA";
//	public static final String PUSH_TYPE_NOTIFICATION = "PUSH_TYPE_NOTIFICATION";


    public static final String RECORD_VOICE_LIST_TYPE_ALL = "RECORD_VOICE_LIST_TYPE_ALL";
    public static final String RECORD_VOICE_LIST_TYPE_ONLY_TO_RECORD = "RECORD_VOICE_LIST_TYPE_ONLY_TO_RECORD";
    public static final String FIREBASE_STORAGE_FOLDER_VOICE = "voice";
    public static final String FIREBASE_STORAGE_FOLDER_IMAGE = "image";

    public static final Integer STUDY_MODE_OFF = 0;
    public static final Integer STUDY_MODE_ON = 1;
    public static final String EVALUATE_VOCA_GRADE_A = "A";
    public static final String EVALUATE_VOCA_GRADE_B = "B";
    public static final String EVALUATE_VOCA_GRADE_C = "C";

    public static final Integer HAS_FILE_NO = 0;
    public static final Integer HAS_FILE_YES = 1;

    public static final Integer SHOW_USERNAME_NO = 0;
    public static final Integer SHOW_USERNAME_YES = 1;

    public static final Integer HAS_SUB_CATEGORY_NO = 0;
    public static final Integer HAS_SUB_CATEGORY_YES = 1;

    public static final Integer PRONOUNCE_DONT_USE_IN_DB = 0;
    public static final Integer PRONOUNCE_USE_IN_DB = 1;
    // public static final Integer WORD_LEVEL_DEFAULT = 3;
    public static final Integer UID_DEFAULT = 1505;

    public static final String REDUCE_POINT_CONTENT_SIZE = "CONTENT_SIZE";
    public static final String REDUCE_POINT_CONTENT_TYPE = "CONTENT_TYPE";
    public static final String REDUCE_POINTTYPE_SUBTITLE = "SUBTITLE";
    public static final String REDUCE_POINTTYPE_HTML = "HTML";
    public static final String REDUCE_POINTTYPE_OCR = "OCR";
    public static final String REDUCE_POINTTYPE_TEXT = "TEXT";

    public static final Integer SORT_BY_ASC = 1;
    public static final Integer SORT_BY_DESC = 2;

    public static final Integer DEFAULT_COUNT_OF_HOMEWORK = 20;
    public static final Integer DEFAULT_COUNT_OF_QUIZ = 20;
    public static final Integer DEFAULT_COUNT_OF_HANJA_QUIZ = 5;

    public static final Integer DEFAULT_VOCABOOKS_ID_EN = 257;
    public static final Integer DEFAULT_VOCABOOKS_ID_KO = 172;
    public static final Integer DEFAULT_VOCABOOKS_ID_JP = 34;
    public static final Integer DEFAULT_VOCABOOKS_ID_ZH_CN = 25;
    public static final Integer DEFAULT_VOCABOOKS_ID_VI = 257; //베트남어는 현재 기본 서버 단어장이 없다.

    public static final String DEFAULT_PREFIX_STUDY_CHATROOM_NAME = "Lesson";

    public static final Integer DEFAULT_POINT_TRANSLATE = 1000;
    public static final Integer DEFAULT_POINT_DALVOCA = 100;
    public static final Integer DEFAULT_POINT_ZERO = 0;
    public static final Integer REDUCE_POINTSIZE_1KB = 1024;
    public static final Integer REDUCE_POINTSIZE_10KB = 1024 * 10;
    public static final Integer REDUCE_POINTSIZE_100KB = 1024 * 100;
    public static final Integer REDUCE_POINTSIZE_1MB = 1024 * 1000;
    public static final Integer REDUCE_POINTSIZE_10MB = 1024 * 1000 * 10;

    public static final Integer REDUCE_POINT = 1;
    public static final Integer REDUCE_POINT_2 = 2;
    public static final Integer REDUCE_POINT_3 = 3;
    public static final Integer REDUCE_POINT_5 = 4;
    public static final Integer REDUCE_POINT_10 = 10;
    public static final Integer REDUCE_POINT_20 = 20;
    public static final Integer REDUCE_POINT_50 = 50;
    public static final Integer REDUCE_POINT_100 = 100;

    public static final Integer MAX_COUNT_HOWEWORK = 15; // DalVoca에서 한 유저가 최대한 가질수 있는 숙제의 수와 "이거 알아요?"의 수(너무 많으면 숙제하기 싫어진다.)
    public static final Integer MAX_WORD_COUNT_NOT_LOGIN_USER = 200; // 현재는 단어수가 아닌 문자수로 한다, 나중에 단어수로 하자...
    public static final Integer MAX_WORD_COUNT_LOGIN_USER = 10000; // 현재는 단어수가 아닌 문자수로 한다, 나중에 단어수로 하자...
    public static final String SPACE = " ";
    // public static final String HTMLTAG_SPACE = "&nbsp;";
    public static final String HTMLTAG_SPACE = "&#160;";
    public static final String HTMLTAG_BR = "<br />";
    public static final String HTMLTAG_TAB_9 = "&#9;";
    public static final String HTMLTAG_SPACE_nbsp = "&nbsp;";
    public static final String HTMLTAG_SPACE_160 = "&#160;";

    public static final String USER_TYPE_ADMIN = "999";
    public static final String USER_TYPE_NORMAL = "0";
    public static final String POS_SEPERATOR_UNDERSCORE = "_";
    public static final String UNDERSCORE = "_";
    // For Password Enryption
    public static final String AES_KEY = "Bar33335Bar12345";
    public static final String AES_PARM_SPECS = "RandomInitVector";
    public static final byte[] SALT = { (byte) 0xde, (byte) 0x33, (byte) 0x10, (byte) 0x12, (byte) 0xde, (byte) 0x33,
            (byte) 0x10, (byte) 0x12, };


    public static final String PREFIX_GET = "get";
    public static final String PREFIX_SET = "set";

    // File 관련
    public static final String FILE_DOT = ".";
    public static final String FILE_SLASH = "/";
    public static final String FILE_SUFFIX_ALLHTML = "_all.html";
    public static final String FILE_SUFFIX_ALLSMI = "_all.smi";

    public static final String FILEEXT_ara = "ara";
    public static final String FILEEXT_ass = "ass";
    public static final String FILEEXT_backup = "backup";
    public static final String FILEEXT_epub = "epub";
    public static final String FILEEXT_html = "html";
    public static final String FILEEXT_m4a = "m4a";
    public static final String FILEEXT_smi = "smi";
    public static final String FILEEXT_sqlite = "sqlite";
    public static final String FILEEXT_ssa = "ssa";
    public static final String FILEEXT_srt = "srt";
    public static final String FILEEXT_lrc = "lrc";
    public static final String FILEEXT_txt = "txt";
    public static final String FILEEXT_zip = "zip";
    public static final String FILE_DSStore = ".DS_Store";


    public static final String INPUT_SUBTITLE_FORMAT_SMI = "SMI";
    public static final String INPUT_SUBTITLE_FORMAT_SRT = "SRT";
    public static final String INPUT_SUBTITLE_FORMAT_ASS = "ASS"; //SSA도 ASS로 칭한다.
    public static final String INPUT_SUBTITLE_FORMAT_SQLITE = "SQLITE";


    public static final String SUBTITLE_FILE_CONTENT_SAMI_TAG = "<SAMI>";
    public static final String SUBTITLE_FILE_CONTENT_SYNC_TAG_LOWERCASE = "<sync start=";
    public static final Integer SUBTITLE_FILE_FORMAT_FROM_CONTENT_NONE = -1;
    public static final Integer SUBTITLE_FILE_FORMAT_FROM_CONTENT_SMI = 1;
    public static final Integer SUBTITLE_FILE_FORMAT_FROM_CONTENT_SRT = 2;
    public static final Integer SUBTITLE_FILE_FORMAT_FROM_CONTENT_ASS = 3; //SSA도 ASS로 칭한다.
    public static final Integer SUBTITLE_FILE_FORMAT_FROM_CONTENT_BRACKET = 4; //이런 포맷(분석은 SRT에서 코드 추가하여 분석) [56][78]to contain the most
    public static final Integer SUBTITLE_FILE_FORMAT_FROM_CONTENT_LRC = 5; //자막 파일 포맷


    // 인코딩 관련
    public static final String ENCODING_UTF8 = "UTF8";
    public static final String ENCODING_UTF_8 = "UTF-8";
    public static final String ENCODING_EUC_KR = "EUC-KR";


    //영어회화 롤플레잉관련
    public static final String RPC_VALUE_PREFIX = ": ";

    //NLP로 단어를 뽑아낼때 인풋데이타의 KEY가 없을때는 dalnim을 임시로 쓴다. 나중에는 다른 이름으로 바꿀것
    public static final String KEY_FOR_TEXT_TO_EXTRACT = "dalnim";

    public static Pattern EXPRESSION_PATTERN_LRC_TIME = Pattern.compile("^\\[\\d{1,2}:\\d{1,2}\\.\\d{1,2}\\].*$");
}