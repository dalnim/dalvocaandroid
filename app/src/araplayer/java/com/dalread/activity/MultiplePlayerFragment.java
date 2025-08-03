package com.dalread.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;

import com.dalread.R;
import com.dalread.base.BasePlayerFragment;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.sqlite.MultiPlayerDatabase;
import com.dalread.database.sqlite.model.MultiPlayerVideoAbRepeatModel;
import com.dalread.database.sqlite.model.MultiPlayerVideoModel;
import com.dalread.databinding.FragmentMultiplePlayerBinding;
import com.dalread.dialog.MultiPlayerOneVideoDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.helper.DoubleClickHelper;
import com.dalread.helper.MultiPlayerFileListHelper;
import com.dalread.helper.PlaylistHelper;
import com.dalread.helper.point.BasePlayerPointHelper;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.PlaylistModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AraRandomUtil;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DialogUtil;
import com.dalread.util.FileUtil;
import com.dalread.util.GuideUtil;
import com.dalread.util.PlayerFileModelUtil;
import com.dalread.util.ProgressTracker;
import com.dalread.util.RepeatUtil;
import com.dalread.util.SortUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.TimeUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.ViewAnimatorUtil;
import com.dalread.util.Voca;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.apache.commons.io.FilenameUtils;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MultiplePlayerFragment extends BasePlayerFragment implements View.OnTouchListener, View.OnClickListener {
    public static final int SELECT_VIDEO_REQUEST_CODE = 9270;
    private MultiplePlayerActivity activity;
    public SimpleExoPlayer exoPlayer;
    private ProgressTracker progressTracker;
    public int tapForwardBackwardValue, swipeForwardBackwardValue;
    private int playbackState;
    private boolean playWhenReady = true;
    private boolean isDurationSet;
    private int rotateVideo = 0;
    private int currentResizeMode = 0;
    private int typeSwipe = Constant.PLAYER.SWIPE.NONE;
    private int numberOfTaps = 0;
    private final Handler mHandler = new Handler();
    private float touchDownX, touchDownY;
    private float deltaX_MoveDistance, deltaY_MoveDistance;
    private long swipeTopMovePosition = 0;
    private long swipeTopNewPosition = 0;
    private long swipeForwardBackwardCurrentDuration;
    private boolean duplicatedFragment = false;
    public boolean isTrackingLeft = true;
    public int timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.NONE;
    public long abRepeatMinSub = 0, abRepeatMaxSub = 0; //In AB Repeat mode, use these to check playing time to repeat
    public final int rangeLeft = 0, rangeRight = 4, leftProgress = 1, rightProgress = 3;
    private MultiPlayerFragHelper helper;
    private MultiPlayerDatabase multiPlayerDatabase;
    private MultiPlayerVideoModel model;
    private List<MultiPlayerVideoAbRepeatModel> abRepeatModelList = new ArrayList<>(); //AB반복이 여러개 있을수 있다.
    private int indexOfAbRepeatModelList = 0;
    private boolean isNewAbRepeatStarted = false; //이건 AB반복을 새로 하나 만들때 사용한다.
    private int screenId = 1;
    private boolean isOrientationVertical;
//    private String filePath = "";
    private boolean isLoadFromTable = false;
    public boolean isShowViewPlayCenter = false;
//    public boolean isPlaying = false;
    private float playerVolume;
    boolean isVideoLoaded = false;
    private boolean isAutoRandomPlay = false;
    private boolean isPinScreen = false;
    private boolean isFirstInitExoplayer = true;
    private boolean wasPlayingBeforeSeek = false;
    private boolean isScreenMuted = true; //이건 비디오의 뮤트여부와 스크린의 뮤트여부를 둘다 나타낸다. 모델의 뮤트는 비디오의 뮤트만 나타낸다. 스크린이 뮤트이면 다름 비디오를 열때도 뮤트로 할려고 한다.
    public float speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_DEFAULT;
    private String fileName = Constant.BASE_BLANK;
    private List<String> currentVideoFilePathList = new ArrayList<>();
    private Set<String> playedVideoFilePaths = new HashSet<>();

    private ActivityResultLauncher<IntentSenderRequest> intentSenderLauncher;
    private FragmentMultiplePlayerBinding binding;

    public MultiplePlayerFragment() {
        // 기본 생성자는 가끔 크래쉬 되는 오류때문에 혹시나 해서 넣어둔다.
    }

    @Override
    protected View getContentView() {
        binding = FragmentMultiplePlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    public MultiplePlayerFragment(int screenId, boolean isOrientationVertical, boolean isLoadFromTable, MultiPlayerDatabase multiPlayerDatabase) {
        this.isOrientationVertical = isOrientationVertical;
        this.multiPlayerDatabase = multiPlayerDatabase;
        model = new MultiPlayerVideoModel();
        abRepeatModelList = new ArrayList<>();
        setPinScreen(true);
        setScreenId(screenId);
        this.isLoadFromTable = isLoadFromTable;
    }

    public List<String> getCurrentVideoFilePathList() {
        return currentVideoFilePathList;
    }

    public void setCurrentVideoFilePathList(List<String> list) {
        this.currentVideoFilePathList = new ArrayList<>(list);  // 얕은 복사
        multiPlayerDatabase.insertVideoListInScreenRecords(list, screenId);
        updateVideoFilePathIndex();
    }

    public boolean isAutoRandomPlay() {
        return isAutoRandomPlay;
    }

    public void setAutoRandomPlay(boolean autoRandomPlay) {
        isAutoRandomPlay = autoRandomPlay;
        if (!isAutoRandomPlay) {
            hide4Buttons();
        }
    }

    public boolean isPinScreen() {
        return isPinScreen;
    }

    public void setPinScreen(boolean pinScreen) {
        isPinScreen = pinScreen;
        updatePinScreenImageMain();
    }

    public int getScreenId() {
        return screenId;
    }

    public void setScreenId(int screenId) {
        this.screenId = screenId;
        model.setSCREEN_ID(screenId);
    }

    public void setModel(MultiPlayerVideoModel model) {
        this.model = model;
        loadAbRepeatModels();
    }
    public void setDuplicatedFragment(boolean value) {
        this.duplicatedFragment = value;
    }
    private void loadAbRepeatModels() {
        abRepeatModelList = multiPlayerDatabase.getRecordsInDicPlayerScreenAbRepeatTblByFilePath(model.getFILE_PATH());
        abRepeatModelList.removeIf(item -> item.getAB_A() == item.getAB_B());
        SortUtil.sortAbRepeatByAbA(abRepeatModelList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initIntentLauncher();
        initEventBus();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = (MultiplePlayerActivity)getActivity();
        return binding.getRoot();
    }

    @Override
    public void initView() {
        binding.playerView.setOnTouchListener(this);
//        viewBinding.ivCloseVideo1.setOnTouchListener((v, event) -> {
//            return doubleClickHelper.onTouch(v, event, null);
//        });
        helper = new MultiPlayerFragHelper(activity, sharedPreferences);
        updateValue_ShowViewPlayCenter(false);
//        initSubDatabase();
        initData();
        handleOrientation();
        loadVideoFromDB(isLoadFromTable);
        showGuideHowToUseMultiPlayView();
        initOnClickListener();
    }

    public void setOrientation(boolean isOrientationVertical) {
        this.isOrientationVertical = isOrientationVertical;
        handleOrientation();
    }

    private void handleOrientation() {
        if (isOrientationVertical) {
            rotateScreenToPortrait();
        } else {
            rotateScreenToLandScape();
        }
    }
    public void loadVideoFromDB(boolean isLoadFromTableLocal) {
        if (isLoadFromTableLocal) {
            setModel(multiPlayerDatabase.getMultiPlayerVideoModelById(model.getSCREEN_ID()));
            if (model != null) {
                String filePath = model.getFILE_PATH();
                if (!Utils.isEmpty(filePath)) {
                    if (FileUtil.isFileExist(filePath)) {
                        initExoPlayer();
                    } else {
                        // 파일이 존재하지 않는 경우
                        multiPlayerDatabase.handleFileDelete(filePath);
                    }
                }
            }
        }
    }

    private void showGuideHowToUseMultiPlayView() {
        if (sharedPreferences.isFirstShowGuideHowToUseMultiPlayView()) {
            sharedPreferences.setFirstShowGuideHowToUseMultiPlayView();
            String title = getString(R.string.guide_multi_player_how_to_use_multi_play_view);
            GuideUtil.showGuideView(getContext(), title, binding.root, view -> activity.showGuideHowToUse());
        }
    }

    @Override
    public void initData() {
        if (isLoadFromTable) {
            currentVideoFilePathList = multiPlayerDatabase.getAllFilePathsInScreen(screenId);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_VIDEO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                currentVideoFilePathList = MultiPlayerFileListHelper.getCurrentVideoFilesFromIntent(data);
                multiPlayerDatabase.insertVideoListInScreenRecords(currentVideoFilePathList, screenId);
                if (data.hasExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILE)) {
                    //선택된 비디오가 1개이면 이 프레그먼트에서 바로 플레이 한다.
                    PlayerFileModel playerFileModel = data.getParcelableExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILE);
                    PlayerFileModelUtil.setNewFileToFalse(playerFileModel);
                    MultiPlayerVideoModel modelLocal = new MultiPlayerVideoModel(); //새로 안만들면 기존 모델의 AB등이 남아서 현재비디오와 충돌이 날수도 있다.
                    modelLocal.setSCREEN_ID(screenId);
                    modelLocal.setFILE_PATH(playerFileModel.getPath());
                    setModel(activity.restoreHistoryModel(playerFileModel.getPath(), modelLocal));
                    updateOrInsertFilePathInTable();
                    initExoPlayer();
                    setPinScreen(true);
                    updateVideoFilePathIndex();
                    activity.playAllVideos();
                } else if (data.hasExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILES)) {
                    //선택된 비디오가 여러개이면, DB에 순차적으로 저장해둔 다암 activity에서 다시 비디오들을 로드해준다.
                    List<PlayerFileModel> selectedVideosFromIntent = data.getParcelableArrayListExtra(Constant.BUNDLE.KEY_SELECTED_VIDEO_FILES);
                    PlayerFileModelUtil.setNewFileToFalse(selectedVideosFromIntent);
                    activity.updateVideosAllFragments(selectedVideosFromIntent, currentVideoFilePathList);
                }

            }
        }
    }

    public void updateOrInsertFilePathInTable() {
        multiPlayerDatabase.updateOrInsertInTable(model);
    }

