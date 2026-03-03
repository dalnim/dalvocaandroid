package com.dalread.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dalread.R;
import com.dalread.adapter.DalPlayerAdapter;
import com.dalread.base.BaseMainPlayerFragment;
import com.dalread.component.CenterLayoutManager;
import com.dalread.database.SubModelQuery;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.VideoSeasonModelQuery;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.databinding.FragmentMainPlayerBinding;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.PlayerShowMeaningDialog;
import com.dalread.dialog.SelectRowColumnDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnLongClickListener;
import com.dalread.listener.OnScrollListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.PlaylistModel;
import com.dalread.model.ServerModel;
import com.dalread.model.VideoModel;
import com.dalread.model.VideoSeasonModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AbstractPointUtil;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.BaseMobileAd;
import com.dalread.util.BaseStorageUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DarkThemeUtil;
import com.dalread.util.FileUtil;
import com.dalread.util.GuideUtil;
import com.dalread.util.Loading;
import com.dalread.util.MediaFileListUtil;
import com.dalread.util.MediaListUtil;
import com.dalread.util.MobileAd;
import com.dalread.util.PointUtil;
import com.dalread.util.ServerManager;
import com.dalread.util.StorageUtil;
import com.dalread.util.SupportSubtitleFormat;
import com.dalread.util.TmdbUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.UserUtil;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;
import com.dalread.util.VideoUtil;
import com.dalread.util.Voca;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.realm.Realm;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;
import okhttp3.ResponseBody;

public class MainPlayerMediaFragment extends BaseMainPlayerFragment implements View.OnClickListener, OnClickListener, OnAsyncTaskListenerWithType {
    protected AdView mAdView;
    protected List<PlayerFileModel> fileListTotal = new ArrayList<>();
    protected List<PlayerFileModel> fileList = new ArrayList<>();
    protected DalPlayerAdapter dalPlayerAdapter;
    protected PlayerFileModel lastPlayerFileModel; // last played video
    private int currentSearchType;
    protected final int TYPE_INIT_DATA = 1;
    protected final int TYPE_RELOAD_DATA_FROM_DB = TYPE_INIT_DATA + 1;
    protected final int TYPE_LOAD_NEXT_FOLDER_DATA = TYPE_RELOAD_DATA_FROM_DB + 1;  //Dalnim : Use when AraPlayre shows media in a folder.
    protected final int TYPE_LOAD_PREV_FOLDER_DATA = TYPE_LOAD_NEXT_FOLDER_DATA + 1;
    protected final int TYPE_DELETE_SUBTITLE = TYPE_LOAD_PREV_FOLDER_DATA + 1;
    protected final int TYPE_DELETE_VIDEO = TYPE_DELETE_SUBTITLE + 1;
    protected final int TYPE_SEARCH = TYPE_DELETE_VIDEO + 1;
    protected final int TYPE_LOAD_SEASON_VIDEO_LIST = TYPE_SEARCH + 1;
    protected final int TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST = TYPE_LOAD_SEASON_VIDEO_LIST + 1;
    protected final int TYPE_DELETE_SELECTED_VIDEOS = TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST + 1;
    protected final int TYPE_LOAD_MUSIC_LIST_BY_CATEGORY = TYPE_DELETE_SELECTED_VIDEOS + 1;
    protected final int TYPE_CREATE_A_NEW_PLAYLIST = TYPE_LOAD_MUSIC_LIST_BY_CATEGORY + 1;
    protected final int TYPE_ADD_SONGS_TO_PLAYLIST = TYPE_CREATE_A_NEW_PLAYLIST + 1;
    protected final int TYPE_LOAD_SONGS_IN_PLAYLIST = TYPE_ADD_SONGS_TO_PLAYLIST + 1;
    protected final int TYPE_REMOVE_SONGS_FROM_PLAYLIST = TYPE_LOAD_SONGS_IN_PLAYLIST + 1;
    protected ServerModel serverModel;
    private boolean isResume = false;  //앱을 열었을때 비디오리스트를 새로 읽을지 아님여부 결정
    protected CenterLayoutManager centerLayoutManager;
    protected AlertDialog alertDialog;
    protected VideoSeasonModel videoSeasonModel;
    protected VideoModel currentVideoModel;
    private ActivityResultLauncher<IntentSenderRequest> intentSenderLauncher;
    private List<PlayerFileModel> deletedFilesList = new ArrayList<>();
    private int deletedFilePos = 0;
    private PlaylistModel selectedPlaylist;
    private ActivityResultLauncher<IntentSenderRequest> deleteSameFileNameIntentSenderLauncher;
    private PlayerFileModel downloadedFile;
    private MultiPlayerDatabase multiPlayerDatabase;
    private AbstractPointUtil pointUtil;

