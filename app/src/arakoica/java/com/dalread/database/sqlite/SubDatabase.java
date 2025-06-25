package com.dalread.database.sqlite;

import static com.dalread.util.Constant.PLAYER.SQL.COLUMN;
import static com.dalread.util.Constant.PLAYER.SQL.QUERY;
import static com.dalread.util.Constant.PLAYER.SQL.TABLE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.DIC_ICT_TERM;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.dalread.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class SubDatabase extends DicSentenceSubDatabase {
    private static SubDatabase instance;
    private static String TABLE_DIC_ICT_TERM;
//    private static Context context;
//    private static String defaultDateTime = "\"1970-01-01 00:00:00\"";

    public static SubDatabase getInstance(Context context, String path) {
        if (instance == null || subPath == null || !subPath.equals(path))
            instance = new SubDatabase(context, path);
        return instance;
    }
    public static SubDatabase getDicDatabaseInstance(Context context) {
        String destPathWithFileName = BaseStorageUtil.getAraKoicaDBPath(context);
        return getInstance(context, destPathWithFileName);
    }
    public SubDatabase(Context context, String path) {
        super(context, path);
        initTableName(context);
    }

    @Override
    protected void initTableName(Context context) {
        super.initTableName(context);
        TABLE_DIC_ICT_TERM = TABLE.DIC_ICT_TERM;
    }

    public void swapBookmark(IVocaBasicItem item) {
        if (isBookmark(item)) {
            setBookmark(item, Constant.INT_BOOLEAN.FASLE);
        } else {
            setBookmark(item, Constant.INT_BOOLEAN.TRUE);
        }
    }

    @Override
    public boolean isBookmark(IVocaBasicItem item) {
        String query = QUERY.SELECT_ALL + TABLE_DIC_ICT_TERM
                + QUERY.WHERE + COLUMN.BOOKMARK + QUERY.EQUAL + Constant.INT_BOOLEAN.TRUE
                + QUERY.AND + COLUMN.ID + QUERY.EQUAL + item.getVIVocaId();
        List<DIC_ICT_TERM> iVocaFullItemList = getIctTermListBySQL(query);
        if (Utils.isEmpty(iVocaFullItemList)) {
            return false;
        }
        return true;
    }

    public void setBookmark(IVocaBasicItem item, int value) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.BOOKMARK, value);
        updateTableById(TABLE_DIC_ICT_TERM, cv, item.getVIId());
    }

    public void updateSeachyHistory(DIC_ICT_TERM item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.SEARCH_HISTORY, item.getSEARCH_HISTORY());
        updateTableById(TABLE_DIC_ICT_TERM, cv, item.getVIId());
    }

    public void updateVoca(DIC_ICT_TERM item) {
        updateTableById(TABLE_DIC_ICT_TERM, getContentValues(item), item.getVIId());
    }

    public int insertVocaByServerId(DIC_ICT_TERM item) {
        return insertTermByServerId(TABLE_DIC_ICT_TERM, getContentValues(item));
    }

    public boolean replaceIctTermListWithoutUserInfo(List<DIC_ICT_TERM> itemList) {
        return insertIctTermList(TABLE_DIC_ICT_TERM, getContentListValuesWithoutUserInfo(itemList));
    }

    private List<ContentValues> getContentListValuesWithoutUserInfo(List<DIC_ICT_TERM> itemList) {
        List<ContentValues> cvList = new ArrayList<>();
        itemList.stream().forEach( item -> cvList.add(getContentValuesWithoutUserInfo(item)));
        return cvList;
    }
    
