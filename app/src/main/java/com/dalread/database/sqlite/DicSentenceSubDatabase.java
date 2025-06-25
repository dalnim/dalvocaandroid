package com.dalread.database.sqlite;

import static com.dalread.util.Constant.PLAYER.SQL.COLUMN;
import static com.dalread.util.Constant.PLAYER.SQL.QUERY;
import static com.dalread.util.Constant.PLAYER.SQL.TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dalread.base.EnumLanguage;
import com.dalread.database.sqlite.model.DIC_SENTENCE_MODEL;
import com.dalread.database.sqlite.model.DIC_WORD_MODEL;
import com.dalread.database.sqlite.model.UserDicEngModel;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaCoreItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.model.EditVoca;
import com.dalread.model.VocaBook;
import com.dalread.model.VocaBookmark;
import com.dalread.model.VocaInBook;
import com.dalread.model.VocaKnowAndKnowpronounce;
import com.dalread.model.VocaTypeId;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.CollectionUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.LanguageUtil;
import com.dalread.util.Utils;
import com.dalread.util.VocaKnow;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DicSentenceSubDatabase extends BaseSubDatabase {
    private static DicSentenceSubDatabase instance;
    protected static String TABLE_DIC_WORD;
    protected static String TABLE_DIC_SENTENCE;
    private final int defaultNewIdInUserDicEng = 1000000;

    public static DicSentenceSubDatabase getInstance(Context contextTemp, String path) {
        context = contextTemp;
        if (instance == null || subPath == null || !subPath.equals(path))
            instance = new DicSentenceSubDatabase(context, path);
        return instance;
    }

    public DicSentenceSubDatabase(Context context, String path) {
        super(context, path);
    }

    @Override
    protected void initTableName(Context context) {
        super.initTableName(context);
        if (LanguageUtil.isStudyLangChinese(context)) {
            TABLE_DIC_WORD = TABLE.DIC_CH_S;
            TABLE_DIC_SENTENCE = TABLE.DIC_CH_S_SENTENCE;
        } else if (LanguageUtil.isStudyLangJapanese(context)) {
            TABLE_DIC_WORD = TABLE.DIC_JP;
            TABLE_DIC_SENTENCE = TABLE.DIC_JP_SENTENCE;
        } else if (LanguageUtil.isStudyLangKorean(context)) {
            TABLE_DIC_WORD = TABLE.DIC_KO;
            TABLE_DIC_SENTENCE = TABLE.DIC_KO_SENTENCE;
        } else {
            TABLE_DIC_WORD = TABLE.DIC_ENG;
            TABLE_DIC_SENTENCE = TABLE.DIC_ENG_SENTENCE;
        }
    }



    public void swapBookmark(IVocaBasicItem item) {
        if (isBookmark(item)) {
            setBookmark(item, Constant.INT_BOOLEAN.FASLE);
        } else {
            setBookmark(item, Constant.INT_BOOLEAN.TRUE);
        }
    }

    public boolean isBookmark(IVocaBasicItem item) {
        String tblName = TABLE_DIC_SENTENCE;
        if (BaseVocaKnow.isVocaTypeWord(item)) {
            tblName = TABLE_DIC_WORD;
        }
        String query = QUERY.SELECT_ALL + tblName
                + QUERY.WHERE + COLUMN.BOOKMARK + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.AND + COLUMN.ID + QUERY.EQUAL + item.getVIVocaId();
        List<IVocaFullPlayTTSItem> iVocaFullItemList = getVocaListBySQL(tblName, query);
        if (Utils.isEmpty(iVocaFullItemList)) {
            return false;
        }
        return true;
    }

    public void setBookmark(IVocaBasicItem item, int value) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.BOOKMARK, value);
        String tableName = TABLE_DIC_SENTENCE;
        if (BaseVocaKnow.isVocaTypeWord(item)) {
            tableName = TABLE_DIC_WORD;
        }
        updateTableById(tableName, cv, item.getVIId());
    }

    public void setHasVoiceFile(IVocaBasicItem item, int value) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.HAS_VOICE_FILE, value);
        String tableName = TABLE_DIC_SENTENCE;
        if (BaseVocaKnow.isVocaTypeWord(item)) {
            tableName = TABLE_DIC_WORD;
        }
        updateTableById(tableName, cv, item.getVIId());
    }

    public boolean updateVoca(IVocaBasicItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.PRONOUNCE, item.getVIPronounce());
        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVIVocaKnowPronounce());
        cv.put(getMeaningFldValue(enumMotherTongueLanguage), item.getVIMeaning(enumMotherTongueLanguage));
        cv.put(getMeaningDetailedFldValue(enumMotherTongueLanguage), item.getVIMeaningDetailed(enumMotherTongueLanguage));
        String tableName = TABLE_DIC_SENTENCE;
        if (BaseVocaKnow.isVocaTypeWord(item)) {
            tableName = TABLE_DIC_WORD;
        }
        return updateTableById(tableName, cv, item.getVIVocaId());
    }

    public boolean updateVocaInUserDicTableInDicDb(IVocaBasicItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.PRONOUNCE, item.getVIPronounce());
        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVIVocaKnowPronounce());
        cv.put(COLUMN.BOOKMARK, item.getVIBookmark());
        cv.put(COLUMN.MEANING, item.getVIMeaning(enumMotherTongueLanguage));
        cv.put(COLUMN.MEANING_DETAILED, item.getVIMeaningDetailed(enumMotherTongueLanguage));
        return updateTableById(TABLE.USER_DIC_ENG, cv, item.getVIVocaId());
    }

    //자막에서 단어를 수정하면 수정된 단어가 추가된거 일수도 있어서 EditVoca타입으로 온다. 이건 로컬에서 단어 뜻 업데이트 할때 사용한다.
    //단어의 vocaId를 리턴한다. 새로운 단어이면 (아이디가 마이너스 인것)는 추후 보완 필요.
    public void updatePhraseInformationOnDicDb(EditVoca editVoca) {
        IVocaFullItem iVocaFullItem = editVoca.getiVocaFullItem();
        boolean isExistInDicTableByVocaId = isVocaExistsInDicTableByVocaIdInDicDb(iVocaFullItem.getVIVocaId());
        boolean isExistInDicTableByVoca = isVocaExistsInDicTableByVocaInDicDb(iVocaFullItem.getVIVoca());
        boolean isExistInUserDicTableByVocaId = isVocaExistsInUserDicTableByVocaIdInDicDb(iVocaFullItem.getVIVocaId());
        boolean isExistInUserDicTableByVoca = isVocaExistsInUserDicTableByVocaInDicDb(iVocaFullItem.getVIVoca());

        //Dic 테이블에 단어를 업데이트 해준다.
        if (isExistInDicTableByVocaId || isExistInDicTableByVoca) {
            updateVoca(iVocaFullItem);
//            updateVocaInUserDicTableInDicDb(iVocaFullItem);
        }

        //UseDic테이블에 단어가 있으면 업데이트 없으면 INSERT해준다. (INSERT시에는 Dic 테이블에도 없으면 해준다.)
        if (isExistInUserDicTableByVocaId || isExistInUserDicTableByVoca) {
            updateVocaInUserDicTableInDicDb(iVocaFullItem);
        } else {
            if (!(isExistInDicTableByVocaId || isExistInDicTableByVoca)) {
                //새로운 Voca이면 기존꺼와 구분하기 위해서 최소 ID를 1000000상으로 준다.
               int newId = getMaxIdInUserDicEngTableInDicDb();
                if (newId < defaultNewIdInUserDicEng) {
                    newId = defaultNewIdInUserDicEng;
                }
                newId++;
                iVocaFullItem.setVIVocaId(newId);
            }

            if (!(isExistInDicTableByVocaId || isExistInDicTableByVoca)) {
                insertVocaInDicDicTableInDicDb(iVocaFullItem);
            }
            insertVocaInUserDicTableInDicDb(iVocaFullItem);

        }
    }
    public void insertVocaInDicDicTableInDicDb(IVocaFullItem item) {
        DLog.d(TAG, "addSubtitle - item=" + item.toString());
        ContentValues cv = new ContentValues();

        cv.put(COLUMN.ID, item.getVIVocaId());
        cv.put(COLUMN.WORD, item.getVIVoca());
        cv.put(COLUMN.WORDORI, item.getVIVoca());
        cv.put(COLUMN.WORDORI_ID, item.getVIVocaId());
        cv.put(COLUMN.WORD_DISPLAY, item.getVIVoca());
        cv.put(COLUMN.WORD_TTS, item.getVIVocaTTS());
        cv.put(COLUMN.PRONOUNCE, item.getVIPronounce());
        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVIVocaKnowPronounce());
        cv.put(COLUMN.BOOKMARK, item.getVIBookmark());

        cv.put(COLUMN.EXAMPLE_SENTENCES, ""); // 기본값 설정
        cv.put(COLUMN.IMAGE_LIST, ""); // 기본값 설정
        cv.put(COLUMN.URL_LIST, ""); // 기본값 설정
        cv.put(COLUMN.MEMO, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TTS, ""); // 기본값 설정

        cv.put(COLUMN.MEANING_AR_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_BN_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CH_T_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CH_S_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CS_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_DA_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_DE_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_EL_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ENG_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ES_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_FI_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_FR_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HE_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HI_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HR_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HU_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ID_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_IT_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_JP_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_KO_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_NL_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_NO_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_PL_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_PT_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_RO_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_RU_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_SK_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_SV_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TH_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TR_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_UK_FOR_HIDE_ALL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_VI_FOR_HIDE_ALL, ""); // 기본값 설정

        cv.put(COLUMN.MEANING_AR_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_AR_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_AR, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_BN_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_BN_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_BN, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CH_T_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CH_T_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CH_T, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CH_S_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CH_S_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CH_S, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CS_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CS_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_CS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_DA_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_DA_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_DA, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_DE_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_DE_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_DE, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_EL_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_EL_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_EL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ENG_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ENG_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ENG, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ES_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ES_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ES, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_FI_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_FI_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_FI, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_FR_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_FR_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_FR, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HE_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HE_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HE, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HI_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HI_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HI, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HR_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HR_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HR, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HU_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HU_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_HU, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ID_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ID_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_ID, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_IT_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_IT_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_IT, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_JP_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_JP_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_JP, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_KO_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_KO_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_KO, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_NL_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_NL_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_NL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_NO_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_NO_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_NO, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_PL_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_PL_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_PL, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_PT_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_PT_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_PT, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_RO_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_RO_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_RO, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_RU_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_RU_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_RU, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_SK_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_SK_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_SK, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_SV_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_SV_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_SV, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TH_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TH_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TH, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TR_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TR_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_TR, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_UK_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_UK_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_UK, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_VI_DETAILED, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_VI_TTS, ""); // 기본값 설정
        cv.put(COLUMN.MEANING_VI, ""); // 기본값 설정

        cv.put(getMeaningFldValue(enumMotherTongueLanguage), item.getVIMeaning(enumMotherTongueLanguage));
        cv.put(getMeaningDetailedFldValue(enumMotherTongueLanguage), item.getVIMeaningDetailed(enumMotherTongueLanguage));
        insertIntoTable(TABLE_DIC_WORD, cv);
    }

    public void insertVocaInUserDicTableInDicDb(IVocaFullItem item) {
        DLog.d(TAG, "addSubtitle - item=" + item.toString());
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.ID, item.getVIVocaId());
        cv.put(COLUMN.VOCA, item.getVIVoca());
        cv.put(COLUMN.PRONOUNCE, item.getVIPronounce());
        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVIVocaKnowPronounce());
        cv.put(COLUMN.BOOKMARK, item.getVIBookmark());
        cv.put(COLUMN.MEANING, item.getVIMeaning(enumMotherTongueLanguage));
        cv.put(COLUMN.MEANING_DETAILED, item.getVIMeaningDetailed(enumMotherTongueLanguage));
        insertIntoTable(TABLE.USER_DIC_ENG, cv);
    }

    public boolean isVocaExistsInDicTableByVocaIdInDicDb(int vocaId) {
        String query = "SELECT 1 FROM " + TABLE_DIC_WORD + " WHERE " + COLUMN.ID + " = " + vocaId + " LIMIT 1";
        return isRecordExistsBySql(query);
    }

    public boolean isVocaExistsInDicTableByVocaInDicDb(String word) {
        String query = "SELECT 1 FROM " + TABLE_DIC_WORD + " WHERE " + COLUMN.WORD + QUERY.EQUAL + "'" + word + "'" + " LIMIT 1";
        return isRecordExistsBySql(query);
    }

    public boolean isVocaExistsInUserDicTableByVocaIdInDicDb(int vocaId) {
        String query = "SELECT 1 FROM " + TABLE.USER_DIC_ENG + " WHERE " + COLUMN.ID + " = " + vocaId + " LIMIT 1";
        return isRecordExistsBySql(query);
    }

    public boolean isVocaExistsInUserDicTableByVocaInDicDb(String word) {
        String query = "SELECT 1 FROM " + TABLE.USER_DIC_ENG + " WHERE " + COLUMN.VOCA + QUERY.EQUAL + "'" + word + "'" + " LIMIT 1";
        return isRecordExistsBySql(query);
    }

    public int getMaxIdInUserDicEngTableInDicDb() {
        int maxVocaId = -1; // 기본값으로 -1 설정, VocaId가 음수가 될 수 없으므로 이를 통해 값이 설정되지 않았음을 확인
        String query = "SELECT MAX(" + COLUMN.ID + ") AS max_id FROM " + TABLE.USER_DIC_ENG;
        DLog.d(TAG, "getMaxVocaId - query=" + query);
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            maxVocaId = cursor.getInt(cursor.getColumnIndexOrThrow("max_id"));
        }
        cursor.close();
        close();
        return maxVocaId;
    }

    public void updateVocaKnow(IVocaBasicItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVIVocaKnowPronounce());
        String tableName = TABLE_DIC_SENTENCE;
        if (BaseVocaKnow.isVocaTypeWord(item)) {
            tableName = TABLE_DIC_WORD;
        }
        updateTableById(tableName, cv, item.getVIId());
    }

    public void updateVocaKnowInUserDicInDicDb(IVocaBasicItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVIVocaKnowPronounce());
        updateTableById(TABLE.USER_DIC_ENG, cv, item.getVIId());
    }

    public List<UserDicEngModel> getUserDicEngList() {
        try {
            List<UserDicEngModel> list = new ArrayList<>();
            String query = QUERY.SELECT_ALL + TABLE.USER_DIC_ENG;
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                final UserDicEngModel item = parserUserDicEngModel(cursor);
                list.add(item);
                cursor.moveToNext();
            }
            cursor.close();
            return list;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return new ArrayList<>();
    }
    private UserDicEngModel parserUserDicEngModel(Cursor cursor) {
        final UserDicEngModel model = new UserDicEngModel();
        model.setVIId(cursor.getInt(cursor.getColumnIndexOrThrow(Constant.PLAYER.SQL.COLUMN.ID)));
        model.setVIVoca(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA)));
        model.setVIPronounce(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PRONOUNCE)));
        model.setVIBookmark(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK)));
        model.setVIVocaKnow(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOW)));
        model.setVIVocaKnowPronounce(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOWPRONOUNCE)));
        model.setVIMeaning(null, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING)));
        model.setVIMeaningDetailed(null, cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DETAILED)));
        return model;
    }
    public List<IVocaFullPlayTTSItem> getVocaBookmarkList() {
        String queryWord = QUERY.SELECT_ALL + TABLE_DIC_WORD
                + QUERY.WHERE + COLUMN.BOOKMARK + QUERY.NOT_EQUAL + Constant.INT_BOOLEAN.FASLE
                + QUERY.ORDER_BY + COLUMN.WORD_DISPLAY + QUERY.FINISH;
        List<IVocaFullPlayTTSItem> vocaListWord = getVocaListBySQL(TABLE_DIC_WORD, queryWord);
        String querySentence = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE
                + QUERY.WHERE + COLUMN.BOOKMARK + QUERY.NOT_EQUAL + Constant.INT_BOOLEAN.FASLE
                + QUERY.ORDER_BY + COLUMN.WORD_DISPLAY + QUERY.FINISH;
        List<IVocaFullPlayTTSItem> vocaListSentence = getVocaListBySQL(TABLE_DIC_SENTENCE, querySentence);

        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
        resultList.addAll(vocaListWord);
        resultList.addAll(vocaListSentence);
        Collections.sort(resultList, Comparator.nullsLast(Comparator.comparing(IVocaFullPlayTTSItem::getVIVoca)));
        return resultList;
    }

    public List<IVocaFullPlayTTSItem> getUserVocaKnowAndBookmarkList() {
        List<IVocaFullPlayTTSItem> vocaListWord = getVocaKnowAndBookmarkListByQuery(TABLE_DIC_WORD);
        List<IVocaFullPlayTTSItem> vocaListSentence = getVocaKnowAndBookmarkListByQuery(TABLE_DIC_SENTENCE);

        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
        resultList.addAll(vocaListWord);
        resultList.addAll(vocaListSentence);
        Collections.sort(resultList, Comparator.nullsLast(Comparator.comparing(IVocaFullPlayTTSItem::getVIVoca)));
        return resultList;
    }

    private List<IVocaFullPlayTTSItem> getVocaKnowAndBookmarkListByQuery(String tableName) {
        String query = QUERY.SELECT_ALL + tableName
                + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.GREATER + Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED
                + QUERY.OR + COLUMN.VOCA_KNOWPRONOUNCE + QUERY.GREATER + Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED
                + QUERY.OR + COLUMN.BOOKMARK + QUERY.NOT_EQUAL +  Constant.INT_BOOLEAN.FASLE
                + QUERY.FINISH;
        return getVocaListBySQL(tableName, query);
    }

    public void updateVocaKnowAndBookmarkList(List<IVocaFullPlayTTSItem> list) {
        for (IVocaFullPlayTTSItem item : list) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
            cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVIVocaKnowPronounce());
            cv.put(COLUMN.BOOKMARK, item.getVIBookmark());
            if (VocaKnow.isVocaTypeWord(item)) {
                updateTableById(TABLE_DIC_WORD, cv, item.getVIId());
            } else {
                updateTableById(TABLE_DIC_SENTENCE, cv, item.getVIId());
            }
        }
    }

    public boolean isExistVocaInDicTable(String voca) {
        String query = QUERY.SELECT_COUNT + TABLE_DIC_WORD + QUERY.WHERE + COLUMN.WORD + QUERY.EQUAL + "'" + voca + "'" + ";";
        return getRecordsCount(query) > 0;
    }

    public void updateUserDicEngListInDicTableInDicDbMain(List<UserDicEngModel> list) {
        List<UserDicEngModel> remainList = updateUserDicEngListInDicTableInDic(list);
//        int newVocaId = defaultNewIdInUserDicEng;
        for (UserDicEngModel item : list) {
//            item.setVIId(newVocaId++);
            if (item.getVIId() >= defaultNewIdInUserDicEng) {
                insertVocaInDicDicTableInDicDb(item);
            }
            insertVocaInUserDicTableInDicDb(item);
        }
    }
    public int getVocaIdByVocaInDicTableInDicDb(String word) {
        int id = -1;
        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD + QUERY.WHERE + COLUMN.WORD +  QUERY.EQUAL + "'" + word + "'" + ";";
        DLog.d(TAG, "getDicModelByVocaId - query=" + query);
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID));
        }
        cursor.close();
        close();
        return id;
    }
    private List<UserDicEngModel> updateUserDicEngListInDicTableInDic(List<UserDicEngModel> list) {
        List<UserDicEngModel> resultList = new ArrayList<>();
        for (UserDicEngModel item : list) {
            if (isExistVocaInDicTable(item.getVIVoca())) {
                boolean result = updateDicTableInDicDbByVoca(item);
                if (result) {
//                if (item.getVIId() >= defaultNewIdInUserDicEng) {
                    int vocaId = getVocaIdByVocaInDicTableInDicDb(item.getVIVoca());
                    if (vocaId > 0) {
                        item.setVIId(vocaId);
                    }
//                }
                }
                resultList.add(item);
            } else {
                resultList.add(item);
            }
        }
        return resultList;
    }
    private boolean updateDicTableInDicDbByVoca(UserDicEngModel item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.WORD, item.getVIVoca());
        cv.put(COLUMN.PRONOUNCE, item.getVIPronounce());
        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, item.getVIVocaKnowPronounce());
        cv.put(COLUMN.BOOKMARK, item.getVIBookmark());
        cv.put(getMeaningFldValue(enumMotherTongueLanguage), item.getVIMeaning(enumMotherTongueLanguage));
        cv.put(getMeaningDetailedFldValue(enumMotherTongueLanguage), item.getVIMeaningDetailed(enumMotherTongueLanguage));
        String whereClause = COLUMN.WORD + " = ?";
        String[] whereArgs = { item.getVIVoca() };

        return updateTable(TABLE_DIC_WORD, cv, whereClause, whereArgs);
    }
    public VocaBook getServerVocaBookListById(int id) {
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.ID + QUERY.EQUAL + id;
        List<VocaBook> vocaBookList = getVocaBookListBySQL(query);
        if (Utils.isEmpty(vocaBookList))
            return null;
        return vocaBookList.get(0);
    }

    public List<VocaBook> getServerVocaBookListByCategory(int categoryId) {
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.PARENT_ID + QUERY.EQUAL + categoryId
//                + QUERY.AND + COLUMN.USE_ALPHABET + QUERY.EQUAL + Constant.INT_BOOLEAN.FASLE
                + QUERY.AND + COLUMN.USED_ARACONV + QUERY.GREATER + Constant.VOCABOOKS.USED.NOT_USE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaBookListBySQL(query);
    }

    public List<VocaBook> getServerVocaBookListByCategory() {
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.PARENT_ID + QUERY.EQUAL + "0"
                + QUERY.AND + COLUMN.USE_ALPHABET + QUERY.EQUAL + Constant.INT_BOOLEAN.FASLE
                + QUERY.AND + COLUMN.USED_ARACONV + QUERY.GREATER + Constant.VOCABOOKS.USED.NOT_USE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaBookListBySQL(query);
    }

    public List<VocaBook> getServerVocaBookListForEnglishWords() {
        String bookId = "80, 86";
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.PARENT_ID + QUERY.IN_OPEN + bookId + QUERY.IN_CLOSE
                + QUERY.AND + COLUMN.USED_ARACONV + QUERY.GREATER + Constant.VOCABOOKS.USED.NOT_USE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaBookListBySQL(query);
    }

    public List<VocaBook> getServerVocaBookListByRecordingList() {
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOKS
                + QUERY.WHERE + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.VOCA_COUNT + QUERY.GREATER + 0
//                + QUERY.AND + COLUMN.USED_ARACONV + QUERY.GREATER + Constant.VOCABOOKS.USED.USE_FOR_BUY
                + QUERY.AND + COLUMN.USE_RECORDING + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaBookListBySQL(query);
    }

    public List<VocaInBook> getVocasFromAllVocaBook(String vocaBooksIdListByComma) {
        String queryVocaTypeId = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOK
                + QUERY.WHERE + COLUMN.USED + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.AND + COLUMN.LANG_STUDY + QUERY.EQUAL + STUDY_LANG_CODE
                + QUERY.AND + COLUMN.VOCABOOKS_ID + QUERY.IN_OPEN + vocaBooksIdListByComma + QUERY.IN_CLOSE
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        List<VocaTypeId> vocaTypeIdList = getVocaTypeIdListBySQL(queryVocaTypeId);
        String vocaWordIdListByComma = vocaTypeIdList.stream()
                .filter(e -> e.getVIVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD)
                .map(e -> e.getVIVocaId().toString())
                .collect(Collectors.joining(","));
        String queryWord = QUERY.SELECT_ALL + TABLE_DIC_WORD
                + QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + vocaWordIdListByComma + QUERY.IN_CLOSE + QUERY.FINISH;
        String vocaSentenceIdListByComma = vocaTypeIdList.stream()
                .filter(e -> e.getVIVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE)
                .map(e -> e.getVIVocaId().toString())
                .collect(Collectors.joining(","));
        String querySentence = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE
                + QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + vocaSentenceIdListByComma + QUERY.IN_CLOSE + QUERY.FINISH;

        List<VocaInBook> vocaInBookListWord = getVocaInBookListBySQL(TABLE_DIC_WORD, queryWord);
        List<VocaInBook> vocaInBookListSentence = getVocaInBookListBySQL(TABLE_DIC_SENTENCE, querySentence);


        Map<String, VocaInBook> mapWord = CollectionUtil.convertListToMapVocaInBook(vocaInBookListWord);
        Map<String, VocaInBook> mapSentence = CollectionUtil.convertListToMapVocaInBook(vocaInBookListSentence);
        List<VocaInBook> resultList = new ArrayList<>();
        for(VocaTypeId vocaTypeId : vocaTypeIdList) {
            if (mapWord.containsKey(vocaTypeId.getVIVocaTypeId())) {
                resultList.add(mapWord.get(vocaTypeId.getVIVocaTypeId()));
            }
            if (mapSentence.containsKey(vocaTypeId.getVIVocaTypeId())) {
                resultList.add(mapSentence.get(vocaTypeId.getVIVocaTypeId()));
            }
        }
        return resultList;
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

    // 이건 VocaBook에 있는 뜻을 가져오기 때문에 회화 내용이 자연스러움. (전화상황이 아닌대도 Hello가 여보세요라고 표기되지 않음) 대신 VocaBook에 번역을 따로 해두어어야함.
    // VocaBook에 번역이 없으면 DIC_ENG_SENTENCE에서 뜻을 가져오는 로직은 추가해야함.
    public List<IVocaFullPlayTTSItem> getVocaListByBookIdInVocaBook(int bookId) {
        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
        List<IVocaFullPlayTTSItem> vocaListWord = getVocaListByBookIdInVocaBook(TABLE_DIC_WORD, Constant.API_VALUE.VALUE_VOCA_TYPE_WORD, bookId);
        List<IVocaFullPlayTTSItem> vocaListSentence = getVocaListByBookIdInVocaBook(TABLE_DIC_SENTENCE, Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE, bookId);
        Map<String, IVocaFullPlayTTSItem> mapVocaList = new HashMap<>();
        mapVocaList.putAll(CollectionUtil.convertListToMap(vocaListWord));
        mapVocaList.putAll(CollectionUtil.convertListToMap(vocaListSentence));

        List<IVocaFullPlayTTSItem> vocaListFromDic = getVocaListByBookId(bookId);
        Map<String, IVocaFullPlayTTSItem> mapVocaListFromDic = CollectionUtil.convertListToMap(vocaListFromDic);

        //book의 대화를 순서대로 가져오기 위해서 필요하다.
        String queryVocaTypeId = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOK
                + QUERY.WHERE + COLUMN.VOCABOOKS_ID + QUERY.EQUAL + bookId
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        List<VocaTypeId> vocaTypeIdList = getVocaTypeIdListBySQL(queryVocaTypeId);

        EnumLanguage motherTongueLanguage = EnumLanguage.getMotherTongueLanguage(context);

        for (VocaTypeId vocaTypeId : vocaTypeIdList) {
            String key = vocaTypeId.getVIVocaTypeId();
            if (mapVocaList.containsKey(key)) {
                IVocaFullPlayTTSItem voca = mapVocaList.get(key);
                resultList.add(voca);
                if (mapVocaListFromDic.containsKey(key)) {
                    IVocaFullPlayTTSItem model = mapVocaListFromDic.get(key);
                    if (Utils.isEmpty(voca.getVIMeaning(motherTongueLanguage))) {
                        voca.setVIMeaning(motherTongueLanguage, model.getVIMeaning(motherTongueLanguage));
                    }
                    voca.setVIBookmark(model.getVIBookmark());
                    voca.setVIVocaKnow(model.getVIVocaKnow());
                    voca.setVIVocaKnowPronounce(model.getVIVocaKnowPronounce());
                    voca.setVIVoiceFile(model.hasVIVoiceFile() ? Constant.INT_BOOLEAN.TRUE : Constant.INT_BOOLEAN.FASLE);
                    voca.setVIPronounce(model.getVIPronounce());
                    voca.setVIVocaTTS(model.getVIVocaTTS());
                    voca.setVISEARCH_HISTORY(model.getVISEARCH_HISTORY());

                }
            }
        }
        return resultList;
    }


    /*
     * 이건 VocaBook이 아니라 DIC_ENG_SENTENCE에 있는 뜻을 가져옴. AB회화의 경우는 getVocaListByBookIdInVocaBook로 가져올것. 아니면 뜻이 이상하게 달릴수 있음. 전화상황이 아닌대도 Hello가 여보세요라고 표기됨.
     * 아라한글은 이걸 써서 DIC_KO_SENTENCE에서 가져오게 해야 하나?
     */
    public List<IVocaFullPlayTTSItem> getVocaListByBookId(int bookId) {
        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
        List<IVocaFullPlayTTSItem> vocaListWord = getVocaListByBookId(TABLE_DIC_WORD, Constant.API_VALUE.VALUE_VOCA_TYPE_WORD, bookId);
        List<IVocaFullPlayTTSItem> vocaListSentence = getVocaListByBookId(TABLE_DIC_SENTENCE, Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE, bookId);
        Map<String, IVocaFullPlayTTSItem> mapWord = CollectionUtil.convertListToMap(vocaListWord);// vocaListWord.stream().collect(Collectors.toMap(e -> e.getVIVocaTypeId(), e -> e, (p1, p2) -> p1));
        Map<String, IVocaFullPlayTTSItem> mapSentence = CollectionUtil.convertListToMap(vocaListSentence);//vocaListSentence.stream().collect(Collectors.toMap(e -> e.getVIVocaTypeId(), e -> e, (p1, p2) -> p1));

        String queryVocaTypeId = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOK
                + QUERY.WHERE + COLUMN.VOCABOOKS_ID + QUERY.EQUAL + bookId
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        List<VocaTypeId> vocaTypeIdList = getVocaTypeIdListBySQL(queryVocaTypeId);

        for(VocaTypeId vocaTypeId : vocaTypeIdList) {
            if (mapWord.containsKey(vocaTypeId.getVIVocaTypeId())) {
                resultList.add(mapWord.get(vocaTypeId.getVIVocaTypeId()));
            }
            if (mapSentence.containsKey(vocaTypeId.getVIVocaTypeId())) {
                resultList.add(mapSentence.get(vocaTypeId.getVIVocaTypeId()));
            }
        }
        return resultList;
    }

    private List<IVocaFullPlayTTSItem> getVocaListByBookIdInVocaBook(String tblName, int vocaType, int bookId) {
        String query = QUERY.SELECT_ALL + TABLE.SERVER_VOCABOOK
                + QUERY.WHERE + COLUMN.VOCABOOKS_ID + QUERY.EQUAL + bookId + QUERY.AND + COLUMN.VOCA_TYPE + QUERY.EQUAL + vocaType
                + QUERY.ORDER_BY + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaListBySQLInVocaBook(tblName, query);
    }
    protected List<IVocaFullPlayTTSItem> getVocaListBySQLInVocaBook(String tblName, String query) {
        List<IVocaFullPlayTTSItem> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (tblName.equals(TABLE_DIC_SENTENCE))
                    itemList.add(parserDicSentenceModelInVocaBook(cursor));
                else
                    itemList.add(parserDicWordModelInVocaBook(cursor));
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

    /*
     * @deprecated Replaced by {@link #getVocaListByBookIdInVocaBook(String tblName, int vocaType, int bookId)}
     */
    @Deprecated
    private List<IVocaFullPlayTTSItem> getVocaListByBookId(String tblName, int vocaType, int bookId) {
        String query = QUERY.SELECT_ALL_A_PERSON_AB + tblName + " A "
                + QUERY.LEFT_JOIN + TABLE.SERVER_VOCABOOK + " B ON A." + COLUMN.ID + QUERY.EQUAL + " B." + COLUMN.VOCA_ID
                + QUERY.LEFT_JOIN + TABLE.SERVER_VOCABOOKS + " C ON B." + COLUMN.VOCABOOKS_ID + QUERY.EQUAL + " C." + COLUMN.ID
                + QUERY.WHERE + " C." + COLUMN.ID + QUERY.EQUAL + bookId + QUERY.AND + " B." + COLUMN.VOCA_TYPE + QUERY.EQUAL + vocaType
                + QUERY.ORDER_BY + " B." + COLUMN.DISP_ORDER + QUERY.FINISH;
        return getVocaListBySQL(tblName, query);
    }

    public List<IVocaFullPlayTTSItem> getPlayTTSVocaListByVocaIdList(List<? extends IVocaCoreItem> itemList) {
        String idsWord = itemList.stream().filter(e -> BaseVocaKnow.isVocaTypeWord(e)).map(e -> e.getVIVocaId().toString()).collect(Collectors.joining(","));
        String idsSentence = itemList.stream().filter(e -> BaseVocaKnow.isVocaTypeSentence(e)).map(e -> e.getVIVocaId().toString()).collect(Collectors.joining(","));
        String queryWord = QUERY.SELECT_ALL + TABLE_DIC_WORD + QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + idsWord + QUERY.IN_CLOSE + QUERY.FINISH;
        String querySentence = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE + QUERY.WHERE + COLUMN.ID + QUERY.IN_OPEN + idsSentence + QUERY.IN_CLOSE + QUERY.FINISH;
        List<IVocaFullPlayTTSItem> listWord = getPlayTTSVocaListBySQL(TABLE_DIC_WORD, queryWord);
        List<IVocaFullPlayTTSItem> listSentence = getPlayTTSVocaListBySQL(TABLE_DIC_SENTENCE, querySentence);
        List<IVocaFullPlayTTSItem> resultList = new ArrayList<>();
        resultList.addAll(listWord);
        resultList.addAll(listSentence);
        Collections.sort(resultList, Comparator.nullsLast(Comparator.comparing(IVocaFullPlayTTSItem::getVIVoca)));
        return resultList;
    }

    //TODO : Need to supprt TABLE_DIC_WORD
    public List<IVocaFullPlayTTSItem> getAllSentenceVocaList() {
        String query = QUERY.SELECT_ALL + TABLE_DIC_SENTENCE;
        return getPlayTTSVocaListBySQL(TABLE_DIC_SENTENCE, query);
    }

    public List<IVocaFullPlayTTSItem> getAllWordVocaListStartWith(String keyword, boolean isSearchInStudyLang) {
        if (Utils.isEmpty(keyword)) {
            return new ArrayList<>();
        } else {
            String fld = COLUMN.WORD;
            if (!isSearchInStudyLang) {
                fld = getMeaningFldValue(enumMotherTongueLanguage);
            }
            String query = QUERY.SELECT_ALL + TABLE_DIC_WORD + QUERY.WHERE + fld + QUERY.LIKE + "\"" + keyword + "%\"";
            return getPlayTTSVocaListBySQL(TABLE_DIC_WORD, query);
        }
    }

    public List<IVocaFullPlayTTSItem> getWordSearchHistory(String keyword, boolean isSearchInStudyLang) {
        String table = TABLE_DIC_WORD;
        String fld = COLUMN.WORD;
        if (!isSearchInStudyLang) {
            fld = getMeaningFldValue(enumMotherTongueLanguage);
        }
        String query = QUERY.SELECT_ALL + table
                + QUERY.WHERE + COLUMN.SEARCH_HISTORY + QUERY.GREATER + defaultDateTime
                + QUERY.ORDER_BY + COLUMN.SEARCH_HISTORY + QUERY.DESC + QUERY.FINISH;
        if (!Utils.isEmpty(keyword)) {
            query = QUERY.SELECT_ALL + table
                    + QUERY.WHERE + COLUMN.SEARCH_HISTORY + QUERY.GREATER + defaultDateTime
                    + QUERY.AND + fld + QUERY.LIKE + "\"%" + keyword + "%\""
                    + QUERY.ORDER_BY + COLUMN.SEARCH_HISTORY + QUERY.DESC + QUERY.FINISH;
        }
        return getPlayTTSVocaListBySQL(table, query);
    }

    public void updateWordSeachyHistory(IVocaFullPlayTTSItem item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.SEARCH_HISTORY, item.getVISEARCH_HISTORY());
        updateTableById(TABLE_DIC_WORD, cv, item.getVIId());
    }

    protected IVocaFullPlayTTSItem getVocaBySQL(String query) {
        IVocaFullPlayTTSItem item = null;
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = parserDicSentenceModel(cursor);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }

        return item;
    }

    public IVocaFullPlayTTSItem getWordInDicTableInDicDbById(int id) {
        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD +
                QUERY.WHERE + COLUMN.ID + QUERY.EQUAL + id;
        List<IVocaFullPlayTTSItem> list = getVocaListBySQL(TABLE_DIC_WORD, query);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    public IVocaFullPlayTTSItem getWordInDicTableInDicDbByWord(String word) {
        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD +
                QUERY.WHERE + COLUMN.WORD + QUERY.EQUAL + "'" + word + "'" ;
        List<IVocaFullPlayTTSItem> list = getVocaListBySQL(TABLE_DIC_WORD, query);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    protected List<IVocaFullPlayTTSItem> getVocaListBySQL(String tblName, String query) {
        List<IVocaFullPlayTTSItem> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (tblName.equals(TABLE_DIC_SENTENCE))
                    itemList.add(parserDicSentenceModel(cursor));
                else
                    itemList.add(parserDicWordModel(cursor));
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

    /*
     * @deprecated Replaced by {@link #getVocaListBySQL(String, String)}
     */
    @Deprecated
    private List<IVocaFullPlayTTSItem> getVocaListBySQL(String query) {
        List<IVocaFullPlayTTSItem> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add(parserDicSentenceModel(cursor));
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

    protected List<VocaBook> getVocaBookListBySQL(String query) {
        List<VocaBook> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add(parserVocaBookModel(cursor));
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

    private List<VocaInBook> getVocaInBookListBySQL(String tblName, String query) {
        List<VocaInBook> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                VocaInBook vocaInBook = parserVocaInBookModel(cursor);
                vocaInBook.setStudyLang(EnumLanguage.getStudyLanguageCode(context));
                if (tblName.equals(TABLE_DIC_SENTENCE))
                    vocaInBook.setVIVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
                else
                    vocaInBook.setVIVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_WORD);

                itemList.add(vocaInBook);
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
    /*
     * @deprecated Replaced by {@link #getVocaInBookListBySQL(String, String)}
     */
    @Deprecated
    private List<VocaInBook> getVocaInBookListBySQL(String query) {
        List<VocaInBook> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                VocaInBook vocaInBook = parserVocaInBookModel(cursor);
                vocaInBook.setStudyLang(EnumLanguage.getStudyLanguageCode(context));
                vocaInBook.setVIVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
                itemList.add(vocaInBook);
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

    public List<VocaTypeId> getVocaTypeIdListBySQL(String query) {
        List<VocaTypeId> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add(parserToVocaTypeId(cursor));
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

    protected List<IVocaFullPlayTTSItem> getPlayTTSVocaListBySQL(String tblName, String query) {
        List<IVocaFullPlayTTSItem> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (tblName.equals(TABLE_DIC_SENTENCE))
                    itemList.add(parserDicSentenceModel(cursor));
                else
                    itemList.add(parserDicWordModel(cursor));

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

    /*
     * @deprecated Replaced by {@link #getPlayTTSVocaListBySQL(String, String)}
     */
    @Deprecated
    private List<IVocaFullPlayTTSItem> getPlayTTSVocaListBySQL(String query) {
        List<IVocaFullPlayTTSItem> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add((IVocaFullPlayTTSItem) parserDicSentenceModel(cursor));
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

    private VocaBook parserVocaBookModel(Cursor cursor) {
        VocaBook model = new VocaBook();
        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        model.setStudyLang(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.LANG_STUDY)));
        model.setWordCount(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_COUNT)));
        if (AppFlavorUtil.isAraHangulApp()) {
            model.setUSED(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USED)));
        } else {
            model.setUSED(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USED_ARACONV)));
        }
        model.setName(cursor.getString(cursor.getColumnIndexOrThrow(getNameFldValue(enumMotherTongueLanguage))));
        model.setNameStudyLang(cursor.getString(cursor.getColumnIndexOrThrow(getNameFldValue(enumStudyLanguage))));
        return model;
    }

    private VocaInBook parserVocaInBookModel(Cursor cursor) {
        VocaInBook model = new VocaInBook();
        model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        model.setVocaId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        model.setVoca(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD)));
        model.setVocaDisplay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD_DISPLAY)));
        //TODO : Set meaning by the Mother Tongue, if not existed, then add it English meaning
        if (LanguageUtil.isMotherTongueLangKorean(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_TTS)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_DETAILED)));
        } else if (LanguageUtil.isMotherTongueLangJapanese(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_TTS)));
        } else if (LanguageUtil.isMotherTongueLangThai(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_TTS)));
        } else if (LanguageUtil.isMotherTongueLangSpanish(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_TTS)));
        } else if (LanguageUtil.isMotherTongueLangRussian(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_TTS)));
        } else if (LanguageUtil.isMotherTongueLangRomanian(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_TTS)));
        } else if (LanguageUtil.isMotherTongueLangProtuguese(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_TTS)));
        } else if (LanguageUtil.isMotherTongueLangPolish(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_TTS)));
        } else if (LanguageUtil.isMotherTongueLangNorwegian(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_TTS)));
        } else if (LanguageUtil.isMotherTongueLangItalian(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_TTS)));
        } else if (LanguageUtil.isMotherTongueLangIndonesian(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_TTS)));
        } else if (LanguageUtil.isMotherTongueLangHindi(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_TTS)));
        } else if (LanguageUtil.isMotherTongueLangHebrew(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_TTS)));
        } else if (LanguageUtil.isMotherTongueLangGreek(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_TTS)));
        } else if (LanguageUtil.isMotherTongueLangGerman(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_TTS)));
        } else if (LanguageUtil.isMotherTongueLangFrench(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_TTS)));
        } else if (LanguageUtil.isMotherTongueLangFinnish(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_TTS)));
        } else if (LanguageUtil.isMotherTongueLangDutch(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_TTS)));
        } else if (LanguageUtil.isMotherTongueLangDanish(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_TTS)));
        } else if (LanguageUtil.isMotherTongueLangCroatian(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_TTS)));
        } else if (LanguageUtil.isMotherTongueLangCH_S(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_TTS)));
        } else if (LanguageUtil.isMotherTongueLangCH_T(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_TTS)));
        } else if (LanguageUtil.isMotherTongueLangBengali(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_TTS)));
        } else if (LanguageUtil.isMotherTongueLangArabic(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_TTS)));
        } else if (LanguageUtil.isMotherTongueLangCzech(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_TTS)));
        } else if (LanguageUtil.isMotherTongueLangSlovak(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_TTS)));
        } else if (LanguageUtil.isMotherTongueLangSwedish(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_TTS)));
        } else if (LanguageUtil.isMotherTongueLangTurksih(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_TTS)));
        } else if (LanguageUtil.isMotherTongueLangVietnamese(context)) {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_TTS)));
        } else {
            model.setMeaning(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG)));
            model.setMeaningDetailed(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_DETAILED)));
            model.setMeaningTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_TTS)));
        }
        model.setMeaningEnglish(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG)));
        model.setVIBookmark(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK)));
        model.setVocaKnow(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOW)));
        model.setVocaKnowPronounce(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOWPRONOUNCE)));
        model.setVIVoiceFile(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.HAS_VOICE_FILE)));
        model.setVIPronounce(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PRONOUNCE)));
        model.setVIVocaTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD_TTS)));
        return model;
    }
    private VocaTypeId parserToVocaTypeId(Cursor cursor) {
        int vocaType = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_TYPE));
        int vocaId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID));
        return new VocaTypeId(vocaId, vocaType);
    }

    private DIC_SENTENCE_MODEL parserDicSentenceModelInVocaBook(Cursor cursor) {
        DIC_SENTENCE_MODEL model = new DIC_SENTENCE_MODEL();
        model.setVIIdInVocaBook(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
//        model.setVIVocaBookType(Constant.API_VALUE.VOCABOOK_TYPE_CODE_SERVER);
        model.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID)));
