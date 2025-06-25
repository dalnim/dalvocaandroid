package com.dalread.database.sqlite;

import static com.dalread.util.Constant.PLAYER.SQL.COLUMN;
import static com.dalread.util.Constant.PLAYER.SQL.QUERY;
import static com.dalread.util.Constant.PLAYER.SQL.TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.GPT_CHAT_MESSAGE;
import com.dalread.model.GptTextShortCut;
import com.dalread.model.UserVocabookLocal;
import com.dalread.network.ChatCompletionRequest;
import com.dalread.network.ChatCompletionResponse;
import com.dalread.network.ChatGPTRequest;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseSubDatabase {
    protected final String TAG = "SubDatabase";
    protected SubDatabaseHelper databaseHelper;
    protected SQLiteDatabase database;
    protected String databasePath;
    protected SharedPreferencesDB sharedPreferencesDB;
    protected int studyLanguage, uid;
//    protected static BaseSubDatabase instance;
    private static String TABLE_GPT_CHAT_MESSAGE;
    private static String TABLE_GPT_TEXT_SHORT_CUT;

    protected static int STUDY_LANG_CODE;
    protected static EnumLanguage enumStudyLanguage;
    protected static EnumLanguage enumMenuLanguage;
    protected static EnumLanguage enumMotherTongueLanguage;
    protected static Context context;

    protected static String subPath = Constant.BASE_BLANK;
    protected static String defaultDateTime = " \"1970-01-01 00:00:00\" ";
//    public static BaseSubDatabase getInstance(Context context, String path) {
//        if (instance == null || subPath == null || !subPath.equals(path))
//            instance = new BaseSubDatabase(context, path);
//        return instance;
//    }

    public BaseSubDatabase(Context contextLocal, String path) {
        context = contextLocal;
        databasePath = path;
        databaseHelper = new SubDatabaseHelper(context, path);
        this.subPath = path;
        initTableName(context);
        sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        studyLanguage = EnumLanguage.findByFormatApi(sharedPreferencesDB.getStudyLanguage()).getIdApi();
        STUDY_LANG_CODE =  LanguageUtil.getStudyLanguageCode(context);
        enumStudyLanguage = LanguageUtil.getStudyLanguage(context);
        updateMotherTongueLanguage(context);
        updateMenuLanguage(context);
        uid = sharedPreferencesDB.getRealUid();
    }

    protected void initTableName(Context context) {
        TABLE_GPT_CHAT_MESSAGE = TABLE.GPT_CHAT_MESSAGE;
        TABLE_GPT_TEXT_SHORT_CUT = TABLE.GPT_TEXT_SHORT_CUT;
    }
    //StudyLanguage can't be changed, but MotherTongue can be changed in the Setting.
    public void updateMotherTongueLanguage(Context context) {
        enumMotherTongueLanguage = LanguageUtil.getMotherTongueLanguage(context);
    }
    public void updateMenuLanguage(Context context) {
        enumMenuLanguage = LanguageUtil.getMenuLanguage(context);
    }

    public void openRead() {
        this.database = databaseHelper.getReadableDatabase();
    }

    /**
     * Open the databases connection.
     */
    public void openWrite() {
        this.database = databaseHelper.getWritableDatabase();
    }

    /**
     * Close the databases connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public String getDatabasePath() {
        return databasePath;
    }

    //Dalnim Add
    public int getMinColumnValueInTbl(String column, String table) {
//        String query = "SELECT MIN(" + column + ") FROM " + table;
        String query = QUERY.SELECT + QUERY.MIN + QUERY.PARENTHESIS_OPEN + column + QUERY.PARENTHESIS_CLOSE + QUERY.FROM + table;
        return getIntBySql(query);
//        openRead();
//        Cursor cursor = database.rawQuery(query, null);
//        cursor.moveToFirst();
//        int id = cursor.getInt(0);
//        cursor.close();
//        close();
//        return id;
    }

    public int getIntBySql(String sql) {
        openRead();
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        close();
        return result;
    }

    public String getStringBySql(String sql) {
        openRead();
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        close();
        return result;
    }

    //Dalnim add
    public int getNewIDInSubtitleTable() {
        String column = Constant.PLAYER.SQL.COLUMN.ID;
        int value = getMaxColumnValueInTbl(column, Constant.PLAYER.SQL.TABLE.SUBTITLE);
        return value + 1;
    }
    public int getNewIDInTable(String tblName) {
        return getNewIDInTable(tblName, Constant.PLAYER.SQL.COLUMN.ID);
    }
    public int getNewIDInTable(String tblName, String columnName) {
        int value = getMaxColumnValueInTbl(columnName, tblName);
        return value + 1;
    }
    //Dalnim add
    public int getNewVocaIDInSubtitleTable() {
        String column = Constant.PLAYER.SQL.COLUMN.VOCA_ID;
        int value = getMinColumnValueInTbl(column, Constant.PLAYER.SQL.TABLE.SUBTITLE);
        if (value >= 0) {
            return -1;
        }
        return value - 1;
    }

    public String getLastDateValueInTbl(String column, String table) {
        String query = QUERY.SELECT + column + QUERY.FROM + table
                + QUERY.ORDER_BY + column + QUERY.DESC
                + QUERY.LIMIT_1;
        return getStringBySql(query);
    }

    //Dalnim Add
    public int getMaxColumnValueInTbl(String column, String table) {
        String query = QUERY.SELECT + QUERY.MAX + QUERY.PARENTHESIS_OPEN + column + QUERY.PARENTHESIS_CLOSE + QUERY.FROM + table;
        return getIntBySql(query);
//        openRead();
//        Cursor cursor = database.rawQuery(query, null);
//        cursor.moveToLast();
//        int id = cursor.getInt(0);
//        cursor.close();
//        close();
//        return id;
    }

    public void updateTableAllRows(String tblName, ContentValues cv) {
        openWrite();
        database.update(tblName, cv, null, null);
        close();
    }

    public boolean updateTable(String tblName, ContentValues cv, String whereClause, String[] whereArgs) {
        int result = 0;
        try {
            openWrite();
            result = database.update(tblName, cv, whereClause, whereArgs);
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return result != 0;
    }

    public boolean updateTableByIds(String tblName, ContentValues cv, List<Integer> ids) {
        openWrite();
        String whereClause = COLUMN.ID + QUERY.IN_OPEN + TextUtils.join(",", Collections.nCopies(ids.size(), "?")) + QUERY.IN_CLOSE;
        String[] whereArgs = ids.stream().map(Object::toString).toArray(String[]::new);
        //String[] whereArgs = ids.toArray(new String[ids.size()]); //이건 integer를 바로 string으로 담기 때문에 에러가 난다.
        int result = database.update(tblName, cv, whereClause, whereArgs);
        close();
        return result != 0;
    }

    public boolean updateTableById(String tblName, ContentValues cv, int id) {
        openWrite();
        boolean result = updateTableByIdAlreadyOpen(tblName, cv, id);
        close();
        return result;
    }

    public boolean updateTableByIdAlreadyOpen(String tblName, ContentValues cv, int id) {
        int result = 0;
        try {
            result = database.update(tblName, cv, COLUMN.ID + "=?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result != 0;
    }

    public void updateTableByVocaTypeId(String tblName, ContentValues cv, int vocaType, int vocaId) {
        openWrite();
        database.update(tblName,
                cv, COLUMN.VOCA_TYPE + "=? AND " + COLUMN.VOCA_ID + "=?",
                new String[]{String.valueOf(vocaType), String.valueOf(vocaId)});
        close();
    }

    public void updateTableByVocaTypeIdBase(ContentValues cv, int vocaTypeBase, int vocaIdBase) {
        openWrite();
        database.update(TABLE.SUBTITLE,
                cv, COLUMN.VOCA_TYPE_BASE + "=? AND " + COLUMN.VOCA_ID_BASE + "=?",
                new String[]{String.valueOf(vocaTypeBase), String.valueOf(vocaIdBase)});
        close();
    }

    public void updateTableByMultipleVocaTypeId(String tblName, ContentValues cv, List<IVocaBasicItem> item) {
        String whereClause = item.stream()
                .map(e -> QUERY.PARENTHESIS_OPEN + COLUMN.VOCA_TYPE + QUERY.EQUAL + e.getVIVocaType() + QUERY.AND
                        + COLUMN.VOCA_ID + QUERY.EQUAL + e.getVIVocaId() + QUERY.PARENTHESIS_CLOSE)
                .collect(Collectors.joining( QUERY.OR));
        openWrite();
        database.update(tblName, cv, whereClause,null);
        close();
    }

    public void clearSearchHistory(String tblName) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.SEARCH_HISTORY, defaultDateTime);
        openWrite();
        database.update(tblName, cv, null,null);
        close();
    }

    public void resetSearchHistory(String tblName, int id) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.SEARCH_HISTORY, defaultDateTime);
        String whereClause = COLUMN.ID + QUERY.EQUAL + id;
        openWrite();
        database.update(tblName, cv, whereClause,null);
        close();
    }

    protected long insertIntoTable(String tblName, ContentValues cv) {
        openWrite();
        long newRowId = database.insert(tblName, null, cv);
        close();
        return newRowId;
    }

    public void deleteAllRecordsAndResetPrimaryKey(String tblName) {
        openWrite();
        database.execSQL("DELETE FROM " + tblName);
        database.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + tblName + "'");
        close();
    }

    public void deleteAllRecords(String tblName) {
        try {
            openWrite();
            database.beginTransaction();
            database.delete(tblName, null, null);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            DLog.e(TAG, "Error deleting records from table " + tblName + ": " + e.getMessage());
        } finally {
            if (database != null) {
                database.endTransaction();
            }
            close();
        }
    }
    //현재 쓰지는 않지만 미리 만들어둠.
    public <T> boolean deleteRecord(String tblName, String columnName, T value) {
        boolean deleted = false;
        openWrite();
        String whereClause = columnName + "=?";
        // whereArgs 설정, value를 문자열로 변환
        String[] whereArgs = { value.toString() };
        int rowsDeleted = database.delete(tblName, whereClause, whereArgs);
        if (rowsDeleted > 0) {
            deleted = true;
        }
        close();
        return deleted;
    }

    public boolean deleteRecordById(String tblName, String idColumnName, int id) {
        boolean deleted = false;
        openWrite();
        String whereClause = idColumnName + "=?";
        String[] whereArgs = {String.valueOf(id)};
        int rowsDeleted = database.delete(tblName, whereClause, whereArgs);
        if (rowsDeleted > 0) {
            deleted = true;
        }
        close();
        return deleted;
    }

    //=========
    public boolean deleteShortCut(int id) {
        return deleteRecordById(TABLE_GPT_TEXT_SHORT_CUT, COLUMN.ID, id);
    }

    public List<GptTextShortCut> getGptShortCutListForPopupMenu1() {
        return getGptShortCutListForPopupMenu(COLUMN.USE_POPUP_1_MENU);
    }

    private List<GptTextShortCut> getGptShortCutListForPopupMenu(String popupMenuFieldName) {
        String query = QUERY.SELECT_ALL + TABLE_GPT_TEXT_SHORT_CUT
                + QUERY.WHERE + popupMenuFieldName + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.AND + getUsedAppColumnName() + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.ASC + QUERY.COMMA + COLUMN.ID + QUERY.ASC;
        return getGptTextShortCutBySQL(query);
    }

    @NonNull
    private static String getUsedAppColumnName() {
        String usedColumnName = COLUMN.USED_ARACONV;
        if (AppFlavorUtil.isAraHangulApp()) {
            usedColumnName = COLUMN.USED_ARAHANGUL;
        }
        return usedColumnName;
    }

    public List<GptTextShortCut> getGptShortCutList() {
        String query = QUERY.SELECT_ALL + TABLE_GPT_TEXT_SHORT_CUT
                + QUERY.WHERE + COLUMN.USE_PROMPT + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.AND + getUsedAppColumnName() + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.ASC + QUERY.COMMA + COLUMN.ID + QUERY.ASC;
        return getGptTextShortCutBySQL(query);
    }

    public void updateGptShortCut(GptTextShortCut item) {
        ContentValues cv = fillContentValues(item);
        cv.put(COLUMN.DISP_ORDER, item.getDisplayOrder());
        updateTableById(TABLE_GPT_TEXT_SHORT_CUT, cv, item.getId());
    }

    public boolean insertGptShortCut(GptTextShortCut item) {
        ContentValues cv = fillContentValues(item);
        cv.put(COLUMN.SYSTEM_DEFAULT, Constant.INT_BOOLEAN.FASLE);
        cv.put(getUsedAppColumnName(), Constant.INT_BOOLEAN.TRUE);
        cv.put(COLUMN.DISP_ORDER, Constant.DEFAULT_DISP_ORDER);
        cv.put(COLUMN.USE_POPUP_1_MENU, item.getUsePopup1Menu());
        cv.put(COLUMN.USE_POPUP_2_MENU, item.getUsePopup2Menu());
        cv.put(COLUMN.USE_POPUP_3_MENU, item.getUsePopup3Menu());
        long newRowId = insertIntoTable(TABLE_GPT_TEXT_SHORT_CUT, cv);
        DLog.d("insertGptRequest", "newRowId : " + newRowId);
        return newRowId == -1 ? false : true;
    }
    private ContentValues fillContentValues(GptTextShortCut item) {
        ContentValues cv = new ContentValues();
        String meaningFld = getMeaningFldValue(enumMenuLanguage);
        String meaningValue = item.getMEANING(enumMenuLanguage);
        cv.put(COLUMN.TITLE, item.getTitle());
        cv.put(meaningFld, meaningValue);
        cv.put(COLUMN.DISP_ORDER, item.getDisplayOrder());
        return cv;
    }
    protected List<GptTextShortCut>  getGptTextShortCutBySQL(String query) {
        List<GptTextShortCut> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add(parserGptTextShortCut(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return itemList;
    }

    protected String getNameFldValue(EnumLanguage enumLanguage, Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(getNameFldValue(enumLanguage)));
    }

    protected String getNameFldValue(EnumLanguage enumLanguage) {
        String result = "";
        switch (enumLanguage) {
            case ARABIC:
                result = COLUMN.NAME_AR;
                break;
            case BENGALI:
                result = COLUMN.NAME_BN;
                break;
            case CHINESE_SIMPLIFIED:
                result = COLUMN.NAME_CH_S;
                break;
            case CHINESE_TRADITIONAL:
                result = COLUMN.NAME_CH_T;
                break;
            case CZECH:
                result = COLUMN.NAME_CS;
                break;
            case DANISH:
                result = COLUMN.NAME_DA;
                break;
            case GERMAN:
                result = COLUMN.NAME_DE;
                break;
            case GREEK:
                result = COLUMN.NAME_EL;
                break;
            case SPANISH:
                result = COLUMN.NAME_ES;
                break;
            case FINNISH:
                result = COLUMN.NAME_FI;
                break;
            case FRENCH:
                result = COLUMN.NAME_FR;
                break;
            case KOREAN:
                result = COLUMN.NAME_KO;
                break;
            case HEBREW:
                result = COLUMN.NAME_HE;
                break;
            case HINDI:
                result = COLUMN.NAME_HI;
                break;
            case CROATIAN:
                result = COLUMN.NAME_HR;
                break;
            case HUNGARIAN:
                result = COLUMN.NAME_HU;
                break;
            case INDONESIAN:
                result = COLUMN.NAME_ID;
                break;
            case ITALIAN:
                result = COLUMN.NAME_IT;
                break;
            case JAPANESE:
                result = COLUMN.NAME_JP;
                break;
            case DUTCH:
                result = COLUMN.NAME_NL;
                break;
            case NORWEGIAN:
                result = COLUMN.NAME_NO;
                break;
            case POLISH:
                result = COLUMN.NAME_PL;
                break;
            case PORTUGUESE:
                result = COLUMN.NAME_PT;
                break;
            case ROMANIAN:
                result = COLUMN.NAME_RO;
                break;
            case RUSSIAN:
                result = COLUMN.NAME_RU;
                break;
            case SLOVAK:
                result = COLUMN.NAME_SK;
                break;
            case SWEDISH:
                result = COLUMN.NAME_SV;
                break;
            case THAI:
                result = COLUMN.NAME_TH;
                break;
            case TURKISH:
                result = COLUMN.NAME_TR;
                break;
            case UKRAINIAN:
                result = COLUMN.NAME_UK;
            case VIETNAMESE:
                result = COLUMN.NAME_VI;
                break;
            default:
                result = COLUMN.NAME_ENG;
                break;
        }
        return result;
    }

    protected String getMeaningFldValue(EnumLanguage enumLanguage) {
        String result = "";
        switch (enumLanguage) {
            case ARABIC:
                result = COLUMN.MEANING_AR;
                break;
            case BENGALI:
                result = COLUMN.MEANING_BN;
                break;
            case CHINESE_SIMPLIFIED:
                result = COLUMN.MEANING_CH_S;
                break;
            case CHINESE_TRADITIONAL:
                result = COLUMN.MEANING_CH_T;
                break;
            case CZECH:
                result = COLUMN.MEANING_CS;
                break;
            case DANISH:
                result = COLUMN.MEANING_DA;
                break;
            case GERMAN:
                result = COLUMN.MEANING_DE;
                break;
            case GREEK:
                result = COLUMN.MEANING_EL;
                break;
            case SPANISH:
                result = COLUMN.MEANING_ES;
                break;
            case FINNISH:
                result = COLUMN.MEANING_FI;
                break;
            case FRENCH:
                result = COLUMN.MEANING_FR;
                break;
            case KOREAN:
                result = COLUMN.MEANING_KO;
                break;
            case HEBREW:
                result = COLUMN.MEANING_HE;
                break;
            case HINDI:
                result = COLUMN.MEANING_HI;
                break;
            case HUNGARIAN:
                result = COLUMN.MEANING_HU;
                break;
            case INDONESIAN:
                result = COLUMN.MEANING_ID;
                break;
            case ITALIAN:
                result = COLUMN.MEANING_IT;
                break;
            case JAPANESE:
                result = COLUMN.MEANING_JP;
                break;
            case DUTCH:
                result = COLUMN.MEANING_NL;
                break;
            case NORWEGIAN:
                result = COLUMN.MEANING_NO;
                break;
            case POLISH:
                result = COLUMN.MEANING_PL;
                break;
            case PORTUGUESE:
                result = COLUMN.MEANING_PT;
                break;
            case ROMANIAN:
                result = COLUMN.MEANING_RO;
                break;
            case RUSSIAN:
                result = COLUMN.MEANING_RU;
                break;
            case SLOVAK:
                result = COLUMN.MEANING_SK;
                break;
            case SWEDISH:
                result = COLUMN.MEANING_SV;
                break;
            case THAI:
                result = COLUMN.MEANING_TH;
                break;
            case TURKISH:
                result = COLUMN.MEANING_TR;
                break;
            case UKRAINIAN:
                result = COLUMN.MEANING_UK;
            case VIETNAMESE:
                result = COLUMN.MEANING_VI;
                break;
            default:
                result = COLUMN.MEANING_ENG;
                break;
        }
        return result;
    }

    public String getMeaningDetailedFldValue(EnumLanguage enumLanguage) {
        String result = "";
        switch (enumLanguage) {
            case ARABIC:
                result = COLUMN.MEANING_AR_DETAILED;
                break;
            case BENGALI:
                result = COLUMN.MEANING_BN_DETAILED;
                break;
            case CHINESE_SIMPLIFIED:
                result = COLUMN.MEANING_CH_S_DETAILED;
                break;
            case CHINESE_TRADITIONAL:
                result = COLUMN.MEANING_CH_T_DETAILED;
                break;
            case CZECH:
                result = COLUMN.MEANING_CS_DETAILED;
                break;
            case DANISH:
                result = COLUMN.MEANING_DA_DETAILED;
                break;
            case GERMAN:
                result = COLUMN.MEANING_DE_DETAILED;
                break;
            case GREEK:
                result = COLUMN.MEANING_EL_DETAILED;
                break;
            case SPANISH:
                result = COLUMN.MEANING_ES_DETAILED;
                break;
            case FINNISH:
                result = COLUMN.MEANING_FI_DETAILED;
                break;
            case FRENCH:
                result = COLUMN.MEANING_FR_DETAILED;
                break;
            case KOREAN:
                result = COLUMN.MEANING_KO_DETAILED;
                break;
            case HEBREW:
                result = COLUMN.MEANING_HE_DETAILED;
                break;
            case HINDI:
                result = COLUMN.MEANING_HI_DETAILED;
                break;
            case HUNGARIAN:
                result = COLUMN.MEANING_HU_DETAILED;
                break;
            case INDONESIAN:
                result = COLUMN.MEANING_ID_DETAILED;
                break;
            case ITALIAN:
                result = COLUMN.MEANING_IT_DETAILED;
                break;
            case JAPANESE:
                result = COLUMN.MEANING_JP_DETAILED;
                break;
            case DUTCH:
                result = COLUMN.MEANING_NL_DETAILED;
                break;
            case NORWEGIAN:
                result = COLUMN.MEANING_NO_DETAILED;
                break;
            case POLISH:
                result = COLUMN.MEANING_PL_DETAILED;
                break;
            case PORTUGUESE:
                result = COLUMN.MEANING_PT_DETAILED;
                break;
            case ROMANIAN:
                result = COLUMN.MEANING_RO_DETAILED;
                break;
            case RUSSIAN:
                result = COLUMN.MEANING_RU_DETAILED;
                break;
            case SLOVAK:
                result = COLUMN.MEANING_SK_DETAILED;
                break;
            case SWEDISH:
                result = COLUMN.MEANING_SV_DETAILED;
                break;
            case THAI:
                result = COLUMN.MEANING_TH_DETAILED;
                break;
            case TURKISH:
                result = COLUMN.MEANING_TR_DETAILED;
                break;
            case UKRAINIAN:
                result = COLUMN.MEANING_UK_DETAILED;
            case VIETNAMESE:
                result = COLUMN.MEANING_VI_DETAILED;
                break;
            default:
                result = COLUMN.MEANING_ENG_DETAILED;
                break;
        }
        return result;
    }

    protected GPT_CHAT_MESSAGE parserGptChatMessages(Cursor cursor) {
        GPT_CHAT_MESSAGE model = new GPT_CHAT_MESSAGE();
        model.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        model.setROOM_ID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ROOM_ID)));
        model.setREQUEST_RESONPSE_TYPE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.REQUEST_RESONPSE_TYPE)));
        model.setROLE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.ROLE)));
        model.setCREATE_DATE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.CREATE_DATE)));
        model.setMESSAGE_TYPE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MESSAGE_TYPE)));
        model.setMESSAGE_CONTENT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MESSAGE_CONTENT)));
        model.setFILE_PATH(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.FILE_PATH)));
        model.setPROMPT_TOKENS(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.PROMPT_TOKENS)));
        model.setCOMPLETION_TOKENS(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.COMPLETION_TOKENS)));
        model.setTOTAL_TOKENS(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.TOTAL_TOKENS)));
        model.setRUBY(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.RUBY)));
        return model;
    }

    protected GptTextShortCut parserGptTextShortCut(Cursor cursor) {
        GptTextShortCut model = new GptTextShortCut();
        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        model.setSystemDefault(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.SYSTEM_DEFAULT)));
        model.setDisplayOrder(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.DISP_ORDER)));
        model.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.TITLE)));
//        model.setEdited(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.EDITED)));
        model.setUsedPrompt(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_PROMPT)));
        model.setUsePopup1Menu(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_POPUP_1_MENU)));
        model.setUsePopup2Menu(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_POPUP_2_MENU)));
        model.setUsePopup3Menu(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_POPUP_3_MENU)));

        String languageName; // = EnumLanguage.KOREAN.getLangaugeName();;
        String languageNameMotherTongue;
        String meaning;
        String studyLangMarker = "[[STUDY_LANG_NAME]]";
        String motherTongueLangMarker = "[[MOTHER_TONGUE_LANG_NAME]]";
        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.KOREAN);
        languageNameMotherTongue = EnumLanguage.getLangNameByLanguage(enumMotherTongueLanguage, EnumLanguage.KOREAN);
//        languageName = EnumLanguage.KOREAN.getStudyLangEnglishByMotherTongueName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO));
        model.setMEANING_KO(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.ENGLISH);
//        languageName = EnumLanguage.ENGLISH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG));
        model.setMEANING_ENG(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.JAPANESE);
//        languageName = EnumLanguage.JAPANESE.getStudyLangEnglishByMotherTongueName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP));
        model.setMEANING_JP(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.CHINESE_SIMPLIFIED);
//        languageName = EnumLanguage.CHINESE_SIMPLIFIED.getStudyLangEnglishByMotherTongueName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S));
        model.setMEANING_CH_S(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.CHINESE_TRADITIONAL);
//        languageName = EnumLanguage.CHINESE_TRADITIONAL.getStudyLangEnglishByMotherTongueName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T));
        model.setMEANING_CH_T(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.FRENCH);
//        languageName = EnumLanguage.FRENCH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR));
        model.setMEANING_FR(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.RUSSIAN);
//        languageName = EnumLanguage.RUSSIAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU));
        model.setMEANING_RU(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.ARABIC);
//        languageName = EnumLanguage.ARABIC.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR));
        model.setMEANING_AR(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.BENGALI);
//        languageName = EnumLanguage.BENGALI.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN));
        model.setMEANING_BN(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.CROATIAN);
//        languageName = EnumLanguage.CROATIAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR));
        model.setMEANING_HR(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.CZECH);
//        languageName = EnumLanguage.CZECH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS));
        model.setMEANING_CS(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.DANISH);
//        languageName = EnumLanguage.DANISH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA));
        model.setMEANING_DA(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.DUTCH);
//        languageName = EnumLanguage.DUTCH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL));
        model.setMEANING_NL(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.FINNISH);
//        languageName = EnumLanguage.FINNISH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI));
        model.setMEANING_FI(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.GERMAN);
//        languageName = EnumLanguage.GERMAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE));
        model.setMEANING_DE(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.GREEK);
//        languageName = EnumLanguage.GREEK.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL));
        model.setMEANING_EL(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.HEBREW);
//        languageName = EnumLanguage.HEBREW.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE));
        model.setMEANING_HE(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.HINDI);
//        languageName = EnumLanguage.HINDI.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI));
        model.setMEANING_HI(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.HUNGARIAN);
//        languageName = EnumLanguage.HUNGARIAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU));
        model.setMEANING_HU(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.INDONESIAN);
//        languageName = EnumLanguage.INDONESIAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID));
        model.setMEANING_ID(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.ITALIAN);
//        languageName = EnumLanguage.ITALIAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT));
        model.setMEANING_IT(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.NORWEGIAN);
//        languageName = EnumLanguage.NORWEGIAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO));
        model.setMEANING_NO(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.POLISH);
//        languageName = EnumLanguage.POLISH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL));
        model.setMEANING_PL(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.PORTUGUESE);
//        languageName = EnumLanguage.PORTUGUESE.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT));
        model.setMEANING_PT(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.ROMANIAN);
//        languageName = EnumLanguage.ROMANIAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO));
        model.setMEANING_RO(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.SLOVAK);
//        languageName = EnumLanguage.SLOVAK.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK));
        model.setMEANING_SK(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.SPANISH);
//        languageName = EnumLanguage.SPANISH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES));
        model.setMEANING_ES(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.SWEDISH);
//        languageName = EnumLanguage.SWEDISH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV));
        model.setMEANING_SV(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.THAI);
//        languageName = EnumLanguage.THAI.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH));
        model.setMEANING_TH(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.TURKISH);
//        languageName = EnumLanguage.TURKISH.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR));
        model.setMEANING_TR(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.UKRAINIAN);
//        languageName = EnumLanguage.UKRAINIAN.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK));
        model.setMEANING_UK(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        languageName = EnumLanguage.getLangNameByLanguage(enumStudyLanguage, EnumLanguage.VIETNAMESE);
//        languageName = EnumLanguage.VIETNAMESE.getLangaugeName();
        meaning = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI));
        model.setMEANING_VI(Utils.isEmpty(meaning) ? "" : meaning.replace(studyLangMarker, languageName).replace(motherTongueLangMarker, languageNameMotherTongue));

        return model;
    }

    public void insertGptRequest(ChatCompletionRequest request) {
        ChatGPTRequest chatGPTRequest = request.getChatGPTRequest();
        ChatGPTRequest.MessagesInRole messagesInRole = chatGPTRequest.getMessagesInRole();
        String currentDateTime = DateUtils.getCurrentDateTimeFullFormat();
//        if (!TextUtils.isEmpty(messagesInRole.getSystem())) {
//            ContentValues cv = new ContentValues();
//            cv.put(COLUMN.ROOM_ID, 0);
//            cv.put(COLUMN.TYPE, "REQUEST");
//            cv.put(COLUMN.ROLE, "system");
//            cv.put(COLUMN.CREATE_DATE, currentDateTime);
//            cv.put(COLUMN.MESSAGE_TYPE, "TEXT");
//            cv.put(COLUMN.MESSAGE_CONTENT, messagesInRole.getSystem());
//            cv.put(COLUMN.PROMPT_TOKENS, 0);
//            cv.put(COLUMN.COMPLETION_TOKENS, 0);
//            cv.put(COLUMN.TOTAL_TOKENS, 0);
//            long newRowId = insertIntoTable(TABLE_GPT_CHAT_MESSAGE, cv);
//            DLog.d("insertGptRequest", "newRowId : " + newRowId);
//        }
        if ((!request.isMessageTypeGuide()) && (!TextUtils.isEmpty(messagesInRole.getUser()))) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.ROOM_ID, 0);
            cv.put(COLUMN.REQUEST_RESONPSE_TYPE, ChatCompletionRequest.RequestResponseType.REQUEST.name());
            cv.put(COLUMN.ROLE, "user");
            cv.put(COLUMN.CREATE_DATE, currentDateTime);
            cv.put(COLUMN.MESSAGE_TYPE, "TEXT");
            cv.put(COLUMN.MESSAGE_CONTENT, messagesInRole.getUser());
            cv.put(COLUMN.PROMPT_TOKENS, 0);
            cv.put(COLUMN.COMPLETION_TOKENS, 0);
            cv.put(COLUMN.TOTAL_TOKENS, 0);
            long newRowId = insertIntoTable(TABLE_GPT_CHAT_MESSAGE, cv);
            DLog.d("insertGptRequest", "newRowId : " + newRowId);
        }
//        if (!TextUtils.isEmpty(messagesInRole.getAssistant())) {
//            ContentValues cv = new ContentValues();
//            cv.put(COLUMN.ROOM_ID, 0);
//            cv.put(COLUMN.TYPE, "REQUEST");
//            cv.put(COLUMN.ROLE, "assistant");
//            cv.put(COLUMN.CREATE_DATE, currentDateTime);
//            cv.put(COLUMN.MESSAGE_TYPE, "TEXT");
//            cv.put(COLUMN.MESSAGE_CONTENT, messagesInRole.getAssistant());
//            cv.put(COLUMN.PROMPT_TOKENS, 0);
//            cv.put(COLUMN.COMPLETION_TOKENS, 0);
//            cv.put(COLUMN.TOTAL_TOKENS, 0);
//            long newRowId = insertIntoTable(TABLE_GPT_CHAT_MESSAGE, cv);
//            DLog.d("insertGptRequest", "newRowId : " + newRowId);
//        }
    }
    public void insertGptResponse(ChatCompletionResponse response, String content) {
        Date date = new Date();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN.ROOM_ID, 0);
        cv.put(COLUMN.REQUEST_RESONPSE_TYPE, ChatCompletionRequest.RequestResponseType.RESPONSE.name());
        cv.put(COLUMN.ROLE, "assistant");
        cv.put(COLUMN.CREATE_DATE, DateUtils.convertLongToFullPatternDateString(response.getCreated()));
        cv.put(COLUMN.MESSAGE_TYPE, "TEXT");
        cv.put(COLUMN.MESSAGE_CONTENT, content);
        cv.put(COLUMN.PROMPT_TOKENS, response.getUsage().getPromptTokens());
        cv.put(COLUMN.COMPLETION_TOKENS, response.getUsage().getCompletionTokens());
        cv.put(COLUMN.TOTAL_TOKENS, response.getUsage().getTotalTokens());
        long newRowId = insertIntoTable(TABLE_GPT_CHAT_MESSAGE, cv);
        DLog.d("insertGptRequest", "newRowId : " + newRowId);
    }

    public void insertGptResponseError(String content) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.ROOM_ID, 0);
        cv.put(COLUMN.TYPE, "RESPONSE");
        cv.put(COLUMN.ROLE, "system");
        cv.put(COLUMN.CREATE_DATE, DateUtils.getCurrentDateTimeFullFormat());
        cv.put(COLUMN.MESSAGE_TYPE, "TEXT");
        cv.put(COLUMN.MESSAGE_CONTENT, content);
        cv.put(COLUMN.PROMPT_TOKENS, 0);
        cv.put(COLUMN.COMPLETION_TOKENS, 0);
        cv.put(COLUMN.TOTAL_TOKENS, 0);
        long newRowId = insertIntoTable(TABLE_GPT_CHAT_MESSAGE, cv);
        DLog.d("insertGptRequest", "newRowId : " + newRowId);
    }

    public List<IVocaFullPlayTTSItem> getVocaListByBookIdInUserVocaBookLocal(int bookId) {
        String query = QUERY.SELECT_ALL + TABLE.USER_VOCABOOK_LOCAL
                + QUERY.WHERE + COLUMN.VOCABOOKS_ID + QUERY.EQUAL + bookId
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaListBySQLInUserVocaBookLocal(query);
    }
    protected List<IVocaFullPlayTTSItem> getVocaListBySQLInUserVocaBookLocal(String query) {
        List<IVocaFullPlayTTSItem> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add(parserUserVocaBookLocal(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return itemList;
    }
    private UserVocabookLocal parserUserVocaBookLocal(Cursor cursor) {
        UserVocabookLocal model = new UserVocabookLocal();
        model.setVIId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        model.setVIVoca(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_DISPLAY)));
        model.setDISP_ORDER(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.DISP_ORDER)));
        model.setVOCABOOKS_ID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCABOOKS_ID)));
        model.setPersonAB(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PERSON_AB)));
        model.setVIBookmark(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK)));
        model.setVIVocaKnow(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOW)));
        model.setVIVocaKnowPronounce(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOWPRONOUNCE)));

        model.setVIMeaning(null, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING)));
        model.setVIMeaningDetailed(null, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DETAILED)));
        model.setVIMeaningTts(null, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TTS)));
        return model;
    }

    protected int getRecordsCount(String query) {
        int count = 0;
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        } catch (Exception ex) {
            return 0;
        } finally {
            close();
        }
        return count;
    }

    public boolean isRecordExistsBySql(String query) {
        boolean exists = false;
        DLog.d(TAG, "isVocaIdExists - query=" + query);
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            exists = true;
        }
        cursor.close();
        close();
        return exists;
    }
}
