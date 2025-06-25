package com.dalread.activity;

import android.content.Intent;
import android.view.View;

import com.dalread.R;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.database.PlaylistModelQuery;
import com.dalread.database.VideoModelQuery;
import com.dalread.listener.OnClickListener;
import com.dalread.model.MusicCategoryModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.PlaylistModel;
import com.dalread.model.PlaylistSongModel;
import com.dalread.model.VideoModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.Realm;

public class MainPlayerMusicFragment extends MainPlayerMediaFragment implements OnClickListener, OnAsyncTaskListener {

    private MusicCategoryModel musicCategoryModel;
    private MusicCategoryModel currentMusicCategoryModel;
    private List<MusicCategoryModel> musicCategoryModelList;
    private List<VideoModel> musicFileTestPlayList;
    private List<VideoModel> songsInPlayList = new ArrayList<>();

    @Override
    public void initData() {
        super.initData();
        initMusicCategoryModelList();
        if (!Utils.isEmpty(musicCategoryModelList))
            currentMusicCategoryModel = musicCategoryModelList.get(0);
    }

    protected boolean isNavTabMedia() {
        return activity.currentBottomNavigationId == R.id.nav_music;
    }

    protected int getAppMediaType() {
        return Constant.AppMediaType.MUSIC;
    }

    protected int getConfirmRefreshPullToRefresh() {
        return R.string.msg_confirm_refresh_pull_to_refresh_music;
    }

    protected int getMsgIndexingMediaFiles() {
        return R.string.msg_indexing_music_files;
    }

    protected void finishLoadData(int type, Object resultData, Object data) {
        super.finishLoadData(type, resultData, data);
        if (currentMusicCategoryModel.isPlaylist()) {
            loadPlaylist();
        } else {
            fileList.add(0, new PlayerFileModel(requireContext()));

//        addLanguageFolderInFileList(fileList);
//        addMusicCategoryList(fileList);//Don't change order with addAdsBannerInFileList
            loadMusicListCommon();
//        addAdsBannerInFileList(fileList);
//        addMusicCategoryList(fileList);//Don't change order with addAdsBannerInFileList
//        dalPlayerAdapter.setData(fileList);
        }
        dalPlayerAdapter.setOnClickListenerMusicCategory(onClickListenerMusicCategory);
    }

    protected void addMusicCategoryList(List<PlayerFileModel> fileList) {
        if (!isExistMusicCategoryListAlready(fileList)) {
            fileList.add(0, new PlayerFileModel(musicCategoryModelList));
        }
    }

    private void initMusicCategoryModelList() {
        musicCategoryModelList = new ArrayList<>();
        musicCategoryModelList.add(new MusicCategoryModel(1, "PLAYLIST"));
        musicCategoryModelList.add(new MusicCategoryModel(2, "SONGS"));
        musicCategoryModelList.add(new MusicCategoryModel(3, "VIDEOS"));
        musicCategoryModelList.add(new MusicCategoryModel(4, "ARTISTS"));
        musicCategoryModelList.add(new MusicCategoryModel(5, "ALBUMS"));
        musicCategoryModelList.add(new MusicCategoryModel(6, "GENRES"));
        musicCategoryModelList.add(new MusicCategoryModel(7, "FOLDERS"));
        musicCategoryModelList.add(new MusicCategoryModel(99, "test"));

    }

    private boolean isExistMusicCategoryListAlready(List<PlayerFileModel> fileList) {
//        if (Utils.isEmpty(fileList))
//            return true;

        for(PlayerFileModel playerFileModel : fileList) {
            if (playerFileModel.isMusicCategory())
                return true;
        }
        return false;
    }