//        model.setVOCA_ID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
//        model.setVOCA_TYPE(Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
        model.setWORD(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_DISPLAY)));
        model.setWORD_DISPLAY(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_DISPLAY)));
        model.setMEANING_AR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR)));
        model.setMEANING_AR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_TTS)));
        model.setMEANING_AR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_DETAILED)));
        model.setMEANING_BN(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN)));
        model.setMEANING_BN_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_TTS)));
        model.setMEANING_BN_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_DETAILED)));
        model.setMEANING_CH_S(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S)));
        model.setMEANING_CH_S_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_TTS)));
        model.setMEANING_CH_S_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_DETAILED)));
        model.setMEANING_CH_T(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T)));
        model.setMEANING_CH_T_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_TTS)));
        model.setMEANING_CH_T_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_DETAILED)));
        model.setMEANING_CS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS)));
        model.setMEANING_CS_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_TTS)));
        model.setMEANING_CS_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_DETAILED)));
        model.setMEANING_DA(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA)));
        model.setMEANING_DA_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_TTS)));
        model.setMEANING_DA_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_DETAILED)));
        model.setMEANING_DE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE)));
        model.setMEANING_DE_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_TTS)));
        model.setMEANING_DE_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_DETAILED)));
        model.setMEANING_EL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL)));
        model.setMEANING_EL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_TTS)));
        model.setMEANING_EL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_DETAILED)));
        model.setMEANING_ENG(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG)));
        model.setMEANING_ENG_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_TTS)));
        model.setMEANING_ENG_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_DETAILED)));
        model.setMEANING_ES(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES)));
        model.setMEANING_ES_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_TTS)));
        model.setMEANING_ES_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_DETAILED)));
        model.setMEANING_FI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI)));
        model.setMEANING_FI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_TTS)));
        model.setMEANING_FI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_DETAILED)));
        model.setMEANING_FR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR)));
        model.setMEANING_FR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_TTS)));
        model.setMEANING_FR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_DETAILED)));
        model.setMEANING_HE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE)));
        model.setMEANING_HE_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_TTS)));
        model.setMEANING_HE_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_DETAILED)));
        model.setMEANING_HI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI)));
        model.setMEANING_HI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_TTS)));
        model.setMEANING_HI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_DETAILED)));
        model.setMEANING_HR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR)));
        model.setMEANING_HR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_TTS)));
        model.setMEANING_HR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_DETAILED)));
        model.setMEANING_HU(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU)));
        model.setMEANING_HU_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU_TTS)));
        model.setMEANING_HU_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU_DETAILED)));
        model.setMEANING_ID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID)));
        model.setMEANING_ID_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_TTS)));
        model.setMEANING_ID_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_DETAILED)));
        model.setMEANING_IT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT)));
        model.setMEANING_IT_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_TTS)));
        model.setMEANING_IT_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_DETAILED)));
        model.setMEANING_JP(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP)));
        model.setMEANING_JP_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_TTS)));
        model.setMEANING_JP_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_DETAILED)));
        model.setMEANING_KO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO)));
        model.setMEANING_KO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_TTS)));
        model.setMEANING_KO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_DETAILED)));
        model.setMEANING_NL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL)));
        model.setMEANING_NL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_TTS)));
        model.setMEANING_NL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_DETAILED)));
        model.setMEANING_NO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO)));
        model.setMEANING_NO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_TTS)));
        model.setMEANING_NO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_DETAILED)));
        model.setMEANING_PL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL)));
        model.setMEANING_PL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_TTS)));
        model.setMEANING_PL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_DETAILED)));
        model.setMEANING_PT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT)));
        model.setMEANING_PT_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_TTS)));
        model.setMEANING_PT_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_DETAILED)));
        model.setMEANING_RO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO)));
        model.setMEANING_RO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_TTS)));
        model.setMEANING_RO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_DETAILED)));
        model.setMEANING_RU(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU)));
        model.setMEANING_RU_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_TTS)));
        model.setMEANING_RU_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_DETAILED)));
        model.setMEANING_SK(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK)));
        model.setMEANING_SK_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_TTS)));
        model.setMEANING_SK_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_DETAILED)));
        model.setMEANING_SV(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV)));
        model.setMEANING_SV_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_TTS)));
        model.setMEANING_SV_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_DETAILED)));
        model.setMEANING_TH(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH)));
        model.setMEANING_TH_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_TTS)));
        model.setMEANING_TH_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_DETAILED)));
        model.setMEANING_TR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR)));
        model.setMEANING_TR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_TTS)));
        model.setMEANING_TR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_DETAILED)));
        model.setMEANING_UK(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK)));
        model.setMEANING_UK_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK_TTS)));
        model.setMEANING_UK_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK_DETAILED)));
        model.setMEANING_VI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI)));
        model.setMEANING_VI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_TTS)));
        model.setMEANING_VI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_DETAILED)));
        if (cursor.getColumnIndex(COLUMN.PERSON_AB) >= 0) {
            model.setPersonAB(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PERSON_AB)));
        }

        return model;
    }

    private DIC_SENTENCE_MODEL parserDicSentenceModel(Cursor cursor) {
        DIC_SENTENCE_MODEL model = new DIC_SENTENCE_MODEL();
        model.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
//        model.setVOCA_ID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
//        model.setVOCA_TYPE(Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
        model.setWORD(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD)));
        String wordDisplay = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD_DISPLAY));
        model.setWORD_DISPLAY(wordDisplay);
        if (LanguageUtil.isStudyLangChinese(context)) {
            try {
                String pronounce = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PRONOUNCE));
                if (Utils.isEmpty(pronounce) && !Utils.isEmpty(wordDisplay)) {
                    model.setPRONOUNCE(PinyinHelper.convertToPinyinString(wordDisplay, " ", PinyinFormat.WITH_TONE_MARK));
                }
            } catch (PinyinException e) {
                DLog.d("PinyinConversion", "Failed to convert to Pinyin: ");
            } catch (Exception e) {
                DLog.d("PinyinConversion", "Failed to convert to Pinyin: ");
            }
        }
        model.setMEANING_AR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR)));
        model.setMEANING_AR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_TTS)));
        model.setMEANING_AR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_DETAILED)));
        model.setMEANING_BN(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN)));
        model.setMEANING_BN_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_TTS)));
        model.setMEANING_BN_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_DETAILED)));
        model.setMEANING_CH_S(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S)));
        model.setMEANING_CH_S_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_TTS)));
        model.setMEANING_CH_S_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_DETAILED)));
        model.setMEANING_CH_T(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T)));
        model.setMEANING_CH_T_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_TTS)));
        model.setMEANING_CH_T_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_DETAILED)));
        model.setMEANING_CS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS)));
        model.setMEANING_CS_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_TTS)));
        model.setMEANING_CS_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_DETAILED)));
        model.setMEANING_DA(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA)));
        model.setMEANING_DA_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_TTS)));
        model.setMEANING_DA_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_DETAILED)));
        model.setMEANING_DE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE)));
        model.setMEANING_DE_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_TTS)));
        model.setMEANING_DE_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_DETAILED)));
        model.setMEANING_EL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL)));
        model.setMEANING_EL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_TTS)));
        model.setMEANING_EL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_DETAILED)));
        model.setMEANING_ENG(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG)));
        model.setMEANING_ENG_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_TTS)));
        model.setMEANING_ENG_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_DETAILED)));
        model.setMEANING_ES(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES)));
        model.setMEANING_ES_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_TTS)));
        model.setMEANING_ES_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_DETAILED)));
        model.setMEANING_FI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI)));
        model.setMEANING_FI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_TTS)));
        model.setMEANING_FI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_DETAILED)));
        model.setMEANING_FR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR)));
        model.setMEANING_FR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_TTS)));
        model.setMEANING_FR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_DETAILED)));
        model.setMEANING_HE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE)));
        model.setMEANING_HE_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_TTS)));
        model.setMEANING_HE_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_DETAILED)));
        model.setMEANING_HI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI)));
        model.setMEANING_HI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_TTS)));
        model.setMEANING_HI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_DETAILED)));
        model.setMEANING_HR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR)));
        model.setMEANING_HR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_TTS)));
        model.setMEANING_HR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_DETAILED)));
        model.setMEANING_HU(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU)));
        model.setMEANING_HU_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU_TTS)));
        model.setMEANING_HU_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU_DETAILED)));
        model.setMEANING_ID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID)));
        model.setMEANING_ID_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_TTS)));
        model.setMEANING_ID_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_DETAILED)));
        model.setMEANING_IT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT)));
        model.setMEANING_IT_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_TTS)));
        model.setMEANING_IT_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_DETAILED)));
        model.setMEANING_JP(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP)));
        model.setMEANING_JP_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_TTS)));
        model.setMEANING_JP_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_DETAILED)));
        model.setMEANING_KO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO)));
        model.setMEANING_KO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_TTS)));
        model.setMEANING_KO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_DETAILED)));
        model.setMEANING_NL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL)));
        model.setMEANING_NL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_TTS)));
        model.setMEANING_NL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_DETAILED)));
        model.setMEANING_NO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO)));
        model.setMEANING_NO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_TTS)));
        model.setMEANING_NO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_DETAILED)));
        model.setMEANING_PL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL)));
        model.setMEANING_PL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_TTS)));
        model.setMEANING_PL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_DETAILED)));
        model.setMEANING_PT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT)));
        model.setMEANING_PT_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_TTS)));
        model.setMEANING_PT_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_DETAILED)));
        model.setMEANING_RO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO)));
        model.setMEANING_RO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_TTS)));
        model.setMEANING_RO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_DETAILED)));
        model.setMEANING_RU(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU)));
        model.setMEANING_RU_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_TTS)));
        model.setMEANING_RU_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_DETAILED)));
        model.setMEANING_SK(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK)));
        model.setMEANING_SK_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_TTS)));
        model.setMEANING_SK_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_DETAILED)));
        model.setMEANING_SV(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV)));
        model.setMEANING_SV_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_TTS)));
        model.setMEANING_SV_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_DETAILED)));
        model.setMEANING_TH(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH)));
        model.setMEANING_TH_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_TTS)));
        model.setMEANING_TH_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_DETAILED)));
        model.setMEANING_TR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR)));
        model.setMEANING_TR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_TTS)));
        model.setMEANING_TR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_DETAILED)));
        model.setMEANING_UK(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK)));
        model.setMEANING_UK_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK_TTS)));
        model.setMEANING_UK_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK_DETAILED)));
        model.setMEANING_VI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI)));
        model.setMEANING_VI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_TTS)));
        model.setMEANING_VI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_DETAILED)));
        model.setBOOKMARK(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK)));
        model.setVOCA_KNOW(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOW)));
        model.setVOCA_KNOWPRONOUNCE(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOWPRONOUNCE)));
        model.setVIVoiceFile(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.HAS_VOICE_FILE)));
        model.setVIVocaTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD_TTS)));
        if (cursor.getColumnIndex(COLUMN.SEARCH_HISTORY) >= 0) {
            model.setSEARCH_HISTORY(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.SEARCH_HISTORY)));
        }
        if (cursor.getColumnIndex(COLUMN.PERSON_AB) >= 0) {
            model.setPersonAB(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PERSON_AB)));
        }

        return model;
    }

    private DIC_WORD_MODEL parserDicWordModelInVocaBook(Cursor cursor) {
        DIC_WORD_MODEL model = new DIC_WORD_MODEL();
        model.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_ID)));
        model.setVIIdInVocaBook(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
//        model.setVIVocaBookType(Constant.API_VALUE.VOCABOOK_TYPE_CODE_SERVER);
        model.setWORD(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_DISPLAY)));
        model.setWORD_DISPLAY(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.VOCA_DISPLAY)));
        model.setMEANING_AR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR)));
        model.setMEANING_AR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_TTS)));
        model.setMEANING_AR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_DETAILED)));
        model.setMEANING_BN(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN)));
        model.setMEANING_BN_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_TTS)));
        model.setMEANING_BN_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_DETAILED)));
        model.setMEANING_CH_S(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S)));
        model.setMEANING_CH_S_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_TTS)));
        model.setMEANING_CH_S_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_DETAILED)));
        model.setMEANING_CH_T(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T)));
        model.setMEANING_CH_T_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_TTS)));
        model.setMEANING_CH_T_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_DETAILED)));
        model.setMEANING_CS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS)));
        model.setMEANING_CS_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_TTS)));
        model.setMEANING_CS_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_DETAILED)));
        model.setMEANING_DA(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA)));
        model.setMEANING_DA_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_TTS)));
        model.setMEANING_DA_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_DETAILED)));
        model.setMEANING_DE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE)));
        model.setMEANING_DE_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_TTS)));
        model.setMEANING_DE_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_DETAILED)));
        model.setMEANING_EL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL)));
        model.setMEANING_EL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_TTS)));
        model.setMEANING_EL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_DETAILED)));
        model.setMEANING_ENG(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG)));
        model.setMEANING_ENG_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_TTS)));
        model.setMEANING_ENG_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_DETAILED)));
        model.setMEANING_ES(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES)));
        model.setMEANING_ES_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_TTS)));
        model.setMEANING_ES_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_DETAILED)));
        model.setMEANING_FI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI)));
        model.setMEANING_FI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_TTS)));
        model.setMEANING_FI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_DETAILED)));
        model.setMEANING_FR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR)));
        model.setMEANING_FR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_TTS)));
        model.setMEANING_FR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_DETAILED)));
        model.setMEANING_HE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE)));
        model.setMEANING_HE_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_TTS)));
        model.setMEANING_HE_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_DETAILED)));
        model.setMEANING_HI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI)));
        model.setMEANING_HI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_TTS)));
        model.setMEANING_HI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_DETAILED)));
        model.setMEANING_HR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR)));
        model.setMEANING_HR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_TTS)));
        model.setMEANING_HR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_DETAILED)));
        model.setMEANING_HU(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU)));
        model.setMEANING_HU_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU_TTS)));
        model.setMEANING_HU_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU_DETAILED)));
        model.setMEANING_ID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID)));
        model.setMEANING_ID_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_TTS)));
        model.setMEANING_ID_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_DETAILED)));
        model.setMEANING_IT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT)));
        model.setMEANING_IT_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_TTS)));
        model.setMEANING_IT_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_DETAILED)));
        model.setMEANING_JP(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP)));
        model.setMEANING_JP_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_TTS)));
        model.setMEANING_JP_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_DETAILED)));
        model.setMEANING_KO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO)));
        model.setMEANING_KO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_TTS)));
        model.setMEANING_KO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_DETAILED)));
        model.setMEANING_NL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL)));
        model.setMEANING_NL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_TTS)));
        model.setMEANING_NL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_DETAILED)));
        model.setMEANING_NO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO)));
        model.setMEANING_NO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_TTS)));
        model.setMEANING_NO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_DETAILED)));
        model.setMEANING_PL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL)));
        model.setMEANING_PL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_TTS)));
        model.setMEANING_PL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_DETAILED)));
        model.setMEANING_PT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT)));
        model.setMEANING_PT_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_TTS)));
        model.setMEANING_PT_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_DETAILED)));
        model.setMEANING_RO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO)));
        model.setMEANING_RO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_TTS)));
        model.setMEANING_RO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_DETAILED)));
        model.setMEANING_RU(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU)));
        model.setMEANING_RU_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_TTS)));
        model.setMEANING_RU_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_DETAILED)));
        model.setMEANING_SK(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK)));
        model.setMEANING_SK_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_TTS)));
        model.setMEANING_SK_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_DETAILED)));
        model.setMEANING_SV(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV)));
        model.setMEANING_SV_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_TTS)));
        model.setMEANING_SV_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_DETAILED)));
        model.setMEANING_TH(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH)));
        model.setMEANING_TH_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_TTS)));
        model.setMEANING_TH_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_DETAILED)));
        model.setMEANING_TR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR)));
        model.setMEANING_TR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_TTS)));
        model.setMEANING_TR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_DETAILED)));
        model.setMEANING_UK(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK)));
        model.setMEANING_UK_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK_TTS)));
        model.setMEANING_UK_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK_DETAILED)));
        model.setMEANING_VI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI)));
        model.setMEANING_VI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_TTS)));
        model.setMEANING_VI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_DETAILED)));
        if (cursor.getColumnIndex(COLUMN.PERSON_AB) >= 0) {
            model.setPersonAB(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PERSON_AB)));
        }
        return model;
    }

    private DIC_WORD_MODEL parserDicWordModel(Cursor cursor) {
        DIC_WORD_MODEL model = new DIC_WORD_MODEL();
        model.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
//        model.setVOCA_ID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
//        model.setVOCA_TYPE(Constant.API_VALUE.VALUE_VOCA_TYPE_SENTENCE);
        model.setWORD(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD)));
        model.setWORD_DISPLAY(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD_DISPLAY)));
        model.setMEANING_AR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR)));
        model.setMEANING_AR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_TTS)));
        model.setMEANING_AR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_AR_DETAILED)));
        model.setMEANING_BN(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN)));
        model.setMEANING_BN_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_TTS)));
        model.setMEANING_BN_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_BN_DETAILED)));
        model.setMEANING_CH_S(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S)));
        model.setMEANING_CH_S_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_TTS)));
        model.setMEANING_CH_S_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_S_DETAILED)));
        model.setMEANING_CH_T(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T)));
        model.setMEANING_CH_T_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_TTS)));
        model.setMEANING_CH_T_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CH_T_DETAILED)));
        model.setMEANING_CS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS)));
        model.setMEANING_CS_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_TTS)));
        model.setMEANING_CS_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_CS_DETAILED)));
        model.setMEANING_DA(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA)));
        model.setMEANING_DA_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_TTS)));
        model.setMEANING_DA_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DA_DETAILED)));
        model.setMEANING_DE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE)));
        model.setMEANING_DE_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_TTS)));
        model.setMEANING_DE_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_DE_DETAILED)));
        model.setMEANING_EL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL)));
        model.setMEANING_EL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_TTS)));
        model.setMEANING_EL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_EL_DETAILED)));
        model.setMEANING_ENG(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG)));
        model.setMEANING_ENG_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_TTS)));
        model.setMEANING_ENG_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ENG_DETAILED)));
        model.setMEANING_ES(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES)));
        model.setMEANING_ES_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_TTS)));
        model.setMEANING_ES_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ES_DETAILED)));
        model.setMEANING_FI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI)));
        model.setMEANING_FI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_TTS)));
        model.setMEANING_FI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FI_DETAILED)));
        model.setMEANING_FR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR)));
        model.setMEANING_FR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_TTS)));
        model.setMEANING_FR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_FR_DETAILED)));
        model.setMEANING_HE(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE)));
        model.setMEANING_HE_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_TTS)));
        model.setMEANING_HE_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HE_DETAILED)));
        model.setMEANING_HI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI)));
        model.setMEANING_HI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_TTS)));
        model.setMEANING_HI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HI_DETAILED)));
        model.setMEANING_HR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR)));
        model.setMEANING_HR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_TTS)));
        model.setMEANING_HR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HR_DETAILED)));
        model.setMEANING_HU(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU)));
        model.setMEANING_HU_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU_TTS)));
        model.setMEANING_HU_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_HU_DETAILED)));
        model.setMEANING_ID(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID)));
        model.setMEANING_ID_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_TTS)));
        model.setMEANING_ID_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_ID_DETAILED)));
        model.setMEANING_IT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT)));
        model.setMEANING_IT_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_TTS)));
        model.setMEANING_IT_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_IT_DETAILED)));
        model.setMEANING_JP(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP)));
        model.setMEANING_JP_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_TTS)));
        model.setMEANING_JP_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_JP_DETAILED)));
        model.setMEANING_KO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO)));
        model.setMEANING_KO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_TTS)));
        model.setMEANING_KO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_KO_DETAILED)));
        model.setMEANING_NL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL)));
        model.setMEANING_NL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_TTS)));
        model.setMEANING_NL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NL_DETAILED)));
        model.setMEANING_NO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO)));
        model.setMEANING_NO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_TTS)));
        model.setMEANING_NO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_NO_DETAILED)));
        model.setMEANING_PL(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL)));
        model.setMEANING_PL_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_TTS)));
        model.setMEANING_PL_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PL_DETAILED)));
        model.setMEANING_PT(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT)));
        model.setMEANING_PT_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_TTS)));
        model.setMEANING_PT_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_PT_DETAILED)));
        model.setMEANING_RO(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO)));
        model.setMEANING_RO_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_TTS)));
        model.setMEANING_RO_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RO_DETAILED)));
        model.setMEANING_RU(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU)));
        model.setMEANING_RU_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_TTS)));
        model.setMEANING_RU_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_RU_DETAILED)));
        model.setMEANING_SK(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK)));
        model.setMEANING_SK_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_TTS)));
        model.setMEANING_SK_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SK_DETAILED)));
        model.setMEANING_SV(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV)));
        model.setMEANING_SV_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_TTS)));
        model.setMEANING_SV_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_SV_DETAILED)));
        model.setMEANING_TH(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH)));
        model.setMEANING_TH_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_TTS)));
        model.setMEANING_TH_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TH_DETAILED)));
        model.setMEANING_TR(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR)));
        model.setMEANING_TR_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_TTS)));
        model.setMEANING_TR_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_TR_DETAILED)));
        model.setMEANING_UK(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK)));
        model.setMEANING_UK_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK_TTS)));
        model.setMEANING_UK_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_UK_DETAILED)));
        model.setMEANING_VI(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI)));
        model.setMEANING_VI_TTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_TTS)));
        model.setMEANING_VI_DETAILED(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.MEANING_VI_DETAILED)));
        model.setBOOKMARK(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK)));
        model.setVOCA_KNOW(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOW)));
        model.setVOCA_KNOWPRONOUNCE(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOCA_KNOWPRONOUNCE)));
        model.setVIVoiceFile(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.HAS_VOICE_FILE)));
        model.setVIPronounce(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.PRONOUNCE)));
        model.setVIVocaTTS(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.WORD_TTS)));
        model.setSEARCH_HISTORY(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.SEARCH_HISTORY)));
        return model;
    }

    public void resetVocaKnowAndBookmark() {
        resetVocaKnowAndBookmark(TABLE_DIC_WORD);
        resetVocaKnowAndBookmark(TABLE_DIC_SENTENCE);
    }
    private void resetVocaKnowAndBookmark(String tableName) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);
        cv.put(COLUMN.BOOKMARK, Constant.INT_BOOLEAN.FASLE);
        openWrite();
        database.update(tableName, cv, null,null);
        close();
    }

    public void updateVocaKnowAndVocaKnowpronounce(List<VocaKnowAndKnowpronounce> vocaKnowKnowpronounceList) {
        List<Integer> word_VOCA_KNOW_AMKI_GRADE_1 = new ArrayList<>();
        List<Integer> word_VOCA_KNOW_AMKI_GRADE_2 = new ArrayList<>();
        List<Integer> word_VOCA_KNOW_UNKNOWN = new ArrayList<>();
        List<Integer> word_VOCA_KNOW_KNOWN = new ArrayList<>();
        List<Integer> word_VOCA_KNOW_EXCLUDE = new ArrayList<>();
        List<Integer> word_VOCA_KNOW_NOTINDIC = new ArrayList<>();

        List<Integer> wordPronounce_VOCA_KNOW_AMKI_GRADE_1 = new ArrayList<>();
        List<Integer> wordPronounce_VOCA_KNOW_AMKI_GRADE_2 = new ArrayList<>();
        List<Integer> wordPronounce_VOCA_KNOW_UNKNOWN = new ArrayList<>();
        List<Integer> wordPronounce_VOCA_KNOW_KNOWN = new ArrayList<>();
        List<Integer> wordPronounce_VOCA_KNOW_EXCLUDE = new ArrayList<>();
        List<Integer> wordPronounce_VOCA_KNOW_NOTINDIC = new ArrayList<>();


        List<Integer> sentence_VOCA_KNOW_AMKI_GRADE_1 = new ArrayList<>();
        List<Integer> sentence_VOCA_KNOW_AMKI_GRADE_2 = new ArrayList<>();
        List<Integer> sentence_VOCA_KNOW_UNKNOWN = new ArrayList<>();
        List<Integer> sentence_VOCA_KNOW_KNOWN = new ArrayList<>();
        List<Integer> sentence_VOCA_KNOW_EXCLUDE = new ArrayList<>();
        List<Integer> sentence_VOCA_KNOW_NOTINDIC = new ArrayList<>();

        List<Integer> sentencePronounce_VOCA_KNOW_AMKI_GRADE_1 = new ArrayList<>();
        List<Integer> sentencePronounce_VOCA_KNOW_AMKI_GRADE_2 = new ArrayList<>();
        List<Integer> sentencePronounce_VOCA_KNOW_UNKNOWN = new ArrayList<>();
        List<Integer> sentencePronounce_VOCA_KNOW_KNOWN = new ArrayList<>();
        List<Integer> sentencePronounce_VOCA_KNOW_EXCLUDE = new ArrayList<>();
        List<Integer> sentencePronounce_VOCA_KNOW_NOTINDIC = new ArrayList<>();

        for(int i = 0; i < vocaKnowKnowpronounceList.size(); i++) {
            VocaKnowAndKnowpronounce vocaKnowValue = vocaKnowKnowpronounceList.get(i);
            List<Integer> listVocaKnowAndKnowpronounce = new ArrayList<>();
            listVocaKnowAndKnowpronounce.add(vocaKnowValue.getVocaKnow());
            listVocaKnowAndKnowpronounce.add(vocaKnowValue.getVocaKnowPronounce());
            if (BaseVocaKnow.isVocaTypeWord(vocaKnowValue)) {
                switch (vocaKnowValue.getVIVocaKnow()) {
                    case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1:
                        word_VOCA_KNOW_AMKI_GRADE_1.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2:
                        word_VOCA_KNOW_AMKI_GRADE_2.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN:
                        word_VOCA_KNOW_UNKNOWN.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_KNOWN:
                        word_VOCA_KNOW_KNOWN.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_EXCLUDE:
                        word_VOCA_KNOW_EXCLUDE.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_NOTINDIC:
                        word_VOCA_KNOW_NOTINDIC.add(vocaKnowValue.getVIVocaId());
                        break;
                }

                switch (vocaKnowValue.getVIVocaKnowPronounce()) {
                    case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1:
                        wordPronounce_VOCA_KNOW_AMKI_GRADE_1.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2:
                        wordPronounce_VOCA_KNOW_AMKI_GRADE_2.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN:
                        wordPronounce_VOCA_KNOW_UNKNOWN.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_KNOWN:
                        wordPronounce_VOCA_KNOW_KNOWN.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_EXCLUDE:
                        wordPronounce_VOCA_KNOW_EXCLUDE.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_NOTINDIC:
                        wordPronounce_VOCA_KNOW_NOTINDIC.add(vocaKnowValue.getVIVocaId());
                        break;
                }

            } else if (BaseVocaKnow.isVocaTypeSentence(vocaKnowValue)) {
                switch (vocaKnowValue.getVIVocaKnow()) {
                    case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1:
                        sentence_VOCA_KNOW_AMKI_GRADE_1.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2:
                        sentence_VOCA_KNOW_AMKI_GRADE_2.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN:
                        sentence_VOCA_KNOW_UNKNOWN.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_KNOWN:
                        sentence_VOCA_KNOW_KNOWN.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_EXCLUDE:
                        sentence_VOCA_KNOW_EXCLUDE.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_NOTINDIC:
                        sentence_VOCA_KNOW_NOTINDIC.add(vocaKnowValue.getVIVocaId());
                        break;
                }

                switch (vocaKnowValue.getVIVocaKnowPronounce()) {
                    case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1:
                        sentencePronounce_VOCA_KNOW_AMKI_GRADE_1.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2:
                        sentencePronounce_VOCA_KNOW_AMKI_GRADE_2.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN:
                        sentencePronounce_VOCA_KNOW_UNKNOWN.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_KNOWN:
                        sentencePronounce_VOCA_KNOW_KNOWN.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_EXCLUDE:
                        sentencePronounce_VOCA_KNOW_EXCLUDE.add(vocaKnowValue.getVIVocaId());
                        break;
                    case Constant.VOCA_KNOW.VOCA_KNOW_NOTINDIC:
                        sentencePronounce_VOCA_KNOW_NOTINDIC.add(vocaKnowValue.getVIVocaId());
                        break;
                }
            }
        }

        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1, word_VOCA_KNOW_AMKI_GRADE_1);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2, word_VOCA_KNOW_AMKI_GRADE_2);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN, word_VOCA_KNOW_UNKNOWN);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, word_VOCA_KNOW_KNOWN);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_EXCLUDE, word_VOCA_KNOW_EXCLUDE);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTINDIC, word_VOCA_KNOW_NOTINDIC);

        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1, wordPronounce_VOCA_KNOW_AMKI_GRADE_1);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2, wordPronounce_VOCA_KNOW_AMKI_GRADE_2);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN, wordPronounce_VOCA_KNOW_UNKNOWN);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, wordPronounce_VOCA_KNOW_KNOWN);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_EXCLUDE, wordPronounce_VOCA_KNOW_EXCLUDE);
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_NOTINDIC, wordPronounce_VOCA_KNOW_NOTINDIC);

        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1, sentence_VOCA_KNOW_AMKI_GRADE_1);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2, sentence_VOCA_KNOW_AMKI_GRADE_2);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN, sentence_VOCA_KNOW_UNKNOWN);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, sentence_VOCA_KNOW_KNOWN);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_EXCLUDE, sentence_VOCA_KNOW_EXCLUDE);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTINDIC, sentence_VOCA_KNOW_NOTINDIC);

        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1, sentencePronounce_VOCA_KNOW_AMKI_GRADE_1);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2, sentencePronounce_VOCA_KNOW_AMKI_GRADE_2);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN, sentencePronounce_VOCA_KNOW_UNKNOWN);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN, sentencePronounce_VOCA_KNOW_KNOWN);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_EXCLUDE, sentencePronounce_VOCA_KNOW_EXCLUDE);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_NOTINDIC, sentencePronounce_VOCA_KNOW_NOTINDIC);
    }

    public void updateVocaBookmark(List<VocaBookmark> vocaBookmarkList) {
        List<Integer> word_BOOKMARK = new ArrayList<>();
        List<Integer> sentence_BOOKMARK = new ArrayList<>();

        for(int i = 0; i < vocaBookmarkList.size(); i++) {
            VocaBookmark vocaBookmark = vocaBookmarkList.get(i);
            if (BaseVocaKnow.isVocaTypeWord(vocaBookmark)) {
                word_BOOKMARK.add(vocaBookmark.getVocaId());
            } else if (BaseVocaKnow.isVocaTypeSentence(vocaBookmark)) {
                sentence_BOOKMARK.add(vocaBookmark.getVocaId());
            }
        }
        updateVocaKnowOrBookmark(TABLE_DIC_WORD, COLUMN.BOOKMARK, Constant.INT_BOOLEAN.TRUE, word_BOOKMARK);
        updateVocaKnowOrBookmark(TABLE_DIC_SENTENCE, COLUMN.BOOKMARK, Constant.INT_BOOLEAN.TRUE, sentence_BOOKMARK);
    }

    public void updateVocaBookmarkInUserDicInDicDb(List<VocaBookmark> vocaBookmarkList) {
        List<Integer> word_BOOKMARK = new ArrayList<>();
        List<Integer> sentence_BOOKMARK = new ArrayList<>();

        for(int i = 0; i < vocaBookmarkList.size(); i++) {
            VocaBookmark vocaBookmark = vocaBookmarkList.get(i);
            if (BaseVocaKnow.isVocaTypeWord(vocaBookmark)) {
                word_BOOKMARK.add(vocaBookmark.getVocaId());
            } else if (BaseVocaKnow.isVocaTypeSentence(vocaBookmark)) {
                sentence_BOOKMARK.add(vocaBookmark.getVocaId());
            }
        }
        updateVocaKnowOrBookmark(TABLE.USER_DIC_ENG, COLUMN.BOOKMARK, Constant.INT_BOOLEAN.TRUE, word_BOOKMARK);
    }
    private void updateVocaKnowOrBookmark(String tableName, String columnName, int value, List<Integer> idList) {
        if (Utils.isEmpty(idList))
            return;
        ContentValues cv = new ContentValues();
        cv.put(columnName, value);
        String strIdListByComma = idList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(QUERY.COMMA));
        String whereClause = COLUMN.ID + QUERY.IN_OPEN + strIdListByComma + QUERY.IN_CLOSE;
        openWrite();
        database.update(tableName, cv, whereClause, null);
        close();
    }

    public void clearSearchHistory() {
        super.clearSearchHistory(TABLE_DIC_WORD);
        super.clearSearchHistory(TABLE_DIC_SENTENCE);
    }
    public void resetSearchHistory(int id) {
        super.resetSearchHistory(TABLE_DIC_WORD, id);
    }

    public List<IVocaFullPlayTTSItem> getDifficultWordListByWordListWithComma(String wordListWithComma) {
        if (wordListWithComma.trim().equals(""))
            return Collections.emptyList();
        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD
                + QUERY.WHERE + COLUMN.VOCA_KNOW + QUERY.LESS_THAN_OR_EQUAL + Constant.VOCA_KNOW.VOCA_KNOW_KNOWN
                + QUERY.AND;
        String queryFinal = query + COLUMN.WORD + QUERY.IN_OPEN + wordListWithComma + QUERY.IN_CLOSE + QUERY.FINISH;
        return getVocaListBySQL(TABLE_DIC_WORD, queryFinal);
    }

    public List<IVocaFullPlayTTSItem> getAllWordListWordListWithComma(String wordListWithComma) {
        if (wordListWithComma.trim().equals(""))
            return Collections.emptyList();
        String query = QUERY.SELECT_ALL + TABLE_DIC_WORD + QUERY.WHERE;
        String queryFinal = query + COLUMN.WORD + QUERY.IN_OPEN + wordListWithComma + QUERY.IN_CLOSE + QUERY.FINISH;
        return getVocaListBySQL(TABLE_DIC_WORD, queryFinal);
    }

    public void resetWordLevelInWord() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);

        updateTable(TABLE_DIC_WORD, cv, null, null);
    }

    public void updateWordLevelInWord(int wordLevel) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
        cv.put(COLUMN.VOCA_KNOWPRONOUNCE, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);

        String whereClause = COLUMN.WORDLEVEL + " <= ?";
        String[] whereArgs = {String.valueOf(wordLevel)};

        updateTable(TABLE_DIC_WORD, cv, whereClause, whereArgs);
    }
}
