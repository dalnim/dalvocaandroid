package com.dalread.database.sqlite;

import static com.dalread.util.Constant.PLAYER.SQL.COLUMN;
import static com.dalread.util.Constant.PLAYER.SQL.QUERY;
import static com.dalread.util.Constant.PLAYER.SQL.TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dalread.base.EnumLanguage;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.GPT_CHAT_MESSAGE;
import com.dalread.model.GptTextShortCut;
import com.dalread.model.UserVocabookLocal;
import com.dalread.model.UserVocabooksLocal;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaInBook;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class SubDatabase extends DicSentenceSubDatabase {
    private static SubDatabase instance;
    private static String TABLE_GPT_CHAT_MESSAGE;
    private static String TABLE_GPT_TEXT_SHORT_CUT;
    private static String TABLE_USER_VOCABOOK_LOCAL;
    private static String TABLE_USER_VOCABOOKS_LOCAL;
    private static int TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_START;
    private static int TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_END;

    public static SubDatabase getInstance(Context context, String path) {
        if (instance == null || subPath == null || !subPath.equals(path))
            instance = new SubDatabase(context, path);
        return instance;
    }

    public SubDatabase(Context context, String path) {
        super(context, path);
    }

    @Override
    protected void initTableName(Context context) {
        super.initTableName(context);
        TABLE_USER_VOCABOOK_LOCAL = TABLE.USER_VOCABOOK_LOCAL;
        TABLE_USER_VOCABOOKS_LOCAL = TABLE.USER_VOCABOOKS_LOCAL;
        if (LanguageUtil.isStudyLangChinese(context)) {
            TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_START = Constant.TODAY_EXPRESSION_VOCA.CH_S.VOCA_ID_IN_SETENCE_START;
            TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_END = Constant.TODAY_EXPRESSION_VOCA.CH_S.VOCA_ID_IN_SETENCE_END;
        } else if (LanguageUtil.isStudyLangJapanese(context)) {
            TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_START = Constant.TODAY_EXPRESSION_VOCA.JP.VOCA_ID_IN_SETENCE_START;
            TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_END = Constant.TODAY_EXPRESSION_VOCA.JP.VOCA_ID_IN_SETENCE_END;
        } else if (LanguageUtil.isStudyLangKorean(context)) {
            TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_START = Constant.TODAY_EXPRESSION_VOCA.KO.VOCA_ID_IN_SETENCE_START;
            TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_END = Constant.TODAY_EXPRESSION_VOCA.KO.VOCA_ID_IN_SETENCE_END;
        } else {
            TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_START = Constant.TODAY_EXPRESSION_VOCA.ENG.VOCA_ID_IN_SETENCE_START;
            TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_END = Constant.TODAY_EXPRESSION_VOCA.ENG.VOCA_ID_IN_SETENCE_END;
        }
    }

    //이건 임시로 사용하는 코드이다. 나중에는 리스트가 아니라 객체한개만 넘겨야 한다. 그리고 문장만 리턴한다.
    public List<IVocaFullPlayTTSItem> getVocaListByVocaId(int vocaId) {
        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
        IVocaFullPlayTTSItem item = getVocaSetenceByVocaId(vocaId);
        resultList.add(item);
        return resultList;
    }

    public IVocaFullPlayTTSItem getVocaSetenceByVocaId(int vocaId) {
        String query = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE
                + QUERY.WHERE + COLUMN.ID + QUERY.EQUAL + vocaId;
        return getVocaBySQL(query);
    }

    public IVocaFullPlayTTSItem getTodayExpression(int id) {
        String query = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE + QUERY.WHERE + COLUMN.ID + QUERY.EQUAL + id;
        return getVocaBySQL(query);
    }

    public IVocaFullPlayTTSItem getTodayExpression() {
        IVocaFullPlayTTSItem voca = getTodayExpressionInTravelConversation();
        if (voca == null) {
            voca = getTodayExpressionFromValidList();
        } else if (voca == null) {
            voca = getTodayExpressionFromSentenceUnknown();
        } else if (voca == null) {
            voca = getTodayExpressionFromSentence();
        }
        return voca;
    }

    public IVocaFullPlayTTSItem getTodayExpressionInTravelConversation() {
        String query = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE
                + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.LESS + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                + QUERY.AND + COLUMN.ID + QUERY.BETWEEN + TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_START + QUERY.AND + TODAY_EXPRESSION_VOCA_ID_IN_SETENCE_END
                + QUERY.RANDOM + QUERY.LIMIT + 1;
        return getVocaBySQL(query);
    }

    public IVocaFullPlayTTSItem getTodayExpressionFromValidList() {
        String query = QUERY.SELECT_ALL_A + TABLE_DIC_SENTENCE + " A "
                + QUERY.LEFT_JOIN + TABLE.SERVER_VOCABOOK + " B ON A.ID = B.VOCA_ID "
                + QUERY.LEFT_JOIN + TABLE.SERVER_VOCABOOKS + " C ON B.VOCABOOKS_ID = C.ID "
                + QUERY.WHERE + " C.LANG_STUDY " + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + " C.USE_TODAY_EXPRESSION = 1 "
                + QUERY.AND + " A." + COLUMN.VOCA_KNOW + QUERY.LESS + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                + QUERY.RANDOM + QUERY.LIMIT + 1;
        return getVocaBySQL(query);
    }

    public IVocaFullPlayTTSItem getTodayExpressionFromSentenceUnknown() {
        String query = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE
                + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.LESS + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                + QUERY.RANDOM + QUERY.LIMIT + 1;
        return getVocaBySQL(query);
    }

    public IVocaFullPlayTTSItem getTodayExpressionFromSentence() {
        String query = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE
                + QUERY.RANDOM + QUERY.LIMIT + 1;
        return getVocaBySQL(query);
    }

    public String getBookTitle(int bookId, EnumLanguage enumLanguage) {
        String result = "";
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS + QUERY.WHERE + COLUMN.ID + QUERY.EQUAL + bookId;
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result = getNameFldValue(enumLanguage, cursor);
                break;
            }
            cursor.close();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return result;
    }

    public List<UserVocabooksLocal> getUserVocabooksLocalList() {
        String query = QUERY.SELECT_ALL + TABLE_USER_VOCABOOKS_LOCAL
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.ASC + QUERY.COMMA + COLUMN.ID + QUERY.ASC;
        return getUserVocabooksLocalListBySQL(query);
    }
    protected List<UserVocabooksLocal> getUserVocabooksLocalListBySQL(String query) {
        List<UserVocabooksLocal> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add(parserUserVocabooksLocal(cursor));
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
    private UserVocabooksLocal parserUserVocabooksLocal(Cursor cursor) {
        UserVocabooksLocal model = new UserVocabooksLocal();
        model.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        model.setPARENT_ID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.PARENT_ID)));
        model.setTITLE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.TITLE)));
        model.setVOCA_COUNT(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_COUNT)));
        model.setUSED(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USED)));
        return model;
    }
    public List<VocaBook> getServerVocaBookListByCategoryForHangulWriting(int categoryId) {
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.PARENT_ID + QUERY.EQUAL + categoryId
//                + QUERY.AND + COLUMN.USE_ALPHABET + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.AND + COLUMN.USE_VOCABOOK + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaBookListBySQL(query);
    }

    public List<VocaBook> getServerVocaBookListByKPopForHangulWriting() {
        int bookId = 186;
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.ID + QUERY.EQUAL + bookId
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaBookListBySQL(query);
    }

    public List<VocaBook> getServerVocaBookListByCategoryForHangulWriting() {
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.PARENT_ID + QUERY.EQUAL + "0"
                + QUERY.AND + COLUMN.USE_ALPHABET + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaBookListBySQL(query);
    }

    //TODO : Spport WORD too.
    public List<VocaInBook> getVocasToPracticeAlpahbetForWriting() {
        Integer VOCABOOK_ALPHABET_ONLY_PRACTICE = 2;
        //TODO : Need to get the Book ID from SQL not by the Hard coding.
        String queryBookId = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.USE_ALPHABET + QUERY.EQUAL + VOCABOOK_ALPHABET_ONLY_PRACTICE
                + QUERY.AND + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.LIMIT_1 + QUERY.FINISH;

        int bookId = 186;
        return getVocasFromAllVocaBook(String.valueOf(bookId));
    }

    public List<VocaInBook> getVocasToPracticeAlpahbetForWriting(int bookId) {
        return getVocasFromAllVocaBook(String.valueOf(bookId));
    }