//    private void duplicateAllScreens() {
//        for (int i = 0; i < activity.numberOfScreens; i++) {
//            MultiPlayerVideoModel modelTemp = model.clone();
//            modelTemp.setSCREEN_ID(i);
//            initExoPlayer(modelTemp.getFILE_PATH());
//            multiPlayerDatabase.updateOrInsertInTable(modelTemp);
//        }
//    }

    private void updateABRepeatInDb() {
        multiPlayerDatabase.updateABRepeat(model);
    }

    private void deleteAllABRepeatInAbRepeatTblByFilePath() {
        abRepeatModelList.clear();
        multiPlayerDatabase.deleteAllABRepeatInAbRepeatTblByFilePath(model.getFILE_PATH());
    }

    private void updateVolume() {
        multiPlayerDatabase.updateVolume(model);
    }

    //비디오를 닫으면 DIC_PLAYER_SCREEN은 내용을 초기화 해준다. 안그러면 다른 비디오를 열었는데 현재 비디오의 AB반복등이 사용될수 있다.
    //DIC_PLAYER_SCREEN_BACKUP는 SCREEN_ID를 사용하지 않고 filePath를 사용하는데, filePath가 없으므로 BACKUP테이블은 상관없다.
    private void clearDBByScreenId() {
        MultiPlayerVideoModel multiPlayerVideoModel = new MultiPlayerVideoModel();
        multiPlayerVideoModel.setSCREEN_ID(model.getSCREEN_ID());
        multiPlayerDatabase.updateOrInsertInTable(multiPlayerVideoModel);
    }

    private void initOnClickListener() {
        binding.ivAddVideo.setOnClickListener(this);
        binding.ivCloseVideo.setOnClickListener(this);
        binding.ivMute.setOnClickListener(this);
        binding.btnSaveABRepeatTime.setOnClickListener(this);
        binding.ivPinScreen.setOnClickListener(this);
        binding.ivLoadRandomVideo.setOnClickListener(this);
//        binding.ivSpeaker999.setOnClickListener(this);
        binding.ivABRepeatOverlay.setOnClickListener(this);
        binding.ivAudioSpeedMinus.setOnClickListener(this);
        binding.tvAudioSpeedValue.setOnClickListener(this);
        binding.ivAudioSpeedPlus.setOnClickListener(this);
        binding.ivPlayNextVideo.setOnClickListener(this);
        binding.ivPlayPrevVideo.setOnClickListener(this);
        binding.ivABRepeat.setOnClickListener(this);
        binding.ivRepeatClose.setOnClickListener(this);
        binding.ivRotateVideo.setOnClickListener(this);
        binding.ivResizeMode.setOnClickListener(this);
        binding.ivPlayMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivABRepeatOverlay:
                ViewAnimatorUtil.animateImageView(binding.ivABRepeatOverlay, ViewAnimatorUtil.MoveDirection.DOWN);
                show4Buttons(false);
                if (isABRepeatMode()) {
                    handleRepeatCloseClick();
                    updateValue_ShowViewPlayCenter(true);
                    showCenterMessageView(getString(R.string.text_exit_ab_repeat_time_on_screen));
                } else {
                    playSavedABRepeatTimeInTable(false);
                }
                break;
            case R.id.ivAudioSpeedMinus:
                handSpeedMinusClick();
                updateAudioSpeedValueOnButton();
                break;
            case R.id.tvAudioSpeedValue:
                handleSpeedValueClick();
                updateAudioSpeedValueOnButton();
                break;
            case R.id.ivAudioSpeedPlus:
                handleSpeedPlusClick();
                updateAudioSpeedValueOnButton();
                break;
            case R.id.ivPinScreen:
                switchPinScreenImage();
                break;
            case R.id.ivLoadRandomVideo:
                playRandomVideo(true, true);
                break;
            case R.id.ivRotateVideo:
                rotateVideo();
                break;
            case R.id.ivPlayNextVideo:
                onPlayNextOrPrevousVideoControlButtonClicked(true);
                break;
            case R.id.ivPlayPrevVideo:
                onPlayNextOrPrevousVideoControlButtonClicked(false);
                break;
            case R.id.ivResizeMode:
                resizeMode();
                break;
            case R.id.ivAddVideo:
                addVideo();
                break;
            case R.id.ivCloseVideo:
                closeVideo(true);
//                clearDBByScreenId();
                break;
            case R.id.ivPlay:
                clickPlayButton();
                break;
            case R.id.ivMute:
            case R.id.ivSpeaker:
                muteOrUnmuteVolume();
                break;
            case R.id.ivRepeatClose:
                handleRepeatCloseClick();
                break;
            case R.id.btnSaveABRepeatTime:
                saveABRepeatTimeInTable();
                break;
            case R.id.ivABRepeat:
                handleABRepeatClick();
                break;
            case R.id.ivPlayMenu:
                showVideoMenu();
                break;
            default:
                break;
        }
    }
