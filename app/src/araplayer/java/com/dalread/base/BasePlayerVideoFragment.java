package com.dalread.base;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.dalread.R;
import com.dalread.activity.PlayerFragment;
import com.dalread.adapter.StudyChatAdapter;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.RecyclerViewDialog;
import com.dalread.helper.SubtitleGroupHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.model.MinMaxSubModel;
import com.dalread.model.RubyTextModel;
import com.dalread.model.ServerModel;
import com.dalread.model.VideoModel;
import com.dalread.model.VocaStudyChat;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.service.PlayerService;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.NetworkUtil;
import com.dalread.util.ProgressTracker;
import com.dalread.util.RepeatUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.SubtitleUtil;
import com.dalread.util.TimeUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class BasePlayerVideoFragment<VB extends ViewBinding> extends BasePlayerFragment implements View.OnTouchListener, IPlayerVideo {

    protected abstract LinearLayout getRoot();
    protected abstract View getLLPlayer();
    protected abstract StyledPlayerView getPlayerView();
    protected abstract View getLLCenter();
    protected abstract ImageView getIvCenterPlay();
    protected abstract TextView getTvCenterText();
    protected abstract View getLLRepeat();

    protected abstract Button getBtnCCRepeatReset();
    protected abstract RangeSeekBar getSbRepeatRange();
    protected abstract @Nullable LinearLayout getLLRepeatRange();
    protected abstract TextView getTvRepeatMin();
    protected abstract TextView getTvRepeatMax();

//    // play
    protected abstract View getLLPlay();
    protected abstract RangeSeekBar getSbPlay();
    protected abstract TextView getTvVideoStartTime();
    protected abstract TextView getTvVideoEndTime();

    protected VB binding;
    protected abstract VB inflateViewBinding(LayoutInflater inflater, ViewGroup container);

    protected abstract void onPlayerVideoPositionListener(long position);
    protected abstract void onPlayerVideoAutoSaveTime(DicModel data);
    protected abstract void onPlayerVideoPlayRangeChanged(float leftValue);
    protected abstract void onPlayerVideoPlayRangeChangedStopTracking();
    protected abstract void onPlayerVideoUpdateRangeSeekProgress();
    protected abstract void onPlayerVideoPlayClick();
    protected abstract void onPlayerVideoPlayerStateChanged(boolean playWhenReady, int playbackState);
    protected abstract void onPlayerVideoPositionDiscontinuity(int reason);
    protected abstract void onPlayerVideoRepeatSubtitle(long position);

    public BasePlayerActivity activity;
    public Map<Integer, DicModel> mapSubtitleTotal; //Dalnim Add
    public List<DicModel> subtitleListTotal; //SQLite에 있는 전체 자막 (숨김 자막도 포함)
    public List<DicModel> subtitleList; //재생할 자막 리스트, subtitleListTotal와 같거나 작음
    public List<RubyTextModel> rubyTextModels;
    public SimpleExoPlayer exoPlayer;
    public ProgressiveMediaSource mediaSource;
    public ProgressTracker progressTracker;
    public int subtitleIndex;
    public String subtitleContent; //dalnimAdd
    public boolean mIsOnEmptyDialog; //Dalnim added : if this is ON subtitleIndex means right before an empty dialog.
//    public int subtitleGroupType = Constant.PLAYER.SUB_TITLE.GROUP_TYPE.ALL_SUBTITLES;
//    public SubtitleGroupHelper.TYPE subtitleGroupHelperType = SubtitleGroupHelper.TYPE.ALL_SUBTITLES;
//    public int[] subtitleDifficultArray = new int[]{0, 0, 0, 0, 0};
//    protected VocaKnowGroupSelect vocaKnowGroupSelect; //Will replace subtitleDifficultArray
    public int typeDisplay = Constant.PLAYER.SUB_TITLE.DISPLAY.NONE;
    public boolean isRepeat;
    public int timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.NONE;
    // Double click
    public int numberOfTaps = 0;
    public final Handler mHandler = new Handler();
    public boolean isBeingPlaying = true; //지금 미디어가 재생중인지 여부 판단
    public boolean isShowViewPlayCenter = false;
    public boolean isReadyShowViewPlayCenter = false;
    public int changeProgressRepeatCount;
    public boolean isChangeProgress;
    public long viewPlayCenterTime = 0;
    public boolean isTrackingLeft = true;
    public float minSubNoExtraTime, maxSubNoExtraTime; //No delay subtitle value or Before/After a subtitle time. Only START/END TIME in the SQLite
    public float minSub, maxSub; //Including delay subtitle value.
    public float minSubWithAllExtraTime, maxSubWithAllExtraTime; //Including delay subtitle value and Before/After a subtitle time.
    public long abRepeatMinSub = 0, abRepeatMaxSub = 0; //In AB Repeat mode, use these to check playing time to repeat
    public final int rangeLeft = 0, rangeRight = 4, leftProgress = 1, rightProgress = 3;
    // audio speed
    public float speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_DEFAULT;
    public boolean isSeekBarPlayChange;
    // https://github.com/dalnim/IssueOnly/issues/141
    public boolean isDataChangeFromPhraseInfor;
    protected String idsDataChangeFromPhraseInfor, vocaIdDataChangeFromPhraseInfor, oldVocaIdDataChangeFromPhraseInfor;
    // vibrator
    private Vibrator vibrator;
    private long[] vibratePattern;
    protected boolean isDurationSet;
    protected boolean isOnStop; //Dalnim : To skip the eventBus for ear phone  when AraPlayer is not in the playing view.

    protected RecyclerViewDialog recyclerSubtitleViewDialog;
    public int playbackState;
    public boolean playWhenReady;

    protected long swipeTopMovePosition = 0;
    protected long swipeTopNewPosition = 0;
    protected int typeSwipe = Constant.PLAYER.SWIPE.NONE;
    protected float touchDownX, touchDownY;
    protected float deltaX_MoveDistance, deltaY_MoveDistance;
    protected int playerViewWidth, playerViewHeight;
    protected boolean isRightHandMode = true;
    protected int tapForwardBackwardValue, swipeForwardBackwardValue;
    protected long swipeForwardBackwardCurrentDuration;
    protected int positionVolume = 0, positionBrightness = 0;
    protected AudioManager mAudioManager;
    protected boolean stayedWithinClickDistance;
    protected float playerWidthPercent;
    private final int minSpaceToInsertMilli = 1000;
    public enum INSERT_SUBTITLE {NO, YES, YES_BUT_OVERLAP_START_TIME, YES_BUT_OVERLAP_END_TIME}
    protected File endMusicSoundFile;
    protected MediaPlayer mediaPlayerEndMusicSound;
    protected boolean isPlayingEndMusicSound;
    protected TextToSpeech textToSpeechToSpeakMediaTitle;
    protected int comprehensionRepeatedCount = 0; //듣기연습 1, 2에서 반복된 횟수, 듣기 연습 1에서는 listComprehension1PlaySubtitlesAtOnce에서 몇번째껄 플레이중인지
    protected boolean isInRepeatCurrentSubtitleOfListenMode2 = false; //듣기 2에서 현재자막을 반복중이면True, 현재 자막은 반복이 끝났고 다음자막으로 가기전이면 false(이때는 자막을 안보여줘야한다.)
    protected int abRepeatingCount = 0; // Repeat count during ABRepeat mode. AB Repeat에서 반복 횟수

    private PlayerService playerService;
    private Intent playerServiceIntent;
    private boolean playerServiceBound = false;
    protected SubtitleGroupHelper subtitleGroupHelper;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.LocalBinder binder = (PlayerService.LocalBinder) iBinder;
            playerService = binder.getService();
            playerServiceBound = true;
            initExoPlayer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playerServiceBound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BasePlayerActivity) getActivity();
        activity.getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                killPlayer();
                activity.finish();
            }
        });
        initVibrator();
        endMusicSoundFile = Voca.getVoiceFileOnLocal(Voca.getVoiceFolderOnLocal(getContext()), Constant.FILE.END_MUSIC_FILE_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = inflateViewBinding(inflater, container);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        bindPlayerService();
        initExoPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        initEventBus();
        initPlayVocaHelperListener();
        isOnStop = false;
    }

    @Override
    public void onDestroyView() {
        requireActivity().stopService(playerServiceIntent);
        killPlayer();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterEventBus();
        destoryMediaPlayerEndMusicSound();
    }

    private void destoryMediaPlayerEndMusicSound() {
        if (mediaPlayerEndMusicSound != null) {
            mediaPlayerEndMusicSound.release();
            mediaPlayerEndMusicSound = null;
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        unBindPlayerService();
        playerServiceBound = false;
        super.onStop();
        isOnStop = true;
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (!isOnStop) {
            // earphone, headphone, ear pod, ear phone
            if (successEvent.getScreen() == BaseEvent.Screen.MEDIA_BUTTON) {
                BaseEvent.EventType type = successEvent.getEventType();
                if (type == BaseEvent.EventType.MEDIA_BUTTON_PAUSE_BRROADCAST) {
                    pausePlayer();
                } else if (type == BaseEvent.EventType.MEDIA_BUTTON_PLAY_BRROADCAST) {
                    forcePlayPlayer();
//                    playPlayer();
                } else if (type == BaseEvent.EventType.MEDIA_BUTTON_PAUSE) {
                    playPlayer();
                } else if (type == BaseEvent.EventType.MEDIA_BUTTON_PLAY) {
                    pausePlayer();
                } else if (type == BaseEvent.EventType.MEDIA_MEDIA_PLAY_OR_PAUSE) {
                    if (isPlaying()) {
                        pausePlayer();
                    } else {
                        playPlayer();
                    }
                } else if (type == BaseEvent.EventType.MEDIA_BUTTON_HEADSETHOOK) {
                    if (isPlaying()) {
                        pausePlayer();
                    } else {
                        playPlayer();
                    }
                } else if (type == BaseEvent.EventType.MEDIA_MEDIA_STOP) {
                    pausePlayer();
                }
            }
        }
    }

    @Override
    public void initView() {
        mAudioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        activity.playerFileModel = getArguments().getParcelable(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        if (activity.playerFileModel == null) {
            Loading.hide();
            activity.finish();
            return;
        }
        startPlayerService();
    }

    private void startPlayerService() {
        playerServiceIntent = new Intent(requireActivity(), PlayerService.class);
        setExtraForPlayerService(playerServiceIntent);
        requireActivity().stopService(playerServiceIntent);
        Util.startForegroundService(requireActivity(), playerServiceIntent);
    }

    protected void setExtraForPlayerService(Intent intent) {
        Bundle serviceBundle = new Bundle();
        serviceBundle.putParcelable(PlayerService.PLAYER_FILE_KEY, activity.playerFileModel);
        intent.putExtra(PlayerService.PLAYER_SERVICE_BUNDLE_KEY, serviceBundle);
    }

    protected void bindPlayerService() {
        requireActivity().bindService(playerServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    protected void unBindPlayerService() {
        activity.unbindService(mConnection);
    }

    @Override
    public DicModel getDicModel(int index) {
        if (index < 0)
            return null;

        if (isHasSubtitle() && Utils.isIndexInsideRange(subtitleList, index)) {
            return subtitleList.get(index);
        }
        return null;
    }

    public DicModel getDicModelGetFirstIfIndexIsMinusOne(int index) {
        //Need to first item if index is -1
        if (index < -1)
            return null;

        if (isHasSubtitle() && Utils.isIndexInsideRange(subtitleList, index)) {
            return subtitleList.get(index);
        } else if (isHasSubtitle() && subtitleIndex == -1) {
            return subtitleList.get(0);
        }
        return null;
    }

    protected DicModel getDicModelFromTotal(int index) {
        if (index < 0)
            return null;

        if (Utils.isIndexInsideRange(subtitleListTotal, index)) {
            return subtitleListTotal.get(index);
        }
        return null;
    }
//    @Override
//    public long getStartTimeWithoutBeforeTime(DicModel item) {
//        return Voca.getStartTimeWithoutBeforeTime(item, activity.playerFileModel.getVideoModel());
//    }
//
//    @Override
//    public long getEndTimeWithoutAfterTime(DicModel item) {
//        return Voca.getEndTimeWithoutAfterTime(item, activity.playerFileModel.getVideoModel());
//    }

    public void getMinMaxTime(DicModel dicModel) {
        if (dicModel == null)
            return;

//        VideoModel videoModel = activity.playerFileModel.getVideoModel();
//        long startTime = dicModel.getStartTime();
//        long endTime = dicModel.getEndTime();
//        long duration = videoModel.getDuration();
//        int delaySubtitle = videoModel.getDelaySubtitles();
//        int playBeforeSubtitle = (int) (videoModel.getPlayBeforeSubtitle() * 1000);
//        int playAfterSubtitle = (int) (videoModel.getPlayAfterSubtitle() * 1000);
//
//        MinMaxSubModel minMaxSubModel = Voca.getMinMaxSubTime(startTime, endTime, duration, delaySubtitle, playBeforeSubtitle, playAfterSubtitle);

        MinMaxSubModel minMaxSubModel = Voca.getMinMaxSubTime(dicModel, activity.playerFileModel.getVideoModel());
        updateValue_minSub(minMaxSubModel.getMinSub());
        updateValue_maxSub(minMaxSubModel.getMaxSub());
        updateValue_minSubNoExtraTime(minMaxSubModel.getMinSubNoExtraTime());
        updateValue_maxSubNoExtraTime(minMaxSubModel.getMaxSubNoExtraTime());
        updateValue_minSubWithAllExtraTime(minMaxSubModel.getMinSubWithAllExtraTime());
        updateValue_maxSubWithAllExtraTime(minMaxSubModel.getMaxSubWithAllExtraTime());

//        minSub = minMaxSubModel.getMinSub();
//        maxSub = minMaxSubModel.getMaxSub();
//        minSubNoExtraTime = minMaxSubModel.getMinSubNoExtraTime();
//        maxSubNoExtraTime = minMaxSubModel.getMaxSubNoExtraTime();
//        minSubWithAllExtraTime = minMaxSubModel.getMinSubWithAllExtraTime();
//        maxSubWithAllExtraTime = minMaxSubModel.getMaxSubWithAllExtraTime();
////        getMinTime(dicModel);
////        getMaxTime(dicModel);
////        getMinMaxTimeNoExtraTime(dicModel);
////        getMinMaxTimeWithAllExtraTime(dicModel);
    }

    private long getMinSubTimeWithoutAddedValue(long position, VideoModel videoModel) {
        return Voca.getMinSubTimeWithoutAddedValue(position, videoModel);
    }

    private long getMaxSubTimeWithoutAddedValue(long position, VideoModel videoModel) {
        return Voca.getMaxSubTimeWithoutAddedValue(position, videoModel);

    }

    public long getMinTime(DicModel dicModel) {
//        minSub = getStartTime(dicModel);
//        long minSub1 = Voca.getStartTime(dicModel, activity.playerFileModel.getVideoModel());
        return (long) getStartTime(dicModel);
    }

    public long getMaxTime(DicModel dicModel) {
//        maxSub = getEndTime(dicModel);
//        long maxSub1 = Voca.getEndTime(dicModel, activity.playerFileModel.getVideoModel());
        return (long) getEndTime(dicModel);
    }

//
//    public void getMinMaxTimeNoExtraTime(DicModel dicModel) {
//        minSubNoExtraTime = getStartTimeNoExtraTime(dicModel);
//        maxSubNoExtraTime = getEndTimeNoExtraTime(dicModel);
//    }
//
//    public void getMinMaxTimeWithAllExtraTime(DicModel dicModel) {
//        minSubWithAllExtraTime = getStartTimeWithAllExtraTime(dicModel);
//        maxSubWithAllExtraTime = getEndTimeWithAllExtraTime(dicModel);
//    }

//    @Override
    //dialog time + delay subtitle time
    public long getStartTime(DicModel item) {
        if (item == null)
            return 0;
        return Voca.getSubtitleTimeWithDelaySubtitleTime(item.getStartTime(), activity.playerFileModel.getVideoModel());
    }
//    @Override
    public long getEndTime(DicModel item) {
        if (item == null)
            return 0;
        return Voca.getSubtitleTimeWithDelaySubtitleTime(item.getEndTime(), activity.playerFileModel.getVideoModel());
    }
//
//    //dialog time only
//    public long getStartTimeNoExtraTime(DicModel item) {
//        return Voca.getStartTimeNoExtraTime(item.getStartTime(), activity.playerFileModel.getVideoModel());
//    }
//    public long getEndTimeNoExtraTime(DicModel item) {
//        return Voca.getEndTimeNoExtraTime(item.getEndTime(), activity.playerFileModel.getVideoModel());
//    }
//
    //dialog time + delay subtitle time + Before/After a subtitle time.
    public long getStartTimeWithAllExtraTime(DicModel item) {
        return Voca.getSubtitleTimeWithAllExtraTime(item.getStartTime(), activity.playerFileModel.getVideoModel());
    }

    public long getEndTimeWithAllExtraTime(DicModel item) {
        return Voca.getSubtitleTimeWithAllExtraTime(item.getEndTime(), activity.playerFileModel.getVideoModel());
    }

    public void seekToInPlayer(long position) {
        exoPlayer.seekTo(position);
    }

    public void seekToInPlayer_MinSubWithAllExtraTime() {
        exoPlayer.seekTo((long) minSubWithAllExtraTime);
    }

    public void seekToInPlayer_MinSubWithDelayTime() {
        exoPlayer.seekTo((long) minSub);
    }

    public void seekToInPlayer_MinSubWithDelayTime_IfSubtitleEndTimeExceedDelaySubtitle(long position) {
        if (isSubtitleEndTimeExceedDelaySubtitle(position)) {
            seekToInPlayer_MinSubWithDelayTime();
        }
    }

    public boolean isHasSubtitle() {
        return !Utils.isEmptyCollection(subtitleList);
//        return subtitleList != null && !subtitleList.isEmpty();
    }

    public ArrayList<RubyTextModel> getRubyTextModels() {
        return activity.getSubDatabase().getWordRubyTag();
    }

    public void killPlayer() {
        pausePlayer();
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public void pausePlayer() {
        activity.runOnUiThread(() -> {
            if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
                DLog.d(getLogTag(), "pausePlayer");
//                exoPlayer.setPlayWhenReady(false);
                setPlayyWhenReady(false);
            }
        });
    }

    public void playPlayer() {
        activity.runOnUiThread(() -> {
            if (exoPlayer != null && !exoPlayer.getPlayWhenReady() && isBeingPlaying) {
                DLog.d(getLogTag(), "playPlayer");
//                exoPlayer.setPlayWhenReady(true);
                setPlayyWhenReady(true);
            }
        });
    }

    protected void setPlayyWhenReady(boolean toPlay) {
        if (exoPlayer != null)
            exoPlayer.setPlayWhenReady(toPlay);
    }

    //Dalnim : Don't check isPlay, just start playing.
    public void forcePlayPlayer() {
        activity.runOnUiThread(() -> {
            DLog.d(getLogTag(), "playPlayer");
            if (exoPlayer != null && !exoPlayer.getPlayWhenReady()) {
                setPlayyWhenReady(true);
            }
        });
    }

    public boolean isPlaying() {
        return exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady();
    }

    private ProgressTracker.PositionListener positionListener = position -> {
        if (activity == null || exoPlayer == null) return;
        onPlayerVideoPositionListener(position);
    };

    private ProgressiveMediaSource buildMediaSource(ServerModel serverModel, final Uri uri, String userAgent) {
        DLog.d(getLogTag(), "buildMediaSource - url=" + uri.toString());
        HttpDataSource.BaseFactory myDSFactory = new HttpDataSource.BaseFactory() {
            @Override
            protected HttpDataSource createDataSourceInternal(HttpDataSource.RequestProperties defaultRequestProperties) {
                final String auth = NetworkUtil.generateBasicAuth(serverModel.getAccountPassword());
                DefaultHttpDataSource.Factory dsf = new DefaultHttpDataSource.Factory();
                dsf.setUserAgent(userAgent);
                HttpDataSource ds = dsf.createDataSource();
                ds.setRequestProperty("Authorization", auth);
                return ds;
            }
        };

        ProgressiveMediaSource.Factory emf = new ProgressiveMediaSource.Factory(myDSFactory);
        return emf.createMediaSource(MediaItem.fromUri(uri));
    }

    protected void initExoPlayer() {
        //Dalnim added this. (I need to move this code to the right place)
//        isDisplayStartEndTimeAsMilliSeconds = sharedPreferences.getDisplayStartEndTimeInSubtitleView();
        //-----------
        if (playerServiceBound && exoPlayer == null) {
            initSeekBarPlayListener();
            initRepeatRangeSeekBar();
            activity.createSubDatabase(activity.playerFileModel);
            if (playerService != null) {
                exoPlayer = playerService.getPlayerInstance();
            } else {
                exoPlayer = new SimpleExoPlayer.Builder(requireContext()).build();
            }
            exoPlayer.addListener(new PlayerEventListener());
            progressTracker = new ProgressTracker(exoPlayer, positionListener);
            getPlayerView().setPlayer(exoPlayer);
//            String userAgent = NetworkUtil.getDefaultUserAgent();
//            if (activity.playerFileModel.isWebDAV()) {
//                Uri videoUri = Uri.parse(activity.playerFileModel.getServerModel().getPath(activity.playerFileModel.getPath()));
//                mediaSource = buildMediaSource(activity.playerFileModel.getServerModel(), videoUri, userAgent);
//            } else {
//                //This code cause an error when a video file has ? or # in the file name.
////            Uri videoUri = Uri.parse(activity.playerFileModel.getPath());
//                Uri videoUri = (new Uri.Builder()).path(activity.playerFileModel.getPath()).build();
//                mediaSource = new ProgressiveMediaSource.Factory(
//                        new DefaultDataSourceFactory(requireContext(), userAgent),
//                        new DefaultExtractorsFactory()
//                ).createMediaSource(MediaItem.fromUri(videoUri));
////            ).createMediaSource((new Uri.Builder()).path(activity.playerFileModel.getPath()).build());
//            }
//            exoPlayer.setMediaSource(mediaSource);
//            exoPlayer.prepare();
//            setPlayyWhenReady(false);
//        exoPlayer.setPlayWhenReady(false);
        }
    }

    protected void initSeekBarPlayListener() {
        getSbPlay().setOnRangeChangedListener(onPlayRangeChangedListener);
    }

    private void initRepeatRangeSeekBar() {
        getSbRepeatRange().setRange(rangeLeft, rangeRight);
        getSbRepeatRange().setProgress(leftProgress, rightProgress);
        getSbRepeatRange().setOnRangeChangedListener(onRepeatRangeRangeChangedListener);
    }

    private class PlayerEventListener implements Player.Listener {

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            DLog.d(getLogTag(), "PlayerEventListener - onPlayWhenReadyChanged - playWhenReady=" + playWhenReady + " - reason=" + reason);
            BasePlayerVideoFragment.this.playWhenReady = playWhenReady;
            updatePlayerStateAndReadyChanged();
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            DLog.d(getLogTag(), "PlayerEventListener - onPlaybackStateChanged - playbackState=" + playbackState);
            BasePlayerVideoFragment.this.playbackState = playbackState;
            updatePlayerStateAndReadyChanged();
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            DLog.d(getLogTag(), "PlayerEventListener - onPlayerError - error=" + error.toString());
            Loading.hide();
            showErrorOpenVideo();
        }

        @Override
        public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
            DLog.d(getLogTag(), "PlayerEventListener - onPositionDiscontinuity - reason=" + reason);
            onPlayerVideoPositionDiscontinuity(reason);
        }
    }

    public void updatePlayerStateAndReadyChanged() {
        activity.runOnUiThread(() -> {
            onPlayerVideoPlayerStateChanged(playWhenReady, playbackState);
            if (playbackState == ExoPlayer.STATE_READY && !isDurationSet) {
                // update seekBar play
                if (exoPlayer.getDuration() <= 0) {
                    showErrorOpenVideo();
                    return;
                }
                setDurationForSeekBar(exoPlayer.getDuration());
                isDurationSet = true;
            }
            handlePlayWhenReady(playWhenReady, playbackState);
        });
    }

    protected void setDurationForSeekBar(long duration) {
        getSbPlay().setRange(0, duration);
    }

    public void handlePlayWhenReady(boolean playWhenReady, int playbackState) {
        try {
            if (playWhenReady) {
                getIvCenterPlay().setTag(R.drawable.ic_new_pause);
                progressTracker.startHandle();
            } else {
                getIvCenterPlay().setTag(R.drawable.ic_new_play);
                progressTracker.stopHandler();
            }
            handleViewPlayCenterClick(playWhenReady, playbackState);
        } catch (Exception ex) {
            DLog.e(getLogTag(), ex.getMessage());
        }
    }

    public void handleViewPlayCenterClick(boolean isPlay, int playbackState) {
        if (!isReadyShowViewPlayCenter) {
            if (playbackState == Player.STATE_READY) {
                isReadyShowViewPlayCenter = true;
            }
            return;
        }

        showCenterMessageView(isPlay ? R.drawable.ic_exo_player_icon_play : R.drawable.ic_exo_player_icon_pause);
    }

    public void showCenterMessageView(String value) {
        showCenterMessageView(value, 0);
    }

    public void showCenterMessageView(int resId) {
        showCenterMessageView(Constant.BASE_BLANK, resId);
    }

    public void showCenterMessageView(String value, int resId) {
        if (!isShowViewPlayCenter) return;
        viewPlayCenterTime = System.currentTimeMillis();

        getTvCenterText().setText(value);
        setVisibleCenterText(Utils.isEmpty(value) ? View.GONE : View.VISIBLE);
//        tvCenterText.setVisibility(Utils.isEmpty(value) ? View.GONE : View.VISIBLE);

        getIvCenterPlay().setImageResource(resId);
        getIvCenterPlay().setVisibility(resId <= 0 ? View.GONE : View.VISIBLE);

        getLLCenter().setVisibility(View.VISIBLE);
        updateValue_ShowViewPlayCenter(false);
        mHandler.post(onViewCenterRunnable);
    }

    /**
     * check after 0.5 seconds > hide view play center
     */
    public Runnable onViewCenterRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                long currentTime = System.currentTimeMillis();
                if (currentTime - viewPlayCenterTime >= Constant.PLAYER.TIMER.HIDE_TEXT_CENTER) {
                    getIvCenterPlay().setVisibility(View.GONE);
                    getIvCenterPlay().setImageResource(0);
                    setVisibleCenterText(View.GONE);
//                    tvCenterText.setVisibility(View.GONE);
                    getTvCenterText().setText(Constant.BASE_BLANK);
                    getLLCenter().setVisibility(View.GONE);
                    updateValue_ShowViewPlayCenter(false);
                } else {
                    mHandler.postDelayed(this, Constant.PLAYER.TIMER.HIDE_TEXT_CENTER);
                }
            } catch (Exception ex) {
                DLog.e(getLogTag(), ex.getMessage());
            }
        }
    };

    protected void handlePlayClick(boolean isPlay, boolean isShowViewPlayCenter) {
        DLog.d(getLogTag(), "handlePlayClick - isPlay=" + isPlay);
        activity.runOnUiThread(() -> {
            onPlayerVideoPlayClick();
            this.isBeingPlaying = isPlay;
            updateValue_ShowViewPlayCenter(isShowViewPlayCenter);
            setPlayyWhenReady(isPlay);
//            exoPlayer.setPlayWhenReady(isPlay);
        });
    }

    public void initVibrator() {
        vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        vibratePattern = new long[]{0, 1000};
    }