    protected FragmentMainPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentMainPlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        initEventBus();
        pointUtil = new PointUtil(activity);
        if (getArguments() != null) {
            serverModel = getArguments().getParcelable(Constant.PLAYER.INTENT.KEY_DATA);
            DLog.d(getLogTag(), "serverModel=" + serverModel.toString());
        }
        new FastScrollerBuilder(binding.rvContent).build();
        dalPlayerAdapter = new DalPlayerAdapter(activity, fileList, getAppMediaType(), this, onClickListenerVideoSeries, onRvSeasonScrollListener, onVideoLongClickListener);
        if (FileUtil.isMusicApp()) {
            dalPlayerAdapter.setOnClickEditPlaylist(onClickEditPlaylist);
        }
        dalPlayerAdapter.setShowIconArrow(!isNavTabMedia());
        centerLayoutManager = new CenterLayoutManager(getActivity());
        binding.rvContent.setAdapter(dalPlayerAdapter);
        binding.rvContent.setHasFixedSize(true);
        binding.rvContent.setLayoutManager(centerLayoutManager);
        //Without this, when I return the video list and scroll up, I can see the "Pull to refresh" menu.
        binding.rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                updateEnabledPullToRefresh();
            }
        });
        activity.resetCurrentPath();
        binding.pullToRefresh.setOnRefreshListener(onRefreshListener);

        showVideoFolderForStudyLanguageUI();
        initDialog();
        initData();
        initOnClickListener();
        intentSenderLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            int resultCode = result.getResultCode();
            if (resultCode != Activity.RESULT_OK) {
                deletedFilesList.remove(deletedFilePos);
            }
            deletedFilePos++;
            if (deletedFilePos == deletedFilesList.size()) {
                deletedFilesList.forEach(file -> deleteRelatedVideoFile(file, false));
                binding.rvContent.post(() -> dalPlayerAdapter.notifyDataSetChanged());
                deletedFilesList.clear();
                deletedFilePos = 0;
            }
        });

        deleteSameFileNameIntentSenderLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && downloadedFile != null) {
                ToastUtil.getInstance(activity).show(R.string.msg_download_not_supported);
                downloadedFile = null;
            }
        });

        binding.tvRemainPoint.setOnClickListener(v -> {
//            if (UserUtil.isDebugOrAdminUser(requireContext())) {
//                Intent intent = new Intent(activity, PlayerNetworkActivity.class);
//                activity.startActivity(intent);
//            } else {
                activity.startActivity(new Intent(activity, InAppPointListActivity.class));
//            }
        });
        initSubDatabase();
        showRewardButton();
        addMobileAdsView();
        loadBanner();
    }

    private void showClearMultiScreenHistoryButton() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            if (multiPlayerDatabase.isHasList()) {
                binding.ivClearMultiScreenHistory.setVisibility(View.VISIBLE);
            } else {
                binding.ivClearMultiScreenHistory.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showSetPoint0ButtonOnDebug() {
        if (UserUtil.isDebugOrAdminUser(activity)) {
            binding.ivSetPoint0.setVisibility(View.VISIBLE);
        } else {
            binding.ivSetPoint0.setVisibility(View.GONE);
        }
//        ivSetPoint0.setVisibility(View.VISIBLE);
    }

    private void showRewardButton() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            if (sharedPreferences.isFirstShowGuideScreenCount() || pointUtil.needToShowRewardButton()) {
                binding.btnWatchRewardedAd.setVisibility(View.VISIBLE);
//                showGuideWatchAd();
            } else {
                binding.btnWatchRewardedAd.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initDialog() {
        alertDialog = new AlertDialog(activity);
    }
    private void showVideoFolderForStudyLanguageUI() {
        int msg = FileUtil.isVideoApp() ? R.string.msg_video_folder : R.string.msg_music_folder;
        binding.llvideoFolderForStudyLanguageInfo.tvvideoFolderForStudyLanguageInfo.setText(getString(msg, StorageUtil.getMediaFolderWithLangName(activity)));
    }
    private void refreshRemainPoint() {
        int point = pointUtil.getPoint();
        if (sharedPreferences.isPointAdded()) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.text_scale_anim);
            binding.tvRemainPoint.startAnimation(animation);
            sharedPreferences.setPointAdded(false);
        }
        binding.tvRemainPoint.setText(getResources().getQuantityString(R.plurals.point, point, point));
        showRewardButton();
    }

    @Override
    public void initData() {
        DLog.d(getLogTag(), "initData");
    }

    @Override
    public void onResume() {
        super.onResume();
        getMultiPlayerRowColumn();
        if (activity.isShowSignInUpHiddenDialog) {
            activity.isShowSignInUpHiddenDialog = false;
            return;
        }
        updateEnabledPullToRefresh();
        if (isResume) {
            exitEditMode();
            lastPlayerFileModel = null;
            if (activity.isSmallGroupVideoListLoaded()) {
                if (activity.isSmallGroupVideoListLoaded_Season()) {
                    activity.callAsyncTask(MainPlayerMediaFragment.this, videoSeasonModel, TYPE_LOAD_SEASON_VIDEO_LIST, true);
                } else if (activity.isSmallGroupVideoListLoaded_StudyLanguageVideoFolder()) {
                    activity.callAsyncTask(MainPlayerMediaFragment.this, null, TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST, true);
                }
            } else {
                callAsyncTask(activity.getCurrentPath(), TYPE_RELOAD_DATA_FROM_DB, false);
            }
        } else {
            isResume = true;
            if (isPlayerFetchAllVideoFirstTime()) {
                callAsyncTask(activity.getCurrentPath(), TYPE_INIT_DATA, true);
            } else {
                callAsyncTask(activity.getCurrentPath(), TYPE_RELOAD_DATA_FROM_DB, true);
            }
        }
        showMainFragmentViewForMultiPlayer();
        showClearMultiScreenHistoryButton();
        showSetPoint0ButtonOnDebug();
        BaseMobileAd.loadRewardedAd(getContext());
        refreshRemainPoint();
    }

    private void getMultiPlayerRowColumn() {
        updateRowColumnButton();
    }

    private void showGuideScreenCount() {
        if (sharedPreferences.isFirstShowGuideScreenCount()) {
            sharedPreferences.setFirstShowGuideScreenCount();
            String title = getString(R.string.guide_multi_player_screen_count);
            activity.runOnUiThread(() -> {
                GuideUtil.showGuideView(activity, title, binding.btnScreenCount, view -> showGuidePointDeduction());
            });
        }
    }

    private void showGuidePointDeduction() {
        if (sharedPreferences.isFirstShowGuidePointDeduction()) {
            sharedPreferences.setFirstShowGuidePointDeduction();
            String title = getString(R.string.guide_multi_player_point_deduction);
            activity.runOnUiThread(() -> {
                GuideUtil.showGuideView(activity, title, binding.tvRemainPoint, view -> showGuideWatchAd());
            });
        }
    }

    private void showGuideWatchAd() {
        if (sharedPreferences.isFirstShowGuideWatchAd()) {
            sharedPreferences.setFirstShowGuideWatchAd();
            String title = getString(R.string.guide_watch_ad_to_get_point);
            GuideUtil.showGuideView(activity, title, binding.btnWatchRewardedAd, view -> {binding.btnWatchRewardedAd.setVisibility(View.INVISIBLE);});
        }
    }
    private void showMainFragmentViewForMultiPlayer() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            binding.llMainMultiplePlayerFragment.setVisibility(View.VISIBLE);
            binding.llMainPlayerFragment.setVisibility(View.GONE);
        } else {
            binding.llMainMultiplePlayerFragment.setVisibility(View.GONE);
            binding.llMainPlayerFragment.setVisibility(View.VISIBLE);
        }
    }

    private void temporarilyHideBannerAdsAfterClick() {
        if (AppFlavorUtil.isAraMultiPlayerApp() && !BaseMobileAd.isShowAdsAfterTimeSinceLastClick(activity)) {
            binding.adViewContainer.setVisibility(View.GONE);
        }
    }
    private void watchRewardedAd() {
        binding.btnWatchRewardedAd.setVisibility(View.INVISIBLE);
        pointUtil.showRewardedAd(createRewardPointListener());
    }

    private PointUtil.OnRewardPointListener createRewardPointListener() {
        return new PointUtil.OnRewardPointListener() {
            @Override
            public void onSuccess() {
                refreshRemainPoint();
            }

            @Override
            public void onContinue() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail() {

            }
        };
    }

    private void openMultiplePlayerView() {
        final int row = sharedPreferences.getMultiPlayerRow();
        final int column = sharedPreferences.getMultiPlayerColumn();
        final int numberOfScreens = row * column;
        if (pointUtil.needToShowFullAd()) {
            final YesNoDialog dialog = new YesNoDialog(activity,
                    R.string.info,
                    R.string.msg_warning_no_points_watch_ads_get_points, null,
                    new OnYesNoClickListener() {
                        @Override
                        public void onYesClick(View view, Object object) {
                            watchRewardedAd();
                        }

                        @Override
                        public void onNoClick(View view, Object object) {

                        }
                    });
            dialog.show();
        } else {
            if (multiPlayerDatabase.isHasList()) {
                final YesNoDialog dialog = new YesNoDialog(activity, R.string.info, R.string.msg_ask_multiplayer_open_last_watched_videos, null, new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        openMultiplePlayerView(row, column, numberOfScreens, true);
                    }

                    @Override
                    public void onNoClick(View view, Object object) {
                        openMultiplePlayerView(row, column, numberOfScreens, false);
                    }
                });
                dialog.show();
            } else {
                openMultiplePlayerView(row, column, numberOfScreens, false);
            }
        }
    }
    private void openMultiplePlayerView(int row, int column, int numberOfScreens, boolean isLoadLastWatchedVideos) {
        Intent intent = new Intent(activity, MultiplePlayerActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_ROW, row);
        intent.putExtra(Constant.BUNDLE.KEY_COLUMN, column);
        intent.putExtra(Constant.BUNDLE.KEY_NUMBER_OF_SCREENS_MULTIPLE_PLAYER, numberOfScreens);
        intent.putExtra(Constant.BUNDLE.KEY_LOAD_LAST_WATCHED_VIDEO_MULTIPLE_PLAYER, isLoadLastWatchedVideos);
        activity.openNewScreen(intent);
    }
    @Override
    public void onStop() {
        super.onStop();
        new Thread(() -> ServerManager.getInstance().disconnectFTPServer()).start();
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.MAIN) {
            switch (event.getEventType()) {
                case PLAYER_BACK_FOLDER:
                    callAsyncTask((String) event.getModel(), TYPE_LOAD_PREV_FOLDER_DATA);
                    break;
                case PLAYER_SORT:
                    sortFiles((int) event.getModel());
                    break;
                case PLAYER_RELOAD:
                    if (videoSeasonModel != null)
                        videoSeasonModel = VideoSeasonModelQuery.getBySeasonNameVideoFile(Voca.getRealm(),videoSeasonModel.getSeasonNameVideoFile());
                    onResume(); // performResume() in Fragment.java DOES NOT call onResume(). So I need this here.
                    break;
                case PLAYER_REFRESH_VIDEO_LIST:
                    callRefreshMethod();
                case PLAYER_SEASON_BACK:
                    onResume();
                    break;
                case PLAYER_SEARCH:
                    activity.callAsyncTask(this, activity.getSearchValue(), TYPE_SEARCH, false);
                    break;
                case PLAYER_RELOAD_VIDEO:
                    currentVideoModel = (VideoModel) event.getModel();
                    break;
                case PLAYER_EDIT:
                    dalPlayerAdapter.setEditMode((boolean) event.getModel());
                    break;
                case SELECT_ALL_VIDEOS:
                    dalPlayerAdapter.selectAllVideo((boolean) event.getModel());
                    break;
                case SHOW_HIDE_VIDEOS:
                    showHideSelectedVideos();
                    break;
                case DELETE_VIDEOS:
                    showDialogConfirmDeleteSelectedVideos();
                    break;
                case CREATE_A_NEW_PLAYLIST:
                    activity.callAsyncTask(MainPlayerMediaFragment.this, event.getModel(), TYPE_CREATE_A_NEW_PLAYLIST, true);
                    break;
                case ADD_SONGS_TO_SELECTED_PLAYLIST:
                    activity.callAsyncTask(MainPlayerMediaFragment.this, event.getModel(), TYPE_ADD_SONGS_TO_PLAYLIST, true);
                    break;
                case RENAME_PLAYLIST:
                    updatePlaylistNameInListAfterRename((PlaylistModel) event.getModel());
                    break;
                case DELETE_PLAYLIST:
                    removePlaylistFromListAfterDelete((PlaylistModel) event.getModel());
                    break;
                case REMOVE_SONGS_FROM_PLAYLIST:
                    activity.callAsyncTask(MainPlayerMediaFragment.this, selectedPlaylist, TYPE_REMOVE_SONGS_FROM_PLAYLIST, true);
                    break;
                case BACK_TO_PLAYLIST:
                    loadPlaylist();
                    break;
                case REMOVE_BANNER_ADS:
                    removeBannerAds((Boolean) event.getModel());
                    break;
            }
        } else if (event.getScreen() == BaseEvent.Screen.VIDEO_INFORMATION) {
            switch (event.getEventType()) {
                case PLAYER_RELOAD:
                    if (videoSeasonModel != null)
                        videoSeasonModel = VideoSeasonModelQuery.getBySeasonNameVideoFile(Voca.getRealm(), videoSeasonModel.getSeasonNameVideoFile());
//                    onResume(); // performResume() in Fragment.java calls onResume() too. So if I have this here, the onResume() is called twice.

                    break;
            }
        }

    }

    @Override
    public void onClick(View view, Object object) {
        PlayerFileModel playerFileModel = (PlayerFileModel) object;
        switch (view.getId()) {
            case R.id.llItem:
                if (playerFileModel.isSeasonItem()) {
                    VideoSeasonModel videoSeasonModel = playerFileModel.getVideoSeasonModel();
                    activity.callAsyncTask(MainPlayerMediaFragment.this, videoSeasonModel, TYPE_LOAD_SEASON_VIDEO_LIST, true);
                    exitEditMode();
                } else {
                    PlayerFileModel playerFile = (PlayerFileModel) object;
                    if ((playerFile.isVideo() || playerFile.isMusic()) && dalPlayerAdapter.isEditMode()) {
                        selectVideoItem();
                    } else {
                        handleVideoItemClick(playerFile);
                    }
                }
                break;
            case R.id.cb_select_item:
                selectVideoItem();
                break;
            case R.id.llFolder:
                if (playerFileModel.isLanguageFolder()) {
                    activity.callAsyncTask(MainPlayerMediaFragment.this, null, TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST, true);
                } else if (playerFileModel.isSeasonItem()) {

                } else {
                    callAsyncTaskPlayerFile((PlayerFileModel) object, TYPE_LOAD_NEXT_FOLDER_DATA);
                }
                exitEditMode();
                break;
            case R.id.ivInfo:
                openOptionPlayer((PlayerFileModel) object);
                break;
            case R.id.izbVideoThumbnail:
                ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(activity, playerFileModel);
                zoomedPhotoDialog.loadThumbnailFromFileAndShow(playerFileModel, ((ImageView) view).getDrawable());
                break;
            case R.id.llPlaylist:
                PlaylistModel playlistModel = playerFileModel.getPlaylistModel();
                selectedPlaylist = playlistModel;
                activity.isInPlaylistWithSongs = true;
                activity.callAsyncTask(MainPlayerMediaFragment.this, playlistModel, TYPE_LOAD_SONGS_IN_PLAYLIST, true);
                break;
        }
    }

    private void selectVideoItem() {
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ALL_VIDEOS_SELECTED, dalPlayerAdapter.isSelectAllVideoFile()));
    }

    protected void exitEditMode() {
        if (dalPlayerAdapter.isEditMode()) {
            dalPlayerAdapter.setEditMode(false);
            eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_EDIT, false));
        }
    }

    private OnClickListener onClickListenerVideoSeries = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            VideoSeasonModel videoSeasonModel = (VideoSeasonModel) object;
            activity.callAsyncTask(MainPlayerMediaFragment.this, videoSeasonModel, TYPE_LOAD_SEASON_VIDEO_LIST, true);
            exitEditMode();
        }
    };

    private OnScrollListener onRvSeasonScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(int newState) {
            binding.pullToRefresh.setEnabled(newState == RecyclerView.SCROLL_STATE_IDLE);
        }
    };

    private OnLongClickListener onVideoLongClickListener = (view, object) -> {
        if (view.getId() == R.id.llItem) {
            eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ENABLE_EDIT_WHEN_LONG_CLICK_ITEM, null));
            selectVideoItem();
        }
    };

    private OnClickListener onClickEditPlaylist = (view, object) -> {
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.EDIT_PLAYLIST, object));
    };

    private void initOnClickListener() {
        binding.llPlayLastVideo.setOnClickListener(this);
        binding.ivPlayLastVideoPlayButton.setOnClickListener(this);
        binding.llSeasonInfo.ivPoster.setOnClickListener(this);
        binding.llSeasonInfo.ivSeasonPoster.setOnClickListener(this);
        binding.btnWatchVideo.setOnClickListener(this);
        binding.btnScreenCount.setOnClickListener(this);
        binding.btnWatchRewardedAd.setOnClickListener(this);
        binding.ivClearMultiScreenHistory.setOnClickListener(this);
        binding.ivSetPoint0.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_play_last_video:
                playLastPlayed();
                break;
            case R.id.iv_play_last_video_play_button:
                openLastPlayedVideo();
                break;
            case R.id.ivPoster: {
                final String posterImagePath = Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_ORIGINAL_SIZE + videoSeasonModel.getTmdbPosterPath();
                showThumbnailFromPath(posterImagePath, binding.llSeasonInfo.ivPoster.getDrawable());
                break;
            }
            case R.id.ivSeasonPoster: {
                final String seasonImagePath = Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_ORIGINAL_SIZE + videoSeasonModel.getTmdbSeasonPosterPath();
                showThumbnailFromPath(seasonImagePath, binding.llSeasonInfo.ivSeasonPoster.getDrawable());
                break;
            }
            case R.id.btnWatchVideo:
                openMultiplePlayerView();
                break;
            case R.id.btnScreenCount:
                selectScreenCount();
                break;
            case R.id.btnWatchRewardedAd:
                watchRewardedAd();
                break;
            case R.id.ivClearMultiScreenHistory:
                clearMultiScreenHistory();
                break;
            case R.id.ivSetPoint0:
                pointUtil.consumePoint(1000);
                refreshRemainPoint();
                break;
        }
    }