//    private List<ContentValues> getContentListValues(List<DIC_ICT_TERM> itemList) {
//        List<ContentValues> cvList = new ArrayList<>();
//        itemList.stream().forEach( item -> cvList.add(getContentValues(item)));
//        return cvList;
//    }

    private ContentValues getContentValuesWithoutUserInfo(DIC_ICT_TERM item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.ID, item.getID());
        cv.put(COLUMN.TERM_ENG_ABBR, item.getTERM_ENG_ABBR());
        cv.put(COLUMN.TERM_ENG_FULL, item.getTERM_ENG_FULL());
        cv.put(COLUMN.TERM_KO_TITLE, item.getTERM_KO_TITLE());
        cv.put(COLUMN.TERM_HANJA_TITLE, item.getTERM_HANJA_TITLE());
        cv.put(COLUMN.TERM_KO_SHORT, item.getTERM_KO_SHORT());
        cv.put(COLUMN.TERM_KO_FULL, item.getTERM_KO_FULL());
        cv.put(COLUMN.TERM_GROUP_ICT, item.getGROUP_ICT());
        cv.put(COLUMN.TERM_GROUP_HW, item.getGROUP_HW());
        cv.put(COLUMN.TERM_GROUP_ETC, item.getGROUP_ETC());
        cv.put(COLUMN.TERM_GROUP_NATION, item.getGROUP_NATION());
        cv.put(COLUMN.TERM_GROUP_GIS, item.getGROUP_GIS());
        cv.put(COLUMN.URL, item.getURL());
        cv.put(COLUMN.MEMO, item.getMEMO()); //Todo : 나중에 메모를 사용하게 되면, 유저가 쓴 메모가 사라지게 된다.
        cv.put(COLUMN.UPDATE_DATE, item.getUPDATE_DATE());
        cv.put(COLUMN.EDITED, item.getEDITED());
//        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
//        cv.put(COLUMN.BOOKMARK, item.getBOOKMARK());
        cv.put(COLUMN.TERM_LEVEL, item.getTERM_LEVEL());