//    @Optional
//    @OnClick({R.id.ivAddVideo, R.id.ivCloseVideo, R.id.ivPlay, R.id.ivMute, R.id.btnSaveABRepeatTime,
//            R.id.ivPinScreen, R.id.ivLoadRandomVideo, R.id.ivSpeaker, R.id.ivABRepeatOverlay,
//            R.id.ivAudioSpeedMinus, R.id.tvAudioSpeedValue, R.id.ivAudioSpeedPlus,
//            R.id.ivPlayNextVideo, R.id.ivPlayPrevVideo,
//            R.id.ivABRepeat, R.id.ivRepeatClose, R.id.ivRotateVideo, R.id.ivResizeMode, R.id.ivPlayMenu})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ivABRepeatOverlay:
//                show4Buttons(false);
//                if (isABRepeatMode()) {
//                    handleRepeatCloseClick();
//                    updateValue_ShowViewPlayCenter(true);
//                    showCenterMessageView(getString(R.string.text_exit_ab_repeat_time_on_screen));
//                } else {
//                    playSavedABRepeatTimeInTable(false);
//                }
//                break;
//            case R.id.ivAudioSpeedMinus:
//                handSpeedMinusClick();
//                updateAudioSpeedValueOnButton();
//                break;
//            case R.id.tvAudioSpeedValue:
//                handleSpeedValueClick();
//                updateAudioSpeedValueOnButton();
//                break;
//            case R.id.ivAudioSpeedPlus:
//                handleSpeedPlusClick();
//                updateAudioSpeedValueOnButton();
//                break;
//            case R.id.ivPinScreen:
//                updatePinScreenImage();
//                break;
//            case R.id.ivLoadRandomVideo:
//                playNextVideoInRandomPlay();
//                break;
//            case R.id.ivRotateVideo:
//                rotateVideo();
//                break;
//            case R.id.ivPlayNextVideo:
//                onPlayNextOrPrevousVideoControlButtonClicked(true);
//                break;
//            case R.id.ivPlayPrevVideo:
//                onPlayNextOrPrevousVideoControlButtonClicked(false);
//                break;
//            case R.id.ivResizeMode:
//                resizeMode();
//                break;
//            case R.id.ivAddVideo:
//                addVideo();
//                break;
//            case R.id.ivCloseVideo:
//                closeVideo(true);
////                clearDBByScreenId();
//                break;
//            case R.id.ivPlay:
//                clickPlayButton();
//                break;
//            case R.id.ivMute:
//            case R.id.ivSpeaker:
//                muteOrUnmuteVolume();
//                break;
//            case R.id.ivRepeatClose:
//                handleRepeatCloseClick();
//                break;
//            case R.id.btnSaveABRepeatTime:
//                saveABRepeatTimeInTable();
//                break;
//            case R.id.ivABRepeat:
//                handleABRepeatClick();
//                break;
//            case R.id.ivPlayMenu:
//                showVideoMenu();
//                break;
//            default:
//                break;
//        }
//    }


    private DoubleClickHelper doubleClickHelper = new DoubleClickHelper(new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            DLog.d("", "onClick");
            ToastUtil.getInstance(activity).show(R.string.toast_press_twice_to_close_video);
        }

        @Override
        public void onDoubleClick(View view, Object data) {
            DLog.d("", "onDoubleClick");
            closeVideo(true);
        }

        @Override
        public void onLongPress(View view, Object data) {
            DLog.d("", "onDoubleClick");
        }
    });
    public void showVideoMenu() {
        MultiPlayerOneVideoDialog dialog = new MultiPlayerOneVideoDialog(activity, activity.playlistHelper.hasPlaylist(), hasSavedAbRepeatTime(), new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llPlaySaveAbRepeat:
                        playSavedABRepeatTimeInTable(false);
                        break;
                    case R.id.llDeleteSavedAbRepeat:
                        pausePlayer();
                        showDeletePlaylistDialog();
                        break;
                    case R.id.llPlayFromPlayList:
                        pausePlayer();
                        showPlayFromPlayList();
                        break;
                    case R.id.llDuplicateAllScreens:
                        pausePlayer();
                        saveLastTimeInTable();
                        activity.hideAllVideosUI();
                        activity.duplicateToSelectedScreens(model);
                        activity.setAllPinScreen(false);
//                        final YesNoDialog dialog = new YesNoDialog(activity,
//                                R.string.info,
//                                R.string.dialog_multi_player_load_same_videos, null,
//                                new OnYesNoClickListener() {
//                                    @Override
//                                    public void onYesClick(View view, Object object) {
//                                        saveLastTimeInTable();
//                                        activity.hideAllVideosUI();
//                                        activity.duplicateAllScreens(model);
//                                    }
//
//                                    @Override
//                                    public void onNoClick(View view, Object object) {
//
//                                    }
//                                });
//                        dialog.show();
                        break;
                    case R.id.llDeleteFile:
                        showDialogConfirmDeleteSelectedVideos();
                        break;
                    case R.id.llShowVideoTitle:
                        showVideoTitleInPopUp();
                        break;
                }
            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
        dialog.show();
    }
    private void showDialogConfirmDeleteSelectedVideos() {
        String message = this.getResources().getQuantityString(
                R.plurals.msg_video_files_confirm_delete, 1, 1);
        final YesNoDialog dialog = new YesNoDialog(activity, R.string.confirm, message,null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                deletePlayerFileModelItem();
            }

            @Override
            public void onNoClick(View view, Object object) {
            }
        });
        dialog.show();
    }
    private void initIntentLauncher() {
        // MANAGE_EXTERNAL_STORAGE권한이 없으면 한건 한건이 들어오기 때문에 하나씩 처리해야 한다.
        intentSenderLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                deleteRelatedVideoFile();
            } else {
                ToastUtil.getInstance(requireContext()).show(getString(R.string.file_deletion_cancelled));
            }
        });
    }
    private void deletePlayerFileModelItem() {
        boolean isRemoved = StorageUtil.removeVideoFile(activity, model.getFILE_PATH(), intentSenderLauncher);
        //파일을 바로 지울수 있는 권한이 있으면. 권한이 없으면 intentSenderLauncher로 간다.
        if (isRemoved) {
            deleteRelatedVideoFile();
        }
    }

    private void deleteRelatedVideoFile() {
        VideoModelQuery.deleteByPath(Voca.getRealm(), model.getFILE_PATH());
        activity.dbHelper.handleFileDelete(model.getFILE_PATH());
        activity.playlistHelper.deleteSelectedItemFromAllPlaylists(model.getFILE_PATH());
        handler.post(() -> closeVideo(true));
    }

    private void addVideo() {
        Intent intent = new Intent(requireContext(), MultiPlayerSelectVideoActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_SUBPATH_INDEX, fileName);
        startActivityForResult(intent, SELECT_VIDEO_REQUEST_CODE);
    }

    public void initExoPlayer() {
        rotateVideo = model.getROTATE();
        currentResizeMode = model.getRESIZE_MODE();
        binding.sbPlayer.setOnSeekBarChangeListener(onSeekBarChangeListener);
        initRepeatRangeSeekBar();
        initRepeatRangeSeekBarChangedListener();
        killPlayer(); //이걸 안하면, 비디오를 여러번 로드하다보면 로드가 안되는 버그가 생긴다.
        exoPlayer = new SimpleExoPlayer.Builder(requireContext()).build();
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        setMediaItemForPlayer(model.getFILE_PATH());
        fileName = FilenameUtils.getName(model.getFILE_PATH());
        exoPlayer.addListener(new PlayerEventListener());
        progressTracker = new ProgressTracker(exoPlayer, positionListener);
        binding.playerView.setPlayer(exoPlayer);
        binding.playerView.getVideoSurfaceView().setRotation(rotateVideo);
        binding.playerView.setResizeMode(currentResizeMode);
        showVideoView(true);
        if (isAutoRandomPlay && !duplicatedFragment) {
            forcePlay();
        } else {
            pausePlayer();
        }
        isFirstInitExoplayer = true;
        show4Buttons(false);
        playerVolume = model.getVOLUME() < 0 ? 1.f : model.getVOLUME() / 100.f;
        if (isScreenMuted) {
            muteVolume(false);
        } else {
            unmuteVolume(false);
        }
        isVideoLoaded = true;
        updatePinScreenImageMain();
        // 아래가 있으면 화면을 복사할때 자동으로 플레이를 한다. 그런데 화면이 많으면 동기화가 잘 안되어서 일단 플레이는 안하고 정지상태로 한다.
//        if (duplicatedFragment) {
//            activity.playDuplicatedFragments();
//        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    //Dalnim Add (use MediaItem instead of MediaSource)
    private void setMediaItemForPlayer(String filePath) {
        //파일명에 #이 들어가면 그냥 String인 filePath를 바로사용하면 안된고, Uri를 만들어서 사용해야 한다.
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        MediaItem mediaItem = MediaItem.fromUri(uri);
//        MediaItem mediaItem = MediaItem.fromUri(filePath);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
        setPlayWhenReady(playWhenReady);
        if (hasSavedAbRepeatTime()) {
            seekToInPlayer(model.getLAST_TIME());
//            seekToInPlayer(model.getAB_A()); //AB반복이 있으면 비디오를 열었을때 AB반복으로 보내는게 맞나?
        } else {
            if (isAutoRandomPlay) {
                seekToInPlayer(0);
            } else {
                seekToInPlayer(model.getLAST_TIME());
            }
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.SELECT_VIDEO_ACTIVITY) {
            BaseEvent.EventType type = event.getEventType();
            switch (type) {
                case DELETE_VIDEOS:
                    String filePath = (String) event.getModel();
                    if (filePath.equals(model.getFILE_PATH())) {
                        handler.post(() -> closeVideo(true));
                    }
                    break;
            }
        }
    }

    private void closeVideoMain(boolean isClearDBByScreenId) {
        exitABRepeatMode();
        if (isClearDBByScreenId) {
            clearDBByScreenId();
        }
        model.clearModelExceptId();
        killPlayer();
    }

    public void closeVideo(boolean isClearDBByScreenId) {
        showVideoView(false);
        closeVideoMain(isClearDBByScreenId);
    }

    private void showVideoView(boolean isShow) {
        if (isShow) {
            binding.ivAddVideo.setVisibility(View.GONE);
            binding.playerView.setVisibility(View.VISIBLE);
        } else {
            binding.ivAddVideo.setVisibility(View.VISIBLE);
            binding.playerView.setVisibility(View.GONE);
            binding.llPlayRepeatControl.setVisibility(View.GONE);
            binding.llPlayTopMenu.setVisibility(View.GONE);
            binding.llPlayBottomMenu.setVisibility(View.GONE);
            binding.llAudioSpeed.setVisibility(View.GONE);
            setVisiblellPlayPrevNextVideo(View.GONE);
            hide4Buttons();
        }
    }
    //실제 비디오의 소리가 0인거와 model의 mute와는 다르다. Mute이면 비디오의 소리가 조절이 안됨. Mute가 아니면 비디오의 소리가 0이라도 크게 조절 가능함.
    public boolean isMuted() {
        return model.isMuted();
        //아래는 exoPlayer의 볼륨이 0보다 작은지 체크하는거다.
//        boolean result = false;
//        if (exoPlayer != null) {
//            if (exoPlayer.getVolume() <= 0) {
//                result = true;
//            }
//        }
//        return result;
    }

    private void muteOrUnmuteVolume() {
//        if (getPlayerVolume() == 0f) {
        if (model.isMuted()) {
            unmuteVolume(true);
        } else {
            muteVolume(true);
        }
        activity.refreshAllSpeakerIcon();
    }
    void rotateVideo() {
        rotateVideo += 90;
        if (rotateVideo >= 360) {
            rotateVideo = 0;
        }
        binding.playerView.getVideoSurfaceView().setRotation(rotateVideo);
        model.setROTATE(rotateVideo);
        updateOrInsertFilePathInTable();
    }
    //이건 선택한 리사이즈모드를 적용하는것
    void resizeMode(int newResizeMode) {
        switch (newResizeMode) {
            case AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_fixed_width_mode));
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_fixed_height_mode));
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_FILL:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_fill_mode));
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_ZOOM:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_zoom_mode));
                break;
            default:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_fit_mode));
                break;
        }
        binding.playerView.setResizeMode(newResizeMode);
        model.setRESIZE_MODE(newResizeMode);
        updateOrInsertFilePathInTable();
    }
    //이건 다음 리사이즈모드를 적용하는것
    void resizeMode() {
        switch (currentResizeMode) {
            case AspectRatioFrameLayout.RESIZE_MODE_FIT:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_fixed_width_mode));
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_fixed_height_mode));
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_fill_mode));
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_FILL:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_zoom_mode));
                break;
            default:
                currentResizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
                ToastUtil.getInstance(requireContext()).show(getString(R.string.screen_resize_fit_mode));
                break;
        }
        binding.playerView.setResizeMode(currentResizeMode);
        model.setRESIZE_MODE(currentResizeMode);
        updateOrInsertFilePathInTable();
    }

    void updateVolumeInExoPlayer() {
        if (exoPlayer != null) {
            exoPlayer.setVolume(playerVolume);
            model.setVOLUME((int) (playerVolume * 100));
        }
    }

    void muteVolume(boolean isShowMessage) {
        if (exoPlayer != null) {
            exoPlayer.setVolume(0f);
            model.setVOLUMEMuted();
            isScreenMuted = true;
            updateMuteOrUnmuteImage(R.drawable.baseline_volume_off_24);
            if (isShowMessage) {
                updateValue_ShowViewPlayCenter(true);
                showCenterMessageView(getString(R.string.toast_one_video_muted));
            }
        }
    }

    void unmuteVolume(boolean isShowMessage) {
        if (exoPlayer != null) {
            exoPlayer.setVolume(playerVolume);
            model.setVOLUME((int) (playerVolume * 100));
            isScreenMuted = false;
            updateMuteOrUnmuteImage(R.drawable.baseline_volume_up_24);
            if (isShowMessage || playerVolume == 0) {
                updateValue_ShowViewPlayCenter(true);
                showCenterMessageView(getString(R.string.display_volume_percent, String.valueOf((int) (playerVolume * 100))));
            }
        }
    }

    private void updateMuteOrUnmuteImage(int volumeRes) {
        binding.ivMute.setImageDrawable(AppCompatResources.getDrawable(requireContext(), volumeRes));
//        binding.ivSpeaker999.setImageDrawable(AppCompatResources.getDrawable(requireContext(), volumeRes));
    }
    private void switchPinScreenImage() {
        setPinScreen(!isPinScreen);
        ViewAnimatorUtil.animateImageView(binding.ivPinScreen, ViewAnimatorUtil.MoveDirection.DOWN_RIGHT);
//        updateValue_ShowViewPlayCenter(true);
//        showCenterMessageView(getString(isPinScreen ? R.string.text_play_pinned_screen : R.string.text_play_unpinned_screen));
        ToastUtil.getInstance(activity).show(isPinScreen ? R.string.text_play_pinned_screen : R.string.text_play_unpinned_screen);
        show4Buttons(true);
    }

    private void updatePinScreenImageMain() {
        if ((binding == null) || (binding.ivPinScreen == null)) {
            return;
        }
        int pinDrawableResId = isPinScreen ? R.drawable.baseline_push_pin_24 : R.drawable.twotone_push_pin_24;
        binding.ivPinScreen.setImageDrawable(AppCompatResources.getDrawable(requireContext(), pinDrawableResId));
    }

    public void killPlayer() {
        pausePlayer();
        releasePlayer();
        isDurationSet = false;
        playWhenReady = true;
        playbackState = 0;
        isVideoLoaded = false;
    }

    private void pausePlayer() {
        pausePlayerMain(false);
    }

    private void pausePlayerMain(boolean fromAllPlay) {
        requireActivity().runOnUiThread(() -> {
            if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
                setPlayWhenReady(false);
                if (!fromAllPlay) {
                    activity.refreshAllPlayPauseIcon();
                }
                saveLastTimeInTable();
            }
        });
    }

    public void moveToStart() {
        seekToInPlayer(0);
    }
    public void pausePlayerFromAllVideos() {
        pausePlayerMain(true);
    }

    private final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateVideoTime(progress);
