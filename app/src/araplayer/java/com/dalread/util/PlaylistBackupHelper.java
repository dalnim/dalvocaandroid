package com.dalread.util;

import android.content.Context;

import com.dalread.database.PlaylistModelQuery;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.helper.MultiplePlayerDbHelper;
import com.dalread.model.PlaylistModel;
import com.google.gson.Gson;

import java.util.List;

public class PlaylistBackupHelper {
  private Context context;
  private MultiPlayerDatabase database;
  private String dicPlayerScreenPath;
  private String dicPlayerScreenBackupPath;
  private String dicPlayerScreenStoredLayoutPath;
  private String playlistJsonPath;

  public PlaylistBackupHelper(Context context) {
    this.context = context;
    MultiplePlayerDbHelper dbHelper = new MultiplePlayerDbHelper(context);
    this.database = dbHelper.initSubDatabase(null);
    this.dicPlayerScreenPath = StorageUtil.getDicPlayerScreenJsonPath(context, Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN);
    this.dicPlayerScreenBackupPath = StorageUtil.getDicPlayerScreenJsonPath(context, Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN_BACKUP);
    this.dicPlayerScreenStoredLayoutPath = StorageUtil.getDicPlayerScreenJsonPath(context, Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT);
    this.playlistJsonPath = StorageUtil.getPlaylistJsonPath(context);
  }

  public void restoreAll() {
    deleteTableAndPlaylist();

    String dicPlayerScreenContent = StorageUtil.readJsonFile(dicPlayerScreenPath);
    String dicPlayerScreenBackupContent = StorageUtil.readJsonFile(dicPlayerScreenBackupPath);
    String dicPlayerScreenStoredLayoutContent = StorageUtil.readJsonFile(dicPlayerScreenStoredLayoutPath);
    String playlistJsonContent = StorageUtil.readJsonFile(playlistJsonPath);

    if (dicPlayerScreenContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN, dicPlayerScreenContent);
    }

    if (dicPlayerScreenBackupContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN_BACKUP, dicPlayerScreenBackupContent);
    }
    if (dicPlayerScreenStoredLayoutContent != null) {
      database.insertJsonToTable(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT, dicPlayerScreenStoredLayoutContent);
    }
    if (playlistJsonContent != null) {
      PlaylistModelQuery.addPlaylistFromJson(playlistJsonContent);
    }
    if (UserUtil.isDebugOrAdminUser(context)) {
      ToastUtil.getInstance(context).show("테이블과 플레이리스트를 복원했습니다.");
    }
  }

  public void backupPlaylist() {
    if (UserUtil.isDebugOrAdminUser(context)) {
      final List<PlaylistModel> playlistModels = PlaylistModelQuery.getAll(Voca.getRealm());
      Gson gson = new Gson();
      String json = gson.toJson(playlistModels);
      StorageUtil.writeJsonFile(playlistJsonPath, json);
//      ToastUtil.getInstance(context).show("플레이리스트를 백업했습니다.");
    }
  }

  public void backupTables() {
    if (UserUtil.isDebugOrAdminUser(context)) {
      String tableDicPlayerScreenContent = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN);
      String tableDicPlayerScreenBackupContentJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN_BACKUP);
      String tableDicPlayerScreenStoredLayoutContentJson = database.getTableAsJson(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT);
      StorageUtil.writeJsonFile(dicPlayerScreenPath, tableDicPlayerScreenContent);
      StorageUtil.writeJsonFile(dicPlayerScreenBackupPath, tableDicPlayerScreenBackupContentJson);
      StorageUtil.writeJsonFile(dicPlayerScreenStoredLayoutPath, tableDicPlayerScreenStoredLayoutContentJson);
//      ToastUtil.getInstance(context).show("테이블을 백업했습니다.");
    }
  }

  // 테이블과 플레이리스트 삭제
  private void deleteTableAndPlaylist() {
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN);
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN_BACKUP);
    database.deleteAllRecords(Constant.PLAYER.SQL.TABLE.DIC_PLAYER_SCREEN_STORED_LAYOUT);
    PlaylistModelQuery.deleteAllPlaylist();
  }
}