package com.dalread.util;

import android.content.Context;

import com.dalread.base.EnumLanguage;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.VideoSeasonModelQuery;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.database.sqlite.model.MultiPlayerVideoModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoModel;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MediaFileListUtil {

    static public List<PlayerFileModel> addAllVideosFromRootFolder(Context context, boolean isTypeInitData, boolean isShowSubtitle, int mediaType) {
        List<PlayerFileModel> list = new ArrayList<>();
        long start = System.currentTimeMillis();
        final List<PlayerFileModel> tmp = StorageUtil.getAllFiles(context, null, false, true, isShowSubtitle, true, mediaType);
//        final List<PlayerFileModel> tmp = StorageUtil.getAllFiles(getContext(), StorageUtil.getRootFolder(), false, true, isShowSubtitle, true, mediaType);
        //Failed to find storage device at /data/local/tmp/external 에러가 나서 일단 주석처리
        //======
//        String sdCardStoragePath = StorageUtil.getRootFolderFromSDCard(context);
//        List<PlayerFileModel> fileListFromSdCard = new ArrayList<>();
//        if (sdCardStoragePath != null) {
//            fileListFromSdCard = StorageUtil.getAllFiles(context, sdCardStoragePath, false, true, isShowSubtitle, true);
//        }
        //======
        List<PlayerFileModel> allFileList = new ArrayList<>();
        allFileList.addAll(tmp);
//        allFileList.addAll(fileListFromSdCard);
        VideoSeasonModelQuery.updateAllByTrashToTrue(Voca.getRealm());
        for(PlayerFileModel f : allFileList) {
            if (checkAndCreateVideoModel(context, isTypeInitData, f)) {
                list.add(f);
                VideoSeasonModelQuery.addOrUpdate(Voca.getRealm(), f);
            } else {
                // list.add(f);
            }

//            if (f.isVideo() && mediaType == Constant.MediaType.VIDEO) {
//                if (checkAndCreateVideoModel(f)) {
//                    list.add(f);
//                    VideoSeasonModelQuery.addOrUpdate(Voca.getRealm(), f);
//                }
//            } else if (f.isMusic() && mediaType == Constant.MediaType.MUSIC) {
//                if (checkAndCreateVideoModel(f)) {
//                    list.add(f);
////                    VideoSeasonModelQuery.addOrUpdate(Voca.getRealm(), f);
//                }
//            } else {
//                list.add(f);
//            }
        }
        VideoSeasonModelQuery.deleteAllByTrash(Voca.getRealm());
        long stop = System.currentTimeMillis();
        DLog.d("getAllFiles in Main", "Elapsed: " + (stop - start) + " ms" );
        return list;
    }

    static public boolean checkAndCreateVideoModel(Context context, boolean isTypeInitData, PlayerFileModel playerFileModel) {
        EnumLanguage motherTongueLanguage = EnumLanguage.findByFormatApi(SharedPreferencesDB.getInstance(context).getMotherTongueLanguage());
        VideoModel videoModel = VideoModelQuery.getByPath(Voca.getRealm(), playerFileModel.getPath());
        if (isTypeInitData) {
            if (videoModel != null) {
                videoModel.setTrash(Constant.INT_BOOLEAN.FASLE);
                // delete item whe difficult size
                if (videoModel.getSize() != playerFileModel.getSize() || videoModel.getCreatedDate() != playerFileModel.getCreatedDate()) {
                    VideoModelQuery.deleteByPath(Voca.getRealm(), videoModel.getPath());
                    videoModel = null;
                } else if (videoModel.isHide()){
                    return false;
                }
            }
            if (videoModel == null) {
                videoModel = new VideoModel(playerFileModel.getVideoModel());
                if (!SharedPreferencesDB.getInstance(context).isPlayerFetchAllVideoFirstTime()) {
                    videoModel.setNewFile(Constant.INT_BOOLEAN.TRUE);
                }
                videoModel.setTongueLang(motherTongueLanguage.getIdApi());
            }
            videoModel.setSeasonNameVideoFile(playerFileModel.getVideoModel().getSeasonNameVideoFile());
            //If AraPlayer refresh (Get All video files from root folder) video files, then set the default subtitle file to the video file(If they have same file name)
            if (!Utils.isEmpty(playerFileModel.getSubPath1())) {
                videoModel.setSubPath1(playerFileModel.getSubPath1());
            }
            if (AraFileNameUtil.isHiddenFile(videoModel.getPath())
            || AraFileNameUtil.isFileInHiddenFolder(videoModel.getPath())) {
                videoModel.setHide(Constant.INT_BOOLEAN.TRUE);
            }

            //TODO : Danim - Why need this? (original?)
//            if (Utils.isEmpty(videoModel.getSubPathOriginal())) {
//                if (!Utils.isEmpty(playerFileModel.getSubPath1())) {
//                    videoModel.setSubPath(playerFileModel.getSubPath1());
//                    videoModel.setSubPathOriginal(playerFileModel.getSubPath1());
//                }
//            }
            VideoModelQuery.add(Voca.getRealm(), videoModel);
//            SubtitleUtil.checkAndCreateLanguages(application, playerFileModel);
        }
        if (videoModel == null) {
            videoModel = new VideoModel(playerFileModel.getVideoModel());
            videoModel.setTongueLang(motherTongueLanguage.getIdApi());
        }
        playerFileModel.setVideoModel(videoModel);
        return true;
    }

    /** Realm 사용. (멀티플레이어 외 화면용) */
    static public List<PlayerFileModel> fetchAllVideosFromDBForMultiPlayer() {
        List<PlayerFileModel> list = new ArrayList<>();

        try (Realm realm = Voca.getRealm()) {
            final List<VideoModel> videos = VideoModelQuery.refreshAndGetAllForMultiPlayer(realm);
            if (videos != null) {
                for (VideoModel v : videos) {
                    if (FileUtil.isVideoApp()) {
                        if (FileUtil.isValidVideoExtension(v.getName())) {
                            list.add(new PlayerFileModel(v));
                        }
                    } else {
                        PlayerFileModel playerFileModel = new PlayerFileModel(v);
                        if (FileUtil.isAudioFormat(v.getName())) {
                            playerFileModel.setMusic();
                        }
                        list.add(playerFileModel);

                    }
                }
            }
        }
        return list;
    }

    /** 멀티플레이어 전용: video_meta(SQLite)에서 비디오 목록 조회. Realm 미사용 */
    static public List<PlayerFileModel> fetchAllVideosFromVideoMetaForMultiPlayer(Context context, MultiPlayerDatabase multiPlayerDatabase) {
        List<PlayerFileModel> list = new ArrayList<>();
        if (multiPlayerDatabase == null) return list;
        List<MultiPlayerVideoModel> metaList = multiPlayerDatabase.getAllVideoMeta();
        if (metaList == null) return list;
        for (MultiPlayerVideoModel meta : metaList) {
            String path = meta.getFILE_PATH();
            if (path == null || path.isEmpty()) continue;
            if (!StorageUtil.isFileExist(path)) continue;
            if (FileUtil.isVideoApp() && !FileUtil.isValidVideoExtension(FilenameUtils.getName(path))) continue;
            VideoModel v = new VideoModel(path);
            v.setName(FilenameUtils.getName(path));
            v.setHide(meta.getHide());
            if (FileUtil.isVideoApp()) {
                list.add(new PlayerFileModel(v));
            } else {
                PlayerFileModel playerFileModel = new PlayerFileModel(v);
                if (FileUtil.isAudioFormat(v.getName())) playerFileModel.setMusic();
                list.add(playerFileModel);
            }
        }
        return list;
    }

    static public List<PlayerFileModel> fetchAllVideosFromDB(Context context, boolean isNavTabMedia, boolean isHiddenFilesOnly) {
        List<PlayerFileModel> list = new ArrayList<>();
        boolean isShowNormalVideo = !isHiddenFilesOnly && SharedPreferencesDB.getInstance(context).getShowNormalVideoFileList();

        try (Realm realm = Voca.getRealm()) {
            final List<VideoModel> videos = VideoModelQuery.refreshAndGetAll(realm, isShowNormalVideo, !isNavTabMedia, true);
            if (videos != null) {
                for (VideoModel v : videos) {
                    if (FileUtil.isVideoApp()) {
                        if (FileUtil.isValidVideoExtension(v.getName())) {
                            list.add(new PlayerFileModel(v));
                        }
                    } else {
                        PlayerFileModel playerFileModel = new PlayerFileModel(v);
                        if (FileUtil.isAudioFormat(v.getName())) {
                            playerFileModel.setMusic();
                        }
                        list.add(playerFileModel);

                    }
                }
            }
        }
        return list;
    }
}
