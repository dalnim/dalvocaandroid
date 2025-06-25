package com.dalread.manager;

import com.dalread.database.RealmManagerPlaylistModel;
import com.dalread.model.PlaylistModel;

import java.util.List;

public class PlaylistManager {
    public static List<PlaylistModel> getAllPlaylistModels() {
        return RealmManagerPlaylistModel.getAllPlaylistModels();
    }

    public static PlaylistModel createNewPlaylist(String name) {
        return PlaylistModel.createNewPlaylist(name);
    }

//    public static Results<VideoModel> getVideoModelsHavingPlaylistIds() {
//        return RealmManager.getVideoModelsHavingPlaylistIds();
//    }
//
//    public static void addPlaylistIdToVideoModel(String path, int playlistId) {
//        RealmManager.addPlaylistIdToVideoModel(path, playlistId);
//    }

    public static void savePlaylistModel(PlaylistModel playlistModel) {
        RealmManagerPlaylistModel.savePlaylistModel(playlistModel);
    }

    public static PlaylistModel getPlaylistModelByName(String name) {
        return RealmManagerPlaylistModel.getPlaylistModelByName(name);
    }

    public static PlaylistModel getPlaylistModelById(int id) {
        return RealmManagerPlaylistModel.getPlaylistModelById(id);
    }

    public static long getPlaylistIdByName(String name) {
        return RealmManagerPlaylistModel.getPlaylistIdByName(name);
    }

    public static boolean isPlaylistNameExists(String name) {
        return RealmManagerPlaylistModel.isPlaylistNameExists(name);
    }

    public static boolean deletePlaylistModelById(long id) {
        return RealmManagerPlaylistModel.deletePlaylistModelById(id);
    }

//    public static VideoModel getVideoModelForPath(String path) {
//        return RealmManager.getVideoModelForPath(path);
//    }

    public static boolean hasPlaylist() {
        return RealmManagerPlaylistModel.hasPlaylist();
    }
    public static boolean isAllPlaylistEmpty() {
        return RealmManagerPlaylistModel.isAllPlaylistEmpty();
    }


//    public static void resetPlaylist() {
//        RealmManager.resetAllVideoModelsPlaylistIds();
//        RealmManagerPlaylistModel.removeAll();
//    }
}
