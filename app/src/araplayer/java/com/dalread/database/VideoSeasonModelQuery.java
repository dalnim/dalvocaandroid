package com.dalread.database;

import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoSeasonModel;
import com.dalread.model.VideoSeasonModelBuilder;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

public class VideoSeasonModelQuery {

    private static final String TAG = "VideoModelQuery";

    private static void addOrUpdateItem(Realm realm, VideoSeasonModel model) {
        realm.executeTransaction(realm1 -> realm1.insertOrUpdate(model));
    }

    public static void addOrUpdate(Realm realm, VideoSeasonModel model) {
        DLog.d(TAG, "add - model=" + model.toString());
        addOrUpdateItem(realm, model);
    }

    public static void addOrUpdate(Realm realm, PlayerFileModel model) {
        if (!model.getVideoModel().isSeason())
            return;

        int tvShowId = model.getVideoModel().getTmdbId();
        int seasonId = model.getVideoModel().getTmdbSeasonId();
        String seasonNameVideoFile = model.getVideoModel().getSeasonNameVideoFile();
        String seasonNameTmdb = model.getVideoModel().getSeasonNameVideoFile();
        int seasonNumber = model.getVideoModel().getTmdbSeasonNumber();

        if (model.getSeasonEpisodeInfo() != null) {
            seasonNameVideoFile = model.getSeasonEpisodeInfo().getSeasonNameVideoFile();
            seasonNameTmdb = model.getSeasonEpisodeInfo().getSeasonNameVideoFile();
            seasonNumber = model.getSeasonEpisodeInfo().getSeasonNumber();
            VideoSeasonModel videoSeasonModelByTitle = getBySeasonNameVideoFile(realm, model.getSeasonEpisodeInfo().getSeasonNameVideoFile());
            if (videoSeasonModelByTitle != null) {
                tvShowId = videoSeasonModelByTitle.getTmdbTvShowId() <= 0 ? tvShowId : videoSeasonModelByTitle.getTmdbTvShowId();
                seasonId = videoSeasonModelByTitle.getTmdbSeasonId() <= 0 ? seasonId : videoSeasonModelByTitle.getTmdbSeasonId();
            }
        }

        VideoSeasonModel videoSeasonModel = VideoSeasonModelBuilder.aVideoSeasonModel()
                .withSeasonNameVideoFile(seasonNameVideoFile)
                .withTmdbVideoName(seasonNameTmdb)
                .withTmdbPosterPath(model.getVideoModel().getTmdbPosterPath())
                .withTmdbSeasonPosterPath(model.getVideoModel().getTmdbSeasonPosterPath())
                .withSeasonNumber(seasonNumber)
                .withTmdbTvShowId(tvShowId)
                .withTmdbSeasonId(seasonId)
                .withTrash(Constant.INT_BOOLEAN.FASLE)
                .build();

        DLog.d(TAG, "add - videoModel=" + model.toString());
        addOrUpdateItem(realm, videoSeasonModel);
    }

    public static void updateVideoTitle(Realm realm, PlayerFileModel model, String title) {
        if (Utils.isEmpty(model.getVideoModel().getSeasonNameVideoFile()) || Utils.isEmpty(title))
            return;
        VideoSeasonModel videoSeasonModel = getBySeasonNameVideoFile(realm, model.getVideoModel().getSeasonNameVideoFile());
        if (videoSeasonModel == null)
            return;
        videoSeasonModel.setTmdbVideoName(title);
        DLog.d(TAG, "add - videoModel=" + model.toString());
        addOrUpdateItem(realm, videoSeasonModel);
    }

    public static void updateAllByTrashToTrue(Realm realm) {
        updateAllByTrash(realm, Constant.INT_BOOLEAN.TRUE);
    }

    public static void updateAllByTrash(Realm realm, int trash) {
        DLog.d(TAG, "updateAllDelete - trash=" + trash);
        realm.executeTransaction(realm1 -> {
            final List<VideoSeasonModel> list = getAll(realm1);
            if (list != null) {
                for (VideoSeasonModel item : list) {
                    item.setTrash(trash);
                    realm1.insertOrUpdate(item);
                }
            }
        });
    }

    public static void update(Realm realm, VideoSeasonModel model) {
        DLog.d(TAG, "update - videoModel=" + model.toString());
        addOrUpdateItem(realm, model);
    }

    public static List<VideoSeasonModel> getAll(Realm realm) {
        final List<VideoSeasonModel> list = realm
                .where(VideoSeasonModel.class)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }


    public static List<VideoSeasonModel> getAllUniqueSeries(Realm realm) {
        final List<VideoSeasonModel> list = realm
                .where(VideoSeasonModel.class)
                .distinct(Constant.PLAYER.DATABASE.FIELD.SEASON_NAME_VIDEO_FILE)
                .sort(Constant.PLAYER.DATABASE.FIELD.SEASON_NAME_VIDEO_FILE, Sort.ASCENDING, Constant.PLAYER.DATABASE.FIELD.SEASON_NUMBER, Sort.ASCENDING)
                .findAll();
        if (list != null)
            return realm.copyFromRealm(list);
        return null;
    }


    public static VideoSeasonModel getBySeasonNameVideoFile(Realm realm, String seasonNameVideoFile) {
        final VideoSeasonModel model = realm
                .where(VideoSeasonModel.class)
                .equalTo(Constant.PLAYER.DATABASE.FIELD.SEASON_NAME_VIDEO_FILE, seasonNameVideoFile)
                .findFirst();
        if (model != null)
            return realm.copyFromRealm(model);
        return null;
    }

    public static void deleteBySeasonNameVideoFile(Realm realm, String seasonNameVideoFile) {
        DLog.d(TAG, "deleteByPath - SeasonNameVideoFile=" + seasonNameVideoFile);
        realm.executeTransaction(realm1 -> {
            final VideoSeasonModel model = realm1
                    .where(VideoSeasonModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.SEASON_NAME_VIDEO_FILE, seasonNameVideoFile)
                    .findFirst();
            if (model != null) {
                model.deleteFromRealm();
            }
        });
    }

    public static void deleteAll(Realm realm) {
        DLog.d(TAG, "deleteAll");
        realm.executeTransaction(realm1 -> {
            realm1.where(VideoSeasonModel.class).findAll().deleteAllFromRealm();
        });
    }

    public static void deleteAllByTrash(Realm realm) {
        DLog.d(TAG, "deleteAllByTrash");
        realm.executeTransaction(realm1 -> {
            realm1.where(VideoSeasonModel.class)
                    .equalTo(Constant.PLAYER.DATABASE.FIELD.TRASH, Constant.INT_BOOLEAN.TRUE)
                    .findAll().deleteAllFromRealm();
        });
    }

}