//    @OnClick({
//            R.id.ll_play_last_video, R.id.iv_play_last_video_play_button, R.id.ivPoster, R.id.ivSeasonPoster,
//            R.id.btnWatchVideo, R.id.btnScreenCount, R.id.btnWatchRewardedAd, R.id.ivClearMultiScreenHistory, R.id.ivSetPoint0
//    })
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ll_play_last_video:
//                playLastPlayed();
//                break;
//            case R.id.iv_play_last_video_play_button:
//                openLastPlayedVideo();
//                break;
//            case R.id.ivPoster: {
//                final String posterImagePath = Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_ORIGINAL_SIZE + videoSeasonModel.getTmdbPosterPath();
//                showThumbnailFromPath(posterImagePath, binding.llSeasonInfo.ivPoster.getDrawable());
//                break;
//            }
//            case R.id.ivSeasonPoster: {
//                final String seasonImagePath = Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_ORIGINAL_SIZE + videoSeasonModel.getTmdbSeasonPosterPath();
//                showThumbnailFromPath(seasonImagePath, binding.llSeasonInfo.ivSeasonPoster.getDrawable());
//                break;
//            }
//            case R.id.btnWatchVideo:
//                openMultiplePlayerView(enumMultiplePlayer.getNumberOfScreen());
//                break;
//            case R.id.btnScreenCount:
//                selectScreenCount();
//                break;
//            case R.id.btnWatchRewardedAd:
//                watchRewardedAd();
//                break;
//            case R.id.ivClearMultiScreenHistory:
//                clearMultiScreenHistory();
//                break;
//            case R.id.ivSetPoint0:
//                pointUtil.consumePoint(1000);
//                refreshRemainPoint();
//                break;
//        }
//    }
    private void selectScreenCount() {
        int currentRow = sharedPreferences.getMultiPlayerRow();
        int currentColumn = sharedPreferences.getMultiPlayerColumn();
        SelectRowColumnDialog dialog = new SelectRowColumnDialog(activity, currentRow, currentColumn, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                if (object instanceof Pair) {
                    @SuppressWarnings("unchecked")
                    Pair<Integer, Integer> pair = (Pair<Integer, Integer>) object;
                    sharedPreferences.setMultiPlayerRow(pair.first);
                    sharedPreferences.setMultiPlayerColumn(pair.second);
                    getMultiPlayerRowColumn();
                }
            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void updateRowColumnButton() {
        int row = sharedPreferences.getMultiPlayerRow();
        int column = sharedPreferences.getMultiPlayerColumn();
        binding.btnScreenCount.setText(row + " × " + column);
    }

    private void showThumbnailFromPath(String path, Drawable thumbnailDrawable) {
        ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(activity, activity.playerFileModel);
        zoomedPhotoDialog.loadThumbnailFromPath(path, thumbnailDrawable);
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = () -> {
        binding.pullToRefresh.setRefreshing(false);
        final YesNoDialog dialog = new YesNoDialog(activity, R.string.warning, getConfirmRefreshPullToRefresh(), null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                callRefreshMethod();
            }

            @Override
            public void onNoClick(View view, Object object) {
            }
        });
        dialog.show();
    };

    protected int getConfirmRefreshPullToRefresh() {
        return R.string.msg_confirm_refresh_pull_to_refresh;
    }

    protected int getMsgIndexingMediaFiles() {
        return R.string.msg_indexing_video_files;
    }

    private void callRefreshMethod() {
        exitEditMode();
        VideoModelQuery.updateAllNewFileToFalse(Voca.getRealm());
        VideoModelQuery.updateAllByTrashToTrue(Voca.getRealm());
        callAsyncTask(activity.getCurrentPath(), TYPE_INIT_DATA);
    }

    private void openDalPlayer(PlayerFileModel playerFileModel) {
        DLog.d(getLogTag(), "openDalPlayer");
        Loading.hide();
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivity(intent);
    }

    protected void handleVideoItemClick(PlayerFileModel playerFileModel) {
        playerFileModel.setServerModel(serverModel);
        boolean isShowDialog = true;
        int serverType = Constant.PLAYER.SERVER.TYPE.NONE;
        if (isNavTabMedia()) {
            if (playerFileModel.isSubtitle()) return;
            if (playerFileModel.isSeasonItem()) {

            } else {
                checkVideoItemLocal(playerFileModel);
                openVideoInformationScreen(playerFileModel);
            }
            return;
        } else {
            //여기서 setSubDatabase을 null을 안하면 어떤 오류가 생길까?
//            activity.setSubDatabase(null);
            serverType = serverModel.getType();
            switch (serverType) {
                case Constant.PLAYER.SERVER.TYPE.FTP:
                case Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD:
                    break;
                case Constant.PLAYER.SERVER.TYPE.WEBDAV:
                    isShowDialog = checkVideoItemLocal(playerFileModel);
                    break;
            }
        }
        if (isShowDialog) {
            showMeaningDialog(playerFileModel, serverType);
        }
    }

    private boolean checkVideoItemLocal(PlayerFileModel playerFileModel) {
        activity.createSubDatabase(playerFileModel);
        return true;
    }

    private void showMeaningDialog(PlayerFileModel playerFileModel, int serverType) {
        PlayerShowMeaningDialog dialog = new PlayerShowMeaningDialog(
                getContext(),
                serverType,
                FileUtil.isValidMediaExtension(playerFileModel.getPath()),
                isHasSubRuby(),
                (dialogInterface, i) -> {
                    switch (i) {
                        case R.id.llOpenVideoInformation:
                            startVideoInformationActivity(playerFileModel);
                            return;
                        case R.id.llDownload:
                            confirmDownloadNetwork(playerFileModel);
                            return;
                        default:
                            return;
                    }
                });
        dialog.show();
    }

//    //Don't delete this.
//    private void parserSubFromFile(PlayerFileModel playerFileModel, BaseSubtitleObject subTitle) {
//        if (subTitle == null) {
//            ToastUtil.getInstance(getActivity()).show(R.string.error_msg_parser_sub_title);
//            openDalPlayer(playerFileModel);
//            return;
//        }
//        for (SubtitleCue s : subTitle.getCues()) {
//            String text = StringUtils.replaceHTML(s.getText());
//            if (!Utils.isEmpty(text)) {
//                DLog.d(getLogTag(), s.getId() + " - " + s.getStartTime() + "( " + s.getStartTime().getTime() + " ) - " + s.getEndTime() + "( " + s.getEndTime().getTime() + " ) - " + text);
//                SubModel subModel = new SubModel(s.getStartTime().getTime(), s.getEndTime().getTime(),
//                        s.getText().replaceAll(Constant.RUBY.KEY.BREAK_REGEX, Constant.BASE_BLANK),
//                        playerFileModel.getPath(), s.getLanguage());
//                subModel.setId(SubModelQuery.createId(Voca.getRealm()));
//                SubModelQuery.add(Voca.getRealm(), subModel);
//            }
//        }
//        openDalPlayer(playerFileModel);
//    }

    private void sortFiles(int sort) {
        DLog.d(getLogTag(), "sortFiles - sort=" + sort);
        Loading.show(activity);
        removeAdsSeasonLanguageFolderInFileList();
        fileList = StorageUtil.sortFiles(fileList, sort);
        AddAdsSeasonLanguageFolderInFileList();
        dalPlayerAdapter.setData(fileList);
        binding.rvContent.scrollToPosition(0);
        Loading.hide();
    }

    private void removeAdsSeasonLanguageFolderInFileList() {
        if (!Utils.isEmpty(fileList)) {
            fileList = fileList.stream()
                    .filter(e -> !e.getVideoModel().isSeason() && !e.isAdsBanner() && !e.isSeasonList() && !e.isLanguageFolder() && !e.isSeasonItem())
                    .collect(Collectors.toList());
        }
//        .filter(e -> !e.getVideoModel().isSeason() || e.isAdsBanner() || e.isSeasonList() || e.isLanguageFolder() || e.isSeasonItem())
    }

    protected void removeSeasonInFileList() {
//        //Don't show Season video file at first.
//        fileList = fileList.stream()
//                .filter(e->!e.getVideoModel().isSeason())
//                .collect(Collectors.toList());
    }
    protected void AddAdsSeasonLanguageFolderInFileList() {
//        if (isNavTabMedia() && (sharedPreferences.getShowNormalVideoFileList())) {
//            List<VideoSeasonModel> videoSeriesModelList = VideoSeasonModelQuery.getAllUniqueSeries(Voca.getRealm());
//            addLanguageFolderAndSeasonInFileList(fileList, videoSeriesModelList);
//            addSeasonItemInVideoFileList(fileList, videoSeriesModelList);
//        }
    }

    private void openOptionPlayer(PlayerFileModel playerFileModel) {
        if (playerFileModel == null)
            return;
        DLog.d(getLogTag(), "openOptionPlayer - file=" + playerFileModel.toString());
        Loading.show(activity);
        MediaFileListUtil.checkAndCreateVideoModel(activity, isSearchTypeInitData(), playerFileModel);
        activity.runOnUiThread(() -> {
            Intent intent = new Intent(getActivity(), OptionPlayerActivity.class);
            playerFileModel.setServerModel(serverModel);
            intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
            startActivity(intent);
            Loading.hide();
        });
    }

//    private boolean checkAndCreateVideoModel(PlayerFileModel playerFileModel) {
//        VideoModel videoModel = VideoModelQuery.getByPath(Voca.getRealm(), playerFileModel.getPath());
//        if (isSearchTypeInitData()) {
//            if (videoModel != null) {
//                videoModel.setTrash(Constant.INT_BOOLEAN.FASLE);
//                // delete item whe difficult size
//                if (videoModel.getSize() != playerFileModel.getSize() || videoModel.getCreatedDate() != playerFileModel.getCreatedDate()) {
//                    VideoModelQuery.deleteByPath(Voca.getRealm(), videoModel.getPath());
//                    videoModel = null;
//                } else if (videoModel.isHide()){
//                    return false;
//                }
//            }
//            if (videoModel == null) {
//                videoModel = new VideoModel(playerFileModel.getVideoModel());
//                if (!isPlayerFetchAllVideoFirstTime()) {
//                    videoModel.setNewFile(Constant.INT_BOOLEAN.TRUE);
//                }
//                videoModel.setTongueLang(activity.motherTongueLanguage.getIdApi());
//            }
//            videoModel.setSeasonNameVideoFile(playerFileModel.getVideoModel().getSeasonNameVideoFile());
//            //If AraPlayer refresh (Get All video files from root folder) video files, then set the default subtitle file to the video file(If they have same file name)
//            if (!Utils.isEmpty(playerFileModel.getSubPath1())) {
//                videoModel.setSubPath1(playerFileModel.getSubPath1());
//            }
//            //TODO : Danim - Why need this? (original?)
////            if (Utils.isEmpty(videoModel.getSubPathOriginal())) {
////                if (!Utils.isEmpty(playerFileModel.getSubPath1())) {
////                    videoModel.setSubPath(playerFileModel.getSubPath1());
////                    videoModel.setSubPathOriginal(playerFileModel.getSubPath1());
////                }
////            }
//            VideoModelQuery.add(Voca.getRealm(), videoModel);
////            SubtitleUtil.checkAndCreateLanguages(application, playerFileModel);
//        }
//        if (videoModel == null) {
//            videoModel = new VideoModel(playerFileModel.getVideoModel());
//            videoModel.setTongueLang(activity.motherTongueLanguage.getIdApi());
//        }
//        playerFileModel.setVideoModel(videoModel);
//        return true;
//    }

    private void makeRubyTextFromServer(PlayerFileModel playerFileModel, Object resultData) {
        String content = Constant.BASE_BLANK;
        if (resultData != null) {
            content = (String) resultData;
        }
        if (Utils.isEmpty(content)) {
            openDalPlayer(playerFileModel);
            return;
        }
        SupportSubtitleFormat typeSub = FileUtil.getSubtitleExtension(playerFileModel.getSubPath());
        switch (typeSub) {
            case NONE:
                openDalPlayer(playerFileModel);
                break;
            default:
                makeRubyTextFromSubtitle(playerFileModel, content, playerFileModel.getVideoModel().getEncodingDefault());
                break;
        }
    }

    private void makeRubyTextFromSubtitle(PlayerFileModel playerFileModel,
                                          String content,
                                          String encoding) {
        //TODO : Dalnim - need to update this code later.
//        application.getAraPlayerApiImpl().makeRubyTextFromSubtitle(activity, playerFileModel, new DalApiListener<ResponseBody>() {
//            @Override
//            public void onSuccess(ResponseBody response) {
//                downloadAndUnzipFile(playerFileModel, response);
//            }
//
//            @Override
//            public void onFailure(String error) {
//                parserSubtitleError();
//            }
//        });
    }

    private void downloadAndUnzipFile(PlayerFileModel playerFileModel, ResponseBody response) {
        if (response == null) {
            parserSubtitleError();
            return;
        }
        File file = StorageUtil.writeResponseBodyToDisk(activity, response);
        String subtitleDatabasePath = StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(activity, playerFileModel.getSubPath(), playerFileModel);
        String path = StorageUtil.getFilesStoragePath(requireContext(), playerFileModel);
        StorageUtil.unzip(requireContext(), file, new File(path), subtitleDatabasePath);
        // check sub database
        activity.createSubDatabase(playerFileModel);
        MediaFileListUtil.checkAndCreateVideoModel(activity, isSearchTypeInitData(), playerFileModel);
//        checkAndCreateVideoModel(playerFileModel);
        if (isHasSubRuby()) {
            openDalPlayer(playerFileModel);
            return;
        }
        ToastUtil.getInstance(getActivity()).show(R.string.error_msg_parser_sub_database_title);
        Loading.hide();
    }

    private void parserSubtitleError() {
        ToastUtil.getInstance(getActivity()).show(R.string.error_msg_parser_sub_title);
        Loading.hide();
    }

    private boolean isHasSubRuby() {
        return activity.getSubDatabase() != null && activity.getSubDatabase().isHasSubRuby();
    }

    private boolean isHasSubtitleLocal(String path) {
        return SubModelQuery.checkPath(Voca.getRealm(), path);
    }

    @Override
    public void onInitAsyncTask(int searchType) {
        DLog.d(getLogTag(), "onInitAsyncTaskWithType");
        switch (searchType) {
            case TYPE_INIT_DATA:
                Loading.show(activity, getMsgIndexingMediaFiles());
                break;
            default:
                Loading.showDelay(activity);
        }
    }

    @Override
    public void onInitAsyncTask() {
        DLog.d(getLogTag(), "onInitAsyncTask");
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        updateCurrentSearchType(searchType);
        switch (searchType) {
            case TYPE_INIT_DATA:
                if (AppFlavorUtil.isAraMultiPlayerApp()) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        DLog.d(getLogTag(), "InterruptedException Thread.sleep", e);
                    }
                    Loading.hide();
                    showGuideScreenCount();
                }
                if (isPlayerFetchAllVideoFirstTime() && isNavTabMedia()) {
                    copyDataToSdCard();
                    //Todo : This is temp code, need to move the right place later.
//                    StorageUtil.createAllBaseFolder(activity);
                    StorageUtil.createAppRelatedFolder(activity);
                }
                //---------
                return loadData(searchType, data);
            case TYPE_LOAD_NEXT_FOLDER_DATA:
            case TYPE_LOAD_PREV_FOLDER_DATA:
                return loadData(searchType, data);
            case TYPE_RELOAD_DATA_FROM_DB:
                List<PlayerFileModel> playerFileModelList = loadData(searchType, data);
                updateCurrentVideoModel();
                return playerFileModelList;
//            case TYPE_RELOAD_DATA: //If I have this code, can't switch to hide files, can't come back from season videos by back button correctly.
//                return reloadData();
            case TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST:
                return loadMediaFolderForStudyLanguageData();
            case TYPE_LOAD_SEASON_VIDEO_LIST:
                return loadSeasonVideoList(data);
            case TYPE_LOAD_MUSIC_LIST_BY_CATEGORY:
                return loadMusicList(data);
//            case TYPE_DELETE_SUBTITLE:
            case TYPE_DELETE_VIDEO:
                PlayerFileModel fileModel = (PlayerFileModel) data;
                deletedFilePos = 0;
                deletedFilesList.clear();
                deletedFilesList.add(fileModel);
                deletePlayerFileModelItem(fileModel, searchType);
                break;
            case TYPE_SEARCH:
                fileList = MediaListUtil.generateSearchData((String) data, fileListTotal);
                if (Utils.isEmpty((String) data)) {
                    removeSeasonInFileList();
                }
                AddAdsSeasonLanguageFolderInFileList();
                break;
            case TYPE_DELETE_SELECTED_VIDEOS:
                showDialogConfirmDeleteSelectedVideos((List<PlayerFileModel>) data);
                break;
            case TYPE_CREATE_A_NEW_PLAYLIST:
                addANewPlaylist((String) data);
                break;
            case TYPE_ADD_SONGS_TO_PLAYLIST:
                addSongsToPlaylist((long) data);
                break;
            case TYPE_LOAD_SONGS_IN_PLAYLIST:
                return loadSongsInPlaylist((PlaylistModel) data);
            case TYPE_REMOVE_SONGS_FROM_PLAYLIST:
                removeSongsFromPlaylist((PlaylistModel) data);
                break;
            default:
                final PlayerFileModel playerFileModel = (PlayerFileModel) data;
                String encoding = null;
                if (playerFileModel.getVideoModel() != null) {
                    encoding = playerFileModel.getVideoModel().getEncoding();
                }
//                if (searchType == Constant.PLAYER.MEANING.WITHOUT) {
//                    return SubtitleUtil.parserSubTitle(playerFileModel, encoding);
//                }
                return "";//SubtitleUtil.parserContentSubTitle(playerFileModel, playerFileModel.getVideoModel().getSubPathIndex());
        }

        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                finishLoadData(searchType, resultData, data);