//            if(progress == seekBar.getMax()) {
//                ToastUtil.getInstance(requireContext()).show(R.string.start_over);
//            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            wasPlayingBeforeSeek = exoPlayer.isPlaying();
            pausePlayer();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekToInPlayer(seekBar.getProgress());
            if (wasPlayingBeforeSeek) {
                forcePlay();
            }
//            exoPlayer.seekTo(seekBar.getProgress());
//            consumePoint();
//            exoPlayer.setPlayWhenReady(true);
        }
    };

    private String getDisplayTimes(long value) {
        if (exoPlayer != null && value > exoPlayer.getDuration()) {
            value = exoPlayer.getDuration();
        }
        if (value < 0) {
            value = 0;
        }

        return TimeUtil.getDisplay(value);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                touchDownX = event.getX();
                touchDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float touchMoveX = event.getX();
                final float touchMoveY = event.getY();
                deltaX_MoveDistance = touchDownX - touchMoveX;
                deltaY_MoveDistance = touchDownY - touchMoveY;
                if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                    if (Math.abs(deltaX_MoveDistance) > Math.abs(deltaY_MoveDistance)) {
                        if (Math.abs(deltaX_MoveDistance) > Constant.PLAYER.SWIPE.DISTANCE_MIN) {
                            touchDownX = event.getX();
                            typeSwipe = Constant.PLAYER.SWIPE.PLAYING_AREA;
                            swipeForwardBackwardCurrentDuration = exoPlayer.getCurrentPosition();
                        }
                    } else if ((Math.abs(deltaY_MoveDistance) > Constant.PLAYER.SWIPE.DISTANCE_MIN) && (Math.abs(deltaY_MoveDistance) > Math.abs(deltaX_MoveDistance))) {
                        typeSwipe = Constant.PLAYER.SWIPE.VERTICALLY_SCREEN;
                    }
                } else if (typeSwipe == Constant.PLAYER.SWIPE.VERTICALLY_SCREEN) {
                    handSwipeVerticallyOnScreen(touchDownY, touchMoveY);
                } else {
                    if ((typeSwipe == Constant.PLAYER.SWIPE.PLAYING_AREA)) {
                        handleSwipeToMovePositionForwardBackward(touchMoveX);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                    if (numberOfTaps == 0) {
                        numberOfTaps++;
                        mHandler.postDelayed(() -> {
                            if (numberOfTaps > 1) {
                                handleDoubleClickOnPlayingScreen();
                            } else {
                                updateValue_ShowViewPlayCenter(true);
                                clickPlayButton();
                            }
                            numberOfTaps = 0;
                        }, ViewConfiguration.getDoubleTapTimeout());
                    } else {
                        numberOfTaps++;
                    }
                } else if (typeSwipe == Constant.PLAYER.SWIPE.VERTICALLY_SCREEN) {
                    savePlayerVolumeInTable();
                } else {
                    if ((typeSwipe == Constant.PLAYER.SWIPE.PLAYING_AREA)) {
                        seekToInPlayer(swipeTopNewPosition);
//                        exoPlayer.seekTo(swipeTopNewPosition);
                    }
                }
                break;
        }
        return true;
    }


    protected void handleForwardClick(int multiplyOfValue) {
        activity.runOnUiThread(() -> {
            int moveValueForwardBackward = tapForwardBackwardValue * multiplyOfValue;
            long position = exoPlayer.getCurrentPosition();
            position += moveValueForwardBackward;
            if (position > exoPlayer.getDuration()) {
                position = exoPlayer.getDuration();
            }
            displayPositionBackForward(moveValueForwardBackward, position, getTapForwardBackwardValueAsString(true));
        });
    }

    protected void handleBackwardClick(int multiplyOfValue) {
        activity.runOnUiThread(() -> {
            int moveValueForwardBackward = tapForwardBackwardValue * multiplyOfValue;
            long position = exoPlayer.getCurrentPosition();
            position -= moveValueForwardBackward;
            if (position < Constant.PLAYER.TIMER.MIN) {
                position = Constant.PLAYER.TIMER.MIN;
            }
            displayPositionBackForward(-moveValueForwardBackward, position, getTapForwardBackwardValueAsString(false));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateForwardBackwardValue();
    }
    private void updateForwardBackwardValue() {
        tapForwardBackwardValue = sharedPreferences.getPlayerTapForBackWard() * Constant.PLAYER.TIMER.SECOND;
        swipeForwardBackwardValue = sharedPreferences.getPlayerSwipeForBackWard() * Constant.PLAYER.TIMER.SECOND;
    }

    public String getTapForwardBackwardValueAsString(boolean isForward) {
        int seconds = (isForward ? 1 : -1) * (tapForwardBackwardValue / 1000);
        if (seconds == 0) {
            return "";
        }
        return getString((Math.abs( seconds) > 1 ) ? R.string.seconds_format : R.string.second_format, seconds);
    }
    private void showForwardBackwardClickOverlay(boolean isLeft, double clickedArea) {
        FrameLayout overlayLayout = new FrameLayout(requireContext());
        overlayLayout.setBackgroundColor(Color.parseColor("#99CCCCCC"));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)clickedArea, binding.playerView.getHeight());
        params.gravity = isLeft ? Gravity.LEFT : Gravity.RIGHT;

        binding.playerView.addView(overlayLayout, params);
        new Handler().postDelayed(() -> {
            binding.playerView.removeView(overlayLayout);
        }, 1000);
    }

    private void handSwipeVerticallyOnScreen(float startY, float endY) {
        if (isMuted()) {
            updateValue_ShowViewPlayCenter(true);
            showCenterMessageView(activity.getString(R.string.toast_volume_status_muted));
            return;
        }
        int playerViewHeight = binding.playerView.getHeight();
        if (playerViewHeight <= 0) return;
        activity.runOnUiThread(() -> {
            updateValue_ShowViewPlayCenter(true);
            float distance = Math.abs(deltaY_MoveDistance / playerViewHeight);
            if (distance > 0) {
                playerVolume = exoPlayer.getVolume();
                if (endY < startY) {
                    playerVolume += distance;
                } else if (endY > startY) {
                    playerVolume -= distance;
                }
                if (playerVolume < 0) {
                    playerVolume = 0;
                } else if (playerVolume > 1) {
                    playerVolume = 1;
                }
                updateVolumeInExoPlayer();
                int displayVolume = (int) (playerVolume * 100);
                DLog.e(getLogTag(), "handSwipeRight - currentPlayerVolume=" + playerVolume + ", displayVolume = " + displayVolume);
                showCenterMessageView(getString(R.string.display_volume_percent, String.valueOf(displayVolume)));
                touchDownY = endY; //need this to adjust the value smoothly (dalnim commented)
            }
        });
    }

    private class PlayerEventListener implements Player.Listener {

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            DLog.d(getLogTag(), "PlayerEventListener - onPlayWhenReadyChanged - playWhenReady=" + playWhenReady + " - reason=" + reason);
            MultiplePlayerFragment.this.playWhenReady = playWhenReady;
            updatePlayerStateAndReadyChanged();
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            DLog.d(getLogTag(), "PlayerEventListener - onPlaybackStateChanged - playbackState=" + playbackState);
            MultiplePlayerFragment.this.playbackState = playbackState;
            updatePlayerStateAndReadyChanged();
//            saveLastTimeInTable();
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            DLog.d(getLogTag(), "PlayerEventListener - onPlayerError - error=" + error.toString());
            setVisiblellPlayPrevNextVideo(View.GONE);
            String filePath = model.getFILE_PATH();
            if (!Utils.isEmpty(filePath)) {
                if (FileUtil.isFileExist(filePath)) {
                    if (!currentVideoFilePathList.isEmpty() && isAutoRandomPlay) {
                        mHandler.postDelayed(() -> {
                            playRandomVideo(false, false);
                        }, 10);
                    } else {
                        ToastUtil.getInstance(requireContext()).show(R.string.exoplayer_msg_error_open_video);
                    }
                } else {
                    // 파일이 존재하지 않는 경우
                    ToastUtil.getInstance(requireContext()).show(R.string.exoplayer_msg_error_open_video_not_exist_file);
                    VideoModelQuery.deleteByPath(Voca.getRealm(), filePath);
                    activity.dbHelper.handleFileDelete(filePath);
                    activity.playlistHelper.deleteSelectedItemFromAllPlaylists(filePath);

                }
            }
            closeVideo(true);
        }

        @Override
        public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
            DLog.d(getLogTag(), "PlayerEventListener - onPositionDiscontinuity - reason=" + reason);
            if (!isPinScreen) {
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                    // 사용자에 의해 위치가 변경됨
                    if (newPosition.positionMs < exoPlayer.getDuration()) {
                        return;
                    }
                }
