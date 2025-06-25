package com.dalread.database;

import com.dalread.model.PlaylistModel;
import com.dalread.model.PlaylistSongModel;
import com.dalread.model.VideoModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Voca;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PlaylistModelQuery {
    private static Realm realm = Voca.getRealm();
    private static final String TAG = "PlayListModelQuery";

    private static void addPlaylistItem(Realm realm, PlaylistModel playlistModel) {
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(playlistModel));
    }

    public static void addPlaylist(Realm realm, PlaylistModel playlistModel) {
        DLog.d(TAG, "add - playlistModel=" + playlistModel.getPlayListId());
        addPlaylistItem(realm, playlistModel);
    }
    //TODO : 실제 비디오 파일이 있는지 확인하고 있는거만 저장해야하는데, 로직이 까다롭다.
    public static void addPlaylistFromJson(String jsonData) {
        DLog.d(TAG, "addPlaylistFromJson");
        if (jsonData != null) {
            // JSON 데이터를 PlaylistModel 리스트로 변환
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PlaylistModel>>() {}.getType();
            List<PlaylistModel> playlistModels = gson.fromJson(jsonData, listType);

            // 데이터베이스에 PlaylistModel 객체 삽입
            realm.executeTransaction(r -> {
                r.insertOrUpdate(playlistModels);
            });
        }
    }

    public static void updatePlaylist(Realm realm, PlaylistModel playlistModel) {
        DLog.d(TAG, "update - playlistModel=" + playlistModel.getPlayListId());
        addPlaylistItem(realm, playlistModel);
    }

    private static void addPlaylistSongItem(Realm realm, PlaylistSongModel playlistSong) {
        realm.executeTransaction(realm1 -> {
            final PlaylistSongModel item = realm1.where(PlaylistSongModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, playlistSong.getPlayListId())
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, playlistSong.getPath())
                    .findFirst();
            if (item == null) {
                DLog.d(TAG, "add song to playlist: " + playlistSong.getId() + " - " + playlistSong.getPath());
                realm1.insertOrUpdate(playlistSong);
            }
        });
    }

    public static void addPlaylistSong(Realm realm, PlaylistSongModel playlistSong) {
        DLog.d(TAG, "add - playlistSong=" + playlistSong.getId());
        addPlaylistSongItem(realm, playlistSong);
    }

    public static List<PlaylistModel> getAll(Realm realm) {
        DLog.d(TAG, "getAll");
        RealmResults<PlaylistModel> results = realm.where(PlaylistModel.class).findAll();
        return realm.copyFromRealm(results);
    }

    public static List<PlaylistModel> getAll(Realm realm, boolean needGetTotalSongs) {
        DLog.d(TAG, "getAll");
        final List<PlaylistModel> list = realm
                .where(PlaylistModel.class)
                .findAll();
        if (list != null) {
            if (needGetTotalSongs && !list.isEmpty()) {
                for (PlaylistModel item : list) {
                    realm.executeTransaction(realm1 -> {
                        int totalSongs = (int) realm1.where(PlaylistSongModel.class)
                                .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, item.getPlayListId()).count();
                        item.setTotalSongs(totalSongs);
                    });
                }
            }
            return realm.copyFromRealm(list);
        }
        return null;
    }

    public static PlaylistModel getTotalSongsInPlaylist(Realm realm, long id) {
        DLog.d(TAG, "getTotalSongsInPlaylist - id=" + id);
        final PlaylistModel item = realm
                .where(PlaylistModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, id)
                .findFirst();
        if (item != null) {
            int totalSongs = (int) realm.where(PlaylistSongModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, item.getPlayListId()).count();
            item.setTotalSongs(totalSongs);
        }

        return item;
    }

    public static void deletePlaylistById(Realm realm, long id) {
        DLog.d(TAG, "deleteById - id=" + id);
        realm.executeTransaction(realm1 -> {
            final PlaylistModel item = realm1
                    .where(PlaylistModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, id)
                    .findFirst();
            if (item != null) {
                item.deleteFromRealm();
                realm1.where(PlaylistSongModel.class)
                        .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, id)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public static void deleteAllPlaylist() {
        DLog.d(TAG, "deleteAllPlaylist");
        realm.executeTransaction(r -> {
            RealmResults<PlaylistModel> results = r.where(PlaylistModel.class).findAll();
            results.deleteAllFromRealm();
        });
    }

    public static void removeSongFromPlaylist(Realm realm, long playListId, String path) {
        DLog.d(TAG, "removeSongFromPlaylist - id=" + playListId + " - path= " + path);
        realm.executeTransaction(realm1 -> {
            final PlaylistSongModel item = realm1.where(PlaylistSongModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, playListId)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                    .findFirst();
            if (item != null) {
                item.deleteFromRealm();
            }
        });
    }

    public static void removeSongsFromPlaylist(Realm realm, long playListId, String[] songPathArray) {
        DLog.d(TAG, "removeSongFromPlaylist - id=" + playListId);
        realm.executeTransaction(realm1 -> {
            realm1.where(PlaylistSongModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, playListId)
                    .in(Constant.PLAYER.DATABASE.FIELD.PATH, songPathArray)
                    .findAll()
                    .deleteAllFromRealm();
        });
    }

    public static List<VideoModel> getSongInPlaylist(Realm realm, long playListId) {
        List<PlaylistSongModel> playlistSongModelList = realm
                .where(PlaylistSongModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PLAYLIST_ID, playListId)
                .findAll();

        if (playlistSongModelList != null && !playlistSongModelList.isEmpty()) {
            String[] songsPathArray = playlistSongModelList.stream().map(PlaylistSongModel::getPath).toArray(String[]::new);
            List<VideoModel> songList = realm.where(VideoModel.class)
                    .in(Constant.PLAYER.DATABASE.FIELD.PATH, songsPathArray).findAll();
            if (songList != null) {
                return realm.copyFromRealm(songList);
            }
        }

        return null;
    }

}