//                isResume = true;
//                root.setVisibility(View.VISIBLE);
                break;
            case TYPE_LOAD_NEXT_FOLDER_DATA:
            case TYPE_LOAD_PREV_FOLDER_DATA:
            case TYPE_RELOAD_DATA_FROM_DB:
                finishLoadData(searchType, resultData, data);
                break;
//            case TYPE_RELOAD_DATA:
//                finishReloadData(resultData);
//                break;
            case TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST:
                finishLoadStudyLanguageVideoFolderData(resultData);
                break;
            case TYPE_LOAD_SEASON_VIDEO_LIST:
                finishLoadSeasonVideoList(resultData, data);
                break;
            case TYPE_LOAD_MUSIC_LIST_BY_CATEGORY:
            case TYPE_LOAD_SONGS_IN_PLAYLIST:
                finishLoadMusicList(resultData, data);
                break;
            //Don't delete this.
//            case Constant.PLAYER.MEANING.WITHOUT:
//                parserSubFromFile((PlayerFileModel) data, (BaseSubtitleObject) resultData);
//                break;
            case TYPE_DELETE_SUBTITLE:
            case TYPE_DELETE_VIDEO:
//                updateUIDeletePlayerFileModelItem((PlayerFileModel) data);
                break;
            case TYPE_SEARCH:
                dalPlayerAdapter.setData(fileList);
                break;
            case TYPE_DELETE_SELECTED_VIDEOS:
