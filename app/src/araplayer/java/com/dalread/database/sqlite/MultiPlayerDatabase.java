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
    private MultiPlayerDatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private SharedPreferencesDB sharedPreferencesDB;
    private static MultiPlayerDatabase instance;
    private static String subPath = Constant.BASE_BLANK;
    private static Context context;

    // 상태 코드 정의
    public static final int STORED_SCREEN_REPLACED = 1;
    public static final int STORED_SCREEN_STORED = 2;
    public static final int STORED_SCREEN_NOTHING_HAPPEN = 0;

    /**
     * DB 경로에 파일이 없어도 인스턴스를 반환한다.
     * MultiPlayerDatabaseHelper가 getReadableDatabase/getWritableDatabase 시 빈 DB를 생성한다.
     */
    public static MultiPlayerDatabase getInstance(Context contextTemp, String path) {
        context = contextTemp;
        if (instance == null || subPath == null || !subPath.equals(path)) {
            instance = new MultiPlayerDatabase(context, path);
        }
        return instance;
    }

    public MultiPlayerDatabase(Context context, String path) {
        databaseHelper = new MultiPlayerDatabaseHelper(context, path);
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
        List<MultiPlayerVideoModel> list = getAllCurrentScreens();
        for (MultiPlayerVideoModel m : list) {
            if (!m.isFilePathEmpty()) return true;
        }
        return false;
    }

    public boolean isHasListByScreenId(int id) {
        MultiPlayerVideoModel m = getCurrentScreenByScreenId(id);
        return !m.isFilePathEmpty();
    }

    public int getRecordCountByByStoredId(int id) {
        String query = QUERY.SELECT_COUNT + TABLE.SCREENS_IN_STORED_LAYOUT + QUERY.WHERE + COLUMN.LAYOUT_ID + QUERY.EQUAL + id;
        return getCount(query);
    }

    public boolean isHasListInBackUpTbl(String filePath) {
        MultiPlayerVideoModel m = getVideoMetaByFilePath(filePath);
        return m.getLAST_TIME() != 0 || (m.getAb_loop_json() != null && !m.getAb_loop_json().isEmpty());
    }

    /** 스크린별 모델 조회. current_screens만 사용 */
    public MultiPlayerVideoModel getMultiPlayerVideoModelById(int screenId) {
        return getCurrentScreenByScreenId(screenId);
    }

    /** 비디오 파일별 마지막 상태. video_meta만 사용 */
    public MultiPlayerVideoModel getMultiPlayerVideoModelByFilePathInBackUp(String filePath) {
        return getVideoMetaByFilePath(filePath);
    }

    /** 그리드 복원용. current_screens만 사용 */
    public List<MultiPlayerVideoModel> getCurrentScreenModels() {
        return getAllCurrentScreens();
    }

    /** video_meta 전체 조회 (백업/refresh용) */
    public List<MultiPlayerVideoModel> getVideoMetaModels() {
        return getAllVideoMeta();
    }

    /** AB는 ab_loop_json만 사용. 레거시 테이블 없음 → 빈 리스트 */
    public List<MultiPlayerVideoAbRepeatModel> getAbRepeatFromLegacyTable() {
        return new ArrayList<>();
    }

    /** ab_loop_json 문자열만 파싱해 AB 구간 목록 반환. video_meta 조회 없음 (스크린별 model 기준 사용) */
    public List<MultiPlayerVideoAbRepeatModel> getAbRepeatListFromJson(String filePath, String abLoopJson) {
        List<MultiPlayerVideoAbRepeatModel> list = new ArrayList<>();
        if (abLoopJson == null || abLoopJson.isEmpty()) {
            return list;
        }
        try {
            com.google.gson.JsonElement el = new Gson().fromJson(abLoopJson, com.google.gson.JsonElement.class);
            if (el != null && el.isJsonArray()) {
                com.google.gson.JsonArray arr = el.getAsJsonArray();
                for (int i = 0; i < arr.size(); i++) {
                    if (arr.get(i).isJsonObject()) {
                        com.google.gson.JsonObject obj = arr.get(i).getAsJsonObject();
                        MultiPlayerVideoAbRepeatModel model = new MultiPlayerVideoAbRepeatModel();
                        model.setFILE_PATH(filePath != null ? filePath : "");
                        model.setID(i);
                        if (obj.has("a")) model.setAB_A(obj.get("a").getAsLong());
                        if (obj.has("b")) model.setAB_B(obj.get("b").getAsLong());
                        list.add(model);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return list;
    }

    /** file_path에 해당하는 AB 구간 목록. video_meta 조회 (새 비디오 열 때 등 video_meta 기준이 필요할 때만 사용) */
    public List<MultiPlayerVideoAbRepeatModel> getAbRepeatListFromVideoMeta(String filePath) {
        MultiPlayerVideoModel meta = getVideoMetaByFilePath(filePath);
        return getAbRepeatListFromJson(filePath, meta.getAb_loop_json());
    }


    /** screens_in_stored_layout + stored_layout 조인 조회 */
    public List<MultiPlayerVideoStoredModel> getAllScreenStoredLayout() {
        List<MultiPlayerVideoStoredModel> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            openRead();
            String query = "SELECT s.layout_id, s.screen_id, s.file_path, s.last_time, s.ab_loop_json, s.use_ab, s.resize_mode, s.volume, s.sort_order, s.speed, s.rotate, l.name "
                    + "FROM " + TABLE.SCREENS_IN_STORED_LAYOUT + " s "
                    + "LEFT JOIN " + TABLE.STORED_LAYOUT + " l ON s.layout_id = l.id "
                    + "ORDER BY s.layout_id, s.screen_id";
            DLog.d(TAG, "query=" + query);
            cursor = database.rawQuery(query, null);
            while (cursor != null && cursor.moveToNext()) {
                list.add(parseStoredModelFromScreensCursor(cursor));
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
        return list;
    }

    /** 조인 커서(screens_in_stored_layout + stored_layout.name) → MultiPlayerVideoStoredModel */
    private MultiPlayerVideoStoredModel parseStoredModelFromScreensCursor(Cursor cursor) {
        MultiPlayerVideoStoredModel storedModel = new MultiPlayerVideoStoredModel();
        int storedId = cursor.getInt(cursor.getColumnIndexOrThrow("layout_id"));
        String layoutName = "";
        int nameIdx = cursor.getColumnIndex("name");
        if (nameIdx >= 0) layoutName = cursor.getString(nameIdx);
        storedModel.setSTORED_ID(storedId);
        storedModel.setLAYOUT_NAME(layoutName != null ? layoutName : "");
        storedModel.setBOOKMARK(0);
        storedModel.setROTATE_LAYOUT(0);
        storedModel.setSCREEN_ID(cursor.getInt(cursor.getColumnIndexOrThrow("screen_id")));
        storedModel.setFILE_PATH(cursor.getString(cursor.getColumnIndexOrThrow("file_path")));
        storedModel.setLAST_TIME((long) cursor.getDouble(cursor.getColumnIndexOrThrow("last_time")));
        String abJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.AB_LOOP_JSON));
        if (abJson != null) storedModel.setAb_loop_json(abJson);
        parseAbLoopJsonToModel(storedModel, abJson);
        storedModel.setUSE_AB(cursor.getInt(cursor.getColumnIndexOrThrow("use_ab")));
        storedModel.setRESIZE_MODE(cursor.getInt(cursor.getColumnIndexOrThrow("resize_mode")));
        int volIdx = cursor.getColumnIndex("volume");
        if (volIdx >= 0) storedModel.setVOLUME(cursor.getInt(volIdx));
        storedModel.setSpeed((float) cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN.SPEED)));
        storedModel.setROTATE(cursor.getInt(cursor.getColumnIndexOrThrow("rotate")));
        return storedModel;
    }

    public void insertRecordsIntoNewDatabase(List<MultiPlayerVideoModel> records) {
        try {
            openWrite();
            for (MultiPlayerVideoModel model : records) {
                database.insertWithOnConflict(TABLE.CURRENT_SCREENS, null, getContentValuesForCurrentScreens(model), SQLiteDatabase.CONFLICT_REPLACE);
                database.insertWithOnConflict(TABLE.VIDEO_META, null, getContentValuesForVideoMeta(model), SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** video_meta만 채움 (백업 복원 시) */
    public void insertDicScreenBackupRecordsIntoNewDatabase(List<MultiPlayerVideoModel> records) {
        try {
            openWrite();
            for (MultiPlayerVideoModel model : records) {
                database.insertWithOnConflict(TABLE.VIDEO_META, null, getContentValuesForVideoMeta(model), SQLiteDatabase.CONFLICT_REPLACE);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** AB는 ab_loop_json만 사용. no-op */
    public void insertDicScreenAbRepeatRecordsIntoNewDatabase(List<MultiPlayerVideoAbRepeatModel> records) {
        // no-op
    }

    /** 다음/이전 목록은 video_meta에서만 사용. 스크린별 목록 저장 없음(no-op). */
    public void insertVideoListInScreenRecords(List<String> filePaths, int screenId) {
        // no-op: 목록은 video_meta 기준으로만 사용
    }

    /** 다음/이전 목록은 video_meta에서만 사용. 스크린별 목록 저장 없음(no-op). */
    public void insertVideoListInScreenRecords(List<MultiPlayerVideoListInScreenModel> models) {
        // no-op: 목록은 video_meta 기준으로만 사용
    }


    /** stored_layout 1행 삽입 후 screens_in_stored_layout에 스크린별 삽입 */
    public void insertDicScreenStoredLayoutRecordsIntoNewDatabase(List<MultiPlayerVideoStoredModel> records) {
        if (records.isEmpty()) return;
        try {
            openWrite();
            String name = records.get(0).getLAYOUT_NAME() != null ? records.get(0).getLAYOUT_NAME() : "";
            ContentValues cvLayout = new ContentValues();
            cvLayout.put("name", name);
            cvLayout.put("created_at", String.valueOf(System.currentTimeMillis()));
            cvLayout.put("grid_row_count", 3);
            cvLayout.put("grid_column_count", 3);
            long newLayoutId = database.insert(TABLE.STORED_LAYOUT, null, cvLayout);
            int layoutId = newLayoutId > 0 ? (int) newLayoutId : 1;
            for (MultiPlayerVideoStoredModel model : records) {
                ContentValues cv = getContentValuesForScreensInStoredLayout(model, layoutId);
                database.insert(TABLE.SCREENS_IN_STORED_LAYOUT, null, cv);
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

    /** 다음/이전 버튼용 비디오 목록: video_meta의 file_path 목록 (순서: file_path ASC) */
    public List<String> getAllFilePathsFromVideoMeta() {
        List<String> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            openRead();
            cursor = database.query(TABLE.VIDEO_META, new String[]{"file_path"}, null, null, null, null, "file_path ASC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow("file_path"));
                    if (path != null && !path.isEmpty()) list.add(path);
                }
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
        return list;
    }

    /** 다음/이전 버튼용 목록. screenId 무시, video_meta 기준으로 반환. */
    public List<String> getAllFilePathsInScreen(int screenId) {
        return getAllFilePathsFromVideoMeta();
    }

    public List<MultiPlayerVideoListInScreenModel> getAllVideListInScreen(int screenId) {
        List<String> paths = getAllFilePathsInScreen(screenId);
        List<MultiPlayerVideoListInScreenModel> list = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            MultiPlayerVideoListInScreenModel m = new MultiPlayerVideoListInScreenModel();
            m.setSCREEN_ID(screenId);
            m.setFILE_PATH(paths.get(i));
            m.setID(i);
            list.add(m);
        }
        return list;
    }

    /** video_meta의 file_path 목록을 MultiPlayerVideoListInScreenModel 리스트로 반환 (screen_id=0). */
    public List<MultiPlayerVideoListInScreenModel> getAllVideoListInScreen() {
        List<String> paths = getAllFilePathsFromVideoMeta();
        List<MultiPlayerVideoListInScreenModel> list = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            MultiPlayerVideoListInScreenModel m = new MultiPlayerVideoListInScreenModel();
            m.setSCREEN_ID(0);
            m.setFILE_PATH(paths.get(i));
            m.setID(i);
            list.add(m);
        }
        return list;
    }

    /** 스크린에 비디오 열기: current_screens 한 행 갱신 + video_meta에 행 없을 때만 insert. (필드 변경은 전용 함수 사용) */
    public boolean openVideoOnScreen(MultiPlayerVideoModel model) {
        if (model.isFilePathEmpty()) {
            deleteCurrentScreen(model.getSCREEN_ID());
            return true;
        }
        insertOrUpdateCurrentScreen(model);
        if (!existsVideoMeta(model.getFILE_PATH())) {
            insertVideoMeta(model);
        }
        return true;
    }

    /** 저장된 레이아웃 로드 시: current_screens만 갱신, video_meta는 수정하지 않음 */
    public boolean insertOrUpdateCurrentScreenOnly(MultiPlayerVideoModel model) {
        if (model.isFilePathEmpty()) {
            deleteCurrentScreen(model.getSCREEN_ID());
            return true;
        }
        insertOrUpdateCurrentScreen(model);
        return true;
    }

    public void deleteRecordsByStoredIdList(List<Integer> storedIds) {
        if (storedIds.isEmpty()) return;
        try {
            openWrite();
            for (Integer id : storedIds) {
                database.delete(TABLE.SCREENS_IN_STORED_LAYOUT, COLUMN.LAYOUT_ID + "=?", new String[]{String.valueOf(id)});
                database.delete(TABLE.STORED_LAYOUT, "id=?", new String[]{String.valueOf(id)});
            }
        } finally {
            close();
        }
    }
    /** 저장된 레이아웃 저장: stored_layout 1행 + screens_in_stored_layout 스크린별 행 insert (동일 파일목록 기존 레이아웃 있으면 삭제 후 저장) */
    public int saveStoredLayout(List<MultiPlayerVideoStoredModel> modelList) {
        int result = MultiPlayerDatabase.STORED_SCREEN_NOTHING_HAPPEN;
        boolean deleted = false;
        boolean inserted = false;
        List<Integer> storedIds = findStoredIdForFilePaths(modelList);
        if (!storedIds.isEmpty()) {
            try {
                openWrite();
                for (Integer id : storedIds) {
                    int recordCount = getRecordCountByByStoredId(id);
                    if (modelList.size() == recordCount) {
                        database.delete(TABLE.SCREENS_IN_STORED_LAYOUT, COLUMN.LAYOUT_ID + "=?", new String[]{String.valueOf(id)});
                        database.delete(TABLE.STORED_LAYOUT, "id=?", new String[]{String.valueOf(id)});
                        deleted = true;
                    }
                }
            } finally {
                close();
            }
        }

        long newLayoutId = insertStoredLayoutRow(modelList.isEmpty() ? "" : modelList.get(0).getLAYOUT_NAME());
        int storedId = (int) newLayoutId;
        for (MultiPlayerVideoStoredModel model : modelList) {
            ContentValues cv = getContentValuesForScreensInStoredLayout(model, storedId);
            insertIntoTable(TABLE.SCREENS_IN_STORED_LAYOUT, cv);
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
                storedIds.add(cursor.getInt(cursor.getColumnIndexOrThrow("layout_id")));
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

        final String fileList = "file_list";
        return "SELECT layout_id FROM (SELECT layout_id, GROUP_CONCAT(file_path, ',') AS " + fileList
                + " FROM (SELECT layout_id, file_path FROM " + TABLE.SCREENS_IN_STORED_LAYOUT + " ORDER BY layout_id, file_path) GROUP BY layout_id) WHERE " + fileList + "='" + filePaths.toString() + "'";
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

    /** current_screens 테이블용 ContentValues (Mac 스키마, 소문자 컬럼명) */
    private static ContentValues getContentValuesForCurrentScreens(MultiPlayerVideoModel model) {
        String abJson = model.getAb_loop_json();
        if (abJson == null || abJson.isEmpty()) {
            abJson = buildAbLoopJsonFromAb(model.getAB_A(), model.getAB_B());
        }
        ContentValues cv = new ContentValues();
        cv.put("screen_id", model.getSCREEN_ID());
        cv.put("file_path", model.getFILE_PATH() != null ? model.getFILE_PATH() : "");
        cv.put("last_time", model.getLAST_TIME());
        cv.put(COLUMN.AB_LOOP_JSON, abJson);
        cv.put("use_ab", model.getUSE_AB());
        cv.put("resize_mode", model.getRESIZE_MODE());
        cv.put("volume", model.getVOLUME());
        cv.put(COLUMN.SPEED, model.getSpeed());
        cv.put("rotate", model.getROTATE());
        return cv;
    }

    private static String buildAbLoopJsonFromAb(long abA, long abB) {
        return "[{\"a\":" + abA + ",\"b\":" + abB + "}]";
    }

    private static void parseAbLoopJsonToModel(MultiPlayerVideoModel model, String abLoopJson) {
        if (abLoopJson == null || abLoopJson.isEmpty()) {
            return;
        }
        try {
            com.google.gson.JsonElement el = new Gson().fromJson(abLoopJson, com.google.gson.JsonElement.class);
            if (el != null && el.isJsonArray()) {
                com.google.gson.JsonArray arr = el.getAsJsonArray();
                if (arr.size() > 0 && arr.get(0).isJsonObject()) {
                    com.google.gson.JsonObject first = arr.get(0).getAsJsonObject();
                    if (first.has("a")) model.setAB_A(first.get("a").getAsLong());
                    if (first.has("b")) model.setAB_B(first.get("b").getAsLong());
                }
            }
        } catch (Exception ignored) {
        }
    }

    /** video_meta 테이블용 ContentValues. current_screens에서 변경되면 같이 반영되는 필드 포함 */
    private static ContentValues getContentValuesForVideoMeta(MultiPlayerVideoModel model) {
        String abJson = model.getAb_loop_json();
        if (abJson == null || abJson.isEmpty()) {
            abJson = buildAbLoopJsonFromAb(model.getAB_A(), model.getAB_B());
        }
        ContentValues cv = new ContentValues();
        cv.put("file_path", model.getFILE_PATH() != null ? model.getFILE_PATH() : "");
        cv.put("last_time", model.getLAST_TIME());
        cv.put(COLUMN.AB_LOOP_JSON, abJson);
        cv.put("use_ab", model.getUSE_AB());
        cv.put("resize_mode", model.getRESIZE_MODE());
        cv.put("volume", model.getVOLUME());
        cv.put(COLUMN.SPEED, model.getSpeed());
        cv.put("rotate", model.getROTATE());
        cv.put("hide", model.getHide());
        return cv;
    }

    // ---------- current_screens CRUD (Mac 스키마) ----------

    public void insertOrUpdateCurrentScreen(MultiPlayerVideoModel model) {
        try {
            openWrite();
            ContentValues cv = getContentValuesForCurrentScreens(model);
            long rowId = database.insertWithOnConflict(TABLE.CURRENT_SCREENS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            if (rowId < 0) {
                database.update(TABLE.CURRENT_SCREENS, cv, "screen_id=?", new String[]{String.valueOf(model.getSCREEN_ID())});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    public MultiPlayerVideoModel getCurrentScreenByScreenId(int screenId) {
        MultiPlayerVideoModel model = new MultiPlayerVideoModel();
        model.setSCREEN_ID(screenId);
        Cursor cursor = null;
        try {
            openRead();
            cursor = database.query(TABLE.CURRENT_SCREENS, null, "screen_id=?", new String[]{String.valueOf(screenId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                model = parseModelFromCurrentScreensCursor(cursor, screenId);
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
        return model;
    }

    public List<MultiPlayerVideoModel> getAllCurrentScreens() {
        List<MultiPlayerVideoModel> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            openRead();
            cursor = database.query(TABLE.CURRENT_SCREENS, null, null, null, null, null, "screen_id ASC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int screenId = cursor.getInt(cursor.getColumnIndexOrThrow("screen_id"));
                    list.add(parseModelFromCurrentScreensCursor(cursor, screenId));
                }
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
        return list;
    }

    public void deleteCurrentScreen(int screenId) {
        try {
            openWrite();
            database.delete(TABLE.CURRENT_SCREENS, "screen_id=?", new String[]{String.valueOf(screenId)});
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** current_screens 전체 삭제 (저장된 레이아웃 로드 전 등) */
    public void deleteAllCurrentScreens() {
        try {
            openWrite();
            database.delete(TABLE.CURRENT_SCREENS, null, null);
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    private MultiPlayerVideoModel parseModelFromCurrentScreensCursor(Cursor cursor, int screenId) {
        MultiPlayerVideoModel model = new MultiPlayerVideoModel();
        model.setSCREEN_ID(screenId);
        model.setFILE_PATH(cursor.getString(cursor.getColumnIndexOrThrow("file_path")));
        model.setLAST_TIME((long) cursor.getDouble(cursor.getColumnIndexOrThrow("last_time")));
        String abJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.AB_LOOP_JSON));
        if (abJson != null) model.setAb_loop_json(abJson);
        parseAbLoopJsonToModel(model, abJson);
        model.setUSE_AB(cursor.getInt(cursor.getColumnIndexOrThrow("use_ab")));
        model.setRESIZE_MODE(cursor.getInt(cursor.getColumnIndexOrThrow("resize_mode")));
        model.setVOLUME(cursor.getInt(cursor.getColumnIndexOrThrow("volume")));
        model.setSpeed((float) cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN.SPEED)));
        model.setROTATE(cursor.getInt(cursor.getColumnIndexOrThrow("rotate")));
        return model;
    }

    // ---------- video_meta CRUD (Mac 스키마) ----------

    /** 해당 file_path 행이 video_meta에 존재하는지 여부. 정규화 경로로도 한 번 더 조회함. */
    public boolean existsVideoMeta(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        Cursor cursor = null;
        try {
            openRead();
            cursor = database.query(TABLE.VIDEO_META, new String[]{"file_path"}, "file_path=?", new String[]{filePath}, null, null, null, "1");
            if (cursor != null && cursor.moveToFirst()) return true;
            if (cursor != null) cursor.close();
            cursor = null;
            String normalized = normalizeFilePath(filePath);
            if (!normalized.isEmpty() && !normalized.equals(filePath)) {
                cursor = database.query(TABLE.VIDEO_META, new String[]{"file_path"}, "file_path=?", new String[]{normalized}, null, null, null, "1");
                return cursor != null && cursor.moveToFirst();
            }
            return false;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
            return false;
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
    }

    /** video_meta에 새 행 삽입. file_path가 이미 있으면 CONFLICT_REPLACE로 덮어씀. */
    public void insertVideoMeta(MultiPlayerVideoModel model) {
        try {
            ContentValues cv = getContentValuesForVideoMeta(model);
            openWrite();
            database.insertWithOnConflict(TABLE.VIDEO_META, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /**
     * video_meta 기존 행 갱신. ab_loop_json·use_ab는 기존 값을 유지한다.
     * (AB 반복은 updateScreenABLoopOnly()에서만 갱신)
     */
    public void updateVideoMeta(MultiPlayerVideoModel model) {
        String path = model.getFILE_PATH();
        if (path == null || path.isEmpty()) return;
        try {
            MultiPlayerVideoModel existing = getVideoMetaByFilePath(path);
            ContentValues cv = getContentValuesForVideoMeta(model);
            if (existing.getAb_loop_json() != null) {
                cv.put(COLUMN.AB_LOOP_JSON, existing.getAb_loop_json());
                cv.put("use_ab", existing.getUSE_AB());
            }
            openWrite();
            database.update(TABLE.VIDEO_META, cv, "file_path=?", new String[]{path});
            String normalized = normalizeFilePath(path);
            if (!normalized.isEmpty() && !normalized.equals(path)) {
                database.update(TABLE.VIDEO_META, cv, "file_path=?", new String[]{normalized});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** video_meta 조회. 같은 파일이 file:// 유무 등 다른 경로로 저장된 경우를 위해 정규화 경로로도 한 번 더 시도함. */
    public MultiPlayerVideoModel getVideoMetaByFilePath(String filePath) {
        MultiPlayerVideoModel model = new MultiPlayerVideoModel();
        model.setFILE_PATH(filePath != null ? filePath : "");
        Cursor cursor = null;
        try {
            openRead();
            cursor = database.query(TABLE.VIDEO_META, null, "file_path=?", new String[]{filePath}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                fillModelFromVideoMetaCursor(model, cursor);
                return model;
            }
            if (cursor != null) cursor.close();
            cursor = null;
            String normalized = normalizeFilePath(filePath);
            if (!normalized.isEmpty() && !normalized.equals(filePath)) {
                cursor = database.query(TABLE.VIDEO_META, null, "file_path=?", new String[]{normalized}, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    fillModelFromVideoMetaCursor(model, cursor);
                }
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
        return model;
    }

    private void fillModelFromVideoMetaCursor(MultiPlayerVideoModel model, Cursor cursor) {
        model.setFILE_PATH(cursor.getString(cursor.getColumnIndexOrThrow("file_path")));
        model.setLAST_TIME((long) cursor.getDouble(cursor.getColumnIndexOrThrow("last_time")));
        String abJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.AB_LOOP_JSON));
        if (abJson != null) model.setAb_loop_json(abJson);
        parseAbLoopJsonToModel(model, abJson);
        model.setUSE_AB(cursor.getInt(cursor.getColumnIndexOrThrow("use_ab")));
        model.setRESIZE_MODE(cursor.getInt(cursor.getColumnIndexOrThrow("resize_mode")));
        int volIdx = cursor.getColumnIndex("volume");
        if (volIdx >= 0) model.setVOLUME(cursor.getInt(volIdx));
        model.setSpeed((float) cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN.SPEED)));
        int rotIdx = cursor.getColumnIndex("rotate");
        if (rotIdx >= 0) model.setROTATE(cursor.getInt(rotIdx));
        int hideIdx = cursor.getColumnIndex("hide");
        if (hideIdx >= 0) model.setHide(cursor.getInt(hideIdx));
    }

    /** video_meta: hide만 갱신 (멀티플레이어 비디오 목록 숨김/표시) */
    public void updateVideoMetaHide(String filePath, int hide) {
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put("hide", hide);
            database.update(TABLE.VIDEO_META, cv, "file_path=?", new String[]{filePath});
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** video_meta 전체 조회 (refreshFilePaths 등용) */
    public List<MultiPlayerVideoModel> getAllVideoMeta() {
        List<MultiPlayerVideoModel> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            openRead();
            cursor = database.query(TABLE.VIDEO_META, null, null, null, null, null, "file_path ASC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    MultiPlayerVideoModel model = new MultiPlayerVideoModel();
                    model.setFILE_PATH(cursor.getString(cursor.getColumnIndexOrThrow("file_path")));
                    model.setLAST_TIME((long) cursor.getDouble(cursor.getColumnIndexOrThrow("last_time")));
                    String abJson = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN.AB_LOOP_JSON));
                    if (abJson != null) model.setAb_loop_json(abJson);
                    parseAbLoopJsonToModel(model, abJson);
                    model.setUSE_AB(cursor.getInt(cursor.getColumnIndexOrThrow("use_ab")));
                    model.setRESIZE_MODE(cursor.getInt(cursor.getColumnIndexOrThrow("resize_mode")));
                    int volIdx = cursor.getColumnIndex("volume");
                    if (volIdx >= 0) model.setVOLUME(cursor.getInt(volIdx));
                    model.setSpeed((float) cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN.SPEED)));
                    int rotIdx = cursor.getColumnIndex("rotate");
                    if (rotIdx >= 0) model.setROTATE(cursor.getInt(rotIdx));
                    int hideIdx = cursor.getColumnIndex("hide");
                    if (hideIdx >= 0) model.setHide(cursor.getInt(hideIdx));
                    list.add(model);
                }
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            close();
        }
        return list;
    }

    /** video_meta: last_time만 갱신 (재생 위치 저장 시) */
    public void updateVideoMetaLastTime(String filePath, long lastTime) {
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put("last_time", lastTime);
            database.update(TABLE.VIDEO_META, cv, "file_path=?", new String[]{filePath});
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** video_meta: ab_loop_json, use_ab만 갱신 (AB 변경 시) */
    public void updateVideoMetaABLoop(String filePath, String abLoopJson, int useAb) {
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.AB_LOOP_JSON, abLoopJson != null ? abLoopJson : "");
            cv.put("use_ab", useAb);
            database.update(TABLE.VIDEO_META, cv, "file_path=?", new String[]{filePath});
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** 재생 위치 저장 전용: current_screens + video_meta + (loadedLayoutId >= 0이면) screens_in_stored_layout. filePath 비어 있으면 video_meta/stored 건너뜀. */
    public void updateScreenLastTimeOnly(int screenId, String filePath, long lastTime, int loadedLayoutId) {
        boolean validPath = filePath != null && !filePath.isEmpty();
        try {
            openWrite();
            ContentValues cvScreen = new ContentValues();
            cvScreen.put("last_time", lastTime);
            database.update(TABLE.CURRENT_SCREENS, cvScreen, "screen_id=?", new String[]{String.valueOf(screenId)});
            if (validPath) {
                ContentValues cvMeta = new ContentValues();
                cvMeta.put("last_time", lastTime);
                database.update(TABLE.VIDEO_META, cvMeta, "file_path=?", new String[]{filePath});
            }
            if (validPath && loadedLayoutId >= 0) {
                ContentValues cvStored = new ContentValues();
                cvStored.put("last_time", lastTime);
                database.update(TABLE.SCREENS_IN_STORED_LAYOUT, cvStored,
                        COLUMN.LAYOUT_ID + "=? AND screen_id=?", new String[]{String.valueOf(loadedLayoutId), String.valueOf(screenId)});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** AB 반복 저장 전용: current_screens + video_meta + (loadedLayoutId >= 0이면) screens_in_stored_layout. filePath 비어 있으면 video_meta/stored 건너뜀. */
    public void updateScreenABLoopOnly(int screenId, String filePath, String abLoopJson, int useAb, int loadedLayoutId) {
        boolean validPath = filePath != null && !filePath.isEmpty();
        try {
            openWrite();
            String ab = abLoopJson != null ? abLoopJson : "";
            ContentValues cvScreen = new ContentValues();
            cvScreen.put(COLUMN.AB_LOOP_JSON, ab);
            cvScreen.put("use_ab", useAb);
            database.update(TABLE.CURRENT_SCREENS, cvScreen, "screen_id=?", new String[]{String.valueOf(screenId)});
            if (validPath) {
                ContentValues cvMeta = new ContentValues();
                cvMeta.put(COLUMN.AB_LOOP_JSON, ab);
                cvMeta.put("use_ab", useAb);
                database.update(TABLE.VIDEO_META, cvMeta, "file_path=?", new String[]{filePath});
            }
            if (validPath && loadedLayoutId >= 0) {
                ContentValues cvStored = new ContentValues();
                cvStored.put(COLUMN.AB_LOOP_JSON, ab);
                cvStored.put("use_ab", useAb);
                database.update(TABLE.SCREENS_IN_STORED_LAYOUT, cvStored,
                        COLUMN.LAYOUT_ID + "=? AND screen_id=?", new String[]{String.valueOf(loadedLayoutId), String.valueOf(screenId)});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    private static ContentValues getContentValuesStoredLayout(MultiPlayerVideoStoredModel model) {
        ContentValues cv = getContentValues(model);
        cv.put(COLUMN.STORED_ID, model.getSTORED_ID());
        cv.put(COLUMN.LAYOUT_NAME, model.getLAYOUT_NAME());
        cv.put(COLUMN.BOOKMARK, model.getBOOKMARK());
        cv.put(COLUMN.ROTATE_LAYOUT, model.getROTATE_LAYOUT());
        return cv;
    }

    /** stored_layout에 1행 insert 후 새 row id 반환 */
    private long insertStoredLayoutRow(String name) {
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put("name", name != null ? name : "");
            cv.put("created_at", String.valueOf(System.currentTimeMillis()));
            cv.put("grid_row_count", 3);
            cv.put("grid_column_count", 3);
            long id = database.insert(TABLE.STORED_LAYOUT, null, cv);
            close();
            return id > 0 ? id : 1;
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
            close();
            return 1;
        }
    }

    /** screens_in_stored_layout용 ContentValues (소문자 컬럼, ab_loop_json). 레이아웃에서 가져왔을 때만 반영 */
    private static ContentValues getContentValuesForScreensInStoredLayout(MultiPlayerVideoStoredModel model, int layoutId) {
        String abJson = model.getAb_loop_json();
        if (abJson == null || abJson.isEmpty()) {
            abJson = buildAbLoopJsonFromAb(model.getAB_A(), model.getAB_B());
        }
        ContentValues cv = new ContentValues();
        cv.put("layout_id", layoutId);
        cv.put("screen_id", model.getSCREEN_ID());
        cv.put("file_path", model.getFILE_PATH() != null ? model.getFILE_PATH() : "");
        cv.put("last_time", model.getLAST_TIME());
        cv.put(COLUMN.AB_LOOP_JSON, abJson);
        cv.put("use_ab", model.getUSE_AB());
        cv.put("resize_mode", model.getRESIZE_MODE());
        cv.put("volume", model.getVOLUME());
        cv.put("sort_order", 0);
        cv.put(COLUMN.SPEED, model.getSpeed());
        cv.put("rotate", model.getROTATE());
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
        return updateABRepeat(model, -1);
    }

    public boolean updateABRepeat(MultiPlayerVideoModel model, int loadedLayoutId) {
        String abJson = model.getAb_loop_json() != null && !model.getAb_loop_json().isEmpty()
                ? model.getAb_loop_json() : buildAbLoopJsonFromAb(model.getAB_A(), model.getAB_B());
        updateScreenABLoopOnly(model.getSCREEN_ID(), model.getFILE_PATH(), abJson, model.getUSE_AB(), loadedLayoutId);
        return true;
    }

    /** volume만 current_screens + video_meta + (loadedLayoutId >= 0이면) screens_in_stored_layout 갱신. filePath 비어 있으면 video_meta/stored 건너뜀. */
    public void updateScreenVolumeOnly(int screenId, String filePath, int volume, int loadedLayoutId) {
        boolean validPath = filePath != null && !filePath.isEmpty();
        try {
            openWrite();
            ContentValues cvScreen = new ContentValues();
            cvScreen.put("volume", volume);
            database.update(TABLE.CURRENT_SCREENS, cvScreen, "screen_id=?", new String[]{String.valueOf(screenId)});
            if (validPath) {
                ContentValues cvMeta = new ContentValues();
                cvMeta.put("volume", volume);
                database.update(TABLE.VIDEO_META, cvMeta, "file_path=?", new String[]{filePath});
            }
            if (validPath && loadedLayoutId >= 0) {
                ContentValues cvStored = new ContentValues();
                cvStored.put("volume", volume);
                database.update(TABLE.SCREENS_IN_STORED_LAYOUT, cvStored,
                        COLUMN.LAYOUT_ID + "=? AND screen_id=?", new String[]{String.valueOf(loadedLayoutId), String.valueOf(screenId)});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** 재생속도만 current_screens + video_meta + (loadedLayoutId >= 0이면) screens_in_stored_layout 갱신. filePath 비어 있으면 video_meta/stored 건너뜀. */
    public void updateScreenSpeedOnly(int screenId, String filePath, float speed, int loadedLayoutId) {
        boolean validPath = filePath != null && !filePath.isEmpty();
        try {
            openWrite();
            ContentValues cvScreen = new ContentValues();
            cvScreen.put(COLUMN.SPEED, speed);
            database.update(TABLE.CURRENT_SCREENS, cvScreen, "screen_id=?", new String[]{String.valueOf(screenId)});
            if (validPath) {
                ContentValues cvMeta = new ContentValues();
                cvMeta.put(COLUMN.SPEED, speed);
                database.update(TABLE.VIDEO_META, cvMeta, "file_path=?", new String[]{filePath});
            }
            if (validPath && loadedLayoutId >= 0) {
                ContentValues cvStored = new ContentValues();
                cvStored.put(COLUMN.SPEED, speed);
                database.update(TABLE.SCREENS_IN_STORED_LAYOUT, cvStored,
                        COLUMN.LAYOUT_ID + "=? AND screen_id=?", new String[]{String.valueOf(loadedLayoutId), String.valueOf(screenId)});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** resize_mode만 current_screens + video_meta + (loadedLayoutId >= 0이면) screens_in_stored_layout 갱신. filePath 비어 있으면 video_meta/stored 건너뜀. */
    public void updateScreenResizeModeOnly(int screenId, String filePath, int resizeMode, int loadedLayoutId) {
        boolean validPath = filePath != null && !filePath.isEmpty();
        try {
            openWrite();
            ContentValues cvScreen = new ContentValues();
            cvScreen.put("resize_mode", resizeMode);
            database.update(TABLE.CURRENT_SCREENS, cvScreen, "screen_id=?", new String[]{String.valueOf(screenId)});
            if (validPath) {
                ContentValues cvMeta = new ContentValues();
                cvMeta.put("resize_mode", resizeMode);
                database.update(TABLE.VIDEO_META, cvMeta, "file_path=?", new String[]{filePath});
            }
            if (validPath && loadedLayoutId >= 0) {
                ContentValues cvStored = new ContentValues();
                cvStored.put("resize_mode", resizeMode);
                database.update(TABLE.SCREENS_IN_STORED_LAYOUT, cvStored,
                        COLUMN.LAYOUT_ID + "=? AND screen_id=?", new String[]{String.valueOf(loadedLayoutId), String.valueOf(screenId)});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** video_meta만 resize_mode 갱신 (파일별 공통) */
    public void updateVideoMetaResizeMode(String filePath, int resizeMode) {
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put("resize_mode", resizeMode);
            database.update(TABLE.VIDEO_META, cv, "file_path=?", new String[]{filePath});
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** rotate만 current_screens + video_meta + (loadedLayoutId >= 0이면) screens_in_stored_layout 갱신.
     * video_meta는 같은 파일이 서로 다른 경로 형태(file:// 유무 등)로 저장된 경우를 위해 filePath와 정규화 경로 둘 다 갱신.
     * filePath 비어 있으면 video_meta/stored 건너뜀. */
    public void updateScreenRotateOnly(int screenId, String filePath, int rotate, int loadedLayoutId) {
        if (filePath == null) filePath = "";
        boolean validPath = !filePath.isEmpty();
        try {
            openWrite();
            ContentValues cvScreen = new ContentValues();
            cvScreen.put("rotate", rotate);
            database.update(TABLE.CURRENT_SCREENS, cvScreen, "screen_id=?", new String[]{String.valueOf(screenId)});
            if (validPath) {
                ContentValues cvMeta = new ContentValues();
                cvMeta.put("rotate", rotate);
                int updated = database.update(TABLE.VIDEO_META, cvMeta, "file_path=?", new String[]{filePath});
                DLog.d(TAG, "video_meta update file_path=\"" + filePath + "\" rotate=" + rotate + " rowsUpdated=" + updated);
                String normalized = normalizeFilePath(filePath);
                if (!normalized.isEmpty() && !normalized.equals(filePath)) {
                    int updatedNorm = database.update(TABLE.VIDEO_META, cvMeta, "file_path=?", new String[]{normalized});
                    DLog.d(TAG, "video_meta update normalized=\"" + normalized + "\" rotate=" + rotate + " rowsUpdated=" + updatedNorm);
                }
            }
            if (validPath && loadedLayoutId >= 0) {
                ContentValues cvStored = new ContentValues();
                cvStored.put("rotate", rotate);
                database.update(TABLE.SCREENS_IN_STORED_LAYOUT, cvStored,
                        COLUMN.LAYOUT_ID + "=? AND screen_id=?", new String[]{String.valueOf(loadedLayoutId), String.valueOf(screenId)});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    /** file_path 비교/저장 시 동일 파일을 하나로 묶기 위해 정규화 (file:// 제거, trim) */
    private static String normalizeFilePath(String path) {
        if (path == null) return "";
        String p = path.trim();
        if (p.startsWith("file://")) p = p.substring(7).trim();
        return p;
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
        deleteAllCurrentScreens();
    }

    public void deleteMultiScreenAllRecords() {
        deleteAllCurrentScreens();
    }

    /** 파일 삭제 시: 해당 file_path를 쓰는 current_screens 행 초기화, video_meta 행 삭제 */
    private void deleteMultiScreenByPath(String filePath) {
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put("file_path", "");
            cv.put("last_time", 0);
            cv.put(COLUMN.AB_LOOP_JSON, "[]");
            cv.put("use_ab", 0);
            database.update(TABLE.CURRENT_SCREENS, cv, "file_path=?", new String[]{filePath});
            database.delete(TABLE.VIDEO_META, "file_path=?", new String[]{filePath});
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    private void deleteMultiScreenBackupByPath(String filePath) {
        deleteRecord(TABLE.VIDEO_META, "file_path", filePath);
    }

    /** current_screens / video_meta의 ab_loop_json만 비움 */
    private void deleteMultiScreenAbRepeatByPath(String filePath) {
        deleteMultiScreenAbRepeatByPath(filePath, -1, -1);
    }

    /** current_screens / video_meta 비움. loadedLayoutId >= 0이면 해당 레이아웃의 해당 스크린(screens_in_stored_layout)도 함께 비움 */
    private void deleteMultiScreenAbRepeatByPath(String filePath, int screenId, int loadedLayoutId) {
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN.AB_LOOP_JSON, "[]");
            cv.put("use_ab", 0);
            database.update(TABLE.CURRENT_SCREENS, cv, "file_path=?", new String[]{filePath});
            database.update(TABLE.VIDEO_META, cv, "file_path=?", new String[]{filePath});
            if (loadedLayoutId >= 0 && screenId >= 0) {
                database.update(TABLE.SCREENS_IN_STORED_LAYOUT, cv,
                        COLUMN.LAYOUT_ID + "=? AND screen_id=?", new String[]{String.valueOf(loadedLayoutId), String.valueOf(screenId)});
            }
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
    }

    private void deleteMultiScreenStoredLayoutByPath(String filePath) {
        deleteRecord(TABLE.SCREENS_IN_STORED_LAYOUT, "file_path", filePath);
    }

    private void deleteMultiScreenVideoListInScreenByScreenId(int screenId) {
        // no-op: 목록은 video_meta 기준, 스크린별 목록 저장 없음
    }

    private void deleteMultiScreenVideoListInScreenByPath(String filePath) {
        // no-op: 목록은 video_meta 기준, 스크린별 목록 저장 없음
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

    /** 해당 파일의 AB 구간 전체 삭제. ab_loop_json 비움 */
    public boolean deleteAllRepeatByFilePathInAbRepeatTbl(String filePath) {
        deleteMultiScreenAbRepeatByPath(filePath);
        return true;
    }

    /** AB는 ab_loop_json만 사용. ids 무시, no-op (호출부 호환용) */
    public boolean deleteRecordsInAbRepeatTbl(List<Integer> ids) {
        return ids != null && !ids.isEmpty();
    }

    /** 해당 파일의 모든 AB 구간 삭제 (Fragment 호출용). ab_loop_json 비움. loadedLayoutId >= 0이면 해당 레이아웃의 해당 스크린도 함께 비움 */
    public boolean deleteAllABRepeatInAbRepeatTblByFilePath(String filePath) {
        deleteMultiScreenAbRepeatByPath(filePath);
        return true;
    }

    /** 해당 파일의 모든 AB 구간 삭제. screenId·loadedLayoutId 전달 시 레이아웃에서 로드한 상태면 screens_in_stored_layout 해당 행도 비움 */
    public boolean deleteAllABRepeatInAbRepeatTblByFilePath(String filePath, int screenId, int loadedLayoutId) {
        deleteMultiScreenAbRepeatByPath(filePath, screenId, loadedLayoutId);
        return true;
    }

    /** 특정 AB 구간 하나 제거. ab_loop_json에서 해당 a,b 제거 후 저장. loadedLayoutId >= 0이면 screens_in_stored_layout도 함께 갱신 */
    public boolean deleteABRepeatInAbRepeatTblBy(MultiPlayerVideoAbRepeatModel model) {
        return deleteABRepeatInAbRepeatTblBy(model, -1, -1);
    }

    /** 특정 AB 구간 하나 제거. loadedLayoutId 전달 시 레이아웃에서 로드한 상태면 screens_in_stored_layout도 갱신 */
    public boolean deleteABRepeatInAbRepeatTblBy(MultiPlayerVideoAbRepeatModel model, int loadedLayoutId) {
        return deleteABRepeatInAbRepeatTblBy(model, -1, loadedLayoutId);
    }

    /** 특정 AB 구간 하나 제거. ab_loop_json은 해당 스크린의 current_screens에서만 읽음 (video_meta 미사용) */
    public boolean deleteABRepeatInAbRepeatTblBy(MultiPlayerVideoAbRepeatModel model, int screenId, int loadedLayoutId) {
        String filePath = model.getFILE_PATH();
        if (filePath == null || filePath.isEmpty()) return false;
        if (screenId < 0) {
            List<MultiPlayerVideoModel> screens = getAllCurrentScreens();
            for (MultiPlayerVideoModel m : screens) {
                if (filePath.equals(m.getFILE_PATH())) {
                    screenId = m.getSCREEN_ID();
                    break;
                }
            }
        }
        if (screenId < 0) return false;
        MultiPlayerVideoModel screenModel = getCurrentScreenByScreenId(screenId);
        String abJson = screenModel.getAb_loop_json();
        if (abJson == null || abJson.isEmpty()) return true;
        try {
            com.google.gson.JsonArray arr = new Gson().fromJson(abJson, com.google.gson.JsonArray.class);
            if (arr == null) return true;
            com.google.gson.JsonArray newArr = new com.google.gson.JsonArray();
            long removeA = model.getAB_A();
            long removeB = model.getAB_B();
            for (int i = 0; i < arr.size(); i++) {
                if (!arr.get(i).isJsonObject()) continue;
                com.google.gson.JsonObject obj = arr.get(i).getAsJsonObject();
                long a = obj.has("a") ? obj.get("a").getAsLong() : 0;
                long b = obj.has("b") ? obj.get("b").getAsLong() : 0;
                if (a == removeA && b == removeB) continue;
                newArr.add(obj);
            }
            String newJson = newArr.size() == 0 ? "[]" : new Gson().toJson(newArr);
            int useAb = newArr.size() > 0 ? 1 : 0;
            updateScreenABLoopOnly(screenId, filePath, newJson, useAb, loadedLayoutId);
            return true;
        } catch (Exception e) {
            DLog.e(TAG, e.getMessage());
            return false;
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
        try {
            openWrite();
            ContentValues cv = new ContentValues();
            cv.put("file_path", newFilePath);
            database.update(TABLE.CURRENT_SCREENS, cv, "file_path=?", new String[]{oldFilePath});
            database.update(TABLE.VIDEO_META, cv, "file_path=?", new String[]{oldFilePath});
            database.update(TABLE.SCREENS_IN_STORED_LAYOUT, cv, "file_path=?", new String[]{oldFilePath});
        } catch (Exception ex) {
            DLog.e(TAG, ex.getMessage());
        } finally {
            close();
        }
        updateVideoListJsonFilePath(oldFilePath, newFilePath);
    }

    /** 목록은 video_meta 기준이라 current_screens의 video_list_json 치환 없음(no-op). */
    private void updateVideoListJsonFilePath(String oldFilePath, String newFilePath) {
        // no-op
    }

    public void refreshFilePathsInTables() {
        refreshFilePaths(getAllCurrentScreens(), MultiPlayerVideoModel::getFILE_PATH, this::deleteMultiScreenByPath);
        refreshFilePaths(getAllVideoMeta(), MultiPlayerVideoModel::getFILE_PATH, this::deleteMultiScreenBackupByPath);
        refreshFilePaths(getAllScreenStoredLayout(), MultiPlayerVideoStoredModel::getFILE_PATH, this::deleteMultiScreenStoredLayoutByPath);
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
