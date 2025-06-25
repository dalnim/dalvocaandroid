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

import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.MultiPlayerVideoAbRepeatModel;
import com.dalread.database.sqlite.model.MultiPlayerVideoListInScreenModel;
import com.dalread.database.sqlite.model.MultiPlayerVideoModel;
import com.dalread.database.sqlite.model.MultiPlayerVideoStoredModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.StorageUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MultiPlayerDatabase {
    private final String TAG = "MultiPlayerDatabase";
    private SubDatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private SharedPreferencesDB sharedPreferencesDB;
    private static MultiPlayerDatabase instance;
    private static String subPath = Constant.BASE_BLANK;
    private static Context context;

    // 상태 코드 정의
    public static final int STORED_SCREEN_REPLACED = 1;
    public static final int STORED_SCREEN_STORED = 2;
    public static final int STORED_SCREEN_NOTHING_HAPPEN = 0;

    public static MultiPlayerDatabase getInstance(Context contextTemp, String path) {
        context = contextTemp;
        if (!StorageUtil.isFileExist(path)) {
            return null;
        }
        if (instance == null || subPath == null || !subPath.equals(path))
            instance = new MultiPlayerDatabase(context, path);
        return instance;
    }

    public MultiPlayerDatabase(Context context, String path) {
        databaseHelper = new SubDatabaseHelper(context, path);
        this.subPath = path;
        sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
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

    public boolean isHasList() {
        String query = QUERY.SELECT_COUNT + TABLE.DIC_PLAYER_SCREEN + QUERY.WHERE + COLUMN.FILE_PATH + QUERY.NOT_EMPTY;
        return getCount(query) > 0;
    }

    public boolean isHasListByScreenId(int id) {
        String query = QUERY.SELECT_COUNT + TABLE.DIC_PLAYER_SCREEN + QUERY.WHERE + COLUMN.SCREEN_ID + QUERY.EQUAL + id;
        return getCount(query) > 0;
    }

    public int getRecordCountByByStoredId(int id) {
        String query = QUERY.SELECT_COUNT + TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT + QUERY.WHERE + COLUMN.STORED_ID + QUERY.EQUAL + id;
        return getCount(query);
    }

    //DIC_PLAYER_SCREEN_BACKUP에 filePath가 비어있는것이 생긴다. 그래도 그냥 두자. 안그르면 일일히 filePath가 empty인것을 다 체크해야 한다.
    public boolean isHasListInBackUpTbl(String filePath) {
        String query = QUERY.SELECT_COUNT + TABLE.DIC_PLAYER_SCREEN_BACKUP + QUERY.WHERE + COLUMN.FILE_PATH + QUERY.EQUAL + "\"" + filePath + "\"";
        return getCount(query) > 0;
    }

//    public boolean isHasListInAbRepeatTbl(String filePath) {
//        String query = QUERY.SELECT_COUNT + TABLE.DIC_PLAYER_SCREEN_AB_REPEAT + QUERY.WHERE + COLUMN.FILE_PATH + QUERY.EQUAL + "\"" + filePath + "\"";
//        return getCount(query) > 0;
//    }

    public MultiPlayerVideoModel getMultiPlayerVideoModelById(int screenId) {
        MultiPlayerVideoModel model = new MultiPlayerVideoModel();
        model.setSCREEN_ID(screenId); //DIC_PLAYER_SCREEN에는 없는 빈 비디오 일지라도 screenId는 가져야한다. 이게 없으면 빈 비디오는 전부 스크린이 0가 되어서 비디오를 저장하고 불러올때 빈비디오때문에 화면 갯수가 안맞아진다.
        try {
            String query = QUERY.SELECT_ALL + TABLE.DIC_PLAYER_SCREEN + QUERY.WHERE + COLUMN.SCREEN_ID + QUERY.EQUAL + screenId;
            openRead();
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                model = parseModel(cursor);
                break;
            }
            cursor.close();

        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return model;
    }

    public MultiPlayerVideoModel getMultiPlayerVideoModelByFilePathInBackUp(String filePath) {
        MultiPlayerVideoModel model = new MultiPlayerVideoModel();
        try {
            String query = QUERY.SELECT_ALL + TABLE.DIC_PLAYER_SCREEN_BACKUP + QUERY.WHERE + COLUMN.FILE_PATH + QUERY.EQUAL + "\"" + filePath + "\"";
            openRead();
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                model = parseModel(cursor);
                break;
            }
            cursor.close();

        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return model;
    }

    public List<MultiPlayerVideoModel> getAllRecordsInDicPlayerScreenTbl() {
        try {
            List<MultiPlayerVideoModel> list = new ArrayList<>();
            openRead();
            String query = QUERY.SELECT_ALL + TABLE.DIC_PLAYER_SCREEN;
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parseModel(cursor));
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
    public List<MultiPlayerVideoModel> getAllRecordsInDicPlayerScreenBackupTbl() {
        try {
            List<MultiPlayerVideoModel> list = new ArrayList<>();
            openRead();
            String query = QUERY.SELECT_ALL + TABLE.DIC_PLAYER_SCREEN_BACKUP;
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parseModel(cursor));
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

    public List<MultiPlayerVideoAbRepeatModel> getAllRecordsInDicPlayerScreenAbRepeatTbl() {
        try {
            List<MultiPlayerVideoAbRepeatModel> list = new ArrayList<>();
            openRead();
            String query = QUERY.SELECT_ALL + TABLE.DIC_PLAYER_SCREEN_AB_REPEAT;
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parseAbRepeatModel(cursor));
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

    public List<MultiPlayerVideoAbRepeatModel> getRecordsInDicPlayerScreenAbRepeatTblByFilePath(String filePath) {
        List<MultiPlayerVideoAbRepeatModel> list = new ArrayList<>();
        Cursor cursor = null;

        try {
            openRead();
            String query = QUERY.SELECT_ALL + TABLE.DIC_PLAYER_SCREEN_AB_REPEAT +
                    " WHERE " + COLUMN.FILE_PATH + "=?";
            DLog.d(TAG, "query=" + query);

            cursor = database.rawQuery(query, new String[]{filePath});
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                list.add(parseAbRepeatModel(cursor));
                cursor.moveToNext();
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }

        return list;
    }


    public List<MultiPlayerVideoStoredModel> getAllScreenStoredLayout() {
        try {
            List<MultiPlayerVideoStoredModel> list = new ArrayList<>();
            openRead();
            String query = QUERY.SELECT_ALL + TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT;
            DLog.d(TAG, "query=" + query);
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(parseStoredModel(cursor));
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

    public void insertRecordsIntoNewDatabase(List<MultiPlayerVideoModel> records) {
        try {
            openWrite();
            for (MultiPlayerVideoModel model : records) {
                ContentValues values = getContentValues(model);
                database.insert(TABLE.DIC_PLAYER_SCREEN, null, values);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    public void insertDicScreenBackupRecordsIntoNewDatabase(List<MultiPlayerVideoModel> records) {
        try {
            openWrite();
            for (MultiPlayerVideoModel model : records) {
                ContentValues cv = getContentValues(model);
                cv.remove(COLUMN.SCREEN_ID);
                database.insert(TABLE.DIC_PLAYER_SCREEN_BACKUP, null, cv);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    public void insertDicScreenAbRepeatRecordsIntoNewDatabase(List<MultiPlayerVideoAbRepeatModel> records) {
        try {
            openWrite();
            for (MultiPlayerVideoAbRepeatModel model : records) {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN.ID, model.getID());
                cv.put(COLUMN.FILE_PATH, model.getFILE_PATH());
                cv.put(COLUMN.AB_A, model.getAB_A());
                cv.put(COLUMN.AB_B, model.getAB_B());
                database.insert(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT, null, cv);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    public void insertVideoListInScreenRecords(List<String> filePaths, int screenId) {
        //스크린에 비디오가 1개일때는 앞뒤 이동 버튼을 보일필요가 없어서 저장도 안한다.
        if (filePaths.size() < 1) {
            return;
        }
        //새로운리스트를 넣을때는 기존 리스트를 먼저 다지운다.
        deleteMultiScreenVideoListInScreenByScreenId(screenId);
        try {
            openWrite();
            for (String filePath : filePaths) {
                MultiPlayerVideoListInScreenModel model = new MultiPlayerVideoListInScreenModel();
                model.setSCREEN_ID(screenId);
                model.setFILE_PATH(filePath);
                ContentValues cv = getContentValuesVideoListInScreen(model);
                database.insert(TABLE.DIC_PLAYER_VIDEO_LIST_IN_SCREEN, null, cv);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }
    public void insertVideoListInScreenRecords(List<MultiPlayerVideoListInScreenModel> models) {
        try {
            openWrite();
            for (MultiPlayerVideoListInScreenModel model : models) {
                ContentValues cv = getContentValuesVideoListInScreen(model);
                database.insert(TABLE.DIC_PLAYER_VIDEO_LIST_IN_SCREEN, null, cv);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }


    public void insertDicScreenStoredLayoutRecordsIntoNewDatabase(List<MultiPlayerVideoStoredModel> records) {
        try {
            openWrite();
            for (MultiPlayerVideoStoredModel model : records) {
                ContentValues cv = getContentValuesStoredLayout(model);
                database.insert(TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT, null, cv);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    private MultiPlayerVideoAbRepeatModel parseAbRepeatModel(Cursor cursor) {
        final MultiPlayerVideoAbRepeatModel model = new MultiPlayerVideoAbRepeatModel();
        model.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        model.setFILE_PATH(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.FILE_PATH)));
        model.setAB_A(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.AB_A)));
        model.setAB_B(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.AB_B)));
        return model;
    }

    private MultiPlayerVideoModel parseModel(Cursor cursor) {
        final MultiPlayerVideoModel model = new MultiPlayerVideoModel();
        // SCREEN_ID가 있는지 확인하고 값을 설정. getColumnIndexOrThrow을 쓰면 해당 컬럼이 없으면 다음줄로 넘어가지 않는다.
//        int screenIdIndex = cursor.getColumnIndex(Constant.PLAYER.SQL.COLUMN.SCREEN_ID);
//        if (screenIdIndex != -1) {
//            model.setSCREEN_ID(cursor.getInt(screenIdIndex));
//        }
        model.setSCREEN_ID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.SCREEN_ID)));
        model.setFILE_PATH(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.FILE_PATH)));
        model.setLAST_TIME(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.LAST_TIME)));
        model.setAB_A(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.AB_A)));
        model.setAB_B(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.AB_B)));
        model.setROTATE(cursor.getColumnIndex(COLUMN.ROTATE) == -1 ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ROTATE)));
        model.setUSE_AB(cursor.getColumnIndex(COLUMN.USE_AB) == -1 ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.USE_AB)));
        model.setVOLUME(cursor.getColumnIndex(COLUMN.VOLUME) == -1 ? -1 : cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.VOLUME)));
        model.setRESIZE_MODE(cursor.getColumnIndex(COLUMN.RESIZE_MODE) == -1 ? 0 : cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.RESIZE_MODE)));
        return model;
    }

    private MultiPlayerVideoStoredModel parseStoredModel(Cursor cursor) {
        MultiPlayerVideoStoredModel storedModel = new MultiPlayerVideoStoredModel();
        int storedId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.STORED_ID));
        String layoutName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.LAYOUT_NAME));
        int bookmark = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.BOOKMARK));
        int rotateLayoutIndex = cursor.getColumnIndex(COLUMN.ROTATE_LAYOUT);
        int rotateLayout = 0;
        if (rotateLayoutIndex != -1) {
            rotateLayout = cursor.getInt(rotateLayoutIndex);
        }
        storedModel.initializeFromBaseModel(parseModel(cursor), storedId, layoutName, bookmark, rotateLayout);
        return storedModel;
    }

    public List<String> getAllFilePathsInScreen(int screenId) {
        List<MultiPlayerVideoListInScreenModel> videoListInScreen = getAllVideListInScreen(screenId);
        List<String> filePaths = new ArrayList<>();

        for (MultiPlayerVideoListInScreenModel model : videoListInScreen) {
            filePaths.add(model.getFILE_PATH());
        }
        return filePaths;
    }

    public List<MultiPlayerVideoListInScreenModel> getAllVideListInScreen(int screenId) {
        List<MultiPlayerVideoListInScreenModel> list = new ArrayList<>();
        try {
            openRead();
            // SCREEN_ID에 해당하는 비디오 리스트를 DISP_ORDER 순으로 가져오는 쿼리
            String query = "SELECT * FROM " + TABLE.DIC_PLAYER_VIDEO_LIST_IN_SCREEN +
                    " WHERE " + COLUMN.SCREEN_ID + " = ? ORDER BY " + COLUMN.ID + " ASC";

            DLog.d(TAG, "query=" + query);

            // 쿼리 실행, ? 부분에 screenId를 바인딩
            Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(screenId)});
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                list.add(parseVideoListInScreenModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return list;
    }
    public List<MultiPlayerVideoListInScreenModel> getAllVideoListInScreen() {
        List<MultiPlayerVideoListInScreenModel> list = new ArrayList<>();
        try {
            openRead();
            // 모든 비디오 리스트를 ID 순으로 가져오는 쿼리
            String query = "SELECT * FROM " + TABLE.DIC_PLAYER_VIDEO_LIST_IN_SCREEN +
                    " ORDER BY " + COLUMN.ID + " ASC";

            DLog.d(TAG, "query=" + query);

            // 쿼리 실행
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                list.add(parseVideoListInScreenModel(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        return list;
    }

    private MultiPlayerVideoListInScreenModel parseVideoListInScreenModel(Cursor cursor) {
        MultiPlayerVideoListInScreenModel videoListModel = new MultiPlayerVideoListInScreenModel();
        videoListModel.setSCREEN_ID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.SCREEN_ID)));
        videoListModel.setFILE_PATH(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.FILE_PATH)));
        videoListModel.setID(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
        return videoListModel;
    }


    //update시에는 AB Repeat는 업데이트안한다. 이건 updateABRepeat메소드로 따로 한다.
    public boolean updateOrInsertInTable(MultiPlayerVideoModel model) {
        ContentValues cv = getContentValues(model);
        if (isHasListInBackUpTbl(model.getFILE_PATH())) {
            updateTableByFilePath(TABLE.DIC_PLAYER_SCREEN_BACKUP, cv, model.getFILE_PATH());
        } else {
            insertIntoTable(TABLE.DIC_PLAYER_SCREEN_BACKUP, cv);
        }
        if (isHasListByScreenId(model.getSCREEN_ID())) {
            return updateTableById(TABLE.DIC_PLAYER_SCREEN, cv, model.getSCREEN_ID());
        } else {
            return insertIntoTable(TABLE.DIC_PLAYER_SCREEN, cv);
        }
    }

    public void deleteRecordsByStoredIdList(List<Integer> storedIds) {
        if (!storedIds.isEmpty()) {
            for(Integer id : storedIds) {
                deleteRecord(TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT, COLUMN.STORED_ID, id);
            }
        }
    }
    public int updateOrInsertInTable(List<MultiPlayerVideoStoredModel> modelList) {
        int result = MultiPlayerDatabase.STORED_SCREEN_NOTHING_HAPPEN;
        boolean deleted = false;
        boolean inserted = false;
        List<Integer> storedIds = findStoredIdForFilePaths(modelList);
        if (!storedIds.isEmpty()) {
            for(Integer id : storedIds) {
                int recordCount = getRecordCountByByStoredId(id);
                if (modelList.size() == recordCount) {
                    deleteRecord(TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT, COLUMN.STORED_ID, id);
                    deleted = true;
                }
            }
        }

        int storedId = getMaxId(TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT, COLUMN.STORED_ID);
        for(MultiPlayerVideoStoredModel model : modelList) {
            ContentValues cv = getContentValuesStoredLayout(model);
            cv.put(COLUMN.STORED_ID, storedId);
            insertIntoTable(TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT, cv);
            inserted = true;
        }
        if (deleted && inserted) {
            result = MultiPlayerDatabase.STORED_SCREEN_REPLACED;
        } else if (inserted) {
            result = MultiPlayerDatabase.STORED_SCREEN_STORED;
        }
        return result;
    }

    public List<Integer> findStoredIdForFilePaths(List<MultiPlayerVideoStoredModel> modelList) {
        List<Integer> storedIds = new ArrayList<>();
        if (modelList.isEmpty()) {
            return storedIds;
        }

        String query = null;
        try {
            query = generateQueryForFilePaths(modelList);

            openRead();
            Cursor cursor = database.rawQuery(query, null);

            while (cursor.moveToNext()) {
                storedIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.STORED_ID)));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }

        return storedIds;  // 결과가 없으면 빈 리스트 반환
    }

    private String generateQueryForFilePaths(List<MultiPlayerVideoStoredModel> modelList) {
        // 파일 경로 기준으로 모델 리스트를 정렬
        List<MultiPlayerVideoStoredModel> sortedList = modelList.stream()
                .sorted((m1, m2) -> m1.getFILE_PATH().compareTo(m2.getFILE_PATH()))
                .collect(Collectors.toList());

        StringBuilder filePaths = new StringBuilder();

        for (int i = 0; i < sortedList.size(); i++) {
            if (i > 0) {
                filePaths.append(",");
            }
            filePaths.append(sortedList.get(i).getFILE_PATH().replace("'", "''"));
        }

        final String fileList = "FILE_LIST";
        return QUERY.SELECT + COLUMN.STORED_ID +
                QUERY.FROM + QUERY.PARENTHESIS_OPEN + QUERY.SELECT +
                    COLUMN.STORED_ID + QUERY.COMMA +
                    QUERY.GROUP_CONCAT + QUERY.PARENTHESIS_OPEN + COLUMN.FILE_PATH + QUERY.COMMA + "','" + QUERY.PARENTHESIS_CLOSE + QUERY.AS + fileList +
                    QUERY.FROM + QUERY.PARENTHESIS_OPEN + QUERY.SELECT +
                        COLUMN.STORED_ID + QUERY.COMMA + COLUMN.FILE_PATH +
                        QUERY.FROM + TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT +
                        QUERY.ORDER_BY + COLUMN.STORED_ID + QUERY.COMMA + COLUMN.FILE_PATH + QUERY.PARENTHESIS_CLOSE +
                    QUERY.GROUP_BY + COLUMN.STORED_ID +
                    QUERY.ORDER_BY + COLUMN.STORED_ID + QUERY.PARENTHESIS_CLOSE +
                QUERY.WHERE + fileList + QUERY.EQUAL + "'" + filePaths + "'";
    }


    @NonNull
    private static ContentValues getContentValues(MultiPlayerVideoModel model) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.SCREEN_ID, model.getSCREEN_ID());
        cv.put(COLUMN.FILE_PATH, model.getFILE_PATH());
        cv.put(COLUMN.LAST_TIME, model.getLAST_TIME());
        cv.put(COLUMN.ROTATE, model.getROTATE());
        cv.put(COLUMN.USE_AB, model.getUSE_AB());
        cv.put(COLUMN.VOLUME, model.getVOLUME());
        cv.put(COLUMN.RESIZE_MODE, model.getRESIZE_MODE());
        cv.put(COLUMN.AB_A, model.getAB_A());
        cv.put(COLUMN.AB_B, model.getAB_B());
        return cv;
    }

    private static ContentValues getContentValuesStoredLayout(MultiPlayerVideoStoredModel model) {
        ContentValues cv = getContentValues(model);
        cv.put(COLUMN.STORED_ID, model.getSTORED_ID());
        cv.put(COLUMN.LAYOUT_NAME, model.getLAYOUT_NAME());
        cv.put(COLUMN.BOOKMARK, model.getBOOKMARK());
        cv.put(COLUMN.ROTATE_LAYOUT, model.getROTATE_LAYOUT());
        return cv;
    }

    private static ContentValues getContentValuesVideoListInScreen(MultiPlayerVideoListInScreenModel model) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.SCREEN_ID, model.getSCREEN_ID());
        cv.put(COLUMN.FILE_PATH, model.getFILE_PATH());
        //ID는 오토 인크리먼트이기 때문에 ID를 임의로 넣어주면 안된다.