//                updateUIDeleteSelectedVideo((List<PlayerFileModel>) data);
                break;
            case TYPE_CREATE_A_NEW_PLAYLIST:
            case TYPE_ADD_SONGS_TO_PLAYLIST:
                ToastUtil.getInstance(requireContext()).show(R.string.add_success);
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ADD_SONG_TO_PLAYLIST_SUCCESS, null));
                break;
            case TYPE_REMOVE_SONGS_FROM_PLAYLIST:
                // reload songs in playlist
                activity.callAsyncTask(MainPlayerMediaFragment.this, data, TYPE_LOAD_SONGS_IN_PLAYLIST, true);
                eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.REMOVE_SONGS_FROM_PLAYLIST_SUCCESS, null));
                break;
            default:
                makeRubyTextFromServer((PlayerFileModel) data, resultData);
                break;
        }
        Loading.hide();
    }

    private void callAsyncTaskPlay(PlayerFileModel playerFileModel, int type) {
        activity.callAsyncTask(this, playerFileModel, type);
    }

    private void callAsyncTask(String data, int type) {
        callAsyncTask(data, type, true);
    }

    private void callAsyncTask(String data, int type, boolean isLoading) {
        callAsyncTaskPlayerFile( new PlayerFileModel(data), type, isLoading);
    }

    private void callAsyncTaskPlayerFile(PlayerFileModel data, int type) {
        callAsyncTaskPlayerFile(data, type, true);
    }

    private void callAsyncTaskPlayerFile(PlayerFileModel data, int type, boolean isLoading) {
        if (!isNavTabMedia() && !activity.isNetwork()) {
            activity.getAlertDialog().showNoInternet();
            return;
        }
        activity.callAsyncTask(this, data, type, isLoading);
    }

    private void confirmDownloadNetwork(PlayerFileModel data) {
        if (!Utils.hasWifiConnected(activity)) {
            final YesNoDialog dialog = new YesNoDialog(activity,
                    R.string.warning,
                    R.string.msg_download_warning_mobile,
                    data,
                    onConfirmDownloadNetwork);
            dialog.show();
        } else {
            checkDownloadFile(data);
        }
    }

    private OnYesNoClickListener onConfirmDownloadNetwork = new OnYesNoClickListener() {
        @Override
        public void onYesClick(View view, Object object) {
            checkDownloadFile((PlayerFileModel) object);
        }

        @Override
        public void onNoClick(View view, Object object) {

        }
    };

    private void checkDownloadFile(PlayerFileModel data) {
        ToastUtil.getInstance(activity).show(R.string.msg_download_not_supported);
    }

    private void copyDataToSdCard() {
        StorageUtil.copyFileToVideoFolder(activity, R.raw.sample, Constant.PLAYER.SAMPLE_FILE_VIDEO);
//        StorageUtil.copyFileToVideoFolder(activity, R.raw.sample_srt, Constant.PLAYER.SAMPLE_FILE_SUB);
        StorageUtil.copyVoicesFolderAsset(activity, Constant.PLAYER.SAMPLE_VOICES_FOLDER);
    }

    private List<PlayerFileModel> loadData(int searchType, Object data) {
//        List<PlayerFileModel> lists = new ArrayList<>();
        final PlayerFileModel file = (PlayerFileModel) data;
        DLog.d(getLogTag(), "loadData - searchType=" + searchType + " - file=" + file.toString());
        List<PlayerFileModel> list = new ArrayList<>();
        if (isNavTabMedia()) {
             if (isAddFromFolder(file)) {
                list.addAll(addFromFolder(file, activity.isShowSubtitle));
            } else {
//Dalnim : I think we don't need this so don't show System folder.. But don't delete the code, maybe I'll use it in future.
//                list.add(getSystemFolder(list));

//                 list.add(new PlayerFileModel(PlayerFileModel.FileType.ADS_BANNER));
                if (searchType == TYPE_INIT_DATA) {
//                    list.addAll(addAllVideosFromRootFolder(activity.isShowSubtitle, getAppMediaType()));
                    list.addAll(MediaFileListUtil.addAllVideosFromRootFolder(activity, isSearchTypeInitData(), activity.isShowSubtitle, getAppMediaType()));
                } else {
                    if (activity.isShowSubtitle)
                        list.addAll(addAllSubtitlesFromRootFolder(true));
//                    list.addAll(fetchAllVideosFromDB());
                    list.addAll(MediaFileListUtil.fetchAllVideosFromDB(activity, isNavTabMedia(), false));
                }
            }
        } else {
            switch (serverModel.getType()) {
                case Constant.PLAYER.SERVER.TYPE.FTP:
                case Constant.PLAYER.SERVER.TYPE.FREE_FTP_DOWNLOAD:
                    list = loadDataFTP(file.getPath());
                    break;
                case Constant.PLAYER.SERVER.TYPE.WEBDAV:
                    list = loadDataWebDAV(file.getPath());
                    break;
            }
            if (list == null) {
                list = new ArrayList<>();
//                ToastUtil.getInstance(activity).show(R.string.cant_connect_the_server);
            }
        }
//        if (FileUtil.isVideoApp())
//            return StorageUtil.sortFiles(VideoUtil.setVideoDurationWhenZero(list), sharedPreferences.getPlayerFileSort());
//        else
            return StorageUtil.sortFiles(list, sharedPreferences.getPlayerFileSort());
    }

    private List<PlayerFileModel> loadMediaFolderForStudyLanguageData() {
        List<PlayerFileModel> list = new ArrayList<>();
//        String mediaFolderPath = StorageUtil.getVideoFolderFullPath(activity).toLowerCase();
        String mediaFolderPath = StorageUtil.getMediaAbsoluteFolder(activity).toLowerCase();
        for(PlayerFileModel f : fileListTotal) {
//            DLog.d("loadMediaFolderData",f.getVideoModel().getPath().toLowerCase() );
            if (f.getVideoModel().getPath().toLowerCase().startsWith(mediaFolderPath)) {
                list.add(f);
            }
        }
        return StorageUtil.sortFiles(list, sharedPreferences.getPlayerFileSort());
    }

    private List<PlayerFileModel> loadSeasonVideoList(Object data) {
        if (!(data instanceof VideoSeasonModel))
            return null;

        videoSeasonModel = (VideoSeasonModel) data;
        DLog.d(getLogTag(), "loadSeasonVideoList - videoSeriesModel.getSeasonNameVideoFile()=" + videoSeasonModel.getSeasonNameVideoFile());
        activity.runOnUiThread(() -> {
            updateTVSeriesPosterUI(videoSeasonModel);
            updateSeasonPosterUI(videoSeasonModel);
            updateSeasonTitle(videoSeasonModel);
        });
        return fetchSeasonVideosFromDB(videoSeasonModel.getSeasonNameVideoFile());
    }

    protected List<PlayerFileModel> loadMusicList(Object data) {
        return null;
    }

    @NotNull
    private List<PlayerFileModel> addAllSubtitlesFromRootFolder(boolean isShowSubtitle) {
        List<PlayerFileModel> allSubtitlesFromExternalStorage = StorageUtil.getAllSubtitlesOrLyrics(activity, null, false, isShowSubtitle, true);
        String sdCardStoragePath = StorageUtil.getRootFolderFromSDCard(activity);
        List<PlayerFileModel> fileListFromSdCard = new ArrayList<>();
        if (sdCardStoragePath != null) {
            fileListFromSdCard =  StorageUtil.getAllSubtitlesOrLyrics(activity, sdCardStoragePath, false, isShowSubtitle, true);
        }
        List<PlayerFileModel> allFileList = new ArrayList<>();
        allFileList.addAll(allSubtitlesFromExternalStorage);
        allFileList.addAll(fileListFromSdCard);
        return allFileList;
    }