//    public void runVibrator() {
//        if (vibrator != null && vibrator.hasVibrator()) {
//            vibrator.vibrate(vibratePattern, -1);
//        }
//    }

    public void onResetButton() {
        DicModel dicModel = getDicModel(subtitleIndex);
        dicModel.setStartTime(dicModel.getStartTimeOriginal());
        dicModel.setEndTime(dicModel.getEndTimeOriginal());
        activity.getSubDatabase().updateTimeSubtitle(dicModel);
        getMinMaxTime(dicModel);
//        minSub = getStartTime(dicModel);
//        maxSub = getEndTime(dicModel);
        updateRangeSeek();
//        setVisibleCCRepeatReset(View.GONE);
//        btnReset.setVisibility(View.GONE);
    }

    /**
     * onPlayRangeChangedListener
     * update when change seekBar play
     */
    public OnRangeChangedListener onPlayRangeChangedListener = new OnRangeChangedListener() {
        @Override
        public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            if (!isSeekBarPlayChange) return;
            getTvVideoStartTime().setText(getDisplayTimes((long) leftValue, isDisplayStartEndTimeInSubtitleView()));
            getTvVideoEndTime().setText(getDisplayTimes((long) (exoPlayer.getDuration() - leftValue), isDisplayStartEndTimeInSubtitleView()));
            onPlayerVideoPlayRangeChanged(leftValue);
        }

        @Override
        public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            isSeekBarPlayChange = true;
            pausePlayer();
        }

        @Override
        public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            seekToInPlayer((long) view.getLeftSeekBar().getProgress());
