package com.dalread.database;

import com.dalread.model.PlaylistModel;

import java.util.List;
import java.util.stream.Collectors;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmManagerPlaylistModel {
    private static final String TAG = "PlaylistModelQuery";

    // Save PlaylistModel
    public static void savePlaylistModel(final PlaylistModel PlaylistModel) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> r.insertOrUpdate(PlaylistModel));
    }

    // Delete PlaylistModel by name
    public static boolean deletePlaylistModelByName(final String name) {
        Realm realm = Realm.getDefaultInstance();
        PlaylistModel model = realm.where(PlaylistModel.class).equalTo(PlaylistModel.FIELD_NAME, name).findFirst();
        if (model != null) {
            realm.executeTransaction(r -> model.deleteFromRealm());
            return true;
        }
        return false;
    }

    // Delete PlaylistModel by ID
    public static boolean deletePlaylistModelById(final long id) {
        Realm realm = Realm.getDefaultInstance();
        PlaylistModel model = realm.where(PlaylistModel.class).equalTo(PlaylistModel.FIELD_PLAYLIST_ID, id).findFirst();
        if (model != null) {
            realm.executeTransaction(r -> model.deleteFromRealm());
            return true;
        }
        return false;
    }

    // Fetch PlaylistModel by ID
    public static PlaylistModel getPlaylistModelById(final int id) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(PlaylistModel.class).equalTo(PlaylistModel.FIELD_PLAYLIST_ID, id).findFirst();
    }

    // Fetch PlaylistModel by name
    public static PlaylistModel getPlaylistModelByName(final String name) {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(PlaylistModel.class).equalTo(PlaylistModel.FIELD_NAME, name).findFirst();
    }

    // Get Playlist ID by name
    public static long getPlaylistIdByName(final String name) {
        Realm realm = Realm.getDefaultInstance();
        PlaylistModel model = realm.where(PlaylistModel.class).equalTo(PlaylistModel.FIELD_NAME, name).findFirst();
        return model != null ? model.getPlayListId() : null;
    }

    // Fetch all PlaylistModels
    public static RealmResults<PlaylistModel> getAllPlaylistModels() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(PlaylistModel.class).sort(PlaylistModel.FIELD_NAME).findAll();
    }

    // Check if any Playlist exists
    public static boolean hasPlaylist() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(PlaylistModel.class).count() > 0;
    }

    // 모든 플레이리스트가 비어 있는지 확인하는 함수
    public static boolean isAllPlaylistEmpty() {
        List<PlaylistModel> modelsTemp = getAllPlaylistModels().stream()
                .filter(model -> model.getFilePathCount() > 0)
                .collect(Collectors.toList());
        return modelsTemp.isEmpty();
    }

    // Get next Playlist ID
    public static int getNextPlaylistId() {
        Realm realm = Realm.getDefaultInstance();
        Number maxId = realm.where(PlaylistModel.class).max(PlaylistModel.FIELD_PLAYLIST_ID);
        return (maxId != null ? maxId.intValue() : 0) + 1;
    }

    // Check if Playlist name exists
    public static boolean isPlaylistNameExists(final String name) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<PlaylistModel> existingPlaylists = realm.where(PlaylistModel.class).equalTo(PlaylistModel.FIELD_NAME, name).findAll();
        return !existingPlaylists.isEmpty();
    }

    // Remove all PlaylistModels
    public static void removeAll() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(r -> {
            RealmResults<PlaylistModel> allPlaylists = r.where(PlaylistModel.class).findAll();
            allPlaylists.deleteAllFromRealm();
        });
    }

    //=======