//        cv.put(COLUMN.ID, model.getId());
        return cv;
    }

    private boolean insertIntoTable(String tblName, ContentValues cv) {
        try {
            openWrite();
            long newRowId = database.insert(tblName, null, cv);
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateTableById(String tblName, ContentValues cv, int screenId) {
        try {
            openWrite();
            database.update(tblName,
                    cv, COLUMN.SCREEN_ID + "=?",
                    new String[]{String.valueOf(screenId)});
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateTableByFilePath(String tblName, ContentValues cv, String filePath) {
        try {
            openWrite();
            database.update(tblName,
                    cv, COLUMN.FILE_PATH + "=?",
                    new String[]{filePath});
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateAbRepeatTableByFilePath(String tblName, long oldAb_A, long oldAb_B, ContentValues cv, String filePath) {
        try {
            openWrite();
            database.update(tblName,
                    cv, COLUMN.FILE_PATH + "=? AND " + COLUMN.AB_A + "=? AND " + COLUMN.AB_B + "=?",
                    new String[]{filePath, String.valueOf(oldAb_A), String.valueOf(oldAb_B)});
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateAbRepeatTableById(String tblName, int id, ContentValues cv) {
        try {
            openWrite();
            database.update(tblName,
                    cv, COLUMN.ID + "=?",
                    new String[]{String.valueOf(id)});
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateTable(String tblName, ContentValues cv) {
        try {
            openWrite();
            database.update(tblName, cv, null, null);
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateABRepeat(MultiPlayerVideoModel model) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.SCREEN_ID, model.getSCREEN_ID());
        cv.put(COLUMN.AB_A, model.getAB_A());
        cv.put(COLUMN.AB_B, model.getAB_B());
        if (isHasListInBackUpTbl(model.getFILE_PATH())) {
            updateTableByFilePath(TABLE.DIC_PLAYER_SCREEN_BACKUP, cv, model.getFILE_PATH());
        }
        updateABRepeatInAbRepeatTbl(model);
        return updateTableById(TABLE.DIC_PLAYER_SCREEN, cv, model.getSCREEN_ID());
    }

    private void updateABRepeatInAbRepeatTbl(MultiPlayerVideoModel model) {
        //AB반복구간을 삭제할때는 아래를 실행하지 않는다. (getAB_A, getAB_B가 0이다.)
        if ((model.getAB_A() != 0) && (model.getAB_B() != 0)) {
            List<Integer> overlayIdList = getOverlappingAbRepeatIds(model);
            if (overlayIdList.isEmpty()) {
                insertAbRepeatInAbRepeatTbl(model);
            } else if (overlayIdList.size() == 1) {
                updateABRepeatInAbRepeatTbl(overlayIdList.get(0), model);
            } else {
                deleteRecordsInAbRepeatTbl(overlayIdList);
                insertAbRepeatInAbRepeatTbl(model);
            }
        }
    }

    //이건 AB반복이 중첩되는 레코드가 있는지 찾는다.
    public List<Integer> getOverlappingAbRepeatIds(MultiPlayerVideoModel model) {
        List<Integer> overlappingIds = new ArrayList<>();
        Cursor cursor = null;
        try {
            openRead();
            cursor = database.query(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT,
                    new String[]{COLUMN.ID},
                    COLUMN.FILE_PATH + "=? AND " + COLUMN.AB_A + "<? AND " + COLUMN.AB_B + ">?",
                    new String[]{model.getFILE_PATH(), String.valueOf(model.getAB_B()), String.valueOf(model.getAB_A())},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // 겹치는 레코드의 ID를 리스트에 추가
                    overlappingIds.add(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN.ID)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            close();
        }
        return overlappingIds;
    }

    public boolean insertAbRepeatInAbRepeatTbl(MultiPlayerVideoModel model) {
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.FILE_PATH, model.getFILE_PATH());
            cv.put(COLUMN.AB_A, model.getAB_A());
            cv.put(COLUMN.AB_B, model.getAB_B());
            database.insert(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT, null, cv);
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public void updateABRepeatInAbRepeatTbl(int id, MultiPlayerVideoModel model) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.AB_A, model.getAB_A());
        cv.put(COLUMN.AB_B, model.getAB_B());
        updateAbRepeatTableById(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT, id, cv);
//        updateAbRepeatTableByFilePath(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT, oldAb_A, oldAb_B, cv, model.getFILE_PATH());
    }

    //이건 특정 비디오의 모든 AB반복을 지우는거다.
    public boolean deleteAllABRepeatInAbRepeatTblByFilePath(String filePath) {
        deleteAllRepeatByFilePathInAbRepeatTbl(filePath);
        return true;
    }

    //이건 특정 비디오의 특정 AB반복 하나를 지운다.
    public boolean deleteABRepeatInAbRepeatTblBy(MultiPlayerVideoAbRepeatModel model) {
        try {
            openWrite();
            database.delete(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT,
                    COLUMN.FILE_PATH + "=? AND " + COLUMN.AB_A + "=? AND " + COLUMN.AB_B + "=?",
                    new String[]{model.getFILE_PATH(), String.valueOf(model.getAB_A()), String.valueOf(model.getAB_B())});
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean updateVolume(MultiPlayerVideoModel model) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.SCREEN_ID, model.getSCREEN_ID());
        cv.put(COLUMN.VOLUME, model.getVOLUME());
        if (isHasListInBackUpTbl(model.getFILE_PATH())) {
            updateTableByFilePath(TABLE.DIC_PLAYER_SCREEN_BACKUP, cv, model.getFILE_PATH());
        }
        return updateTableById(TABLE.DIC_PLAYER_SCREEN, cv, model.getSCREEN_ID());
    }

    private int getCount(String query) {
        int count = 0;
        try {
            openRead();
            Cursor cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        } catch (Exception ex) {
            count = 0;
        } finally {
            close();
        }
        return count;
    }

    private ContentValues getEmptyCv() {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.FILE_PATH, "");
        cv.put(COLUMN.LAST_TIME, 0);
        cv.put(COLUMN.AB_A, 0);
        cv.put(COLUMN.AB_B, 0);
        cv.put(COLUMN.ROTATE, 0);
        cv.put(COLUMN.USE_AB, 0);
        cv.put(COLUMN.VOLUME, -1);
        cv.put(COLUMN.RESIZE_MODE, 0);
        return cv;
    }
    public void clearMultiScreenHistory() {
        ContentValues cv = getEmptyCv();
        updateTable(TABLE.DIC_PLAYER_SCREEN, cv);
        deleteAllRecordsInDicPlayerVideoListInScreen();
        //이건 나중에 다른 UI를 만들어서 처리하자. (근데 개발시 말고 BACKUP을 초기화 할 필요가 있나?)
//        deleteAllRecords(TABLE.DIC_PLAYER_SCREEN_BACKUP);
    }

    public void deleteMultiScreenAllRecords() {
        deleteAllRecords(TABLE.DIC_PLAYER_SCREEN);
    }

    public void deleteAllRecordsInDicPlayerVideoListInScreen() {
        deleteAllRecords(TABLE.DIC_PLAYER_VIDEO_LIST_IN_SCREEN);
    }

    //파일이 삭제되었으면 SCREEN테이블에서는 초기화 시킨다. (여기서는 삭제를 하면 왜 안되지?)
    private void deleteMultiScreenByPath(String filePath) {
        ContentValues cv = getEmptyCv();
        updateTableByFilePath(TABLE.DIC_PLAYER_SCREEN, cv, filePath);
    }

    //파일이 삭제되었으면 Backup테이블에서는 지워버린다.
    private void deleteMultiScreenBackupByPath(String filePath) {
        deleteRecord(TABLE.DIC_PLAYER_SCREEN_BACKUP, COLUMN.FILE_PATH, filePath);
    }

    private void deleteMultiScreenAbRepeatByPath(String filePath) {
        deleteRecord(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT, COLUMN.FILE_PATH, filePath);
    }

    private void deleteMultiScreenStoredLayoutByPath(String filePath) {
        deleteRecord(TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT, COLUMN.FILE_PATH, filePath);
    }

    private void deleteMultiScreenVideoListInScreenByScreenId(int screenId) {
        deleteRecord(TABLE.DIC_PLAYER_VIDEO_LIST_IN_SCREEN, COLUMN.SCREEN_ID, screenId);
    }

    private void deleteMultiScreenVideoListInScreenByPath(String filePath) {
        deleteRecord(TABLE.DIC_PLAYER_VIDEO_LIST_IN_SCREEN, COLUMN.FILE_PATH, filePath);
    }

    //아래는 BaseSubDatabase와 중복되는 코드이다.
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

    public boolean deleteAllRepeatByFilePathInAbRepeatTbl(String filePath) {
        try {
            openWrite();
            database.delete(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT, COLUMN.FILE_PATH + "=?", new String[]{filePath});
            close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteRecordsInAbRepeatTbl(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        try {
            openWrite();
            String whereClause = COLUMN.ID + " IN (" + TextUtils.join(",", ids) + ")";
            int rowsDeleted = database.delete(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT, whereClause, null);
            close();
            return rowsDeleted > 0; // 삭제된 행이 있는 경우 true 반환
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            close();
        }
    }


    public String getTableAsJson(String tableName) {
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            openRead();
            Cursor cursor = database.rawQuery("SELECT * FROM " + tableName, null);

            if (cursor.moveToFirst()) {
                int columnCount = cursor.getColumnCount();
                do {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 0; i < columnCount; i++) {
                        String columnName = cursor.getColumnName(i);
                        int columnType = cursor.getType(i);
                        switch (columnType) {
                            case Cursor.FIELD_TYPE_INTEGER:
                                row.put(columnName, cursor.getInt(i));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                row.put(columnName, cursor.getFloat(i));
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                row.put(columnName, cursor.getString(i));
                                break;
                            case Cursor.FIELD_TYPE_BLOB:
                                row.put(columnName, cursor.getBlob(i));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                            default:
                                row.put(columnName, null);
                                break;
                        }
                    }
                    result.add(row);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        Gson gson = new Gson();
        return gson.toJson(result);
    }

    public void insertJsonToTable(String tableName, String jsonData) {
        Gson gson = new Gson();
        TypeToken<List<Map<String, Object>>> typeToken = new TypeToken<List<Map<String, Object>>>() {};
        List<Map<String, Object>> dataList = gson.fromJson(jsonData, typeToken.getType());
        //TODO : 실제 비디오 파일이 있는지 체크하고 있는거만 저장해야함.
        try {
            openWrite();
            database.beginTransaction();
            for (Map<String, Object> row : dataList) {
                ContentValues values = new ContentValues();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String columnName = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof Integer) {
                        values.put(columnName, (Integer) value);
                    } else if (value instanceof Float) {
                        values.put(columnName, (Float) value);
                    } else if (value instanceof Double) {
                        values.put(columnName, (Double) value);
                    } else if (value instanceof String) {
                        values.put(columnName, (String) value);
                    } else if (value instanceof byte[]) {
                        values.put(columnName, (byte[]) value);
//                    } else if (value == null) {
//                        values.putNull(columnName);
                    }
                }
                database.insert(tableName, null, values);
            }
            database.setTransactionSuccessful();
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            if (database != null) {
                database.endTransaction();
            }
            close();
        }
    }

    public void handleFileDelete(String filePath) {
        deleteMultiScreenByPath(filePath);
        deleteMultiScreenBackupByPath(filePath);
        deleteMultiScreenAbRepeatByPath(filePath);
        deleteMultiScreenStoredLayoutByPath(filePath);
        deleteMultiScreenVideoListInScreenByPath(filePath);
    }

    public void handleFilePathRename(String oldFilePath, String newFilePath) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN.FILE_PATH, newFilePath);
        updateTableByFilePath(TABLE.DIC_PLAYER_SCREEN, cv, oldFilePath);
        updateTableByFilePath(TABLE.DIC_PLAYER_SCREEN_BACKUP, cv, oldFilePath);
        updateTableByFilePath(TABLE.DIC_PLAYER_SCREEN_AB_REPEAT, cv, oldFilePath);
        updateTableByFilePath(TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT, cv, oldFilePath);
        updateTableByFilePath(TABLE.DIC_PLAYER_VIDEO_LIST_IN_SCREEN, cv, oldFilePath);
    }

    public void refreshFilePathsInTables() {
        refreshFilePaths(getAllRecordsInDicPlayerScreenTbl(), MultiPlayerVideoModel::getFILE_PATH, this::deleteMultiScreenByPath);
        refreshFilePaths(getAllRecordsInDicPlayerScreenBackupTbl(), MultiPlayerVideoModel::getFILE_PATH, this::deleteMultiScreenBackupByPath);
        refreshFilePaths(getAllRecordsInDicPlayerScreenAbRepeatTbl(), MultiPlayerVideoAbRepeatModel::getFILE_PATH, this::deleteMultiScreenAbRepeatByPath);
        refreshFilePaths(getAllScreenStoredLayout(), MultiPlayerVideoStoredModel::getFILE_PATH, this::deleteMultiScreenStoredLayoutByPath);
        refreshFilePaths(getAllVideoListInScreen(), MultiPlayerVideoListInScreenModel::getFILE_PATH, this::deleteMultiScreenVideoListInScreenByPath);
    }

    private <T> void refreshFilePaths(List<T> models, Function<T, String> filePathExtractor, Consumer<String> deleteAction) {
        for (T model : models) {
            String filePath = filePathExtractor.apply(model);
            if (!filePath.isEmpty() && !StorageUtil.isFileExist(filePath)) {
                deleteAction.accept(filePath);
            }
        }
    }

    public int getMaxId(String tableName, String colummName) {
        int maxVocaId = -1; // 기본값으로 -1 설정, VocaId가 음수가 될 수 없으므로 이를 통해 값이 설정되지 않았음을 확인
        String query = "SELECT MAX(" + colummName + ") AS max_id FROM " + tableName;
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