//                if ((!isPinScreen) && (isAutoRandomPlay)) {
                    playRandomVideo(false, false);
//                }
            }
        }
    }

    private void playRandomVideo(boolean isShowAnimation, boolean isForceRandomPlay) {
        if (isShowAnimation) {
            ViewAnimatorUtil.animateImageView(binding.ivLoadRandomVideo, ViewAnimatorUtil.MoveDirection.DOWN_LEFT);
        }

        if (!currentVideoFilePathList.isEmpty()) {
            boolean randomPlayMode = isForceRandomPlay || sharedPreferences.isRandomPlayMode();
            String filePath = null;

            if (randomPlayMode) {
                // 랜덤 플레이 모드
                List<String> exclusionList = new ArrayList<>(playedVideoFilePaths);
                exclusionList.add(model.getFILE_PATH());

                filePath = AraRandomUtil.getRandomElementExcluding(currentVideoFilePathList, exclusionList);

                if (filePath != null) {
                    playedVideoFilePaths.add(filePath); // 재생한 비디오 경로 추가
                }

                // 모든 비디오를 재생했다면 초기화
                if (playedVideoFilePaths.size() == currentVideoFilePathList.size()) {
                    playedVideoFilePaths.clear();
                }
            } else {
                // 순차 재생 모드
                int currentIndex = currentVideoFilePathList.indexOf(model.getFILE_PATH());

                if (currentIndex != -1) {
                    int nextIndex = (currentIndex + 1) % currentVideoFilePathList.size(); // 다음 비디오 (순환)
                    filePath = currentVideoFilePathList.get(nextIndex);
                }
            }

            if (filePath != null) {
                closeVideo(true);
                setAutoRandomPlay(true);
                loadVideoInFragment(filePath);
            }
        }
    }

    //앞뒤 비디오보기하면 일정시간 뒤에 버튼들이 보이게, 안그러면 너무 빨리 자주 눌러서 무리를 줄수 있다.
    private void onPlayNextOrPrevousVideoControlButtonClicked(boolean isNext) {
        setVisiblellPlayPrevNextVideo(View.GONE);
        playNextOrPreviousVideo(isNext);
        final long BUTTON_COOLDOWN_TIME = 500;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisiblellPlayPrevNextVideo(View.VISIBLE);
            }
        }, BUTTON_COOLDOWN_TIME);
    }

    public void playNextOrPreviousVideo(boolean isPlayNext) {
        String filePath = model.getFILE_PATH();
        updatePinScreenImageMain();
        closeVideoMain(true);

        if (!currentVideoFilePathList.isEmpty()) {
            String filePathNext = getNextOrPreviousModel(isPlayNext, filePath);
            if (!Utils.isEmpty(filePathNext)) {
                loadVideoInFragment(filePathNext);
                playCommon(true, false);
            }
        }
    }

    private String  getNextOrPreviousModel(boolean isPlayNext, String filePath) {
        if (currentVideoFilePathList.isEmpty()) {
            return null;
        }

        int currentIndex = getCurrentVideoFilePathIndex(filePath);

        if (currentIndex == -1) {
            return null;  // 현재 파일 경로와 일치하는 모델이 없는 경우
        }

        int nextIndex;
        if (isPlayNext) {
            // isPlayNext가 true인 경우 다음 모델을 반환
            nextIndex = (currentIndex + 1) % currentVideoFilePathList.size();  // 다음 인덱스를 계산, 마지막일 경우 첫 번째로 순환
        } else {
            // isPlayNext가 false인 경우 이전 모델을 반환
            nextIndex = (currentIndex - 1 + currentVideoFilePathList.size()) % currentVideoFilePathList.size();  // 이전 인덱스를 계산, 처음일 경우 마지막으로 순환
        }
        setTextOnTvVideoIndexInList(nextIndex);
        return currentVideoFilePathList.get(nextIndex);
    }

    private void updateVideoFilePathIndex() {
        binding.tvVideoIndexInList.setText("");
        if (Utils.isEmpty(model.getFILE_PATH())) {
            return;
        }
        int currentIndex = getCurrentVideoFilePathIndex(model.getFILE_PATH());

        if (currentIndex >= 0) {
            setTextOnTvVideoIndexInList(currentIndex);
        }
    }

    private void setTextOnTvVideoIndexInList(int index) {
        binding.tvVideoIndexInList.setText(index + 1 + " / " + currentVideoFilePathList.size());
    }
    private int getCurrentVideoFilePathIndex(String filePath) {
        int currentIndex = -1;
        for (int i = 0; i < currentVideoFilePathList.size(); i++) {
            String filePathInList = currentVideoFilePathList.get(i);
            if (filePath.equals(filePathInList)) {
                currentIndex = i;
                break;
            }
        }
        return currentIndex;
    }

    public void loadVideoInFragment(String filePath) {
        MultiPlayerVideoModel modelLocal = new MultiPlayerVideoModel();
        modelLocal.setSCREEN_ID(screenId);
        modelLocal.setFILE_PATH(filePath);
        setModel(activity.restoreHistoryModel(filePath, modelLocal));
        updateOrInsertFilePathInTable();
        loadVideoFromDB(true);
    }

    private final ProgressTracker.PositionListener positionListener = position -> {
        if (exoPlayer == null) return;
        onPlayerVideoPositionListener(position);
    };

    private void onPlayerVideoPositionListener(long position) {
        updateSeekBarPlay(position);
        onPlayerVideoABRepeat(position);
    }

    private void updateSeekBarPlay(final long position) {
        binding.sbPlayer.setProgress((int) position);
        updateVideoTime(position);
    }

    private void onPlayerVideoABRepeat(long position) {
        if (isABRepeatMode()) {
            if (isPositionExceedABRepeat(position)) {
                seekToInPlayer(abRepeatMinSub);
            }
        }
    }
    protected boolean isPositionExceedABRepeat(long position) {
        if (isNewAbRepeatStarted) {
            return position >= abRepeatMaxSub;
        }
        if (!abRepeatModelList.isEmpty()) {
            boolean hasNextIndex = false;
            if (position >= abRepeatMaxSub) {
                for (int i = 0; i < abRepeatModelList.size(); i++) {
                    long abA = abRepeatModelList.get(i).getAB_A();
                    long abB = abRepeatModelList.get(i).getAB_B();
                    indexOfAbRepeatModelList = i;
                    if (position >= abA && position < abB) {
                        indexOfAbRepeatModelList = i + 1;
                        if (indexOfAbRepeatModelList >= abRepeatModelList.size()) {
                            indexOfAbRepeatModelList = 0;
                        }
                        hasNextIndex = true;
                        break;
                    } else if (position < abA) {
                        hasNextIndex = true;
                        break;
                    }
                }
                if (!hasNextIndex) {
                    indexOfAbRepeatModelList = 0;
                }

                if (indexOfAbRepeatModelList < 0) {
                    indexOfAbRepeatModelList = 0;
                } else if (indexOfAbRepeatModelList >= abRepeatModelList.size()) {
                    indexOfAbRepeatModelList = abRepeatModelList.size() - 1;
                }

                abRepeatMinSub = abRepeatModelList.get(indexOfAbRepeatModelList).getAB_A();
                abRepeatMaxSub = abRepeatModelList.get(indexOfAbRepeatModelList).getAB_B();
                return true;
            } else if (position < abRepeatMinSub) {
                for (int i = 0; i < abRepeatModelList.size(); i++) {
                    long abA = abRepeatModelList.get(i).getAB_A();
                    long abB = abRepeatModelList.get(i).getAB_B();
                    indexOfAbRepeatModelList = i;
                    if (position >= abA && position < abB) {
                        indexOfAbRepeatModelList = i - 1;
                        if (indexOfAbRepeatModelList < 0) {
                            indexOfAbRepeatModelList =  abRepeatModelList.size() - 1;
                        }
                        hasNextIndex = true;
                        break;
                    } else if (position < abA) {
                        indexOfAbRepeatModelList = i - 1;
                        if (i == 0) {
                            indexOfAbRepeatModelList = abRepeatModelList.size() - 1;
                        }
                        hasNextIndex = true;
                        break;
                    }
                }
                if (!hasNextIndex) {
                    indexOfAbRepeatModelList = abRepeatModelList.size() - 1;
                }
                if (indexOfAbRepeatModelList < 0) {
                    indexOfAbRepeatModelList = 0;
                }

                abRepeatMinSub = abRepeatModelList.get(indexOfAbRepeatModelList).getAB_A();
                abRepeatMaxSub = abRepeatModelList.get(indexOfAbRepeatModelList).getAB_B();
                return true;
            }

        }
        return false;
    }
    public void seekToInPlayer(long position) {
        if (exoPlayer != null) {
            exoPlayer.seekTo(position);
        }
    }
    public boolean isABRepeatMode() {
        return timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.B;
    }

    private long lastTimeUpdate = 0;
    private static final long TIME_UPDATE_INTERVAL = 500; // 0.5초마다 업데이트

    private void updateVideoTime(long position) {
        long currentTime = System.currentTimeMillis();
        
        // 0.5초마다만 업데이트
        if (currentTime - lastTimeUpdate < TIME_UPDATE_INTERVAL) {
            return;
        }
        
        // TextView가 보이지 않으면 업데이트하지 않음
        if (binding.tvVideoStartTime.getVisibility() != View.VISIBLE || 
            binding.tvVideoEndTime.getVisibility() != View.VISIBLE) {
            return;
        }
        
        binding.tvVideoStartTime.setText(getDisplayTimes(position));
        binding.tvVideoEndTime.setText(getDisplayTimes((exoPlayer.getDuration() - position)));
        lastTimeUpdate = currentTime;
    }

    private void updatePlayerStateAndReadyChanged() {
        requireActivity().runOnUiThread(() -> {
            if (playbackState == ExoPlayer.STATE_READY && !isDurationSet) {
//                show4Buttons(false);
                binding.sbPlayer.setMax((int) exoPlayer.getDuration());
                updateSeekBarPlay(0);
                isDurationSet = true;
            }
            onPlayerVideoPlayerStateChanged();
        });
    }

    private void onPlayerVideoPlayerStateChanged() {
//        ImageView ivPlay = viewBinding.ivPlay;
        if (playWhenReady) {
//            ivPlay.setImageResource(R.drawable.ic_new_pause);
            progressTracker.startHandle();
        } else {
//            ivPlay.setImageResource(R.drawable.ic_new_play);
            progressTracker.stopHandler();
        }
        handleViewPlayCenterClick(playWhenReady);
    }
    public void handleViewPlayCenterClick(boolean isPlay) {
        String message = exoPlayer.isPlaying() && isMuted() ? getString(R.string.toast_one_video_muted) : "";
        showCenterMessageView(message, isPlay ? R.drawable.ic_exo_player_icon_play : R.drawable.exo_controls_pause);
    }

    public void showCenterMessageView(String value) {
        showCenterMessageView(value, 0);
    }

    public void showCenterMessageView(int resId) {
        showCenterMessageView(Constant.BASE_BLANK, resId);
    }

    public void showCenterMessageView(String value, int resId) {
        if (!isShowViewPlayCenter) return;

        binding.tvCenterText.setText(value);
        setVisibleCenterText(Utils.isEmpty(value) ? View.GONE : View.VISIBLE);

        binding.ivCenterPlay.setImageResource(resId);
        binding.ivCenterPlay.setVisibility(resId <= 0 ? View.GONE : View.VISIBLE);

        binding.llCenter.setVisibility(View.VISIBLE);

        updateValue_ShowViewPlayCenter(false);
        mHandler.post(onViewCenterRunnable);
    }
    private void setVisibleCenterText(int visibility) {
        binding.tvCenterText.setVisibility(visibility);
    }
    private void setVisiblellPlayPrevNextVideo(int visibility) {
        binding.llPlayPrevNextVideo.setVisibility(visibility);
        updateVideoFilePathIndex();
        if (currentVideoFilePathList.isEmpty() || currentVideoFilePathList.size() == 1) {
            binding.llPlayPrevNextVideo.setVisibility(View.GONE);
        }
    }
    private void updateValue_ShowViewPlayCenter(boolean value) {
        DLog.d("dalnim", "updateValue_ShowViewPlayCenter value : " + value);
        isShowViewPlayCenter = value;
    }
    Runnable onViewCenterRunnable = () -> {
        mHandler.postDelayed(() -> {
            binding.llCenter.setVisibility(View.INVISIBLE);
        }, Constant.PLAYER.TIMER.SECOND);
    };

    private void clickPlayButton() {
        playCommon(!playWhenReady, false);
        show4Buttons(false);
    }

    public void forcePlayFromAllVideos() {
        playCommon(true, true);
    }

    private void forcePlay() {
        playCommon(true, false);
    }

    private void playCommon(boolean shouldPlay, boolean fromAllPlay) {
        if (exoPlayer != null) {
            consumePoint();
            setPlayWhenReady(shouldPlay);
            if (!fromAllPlay) {
                activity.refreshAllPlayPauseIcon();
            }
        }
    }

    private void consumePoint() {
        activity.pointHelper.consumePoint(BasePlayerPointHelper.consumePoint1);
    }

    private void handleDoubleClickOnPlayingScreen() {
        final float divide = 4.5f;
        float clickedArea = binding.playerView.getWidth() / divide;
        if (isABRepeatMode()) {
            showOrHideRepeatControl();
        } else {
            if (touchDownX < clickedArea) {
                handleBackwardClick(1);
                showForwardBackwardClickOverlay(true, clickedArea);
            } else if (touchDownX < clickedArea * (divide - 1)) {
                showOrHideMenuControl();
            } else {
                handleForwardClick(1);
                showForwardBackwardClickOverlay(false, clickedArea);
            }
        }
    }

    private void handleSwipeToMovePositionForwardBackward(float touchMoveX) {
        int playerViewWidth = binding.playerView.getWidth();
        if (playerViewWidth <= 0) return;
        final int width = playerViewWidth - Constant.PLAYER.SWIPE.DISTANCE_MIN;
        float percentMoved = Math.abs(deltaX_MoveDistance) / width;
        float tempDistance = (percentMoved + (Constant.PLAYER.SWIPE.DISTANCE_MIN / width)) * swipeForwardBackwardValue;

        requireActivity().runOnUiThread(() -> {
            if (touchMoveX < touchDownX) {
                updateValue_swipeTopMovePosition((long) tempDistance);
            } else if (touchMoveX > touchDownX) {
                updateValue_swipeTopMovePosition((long) -tempDistance);
            }
            if (swipeTopMovePosition > swipeForwardBackwardValue) {
                updateValue_swipeTopMovePosition(swipeForwardBackwardValue);
            } else if (swipeTopMovePosition < -swipeForwardBackwardValue) {
                updateValue_swipeTopMovePosition(-swipeForwardBackwardValue);
            }
            updateValue_swipeTopNewPosition(swipeTopMovePosition + swipeForwardBackwardCurrentDuration);
            if (swipeTopNewPosition < 0) {
                updateValue_swipeTopNewPosition(0);
            } else if (swipeTopNewPosition > binding.sbPlayer.getMax()) {
                updateValue_swipeTopNewPosition(binding.sbPlayer.getMax());
            }
            seekToInPlayer(swipeTopNewPosition);
            displayPositionBackForward(swipeTopMovePosition, swipeTopNewPosition, "");
        });
    }
    private void displayPositionBackForward(long oldPosition, long newPosition, String value) {
        updateValue_ShowViewPlayCenter(true);
        showCenterMessageView(value, oldPosition < 0 ? R.drawable.ic_exo_player_icon_rewind : R.drawable.ic_exo_player_icon_fastforward);
        updateSeekBarPlay((int) newPosition);
        seekToInPlayer(newPosition);
    }

    private void updateValue_swipeTopMovePosition(long value) {
        swipeTopMovePosition = value;
    }

    private void updateValue_swipeTopNewPosition(long value) {
        swipeTopNewPosition = value;
    }

    @Override
    public void onPause() {
        super.onPause();
        DLog.i(getLogTag(), "onPause");
        saveLastTimeInTable();
        pausePlayer();
    }

    @Override
    public void onDestroyView() {
        killPlayer();
        saveLastTimeInTable();
        super.onDestroyView();
    }

    private void saveLastTimeInTable() {
        if (exoPlayer != null) {
            model.setLAST_TIME(exoPlayer.getCurrentPosition());
            updateOrInsertFilePathInTable();
        }
    }
    public void handleRepeatCloseClick() {
        exitABRepeatMode();
        hideAllControl();
    }
    public MultiPlayerVideoModel getMultiPlayerVideoModel() {
        return model;
    }
    private void showOrHideRepeatControl() {
        if (binding.llPlayRepeatControl.getVisibility() == View.VISIBLE) {
            hideAllControl();
        } else {
            showRepeatControl();
        }
    }

    public void hideAllControl() {
        binding.llPlayRepeatControl.setVisibility(View.GONE);
        binding.llPlayBottomMenu.setVisibility(View.GONE);
        binding.llPlayTopMenu.setVisibility(View.GONE);
        binding.llAudioSpeed.setVisibility(View.GONE);
        setVisiblellPlayPrevNextVideo(View.GONE);

    }

    public void hideAllControlAndShow4Buttons() {
        hideAllControl();
        show4Buttons(false);
    }

    private boolean isBottomMenuShow() {
        return binding.llPlayBottomMenu.getVisibility() == View.VISIBLE;
    }
    private void showRepeatControl() {
        binding.llPlayRepeatControl.setVisibility(View.VISIBLE);
        binding.llPlayBottomMenu.setVisibility(View.VISIBLE);
    }
    private void showOrHideMenuControl() {
        if (binding.llPlayBottomMenu.getVisibility() == View.VISIBLE) {
            hideAllControlAndShow4Buttons();
        } else {
            hide4Buttons();
            showMenuControl();
        }
    }

    void showMenuControl() {
        binding.llPlayTopMenu.setVisibility(View.VISIBLE);
        binding.llPlayBottomMenu.setVisibility(View.VISIBLE);
        setVisiblellPlayPrevNextVideo(View.VISIBLE);
        if (sharedPreferences.isShowAdvancedMode()) {
            binding.llAudioSpeed.setVisibility(View.VISIBLE);
        }
//        if (isAutoRandomPlay) {
//            hide4Buttons();
//        }
    }

    private boolean canShow4Buttons() {
        boolean result = false;
        if ((exoPlayer != null) && (!isBottomMenuShow())) {
            if (!sharedPreferences.getHide4ButtonsOnPlayingScreen()) {
                result = true;
            }
//            if (isAutoRandomPlay) {
//                result = true;
//            }
            if (!playWhenReady) {
                result = true;
            }
        }
        return result;
    }
    private boolean showAnimationWhenShow4Buttons() {
        boolean result = false;
        if ((exoPlayer != null) && (!exoPlayer.isPlaying()) && (!playWhenReady)) {
            result = true;
        }
        if (isFirstInitExoplayer) {
            isFirstInitExoplayer = false;
            result = false;
        }
        return result;
    }
    public void show4Buttons(boolean isFromPinScreen) {
        if (canShow4Buttons()) {
            showOrHide4Buttons(View.VISIBLE);
            if (showAnimationWhenShow4Buttons()) {
                List<View> viewsToCancel;
                if (isFromPinScreen) {
                    viewsToCancel = Collections.singletonList(binding.ivPinScreen);
                } else {
                    viewsToCancel = new ArrayList<>();
                    if (binding.ivPinScreen.getVisibility() != View.GONE) {
                        viewsToCancel.add(binding.ivPinScreen);
                    }

                    if (hasSavedAbRepeatTime() && binding.ivABRepeatOverlay.getVisibility() != View.GONE) {
                        viewsToCancel.add(binding.ivABRepeatOverlay);
                    }

                    if (binding.ivLoadRandomVideo.getVisibility() != View.GONE) {
                        viewsToCancel.add(binding.ivLoadRandomVideo);
                    }
                }
                ViewAnimatorUtil.hideViewsOnFullScreenWithAnimation(activity, viewsToCancel);
            }
        }
    }

    void hide4Buttons() {
        showOrHide4Buttons(View.GONE);
    }
    public void show4ButtonsWithoutAnimation() {
        showOrHide4Buttons(View.VISIBLE);
    }
    void showOrHide4Buttons(int visibility) {
        //알파값을 따로 안주면 여기서 이미 불투명한걸로 되어서 showAnimationWhenShow4Buttons가 false이면 알파값이 적용이 안된다.
        //showAnimationWhenShow4Buttons을 체크안하면 재생을 시작할때도 애니메이션이 보여서 체크한다.
        float alpha = ViewAnimatorUtil.getAlpha(activity);
        int ivLoadRandomVideoVisibility = currentVideoFilePathList.size() < 2 ? View.GONE : visibility;

        binding.ivPinScreen.setAlpha(alpha);
        binding.ivPinScreen.setVisibility(ivLoadRandomVideoVisibility);
//        viewBinding.ivCloseVideo1.setAlpha(alpha);
//        viewBinding.ivCloseVideo1.setVisibility(visibility);
        //일단 오버레이버튼에서 비디오 닫기는 무조건 안보이게 한다. (더블클릭이 잘안됨)
//        viewBinding.ivCloseVideo1.setVisibility(View.GONE);

        binding.ivLoadRandomVideo.setAlpha(alpha);
        binding.ivLoadRandomVideo.setVisibility(ivLoadRandomVideoVisibility);

//        binding.ivSpeaker.setAlpha(alpha);
//        binding.ivSpeaker999.setVisibility(View.GONE);
        binding.ivABRepeatOverlay.setAlpha(alpha);
        binding.ivABRepeatOverlay.setVisibility(hasSavedAbRepeatTime() ? visibility : View.GONE);


    }

    public void resetTimeBaseRepeatStatus() {
        if (isABRepeatMode()) {
//            isRepeat = false;
            timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.NONE;
        }
    }
    protected void handleABRepeatClick() {
        isNewAbRepeatStarted = false;
        switch (timeBaseRepeatStatus) {
            case Constant.PLAYER.REPEAT.TIMEBASE.NONE:
                enterABRepeatModeAB_A();
                break;
            case Constant.PLAYER.REPEAT.TIMEBASE.A:
                isNewAbRepeatStarted = true;
                enterABRepeatModeAB_B(false, true);
                setVisibleBtnSaveABRepeatTime(View.VISIBLE);
                hideAllControl();
                helper.showABRepeatGuide();
                break;
            case Constant.PLAYER.REPEAT.TIMEBASE.B:
                handleRepeatCloseClick();
                break;
            default:
        }
    }

    private void saveABRepeatTimeInTable() {
        ToastUtil.getInstance(activity).show(R.string.toast_ab_repeat_time_saved);
        setVisibleBtnSaveABRepeatTime(View.GONE);
        model.setAB_A(abRepeatMinSub);
        model.setAB_B(abRepeatMaxSub);
        updateABRepeatInDb();
        loadAbRepeatModels();
    }

    private void showVideoTitleInPopUp() {
//        DialogUtil.showCopyTextDialog(activity, FilenameUtils.getName(model.getFILE_PATH()));
        DialogUtil.showCopyTextDialog(activity, model.getFILE_PATH());
    }

    public void showPlayFromPlayList() {
        activity.playlistHelper.selectPlaylists(false, false,R.string.playlist_dialog_button_select, R.string.playlist_dialog_button_cancel, new PlaylistHelper.PlaylistSelectionCallback() {
            @Override
            public void onPlaylistsSelected(List<PlaylistModel> selectedPlaylists) {
                if (!selectedPlaylists.isEmpty()) {
                    selectVideosFromPlaylists(selectedPlaylists);
                }
            }
        });
    }

    private void selectVideosFromPlaylists(List<PlaylistModel> selectedPlaylists) {
        List<String> allList = selectedPlaylists.stream()
                .flatMap(model -> model.getFilePathsAsList().stream())
                .distinct()
                .collect(Collectors.toList());
        setCurrentVideoFilePathList(allList);

        playRandomVideo(false, false);
    }

    public void showDeletePlaylistDialog() {
        String[] items = new String[abRepeatModelList.size()];
        boolean[] checkedItems = new boolean[abRepeatModelList.size()];
        List<Integer> preCheckedIndexes = new ArrayList<>();
        for (int i = 0; i < abRepeatModelList.size(); i++) {
            items[i] = String.valueOf((i + 1));
            checkedItems[i] = preCheckedIndexes.contains(i);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.MultiMultiChoiceDialog);
        builder.setTitle(activity.getString(R.string.dialog_title_choose_to_delete_ab_repeat))
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        checkedItems[indexSelected] = isChecked;
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<MultiPlayerVideoAbRepeatModel> selectedModels = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                selectedModels.add(abRepeatModelList.get(i));
                            }
                        }
                        int totalSelected = abRepeatModelList.size();
                        int deleteCount = 0;

                        for (MultiPlayerVideoAbRepeatModel model : selectedModels) {
                            handleRepeatCloseClick();
                            show4Buttons(false);
                            boolean isDeleted = multiPlayerDatabase.deleteABRepeatInAbRepeatTblBy(model);
                            if (isDeleted) {
                                deleteCount++;
                            }
                        }

                        String toastMessage;
                        if (deleteCount == 0) {
                            toastMessage = activity.getString(R.string.toast_no_ab_repeat_removed);
                        } else if (deleteCount == totalSelected) {
                            toastMessage = activity.getString(R.string.toast_all_ab_repeat_removed, totalSelected);
                        } else {
                            toastMessage = activity.getString(R.string.toast_some_ab_repeat_removed, totalSelected, deleteCount);
                        }
                        ToastUtil.getInstance(activity).show(toastMessage);
                        loadAbRepeatModels();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    //이건 기존에 AB 반복이 하나만 있을때의 코드 (일단 지우지는 말고, 나중에 이상없으면 지우자)