//    private List<PlayerFileModel> addAllVideosFromRootFolder(boolean isShowSubtitle, int mediaType) {
//        List<PlayerFileModel> list = new ArrayList<>();
//        long start = System.currentTimeMillis();
//        final List<PlayerFileModel> tmp = StorageUtil.getAllFiles(getContext(), null, false, true, isShowSubtitle, true, mediaType);
////        final List<PlayerFileModel> tmp = StorageUtil.getAllFiles(getContext(), StorageUtil.getRootFolder(), false, true, isShowSubtitle, true, mediaType);
//        String sdCardStoragePath = StorageUtil.getRootFolderFromSDCard(activity);
//        List<PlayerFileModel> fileListFromSdCard = new ArrayList<>();
//        if (sdCardStoragePath != null) {
//            fileListFromSdCard = StorageUtil.getAllFiles(getContext(), sdCardStoragePath, false, true, isShowSubtitle, true);
//        }
//        List<PlayerFileModel> allFileList = new ArrayList<>();
//        allFileList.addAll(tmp);
//        allFileList.addAll(fileListFromSdCard);
//        VideoSeasonModelQuery.updateAllByTrashToTrue(Voca.getRealm());
//        for(PlayerFileModel f : allFileList) {
//            if (checkAndCreateVideoModel(f)) {
//                list.add(f);
//                VideoSeasonModelQuery.addOrUpdate(Voca.getRealm(), f);
//            } else {
//                // list.add(f);
//            }
//
////            if (f.isVideo() && mediaType == Constant.MediaType.VIDEO) {
////                if (checkAndCreateVideoModel(f)) {
////                    list.add(f);
////                    VideoSeasonModelQuery.addOrUpdate(Voca.getRealm(), f);
////                }
////            } else if (f.isMusic() && mediaType == Constant.MediaType.MUSIC) {
////                if (checkAndCreateVideoModel(f)) {
////                    list.add(f);
//////                    VideoSeasonModelQuery.addOrUpdate(Voca.getRealm(), f);
////                }
////            } else {
////                list.add(f);
////            }
//        }
//        VideoSeasonModelQuery.deleteAllByTrash(Voca.getRealm());
//        long stop = System.currentTimeMillis();
//        DLog.d("getAllFiles in Main", "Elapsed: " + (stop - start) + " ms" );
//        return list;
//    }

    private List<PlayerFileModel> addFromFolder(final PlayerFileModel file, final boolean isShowSubtitle) {
        List<PlayerFileModel> list = StorageUtil.getAllFiles(getContext(), file.getPath(), isShowSubtitle);
        for(PlayerFileModel f : list) {
            if (f.isVideo()) {
                MediaFileListUtil.checkAndCreateVideoModel(activity, isSearchTypeInitData(), f);
            }
        }
        return list;
    }

    private boolean isAddFromFolder(PlayerFileModel file) {
        return !Utils.isEmpty(file.getPath());
    }

    private PlayerFileModel getSystemFolder(List<PlayerFileModel> list) {
        return new PlayerFileModel(activity.getString(R.string.system_folder), 0, 0, StorageUtil.getRootFolder(), PlayerFileModel.DirectoryType.BLACK);
    }

    private List<PlayerFileModel> loadDataFTP(String path) {
        DLog.d(getLogTag(), "loadDataFTP");
        return ServerManager.getInstance().getListFileFromFTPServer(path);
    }

    private List<PlayerFileModel> loadDataWebDAV(String path) {
        DLog.d(getLogTag(), "loadDataWebDAV");
        return ServerManager.getInstance().getListFileFromWebDAVServer(path, getAppMediaType());
    }

    protected void finishLoadData(int type, Object resultData, Object data) {
        showSmallGroupVideoListUI();
        fileListTotal = ((List<PlayerFileModel>) resultData);
        getLastPlayedVideo();

        final PlayerFileModel file = (PlayerFileModel) data;
        if (type == TYPE_LOAD_NEXT_FOLDER_DATA) {
            activity.setCurrentNextPath(file.getName(), file.getPath());
        } else if (type == TYPE_LOAD_PREV_FOLDER_DATA) {
            activity.setCurrentPrevPath();
        }

        updateSetPlayerFetchAllVideoFirstTime();
        VideoModelQuery.deleteAllByTrash(Voca.getRealm());

        DLog.d(getLogTag(), "finishLoadData");

        fileList = MediaListUtil.generateSearchData(activity.getSearchValue(), fileListTotal);

    }

    protected void addAdsBannerInFileList(List<PlayerFileModel> fileList) {
        if (sharedPreferences.isRemoveBannerAds()) return;
        if (Utils.isEmpty(fileList))
            return;

        if (!BaseMobileAd.isShowAdsAfterTimeSinceLastClick(activity))
            return;

        for (int i = 1; i < fileList.size(); i++) {
            int adsIndex = i;
            if (adsIndex % Constant.ARAPLAYER.numberOfFileGroupToDisplayAdsBanner == 0) {
                fileList.add(adsIndex, new PlayerFileModel(requireContext()));
            }
            adsIndex++;
        }
        //Don't delete this code. May use later.
        MobileAd.addAdsBannerIfNumberOfFileGroupIsSmall(requireContext(), fileList);
    }

    protected void addLanguageFolderInFileList(List<PlayerFileModel> fileList) {
        if (!isExistLanguageFolderInFileListAlready(fileList)) {
            fileList.add(0, new PlayerFileModel(PlayerFileModel.FileType.LANGUAGE_FOLDER));
        }
    }

    private boolean isExistLanguageFolderInFileListAlready(List<PlayerFileModel> fileList) {
        if (Utils.isEmpty(fileList))
            return true;
        for(PlayerFileModel playerFileModel : fileList) {
            if (playerFileModel.isLanguageFolder())
                return true;
        }
        return false;
    }

