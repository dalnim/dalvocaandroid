package com.dalread.database.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dalread.BuildConfig;

/**
 * MultiPlayer 전용 SQLiteOpenHelper.
 * Mac 스키마(current_screens, video_meta, stored_layout, screens_in_stored_layout 등) 기준으로
 * DB 파일이 없을 때 앱에서 테이블을 생성한다.
 * 모든 CREATE TABLE 필드에 DEFAULT를 지정한다.
 */
public class MultiPlayerDatabaseHelper extends SQLiteOpenHelper {

    /** DB 스키마 버전. 테이블 구조 변경 시 증가 후 onUpgrade에서 마이그레이션 처리 */
    private static final int MULTIPLAYER_DB_VERSION = 4;

    public MultiPlayerDatabaseHelper(Context context, String dbPath) {
        super(new SubDatabaseContext(context), dbPath, null, MULTIPLAYER_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createCurrentScreens(db);
        createVideoMeta(db);
        createStoredLayout(db);
        createScreensInStoredLayout(db);
        createPlaylist(db);
        createPlaylistItem(db);
    }

    /** current_screens: 현재 그리드 스크린별 상태 (Mac + Android speed, rotate). */
    private void createCurrentScreens(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS current_screens ("
                        + " screen_id       INTEGER NOT NULL PRIMARY KEY,"
                        + " file_path       TEXT NOT NULL DEFAULT '',"
                        + " last_time       REAL DEFAULT 0,"
                        + " ab_loop_json    TEXT NOT NULL DEFAULT '',"
                        + " use_ab          INTEGER DEFAULT 0,"
                        + " resize_mode     INTEGER DEFAULT 0,"
                        + " volume          INTEGER DEFAULT -1,"
                        + " speed           REAL DEFAULT 1.0,"
                        + " rotate          INTEGER DEFAULT 0"
                        + ")"
        );
    }

    /** video_meta: 파일별 마지막 재생 상태. current_screens에서 변경되면 같이 반영. hide=멀티플레이어 비디오 목록 숨김 여부 */
    private void createVideoMeta(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS video_meta ("
                        + " file_path     TEXT NOT NULL PRIMARY KEY,"
                        + " last_time     REAL DEFAULT 0,"
                        + " ab_loop_json  TEXT NOT NULL DEFAULT '',"
                        + " use_ab        INTEGER DEFAULT 0,"
                        + " resize_mode   INTEGER DEFAULT 0,"
                        + " volume        INTEGER DEFAULT -1,"
                        + " speed         REAL DEFAULT 1.0,"
                        + " rotate        INTEGER DEFAULT 0,"
                        + " hide          INTEGER DEFAULT 0"
                        + ")"
        );
    }

    /** stored_layout: 저장된 레이아웃 메타 */
    private void createStoredLayout(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS stored_layout ("
                        + " id                INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " name              TEXT NOT NULL DEFAULT '',"
                        + " created_at        TEXT NOT NULL DEFAULT '',"
                        + " grid_row_count    INTEGER DEFAULT 3,"
                        + " grid_column_count INTEGER DEFAULT 3"
                        + ")"
        );
    }

    /** screens_in_stored_layout: 저장된 레이아웃별 스크린 스냅샷. 레이아웃에서 가져왔을 때만 반영 */
    private void createScreensInStoredLayout(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS screens_in_stored_layout ("
                        + " id           INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " layout_id    INTEGER NOT NULL DEFAULT 0,"
                        + " screen_id    INTEGER NOT NULL DEFAULT 0,"
                        + " file_path    TEXT NOT NULL DEFAULT '',"
                        + " last_time    REAL DEFAULT 0,"
                        + " ab_loop_json TEXT NOT NULL DEFAULT '',"
                        + " use_ab       INTEGER DEFAULT 0,"
                        + " resize_mode  INTEGER DEFAULT 0,"
                        + " volume       INTEGER DEFAULT -1,"
                        + " sort_order   INTEGER DEFAULT 0,"
                        + " speed        REAL DEFAULT 1.0,"
                        + " rotate       INTEGER DEFAULT 0"
                        + ")"
        );
        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_screens_in_stored_layout_layout_id "
                        + "ON screens_in_stored_layout(layout_id)"
        );
    }

    /** playlist: 플레이리스트 메타 (category1/2/3 제외) */
    private void createPlaylist(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS playlist ("
                        + " id            INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " name          TEXT NOT NULL DEFAULT '',"
                        + " is_selected   INTEGER DEFAULT 0,"
                        + " favorite      INTEGER DEFAULT 0,"
                        + " is_auto_created INTEGER DEFAULT 0,"
                        + " bookmark      INTEGER DEFAULT 0,"
                        + " unused        INTEGER DEFAULT 0"
                        + ")"
        );
    }

    /** playlist_item: 플레이리스트별 항목 (playlist_id, sort_order, file_path) */
    private void createPlaylistItem(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS playlist_item ("
                        + " id          INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + " playlist_id INTEGER NOT NULL DEFAULT 0,"
                        + " sort_order  INTEGER NOT NULL DEFAULT 0,"
                        + " file_path   TEXT NOT NULL DEFAULT ''"
                        + ")"
        );
        db.execSQL(
                "CREATE INDEX IF NOT EXISTS idx_playlist_item_playlist_id "
                        + "ON playlist_item(playlist_id)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            createPlaylist(db);
            createPlaylistItem(db);
        }
        if (oldVersion < 4) {
            try {
                db.execSQL("ALTER TABLE video_meta ADD COLUMN hide INTEGER DEFAULT 0");
            } catch (Exception ignored) {
            }
        }
    }
}