//    private void deleteSaveABRepeatTimeInTable() {
//        if (hasSavedAbRepeatTime()) {
//            handleRepeatCloseClick();
//            updateValue_ShowViewPlayCenter(true);
//            showCenterMessageView(getString(R.string.text_delete_ab_repeat_time_on_screen));
//            //값을 초기화해주기전에 deleteABRepeatInAbRepeatTbl를 먼저 불러야 한다.
//            deleteAllABRepeatInAbRepeatTblByFilePath();
//            model.setAB_A(0);
//            model.setAB_B(0);
//            updateABRepeatInDb();
//            show4Buttons(false); //이걸 안하면 AB오버레이버튼이 계속 보인다.
//        } else {
//            ToastUtil.getInstance(activity).show(R.string.toast_play_no_saved_ab_repeat_time);
//        }
//    }

    public boolean hasSavedAbRepeatTime() {
        if (exoPlayer == null) {
            return false;
        }
        return !abRepeatModelList.isEmpty();
        //이제 AB반복은 abRepeatModelList에 여러개 들어가기 때운에 아래는 안쓴다.
//        long duration = exoPlayer.getDuration();
//        long abA = model.getAB_A();
//        long abB = model.getAB_B();
//
//        // AB 반복 구간이 비디오 범위 내에 있고, A와 B가 서로 다르면
//        if (abA < duration && abB <= duration && abA != abB) {
//            // 둘 중 하나라도 0보다 크면 true 반환
//            if (abA > 0 || abB > 0) {
//                return true;
//            }
//        }
//
//        return false;
    }

    public void playSavedABRepeatTimeInTable(boolean isFromParent) {
        if (hasSavedAbRepeatTime()) {
            setVisibleBtnSaveABRepeatTime(View.GONE);
            hideAllControl();
            indexOfAbRepeatModelList = 0;
            if (indexOfAbRepeatModelList < abRepeatModelList.size()) {
                long abA = abRepeatModelList.get(indexOfAbRepeatModelList).getAB_A();
                long abB = abRepeatModelList.get(indexOfAbRepeatModelList).getAB_B();

                updateValue_abRepeatMinSub(abA + RepeatUtil.getTime(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BEFORE_AB_REPEAT));
                updateValue_abRepeatMaxSub(abB + RepeatUtil.getTime(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_AFTER_AB_REPEAT));
            }

            enterABRepeatModeAB_B(false, false);
            seekToInPlayer(abRepeatMinSub);
            forcePlay();
            updateValue_ShowViewPlayCenter(true);
            showCenterMessageView(getString(R.string.text_play_ab_repeat_time_on_screen));
        } else {
            if (isFromParent) {
                forcePlay();
            }
        }
        if (!isFromParent) {
            activity.refreshAllPlayPauseIcon();
        }
    }

    private void savePlayerVolumeInTable() {
        if (Utils.isDebug()) {
            model.setVOLUME((int) (playerVolume * 100));
            updateVolume();
        }
    }

    private void setVisibleBtnSaveABRepeatTime(int visibility) {
        binding.btnSaveABRepeatTime.setVisibility(visibility);
    }
    protected void enterABRepeatModeAB_A() {
        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.A;
//        resetRepeat();
        updateValue_abRepeatMinSub(exoPlayer.getCurrentPosition() + RepeatUtil.getTime(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BEFORE_AB_REPEAT));
        forcePlay();
        setABRepeatImage(R.drawable.ic_repeat_ab_a);
    }

    protected void enterABRepeatModeAB_B(boolean isEndOfDuration, boolean isNewRepeat) {
        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.B;
//        abRepeatingCount = 0;
//        setRepeat();
        if (isNewRepeat) {
            //저장된 AB를 플레이할때 enterABRepeatModeAB_B로 들어와서 updateValue_abRepeatMaxSub를 바꾸어주면 안된다. 이건 새로 AB를 시작할때만 해야한다.
            updateValue_abRepeatMaxSub(exoPlayer.getCurrentPosition() + RepeatUtil.getTime(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_AFTER_AB_REPEAT));
            if (isEndOfDuration) {
                updateValue_abRepeatMaxSub(exoPlayer.getDuration());
            }
        }
//        updateRangeSeek();
//        handlePlayClick(true, false);
        setABRepeatImage(R.drawable.ic_repeat_ab_b);
    }

    protected void exitABRepeatMode() {
        isNewAbRepeatStarted = false;
        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.NONE;
        abRepeatMinSub = abRepeatMaxSub = 0;
//        resetRepeat();
        setABRepeatImage(R.drawable.ic_repeat_ab);
    }

    protected void setABRepeatImage(@DrawableRes int resId) {
        binding.ivABRepeat.setImageResource(resId);
        binding.ivABRepeatOverlay.setImageResource(resId);
    }

    protected void updateValue_abRepeatMinSub(long value) {
        abRepeatMinSub = value < 0 ? 0 :value;
    }

    protected void updateValue_abRepeatMaxSub(long value) {
        abRepeatMaxSub = value > exoPlayer.getDuration() ? exoPlayer.getDuration() :value;
    }