//            exoPlayer.seekTo((long) view.getLeftSeekBar().getProgress());
            onPlayerVideoPlayRangeChangedStopTracking();
            playPlayer();
        }
    };
    // Long subtitle repeat time range bar for Repetition on top menu(Olny AB Repeat uses this. No CC Repeat)
    public OnRangeChangedListener onRepeatRangeRangeChangedListener = new OnRangeChangedListener() {
        @Override
        public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            DLog.d(getLogTag(), "sbRange - onRangeChanged - leftValue=" + leftValue + " - rightValue=" + rightValue + " - isFromUser=" + isFromUser);
            if (isTrackingLeft) {
                handleMoveVideoTimeLeft(leftValue);
            } else {
                handleMoveVideoTimeRight(rightValue);
            }
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

    protected void onStopTrackingTouchAtRepeatSeekbar(boolean isLeft, float seekbarProgress) {
        int changedValue = (int) ((seekbarProgress - rightProgress) * Constant.PLAYER.TIMER.SECOND);
        if (isLeft) {
            changedValue = (int) ((seekbarProgress - leftProgress) * Constant.PLAYER.TIMER.SECOND);
        }

        long minSubTemp = getMinSubByMode();
        long maxSubTemp = getMaxSubByMode();

        int minTime = (int) (Constant.PLAYER.TIMER.SECOND * 0.2);
        if (isLeft) {
            if ((minSubTemp + changedValue) > (maxSubTemp - minTime)) {
                if (isABRepeatMode()) {
                    updateValue_abRepeatMinSub((long) (maxSubTemp - minTime));
                } else {
                    updateValue_minSub((long) (maxSubTemp - minTime));
                }
//                minSub = (float) (maxSub - Constant.PLAYER.TIMER.SECOND * 0.1);
                ToastUtil.getInstance(getContext()).show(R.string.message_subtitle_start_time_less_than_end_time);
            } else {
                if (isABRepeatMode()) {
                    updateValue_abRepeatMinSub((long) (minSubTemp + changedValue));
                } else {
                    updateValue_minSub((long) (minSubTemp + changedValue));
                }
//                minSub += changedValue;
            }
        } else {
            if ((maxSubTemp + changedValue) < (minSubTemp + minTime)) {
                if (isABRepeatMode()) {
                    updateValue_abRepeatMaxSub((long) ((minSubTemp + minTime)));
                } else {
                    updateValue_maxSub((long) ((minSubTemp + minTime)));
                }

//                maxSub = (float) (minSub + Constant.PLAYER.TIMER.SECOND * 0.1);
                ToastUtil.getInstance(getContext()).show(R.string.message_subtitle_end_time_greater_than_start_time);
            } else {
                if (isABRepeatMode()) {
                    updateValue_abRepeatMaxSub((long) (maxSubTemp + changedValue));
                } else {
                    updateValue_maxSub((long) (maxSubTemp + changedValue));
                }
//                maxSub += changedValue;
            }
        }

        if (!isABRepeatMode() && Utils.isIndexInsideRange(subtitleList, subtitleIndex)) {
            DicModel dicModel = getDicModel(subtitleIndex);
            if (dicModel != null) {
                int delaySubtitle = activity.playerFileModel.getVideoModel().getDelaySubtitles();
                if (isLeft) {
                    dicModel.setStartTime((long) minSub - delaySubtitle);
                } else {
                    dicModel.setEndTime((long) maxSub - delaySubtitle);
                }
                getMinMaxTime(dicModel);
            }
        }
        //Old code.
//        if (isCCRepeatMode()) {
//            DicModel dicModel = getDicModel(subtitleIndex);
//            if (isLeft) {
//                dicModel.setStartTime(dicModel.getStartTime() + changedValue);
//            } else {
//                dicModel.setEndTime(dicModel.getEndTime() + changedValue);
//            }
//            getMinMaxTime(dicModel);
//        } else {
////            if (isLeft) {
////                minSub += changedValue;
////            } else {
////                maxSub += changedValue;
////            }
//
//        }

        isChangeProgress = true;
        changeProgressRepeatCount = 0;
        updateRangeSeekProgress(true);
        playPlayer();

    }
    public void updateRangeSeekProgress(boolean isFromUser) {
        getSbRepeatRange().setProgress(leftProgress, rightProgress);
        if (isFromUser && isCCRepeatMode())
            checkAutoSaveTime();
        // https://github.com/dalnim/IssueOnly/issues/122
        // If I change START_TIME then play START_TIME again.
        playRepeatWhenChangeTimeFromRangeSeek(isFromUser);
        onPlayerVideoUpdateRangeSeekProgress();
    }

    public void checkAutoSaveTime() {
        DLog.d(getLogTag(), "checkAutoSaveTime");
        if (!isHasSubtitle() || subtitleIndex < 0 || Utils.isEmpty(subtitleList))
            return;

        final DicModel dicModel = subtitleList.get(subtitleIndex);
        if (isDifferentSubtitleStartEndTimeVsOriginalTime(dicModel, activity.playerFileModel.getVideoModel())) {
            //DALNIM : Don't add Delay Value when the min/maxSub value is stored in the SQLite.
            dicModel.setStartTime(getMinSubTimeWithoutAddedValue((long) minSub, activity.playerFileModel.getVideoModel()));
            dicModel.setEndTime(getMaxSubTimeWithoutAddedValue((long) maxSub, activity.playerFileModel.getVideoModel()));
            activity.getSubDatabase().updateTimeSubtitle(dicModel);
//            if (isCCRepeatMode()) {
                setVisibleCCRepeatReset(View.VISIBLE);
//            }
            onPlayerVideoAutoSaveTime(dicModel);
            DLog.d(getLogTag(), "checkAutoSaveTime - minSub=" + minSub + " - maxSub=" + maxSub);
        } else {
            setVisibleCCRepeatReset(View.GONE);
        }
    }

    public void playRepeatWhenChangeTimeFromRangeSeek(boolean isFromUser) {
//        if (isChangeProgress && isRepeat && isDisplaySubtitle()) {
        if (isFromUser) {
            long minSubTemp = getMinSubByMode();
            long maxSubTemp = getMaxSubByMode();


            isChangeProgress = false;
            DLog.d(getLogTag(), "updateRangeSeekProgress - minSub=" + minSub + " - maxSub=" + maxSub + " - isTrackingLeft=" + isTrackingLeft);
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
            if (isBeingPlaying && !exoPlayer.getPlayWhenReady()) {
                handlePlayClick(true, false);
            }
        }
    }

    protected boolean isDifferentSubtitleStartEndTimeVsOriginalTime(DicModel dicModel, VideoModel videoModel) {
        if (minSub < 0 || maxSub >= videoModel.getDuration()) {
            return false;
        }
        if (maxSub < 0 || maxSub >= videoModel.getDuration()) {
            return false;
        }
        if (minSub >= maxSub) {
            return false;
        }
        long startTimeOriginalWithDelaySubtitleTime = dicModel.getStartTimeOriginal() + videoModel.getDelaySubtitles();
        long endTimeOriginalWithDelaySubtitleTime = dicModel.getEndTimeOriginal() + videoModel.getDelaySubtitles();
        if ((minSub == startTimeOriginalWithDelaySubtitleTime) && (maxSub == endTimeOriginalWithDelaySubtitleTime)) {
            return  false;
        }

        return true;
    }

    private long getMinSubByMode() {
        if (isABRepeatMode()) {
            return abRepeatMinSub;
        } else {
            return (long) minSub;
        }
    }
    private long getMaxSubByMode() {
        if (isABRepeatMode()) {
            return abRepeatMaxSub;
        } else {
            return (long) maxSub;
        }
    }

    public void handleMoveVideoTimeLeft(final float leftValue) {
        long minSubTemp = getMinSubByMode();
        long maxSubTemp = getMaxSubByMode();

        DLog.d(getLogTag(), "handleMoveLeft - leftValue=" + leftValue + "- minSub=" + minSub);
        if (Float.compare(leftValue, rangeLeft) == 0) return;
        if (Float.compare(minSubTemp, 0) <= 0) {
            if (isABRepeatMode()) {
                updateValue_abRepeatMinSub(0);
            } else {
                updateValue_minSub(0);
            }
//            minSub = 0;
        }
        if (Float.compare(minSubTemp, maxSubTemp) >= 0) {
            if (isABRepeatMode()) {
                updateValue_abRepeatMinSub((long) (maxSubTemp - Constant.PLAYER.TIMER.SECOND));
            } else {
                updateValue_minSub((long) (maxSubTemp - Constant.PLAYER.TIMER.SECOND));
            }
//            minSub = maxSub - Constant.PLAYER.TIMER.SECOND;
        }

        String displayMinSubtitleVideoTime = TimeUtil.displayTimesMilliseconds((long) (minSubTemp + (leftValue - leftProgress) * Constant.PLAYER.TIMER.SECOND));
        String displayMaxSubtitleVideoTime = TimeUtil.displayTimesMilliseconds((long) maxSubTemp);
        String displayMinMaxSubtitleVideoTime = displayMinSubtitleVideoTime + " ~ " + displayMaxSubtitleVideoTime;
        if (isCCRepeatMode()) {
            displayMinMaxSubtitleVideoTime = getDisplayMinMaxSubtitleVideoTimeWithPrevNext(displayMinSubtitleVideoTime, displayMaxSubtitleVideoTime);
        }
        //When I click the CC Repeat(No displaying RepeatRange bar), this method is called and show the showCenterMessageView. (No need this, so I use if statement)

        if (leftValue != leftProgress)
            updateValue_ShowViewPlayCenter(true);
        showCenterMessageView(displayMinMaxSubtitleVideoTime);

        updateRangeSeekChangedMinText(displayMinSubtitleVideoTime);
    }

    public void handleMoveVideoTimeRight(final float rightValue) {
        long minSubTemp = getMinSubByMode();
        long maxSubTemp = getMaxSubByMode();

        DLog.d(getLogTag(), "handleMoveRight - rightValue=" + rightValue + "- maxSub=" + maxSub);
        if (Float.compare(rightValue, rangeRight) == 0) return;
        if (Float.compare(maxSubTemp, exoPlayer.getDuration()) >= 0) {
            if (isABRepeatMode()) {
                updateValue_abRepeatMaxSub(exoPlayer.getDuration());
            } else {
                updateValue_maxSub(exoPlayer.getDuration());
            }
//            maxSub = exoPlayer.getDuration();
        }
        if (Float.compare(maxSubTemp, minSubTemp) <= 0) {
            if (isABRepeatMode()) {
                updateValue_abRepeatMaxSub((long) (minSubTemp + Constant.PLAYER.TIMER.SECOND));
            } else {
                updateValue_maxSub((long) (minSubTemp + Constant.PLAYER.TIMER.SECOND));
            }
//            maxSub = minSub + Constant.PLAYER.TIMER.SECOND;
        }

        String displayMinSubtitleVideoTime = TimeUtil.displayTimesMilliseconds((long) minSubTemp);
        String displayMaxSubtitleVideoTime = TimeUtil.displayTimesMilliseconds((long) (maxSubTemp + (rightValue - rightProgress) * Constant.PLAYER.TIMER.SECOND));
        String displayMinMaxSubtitleVideoTime = displayMinSubtitleVideoTime + " ~ " + displayMaxSubtitleVideoTime;
        if (isCCRepeatMode()) {
            displayMinMaxSubtitleVideoTime = getDisplayMinMaxSubtitleVideoTimeWithPrevNext(displayMinSubtitleVideoTime, displayMaxSubtitleVideoTime);
        }
        //Without IF statement, center message view is displayed when I click the Reset button after I change the End time. (I don't need it to be displayed.)
        if (rightValue != rightProgress)
            updateValue_ShowViewPlayCenter(true);
        showCenterMessageView(displayMinMaxSubtitleVideoTime);

        updateRangeSeekChangedMaxText(displayMaxSubtitleVideoTime);
    }

    private String getDisplayMinMaxSubtitleVideoTimeWithPrevNext(String displayMinSubtitleVideoTime, String displayMaxSubtitleVideoTime) {
        String displayMinMaxVideoTimePreviousSubtitle = getMinMaxSubtitleTime(subtitleIndex - 1);
        String displayMinMaxVideoTimeNextSubtitle = getMinMaxSubtitleTime(subtitleIndex + 1);

        String displayMinMaxSubtitleVideoTime = displayMinMaxVideoTimePreviousSubtitle + "\n\n" + displayMinSubtitleVideoTime + " ~ " + displayMaxSubtitleVideoTime + "\n\n" + displayMinMaxVideoTimeNextSubtitle;
        return org.apache.commons.lang3.StringUtils.strip(displayMinMaxSubtitleVideoTime);
    }
    private String getMinMaxSubtitleTime(int subtitleIndexToFind) {
        String minMaxSubtitleTime = "";
        if ((0 <= subtitleIndexToFind) && (subtitleIndexToFind < subtitleList.size())) {
            DicModel dicModel = subtitleList.get(subtitleIndexToFind);
            String minSubtitleTime = TimeUtil.displayTimesMilliseconds(dicModel.getStartTime());
            String maxSubtitleTime = TimeUtil.displayTimesMilliseconds(dicModel.getEndTime());
            minMaxSubtitleTime = minSubtitleTime + " ~ " + maxSubtitleTime;
        }
        return minMaxSubtitleTime;
    }

    public void updateRangeSeek() {
        long minSubTemp = getMinSubByMode();
        long maxSubTemp = getMaxSubByMode();

        boolean isZero = minSubTemp < Constant.PLAYER.TIMER.SECOND;
        if (isZero) {
            minSubTemp = 0;
        }
        if (isRepeat) {
            updateRangeSeekChangedMinText(TimeUtil.displayTimesMilliseconds(minSubTemp));
            updateRangeSeekChangedMaxText(TimeUtil.displayTimesMilliseconds(maxSubTemp));
        } else {
            updateRangeSeekChangedMinText(getDisplayTimes(minSubTemp, isDisplayStartEndTimeInSubtitleView()));
            updateRangeSeekChangedMaxText(getDisplayTimes(maxSubTemp, isDisplayStartEndTimeInSubtitleView()));
        }
        updateRangeSeekProgress(false);
    }


    protected void updateRangeSeekChangedMinText(String value) {
//        ToastUtil.getInstance(activity).show("updateRepeatMinText : " + value);
        getTvRepeatMin().setText(value);
    }

    protected void updateRangeSeekChangedMaxText(String value) {
//        ToastUtil.getInstance(activity).show("updateRepeatMaxText : " + value);
        getTvRepeatMax().setText(value);
    }

    public void updateSeekBarPlay(final long position) {
        if (getSbPlay() != null && getLLPlay().getVisibility() == View.VISIBLE) {
            getSbPlay().setProgress(position);
            getTvVideoStartTime().setText(getDisplayTimes(position, isDisplayStartEndTimeInSubtitleView()));
            getTvVideoEndTime().setText(getDisplayTimes(exoPlayer.getDuration() - position, isDisplayStartEndTimeInSubtitleView()));
        }
    }

    public void repeatSubtitle(final long position) {
        if (exoPlayer != null && exoPlayer.getPlaybackState() != ExoPlayer.STATE_READY)
            return;
        onPlayerVideoRepeatSubtitle(position);
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

    public String getDisplayTimes(long value, boolean isDisplayStartEndTime) {
        if (isDisplayStartEndTime) {
            return String.valueOf(value);
        }
        return getDisplayTimes(value);
    }

    public String getDisplayTimes(long value) {
        if (exoPlayer != null && value > exoPlayer.getDuration()) {
            value = exoPlayer.getDuration();
        }
        if (value < 0) {
            value = 0;
        }

        return TimeUtil.getDisplay(value);
    }
    // TODO : Can not understand this exactly.
    public boolean isDisplaySubtitle() {
        return typeDisplay == Constant.PLAYER.SUB_TITLE.DISPLAY.SUBTITLE_LIST;
    }

    protected boolean isListenComprehensionMode() {
        return isDisplayListenComprehension1() || isDisplayListenComprehension2();
    }

    public boolean isDisplayListenComprehension1() {
        return typeDisplay == Constant.PLAYER.SUB_TITLE.DISPLAY.LISTEN_COMPREHENSION_1;
    }

    public boolean isDisplayListenComprehension2() {
        return typeDisplay == Constant.PLAYER.SUB_TITLE.DISPLAY.LISTEN_COMPREHENSION_2;
    }

    public void setEditSubtitle() {
        typeDisplay = Constant.PLAYER.SUB_TITLE.DISPLAY.SUBTITLE_LIST;
        setRepeat();
//        isRepeat = true;
    }

    public void setDictationMode() {
        typeDisplay = Constant.PLAYER.SUB_TITLE.DISPLAY.SUBTITLE_LIST;
        setRepeat();
//        isRepeat = true;
    }

    public void resizePlayerView(View view) {
        view.post(() -> {
            playerViewWidth = view.getWidth();
            playerViewHeight = (int) (view.getWidth() * Constant.PLAYER.THUMBNAIL_HEIGHT_RATIO_BY_WIDTH);
            view.getLayoutParams().height = playerViewHeight;
//            getScreenSize();
            }
        );
    }

    //A-B repeat
    public boolean isABRepeatMode() {
        return timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.B;
    }

    public boolean isABRepeatMode_A_Button_Clicked() {
        return timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.A;
    }

    //Dalnim Added this
    public boolean isCCRepeatMode() {
        if (isABRepeatMode()  == false && isRepeat == true) {
            return true;
        }
        return false;
    }

    protected boolean isAnyRepeatMode() {
        return isCCRepeatMode()
                || isABRepeatMode()
                || isListenComprehensionMode();
    }
    public void resetTimeBaseRepeatStatus() {
        if (isABRepeatMode()) {
            resetRepeat();
//            isRepeat = false;
            timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.NONE;
        }
    }

    protected void showErrorOpenVideo() {
        pausePlayer();
        final AlertDialog errorDialog = new AlertDialog(activity);
        errorDialog.setCancelable(false);
        errorDialog.setCanceledOnTouchOutside(false);
        errorDialog.show(getString(R.string.exoplayer_msg_error_open_video),
                null, (dialog, which) -> {
                    dialog.dismiss();
                    activity.finish();
                });
    }

    protected boolean isVisibleUI(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    protected void showOrHideCCRepeatReset(DicModel dicModel) {
        if (dicModel == null)
            return;

        if (isABRepeatMode()) {
            setVisibleCCRepeatReset(View.GONE);
        } else if (isDifferentSubtitleStartEndTimeVsOriginalTime(dicModel, activity.playerFileModel.getVideoModel())) {
            setVisibleCCRepeatReset(View.VISIBLE);
        } else {
            setVisibleCCRepeatReset(View.INVISIBLE);
        }
    }

    protected void setVisibleCCRepeatReset(int visibility) {
        getBtnCCRepeatReset().setVisibility(visibility);
    }

    protected void setVisiblellCenter(int visibility) {
        setVisibleCenterText(visibility);
        setVisibleLLPlay(visibility);
    }
    protected void setVisibleCenterText(int visibility) {
        getTvCenterText().setVisibility(visibility);
    }

    protected void setVisibleLLPlay(int visibility) {
        getLLPlay().setVisibility(visibility);
    }

    protected boolean isPositionExceedABRepeat(long position) {
        return position >= abRepeatMaxSub;
    }

    protected boolean isSubtitleEndTimeExceedDelaySubtitle(long position) {
        return position >= maxSub;
    }

    protected boolean isSubtitleEndTimeExceedNoExtraTime(long position) {
        return position >= maxSubNoExtraTime;
    }

    protected boolean isSubtitleEndTimeExceedAllExtraTime(long position) {
        return position >= maxSubWithAllExtraTime;
    }

    protected boolean isSubtitleEndTimeExceedAllExtraTimeAndAdjustEndTime(long position) {
        int adjustEndTime = (int) (activity.playerFileModel.getVideoModel().getPlayAfterSubtitle() * 1000);
        return position >= (maxSubWithAllExtraTime + adjustEndTime);
    }

    protected boolean isExceedSubtitleEndTimeInRepeatMode(long position) {
//        return (getPlayingPositionWithDelaySubtitlePlusPlayAfterTime(position)) >= mMinMaxSubModel.getMaxSubWithAllExtraTime();
//        return (getPlayingPositionWithDelaySubtitleTime(position)) >= mMinMaxSubModel.getMaxSubWithAllExtraTime();
        return position >= maxSubWithAllExtraTime;
    }

//    protected void saveVocaKnowValue(IVocaBasicItem item) {
//        activity.saveVocaKnowValueInDB(item);
//        saveVocaKnowValueInServer(item.getVIVocaType(), item.getVIVocaId(), item.getVIVocaKnow(), item.getVIVocaKnowPronounce());
//        saveVocaKnowValueInServerWhenSubtitleHasOneWord(item);
//    }
//
//    private void saveVocaKnowValueInServer(int vocaType, int vocaId, int vocaKnow, int vocaKnowPronounce) {
//        application.getDalAiImpl().changeMultipleVocaKnow(
//                String.valueOf(vocaKnow),
//                String.valueOf(vocaKnowPronounce),
//                String.valueOf(vocaId),
//                String.valueOf(vocaType),
//                null);
//    }
//
//    private void saveVocaKnowValueInServerWhenSubtitleHasOneWord(IVocaBasicItem item) {
//        if (isVocaTypeSubtitleAndHasOneWord(item)) {
//            //Save VocaBase word's KNOW value in server too.
//            saveVocaKnowValueInServer(item.getVIVocaTypeBase(), item.getVIVocaIdBase(), item.getVIVocaKnow(), item.getVIVocaKnowPronounce());
//        }
//    }

//    protected boolean isVocaTypeSubtitleAndHasOneWord(IVocaBasicItem iVocaBasicItem) {
//        return isVocaTypeSubtitle(iVocaBasicItem) && (iVocaBasicItem.getVIVocaTypeBase() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD);
//    }

//    protected boolean isVocaTypeSubtitle(IVocaBasicItem iVocaBasicItem) {
//        return (iVocaBasicItem.getVIVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE) || (iVocaBasicItem.getVIVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE_UNTUNED);
//    }

    private void initPlayVocaHelperListener() {
        DLog.d(getLogTag(), "initPlayVocaHelper");
        if (!activity.playTTS.playTTSHelper.hasMotherTongueListener()) {
            activity.playTTS.playTTSHelper.setMotherTongueListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                    updateItemStatus(utteranceId, true);
                }

                @Override
                public void onDone(String utteranceId) {
                    if (!Utils.isEmpty(utteranceId) && utteranceId.equals(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.KEY_PLAY_DONE)) {
                        handlePlayClick(true, false);
                    }
                }

                @Override
                public void onError(String utteranceId) {
                }
            });
        }
        if (!activity.playTTS.playTTSHelper.hasStudyListener()) {
            activity.playTTS.playTTSHelper.setStudyListener(new UtteranceProgressListener() {

                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    updateItemStatus(utteranceId, false);
                }

                @Override
                public void onError(String utteranceId) {
                    updateItemStatus(utteranceId, false);
                }
            });
        }
    }

    private void updateItemStatus(String utteranceId, boolean playing) {
        if (recyclerSubtitleViewDialog != null && recyclerSubtitleViewDialog.isShowing()) {
            StudyChatAdapter adapter = (StudyChatAdapter) recyclerSubtitleViewDialog.getAdapter();
            for (Object obj : adapter.getData()) {
                if (obj instanceof VocaStudyChat) {
                    VocaStudyChat voca = (VocaStudyChat) obj;
                    if (utteranceId.equals(String.valueOf(voca.getVIId()))) {
                        DLog.d(getLogTag(), "utteranceId=" + utteranceId + " - " + voca.getVIId() + " - voca");
                        voca.setVIPlaying(playing);
                        activity.runOnUiThread(() -> {
                            adapter.notifyItemChanged(voca);
                        });
//                        rvListMeaning.post(() -> adapter.notifyItemChanged(voca));
                        break;
                    }
                }
            }
        }
    }

    protected void updateKnowWhenDoubleClick(IVocaBasicItem iVocaBasicItem) {
        updateVocaKnow(VocaKnow.switchVocaKnow(iVocaBasicItem));
    }

    protected void updateVocaKnow(IVocaBasicItem iVocaBasicItem) {
        activity.saveVocaKnowValue(iVocaBasicItem);
        updateUIVocaInfoInWordListPopup(iVocaBasicItem);
    }

    protected void updateUIVocaInfoInWordListPopup(IVocaBasicItem iVocaBasicItem) {
        if (recyclerSubtitleViewDialog != null && recyclerSubtitleViewDialog.isShowing() && iVocaBasicItem != null) {
//            recyclerSubtitleViewDialog.setUpdateData(true);
            //Update only this Voca (So don't reorder the items)
            StudyChatAdapter studyChatAdapter = (StudyChatAdapter) recyclerSubtitleViewDialog.getAdapter();
            for (Object obj : studyChatAdapter.getData()) {
                if (obj instanceof VocaStudyChat) {
                    VocaStudyChat voca = (VocaStudyChat) obj;
                    if ((voca.getVIVocaType() == iVocaBasicItem.getVIVocaType()) && (voca.getVocaId() == iVocaBasicItem.getVIVocaId())) {
                        voca.setVIVocaKnow(iVocaBasicItem.getVIVocaKnow());
                        voca.setVIVocaKnowPronounce(iVocaBasicItem.getVIVocaKnowPronounce());
                        voca.setVIBookmark(iVocaBasicItem.getVIBookmark());
                        studyChatAdapter.notifyItemChanged(voca);
                        break;
                    }
                }
            }

            //This updates all vocas and re-order the items order.
//            if (activity.getSubDatabase() != null && studyChatAdapter.getSubtitleId() != null) {
//                studyChatAdapter.setDataNoLoop(activity.getSubDatabase().getVocaStudyChat(studyChatAdapter.getSubtitleId()));
//            }
//            studyChatAdapter.notifyDataSetChanged();
        }
    }

    protected OnKnowChangeListener onKnowChangeListenerForVocaWord = new OnKnowChangeListener() {
        @Override
        public void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {
            if (activity.updateModelWithNewVocaKnow(iVocaBasicItem, newVocaKnow, -1)) {
                updateVocaKnow(iVocaBasicItem);
                //Update KNOW displaying in playerfragment's subtitle
                //TODO : If I'm on playerfragment, I don't want to call this.
                // https://github.com/dalnim/IssueOnly/issues/185#issuecomment-900787957
                PlayerFragment playerFragment = (PlayerFragment) activity.getSupportFragmentManager().findFragmentByTag(PlayerFragment.class.getName());
                if (playerFragment == null || !playerFragment.isVisible()) {
                    eventBus.post(new SuccessEvent(BaseEvent.Screen.BASE_PLAYER_VIDEO_FRAGMENT, BaseEvent.EventType.REAOAD_SUBTITLE_AGAIN, iVocaBasicItem));
                }
            }
        }

        @Override
        public void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce) {
            //Todo : update it.
            if (activity.updateModelWithNewVocaKnow(iVocaBasicItem, -1, newVocaKnowPronounce)) {
                updateVocaKnow(iVocaBasicItem);
            }
        }

        @Override
        public void onAddToWordbook(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onAddToBookmark(IVocaBasicItem iVocaBasicItem) {
            if (iVocaBasicItem instanceof VocaStudyChat) {
                updateBookmark(iVocaBasicItem);
                if (recyclerSubtitleViewDialog.getAdapter() instanceof StudyChatAdapter) {
                    StudyChatAdapter studyChatAdapter = (StudyChatAdapter) recyclerSubtitleViewDialog.getAdapter();
                    //TODO : Is it best to load from SQLite again?
                    studyChatAdapter.setDataNoLoop(activity.getSubDatabase().getVocaStudyChat( studyChatAdapter.getSubtitleId()));
                    studyChatAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem) {
            if (iVocaBasicItem instanceof VocaStudyChat) {
                updateBookmark(iVocaBasicItem);
                if (recyclerSubtitleViewDialog.getAdapter() instanceof StudyChatAdapter) {
                    StudyChatAdapter studyChatAdapter = (StudyChatAdapter) recyclerSubtitleViewDialog.getAdapter();
                    studyChatAdapter.setDataNoLoop(activity.getSubDatabase().getVocaStudyChat( studyChatAdapter.getSubtitleId()));
                    studyChatAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onDismiss() {

        }
    };

    protected void updateValue_ShowViewPlayCenter(boolean value) {
        DLog.d("dalnim", "updateValue_ShowViewPlayCenter value : " + value);
        isShowViewPlayCenter = value;
    }

    protected void updateBookmark(IVocaBasicItem iVocaBasicItem) {
        activity.updateBookmarkValue(iVocaBasicItem);
//        iVocaBasicItem.swapVIBookmark();
//        saveVocaBookmarkValue(iVocaBasicItem);
        updateUIVocaInfoInWordListPopup(iVocaBasicItem);
    }

//    private void saveVocaBookmarkValue(IVocaBasicItem item) {
//        activity.getSubDatabase().updateBookmarkInDB(item);
//        saveBookmarkValueInServer(item);
//    }
//
//    private void saveBookmarkValueInServer(IVocaBasicItem item) {
//        if (item.isVIBookmark()) {
//            application.getDalAiImpl().addToBookmark(item.getVIVoca(), String.valueOf(item.getVIVocaId()), item.getVIVocaType(), null);
//        } else {
//            application.getDalAiImpl().deleteFromBookmark(item.getVIVoca(), String.valueOf(item.getVIVocaId()), item.getVIVocaType(), null);
//        }
//    }

    protected void handleABRepeatClick() {
        switch (timeBaseRepeatStatus) {
            case Constant.PLAYER.REPEAT.TIMEBASE.NONE:
                enterABRepeatModeAB_A();
                break;
            case Constant.PLAYER.REPEAT.TIMEBASE.A:
                enterABRepeatModeAB_B(false);
                break;
            case Constant.PLAYER.REPEAT.TIMEBASE.B:
                exitABRepeatMode();
                break;
            default:

        }
    }

    protected void enterABRepeatModeAB_A() {
        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.A;
        resetRepeat();
        updateValue_abRepeatMinSub(exoPlayer.getCurrentPosition() + RepeatUtil.getTime(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BEFORE_AB_REPEAT));
        handlePlayClick(true, false);
        setABRepeatImage(R.drawable.ic_repeat_ab_a);
    }

    protected void enterABRepeatModeAB_B(boolean isEndOfDuration) {
        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.B;
        abRepeatingCount = 0;
        setRepeat();
        updateValue_abRepeatMaxSub(exoPlayer.getCurrentPosition() + RepeatUtil.getTime(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_AFTER_AB_REPEAT));
        if (isEndOfDuration) {
            updateValue_abRepeatMaxSub(exoPlayer.getDuration());
        }
        updateRangeSeek();
        handlePlayClick(true, false);
        setABRepeatImage(R.drawable.ic_repeat_ab_b);
    }

    protected void exitABRepeatMode() {
        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.NONE;
        abRepeatMinSub = abRepeatMaxSub = 0;
        resetRepeat();
        setABRepeatImage(R.drawable.ic_repeat_ab);
    }

    protected void setABRepeatImage(@DrawableRes int resId) {

    }

    protected void resetRepeat() {
        setRepeat(false);
    }

    protected void setRepeat() {
        setRepeat(true);
    }

    protected void setRepeat(boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        PointF curr = new PointF(event.getX(), event.getY());

        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                stayedWithinClickDistance = true;
                updateValue_swipeTopMovePosition(0);
//                swipeTopMovePosition = 0;
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                //touch is start
//                touchDownX = event.getRawX();
//                touchDownY = event.getRawY();
                touchDownX = event.getX();
                touchDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //finger move to screen
                final float touchMoveX = event.getX();
                final float touchMoveY = event.getY();
                deltaX_MoveDistance = touchDownX - touchMoveX;
                deltaY_MoveDistance = touchDownY - touchMoveY;

                //HORIZONTAL SCROLL
                if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                    if (Math.abs(deltaX_MoveDistance) > Math.abs(deltaY_MoveDistance)) {
                        if (Math.abs(deltaX_MoveDistance) > Constant.PLAYER.SWIPE.DISTANCE_MIN) {
                            touchDownX = event.getX();
                            if (isAreaOfPlayingScreen()) {
                                typeSwipe = Constant.PLAYER.SWIPE.PLAYING_AREA;
                            } else if (!isMenuUIVisible() && isAreaOfBottomEdgeScreen(1)) { //Trying to move prev/next dialog when I swipe bottom of the playing view or subtitle table view's bottom navigation bar
                                typeSwipe = Constant.PLAYER.SWIPE.BOTTOM_EDGE;
                            } else if (!isMenuUIVisible() && isAreaOfTopEdgeScreen(1)) {
                                typeSwipe = Constant.PLAYER.SWIPE.TOP_EDGE;
                            }

                            if ((typeSwipe == Constant.PLAYER.SWIPE.PLAYING_AREA) || (typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)) { // Was if (typeSwipe == Constant.PLAYER.SWIPE.TOP)
//                                if (!isRepeat) {
//                                    pausePlayer();
                                    swipeForwardBackwardCurrentDuration = exoPlayer.getCurrentPosition();
//                                }
                            }
                        }
                    } else if ((Math.abs(deltaY_MoveDistance) > Constant.PLAYER.SWIPE.DISTANCE_MIN) && (Math.abs(deltaY_MoveDistance) > Math.abs(deltaX_MoveDistance))) {
                        //VERTICALLY SCROLLING (Not mean vertical mode, it applies on landscape mode and vertical mode too)
                        if (isAreaOfPlayingScreen()) {
                            setVerticalScreen_LeftOrRight();
                        }
                    }
                } else {
                    try {
                        if ((typeSwipe == Constant.PLAYER.SWIPE.PLAYING_AREA) || (typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)) { // Was if (typeSwipe == Constant.PLAYER.SWIPE.TOP)
                            if (Math.abs(deltaX_MoveDistance) > Math.abs(deltaY_MoveDistance)) {
//                                if (!isRepeat) {
                                    handleSwipeToMovePositionForwardBackward(touchMoveX);
//                                }
                            }
//                        } else if (typeSwipe == Constant.PLAYER.SWIPE.VERTICALLY_LEFT_SCREEN) {
////                            handleSwipeVerticallyOnLeftScreen(touchDownY, touchMoveY); //Dalnim added this. Don't need to use getHistoricalY (In vertical mode it's always smaller than touchMoveY)
//                            handSwipeVerticallyOnRightScreen(touchDownY, touchMoveY, mAudioManager);
//                        } else if (typeSwipe == Constant.PLAYER.SWIPE.VERTICALLY_RIGHT_SCREEN) {
//                            handSwipeVerticallyOnRightScreen(touchDownY, touchMoveY, mAudioManager);
                        }
                    } catch (Exception ex) {
                        DLog.e(getLogTag(), ex.getMessage());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isNotScalingVideo()) {
                    if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                        if (numberOfTaps == 0) {
                            numberOfTaps++;
                            mHandler.postDelayed(() -> {
                                if (numberOfTaps > 1) {
                                    if (isAreaOfTopBottomEdgeScreen()) {
                                        if (!isMenuUIVisible())
                                            handleForwardClick(1);
                                    } else if (isAreaOfPlayingScreen()) {
                                        handleDoubleClickOnPlayingScreen();
                                    } else {
                                        // just in case.
                                        handleDoubleClickOnPlayingScreen();
                                    }
                                } else if (stayedWithinClickDistance) {
                                    if (isAreaOfTopBottomEdgeScreen()) {
                                        if (!isMenuUIVisible())
                                            handleBackwardClick(1);
                                    } else if (isAreaOfPlayingScreen()) {
                                        handleClickOnPlayingScreen();
                                    } else {
                                        // just in case.
                                        handleClickOnPlayingScreen();
                                    }
                                }
                                numberOfTaps = 0;
                            }, ViewConfiguration.getDoubleTapTimeout());
                        } else {
                            numberOfTaps++;
                        }
                    } else {
                        // swipe playing area or subtitle view (Was swipe top and bottom)
                        // when isRepeat is true > not run

                        //Dalnim removed this if statement to apply swiping for all cases. (With this I can't swipe on the subtitle view on full screen if I click CC Repeat or Listen Comprehension 2.
                        if ((typeSwipe == Constant.PLAYER.SWIPE.PLAYING_AREA) || (typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)) { // Was if (typeSwipe == Constant.PLAYER.SWIPE.TOP)
                            activity.runOnUiThread(() -> {
//                                if (isFullscreenMode()) {
                                    displayPositionBackForward(swipeTopMovePosition, swipeTopNewPosition);
                                    if (!Utils.isEmpty(subtitleList)) {
                                        updateValue_SubtitleIndexFromCurrentTime();
                                        getMinMaxTime(subtitleList.get(subtitleIndex));
                                        updateCountSubtitle();
                                    }
//                                } else {
////                                    showSubtitle(exoPlayer.getCurrentPosition(), -1);
//
//                                }
                                playPlayer();
                            });
                        } else if ((typeSwipe == Constant.PLAYER.SWIPE.SUBTITLE_VIEW) || (typeSwipe == Constant.PLAYER.SWIPE.BOTTOM_EDGE)) {
                            handleSwipeToMovePrevNextDialog(deltaX_MoveDistance < 0);
                        }

                    }
                }
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                break;
        }

        return true;
    }

    protected boolean isAreaOfPlayingScreen() {
        boolean result = true;

        result = result && !isAreaOfTopBottomEdgeScreen();
        result = result && isTouchDownXInPlayerViewWidth();
        return result;
    }

    protected boolean isAreaOfTopBottomEdgeScreen() {
        if (isVerticalMode())
            return (isAreaOfTopEdgeScreen(0.5f) || isAreaOfBottomEdgeScreen(0.5f));

        return (isAreaOfTopEdgeScreen(1) || isAreaOfBottomEdgeScreen(1));
    }

    protected boolean isAreaOfBottomEdgeScreen(float ratio) {
        if (isTouchDownXInPlayerViewWidth() && !FileUtil.isMusicApp()) {
            int menuBarHeight = getMenuBarHeight(); //viewBinding.llPlay.llPlayBottomMenu.getHeight() == 0 ? Constant.PLAYER.SWIPE.HEIGHT_OF_EDGE : viewBinding.llPlay.llPlayBottomMenu.getHeight();
            return touchDownY >= playerViewHeight - (menuBarHeight * ratio);
        } else {
            return false;
        }
    }

    protected boolean isAreaOfTopEdgeScreen(float ratio) {
        if (isTouchDownXInPlayerViewWidth() && !FileUtil.isMusicApp()) {
            int menuBarHeight = getMenuBarHeight(); // viewBinding.llPlay.llPlayTopMenu.getHeight() == 0 ? Constant.PLAYER.SWIPE.HEIGHT_OF_EDGE : viewBinding.llPlay.llPlayTopMenu.getHeight();
            return touchDownY <= menuBarHeight * ratio;
        } else {
            return false;
        }
    }

    protected int getMenuBarHeight() {
        return Constant.PLAYER.SWIPE.HEIGHT_OF_EDGE;
    }

    protected boolean isTouchDownXInPlayerViewWidth() {
        boolean blnResult = false;
        if (isFullscreenMode() || isVerticalMode()) {
            blnResult = touchDownX <= playerViewWidth;
        } else {
            if (isRightHandMode) {
                blnResult = touchDownX <= playerViewWidth;
            } else {
//                LinearLayout llListMeaning = viewBinding.layoutPlayerSubtitleTable.llListMeaning;
                int llListMeaningWidth = getListMeaningWidth();
                if (touchDownX > llListMeaningWidth && (touchDownX < (llListMeaningWidth + playerViewWidth))) {
                    blnResult = true;
                } else {
                    blnResult = false;
                }
            }
        }


        return blnResult;
    }

    protected int getListMeaningWidth() {
        return 0;
    }

    protected boolean isVerticalMode() {
        return true;
    }

    protected boolean isFullscreenMode() {
        return false;
    }

    protected boolean isMenuUIVisible() {
        return false;
    }

    protected void setVerticalScreen_LeftOrRight() {
        int halfOfPlayingWidth = playerViewWidth / 2;
        if (!isVerticalMode() && !isFullscreenMode() && !isRightHandMode) {
            if (touchDownX > getListMeaningWidth()) {
                halfOfPlayingWidth = getListMeaningWidth() + (playerViewWidth / 2);
            }
        }

        if (touchDownX < halfOfPlayingWidth) {
            typeSwipe = Constant.PLAYER.SWIPE.VERTICALLY_LEFT_SCREEN;
        } else {
            typeSwipe = Constant.PLAYER.SWIPE.VERTICALLY_RIGHT_SCREEN;
        }
    }

    protected void handleSwipeVerticallyOnLeftScreen(float startY, float endY) {
        if (FileUtil.isMusicApp() || typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)
            return;

        activity.runOnUiThread(() -> {
            updateValue_ShowViewPlayCenter(true);
            int distance = Math.abs(Math.round((deltaY_MoveDistance / playerViewHeight) * Constant.PLAYER.SWIPE.BRIGHTNESS.MAX));
            if (distance > 0) {
                if (endY < startY) {
                    positionBrightness += distance;
                } else if (endY > startY) {
                    positionBrightness -= distance;
                }
                positionBrightness = Utils.checkBrightnessRange(positionBrightness);
                int percent = Utils.changeBrightness(activity, positionBrightness);
                DLog.e(getLogTag(), "handleSwipeLeft - positionBrightness=" + positionBrightness + " - percent=" + percent);
                String valueDisplay = getString(R.string.display_brightness_percent, String.valueOf(percent));
                showCenterMessageView(valueDisplay);
                touchDownY = endY; //need this to adjust the value smoothly (dalnim commented)
            }
        });
    }

    protected void handSwipeVerticallyOnRightScreen(float startY, float endY, AudioManager audioManager) {
        if (FileUtil.isMusicApp() || typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)
            return;

        activity.runOnUiThread(() -> {
            updateValue_ShowViewPlayCenter(true);
            int distance = Math.abs(Math.round((deltaY_MoveDistance / playerViewHeight) * Constant.PLAYER.SWIPE.VOLUME.MAX));
            if (distance > 0) {
                if (endY < startY) {
                    positionVolume += distance;
                } else if (endY > startY) {
                    positionVolume -= distance;
                }
                positionVolume = Utils.checkVolumeRange(positionVolume);
                int value = Math.round((positionVolume * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 100);
                DLog.e(getLogTag(), "handSwipeRight - positionVolume=" + positionVolume + " - value=" + value);
                Utils.changeVolume(audioManager, value);
                String valueDisplay = getString(R.string.display_volume_percent, String.valueOf(positionVolume));
                showCenterMessageView(valueDisplay);
                touchDownY = endY; //need this to adjust the value smoothly (dalnim commented)
            }
        });
    }

    protected void handleSwipeToMovePositionForwardBackward(float touchMoveX) {
        if (playerViewWidth <= 0) return;
        updateValue_ShowViewPlayCenter(true);
        final int width = playerViewWidth - Constant.PLAYER.SWIPE.DISTANCE_MIN;
        float percentMoved = Math.abs(deltaX_MoveDistance) / width;
        float tempDistance = (percentMoved + (Constant.PLAYER.SWIPE.DISTANCE_MIN / width)) * swipeForwardBackwardValue;

        final boolean isTouchDownTopEdgeOfPlayingArea = typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE ? true : false;
        activity.runOnUiThread(() -> {
            if (touchMoveX < touchDownX) {
                updateValue_swipeTopMovePosition((long) tempDistance);
//                swipeTopMovePosition = (long) tempDistance;
            } else if (touchMoveX > touchDownX) {
                updateValue_swipeTopMovePosition((long) -tempDistance);
//                swipeTopMovePosition = (long) -tempDistance;
            }
            if (isTouchDownTopEdgeOfPlayingArea) {
                updateValue_swipeTopMovePosition(swipeTopMovePosition * Constant.PLAYER.SWIPE.MULTIPLIER_TO_MOVE_FORWARD_BACKWARD_QUICKLY);
//                swipeTopMovePosition *= Constant.PLAYER.SWIPE.MULTIPLIER_TO_MOVE_FORWARD_BACKWARD_QUICKLY;
            } else {
                if (swipeTopMovePosition > swipeForwardBackwardValue) {
                    updateValue_swipeTopMovePosition(swipeForwardBackwardValue);
//                    swipeTopMovePosition = swipeForwardBackwardValue;
                } else if (swipeTopMovePosition < -swipeForwardBackwardValue) {
                    updateValue_swipeTopMovePosition(-swipeForwardBackwardValue);
//                    swipeTopMovePosition = -swipeForwardBackwardValue;
                }
            }
            updateValue_swipeTopNewPosition(swipeTopMovePosition + swipeForwardBackwardCurrentDuration);
//            swipeTopNewPosition = swipeTopMovePosition + swipeForwardBackwardCurrentDuration;
            if (swipeTopNewPosition < 0) {
                updateValue_swipeTopNewPosition(0);
//                swipeTopNewPosition = 0;
            } else if (swipeTopNewPosition > activity.playerFileModel.getDuration()) {
                updateValue_swipeTopNewPosition(activity.playerFileModel.getDuration());
//                swipeTopNewPosition = activity.playerFileModel.getDuration();
            }
            displayPositionBackForward(swipeTopMovePosition, swipeTopNewPosition);
        });
    }

    protected void updateValue_swipeTopMovePosition(long value) {
        swipeTopMovePosition = value;
    }

    private void updateValue_swipeTopNewPosition(long value) {
        swipeTopNewPosition = value;
    }


    protected void updateValue_abRepeatMinSub(long value) {
        abRepeatMinSub = value;
    }

    protected void updateValue_abRepeatMaxSub(long value) {
        abRepeatMaxSub = value;
    }

    protected void updateValue_minSub(long value) {
        minSub = value;
    }

    protected void updateValue_maxSub(long value) {
        maxSub = value;
    }

    protected void updateValue_minSubNoExtraTime(long value) {
        minSubNoExtraTime = value;
    }

    protected void updateValue_maxSubNoExtraTime(long value) {
        maxSubNoExtraTime = value;
    }

    protected void updateValue_minSubWithAllExtraTime(long value) {
        minSubWithAllExtraTime = value;
    }

    protected void updateValue_maxSubWithAllExtraTime(long value) {
        maxSubWithAllExtraTime = value;
    }

//    protected void getScreenSize() {
//        getScreenSize(null);
//    }
//
//    protected void getScreenSize(Configuration newConfig) {
//        DLog.d(getLogTag(), "getScreenSize");
////        LinearLayout llListMeaning = viewBinding.layoutPlayerSubtitleTable.llListMeaning;
//        if (newConfig != null) {
//            if (activity.playerFileModel.isShowRuby()) {
//                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//                    getRoot().setOrientation(LinearLayout.HORIZONTAL);
//                    getRoot().setWeightSum(0f);
//                    setPanelWeights(playerWidthPercent);
//                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//                    getRoot().setOrientation(LinearLayout.VERTICAL);
//                    final LinearLayout.LayoutParams playerParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            0, 1.0f);
//                    getLLPlayer().setLayoutParams(playerParam);
//                    final LinearLayout.LayoutParams meaningParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            0, 2.0f);
////                    llListMeaning.setLayoutParams(meaningParam);
//                }
//
//            }
////            viewBinding.layoutPlayerSubtitleTable.rvListMeaning.setLayoutManager(getCurrentLinearLayoutManager());
////            resizePlayerView();
////            showllPlay();
////            updateLayoutSubtitleWhenChangeRotate();
//        }
//        getLLPlayer().postDelayed(() -> {
//            playerViewWidth = getLLPlayer().getWidth();
//            playerViewHeight = getLLPlayer().getHeight();
//            DLog.d(getLogTag(), "getScreenSize - sWidth=" + playerViewWidth + " - sHeight=" + playerViewHeight);
//        }, 500);
//    }

    protected void displayPositionBackForward(long oldPosition, long newPosition) {
        displayPositionBackForward(true, oldPosition, newPosition);
    }

    protected void displayPositionBackForward(boolean isSeekTo, long oldPosition, long newPosition) {
        //Dalnim add : to show the dialog on the real time during I'm swiping top edge of full screen to go forward or backward.
//        if (isFullscreenMode())
//            showSubtitle(exoPlayer.getCurrentPosition(), -1);
        updateValue_ShowViewPlayCenter(true);
        String valueDisplay = getString(R.string.display_forward_backward, (oldPosition < 0 ? "-" : "+"), getDisplayTimes(Math.abs(oldPosition)), getDisplayTimes(newPosition), getDisplayTimes(exoPlayer.getDuration()));
        DLog.d(getLogTag(), "displayPositionBackForward - display=" + valueDisplay);
        showCenterMessageView(valueDisplay, oldPosition < 0 ? R.drawable.ic_exo_player_icon_rewind : R.drawable.ic_exo_player_icon_fastforward);
        getSbPlay().setProgress((int) newPosition);
        if (isSeekTo) {
            seekToInPlayer(newPosition);
//            exoPlayer.seekTo(newPosition);
        }
    }

    protected boolean isNotScalingVideo() {
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
            displayPositionBackForward(moveValueForwardBackward, position);
        });
    }

    protected void handleDoubleClickOnPlayingScreen() {

    }

    protected void handleBackwardClick(int multiplyOfValue) {
        activity.runOnUiThread(() -> {
            int moveValueForwardBackward = tapForwardBackwardValue * multiplyOfValue;
            long position = exoPlayer.getCurrentPosition();
            position -= moveValueForwardBackward;
            if (position < Constant.PLAYER.TIMER.MIN) {
                position = Constant.PLAYER.TIMER.MIN;
            }
            displayPositionBackForward(-moveValueForwardBackward, position);
        });
    }

    protected void handleClickOnPlayingScreen() {

    }

    protected void handleSwipeToMovePrevNextDialog(boolean isLeft) {

    }

    protected void getPlayerWidthPercent() {
        playerWidthPercent = 100;
    }

    protected void setPanelWeights(float percentLeft) {

    }

    protected void updateForwardBackwardValue() {
        tapForwardBackwardValue = sharedPreferences.getPlayerTapForBackWard() * Constant.PLAYER.TIMER.SECOND;
        swipeForwardBackwardValue = sharedPreferences.getPlayerSwipeForBackWard() * Constant.PLAYER.TIMER.SECOND;
    }

    protected void updateValue_SubtitleIndexFromCurrentTime() {
//        ToastUtil.getInstance(activity).show("updateValue_SubtitleIndexFromCurrentTime is called : " + subtitleIndex);
        updateValue_SubtitleIndex(getSubtitleIndexFromCurrentTime());
    }

    protected void updateValue_SubtitleIndex(int value) {
//        ToastUtil.getInstance(activity).show("subtitleIndex, new subtitleIndex, position : " + subtitleIndex + "," + value + ", " + exoPlayer.getCurrentPosition());
        DLog.d("dalnim", "updateValue_SubtitleIndex value : " + value);
        subtitleIndex = value;
    }

    //Dalnim : update logic to use binary search and if it's empty dialog, then return previous dialog and set mIsOnEmptyDialog to FALSE
    //이진 검색, 바이너리 검색, binary search
    //자막의 시작/끝시간에 딜레이 시간을 더한 값으로 검색한다.
    protected int getSubtitleIndexFromCurrentTime() {
        if (Utils.isEmpty(subtitleList)) {
            mIsOnEmptyDialog = true;
            return -1;
        }

        long position = 0;
        try {
            position = exoPlayer.getCurrentPosition();
        } catch (Exception ex) {
            return -1;
        }
        DLog.d("dalnim", "getSubtitleIndexFromCurrentTime position : " + position);
        int subtitleIndexPrevious = -1;
        int subtitleIndexFound = -1;
        int midIndex = 0;
        int firstIndex = 0;
        int lastIndex = subtitleList.size() - 1;
        while (lastIndex >= firstIndex) {
            midIndex = (lastIndex + firstIndex) / 2;
            DicModel dicModel = subtitleList.get(midIndex);
            long startPositionInSubtitleDicModel = getMinTime(dicModel); //Seach surbitlte Index from Playing times doesn't need to use Play Before/After a time
            long endPositionInSubtitleDicModel = getMaxTime(dicModel);
            if ((startPositionInSubtitleDicModel <= position) && (position < endPositionInSubtitleDicModel)) {
                subtitleIndexFound = midIndex;
                break;
            }
            if (position <= startPositionInSubtitleDicModel) {
                lastIndex = midIndex - 1;
                subtitleIndexPrevious = lastIndex;
            } else {
                firstIndex = midIndex + 1;
                subtitleIndexPrevious = midIndex;
            }

            //Dalnim : Don't delete this, I need to dubeg this logic many times.
            if (Utils.isDebug()) {
                boolean isSafe = true;
                if ((firstIndex < 0) || (firstIndex >= subtitleList.size())) {
//                    DLog.d(getLogTag(), "firstIndex is out of bound, firstIndex = " + firstIndex);
                    isSafe = false;
                }
                if ((midIndex < 0) || (midIndex >= subtitleList.size())) {
//                    DLog.d(getLogTag(), "midIndex is out of bound, midIndex = " + midIndex);
                    isSafe = false;
                }
                if ((lastIndex < 0) || (lastIndex >= subtitleList.size())) {
//                    DLog.d(getLogTag(), "lastIndex is out of bound, lastIndex = " + lastIndex);
                    isSafe = false;
                }
                if ((subtitleIndexPrevious < 0) || (subtitleIndexPrevious >= subtitleList.size())) {
//                    DLog.d(getLogTag(), "subtitleIndexPrevious is out of bound, subtitleIndexPrevious = " + subtitleIndexPrevious);
                    isSafe = false;
                }

                if (isSafe) {
                    DicModel dicModel1 = subtitleList.get(firstIndex);
                    DicModel dicModel2 = subtitleList.get(midIndex);
                    DicModel dicModel3 = subtitleList.get(lastIndex);
                    DicModel dicModel4 = subtitleList.get(subtitleIndexPrevious);
                    DicModel dicModel5 = subtitleList.get(subtitleIndexPrevious);
                } else {
//                    DLog.d(getLogTag(), "out of bound bug");
                }
            }
        }

        if (subtitleIndexFound == -1) {
            mIsOnEmptyDialog = true;
            // Dalnim : If it's empty dialog, use subtitleIndexPrevious. And if it's next of last dialog, then use last dialog otherwise it goes first dialog when I swipe to right at the last empty dialog.
            subtitleIndexFound = subtitleIndexPrevious >= (subtitleList.size() - 1) ? subtitleList.size() - 1 : subtitleIndexPrevious;
        } else {
            mIsOnEmptyDialog = false;
        }
        DLog.d("dalnim", "getSubtitleIndexFromCurrentTime subtitleIndexFound : " + subtitleIndexFound);
        return subtitleIndexFound;
    }

    protected void updateCountSubtitle() {

    }

    protected void startOverWhenItReachEndOfMedia() {
        if (FileUtil.isMusicApp()) {
            playEndMusicSoundWhenStartOver();
        } else {
            startOverWhenItReachEndOfMediaMain();
        }

    }

    protected void playMediaFromBeginningOrFirstDialog(boolean isFirstTime) {
        if (activity.isAudioFormat()) {
            playMediaFromBeginningOrFirstDialogMain(isFirstTime);
        } else {
            playVideoFromBeginningOrFirstDialog(isFirstTime);
        }

    }

    private void playVideoFromBeginningOrFirstDialog(boolean isFirstTime) {
        if ((isFirstTime) && (sharedPreferences.getPlayVideoFromWhereYouLeft())) {
            long timeToStart = getArguments().getLong(Constant.PLAYER.INTENT.KEY_VIDEO_TIME_TO_START) > 0 ? getArguments().getLong(Constant.PLAYER.INTENT.KEY_VIDEO_TIME_TO_START) : activity.playerFileModel.getVideoModel().getLastDuration();
            if (timeToStart <= activity.playerFileModel.getDuration()) {
                seekToInPlayer(timeToStart);
            } else {
                playMediaFromBeginningOrFirstDialogMain(isFirstTime);
            }
        } else {
            playMediaFromBeginningOrFirstDialogMain(isFirstTime);
        }
    }

    private void playMediaFromBeginningOrFirstDialogMain(boolean isFirstTime) {
        updateValue_comprehensionRepeatedCount(0); //Without this code, 2nd round in the Listen Comprehension 2's first subtitle is not repeated but just play once.
        updateValue_SubtitleIndex(0);
        DicModel dicModel = getDicModel(subtitleIndex);
        getMinMaxTime(dicModel);
        if (sharedPreferences.getPlayMusicBetweenLyricsOnly()) {
            if (!isFirstTime) {
                subtitleList.forEach(e -> e.setPlayRecordOrTSS(false));
            }
            seekToInPlayer(SubtitleUtil.getStartTimeOfFirstDialogWithBufferTime((long)minSub));
        } else {
            seekToInPlayer(0);
        }
        Loading.hide();
    }

    protected void updateValue_comprehensionRepeatedCount(int value) {
        comprehensionRepeatedCount = value;
    }

    protected boolean isPlayBetweenLyricsOnlyAndOverLastLyric(long position) {
        boolean result = false;
        //To play from the first subtitle to last subtitle. Or play from beginning to last in the media. 첫 자막부터 마지막 자막까지만 플레이 할지... 전체 다 플레이 할지
        if (sharedPreferences.getPlayMusicBetweenLyricsOnly() && isHasSubtitle() && (activity.playerFileModel.getDuration() > 0) ) {
            DicModel dicModel = subtitleList.get(subtitleList.size() -1);
            if (dicModel != null) {
                long endTimeWithBuffer = SubtitleUtil.getEndTimeOfFirstDialogWithBufferTime(dicModel.getEndTime() + activity.playerFileModel.getVideoModel().getDelaySubtitles(), activity.playerFileModel.getDuration());
                if (position > endTimeWithBuffer)
                    result = true;
            }
        }
        return result;
    }



    protected void beginABRepeatWhenItReachEndOfVideo() {
        enterABRepeatModeAB_B(true);
        seekToInPlayer((long) minSub);
    }

    //Dalnim add
    protected void onInsertABRepeat(String studyLanguageContent, String tongueLanguageContent) {
//        DicModel dicModel = subtitleList.get(subtitleIndex);
        long minSubTemp = getMinSubByMode();
        long maxSubTemp = getMaxSubByMode();
        long startTimeNew = minSubTemp;
        long endTimeNew = maxSubTemp;

        if (startTimeNew < 0)
            startTimeNew = 0;

        if (endTimeNew > exoPlayer.getDuration())
            endTimeNew = exoPlayer.getDuration();

        DicModel newDicModel = new DicModel();// gson.fromJson(gson.toJson(dicModel), DicModel.class);
        setNewIDForNewDicModel(newDicModel);
        newDicModel.setVocaDisplay(studyLanguageContent);
        newDicModel.setVocaDisplayRuby(studyLanguageContent);
        newDicModel.setSubtitleOriginal(studyLanguageContent);
        newDicModel.setMeaning(tongueLanguageContent);
        newDicModel.setStartTime(startTimeNew);
        newDicModel.setStartTimeOriginal(startTimeNew);
        newDicModel.setEndTime(endTimeNew);
        newDicModel.setEndTimeOriginal(endTimeNew);
        newDicModel.setMemo("");

        activity.getSubDatabase().addSubtitle(newDicModel);
        subtitleIndex++;
        subtitleIndex = Voca.getDefaultIndexIfOutOfIndex(subtitleList, subtitleIndex);
        subtitleList.add(subtitleIndex, newDicModel);
        subtitleListTotal.add(newDicModel);
        Collections.sort(subtitleList, (p1, p2) -> (int) (p1.getStartTime() - p2.getStartTime()));
        Collections.sort(subtitleListTotal, (p1, p2) -> (int) (p1.getStartTime() - p2.getStartTime()));

        updateCountSubtitle();
        ToastUtil.getInstance(activity).show(R.string.msg_inserted);
    }

    protected INSERT_SUBTITLE isAbleToInsertNewSubtitle() {
        long minSubTemp = getMinSubByMode();
        long maxSubTemp = getMaxSubByMode();

        long startTimeNew = minSubTemp < 0 ? 0 : minSubTemp;
        long endTimeNew = maxSubTemp > exoPlayer.getDuration() ? exoPlayer.getDuration() : maxSubTemp;
        INSERT_SUBTITLE insertSubtitle = INSERT_SUBTITLE.YES;
        if (Utils.isEmpty(subtitleList))
            return insertSubtitle;

        for (DicModel dicModel : subtitleList) {
            if (isASubtitleBeforeNewSubtitle(startTimeNew, dicModel))
                continue;

            if (isASubtitleContainNewSubtitleComplelety(startTimeNew, endTimeNew, dicModel)) {
                insertSubtitle = INSERT_SUBTITLE.NO;
                break;
            }

            if (isNewSubtitleContainASubtitleComplelety(startTimeNew, endTimeNew, dicModel)) {
                insertSubtitle = INSERT_SUBTITLE.NO;
                break;
            }

            if (isASubtitleOverlapStartTimeOfNewSubtitle(startTimeNew, endTimeNew, dicModel)) {
                insertSubtitle = INSERT_SUBTITLE.YES_BUT_OVERLAP_START_TIME;
            }

            if (isASubtitleOverlapEndTimeOfNewSubtitle(startTimeNew, endTimeNew, dicModel)) {
                insertSubtitle = INSERT_SUBTITLE.YES_BUT_OVERLAP_END_TIME;
            }

//            if (isASubtitleAfterNewSubtitle(startTimeNew, dicModel))
                break;
        }

        return insertSubtitle;
    }

    private boolean isASubtitleOverlapEndTimeOfNewSubtitle(long startTimeNew, long endTimeNew, DicModel dicModel) {
        return (dicModel.getStartTime() <= endTimeNew) && (endTimeNew <= dicModel.getEndTime());
    }

    private boolean isASubtitleOverlapStartTimeOfNewSubtitle(long startTimeNew, long endTimeNew, DicModel dicModel) {
        return (dicModel.getStartTime() <= startTimeNew) && (startTimeNew <= dicModel.getEndTime());
    }

    private boolean isASubtitleBeforeNewSubtitle(long startTimeNew, DicModel dicModel) {
        return (dicModel.getStartTime() < startTimeNew) && (dicModel.getEndTime() <= startTimeNew);
    }

    private boolean isASubtitleAfterNewSubtitle(long endTimeNew, DicModel dicModel) {
        return (endTimeNew <= dicModel.getStartTime()) && (endTimeNew <= dicModel.getEndTime());
    }


    private boolean isASubtitleContainNewSubtitleComplelety(long startTimeNew, long endTimeNew, DicModel dicModel) {
        return (dicModel.getStartTime() <= startTimeNew) && (endTimeNew <= dicModel.getEndTime());
    }

    private boolean isNewSubtitleContainASubtitleComplelety(long startTimeNew, long endTimeNew, DicModel dicModel) {
        return (startTimeNew <= dicModel.getStartTime()) && (dicModel.getEndTime() <= endTimeNew);
    }



    //Dalnim add
    protected void setNewIDForNewDicModel(DicModel newDicModel) {
        newDicModel.setVocaType(Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE);
        newDicModel.setVocaTypeBase(Constant.API_VALUE.VALUE_VOCA_TYPE_SUBTITLE);
        newDicModel.setId(activity.getSubDatabase().getNewIDInSubtitleTable());
        newDicModel.setVocaId(activity.getSubDatabase().getNewVocaIDInSubtitleTable());
        newDicModel.setLangStudy(EnumLanguage.findByFormatApi(Constant.API.STUDY_LANG).getIdApi());
//        dicModelNew.setVocaIdServer(getNewIDInSubtitleTable(Constant.PLAYER.SQL.COLUMN.VOCA_ID_TO_SEND_SERVER));
    }

    //Dalnim add
    protected void updateSubtitleInDB(DicModel dicModel) {
//        String subtitle = dicModel.getVocaDisplay();
//        DicModel dicModel1 = subtitleList.get(subtitleIndex);
//        dicModel1.setVocaDisplay(subtitle);
//        dicModel1.setVocaDisplayRuby(subtitle);
//        dicModel1.setMeaning(dicModel.getMeaning());
        activity.getSubDatabase().updateMultipleWordMeaning(dicModel);

        ToastUtil.getInstance(activity).show(R.string.saved);
    }

    protected void setAnalyzeAgain() {
        activity.playerFileModel.getVideoModel().setAnalyzeAgain(Constant.INT_BOOLEAN.TRUE);
        activity.updateVideoModel(activity.playerFileModel.getVideoModel());
    }

    protected void initMediaPlayerEndMusicSound() {
        try {
            Uri uri = Uri.fromFile(endMusicSoundFile);
            mediaPlayerEndMusicSound = new MediaPlayer();
            mediaPlayerEndMusicSound.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayerEndMusicSound.setDataSource(getContext(), uri);
            mediaPlayerEndMusicSound.prepare();
            mediaPlayerEndMusicSound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
//                    isPlayingEndMusicSound = false;
                    if (activity.isAudioFormat() && isPlayTitleByTtsBeforePlaying()) {
                        //TODO : If I use textToSpeechToSpeakMediaTitle.speak, it repeats endlessly.
                        String ttsTitleToIntroduceBeforeRepeatingMusic = getTtsTitleToIntroduceBeforeRepeatingMusic();
                        textToSpeechToSpeakMediaTitle.speak(ttsTitleToIntroduceBeforeRepeatingMusic, TextToSpeech.QUEUE_FLUSH, null, ttsTitleToIntroduceBeforeRepeatingMusic);
//                        startOverWhenItReachEndOfMediaMain();
                    } else {
                        startOverWhenItReachEndOfMediaMain();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            startOverWhenItReachEndOfMediaMain();
        }
    }

    protected void playEndMusicSoundWhenStartOver() {
        isPlayingEndMusicSound = true;
        try {
            if (mediaPlayerEndMusicSound == null) {
                startOverWhenItReachEndOfMediaMain();
            } else {
//                pausePlayer();
                mediaPlayerEndMusicSound.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
            isPlayingEndMusicSound = false;
            startOverWhenItReachEndOfMediaMain();
        }
    }

    protected void startOverWhenItReachEndOfMediaMain() {
        activity.runOnUiThread(() -> {
            isPlayingEndMusicSound = false;
            playPlayer();
            if (!application.isBackground()) {
                ToastUtil.getInstance(activity).show(R.string.msg_play_again_started_over);
            }
            playMediaFromBeginningOrFirstDialog(false);
        });
    }


    private String getTtsTitleToIntroduceBeforeRepeatingMusic() {
        String title = StringUtils.covertStringToNFC(activity.playerFileModel.getVideoModel().getTitleTts());
        String artist = StringUtils.covertStringToNFC(activity.playerFileModel.getVideoModel().getArtistTts());
        String result = getString(R.string.msg_introduce_media_title_before_repeating_playing, title);
        if (!Utils.isEmpty(artist))
            result = getString(R.string.msg_introduce_media_title_and_artist_before_repeating_playing, artist, title);
        return result;
    }

    protected boolean isDisplayStartEndTimeInSubtitleView() {
        return sharedPreferences.getDisplayStartEndTimeInSubtitleView();
    }

    protected boolean isPlayTitleByTtsBeforePlaying() {
        return sharedPreferences.getPlayTitleByTtsBeforePlaying();
    }

    //For Debugging only, Don't use this
    protected String getCurrentSubtitleContent() {
        Optional<DicModel> dicModel = Optional.ofNullable(getDicModel(subtitleIndex));
        return dicModel.map(DicModel::getVocaDisplay).orElse("");
    }
}