//    public boolean deleteShortCut(int id) {
//        return deleteRecordById(TABLE_GPT_TEXT_SHORT_CUT, COLUMN.ID, id);
//    }
//
//    public List<GptTextShortCut> getGptShortCutListForPopupMenu1() {
//        return getGptShortCutListForPopupMenu(COLUMN.USE_POPUP_1_MENU);
//    }
//
//    private List<GptTextShortCut> getGptShortCutListForPopupMenu(String popupMenuFieldName) {
//        String query = QUERY.SELECT_ALL + TABLE_GPT_TEXT_SHORT_CUT
//                + QUERY.WHERE + popupMenuFieldName + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
//                + QUERY.AND + getUsedAppColumnName() + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
//                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.ASC + QUERY.COMMA + COLUMN.ID + QUERY.ASC;
//        return getGptTextShortCutBySQL(query);
//    }
//
//    @NonNull
//    private static String getUsedAppColumnName() {
//        String usedColumnName = COLUMN.USED_ARACONV;
//        if (AppFlavorUtil.isAraHangulApp()) {
//            usedColumnName = COLUMN.USED_ARAHANGUL;
//        }
//        return usedColumnName;
//    }

    public String getGptShortCutListForMakeRolePlay(EnumLanguage enumLanguage) {
        int idNoPromptToMakeRolePlaying = 1;
        return getGptShortCutByIdCommon(enumLanguage, idNoPromptToMakeRolePlaying);
    }

    public String getGptShortCutListForMakeRolePlayFormat(EnumLanguage enumLanguage) {
        int idNoUseThisFormat = 8;
        return getGptShortCutByIdCommon(enumLanguage, idNoUseThisFormat);
    }

    public String getGptShortCutToPracticeConversation(EnumLanguage enumLanguage, boolean isGptStartFirst) {
        int idNoPracticeConversation = 10;
        if (isGptStartFirst)
            idNoPracticeConversation = 9;
        return getGptShortCutByIdCommon(enumLanguage, idNoPracticeConversation);
    }

    public String getGptShortCutByIdCommon(EnumLanguage enumLanguage, int id) {
        String query = QUERY.SELECT_ALL + TABLE_GPT_TEXT_SHORT_CUT
                + QUERY.WHERE + COLUMN.ID + QUERY.EQUAL + id;
        List<GptTextShortCut> list = getGptTextShortCutBySQL(query);
        if (!Utils.isEmpty(list) && list.size() > 0) {
            return list.get(0).getMEANING(enumLanguage);
        }
        return "";
    }