//        cv.put(COLUMN.SEARCH_HISTORY, item.getSEARCH_HISTORY());
        return cv;
    }
    
    private ContentValues getContentValues(DIC_ICT_TERM item) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.ID, item.getID());
        cv.put(COLUMN.TERM_ENG_ABBR, item.getTERM_ENG_ABBR());
        cv.put(COLUMN.TERM_ENG_FULL, item.getTERM_ENG_FULL());
        cv.put(COLUMN.TERM_KO_TITLE, item.getTERM_KO_TITLE());
        cv.put(COLUMN.TERM_HANJA_TITLE, item.getTERM_HANJA_TITLE());
        cv.put(COLUMN.TERM_KO_SHORT, item.getTERM_KO_SHORT());
        cv.put(COLUMN.TERM_KO_FULL, item.getTERM_KO_FULL());
        cv.put(COLUMN.TERM_GROUP_ICT, item.getGROUP_ICT());
        cv.put(COLUMN.TERM_GROUP_HW, item.getGROUP_HW());
        cv.put(COLUMN.TERM_GROUP_ETC, item.getGROUP_ETC());
        cv.put(COLUMN.TERM_GROUP_NATION, item.getGROUP_NATION());
        cv.put(COLUMN.TERM_GROUP_GIS, item.getGROUP_GIS());
        cv.put(COLUMN.URL, item.getURL());
        cv.put(COLUMN.MEMO, item.getMEMO());
        cv.put(COLUMN.UPDATE_DATE, item.getUPDATE_DATE());
        cv.put(COLUMN.EDITED, item.getEDITED());
        cv.put(COLUMN.VOCA_KNOW, item.getVIVocaKnow());
        cv.put(COLUMN.BOOKMARK, item.getBOOKMARK());
        cv.put(COLUMN.TERM_LEVEL, item.getTERM_LEVEL());
        cv.put(COLUMN.SEARCH_HISTORY, item.getSEARCH_HISTORY());
        return cv;
    }

    private boolean insertIctTermList(String tblName, List<ContentValues> contentValuesList) {
        boolean result = true;
        try {
            openWrite();
            database.beginTransaction();
            for (ContentValues cv : contentValuesList) {
                Integer idInCv = cv.getAsInteger(COLUMN.ID);
                if (idInCv > 551) {
                    DLog.d("cv", cv.toString());
                }
                int id = (int) database.replace(tblName, null, cv);
                if (id > 0) {
                    String strDate = DateUtils.convertLongToFullPatternDateString(cv.getAsLong(COLUMN.UPDATE_DATE));
                    String query = QUERY.UPDATE + tblName
                            + QUERY.SET + COLUMN.UPDATE_DATE + QUERY.EQUAL + " \"" + strDate + "\" "
                            + ", " + COLUMN.SEARCH_HISTORY + QUERY.EQUAL + defaultDateTime
                            + QUERY.WHERE + COLUMN.ID + QUERY.EQUAL + id;
                    Cursor cursor = database.rawQuery( query, null);
                    //아래를 안해주면 실제 update쿼리가 안먹힌다.
                    cursor.moveToFirst();
                    cursor.close();
                }
            }
            database.setTransactionSuccessful(); //이것 없이 endTransaction() 을 부르면 그냥 롤백된다.
            database.endTransaction();
            close();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public int insertTermByServerId(String tblName, ContentValues cv) {
        int id = -1;
        try {
            openWrite();
//            id = (int) database.insert(tblName, null, cv);
            id = (int) database.replace(tblName, null, cv);
            if (id > 0) {
                String strDate = DateUtils.convertLongToFullPatternDateString(cv.getAsLong(COLUMN.UPDATE_DATE));
                String query = QUERY.UPDATE + tblName
                        + QUERY.SET + COLUMN.UPDATE_DATE + QUERY.EQUAL + " \"" + strDate + "\" "
                        + ", " + COLUMN.SEARCH_HISTORY + QUERY.EQUAL + defaultDateTime
                        + QUERY.WHERE + COLUMN.ID + QUERY.EQUAL + id;
                Cursor cursor = database.rawQuery( query, null);
                //아래를 안해주면 실제 update쿼리가 안먹힌다.
                cursor.moveToFirst();
                cursor.close();
            }
            close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public List<DIC_ICT_TERM> getIctTermList() {
        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        boolean showIctTerm = sharedPreferencesDB.getShowIctTerms();
        boolean showLocalTerm = sharedPreferencesDB.getShowLocalTerms();
        String whereClause = "";

        if (showIctTerm && showLocalTerm) {

        } else {
            if (showIctTerm) {
                whereClause = QUERY.WHERE + COLUMN.GROUP_ETC + QUERY.EQUAL + Constant.INT_BOOLEAN.FASLE;
            } else if (showLocalTerm) {
                whereClause = QUERY.WHERE + COLUMN.GROUP_ICT + QUERY.EQUAL + Constant.INT_BOOLEAN.FASLE;
            } else {
                whereClause = QUERY.WHERE + COLUMN.GROUP_ETC + QUERY.EQUAL + Constant.INT_BOOLEAN.FASLE
                    + QUERY.AND + COLUMN.GROUP_ICT + QUERY.EQUAL + Constant.INT_BOOLEAN.FASLE;
            }
        }
        String query = QUERY.SELECT_ALL + TABLE_DIC_ICT_TERM
                + whereClause
                + QUERY.ORDER_BY + COLUMN.BOOKMARK + QUERY.DESC
                + QUERY.COMMA + COLUMN.TERM_ENG_ABBR + QUERY.ASC + QUERY.FINISH;
        return getIctTermListBySQL(query);
    }

    public String getLastUpdateDate() {
        String tblName = TABLE_DIC_ICT_TERM;
        String fldName = COLUMN.UPDATE_DATE;
        String query = QUERY.SELECT + fldName + QUERY.FROM + tblName
                + QUERY.ORDER_BY + fldName + QUERY.DESC
                + QUERY.LIMIT_1;
        return getLastDateValueInTbl(fldName, tblName);

    }
    public List<DIC_ICT_TERM> getSearchHistoryIctTermList() {
        String query = QUERY.SELECT_ALL + TABLE_DIC_ICT_TERM
                + QUERY.WHERE + COLUMN.SEARCH_HISTORY + QUERY.GREATER + defaultDateTime
                + QUERY.ORDER_BY + COLUMN.SEARCH_HISTORY + QUERY.DESC + QUERY.FINISH;
        return getIctTermListBySQL(query);
    }

    public DIC_ICT_TERM getIctTermById(int id) {
        String query = QUERY.SELECT_ALL + TABLE_DIC_ICT_TERM
                + QUERY.WHERE + COLUMN.ID + QUERY.EQUAL + id
                + QUERY.LIMIT_1;
        return getIctTermBySQL(query);
    }

    private DIC_ICT_TERM getIctTermBySQL(String query) {
        DIC_ICT_TERM item = null;
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                item = parseIctTermModel(cursor);
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

    private List<DIC_ICT_TERM> getIctTermListBySQL(String query) {
        List<DIC_ICT_TERM> itemList = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                itemList.add(parseIctTermModel(cursor));
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

    private DIC_ICT_TERM parseIctTermModel(Cursor cursor) {
        DIC_ICT_TERM model = new DIC_ICT_TERM();
        model.setID(cursor.getInt(cursor.getColumnIndex(COLUMN.ID)));
        model.setTERM_ENG_ABBR(cursor.getString(cursor.getColumnIndex(COLUMN.TERM_ENG_ABBR)));
        model.setTERM_ENG_FULL(cursor.getString(cursor.getColumnIndex(COLUMN.TERM_ENG_FULL)));
        model.setTERM_KO_TITLE(cursor.getString(cursor.getColumnIndex(COLUMN.TERM_KO_TITLE)));
        model.setTERM_HANJA_TITLE(cursor.getString(cursor.getColumnIndex(COLUMN.TERM_HANJA_TITLE)));
        model.setTERM_KO_SHORT(cursor.getString(cursor.getColumnIndex(COLUMN.TERM_KO_SHORT)));
        model.setTERM_KO_FULL(cursor.getString(cursor.getColumnIndex(COLUMN.TERM_KO_FULL)));
        model.setURL(cursor.getString(cursor.getColumnIndex(COLUMN.URL)));
        model.setMEMO(cursor.getString(cursor.getColumnIndex(COLUMN.MEMO)));
        model.setEDITED(cursor.getInt(cursor.getColumnIndex(COLUMN.EDITED)));
        model.setBOOKMARK(cursor.getInt(cursor.getColumnIndex(COLUMN.BOOKMARK)));
        model.setVOCA_KNOW(cursor.getInt(cursor.getColumnIndex(COLUMN.VOCA_KNOW)));
        model.setTERM_LEVEL(cursor.getInt(cursor.getColumnIndex(COLUMN.TERM_LEVEL)));
        model.setSEARCH_HISTORY(cursor.getString(cursor.getColumnIndex(COLUMN.SEARCH_HISTORY)));
        model.setGROUP_ETC(cursor.getInt(cursor.getColumnIndex(COLUMN.TERM_GROUP_ETC)));
        model.setGROUP_HW(cursor.getInt(cursor.getColumnIndex(COLUMN.TERM_GROUP_HW)));
        model.setGROUP_ICT(cursor.getInt(cursor.getColumnIndex(COLUMN.TERM_GROUP_ICT)));
        model.setGROUP_NATION(cursor.getString(cursor.getColumnIndex(COLUMN.TERM_GROUP_NATION)));
        model.setGROUP_GIS(cursor.getInt(cursor.getColumnIndex(COLUMN.TERM_GROUP_GIS)));
        return model;
    }


    public void resetVocaKnowAndBookmark() {
        resetVocaKnowAndBookmark(TABLE_DIC_ICT_TERM);
    }
    private void resetVocaKnowAndBookmark(String tableName) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.VOCA_KNOW, Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);
        cv.put(COLUMN.BOOKMARK, Constant.INT_BOOLEAN.FASLE);
        openWrite();
        database.update(tableName, cv, null,null);
        close();
    }

    public void clearSearchHistory() {
        super.clearSearchHistory(TABLE_DIC_ICT_TERM);
    }
    public void resetSearchHistory(int id) {
        super.resetSearchHistory(TABLE_DIC_ICT_TERM, id);
    }
//    public void clearSearchHistory() {
//        ContentValues cv = new ContentValues();
//        cv.put(COLUMN.SEARCH_HISTORY, defaultDateTime);
//        openWrite();
//        database.update(TABLE_DIC_ICT_TERM, cv, null,null);
//        close();
//    }

    public int getMaxId() {
        int maxVocaId = -1; // 기본값으로 -1 설정, VocaId가 음수가 될 수 없으므로 이를 통해 값이 설정되지 않았음을 확인
        String query = "SELECT MAX(" + COLUMN.ID + ") AS max_id FROM " + TABLE_DIC_ICT_TERM;
        DLog.d(TAG, "getMaxVocaId - query=" + query);
        openRead();
        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            maxVocaId = cursor.getInt(cursor.getColumnIndexOrThrow("max_id"));
            maxVocaId++;
        }
        cursor.close();
        close();
        return maxVocaId;
    }
}