//    protected void initSeekBarPlayListener() {
//        viewBinding.sbRepeatRange.setOnRangeChangedListener(onPlayRangeChangedListener);
//    }

    private void initRepeatRangeSeekBar() {
        binding.sbRepeatRange.setRange(rangeLeft, rangeRight);
        binding.sbRepeatRange.setProgress(leftProgress, rightProgress);
    }

    private void initRepeatRangeSeekBarChangedListener() {
        binding.sbRepeatRange.setOnRangeChangedListener(onRepeatRangeRangeChangedListener);
    }

    public OnRangeChangedListener onRepeatRangeRangeChangedListener = new OnRangeChangedListener() {
        @Override
        public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            DLog.d(getLogTag(), "sbRange - onRangeChanged - leftValue=" + leftValue + " - rightValue=" + rightValue + " - isFromUser=" + isFromUser);
            if (isTrackingLeft) {
                handleMoveVideoTimeLeft(leftValue);
            } else {
                handleMoveVideoTimeRight(rightValue);
            }
            setVisibleBtnSaveABRepeatTime(View.VISIBLE);
        }

        @Override
        public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            DLog.d(getLogTag(), "sbRange - onStartTrackingTouch - isLeft=" + isLeft);
            isTrackingLeft = isLeft;
            pausePlayer();
        }

        @Override
        public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            DLog.d(getLogTag(), "sbRange - onStopTrackingTouch - isLeft=" + isLeft + " - lefValue=" + view.getLeftSeekBar().getProgress() + " - rightValue=" + view.getRightSeekBar().getProgress());
            if (isLeft) {
                onStopTrackingTouchAtRepeatSeekbar(isLeft, view.getLeftSeekBar().getProgress());
            } else {
                onStopTrackingTouchAtRepeatSeekbar(isLeft, view.getRightSeekBar().getProgress());
            }