    private OnClickListener onClickListenerMusicCategory = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            MusicCategoryModel musicCategoryModel = (MusicCategoryModel) object;
            if (musicCategoryModel.getId() != currentMusicCategoryModel.getId()) {
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.IS_SELECT_PLAYLIST_CATEGORY, musicCategoryModel.isPlaylist()));
                activity.callAsyncTask(MainPlayerMusicFragment.this, musicCategoryModel, TYPE_LOAD_MUSIC_LIST_BY_CATEGORY, true);
                activity.isInPlaylistWithSongs = false;
                exitEditMode();
            }
        }
    };

    protected List<PlayerFileModel> loadMusicList(Object data) {
        List<PlayerFileModel> playerFileModelList = null;
        if (!(data instanceof MusicCategoryModel))
            return null;

        musicCategoryModel = (MusicCategoryModel) data;
        currentMusicCategoryModel = musicCategoryModel;

//        if (musicCategoryModel.getId() == currentMusicCategoryModel.getId())
//            return;

        if (musicCategoryModel.isPlaylist()) {
            playerFileModelList = fetchPlaylistFromDB();
        } else if (musicCategoryModel.isSongs()) {
            playerFileModelList = fetchMusicListFromDBBySongs();
        } else if (musicCategoryModel.isVideos()) {
            playerFileModelList = fetchMusicListFromDBByVideos();
        } else if (musicCategoryModel.isArtists()) {
            playerFileModelList = fetchMusicListFromDBByArtists();
        } else if (musicCategoryModel.isAlbums()) {
            playerFileModelList = fetchMusicListFromDBForTest();
        } else if (musicCategoryModel.isGenres()) {
            playerFileModelList = fetchMusicListFromDBForTest();
        } else if (musicCategoryModel.isFolders()) {
            playerFileModelList = fetchMusicListFromDBForTest();
        } else if (musicCategoryModel.isTestList()) {
            playerFileModelList = fetchMusicListFromDBForTest();
        }
        return playerFileModelList;
    }

    private List<PlayerFileModel> fetchMusicListFromDBBySongs() {
        return fileListTotal.stream().filter(e -> e.isMusic()).collect(Collectors.toList());
    }
    private List<PlayerFileModel> fetchMusicListFromDBByVideos() {
        return fileListTotal.stream().filter(e -> e.isVideo()).collect(Collectors.toList());
    }

    private List<PlayerFileModel> fetchMusicListFromDBByArtists() {
        DLog.d(getLogTag(), "fetchMusicListFromDBByArtist");
        List<PlayerFileModel> list = new ArrayList<>();
        try (Realm realm = Voca.getRealm()) {
            final List<VideoModel> videos = VideoModelQuery.getMusicListByArtist(realm);
            videos.forEach(e -> list.add(new PlayerFileModel(e)));
//            if (videos != null) {
//                for (VideoModel v : videos) {
//                    list.add(new PlayerFileModel(v));
//                }
//            }
        }
        return list;
    }

    private List<PlayerFileModel> fetchMusicListFromDBForTest() {
        DLog.d(getLogTag(), "fetchMusicListFromDBByArtist");
        List<PlayerFileModel> list = new ArrayList<>();
        try (Realm realm = Voca.getRealm()) {
            final List<VideoModel> videos = VideoModelQuery.getMusicListForTest(realm);
            musicFileTestPlayList = new ArrayList<>();
//            videos.forEach(e -> list.add(new PlayerFileModel(e)));
            if (videos != null) {
                for (VideoModel v : videos) {
                    if (v.getName().contains("01_test_Beatles-Yesterday.flac") || v.getName().contains("02_test_Olivia Newton  John Physical.mp3")) {
                        list.add(new PlayerFileModel(v));
                        musicFileTestPlayList.add(v);
                    }
                }

                if (list.size() < 2) {
                    for (VideoModel v : videos) {
                        list.add(new PlayerFileModel(v));
                        musicFileTestPlayList.add(v);
                        if (list.size() >= 2)
                            break;
                    }
                }
            }
        }
        return list;
    }

    private List<PlayerFileModel> fetchPlaylistFromDB() {
        DLog.d(getLogTag(), "fetchPlaylistFromDB");
        List<PlayerFileModel> list = new ArrayList<>();
        try (Realm realm = Voca.getRealm()) {
            final List<PlaylistModel> playlistModels = PlaylistModelQuery.getAll(realm);
            if (playlistModels != null) {
                for (PlaylistModel playlistModel : playlistModels) {
                    list.add(new PlayerFileModel(playlistModel));
                }
            }
        }
        return list;
    }

    protected void finishLoadMusicList(Object resultData, Object data) {
        DLog.d(getLogTag(), "finishLoadMusicList");
        fileList = (List<PlayerFileModel>) resultData;
//        addMusicCategoryList(fileList);//Don't change order with addAdsBannerInFileList
        loadMusicListCommon();
    }

    private void loadMusicListCommon() {
//        if (currentMusicCategoryModel.isSongs())
            addLanguageFolderInFileList(fileList);

        addAdsBannerInFileList(fileList);
        addMusicCategoryList(fileList);//Don't change order with addAdsBannerInFileList and addLanguageFolderInFileList

        dalPlayerAdapter.setData(fileList);
        dalPlayerAdapter.setCurrentMusicCategoryModel(currentMusicCategoryModel);
//        rvContent.scrollToPosition(0);
        getLastPlayedVideo();
    }

    @Override
    protected void handleVideoItemClick(PlayerFileModel playerFileModel) {
        if (currentMusicCategoryModel.isTestList()) {
            sharedPreferences.setList(Constant.SHARE_PREF.KEY_MUSIC_PLAYLIST, musicFileTestPlayList);
            Intent intent = new Intent(activity, PlayerActivity.class);
            intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
            intent.putExtra(Constant.PLAYER.INTENT.KEY_MUSIC_PLAYLIST, true);
            startActivity(intent);
        } else if (currentMusicCategoryModel.isPlaylist()) {
            sharedPreferences.setList(Constant.SHARE_PREF.KEY_MUSIC_PLAYLIST, songsInPlayList);
            Intent intent = new Intent(activity, PlayerActivity.class);
            intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
            intent.putExtra(Constant.PLAYER.INTENT.KEY_MUSIC_PLAYLIST, true);
            startActivity(intent);
        } else {
            super.handleVideoItemClick(playerFileModel);
        }
    }

    protected void addANewPlaylist(String name) {
        long playListId = System.currentTimeMillis();
        PlaylistModel playlistModel = new PlaylistModel(playListId, name);
        PlaylistModelQuery.addPlaylist(Voca.getRealm(), playlistModel);
        addSongsToPlaylist(playListId);
    }

    protected void addSongsToPlaylist(long playListId) {
        List<PlayerFileModel> selectedVideoList = dalPlayerAdapter.getSelectedVideoList();
        if (selectedVideoList.isEmpty()) return;
        for (PlayerFileModel playerFileModel : selectedVideoList) {
            PlaylistModelQuery.addPlaylistSong(Voca.getRealm(), new PlaylistSongModel(System.currentTimeMillis(), playListId, playerFileModel.getPath()));
        }
    }

    protected void updatePlaylistNameInListAfterRename(PlaylistModel playlistModel) {
        int index = getPlaylistIndexFromList(playlistModel);
        if (index >= 0) {
            PlaylistModel item = fileList.get(index).getPlaylistModel();
            if (item != null) {
                item.setName(playlistModel.getName());
                dalPlayerAdapter.notifyItemChanged(index);
            }
        }
    }

    protected void removePlaylistFromListAfterDelete(PlaylistModel playlistModel) {
        int index = getPlaylistIndexFromList(playlistModel);
        if (index >= 0) {
            fileList.remove(index);
            dalPlayerAdapter.notifyItemRemoved(index);
        }
    }

    private int getPlaylistIndexFromList(PlaylistModel playlistModel) {
        int index = -1;
        for (int i = 0; i < fileList.size(); i++) {
            if (fileList.get(i).getPlaylistModel() != null &&
                    fileList.get(i).getPlaylistModel().getPlayListId() == playlistModel.getPlayListId()) {
                index = i;
                break;
            }
        }
        return index;
    }

    @Override
    protected List<PlayerFileModel> loadSongsInPlaylist(PlaylistModel data) {
        DLog.d(getLogTag(), "loadSongsInPlaylist playlistID=" + data.getPlayListId());
        List<PlayerFileModel> list = new ArrayList<>();
        songsInPlayList.clear();
        try (Realm realm = Voca.getRealm()) {
            final List<VideoModel> videos = PlaylistModelQuery.getSongInPlaylist(realm, data.getPlayListId());
            if (videos != null) {
                videos.forEach(e -> {
                    list.add(new PlayerFileModel(e));
                    songsInPlayList.add(e);
                });
            }
        }
        return list;
    }

    @Override
    protected void removeSongsFromPlaylist(PlaylistModel data) {
        DLog.d(getLogTag(), "removeSongsFromPlaylist playlistID=" + data.getPlayListId());
        List<PlayerFileModel> selectedVideoList = dalPlayerAdapter.getSelectedVideoList();
        if (selectedVideoList.isEmpty()) return;
        String[] songPathArray = selectedVideoList.stream().map(PlayerFileModel::getPath).toArray(String[]::new);
        PlaylistModelQuery.removeSongsFromPlaylist(Voca.getRealm(), data.getPlayListId(), songPathArray);
    }

    @Override
    protected void loadPlaylist() {
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.IS_SELECT_PLAYLIST_CATEGORY, currentMusicCategoryModel.isPlaylist()));
        activity.callAsyncTask(MainPlayerMusicFragment.this, currentMusicCategoryModel, TYPE_LOAD_MUSIC_LIST_BY_CATEGORY, true);
    }
}