package com.dalread.database;

import com.dalread.model.VideoModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

public class VideoModelQuery {

    private static final String TAG = "VideoModelQuery";

    private static void addItem(Realm realm, VideoModel videoModel) {
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(videoModel));
    }

    public static void add(Realm realm, String path) {
        VideoModel videoModel = new VideoModel(path);
        add(realm, videoModel);
    }

    public static void add(Realm realm, VideoModel videoModel) {
        DLog.d(TAG, "add - videoModel=" + videoModel.toString());
        addItem(realm, videoModel);
    }

    public static void update(Realm realm, VideoModel videoModel) {
        DLog.d(TAG, "update - videoModel=" + videoModel.toString());
        addItem(realm, videoModel);
    }

    public static List<VideoModel> getAll(Realm realm) {
        return getAll(realm, true, false);
    }

    public static List<VideoModel> getAll(Realm realm, boolean isShowNormalVideo) {
        DLog.d(TAG, "getAll");
        final List<VideoModel> list = realm
                .where(VideoModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.FASLE)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.HIDE, isShowNormalVideo ? Constant.INT_BOOLEAN.FASLE : Constant.INT_BOOLEAN.TRUE)
                .findAll();
        if (list != null) {
            return realm.copyFromRealm(list);
        }
        return null;
    }

    public static List<VideoModel> getAll(Realm realm, boolean isShowNormalVideo, boolean isVideoNetwork) {
        DLog.d(TAG, "getAll");
        return refreshAndGetAll(realm, isShowNormalVideo, isVideoNetwork, false);
    }

    public static List<VideoModel> refreshAndGetAllForMultiPlayer(Realm realm) {
        DLog.d(TAG, "refreshAndGetAll: ");
        realm.refresh();

        RealmQuery<VideoModel> query = realm
                .where(VideoModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.FASLE);

        final List<VideoModel> list = query.findAll();
        if (list != null) {
            return realm.copyFromRealm(list);
        }
        return null;
    }


    public static List<VideoModel> refreshAndGetAllForMultiPlayer1(Realm realm, boolean isShowNormalVideo, boolean isPlaylist) {
        DLog.d(TAG, "refreshAndGetAll: ");
        realm.refresh();
        final List<VideoModel> list = realm
                .where(VideoModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.FASLE)

                .equalTo(Constant.PLAYER.DATABASE.FIELD.HIDE, isShowNormalVideo ? Constant.INT_BOOLEAN.FASLE : Constant.INT_BOOLEAN.TRUE)
                .findAll();
        if (list != null) {
            return realm.copyFromRealm(list);
        }
        return null;
    }

    public static List<VideoModel> refreshAndGetAll(Realm realm, boolean isShowNormalVideo, boolean isVideoNetwork, boolean isNeedRefresh) {
        DLog.d(TAG, "refreshAndGetAll: " + isNeedRefresh);
        if (isNeedRefresh) {
            realm.refresh();
        }
        final List<VideoModel> list = realm
                .where(VideoModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.FASLE)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.HIDE, isShowNormalVideo ? Constant.INT_BOOLEAN.FASLE : Constant.INT_BOOLEAN.TRUE)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.VIDEO_FROM_NETWORK, isVideoNetwork ? Constant.INT_BOOLEAN.TRUE : Constant.INT_BOOLEAN.FASLE)
                .findAll();
        if (list != null) {
            return realm.copyFromRealm(list);
        }
        return null;
    }

    public static List<VideoModel> getSeasonVideoList(Realm realm, String seasonNameVideoFile) {
        final List<VideoModel> list = realm
                .where(VideoModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.FASLE)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.HIDE, Constant.INT_BOOLEAN.FASLE)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.SEASON_NAME_VIDEO_FILE, seasonNameVideoFile)
                .sort(Constant.PLAYER.DATABASE.FIELD.NAME)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static List<VideoModel> getMusicListByArtist(Realm realm) {
        final List<VideoModel> list = realm
                .where(VideoModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.FASLE)
                .notEqualTo(Constant.PLAYER.DATABASE.FIELD.ARTIST, Constant.BASE_BLANK)
                .sort(Constant.PLAYER.DATABASE.FIELD.ARTIST)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static List<VideoModel> getMusicListForTest(Realm realm) {
        final List<VideoModel> list = realm
                .where(VideoModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.FASLE)
                .sort(Constant.PLAYER.DATABASE.FIELD.NAME)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static List<VideoModel> getAllByQuiz(Realm realm) {
        final List<VideoModel> list = realm
                .where(VideoModel.class)
                .distinct(Constant.PLAYER.DATABASE.FIELD.SUB_PATH1)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.FASLE)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.HIDE, Constant.INT_BOOLEAN.FASLE)
                .greaterThan(Constant.PLAYER.DATABASE.FIELD.VOCA_KNOW_ALL, 0)
                .sort(Constant.PLAYER.DATABASE.FIELD.VOCA_KNOW_ALL, Sort.DESCENDING)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }

    public static VideoModel getByPath(Realm realm, String path) {
        final VideoModel videoModel = realm
                .where(VideoModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                .findFirst();
        if (videoModel != null)
            return realm.copyFromRealm(videoModel);
        return null;
    }

    public static void updateBySubPath(Realm realm, String subPath, String value) {
        realm.executeTransaction(realm1 -> {
            final List<VideoModel> items = realm1
                    .where(VideoModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.SUB_PATH_ORIGINAL, subPath)
                    .findAll();
            if (items != null && !items.isEmpty()) {
                for (VideoModel item : items) {
                    item.setSubPath(value);
//                    item.setSubPathOriginal(value);
                }
            }

            final List<VideoModel> items2 = realm1
                    .where(VideoModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.SUB_PATH1, subPath)
                    .findAll();
            if (items2 != null && !items2.isEmpty()) {
                for (VideoModel item : items2) {
//                    item.setSubPath(item.getSubPathOriginal());
                    item.setSubPath1(item.getSubPath1());
                }
            }
        });
    }
    public static void updateByPath(Realm realm, String oldPath, String newPath) {
        realm.executeTransaction(realm1 -> {
            final VideoModel video = realm1
                    .where(VideoModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, oldPath)
                    .findFirst();

            if (video != null) {
                // 새로운 VideoModel 객체를 복사 생성자를 사용하여 생성합니다.
                VideoModel updatedVideo = new VideoModel(video, newPath);
                // Realm에 새로운 객체 추가
                realm1.insertOrUpdate(updatedVideo);
                video.deleteFromRealm();
            }
        });
    }

    public static void deleteByPath(Realm realm, String path) {
        DLog.d(TAG, "deleteByPath - path=" + path);
        realm.executeTransaction(realm1 -> {
            final VideoModel video = realm1
                    .where(VideoModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                    .findFirst();
            if (video != null) {
                video.deleteFromRealm();
            }
        });
    }

    public static void deleteAll(Realm realm) {
        DLog.d(TAG, "deleteAll");
        realm.executeTransaction(realm1 -> {
            realm1.where(VideoModel.class).findAll().deleteAllFromRealm();
        });
    }

    public static void deleteAllByTrash(Realm realm) {
        DLog.d(TAG, "deleteAllByTrash");
        realm.executeTransaction(realm1 -> {
            realm1.where(VideoModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.TRUE)
//                    .equalTo(Constant.PLAYER.DATABASE.FIELD.HIDE, Constant.INT_BOOLEAN.FASLE)
                    .findAll().deleteAllFromRealm();
        });
    }

    public static void updateAllByTrashToTrue(Realm realm) {
        updateAllByTrash(realm, Constant.INT_BOOLEAN.TRUE);
    }

    public static void updateAllByTrash(Realm realm, int trash) {
        DLog.d(TAG, "updateAllDelete - trash=" + trash);
        realm.executeTransaction(realm1 -> {
            final List<VideoModel> list = getAll(realm1);
            if (list != null) {
                for (VideoModel item : list) {
                    item.setTrash(trash);
                    realm1.insertOrUpdate(item);
                }
            }
        });
    }

    public static void updateAllNewFileToFalse(Realm realm) {
        realm.executeTransaction(realm1 -> {
            final List<VideoModel> list = getAll(realm1);
            if (list != null) {
                for (VideoModel item : list) {
                    item.setNewFile(Constant.INT_BOOLEAN.FASLE);
                    realm1.insertOrUpdate(item);
                }
            }
        });
    }

    //Dalnim add
    public static void updateVideoDuration(Realm realm, String path, long duration) {
        realm.executeTransaction(realm1 -> {
            final VideoModel videoModel = realm1
                    .where(VideoModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.PATH, path)
                    .findFirst();
            if (videoModel != null) {
                videoModel.setDuration(duration);
                realm1.insertOrUpdate(videoModel);
            }
        });
    }
}