//    private void addSeasonItemInVideoFileList(List<PlayerFileModel> fileList, List<VideoSeasonModel> videoSeriesModelList) {
//        //Don't delete this code. May use this later. Season item in the video file list is not finished yet.
////        int indexInFileList = 0;
////        for (int i = 0; i < fileList.size(); i++) {
////            PlayerFileModel playerFileModel = fileList.get(i);
////            if (playerFileModel.isAdsBanner() || playerFileModel.isLanguageFolder() || playerFileModel.isSeasonList())
////                continue;
////
////            String videoFileName = playerFileModel.getName().toLowerCase();
////            for (int j = indexInFileList; j < videoSeriesModelList.size(); j++) {
////                VideoSeasonModel VideoSeasonModel = videoSeriesModelList.get(j);
////                String videoFileNameInSeason = VideoSeasonModel.getSeasonNameVideoFile().toLowerCase();
////                int sortFileName = videoFileName.compareTo(videoFileNameInSeason);
////                if (sortFileName > 0) {
////                    fileList.add(i + 1, new PlayerFileModel(requireContext(), VideoSeasonModel));
////                    indexInFileList++;
////                }
////            }
////
////        }
////        return;
//    }

    private void finishLoadStudyLanguageVideoFolderData(Object resultData) {
        DLog.d(getLogTag(), "finishLoadSeasonVideoList");
        dalPlayerAdapter.setData((List<PlayerFileModel>) resultData);

        activity.setSmallGroupVideoListLoaded_StudyLanguageVideoFolder(true);
        activity.setSmallGroupVideoListLoaded(true);
        activity.updateHeaderSeasonView();
        showSmallGroupVideoListUI();
        getLastPlayedVideo();
    }

    private void finishLoadSeasonVideoList(Object resultData, Object data) {
        DLog.d(getLogTag(), "finishLoadSeasonVideoList");

        activity.setSmallGroupVideoListLoaded_Season(true);
        activity.setSmallGroupVideoListLoaded(true);
        activity.updateHeaderSeasonView();
        showSmallGroupVideoListUI();
        List<PlayerFileModel> playerFileList = (List<PlayerFileModel>) resultData;
        addAdsBannerInFileList(playerFileList);
        dalPlayerAdapter.setData(playerFileList);
//        binding.rvContent.scrollToPosition(0);
        getLastPlayedVideo();
    }

    protected void finishLoadMusicList(Object resultData, Object data) {

    }

    protected void getLastPlayedVideo() {
        if (!isNavTabMedia())
            return;

        lastPlayerFileModel = null;
        String lastPath = VideoUtil.getLastPlayedPath(activity);
        if ((binding.tvPlayLastVideo == null)
                || (binding.llPlayLastVideo == null)
                || Utils.isEmpty(lastPath)
                || Utils.isEmptyCollection(fileListTotal)) {
            binding.llPlayLastVideo.setVisibility(View.GONE);
            return;
        }

        boolean isFindLastPlayerFileModel = false;
        for(PlayerFileModel f : fileListTotal) {
            if (lastPath.equals(f.getPath())){
                lastPlayerFileModel = f;
                binding.tvPlayLastVideo.setText(lastPlayerFileModel.getName());
                binding.llPlayLastVideo.setVisibility(View.VISIBLE);
                isFindLastPlayerFileModel = true;
                break;
            }
        }
        binding.llPlayLastVideo.setVisibility(isFindLastPlayerFileModel ? View.VISIBLE : View.GONE);
    }

    private void openLastPlayedVideo() {
        if ((lastPlayerFileModel == null) || (Utils.isEmpty(lastPlayerFileModel.getPath())))
            return;

        if (isNavTabMedia()) {
            File f = new File(lastPlayerFileModel.getPath());
            if(!f.exists()) {
                ToastUtil.getInstance(activity).show(R.string.file_is_not_existed);
                return;
            }
        } else {
            lastPlayerFileModel.setServerModel(serverModel);
        }

        Intent intent = new Intent(activity, PlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, lastPlayerFileModel);
        startActivity(intent);
    }
    private void playLastPlayed() {
        if (lastPlayerFileModel == null) return;
        handleVideoItemClick(lastPlayerFileModel);
    }

    protected boolean isNavTabMedia() {
        return activity.currentBottomNavigationId == R.id.nav_video || activity.currentBottomNavigationId == R.id.nav_music;
    }

    protected int getAppMediaType() {
        return Constant.AppMediaType.VIDEO;
    }

    private void onUpdateShowHideItem(PlayerFileModel file, boolean needNotifyItem) {
        DLog.d(getLogTag(), "onUpdateShowHideItem - file=" + file.toString());
        file.getVideoModel().swapHide();
        final int indexPosition = fileList.indexOf(file);
        fileListTotal.remove(file);
        fileList.remove(file);
        activity.updateVideoModel(file.getVideoModel());
        getLastPlayedVideo();
        if (activity.isSmallGroupVideoListLoaded()) {
            if (activity.isSmallGroupVideoListLoaded_Season()) {
                activity.callAsyncTask(MainPlayerMediaFragment.this, videoSeasonModel, TYPE_LOAD_SEASON_VIDEO_LIST, true);
            } else if (activity.isSmallGroupVideoListLoaded_StudyLanguageVideoFolder()) {
                activity.callAsyncTask(MainPlayerMediaFragment.this, null, TYPE_LOAD_VIDEO_FOR_STUDY_LANGUAGE_FOLDER_LIST, true);
            }
        } else {
            if (needNotifyItem) {
                binding.rvContent.post(() -> dalPlayerAdapter.notifyItemRemoved(indexPosition));
            }
        }
    }

    private void deletePlayerFileModelItem(PlayerFileModel file, int type) {
        if (type == TYPE_DELETE_SUBTITLE) {
            StorageUtil.removeFile(file.getPath());
            StorageUtil.removeSubtitleSQLiteFile(activity, file);
            StorageUtil.removeLastPositionVideoScreenFile(activity, file);
            VideoModelQuery.updateBySubPath(Voca.getRealm(), file.getPath(), Constant.BASE_BLANK);
        } else if (type == TYPE_DELETE_VIDEO) {
            boolean isRemoved = StorageUtil.removeVideoFile(activity, file.getPath(), intentSenderLauncher);
            if (isRemoved) {
                deleteRelatedVideoFile(file, true);
            }
        }
    }

    private void deleteRelatedVideoFile(PlayerFileModel file, boolean notifyItem) {
        StorageUtil.removeSubtitleSQLiteFile(activity, file);
        StorageUtil.removeLastPositionVideoScreenFile(activity, file);
        VideoModelQuery.deleteByPath(Voca.getRealm(), file.getPath());
        SubModelQuery.deleteByPath(Voca.getRealm(), file.getPath());
        activity.runOnUiThread(() -> updateUIDeletePlayerFileModelItem(file, notifyItem));
    }

    private void updateUIDeletePlayerFileModelItem(PlayerFileModel file, boolean notifyItem) {
        final int indexPosition = fileList.indexOf(file);
        fileListTotal.remove(file);
        fileList.remove(file);
        getLastPlayedVideo();
        if (notifyItem) {
            binding.rvContent.post(() -> dalPlayerAdapter.notifyItemRemoved(indexPosition));
        }
        Loading.hide();
    }

    private void updateUIDeleteSelectedVideo(List<PlayerFileModel> selectedVideos) {
        fileListTotal.removeAll(selectedVideos);
        fileList.removeAll(selectedVideos);
        getLastPlayedVideo();
        dalPlayerAdapter.getSelectedVideoList().clear();
        dalPlayerAdapter.notifyDataSetChanged();
        selectVideoItem();
        Loading.hide();
    }

    private OnYesNoClickListener onConfirmDeleteItemListener = new OnYesNoClickListener() {
        @Override
        public void onYesClick(View view, Object object) {
            PlayerFileModel item = (PlayerFileModel) object;
            activity.callAsyncTask(MainPlayerMediaFragment.this, object, item.isVideo() ? TYPE_DELETE_VIDEO : TYPE_DELETE_SUBTITLE);
        }

        @Override
        public void onNoClick(View view, Object object) {
        }
    };