//    public List<GptTextShortCut> getGptShortCutList() {
//        String query = QUERY.SELECT_ALL + TABLE_GPT_TEXT_SHORT_CUT
//                + QUERY.WHERE + COLUMN.USE_PROMPT + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
//                + QUERY.AND + getUsedAppColumnName() + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
//                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.ASC + QUERY.COMMA + COLUMN.ID + QUERY.ASC;
//        return getGptTextShortCutBySQL(query);
//    }
//
//    public void updateGptShortCut(GptTextShortCut item) {
//        ContentValues cv = fillContentValues(item);
//        cv.put(COLUMN.DISP_ORDER, item.getDisplayOrder());
//        updateTableById(TABLE_GPT_TEXT_SHORT_CUT, cv, item.getId());
//    }
//
//    public boolean insertGptShortCut(GptTextShortCut item) {
//        ContentValues cv = fillContentValues(item);
//        cv.put(COLUMN.SYSTEM_DEFAULT, Constant.INT_BOOLEAN.FASLE);
//        cv.put(getUsedAppColumnName(), Constant.INT_BOOLEAN.TRUE);
//        cv.put(COLUMN.DISP_ORDER, Constant.DEFAULT_DISP_ORDER);
//        cv.put(COLUMN.USE_POPUP_1_MENU, item.getUsePopup1Menu());
//        cv.put(COLUMN.USE_POPUP_2_MENU, item.getUsePopup2Menu());
//        cv.put(COLUMN.USE_POPUP_3_MENU, item.getUsePopup3Menu());
//        long newRowId = insertIntoTable(TABLE_GPT_TEXT_SHORT_CUT, cv);
//        DLog.d("insertGptRequest", "newRowId : " + newRowId);
//        return newRowId == -1 ? false : true;
//    }
    public boolean saveVocaListInUserVocaBook(UserVocabooksLocal userVocabooksLocal, List<UserVocabookLocal> itemList) {
        boolean result = true;
        boolean resultInsertUserVocabooks = insertUserVocabooks(userVocabooksLocal);
        boolean resultInsertUserVocabook = insertUserVocabookLocal(itemList);
        if ((resultInsertUserVocabooks == false) || (resultInsertUserVocabook == false)) {
            result = false;
        }
        return result;
    }
    public void updateUserVocabookLocal(UserVocabooksLocal item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.TITLE, item.getTITLE());
        updateTableById(TABLE_USER_VOCABOOKS_LOCAL, cv, item.getID());
    }
    public boolean deleteUserVocabookLocal(int id) {
        boolean result = true;
        if (deleteRecordById(TABLE_USER_VOCABOOKS_LOCAL, COLUMN.ID, id)) {
            result = deleteRecordById(TABLE_USER_VOCABOOK_LOCAL, COLUMN.VOCABOOKS_ID, id);
        } else {
            result = false;
        }
        return result;
    }
    public boolean insertUserVocabookLocal(List<UserVocabookLocal> itemList) {
        boolean result = true;
        for(UserVocabookLocal item : itemList) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.ID, item.getVIId());
            cv.put(COLUMN.VOCABOOKS_ID, item.getVOCABOOKS_ID());
            cv.put(COLUMN.VOCA_DISPLAY, item.getVIVoca());
            cv.put(COLUMN.PRONOUNCE, item.getVIPronounce());
            cv.put(COLUMN.PERSON_AB, item.getPersonAB());
            cv.put(COLUMN.DISP_ORDER, item.getDISP_ORDER());
            cv.put(COLUMN.MEANING, item.getVIMeaning(context));
            cv.put(COLUMN.MEANING_DETAILED, item.getVIMeaningDetailed(context));
            cv.put(COLUMN.MEANING_TTS, item.getVIMeaningTts(context));
            long newRowId = insertIntoTable(TABLE_USER_VOCABOOK_LOCAL, cv);
            DLog.d("insertGptRequest", "newRowId : " + newRowId);
            if (newRowId == -1) {
                result = false;
            }
        }
        return result;
    }

    public boolean insertUserVocabooks(UserVocabooksLocal item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.ID, item.getID());
        cv.put(COLUMN.TITLE, item.getTITLE());
        cv.put(COLUMN.UID, item.getUID());
        cv.put(COLUMN.LANG_STUDY, item.getLANG_STUDY());
        long newRowId = insertIntoTable(TABLE_USER_VOCABOOKS_LOCAL, cv);
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
    public void deleteAllGptChatMessages() {
        deleteAllRecordsAndResetPrimaryKey(TABLE_GPT_CHAT_MESSAGE);
    }
    public List<GPT_CHAT_MESSAGE> getGptChatMessages(int currentPage, int messagesPerPage) {
        int offset = (currentPage - 1) * messagesPerPage;
        String query = QUERY.SELECT_ALL + TABLE_GPT_CHAT_MESSAGE
//                + QUERY.WHERE + COLUMN.TYPE + QUERY.NOT_EQUAL + "\"" + ChatGPTRequest.MessageType.APP.name() + "\""
//                + QUERY.AND + COLUMN.ROLE + QUERY.NOT_EQUAL + "\"" + ChatGPTRequest.Message.Role..APP.name() + "\""
                + QUERY.ORDER_BY + COLUMN.ID + QUERY.DESC
                + QUERY.LIMIT + messagesPerPage + QUERY.OFFSET + offset;
        return getGptChatMessagesBySQL(query);

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

    protected List<GPT_CHAT_MESSAGE>  getGptChatMessagesBySQL(String query) {
        List<GPT_CHAT_MESSAGE> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add(parserGptChatMessages(cursor));
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


//    public List<IVocaFullPlayTTSItem> getVocaListByBookIdInUserVocaBookLocal(int bookId) {
//        List<IVocaFullPlayTTSItem> vocaListSentence = getVocaListByBookIdInUserVocaBookLocalBySQL(bookId);
//        return vocaListSentence;
//    }



    public boolean updateVocaInVocabook(IVocaFullItem item, int vocabookTypeCode) {
        if (vocabookTypeCode == Constant.API_VALUE.VOCABOOK_TYPE_CODE_NONE) {
            return updateVoca(item);
        } else {
            String tableName = TABLE.SERVER_VOCABOOK;
            String fldNameMeaning = getMeaningFldValue(enumMotherTongueLanguage);
            String fldNameMeaningDetailed = getMeaningDetailedFldValue(enumMotherTongueLanguage);
            int id = item.getVIIdInVocaBook();
            if (vocabookTypeCode == Constant.API_VALUE.VOCABOOK_TYPE_CODE_USER) {
                tableName = TABLE.USER_VOCABOOK_LOCAL;
                id = item.getVIId();
                fldNameMeaning = COLUMN.MEANING;
                fldNameMeaningDetailed = COLUMN.MEANING_DETAILED;
            }
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.PRONOUNCE, item.getVIPronounce());
            cv.put(fldNameMeaning, item.getVIMeaning(enumMotherTongueLanguage));
            cv.put(fldNameMeaningDetailed, item.getVIMeaningDetailed(enumMotherTongueLanguage));

            return updateTableById(tableName, cv, id);
        }
    }

}