//            DicModel dicModel = getDicModel(subtitleIndex);
//            if (dicModel != null) {
//                if (isLeft) {
//                    int changedValue = (int) ((view.getLeftSeekBar().getProgress() - leftProgress) * Constant.PLAYER.TIMER.SECOND);
//                    dicModel.setStartTime(dicModel.getStartTime() + changedValue);
////                    minSub += (view.getLeftSeekBar().getProgress() - leftProgress) * Constant.PLAYER.TIMER.SECOND;
//                } else {
////                    maxSub += (view.getRightSeekBar().getProgress() - rightProgress) * Constant.PLAYER.TIMER.SECOND;
//                    int changedValue = (int) ((view.getRightSeekBar().getProgress() - rightProgress) * Constant.PLAYER.TIMER.SECOND);
//                    dicModel.setEndTime(dicModel.getEndTime() + changedValue);
//                }
//                getMinMaxTime(dicModel);
//                isChangeProgress = true;
//                changeProgressRepeatCount = 0;
//                updateRangeSeekProgress(true);
//                playPlayer();
//            }
        }
    };

    public void handleMoveVideoTimeLeft(final float leftValue) {
        long minSubTemp = abRepeatMinSub;
        long maxSubTemp = abRepeatMaxSub;

        if (Float.compare(leftValue, rangeLeft) == 0) return;
        if (Float.compare(minSubTemp, 0) <= 0) {
            if (isABRepeatMode()) {
                updateValue_abRepeatMinSub(0);
            }
        }
        if (Float.compare(minSubTemp, maxSubTemp) >= 0) {
            if (isABRepeatMode()) {
                updateValue_abRepeatMinSub((long) (maxSubTemp - Constant.PLAYER.TIMER.SECOND));
            }
        }
    }

    public void handleMoveVideoTimeRight(final float rightValue) {
        long minSubTemp = abRepeatMinSub;
        long maxSubTemp = abRepeatMaxSub;

        if (Float.compare(rightValue, rangeRight) == 0) return;
        if (Float.compare(maxSubTemp, exoPlayer.getDuration()) >= 0) {
            if (isABRepeatMode()) {
                updateValue_abRepeatMaxSub(exoPlayer.getDuration());
            }
        }
        if (Float.compare(maxSubTemp, minSubTemp) <= 0) {
            if (isABRepeatMode()) {
                updateValue_abRepeatMaxSub((long) (minSubTemp + Constant.PLAYER.TIMER.SECOND));
            }
        }

        String displayMinSubtitleVideoTime = TimeUtil.displayTimesMilliseconds((long) minSubTemp);
        String displayMaxSubtitleVideoTime = TimeUtil.displayTimesMilliseconds((long) (maxSubTemp + (rightValue - rightProgress) * Constant.PLAYER.TIMER.SECOND));
        String displayMinMaxSubtitleVideoTime = displayMinSubtitleVideoTime + " ~ " + displayMaxSubtitleVideoTime;
    }

    protected void onStopTrackingTouchAtRepeatSeekbar(boolean isLeft, float seekbarProgress) {
        int changedValue = (int) ((seekbarProgress - rightProgress) * Constant.PLAYER.TIMER.SECOND);
        if (isLeft) {
            changedValue = (int) ((seekbarProgress - leftProgress) * Constant.PLAYER.TIMER.SECOND);
        }

        long minSubTemp = abRepeatMinSub;
        long maxSubTemp = abRepeatMaxSub;

        int minTime = (int) (Constant.PLAYER.TIMER.SECOND * 0.2);
        if (isLeft) {
            if ((minSubTemp + changedValue) > (maxSubTemp - minTime)) {
                if (isABRepeatMode()) {
                    updateValue_abRepeatMinSub((long) (maxSubTemp - minTime));
                }
            } else {
                if (isABRepeatMode()) {
                    updateValue_abRepeatMinSub((long) (minSubTemp + changedValue));
                }
            }
        } else {
            if ((maxSubTemp + changedValue) < (minSubTemp + minTime)) {
                if (isABRepeatMode()) {
                    updateValue_abRepeatMaxSub((long) ((minSubTemp + minTime)));
                }
            } else {
                if (isABRepeatMode()) {
                    updateValue_abRepeatMaxSub((long) (maxSubTemp + changedValue));
                }
            }
        }
        updateRangeSeekProgress(true);
        forcePlay();

//        isChangeProgress = true;
//        changeProgressRepeatCount = 0;
//        updateRangeSeekProgress(true);
//        playPlayer();

    }

    private void updateRangeSeekProgress(boolean isFromUser) {
        initRepeatRangeSeekBar();
        // https://github.com/dalnim/IssueOnly/issues/122
        // If I change START_TIME then play START_TIME again.
        playRepeatWhenChangeTimeFromRangeSeek(isFromUser);
    }
    public void playRepeatWhenChangeTimeFromRangeSeek(boolean isFromUser) {
//        if (isChangeProgress && isRepeat && isDisplaySubtitle()) {
        if (isFromUser) {
            long minSubTemp = abRepeatMinSub;
            long maxSubTemp = abRepeatMaxSub;


            final float repeatTime = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.PREVIEW_REPEAT_TIME;
            if (isTrackingLeft) {
                long startTime = minSubTemp;
                DLog.d(getLogTag(), "updateRangeSeekProgress - startTime=" + startTime);
                seekToInPlayer(startTime);
            } else {
                long endTime = (long) (maxSubTemp - repeatTime);
                if (endTime < 0) {
                    endTime = 0;
                }
                DLog.d(getLogTag(), "updateRangeSeekProgress - endTime=" + endTime);
                seekToInPlayer(endTime);
            }
        }
    }

    public MultiPlayerVideoModel getModel() {
        return model;
    }

    /**
     * Sub - Audio Speed
     * Adjust: 0.1F
     */
    public void handSpeedMinusClick() {
        speedAudio -= Constant.PLAYER.AUDIO.AUDIO_SPEED_ADJUST;
        updateAudioSpeed();
    }

    /**
     * set default audio speed to 1.0f
     */
    public void handleSpeedValueClick() {
        speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_DEFAULT;
        updateAudioSpeed();
    }


    /**
     * Add - Audio Speed
     * Adjust: 0.1F
     */
    public void handleSpeedPlusClick() {
        speedAudio += Constant.PLAYER.AUDIO.AUDIO_SPEED_ADJUST;
        updateAudioSpeed();
    }

    public void updateAudioSpeed() {
        updateValue_ShowViewPlayCenter(true);
        showCenterMessageView(getString(R.string.display_audio_speed, getAudioSpeedToDisplay()));
    }

    @NotNull
    protected String getAudioSpeedToDisplay() {
        if (speedAudio < Constant.PLAYER.AUDIO.AUDIO_SPEED_MIN) {
            speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_MIN;
        } else if (speedAudio > Constant.PLAYER.AUDIO.AUDIO_SPEED_MAX) {
            speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_MAX;
        }

        exoPlayer.setPlaybackParameters(new PlaybackParameters(speedAudio));
        return String.format("%.01fx", speedAudio);
    }
    private void updateAudioSpeedValueOnButton() {
        binding.tvAudioSpeedValue.setText(getAudioSpeedToDisplay());
    }

    public void setPlayWhenReady(boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(playWhenReady);
        }
    }
}