//    private static void addPlaylistItem(Realm realm, PlaylistModel PlaylistModel) {
//        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(PlaylistModel));
//    }
//
//    public static void addPlaylist(Realm realm, PlaylistModel PlaylistModel) {
//        DLog.d(TAG, "add - PlaylistModel=" + PlaylistModel.getId());
//        addPlaylistItem(realm, PlaylistModel);
//    }
//
//    public static void updatePlaylist(Realm realm, PlaylistModel PlaylistModel) {
//        DLog.d(TAG, "update - PlaylistModel=" + PlaylistModel.getId());
//        addPlaylistItem(realm, PlaylistModel);
//    }
//
//    private static void addPlaylistSongItem(Realm realm, PlaylistSongModel playlistSong) {
//        realm.executeTransaction(realm1 -> {
//            final PlaylistSongModel item = realm1.where(PlaylistSongModel.class)
//                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, playlistSong.getPlayListId())
//                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, playlistSong.getPath())
//                    .findFirst();
//            if (item == null) {
//                DLog.d(TAG, "add song to playlist: " + playlistSong.getId() + " - " + playlistSong.getPath());
//                realm1.insertOrUpdate(playlistSong);
//            }
//        });
//    }
//
//    public static void addPlaylistSong(Realm realm, PlaylistSongModel playlistSong) {
//        DLog.d(TAG, "add - playlistSong=" + playlistSong.getId());
//        addPlaylistSongItem(realm, playlistSong);
//    }
//
//    public static List<PlaylistModel> getAll(Realm realm) {
//        return getAll(realm, true);
//    }
//
//    public static List<PlaylistModel> getAll(Realm realm, boolean needGetTotalSongs) {
//        DLog.d(TAG, "getAll");
//        final List<PlaylistModel> list = realm
//                .where(PlaylistModel.class)
//                .findAll();
//        return realm.copyFromRealm(list);
//    }
//
//    public static PlaylistModel getTotalSongsInPlaylist(Realm realm, long id) {
//        DLog.d(TAG, "getTotalSongsInPlaylist - id=" + id);
//        final PlaylistModel item = realm
//                .where(PlaylistModel.class)
//                .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, id)
//                .findFirst();
//        return item;
//    }
//
//    public static void deletePlaylistById(Realm realm, long id) {
//        DLog.d(TAG, "deleteById - id=" + id);
//        realm.executeTransaction(realm1 -> {
//            final PlaylistModel item = realm1
//                    .where(PlaylistModel.class)
//                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, id)
//                    .findFirst();
//            if (item != null) {
//                item.deleteFromRealm();
//                realm1.where(PlaylistSongModel.class)
//                        .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, id)
//                        .findAll()
//                        .deleteAllFromRealm();
//            }
//        });
//    }
//
//    public static void removeSongFromPlaylist(Realm realm, long playListId, String path) {
//        DLog.d(TAG, "removeSongFromPlaylist - id=" + playListId + " - path= " + path);
//        realm.executeTransaction(realm1 -> {
//            final PlaylistSongModel item = realm1.where(PlaylistSongModel.class)
//                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, playListId)
//                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
//                    .findFirst();
//            if (item != null) {
//                item.deleteFromRealm();
//            }
//        });
//    }
//
//    public static void removeSongsFromPlaylist(Realm realm, long playListId, String[] songPathArray) {
//        DLog.d(TAG, "removeSongFromPlaylist - id=" + playListId);
//        realm.executeTransaction(realm1 -> {
//            realm1.where(PlaylistSongModel.class)
//                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, playListId)
//                    .in(Constant.PLAYER.DATABASE.FIELD.PATH, songPathArray)
//                    .findAll()
//                    .deleteAllFromRealm();
//        });
//    }
//
//    public static List<VideoModel> getSongInPlaylist(Realm realm, long playListId) {
//        List<PlaylistSongModel> playlistSongModelList = realm
//                .where(PlaylistSongModel.class)
//                .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, playListId)
//                .findAll();
//
//        if (playlistSongModelList != null && !playlistSongModelList.isEmpty()) {
//            String[] songsPathArray = playlistSongModelList.stream().map(PlaylistSongModel::getPath).toArray(String[]::new);
//            List<VideoModel> songList = realm.where(VideoModel.class)
//                    .in(Constant.PLAYER.DATABASE.FIELD.PATH, songsPathArray).findAll();
//            if (songList != null) {
//                return realm.copyFromRealm(songList);
//            }
//        }
//
//        return null;
//    }

}
