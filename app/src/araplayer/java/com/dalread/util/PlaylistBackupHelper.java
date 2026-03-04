package com.dalread.util;

import android.content.Context;

import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.helper.MultiplePlayerDbHelper;

/**
 * 멀티플레이어 전용 백업/복원. Realm·레거시 SQLite 미사용.
 * current_screens, video_meta, stored_layout, screens_in_stored_layout, playlist, playlist_item 테이블만 SQLite로 처리.
 */
public class PlaylistBackupHelper {
  private Context context;
  private MultiPlayerDatabase database;
  private String currentScreensPath;
  private String videoMetaPath;
  private String storedLayoutPath;
  private String screensInStoredLayoutPath;
  private String playlistPath;
  private String playlistItemPath;

  public PlaylistBackupHelper(Context context) {
    this.context = context;
    MultiplePlayerDbHelper dbHelper = new MultiplePlayerDbHelper(context);
    this.database = dbHelper.initSubDatabase(null);
    this.currentScreensPath = StorageUtil.getMultiPlayerTableJsonPath(context, Constant.PLAYER.SQL.TABLE.current_screens);
    this.videoMetaPath = StorageUtil.getMultiPlayerTableJsonPath(context, Constant.PLAYER.SQL.TABLE.video_meta);
    this.storedLayoutPath = StorageUtil.getMultiPlayerTableJsonPath(context, Constant.PLAYER.SQL.TABLE.stored_layout);
    this.screensInStoredLayoutPath = StorageUtil.getMultiPlayerTableJsonPath(context, Constant.PLAYER.SQL.TABLE.screens_in_stored_layout);
    this.playlistPath = StorageUtil.getMultiPlayerTableJsonPath(context, Constant.PLAYER.SQL.TABLE.playlist);
    this.playlistItemPath = StorageUtil.getMultiPlayerTableJsonPath(context, Constant.PLAYER.SQL.TABLE.playlist_item);
  }

  public void restoreAll() {
    deleteTableAndPlaylist();

    String currentScreensContent = StorageUtil.readJsonFile(currentScreensPath);
    String videoMetaContent = StorageUtil.readJsonFile(videoMetaPath);
    String storedLayoutContent = StorageUtil.readJsonFile(storedLayoutPath);
    String screensInStoredLayoutContent = StorageUtil.readJsonFile(screensInStoredLayoutPath);
    String playlistContent = StorageUtil.readJsonFile(playlistPath);
    String playlistItemContent = StorageUtil.readJsonFile(playlistItemPath);

    if (currentScreensContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.current_screens, currentScreensContent);
    }
    if (videoMetaContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.video_meta, videoMetaContent);
    }
    if (storedLayoutContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.stored_layout, storedLayoutContent);
    }
    if (screensInStoredLayoutContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.screens_in_stored_layout, screensInStoredLayoutContent);
    }
    if (playlistContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.playlist, playlistContent);
    }
    if (playlistItemContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.playlist_item, playlistItemContent);
    }
    if (UserUtil.isDebugOrAdminUser(context)) {
      ToastUtil.getInstance(context).show("테이블과 플레이리스트를 복원했습니다.");
    }
  }

  /** SQLite playlist, playlist_item 테이블 백업 */
  public void backupPlaylist() {
    if (UserUtil.isDebugOrAdminUser(context)) {
      String playlistJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.playlist);
      String playlistItemJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.playlist_item);
      StorageUtil.writeJsonFile(playlistPath, playlistJson);
      StorageUtil.writeJsonFile(playlistItemPath, playlistItemJson);
    }
  }

  public void backupTables() {
    if (UserUtil.isDebugOrAdminUser(context)) {
      String currentScreensJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.current_screens);
      String videoMetaJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.video_meta);
      String storedLayoutJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.stored_layout);
      String screensInStoredLayoutJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.screens_in_stored_layout);
      String playlistJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.playlist);
      String playlistItemJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.playlist_item);
      StorageUtil.writeJsonFile(currentScreensPath, currentScreensJson);
      StorageUtil.writeJsonFile(videoMetaPath, videoMetaJson);
      StorageUtil.writeJsonFile(storedLayoutPath, storedLayoutJson);
      StorageUtil.writeJsonFile(screensInStoredLayoutPath, screensInStoredLayoutJson);
      StorageUtil.writeJsonFile(playlistPath, playlistJson);
      StorageUtil.writeJsonFile(playlistItemPath, playlistItemJson);
    }
  }

  private void deleteTableAndPlaylist() {
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.current_screens);
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.video_meta);
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.stored_layout);
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.screens_in_stored_layout);
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.playlist);
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.playlist_item);
  }
}