//    private List<PlayerFileModel> fetchAllVideosFromDB() {
//        DLog.d(getLogTag(), "fetchAllVideo");
//        List<PlayerFileModel> list = new ArrayList<>();
//        try (Realm realm = Voca.getRealm()) {
//            final List<VideoModel> videos = VideoModelQuery.refreshAndGetAll(realm, sharedPreferences.getShowNormalVideoFileList(), !isNavTabMedia(), true);
//            if (videos != null) {
//                for (VideoModel v : videos) {
//                    if (FileUtil.isVideoApp()) {
//                        if (FileUtil.isValidVideoExtension(v.getName())) {
//                            list.add(new PlayerFileModel(v));
//                        }
//                    } else {
//                        PlayerFileModel playerFileModel = new PlayerFileModel(v);
//                        if (FileUtil.isAudioFormat(v.getName())) {
//                            playerFileModel.setMusic();
//                        }
//                        list.add(playerFileModel);
//
//                    }
//                }
//            }
//        }
//        return list;
//    }

    private List<PlayerFileModel> fetchSeasonVideosFromDB(String seasonNameVideoFile) {
        DLog.d(getLogTag(), "fetchSesaonVideosFromDB");
        List<PlayerFileModel> list = new ArrayList<>();
        try (Realm realm = Voca.getRealm()) {
            final List<VideoModel> videos = VideoModelQuery.getSeasonVideoList(realm, seasonNameVideoFile);
            if (videos != null) {
                for (VideoModel v : videos) {
                    list.add(new PlayerFileModel(v));
                }
            }
        }
        return list;
    }

    private void openVideoInformationScreen(PlayerFileModel playerFileModel) {
        File f = new File(playerFileModel.getPath());
        if(!f.exists()) {
            ToastUtil.getInstance(getActivity()).show(R.string.file_is_not_existed);
            return;
        }

//        if (!playerFileModel.getVideoModel().isNewFile()) {
//            playerFileModel.getVideoModel().setNewFile(Constant.INT_BOOLEAN.TRUE);
//            activity.updateVideoModel(playerFileModel);
//            dalPlayerAdapter.notifyItemChanged(playerFileModel.getIndex());
//        }
        startVideoInformationActivity(playerFileModel);
    }

    private void startVideoInformationActivity(PlayerFileModel playerFileModel) {
        Intent intent = new Intent(activity, VideoInformationActivity.class);
        if (FileUtil.isMusicApp())
            intent = new Intent(activity, MusicInformationActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_IS_VIDEO_FROM_NETWORK, !isNavTabMedia());
        startActivity(intent);
    }

    //Not to go to sleep (or stop playing media) when I turn off the screen while I'm listening music or video
    private void askToIgnoringBatteryOptimizations() {
        //TODO : Ask this only once
        PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        if ((getContext().getPackageName() != null)
                && (powerManager.isIgnoringBatteryOptimizations(getContext().getPackageName()) == false)) {
            final YesNoDialog dialog = new YesNoDialog(activity, R.string.info, R.string.messsage_ask_to_ignore_battery_optimiszations, null, new OnYesNoClickListener() {
                @Override
                public void onYesClick(View view, Object object) {
                    String fileNameWithoutExt = (String) object;
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                    startActivity(intent);
                }

                @Override
                public void onNoClick(View view, Object object) {

                }
            });
            dialog.show();
        }
    }

    private void updateEnabledPullToRefresh() {
        if (binding.pullToRefresh != null) {
            binding.pullToRefresh.setEnabled(isNavTabMedia()
                    && Utils.isEmpty(activity.getCurrentPath())
                    && (fileList.isEmpty() || centerLayoutManager.findFirstCompletelyVisibleItemPosition() == 0)
                    && sharedPreferences.getShowNormalVideoFileList()
                    && !activity.isSmallGroupVideoListLoaded_Season()
                    && !activity.isSmallGroupVideoListLoaded_StudyLanguageVideoFolder());
        }
    }

    protected void showSmallGroupVideoListUI() {
        if (isNavTabMedia()) {
            int visibility = View.VISIBLE;
            if (activity.isSmallGroupVideoListLoaded() || !sharedPreferences.getShowNormalVideoFileList()) {
                visibility = View.GONE;
            }

//            llVideoSeasonList.setVisibility(visibility);
//            ll_video_folder_for_study_language.setVisibility(visibility);
            if (!AppFlavorUtil.isAraMultiPlayerApp()) {
                activity.setVisibleNvBottom(visibility);
//            activity.nvBottom.setVisibility(visibility);
            }
            binding.llPlayLastVideo.setVisibility(visibility);

            showSmallGroupVideoListInfoUI();
        }
    }

    private void showSmallGroupVideoListInfoUI() {
        if (activity.isSmallGroupVideoListLoaded_Season()) {
            binding.llSeasonInfo.getRoot().setVisibility(View.VISIBLE);
            binding.llvideoFolderForStudyLanguageInfo.getRoot().setVisibility(View.GONE);
        } else if (activity.isSmallGroupVideoListLoaded_StudyLanguageVideoFolder()) {
            binding.llSeasonInfo.getRoot().setVisibility(View.GONE);
            binding.llvideoFolderForStudyLanguageInfo.getRoot().setVisibility(View.VISIBLE);
        } else {
            binding.llSeasonInfo.getRoot().setVisibility(View.GONE);
            binding.llvideoFolderForStudyLanguageInfo.getRoot().setVisibility(View.GONE);
        }
    }
    private void updateTVSeriesPosterUI(VideoSeasonModel videoSeasonModel) {
        binding.llSeasonInfo.ivPoster.setVisibility(View.GONE);
        if (videoSeasonModel == null)
            return;

        if (Utils.isEmpty(videoSeasonModel.getTmdbPosterPath())) {
            return;
        }
        binding.llSeasonInfo.ivPoster.setVisibility(View.VISIBLE);

        final String posterImagePath = Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_SMALL_SIZE + videoSeasonModel.getTmdbPosterPath();
        UtilImage.getThumbnail(activity, binding.llSeasonInfo.ivPoster, 0, posterImagePath, null);
    }

    private void updateSeasonTitle(VideoSeasonModel videoSeasonModel) {
        if (Utils.isEmpty(videoSeasonModel.getTmdbPosterPath())) {
            binding.llSeasonInfo.tvSeasonTitle.setText(videoSeasonModel.getSeasonNameVideoFile());
        } else {
            binding.llSeasonInfo.tvSeasonTitle.setText(videoSeasonModel.getTmdbVideoName()
                    + "\n"
                    + getString(R.string.season)
                    + Constant.BASE_ONE_SPACE
                    + videoSeasonModel.getSeasonNumber());
        }
    }

    private void updateSeasonPosterUI(VideoSeasonModel videoSeasonModel) {
        binding.llSeasonInfo.ivSeasonPoster.setVisibility(View.GONE);
        if (TmdbUtil.isDisplayTvSeasonPoster(videoSeasonModel)) {
            binding.llSeasonInfo.ivSeasonPoster.setVisibility(View.VISIBLE);
            final String seasonImagePath = Constant.PLAYER.THE_MOVIE_DB.BASE_IMAGE_URL_SMALL_SIZE + videoSeasonModel.getTmdbSeasonPosterPath();
            UtilImage.getThumbnail(activity, binding.llSeasonInfo.ivSeasonPoster, 0, seasonImagePath, null);
        }
    }

    private void updateCurrentVideoModel() {
        if (currentVideoModel == null)
            return;
        //Do I need this code? below codes update f.setVideoModel(currentVideoModel);
//        for (PlayerFileModel f : fileListTotal) {
//            if (f.getVideoModel().getPath().equalsIgnoreCase(currentVideoModel.getPath())) {
//                f.setVideoModel(currentVideoModel);
//                break;
//            }
//        }
        int index = 0;
        for (PlayerFileModel f : fileList) {
            if (f.getVideoModel().getPath().equalsIgnoreCase(currentVideoModel.getPath())) {
                f.setVideoModel(currentVideoModel);
                index = f.getIndex();
                break;
            }
        }
        dalPlayerAdapter.notifyItemChanged(index);
        currentVideoModel = null;
    }

    private void showHideSelectedVideos() {
        List<PlayerFileModel> selectedVideoList = dalPlayerAdapter.getSelectedVideoList();
        if (selectedVideoList.isEmpty()) return;
        selectedVideoList.forEach(item -> onUpdateShowHideItem(item, false));
        selectedVideoList.clear();
        dalPlayerAdapter.notifyDataSetChanged();
        selectVideoItem();
    }

    private void showDialogConfirmDeleteSelectedVideos() {
        List<PlayerFileModel> selectedVideoList = dalPlayerAdapter.getSelectedVideoList();
        int selectedCount = selectedVideoList.size();
        if (selectedCount == 0) return;

        String message = activity.getResources().getQuantityString(
                R.plurals.msg_video_files_confirm_delete, selectedCount, selectedCount);

        YesNoDialog dialog = new YesNoDialog(
                activity,
                R.string.confirm,
                message,
                selectedVideoList,
                new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        activity.callAsyncTask(MainPlayerMediaFragment.this, object, TYPE_DELETE_SELECTED_VIDEOS);
                    }

                    @Override
                    public void onNoClick(View view, Object object) {

                    }
                });
        dialog.show();
    }


    private void showDialogConfirmDeleteSelectedVideos(List<PlayerFileModel> data) {
        deletedFilePos = 0;
        deletedFilesList.clear();
        deletedFilesList.addAll(data);
        data.forEach(item -> {
            deletePlayerFileModelItem(item, TYPE_DELETE_VIDEO);
        });
    }

    private void updateSetPlayerFetchAllVideoFirstTime() {
        sharedPreferences.setPlayerFetchAllVideoFirstTime(false);
    }

    private boolean isPlayerFetchAllVideoFirstTime() {
        return sharedPreferences.isPlayerFetchAllVideoFirstTime();
    }

    private void updateCurrentSearchType(int currentSearchType) {
        this.currentSearchType = currentSearchType;
    }

    private boolean isSearchTypeInitData() {
        return currentSearchType == TYPE_INIT_DATA;
    }

    protected void addANewPlaylist(String name) {
    }

    protected void addSongsToPlaylist(long playListId) {
    }

    protected void updatePlaylistNameInListAfterRename(PlaylistModel playlistModel) {
    }

    protected void removePlaylistFromListAfterDelete(PlaylistModel playlistModel) {
    }

    protected List<PlayerFileModel> loadSongsInPlaylist(PlaylistModel data) {
        return null;
    }

    protected void removeSongsFromPlaylist(PlaylistModel data) {

    }

    protected void loadPlaylist() {

    }

    private void removeBannerAds(boolean isRemove) {
        if (isRemove) {
            fileList.removeIf(PlayerFileModel::isAdsBanner);
        } else {
            addAdsBannerInFileList(fileList);
        }
        dalPlayerAdapter.notifyDataSetChanged();
    }

    protected void addMobileAdsView() {
        //        if (!canShowAds) return;
        // Admob Step 1 - Create an AdView and set the ad unit ID on it.
        mAdView = new AdView(activity);
        if (DarkThemeUtil.isDarkMode(activity)) {
            mAdView.setForeground(new ColorDrawable(ContextCompat.getColor(activity, R.color.ads_layer_color_in_dark_mode)));
        }
        mAdView.setAdUnitId(MobileAd.getAdsBannerId(activity));
        binding.adViewContainer.addView(mAdView);
        temporarilyHideBannerAdsAfterClick();
    }

    protected void loadBanner() {
//        if (!canShowAds) return;
        AdRequest adRequest = new AdRequest.Builder().build();
        AdSize adSize = BaseMobileAd.getAdSize(activity);
        // Admob Step 4 - Set the adaptive ad size on the ad view.
        mAdView.setAdSize(adSize);

        // Admob Step 5 - Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                DLog.d(getLogTag(), "onAdLoaded");
//                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.ON_ADS_LOADED, true));
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                DLog.d(getLogTag(), "onAdFailedToLoad");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                sharedPreferences.setLastAdsClickedTime(System.currentTimeMillis());
                if (binding.adViewContainer != null) {
                    binding.adViewContainer.removeView(mAdView);
                }
                DLog.d(getLogTag(), "onAdOpened");
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                DLog.d(getLogTag(), "onAdClicked");
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
                DLog.d(getLogTag(), "onAdClosed");
            }
        });
    }

    private void initSubDatabase() {
        String databasePath = BaseStorageUtil.getAraMultiPlayerDBPathWithFileName(getContext());
//        String databasePath = StorageUtil.getAssetFolderName(getContext()) + File.separator + "araonesoft.multiplayer.sqlite";
        boolean isFIleExist = StorageUtil.isFileExist(databasePath);
        if (multiPlayerDatabase != null) {
            multiPlayerDatabase.close();
        }
        multiPlayerDatabase = null;
        multiPlayerDatabase = MultiPlayerDatabase.getInstance(getContext(), databasePath);
    }

    private void clearMultiScreenHistory() {
        final YesNoDialog dialog = new YesNoDialog(activity, R.string.warning, R.string.msg_ask_multiplayer_screen_clear_history, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                multiPlayerDatabase.clearMultiScreenHistory();
                ToastUtil.getInstance(activity).show(R.string.msg_ok_multiplayer_screen_clear_history);
                binding.ivClearMultiScreenHistory.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNoClick(View view, Object object) {

            }
        });
        dialog.show();

    }
}