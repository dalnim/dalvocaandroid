package com.dalread.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.BuildConfig;
import com.dalread.DalFlavor;
import com.dalread.R;
import com.dalread.adapter.ScrollableMenuAdapter;
import com.dalread.adapter.StudyChatAdapter;
import com.dalread.adapter.SubtitleDialogAdapter;
import com.dalread.base.BasePlayerVideoFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.TextViewOutline;
import com.dalread.component.VariableScrollSpeedLinearLayoutManager;
import com.dalread.component.VariableScrollSpeedTopLinearLayoutManager;
import com.dalread.database.BookmarkPlayerModelQuery;
import com.dalread.database.ListenComprehensionQuery;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.SubModelQuery;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.database.sqlite.model.SubtitleModel;
import com.dalread.databinding.FragmentPlayerBinding;
import com.dalread.databinding.LayoutPlayerPlayBinding;
import com.dalread.databinding.LayoutPlayerPlayScrollableMenuBinding;
import com.dalread.databinding.LayoutPlayerPlaySubBinding;
import com.dalread.databinding.LayoutPlayerSubtitleTableBinding;
import com.dalread.databinding.LayoutShadowingBinding;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.CopySubtitleByLanguageDialog;
import com.dalread.dialog.EditSubtitleDialog;
import com.dalread.dialog.PlayerCCRepeatTuningScreenDialog;
import com.dalread.dialog.PlayerListenComprehension1Dialog;
import com.dalread.dialog.PlayerListenComprehensionOptionDialog;
import com.dalread.dialog.PlayerOptionDialog;
import com.dalread.dialog.PlayerSettingDialog;
import com.dalread.dialog.PlayerShowMenuDialog;
import com.dalread.dialog.PlayerShowPlayerThemeDialog;
import com.dalread.dialog.PlayerShowSubtitleDialog;
import com.dalread.dialog.PlayerShowSubtitleGroupDialogCaption;
import com.dalread.dialog.PlayerShowSubtitleMenuDialog;
import com.dalread.dialog.RecyclerViewDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.dialog.SingleChoiceWithMessageDialog;
import com.dalread.dialog.TrackSelectionDialog;
import com.dalread.dialog.VocaStudyChatDialog;
import com.dalread.dialog.WebDictionaryDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.helper.DoubleClickHelper;
import com.dalread.helper.SubtitleGroupHelper;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.LongTouchIntervalListener;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.listener.OnScrollableMenuClickListener;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.AmkiItem;
import com.dalread.model.BookmarkPlayerModel;
import com.dalread.model.EditVoca;
import com.dalread.model.ListenComprehensionModel;
import com.dalread.model.MinMaxSubModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.RubyTextModel;
import com.dalread.model.ScrollableMenuModel;
import com.dalread.model.SubModel;
import com.dalread.model.SubtitleHideModel;
import com.dalread.model.SubtitleLanguageModel;
import com.dalread.model.TrackSelection;
import com.dalread.model.VideoModel;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.model.VocaTypeId;
import com.dalread.model.WebDictionaryModel;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.service.PlayerService;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.BaseCollectionUtil;
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.ChatGptWebUtil;
import com.dalread.util.CollectionUtil;
import com.dalread.util.Constant;
import com.dalread.util.CopyTextUtil;
import com.dalread.util.CustomTranslate;
import com.dalread.util.DLog;
import com.dalread.util.DialogUtil;
import com.dalread.util.FileUtil;
import com.dalread.util.FuriganaUtil;
import com.dalread.util.Loading;
import com.dalread.util.LyricUtils;
import com.dalread.util.MergeUtil;
import com.dalread.util.NetworkUtil;
import com.dalread.util.PermissionUtils;
import com.dalread.util.RepeatUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.SubtitlePositionTimeUtil;
import com.dalread.util.SubtitleUtil;
import com.dalread.util.TimeUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.TranslateUtil;
import com.dalread.util.UtilImage;
import com.dalread.util.Utils;
import com.dalread.util.VideoUtil;
import com.dalread.util.ViewUtil;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.apache.commons.lang3.math.NumberUtils;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import butterknife.ButterKnife;
import io.github.hyuwah.draggableviewlib.DraggableListener;
import okhttp3.ResponseBody;


public class PlayerFragment extends BasePlayerVideoFragment<FragmentPlayerBinding> implements View.OnClickListener, View.OnTouchListener, OnAsyncTaskListenerWithType, DraggableListener, ScaleGestureDetector.OnScaleGestureListener {
    private int changeProgressRepeatCount = 0;
    private int llListMeaningWidth, llListMeaningHeight;
    private float rubyY;
    private int positionBrightnessBeforeSleeping = 0;
    private float distanceCovered = 0;

    private boolean isVideoPlaying = true;
    private boolean isLockAll = false;
    // bookmark
    private boolean isBookmark;
    private List<BookmarkPlayerModel> bookmarkPlayerModels;
    private List<SubtitleLanguageModel> subtitleLanguageModels;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_UPDATE_WORD_VOCA_KNOW = TYPE_INIT_DATA + 1;
    private final int TYPE_REPEAT_UPDATE = TYPE_UPDATE_WORD_VOCA_KNOW + 1;
    private final int TYPE_GET_DIALOG_VERTICAL = TYPE_REPEAT_UPDATE + 1;
    private final int TYPE_UPDATE_REPEAT_FROM_ITEM_DIALOG = TYPE_GET_DIALOG_VERTICAL + 1;
    private final int TYPE_REPEAT_RESET_CHECKED = TYPE_UPDATE_REPEAT_FROM_ITEM_DIALOG + 1;
    private final int TYPE_GET_DATA_PLAY_VOICE = TYPE_REPEAT_RESET_CHECKED + 1;
    private final int TYPE_GET_ALL_DIFFICULT_WORD_AT_ONCE_SUBTITLE_LIST_TO_PLAY_VOICE = TYPE_GET_DATA_PLAY_VOICE + 1;
    private final int TYPE_GET_ALL_DIFFCULT_WORD_TO_PLAY_VOICE = TYPE_GET_ALL_DIFFICULT_WORD_AT_ONCE_SUBTITLE_LIST_TO_PLAY_VOICE + 1;
    private final int TYPE_RELOAD_SUBTITLE_LIST_FROM_DB = TYPE_GET_ALL_DIFFCULT_WORD_TO_PLAY_VOICE + 1;
    private final int TYPE_RELOAD_SUBTITLE_LIST_TOTAL_FROM_DB = TYPE_RELOAD_SUBTITLE_LIST_FROM_DB + 1;
    private final int TYPE_CLEAR_VIDEO_CACHE = TYPE_RELOAD_SUBTITLE_LIST_TOTAL_FROM_DB + 1;
    private final int TYPE_SYNC_DATA_BEFORE_EXIT = TYPE_CLEAR_VIDEO_CACHE + 1;
    private final int TYPE_UPDATE_FROM_EDIT_SUBTITLE = TYPE_SYNC_DATA_BEFORE_EXIT + 1;
    private final int TYPE_ANALYZE_NEW_LYRIC_FILE = TYPE_UPDATE_FROM_EDIT_SUBTITLE + 1;
    private final int TYPE_SEARCHED_SUBTITLE = TYPE_ANALYZE_NEW_LYRIC_FILE + 1;

    private final int DRAG_ZOOM_MODE_NONE = 0;
    private final int DRAG_ZOOM_MODE_DRAG = 1;
    private final int DRAG_ZOOM_MODE_ZOOM = 2;

    private RegisterVocaDialog registerVocaDialog;
    private SubtitleDialogAdapter subtitleDialogAdapter;
    private PlayerShowSubtitleGroupDialogCaption subtitleDialogCaption;
    private SingleChoiceDialog singleChoiceDialog;
    private SingleChoiceWithMessageDialog singleChoiceWithMessageDialog;
    private int repeatCountRepetition = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.DEAFULT_VALUE;
    private PlayerShowSubtitleDialog subTitleDialog; //Why there are 2 places to init this?
    private PlayerOptionDialog playerOptionDialog;
    private PlayerSettingDialog playerSettingDialog;
//    private PlayerListenComprehensionOptionDialog playerListenComprehensionOptionDialog;

    private PlayerListenComprehension1Dialog playerListenComprehension1Dialog;
    private List<Integer> repeatList = new ArrayList<>();
    // search view
    private boolean isSearchTitle = true;
    private boolean isSearchMeaning = true;
    private String searchValue;
    private int showAsteriskBeforeAtNormalPlaying; //Dalnim : to show normal playing's previous status of showAsterisk when repeating is done. (no repeation)
    private int showAsteriskBefore; //Dalnim : to show previous status of showAsterisk when repeating is done. (in Listen comprehension 1/2, I can run CC Repeat mode)
    private int showAsterisk;// = Constant.SHOW_ASTERISK_OFF;
    private LinearLayoutManager layoutManager;

    // repeat count
    private int repeatCount = 0;
    private int repeatIndex = 0;

    private List<ListenComprehensionModel> listenComprehension1ModelList; //For Listen Comprehension 1, 자막 표시 방법 및 순서를 담아둠 (자막 숨기기, 자막 보이기등).
    private int listComprehensionCurrentIndex = 0; //듣기 연습 1에서 같은 자막을 다시 그리지 않을려고. listComprehensionModelIndex와 같은 값이면 다시 자막을 안그린다.
    private int listComprehensionModelIndex = 0; //자막 표시 방법 및 순서의 인덱스
    private int listComprehension1PlaySubtitlesAtOnce; // 듣기 연습 1 옵션의 한번에 반복할 자막수 (예, 3) 자막 그룹이 됨.
    private int listComprehension1PlaySubtitlesAtOnceCount = 1; //"한번에 반복할 자막수"그룹을 현재 몇개째인지.(예, 5이면, 3개 자막씩 반복해서 현재 5번째 자막그룹(3개반복)이란 뜻), "한번에 반복할 자막수"그룹내의 현재 플레이중인 인덱스는 comprehensionRepeatedCount가 나타낸다.

    private int ccRepeatingCount = 0;

    private List<Integer> listComprehension1SubtitleList = new ArrayList<>(); //듣기 연습시 자막들의 ID를 담아둠. (듣기 연습을 시작한 자막부터 담음)
    private int comprehension1SubtitleStartIndex = 0; //이건 listComprehension1SubtitleList의 첫번째 index에 해당되는 실제 subtitleIndex의 값이다. 이건 듣기연습1을 시작하면 listComprehension1SubtitleList를 다시 반복할때 사용할려고 하는것이다. 이것이 없으면 듣기연습1을 중간부터 해도 반복시 맨처음 자막으로 가게 된다.
    private int comprehension1SubtitleIndexInList = 0; //listComprehension1SubtitleList에서의 index, 최초에는 0으로 시작해서 증가한다., 이건 listComprehension1SubtitleList의 값에서 계속 증가됨.
    private int comprehension1SubtitleIndexInListFirstIndex = 0; //listComprehension1SubtitleList에서의 index 최초에는 0으로 시작해서 증가한다., 이건 자막 그룹내에서 맨처음 인덱스가 유지됨.
    private int repeatCountOfListen1 = 1; //듣기연습1을 몇번째 반복하고 있는지? 전체 듣기 연습1이 반복될때 마다 횟수를 알려주기 위해서 사용한다. "반복 연습할 자막 그룹 갯수"
    private int comprehension1PlaySubtitlesPlayPartsCount = -1;// listComprehension1SubtitleList을 만들때, sharedPreferences.getPlayerListenComprehensionPlaySubtitlesPlayParts에서 값을 가져오는데, 듣기연습중에 일부만 반복연습할려고 할때 사용  -1이면 이값을 사용안한다.
    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;
    // https://github.com/dalnim/IssueOnly/issues/141
    private boolean isDataChangeFromPhraseInfor;
    private int currentRotation = Surface.ROTATION_90;
    private int deviceRotation = Surface.ROTATION_270;
    private int prevRotation = currentRotation;
    private boolean isStudyLang, isTongueLang, isHasSubtitleBothLang; //Dalnim , now we always have both, so need to check it has real data.

    private CountDownTimer countDownTimer;
    // changed from edit subtitle screen
    private boolean isSubtitleEdited;
    private String idsFromEditSubtitle;

    //Dalnim add
    private boolean isSkipPlayingNoSubtitlePart = false; //To skip the part when there are no subtitles. Play only the subtitle's part
    private boolean mIsHideKnownDialogDuringPlaying; //Dalnim added : to hide Known dialog when it's playing and show them when it's paused.
    private boolean mIsOnDelaySubtitle = false; //자막 시간 전체적으로 조절할때.. Dalnim Add : Indicate I'm on Delay Subtitle or not
    private int mMinMaxDelaySubtitle = Constant.PLAYER.DELAY_SUBTITLE.MAX_TIME; //Dalnim
    private int mMinMaxDelaySubtitleIndex = Constant.PLAYER.DELAY_SUBTITLE.DEFAULT_MIN_MAX_INDEX; //Dalnim
    private float playerSubtitleFontSize, playerSubtitleFullScreenFontSize; //Dalnim
    private boolean mIsDisplaySubtitleLangStudyFirst; //Dalnim
    private int mDisplaySubtitleLang; //Dalnim : to display both lang, study lang, or meaning only
//    private SubtitleHideModel subtitleHideModel;
    private boolean isShowSubtitleAlways; //Dalnim : this is using in the showSubtitleAlways only. But need to merge with mIsAlawysShowSubtitle later.
    private boolean mIsAlawysShowSubtitle; //Dalnim : in Delay Subtitle or CC Repeat Range bar is visible
    private int screen_resize_mode;
//    private boolean isRightHandMode = true;
    private boolean isGoingToSleep = false;
//    private boolean isSleeping = false;
    private boolean isReloadAllSubtitleAgain; // 현재 표시되고 있는 subtitleList의 내용만 DB에서 다시 읽는다.
    private boolean isReloadAllSubtitleTotalAgain; //이건 맨처음 처럼 subtitleListTotal부터 다시 읽는다.(자막 지우고 나면 subtitleListTotal이 달라진다) 현재 선택된 subtitleList가 subtitleListTotal과 같다.
    // For zooming
    private float savedVideoScaleRatio = 1f;
    private Matrix matrixVideoScale = new Matrix();
    private float[] mVideoScale = new float[9];
    private PointF touchDownLast = new PointF(); //TODO : Need to merge with touchDownX, touchDownY and move this to super class
    private PointF touchDownStart = new PointF();
    private float videoScalePositionRight, videoScalePositionBottom;
    private ScaleGestureDetector mScaleDetector;
    private boolean canScaleVideoScreen = false;
    private final float videoScaleDefaultRatioToDisplayText = 1.01f;
    private final float videoScaleDefaultRatio = 1f;
    private final float videoScaleMinRatio = 1f;
    private final float videoScaleMaxRatio = 10f;
    private boolean isZoomOut = false;
    private int dragZoomMode = DRAG_ZOOM_MODE_NONE;
    private YesNoDialog confirmCancelSleepingYesNoDialog;
    // https://github.com/dalnim/IssueOnly/issues/185#issuecomment-900759135
    // Update data from Phrase Information view
    private IVocaBasicItem vocaBasicItemFromPhraseInformation;
    private final int numberOfScrollableMenuDisplayForLandScapeWhenCollapse = 3;
    private final int numberOfScrollableMenuDisplayForVerticalWhenCollapse = 1;
    private boolean isOpenFirstTime = true;
    private boolean isExpandedScrollableMenu = true;
    private ScrollableMenuAdapter scrollableMenuAdapter;

    private int screenWidth;
    private float minimumPanelWidthPercent;
//    private float playerWidthPercent;
    private boolean isEnableChangeTableWidth;
    private ScrollableMenuModel menuEmbeddedTracks;
    private int menuEmbeddedTracksPosition = 0;
    private boolean isShadowing = false;
    private boolean isRecordingOrListeningInShadowing = false;
    private CountDownTimer shadowingCountDownTimer;
    private MediaPlayer mediaPlayer;
    private MediaRecorder mediaRecorder;
    private List<DicModel> listenRecordedList = new ArrayList<>();
    private int listenRecordedSubtitleIndex = -1;
    private float currentVolume;
    private boolean isListeningRecorded;
    private boolean wasABRepeatModeBeforeEditView;
    private boolean needToRestoreMinMaxSubAfterEditSubtitle;
    private float minSubCCRepeatModeBeforeEditView, maxSubCCRepeatModeBeforeEditView; //To restore after Edit view.
    public float minSubNoExtraTimeCCRepeatModeBeforeEditView, maxSubNoExtraTimeCCRepeatModeBeforeEditView;
    public float minSubWithAllExtraTimeCCRepeatModeBeforeEditView, maxSubWithAllExtraTimeCCRepeatModeBeforeEditView;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private boolean isPlayingSTT;

    private final int REQUEST_CODE_RECORD_AUDIO_TYPE_SHADOWING = 1001;
    private final int REQUEST_CODE_RECORD_AUDIO_TYPE_STT = 1002;
    private EditSubtitleDialog editSubtitleDialog;
    private boolean isInitViewFinished;

    private List<PlayerFileModel> musicFilePlayList = new ArrayList<>();
    private boolean isFinishing = false;
    private boolean isSettingNextFile = false; //AraMusic에서 사용. 재생할 플레이 리스트에서 다음 파일이 있을때? getFileFromPlaylistIndexAndPlay
    private boolean isAnalyzingLyric = false;
    private int previousPlaylistIndex = -1;

    // For zooming subtitle to increase/decrease font size
    private final static long DELAY_FINISH_ZOOMING_SUBTITLE = 300L;
    private int mSubtitleBaseDist;
    private boolean isZoomingSubtitle = false;
    private long lastTimeZoomingSubtitle;

    private View blackOverlay;
//    private static final int OPEN_SUBTITLE_GROUP_ACTIVITY = 1;

    //여기서는 이걸 하면 안된다. inflateViewBinding에서 binding을 부모 클래스로 리턴한다.getContentView도 추상클래스라서 그냥 놔둔다. 불리지 않는다.
//    private FragmentPlayerBinding binding;

    //이것도 inflateViewBinding()가 불려서 아래는 필요없는데, getContentView도 추상클래스라서 그냥 놔둔다. 불리지 않는다.
    @Override
    protected View getContentView() {
        binding = FragmentPlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    protected LinearLayout getRoot() {
        return binding.root;
    }

    @Override
    protected View getLLPlayer() {
        return binding.llPlayer;
    }

    @Override
    protected StyledPlayerView getPlayerView() {
        return binding.playerView;
    }

    @Override
    protected View getLLCenter() {
        return binding.llCenter;
    }

    @Override
    protected ImageView getIvCenterPlay() {
        return binding.ivCenterPlay;
    }

    @Override
    protected TextView getTvCenterText() {
        return binding.tvCenterText;
    }

    @Override
    protected View getLLRepeat() {
        return binding.llRepeat.root;
    }

    @Override
    protected Button getBtnCCRepeatReset() {
        return binding.llRepeat.btnCCRepeatReset;
    }

    @Override
    protected RangeSeekBar getSbRepeatRange() {
        return binding.llRepeat.sbRepeatRange;
    }

    @Override
    protected LinearLayout getLLRepeatRange() {
        return binding.llRepeat.llRepeatRange;
    }

    @Override
    protected TextView getTvRepeatMin() {
        return binding.llRepeat.tvRepeatMin;
    }

    @Override
    protected TextView getTvRepeatMax() {
        return binding.llRepeat.tvRepeatMax;
    }

    @Override
    protected View getLLPlay() {
        return binding.llPlay.root;
    }

    @Override
    protected RangeSeekBar getSbPlay() {
        if (FileUtil.isMusicApp()) {
            return binding.sbPlay;
        } else {
            return binding.llPlay.sbPlay;
        }
    }

    @Override
    protected TextView getTvVideoStartTime() {
        if (FileUtil.isMusicApp()) {
            return binding.tvVideoStartTime;
        } else {
            return binding.llPlay.tvVideoStartTime;
        }
    }

    @Override
    protected TextView getTvVideoEndTime() {
        if (FileUtil.isMusicApp()) {
            return binding.tvVideoEndTime;
        } else {
            return binding.llPlay.tvVideoEndTime;
        }
    }

    private TextViewOutline getTvTitle() {
        if (FileUtil.isMusicApp()) {
            return binding.tvMediaTitle;
        } else {
            return binding.llPlay.tvMediaTitle;
        }
    }

    private AppCompatSeekBar getSBPlayer() {
        if (FileUtil.isMusicApp()) {
            return binding.sbPlayer;
        } else {
            return binding.llPlay.sbPlayer;
        }
    }

    @Override
    protected FragmentPlayerBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPlayerBinding.inflate(inflater, container, false);
    }

    @Override
    protected void onPlayerVideoPositionListener(long position) {
        updateSeekBarPlay(position);
        repeatSubtitle(position);
    }

    @Override
    protected void onPlayerVideoAutoSaveTime(DicModel data) {

    }

    @Override
    protected void onPlayerVideoPlayRangeChanged(float leftValue) {

    }

    @Override
    protected void onPlayerVideoPlayRangeChangedStopTracking() {
        if (isDisplaySubtitle() && isHasSubtitle()) {
//            if (includeMotherTongueSubtitle || playDifficultWordsBeforePlayingSubtitle) {
//                resetPlayingDifficultWord();
//            }
            showSubtitle(exoPlayer.getCurrentPosition(), -1);
            DLog.d(getLogTag(), "onStopTrackingTouch - subPosition=" + subtitleIndex);
//            if (subtitleIndex >= 0) {
            if (BaseCollectionUtil.isSubtitleIndexInsideList(subtitleList, subtitleIndex)) {
                repeatIndex = subtitleIndex;
                DicModel dicModel = getDicModel(subtitleIndex);
                getMinMaxTime(dicModel);
                repeatCount = 0;
                //Dalnim added this to scroll the dialog to table's center when I adjust the range bar's time.
                if (!isFullscreenMode())
                    scrollToPositionSubtitleDialog();
            }
        }
    }

    protected void onPlayerVideoUpdateRangeSeekProgress() {
        // check and update ui repeat tuning dialogue
        //Why need this?
//        if (isShowButtonsOnFullScreen())
//            updateVisibilityButtonsOnFullScreen();
        onUpdateRepeatTuningDialogue();
    }

    private void onUpdateRepeatTuningDialogue() {
//        if (!isVisibleCCRepeatTuning())
//            return;

        if (!isVisiblell_repeat())
            return;

        DicModel currentDicModel = getDicModel(subtitleIndex);
        if (currentDicModel == null)
            return;

        getLLRepeatRange().post(() -> {
            View vCCRepeatTuningCurrentDialogue = binding.llRepeat.vCcRepeatTuningCurrentDialogue;
            int divideNumber = 3;
            final int prevNextSubtitleWidth = getLLRepeatRange().getWidth() / divideNumber;
            if (prevNextSubtitleWidth <= 0) return;
            DLog.d(getLogTag(), "onUpdateRepeatTuningDialogue width=" + prevNextSubtitleWidth);
            vCCRepeatTuningCurrentDialogue.getLayoutParams().width = prevNextSubtitleWidth;
            vCCRepeatTuningCurrentDialogue.requestLayout();

            DicModel prevDicModel = null;
            if (subtitleIndex > 0) {
                prevDicModel = getDicModel(subtitleIndex - 1);
            }
//            DicModel currentDicModel = getDicModel(subtitleIndex);
            DicModel nextDicModel = null;
            if (subtitleIndex < subtitleList.size() - 1) {
                nextDicModel = getDicModel(subtitleIndex + 1);
            }

            View vCCRepeatTuningPreviousDialogue = binding.llRepeat.vCcRepeatTuningPreviousDialogue;
            if (prevDicModel != null) {
                Voca.updateBackgroundCCRepeatTuningScreen(vCCRepeatTuningPreviousDialogue, prevNextSubtitleWidth, prevDicModel.getEndTime() - currentDicModel.getStartTime());
                vCCRepeatTuningPreviousDialogue.setVisibility(View.VISIBLE);
            } else {
                vCCRepeatTuningPreviousDialogue.setVisibility(View.INVISIBLE);
            }

            View vCCRepeatTuningNextDialogue = binding.llRepeat.vCcRepeatTuningNextDialogue;
            if (nextDicModel != null) {
                Voca.updateBackgroundCCRepeatTuningScreen(vCCRepeatTuningNextDialogue, prevNextSubtitleWidth, currentDicModel.getEndTime() - nextDicModel.getStartTime());
                vCCRepeatTuningNextDialogue.setVisibility(View.VISIBLE);
            } else {
                vCCRepeatTuningNextDialogue.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onPlayerVideoPlayClick() {
        if (isVideoPlaying) {
            activity.playTTS.stop();
        }
//        if (isPlayingDifficultWordAndMotherTongueSubtitle) {
//            resetPlayingDifficultWord(false);
//        }
    }

    private void clickPlayButton() {
        if (exoPlayer.isPlaying()) {
            handlePlayClick(false, true);
            binding.llPlay.ivPlay.setTag(R.drawable.ic_new_play);
        } else {
            handlePlayClick(true, true);
            //TODO : Need to remove setTag codes in other places later.
            binding.llPlay.ivPlay.setTag(R.drawable.ic_new_pause);
        }
        //Old codes use tag to determine playing to show play or pause icon.
//        if ((Integer) viewBinding.llPlay.ivPlay.getTag() == R.drawable.ic_new_play) {
//            handlePlayClick(true, true);
//            //TODO : Need to remove setTag codes in other places later.
//            viewBinding.llPlay.ivPlay.setTag(R.drawable.ic_new_pause);
//        } else {
//            handlePlayClick(false, true);
//            viewBinding.llPlay.ivPlay.setTag(R.drawable.ic_new_play);
//        }
    }

    @Override
    protected void onPlayerVideoPlayerStateChanged(boolean playWhenReady, int playbackState) {
        checkDuration(playbackState);
        ImageView ivPlay = binding.llPlay.ivPlay;
        ImageView ivLMPlay = binding.layoutPlayerSubtitleTable.ivLMPlay;
        if (playWhenReady) {
            ivPlay.setImageResource(R.drawable.ic_new_pause);
            ivPlay.setTag(R.drawable.ic_new_pause);
            ivLMPlay.setImageResource(R.drawable.ic_new_pause);
            ivLMPlay.setTag(R.drawable.ic_new_pause);
        } else {
            ivPlay.setImageResource(R.drawable.ic_new_play);
            ivPlay.setTag(R.drawable.ic_new_play);
            ivLMPlay.setImageResource(R.drawable.ic_new_play);
            ivLMPlay.setTag(R.drawable.ic_new_play);
        }
        if (playbackState == ExoPlayer.STATE_READY) {
            if (scrollableMenuAdapter != null && !isAnyRepeatMode() && !scrollableMenuAdapter.isMenuExisted(getMenuEmbeddedTracks())) {
                if (hasMultipleTracksToSelect()) {
                    scrollableMenuAdapter.addItem(getMenuEmbeddedTracks(), menuEmbeddedTracksPosition);
                }
            }
        }
    }

    @Override
    protected void onPlayerVideoPositionDiscontinuity(int reason) {
        if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
            // reset repeatPosition
            if (isDisplaySubtitle() &&
                    subtitleList != null &&
                    !subtitleList.isEmpty()) {
                resetMinMaxSubtitle();
//                resetPlayingDifficultWord();
            } else if (isListenComprehensionMode()) {
                resetMinMaxSubtitle();
                updateValue_SubtitleIndex(0);
//                subtitleIndex = 0;
            }
        }
    }

    private void increaseComprehensionCountForCCRepeat() {
        //Dalnim : When I click CC Repeat button in the Listen Comprehension 2, I want to keep increase the listComprehensionCount
        if (isListenComprehensionMode() || isCCRepeatMode()) {
            DLog.d(getLogTag(), "listComprehensionCount = " + comprehensionRepeatedCount
                    + " - listComprehensionIndex=" + listComprehensionModelIndex
                    + " - RepeatUtil.getRepeatValue(getDicModel(subtitleIndex))=" + RepeatUtil.getRepeatValue(getDicModel(subtitleIndex)));
            if (isListenComprehensionMode()) {
                if (comprehensionRepeatedCount < RepeatUtil.getRepeatValue(getDicModel(subtitleIndex)))
                    updateValue_comprehensionRepeatedCount(comprehensionRepeatedCount + 1);//listComprehensionRepeatedCount++;
            }

            increaseCCRepeatCount();
            updateListenComprehensionCurrentStatus(RepeatUtil.getRepeatValue(getDicModel(subtitleIndex)));
        }
        //----
    }

    private void increaseCCRepeatCount() {
        if (isCCRepeatMode()) {
            ccRepeatingCount++;
        }
    }

    //This is called every 0.01 seconds(Not sure) when a video is playing, So this code controls displaying subtitle and repetition.
    //During repetition, there is only one subtitle to repeat, then call "return" after doing its job to make sure display current repeating subtitle only.
    //because if there are extra time(playBefore/After subtitle time), AraPlayer may show previous/next subtitle, especially in the subtitle table view.
    @Override
    protected void onPlayerVideoRepeatSubtitle(long position) {
// LISTEN COMPREHENSION
        DLog.d("onPlayerVideoRepeatSubtitle", "onPlayerVideoRepeatSubtitle start position : " + position);
        if (isPlayingEndMusicSound)
            return;

        if (isPlayBetweenLyricsOnlyAndOverLastLyric(position)) {
            pausePlayer();
            startOverWhenItReachEndOfMedia();
            return;
        }

        if (mIsOnDelaySubtitle) {
            if (isSubtitleEndTimeExceedDelaySubtitle(position)) {
                seekToInPlayer_MinSubWithDelayTime();
//                seekToInPlayer(mMinMaxSubModel.getMinSubWithDelayTime());
            }
            // If only one subittle is repeated then no need to get next subtitle. So I return here.
            return;
        } else if (isABRepeatMode()) {
            if (isPositionExceedABRepeat(position)) {
                seekToInPlayer(abRepeatMinSub);
                //if문이 없으면, 자막이 없는 구간에서 AB반복을 하면 바로 앞의 자막이 두번째 반복부터 보인다.(빈자막구간이라서 자막이 보이면 안됨) 그래서 빈자막구간이면 hideOrShowDialogsRuleAtABRepeating를 실행안함.
                if (!mIsOnEmptyDialog)
                    hideOrShowDialogsRuleAtABRepeating();
                abRepeatingCount++;
            }
        } else if (!isRepeat && isHasSubtitle() && isHasListenComprehension1()) {
            if (isDisplayListenComprehension1()) {
                boolean returnHereToKeepPlayForSkipParts = handleEventDuringPlaying_ListenComprehension1(position);
                if (returnHereToKeepPlayForSkipParts)
                    return;

            } else if (isDisplayListenComprehension2()) {
                boolean returnHereToKeepPlayForSkipParts = handleEventDuringPlaying_ListenComprehension2(position);
                if (returnHereToKeepPlayForSkipParts)
                    return;
//Be careful return here. I met these issues when I return every time.
////                    return; // If I return here, current subtitle is still showing after his end time. 여기서 리턴하면, 현재자막이 시간이 지나도 사라지지 않고 계속 보인다.
////                return; //If I return here, the subtitle is not refreshed when it goes next subtitle (Always see the old same subtitle)
            }
        } else if (isRepeat) {
            // REPEAT
            //Dalnim : In CC Repeat mode, I use  maxSubWithAllExtraTime instead of maxSub, because I want to add the PlayBefore/After subtitle time when repeating.
            // On Subtitle table view's CC Repeat mode
            if (!repeatList.isEmpty() && (isDisplaySubtitle() || isListenComprehensionMode())) {
                if ((position + Constant.PLAYER.VIDEO_POSITION_BONUS) >= maxSubWithAllExtraTime ||
                        // repeat 2 times when when change startTime
                        isChangeProgress && isTrackingLeft && position >= maxSubWithAllExtraTime + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.PREVIEW_REPEAT_TIME) {

                    int pos = repeatList.indexOf(subtitleIndex);
                    pos++;
                    DLog.d(getLogTag(), "repeatSubTitle - pos=" + pos + " - subtitleIndex=" + subtitleIndex + " - repeatIndex=" + repeatIndex);
                    if (pos >= repeatList.size()) {
                        pos = 0;
                    }
                    repeatIndex = repeatList.get(pos);
                    //TODO : Dalnim : Do I need to update subtitleIndex here too?
                    updateValue_SubtitleIndex(repeatIndex);
                    if (repeatList.size() > 1) {
                        long nextMinSub = getStartTime(getDicModel(subtitleIndex));
                        long nextMinSubWithAllExtraTime = getStartTimeWithAllExtraTime(getDicModel(subtitleIndex));
                        long nextMaxSubWithAllExtraTime = getEndTimeWithAllExtraTime(getDicModel(subtitleIndex));

                        DLog.d(getLogTag(), "repeatSubTitle - pos=" + pos + " - nextMinSub=" + nextMinSubWithAllExtraTime + " - nextMaxSub=" + nextMaxSubWithAllExtraTime);
                        if (isJumpToNextDialogDuringCCRepeatInSubtitleTableView(pos, repeatList, nextMinSub)) {
                            refreshShowAsteriskInCCRepeatMode();
                            seekToInPlayer(nextMinSubWithAllExtraTime);
                            DLog.d(getLogTag(), "repeatSubTitle - pos=" + pos + " - seekTo=" + nextMinSubWithAllExtraTime);
                        }
                        getMinMaxTime(getDicModel(subtitleIndex));
                        DLog.d(getLogTag(), "repeatSubTitle - pos=" + pos + " - subtitleIndex=" + subtitleIndex + " - position=" + position + " - minSub=" + minSub + " - maxSub=" + maxSub + " - size=" + repeatList.size());
                        updateRangeSeek();
                        scrollToPositionSubtitleDialog();
                    } else {
                        refreshShowAsteriskInCCRepeatMode();
                        DLog.d(getLogTag(), "onPlayerVideoRepeatSubtitle - if (isChangeProgress) else" + position);
                        seekToInPlayer((long) minSubWithAllExtraTime);
                    }
                }
                if (repeatList.size() == 1)
                    return;
            } else {
                // On Full Screen's CC Repeat mode

                //If I have this code, it shows next subtitle as time goes by.
                // CC반복일때 현재자막을 Adjust start부터 Adjsut end 시간까지 보여준다. (현재 자막만의 시간대에서 보여주는건 필요하면 별도로 개발해야함)
                // (이줄의 주석은 틀릴거 같음) 반복 시간이 길어서 다음자막에 시간이 걸칠때, 아래코드를 실행시키면 다음자막도 시간에 맞게 보여준다. 지금은 CC반복일때 현재자막만 보여줄려고 아래는 주석처리함.
//                checkAndShowSubTitle(position); //이코드가 있으면 플레이시간에 맞게 자막이 바뀌어버려서 아래 isSubtitleEndTimeExceedAllExtraTime이 항상 false가 나온다.(그래서 CC Repeat를 안한다.)
                if (isSubtitleEndTimeExceedAllExtraTime(position)) {
                    refreshShowAsteriskInCCRepeatMode();
                    seekToInPlayer_MinSubWithAllExtraTime();
                }
                return;
            }
            // SUBTITLE LIST & PLAY TYPE != NORMAL
        } else if (isSkipPlayingNoSubtitlePart && isDisplaySubtitle() && isHasSubtitle()) {
            DLog.d("isSkipPlayingNoSubtitlePart", "isSubtitleEndTimeExceedDelaySubtitle(position) before : " + position);
            if (isSubtitleEndTimeExceedDelaySubtitle(position)) {
                DLog.d("isSkipPlayingNoSubtitlePart", "isSubtitleEndTimeExceedDelaySubtitle(position) inside : " + position);
                //일반 플레이에서 빈 자막 건너뛰기
                if (needToCheckSkipSubtileForListenComprehension(position,isSkipPlayingNoSubtitlePart)) {
                    DLog.d("isSkipPlayingNoSubtitlePart", "isJumpToNextSubtitleWhenSkipPlayingNoSubtitlePart inside : " + position);
                    DicModel dicModelNext = getDicModel(getSubtitleIndexFromCurrentTime() + 1);
                    if (dicModelNext != null) {
                        getMinMaxTime(dicModelNext);
                        seekToInPlayer((long) minSub);
                        return;
                    }
                }
            }
        } else if (isShadowing) {
            if ((!isRecordingOrListeningInShadowing) && (isSubtitleEndTimeExceedAllExtraTime(position))) {
                recordShadowing(getDicModel(subtitleIndex));
            }
            return;
        } else if (!listenRecordedList.isEmpty()) {
            if (maxSub > 0 && position >= maxSub) {
                if (isListeningRecorded) return;
                listenRecordedSubtitle();
            }
            // Prevent select next subtitle when listening recorded
            return;
        }
        activity.runOnUiThread(() -> {
//            DLog.d("isSkipPlayingNoSubtitlePart", "onPlayerVideoRepeatSubtitle end position : " + position);
            checkAndShowSubTitle(position);
            checkRepeatBookmark(position);
            //To play difficult words on normal play mode, comment out this and don't check isListenComprehensionMode in the handlePlayDifficultWord()
//            handlePlayDifficultWord();
        });
    }

    //듣기 연습 1에서 1번 반복이 끝난후에 반복내 모든 자막이 아는거면 다음 반복 그룹으로 넘어갈려고
    private boolean isAllSubtitlesKnowInListenComp1RepeatGroup() {
        int subtitleIndexLocal = getStartSubtitleIndexInListen1();
        boolean isAllSubtitlesKnow = true;
        for (Integer i = 0; i < listComprehension1PlaySubtitlesAtOnce; i++) {
            DicModel dicModel = getDicModel(subtitleIndexLocal + i);
            if (dicModel != null) {
                if (BaseVocaKnow.isUnknownAndLess(dicModel)) {
                    isAllSubtitlesKnow = false;
                    break;
                }
            }
        }
        return isAllSubtitlesKnow;
    }

    private Integer getStartSubtitleIndexInListen1() {
        return listComprehension1SubtitleList.get(comprehension1SubtitleIndexInListFirstIndex);
    }

    private Integer getEndSubtitleIndexInListen1() {
        int index = comprehension1SubtitleIndexInListFirstIndex + listComprehension1PlaySubtitlesAtOnce - 1;
        //듣기 연습1은 listComprehension1PlaySubtitlesAtOnce횟수만큼 반복할때, 마지막 라운드에서는 남은 자막수가 listComprehension1PlaySubtitlesAtOnce보다 작을수가 있으므로 싸이즈 체크를 해줘야한다.
        if (index > (listComprehension1SubtitleList.size() - 1)) {
            index = listComprehension1SubtitleList.size() - 1;
        }
        return listComprehension1SubtitleList.get(index);
    }

    //"자막의 끝시간조절"에 값이 있으면 연속되는 자막일때 잠깐 화면이 끊기는 현상이 발생한다. 이를 해결하기 위한 코드인데, 이것도 잘 안된다.
    private boolean isMoveToNextSubtitleInListenComprehension1(long position) {
        boolean result = false;

        //현재 자막의 끝시간이 지나면("자막의 끝시간조절"의 값을 체크하지 않음) 근데 이렇게 해도 "자막의 끝시간조절"에 값만큼 더 재생을 한다. 원하는거이긴 하지만 이상하네?
        if (isSubtitleEndTimeExceedNoExtraTime(position)) {
//        if (isSubtitleEndTimeExceedAllExtraTime(position)) { //현재 자막의 끝시간이 지나면("자막의 끝시간조절"의 값을 체크함) //이걸 쓰면 연속되는 자막일때 잠깐 화면이 끊기는 현상이 발생한다.
            if (isEndOfListInListenComprehension1Group()) {
                //듣기 연습 1의 그룹의 마지막이면 "자막의 끝시간조절"까지 하고 나서 첨으로 간다.
                if (isSubtitleEndTimeExceedAllExtraTime(position)) {
                    result = true;
                }
            } else {
                //듣기 연습 1의 그룹의 마지막이 아니면, 즉 그룹내의 자막이면, 현재 자막의 "자막의 끝시간조절"과 다음 자막의 "자막의 시작시간조절"을 고려해서 다음으로 넘어갈 시점을 따진다.
                if (Utils.isIndexInsideRange(listComprehension1SubtitleList, comprehension1SubtitleIndexInList + 1)) {
                    int tempSubtitleIndex = listComprehension1SubtitleList.get(comprehension1SubtitleIndexInList + 1);
                    DicModel nextDicModel = SubtitleUtil.getDicModel(subtitleList, tempSubtitleIndex);
                    long nextMinSub = getStartTime(nextDicModel);

                    if (position >= nextMinSub) {
                        //position("자막의 끝시간조절" 계산 안함)이 다음 자막의 시작시간("자막의 시시간조절" 계산 안함)보다 같거나 크면 다음 자막으로 넘어간다.
                        result = true;
                    } else {
                        long nextMinSubWithAllExtraTime = getStartTimeWithAllExtraTime(nextDicModel);
                        long nextMaxSubWithAllExtraTime = getEndTimeWithAllExtraTime(nextDicModel);
                        int playBeforeSubtitle = (int) (activity.playerFileModel.getVideoModel().getPlayBeforeSubtitle() * 1000);
                        int playAfterSubtitle = (int) (activity.playerFileModel.getVideoModel().getPlayAfterSubtitle() * 1000);
                        int meanBeforeAfterSubtitle = (playBeforeSubtitle + playAfterSubtitle) / 2;
                        if ((position + meanBeforeAfterSubtitle) >= nextMinSub) {
                            result = true;
                        } else if ((position + playAfterSubtitle) >= nextMinSub) {
                            result = true;
                        } else if ((position + playAfterSubtitle) >= maxSub) {
                            result = true;
                        } else {
                            result = false;
                        }
                    }
                }
            }
        }
        return result;
    }
    private boolean handleEventDuringPlaying_ListenComprehension1(long position) {
        //현재 자막이 끝나면 자막 그룹내 다음자막이나 맨첨으로 갈지 아님, 다음 자막그룹으로 갈지 체크한다.
//        if (isSubtitleEndTimeExceedNoExtraTime(position)) { //2이걸 쓰면 "자막의 끝시간조절"에 값이 있어도 사용하지 않는다.(반복을 그 시간만큼 길게 하지 않는다.). 단 화면 끊김은 없다.
//        if (isSubtitleEndTimeExceedAllExtraTime(position)) { //1이걸 쓰니까 "자막의 끝시간조절"에 값이 있으면 연속되는 자막일때 잠깐 화면이 끊기는 현상이 발생한다. "자막의 끝시간조절" 시간이 길수록 끊기는 현상도 길어진다. 왜그럴까?
        if (isMoveToNextSubtitleInListenComprehension1(position)) { //3. 이걸써도 화면이 잠깐 끊기는 느낌이다. 근데 "자막의 끝시간조절" 시간이 길어도 끊기는 현상은 안길어지는거 같다.
            boolean isJumpToNextSubtitle = false;
            //듣기 연습 1 옵션의 한번에 반복할 자막수 만큼 반복했으면, 반복의 맨처음으로 가서 listenComprehension1ModelList에 선택된 항목(자막 숨기기, 자막 보이기등)으로 또 반복한다. 아니면 다음 반복 그룹으로 넘어간다.
            if (isEndOfListInListenComprehension1Group()) {
                DLog.d("isSkipPlayingNoSubtitlePart", "듣기 연습 1 옵션의 한번에 반복할 자막수 만큼 반복했으면, 반복의 맨처음으로 가서 listenComprehension1ModelList에 선택된 항목(자막 숨기기, 자막 보이기등)으로 또 반복한다");
                isJumpToNextSubtitle = true; //여기서 왜 할까?
                // 현재 자막 그룹을 반복할려고 comprehension1SubtitleIndexInList를 현재 자막 그룹의 첫번째로 둔다. (다음 자막그룹으로 가는건 밑에서 listComprehension1PlaySubtitlesAtOnceCount++해준다)
                updateValue_listComprehensionModelIndex(listComprehensionModelIndex + 1);//현재 자막 그룹에서 "자막 표시 방법 및 순서"를 다음껄로 한다.
                //"자막 표시 방법 및 순서"를 다 돌았거나, "한번에 반복할 자막수"가 전부 아는 자막이면 바로 다음 자막 그룹으로 간다.
                if (listComprehensionModelIndex >= listenComprehension1ModelList.size() || comprehensionRepeatedCount >= listenComprehension1ModelList.size() || isAllSubtitlesKnowInListenComp1RepeatGroup()) {
                    updateValue_listComprehensionModelIndex(0);
                    updateValue_comprehensionRepeatedCount(0); //현재 자막 그룹에서의 몇번째 반복인지(comprehensionRepeatedCount)를 0으로 줌.
                    isJumpToNextSubtitle = false; //여기서 왜할까?
                    listComprehension1PlaySubtitlesAtOnceCount++;
                    // reset play subtitles at once 비디오의 맨 마지막 자막까지 갔으면...
                    if ((comprehension1SubtitleIndexInList + 1) >= listComprehension1SubtitleList.size()
                            || (subtitleIndex + 1) >= subtitleList.size()) {
                        initListComprehension1SubtitleList(comprehension1SubtitleStartIndex); //중간 자막부터 듣기연습1을 했을때 끝까지 다돌고 나면, 처음 중간자막부터 하게 한다.
                        listComprehension1PlaySubtitlesAtOnceCount = 1;
                        updateValue_comprehension1SubtitleIndexInList(0);
                        updateValue_comprehension1SubtitleIndexInListFirstIndex(comprehension1SubtitleIndexInList);
                        isJumpToNextSubtitle = true; // 비디오의 맨 마지막 자막까지 갔으면 첨 자막으로 점프를 해야한다. 안그러면 Listen1이 처음으로 가긴 가는데, 반복이 안되고 계속 앞으로 나간다. comprehension1SubtitleIndexInList이 계속 증가되어서 isEndOfListInListenComprehension1Group이 제대로 동작안한다.
                        repeatCountOfListen1++;
                        ToastUtil.getInstance(activity).show(getContext().getString(R.string.start_over_listen1, repeatCountOfListen1));
                    } else { // next size of play subtitles at once //현재 반복중인 자막들이 끝나고 다음 자막 그룹으로 넘어감.
                        int tempSubtitleIndex = listComprehension1SubtitleList.get(comprehension1SubtitleIndexInList + 1);
                        DicModel nextDicModel = SubtitleUtil.getDicModel(subtitleList, tempSubtitleIndex);
                        //아래 코드가 있어야 자막 건너뛰기일때 Listen1그룹이 끝나면 그다음 자막그룹으로 갈때 자막을 건너뛴다. 듣기1을 "반복 연습할 자막 그룹 갯수"가 2번째부터도 자막을 건너뛴다.
                        if ((isSkipPlayingNoSubtitlePart) || (repeatCountOfListen1 > 1)) {
                            isJumpToNextSubtitle = true; //아래를 체크할 필요가 없다. 아래에서 "재생위치가 이미  minSubWithAllExtraTime보다 지났으면 점프 하지 않는다"가 있다.
//                            if (needToCheckSkipSubtileForListenComprehension2(position, true, nextDicModel)) {
//                                isJumpToNextSubtitle = true;
//                            }
                        }

                        updateValue_comprehension1SubtitleIndexInList(comprehension1SubtitleIndexInList + 1);
                        updateValue_comprehension1SubtitleIndexInListFirstIndex(comprehension1SubtitleIndexInList);
                    }
                    handlePlayDifficultWordAtOnceSubtitleList(subtitleIndex);
                } else {
                    //다시 현 자막 그룹을 돈다. "자막 표시 방법 및 순서"를 다음단계를 보여준다.
                    updateValue_comprehension1SubtitleIndexInList((listComprehension1PlaySubtitlesAtOnceCount - 1) * listComprehension1PlaySubtitlesAtOnce);
                }
            } else {
                DicModel nextDicModel = null;
                if (Utils.isIndexInsideRange(listComprehension1SubtitleList, comprehension1SubtitleIndexInList + 1)) {
                    int tempSubtitleIndex = listComprehension1SubtitleList.get(comprehension1SubtitleIndexInList + 1);
                    nextDicModel = SubtitleUtil.getDicModel(subtitleList, tempSubtitleIndex);
                }
                //다음 자막으로 점프 할지 여부 판단. Extra시간을 포함해서 체크한다. (자막그룹내의 다음자막으로 점프다. 첫번째자막으로 돌아가는거랑, 다음 자막그룹으로 가는건 어디서 처리하나?
                //두번째 반복부터 체크한다. (isSkipPlayingNoSubtitlePart이 true이면 처음부터 체크한다.)
                boolean isSkipPlayingNoSubtitlePartCheckNeeded = listComprehensionModelIndex > 0;
                if (needToCheckSkipSubtileForListenComprehension2(position, isSkipPlayingNoSubtitlePartCheckNeeded, nextDicModel)) {
                    //다음 자막으로 점프 할거면, 실제로는 Extra시간을 빼고 점프한다.
                    if (isJumpToNextSubtitleWhenSkipPlayingNoSubtitlePartWithAllExtraTime(position)) {
                        isJumpToNextSubtitle = true;
                    }
                }

                //다음 자막으로 점프를 안할거면,
                if (!isJumpToNextSubtitle) {
                    if (nextDicModel != null) {
                        //다음 자막 이전이면 현재자막을 계속 플레이 하면서 다음 자막 시간이 되기전까지는 다음자막을 찾을려고 시간 소요안하게 false를 리턴한다.
                        final int value = activity.playerFileModel.getVideoModel().getDelaySubtitles();
                        final int playAfterSubtitle = 0;// (int) (activity.playerFileModel.getVideoModel().getPlayAfterSubtitle() * 1000); //getPlayAfterSubtitle에 값이 있으면 바로 연결된 자막을 갈때 잠깐 끊기는 현상이 있다. 왜그런지는 모르겠다. 위에서 isSubtitleEndTimeExceedAllExtraTime로 체크할때 그런 현상 발생함.
                        if ((position - playAfterSubtitle) < (nextDicModel.getStartTime() + value)) { //여기에 자막 딜레이 시간도 나중에 더해서 체크해야 한다. (value로 자막 딜레이 시간을 더한거 아닌가?)
                            return true; // true : 현재 자막을 계속 플레이한다.다음 자막을 찾지 않는다. (숨기는건 어떻게 하나?), false : 다음 자막을 찾아본다.
                        }
                    }
                }
                updateValue_comprehension1SubtitleIndexInList(comprehension1SubtitleIndexInList + 1);
            }

            getMinMaxTime(getDicModel(subtitleIndex));
            if (isJumpToNextSubtitle) {
                if (listComprehensionModelIndex == 0) {
                    //반복 그룹이 끝났으면...
                    if (comprehension1SubtitleIndexInList == 0 ) {
                        //듣기 연습1의 맨마지막 반복 그룹이 끝났으면... 첨으로 점프한다.
                        seekToInPlayer((long) minSubWithAllExtraTime);
                    } else if (position < minSubWithAllExtraTime) {
                        // isJumpToNextSubtitle가 true일때, 다음 반복 그룹이 현재 재생위치보다 더 뒤에 있을때만 점프한다. (재생위치가 이미  minSubWithAllExtraTime보다 지났으면 점프 하지 않는다. 점프하면 일부 구간이 중복으로 플레이된다.)
                        seekToInPlayer((long) minSubWithAllExtraTime);
                    }
                } else {
                    //반복 그룹 중이면, isJumpToNextSubtitle가 true이면 점프한다.
                    seekToInPlayer((long) minSubWithAllExtraTime);
                }
            }
            updateListenComprehensionCurrentStatus();
            updateRangeSeek();
            scrollToPositionSubtitleDialog();
        }
        handleShowAsteriskInListenComprehension1();
        return false; // true : 현재 자막을 계속 플레이한다.다음 자막을 찾지 않는다. (숨기는건 어떻게 하나?), false : 다음 자막을 찾아본다.
    }
    private boolean isEndOfListInListenComprehension1Group() {
        return (comprehension1SubtitleIndexInList + 1) >= (listComprehension1PlaySubtitlesAtOnceCount * listComprehension1PlaySubtitlesAtOnce)
                || (comprehension1SubtitleIndexInList + 1) >= listComprehension1SubtitleList.size()
                || (subtitleIndex + 1) >= subtitleList.size();
    }
    private boolean handleEventDuringPlaying_ListenComprehension2(long position) {
//        if (isSubtitleEndTimeExceedAllExtraTimeAndAdjustEndTime(position)) { //이거는 아래꺼와 별 차이를 못느끼겠음. (이걸 쓰면 AdjustEndTime만큼 현재자막을 더 보여줄줄 알았는데.. 이상하네...)
        if (isSubtitleEndTimeExceedAllExtraTime(position)) {
            updateValue_comprehensionRepeatedCount(comprehensionRepeatedCount + 1);//listComprehensionRepeatedCount++;
            int countToRepeat = RepeatUtil.getRepeatValue(getDicModel(subtitleIndex));
            if (comprehensionRepeatedCount >= countToRepeat) {
                boolean isJumpToNextSubtitle = false;

                if (needToCheckSkipSubtileForListenComprehension2(position, isSkipPlayingNoSubtitlePart, SubtitleUtil.getNextDicModel(subtitleList, subtitleIndex))) {
                    if (isJumpToNextSubtitleWhenSkipPlayingNoSubtitlePartWithAllExtraTime(position)) {
                        DLog.d("handleEventComprehension2", "isJumpToNextSubtitleWhenSkipPlayingNoSubtitlePartWithAllExtraTime true : ");
                        isJumpToNextSubtitle = true;
                    } else {
                        DLog.d("handleEventComprehension2", "isJumpToNextSubtitleWhenSkipPlayingNoSubtitlePartWithAllExtraTime false : ");
                        return true;
                    }
                }
                DicModel nextDicModel = SubtitleUtil.getNextDicModel(subtitleList, subtitleIndex);
                //다음 자막으로 점프를 안할거면,
                if (!isJumpToNextSubtitle) {
                    if (nextDicModel != null) {
                        //다음 자막 이전이면 현재자막을 계속 플레이 하면서 시간이 되면 다음 자막을 찾는다.
                        final int value = activity.playerFileModel.getVideoModel().getDelaySubtitles();
                        if (position < nextDicModel.getStartTime() + value) { //여기에 자막 딜레이 시간도 나중에 더해서 체크해야 한다.
                            DLog.d("isSkipPlayingNoSubtitlePart", "position < nextDicModel.getStartTime()");
                            setHideSubtitle(); //이전 자막이 끝났고 다음자막이 안왔기 때문에 이전 자막을 안보이게 한다. 이게 없으면 빈 자막구간에 이전 자막이 계속 보인다.
                            return true; //근데 true와 false의 차이점을 모르겠네. true : 현재 자막을 계속 플레이한다.다음 자막을 찾지 않는다. false : 다음 자막을 찾아본다.
                        } else {
//                            comprehension1SubtitleIndexInList++; //다음자막으로 가면 자막 반복그룹의 인덱스를 하나 증가시킴.
                            DLog.d("isSkipPlayingNoSubtitlePart", "comprehension1SubtitleIndexInList++ : " + comprehension1SubtitleIndexInList);
                        }
                    }
                }

                //문제점. 듣기 2에서 현재 자막 반복이 끝나고 다음자막으로 가야하는데, 빈 자막 구간이 있을때, 여기서 아는정도를 바꾸면 다음자막의 아는정도가 바뀐다. (듣기 연습1에서는 이 이슈를 고침. 참고할것)
                // increaseSubtitleIndexAndBackToBeginningWhenOverflow로 자막을 옮겨버렸기 때문
                //(increaseSubtitleIndexAndBackToBeginningWhenOverflow로 다음자막으로 안옮기면, 듣기 2가 다음자막으로 가지 않는다.)
                increaseSubtitleIndexAndBackToBeginningWhenOverflow();
                DLog.d("onPlayerVideoRepeatSubtitle", "move next subtitle, subtitleIndex : " + subtitleIndex);
                DicModel dicModelNext = getDicModel(subtitleIndex);
                getMinMaxTime(dicModelNext);
                if (isJumpToNextSubtitle) {
                    DLog.d("handleEventComprehension2", "isJumpToNextSubtitle true : ");
                    seekToInPlayer((long) minSubWithAllExtraTime);
                }
                updateValue_comprehensionRepeatedCount(0);//listComprehensionRepeatedCount = 0;
                isInRepeatCurrentSubtitleOfListenMode2 = false; //듣기 2에서 현재 자막의 반복이 끝났으니, 현재 자막을 안보여줘도 된다는 뜻. (없으면 다음자막이 아는 자막이면 현재자막을 ---- 로 계속 보여준다.)
            } else {
                //Play current subtitle again in the repetition round. (Not move to next subtitle).듣기 연습 라운드중에 다시 자막 앞으로 가서 플레이 한다. (다음자막으로 가는게 아니다.)
                DLog.d("handleEventComprehension2", "isSubtitleEndTimeExceedAllExtraTime(position) true : " + position);
//                final DicModel dicModel = getDicModel(subtitleIndex);
//                getMinMaxTime(dicModel);
                seekToInPlayer((long) minSubWithAllExtraTime);
//                updateStatusOfListComprehension2(dicModel);
            }
            //이걸 여기서 안하면 듣기2에서 2번째 반복때 자막이 안보인다. 그리고 "1/2 자막숨기기" 문구가 가 2/2로 바뀌지 않는다.

            //근데 현재자막의 듣기 2가 끝나서 다음자막으로 넘어가기전에 빈 자막구간이 있을때, subtitleIndex가 이미 다음자막으로 넘어갔기때문에, 다음자막이 아는 자막이면, 현재자막이 ----- 로 계속 보인다. (아는 자막일때는 ---로 보이게 한게 적용됨)
            // (다음 자막이 아는 자막이 아니면 듣기 2에서는 처음은 무조건 숨기기 때문에 현재자막이 숨겨져서 보이기 때문에 위의 현상이 안생긴다)
            // 이건 isInRepeatCurrentSubtitleOfListenMode2를 추가하고 refreshSubtitleUnlessSameSubtitle에서 if (isDisplayListenComprehension2() && isInRepeatCurrentSubtitleOfListenMode2) {를 검사해서 해결함.
            final DicModel dicModel = getDicModel(subtitleIndex);
            updateStatusOfListComprehension2(dicModel);
        } else {
            //When starts Repeating, read difficult words first if the option is on. 듣기 연습 2에서 반복횟수중 처음일때 어려운 단어 말하기를 호출한다.
            if (comprehensionRepeatedCount == 0) {
                DLog.d("handleEventComprehension2", "isSubtitleEndTimeExceedAllExtraTime(position) false : " + position);
                handlePlayDifficultWord();
                isInRepeatCurrentSubtitleOfListenMode2 = true; //듣기 2에서 현재 자막의 반복이 시작했으니 항상 자막을 보여주라는 뜻.
            }
        }
        DLog.d("onPlayerVideoRepeatSubtitle", " If I return here, the subtitle is not refreshed");
        return false;
//                return; //If I return here, the subtitle is not refreshed when it goes next subtitle (Always see the old same subtitle)
    }


    private boolean isJumpToNextDialogDuringCCRepeatInSubtitleTableView(int pos, List<Integer> repeatList, long nextMinSub) {
        boolean isJump = false;
        if (pos > repeatList.size() - 1) {
            isJump = true;
        } else if (Math.abs(nextMinSub - maxSub) > getMinTimeKeepPlayBetween()) {
            isJump = true;
        }
        return isJump;
    }

//    private boolean isDontJumpToNextSubtitle(long nextMinSub) {
//        if (isSkipPlayingNoSubtitlePart) {
//            if (Math.abs(nextMinSub - maxSub) > getMinTimeKeepPlayBetween()) {
//                return true;
//            }
//        }
//        return false;
//    }

    //MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE를 더해 주는 이유는 다음 자막으로 건너뛸때 끊기는 현상을 없앨려고 하는것이다.
    //getMinTimeKeepPlayBetween() 쓰게 되면 getMinTimeKeepPlayBetween() 바로 조금 뒤에 시작하는 다음자막으로 점프를 하여 끊겨서 보이지만
    //MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE를 사용하면 다음자막이 바로 뒤 MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE시간 안에 있으면 점프를 하지 않는다.
    //또 바로 다음 자막이 MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE보다 뒤에 있으면 getMinTimeKeepPlayBetween()에서 점프를 하기 때문에 좀더 자연스럽게 보인다.
    private boolean isJumpToNextSubtitleWhenSkipPlayingNoSubtitlePart(long position) {
        if (isSkipPlayingNoSubtitlePart) {
            DLog.d("isSkipPlayingNoSubtitlePart", "position : " + position);
            DLog.d("isSkipPlayingNoSubtitlePart", "maxSub : " + maxSub);
            DLog.d("isSkipPlayingNoSubtitlePart", "position - maxSub : " + (position - maxSub));
            DLog.d("isSkipPlayingNoSubtitlePart", "getMinTimeKeepPlayBetweenWithExtraValue() : " + getMinTimeKeepPlayBetweenWithExtraValue());
            if (((position - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue())) {
                DLog.d("isSkipPlayingNoSubtitlePart", "isJumpToNextSubtitleWhenSkipPlayingNoSubtitlePart true");
                return true;
            }
        }
        return false;
    }


    private int getMinTimeKeepPlayBetweenWithExtraValue() {
        int minTimeKeepPlayBetweenWithExtraValue = (int) (getMinTimeKeepPlayBetween() + (Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE * 1000));
        return minTimeKeepPlayBetweenWithExtraValue;
    }

    private boolean isJumpToNextSubtitleWhenSkipPlayingNoSubtitlePartWithAllExtraTime(long position) {
        if ((position - maxSub) > getMinTimeKeepPlayBetween()) {
            DLog.d("onPlayerVideoRepeatSubtitle", "isJump true maxSub: " + maxSub + ", (position - maxSub) = " + (position - maxSub) + ", getMinTimeKeepPlayBetween = " + getMinTimeKeepPlayBetween());
            return true;
        }
        DLog.d("onPlayerVideoRepeatSubtitle", "isJump false maxSub: " + maxSub + ", (position - maxSub) = " + (position - maxSub) + ", getMinTimeKeepPlayBetween = " + getMinTimeKeepPlayBetween());
        return false;
    }

    //Don't check Skip Subtilte if the two subtitle's time gap is short.
    private boolean needToCheckSkipSubtileForListenComprehension(long position, boolean isSkipValueCheckNeeded) {
//        DicModel currentDicModel = getDicModel(subtitleIndex);
        DLog.d("isSkipPlayingNoSubtitlePart", "needToCheckSkipSubtileForListenComprehension isSkipValueCheckNeeded : " + isSkipValueCheckNeeded);
        DicModel nextDicModel = SubtitleUtil.getNextDicModel(subtitleList, subtitleIndex);
        if (nextDicModel == null) {
            return false;
        }
        if (isSkipValueCheckNeeded) {
            if ((position - maxSub) > getMinTimeKeepPlayBetween()) {
                DLog.d("isSkipPlayingNoSubtitlePart", "position : " + position);
                DLog.d("isSkipPlayingNoSubtitlePart", "maxSub : " + maxSub);
                DLog.d("isSkipPlayingNoSubtitlePart", "nextDicModel.getStartTime() - maxSub : " + (nextDicModel.getStartTime() - maxSub));
                DLog.d("isSkipPlayingNoSubtitlePart", "getMinTimeKeepPlayBetweenWithExtraValue() : " + getMinTimeKeepPlayBetweenWithExtraValue());
                DLog.d("isSkipPlayingNoSubtitlePart", "nextDicModel.getStartTime() - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue() : " + ((nextDicModel.getStartTime() - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue()));
                if ((nextDicModel.getStartTime() - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue()) {
                    return true;
                } else {
                    return false;
                }
//                return (nextDicModel.getStartTime() - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue();
            }
        }
        return false;
        //MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE를 더해 주는 이유는 다음 자막으로 건너뛸때 끊기는 현상을 없앨려고 하는것이다.
        //getMinTimeKeepPlayBetween() 쓰게 되면 getMinTimeKeepPlayBetween() 바로 조금 뒤에 시작하는 다음자막으로 점프를 하여 끊겨서 보이지만
        //MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE를 사용하면 다음자막이 바로 뒤 MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE시간 안에 있으면 점프를 하지 않는다.
        //또 바로 다음 자막이 MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE보다 뒤에 있으면 getMinTimeKeepPlayBetween()에서 점프를 하기 때문에 좀더 자연스럽게 보인다.
//        int minTimeKeepPlayBetweenWithExtraValue = (int) (getMinTimeKeepPlayBetween() + (Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE * 1000));

    }

    //Don't check Skip Subtilte if the two subtitle's time gap is short.
    private boolean needToCheckSkipSubtileForListenComprehension2(long position, boolean isSkipPlayingNoSubtitlePartCheckNeeded, DicModel nextDicModel) {
//        DicModel currentDicModel = getDicModel(subtitleIndex);
        DLog.d("isSkipPlayingNoSubtitlePart", "needToCheckSkipSubtileForListenComprehension isSkipPlayingNoSubtitlePartCheckNeeded : " + isSkipPlayingNoSubtitlePartCheckNeeded);
        if (nextDicModel == null) {
            return false;
        }

        //듣기 연습 1에서 두번째 반복부터는 무조건 빈 자막은 스킵한다.
        if (repeatCountOfListen1 > 1) {
            return true;
        }

        if (isSkipPlayingNoSubtitlePartCheckNeeded || isSkipPlayingNoSubtitlePart) {
            if ((position - maxSub) > getMinTimeKeepPlayBetween()) {
                DLog.d("isSkipPlayingNoSubtitlePart", "position : " + position);
                DLog.d("isSkipPlayingNoSubtitlePart", "maxSub : " + maxSub);
                DLog.d("isSkipPlayingNoSubtitlePart", "nextDicModel.getStartTime() - maxSub : " + (nextDicModel.getStartTime() - maxSub));
                DLog.d("isSkipPlayingNoSubtitlePart", "getMinTimeKeepPlayBetweenWithExtraValue() : " + getMinTimeKeepPlayBetweenWithExtraValue());
                DLog.d("isSkipPlayingNoSubtitlePart", "nextDicModel.getStartTime() - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue() : " + ((nextDicModel.getStartTime() - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue()));
                if ((nextDicModel.getStartTime() - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue()) {
                    DLog.d("isSkipPlayingNoSubtitlePart", "needToCheckSkipSubtileForListenComprehension2 : true");
                    return true;
                } else {
                    DLog.d("isSkipPlayingNoSubtitlePart", "needToCheckSkipSubtileForListenComprehension2 : false");
                    return false;
                }
//                return (nextDicModel.getStartTime() - maxSub) > getMinTimeKeepPlayBetweenWithExtraValue();
            }
        }

        return false;
        //MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE를 더해 주는 이유는 다음 자막으로 건너뛸때 끊기는 현상을 없앨려고 하는것이다.
        //getMinTimeKeepPlayBetween() 쓰게 되면 getMinTimeKeepPlayBetween() 바로 조금 뒤에 시작하는 다음자막으로 점프를 하여 끊겨서 보이지만
        //MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE를 사용하면 다음자막이 바로 뒤 MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE시간 안에 있으면 점프를 하지 않는다.
        //또 바로 다음 자막이 MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE보다 뒤에 있으면 getMinTimeKeepPlayBetween()에서 점프를 하기 때문에 좀더 자연스럽게 보인다.
//        int minTimeKeepPlayBetweenWithExtraValue = (int) (getMinTimeKeepPlayBetween() + (Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE * 1000));

    }

    //Don't check Skip Subtilte if the two subtitle's time gap is short.
    private boolean needToCheckSkipSubtileForListenComprehension1(boolean isSkipValueCheckNeeded) {
        DicModel currentDicModel = getDicModel(subtitleIndex);
        DicModel nextDicModel = SubtitleUtil.getNextDicModel(subtitleList, subtitleIndex);
        if ((currentDicModel == null) || (nextDicModel == null) || isSkipValueCheckNeeded) {
            if (!isSkipPlayingNoSubtitlePart)
                return false;
        }
        //MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE를 더해 주는 이유는 다음 자막으로 건너뛸때 끊기는 현상을 없앨려고 하는것이다.
        //getMinTimeKeepPlayBetween() 쓰게 되면 getMinTimeKeepPlayBetween() 바로 조금 뒤에 시작하는 다음자막으로 점프를 하여 끊겨서 보이지만
        //MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE를 사용하면 다음자막이 바로 뒤 MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE시간 안에 있으면 점프를 하지 않는다.
        //또 바로 다음 자막이 MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE보다 뒤에 있으면 getMinTimeKeepPlayBetween()에서 점프를 하기 때문에 좀더 자연스럽게 보인다.
//        int minTimeKeepPlayBetweenWithExtraValue = (int) (getMinTimeKeepPlayBetween() + (Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BETWEEN_SUBTITLE_EXTRA_VALUE * 1000));
        return (nextDicModel.getStartTime() - currentDicModel.getEndTime()) > getMinTimeKeepPlayBetweenWithExtraValue();
    }

    @Override
    public void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        unregisterObserver();
        destroyShadowingParams();
        super.onDestroy();
    }

    private void destroyTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void initOnClickListener() {
        binding.ivBackMusicPlayer.setOnClickListener(this);
        binding.ivCenterPlay.setOnClickListener(this);
        binding.ivCloseAdjustableBarView.setOnClickListener(this);
        binding.ivListenComprehension1.setOnClickListener(this);
        binding.ivLockAll.setOnClickListener(this);
        binding.ivMenuMusicPlayer.setOnClickListener(this);
        binding.ivResetAdjustableBarView.setOnClickListener(this);
        binding.ivShowButtonsPlayPause.setOnClickListener(this);
        binding.ivShowButtonsRepetitionByAbRightOnMainPlayerScreen.setOnClickListener(this);
        binding.ivShowButtonsRepetitionByCcRight.setOnClickListener(this);
        binding.ivShowButtonsSubtitleEye.setOnClickListener(this);
        binding.ivTranslate.setOnClickListener(this);
        binding.ivWebDictionary.setOnClickListener(this);
        binding.tvPinchZoomClose.setOnClickListener(this);
        binding.tvSleepTime.setOnClickListener(this);

        binding.layoutListenComprehension.ivListenComprehensionExit.setOnClickListener(this);
        binding.layoutListeningRecordedSubtitle.ivExitListeningRecorded.setOnClickListener(this);

        binding.layoutShadowing.ivCloseShadowing.setOnClickListener(this);
        binding.layoutShadowing.ivRecording.setOnClickListener(this);

        binding.llDelaySubtitle.btnResetMaxValue.setOnClickListener(this);
        binding.llDelaySubtitle.icDelaySubtitleMinus.setOnClickListener(this);
        binding.llDelaySubtitle.icDelaySubtitlePlus.setOnClickListener(this);
        binding.llDelaySubtitle.icDelaySubtitleReset.setOnClickListener(this);
        binding.llDelaySubtitle.ivDelayClose.setOnClickListener(this);
        binding.llDelaySubtitle.llDelayClose.setOnClickListener(this);

        binding.llPlay.btnSelectLyric.setOnClickListener(this);
        binding.llPlay.ivAdjustSubtitleFontSize.setOnClickListener(this);
        binding.llPlay.ivBack.setOnClickListener(this);
        binding.llPlay.ivBackward.setOnClickListener(this);
        binding.llPlay.ivForward.setOnClickListener(this);
        binding.llPlay.ivLock.setOnClickListener(this);
        binding.llPlay.ivMenu.setOnClickListener(this);
        binding.llPlay.ivNextSong.setOnClickListener(this);
        binding.llPlay.ivPlay.setOnClickListener(this);
        binding.llPlay.ivPreviousSong.setOnClickListener(this);
        binding.llPlay.ivRepeatSong.setOnClickListener(this);
        binding.llPlay.ivScreenResize.setOnClickListener(this);
        binding.llPlay.ivShowButtonsRepetitionByAbRight.setOnClickListener(this);
        binding.llPlay.ivShowHideTableview.setOnClickListener(this);
        binding.llPlay.ivShuffle.setOnClickListener(this);
        binding.llPlay.ivSubtitleLangChoose.setOnClickListener(this);
        binding.llPlay.ivZoom.setOnClickListener(this);

        binding.llPlay.layoutPlayerPlayScrollableMenu.ivScollableMenuRightArrow.setOnClickListener(this);

        binding.llPlay.layoutPlayerPlaySpeed.ivAudioSpeedMinus.setOnClickListener(this);
        binding.llPlay.layoutPlayerPlaySpeed.ivAudioSpeedPlus.setOnClickListener(this);
        binding.llPlay.layoutPlayerPlaySpeed.tvAudioSpeedValue.setOnClickListener(this);

        binding.llPlaySub.ivFontSizeBig.setOnClickListener(this);
        binding.llPlaySub.ivFontSizeNormal.setOnClickListener(this);
        binding.llPlaySub.ivFontSizeSmall.setOnClickListener(this);
        binding.llPlaySub.ivSubtitleLangBoth.setOnClickListener(this);
        binding.llPlaySub.ivSubtitleLangMeaning.setOnClickListener(this);
        binding.llPlaySub.ivSubtitleLangStudy.setOnClickListener(this);

        binding.llRepeat.btnAddSubtitle.setOnClickListener(this);
        binding.llRepeat.btnCCRepeatReset.setOnClickListener(this);
        binding.llRepeat.ivRepeatBookmark.setOnClickListener(this);
        binding.llRepeat.ivRepeatClose.setOnClickListener(this);
        binding.llRepeat.rlLayoutPrevNextSubtitleOverlap.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivListenComprehension1:
                onClickListenComprehensionIcon(v);
                break;
            case R.id.ivBackMusicPlayer:
            case R.id.ivBack:
                finishActivity();
                break;
            case R.id.ivMenuMusicPlayer:
            case R.id.ivMenu:
                handleMenuClick();
                break;
            case R.id.ivWebDictionary:
                pausePlayer();
                DicModel dicModel1 = getDicModel(subtitleIndex);
                openWebDictionary(dicModel1);
                break;
            case R.id.ivTranslate:
                pausePlayer();
                DicModel dicModel = getDicModel(subtitleIndex);
                String text = "";
                if (dicModel != null) {
                    text = dicModel.getVocaDisplay();
                }
                openWebTranslate(text);
                break;
            case R.id.ivShowHideTableview:
                updateVisibilityTableView();
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
            case R.id.ivAdjustSubtitleFontSize:
                handleAdjustSubtitleFontSize();
                break;
            case R.id.ivScreenResize:
                handleScreenZoomClick();
                break;
            case R.id.ivBackward:
                handleBackwardClick(1);
                break;
            case R.id.ivCenterPlay:
            case R.id.ivPlay:
            case R.id.ivShowButtonsPlayPause:
                clickPlayButton();
                break;
            case R.id.ivForward:
                handleForwardClick(1);
                break;
            case R.id.ivZoom:
                handleZoomClick();
                break;
            case R.id.ivLockAll:
            case R.id.ivLock:
                handleLockClick();
                break;
            case R.id.ivShowButtonsRepetitionByCcRight:
                handleSubRepetitionByCCClick(v.getId());
                break;
            case R.id.ivShowButtonsRepetitionByAbRight:
            case R.id.ivShowButtonsRepetitionByAbRightOnMainPlayerScreen:
                handleABRepeatClick();
                break;
            case R.id.ivRepeatClose:
                handleRepeatCloseClick();
                break;
            case R.id.ivRepeatBookmark:
                handleRepeatBookmark();
                break;
            case R.id.ivShowButtonsSubtitleEye:
                handleSubtitleDialogEyeClick();
                break;
            case R.id.btnCCRepeatReset:
                handleCCRepeatReset();
                break;
            case R.id.btnAddSubtitle:
                showPopupToInsertABRepeat(new DicModel(), false);
                break;
            case R.id.ivListenComprehensionExit:

                handleExitListenComprehensionMode();
                break;
//            case R.id.iv_show_buttons_bookmark:
//                final DicModel item = getDicModel(subtitleIndex);
//                updateBookmark(item);
//                updateBookmarkUI(item);
//                break;
            case R.id.ic_delay_subtitle_minus:
            case R.id.ic_delay_subtitle_reset:
            case R.id.ic_delay_subtitle_plus:
                onDelaySubtitleChangeValue(v.getId());
                break;
            case R.id.ll_delay_close:
            case R.id.iv_delay_close:
                onDelaySubtitleClose();
                break;
            case R.id.btn_reset_max_value:
                resetMaxValueOnDelaySubtitle();
                break;
            case R.id.ivSubtitleLangChoose:
                mDisplaySubtitleLang = (mDisplaySubtitleLang + 1) % Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.COUNT_OF_TYPE;
                chooseSubtitleLangToDisplay(mDisplaySubtitleLang);
                break;
            case R.id.ivSubtitleLangBoth:
                chooseSubtitleLangToDisplay(Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.BOTH);
                break;
            case R.id.ivSubtitleLangStudy:
                chooseSubtitleLangToDisplay(Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.STUDY);
                break;
            case R.id.ivSubtitleLangMeaning:
                chooseSubtitleLangToDisplay(Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.MEAING);
                break;
            case R.id.ivFontSizeSmall:
                resizeSubtitleFontSize(false, Constant.PLAYER.SETTING.SUBTITLE_FONT_SIZE_ADJUST);
                break;
            case R.id.ivFontSizeNormal:
                resetSubtitleFontSize();
                break;
            case R.id.ivFontSizeBig:
                resizeSubtitleFontSize(true, Constant.PLAYER.SETTING.SUBTITLE_FONT_SIZE_ADJUST);
                break;
            case R.id.rlLayoutPrevNextSubtitleOverlap:
                openCCRepeatTuningScreenDialog();
                break;
            case R.id.tvSleepTime:
                askToCancelSleep();
                break;
            case R.id.tvPinchZoomClose:
                setScaleToDefault();
                break;
            case R.id.ivScollableMenuRightArrow:
                expandScrollableMenu();
                break;
            case R.id.ivCloseAdjustableBarView:
                closeAdjustableBarView();
                break;
            case R.id.ivResetAdjustableBarView:
                resetAdjustableBarView();
                break;
            case R.id.ivCloseShadowing:
                destroyShadowingParams();
                closeShadowing(null);
                break;
            case R.id.iv_exit_listening_recorded:
                closeListeningRecorded();
                break;
            case R.id.ivRecording:
                manualStopRecording();
                break;
            case R.id.ivPreviousSong:
                handleClickPreviousSong();
                break;
//            case R.id.ivNextSong:
//                handleClickNextSong();
//                break;
            case R.id.ivShuffle:
                handleClickShuffle();
                break;
            case R.id.ivRepeatSong:
                handleClickRepeatSong();
                break;
            case R.id.btn_select_lyric:
                handleClickSelectLyric();
                break;
        }
    }
//    @Optional
//    @OnClick({
//            R.id.ivBack, R.id.ivMenu, R.id.ivLockAll, R.id.ivCenterPlay, R.id.btnAddSubtitle, R.id.ivShowButtonsPlayPause,
//            R.id.ivShowHideTableview, R.id.ivAudioSpeedMinus, R.id.tvAudioSpeedValue, R.id.ivAudioSpeedPlus,
//            R.id.ivAdjustSubtitleFontSize, R.id.ivScreenResize, R.id.ivBackward, R.id.ivPlay, R.id.ivForward, R.id.ivZoom, R.id.ivLock,
//            R.id.ivRepeatClose, R.id.ivRepeatBookmark, R.id.ivListenComprehension1,
//            R.id.btnCCRepeatReset, R.id.ivTranslate,
//            R.id.ivListenComprehensionExit, R.id.ivWebDictionary, R.id.ivShowButtonsRepetitionByAbRightOnMainPlayerScreen,
//            R.id.ivShowButtonsRepetitionByAbRight, R.id.ivShowButtonsRepetitionByCcRight, R.id.ivShowButtonsSubtitleEye,
//            R.id.ivSubtitleLangChoose, R.id.ivSubtitleLangBoth, R.id.ivSubtitleLangStudy, R.id.ivSubtitleLangMeaning, R.id.ivFontSizeSmall, R.id.ivFontSizeNormal, R.id.ivFontSizeBig,
//            R.id.ll_delay_close, R.id.iv_delay_close, R.id.ic_delay_subtitle_minus, R.id.ic_delay_subtitle_reset, R.id.ic_delay_subtitle_plus, R.id.btn_reset_max_value,
//            R.id.tvSleepTime, R.id.tvPinchZoomClose, R.id.rlLayoutPrevNextSubtitleOverlap, R.id.ivScollableMenuRightArrow,
//            R.id.ivCloseAdjustableBarView, R.id.ivResetAdjustableBarView, R.id.ivCloseShadowing, R.id.iv_exit_listening_recorded, R.id.ivRecording,
//            R.id.ivPreviousSong, R.id.ivShuffle, R.id.ivRepeatSong, R.id.ivNextSong, R.id.btn_select_lyric, R.id.ivBackMusicPlayer, R.id.ivMenuMusicPlayer})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ivListenComprehension1:
//                onClickListenComprehensionIcon(view);
//                break;
//            case R.id.ivBackMusicPlayer:
//            case R.id.ivBack:
//                finishActivity();
//                break;
//            case R.id.ivMenuMusicPlayer:
//            case R.id.ivMenu:
//                handleMenuClick();
//                break;
//            case R.id.ivWebDictionary:
//                pausePlayer();
//                DicModel dicModel1 = getDicModel(subtitleIndex);
//                openWebDictionary(dicModel1);
//                break;
//            case R.id.ivTranslate:
//                pausePlayer();
//                DicModel dicModel = getDicModel(subtitleIndex);
//                String text = "";
//                if (dicModel != null) {
//                    text = dicModel.getVocaDisplay();
//                }
//                openWebTranslate(text);
//                break;
//            case R.id.ivShowHideTableview:
//                updateVisibilityTableView();
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
//            case R.id.ivAdjustSubtitleFontSize:
//                handleAdjustSubtitleFontSize();
//                break;
//            case R.id.ivScreenResize:
//                handleScreenZoomClick();
//                break;
//            case R.id.ivBackward:
//                handleBackwardClick(1);
//                break;
//            case R.id.ivCenterPlay:
//            case R.id.ivPlay:
//            case R.id.ivShowButtonsPlayPause:
//                clickPlayButton();
//                break;
//            case R.id.ivForward:
//                handleForwardClick(1);
//                break;
//            case R.id.ivZoom:
//                handleZoomClick();
//                break;
//            case R.id.ivLockAll:
//            case R.id.ivLock:
//                handleLockClick();
//                break;
//            case R.id.ivShowButtonsRepetitionByCcRight:
//                handleSubRepetitionByCCClick(view.getId());
//                break;
//            case R.id.ivShowButtonsRepetitionByAbRight:
//            case R.id.ivShowButtonsRepetitionByAbRightOnMainPlayerScreen:
//                handleABRepeatClick();
//                break;
//            case R.id.ivRepeatClose:
//                handleRepeatCloseClick();
//                break;
//            case R.id.ivRepeatBookmark:
//                handleRepeatBookmark();
//                break;
//            case R.id.ivShowButtonsSubtitleEye:
//                handleSubtitleDialogEyeClick();
//                break;
//            case R.id.btnCCRepeatReset:
//                handleCCRepeatReset();
//                break;
//            case R.id.btnAddSubtitle:
//                showPopupToInsertABRepeat(new DicModel(), false);
//                break;
//            case R.id.ivListenComprehensionExit:
//
//                handleExitListenComprehensionMode();
//                break;
////            case R.id.iv_show_buttons_bookmark:
////                final DicModel item = getDicModel(subtitleIndex);
////                updateBookmark(item);
////                updateBookmarkUI(item);
////                break;
//            case R.id.ic_delay_subtitle_minus:
//            case R.id.ic_delay_subtitle_reset:
//            case R.id.ic_delay_subtitle_plus:
//                onDelaySubtitleChangeValue(view.getId());
//                break;
//            case R.id.ll_delay_close:
//            case R.id.iv_delay_close:
//                onDelaySubtitleClose();
//                break;
//            case R.id.btn_reset_max_value:
//                resetMaxValueOnDelaySubtitle();
//                break;
//            case R.id.ivSubtitleLangChoose:
//                mDisplaySubtitleLang = (mDisplaySubtitleLang + 1) % Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.COUNT_OF_TYPE;
//                chooseSubtitleLangToDisplay(mDisplaySubtitleLang);
//                break;
//            case R.id.ivSubtitleLangBoth:
//                chooseSubtitleLangToDisplay(Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.BOTH);
//                break;
//            case R.id.ivSubtitleLangStudy:
//                chooseSubtitleLangToDisplay(Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.STUDY);
//                break;
//            case R.id.ivSubtitleLangMeaning:
//                chooseSubtitleLangToDisplay(Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.MEAING);
//                break;
//            case R.id.ivFontSizeSmall:
//                resizeSubtitleFontSize(false, Constant.PLAYER.SETTING.SUBTITLE_FONT_SIZE_ADJUST);
//                break;
//            case R.id.ivFontSizeNormal:
//                resetSubtitleFontSize();
//                break;
//            case R.id.ivFontSizeBig:
//                resizeSubtitleFontSize(true, Constant.PLAYER.SETTING.SUBTITLE_FONT_SIZE_ADJUST);
//                break;
//            case R.id.rlLayoutPrevNextSubtitleOverlap:
//                openCCRepeatTuningScreenDialog();
//                break;
//            case R.id.tvSleepTime:
//                askToCancelSleep();
//                break;
//            case R.id.tvPinchZoomClose:
//                setScaleToDefault();
//                break;
//            case R.id.ivScollableMenuRightArrow:
//                expandScrollableMenu();
//                break;
//            case R.id.ivCloseAdjustableBarView:
//                closeAdjustableBarView();
//                break;
//            case R.id.ivResetAdjustableBarView:
//                resetAdjustableBarView();
//                break;
//            case R.id.ivCloseShadowing:
//                destroyShadowingParams();
//                closeShadowing(null);
//                break;
//            case R.id.iv_exit_listening_recorded:
//                closeListeningRecorded();
//                break;
//            case R.id.ivRecording:
//                manualStopRecording();
//                break;
//            case R.id.ivPreviousSong:
//                handleClickPreviousSong();
//                break;
////            case R.id.ivNextSong:
////                handleClickNextSong();
////                break;
//            case R.id.ivShuffle:
//                handleClickShuffle();
//                break;
//            case R.id.ivRepeatSong:
//                handleClickRepeatSong();
//                break;
//            case R.id.btn_select_lyric:
//                handleClickSelectLyric();
//                break;
//        }
//    }

    private void onClickListenComprehensionIcon(View view) {
        if (isListenComprehensionMode()) {
            handleExitListenComprehensionMode();
        } else {
            DicModel dicModel = getDicModelGetFirstIfIndexIsMinusOne(subtitleIndex);
            if (dicModel != null) {
                if (view.getId() == R.id.ivListenComprehension1) {
                    openPlayerListenComprehension1Dialog(dicModel);
                } else {
                    enterListenComprehensionMode2(dicModel);
                }
            }
        }
    }

    private void showPopupToInsertABRepeat(DicModel dicModel, boolean isUpdateSubtitle) {
        if (isABRepeatMode()) {
            INSERT_SUBTITLE insertSubtitle = isAbleToInsertNewSubtitle();
            if (insertSubtitle == INSERT_SUBTITLE.NO) {
                ToastUtil.getInstance(activity).show(R.string.messsage_insert_subtitle_no);
                return;
            }

            if (insertSubtitle == INSERT_SUBTITLE.YES_BUT_OVERLAP_START_TIME) {
                ToastUtil.getInstance(activity).show(R.string.messsage_insert_subtitle_overlap_start_time);
            } else if (insertSubtitle == INSERT_SUBTITLE.YES_BUT_OVERLAP_END_TIME) {
                ToastUtil.getInstance(activity).show(R.string.messsage_insert_subtitle_overlap_end_time);
            }
        }

        editSubtitleDialog = new EditSubtitleDialog(activity, dicModel, isUpdateSubtitle, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                SubtitleModel subtitleModel = (SubtitleModel) object;
                switch (view.getId()) {
                    case R.id.ivUpdateOrAdd:
                        if (dicModel.getId() >= 0) {
                            //Don't need to update time.
                            dicModel.setVocaDisplay(subtitleModel.getSubtitle());
                            dicModel.setVocaDisplayRuby(subtitleModel.getSubtitle());
                            dicModel.setMeaning(subtitleModel.getMeaning());
                            updateSubtitleInDB(dicModel);
                        } else {
                            onInsertABRepeat(subtitleModel.getSubtitle(), subtitleModel.getMeaning());
                            updateValue_SubtitleIndexFromCurrentTime();
                            if (isABRepeatMode()) {
                                exitABRepeatMode();
                            }
                        }
                        setAnalyzeAgain();
                        refreshSubtitle();
                        setVisibleLlRubyButtomSubtitle();
                        break;
                    case R.id.ivTranslate:
                        openWebTranslate(subtitleModel.getSubtitle());
//                        startActivity(PopupSubtitleTranslate.createIntent(activity, subtitleModel.getSubtitle()));
                        break;
                    case R.id.ivLMPlay:
                        clickPlayButton();
                        break;
                    case R.id.ivSTT:
                        onClickSubtitleTTS();
                        break;
                    case R.id.ivWebDictionary:
                        openWebDictionary(subtitleModel.getSubtitle());
                        break;
                }
            }

            @Override
            public void onDismiss(View view, Object object) {
                if (needToRestoreMinMaxSubAfterEditSubtitle) {
                    restoreMinMaxSubAfterEditSubtitle();
                }
            }
        });
        editSubtitleDialog.show();
//        startActivity(PopupEditSubtitle.createIntent(activity, onClickListenerEditSubtitle));
    }

    private void updateAudioSpeedValueOnButton() {
        binding.llPlay.layoutPlayerPlaySpeed.tvAudioSpeedValue.setText(getAudioSpeedToDisplay());
    }

    private void chooseSubtitleLangToDisplay(int displaySubtitleLang) {
        if (displaySubtitleLang == Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.STUDY) {
            updateSubtitleLangChooseImage(R.drawable.ic_subtitle_lang_study);
            isStudyLang = true;
            isTongueLang = false;
        } else if (displaySubtitleLang == Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.MEAING) {
            updateSubtitleLangChooseImage(R.drawable.ic_subtitle_lang_meaning);
            isStudyLang = false;
            isTongueLang = true;
        } else {
            updateSubtitleLangChooseImage(R.drawable.ic_subtitle_lang_both);
            isStudyLang = true;
            isTongueLang = true;
        }
        subtitleDialogAdapter.setDisplayLanguages(isStudyLang, isTongueLang);
        notifyDataSetChanged_subtitleDialogAdapter();
//        subtitleDialogAdapter.notifyDataSetChanged();
        showSubtitle(exoPlayer.getCurrentPosition(), -1);
    }

    private void updateSubtitleLangChooseImage(@DrawableRes int resId) {
        binding.llPlay.ivSubtitleLangChoose.setImageResource(resId);
        binding.layoutPlayerSubtitleTable.ivLMSubtitleLangChoose.setImageResource(resId);
    }

    private void resizeSubtitleFontSize(boolean isMakeBigger, float adjustValue) {
        if (isMakeBigger) {
            increaseSubtitleFontSize(adjustValue);
        } else {
            decreaseSubtitleFontSize(adjustValue);
        }
        refreshSubtitleFontSize();
    }

    private void increaseSubtitleFontSize(float adjustValue) {
        playerSubtitleFontSize += adjustValue;
        playerSubtitleFullScreenFontSize += (adjustValue * 2);
        DLog.d("isPinchToZoomSubtitle increaseSubtitleFontSize", "playerSubtitleFontSize : " + playerSubtitleFontSize);
    }
    private void decreaseSubtitleFontSize(float adjustValue) {
        playerSubtitleFontSize -= adjustValue;
        playerSubtitleFullScreenFontSize -= (adjustValue * 2);
        if (playerSubtitleFontSize < Constant.PLAYER.SETTING.SUBTITLE_FONT_SIZE_MIN) {
            playerSubtitleFontSize = Constant.PLAYER.SETTING.SUBTITLE_FONT_SIZE_MIN;
            playerSubtitleFullScreenFontSize = Constant.PLAYER.SETTING.SUBTITLE_FULL_SCREEN_FONT_SIZE_MIN;
        }
        DLog.d("isPinchToZoomSubtitle decreaseSubtitleFontSize", "playerSubtitleFontSize : " + playerSubtitleFontSize);
        DLog.d("isPinchToZoomSubtitle decreaseSubtitleFontSize", "playerSubtitleFullScreenFontSize : " + playerSubtitleFullScreenFontSize);
    }

    private void resetSubtitleFontSize() {
        playerSubtitleFontSize = Utils.getSPValue(activity, R.dimen.font_player_subtitle_ruby);
        playerSubtitleFullScreenFontSize = Utils.getSPValue(activity, R.dimen.font_player_subtitle_ruby_fullscreen);
        refreshSubtitleFontSize();
    }

    private void refreshSubtitleFontSize() {
        //Don't delete it.
//        ToastUtil.getInstance(activity).show(getString(R.string.message_subtitle_font_size, playerSubtitleFontSize, playerSubtitleFullScreenFontSize));
        setSubtitleFontSize();
        refreshSubtitleFontSizeInSubtitleTable();
        refreshSubtitleFontSizeOnFullScreen();
    }

    private void refreshSubtitleFontSizeOnFullScreen() {
        setSubtitleFontSizeInRubyBottom();
        showSubtitleAlways(exoPlayer.getCurrentPosition());
    }

    private void setSubtitleFontSizeInRubyBottom() {
        binding.tvRubyBottom.setTextSize(TypedValue.COMPLEX_UNIT_SP, playerSubtitleFullScreenFontSize);
    }

    private void refreshSubtitleFontSizeInSubtitleTable() {
        subtitleDialogAdapter.setPlayerSubtitleFontSize(playerSubtitleFontSize);
        notifyDataSetChanged_subtitleDialogAdapter();
    }

    private void setSubtitleFontSize() {
        sharedPreferences.setPlayerSubtitleFontSize(playerSubtitleFontSize);
        sharedPreferences.setPlayerSubtitleFullScreenFontSize(playerSubtitleFullScreenFontSize);
    }

    private void getSubtitleFontSize() {
        playerSubtitleFontSize = sharedPreferences.getPlayerSubtitleFontSize(activity);
        playerSubtitleFullScreenFontSize = sharedPreferences.getPlayerSubtitleFullScreenFontSize(activity);
    }

    @Override
    public void initView() {
        super.initView();
        if (FileUtil.isMusicApp()) {
            currentRotation = Surface.ROTATION_0;
            rotateScreenToPortrait();
            showAraMusicUI();
        } else {
            hideAraMusicUIForAraPlayer();
        }
        Loading.setCancelable(isPlayTitleByTtsBeforePlaying());
        Loading.show(activity);
        screenWidth = Utils.getDisplayDimensions(requireContext()).x;
        minimumPanelWidthPercent = (getResources().getDimension(R.dimen.minimum_panel_in_player_width) / screenWidth) * 100;
        getPlayerWidthPercent();
        initTTSTOSpeakMediaTitle();
        initSeekBarRepeatStartEnd();
        initSubtitleFontSize();
        setPlayDifficultWordsBeforePlayingSubtitleFalse();
        mScaleDetector = new ScaleGestureDetector(activity, this);

        createAndAddBlackOverlay();
        initOnClickListener();
    }

    //TODO : Sleep시 화면 꺼짐이 안되어서 임시로 검은화면을 띄움.
    private void createAndAddBlackOverlay() {
        // Create the black overlay view
        blackOverlay = new View(activity);
        blackOverlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        blackOverlay.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorBlack));
        blackOverlay.setVisibility(View.GONE);

        // Add the black overlay view to the root layout of the activity
        ViewGroup rootLayout = activity.findViewById(android.R.id.content);
        rootLayout.addView(blackOverlay);

        blackOverlay.setOnClickListener( v -> {
            dismissBlackOverlay();
        });
    }

    private void showBlackOverlay() {
        blackOverlay.setVisibility(View.VISIBLE);
    }

    private void dismissBlackOverlay() {
        blackOverlay.setVisibility(View.GONE);
//        handleClickOnPlayingScreen();
    }
    private void initTTSTOSpeakMediaTitle() {
        textToSpeechToSpeakMediaTitle = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                if (activity.isAudioFormat() && isPlayTitleByTtsBeforePlaying()) {
                    Locale menuLanguageLocale = EnumLanguage.findByFormatApi(sharedPreferences.getMenuLanguage()).getLocale();
                    int isLanguageAvailable = textToSpeechToSpeakMediaTitle.isLanguageAvailable(menuLanguageLocale);
                    if (isLanguageAvailable == TextToSpeech.LANG_NOT_SUPPORTED || isLanguageAvailable == TextToSpeech.LANG_MISSING_DATA) {
                        DLog.e(getLogTag(), "Lang not supported");
                        menuLanguageLocale = Locale.ENGLISH;
                    }
                    textToSpeechToSpeakMediaTitle.setLanguage(menuLanguageLocale);
                    //Dalnim : Some Korean characters are not played by TTS, so normalize with NFC
                    String ttsTitleIntroduce = getTtsTitleToIntroduceBeforePlayingMusic();
                    textToSpeechToSpeakMediaTitle.speak(ttsTitleIntroduce, TextToSpeech.QUEUE_FLUSH, null, ttsTitleIntroduce);
                    textToSpeechToSpeakMediaTitle.setOnUtteranceProgressListener(new UtteranceProgressListener() {

                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            isSettingNextFile = false;
                            //TODO : I want to play TTS for difficult words before playing a music.
                            checkToPlayDifficultWordsOrInitView();
                        }

                        @Override
                        public void onError(String utteranceId) {
                            isSettingNextFile = false;
                            checkToPlayDifficultWordsOrInitView();
                        }
                    });
                } else {
                    checkToPlayDifficultWordsOrInitView();
                }
            } else {
                checkToPlayDifficultWordsOrInitView();
            }

        });
    }

    private void checkToPlayDifficultWordsOrInitView() {
        if (isInitViewFinished) {
            startOverWhenItReachEndOfMediaMain();
        } else {
            if (FileUtil.isMusicApp() && playAllDifficultWordsBeforePlayingMusic()) {
                handlePlayAllDifficultWordBeforePlaying();
            } else {
                startInitViewDelay();
            }
        }
    }

    private String getTtsTitleToIntroduceBeforePlayingMusic() {
        String title = StringUtils.covertStringToNFC(activity.playerFileModel.getVideoModel().getTitleTts());
        String artist = StringUtils.covertStringToNFC(activity.playerFileModel.getVideoModel().getArtistTts());
        String result = getString(R.string.msg_introduce_media_title_before_playing, title);
        if (!Utils.isEmpty(artist))
                result = getString(R.string.msg_introduce_media_title_and_artist_before_playing, artist, title);
        return result;
    }

    private void startInitViewDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> initViewDelay(), 500);
    }

    @Override
    protected void getPlayerWidthPercent() {
        playerWidthPercent = sharedPreferences.getPlayerWidthPercent();
    }

    private void initViewDelay() {
        if (FileUtil.isMusicApp()) {
            ViewUtil.setViewListVisibility(View.VISIBLE, binding.llPlayTopAraMusic, binding.llPlay.layoutBtnCenterAraMusic,
                    binding.llPlay.ivPreviousSong, binding.llPlay.ivNextSong, binding.llPlay.ivShuffle, binding.llPlay.ivRepeatSong);
        }
        getLLPlayer().setOnTouchListener(this);
        registerObserver();
        initToolbar();
//        mAudioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        positionVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        binding.tvRubyBottom.settingForSubtitleOnFullScreen();
        binding.tvRubyBottom.setOnTouchListener(false);

        binding.llRubyBottom.setOnTouchListener(this);
        LayoutPlayerSubtitleTableBinding llSubtitleTable = binding.layoutPlayerSubtitleTable;
        llSubtitleTable.llLMView.setOnTouchListener(this);
        llSubtitleTable.ivLMSubEyeVertical.setOnTouchListener(this);
        llSubtitleTable.ivLMPlay.setOnTouchListener(this);
        llSubtitleTable.ivLMSubtitleLangChoose.setOnTouchListener(this);
        //llSubtitleTable.ivWebDictionary.setOnTouchListener(this);
        llSubtitleTable.ivLMABRight.setOnTouchListener(this);

        binding.tvShowButtonsKnowValueRight.setOnClickListener(new DoubleClick(oOnDoubleClickListener, null));

        binding.llDelaySubtitle.sbDelaySubtitle.setOnRangeChangedListener(onDelayRangeChangedListener);
        mMinMaxDelaySubtitle = sharedPreferences.getDelaySubtitleMinMaxValue();
        binding.llDelaySubtitle.icDelaySubtitleMinus.setOnTouchListener(onLongTouchIntervalListener);
        binding.llDelaySubtitle.icDelaySubtitlePlus.setOnTouchListener(onLongTouchIntervalListener);

        refreshDelaySubtitleUI(mMinMaxDelaySubtitle);
        initExoPlayer();
        initMediaPlayerEndMusicSound();
        createDialogAdapter();
        llSubtitleTable.rvListMeaning.setLayoutManager(getCurrentLinearLayoutManager());
        llSubtitleTable.rvListMeaning.addItemDecoration(new SeparatorDecoration(getActivity(), ContextCompat.getColor(requireContext(), R.color.backgroundCellSubtitleNormalColor), BaseBindUtils.getDividerHeight(requireContext())));
        binding.layoutPlayerSubtitleTable.rvListMeaning.setAdapter(subtitleDialogAdapter);
        initDialog();
        //DO this next time when I decide it
//        screen_resize_mode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT;
//        resizePlayerView();
        @DrawableRes int resId = R.drawable.ic_new_eye_open;
        setDefaultEyeImage(resId);
        if (FileUtil.isVideoApp()) {
            rotateScreenToLandScape();
        }
        initAdjustableBarView();
        isInitViewFinished = true;
        activity.runOnUiThread(() -> initData());
    }

    private void refreshDelaySubtitleUI(int minMaxDelaySubtitle) {
        binding.llDelaySubtitle.sbDelaySubtitle.setRange(minMaxDelaySubtitle * -1, minMaxDelaySubtitle);
        binding.llDelaySubtitle.tvDelayMin.setText(TimeUtil.getDisplay(minMaxDelaySubtitle * -1));
        binding.llDelaySubtitle.tvDelayMax.setText(TimeUtil.getDisplay(minMaxDelaySubtitle));
    }

    private void createDialogAdapter() {
        updateHideKnownDialogValueAndRefreshSubtitle(); //Dalnim : need to refresh mIsHideKnownDialogDuringPlaying before open SubtitleDialogAdapter.

        subtitleDialogAdapter = new SubtitleDialogAdapter(getActivity(),
                sharedPreferences.getDisplayPronunciation(),
                showAsterisk,
                mIsHideKnownDialogDuringPlaying,
                playerSubtitleFontSize,
                isRightHandMode,
                doubleClickHelper,
                subtitleDialogAdapterOnDoubleClickListener);
        subtitleDialogAdapter.setVideoModel(activity.playerFileModel.getVideoModel());
        subtitleDialogAdapter.setDisplayLanguages(isStudyLang, isTongueLang);
        subtitleDialogAdapter.setmIsDisplaySubtitleLangStudyFirst(mIsDisplaySubtitleLangStudyFirst);
        refreshSubtitleFontSizeInSubtitleTable();
    }

    private void initDialog() {
        registerVocaDialog = new RegisterVocaDialog(activity, onKnowChangeListenerForVocaWord);
        subtitleDialogCaption = new PlayerShowSubtitleGroupDialogCaption(activity, onSubtitleDialogCaptionListener);
//        subtitleDialogHided = new PlayerShowSubtitleDialogHided(activity, onSubtitleDialogHidedListener);
        singleChoiceDialog = new SingleChoiceDialog(activity);
        singleChoiceWithMessageDialog = new SingleChoiceWithMessageDialog(activity);
//        subTitleDialog = new PlayerShowSubtitleDialog(activity, isListenComprehensionMode(), isCCRepeatMode(), isABRepeatMode(), PlayerShowSubtitleDialog.SUBTITLE_TYPE.TABLE_VIEW, onSubtitleDialogListener);
        recyclerSubtitleViewDialog = new RecyclerViewDialog(activity, (view, object) -> {
            if ((Boolean) object) {
                recyclerSubtitleViewDialog.setUpdateData(false);
            }
            activity.playTTS.stop();
            activity.playTTS.playTTSHelper.resetPlayPlayer();

//            resetPlayingDifficultWord(); // Dalnim : With this, the Click CC Repeat icon shows non clicked image when I open StudyChatAdapter and return (In the Subtitle table view)
            playPlayer();
        });
        playerOptionDialog = new PlayerOptionDialog(activity, activity.playerFileModel, onPlayerSubtitleOptionSettingDialogListener);
        playerSettingDialog = new PlayerSettingDialog(activity, activity.playerFileModel, onPlayerSubtitleOptionSettingDialogListener);
    }

    @Override
    public void initData() {
        DLog.d(getLogTag(), "initData");
        subtitleListTotal = null;
        subtitleList = null;
        typeDisplay = Constant.PLAYER.SUB_TITLE.DISPLAY.NONE;
        isPlayingEndMusicSound = false;
        createBookmarkList();
//        activity.createSubDatabase(activity.playerFileModel);
        getFistIsStudyLangInSubtitle();
        activity.callAsyncTask(this, TYPE_INIT_DATA);
        if (isMusicPlaylist()) {
            getMusicPlayList();
        }
    }

    //Dalnim add : Now we always have VOCA_RUBY and MEANING columns in the SUBTITLE table, so we need to check they have real data now.
    private void checkSubtitleLangHasData() {
        isStudyLang = false;
        isTongueLang = false;
        isHasSubtitleBothLang = false;
        if (!Utils.isEmptyCollection(subtitleListTotal)) {
            for (DicModel dicModel : subtitleListTotal) {
                if (!dicModel.getVocaDisplayRuby().trim().equals(""))
                    isStudyLang = true;

                if (!dicModel.getMeaning().trim().equals(""))
                    isTongueLang = true;

                if (isStudyLang && isTongueLang) {
                    isHasSubtitleBothLang = true;
                    break;
                }
            }
        }

        activity.runOnUiThread(() -> {
            LayoutPlayerPlaySubBinding llPlaySub = binding.llPlaySub;
            if (isHasSubtitleBothLang) {
                ViewUtil.setViewListVisibility(View.VISIBLE, llPlaySub.ivSubtitleLangBoth, llPlaySub.ivSubtitleLangStudy, llPlaySub.ivSubtitleLangMeaning, binding.layoutPlayerSubtitleTable.ivLMSubtitleLangChoose);
                if (isFullscreenMode()) {
                    ViewUtil.setViewListVisibility(View.VISIBLE, binding.llPlay.ivSubtitleLangChoose);
                } else {
                    ViewUtil.setViewListVisibility(View.GONE, binding.llPlay.ivSubtitleLangChoose);
                }
            } else {
                ViewUtil.setViewListVisibility(View.GONE, binding.llPlay.ivSubtitleLangChoose, llPlaySub.ivSubtitleLangBoth, llPlaySub.ivSubtitleLangStudy, llPlaySub.ivSubtitleLangMeaning, binding.layoutPlayerSubtitleTable.ivLMSubtitleLangChoose);
            }
        });

        subtitleDialogAdapter.setDisplayLanguages(isStudyLang, isTongueLang);
    }

    private void updateInitData(Object resultData) {
        subtitleListTotal = (ArrayList<DicModel>) resultData;
        if (subtitleListTotal == null && !activity.playerFileModel.isShowRuby()) {
            // get subtitle from sub file
            subtitleListTotal = getSubtitleFromFile();
        }
        mapRecordedPathForSubtitleList(subtitleListTotal);
        subtitleList = new ArrayList<>(subtitleListTotal);
        mapSubtitleTotal = subtitleListTotal.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));

        checkSubtitleLangHasData();
        if (!isHasShowSubtitle()) {
            //If there is no subtitle file, then set the eye to gray as a default for HideEmbeddedSubtitle.
            binding.ivShowButtonsSubtitleEye.setImageResource(R.drawable.ic_new_eye_close_half);
        }
        generateSubtitleList();
        if (activity.getSubDatabase() != null)
            updateRepeatAll(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.DEAFULT_VALUE); //Make the repeat count to default(1) when a video is opened.
        getScreenSize();
        getTvTitle().setText(activity.playerFileModel.getName());
        if (FileUtil.isMusicApp()) {
            showMediaTitleWithMarquee(getTvTitle(), true, false);
            String title = StringUtils.covertStringToNFC(activity.playerFileModel.getVideoModel().getTitleTts());
            String artist = StringUtils.covertStringToNFC(activity.playerFileModel.getVideoModel().getArtistTts());
            binding.llPlay.tvTitle.setText(title);
            binding.llPlay.tvArtist.setText(artist);
        }
        playMediaFromBeginningOrFirstDialog(true);

        isBeingPlaying = sharedPreferences.getPlayVideoAutomaticallyWhenOpenIt() ? true : false;
        if (!isSettingNextFile) {
            setPlayyWhenReady(isBeingPlaying);
        }
        if (!isHasShowSubtitle()) {
            Loading.hide();
        }
        updateInitLayout(Constant.PLAYER.SUB_TITLE.DISPLAY.SUBTITLE_LIST);
    }

    private void getFistIsStudyLangInSubtitle() {
        DLog.d(getLogTag(), "getSubtitleLanguage");
        subtitleLanguageModels = new ArrayList<>();
        subtitleLanguageModels = SubtitleUtil.getSubtitleLanguageModels(activity, activity.playerFileModel, activity.getSubDatabase());
        mIsDisplaySubtitleLangStudyFirst = activity.playerFileModel.getVideoModel().getIsDisplaySubtitleLangStudyFirst() == Constant.INT_BOOLEAN.TRUE ? true : false;
        subtitleDialogAdapter.setmIsDisplaySubtitleLangStudyFirst(mIsDisplaySubtitleLangStudyFirst);
        notifyDataSetChanged_subtitleDialogAdapter();
        DLog.d(getLogTag(), "getSubtitleLanguage - isStudyLang=" + isStudyLang + " - isTongueLang=" + isTongueLang);
    }

    private List<DicModel> getSubtitleData() {
        if (isHasSubtitleLanguage()
                && activity.getSubDatabase() != null
                && activity.playerFileModel.isShowRuby()) {
            return getSubtitleFromDB();
        }
        return new ArrayList<>();
    }
    // 이건 지금은 쓰는곳이 없다.
    private ArrayList<DicModel> getSubtitleFromFile() {
        final ArrayList<DicModel> list = new ArrayList<>();
        final ArrayList<SubModel> subList = new ArrayList<>();
        subList.addAll(SubModelQuery.getByPath(Voca.getRealm(), activity.playerFileModel.getPath()));
        if (!subList.isEmpty()) {
            for (SubModel item : subList) {
                list.add(new DicModel(item));
            }
        }
        return list;
    }

    private List<DicModel> getSubtitleFromDB() {
        List<DicModel> list = new ArrayList<>();
        list.addAll(activity.getSubDatabase().getSubtitleDialogListUsed());
        rubyTextModels = getRubyTextModels();
        MergeUtil.generateMeaning(list, rubyTextModels);
        return list;
    }

    private boolean isHasShowSubtitle() {
        return activity.playerFileModel.isShowRuby() && isHasSubtitleLanguage() && isHasSubtitleTotal();
    }

    private boolean isHasSubtitleLanguage() {
        return subtitleLanguageModels != null && !subtitleLanguageModels.isEmpty();
    }

    private boolean isHasSubtitleTotal() {
        return !Utils.isEmpty(subtitleListTotal);
//        return subtitleListTotal != null && !subtitleListTotal.isEmpty();
    }

    //TODO : 이건 체크할 필요가 없음 듣기연습1에서 모드는 최소 하나는 무조건 있음.
    public boolean isHasListenComprehension1() {
        return listenComprehension1ModelList != null && !listenComprehension1ModelList.isEmpty();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DLog.d(getLogTag(), "onConfigurationChanged");
        getScreenSize(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        DisplayManager displayManager = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(mDisplayListener, new Handler(Looper.getMainLooper()));
        activity.runOnUiThread(() -> handleOnResume());
    }

    private void handleOnResume() {
        positionBrightness = Utils.getSystemBrightness(activity);
//        updateForwardBackwardValue();
        activity.playTTS.setIncludeMeaning(false);

        updateValueFromSharedPreferences();

//        getIncludeMotherTongueSubtitle();
//        getPlayDifficultWordsBeforePlayingSubtitle();

//        initPlayVocaHelperListener();
        if (activity.playerFileModel == null) return;
        createBookmarkList();
        restoreShowAsteriskFromBefore();
        VideoModel tmpVideo = null;
        if (activity.playerFileModel != null && activity.playerFileModel.getVideoModel() != null && !Utils.isEmpty(activity.playerFileModel.getVideoModel().getPath())) {
            tmpVideo = VideoModelQuery.getByPath(Voca.getRealm(), activity.playerFileModel.getPath());
            if (FileUtil.isMusicApp() && !TextUtils.isEmpty(tmpVideo.getSubPath()) && !tmpVideo.getSubPath().equals(activity.playerFileModel.getSubPath())) {
                activity.playerFileModel.setVideoModel(tmpVideo);
                String subPath = activity.playerFileModel.getSubPath();
                if (FileUtil.isTxtFormat(subPath)) {
                    showLyricTxtView();
                    if (!TextUtils.isEmpty(subPath)) {
                        showTxtLyric(subPath);
                    }
                    setPlayyWhenReady(true);
                } else {
                    isAnalyzingLyric = true;
                    setAnalyzeAgain();
                    activity.callAsyncTask(this, TYPE_ANALYZE_NEW_LYRIC_FILE);
                }
            }
        }
        // check and refresh if display START END time changed from Setting View
        if (subtitleDialogAdapter != null) {
            getFistIsStudyLangInSubtitle(); //Dalnim add : to refresh first displaying subtitle langauge when I change it in the Option view.
            boolean isRefresh = false;

            if (isDataChangeFromPhraseInfor) {
                if (!Utils.isEmpty(idsDataChangeFromPhraseInfor)) {
                    updateSubtitleModelBySubtitleIdList(idsDataChangeFromPhraseInfor);
                    isRefresh = true;
                    updateValue_SubtitleContent(Constant.BASE_BLANK);
                }
                isDataChangeFromPhraseInfor = false;
                idsDataChangeFromPhraseInfor = Constant.BASE_BLANK;
            }

            // check data from option view
            if (tmpVideo != null) {
                if (activity.playerFileModel.getVideoModel().getPlayBeforeSubtitle() != tmpVideo.getPlayBeforeSubtitle() ||
                        activity.playerFileModel.getVideoModel().getPlayAfterSubtitle() != tmpVideo.getPlayAfterSubtitle() ||
                        activity.playerFileModel.getVideoModel().getKeepPlayBetweenSubtitle() != tmpVideo.getKeepPlayBetweenSubtitle()) {
                    activity.playerFileModel.setVideoModel(tmpVideo);
                    isRefresh = true;
                    subtitleDialogAdapter.setVideoModel(activity.playerFileModel.getVideoModel());
                }
            }

            if (!Utils.isEmpty(idsFromEditSubtitle)) {
                // update videoModel when back from EditSubtitle screen
                activity.playerFileModel.setVideoModel(tmpVideo);
                activity.callAsyncTask(this, idsFromEditSubtitle, TYPE_UPDATE_FROM_EDIT_SUBTITLE);
                return;
            }
            if (isRefresh) {
                refreshSubtitle();
            }
        }
        updateVisibilityButtonsOnFullScreen();
        getSubtitleFontSize();
        setSubtitleFontSizeInRubyBottom();
        hideAllControlsOverPlayingScreen();
        setSubtitleViewBackgroundColorOnFullScreenMode();
        mDisplaySubtitleLang = Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.BOTH;
        isSubtitleEdited = false;
        updateValue_SubtitleContent("");
        if (isReloadAllSubtitleAgain) {
            isReloadAllSubtitleAgain = false;
            activity.callAsyncTask(this, null, TYPE_RELOAD_SUBTITLE_LIST_FROM_DB);
            // Both don't work.
//            generateSubtitleList(subtitleGroupType);
            //or call this.
//            activity.callAsyncTask(this, TYPE_INIT_DATA);

        }
        if (isReloadAllSubtitleTotalAgain) {
            isReloadAllSubtitleTotalAgain = false;
            activity.callAsyncTask(this, null, TYPE_RELOAD_SUBTITLE_LIST_TOTAL_FROM_DB);
            // Both don't work.
//            generateSubtitleList(subtitleGroupType);
            //or call this.
//            activity.callAsyncTask(this, TYPE_INIT_DATA);

        }
//        if (!isSleeping && !isAnalyzingLyric) {
        if (!isAnalyzingLyric) {
            doNotResumePlayingWhenWordlistDialogIsShowing();
        }
        hideButtonsOnFullScreenWithAnimation();
        enableChangeTableWidthWhenItIsDisplayingOnResume();
        updateValue_isPlayingSTT_false();
        setScrollableMenuModeList();
    }

    private void enableChangeTableWidthWhenItIsDisplayingOnResume() {
        if (isEnableChangeTableWidth) {
            enableChangeTableWidth();
        }
    }

    private void updateValueFromSharedPreferences() {
        updateForwardBackwardValue();
    }


    private void setPlayDifficultWordsBeforePlayingSubtitleFalse() {
        sharedPreferences.setPlayDifficultWordsBeforePlayingSubtitles(false);
    }

    private boolean isPlayDifficultWordsBeforePlayingSubtitle() {
        return sharedPreferences.getPlayDifficultWordsBeforePlayingSubtitles();
    }

    private boolean isIncludeMotherTongueSubtitle() {
        return sharedPreferences.getIncludeMotherTongueSubtitle();
    }



    private boolean playAllDifficultWordsBeforePlayingMusic() {
        return sharedPreferences.getPlayAllDifficultWordsBeforePlayingMusic();
//        return sharedPreferences.getPlayDifficultWordsBeforePlayingSubtitles();
    }

    private void refreshSubtitle() {
//        subtitleDialogAdapter.setData(subtitleList, subtitleGroupType);
//        notifyDataSetChanged_subtitleDialogAdapter();
//        if (isFullscreenMode()) {
//            showSubtitle(exoPlayer.getCurrentPosition(), -1);
//        }
// TODO : Update와 Modiyfy를 구분해야 함. subtitleListTotal가 현재 0임
//        if (Utils.isEmpty(subtitleLanguageModels)) {
//            getFistIsStudyLangInSubtitle();
//            checkSubtitleLangHasData();
//        }

        handleAfterModifySubtitle();
    }

    @Override
    public void onPause() {
        DLog.d(getLogTag(), "onPause");
        super.onPause();
        DisplayManager displayManager = (DisplayManager) activity.getSystemService(Context.DISPLAY_SERVICE);
        displayManager.unregisterDisplayListener(mDisplayListener);
    }

    @Override
    public void onStop() {
        DLog.d(getLogTag(), "onStop");
        super.onStop();
        if (!sharedPreferences.getKeepPlayingOnBackgroundMode()) {
            super.handlePlayClick(false, false);
        }
    }

    @Override
    public void onDestroyView() {
        if(textToSpeechToSpeakMediaTitle != null){
            textToSpeechToSpeakMediaTitle.stop();
            textToSpeechToSpeakMediaTitle.shutdown();
        }
        super.onDestroyView();
    }

    @Override
    @Subscribe
    public void onEvent(SuccessEvent event) {
        super.onEvent(event);
        if (event.getScreen() == BaseEvent.Screen.EDIT_VOCA) {
            switch (event.getEventType()) {
                case SAVE_REFRESHED_VOCA_IN_DIC_TABLE_IN_SUBTITLE_DB:
                case DATA_CHANGED:
                    EditVoca editVoca = (EditVoca) event.getModel();
                    //Dic DB의 Dic테이블을 먼저 업데이트 해서, 혹시 기존의 VocaId가 마이너스면 새로운 VocaId를 사용할수 있게한다.
                    if (event.getEventType() == BaseEvent.EventType.DATA_CHANGED) {
                        activity.getDicDatabase().updatePhraseInformationOnDicDb(editVoca);
                    }
                    activity.getSubDatabase().updatePhraseInformation(editVoca);

                    String subtitleIdsThatContainThisVoca = activity.getSubDatabase().getIdOfSubtitleFromPhraseInformation(editVoca.getiVocaFullItem().getVIVocaId());

                    this.idsDataChangeFromPhraseInfor = subtitleIdsThatContainThisVoca;
                    this.vocaIdDataChangeFromPhraseInfor = editVoca.getiVocaFullItem().getVIVocaId().toString();
                    this.oldVocaIdDataChangeFromPhraseInfor = editVoca.getOldVocaId().toString();
                    this.isDataChangeFromPhraseInfor = true;
            }
        } else if (event.getScreen() == BaseEvent.Screen.DAL_PLAYER) {
            switch (event.getEventType()) {
                case KEYBOARD_EVENT:
                    onKeyboardEvent((Integer) event.getModel());
                    break;
                case PLAYER_EDIT_SUBTITLE:
                    idsFromEditSubtitle = (String) event.getModel();
                    break;
                case CANCEL_PROGRESS_DIALOG:
                    stopTTSWhenPressBack();
                    break;
            }
        } else if (event.getScreen() == BaseEvent.Screen.EDIT_SUBTITLE) {
            switch (event.getEventType()) {
                case PLAYER_EDIT_SUBTITLE:
                    idsFromEditSubtitle = (String) event.getModel();
                    break;
            }
        } else if (event.getScreen() == BaseEvent.Screen.WORD_INFO_FRAGMENT) {
            IVocaFullItem iVocaFullItem = (IVocaFullItem) event.getModel();
            switch (event.getEventType()) {
                case VOCA_KNOW_CHANGED:
                    updateVocaKnow(iVocaFullItem);
                    break;
                case BOOKMARK_CHANGED:
                    updateBookmark(iVocaFullItem);
                    break;
            }
            isReloadAllSubtitleAgain = true;
        } else if ((event.getScreen() == BaseEvent.Screen.BASE_PLAYER_VIDEO_FRAGMENT) ||
                (event.getScreen() == BaseEvent.Screen.DICTATION_MODE)) {
            switch (event.getEventType()) {
                case REAOAD_SUBTITLE_AGAIN:
                    //Want to call applyKnowValueInSubtitle(iVocaBasicItem); but it's not called when I'm from Dictation mode or BASE_PLAYER_VIDEO_FRAGMENT
                    IVocaFullItem iVocaFullItem = (IVocaFullItem) event.getModel();
                    applyKnowValueInSubtitle(iVocaFullItem);
//                        updateKnow(iVocaFullItem);

                    //Need to reload all subtitles again when onResume is called. But don't know how to reload all subtitles
                    isReloadAllSubtitleAgain = true;
                    break;
            }
        } else if (event.getScreen() == BaseEvent.Screen.NOTIFICATION) {
            switch (event.getEventType()) {
                case MEDIA_BUTTON_PLAY:
                case MEDIA_BUTTON_PAUSE:
                    clickPlayButton();
                    break;
            }
        }
    }

    private void checkAndShowSubTitle(long position) {
        if (exoPlayer == null || progressTracker == null ||
                activity.playerFileModel == null ||
                !isHasSubtitleLanguage() || !isHasSubtitle()) {
            fullScreenSubtitleTextViewGone();
            return;
        }
//        if (isListenComprehensionMode()) {
        if (isRepeat) {
            if (!isHasListenComprehension1() || (isDisplayListenComprehension1() && listenComprehension1ModelList.get(listComprehensionModelIndex).isHideTheSubtitle())) {
                if (isCCRepeatMode()) {
                    //Make it change showAsterisk when CC Repeat in ListenComprehension 1
                    refreshSubtitleUnlessSameSubtitle(position);
                } else {
                    //TODO : Need to check the 2 methods and which one is correct to use.
                    //Hide subitlte if it's hide in ListenComprehension 1
                    if (isListenComprehensionMode()) {
                        setHideSubtitle();
                    } else {
                        refreshSubtitleUnlessSameSubtitle(position);
                    }
                }
            } else {
                refreshSubtitleUnlessSameSubtitle(position);
            }
        } else {
            showSubtitle(position);
        }
    }

    private void refreshSubtitleUnlessSameSubtitle(long position) {
        // Dalnim : get previous dialog if can't get current dialog and use mIsOnEmptyDialog to check I'm on empty dialog or not.
        String content = Constant.BASE_BLANK;
        DLog.d("dalnim", "subtitleIndex=" + subtitleIndex);
        int newSubtitleIndex = getSubtitleIndexFromCurrentTime();
        if (!mIsOnEmptyDialog && (newSubtitleIndex >= 0) && (newSubtitleIndex < subtitleList.size())) {
            DicModel item = getDicModel(newSubtitleIndex);
            content = item.getTextDisplayRubyByLang(activity.playerFileModel.isShowRuby(), isStudyLang, isTongueLang, mIsDisplaySubtitleLangStudyFirst);
        }

        if (isShowSubtitleAlways != true) {
            if (subtitleContent.equals(content) && subtitleIndex == newSubtitleIndex) {
                DLog.d("showSubtitle111", "don't update subtitle because they are same contents and same subtitleIndex : " + subtitleIndex);
                DLog.d("showSubtitle111", "subtitleContent" + subtitleContent);
//                ToastUtil.getInstance(activity).show("same content");
                return;
            }
//            ToastUtil.getInstance(activity).show(content);
            //During Listen 2, don't need to show pre/next subtitle, but only show current subtitle from Adjust start time when it's ShowSubtitle(2nd repeat)
            //듣기2일때 현재 반복 자막을 Adjust start 시간부터 보여준다.(여기서 return을 안하면 자막을 이전자막을 보여줄수 있다) 그리고 이전/이후 자막은 듣기2에서는 안보여준다. (Adjust start 시간 말고 현재 자막시간부터 보여주는건 필요하면 별도로 개발해야함)
            //어느 코드가 Adjust start 시간부터 보여주는지 모르겠음. 현재 자막 듣기2의 마지막은 Adjust end까지는 안보여주고 싶은데 어디서 해야 할지 모르겠음.
//            if (isDisplayListenComprehension2() && subtitleIndex != newSubtitleIndex) {
            if (isDisplayListenComprehension2() && isInRepeatCurrentSubtitleOfListenMode2) {
                if (subtitleIndex != newSubtitleIndex) {
                    DLog.d("showSubtitle111", "don't show subtitle in Listen 2 mode, if it's pre/next subtitle : " + subtitleIndex);
                    DLog.d("showSubtitle111", "subtitleContent" + subtitleContent);
                    //아래 문구가 없으면 자막이 끝나도 다음자막때까지 (현재 자막이 빈자막일때) 빈 현재 자막을 계속 보여준다.(아는 자막이면 보이는 상태로 계속 보여준다) 그래서 빈자막이면 return하지 않고 자막을 갱신해야한다.
                    if (!mIsOnEmptyDialog) {
                        DLog.d("showSubtitle111", "!mIsOnEmptyDialog and return");
                        return;
                    }
                }
                //이게 없으면 듣기2에서 자막이 보일때, 맨 앞에 한번 잠시 보였다가 숨겨지고, 또 다시 제대로 보인다. 그리고 자막이 끝나면 숨겨버린다.(자막이 끝나도 듣기 2에서는 Adjust End Time까지 계속 보이게 해야한다)
                if (content.trim().equals("")) {
                    DLog.d("showSubtitle111", "content empty and return");
                    return;
                }
            }

            //듣기 1일때 반복 그룹내의 자막이 아닌건 안보여줄려고 (자막 그룹전이나 후의 자막이 잠깐씩 보일때가 있는데, 이걸 없애준다.)
            if (isDisplayListenComprehension1()) {
                int startSubtitleIndexComprehension1SubtitleList = getStartSubtitleIndexInListen1();
                int endSubtitleIndexComprehension1SubtitleList = getEndSubtitleIndexInListen1();

                DLog.d("showisDisplayListenComprehension1", "\n\ndon't show subtitle in Listen 1 mode");
                DLog.d("showisDisplayListenComprehension1", "position : " + position);
                DLog.d("showisDisplayListenComprehension1", "subtitleIndex : " + subtitleIndex);
                DLog.d("showisDisplayListenComprehension1", "subtitleContent : " + subtitleContent);
                DLog.d("showisDisplayListenComprehension1", "newSubtitleIndex : " + newSubtitleIndex);
                DLog.d("showisDisplayListenComprehension1", "content : " + content);
                DLog.d("showisDisplayListenComprehension1", "startSubtitleIndexComprehension1SubtitleList : " + startSubtitleIndexComprehension1SubtitleList);
                DLog.d("showisDisplayListenComprehension1", "endSubtitleIndexComprehension1SubtitleList : " + endSubtitleIndexComprehension1SubtitleList);
                DLog.d("showisDisplayListenComprehension1", "mIsOnEmptyDialog : " + mIsOnEmptyDialog);
                DLog.d("showisDisplayListenComprehension1", "listComprehensionCurrentIndex : " + listComprehensionCurrentIndex);
                DLog.d("showisDisplayListenComprehension1", "listComprehensionModelIndex : " + listComprehensionModelIndex);

                if (subtitleIndex != newSubtitleIndex) {
                    if ((newSubtitleIndex < startSubtitleIndexComprehension1SubtitleList)
                            || (newSubtitleIndex > endSubtitleIndexComprehension1SubtitleList)){
                        content = ""; //여기서 보여줄 자막을 공백으로 만들어서 보여준다.
                        DLog.d("showisDisplayListenComprehension1", "=====");
                        DLog.d("showisDisplayListenComprehension1", "subtitleIndex != newSubtitleIndex");
                        DLog.d("showisDisplayListenComprehension1", "=====");
                    }
                }
            }
        }
        updateValue_SubtitleContent(content);

        //This code don't update subtitleIndex during Listen Comprehension mode and delaySubtitle mode(Must show same subtitle). Those mode must not update subtitleIndex here, because the should keep current subtitleIndex
        //Side effect. Can't play difficult word TTS after one round, don't go position in the subtitle table view to next subtitle.
        updateSubtitleIndexWhenNeededOnly(newSubtitleIndex);
//        updateValue_SubtitleIndex(newSubtitleIndex); //Don't use this.

        DLog.d("showSubtitle", "position=" + position + ", subtitleIndex=" + subtitleIndex + ", newSubtitleIndex=" + newSubtitleIndex + ", content" + content);

        if (Utils.isEmpty(content) && !isVerticalMode() && !isDelaySubtitleMode()) {
            DLog.d(getLogTag(), "content is null - newSubtitleIndex=" + newSubtitleIndex);
            fullScreenSubtitleTextViewGone();
            if (isDisplaySubtitle()) {
                subtitleDialogAdapter.resetLastCheckedPosition();
            }
        }

        final DicModel item = getDicModel(newSubtitleIndex);

        //If have this code, the subtitle table view can scroll in this case also. (I want to stay in this case, just update KNOW words only)
        //ex) Current subtitle index is 1, I scroll to 100th item and change word's know in the 100th item(Current item is still 100),
        // but with this code, AraPlayer scrolls to 1st item again. I want to stay 100 item.
        // But this code needs in other cases, to scroll automatically
        if (isShowSubtitleAlways != true) {
            scrollToPositionSubtitleDialog();
        }

        // update subtitle
        if (isFullscreenMode() && item != null && !Utils.isEmpty(content)) {
            getSubtitleMeaningData(item, content, item.getMeaningWords());
        }
    }

    private void updateSubtitleIndexWhenNeededOnly(int newSubtitleIndex) {
        if (isListenComprehensionMode()
                || mIsOnDelaySubtitle )
            return;

        updateValue_SubtitleIndex(newSubtitleIndex);
        getMinMaxTime(getDicModel(newSubtitleIndex));
    }


    private void showSubtitle(long position) {
        showSubtitle(position, subtitleIndex);
    }

    private void showSubtitleAlways(long position) {
        isShowSubtitleAlways = true;
        showSubtitle(position, -1);
        isShowSubtitleAlways = false;
    }


    private void showSubtitle(long position, int oldSubtitleIndex) {
        if (!isHasSubtitle() ||
                position > getEndTime(getDicModel(subtitleList.size() - 1))) {
            //position > getDicModel(subtitleList.size() - 1).getEndTime()) {
            fullScreenSubtitleTextViewGone();
            if (isDisplaySubtitle()) {
                subtitleDialogAdapter.resetLastCheckedPosition();
            }
            return;
        }

        if (isRepeat && timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.NONE) {
            if (oldSubtitleIndex < 0)
                oldSubtitleIndex = getSubtitleIndexFromCurrentTime();
            DicModel item = getDicModel(oldSubtitleIndex);

            if (item != null) {
                String content = item.getTextDisplayRubyByLang(activity.playerFileModel.isShowRuby(), isStudyLang, isTongueLang, mIsDisplaySubtitleLangStudyFirst); //Dalnim add
                getSubtitleMeaningData(item, content, item.getMeaningWords(), true);
            }
        } else {
            // Dalnim : get previous dialog if can't get current dialog and use mIsOnEmptyDialog to check I'm on empty dialog or not.
            refreshSubtitleUnlessSameSubtitle(position);
        }
    }

    private void updatePreviousNextDialogOnDelaySubtitleMode() {
        int subtitleIndexTemp = subtitleIndex;
        if (subtitleIndexTemp < 0)
            subtitleIndexTemp = 0;
        if (subtitleIndexTemp > subtitleList.size() - 1)
            subtitleIndexTemp = subtitleList.size() - 1;

        Integer previousSubtitleIndex = subtitleIndexTemp - 1;
        Integer previousSubtitleIndex2 = subtitleIndexTemp - 2;
        Integer nextSubtitleIndex = subtitleIndexTemp + 1;
        Integer nextSubtitleIndex2 = subtitleIndexTemp + 2;
        if (previousSubtitleIndex2 >= 0) {
            final DicModel item = getDicModel(previousSubtitleIndex2);
            binding.llDelaySubtitle.tvDialogPrevious2.setText(getDisplaySubtitleElseMeaningOnDelaySUbtitleMode(item));
        }
        if (previousSubtitleIndex >= 0) {
            final DicModel item = getDicModel(previousSubtitleIndex);
            binding.llDelaySubtitle.tvDialogPrevious.setText(getDisplaySubtitleElseMeaningOnDelaySUbtitleMode(item));
        }


        if (nextSubtitleIndex2 < subtitleList.size()) {
            final DicModel item = getDicModel(nextSubtitleIndex2);
            binding.llDelaySubtitle.tvDialogNext2.setText(getDisplaySubtitleElseMeaningOnDelaySUbtitleMode(item));
        }
        if (nextSubtitleIndex < subtitleList.size()) {
            final DicModel item = getDicModel(nextSubtitleIndex);
            binding.llDelaySubtitle.tvDialogNext.setText(getDisplaySubtitleElseMeaningOnDelaySUbtitleMode(item));
        }
    }

    private String getDisplaySubtitleElseMeaningOnDelaySUbtitleMode(DicModel item) {
        return StringUtils.removeLineBreaks(Utils.isEmpty(item.getVocaDisplay()) ? item.getMeaning() : item.getVocaDisplay());
    }

    private void fullScreenSubtitleTextViewGone() {
        if (binding.tvRubyBottom.getVisibility() == View.GONE || binding.tvRubyBottom.getVisibility() == View.INVISIBLE)
            return;
        DLog.d(getLogTag(), "subtitleTextViewGone");
        binding.tvRubyBottom.resetText();
        setVisibleTvRubyBottomSubtitle(View.INVISIBLE);
        updateVisibilityButtonsOnFullScreen();
    }

    private void findNextSubTitle() {
        if (!isRepeat || !isHasSubtitle()) return;

        activity.runOnUiThread(() -> {
            final long position = exoPlayer.getCurrentPosition();
            for (int i = 0; i < subtitleList.size(); i++) {
                DicModel dicModel = getDicModel(i);
                DicModel nextDicModel = getDicModel(i + 1);
                if (position >= getStartTime(dicModel) && position <= getEndTime(dicModel) && position < (nextDicModel == null ? position : getStartTime(nextDicModel))) {
                    DLog.d(getLogTag(), "findNextSubTitle position=" + position);
                    getMinMaxTime(dicModel);
                    updateRangeSeek();
                    return;
                }
            }
        });
    }

    private void checkDuration(int playbackState) {
        DLog.d(getLogTag(), "checkDuration - playbackState=" + playbackState + " - duration=" + exoPlayer.getDuration());
        if (playbackState == ExoPlayer.STATE_ENDED) {
            if (activity.playerFileModel.getVideoModel().getWatchDuration() < activity.playerFileModel.getVideoModel().getDuration()) {
                activity.playerFileModel.getVideoModel().setWatchDuration(activity.playerFileModel.getVideoModel().getDuration());
                activity.updateVideoModel(activity.playerFileModel.getVideoModel());
            }

            if (isABRepeatMode_A_Button_Clicked()) {
                beginABRepeatWhenItReachEndOfVideo();
            } else {
                startOverWhenItReachEndOfMedia();
            }
        }
        if (playbackState == ExoPlayer.STATE_READY && !isDurationSet) {
            VideoUtil.setLastPlayedPath(activity, activity.playerFileModel);
            activity.updateVideoModel(activity.playerFileModel.getVideoModel());
//            Loading.hide();
        }
    }

    @Override
    protected void handleSwipeToMovePrevNextDialog(boolean isLeft) {
        if (!isHasSubtitle() || !isAllowToMovePrevNextDialog() || isABRepeatMode()) {
            return;
        }
        //Dalnim : OLD - If I swipe on the screen and move a few mintues, the subtitle index can be wrong, so I get the current or previous dialog(if it's empty dialog) again.
        //               Only when I swipe on the subtitle view by moving 1 dialog, then subtitleIndex is correct.
        //Dalnim : In DelaySubtitleMode, sometimes can't go to next dialog, so just use current subtitle index and do ++ or --.
        // If SubtitleList has 1 item, mIsOnEmptyDialog is not correct sometimes. so call updateValue_SubtitleIndexFromCurrentTime to get mIsOnEmptyDialog correctly
//        if ((subtitleIndex <= 0) || (subtitleIndex >= subtitleList.size())) {
        updateValue_SubtitleIndexFromCurrentTime();
//        }
        //If I have this code, before 1st subtitle I can't swipe to go first subtitle, because subtitleIndex is -1
//        if (!Utils.isIndexInsideRange(subtitleList, subtitleIndex))
//            return;

        activity.runOnUiThread(() -> {
            dontShowSubtitleSlideAnimationDuringRepeat(isLeft);
            DicModel dicModelCurrent = getDicModel(subtitleIndex);
//            int subtitleIndexPrevNext = subtitleIndex; //이걸 쓰니까, CC반복중에 자막 앞뒤로 가니까, 자막 테이블의 배경색이 이전 자막에 칠해짐. (CC반복을 안할때는 괜찮았음)
            if (isLeft) {
                //Dalnim : if we don't check this, when I swipe on "No dialog 2" to go previous "Dialog 2", it goes to "Dialog 1"
                //So only go to previous dialog if I'm not on empty dialog.
                if (!mIsOnEmptyDialog) {
                    subtitleIndex--;
                }
                if (subtitleIndex < 0) {
                    updateValue_SubtitleIndex(0);
//                    subtitleIndex = 0;
                    updateSubtitleIndexBySwipingToMoveSubtitle(isLeft, subtitleIndex);
                    ToastUtil.getInstance(activity).show(R.string.first_dialogue);
                    return; //Dalnim Add : Don't have to do more if it's first/last dialog
                }

                DicModel dicModelPrevious = getDicModel(subtitleIndex);
                if (dicModelCurrent.getStartTime() < dicModelPrevious.getStartTime()) {
                    ToastUtil.getInstance(activity).show("Check the subtitle's time. Previous dialog's start time is greater than current dialog's start time");
                }
            } else {
                subtitleIndex++;
                if (subtitleIndex > subtitleList.size() - 1) {
                    updateValue_SubtitleIndex(subtitleList.size() - 1);
//                    subtitleIndex = subtitleList.size() - 1;
                    updateSubtitleIndexBySwipingToMoveSubtitle(isLeft, subtitleIndex);
                    ToastUtil.getInstance(activity).show(R.string.last_dialogue);
                    return; //Dalnim Add : Don't have to do more if it's first/last dialog
                }

                DicModel dicModelNext = getDicModel(subtitleIndex);
                if ((dicModelCurrent != null) && (dicModelCurrent.getEndTime() > dicModelNext.getEndTime())) {
                    ToastUtil.getInstance(activity).show("Check the subtitle's time. current dialog's end time is greater than next dialog's end time");
                }
            }
            // update and show again subtitle
            updateSubtitleIndexBySwipingToMoveSubtitle(isLeft, subtitleIndex);

            //STOP TTS just in case, need to update code to stop TTS when it's playing. TTS를 플레이 하고 있을수도 있으니 다른 자막으로 가면 멈춘다.
            if (isListenComprehensionMode()) {
                activity.playTTS.stop();
                activity.playTTS.playTTSHelper.resetPlayPlayer();
            }
        });
    }

    private void updateSubtitleIndexBySwipingToMoveSubtitle(boolean isLeft, int subtitleIndexPrevNext) {
        updateSubtitleIndexBySwipingToMoveSubtitleMain(getDicModel(subtitleIndexPrevNext), isLeft);
        updateValue_SubtitleIndex(subtitleIndexPrevNext);
    }

    private void dontShowSubtitleSlideAnimationDuringRepeat(boolean isLeft) {
        if (isRepeat || isListenComprehensionMode()) {
        } else {
            Animation animation;
            if (isLeft) {
                animation = AnimationUtils.loadAnimation(requireContext(), R.anim.subtitle_slide_left);
            } else {
                animation = AnimationUtils.loadAnimation(requireContext(), R.anim.subtitle_slide_right);
            }
            binding.tvRubyBottom.setAnimation(animation);
        }
    }

    private void handleSwipeTimeToMovePrevNextDialogDuringListenComprehensionMode() {
        if (!isHasSubtitle() || !isAllowToMovePrevNextDialog() || isABRepeatMode() || !isListenComprehensionMode()) {
            return;
        }
        int subtitleIndexPrevious = subtitleIndex;
        updateValue_SubtitleIndexFromCurrentTime();

        updateSubtitleIndexBySwipingToMoveSubtitleMain(getDicModel(subtitleIndex), subtitleIndexPrevious > subtitleIndex);
    }

    public void seekToInPlayer_MinSubWithDelayTime_ForSwipeToMovePrevNextDialog() {
        //어떤 영화는 minSub만큼 옮겨도 exoplayer의 position이 정확하게 minSub안간다. 특히 문제가 minSub보다 앞으로 가면 자막이 깜박이느 버그가 발생한다. 그래서 버퍼값을 추가한다.
        //버그 발생이유는 자막 바로 앞으로 가기 때문에 이전자막이나 빈자막이 한번 보였다가 minSub가 있는 자막이 다시 보여서 깜빡거리게 보이게 된다.
        int extraTime = 100;
        if (needToAddExtraTime_ForSwipeToMovePrevNextDialog(extraTime)) {
            seekToInPlayer((long) minSub + extraTime);
        } else {
            seekToInPlayer((long) minSub);
        }

    }

    private boolean needToAddExtraTime_ForSwipeToMovePrevNextDialog(int extraTime) {
        boolean result = false;
        DicModel dicModel = getDicModel(subtitleIndex);
        long subtitleLength = dicModel.getEndTime() - dicModel.getStartTime();
        if (subtitleLength > (extraTime*2)) {
            result = true;
        }
        return result;
    }

    private void updateSubtitleIndexBySwipingToMoveSubtitleMain(DicModel dicModel, boolean isLeft) {
        if (dicModel == null)
            return;

        activity.runOnUiThread(() -> {
            getMinMaxTime(dicModel);
            seekToInPlayer_MinSubWithDelayTime_ForSwipeToMovePrevNextDialog();
//            seekToInPlayer_MinSubWithDelayTime();
            if (isDelaySubtitleMode()) {
                updateDelaySubtitle();
                showSubtitle((long) minSub);
            } else {
                if (isDisplayListenComprehension1()) {
                    openListenComprehensionModeMain(dicModel.getPosition());
                } else if (isDisplayListenComprehension2()) {
                    resetAndResumeListenComprehensionMode2(dicModel);
                }
                // update and show again subtitle
                updateSeekBarPlay((long) minSub);
                if (isFullscreenMode()) {
                    checkAndShowSubTitle((long) minSub);
                } else {
                    scrollToPositionSubtitleDialog();
                }

                updateVisibilityOfCCRepeatResetButton(dicModel);
            }
//            applyKnowValueInSubtitle(dicModel); //자막을 앞뒤 자막으로 옮기는데 아는정도를 DB로 부터 다시 가져올 필요가 있나?
            udpateCCRepeatIconInSubtitleView(dicModel);
            onUpdateRepeatTuningDialogue();
            updateRangeSeek();
            playPlayer();
            hideSubtitleAtFirstCCRepeatOrListenComp2();
        });
    }

    private void updateVisibilityOfCCRepeatResetButton(DicModel dicModel) {
        if (isCCRepeatMode() && isVisibileRepeatRange()) {
            if (isDifferentSubtitleStartEndTimeVsOriginalTime(dicModel, activity.playerFileModel.getVideoModel())) {
                setVisibleCCRepeatReset(View.VISIBLE);
            } else {
                setVisibleCCRepeatReset(View.GONE);
            }
        }
    }

    private boolean isAllowToMovePrevNextDialog() {
        if (isHasMoreThanTwoDialogsInCCRepeat()) {
            ToastUtil.getInstance(activity).show("Can not swipe to go next/previous dialog if CC Repeat has more than 2 dialogs");
            return false;
        }

        return true;
    }

    private boolean isHasMoreThanTwoDialogsInCCRepeat() {
        return isCCRepeatMode() && (!repeatList.isEmpty() && repeatList.size() > 1);
    }

    //Dalnim made this method.
    private void updateStatusOfListComprehension2(DicModel dicModel) {
        updateRangeSeek();
        scrollToPositionSubtitleDialog();
        hideOrShowDialogsRuleAtRepeating(dicModel);
        updateListenComprehensionCurrentStatus(RepeatUtil.getRepeatValue(dicModel));
    }

    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        float distanceInPx = (float) Math.sqrt(dx * dx + dy * dy);
        return pxToDp(distanceInPx);
    }

    private float pxToDp(float px) {
        return px / getResources().getDisplayMetrics().density;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
//        super.onTouch(view, event);
        if (isLockAll)
            return false;

        if (isPinchToZoomSubtitle(view, event) || isZoomingSubtitle) {
            return true;
        }

        mScaleDetector.onTouchEvent(event);
//        if (event.getPointerCount() > 1)
//            return false;

        matrixVideoScale.getValues(mVideoScale);
        float x = mVideoScale[Matrix.MTRANS_X];
        float y = mVideoScale[Matrix.MTRANS_Y];
        PointF curr = new PointF(event.getX(), event.getY());

        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                stayedWithinClickDistance = true;
                distanceCovered = 0;
                updateValue_swipeTopMovePosition(0);
//                swipeTopMovePosition = 0;
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                //touch is start
                touchDownX = event.getRawX();
                touchDownY = event.getRawY();
                rubyY = view.getY() - event.getRawY();
                dragZoomMode = DRAG_ZOOM_MODE_DRAG;
                setUpZoomActionDown(event);
                break;
            case MotionEvent.ACTION_MOVE:

                if (stayedWithinClickDistance && distance(touchDownX, touchDownY, event.getRawX(), event.getRawY()) > MAX_CLICK_DISTANCE) {
                    stayedWithinClickDistance = false;
                }
                //finger move to screen
                final float touchMoveX = event.getRawX();
                final float touchMoveY = event.getRawY();
                deltaX_MoveDistance = touchDownX - touchMoveX;
                deltaY_MoveDistance = touchDownY - touchMoveY;
                distanceCovered = Utils.getDistance(touchMoveX, touchMoveY, event);

                if (isVideoPausedAndZoomed()) {
                    if ((isNotScalingVideo() && !stayedWithinClickDistance && dragZoomMode == DRAG_ZOOM_MODE_DRAG) ||
                            dragZoomMode == DRAG_ZOOM_MODE_ZOOM || isZoomOut) {
                        moveZoomedImage(x, y, curr);
                    }
                    break;
                }

                //HORIZONTAL SCROLL
                if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                    if (Math.abs(deltaX_MoveDistance) > Math.abs(deltaY_MoveDistance)) {
                        if (Math.abs(deltaX_MoveDistance) > Constant.PLAYER.SWIPE.DISTANCE_MIN) {
                            touchDownX = event.getRawX();
                            if (isSwipingOverSubtitleView()) {
                                if (isVisibleSubtitleView()) {
                                    typeSwipe = Constant.PLAYER.SWIPE.SUBTITLE_VIEW;
                                } else {
                                    typeSwipe = Constant.PLAYER.SWIPE.PLAYING_AREA;
                                }
                            } else if (isAreaOfPlayingScreen()) {
                                typeSwipe = Constant.PLAYER.SWIPE.PLAYING_AREA;
                            } else if (isBottomNavigationBarOnSubtitleTableScreen(1)) { //Trying to move prev/next dialog when I swipe bottom of the playing view or subtitle table view's bottom navigation bar
                                typeSwipe = Constant.PLAYER.SWIPE.BOTTOM_EDGE;
                            } else if (!isMenuUIVisible() && isAreaOfBottomEdgeScreen(1)) { //Trying to move prev/next dialog when I swipe bottom of the playing view or subtitle table view's bottom navigation bar
                                typeSwipe = Constant.PLAYER.SWIPE.BOTTOM_EDGE;
                            } else if (!isMenuUIVisible() && isAreaOfTopEdgeScreen(1)) {
                                typeSwipe = Constant.PLAYER.SWIPE.TOP_EDGE;
                            } else {
                                typeSwipe = Constant.PLAYER.SWIPE.PLAYING_AREA;
                            }

                            if ((typeSwipe == Constant.PLAYER.SWIPE.PLAYING_AREA) || (typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)) { // Was if (typeSwipe == Constant.PLAYER.SWIPE.TOP)
                                if (!isRepeat) {
//                                    pausePlayer();
                                    swipeForwardBackwardCurrentDuration = exoPlayer.getCurrentPosition();
                                }
                            }
                        }
                    } else if ((Math.abs(deltaY_MoveDistance) > Constant.PLAYER.SWIPE.DISTANCE_MIN) && (Math.abs(deltaY_MoveDistance) > Math.abs(deltaX_MoveDistance))) {
                        //VERTICALLY SCROLLING (Not mean vertical mode, it applies on landscape mode and vertical mode too)
                        if (isAreaOfPlayingScreen()) {
                            setVerticalScreen_LeftOrRight();
                        }

                    } else if (!stayedWithinClickDistance
                            && (view.getId() == R.id.llRubyBottom && !ViewUtil.isViewGone(binding.llRubyBottom) )) {
                        typeSwipe = Constant.PLAYER.SWIPE.RUBY;
                    }
                } else {
                    try {
                        if ((typeSwipe == Constant.PLAYER.SWIPE.PLAYING_AREA) || (typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)) { // Was if (typeSwipe == Constant.PLAYER.SWIPE.TOP)
                            if (Math.abs(deltaX_MoveDistance) > Math.abs(deltaY_MoveDistance)) {
                                //This is not working well. (I tried to swipe on screen to move other subtitles on CC Repeat mode but failed)
//                                if (isCCRepeatMode() || !isRepeat) {
                                if (!isRepeat) {
                                    handleSwipeToMovePositionForwardBackward(touchMoveX);
                                }
                            }
                        } else if (typeSwipe == Constant.PLAYER.SWIPE.VERTICALLY_LEFT_SCREEN) {
                            handleSwipeVerticallyOnLeftScreen(touchDownY, touchMoveY); //Dalnim added this. Don't need to use getHistoricalY (In vertical mode it's always smaller than touchMoveY)
                        } else if (typeSwipe == Constant.PLAYER.SWIPE.VERTICALLY_RIGHT_SCREEN) {
                            handSwipeVerticallyOnRightScreen(touchDownY, touchMoveY, mAudioManager);
                        } else if (typeSwipe == Constant.PLAYER.SWIPE.RUBY) {
                            handleMoveRubyText(event.getRawY(), view);
                        }
                    } catch (Exception ex) {
                        DLog.e(getLogTag(), ex.getMessage());
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                dragZoomMode = DRAG_ZOOM_MODE_NONE;
                if (isNotScalingVideo()) {
                    if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                        if (numberOfTaps == 0) {
                            numberOfTaps++;
                            mHandler.postDelayed(() -> {
                                if (numberOfTaps > 1) {
                                    if (isTouchOnSubtitleViewOnFullScreenMode(view)) {
                                        if (!isDelaySubtitleMode()) {
                                            pausePlayer();
                                            openWordListPopUpFromSubtitle(getDicModel(subtitleIndex < 0 ? 0 : subtitleIndex), null);
                                        }
                                    } else if (isAreaOfTopBottomEdgeScreen()) {
                                        if (!isMenuUIVisible())
                                            handleForwardClick(1);
                                    } else if (isAreaOfPlayingScreen()) {
                                        handleDoubleClickOnPlayingScreen();
                                    } else {
                                        // just in case.
                                        handleDoubleClickOnPlayingScreen();
                                    }
                                } else if (stayedWithinClickDistance) {
                                    DicModel model = getDicModel(subtitleIndex);
                                    if (isTouchOnSubtitleViewOnFullScreenMode(view)) {
                                        handleClickOnSubtitle(getDicModel(subtitleIndex < 0 ? 0 : subtitleIndex), PlayerShowSubtitleDialog.SUBTITLE_TYPE.FULL_SCREEN);
                                    } else if (view.getId() == R.id.ivLMSubEyeVertical) {
                                        handleSubtitleDialogEyeClick();
                                    } else if (view.getId() == R.id.ivLMABRight) {
                                        handleABRepeatClick();
                                    } else if (view.getId() == R.id.ivLMSubtitleLangChoose) {
                                        mDisplaySubtitleLang = (mDisplaySubtitleLang + 1) % Constant.PLAYER.SUB_TITLE.DISPLAY_LANG.COUNT_OF_TYPE;
                                        chooseSubtitleLangToDisplay(mDisplaySubtitleLang);
                                    } else if (view.getId() == R.id.ivWebDictionary) {
                                        openWebDictionary(null);
                                    } else if (view.getId() == R.id.ivLMPlay) {
                                        clickPlayButton();
                                    } else if (isAreaOfTopBottomEdgeScreen()) {
                                        if (!isMenuUIVisible())
                                            handleBackwardClick(1);
                                    } else if (isAreaOfPlayingScreen()) {
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
                                if (!isABRepeatMode() && !isCCRepeatMode()) {
                                    if (isFullscreenMode()) {
                                        displayPositionBackForward(swipeTopMovePosition, swipeTopNewPosition);
                                    } else {
                                        showSubtitle(exoPlayer.getCurrentPosition(), -1);
                                        scrollToPositionSubtitleDialog();
                                    }
                                }

                                if (isListenComprehensionMode()) {
                                    handleSwipeTimeToMovePrevNextDialogDuringListenComprehensionMode();
                                } else if (isCCRepeatMode()) {
                                    //This is not working well. (I tried to swipe on screen to move other subtitles on CC Repeat mode but failed)
//                                    updateValue_SubtitleIndexFromCurrentTime();
//                                    getMinMaxTime(getDicModel(subtitleIndex));
//                                    updateRangeSeek();
//                                    seekToInPlayer_MinSubWithAllExtraTime();
//                                    hideSubtitleAtFirstCCRepeatOrListenComp2();
                                }
                                playPlayer();
                            });
                        } else if ((typeSwipe == Constant.PLAYER.SWIPE.SUBTITLE_VIEW) || (typeSwipe == Constant.PLAYER.SWIPE.BOTTOM_EDGE)) {
                            handleSwipeToMovePrevNextDialog(deltaX_MoveDistance < 0);
                        }

                    }
                }
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                break;
            case MotionEvent.ACTION_CANCEL:
                dragZoomMode = DRAG_ZOOM_MODE_NONE;
                numberOfTaps = 0;
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                dragZoomMode = DRAG_ZOOM_MODE_ZOOM;
                touchDownLast.set(curr.x, curr.y);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                dragZoomMode = DRAG_ZOOM_MODE_NONE;
                break;
        }

        return true;
    }

    private boolean isTouchOnSubtitleViewOnFullScreenMode(View view) {
        return view.getId() == R.id.llRubyBottom && !ViewUtil.isViewGone(binding.llRubyBottom) && !isVerticalMode();
    }

    @Override
    protected boolean isNotScalingVideo() {
        return (!canScaleVideoScreen);
    }

    private boolean isVideoPausedAndZoomed() {
        return ((savedVideoScaleRatio > 1) || (savedVideoScaleRatio == 1 && canScaleVideoScreen)) && (!isBeingPlaying);
    }

    private void setUpZoomActionDown(MotionEvent event) {
        touchDownStart.set(event.getRawX(), event.getRawY());
        touchDownLast.set(touchDownStart);
    }

    private void moveZoomedImage(float x, float y, PointF curr) {
        if (savedVideoScaleRatio == videoScaleDefaultRatio) return;
        float deltaX = curr.x - touchDownLast.x;// x difference
        float deltaY = curr.y - touchDownLast.y;// y difference
        if ((deltaX == 0) && (deltaY == 0))
            return;

        if (y + deltaY > 0)
            deltaY = -y;
        else if (y + deltaY < -videoScalePositionBottom)
            deltaY = -(y + videoScalePositionBottom);

        if (x + deltaX > 0)
            deltaX = -x;
        else if (x + deltaX < -videoScalePositionRight)
            deltaX = -(x + videoScalePositionRight);
        matrixVideoScale.postTranslate(deltaX, deltaY);
        touchDownLast.set(curr.x, curr.y);
        View videoSurfaceView = getPlayerView().getVideoSurfaceView();
        setTransformTextureView(videoSurfaceView);
    }

    private boolean isBottomNavigationBarOnSubtitleTableScreen(float ratio) {
        if (isTouchDownXInllListMeaningWidth()) {
            LinearLayout llLMView = binding.layoutPlayerSubtitleTable.llLMView;
            int menuBarHeight = llLMView.getHeight() == 0 ? Constant.PLAYER.SWIPE.HEIGHT_OF_EDGE : llLMView.getHeight();
            return touchDownY >= llListMeaningHeight - (menuBarHeight * ratio);
        } else {
            return false;
        }
    }

//    private boolean isAreaOfBottomEdgeScreen(float ratio) {
//        if (isTouchDownXInPlayerViewWidth()) {
//            int menuBarHeight = viewBinding.llPlay.llPlayBottomMenu.getHeight() == 0 ? Constant.PLAYER.SWIPE.HEIGHT_OF_EDGE : viewBinding.llPlay.llPlayBottomMenu.getHeight();
//            return touchDownY >= playerViewHeight - (menuBarHeight * ratio);
//        } else {
//            return false;
//        }
//    }
//
//    private boolean isAreaOfTopEdgeScreen(float ratio) {
//        if (isTouchDownXInPlayerViewWidth()) {
//            int menuBarHeight = viewBinding.llPlay.llPlayTopMenu.getHeight() == 0 ? Constant.PLAYER.SWIPE.HEIGHT_OF_EDGE : viewBinding.llPlay.llPlayTopMenu.getHeight();
//            return touchDownY <= menuBarHeight * ratio;
//        } else {
//            return false;
//        }
//    }

    @Override
    protected int getMenuBarHeight() {
        return binding.llPlay.llPlayBottomMenu.getHeight() == 0 ? Constant.PLAYER.SWIPE.HEIGHT_OF_EDGE : binding.llPlay.llPlayBottomMenu.getHeight();
    }

//    private boolean isAreaOfTopBottomEdgeScreen() {
//        if (isVerticalMode())
//            return (isAreaOfTopEdgeScreen(0.5f) || isAreaOfBottomEdgeScreen(0.5f));
//
//        return (isAreaOfTopEdgeScreen(1) || isAreaOfBottomEdgeScreen(1));
//    }

//    private boolean isTouchDownXInPlayerViewWidth() {
//        boolean blnResult = false;
//        if (isFullscreenMode() || isVerticalMode()) {
//            blnResult = touchDownX <= playerViewWidth;
//        } else {
//            if (isRightHandMode) {
//                blnResult = touchDownX <= playerViewWidth;
//            } else {
//                LinearLayout llListMeaning = viewBinding.layoutPlayerSubtitleTable.llListMeaning;
//                if (touchDownX > llListMeaning.getWidth() && (touchDownX < (llListMeaning.getWidth() + playerViewWidth))) {
//                    blnResult = true;
//                } else {
//                    blnResult = false;
//                }
//            }
//        }
//
//
//        return blnResult;
//    }

    @Override
    protected int getListMeaningWidth() {
        LinearLayout llListMeaning = binding.layoutPlayerSubtitleTable.llListMeaning;
        return llListMeaning.getWidth();
    }

    private boolean isTouchDownXInllListMeaningWidth() {
        boolean blnResult = false;
        if (isFullscreenMode()) {
            blnResult = false;
        } else {
            if (isRightHandMode) {
                blnResult = (touchDownX > playerViewWidth) && touchDownX < (playerViewWidth + llListMeaningWidth);
            } else {
                if (touchDownX < binding.layoutPlayerSubtitleTable.llListMeaning.getWidth()) {
                    blnResult = true;
                } else {
                    blnResult = false;
                }
            }
        }
        return blnResult;
    }

//    private void setVerticalScreen_LeftOrRight() {
//        int halfOfPlayingWidth = playerViewWidth / 2;
//        if (!isVerticalMode() && !isFullscreenMode() && !isRightHandMode) {
//            if (touchDownX > llListMeaningWidth) {
//                halfOfPlayingWidth = llListMeaningWidth + (playerViewWidth / 2);
//            }
//        }
//
//        if (touchDownX < halfOfPlayingWidth) {
//            typeSwipe = Constant.PLAYER.SWIPE.VERTICALLY_LEFT_SCREEN;
//        } else {
//            typeSwipe = Constant.PLAYER.SWIPE.VERTICALLY_RIGHT_SCREEN;
//        }
//    }


//    private boolean isAreaOfPlayingScreen() {
//        boolean result = true;
//
//        result = result && !isAreaOfTopBottomEdgeScreen();
//        result = result && isTouchDownXInPlayerViewWidth();
//        return result;
//    }

    private boolean isSwipingOverSubtitleView() {
        return touchDownY >= binding.llRubyBottom.getY() && touchDownY <= (binding.llRubyBottom.getY() + binding.llRubyBottom.getHeight());
    }


//    private void handleSwipeVerticallyOnLeftScreen(float startY, float endY) {
//        if (typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)
//            return;
//
//        activity.runOnUiThread(() -> {
//            updateValue_ShowViewPlayCenter(true);
//            int distance = Math.abs(Math.round((deltaY_MoveDistance / playerViewHeight) * Constant.PLAYER.SWIPE.BRIGHTNESS.MAX));
//            if (distance > 0) {
//                if (endY < startY) {
//                    positionBrightness += distance;
//                } else if (endY > startY) {
//                    positionBrightness -= distance;
//                }
//                positionBrightness = Utils.checkBrightnessRange(positionBrightness);
//                int percent = Utils.changeBrightness(activity, positionBrightness);
//                DLog.e(getLogTag(), "handleSwipeLeft - positionBrightness=" + positionBrightness + " - percent=" + percent);
//                String valueDisplay = getString(R.string.display_brightness_percent, String.valueOf(percent));
//                showCenterMessageView(valueDisplay);
//                touchDownY = endY; //need this to adjust the value smoothly (dalnim commented)
//            }
//        });
//    }
//
//    private void handSwipeVerticallyOnRightScreen(float startY, float endY, AudioManager audioManager) {
//        if (typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE)
//            return;
//
//        activity.runOnUiThread(() -> {
//            updateValue_ShowViewPlayCenter(true);
//            int distance = Math.abs(Math.round((deltaY_MoveDistance / playerViewHeight) * Constant.PLAYER.SWIPE.VOLUME.MAX));
//            if (distance > 0) {
//                if (endY < startY) {
//                    positionVolume += distance;
//                } else if (endY > startY) {
//                    positionVolume -= distance;
//                }
//                positionVolume = Utils.checkVolumeRange(positionVolume);
//                int value = Math.round((positionVolume * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 100);
//                DLog.e(getLogTag(), "handSwipeRight - positionVolume=" + positionVolume + " - value=" + value);
//                Utils.changeVolume(audioManager, value);
//                String valueDisplay = getString(R.string.display_volume_percent, String.valueOf(positionVolume));
//                showCenterMessageView(valueDisplay);
//                touchDownY = endY; //need this to adjust the value smoothly (dalnim commented)
//            }
//        });
//    }
//
//    private void handleSwipeToMovePositionForwardBackword(float touchMoveX) {
//        if (playerViewWidth <= 0) return;
//        updateValue_ShowViewPlayCenter(true);
//        final int width = playerViewWidth - Constant.PLAYER.SWIPE.DISTANCE_MIN;
//        float percentMoved = Math.abs(deltaX_MoveDistance) / width;
//        float tempDistance = (percentMoved + (Constant.PLAYER.SWIPE.DISTANCE_MIN / width)) * swipeForwardBackwardValue;
//
//        final boolean isTouchDownTopEdgeOfPlayingArea = typeSwipe == Constant.PLAYER.SWIPE.TOP_EDGE ? true : false;
//        activity.runOnUiThread(() -> {
//            if (touchMoveX < touchDownX) {
//                swipeTopMovePosition = (long) tempDistance;
//            } else if (touchMoveX > touchDownX) {
//                swipeTopMovePosition = (long) -tempDistance;
//            }
//            if (isTouchDownTopEdgeOfPlayingArea) {
//                swipeTopMovePosition *= Constant.PLAYER.SWIPE.MULTIPLIER_TO_MOVE_FORWARD_BACKWARD_QUICKLY;
//            } else {
//                if (swipeTopMovePosition > swipeForwardBackwardValue) {
//                    swipeTopMovePosition = swipeForwardBackwardValue;
//                } else if (swipeTopMovePosition < -swipeForwardBackwardValue) {
//                    swipeTopMovePosition = -swipeForwardBackwardValue;
//                }
//            }
//            swipeTopNewPosition = swipeTopMovePosition + swipeForwardBackwardCurrentDuration;
//            if (swipeTopNewPosition < 0) {
//                swipeTopNewPosition = 0;
//            } else if (swipeTopNewPosition > activity.playerFileModel.getDuration()) {
//                swipeTopNewPosition = activity.playerFileModel.getDuration();
//            }
//            displayPositionBackForward(swipeTopMovePosition, swipeTopNewPosition);
//        });
//    }
//
    private void getScreenSize() {
        getScreenSize(null);
    }
//
//    @Override
    private void getScreenSize(Configuration newConfig) {
        DLog.d(getLogTag(), "getScreenSize");
        if (newConfig != null) {
            if (activity.playerFileModel.isShowRuby()) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    getRoot().setOrientation(LinearLayout.HORIZONTAL);
                    getRoot().setWeightSum(0f);
//                    final LinearLayout.LayoutParams playerParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.MATCH_PARENT, 2.0f);
//                    llPlayer.setLayoutParams(playerParam);
//                    final LinearLayout.LayoutParams meaningParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.MATCH_PARENT, 3.0f);
//                    llListMeaning.setLayoutParams(meaningParam);
                    setPanelWeights(playerWidthPercent);
                } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setPanelWeightsInPortrait();
                }

            }
            binding.layoutPlayerSubtitleTable.rvListMeaning.setLayoutManager(getCurrentLinearLayoutManager());
            resizePlayerView();
//            showllPlay();
            updateLayoutSubtitleWhenChangeRotate();
        }
        getLLPlayer().postDelayed(() -> {
            playerViewWidth = getLLPlayer().getWidth();
            playerViewHeight = getLLPlayer().getHeight();

            LinearLayout llListMeaning = binding.layoutPlayerSubtitleTable.llListMeaning;
            llListMeaningWidth = llListMeaning.getWidth();
            llListMeaningHeight = llListMeaning.getHeight();
            DLog.d(getLogTag(), "getScreenSize - sWidth=" + playerViewWidth + " - sHeight=" + playerViewHeight);
            DLog.d(getLogTag(), "llListMeaning - sWidth=" + llListMeaningWidth + " - sHeight=" + llListMeaningHeight);
        }, 500);
    }

    private void setPanelWeightsInPortrait(){
        getRoot().setOrientation(LinearLayout.VERTICAL);
        final LinearLayout.LayoutParams playerParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                0, 1.0f);
        getLLPlayer().setLayoutParams(playerParam);
        final LinearLayout.LayoutParams meaningParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                0, 2.0f);
        binding.layoutPlayerSubtitleTable.llListMeaning.setLayoutParams(meaningParam);
    }

    private ContentObserver contentBrightness = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            positionBrightness = Utils.getSystemBrightness(activity); //Dalnim added
            Utils.changeBrightness(activity, positionBrightness);
            DLog.d(getLogTag(), "positionBrightness=" + positionBrightness);
        }
    };

    private ContentObserver contentVolume = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (typeSwipe != Constant.PLAYER.SWIPE.VERTICALLY_RIGHT_SCREEN) {
                positionVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                DLog.d(getLogTag(), "positionVolume=" + positionVolume);
            }
        }
    };

    private void registerObserver() {
        activity.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), false, contentBrightness);
        activity.getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, contentVolume);
    }

    private void unregisterObserver() {
        activity.getContentResolver().unregisterContentObserver(contentBrightness);
        activity.getContentResolver().unregisterContentObserver(contentVolume);
    }

    private int getHeightView(View v) {
        if (v.getVisibility() == View.VISIBLE)
            return v.getHeight();
        return 0;
    }

    private void handleMoveRubyText(float y, View view) {
        activity.runOnUiThread(() -> {
            float rubyPosition = y + rubyY;
            if (rubyPosition < 0) {
                rubyPosition = 0;
            } else {
                final float tmp = playerViewHeight - view.getHeight();
                if (rubyPosition > tmp) {
                    rubyPosition = tmp;
                }
            }
            updateRubyViewPosition(view, rubyPosition);
        });
    }

    private void updateRubyViewPosition(View view, float rubyPosition) {
        DLog.e(getLogTag(), "updateRubyViewPosition view=" + view.getId() + " - rubyPosition=" + rubyPosition);
        if (rubyPosition < 0) return;
        view.animate()
                .y(rubyPosition)
                .setDuration(0)
                .start();
    }

    @Override
    protected boolean isMenuUIVisible() {
        if ((binding.llDelaySubtitle.root.getVisibility() == View.VISIBLE)
                || (binding.llPlaySub.root.getVisibility() == View.VISIBLE)
                || (getLLPlay().getVisibility() == View.VISIBLE)
                || (getLLRepeat().getVisibility() == View.VISIBLE)
        ) {
            return true;
        }

        return false;
    }

    @Override
    protected void handleClickOnPlayingScreen() {
//        if (isSleeping) {
//            positionBrightness = positionBrightnessBeforeSleeping;
//            Utils.changeBrightness(activity, positionBrightness);
//            isSleeping = false;
//        } else {
            clickPlayButton();
//        }
    }

    @Override
    protected void handleDoubleClickOnPlayingScreen() {
        if (isDoNotHandleDoubleClick())
            return;

        //CC Repeat or AB Repeat
        if (isRepeat) {
            if (repeatList.size() <= 1) {
                if (isVisibileRepeatRange()) {
                    setVisibleRepeatRange(View.GONE);
                    if (!isCCRepeatMode())
                        setVisibleButtonsEyeRight(View.VISIBLE);
                    updatemValue_IsAlawysShowSubtitle(false);
                    //Don't show previous dialog when it's empty dialog.
                    if (!(isFullscreenMode() && mIsOnEmptyDialog))
                        restoreShowAsteriskFromBefore();
                } else {
                    setVisibleRepeatRange(View.VISIBLE);
                    makeSpaceSmallCCRepeatBarForFullScreenModeOneHandMode();

                    setVisibleButtonsEyeRight(View.INVISIBLE);
                    showOrHideCCRepeatReset(getDicModel(subtitleIndex));
                    showRangeBarOrSubtitleTimeGapUI();
                    //When CC Repeat Range bar is displaying then always show the subtitle because I need to adjust the start/end time of the dialog.
                    updatemValue_IsAlawysShowSubtitle(true);
                    setShowAsteriskToBefore();
                    //Don't show previous dialog when it's empty dialog.
                    if (!(isFullscreenMode() && mIsOnEmptyDialog))
                        setShowSubtitle();
                }
                onUpdateRepeatTuningDialogue();
            } else {
                handleVisiblityListenComprehensionUI(binding.layoutListenComprehension.llListenComprehension.getVisibility() == View.GONE);
            }
        } else if (isListenComprehensionMode()) {
            if (repeatList.size() == 1) {
                setVisibleRepeatRange(getLLRepeat().getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                setVisibleLLListenComprehension(getLLRepeat().getVisibility());
            } else {
                if (isRepeat && isDisplayListenComprehension2()) {
                    setVisibleRepeatRange(getLLRepeat().getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                } else {
                    handleVisiblityListenComprehensionUI(binding.layoutListenComprehension.llListenComprehension.getVisibility() == View.GONE);
                    //Dalnim : I added this to show the AB, CC buttons and autohides them when I double tap to hide the Listen Comprehension's title
                    if (binding.layoutListenComprehension.tvListenComprehensionDescription.getVisibility() == View.GONE) {
                        showButtonsOnFullScreen();
                    }
                }
            }
        } else if (!listenRecordedList.isEmpty()) {
            if (binding.layoutListeningRecordedSubtitle.root.getVisibility() != View.VISIBLE) {
                setVisibleUI_ListenRecorded_ShowExitUI();
            } else {
                setVisibleUI_ListenRecorded_HideExitUI();
            }
        } else if (isVisibilellPlaySub()) {
            hideAllControlsOverPlayingScreen();
        } else {
            handleVisiblityllPlay();
        }
    }

    private void showRangeBarOrSubtitleTimeGapUI() {
        RelativeLayout vLayoutCCRepeatTuning = binding.llRepeat.rlLayoutPrevNextSubtitleOverlap;
        if (isABRepeatMode()) {
            getSbRepeatRange().setVisibility(View.VISIBLE);
            vLayoutCCRepeatTuning.setVisibility(View.GONE);
        } else if (isCCRepeatMode()) {
            getSbRepeatRange().setVisibility(View.GONE);
            vLayoutCCRepeatTuning.setVisibility(View.VISIBLE);
        }
    }

    private boolean isDoNotHandleDoubleClick() {
        return mIsOnDelaySubtitle || (timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.A) || isEnableChangeTableWidth;
    }

    private void makeSpaceSmallCCRepeatBarForFullScreenModeOneHandMode() {
        int rightSpaceValueForFullScreen = 80;
        RelativeLayout.LayoutParams repeatRangeLP = (RelativeLayout.LayoutParams) binding.llRepeat.llRepeatStartEnd.getLayoutParams();
        if (isVisibleSubtitleTable()) {
            repeatRangeLP.setMargins(0, 0, 0, 0);
        } else {
            int spaceForSmallCCRepeat = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightSpaceValueForFullScreen, activity.getResources().getDisplayMetrics());
            if (isRightHandMode) {
                repeatRangeLP.setMargins(0, 0, spaceForSmallCCRepeat, 0);
            } else {
                repeatRangeLP.setMargins(spaceForSmallCCRepeat, 0, 0, 0);
            }
        }
        binding.llRepeat.llRepeatStartEnd.setLayoutParams(repeatRangeLP);
    }

    private OnRangeChangedListener onDelayRangeChangedListener = new OnRangeChangedListener() {
        private float leftValue_ = 0.0f;
        @Override
        public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            this.leftValue_ = leftValue;
//            if (isFromUser) {
//                viewBinding.llDelaySubtitle.sbDelaySubtitle.setProgress(leftValue);
//                activity.playerFileModel.getVideoModel().setDelaySubtitles((int) leftValue);
//                updateDelaySubtitle();
//            }
        }

        @Override
        public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            pausePlayer();
        }

        @Override
        public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            playPlayer();
            binding.llDelaySubtitle.sbDelaySubtitle.setProgress(leftValue_);
            activity.playerFileModel.getVideoModel().setDelaySubtitles((int) leftValue_);
            updateDelaySubtitle();


        }
    };

    private void handleMenuClick() {
        pausePlayer();
        hideAllControlsOverPlayingScreen();
        boolean isClearVideoCache = false;
        if (activity.getSubDatabase() != null) {
            isClearVideoCache = activity.getSubDatabase().getDataChangedSize() > 0;
        }
        PlayerShowMenuDialog dialog = new PlayerShowMenuDialog(getContext(), activity.playerFileModel, isFullscreenMode(), isVerticalMode(), new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.tvSetting:
                        DalFlavor.openSettings(activity);
                        break;
                    case R.id.tvVideoBookmarkList:
                        openVideoBookmarkList();
                        break;
                    case R.id.llStudyMode:
                        openSubtitleMenuDialog();
                        break;
                    case R.id.ll_clear_video_cache:
                        showWarningClearVideoCacheDialog();
                        break;
                    case R.id.llEnableChangeTableWidth:
                        enableChangeTableWidth();
                        break;
                    case R.id.ll_word_list:
                        openWordsList();
                        break;
                    case R.id.llChoosePlayingScreenTheme:
                        choosePlayingScreenThemeDialog();
                        break;
                }
            }

            @Override
            public void onDismiss(View view, Object object) {
                if (view == null) {
                    playPlayer();
                }
            }
        });
        dialog.show();
    }

    private void handleRotateClick() {
        if (isVerticalMode() && binding.header.getVisibility() == View.VISIBLE) return;
        int requestOrientation;
        prevRotation = currentRotation;
        switch (currentRotation) {
            case Surface.ROTATION_0:
            case Surface.ROTATION_180:
                rotateScreenToLandScape();
                currentRotation = Surface.ROTATION_270;
                break;
            default:
                rotateScreenToPortrait();
                closeAdjustableBarView();
                currentRotation = Surface.ROTATION_0;
                break;
        }
        showOneHandModeAfterRotatingScreen();
        hideButtonsOnFullScreenWithAnimation();
        showListenComprehensionUI();
        setScrollableMenuModeList();
    }

    private void setScrollableMenuModeList() {
        if (scrollableMenuAdapter != null)
            scrollableMenuAdapter.setScrollableMenuModelList(createScrollableMenuList());
    }

    private void handleAdjustSubtitleFontSize() {
        hideAllControlsOverPlayingScreen();
        setVisiblellPlaySub(View.VISIBLE);
    }

    private void handleScreenZoomClick() {
        int currentResizeMode = getPlayerView().getResizeMode();
        int nextResizeMode = (currentResizeMode + 1) % 5;
        screen_resize_mode = nextResizeMode;
        String resizeModeName = "";
        switch (nextResizeMode) {
            case AspectRatioFrameLayout.RESIZE_MODE_FIT:
                resizeModeName = getString(R.string.resize_mode_fit);
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH:
                resizeModeName = getString(R.string.resize_mode_fixed_width);
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT:
                resizeModeName = getString(R.string.resize_mode_fixed_height);
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_FILL:
                resizeModeName = getString(R.string.resize_mode_fill);
                break;
            case AspectRatioFrameLayout.RESIZE_MODE_ZOOM:
                resizeModeName = getString(R.string.resize_mode_zoom);
                break;
        }
        resizePlayerView();
        ToastUtil.getInstance(activity).show(getString(R.string.message_change_resize_mode, resizeModeName));
    }

//    private void handleBackwardClick(int multiplyOfValue) {
//        activity.runOnUiThread(() -> {
//            int moveValueForwardBackward = tapForwardBackwardValue * multiplyOfValue;
//            long position = exoPlayer.getCurrentPosition();
//            position -= moveValueForwardBackward;
//            if (position < Constant.PLAYER.TIMER.MIN) {
//                position = Constant.PLAYER.TIMER.MIN;
//            }
//            displayPositionBackForward(-moveValueForwardBackward, position);
//        });
//    }

//    private void handleForwardClick(int multiplyOfValue) {
//        activity.runOnUiThread(() -> {
//            int moveValueForwardBackward = tapForwardBackwardValue * multiplyOfValue;
//            long position = exoPlayer.getCurrentPosition();
//            position += moveValueForwardBackward;
//            if (position > exoPlayer.getDuration()) {
//                position = exoPlayer.getDuration();
//            }
//            displayPositionBackForward(moveValueForwardBackward, position);
//        });
//    }

    private void handleZoomClick() {
        pausePlayer();
//        PhotoView ivZoomedPhoto = viewBinding.ivZoomedPhoto;
//        ivZoomedPhoto.setVisibility(View.VISIBLE);
//
//        UtilImage.getCurrentFrameSnapShot(activity, ivZoomedPhoto, activity.playerFileModel.getPath(), exoPlayer.getCurrentPosition(), () -> {
//                    ivZoomedPhoto.post(() -> { ivZoomedPhoto.setScale(1.0f); });
//        });
//        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(activity, activity.playerFileModel);
        zoomedPhotoDialog.loadCurrentFrameSnapShotAndShow(exoPlayer.getCurrentPosition());
    }

    /**
     * Lock all screen
     */
    private void handleLockClick() {
        isLockAll = !isLockAll;
        activity.runOnUiThread(() -> {
            subtitleDialogAdapter.setIsLockAll(isLockAll);
//            viewBinding.layoutPlayerSubtitleTable.rvListMeaning.setAdapter(subtitleDialogAdapter);
            notifyDataSetChanged_subtitleDialogAdapter();
            binding.ivLockAll.setVisibility(isLockAll ? View.VISIBLE : View.GONE);
            if (isLockAll) {
                hideAllControlsOverPlayingScreen();
            } else {
                showNormalPlayingScreenUI();
            }
        });
    }

    private void handleSubRepetitionByCCClick(int viewId) {
        activity.runOnUiThread(() -> {
            setVisiblellCenter(View.GONE);
//            setVisibleLLPlay(View.GONE);
//            setVisibleCenterText(View.GONE);
            setVisibleRepeatRange(View.GONE);
            setVisibleLLListenComprehension(View.GONE);
            setVisiblellPlaySub(View.GONE);
            setVisibleivRepeatBookmark(View.GONE);

            setRepeat(!isRepeat);
            DicModel dicModel = getDicModel(subtitleIndex);
            if (isRepeat) {
                if (viewId == R.id.ivShowButtonsRepetitionByCcRight) {
                    setVisibleIvListenComprehension(View.INVISIBLE);
                    enterCCRepeatMode();
                }
                if (repeatList.size() == 1) {
                    enterCCRepeatMode();
                }
                handlePlayClick(true, false);
                hideSubtitleAtFirstCCRepeatOrListenComp2();
            } else {
                restoreFromCCRepeatMode();
                hideOrShowABRepeatButtonOnFullScreen();
            }
            if (isHasSubtitle()) {
                // reset subPosition to 0 when cannot get data
                if (dicModel == null) {
                    updateValue_SubtitleIndex(0);
                }
                //Dalnim : Only in this function isRepeat means I just clicked the Repeat button, so need to move start Time of the clicked subtitle.
                //If I unclick the CC Repeat button, then I don't want to jump back to the current subtitle again. Just keep playing current subtitle and move to the next subtitle. So I added the if statement.
                if (isRepeat) {
                    getMinMaxTime(dicModel);
                    updateRangeSeek();
                    seekToInPlayer_MinSubWithAllExtraTime();
                }
            } else {
                findNextSubTitle();
            }
        });
    }

    //Dalnim added : without this, the subtitle on the full screen may be SHOW_ASTERISK_ON (It's the last showAsterisk in the CC Repeat. so set it as SHOW_ASTERISK_OFF)
    private void restoreFromCCRepeatMode() {
        exitCCRepeatMode();
        updatemValue_IsAlawysShowSubtitle(false);
    }

    private void hideSubtitleAtFirstCCRepeatOrListenComp2() {
        if (!isDelaySubtitleMode()
                && (isCCRepeatMode() || isDisplayListenComprehension2())) {
            ccRepeatingCount = 0;
            setShowAsteriskToBefore();
            //If a video is paused then show subtitle even if first Repeat of CCRepat / Listen 2
            if (isBeingPlaying)
                setHideSubtitle();
            else
                setShowSubtitle();
        }
    }

    private void handleRepeatCloseClick() {
        if (isCCRepeatMode()) {
            restoreFromCCRepeatMode();
        }
        resetRepeat();
        resetTimeBaseRepeatStatus();
        exitABRepeatMode();
        if (isDisplayListenComprehension1()) {
            setVisibleRepeatRange(View.GONE);
            handleVisiblityListenComprehensionUI(true);
        } else if (getLLRepeat().getVisibility() == View.VISIBLE) {
            //When this code is called? //Need this?
//            minSub = maxSub = 0.0f;
            updateValue_minSub(0);
            updateValue_maxSub(0);
            updateRangeSeekChangedMinText(Constant.BASE_BLANK);
            updateRangeSeekChangedMaxText(Constant.BASE_BLANK);
            setVisibleRepeatRange(View.GONE);
            setVisibleivRepeatBookmark(View.GONE);
            setVisibleLLListenComprehension(View.GONE);
            setVisiblellPlaySub(View.GONE);
            updateVisibilityButtonsOnFullScreen();
        }
    }

//    private void handleSubABRepeatClick() {
//        switch (timeBaseRepeatStatus) {
//            case Constant.PLAYER.REPEAT.TIMEBASE.NONE:
//                enterABRepeatModeAB_A();
////                setVisibleEyeButton(View.INVISIBLE);
//                break;
//            case Constant.PLAYER.REPEAT.TIMEBASE.A:
//                enterABRepeatModeAB_B(false);
////                setVisibleEyeButton(View.VISIBLE);
//                break;
//            case Constant.PLAYER.REPEAT.TIMEBASE.B:
//                exitABRepeatMode();
//
//                break;
//            default:
//
//        }
//    }
//
//    private void enterABRepeatModeAB_A() {
//        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.A;
//        resetRepeat();
//        minSub = exoPlayer.getCurrentPosition();
//        viewBinding.ivShowButtonsRepetitionByCcRight.setVisibility(View.INVISIBLE);
//        handlePlayClick(true, false);
//        subtitleDialogAdapter.setABRepeatMode(true);
//        if (!isFullscreenMode())
//            hideAllControlsOverPlayingScreen();
//
//        setABRepeatImage(R.drawable.ic_repeat_ab_a);
//        setVisibleEyeButton(View.INVISIBLE);
//    }
//
//    private void enterABRepeatModeAB_B(boolean isEndOfDuration) {
//        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.B;
//        setRepeat();
//        maxSub = exoPlayer.getCurrentPosition();
//        viewBinding.llPlaySub.root.setVisibility(View.GONE);
//        setVisibleLLPlay(View.GONE);
//        if (isDisplaySubtitle()) {
//            setVisibleCCRepeatReset(View.GONE);
//            setVisibleivRepeatBookmark(View.VISIBLE);
//        }
//        minSub += RepeatUtil.getTime(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_BEFORE_AB_REPEAT);
//        maxSub += RepeatUtil.getTime(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_KEEP_PLAY_AFTER_AB_REPEAT);
//        if (minSub < 0) {
//            minSub = 0;
//        }
//        if (isEndOfDuration) {
//            maxSub = exoPlayer.getDuration();
//        }
//        updateRangeSeek();
//        handlePlayClick(true, false);
//        setABRepeatImage(R.drawable.ic_repeat_ab_b);
//        setVisibleEyeButton(View.VISIBLE);
//    }
//
//    private void exitABRepeatMode() {
//        timeBaseRepeatStatus = Constant.PLAYER.REPEAT.TIMEBASE.NONE;
//        resetRepeat();
//        setVisibleRepeatRange(View.GONE);
//        subtitleDialogAdapter.setABRepeatMode(false);
//        notifyDataSetChanged_subtitleDialogAdapter();
////        subtitleDialogAdapter.notifyDataSetChanged();
//        setABRepeatImage(R.drawable.ic_repeat_ab);
//    }

    @Override
    protected void enterABRepeatModeAB_A() {
        super.enterABRepeatModeAB_A();
        resetRepeat();
        setVisibleUI_enterABRepeatModeAB_A();
        subtitleDialogAdapter.setABRepeatMode(true);
    }

    @Override
    protected void enterABRepeatModeAB_B(boolean isEndOfDuration) {
        super.enterABRepeatModeAB_B(isEndOfDuration);
        setRepeat();
        setVisibleUI_enterABRepeatModeAB_B();
        setShowAsteriskToBefore();
    }
    @Override
    protected void exitABRepeatMode() {
        super.exitABRepeatMode();
        resetRepeat();
        setVisibleUI_exitABRepeatMode();
        restoreShowAsteriskFromBefore();
        subtitleDialogAdapter.setABRepeatMode(false);
        notifyDataSetChanged_subtitleDialogAdapter();
    }

    private void setVisibleUI_enterListenRecorded() {
        subtitleDialogAdapter.setRecordingSubtitle(true);
        setVisibleUI_ListenRecorded_ShowExitUI();
        hideAllControlsOverPlayingScreen();
        setVisibleABRepeatUI(View.INVISIBLE);
        setVisibleCCRepeatUI(View.INVISIBLE);
        setVisibleEyeButton(View.INVISIBLE);
        setVisibleIvListenComprehension(View.INVISIBLE);
    }

    private void setVisibleUI_ListenRecorded_ShowExitUI() {
        LayoutPlayerPlayBinding llPlay = binding.llPlay;

        binding.layoutListeningRecordedSubtitle.root.setVisibility(View.VISIBLE);
        getRoot().postDelayed(() -> {
            setVisibleLLPlay(View.VISIBLE);
            ViewUtil.setViewListVisibility(View.GONE, llPlay.llPlayTopMenu, llPlay.llPlayBottomMenu);
        }, 100);
    }

    private void setVisibleUI_exitListenRecorded() {
        subtitleDialogAdapter.setRecordingSubtitle(false);
        setVisibleUI_ListenRecorded_HideExitUI();

        setVisibleABRepeatUI(View.VISIBLE);
        setVisibleCCRepeatUI(View.VISIBLE);
        setVisibleEyeButton(View.VISIBLE);
        setVisibleIvListenComprehension(View.VISIBLE);
    }
    private void setVisibleUI_ListenRecorded_HideExitUI() {
        LayoutPlayerPlayBinding llPlay = binding.llPlay;
        subtitleDialogAdapter.setRecordingSubtitle(false);
        binding.layoutListeningRecordedSubtitle.root.setVisibility(View.GONE);
        showNormalPlayingScreenUI();
        ViewUtil.setViewListVisibility(View.VISIBLE, llPlay.llPlayTopMenu, llPlay.llPlayBottomMenu);
    }

    private void setVisibleUI_enterABRepeatModeAB_A() {
        setVisibleCCRepeatUI(View.INVISIBLE);
        setVisibleEyeButton(View.INVISIBLE);
        setVisibleIvListenComprehension(View.INVISIBLE);
    }
    private void setVisibleUI_enterABRepeatModeAB_B() {
        binding.llPlaySub.root.setVisibility(View.GONE);
        setVisibleLLPlay(View.GONE);
        if (isDisplaySubtitle()) {
            setVisibleCCRepeatReset(View.GONE);
            setVisibleivRepeatBookmark(View.VISIBLE);
        }
    }
    private void setVisibleUI_exitABRepeatMode() {
        setVisibleRepeatRange(View.GONE);
        setVisibleCCRepeatUI(View.VISIBLE);
        setVisibleEyeButton(View.VISIBLE);
        setVisibleIvListenComprehension(View.VISIBLE);
    }
    @Override
    protected void setABRepeatImage(@DrawableRes int resId) {
        binding.ivShowButtonsRepetitionByAbRightOnMainPlayerScreen.setImageResource(resId);
        binding.layoutPlayerSubtitleTable.ivLMABRight.setImageResource(resId);
    }

//    @Override
//    protected void displayPositionBackForward(long oldPosition, long newPosition) {
//        displayPositionBackForward(true, oldPosition, newPosition);
//    }

    @Override
    protected void displayPositionBackForward(boolean isSeekTo, long oldPosition, long newPosition) {
        //Dalnim add : to show the dialog on the real time during I'm swiping top edge of full screen to go forward or backward.
        if (isFullscreenMode())
            showSubtitle(exoPlayer.getCurrentPosition(), -1);
        super.displayPositionBackForward(isSeekTo, oldPosition, newPosition);

//        updateValue_ShowViewPlayCenter(true);
//        String valueDisplay = getString(R.string.display_forward_backward, (oldPosition < 0 ? "-" : "+"), getDisplayTimes(Math.abs(oldPosition)), getDisplayTimes(newPosition));
//        DLog.d(getLogTag(), "displayPositionBackForward - display=" + valueDisplay);
//        showCenterMessageView(valueDisplay, oldPosition < 0 ? R.drawable.exo_icon_rewind : R.drawable.exo_icon_fastforward);
//        getSbPlay().setProgress((int) newPosition);
//        if (isSeekTo) {
//            seekToInPlayer(newPosition);
////            exoPlayer.seekTo(newPosition);
//        }
    }

    private void handleRepeatBookmark() {
        DLog.d(getLogTag(), "handleRepeatBookmark - isRepeat=" + isRepeat + " - isBookmark=" + isBookmark);
        if (!isRepeat) return;
        if (isBookmark) {
            removeABRepeatBookmark();
            return;
        }
        ;
        activity.runOnUiThread(() -> {
            Loading.show(activity);
            String content = Constant.BASE_BLANK;
            if (isHasSubtitle()) {
                for (int i = 0; i < subtitleList.size(); i++) {
                    DicModel item = getDicModel(i);
                    DicModel nextDicModel = getDicModel(i + 1);
                    if (minSub >= getStartTime(item) && maxSub <= getEndTime(item) && minSub < (nextDicModel == null ? maxSub : getStartTime(nextDicModel))) {
                        if (!Utils.isEmpty(content)) {
                            content += Constant.RUBY.KEY.BREAK_BR_START;
                        }
                        content += FuriganaUtil.checkAndParserRubyText(item.getVocaDisplayRuby());
                    }
                }
            }
            BookmarkPlayerModel bookmark = new BookmarkPlayerModel(
                    System.currentTimeMillis(),
                    activity.playerFileModel.getPath(),
                    BookmarkPlayerModelQuery.createIndex(Voca.getRealm(), activity.playerFileModel.getPath()),
                    (long) minSub,
                    (long) maxSub,
                    content);
            bookmarkPlayerModels.add(bookmark);
            BookmarkPlayerModelQuery.add(Voca.getRealm(), bookmark);
            checkRepeatBookmark();
            Loading.hide();
        });
    }

    private void checkRepeatBookmark() {
        checkRepeatBookmark(-1);
    }

    private void checkRepeatBookmark(long position) {
        isBookmark = false;
        if (bookmarkPlayerModels == null || bookmarkPlayerModels.isEmpty()) {
            return;
        }
        for (BookmarkPlayerModel model : bookmarkPlayerModels) {
            if (isRepeat) {
                if ((long) minSub >= model.getStart() && (long) maxSub <= model.getEnd()) {
                    isBookmark = true;
                    break;
                }
            } else {
                if (position > 0 && position >= model.getStart() && position <= model.getEnd()) {
                    isBookmark = true;
                    break;
                }
            }
        }
        binding.llRepeat.ivRepeatBookmark.setSelected(isBookmark);
    }

    private void removeABRepeatBookmark() {
        if (bookmarkPlayerModels == null || bookmarkPlayerModels.isEmpty()) {
            return;
        }

        boolean isDeleteBookmark = false;
        long bookmarkID = -1;
        for (BookmarkPlayerModel model : bookmarkPlayerModels) {
            if ((long) minSub == model.getStart() && (long) maxSub == model.getEnd()) {
                binding.llRepeat.ivRepeatBookmark.setSelected(false);
                isDeleteBookmark = true;
                bookmarkID = model.getId();
                bookmarkPlayerModels.remove(model);
                break;
            }
        }

        if (isDeleteBookmark) {
            BookmarkPlayerModelQuery.deleteById(Voca.getRealm(), bookmarkID);
        }
    }

    private void openVideoBookmarkList() {
        Intent intent = new Intent(activity, BookmarkListActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, activity.playerFileModel);
        startActivity(intent);
    }

    private void createBookmarkList() {
        if (activity.playerFileModel == null) return;
        if (bookmarkPlayerModels == null) {
            bookmarkPlayerModels = new ArrayList<>();
        } else {
            bookmarkPlayerModels.clear();
        }
        bookmarkPlayerModels.addAll(BookmarkPlayerModelQuery.getAllByPath(Voca.getRealm(), activity.playerFileModel.getPath()));
        checkRepeatBookmark();
    }

    private boolean isShowRubyText() {
        return !isVerticalMode() && (isDisplaySubtitle() || isDisplayListenComprehension1() || isDisplayListenComprehension2()) && binding.layoutPlayerSubtitleTable.llListMeaning.getVisibility() != View.VISIBLE;
    }

    private void getSubtitleMeaningData(DicModel dicModel, String content, String meaning) {
        getSubtitleMeaningData(dicModel, content, meaning, false);
    }

    private void getSubtitleMeaningData(DicModel dicModel, String content, String meaning, boolean isRepeat) {
        // https://github.com/dalnim/IssueOnly/issues/106#issuecomment-706502277
        boolean isShow = isShowRubyText();
        DLog.d(getLogTag(), "getSubtitleMeaningData content=" + content + " - isShow=" + isShow);
        if (isDelaySubtitleMode() && Utils.isEmpty(content)) {
            content = binding.tvRubyBottom.getBaseText();
        }

        binding.tvRubyBottom.resetText();
        binding.tvRubyBottom.setIsWord(false);
        if (Utils.isEmpty(content)) {
            meaning = Constant.BASE_BLANK;
        }
        binding.tvRubyBottom.setListRubyText((HashMap<Integer, RubyTextModel>) dicModel.getListRubyTextModel().clone());
        binding.tvRubyBottom.setJText(content, meaning);

        binding.tvRubyBottom.setSetColorOnSubtitle(VocaKnow.isSetColorOnSubtitle(dicModel) ? true : false);
        binding.tvRubyBottom.setVocaKnow_Sentence(dicModel.getVocaKnow());


        //Dalnim Added : to hide the KNOWN dialogs during playing for Full Screen mode
        if (mIsAlawysShowSubtitle) {
            //Dalnim : Don't hide dialogs in the Delay Subtitle mode.
            updateTvRubyBottomSubtitleShowAsterisk(Constant.SHOW_ASTERISK.SHOW_SENTENCE);
            if (mIsOnDelaySubtitle) {
                updatePreviousNextDialogOnDelaySubtitleMode();
            }
        } else {
            updateTvRubyBottomSubtitleShowAsterisk(Voca.getShowAsteriskWhenHideKnowDialogIsOn(dicModel, mIsHideKnownDialogDuringPlaying, isCCRepeatMode(), showAsterisk, isBeingPlaying));
        }

        if (isFullscreenMode())
            refreshButtonsOnFullScreen(dicModel); //Dalnim add : To update KNOW button's status on full screen mode when dialog is changed.

        if (mIsAlawysShowSubtitle) {
            setVisibleTvRubyBottomSubtitle(View.VISIBLE);
//            setVisibleLlRubyButtomSubtitle(View.VISIBLE);
        } else {
            if (showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE) {
                setVisibleTvRubyBottomSubtitle(View.INVISIBLE);
//                setVisibleLlRubyButtomSubtitle(View.INVISIBLE);
            } else {
                setVisibleTvRubyBottomSubtitle(isShow ? View.VISIBLE : View.GONE);
//                setVisibleLlRubyButtomSubtitle(isShow ? View.VISIBLE : View.INVISIBLE);
            }
        }
        DLog.d(getLogTag(), "getSubtitleMeaningData - meaning=" + meaning);
        if (!isRepeat && binding.tvRubyBottom.getBaseText().equals(content)) {
            return;
        }
        updateVisibilityButtonsOnFullScreen();
    }

    private void openWordsList() {
        openWordsList(true);
    }

    private void openWordsList(boolean isWordList) {
        Intent intent = new Intent(activity, WordListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, activity.playerFileModel);
        startActivity(intent);
    }

    private void openDialogList() {
        Intent intent = new Intent(activity, DialogueListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, activity.playerFileModel);
        startActivity(intent);
    }

    @Override
    public void onInitAsyncTask() {
//        Loading.show(activity);
    }

    @Override
    public void onInitAsyncTask(int searchType) {
        DLog.d("", "onInitAsyncTask searchType");
        switch (searchType) {
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                Loading.show(activity, R.string.saving);
                break;
            case TYPE_ANALYZE_NEW_LYRIC_FILE:
                Loading.show(activity, R.string.msg_analyzing_title, R.string.msg_analyzing_message);
                break;
            case TYPE_SEARCHED_SUBTITLE:
                // No need to show loading to prevent the keyboard is hidden
                break;
            default:
                Loading.show(activity);
                break;
        }
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        DLog.d(getLogTag(), "onPostExecuteAsyncTask - searchType=" + searchType);
        switch (searchType) {
            case TYPE_INIT_DATA:
            case TYPE_RELOAD_SUBTITLE_LIST_TOTAL_FROM_DB:
                return getSubtitleData();
            case TYPE_UPDATE_WORD_VOCA_KNOW:
                VocaTypeId vocaTypeId = (VocaTypeId) data;
                updateVocaKnowPronounceChanged(vocaTypeId);
                break;
            case TYPE_REPEAT_UPDATE:
                updateRepeatAll(repeatCountRepetition);
                break;
            case TYPE_SEARCHED_SUBTITLE:
            case TYPE_GET_DIALOG_VERTICAL:
                resetTimeBaseRepeatStatus();
                repeatList.clear();
                activity.playTTS.stop();
                resetCheckedRepeatAll();
                generateSubtitleList(); //Don't change order with createDialogAdapter();. then subtitleIndex will be -1 and get null crash
                createDialogAdapter();
                checkSubtitleLangHasData();
                break;
            case TYPE_UPDATE_REPEAT_FROM_ITEM_DIALOG:
                startUpdateRepeatFromItemDialog((DicModel) data);
                //Clicking the CC Repeat at first time, it's not yet CC Repeat mode but will enter CC Repeat mode soon.
                if ((repeatList != null) && (repeatList.size() > 0)) { //use repeatList instead.
                    subtitleDialogAdapter.setCCRepeatMode(true);
                } else {
                    subtitleDialogAdapter.setCCRepeatMode(false);
                }
                break;
            case TYPE_REPEAT_RESET_CHECKED:
                resetCheckedRepeatAll();
                break;
            case TYPE_GET_DATA_PLAY_VOICE:
                resetCheckedRepeatAll();
                DicModel dicModel = (DicModel) data;
                dicModel.setPlayRecordOrTSS(true);

                final List<DicModel> list = getDicModelListByDifficultWordAppearance(dicModel);
                activity.playTTS.preparePlayPlayer(list);
                return list;

            case TYPE_GET_ALL_DIFFICULT_WORD_AT_ONCE_SUBTITLE_LIST_TO_PLAY_VOICE:
                resetCheckedRepeatAll();
                List<DicModel> subtitleAtOnceList = (List<DicModel>) data;

                final List<DicModel> listWords = getDicModelListByDifficultWordAtOnceSubtitleList(subtitleAtOnceList);
                activity.playTTS.preparePlayPlayer(listWords);
                return listWords;

            case TYPE_GET_ALL_DIFFCULT_WORD_TO_PLAY_VOICE:
                if (activity.getSubDatabase() == null) {
                    activity.createSubDatabase(activity.playerFileModel);
                }
//                handlePlayClick(false, false);
                activity.playTTS.playTTSHelper.setSubtitleMotherTongue(""); //To prevent null
                activity.playTTS.playTTSHelper.setSubtitleStudyLang("");
                activity.playTTS.playTTSHelper.setPlayDifficultWordsBeforePlayingSubtitle(true);
                DLog.d(getLogTag(), "handlePlayDifficultWord - repeatSubTitle - subtitleIndex=" + subtitleIndex);

                final List<DicModel> list1 = getAllDifficultWordToPlayTTSByAppearance();
                if (!list1.isEmpty()) {
                    activity.playTTS.preparePlayPlayer(list1);
                }
                return list1;

            case TYPE_RELOAD_SUBTITLE_LIST_FROM_DB:
                subtitleList = reloadSubtitleListFromDB();
                break;
            case TYPE_CLEAR_VIDEO_CACHE:
                clearVideoCache();
                break;
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                UtilImage.saveLastWatchPositionAsThumbnailImage(activity, activity.playerFileModel);
                activity.syncVocaKnow(activity.playerFileModel.getVideoModel());
                break;
            case TYPE_UPDATE_FROM_EDIT_SUBTITLE:
                return updateDataFromEditSubtitle((String) data);
//            case TYPE_ANALYZE_NEW_LYRIC_FILE:
//                return SubtitleUtil.parserContentSubTitle(activity.playerFileModel,  Constant.PLAYER.INTENT.SUBPATH_INDEX_1);
        }
        return null;
    }

    @NotNull
    private List<DicModel> getDicModelListByDifficultWordAppearance(DicModel data) {
        final DicModel dicModel = data;

        String strWordIdList = dicModel.getWordsIds();
        List<DicModel> dicModels = activity.getSubDatabase().getDicModelByVocaIds(strWordIdList);
        Map<Integer, DicModel> mapDicModels = dicModels.stream()
                .collect(Collectors.toMap(e -> e.getVocaId(), e -> e));

        List<DicModel> dicModelsSortedByAppearnce = new ArrayList<>();

        if (mapDicModels.size() > 0) {
            List<Integer> wordIdList = Stream.of(strWordIdList.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            for (Integer wordId : wordIdList) {
                if (mapDicModels.containsKey(wordId)) {
                    DicModel dicModelInMap = mapDicModels.get(wordId);
                    int repeatCount = RepeatUtil.getRepeatCount(activity, sharedPreferences.getReadCount());
                    dicModelInMap.setVIRepeatCount(repeatCount);
                    dicModelsSortedByAppearnce.add(dicModelInMap);

//                    dicModelsSortedByAppearnce.add(mapDicModels.get(wordId));
                }
            }
        }
        return dicModelsSortedByAppearnce;
    }

    @NotNull
    private List<DicModel> getDicModelListByDifficultWordAtOnceSubtitleList(List<DicModel> dicModelList) {
        List<DicModel> dicModels = new ArrayList<>();
        StringBuilder strWordIdList = new StringBuilder();
        for (DicModel dicModel: dicModelList) {
            String wordsIds = dicModel.getWordsIds();
            if (wordsIds == null || wordsIds.isEmpty() || strWordIdList.toString().contains(wordsIds)) continue;
            strWordIdList.append(dicModel.getWordsIds()).append(",");
            dicModels.addAll(activity.getSubDatabase().getDicModelByVocaIds(dicModel.getWordsIds()));
        }
        Map<Integer, DicModel> mapDicModels = dicModels.stream()
                .collect(Collectors.toMap(e -> e.getVocaId(), e -> e));

        List<DicModel> dicModelsSortedByAppearnce = new ArrayList<>();

        if (mapDicModels.size() > 0) {
            List<Integer> wordIdList = Stream.of(strWordIdList.toString().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            for (Integer wordId : wordIdList) {

                if (mapDicModels.containsKey(wordId)) {
                    DicModel dicModelInMap = mapDicModels.get(wordId);
                    int repeatCount = RepeatUtil.getRepeatCount(activity, sharedPreferences.getReadCount());
                    dicModelInMap.setVIRepeatCount(repeatCount);
                    dicModelsSortedByAppearnce.add(dicModelInMap);
                }
            }
        }
        return dicModelsSortedByAppearnce;
    }

    @NotNull
    private List<DicModel> getAllDifficultWordToPlayTTSByAppearance() {

        List<DicModel> dicModels = activity.getSubDatabase().getDicModelByUnknownWord();
        Map<Integer, DicModel> mapDicModels = dicModels.stream().limit(2)
                .collect(Collectors.toMap(e -> e.getVocaId(), e -> e));

        List<DicModel> dicModelsSortedByAppearnce = new ArrayList<>();

        if (mapDicModels.size() > 0) {
            List<Integer> wordIdList = dicModels.stream().map(e -> e.getVocaId()).collect(Collectors.toList());

            for (Integer wordId : wordIdList) {
                if (mapDicModels.containsKey(wordId)) {
                    dicModelsSortedByAppearnce.add(mapDicModels.get(wordId));
                }
            }
        }
        return dicModelsSortedByAppearnce;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                updateInitData(resultData);
                initRvScrollableMenu();
                setVisibleLlRubyButtomSubtitle();
                break;
            case TYPE_RELOAD_SUBTITLE_LIST_TOTAL_FROM_DB:
                updateInitData(resultData);
                initRvScrollableMenu();
                setVisibleLlRubyButtomSubtitle();
                handleAfterModifySubtitle();
                break;
            case TYPE_UPDATE_WORD_VOCA_KNOW:
                binding.tvRubyBottom.setBaseText(Constant.BASE_BLANK);
                //Don't remove this code. if I comment this out,then when I change thge KNOW value, it's not applied right away on the full screen mode.
                showSubtitleAlways(exoPlayer.getCurrentPosition());
                subtitleDialogAdapter.setData(subtitleList);
                notifyDataSetChanged_subtitleDialogAdapter();
                Loading.hide();
                break;
            case TYPE_CLEAR_VIDEO_CACHE:
                playPlayer();
                break;
            case TYPE_REPEAT_UPDATE:
                int[] requestData = (int[]) data;
                Loading.hide();
                if (requestData[1] == Constant.PLAYER.SUB_TITLE.DISPLAY.LISTEN_COMPREHENSION_2) {
                    openListenComprehensionMode(Constant.PLAYER.SUB_TITLE.DISPLAY.LISTEN_COMPREHENSION_2, requestData[0]);
                }
                break;
            case TYPE_REPEAT_RESET_CHECKED:
                notifyDataSetChanged_subtitleDialogAdapter();
                Loading.hide();
                break;
            case TYPE_SEARCHED_SUBTITLE:
            case TYPE_GET_DIALOG_VERTICAL:
                if (isRepeat) {
                    setRepeat(false);
                    setVisibleLLPlay(View.GONE);
                    setVisibleLLListenComprehension(View.GONE);
                    setVisibleRepeatRange(View.GONE);
                    setVisiblellPlaySub(View.GONE);
                }
                binding.layoutPlayerSubtitleTable.rvListMeaning.setAdapter(subtitleDialogAdapter);
                subtitleDialogAdapter.setSearchingKeyWord(searchValue);
                subtitleDialogAdapter.setData(subtitleList);
                notifyDataSetChanged_subtitleDialogAdapter();
                if (subtitleList != null && !subtitleList.isEmpty()) {
                    resetMinMaxSubtitle(getPositionFromCurrentTime());
                    updateRangeSeek();
                    scrollToPositionSubtitleDialog();
                }
                if (isOpenFirstTime) {
                    isOpenFirstTime = false;
                    if (sharedPreferences.getPlayVideoAutomaticallyWhenOpenIt()) {
//                        playPlayer();
                    } else {
                        pausePlayer();
                        updateRangeSeek();
                    }
                } else {
                    if (!isSettingNextFile) {
                        playPlayer();
                    }
                }
                Loading.hide();
                if (!isVerticalMode()) {
                    setPanelWeights(playerWidthPercent);
                }
                break;
            case TYPE_UPDATE_REPEAT_FROM_ITEM_DIALOG:
                endUpdateRepeatFromItemDialog((DicModel) data);
                Loading.hide();
                break;
            case TYPE_GET_DATA_PLAY_VOICE:
            case TYPE_GET_ALL_DIFFICULT_WORD_AT_ONCE_SUBTITLE_LIST_TO_PLAY_VOICE:
                Loading.hide();
                break;
            case TYPE_GET_ALL_DIFFCULT_WORD_TO_PLAY_VOICE:
                final ArrayList<DicModel> list = (ArrayList<DicModel>) resultData;
//                activity.playTTS.preparePlayPlayer(list);
                if (list.isEmpty()) {
                    if (isInitViewFinished) {
                        startOverWhenItReachEndOfMediaMain();
                    } else {
                        startInitViewDelay();
                    }
                }
                Loading.hide();
                break;
            case TYPE_RELOAD_SUBTITLE_LIST_FROM_DB:
                subtitleDialogAdapter.setData(subtitleList);
                notifyDataSetChanged_subtitleDialogAdapter();
                showSubtitle(exoPlayer.getCurrentPosition(), -1);
                Loading.hide();
                break;
            case TYPE_SYNC_DATA_BEFORE_EXIT:
                activity.updateVideoModel(activity.playerFileModel);
                Loading.hide();
                activity.onBackPressed();
                break;
            case TYPE_UPDATE_FROM_EDIT_SUBTITLE:
                if ((Boolean) resultData) {
                    subtitleDialogAdapter.setData(subtitleList);
                    notifyDataSetChanged_subtitleDialogAdapter();
                    showSubtitle(exoPlayer.getCurrentPosition(), -1);
                }
                playPlayer();
                Loading.hide();
                break;
            case TYPE_ANALYZE_NEW_LYRIC_FILE:
                makeRubyTextFromServer(resultData);
                break;
        }
        Loading.hide();
    }

//    private void handleTTSPlay(IVocaFullPlayTTSItem model) {
//        handleTTSPlay(model, true);
//    }

    private void handleTTSPlay(IVocaFullPlayTTSItem model, boolean isAutoPlay) {
        DLog.d(getLogTag(), "handlePlay");
        boolean isTTSPlaying = model.isVIPlaying();
        activity.playTTS.stop();
        if (!isTTSPlaying) {
            if (isAutoPlay) {
                pausePlayer();
            }
            activity.playTTS.playSingle(model);
        } else {
            if (isAutoPlay) {
                playPlayer();
            }
        }
    }

    private void stopTTSPlaying(IVocaFullPlayTTSItem model) {
        boolean isTTSPlaying = model.isVIPlaying();
        if (isTTSPlaying) {
            activity.playTTS.stop();
        }

    }

    private void updateVocaKnowPronounceChanged(VocaTypeId vocaTypeId) {
        DLog.d(getLogTag(), "updateKnownPronounceChanged");
        final String ids = activity.getSubDatabase().getSubtitleIdListToUpdateVocaKnow(vocaTypeId);
        updateSubtitleModelBySubtitleIdList(ids);
    }

    private void updateSubtitleModelBySubtitleIdList(String id) {
        if (Utils.isEmpty(id)) return;
        if (!isHasSubtitleTotal()) return;
        ArrayList<DicModel> dicModels = new ArrayList<>();
        dicModels.addAll(activity.getSubDatabase().getSubtitleDialogListById(id));
        rubyTextModels = getRubyTextModels();
        MergeUtil.generateMeaning(dicModels, rubyTextModels);
        //TODO : Dalnim - use Map to avoid 2 for loops.
        for (DicModel d1 : dicModels) {
            for (int i = 0; i < subtitleListTotal.size(); i++) {
                DicModel d2 = subtitleListTotal.get(i);
                if (d1.getId() == d2.getId()) {
                    d1.setPlayRecordOrTSS(d2.isPlayRecordOrTSS());
                    d1.setVIChecked(d2.isVIChecked());
                    addRecordedFileInDicModel(d1);

                    DLog.d(getLogTag(), "total d1=" + d1.getId() + " ||| d2=" + d2.getId());
                    subtitleListTotal.set(i, d1);
                    break;
                }
            }
            for (int i = 0; i < subtitleList.size(); i++) {
                DicModel d2 = getDicModel(i);
                if (d1.getId() == d2.getId()) {
                    d1.setPlayRecordOrTSS(d2.isPlayRecordOrTSS());
                    d1.setVIChecked(d2.isVIChecked());
                    addRecordedFileInDicModel(d1);
                    DLog.d(getLogTag(), "d1=" + d1.getId() + " ||| d2=" + d2.getId());
                    subtitleList.set(i, d1);
                    break;
                }
            }
        }
        // The position and index is messed in the  MergeUtil.generateMeaning(listDicModelForIds, rubyTextModels). So reset it again.
        // (Without this, If I change a word's KNOW value in a dialog, MergeUtil.generateMeaning make the dialog's position to 0, so if I swipe to go next dialog, it goes to 0 dialog in the subtitle)
        SubtitlePositionTimeUtil.resetSubtitlePosition(subtitleListTotal);
        SubtitlePositionTimeUtil.resetSubtitlePosition(subtitleList);

        // check StudyChatAdapter when changed data from phrase information
        if (recyclerSubtitleViewDialog != null && recyclerSubtitleViewDialog.isShowing()
                && !Utils.isEmpty(vocaIdDataChangeFromPhraseInfor)) {
            final DicModel item = activity.getSubDatabase().getDicModelByVocaId(vocaIdDataChangeFromPhraseInfor);
            if (item != null) {
                StudyChatAdapter studyChatAdapter = (StudyChatAdapter) recyclerSubtitleViewDialog.getAdapter();
                for (Object obj : studyChatAdapter.getData()) {
                    if (obj instanceof VocaStudyChat) {
                        VocaStudyChat voca = (VocaStudyChat) obj;
                        if (voca.getVocaId() == Integer.parseInt(oldVocaIdDataChangeFromPhraseInfor)) {
                            voca.setVocaId(item.getVocaId());
                            voca.setVocaDisplay(item.getVocaDisplay());
                            voca.setVocaDisplayRubyText(item.getVocaDisplayRuby());
                            voca.setPronounce(item.getPronounce());
                            voca.setMeaning(item.getMeaning());
                            studyChatAdapter.notifyItemChanged(voca);
                            break;
                        }
                    }
                }
            }
            vocaIdDataChangeFromPhraseInfor = Constant.BASE_BLANK;
            oldVocaIdDataChangeFromPhraseInfor = Constant.BASE_BLANK;
        }
    }

    private void addRecordedFileInDicModel(DicModel d1) {
        File recordedFile = Voca.getVoiceFileOnLocal(activity, activity.playerFileModel, Voca.getVoiceFolderOnLocal(activity), d1.getVIPath(), d1.getVocaId());
        if (recordedFile.exists()) {
            d1.setRecordedPath(recordedFile.getPath());
        }
    }

    @Override
    protected void resetRepeat() {
        super.resetRepeat();
        if (!repeatList.isEmpty()) {
            repeatList.clear();
            activity.callAsyncTask(this, TYPE_REPEAT_RESET_CHECKED);
        }
    }

    @Override
    protected void setRepeat(boolean isRepeat) {
        super.setRepeat(isRepeat);
        showButtonsOnFullScreen(); //Dalnim : Why I called it inside setRepeat? ^^; (If I comment this, I can't click CC/AB repeat button) This is called when it's full screen mode and when I hide the main menu or listen comprehension 1 or 2
    }

//    private void resetRepeat() {
//        setRepeat(false);
//        if (!repeatList.isEmpty()) {
//            repeatList.clear();
//            activity.callAsyncTask(this, TYPE_REPEAT_RESET_CHECKED);
//        }
//    }
//
//    private void setRepeat() {
//        setRepeat(true);
//    }
//
//    private void setRepeat(boolean isRepeat) {
//        this.isRepeat = isRepeat;
//        showButtonsOnFullScreen(); //Dalnim : Why I called it inside setRepeat? ^^; (If I comment this, I can't click CC/AB repeat button) This is called when it's full screen mode and when I hide the main menu or listen comprehension 1 or 2
//    }

    private void showButtonsOnFullScreen() {
        if (isDelaySubtitleMode()) {
            return;
        }
        if (isRepeat) {
            if (timeBaseRepeatStatus != Constant.PLAYER.REPEAT.TIMEBASE.NONE) {
                //Don't change order. (AB Repeat can't hide Eye button if I change order)
                setVisibleCCRepeatUI(View.INVISIBLE);
                hideOrShowABRepeatButtonOnFullScreen();
            } else if (isCCRepeatMode()) {
                setVisibleABRepeatUI(View.INVISIBLE);
            } else {
                setVisibleABRepeatUI(View.GONE);
                setVisibleCCRepeatUI(View.INVISIBLE);
            }
            cancelHidingButtonsOnFullScreenWithAnimation();
        } else {
            // check display AB repeat button
            setVisibleCCRepeatUI(isHasShowSubtitle() ? View.VISIBLE : View.INVISIBLE);
            if (timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.A) {
                cancelHidingButtonsOnFullScreenWithAnimation();
            } else if (timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.NONE) {
                hideOrShowABRepeatButtonOnFullScreen();
                hideButtonsOnFullScreenWithAnimation();
            }
        }
        binding.ivShowButtonsRepetitionByCcRight.setSelected(isRepeat);
    }

    private void hideOrShowABRepeatButtonOnFullScreen() {
        if (isListenComprehensionMode()) {
            setVisibleABRepeatUI(View.INVISIBLE);
        } else {
            setVisibleABRepeatUI(View.VISIBLE);
            if (!isABRepeatMode()) {
                setVisibleButtonsEyeRight(View.VISIBLE);
            }
        }
    }

    private void cancelHidingButtonsOnFullScreenWithAnimation() {
        float alphaMaxButton = 1.0f;

        binding.rlShowButtonsOnFullScreen.setAlpha(alphaMaxButton);
        binding.rlShowButtonsOnFullScreen.animate().cancel();
        binding.rlShowButtonsOnFullScreenLeft.setAlpha(alphaMaxButton);
        binding.rlShowButtonsOnFullScreenLeft.animate().cancel();
    }

    private void hideButtonsOnFullScreenWithAnimation() {
        if (dontShowAndHide4Buttons())
            return;

        float alphaMaxButton = 1.0f;
        float alphaMinButton = 1.0f - (sharedPreferences.getHiddenButtonsTransparencyOnFullScreen() / 100f);
        Integer startDelayToHideButton = 2000;
        Integer durationToHideButton = 1500;

        setVisibleShowButtonsOnFullScreen(View.VISIBLE);
        binding.rlShowButtonsOnFullScreen.setAlpha(alphaMaxButton);
        binding.rlShowButtonsOnFullScreen.animate().setStartDelay(startDelayToHideButton).alpha(alphaMinButton).setDuration(durationToHideButton).start();
        binding.rlShowButtonsOnFullScreenLeft.setAlpha(alphaMaxButton);
        binding.rlShowButtonsOnFullScreenLeft.animate().setStartDelay(startDelayToHideButton).alpha(alphaMinButton).setDuration(durationToHideButton).start();
    }

    private boolean dontShowAndHide4Buttons() {
        if (!isVerticalMode() && isVisibleSubtitleTable())
            return true;

        if (isVerticalMode() && isHasSubtitleTotal()) {
            return true;
        }
        return false;
    }

    private boolean make4ButtonsGone() {
        if (sharedPreferences.getHide4ButtonsOnPlayingScreen()) {
            return true;
        }
        return false;
    }
//    private void initPlayVocaHelperListener() {
//        DLog.d(getLogTag(), "initPlayVocaHelper");
//        if (!activity.playVocaHelper.hasMotherTongueListener()) {
//            activity.playVocaHelper.setMotherTongueListener(new UtteranceProgressListener() {
//
//                @Override
//                public void onStart(String utteranceId) {
//                    updateItemStatus(utteranceId, true);
//                }
//
//                @Override
//                public void onDone(String utteranceId) {
//                    if (!Utils.isEmpty(utteranceId) && utteranceId.equals(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.KEY_PLAY_DONE)) {
//                        handlePlayClick(true, false);
//                    }
//                }
//
//                @Override
//                public void onError(String utteranceId) {
//                }
//            });
//        }
//        if (!activity.playVocaHelper.hasStudyListener()) {
//            activity.playVocaHelper.setStudyListener(new UtteranceProgressListener() {
//
//                @Override
//                public void onStart(String utteranceId) {
//                }
//
//                @Override
//                public void onDone(String utteranceId) {
//                    updateItemStatus(utteranceId, false);
//                }
//
//                @Override
//                public void onError(String utteranceId) {
//                    updateItemStatus(utteranceId, false);
//                }
//            });
//        }
//    }
//
//    private void updateItemStatus(String utteranceId, boolean playing) {
//        if (recyclerSubtitleViewDialog != null && recyclerSubtitleViewDialog.isShowing()) {
//            StudyChatAdapter adapter = (StudyChatAdapter) recyclerSubtitleViewDialog.getAdapter();
//            for (Object obj : adapter.getData()) {
//                if (obj instanceof VocaStudyChat) {
//                    VocaStudyChat voca = (VocaStudyChat) obj;
//                    if (utteranceId.equals(String.valueOf(voca.getPIId()))) {
//                        DLog.d(getLogTag(), "utteranceId=" + utteranceId + " - " + voca.getPIId() + " - voca");
//                        voca.setPIPlaying(playing);
//                        rvListMeaning.post(() -> adapter.notifyItemChanged(voca));
//                        break;
//                    }
//                }
//            }
//        }
//    }

    /**
     * Vertical mode (ORIENTATION_PORTRAIT)
     * disable click show/hide subtitle
     *
     * @return
     */
    @Override
    protected boolean isVerticalMode() {
        return currentRotation == Surface.ROTATION_0 || currentRotation == Surface.ROTATION_180;
    }

    private void updateInitLayout(int typeDisplay) {
        if (!isHasShowSubtitle()) {
            this.typeDisplay = typeDisplay;
            if (FileUtil.isMusicApp()) {
                showLyricTxtView();
                String subTitlePath = activity.playerFileModel.getSubPath();
                if (!TextUtils.isEmpty(subTitlePath)) {
                    showTxtLyric(subTitlePath);
                }
                showllPlay();
                setVisibleSubtitleTable(View.VISIBLE);
            } else {
                setVisibilitySubtitleTableViewAndIcon(View.GONE);
                showNormalPlayingScreenUI();
            }
            return;
        } else {
            if (FileUtil.isMusicApp()) {
                hideLyricTxtViewAndShowSubtitle();
                showllPlay();
            }
        }

        int showSubtitleTableWhenOpen = sharedPreferences.getShowSubtitleTableWhenOpen() ? View.VISIBLE : View.GONE;
        if (this.typeDisplay == typeDisplay) {
            if (binding.layoutPlayerSubtitleTable.llListMeaning.getVisibility() == View.GONE) {
//                ToastUtil.getInstance(activity).show("updateInitLayout llListMeaning = View.GONE");
                setVisibilitySubtitleTableViewAndIcon(showSubtitleTableWhenOpen);
            }
            return;
        }
        restoreFromDisplayListenComprehension();
        this.typeDisplay = typeDisplay;
        activity.runOnUiThread(() -> {
            setVisibilitySubtitleTableViewAndIcon(showSubtitleTableWhenOpen);
            hideButtonsOnFullScreenWithAnimation();
            activity.callAsyncTask(this, null, TYPE_GET_DIALOG_VERTICAL);
        });
    }

    private void showLyricTxtView() {
        binding.layoutPlayerSubtitleTable.svTvLyric.setVisibility(View.VISIBLE);
        binding.layoutPlayerSubtitleTable.rvListMeaning.setVisibility(View.GONE);
        binding.layoutPlayerSubtitleTable.llLMView.setVisibility(View.GONE);
    }

    private void hideLyricTxtViewAndShowSubtitle() {
        binding.layoutPlayerSubtitleTable.svTvLyric.setVisibility(View.GONE);
        binding.layoutPlayerSubtitleTable.rvListMeaning.setVisibility(View.VISIBLE);
        binding.layoutPlayerSubtitleTable.llLMView.setVisibility(View.VISIBLE);
    }

    private OnDoubleClickListener studyChatAdapterOnDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object object) {
            VocaStudyChat voca = (VocaStudyChat) object;
            registerVocaDialog.show(voca, false);
        }

        @Override
        public void onDoubleClick(View view, Object object) {
            VocaStudyChat voca = (VocaStudyChat) object;
            updateKnowWhenDoubleClick(voca);
        }
    };

    private DoubleClickHelper doubleClickHelper = new DoubleClickHelper(new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            if (data instanceof DicModel) {
                DicModel model = (DicModel) data;
                handlePlayTheSubtitle(model);
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {
            if (data instanceof DicModel) {
                DicModel model = (DicModel) data;
                pausePlayer();
                openWordListPopUpFromSubtitle(model, null);
            }
        }

        @Override
        public void onLongPress(View view, Object data) {
            if (data instanceof DicModel) {
                DicModel model = (DicModel) data;
                showSubtitleDialogItemClick(model);
            }
        }
    });

    private OnDoubleClickListener subtitleDialogAdapterOnDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object object) {
            if (isLockAll) return;
            final DicModel model = (DicModel) object;
            switch (view.getId()) {
                case R.id.llItem:
                case R.id.fvSubtitle:
                case R.id.rl_dialog:
                case R.id.fvDifficultWord:
                    showSubtitleDialogItemClick(model);
                    break;
                case R.id.ivCCRepeat:
                    repeatSubtitleDialogItemClick(model);
                    break;
                case R.id.llKnowValue:
                case R.id.tvKnowValue:
                    showSubtitleKnowDialog(model);
                    break;
//                case R.id.iv_bookmark:
//                    updateBookmark(model);
//                    break;
                case R.id.ivRecordingSubtitle:
                    onClickShadowing(model);
                    break;
                case R.id.ivListenRecordedSubtitle:
                    onClickListeningRecorded(model);
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object object) {
            if (isLockAll) return;
            final DicModel model = (DicModel) object;
            if (view.getId() == R.id.tvKnowValue) {
                updateKnowWhenDoubleClick(model);
            } else {
                pausePlayer();
                openWordListPopUpFromSubtitle(model, null);
            }


        }
    };

    private void generateSubtitleList() {
        if (!isHasSubtitleTotal()) return;
        if (subtitleList == null) {
            subtitleList = new ArrayList<>();
        }
        subtitleList.clear();
        subtitleList = generateSubtitleDialogByType();
        DLog.d(getLogTag(), "generatesubtitleList - size=" + subtitleList.size());
    }

    private List<DicModel> generateSubtitleDialogByType() {
        SubtitleGroupHelper subtitleGroupHelper = new SubtitleGroupHelper(subtitleListTotal, activity.getSubDatabase(), new SubtitleHideModel());//, rubyTextModels);
        final List<DicModel> items = subtitleGroupHelper.generateSubtitleDialogByType(SubtitleGroupHelper.TYPE.ALL_SUBTITLES);
        SubtitlePositionTimeUtil.resetSubtitlePosition(items);
        updateValue_SubtitleIndexFromCurrentTime();
        if (!isFullscreenMode()) { //Dalnim : Want to go to current dialog after generateSubtitleDialogByType.
            scrollToPositionSubtitleDialog();
        }
        return items;
    }

    private void repeatSubtitleDialogItemClick(DicModel model) {
        DLog.d(getLogTag(), "repeatSubtitleDialogItemClick");
        // stop play voice with repeat first
        resetTimeBaseRepeatStatus();
        ccRepeatingCount = 0;
//        if (repeatList == null || repeatList.isEmpty()) {
//            resetPlayingDifficultWord();
//        }
        activity.callAsyncTask(this, model, TYPE_UPDATE_REPEAT_FROM_ITEM_DIALOG, false);
    }

    private void scrollToPositionSubtitleDialog() {
        if (isFullscreenMode())
            return;

        activity.runOnUiThread(() -> {
            scrollToPositionSubtitleDialog(true, true);
        });
    }

    private void scrollToPositionSubtitleDialog(boolean lastCheck, boolean isScroll) {
        DLog.d(getLogTag(), "scrollToPositionSubtitleDialog - lastCheck=" + lastCheck + " - isScroll=" + isScroll + " - subPosition=" + subtitleIndex + " - dialogAdapter.getLastCheckedPosition()=" + subtitleDialogAdapter.getLastCheckedPosition());
        if (subtitleIndex < 0) {
            //Dalnim updated : always go to the current time's dialog or 1 dialog before(if it's empty)
            updateValue_SubtitleIndexFromCurrentTime();
            if (subtitleIndex < 0) {
                //When I get the subtitle index from time and it's still -1 then set to 0, if it's still -1 in subtitle table view the position stays even if I move the range bat to time 0.
                //This 0 makes the position to 1st dialog in the subtitle table view when I move the range bar to time 0.
                updateValue_SubtitleIndex(0);
            }
        }
        final int oldPosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        if (lastCheck) {
            subtitleDialogAdapter.setLastCheckedPosition(subtitleIndex);
        }
        //Dalnim : Don't move this code before "if (lastCheck)" then, I can't see the current dialog background color when I start Listen 2 on first visible dialog.
        //If I remove this code, I can't click a UI in the subtitle table view when AraPalyer is playing before 1st item.
        if (subtitleIndex == oldPosition) {
            return;
        }

        DLog.d(getLogTag(), "scrollToPositionSubtitleDialog - oldPosition=" + oldPosition);
        if (isScroll) { // Why check isScroll? I think Table view should scroll always
            mHandler.postDelayed(() -> {
                RecyclerView rvListMeaning = binding.layoutPlayerSubtitleTable.rvListMeaning;
                rvListMeaning.post(() -> {
                    //Dalnim updated. when I rotate screen, if oldPosition is -1 it doesn't go to the subtitleIndex in the table view.
                    scrollPositionInSubtitleTableView(rvListMeaning, subtitleIndex, oldPosition < 0 ? 0 : oldPosition);
                });
            }, 200);
        }
    }

    private void showSubtitleDialogItemClick(DicModel model) {
        createSubtitleDialog(model, PlayerShowSubtitleDialog.SUBTITLE_TYPE.TABLE_VIEW);
    }

    private void handleClickOnSubtitle(DicModel data, PlayerShowSubtitleDialog.SUBTITLE_TYPE type) {
        if (mIsOnDelaySubtitle || isVisibilellPlaySub()) {
            clickPlayButton();
            return;
        }

        createSubtitleDialog(data, type);
    }

    private void createSubtitleDialog(DicModel data, PlayerShowSubtitleDialog.SUBTITLE_TYPE subtitleType) {
        if (data == null)
            return;

        pausePlayer();
        subTitleDialog = new PlayerShowSubtitleDialog(activity, isShowAdvancedMode(), isListenComprehensionMode(), isCCRepeatMode(), isABRepeatMode(), !listenRecordedList.isEmpty(), subtitleType, onSubtitleDialogListener);

        subTitleDialog.setData(data);
        if (subTitleDialog != null && subTitleDialog.isShowing()) {
            subTitleDialog.dismiss();
        }
        subTitleDialog.show();
    }

    private OnClickDialogListener onSubtitleDialogListener = new OnClickDialogListener() {
        @Override
        public void onClick(View view, Object object) {
            final DicModel model = (DicModel) object;
            switch (view.getId()) {
                case R.id.llChangeWordKnownStatus:
                    openWordListPopUpFromSubtitle(model, null);
                    break;
                case R.id.llChangeSubtitleKnownStatus:
                    showSubtitleKnowDialog(model);
                    break;
                case R.id.llDictationMode:
                    openDictationScreen(model.getPosition());
                    break;
                case R.id.llPlayThisSubtitle:
                    handlePlayTheSubtitle(model);
                    break;
                case R.id.llOpenAraHanjaWithHanja:
                    Utils.openAraHanjaApp(activity, model.getVocaDisplay());
                    break;
                case R.id.llListenComprehension1:
                    openPlayerListenComprehension1Dialog(model);
//                    enterListenComprehensionMode1(model);
                    break;
                case R.id.llListenComprehension2:
                    enterListenComprehensionMode2(model);
                    break;
                case R.id.llListenComprehensionExit:
                    handleExitListenComprehensionMode();
                    break;
                case R.id.llListenComprehension1StartOver:
                    handleListenComprehension1StartOver();
                    break;
                case R.id.llShowNormalPlayingMenuUi:
                    handleVisiblityllPlay();
//                    showllPlay();
                    break;
                case R.id.llCopyTheSubtitle:
//                    showCopyLanguagesSubtitleDialog(model);
                    onCopyThisSubtitle(model);
                    break;
                case R.id.llGptWithThisSubtitle:
                    openGptCustomTab(model);
                    break;
                case R.id.llOpenPhraseInformation:
                    activity.openPhraseInformation(model);
                    break;
                case R.id.llWebDicationary:
                    openWebDictionary(model);
                    break;
                case R.id.llTranslateThisSubtitle:
                    openWebTranslate(model.getVocaDisplay());
                    //Don't open the translator in a new activity
//                    openTranslatorScreen(model);
                    break;
                case R.id.llDeleteRecordingFile:
                    deleteRecordingFile(model);
                    break;
                case R.id.llEditThisSubtitle:
                    openEditSubtitle(model);
                    break;
                case R.id.llDeleteTheSubtitle:
                    askToDeleteSubtitle(model);
                    break;
                case R.id.llMergeWithPreviousSubtitle:
                    askToMergeSubtitle(model, true);
                    break;
                case R.id.llMergeWithNextSubtitle:
                    askToMergeSubtitle(model, false);
                    break;
                case R.id.llDivideThisSubtitle:
                    askToDivideSubtitle(model);
                    break;
            }
        }

        @Override
        public void onDismiss(View view, Object object) {
            if (view == null) {
                if ((playerListenComprehension1Dialog != null) && (playerListenComprehension1Dialog.isShowing())) {
                    pausePlayer();
                } else {
                    playPlayer();
                }
            } else {
                switch (view.getId()) {
                    case R.id.llOpenAraHanjaWithHanja:
                        pausePlayer();
                        break;
                }
            }
        }
    };

    private OnClickDialogListener onPlayerSubtitleOptionSettingDialogListener = new OnClickDialogListener() {
        @Override
        public void onClick(View view, Object object) {
            //TODO : This is not called
            switch (view.getId()) {
                case R.id.llHiddenButtonsTransparencyOnFullScreen:
                    hideButtonsOnFullScreenWithAnimation();
                    break;
                case R.id.scSkipPlayingNoSubtitlePart:
                    updateValue_isSkipPlayingNoSubtitlePart((boolean) object);
                    break;
            }
        }

        @Override
        public void onDismiss(View view, Object object) {
            hideButtonsOnFullScreenWithAnimation();
            setSubtitleViewBackgroundColorOnFullScreenMode();
//            updateForwardBackwardValue();
            showStartEndTimeInSubtitleView();
//            getPlayDifficultWordsBeforePlayingSubtitle();
//            getIncludeMotherTongueSubtitle();
//            getPlayTitleByTtsBeforePlaying();

            updateValueFromSharedPreferences();

            if (view == null) {
                playPlayer();
            }
        }
    };

    private OnClickDialogListener onPlayerListenComprehensionOptionDialogListener = new OnClickDialogListener() {
        @Override
        public void onClick(View view, Object object) {

        }

        @Override
        public void onDismiss(View view, Object object) {
            hideButtonsOnFullScreenWithAnimation();
            updateValueFromSharedPreferences();
            playPlayer();

        }
    };

    private void showStartEndTimeInSubtitleView() {
        if (isDisplayStartEndTimeInSubtitleView() != subtitleDialogAdapter.isDisplayStartEndTimeAsMilliSeconds()) {
            subtitleDialogAdapter.setDisplayStartEndTimeAsMilliSeconds(isDisplayStartEndTimeInSubtitleView());
            refreshSubtitle();
        }
    }

//    private void updateForwardBackwardValue() {
//        tapForwardBackwardValue = sharedPreferences.getPlayerTapForBackWard() * Constant.PLAYER.TIMER.SECOND;
//        swipeForwardBackwardValue = sharedPreferences.getPlayerSwipeForBackWard() * Constant.PLAYER.TIMER.SECOND;
//    }

    private OnClickDialogListener onPlayerListenComprehension1DialogListener = new OnClickDialogListener() {
        @Override
        public void onClick(View view, Object object) {
            if (view.getId() == R.id.scSkipPlayingNoSubtitlePart) {
                updateValue_isSkipPlayingNoSubtitlePart((boolean) object);
            } else {
                playPlayer();
                if (view.getId() == R.id.tvOK) {
                    binding.ivListenComprehension1.setSelected(true);
                    DicModel dicModel = (DicModel) object;
                    enterListenComprehensionMode1(dicModel);
                }
            }
        }

        @Override
        public void onDismiss(View view, Object object) {
//            playPlayer();
        }
    };

    private void openTranslatorScreen(DicModel dicModel) {
        Intent intent = new Intent(activity, TranslatorActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, true);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_WORD, dicModel.getVocaDisplay());
        startActivity(intent);
    }

    private void showSubtitleDialogItemClickCaption() {
        pausePlayer();

        subtitleDialogCaption.show();
    }

    private OnClickDialogListener onSubtitleDialogCaptionListener = new OnClickDialogListener() {
        @Override
        public void onClick(View view, Object object) {
            switch (view.getId()) {
                case R.id.scHideKnownDialog:
                    updateHideKnownDialogValueAndRefreshSubtitle();
                    return;
                case R.id.llDeleteUselessSubtitles:
                    openSubtitleGroupToDeleteActivity();
                    break;
                case R.id.llSelectSubtitles:
                    openSubtitleGroupToSelectActivity();
                    break;
            }
            hideButtonsOnFullScreenWithAnimation();
        }

        @Override
        public void onDismiss(View view, Object object) {
            hideButtonsOnFullScreenWithAnimation();
            if (view == null) {
                playPlayer();
            }
        }
    };

    private void setMarginSearchLayoutWithStatusAndNavigationBar(int marginStatusBar, int marginNavigationBar) {
        getRoot().post(() -> {
            LinearLayout.LayoutParams rootLP = (LinearLayout.LayoutParams) getRoot().getLayoutParams();
            rootLP.topMargin = marginStatusBar;
            rootLP.bottomMargin = marginNavigationBar;
            getRoot().setLayoutParams(rootLP);
        });
    }

    private void showRepeatCountDialog(DicModel model, boolean isChangeAll, int typeDisplay) {
        pausePlayer();
        final String[] readCountValues = Voca.getRepeatCountValues(activity);
        int index = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.COUNT_OF_SMART_REPEAT;
        singleChoiceWithMessageDialog.show(
                R.string.repeat_count_per_dialog,
                R.string.desc_listen_comprehension_2,
                readCountValues,
                sharedPreferences.getMediaListen2RepeatIndex(),
                R.string.select,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        subtitleDialogAdapter.setListenComprehensionMode(true);
                        subtitleDialogAdapter.setListenComprehension1(false);
                        subtitleDialogAdapter.setListenComprehension2(true);

                        int which = (int) object;
                        sharedPreferences.setMediaListen2RepeatIndex(which);
                        if (!isChangeAll && which == index) return;
                        DLog.d(getLogTag(), "which=" + which + " - value=" + readCountValues[which]);
                        enterListenComprehensionMode();
                        subtitleDialogAdapter.setListenComprehension2(true);

                        setVisibleIvListenComprehension1(View.INVISIBLE);
                        updateChangeRepeatCount(model, isChangeAll, readCountValues[which], typeDisplay);
                    }

                    @Override
                    public void onDismiss(View view, Object object) {
                        playPlayer();
                    }
                });
    }

    private void updateChangeRepeatCount(DicModel model, boolean isChangeAll, String count, int typeDisplay) {
        int repeatCount = RepeatUtil.getRepeatCount(activity, count);
        int index = model != null ? model.getPosition() : 0;
        activity.runOnUiThread(() -> {
            if (isChangeAll) {
                repeatCountRepetition = repeatCount;
                activity.callAsyncTask(this, new int[]{index, typeDisplay}, TYPE_REPEAT_UPDATE);
            } else {
                if (model != null && model.getVIRepeatCount() != repeatCount) {
                    model.setVIRepeatCount(repeatCount);
                    activity.getSubDatabase().updateRepeatSubtitle(model);
                    binding.layoutPlayerSubtitleTable.rvListMeaning.post(() -> subtitleDialogAdapter.notifyItemChanged(model.getPosition()));
                }
            }
        });
    }

    private void updateRepeatAll(int repeat) {
        for (DicModel dic : subtitleList) {
            dic.setVIRepeatCount(repeat);
        }
        activity.getSubDatabase().updateRepeatSubtitle(repeat);
    }

    private void resetCheckedRepeatAll() {
        if (subtitleList == null) return;
        DLog.d(getLogTag(), "resetCheckedRepeatAll");
        for (DicModel dic : subtitleList) {
            dic.setVIChecked(false);
            dic.setPlayRecordOrTSS(false);
        }
    }

    private void startUpdateRepeatFromItemDialog(DicModel model) {
        if (repeatList != null && repeatList.isEmpty()) {
            resetCheckedRepeatAll();
        }

        model.setVIChecked(!model.isVIChecked());
        if (model.isVIChecked()) {
            repeatList.add(model.getPosition());
        } else {
            repeatList.remove(Integer.valueOf(model.getPosition()));
        }
        Collections.sort(repeatList);
    }

    private void endUpdateRepeatFromItemDialog(DicModel model) {
        if (repeatList.isEmpty()) {
            DLog.d(getLogTag(), "endUpdateRepeatFromItemDialog - repeatList is isEmpty - isRepeat=" + isRepeat);
            handleSubRepetitionByCCClick(0);
            subtitleDialogAdapter.notifyItemChanged(model.getPosition());
        } else {
            DLog.d(getLogTag(), "endUpdateRepeatFromItemDialog - isRepeat=" + isRepeat);
            if (!isRepeat) {
                //Dalnim : Moved this code here. It was below restoreFromDisplayListenComprehension  because it caused the llLMView(subtitle view's bottom UI) displays when I click the CC button during Listen Comprehension mode.
                updateValue_SubtitleIndex(model.getPosition());
                repeatIndex = subtitleIndex;
                handleSubRepetitionByCCClick(0);
                // not change typeDisplay when repeat with typeDisplay is listen comprehension
                restoreFromDisplayListenComprehension(true);
                seekToInPlayer((long) minSubWithAllExtraTime);
                // play player with repeat first (play voice can playing, so player stopped). Need play again
                handlePlayClick(true, false);
                // scroll to position when start repeat
                scrollToPositionSubtitleDialog();
            } else {
                // check and show listen comprehension UI (repeatList size > 1)
                setVisibleivRepeatBookmark(View.GONE);
                if (repeatList.size() > 1) {
                    handleVisiblityListenComprehensionUI(true);
                } else {
                    handleVisiblityListenComprehensionUI(false);
                    // reset repeat when repeatList is once
                    updateValue_SubtitleIndex(repeatList.get(0));
                    DicModel dicModel = getDicModel(subtitleIndex);
                    getMinMaxTime(dicModel);
                    updateRangeSeek();
                    scrollToPositionSubtitleDialog();
//                    // Dalnim : in CC Repeat mode, if there are more than 2 dialogs are CC Repeating and, don't want to call resetCheckedRepeatAll (If it's called then when I click one of the CC Repeating dialogs, all dialog's CC Repeating color is gone)
//                    if (repeatList.size() > 1) {
//                        resetPlayingDifficultWord();
//                    } else {
//                        resetPlayingDifficultWord(false);
//                    }
                }
                subtitleDialogAdapter.notifyItemChanged(model.getPosition());
            }
        }
    }

    private void initToolbar() {
        View popupView = getLayoutInflater().inflate(R.layout.item_search_vocabook, null);
        CheckBox cbTitle = popupView.findViewById(R.id.cb_title);
        CheckBox cbMeaning = popupView.findViewById(R.id.cb_meaning);
        cbTitle.setOnCheckedChangeListener(onCheckedChangeListener);
        cbMeaning.setOnCheckedChangeListener(onCheckedChangeListener);
        PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        binding.header.setPopupWindow(popupWindow);
        binding.header.setSearchListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchValue = s;
                callAsyncTaskSearch();
                return true;
            }
        }, () -> {
//            searchValue = Constant.BASE_BLANK;
//            viewBinding.header.getViewSearch().setQuery(searchValue, false);
//            viewBinding.header.setVisibility(View.GONE);
//            callAsyncTaskSearch();
            return true;
        });
        binding.header.showSearchView();
        binding.header.getIconLeft().setOnClickListener(v -> {
            handleOnClickBack();
        });
    }

    private void handleOnClickBack() {
        binding.header.setVisibility(View.GONE);
        getLLPlayer().setVisibility(View.VISIBLE);
        binding.layoutPlayerSubtitleTable.llLMView.setVisibility(View.VISIBLE);
        setMarginSearchLayoutWithStatusAndNavigationBar(0, 0);
        ((PlayerActivity) requireActivity()).setTransparentStatusBarAndNavigationBar();
        if (prevRotation == Surface.ROTATION_90) {
            handleRotateClick();
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
        int id = buttonView.getId();
        switch (id) {
            case R.id.cb_title:
                if (canCheckValue(isChecked)) {
                    isSearchTitle = isChecked;
                    callAsyncTaskSearch();
                } else {
                    buttonView.setChecked(true);
                }
                break;
            case R.id.cb_meaning:
                if (canCheckValue(isChecked)) {
                    isSearchMeaning = isChecked;
                    callAsyncTaskSearch();
                } else {
                    buttonView.setChecked(true);
                }
                break;
        }
    };

    private void callAsyncTaskSearch() {
        if (isOpenFirstTime) return;
        activity.callAsyncTask(this, Constant.PLAYER.SUB_TITLE.GROUP_TYPE.SEARCH, TYPE_SEARCHED_SUBTITLE);
    }

    private boolean canCheckValue(boolean isChecked) {
        DLog.d(getLogTag(), "canCheckValue - isChecked=" + isChecked);
        if (isChecked)
            return true;
        DLog.d(getLogTag(), "canCheckValue - isSearchTitle=" + isSearchTitle);
        DLog.d(getLogTag(), "canCheckValue - isSearchMeaning=" + isSearchMeaning);
        return isSearchTitle && isSearchMeaning;
    }

    public ArrayList<DicModel> generaSearchSubtitleDialog(ArrayList<DicModel> totalData) {
        if (Utils.isEmpty(searchValue) || totalData == null || totalData.isEmpty())
            return totalData;
        final ArrayList<DicModel> temp = new ArrayList<>();
        final int searchType = getSearchType(searchValue);
        final int searchIn = getValueSearchIn();
        DLog.d("generaSearchSubtitleDialog", "searchType=" + searchType + " - searchIn=" + searchIn);
        for (DicModel model : totalData) {
            if (checkSearchSubtitleDialog(searchType, searchIn, model, searchValue)) {
                temp.add(model);
            }
        }
        return temp;
    }


    private boolean checkSearchSubtitleDialog(int type, int searchIn, DicModel model, String searchValue) {
        final String text = searchValue.replace(Constant.SEARCH.KEY_PERCENT, Constant.BASE_BLANK);
        DLog.d("checkSearchSubtitleDialog", "text=" + text);
        String str1 = Constant.BASE_BLANK;
        String str2 = Constant.BASE_BLANK;
        String strTitle = Constant.BASE_BLANK;
        String strMeaning = Constant.BASE_BLANK;
        String tmpTitle = model.getVocaDisplay();
        if (!Utils.isEmpty(tmpTitle)) {
            strTitle = tmpTitle.toLowerCase();
        }
        String tmpMeaning = model.getMeaning();
        if (!Utils.isEmpty(tmpMeaning)) {
            strMeaning = tmpMeaning.toLowerCase();
        }
        if (searchIn == Constant.SEARCH.VALUE.TITLE) {
            str1 = strTitle;
        } else if (searchIn == Constant.SEARCH.VALUE.MEANING) {
            str1 = strMeaning;
        } else {
            str1 = strTitle;
            str2 = strMeaning;
        }

        if (searchIn == Constant.SEARCH.VALUE.ALL) {
            if (type == Constant.SEARCH.TYPE.START) {
                return str1.startsWith(text) || str2.startsWith(text);
            } else if (type == Constant.SEARCH.TYPE.END) {
                return str1.endsWith(text) || str2.endsWith(text);
            }
            return str1.contains(text) || str2.contains(text);
        } else {
            if (type == Constant.SEARCH.TYPE.START) {
                return str1.startsWith(text);
            } else if (type == Constant.SEARCH.TYPE.END) {
                return str1.endsWith(text);
            }
            return str1.contains(text);
        }
    }

    private int getSearchType(String value) {
        if (value.startsWith(Constant.SEARCH.KEY_PERCENT))
            return Constant.SEARCH.TYPE.START;
        if (value.endsWith(Constant.SEARCH.KEY_PERCENT))
            return Constant.SEARCH.TYPE.END;
        return Constant.SEARCH.TYPE.MATCHED;
    }

    private int getValueSearchIn() {
        if (isSearchTitle && isSearchMeaning)
            return Constant.SEARCH.VALUE.ALL;
        if (isSearchTitle)
            return Constant.SEARCH.VALUE.TITLE;
        return Constant.SEARCH.VALUE.MEANING;
    }

    private boolean isSubtitleDialogEyeClose() {
        return (Integer) binding.layoutPlayerSubtitleTable.ivLMSubEyeVertical.getTag() == R.drawable.ic_new_eye_close;
    }

    private void handleSubtitleDialogEyeClick() {
        activity.runOnUiThread(() -> {
            if (isHasShowSubtitle()) {
//                @DrawableRes Integer resId = R.drawable.ic_new_eye_open;
                Integer resIdInTag = (Integer) binding.layoutPlayerSubtitleTable.ivLMSubEyeVertical.getTag();
                if (resIdInTag == R.drawable.ic_new_eye_open) {
                    setShowDifficutlWordsOnlyInSubtitle();
                    setDefaultEyeImage(R.drawable.ic_new_eye_close_half);
                    refreshSubtitleUnlessSameSubtitle(getPositionFromCurrentTime());
                    setVisibleTvRubyBottomSubtitle(View.VISIBLE);
//                    setVisibleLlRubyButtomSubtitle(View.VISIBLE);
                } else if (resIdInTag == R.drawable.ic_new_eye_close_half) {
                    setHideSubtitle();
                    setDefaultEyeImage(R.drawable.ic_new_eye_close);
                    setVisibleTvRubyBottomSubtitle(View.INVISIBLE);
//                    setVisibleLlRubyButtomSubtitle(View.INVISIBLE);
                } else {
                    setShowSubtitle();
                    setDefaultEyeImage(R.drawable.ic_new_eye_open);
                    refreshSubtitleUnlessSameSubtitle(getPositionFromCurrentTime());
                    setVisibleTvRubyBottomSubtitle(View.VISIBLE);
//                    setVisibleLlRubyButtomSubtitle(View.VISIBLE);
                }

                if (timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.NONE) {
                    hideButtonsOnFullScreenWithAnimation();
                }

                if (isFullscreenMode()) {
                    showSubtitle(exoPlayer.getCurrentPosition(), -1);
                } else {
                    subtitleDialogAdapter.setShowAsterisk(showAsterisk);
                    notifyDataSetChanged_subtitleDialogAdapter();
                    scrollPositionInSubtitleTableView(binding.layoutPlayerSubtitleTable.rvListMeaning, subtitleIndex, subtitleIndex);
                }
            } else {
                binding.ivShowButtonsSubtitleEye.setImageResource(R.drawable.ic_new_eye_close_half);
                setSubtitleViewBackgroundColorOnFullScreenMode();
            }
        });
    }

    private void setDefaultEyeImage(Integer resId) {
        binding.layoutPlayerSubtitleTable.ivLMSubEyeVertical.setImageResource(resId);
        binding.layoutPlayerSubtitleTable.ivLMSubEyeVertical.setTag(resId);
        binding.ivShowButtonsSubtitleEye.setImageResource(resId);
        binding.ivShowButtonsSubtitleEye.setTag(resId);
    }

    private void scrollPositionInSubtitleTableView(RecyclerView list, int pos, int oldPos) {
        try {
            if (Math.abs(Math.abs(pos) - Math.abs(oldPos)) <= Constant.PLAYER.MAX_SMOOTH_SCROLL_POSITION) {
                list.smoothScrollToPosition(pos);
                return;
            }
            if (list.getLayoutManager() instanceof LinearLayoutManager) {
                if (isVerticalMode()) {
                    scrollToTopPositionInSubtitleTableView(list, pos);
                } else {
                    scrollToCenterPositionInSubtitleTableView(list, pos);
                }
            } else {
                ToastUtil.getInstance(activity).show("scrollPositionInSubtitleTableView else");
                list.scrollToPosition(pos);
            }
        } catch (Exception ex) {
            DLog.e(getLogTag(), "error=" + ex.getMessage());
        }
    }

    private void scrollToCenterPositionInSubtitleTableView(RecyclerView list, int pos) {
        final LinearLayoutManager manager = (LinearLayoutManager) list.getLayoutManager();
        final boolean isHorizontal = manager.getOrientation() == LinearLayoutManager.HORIZONTAL;
        int offset = isHorizontal
                ? (list.getWidth() - list.getPaddingLeft() - list.getPaddingRight()) / 2
                : (list.getHeight() - list.getPaddingTop() - list.getPaddingBottom()) / 2;
        final RecyclerView.ViewHolder holder = list.findViewHolderForAdapterPosition(pos);
        if (holder != null) {
            final View view = holder.itemView;
            offset -= isHorizontal ? view.getWidth() / 2 : view.getHeight() / 2;
        }
        manager.scrollToPositionWithOffset(pos, offset);
    }

    private void scrollToTopPositionInSubtitleTableView(RecyclerView list, int pos) {
        ((LinearLayoutManager) list.getLayoutManager()).scrollToPositionWithOffset(pos, 0);
    }


    private void openWordListPopUpFromSubtitle(DicModel model, OnClickListener callback) {
        if (model == null)
            return;

        DLog.d(getLogTag(), "getRubyList - model=" + model.toString());
        StudyChatAdapter adapter = new StudyChatAdapter(activity);
        adapter.setStudyLang(activity.studyLanguage.getIdApi());
        adapter.setDataNoLoop(activity.getSubDatabase().getVocaStudyChat(model.getId()));
        adapter.setSubtitleId(model.getId());
        adapter.setRightHandMode(isRightHandMode);
        setRecyclerViewDataDialog(adapter, callback);
    }

    private void setRecyclerViewDataDialog(StudyChatAdapter adapter, OnClickListener callback) {
        setRecyclerViewDataDialog(null, adapter, callback);
    }

    private void setRecyclerViewDataDialog(String title, StudyChatAdapter adapter, OnClickListener callback) {
        adapter.setDisplayPronunciation(sharedPreferences.getDisplayPronunciation());
        adapter.setHighlightIndex(0);
        adapter.setHasHeader(true);
        adapter.setListener(onVocaStudyChatClickListener);
        adapter.setDoubleClickListener(studyChatAdapterOnDoubleClickListener);
        recyclerSubtitleViewDialog.setAdapter(adapter, title);
        recyclerSubtitleViewDialog.show();
        recyclerSubtitleViewDialog.getAdapter();
        activity.playTTS.stop();
        activity.playTTS.playTTSHelper.resetPlayPlayer();
        if (callback != null) {
            callback.onClick(null, adapter);
        }
    }

    private OnVocaStudyChatClickListener onVocaStudyChatClickListener = new OnVocaStudyChatClickListener() {

        @Override
        public void onItemClick(VocaStudyChat voca) {
            openVocaStudyChatDialog(voca);
            stopTTSPlaying(voca);
        }

        @Override
        public void onDoubleItemClick(VocaStudyChat voca) {
            updateKnowWhenDoubleClick(voca);
//            VocaKnow.switchVocaKnow(voca);
//            updateKnow(voca);
        }

        @Override
        public void onPlayClick(final VocaStudyChat voca) {
            handleTTSPlay(voca, false);
        }

        @Override
        public void onBigIconClick(VocaStudyChat voca) {
            registerVocaDialog.show(voca, false);
            stopTTSPlaying(voca);
        }

        @Override
        public void onAsteriskSentenceClick(final VocaStudyChat voca) {
        }

        @Override
        public void onVocaKnowClick(VocaStudyChat voca, int vocaKnow) {
            onKnowChangeListenerForVocaWord.onVocaKnowChange(voca, vocaKnow);
            stopTTSPlaying(voca);
        }

        @Override
        public void onEvaluateGradeClick(VocaStudyChat voca, String grade) {
        }

        @Override
        public void onAnswerClick(VocaStudyChatExam voca) {
        }
    };

    private void openSubtitleMenuDialog() {
        DLog.d(getLogTag(), "openSubtitleMenuDialog - typeDisplay=" + typeDisplay);
        pausePlayer();
        final PlayerShowSubtitleMenuDialog dialog = new PlayerShowSubtitleMenuDialog(activity, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.tv_dictation_mode:
                        openDictationScreen(subtitleIndex);
                        break;
                    case R.id.tv_shadow_mode:
                        ToastUtil.getInstance(activity).show(R.string.msg_under_development);
                        break;
                }
            }

            @Override
            public void onDismiss(View view, Object object) {
                if (view == null) {
                    playPlayer();
                }
            }
        });
        dialog.show();
    }

    private void choosePlayingScreenThemeDialog() {
        DLog.d(getLogTag(), "choosePlayingScreenThemeDialog - typeDisplay=" + typeDisplay);
        pausePlayer();
        final PlayerShowPlayerThemeDialog dialog = new PlayerShowPlayerThemeDialog(activity, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.tvPalyerTheme1:
                        ToastUtil.getInstance(activity).show(R.string.msg_under_development);
                        break;
                    case R.id.tvPalyerTheme2:
                        ToastUtil.getInstance(activity).show(R.string.msg_under_development);
                        break;
                }
            }

            @Override
            public void onDismiss(View view, Object object) {
                if (view == null) {
                    playPlayer();
                }
            }
        });
        dialog.show();
    }

    private void handleCCRepeatReset() {
        DicModel dicModel = getDicModel(subtitleIndex);

        dicModel.setStartTime(dicModel.getStartTimeOriginal());
        dicModel.setEndTime(dicModel.getEndTimeOriginal());
        activity.getSubDatabase().updateTimeSubtitle(dicModel);
        getMinMaxTime(dicModel);
        if (isRepeatSubtitle()) {
            subtitleDialogAdapter.notifyItemChanged(dicModel.getPosition());
        }
        updateRangeSeek();
        setVisibleCCRepeatReset(View.INVISIBLE);
        resetCCRepeatTuningColor();
    }

    private void resetCCRepeatTuningColor() {
        binding.llRepeat.vCcRepeatTuningPreviousDialogue.setBackgroundResource(R.color.color_cc_repeat_tuning_screen_default);
        binding.llRepeat.vCcRepeatTuningNextDialogue.setBackgroundResource(R.color.color_cc_repeat_tuning_screen_default);
    }

    private int getMinTimeKeepPlayBetween() {
        //Used RepeatUtil.getMinTimeKeepPlaySubtitle (Forget why I don't use RepeatUtil's method -_-;;)
        return getMinTimeKeepPlaySubtitle(activity.playerFileModel.getVideoModel(), Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.BETWEEN);
    }
    //이제 이건 getKeepPlayBetweenSubtitle()구할때만 써진다. BEFORE, AFTER 등은 삭제해도 될듯
    private int getMinTimeKeepPlaySubtitle(VideoModel videoModel, int type) {
        float value;
        switch (type) {
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.BEFORE:
                value = videoModel.getPlayBeforeSubtitle();
                break;
            case Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.TYPE.AFTER:
                value = videoModel.getPlayAfterSubtitle();
                break;
            default:
                value = videoModel.getKeepPlayBetweenSubtitle();
                break;
        }
        int time = getTime(value);
        return time;
    }
    public int getTime(float value) {
        return (int) (value * Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.MIN_TIME_VALUE);
    }

    private void startOverSubtitleList() {
        if (subtitleList == null || subtitleList.isEmpty()) return;

        repeatIndex = 0;
        repeatCount = 0;
        final DicModel dicModel = getDicModel(0);
        if (dicModel != null) {
            getMinMaxTime(dicModel);
            seekToInPlayer((long) minSub);
            ToastUtil.getInstance(activity).show(R.string.msg_play_again_started_over);
        }
    }

    private void resetMinMaxSubtitle() {
        resetMinMaxSubtitle(0);
    }

    private void resetMinMaxSubtitle(int position) {
        if (Utils.isEmpty(subtitleList) || !BaseCollectionUtil.isSubtitleIndexInsideList(subtitleList, position)) return;
        DLog.d(getLogTag(), "resetMinMaxSubtitle - position=" + position);
        repeatIndex = position;
        repeatCount = 0;
        final DicModel dicModel = getDicModel(position);
        if (dicModel != null) {
            getMinMaxTime(dicModel);
        }
    }

    private void handlePlayDifficultWord() {
        if (!isPlayDifficultWordsBeforePlayingSubtitle())
            return;
        if (!isListenComprehensionMode())
            return;
        final DicModel dicModel = getDicModel(subtitleIndex);
//        if (dicModel == null || dicModel.isPlayRecordOrTSS() || VocaKnow.isKnown(dicModel))
//            return;

        if (ishandlePlayDifficultWord(dicModel)) {
            handlePlayClick(false, false);
            activity.playTTS.playTTSHelper.setSubtitleMotherTongue(dicModel.getMeaning());
            activity.playTTS.playTTSHelper.setSubtitleStudyLang(dicModel.getVocaDisplay());
            final boolean isPlayDifficult = isPlayDifficultWordsBeforePlayingSubtitle() && !Utils.isEmpty(dicModel.getWordsIds());
            activity.playTTS.playTTSHelper.setPlayDifficultWordsBeforePlayingSubtitle(isPlayDifficult);
            DLog.d(getLogTag(), "handlePlayDifficultWord - repeatSubTitle - subtitleIndex=" + subtitleIndex);
            activity.callAsyncTask(this, dicModel, TYPE_GET_DATA_PLAY_VOICE, false);
        }
    }

    private void handlePlayDifficultWordAtOnceSubtitleList(int subIndex) {
        if (!isPlayDifficultWordsBeforePlayingSubtitle())
            return;
        List<DicModel> dicModelList = new ArrayList<>();
        if (subIndex < subIndex + listComprehension1PlaySubtitlesAtOnce) {
            for (int i = subIndex; i < subIndex + listComprehension1PlaySubtitlesAtOnce; i++) {
                final DicModel dicModel = getDicModel(i);
//                if (dicModel == null || dicModel.isPlayRecordOrTSS() || VocaKnow.isKnown(dicModel))
//                    continue;

                if (ishandlePlayDifficultWord(dicModel)) {
                    handlePlayClick(false, false);
                    activity.playTTS.playTTSHelper.setSubtitleMotherTongue(dicModel.getMeaning());
//                    activity.playTTS.playTTSHelper.setSubtitleStudyLang(dicModel.getVocaDisplay());
//                    final boolean isPlayDifficult = isPlayDifficultWordsBeforePlayingSubtitle() && !Utils.isEmpty(dicModel.getWordsIds());
//                    activity.playTTS.playTTSHelper.setPlayDifficultWordsBeforePlayingSubtitle(isPlayDifficult);
                    dicModel.setPlayRecordOrTSS(true);
                    dicModelList.add(dicModel);
                    DLog.d(getLogTag(), "handlePlayDifficultWordAtOnceSubtitleList - repeatSubTitle - subtitleIndex=" + i);
                }
            }
        }
        if (!dicModelList.isEmpty()) {
            activity.playTTS.playTTSHelper.setPlayDifficultWordsBeforePlayingSubtitle(true);
            activity.callAsyncTask(this, dicModelList, TYPE_GET_ALL_DIFFICULT_WORD_AT_ONCE_SUBTITLE_LIST_TO_PLAY_VOICE, false);
        }
    }

    private boolean ishandlePlayDifficultWord(DicModel dicModel) {
        boolean result = false;
        if (!(dicModel == null || dicModel.isPlayRecordOrTSS() || VocaKnow.isKnown(dicModel))) {
            final String wordIds = dicModel.getWordsIds();
            final String subtitleMotherTongue = dicModel.getMeaning();
            final String subtitleStudyLang = dicModel.getVocaDisplay();
            final boolean isIncludeMother = isIncludeMotherTongueSubtitle() && !Utils.isEmpty(subtitleMotherTongue);
            final boolean isIncludeStudyLang = isIncludeMotherTongueSubtitle() && !Utils.isEmpty(subtitleStudyLang);
            final boolean isPlayDifficult = isPlayDifficultWordsBeforePlayingSubtitle() && !Utils.isEmpty(wordIds);

            if (isPlayDifficult || isIncludeMother || isIncludeStudyLang) {
                result = true;
            }
        }
        return result;
    }


    private void handlePlayAllDifficultWordBeforePlaying() {
        activity.callAsyncTask(this, null, TYPE_GET_ALL_DIFFCULT_WORD_TO_PLAY_VOICE, false);
    }

    private void resetPlayingDifficultWord() {
        resetPlayingDifficultWord(true);
    }

    private void resetPlayingDifficultWord(boolean isAll) {
        DLog.d(getLogTag(), "resetPlayingDifficultWord - isAll=" + isAll);
        activity.playTTS.playTTSHelper.stop();
        activity.playTTS.playTTSHelper.resetPlayPlayer();
        if (isAll) {
//            isPlayingDifficultWordAndMotherTongueSubtitle = false;
            resetCheckedRepeatAll();
        }
    }

    private boolean isRepeatSubtitle() {
        return isRepeat && isDisplaySubtitle();
    }

    private void handlePlayTheSubtitle(DicModel model) {
        if (isRepeat || !repeatList.isEmpty()) {
            return;
        }
        if (isListenComprehensionMode())
            return;
//        resetPlayingDifficultWord();
        final long position = getStartTime(model);
        seekToInPlayer(position);
        updateSeekBarPlay(position);
        showSubtitle(position);
        repeatIndex = subtitleIndex;
        DLog.d(getLogTag(), "handlePlayTheSubtitle - subPosition=" + subtitleIndex + " - repeatPosition=" + repeatIndex);
        repeatCount = 0;
        DicModel dicModel = getDicModel(subtitleIndex);
        getMinMaxTime(dicModel);
        scrollToPositionSubtitleDialog();
        handlePlayClick(true, false);
    }

    private void initListComprehension1SubtitleList(int position) {
        listComprehension1SubtitleList = new ArrayList<>();
        for (int i = position; i < subtitleList.size(); i++) {
            listComprehension1SubtitleList.add(i);
        }
//        if (position > 0) {
//            for (int i = 0; i < position; i++) {
//                listComprehension1SubtitleList.add(i);
//            }
//        }

        int countOfPlayParts = sharedPreferences.getPlayerListenComprehensionPlaySubtitlesPlayParts();
        if (comprehension1PlaySubtitlesPlayPartsCount > 0) {
            countOfPlayParts = comprehension1PlaySubtitlesPlayPartsCount;
        }
        if (listComprehension1SubtitleList.size() >= countOfPlayParts)
            listComprehension1SubtitleList = listComprehension1SubtitleList.subList(0, countOfPlayParts);
    }

    private void openListenComprehensionModeMain(int position) {
        DLog.d(getLogTag(), "openListenComprehensionMode typeDisplay=" + typeDisplay + " - position=" + position);
        initListComprehension1SubtitleList(position);
        comprehension1SubtitleStartIndex = position;
//        listComprehension1SubtitleList = new ArrayList<>();
//        // create list play - list comprehension
//        for (int i = position; i < subtitleList.size(); i++) {
//            listComprehension1SubtitleList.add(i);
//        }
//        if (position > 0) {
//            for (int i = 0; i < position; i++) {
//                listComprehension1SubtitleList.add(i);
//            }
//        }
        if (listComprehension1SubtitleList.isEmpty())
            return;

        updateValue_comprehensionRepeatedCount(0);//listComprehensionRepeatedCount = 0;
        updateValue_listComprehensionCurrentIndex(-1);
        updateValue_listComprehensionModelIndex(0);
        resetRepeat();
        repeatCountOfListen1 = 1;
        updateValue_comprehension1SubtitleIndexInList(0);

        getListenComprehensionData();
        resetMinMaxSubtitle(position);
        int repeatCountOfDialog = 0;
        final DicModel item = getDicModel(listComprehension1SubtitleList.get(listComprehensionModelIndex));
        seekToInPlayer((long) minSubWithAllExtraTime);

        if (isDisplayListenComprehension2()) {
            repeatCountOfDialog = RepeatUtil.getRepeatValue(item);
            hideOrShowDialogsRuleAtRepeating(item);
        }
        updateListenComprehensionCurrentStatus(repeatCountOfDialog);
        scrollToPositionSubtitleDialog();
        handlePlayClick(true, false);
    }

    //If I omit typeDisplay, I can't see Listen Comprehension UI(Title, Exit button etc) when I double tap on the playing screen.
    private void openListenComprehensionMode(int typeDisplay, int position) {
        this.typeDisplay = typeDisplay;
        openListenComprehensionModeMain(position);
        // update UI
        handleVisiblityListenComprehensionUI(isShowVisiblityListenComprehensionUIAtFirstTime());
    }

    private boolean isShowVisiblityListenComprehensionUIAtFirstTime() {
        boolean isShow = false;
        if (isDisplayListenComprehension1()) {
            isShow = sharedPreferences.isShowListenComprehnesion1UIAtFirstTime();
            sharedPreferences.setDontShowListenComprehnesion1UIAtFirstTime();
        } else if (isDisplayListenComprehension2()) {
            isShow = sharedPreferences.isShowListenComprehnesion2UIAtFirstTime();
            sharedPreferences.setDontShowListenComprehnesion2UIAtFirstTime();
        }

        if (!isShow) {
            ToastUtil.getInstance(getContext()).show(R.string.messsage_listen_comprehension_mode_starts);
        }
        return isShow;
    }


    private void getListenComprehensionData() {
        listComprehension1PlaySubtitlesAtOnceCount = 1;
        listComprehension1PlaySubtitlesAtOnce = sharedPreferences.getPlayerListenComprehensionPlaySubtitlesAtOnce();
        if (listComprehension1PlaySubtitlesAtOnce > subtitleList.size() || listComprehension1PlaySubtitlesAtOnce <= 0) {
            listComprehension1PlaySubtitlesAtOnce = subtitleList.size();
        }
        listenComprehension1ModelList = ListenComprehensionQuery.getAllByCount(Voca.getRealm());
        if (!isHasListenComprehension1()) {
            updateValue_listComprehensionCurrentIndex(-1);
        } else if (listComprehensionCurrentIndex >= listenComprehension1ModelList.size()) {
            updateValue_comprehensionRepeatedCount(0);//listComprehensionRepeatedCount = 0;
            updateValue_listComprehensionModelIndex(0);
        }
    }

    private void handleShowAsteriskInListenComprehension1() {
        if (listComprehensionModelIndex == listComprehensionCurrentIndex || !isHasListenComprehension1())
            return;

        updateValue_listComprehensionCurrentIndex(listComprehensionModelIndex);
        final ListenComprehensionModel item = listenComprehension1ModelList.get(listComprehensionCurrentIndex);
        DLog.d(getLogTag(), "handleListenComprehension - listComprehensionIndex=" + listComprehensionCurrentIndex + " - listComprehensionCount=" + comprehensionRepeatedCount + " - count=" + item.getCount());
        switch (item.getType()) {
            case Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.HIDE_THE_SUBTITLE:
                setHideSubtitle();//setShowDifficutlWordsOnlyInSubtitle();
                break;
            case Constant.PLAYER.SUB_TITLE.LISTEN_COMPREHENSION_1.TYPE.SHOW_THE_SUBTITLE:
                setShowSubtitle();//setHideSubtitle();
                break;
            default:
                setShowDifficutlWordsOnlyInSubtitle();//setShowSubtitle();
                break;
        }
    }

    private void updateListenComprehensionCurrentStatus() {
        updateListenComprehensionCurrentStatus(0);
    }

    private void updateListenComprehensionCurrentStatus(int repeatCountOfDialog) {
        if (!isHasListenComprehension1()) return;
        DLog.d(getLogTag(), "updateListenComprehensionCurrentStatus");
        subtitleDialogAdapter.setCurrentListenComprehensionCount(comprehensionRepeatedCount);
        notifyDataSetChanged_subtitleDialogAdapter();
        String status1;
        String status2;
        String status3 = getDisplayTimes(exoPlayer.getCurrentPosition()) + "/" + getDisplayTimes(exoPlayer.getDuration());

        if (isDisplayListenComprehension1()) {
            final ListenComprehensionModel item = listenComprehension1ModelList.get(listComprehensionModelIndex);
            int listComprehensionCountToDisplay = comprehensionRepeatedCount >= item.getCount() ? item.getCount() : comprehensionRepeatedCount + 1;
            String text = getString(R.string.repeat_times_no_title, listComprehensionCountToDisplay, item.getCount());
            if (item.getCount() < 2)
                text = "";
            String totalListGroupCount = listComprehension1PlaySubtitlesAtOnceCount + "/" +  listComprehension1SubtitleList.size();
            DLog.d(getLogTag(), "updateListenComprehensionCurrentStatus - listComprehension1PlaySubtitlesAtOnceCount " + listComprehension1PlaySubtitlesAtOnceCount);
            DLog.d(getLogTag(), "updateListenComprehensionCurrentStatus - listComprehension1SubtitleList.size() " + listComprehension1SubtitleList.size());
            status1 = text + " " + ((comprehension1SubtitleIndexInList % listComprehension1PlaySubtitlesAtOnce) + 1) + "/" + listComprehension1PlaySubtitlesAtOnce;
            status2 = (listComprehensionModelIndex + 1) + "/" + listenComprehension1ModelList.size() + " " + getString(item.getTitle()) + ", " + totalListGroupCount + " " + getString(R.string.listen_comprehension_1_subtitles_play_parts);
        } else {
            int listComprehensionCountToDisplay = comprehensionRepeatedCount >= repeatCountOfDialog ? repeatCountOfDialog : comprehensionRepeatedCount + 1;
            status1 = (listComprehensionCountToDisplay) + "/" + repeatCountOfDialog + " " + getString(Voca.getAsteriskTitle(showAsterisk));
            status2 = getString(R.string.listen_comprehension_2);
        }

        activity.runOnUiThread(() -> {
            if (isDisplayListenComprehension1()) {
                binding.layoutListenComprehension.tvListenComprehensionDescription.setText(getString(listComprehension1PlaySubtitlesAtOnce > 1 ? R.string.listen_comprehension_current_status_times :
                        R.string.listen_comprehension_current_status_time, status1, status2, status3));
            } else {
                binding.layoutListenComprehension.tvListenComprehensionDescription.setText(getString(R.string.listen_comprehension_2_current_status, status1, status2, status3));
            }
        });
    }

    private void restoreFromDisplayListenComprehension() {
        restoreFromDisplayListenComprehension(false);
    }

    private void restoreFromDisplayListenComprehension(boolean keepListenComprehension) {
        DLog.d(getLogTag(), "restoreFromDisplayListenComprehension - keepListenComprehension=" + keepListenComprehension);
        if (isListenComprehensionMode()) {
            //Dalnim : if it's not CCRepeatmode, then reset this value. But I'm not sure this is correct code. So I uncommented these out.
            //But if I added this code, and run listen comprehension 2 and click CC Repeat button, then unclick the CC Repeat button, the List comprehension 2 is exited(should stay) and the title of Listen comprehension 2 is on.
            //So I commented this out.
            //April 26, 2021 : If I don't have this code, Finishing Listen comprehension mode in the Bookmarked Subtitle Group, I can hear a dialog is playing multiple times, it's because the repeat count is not set to 1(Default Value)
            if (!isCCRepeatMode()) {
                listenComprehension1ModelList.clear();
                updateRepeatAll(Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.DEAFULT_VALUE);
            }
            setShowSubtitle();

            if (!keepListenComprehension) {
                typeDisplay = Constant.PLAYER.SUB_TITLE.DISPLAY.SUBTITLE_LIST;
                //Dalnim
                //Don't show the Listen comprehension 2's repeating count (ex, 2/4 next to CC Repeat button in the subtitle table view), when I unclick the CC Repeat button while Listen Comprehension 2 is running)
                //Then AraPlayer will show just total repeat count only.(ex 4, not 1/4 for the current playing dialog)
                subtitleDialogAdapter.setListenComprehension2(false);
                notifyDataSetChanged_subtitleDialogAdapter();
            }
            handleVisiblityListenComprehensionUI(false);
            binding.layoutPlayerSubtitleTable.rvListMeaning.setVisibility(View.VISIBLE);
        }
    }

    //Todo : I think this is not correct. getSubtitleIndexFromCurrentTime is better though it may take more time.
    private int getPositionFromCurrentTime() {
        final long position = exoPlayer.getCurrentPosition();
        int index = 0;
        for (DicModel s : subtitleList) {
            if (position >= getStartTime(s) && position <= getEndTime(s)) {
                return -1;
            }
        }
        for (int i = 0; i < subtitleList.size(); i++) {
            if (getStartTime(subtitleList.get(i)) > position) {
                index = i > 0 ? i - 1 : i;
                break;
            }
        }
        return index;
    }

    private void removeSubtitleInSubtitleList(DicModel model) {
        subtitleListTotal.remove(model);
        subtitleList.remove(model);
        updateValue_SubtitleIndex(BaseCollectionUtil.decreaseIndexInList(subtitleList, subtitleIndex));
        handleAfterModifySubtitle();
        resetMinMaxSubtitle(BaseCollectionUtil.getDefaultIndexIfOutOfIndex(subtitleList, model.getPosition()));
    }

    //After delete or add subtitle (Not change subtitle's content)
    private void handleAfterModifySubtitle() {

        getFistIsStudyLangInSubtitle();
        checkSubtitleLangHasData();

        SubtitlePositionTimeUtil.resetSubtitlePosition(subtitleList);

        updateValue_SubtitleIndexFromCurrentTime();
        //Subtitle index can have -1. (It's the playing time is before 1st subtitle, AraPlayer shows empty subtitle for this case
        if (!BaseCollectionUtil.isSubtitleIndexInsideList(subtitleList, subtitleIndex))
            return;

//        if (subtitleIndex != -1)
//            getMinMaxTime(subtitleList.get(subtitleIndex));

        if (isFullscreenMode()) {
            showSubtitle(exoPlayer.getCurrentPosition(), -1);
        }
        binding.layoutPlayerSubtitleTable.rvListMeaning.post(() -> {
            subtitleDialogAdapter.setData(subtitleList);
            notifyDataSetChanged_subtitleDialogAdapter();
        });
        if (FileUtil.isMusicApp()) {
            if (binding.layoutPlayerSubtitleTable.svTvLyric.getVisibility() == View.VISIBLE) {
                hideLyricTxtViewAndShowSubtitle();
            }
        }
    }

    private void openPlayerOptionDialog(PlayerFileModel playerFileModel) {
        DLog.d(getLogTag(), "openOptionPlayer - file=" + playerFileModel.toString());
        playerOptionDialog.setRightHandMode(isRightHandMode);
        playerOptionDialog.setIsSkipPlayingNoSubtitlePart(isSkipPlayingNoSubtitlePart);
        playerOptionDialog.show();
    }

    private void openPlayerSettingDialog(PlayerFileModel playerFileModel) {
        DLog.d(getLogTag(), "openOptionPlayer - file=" + playerFileModel.toString());
        playerSettingDialog.setRightHandMode(isRightHandMode);
        playerSettingDialog.show();
    }

    private void openPlayerListenComprehensionOptionDialog() {
        PlayerListenComprehensionOptionDialog playerListenComprehensionOptionDialog = new PlayerListenComprehensionOptionDialog(activity, onPlayerListenComprehensionOptionDialogListener);
        playerListenComprehensionOptionDialog.show();
    }

    private void openPlayerListenComprehension1Dialog(DicModel dicModel) {
        pausePlayer();
        playerListenComprehension1Dialog = new PlayerListenComprehension1Dialog(activity, dicModel, onPlayerListenComprehension1DialogListener);
        playerListenComprehension1Dialog.setIsSkipPlayingNoSubtitlePart(isSkipPlayingNoSubtitlePart);
        playerListenComprehension1Dialog.show();
    }

    // https://github.com/dalnim/IssueOnly/issues/141
    private void openVocaStudyChatDialog(VocaStudyChat voca) {
        final VocaStudyChatDialog dialog = new VocaStudyChatDialog(activity, voca, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llOpenPhraseInformation:
                        activity.openPhraseInformation((IVocaFullItem) object);
                        break;
                    case R.id.tv_edit_phrase:
                        activity.openEditMeaningScreen((IVocaFullItem) object);
                        break;
                    case R.id.tv_play_this_phrase:
                        handleTTSPlay(voca, false);
                        break;
                    case R.id.tv_web_dictionary:
                        openWebDictionary(object);
                        break;
                    case R.id.tv_copy_this_phrase:
                        onCopyThisPhrase(object);
                        break;
                    case R.id.llOpenAraHanjaWithHanja:
                        Utils.openAraHanjaApp(activity, ((IVocaBasicItem) object).getVIVoca());
                        break;
                }
            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void openWebDictionary(Object data) {
        if (NetworkUtil.isNetworkConnetedIfNotShowWarningAsPopup(getActivity())) {
            pausePlayer();
            String voca = data == null ? "" : data.toString();
            final WebDictionaryModel webDictionary = WebDictionaryQuery.getFirst(Voca.getRealm(),
                    activity.studyLanguage.getIdApi(), activity.motherTongueLanguage.getIdApi());
            if (data instanceof AmkiItem) {
                AmkiItem amkiItem = (AmkiItem) data;
                voca = amkiItem.getAmki();
            } else if (data instanceof DicModel) {
                DicModel dicModel = (DicModel) data;
                voca = dicModel.getVIVoca();
            }

            String url;
            if (webDictionary == null) {
                if (activity.motherTongueLanguage.getIdApi() == EnumLanguage.KOREAN.getIdApi()) {
                    url = Constant.PLAYER.WEB_DICTIONARY.URL_DEFAULT + voca;
                } else {
                    openWebDictionary();
                    return;
                }
            } else {
                url = webDictionary.getUrl().replace(Constant.PLAYER.WEB_DICTIONARY.WORD_REPLACE, voca);
            }
            final WebDictionaryDialog dialog = new WebDictionaryDialog(activity, url, new OnClickDialogListener() {
                @Override
                public void onClick(View view, Object object) {

                }

                @Override
                public void onDismiss(View view, Object object) {
                    hideButtonsOnFullScreenWithAnimation();
                    doNotResumePlayingWhenWordlistDialogIsShowing();
                }
            });
            dialog.show();
        }
    }

    private void openWebTranslate(String text) {
        TranslateUtil.openWebTranslate(getActivity(), text, () -> {
            onDismissPopupSubtitleTranslateDialog();
        });
    }

    private void onDismissPopupSubtitleTranslateDialog() {
        hideButtonsOnFullScreenWithAnimation();
        doNotResumePlayingWhenWordlistDialogIsShowing();
    }

    private void doNotResumePlayingWhenWordlistDialogIsShowing() {
        if (recyclerSubtitleViewDialog != null && recyclerSubtitleViewDialog.isShowing()) {
        } else {
            playPlayer();
        }
    }

//    private void openPhraseInformation(Object data) {
//        if (Utils.isConnected(activity)) {
//            if (data instanceof IVocaFullItem) {
//                IVocaFullItem iVocaFullItem = (IVocaFullItem) data;
//                if (iVocaFullItem.getVIVocaId() >= 0) {
//                    openNewScreen(
//                            WordInfoActivity.createIntent(activity, iVocaFullItem)
//                    );
//                } else {
//                    openEditMeaningScreen(iVocaFullItem);
//                }
//            }
//        } else {
//            activity.alertDialog.showNoInternet();
//        }
//    }

    //    private void openEditMeaningScreen(Object data) {
//        if (Utils.isConnected(activity)) {
//            try {
////                final VocaStudyChat item = (VocaStudyChat) data;
//                IVocaFullItem iVocaFullItem = (IVocaFullItem) data;
//                DicModel dicModel = activity.getSubDatabase().getDicModelByVocaId(String.valueOf(iVocaFullItem.getVIVocaId()));
//
//                Intent intent = new Intent(activity, EditMeaningActivity.class);
//                intent.putExtra(Constant.BUNDLE.KEY_VOCA, (IVocaFullItem)dicModel);
//                openNewScreen(intent);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        } else {
//            activity.alertDialog.showNoInternet();
//        }
//    }
//    public void openNewScreen(Intent i) {
//        if (this instanceof OnOpenNewScreen) {
//            ((OnOpenNewScreen) this).onOpen();
//        }
//        startActivity(i);
//    }

    private void openWebDictionary() {
        Intent intent = new Intent(activity, WebDictionaryActivity.class);
        startActivity(intent);
    }

    @Override
    protected boolean isFullscreenMode() {
        return !isVerticalMode() && binding.layoutPlayerSubtitleTable.llListMeaning.getVisibility() == View.GONE;
    }

    private boolean isShowButtonsOnFullScreen() {
        return !isVerticalMode() && isFullscreenMode()
                && (isDisplaySubtitle() || isDisplayListenComprehension1() || isDisplayListenComprehension2());
    }

    private boolean isHideAllLayoutControl() {
        return binding.llPlaySub.root.getVisibility() == View.GONE
                && getLLPlay().getVisibility() == View.GONE
                && (binding.layoutListenComprehension.llListenComprehension.getVisibility() == View.GONE || isDisplayListenComprehension1() || isDisplayListenComprehension2());
    }

    private void setVisibilitySubtitleTableViewAndIcon(int visibility) {
        setVisibleSubtitleTable(visibility);
        ImageView ivShowHideTableView = binding.llPlay.ivShowHideTableview;
        if (isVisibleSubtitleTable()) {
            if (!isVerticalMode()) {
                if (binding.layoutPlayerSubtitleTable.llListMeaning.getWidth() == 0) {
                    setPanelWeights(playerWidthPercent);
                }
            } else {
                if (binding.layoutPlayerSubtitleTable.llListMeaning.getHeight() == 0) {
                    setPanelWeightsInPortrait();
                }
            }

//        if (isToShowSubtitleTableView(visibility)) {
            ivShowHideTableView.setImageResource(R.drawable.ic_subtitle_table_hide);
            ivShowHideTableView.setTag(R.drawable.ic_subtitle_table_hide);
            scrollToPositionSubtitleDialog();
//            setVisibleLlRubyButtomSubtitle(View.GONE); //During Listen Comprehension mode and show subtitle table view, then want to hide Subtitle bottom view.
//            setVisibleMeadiaTitle(View.GONE);
        } else {
            ivShowHideTableView.setImageResource(R.drawable.ic_subtitle_table_show);
            ivShowHideTableView.setTag(R.drawable.ic_subtitle_table_show);
            showSubtitleAlways(exoPlayer.getCurrentPosition());

//            if (isListenComprehensionMode())
//                setVisibleLlRubyButtomSubtitle(View.VISIBLE); //Only During Listen Comprehension mode and hide subtitle table view, then show Subtitle bottom view.

//            setVisibleMeadiaTitle(View.VISIBLE);
        }
//        showHideStatusbar();
        updateVisibilityButtonsOnFullScreen();
        getScreenSize();
        setVisibleLlRubyButtomSubtitle();
    }

    private boolean isToShowSubtitleTableView(int visibility) {
        return visibility == View.VISIBLE;
    }

    private boolean isShowButtonsAndMeaningOnFullScreen() {
        return isHideAllLayoutControl() && isShowButtonsOnFullScreen();
    }

    private void setSubtitleViewBackgroundColorOnFullScreenMode() {
        int alpha = getAlphaSubtitleViewTransparency();
        binding.llRubyBottom.setBackgroundColor(ColorUtils.setAlphaComponent(activity.getResources().getColor(R.color.color_player_background_subtitle_not_transparent, activity.getTheme()), alpha));
        setVisibleLlRubyButtomSubtitle();
//        //
//        if (alpha == 0 && !isHasSubtitleTotal()) {
//            setVisibleLlRubyButtomSubtitle(View.GONE);
//        } else {
//            setVisibleLlRubyButtomSubtitle(View.VISIBLE);
//        }
    }

    private void updateVisibilityButtonsOnFullScreen() {
//        ImageView ivShowButtonsRepetitionCCRight = viewBinding.ivShowButtonsRepetitionByCcRight;
        if (isShowButtonsAndMeaningOnFullScreen()) {
            setVisibleShowButtonsOnFullScreen(View.VISIBLE);
            binding.llRepeat.llRepeatStartEnd.setVisibility(View.VISIBLE);

            // restore icon on full screen
            if (timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.A) {
                setVisibleCCRepeatUI(View.INVISIBLE);
            }
//            setVisibleLlRubyButtomSubtitle(View.VISIBLE);
        }

        if (!isRepeat && timeBaseRepeatStatus == Constant.PLAYER.REPEAT.TIMEBASE.NONE) {
            binding.ivShowButtonsRepetitionByAbRightOnMainPlayerScreen.setImageResource(R.drawable.ic_repeat_ab);
            // check display AB repeat button
            if (!isHasShowSubtitle()) {
                setVisibleCCRepeatUI(View.INVISIBLE);
                setVisibleButtonsEyeRight(View.INVISIBLE);
                setVisibleButtonsKnowValueRight(View.INVISIBLE);
                setVisibleIvListenComprehension(View.INVISIBLE);
            } else {
                setVisibleCCRepeatUI(View.VISIBLE);
                setVisibleButtonsKnowValueRight(View.VISIBLE);
                if (!isListenComprehensionMode()) {
                    setVisibleButtonsEyeRight(View.VISIBLE); //듣기 연습 모드에서 2번째 턴부터 Eye버튼이 보여서 이줄을 추가함.(안보이게)
                    setVisibleIvListenComprehension(View.VISIBLE);
                }
            }
            hideOrShowABRepeatButtonOnFullScreen();
            if (!isListenComprehensionMode())
                setVisibleButtonsEyeRight(View.VISIBLE);
        }
//        if (isRepeat && !isABRepeatMode()) {
//            vLayoutCCRepeatTuning.setVisibility(View.VISIBLE);
//        } else {
//            vLayoutCCRepeatTuning.setVisibility(View.GONE);
//        }
    }

    private void updateVisibilityTableView() {
        if (binding.layoutPlayerSubtitleTable.llListMeaning.getVisibility() == View.VISIBLE) {
            setVisibilitySubtitleTableViewAndIcon(View.GONE);
            sharedPreferences.setShowSubtitleTableWhenOpen(false);
            //Without this AraPlayer doesn't show the KNOW value for first subtitle.
            DicModel dicModel = getDicModel(subtitleIndex);
            if (dicModel != null) {
                VocaKnow.updateIconVocaKnow(activity, binding.tvShowButtonsKnowValueRight, getDicModel(subtitleIndex).getVIVocaKnow());
            }
        } else {
            setVisibilitySubtitleTableViewAndIcon(View.VISIBLE);
            sharedPreferences.setShowSubtitleTableWhenOpen(true);
        }
//        setVisibilitySubtitleTableViewAndIcon(llListMeaning.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        showHideStatusbar();
        checkSubtitleLangHasData();
    }

    private void showSubtitleKnowDialog(DicModel item) {
        if (item == null)
            return;

        pausePlayer();

        final RegisterVocaDialog registerVocaDialog = new RegisterVocaDialog(activity, new OnKnowChangeListener() {
            @Override
            public void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {
                playPlayer();
                if (activity.updateModelWithNewVocaKnow(iVocaBasicItem, newVocaKnow, -1)) {
                    updateVocaKnow(iVocaBasicItem);
                }
            }


            @Override
            public void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce) {
                playPlayer();
                if (activity.updateModelWithNewVocaKnow(iVocaBasicItem, -1, newVocaKnowPronounce)) {
                    updateVocaKnow(iVocaBasicItem);
                }
            }

            @Override
            public void onAddToWordbook(IVocaBasicItem iVocaBasicItem) {
                playPlayer();
            }

            @Override
            public void onAddToBookmark(IVocaBasicItem iVocaBasicItem) {
                updateBookmark(item);
//                playPlayer();
            }

            @Override
            public void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem) {
                updateBookmark(item);
//                playPlayer();
            }

            @Override
            public void onDismiss() {
                playPlayer();
            }
        });
        registerVocaDialog.show(item, false);
    }

    protected void updateBookmark(IVocaBasicItem iVocaBasicItem) {
        super.updateBookmark(iVocaBasicItem);
        if (isFullscreenMode()) {
            applyKnowValueInSubtitleOnFullScreen(iVocaBasicItem);
        } else {
            applyKnowValueInSubtitleInSubtitleTableView(iVocaBasicItem);
        }
    }

    private void applyKnowValueInSubtitle(IVocaBasicItem iVocaBasicItem) {
        if (iVocaBasicItem == null) return;

        VocaTypeId vocaTypeId = new VocaTypeId(iVocaBasicItem.getVIVocaId(), iVocaBasicItem.getVIVocaType());
        if (activity.isVocaTypeSubtitleAndHasOneWord(iVocaBasicItem)) {
            vocaTypeId = new VocaTypeId(iVocaBasicItem.getVIVocaIdBase(), iVocaBasicItem.getVIVocaTypeBase());
        }
        activity.callAsyncTask(this, vocaTypeId, TYPE_UPDATE_WORD_VOCA_KNOW, false);

        // Need this? (Don't delete it until bookmark feature is refactored.)
//        if (isFullscreenMode()) {
//            applyKnowValueInSubtitleOnFullScreen(iVocaBasicItem);
//
//            if (isVocaTypeSubtitle(iVocaBasicItem)) {
//                if (VocaKnow.isKnown(iVocaBasicItem.getVIVocaKnow())) {
//                    setShowDifficutlWordsOnlyInSubtitle();
//                } else {
//                    setShowSubtitle();
//                }
//                hideButtonsOnFullScreenWithAnimation();
//            }
//
//        } else {
//            //TODO : If Subtitle has only 1 word, then need to update the Word's KNOW value too.
////            subtitleList = (ArrayList<DicModel>) Voca.udpateVocaKnowInSubtitleList(subtitleList, item);
//            applyKnowValueInSubtitleInSubtitleTableView(iVocaBasicItem);
//        }
    }

    private void applyKnowValueInSubtitleOnFullScreen(IVocaBasicItem item) {
        showSubtitleAlways(exoPlayer.getCurrentPosition());
        refreshButtonsOnFullScreen(item);
    }

    private void applyKnowValueInSubtitleInSubtitleTableView(IVocaBasicItem item) {
        subtitleList = (ArrayList<DicModel>) Voca.udpateVocaKnowInSubtitleList(subtitleList, item);
        subtitleDialogAdapter.setData(subtitleList);
        notifyDataSetChanged_subtitleDialogAdapter();
    }

    private void udpateCCRepeatIconInSubtitleView(DicModel item) {
        if (isCCRepeatMode()
                && !isFullscreenMode()
                && (!isHasMoreThanTwoDialogsInCCRepeat())) {
            repeatList.clear();
            startUpdateRepeatFromItemDialog(item);
            notifyDataSetChanged_subtitleDialogAdapter();
            scrollToPositionSubtitleDialog();
        }
    }

    //Dalnim add
    private void refreshButtonsOnFullScreen(IVocaBasicItem item) {
//        updateBookmarkUI(item);
        updateKnowPronounceUI(item);
        VocaKnow.updateIconVocaKnow(activity, binding.tvShowButtonsKnowValueRight, item.getVIVocaKnow());
    }


//    private void updateBookmarkUI(IVocaBasicItem item) {
//        ivShowButtonsBookmark.setVisibility(item.isVIBookmark() ? View.VISIBLE : View.INVISIBLE);
//    }

    private void updateKnowPronounceUI(IVocaBasicItem item) {
        binding.ivBookmarkRight.setVisibility(item.isVIBookmark() ? View.VISIBLE : View.INVISIBLE);
        VocaKnow.updateIconVocaKnowPronounce(getContext(), binding.ivShowButtonsKnowPronounceRight, item);
//        boolean showKnowPronounceIcon = VocaKnow.showKnowPronounceIcon(sharedPreferences.getLangStudyCode(), item.getVIVocaKnow(), item.getVIVocaKnowPronounce());
//        ivShowButtonsKnowPronounceRight.setVisibility(showKnowPronounceIcon ? View.VISIBLE : View.INVISIBLE);
    }

    private void updateLayoutSubtitleWhenChangeRotate() {
        if (!isHasShowSubtitle())
            return;

        showllPlay();
        scrollToPositionSubtitleDialog(); //There is always the subtitle table view when AraPlayer rotate the screen.
    }

    private void resizePlayerView() {
        getPlayerView().setResizeMode(screen_resize_mode);
    }

    private void showWarningClearVideoCacheDialog() {
        final ConfirmationDialog dialog = new ConfirmationDialog(
                activity,
                R.string.warning,
                R.string.clear_video_cache_msg,
                R.string.no_upper,
                R.string.yes_upper,
                new ConfirmationDialog.OnDialogClickListener() {
                    @Override
                    public void onPositive(DialogInterface dialog) {
                        dialog.dismiss();
                        playPlayer();
                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {
                        //YES falls here
                        dialog.dismiss();
                        activity.callAsyncTask(PlayerFragment.this, TYPE_CLEAR_VIDEO_CACHE);
                    }
                });
        dialog.show();
    }

    private void clearVideoCache() {
        for (DicModel dicModel : subtitleList) {
            dicModel.clearCache();
        }
        activity.getSubDatabase().updateDataFromRefreshSubtitle(subtitleList);
    }

    private void cancelSleep() {
        ToastUtil.getInstance(activity).show(R.string.msg_cancel_sleeping);
        destroyTimer();
        setVisibleSleepTime(View.GONE);
        isGoingToSleep = false;
    }

    private void sleep() {
//        final String[] readCountValues = Constant.PLAYER.SLEEP.RANGE;
        final String[] readCountValues = BaseVoca.getRepeatCountValues(1, 60);
        int index = Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.SMART_FEW + Constant.PLAYER.SUB_TITLE.REPEAT_COUNT.COUNT_OF_SMART_REPEAT;
        singleChoiceDialog.show(
                R.string.msg_will_sleep_in_future_title,
                readCountValues,
                sharedPreferences.getMediaSleepValue(),
                R.string.yes,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        hideButtonsOnFullScreenWithAnimation();
                        if (!isMenuUIVisible())
                            setVisibleSleepTime(View.VISIBLE);

                        isGoingToSleep = true;
//                        isSleeping = false;
                        int which = (int) object;
                        DLog.d(getLogTag(), "which=" + which + " - value=" + readCountValues[which]);
                        sharedPreferences.setMediaSleepValue(which);
                        int minutesToSleepInMilliUnit = Integer.parseInt(readCountValues[which]) * Constant.PLAYER.SLEEP.ONE_MINUTE;
                        float sleepInFutureMinute = minutesToSleepInMilliUnit / (1000f * 59);
                        ToastUtil.getInstance(activity).show(getString(R.string.msg_will_sleep_in_future, String.valueOf(Math.round(sleepInFutureMinute))));
                        countDownTimer = new CountDownTimer(minutesToSleepInMilliUnit, Constant.PLAYER.SLEEP.COUNTDOWN_INTERVAL) {
                            boolean blnShowYesNoDialog = true;

                            @Override
                            public void onTick(long millisUntilFinished) {
                                binding.tvSleepTime.setText(getString(R.string.sleep) + " : " + TimeUtil.getVideoTimeDisplay(millisUntilFinished));
                                DLog.d(getLogTag(), "seconds remaining: " + millisUntilFinished / 1000);
                                if (blnShowYesNoDialog && (millisUntilFinished <= Constant.PLAYER.SLEEP.SHOW_CONFIRM_POPUP)) {
                                    askToCancelSleep();
                                    blnShowYesNoDialog = false;
                                }

                            }

                            @Override
                            public void onFinish() {
                                if ((confirmCancelSleepingYesNoDialog != null) && (confirmCancelSleepingYesNoDialog.isShowing())) {
                                    confirmCancelSleepingYesNoDialog.dismiss();
                                }
                                binding.tvSleepTime.setVisibility(View.GONE);
                                ToastUtil.getInstance(activity).show(R.string.msg_cancel_now);
                                pausePlayer();
                                destroyTimer();
                                // I want to turn off the screen too, but don't want to use "Lock the Screen" feature.so just make the screen black for the Sleep feature.
//                lockScreen();
//                                positionBrightnessBeforeSleeping = Utils.getSystemBrightness(activity);
//                                positionBrightness = 0;
//                                Utils.changeBrightness(activity, positionBrightness);
                                isGoingToSleep = false;
//                                isSleeping = true;
                                showBlackOverlay();
                            }
                        };
                        countDownTimer.start();
                    }

                    @Override
                    public void onDismiss(View view, Object object) {
                        playPlayer();
                        hideButtonsOnFullScreenWithAnimation();
                    }
                });


    }

    private void askToCancelSleep() {
        confirmCancelSleepingYesNoDialog = new YesNoDialog(activity, R.string.confirm, R.string.msg_confirm_cancel_sleeping, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                cancelSleep();
            }

            @Override
            public void onNoClick(View view, Object object) {
                ToastUtil.getInstance(activity).show(getString(R.string.msg_will_sleep_on_time));
            }
        });
        confirmCancelSleepingYesNoDialog.show();

    }


    //Dalnim : Don't use this, because I can't delete this app if I have "Lock the Screen" feature. so just make the screen black for the Sleep feature.
    //-------------------------------------------------------------------------------------------
//    private void sleep() {
//        if (!canLockScreen())
//            return;
//
//        handleDoubleClickOnPlayingScreen();
//        playPlayer();
//        float sleepInFutureMinute = Constant.PLAYER.SLEEP.MILLIS_IN_FUTURE / (1000f * 59);
//        ToastUtil.getInstance(activity).show(getString(R.string.msg_will_sleep_in_future, String.valueOf(Math.round(sleepInFutureMinute))));
//        countDownTimer = new CountDownTimer(Constant.PLAYER.SLEEP.MILLIS_IN_FUTURE, Constant.PLAYER.SLEEP.COUNTDOWN_INTERVAL) {
//            boolean blnShowYesNoDialog = true;
//            @Override
//            public void onTick(long millisUntilFinished) {
//                DLog.d(getLogTag(), "seconds remaining: " + millisUntilFinished / 1000);
//                if (blnShowYesNoDialog && (millisUntilFinished <= Constant.PLAYER.SLEEP.SHOW_CONFIRM_POPUP)) {
//                    final YesNoDialog dialog = new YesNoDialog(activity, R.string.confirm, R.string.msg_confirm_cancel_sleeping, null, new OnYesNoClickListener() {
//                        @Override
//                        public void onYesClick(View view, Object object) {
//                            ToastUtil.getInstance(activity).show(R.string.msg_cancel_sleeping);
//                            destroyTimer();
//                        }
//
//                        @Override
//                        public void onNoClick(View view, Object object) {
//                            float timeLeft = millisUntilFinished / (1000f * 59);
//                            ToastUtil.getInstance(activity).show(getString(R.string.msg_will_sleep_in_future,  String.valueOf(Math.round(timeLeft))));
//                        }
//                    });
//                    dialog.show();
//                    blnShowYesNoDialog = false;
//                }
//
//            }
//
//            @Override
//            public void onFinish() {
//                ToastUtil.getInstance(activity).show(R.string.msg_cancel_now);
//                pausePlayer();
//                destroyTimer();
//                // I want to turn off the screen too, but don't know how to.
//                lockScreen();
//            }
//        };
//        countDownTimer.start();
//    }
//    public boolean canLockScreen() {
//        PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
//        if (powerManager.isInteractive()) {
//            DevicePolicyManager devicePolicyManager = (DevicePolicyManager)activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
//            try {
//                ComponentName componentName = new ComponentName(activity, AdminReceiver.class);
//                if(devicePolicyManager.isAdminActive(componentName)) {
//                    return true;
//                } else {
//                    final AlertDialog alertDialog = new AlertDialog(activity);
//                    alertDialog.show(R.string.info, R.string.mag_activate_lock_screen_first, 0, (dialog, which) -> {
//                        DLog.d(getLogTag(), "must enable device administrator");
//                        dialog.dismiss();
//                        Intent intent = new Intent(
//                                DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).putExtra(
//                                DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
//                        activity.startActivity(intent);
//                    });
//                    return false;
//                }
//            } catch (SecurityException ex) {
//                return false;
//            }
//        }
//        return false;
//    }
//
//    public void lockScreen() {
//        PowerManager powerManager = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
//        if (powerManager.isInteractive()) {
//            DevicePolicyManager devicePolicyManager = (DevicePolicyManager)activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
//            try {
//                ComponentName componentName = new ComponentName(activity, AdminReceiver.class);
//                if(devicePolicyManager.isAdminActive(componentName)) {
//                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//                    devicePolicyManager.lockNow();
//                }
//            } catch (SecurityException ex) {
//                DLog.d(getLogTag(), "must enable device administrator");
//            }
//        }
//    }
    //-------------------------------------------------------------------------------------------

    private void onKeyboardEvent(int keyCode) {
        DLog.d(getLogTag(), "onKeyboardEvent - keyCode=" + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_J:
                handleBackwardClick(1);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_L:
                handleForwardClick(1);
                break;
            case KeyEvent.KEYCODE_SPACE:
                getIvCenterPlay().performClick();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_I:
                handleSwipeToMovePrevNextDialog(true);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_K:
                handleSwipeToMovePrevNextDialog(false);
                break;
            case KeyEvent.KEYCODE_O:
                final DicModel item = getDicModel(subtitleIndex);
                if (item != null) {
                    repeatSubtitleDialogItemClick(item);
                }
                break;
            case KeyEvent.KEYCODE_SLASH:
                handleSubtitleDialogEyeClick();
                break;
            case KeyEvent.KEYCODE_BACK:
                if (stopTTSWhenPressBack()) return;
                if (binding.header.getVisibility() == View.VISIBLE) {
                    handleOnClickBack();
                } else {
                    finishActivity();
                }
                break;
            case KeyEvent.KEYCODE_N:
                if (!isHasShowSubtitle() || isVerticalMode()) {
                    return;
                }
                updateVisibilityTableView();
                break;
        }
    }

    private void finishActivity() {
        isFinishing = true;
        pausePlayer();
        destroyTimer();
        destroyShadowingParams();
        activity.playerFileModel.getVideoModel().setLastDuration(exoPlayer.getCurrentPosition());
        if (activity.playerFileModel.isWebDAV()) {
            activity.playerFileModel.getVideoModel().setDuration(exoPlayer.getDuration());
        }
//        BaseStorageUtil.setBitmapThumbnailFromVideoFile(activity, activity.playerFileModel);
        activity.callAsyncTask(this, null, TYPE_SYNC_DATA_BEFORE_EXIT, true);
    }

    private void destroyShadowingParams() {
        if (shadowingCountDownTimer != null) {
            shadowingCountDownTimer.cancel();
            shadowingCountDownTimer = null;
        }
        releaseMediaPlayer();
        releaseMediaRecorder();
    }

    private void openSelectEmbedTracksDialog() {
        //https://www.codexpedia.com/android/android-exoplayer-2-track-selection-example/
        DefaultTrackSelector trackSelector = (DefaultTrackSelector) exoPlayer.getTrackSelector();
        TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(trackSelector, onTrackSelected -> {
            activity.playerFileModel.getVideoModel().setTrackSelectionJsonStr(onTrackSelected);
        }, dismissedDialog ->
        {
            playPlayer();
            hideButtonsOnFullScreenWithAnimation();
        });
        trackSelectionDialog.show(getActivity().getSupportFragmentManager(), null);
    }

    private void askToDivideSubtitle(DicModel dicModel) {
        final YesNoDialog dialog = new YesNoDialog(activity, R.string.confirm, R.string.msg_divide_dialog, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                onDivideSubtitle(dicModel);
                playPlayer();
            }

            @Override
            public void onNoClick(View view, Object object) {
                playPlayer();
            }
        });
        dialog.show();
    }

    private void onDivideSubtitle(DicModel dicModel) {
        Gson gson = new Gson();
        DicModel newDicModel = gson.fromJson(gson.toJson(dicModel), DicModel.class);

        int midTime = (int) ((dicModel.getStartTime() + dicModel.getEndTime()) / 2);
        int midTimeOrginal = (int) (dicModel.getStartTimeOriginal() + dicModel.getEndTimeOriginal()) / 2;

        dicModel.setEndTime(midTime);
        dicModel.setEndTimeOriginal(midTimeOrginal);
        activity.getSubDatabase().updateSubtitle(dicModel);

        setNewIDForNewDicModel(newDicModel);
        newDicModel.setStartTime(midTime);
        newDicModel.setEndTimeOriginal(midTime);
        activity.getSubDatabase().addSubtitle(newDicModel);
        subtitleList.add(newDicModel);
        Collections.sort(subtitleList, (p1, p2) -> (int) (p1.getStartTime() - p2.getStartTime()));

        handleAfterModifySubtitle();
        resetMinMaxSubtitle(dicModel.getPosition() >= subtitleList.size() ? 0 : dicModel.getPosition());
        setAnalyzeAgain();
        ToastUtil.getInstance(activity).show(R.string.msg_divided);
    }

    private void openEditSubtitle(DicModel dicModel) {
        needToRestoreMinMaxSubAfterEditSubtitle = false;
        if (isABRepeatMode()) {

        } else {
            if (isCCRepeatMode()) {

            } else {
                needToRestoreMinMaxSubAfterEditSubtitle = true;
                mIsAlawysShowSubtitle = true;
                setRepeat(true);
                saveMinMaxSubBeforeEditSubtitle();
                setSelectedSUbtitleMinMaxSubBeforeEditSubtitle(dicModel);
                seekToInPlayer((long) minSubWithAllExtraTime);
            }
        }


        showPopupToInsertABRepeat(dicModel, true);
    }

    private void saveMinMaxSubBeforeEditSubtitle() {
        minSubCCRepeatModeBeforeEditView = minSub;
        maxSubCCRepeatModeBeforeEditView = maxSub;
        minSubNoExtraTimeCCRepeatModeBeforeEditView = minSubNoExtraTime;
        maxSubNoExtraTimeCCRepeatModeBeforeEditView = maxSubNoExtraTime;
        minSubWithAllExtraTimeCCRepeatModeBeforeEditView = minSubWithAllExtraTime;
        maxSubWithAllExtraTimeCCRepeatModeBeforeEditView = maxSubWithAllExtraTime;
    }

    private void setSelectedSUbtitleMinMaxSubBeforeEditSubtitle(DicModel dicModel) {
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
    }

    private void restoreMinMaxSubAfterEditSubtitle() {
        updateValue_minSub((long) minSubCCRepeatModeBeforeEditView);
        updateValue_maxSub((long) maxSubCCRepeatModeBeforeEditView);
        updateValue_minSubNoExtraTime((long) minSubNoExtraTimeCCRepeatModeBeforeEditView);
        updateValue_maxSubNoExtraTime((long) maxSubNoExtraTimeCCRepeatModeBeforeEditView);
        updateValue_minSubWithAllExtraTime((long) minSubWithAllExtraTimeCCRepeatModeBeforeEditView);
        updateValue_maxSubWithAllExtraTime((long) maxSubWithAllExtraTimeCCRepeatModeBeforeEditView);
        mIsAlawysShowSubtitle = false;
        setRepeat(false);
    }

    //Don't delete this. It calls EditSubtitleActivity to edit subtitle.
//    private void openEditSubtitle(DicModel dicModel) {
//        StringBuilder ids = new StringBuilder();
//        for (DicModel item : subtitleList) {
//            ids.append(",").append(item.getSubtitleWordlistId());
//        }
//        Intent intent = new Intent(activity, EditSubtitleActivity.class);
//        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, activity.playerFileModel);
//        if (dicModel == null) {
//            intent.putExtra(Constant.PLAYER.INTENT.KEY_INDEX, -1);
//        } else {
//            int subtileIndexFromDicModel = dicModel.getPosition();
//            if ((subtileIndexFromDicModel == 0) && (subtitleIndex >= 0)) {
//                intent.putExtra(Constant.PLAYER.INTENT.KEY_INDEX, subtitleIndex);
//            } else {
//                intent.putExtra(Constant.PLAYER.INTENT.KEY_INDEX, subtileIndexFromDicModel);
//            }
//        }
//
//        if (ids.length() > 0) {
//            intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, ids.substring(1));
//        }
//        startActivity(intent);
//    }

    private void onCopyThisPhrase(Object data) {
        final VocaStudyChat item = (VocaStudyChat) data;
        Utils.copyToClipboard(activity, item.getVocaDisplay(), R.string.copied);
    }

    //Don't delete this. Will use later.. (This is for VocaStudyCnat)
    private void showCopyLanguagesDialog(Object data) {
        final CopySubtitleByLanguageDialog dialog = new CopySubtitleByLanguageDialog(activity, data, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                final VocaStudyChat voca = (VocaStudyChat) data;
                String content = Constant.BASE_BLANK;
                switch (view.getId()) {
                    case R.id.tv_study_language:
                        content = voca.getVocaDisplay();
                        break;
                    case R.id.tv_mother_tongue:
                        content = voca.getMeaning();
                        break;
                    case R.id.tv_both_study_mother_language:
                        content = voca.getVocaDisplay() + "\n" + voca.getMeaning();
                        break;
                }

                if (!Utils.isEmpty(content)) {
                    Utils.copyToClipboard(activity, content, R.string.copied);
                }
            }

            @Override
            public void onDismiss(View view, Object object) {

            }
        });
        dialog.show();
    }

    private void openGptCustomTab(DicModel dicModel) {
        CopyTextUtil.openCopyConvPractice(activity, (countToCopy) -> {
            List<DicModel> dicModels = new ArrayList<>();
            if (countToCopy > 0 && dicModel != null) {
                dicModels.add(dicModel);
                if (countToCopy > 1) {
                    DicModel dicModelPrev1 = getDicModel(dicModel.getVIIndex() - 2);
                    if (dicModelPrev1 != null) {
                        dicModels.add(0, dicModelPrev1);
                        if (countToCopy > 2) {
                            DicModel dicModelPrev2 = getDicModel(dicModel.getVIIndex() - 3);
                            if (dicModelPrev2 != null) {
                                dicModels.add(0, dicModelPrev2);
                            }
                        }
                    }
                }
                if (isHasSubtitleBothLang) {
                    Voca.showPopupTogetSubtitleVocaMeaningToCopy(activity, dicModels, value -> {
                        showPopupToAddPompt(value);
                    });
                } else {
                    showPopupToAddPompt(Voca.getSubtitleVocaOrMeaningToCopy(activity, dicModels));
                }
            } else {
                openGptCustomTabFromSentenceMain("");
            }

        });
    }

    private void showPopupToAddPompt(String value) {
        String[] displayOptions = activity.getResources().getStringArray(R.array.array_choice_ask_what_to_do_with_copied_text);
        // Replace the [[STUDY_LANG_NAME]] placeholder with "English" in the relevant menu items
        for (int i = 0; i < displayOptions.length; i++) {
            displayOptions[i] = StringUtils.replaceLanguageMarker(displayOptions[i], activity);
        }

        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(activity);
        int checkedItem = SharedPreferencesDB.getInstance(activity).getChoiceAskWhatToDoWithCopiedText();
        singleChoiceDialog.showWrapContentHeight(
                R.string.dialog_title_ask_what_to_do_with_copied_text,
                displayOptions,
                checkedItem,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        SharedPreferencesDB.getInstance(activity).setChoiceAskWhatToDoWithCopiedText(which);
                        if (which == 0) {
                            openGptCustomTabFromSentenceMain(value);
                        } else {
                            String prefix = activity.getString(R.string.watching_this_video, activity.playerFileModel.getVideoModel().getDisplayTitle());
                            String suffix = displayOptions[which];
                            openGptCustomTabFromSentenceMain(prefix + "\n\n" + value + "\n\n-----\n" + suffix);
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void openGptCustomTabFromSentenceMain(String result) {
        if (!Utils.isEmpty(result)) {
            CopyTextUtil.copyToClipboard(activity, result.toString());
        }
        ChatGptWebUtil.openUrlInCustomTab(activity, null);
    }
    private void onCopyThisSubtitle(DicModel dicModel) {
        if ((!Utils.isEmpty(dicModel.getVocaDisplay())) && (!Utils.isEmpty(dicModel.getMeaning()))) {
            Voca.openCopySubtitleDialog(activity, dicModel);
        } else {
            playPlayer();
            String copiedText = org.apache.commons.lang3.StringUtils.trim(dicModel.getVocaDisplay());
            if (Utils.isEmpty(copiedText)) {
                copiedText = org.apache.commons.lang3.StringUtils.trim(dicModel.getMeaning());
            }
            Utils.copyToClipboard(activity, copiedText, Voca.getMessageInToastToShow(activity, copiedText));
        }
    }

    private void askToDeleteSubtitle(DicModel dicModel) {
        DialogUtil.askToDeleteDialogInSubtitle(activity, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                removeSubtitleInSubtitleList(dicModel);
                activity.getSubDatabase().deleteSubtitle(dicModel.getId());
                playPlayer();
                setVisibleLlRubyButtomSubtitle();
                ToastUtil.getInstance(activity).show(R.string.deleted);
            }

            @Override
            public void onNoClick(View view, Object object) {
                playPlayer();
            }
        });
    }

    private void askToMergeSubtitle(DicModel dicModel, boolean isMergeWithPreviousSubtitle) {

        if (sharedPreferences.isToShowMergeSubtitlePopup()) {
            sharedPreferences.setToShowMergeSubtitlePopup(false);
            int message = isMergeWithPreviousSubtitle ? R.string.msg_merge_previous_dialog : R.string.msg_merge_next_dialog;
            final YesNoDialog dialog = new YesNoDialog(activity, R.string.confirm, message, null, new OnYesNoClickListener() {
                @Override
                public void onYesClick(View view, Object object) {
                    callMergeSubtitle(dicModel, isMergeWithPreviousSubtitle);
                }

                @Override
                public void onNoClick(View view, Object object) {
                    playPlayer();
                }
            });
            dialog.show();
        } else {
            callMergeSubtitle(dicModel, isMergeWithPreviousSubtitle);
        }
    }

    private void callMergeSubtitle(DicModel dicModel, boolean isMergeWithPreviousSubtitle) {
        onMergeSubtitle(dicModel, isMergeWithPreviousSubtitle);
        playPlayer();
    }

    private void onMergeSubtitle(DicModel dicModel, boolean isMergeWithPreviousSubtitle) {
        int selectedSubtitleIndex = dicModel.getIndex() - 1; //getIndex() starts from 1 and need to minus 1 to get item from array(starts from 0)
//        int nextOrPrevSubtitleIndex = isMergeWithPreviousSubtitle ? subtitleIndex - 1 : subtitleIndex + 1;
        DicModel dicModelNextOrPrev = null;
        if (isMergeWithPreviousSubtitle) {
            dicModelNextOrPrev = getDicModel(selectedSubtitleIndex - 1);
        } else {
            dicModelNextOrPrev = getDicModel(selectedSubtitleIndex + 1);
        }
        if (dicModelNextOrPrev == null) {
            ToastUtil.getInstance(activity).show("Nothing to merge");
            return;
        }


        //Dalnim : Get the smallest minus value for VocaIDToserver(not just -1), but don't change VOCA ID, it's used in local SQLIte only.
        //And need to send server the dialog ang get new voca sender id for this. Otherwise the KNOW is not saved in server until I change the KNOW value.
//        dicModel.setVocaIdServer(activity.getSubDatabase().getMinColumnValueInTbl(Constant.PLAYER.SQL.COLUMN.VOCA_ID_TO_SEND_SERVER, Constant.PLAYER.SQL.TABLE.SUBTITLE) - 1);
        dicModel.setVocaId(activity.getSubDatabase().getMinColumnValueInTbl(Constant.PLAYER.SQL.COLUMN.VOCA_ID, Constant.PLAYER.SQL.TABLE.SUBTITLE) - 1);
        String voca, meaning, memo;
        String newLine = "\n";
        if (isMergeWithPreviousSubtitle) {
            voca = dicModel.getVocaDisplay().trim().equals("") ? dicModelNextOrPrev.getVocaDisplay() : dicModelNextOrPrev.getVocaDisplay() + newLine + dicModel.getVocaDisplay();
            meaning = dicModel.getMeaning().trim().equals("") ? dicModelNextOrPrev.getMeaning() : dicModelNextOrPrev.getMeaning() + newLine + dicModel.getMeaning();
            memo = dicModel.getMemo().trim().equals("") ? dicModelNextOrPrev.getMemo() : dicModelNextOrPrev.getMemo() + newLine + dicModel.getMemo();

            dicModel.setStartTime(dicModelNextOrPrev.getStartTime());
            dicModel.setStartTimeOriginal(dicModelNextOrPrev.getStartTimeOriginal());
        } else {
            voca = dicModelNextOrPrev.getVocaDisplay().trim().equals("") ? dicModel.getVocaDisplay() : dicModel.getVocaDisplay() + newLine + dicModelNextOrPrev.getVocaDisplay();
            meaning = dicModelNextOrPrev.getMeaning().trim().equals("") ? dicModel.getMeaning() : dicModel.getMeaning() + newLine + dicModelNextOrPrev.getMeaning();
            memo = dicModelNextOrPrev.getMemo().trim().equals("") ? dicModel.getMemo() : dicModel.getMemo() + newLine + dicModelNextOrPrev.getMemo();

            dicModel.setEndTime(dicModelNextOrPrev.getEndTime());
            dicModel.setEndTimeOriginal(dicModelNextOrPrev.getEndTimeOriginal());
        }
        dicModel.setVocaDisplay(voca.trim());
        dicModel.setMeaning(meaning.trim());
        dicModel.setMemo(memo.trim());
        dicModel.setVocaDisplayRuby(voca.trim());
        dicModel.setVocaKnow(Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);
        dicModel.setVocaKnowPronounce(Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED);
        activity.getSubDatabase().updateSubtitle(dicModel);
        activity.getSubDatabase().deleteSubtitle(dicModelNextOrPrev.getId());

//        subtitleList.remove(nextOrPrevSubtitleIndex);
        subtitleListTotal.remove(dicModelNextOrPrev);
        subtitleList.remove(dicModelNextOrPrev);
//        subtitleIndex = Voca.getDefaultIndexIfOutOfIndex(subtitleList, subtitleIndex);
        handleAfterModifySubtitle();
        resetMinMaxSubtitle(dicModel.getPosition() >= subtitleList.size() ? 0 : dicModel.getPosition());
        setAnalyzeAgain();
        ToastUtil.getInstance(activity).show(R.string.msg_merged);
    }

    //Don't delete this. Will use later.. (This is for DicModel)
    private void showCopyLanguagesSubtitleDialog(Object data) {
        final CopySubtitleByLanguageDialog dialog = new CopySubtitleByLanguageDialog(activity, data, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                final DicModel item = (DicModel) object;
                final String studyLanguage = activity.studyLanguage.getFormatApi();
                final String motherTongue = activity.motherTongueLanguage.getFormatApi();
                String content = Constant.BASE_BLANK;
                switch (view.getId()) {
                    case R.id.tv_study_language:
                        content = studyLanguage;
                        break;
                    case R.id.tv_mother_tongue:
                        content = motherTongue;
                        break;
                    case R.id.tv_both_study_mother_language:
                        content = studyLanguage + "\n" + motherTongue;
                        break;
                    case R.id.tv_both_mother_study_language:
                        content = motherTongue + "\n" + studyLanguage;
                        break;
                }

                if (!Utils.isEmpty(content)) {
                    Utils.copyToClipboard(activity, content, R.string.copied);
                }
            }

            @Override
            public void onDismiss(View view, Object object) {
                if (view == null) {
                    playPlayer();
                }
            }
        });
        dialog.show();
    }

    private LinearLayoutManager getCurrentLinearLayoutManager() {
        if (isVerticalMode()) {
            layoutManager = new VariableScrollSpeedTopLinearLayoutManager(activity);
        } else {
            layoutManager = new VariableScrollSpeedLinearLayoutManager(activity);
        }
        return layoutManager;
    }

    private List<DicModel> reloadSubtitleListFromDB() {
        String ids = subtitleList.stream().map(e -> String.valueOf(e.getId())).collect(Collectors.joining(","));
        subtitleList = activity.getSubDatabase().getSubtitleDialogListByIds(ids);

        for (DicModel dicModel : subtitleList) {
            if (mapSubtitleTotal.containsKey(dicModel.getId())) {
                mapSubtitleTotal.put(dicModel.getId(), dicModel);
            }
        }

        rubyTextModels = getRubyTextModels();
        MergeUtil.generateMeaning(subtitleListTotal, rubyTextModels);
        MergeUtil.generateMeaning(subtitleList, rubyTextModels);

//        if (!isFullscreenMode()) {
//            subtitleDialogAdapter.setData(subtitleList);
//            notifyDataSetChanged_subtitleDialogAdapter();
//        }
        return subtitleList;
    }

    private boolean updateDataFromEditSubtitle(String data) {
        if (data.equals(BuildConfig.FLAVOR)) {
            return false;
        }
        final List<DicModel> list = activity.getSubDatabase().getSubtitleDialogListByIds(data);
        if (list == null || list.isEmpty())
            return false;
        Map<Integer, DicModel> mapDicModel = list.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
        Map<Integer, DicModel> mapSubtitleList = subtitleList.stream().collect(Collectors.toMap(e -> e.getId(), e -> e));
        for (DicModel item : mapDicModel.values()) {
            if (mapSubtitleTotal.containsKey(item.getId())) {
                updateEditedSubtitle(mapSubtitleTotal, item);
            }
            if (mapSubtitleList.containsKey(item.getId())) {
                updateEditedSubtitle(mapSubtitleList, item);
            }
        }
        rubyTextModels = getRubyTextModels();
        MergeUtil.generateMeaning(subtitleListTotal, rubyTextModels);
        MergeUtil.generateMeaning(subtitleList, rubyTextModels);
        return true;
    }

    private boolean updateEditedSubtitle(Map<Integer, DicModel> mapSubtitleList, DicModel item) {
        DicModel x = mapSubtitleList.get(item.getId());
        if (x.getId() == item.getId()) {
            x.setStartTime(item.getStartTime());
            x.setEndTime(item.getEndTime());
            x.setVocaDisplay(item.getVocaDisplay());
            x.setVocaDisplayRuby(item.getVocaDisplayRuby());
            x.setMeaning(item.getMeaning());
            return true;
        }
        return false;
    }

    private void onDelaySubtitles() {
        mIsOnDelaySubtitle = true;
        updatemValue_IsAlawysShowSubtitle(true);
        setVisibleRepeatRange(View.GONE);
        setVisibleLLListenComprehension(View.GONE);
        setVisiblellPlaySub(View.GONE);
        setVisibleLLPlay(View.GONE);
        setVisibilitySubtitleTableViewAndIcon(View.GONE);
        binding.llDelaySubtitle.root.setVisibility(View.VISIBLE);
//        isRepeat = true;
//        setVisibleMeadiaTitle(View.GONE);
//        setVisibleLlRubyButtomSubtitle(View.VISIBLE);

        setShowSubtitle();
        updateDelaySubtitle();
        showSubtitle(exoPlayer.getCurrentPosition());
        getScreenSize();
        setVisibleShowButtonsOnFullScreen(View.GONE); //In previous code's inside make this visible, so make it Gone here again.
    }

    private void onDelaySubtitleChangeValue(int id) {
        int value = activity.playerFileModel.getVideoModel().getDelaySubtitles();
        switch (id) {
            case R.id.ic_delay_subtitle_minus:
                value -= Constant.PLAYER.DELAY_SUBTITLE.ADJUST;
                if (value < (mMinMaxDelaySubtitle * -1)) {
                    value = Constant.PLAYER.DELAY_SUBTITLE.RESET;
                }
                break;
            case R.id.ic_delay_subtitle_reset:
                value = Constant.PLAYER.DELAY_SUBTITLE.RESET;
                break;
            default:
                value += Constant.PLAYER.DELAY_SUBTITLE.ADJUST;
                if (value > mMinMaxDelaySubtitle) {
                    value = Constant.PLAYER.DELAY_SUBTITLE.RESET;
                }
                break;
        }
        activity.playerFileModel.getVideoModel().setDelaySubtitles(value);
        updateDelaySubtitle();
    }

    private void updateDelaySubtitle() {
        final int value = activity.playerFileModel.getVideoModel().getDelaySubtitles();
        DLog.d(getLogTag(), "updateDelaySubtitle - value=" + value);
        String operation = value < 0 ? "-" : "+";
        String finalOperation = operation;

        DicModel dicModel = getDicModel(subtitleIndex);

        activity.runOnUiThread(() -> {
            //Dalnim Added : Not to hide the KNOWN dialogs during Delay subtitles
            getMinMaxTime(dicModel);
            binding.llDelaySubtitle.tvDelaySubtitle.setText(getString(R.string.delay_audio_format, finalOperation, TimeUtil.displayMinuteAndMilliseconds(Math.abs(value))));
            seekToInPlayer((long) minSub);
            binding.llDelaySubtitle.sbDelaySubtitle.setProgress(value);
//            showSubtitle((long) minSub, subtitleIndex);
            showSubtitleAlways((long) minSub);
        });
    }

    private void onDelaySubtitleClose() {
        mIsOnDelaySubtitle = false;
        activity.updateVideoModel(activity.playerFileModel.getVideoModel()); // To save delay value
        updatemValue_IsAlawysShowSubtitle(false);
        DicModel dicModel = getDicModel(subtitleIndex);
        activity.runOnUiThread(() -> {
            //Dalnim Added : Back to original after Delay Subtitle
            if (dicModel != null) {
                updateTvRubyBottomSubtitleShowAsterisk(Voca.getShowAsteriskWhenHideKnowDialogIsOn(dicModel, mIsHideKnownDialogDuringPlaying, isCCRepeatMode(), showAsterisk, isBeingPlaying));
            }
            binding.llDelaySubtitle.root.setVisibility(View.GONE);
            hideButtonsOnFullScreenWithAnimation();
        });
        playPlayer();
    }

    private void resetMaxValueOnDelaySubtitle() {

        singleChoiceDialog.show(
                R.string.choose_min_max_delay_subtitle_value,
                Constant.PLAYER.DELAY_SUBTITLE.MIN_MAX_DELAY_RANGE,
                mMinMaxDelaySubtitleIndex,
                R.string.choose,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        int which = (int) object;
                        if (which != mMinMaxDelaySubtitleIndex) {
                            mMinMaxDelaySubtitleIndex = which;
                            String chooseData = Constant.PLAYER.DELAY_SUBTITLE.MIN_MAX_DELAY_RANGE[which];
                            if (NumberUtils.isCreatable(chooseData)) {
                                mMinMaxDelaySubtitle = Integer.parseInt(chooseData) * 1000;
                                sharedPreferences.setDelaySubtitleMinMaxValue(mMinMaxDelaySubtitle);
                                refreshDelaySubtitleUI(mMinMaxDelaySubtitle);
                            }
                        }
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private boolean isDelaySubtitleMode() {
        return binding.llDelaySubtitle.root.getVisibility() == View.VISIBLE;
    }

    private void openSubtitleGroupToSelectActivity() {
        launcherSubtitleGroup.launch(SubtitleGroupActivity.createIntent(activity, activity.playerFileModel, SubtitleGroupActivity.Type.SELECT));
    }

    private void openSubtitleGroupToDeleteActivity() {
        launcherSubtitleGroup.launch(SubtitleGroupActivity.createIntent(activity, activity.playerFileModel, SubtitleGroupActivity.Type.DELETE));
    }
    private final ActivityResultLauncher<Intent> launcherSubtitleGroup = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                isReloadAllSubtitleTotalAgain = true;
            } else {
                isReloadAllSubtitleTotalAgain = false;
            }
        });

    private void openDictationScreen(int subtitleIndex) {
        updateValue_SubtitleIndex(CollectionUtil.getIndexInsideList(subtitleIndex, subtitleList));
        Intent intent = new Intent(activity, DictationModeActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, activity.playerFileModel);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_INDEX, subtitleIndex);
        startActivity(intent);
    }

    //Dalnim added
    protected void handlePlayClick(boolean isPlay, boolean isShowViewPlayCenter) {
        if (FileUtil.isMusicApp() && isPlay) {
            if(!isInitViewFinished) {
                startInitViewDelay();
                return;
            }
            if(isPlayingEndMusicSound) {
                super.startOverWhenItReachEndOfMediaMain();
                return;
            }
        }
        super.handlePlayClick(isPlay, isShowViewPlayCenter);

        if (isVisibileLLPlay()) {
            showMediaTitleWithMarquee(getTvTitle(), false, isPlay);
            if (FileUtil.isMusicApp()) {
                showMediaTitleWithMarquee(binding.llPlay.tvTitle, false, isPlay);
            }
        }
        //When I puase a video at empty dialog, it shows previous dialog because sometimes mIsOnEmptyDialog is false.
        //It should be true on empty dialog. So I refresh mIsOnEmptyDialog value again.
        getSubtitleIndexFromCurrentTime();
        if (mIsOnEmptyDialog && isFullscreenMode())
            return;

        //if it's not full screen mode, I don't have to refresh refresh subtitle to show the KNOWN dialog when it's not playing.
        if (sharedPreferences.getHideKnownDialogsDuringPlaying() == false)
            return;

        updateHideKnownDialogValueAndRefreshSubtitle();
    }

    //Dalnim added
    private void updateHideKnownDialogValueAndRefreshSubtitle() {
        updatemValue_IsHideKnownDialogDuringPlaying(sharedPreferences.getHideKnownDialogsDuringPlaying());
        if (isBeingPlaying) {
            if (isAnyRepeatMode()) {
                if (isDisplayListenComprehension2() && comprehensionRepeatedCount == 0) {
                    //Try to "Hide subtitle" when first time in DisplayListenComprehension2
                    hideSubtitleAtFirstCCRepeatOrListenComp2();
                } else if (!isFullscreenMode() && isCCRepeatMode()) {
                    //Try to "Hide subtitle" when first time in CC Repeat mode when it's not full screen mode.
                    if (ccRepeatingCount == 0)
                        hideSubtitleAtFirstCCRepeatOrListenComp2();
                    else
                        restoreShowAsteriskFromBefore();
                } else {
                    // I added this code for isDisplayListenComprehension1, when AraPlayer hides subtitle at first, then I pause playing to see the subtitle, but it still shows when I resume playing.
                    // 듣기연습1에서 정지해서 자막을 보이게 하면 다시 플레이 해도 계속 자막이 보여서, 안보이게 하기 위해서 이 줄을 추가했다.
                    // (이줄이 AB반복때도 적용되면, AB반복시 자막이 안보인다.)
                    if (isDisplayListenComprehension1()) {
                        restoreShowAsteriskFromBefore();
                    }
                }
            } else {
                if (!isABRepeatMode_A_Button_Clicked()) {
                    setShowAsteriskBeforeAtNormalPlaying(); // With this, If I set to show difficult words and click AB repeat then I can see the previous(Show Subtitle) value.
                }
            }
        } else {
            if (!isAnyRepeatMode()) {
                restoreShowAsteriskBeforeAtNormalPlaying();
            }
            //Display subtitle always when it's paused.
            updatemValue_IsHideKnownDialogDuringPlaying(false);
            setShowAsteriskToBefore();
            setShowSubtitle();
        }

        if (mIsAlawysShowSubtitle) {
            //Dalnim : If I don't have this, it hides the dialog once and shows the KNOWN dialog, so it's blinking when Araplayer starts playing a dialog in the Delay Subtitle mode.
            refreshDisplayingSubtitle(false, showAsterisk);
        } else {
            refreshDisplayingSubtitle(mIsHideKnownDialogDuringPlaying, showAsterisk);
        }
    }

    //Dalnim added
    private void refreshDisplayingSubtitle(boolean isHideKnownDialogDuringPlaying, int showAsterisk) {
        if (!isHasSubtitle())
            return;

        activity.runOnUiThread(() -> {
            if (isFullscreenMode()) {
                refreshDisplayingSubtitleOnFullscreenMode(isHideKnownDialogDuringPlaying);
            } else {
                refreshDisplayingSubtitleInSubtitleTableView(isHideKnownDialogDuringPlaying, showAsterisk);
            }
        });

    }

    //Dalnim added
    private void refreshDisplayingSubtitleOnFullscreenMode(boolean isHideKnownDialogDuringPlaying) {
        DicModel dicModel = getDicModel(subtitleIndex);
        if (isSubtitleDialogEyeClose()) {
//            setVisibleLlRubyButtomSubtitle(View.INVISIBLE);
            setVisibleTvRubyBottomSubtitle(View.INVISIBLE);
        } else if (dicModel != null) {
            updateTvRubyBottomSubtitleShowAsterisk(Voca.getShowAsteriskWhenHideKnowDialogIsOn(dicModel, isHideKnownDialogDuringPlaying, isCCRepeatMode(), showAsterisk, isBeingPlaying));
        }
    }

    //Dalnim added
    private void refreshDisplayingSubtitleInSubtitleTableView(boolean isHideKnownDialogDuringPlaying, int showAsterisk) {
        if (subtitleDialogAdapter == null)
            return;
        subtitleDialogAdapter.setHideKnownDialogDuringPlaying(isHideKnownDialogDuringPlaying);
        refreshShowAsteriskInSubtitleOnSubtitleTableViewMode();
    }

    private LongTouchIntervalListener onLongTouchIntervalListener = new LongTouchIntervalListener() {
        @Override
        public void onTouchInterval(View v) {
            onDelaySubtitleChangeValue(v.getId());
        }
    };

    @Override
    public void onPositionChanged(@NotNull View view) {
    }

    private void openCCRepeatTuningScreenDialog() {
        pausePlayer();
        final PlayerCCRepeatTuningScreenDialog dialog = new PlayerCCRepeatTuningScreenDialog(activity, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                onSyncDialogueCCRepeatTuning((Integer) object);
            }

            @Override
            public void onDismiss(View view, Object object) {
                playPlayer();
            }
        });
        dialog.show();
    }

    private void onSyncDialogueCCRepeatTuning(int viewId) {
        DicModel prevDicModel = null;
        if (subtitleIndex > 0) {
            prevDicModel = getDicModel(subtitleIndex - 1);
        }
        DicModel currentDicModel = getDicModel(subtitleIndex);
        DicModel nextDicModel = null;
        if (subtitleIndex < subtitleList.size() - 1) {
            nextDicModel = getDicModel(subtitleIndex + 1);
        }
        switch (viewId) {
            case R.id.tv_sync_previous_dialogue_end_time_to_current_dialog_start_time:
                if (prevDicModel != null) {
                    prevDicModel.setEndTime(currentDicModel.getStartTime());
                    activity.getSubDatabase().updateTimeSubtitle(prevDicModel);
                }
                break;
            case R.id.tv_sync_current_dialogue_start_time_to_previous_dialog_end_time:
                if (prevDicModel != null) {
                    currentDicModel.setStartTime(prevDicModel.getEndTime());
                    activity.getSubDatabase().updateTimeSubtitle(currentDicModel);
                }
                break;
            case R.id.tv_sync_next_dialogue_start_time_to_current_dialog_end_time:
                if (nextDicModel != null) {
                    nextDicModel.setStartTime(currentDicModel.getEndTime());
                    activity.getSubDatabase().updateTimeSubtitle(nextDicModel);
                }
                break;
            case R.id.tv_sync_current_dialogue_end_time_to_next_dialog_start_time:
                if (nextDicModel != null) {
                    currentDicModel.setEndTime(nextDicModel.getStartTime());
                    activity.getSubDatabase().updateTimeSubtitle(currentDicModel);
                }
                break;
        }
        getMinMaxTime(currentDicModel);
        updateRangeSeek();
        onUpdateRepeatTuningDialogue();
    }

    //Short subtitle time range bar for Repetition on the right/left side of the playing screen
    private void initSeekBarRepeatStartEnd() {
        RangeSeekBar sbRepeatStart = binding.llRepeat.sbRepeatStart;
        RangeSeekBar sbRepeatEnd = binding.llRepeat.sbRepeatEnd;
        sbRepeatStart.setRange(leftProgress - 1, leftProgress + 1);
        sbRepeatStart.setProgress(leftProgress);
        sbRepeatEnd.setRange(rightProgress - 1, rightProgress + 1);
        sbRepeatEnd.setProgress(rightProgress);
        sbRepeatStart.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                handleMoveVideoTimeLeft(leftValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                isTrackingLeft = true;
                pausePlayer();
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                onStopTrackingTouchAtRepeatSeekbar(isLeft, view.getLeftSeekBar().getProgress());
                sbRepeatStart.setProgress(leftProgress);
            }
        });
        sbRepeatEnd.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                //There is only one thumb so use left value.
                handleMoveVideoTimeRight(leftValue);
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                isTrackingLeft = false;
                pausePlayer();
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //There is only one thumb so use left value for EndTime
                onStopTrackingTouchAtRepeatSeekbar(false, view.getLeftSeekBar().getProgress());
                sbRepeatEnd.setProgress(rightProgress);
            }
        });
    }

    private OnDoubleClickListener oOnDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {
            switch (view.getId()) {
                case R.id.tvShowButtonsKnowValueRight:
                    //Sometimes I try to double click this button but I click subTitleDialog then click  tv_show_buttons_know_value_right.
                    //So I make sure tv_show_buttons_know_value_right is not displaying when subTitleDialog is showing.
//                    if ((subTitleDialog == null) || (subTitleDialog.isShowing())) {
//                        ToastUtil.getInstance(activity).show("subTitleDialog is null or showing");
//                    } else {
                    if (!isAnyRepeatMode())
                        hideButtonsOnFullScreenWithAnimation();

                    showSubtitleKnowDialog(getDicModel(subtitleIndex));
//                    }
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object data) {
            switch (view.getId()) {
                case R.id.tvShowButtonsKnowValueRight:
                    DicModel dicModel = getDicModel(subtitleIndex);
                    updateKnowWhenDoubleClick(dicModel);
                    //Without this code, when it's empty dialog, double click tv_show_buttons_know_value_right changes the previous dialog's KNOW value in DB
                    //But didn't update the KNOW value in the UI. So I update the KNOW value in the UI too.
                    VocaKnow.updateIconVocaKnow(activity, binding.tvShowButtonsKnowValueRight, dicModel.getVIVocaKnow());
                    break;
            }
        }
    };

    @Override
    protected void updateVocaKnow(IVocaBasicItem iVocaBasicItem) {
        super.updateVocaKnow(iVocaBasicItem);
        applyKnowValueInSubtitle(iVocaBasicItem);
    }

    private void hideOrShowDialogsRuleAtABRepeating() {
        if (isABRepeatMode()) {
            if (abRepeatingCount == 0) {
                setHideSubtitle();
            } else if ((abRepeatingCount % 2) == 1) {
                setShowSubtitle();
            } else {
                setShowDifficutlWordsOnlyInSubtitle();
            }
        }
    }
    //dalnim :
    private void hideOrShowDialogsRuleAtRepeating(DicModel dicModel) {
        if (dicModel == null)
            return;

        int repeatCountOfDialog = RepeatUtil.getRepeatValue(dicModel);

        if (isCCRepeatMode()) {
            if (repeatList.size() > 1) {
                if ((ccRepeatingCount % repeatList.size()) == 0) {
                    setShowSubtitle();
                } else {
                    setShowDifficutlWordsOnlyInSubtitle();
                }
            } else {
                if (VocaKnow.isKnown(dicModel)) {
                    if ((ccRepeatingCount % 2) == 1) {
                        setShowDifficutlWordsOnlyInSubtitle();
                    } else {
                        setHideSubtitle();
                    }
                } else {
                    if ((ccRepeatingCount % 2) == 1) {
                        setShowSubtitle();
                    } else {
                        setShowDifficutlWordsOnlyInSubtitle();
                    }
                }
            }
        } else if (isDisplayListenComprehension2()) {
            if (repeatCountOfDialog == 1) {
                if (VocaKnow.isKnown(dicModel)) {
                    DLog.d("Difficult", "repeatCountOfDialog == 1 VocaKnow.isKnown(dicModel) true");
                    if (isBeingPlaying) {
                        setShowDifficutlWordsOnlyInSubtitle();
                    } else {
                        setShowSubtitle();
                    }

//
//                    //Try to show KNOWN subtitle when a video is paused during Listen Comprehension mode 2
//                    // 듣기2에서 다음자막이 아는 자막이면, 현재 자막이 끝나고 나서 다음자막이 어려운 자막보이기로 미리 계속 보인다.  그래서 "&& !isListenComprehensionMode()"를 추가함.
//                    if (isBeingPlaying && !isListenComprehensionMode()) {
//                        DLog.d("Difficult", "repeatCountOfDialog == 1 VocaKnow.isKnown(dicModel) true isBeingPlaying true");
//                        setShowDifficutlWordsOnlyInSubtitle();
//                    } else {
//                        DLog.d("Difficult", "repeatCountOfDialog == 1 VocaKnow.isKnown(dicModel) true isBeingPlaying false");
////                        if (isBeingPlaying) {
////                            setShowDifficutlWordsOnlyInSubtitle();
////                        } else {
////                            setShowSubtitle();
////                        }
//                    }
                } else {
                    DLog.d("Difficult", "repeatCountOfDialog == 1 VocaKnow.isKnown(dicModel) false");
                    if (FileUtil.isMusicApp())
                        setShowSubtitle();
                    else
                        setShowDifficutlWordsOnlyInSubtitle();
                }
            } else {
                if (comprehensionRepeatedCount == 0) {
                    DLog.d("Difficult", "repeatCountOfDialog != 1 listComprehensionRepeatedCount == 0 true");
                    setHideSubtitle();
                } else if ((comprehensionRepeatedCount % 2) == 1) {
                    DLog.d("Difficult", "repeatCountOfDialog != 1 (listComprehensionRepeatedCount % 2) == 1");
                    setShowSubtitle();
                } else {
                    DLog.d("Difficult", "repeatCountOfDialog != 1 listComprehensionRepeatedCount == 0 false");
                    setShowDifficutlWordsOnlyInSubtitle();
                }
            }
        } else {
            if (repeatCountOfDialog == 1) {
                if (showAsterisk != Constant.SHOW_ASTERISK.SHOW_SENTENCE) {
                    ToastUtil.getInstance(activity).show("Debugging hideOrShowDialogsRuleAtRepeating else");
                    setShowSubtitle();
                }
            }
        }

        DLog.d(getLogTag(), "hideOrShowDialogsRuleAtCCRepeating - showAsterisk=" + showAsterisk);
    }

    private void setShowAsteriskBeforeAtNormalPlaying() {
        updateValue_showAsterisk(showAsteriskBeforeAtNormalPlaying);
    }

    private void restoreShowAsteriskBeforeAtNormalPlaying() {
        //When enters no repeat mode (No Listen comprehension, no CC Repeat, No AB Repeat)
        showAsteriskBeforeAtNormalPlaying = showAsterisk;
        ;
    }

    private void setShowAsteriskToBefore() {
        showAsteriskBefore = showAsterisk;
//        ToastUtil.getInstance(activity).show("showAsteriskBefore = showAsterisk " + showAsterisk);
    }

    private void restoreShowAsteriskFromBefore() {
        //Just restore from previous mode(CC Repeat -> Listen compreshension, CC Repeat -> Nomral Playing mode)
        updateValue_showAsterisk(showAsteriskBefore);
        //ABRepeat를 빨리 실행하고 해제하면 빈자막인데 이전 자막이 잠깐 보였다가 사라져서 if문을 추가함.
        if (!mIsOnEmptyDialog)
            refreshShowAsteriskInSubtitle();
    }

    private void setShowSubtitle() {
        updateValue_showAsterisk(Constant.SHOW_ASTERISK.SHOW_SENTENCE);
        refreshShowAsteriskInSubtitle();
    }

    private void setShowDifficutlWordsOnlyInSubtitle() {
        if (mIsAlawysShowSubtitle) {
            setShowAsteriskToBefore();
        } else {
            updateValue_showAsterisk(Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY);
//            showAsterisk = Constant.SHOW_ASTERISK_ON;
            refreshShowAsteriskInSubtitle();
//            ToastUtil.getInstance(activity).show("SHOW_ASTERISK_ON : difficult words only");
        }

    }

    private void setHideSubtitle() {
        if (mIsAlawysShowSubtitle) {
            setShowAsteriskToBefore();
        } else {
            if (showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE)
                return;
            updateValue_showAsterisk(Constant.SHOW_ASTERISK.HIDE_SENTENCE);
            refreshShowAsteriskInSubtitle();
        }

    }

    private void refreshShowAsteriskInSubtitle() {
        updateTvRubyBottomSubtitleShowAsterisk(showAsterisk);
        refreshShowAsteriskInSubtitleOnSubtitleTableViewMode();
    }

    private void refreshShowAsteriskInSubtitleOnSubtitleTableViewMode() {
        if (isFullscreenMode() || subtitleDialogAdapter == null)
            return;
        subtitleDialogAdapter.setShowAsterisk(showAsterisk);
        notifyDataSetChanged_subtitleDialogAdapter();
    }

    private void refreshShowAsteriskInCCRepeatMode() {
        if (isCCRepeatMode() && !isDelaySubtitleMode()) {
            if (!isListenComprehensionMode()) {
                increaseCCRepeatCount();
            } else if (isListenComprehensionMode() && isCCRepeatMode()) {
                increaseComprehensionCountForCCRepeat();
            }
            final DicModel dicModel = getDicModel(subtitleIndex);
            hideOrShowDialogsRuleAtRepeating(dicModel);
        }
    }

    private void updatemValue_IsHideKnownDialogDuringPlaying(boolean value) {
        mIsHideKnownDialogDuringPlaying = value;
    }

    //need this for (Delay subtitle mode, cc repeat range ui mode)
    private void updatemValue_IsAlawysShowSubtitle(boolean value) {
        mIsAlawysShowSubtitle = value;
    }

    private void updateTvRubyBottomSubtitleShowAsterisk(int showAsteriskLocal) {
        activity.runOnUiThread(() -> {
            //Sometimes when I click the "Skip the parts that have no subtitles" menu, AraPlayer crashes due to UI and thread issue.
            binding.tvRubyBottom.setShowAsterisk(showAsteriskLocal);
        });

    }

    private void updateValue_listComprehensionCurrentIndex(int value) {
        listComprehensionCurrentIndex = value;
    }
    private void updateValue_listComprehensionModelIndex(int value) {
        listComprehensionModelIndex = value;
    }

    private void updateValue_comprehension1SubtitleIndexInList(int value) {
        comprehension1SubtitleIndexInList = CollectionUtil.getIndexInsideListWhenOutOfIndex(value, listComprehension1SubtitleList);
        int subtitleIndexLocal = listComprehension1SubtitleList.get(comprehension1SubtitleIndexInList);
        updateValue_SubtitleIndex(subtitleIndexLocal);
    }

    private void updateValue_comprehension1SubtitleIndexInListFirstIndex(int value) {
        comprehension1SubtitleIndexInListFirstIndex = value;
    }

    private void updateValue_SubtitleContent(String value) {
        subtitleContent = value;
    }

    private void updateValue_isSkipPlayingNoSubtitlePart(boolean value) {
        isSkipPlayingNoSubtitlePart = value;
    }

    private void updateValue_showAsterisk(int value) {
        showAsterisk = value;
        //For testing
        if (value == Constant.SHOW_ASTERISK.SHOW_SENTENCE) {
            // don't show asterisk = show sentence
//            showCenterMessageView("show sentence");
//            ToastUtil.getInstance(activity).show("show sentence");
            DLog.d("updateValue_showAsterisk", "updateValue_showAsterisk don't show asterisk = show sentence, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent());
        } else if (value == Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY) {
            // show difficult words only
//            ToastUtil.getInstance(activity).show("show difficult words only");
//            showCenterMessageView("show difficult words only");
            DLog.d("updateValue_showAsterisk", "updateValue_showAsterisk show difficult words only, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
        } else if (value == Constant.SHOW_ASTERISK.HIDE_SENTENCE) {
            // completely hide sentence
//            ToastUtil.getInstance(activity).show("completely hide sentence");
//            showCenterMessageView("completely hide sentence");
            DLog.d("updateValue_showAsterisk", "updateValue_showAsterisk completely hide sentence, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
        }
    }



//    private void updateValue_SubtitleIndex(int value) {
////        ToastUtil.getInstance(activity).show("subtitleIndex, new subtitleIndex, position : " + subtitleIndex + "," + value + ", " + exoPlayer.getCurrentPosition());
//        DLog.d("dalnim", "updateValue_SubtitleIndex value : " + value);
//        subtitleIndex = value;
////        getMinMaxTime(getDicModel(subtitleIndex));
//    }

    private void increaseSubtitleIndex() {
        if (subtitleIndex < subtitleList.size()) {
            updateValue_SubtitleIndex(subtitleIndex + 1);
        }
    }

    private void increaseSubtitleIndexAndBackToBeginningWhenOverflow() {
        if (subtitleIndex < subtitleList.size() - 1) {
            updateValue_SubtitleIndex(subtitleIndex + 1);
        } else {
            updateValue_SubtitleIndex(0);
        }
    }

    private void decreaseSubtitleIndex() {
        if (subtitleIndex > 0) {
            updateValue_SubtitleIndex(subtitleIndex - 1);
        }
    }

//    private void updateValue_SubtitleIndexFromCurrentTime() {
////        ToastUtil.getInstance(activity).show("updateValue_SubtitleIndexFromCurrentTime is called : " + subtitleIndex);
//        updateValue_SubtitleIndex(getSubtitleIndexFromCurrentTime());
//    }

    private void notifyDataSetChanged_subtitleDialogAdapter() {
        binding.layoutPlayerSubtitleTable.rvListMeaning.post(() -> subtitleDialogAdapter.notifyDataSetChanged());
    }

    private boolean isVisibileRepeatRange() {
        return isVisibleUI(getLLRepeat());
    }

//    private boolean isVisibleCCRepeatTuning() {
//        return isVisibleUI(viewBinding.llRepeat.rlLayoutCcRepeat);
//    }

    private boolean isVisiblell_repeat() {
        return isVisibleUI(getLLRepeat());
    }

    private boolean isVisibileLLPlay() {
        return isVisibleUI(getLLPlay());
    }

    private boolean isVisibilellPlaySub() {
        return isVisibleUI(binding.llPlaySub.root);
    }

    private void setVisibleRepeatRange(int visibility) {
        getLLRepeat().setVisibility(visibility);
    }

    protected void setVisibleivRepeatBookmark(int visibility) {
        if (Utils.isDebugOrAdminUser(getContext())) {
            binding.llRepeat.ivRepeatBookmark.setVisibility(visibility);
        } else {
            binding.llRepeat.ivRepeatBookmark.setVisibility(View.GONE);
        }
        binding.llRepeat.btnAddSubtitle.setVisibility(visibility);
    }

    private void setVisibleABRepeatUI(int visibility) {
        setVisibleLMABRight(visibility);
        setVisibleButtonsRepetitionABRight(visibility);
    }

    private void setVisibleLMABRight(int visibility) {
        binding.layoutPlayerSubtitleTable.ivLMABRight.setVisibility(visibility);
    }

    private void setVisibleButtonsRepetitionABRight(int visibility) {
        binding.ivShowButtonsRepetitionByAbRightOnMainPlayerScreen.setVisibility(visibility);
    }

    private void setVisibleLLListenComprehension(int visibility) {
        binding.layoutListenComprehension.llListenComprehension.setVisibility(visibility);
    }

    private void setVisibleListenComprehensionExit(int visibility) {
        binding.layoutListenComprehension.ivListenComprehensionExit.setVisibility(visibility);
    }

    protected void setVisibleLLPlay(int visibility) {
        super.setVisibleLLPlay(visibility);
//        llPlay.setVisibility(visibility);

        if (!FileUtil.isMusicApp()) {
            //TODO : If I comment this out, then I can see gray status bar and navigation bar when AraPlayer is entering playing view.
            showHideStatusbar();
            if (visibility == View.VISIBLE) {
                isExpandedScrollableMenu = true;
                expandScrollableMenu();
            } else {
//            setVisibleMeadiaTitle(visibility);
            }
        }
    }

    private void showHideStatusbar() {
        if (FileUtil.isMusicApp()) return;
        getRoot().postDelayed(() -> {
            int marginStatusBar = 0;
            int marginNavigationBar = 0;
            //Don't show status bar on vertical mode.
//        if ((!isVisibleLLPlay()) || isVisibleSubtitleTable() && !isVerticalMode()) {
            if (isHideStatusBar()) {
                Utils.toggleFullscreen(activity);
            } else {
                Utils.toggleFullscreen(activity, false);
                marginStatusBar = Utils.getStatusBarHeight(requireContext());
                marginNavigationBar = Utils.getNavigationBarHeight(activity);
            }
            setMarginLayoutWithStatusAndNavigationBar(marginStatusBar, marginNavigationBar);
        }, 100);
    }

    private boolean isHideStatusBar() {
        if ((!isVisibleLLPlay()) || isVisibleSubtitleTable()) {
            return true;
        } else if (isVerticalMode() && isHasSubtitle()) {
            return true;
        } else if (isListenComprehensionMode()) {
            return true;
        }
        return false;
    }

    private void setMarginLayoutWithStatusAndNavigationBar(int marginStatusBar, int marginNavigationBar) {
        getRoot().post(() -> {
            RelativeLayout.LayoutParams playTopMenuLP = (RelativeLayout.LayoutParams) binding.llPlay.llPlayTopMenu.getLayoutParams();
            RelativeLayout.LayoutParams layoutBtnLeftLP = (RelativeLayout.LayoutParams) binding.llPlay.layoutBtnLeft.getLayoutParams();
            RelativeLayout.LayoutParams llPlayBottomMenuLP = (RelativeLayout.LayoutParams) binding.llPlay.llPlayBottomMenu.getLayoutParams();
            LinearLayout.LayoutParams llListMeaningLP = (LinearLayout.LayoutParams) binding.layoutPlayerSubtitleTable.llListMeaning.getLayoutParams();
            RelativeLayout.LayoutParams llSpeedLP = (RelativeLayout.LayoutParams) binding.llPlay.layoutPlayerPlaySpeed.llSpeed.getLayoutParams();
            FrameLayout.LayoutParams llListenComprehensionLP = (FrameLayout.LayoutParams) binding.layoutListenComprehension.llListenComprehension.getLayoutParams();
            activity.getWindow().setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.status_and_navigation_bar_transparent_color));
            if (isVerticalMode()) {
                playTopMenuLP.setMargins(0, marginStatusBar, 0, 0);
                layoutBtnLeftLP.setMargins(0, 0, 0, 0);
                llSpeedLP.setMargins(0, 0, 0, 0);
                llListenComprehensionLP.setMargins(0, 0, 0, 0);
                if (isVisibleSubtitleTable()) {
                    llListMeaningLP.setMargins(0, 0, 0, marginNavigationBar);
                    llPlayBottomMenuLP.setMargins(0, 0, 0, 0);
                    activity.getWindow().setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.colorBlack));
                } else {
                    llPlayBottomMenuLP.setMargins(0, 0, 0, marginNavigationBar);
                }
            } else {
                if (deviceRotation == Surface.ROTATION_90) {
                    layoutBtnLeftLP.setMargins(0, 0, 0, 0);
                    llSpeedLP.setMargins(0, 0, marginNavigationBar, 0);
                    playTopMenuLP.setMargins(0, marginStatusBar, marginNavigationBar, 0);
                } else if (deviceRotation == Surface.ROTATION_270) {
                    playTopMenuLP.setMargins(marginNavigationBar, marginStatusBar, 0, 0);
                    layoutBtnLeftLP.setMargins(marginNavigationBar, 0, 0, 0);
                    llSpeedLP.setMargins(0, 0, 0, 0);
                }
                llPlayBottomMenuLP.setMargins(0, 0, 0, 0);
                llListMeaningLP.setMargins(0, 0, 0, 0);
                llListenComprehensionLP.setMargins(marginNavigationBar, marginStatusBar, 0, 0);
            }

            binding.llPlay.llPlayTopMenu.setLayoutParams(playTopMenuLP);
            binding.llPlay.layoutBtnLeft.setLayoutParams(layoutBtnLeftLP);
            binding.llPlay.llPlayBottomMenu.setLayoutParams(llPlayBottomMenuLP);
            binding.layoutPlayerSubtitleTable.llListMeaning.setLayoutParams(llListMeaningLP);
            binding.llPlay.layoutPlayerPlaySpeed.llSpeed.setLayoutParams(llSpeedLP);
        });
    }

    private boolean isVisibleLLPlay() {
        return isVisibleView(getLLPlay());
    }

    private boolean isVisibleSubtitleTable() {
        return isVisibleView(binding.layoutPlayerSubtitleTable.llListMeaning);
    }

    private boolean isVisibleSubtitleView() {
        return isVisibleView(binding.llRubyBottom);
    }

    private boolean isVisibleView(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    private void setVisiblellPlaySub(int visibility) {
        binding.llPlaySub.root.setVisibility(visibility);
    }

    private void setVisibleSubtitleTable(int visibility) {
        binding.layoutPlayerSubtitleTable.llListMeaning.setVisibility(visibility);
    }

//    private void setVisibleMeadiaTitle(int visibility) {
//        tvMediaTitle.setVisibility(View.VISIBLE);
//    }

    private void setVisibleEyeButton(int visibility) {
        setVisibleButtonsEyeRight(visibility);
        setVisibleLMSubEyeVertical(visibility);
    }

    private void setVisibleButtonsEyeRight(int visibility) {
        if (isHasSubtitleTotal()) {
            binding.ivShowButtonsSubtitleEye.setVisibility(visibility);
        } else {
            binding.ivShowButtonsSubtitleEye.setVisibility(View.INVISIBLE);
        }
    }

    private void setVisibleIvListenComprehension(int visibility) {
        if (isHasSubtitleTotal()) {
            setVisibleIvListenComprehension1(visibility);
        } else {
            binding.ivListenComprehension1.setVisibility(View.INVISIBLE);
        }
    }

    private void setVisibleIvListenComprehension1(int visibility) {
        binding.ivListenComprehension1.setVisibility(visibility);
    }

    private void setVisibleCCRepeatUI(int visibility) {
        binding.ivShowButtonsRepetitionByCcRight.setVisibility(visibility);
    }


    private void setVisibleButtonsKnowValueRight(int visibility) {
        binding.tvShowButtonsKnowValueRight.setVisibility(visibility);
    }

    private void setVisibleLMSubEyeVertical(int visibility) {
        binding.layoutPlayerSubtitleTable.ivLMSubEyeVertical.setVisibility(visibility);
    }

//    //tvRubyBottom(Ruby subtitle) is in the llRubyBottom
//    private void setVisibleLlRubyButtomSubtitle(int visibility) {
//        setVisibleLlRubyButtomSubtitle();
//    }

    //tvRubyBottom(Ruby subtitle) is in the llRubyBottom
    private void setVisibleLlRubyButtomSubtitle() {
        if (isFullscreenMode()) {
            if (getAlphaSubtitleViewTransparency() == 0 && !isHasSubtitleTotal()) {
                setVisibleLlRubyBottomSubtitle(View.GONE);
            } else {
                setVisibleLlRubyBottomSubtitle(View.VISIBLE);
            }
        } else {
            setVisibleLlRubyBottomSubtitle(View.GONE);
        }
    }

//    private void setVisibleLlRubyButtomSubtitle(int visibility) {
//        int alpha = getAlphaSubtitleViewTransparency();
//        if (alpha == 0 && !isHasSubtitleTotal()) {
//            viewBinding.llRubyBottom.setVisibility(View.GONE);
//        } else if (!isFullscreenMode()) {
//            //Sometimes I can see subtitle on the playing view when there is a subtitle table view.
//            viewBinding.llRubyBottom.setVisibility(View.GONE);
//        } else if (isSubtitleDialogEyeClose()) {
//            viewBinding.llRubyBottom.setVisibility(View.GONE);
//        } else {
//            if (isVerticalMode()) {
//                viewBinding.llRubyBottom.setVisibility(View.GONE);
//            } else {
//                viewBinding.llRubyBottom.setVisibility(View.VISIBLE);
//            }
////            viewBinding.llRubyBottom.setVisibility(visibility);
//            //For testing code : Sometimes I can see subtitle on the playing view when there is a subtitle table view.
//            if (visibility == View.VISIBLE) {
//                DLog.d("Difficult", "setVisibleLlRubyButtomSubtitle View.VISIBLE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent());
//            } else if (visibility == View.INVISIBLE) {
//                DLog.d("Difficult", "setVisibleLlRubyButtomSubtitle View.INVISIBLE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
//            } else if (visibility == View.GONE) {
//                DLog.d("Difficult", "setVisibleLlRubyButtomSubtitle View.GONE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
//            }
//        }
//    }

    private int getAlphaSubtitleViewTransparency() {
        return (int) ((1.0f - (float) sharedPreferences.getSubtitleViewTransparencyOnFullScreen() / 100) * 255);
    }

    //llRubyBottom contains tvRubyBottom
    private void setVisibleTvRubyBottomSubtitle(int visibility) {
        binding.tvRubyBottom.setVisibility(visibility);
        //For testing code : Sometimes I can see subtitle on the playing view when there is a subtitle table view.
        if (visibility == View.VISIBLE) {
            DLog.d("setVisibleRubyBottomSubtitle tvRubyBottom", "setVisibleTvRubyBottomSubtitle View.VISIBLE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
        } else if (visibility == View.INVISIBLE) {
            //If I don't have INVISIBLE, then when I click EYE button to hide subtitle, it doesn't work
            if (subtitleIndex == -1) {
                DLog.d("setVisibleRubyBottomSubtitle tvRubyBottom", "setVisibleTvRubyBottomSubtitle View.INVISIBLE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
            } else {
                DLog.d("setVisibleRubyBottomSubtitle tvRubyBottom", "setVisibleTvRubyBottomSubtitle View.INVISIBLE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
            }
        } else if (visibility == View.GONE) {
            DLog.d("setVisibleRubyBottomSubtitle tvRubyBottom", "setVisibleTvRubyBottomSubtitle View.GONE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
        }
    }

    private void setVisibleLlRubyBottomSubtitle(int visibility) {
        binding.llRubyBottom.setVisibility(visibility);
        //For testing code : Sometimes I can see subtitle on the playing view when there is a subtitle table view.
        if (visibility == View.VISIBLE) {
            DLog.d("setVisibleRubyBottomSubtitle llRubyBottom", "setVisibleLlRubyBottomSubtitle View.VISIBLE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
        } else if (visibility == View.INVISIBLE) {
            //If I don't have INVISIBLE, then when I click EYE button to hide subtitle, it doesn't work
            DLog.d("setVisibleRubyBottomSubtitle llRubyBottom", "setVisibleLlRubyBottomSubtitle View.INVISIBLE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
        } else if (visibility == View.GONE) {
            DLog.d("setVisibleRubyBottomSubtitle llRubyBottom", "setVisibleLlRubyBottomSubtitle View.GONE, subtitleIndex : " + subtitleIndex + getCurrentSubtitleContent() );
        }
    }

    private void setVisibleShowButtonsOnFullScreen(int visibility) {
        if (FileUtil.isMusicApp()) return;
        if (make4ButtonsGone() || isDelaySubtitleMode()) {
            binding.rlShowButtonsOnFullScreen.setVisibility(View.GONE);
            binding.rlShowButtonsOnFullScreenLeft.setVisibility(View.GONE);
        } else {
            binding.rlShowButtonsOnFullScreen.setVisibility(visibility);
            binding.rlShowButtonsOnFullScreenLeft.setVisibility(visibility);
        }
    }

    private void setVisibleSleepTime(int visibility) {
        binding.tvSleepTime.setVisibility(visibility);
    }

    private void setVisiblePinchZoomClose(int visibility) {
        binding.tvPinchZoomClose.setVisibility(visibility);
    }

    private void showllPlay() {
        setVisibleLLPlay(View.VISIBLE);
//        setVisibleLlRubyButtomSubtitle(View.GONE);
        setVisibleShowButtonsOnFullScreen(View.GONE);

        showMediaTitleWithMarquee(getTvTitle(), true, false);
        if (FileUtil.isMusicApp()) {
            showMediaTitleWithMarquee(binding.llPlay.tvTitle, true, false);
        }

        if (isHasShowSubtitle()) {
            showllPlayWithSubtitle();
        } else {
            showllPlayWithoutSubtitle();
        }

        if (isGoingToSleep) {
            setVisibleSleepTime(View.GONE);
        }
        if (!isDisplayVideoScale()) {
            setVisiblePinchZoomClose(View.GONE);
        }
    }

    private void showMediaTitleWithMarquee(View view, boolean beginMarquee, boolean pauseMarquee) {
        // marquee title
        if (beginMarquee) {
            view.setSelected(false);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    view.setSelected(true);
                }
            }, 1500);
        } else {
            //resume/Pause the marquee
            activity.runOnUiThread(() -> view.setSelected(pauseMarquee));
        }
    }

    private void showllPlayWithoutSubtitle() {
        binding.llPlay.ivShowHideTableview.setVisibility(View.GONE);
        int visibility = View.VISIBLE;
        if (isVerticalMode()) {
            visibility = View.GONE;
        }
        ViewUtil.setViewListVisibility(visibility, binding.llPlay.layoutPlayerPlaySpeed.tvAudioSpeedValue, binding.llPlay.ivScreenResize, binding.llPlay.ivZoom);
    }

    private void showllPlayWithSubtitle() {
        if (isFullscreenMode()) {

//            setVisibleMeadiaTitle(View.VISIBLE);
            setVisibilitySubtitleTableViewAndIcon(View.GONE);
        } else {
//            setVisibleMeadiaTitle(View.GONE);
            setVisibilitySubtitleTableViewAndIcon(View.VISIBLE); //Don't do this before isFullscreenMode(), because it refers llListMeaning's visibility
        }

        int visibility = View.VISIBLE;
        if (isVerticalMode()) {
            visibility = View.GONE;
        }
        ViewUtil.setViewListVisibility(visibility, getTvRepeatMin(), getTvRepeatMax(), binding.llRepeat.tvRepeatStart, binding.llRepeat.tvRepeatEnd, binding.llPlay.ivShowHideTableview, binding.llPlay.layoutPlayerPlaySpeed.tvAudioSpeedValue, binding.llPlay.ivScreenResize, binding.llPlay.ivZoom);
    }

    private void showNormalPlayingScreenUI() {
        hideAllControlsOverPlayingScreen();
        hideButtonsOnFullScreenWithAnimation();
//        if (isFullscreenMode()) {
//            hideButtonsOnFullScreenWithAnimation();
//        } else {
//        }
    }

    private void showListenComprehensionUI() {
//        setVisibleLLListenComprehension(View.VISIBLE);
//        setVisibleListenComprehensionExit(View.VISIBLE);
        if (isListenComprehensionMode()) {
            setVisibleLLListenComprehension(View.VISIBLE);
            setVisibleListenComprehensionExit(View.VISIBLE);
            binding.layoutListenComprehension.tvListenComprehensionDescription.setVisibility(View.VISIBLE);
        }
    }

    private void handleVisiblityllPlay() {
        if (isVisibileLLPlay()) {
            showNormalPlayingScreenUI();
        } else {
            showllPlay();
        }
    }

    private void handleVisiblityListenComprehensionUI(boolean isShow) {
        LayoutPlayerPlayBinding llPlay = binding.llPlay;
        if (isShow) {
            hideAllControlsOverPlayingScreen();
            setScrollableMenuModeList();
            showListenComprehensionUI();
            getRoot().postDelayed(() -> {
                setVisibleLLPlay(View.VISIBLE);
                ViewUtil.setViewListVisibility(View.GONE, llPlay.llPlayTopMenu, llPlay.llPlayBottomMenu);
            }, 100);
            setVisibleLLListenComprehension(View.VISIBLE);
        } else {
            showNormalPlayingScreenUI();
            ViewUtil.setViewListVisibility(View.VISIBLE, llPlay.llPlayTopMenu, llPlay.llPlayBottomMenu);
            setVisibleLLListenComprehension(View.GONE);
        }
    }

    private void hideAllControlsOverPlayingScreen() {
        setVisibleLLPlay(View.GONE);
        setVisiblellPlaySub(View.GONE);
//        setVisibleMeadiaTitle(View.GONE);
        setVisibleLLListenComprehension(View.GONE);
        binding.adjustableBarView.setVisibility(View.GONE);
        if (!isCCRepeatMode()) {
            setVisibleShowButtonsOnFullScreen(View.GONE);
        }
//        if (isFullscreenMode()) {
//            setVisibleLlRubyButtomSubtitle(View.VISIBLE);
//        } else {
//            setVisibleLlRubyButtomSubtitle(View.GONE);
//        }

        if (isGoingToSleep) {
            setVisibleSleepTime(View.VISIBLE);
        }
        if (!isDisplayVideoScale()) {
            setVisiblePinchZoomClose(View.VISIBLE);
        }
    }

    private void enterAnyRepeatMode() {
        showAsteriskBeforeAtNormalPlaying = getShowAsteriskAtNormalPlaying();

        activity.runOnUiThread(() -> {
            if (isFullscreenMode()) {
                setVisibleButtonsEyeRight(View.INVISIBLE);
            } else {
                setVisibleLMSubEyeVertical(View.INVISIBLE);
            }
        });
    }

    private void exitAnyRepeatMode() {
        setShowAsteriskBeforeAtNormalPlaying();
        activity.runOnUiThread(() -> {
            if (isFullscreenMode()) {
                if (isListenComprehensionMode()) {
                    setVisibleButtonsEyeRight(View.INVISIBLE);
                } else {
                    setVisibleButtonsEyeRight(View.VISIBLE);
                }
            } else {
                if (!isListenComprehensionMode())
                    setVisibleLMSubEyeVertical(View.VISIBLE);
            }
        });
        refreshDisplayingSubtitle(mIsHideKnownDialogDuringPlaying, Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY);


    }

    private void enterABRepeatMode() {
        enterAnyRepeatMode();
    }

    private void enterCCRepeatMode() {
        if (!isListenComprehensionMode()) {
            enterAnyRepeatMode();
        }
        setVisibleButtonsEyeRight(View.INVISIBLE);
        setVisibleABRepeatUI(View.INVISIBLE);
    }

    private void exitCCRepeatMode() {
        if (isDisplayListenComprehension1()) {
            setVisibleIvListenComprehension1(View.VISIBLE);
//        } else if (isDisplayListenComprehension2()) {
//            setVisibleIvListenComprehension2(View.VISIBLE);
        } else {
            setVisibleABRepeatUI(View.VISIBLE);
            if (!isListenComprehensionMode()) {
                setVisibleIvListenComprehension(View.VISIBLE);
            }
        }
        exitAnyRepeatMode();
    }

    private void enterListenComprehensionMode() {
        if (binding.header.getVisibility() == View.VISIBLE) {
            handleOnClickBack();
        }
        setVisibleABRepeatUI(View.INVISIBLE);
        enterAnyRepeatMode();
        int descriptionTextSizeDimen = R.dimen.font_player_text_top_fullscreen;
        if (isVerticalMode()) {
            descriptionTextSizeDimen = R.dimen.font_player_text_top_vertical;
        }
        binding.layoutListenComprehension.tvListenComprehensionDescription.setTextSize(Utils.getSPValue(requireContext(), descriptionTextSizeDimen));
    }

    private void enterListenComprehensionMode1(DicModel model) {
        enterListenComprehensionMode();
        subtitleDialogAdapter.setListenComprehensionMode(true);
        subtitleDialogAdapter.setListenComprehension1(true);
        subtitleDialogAdapter.setListenComprehension2(false);
        openListenComprehensionMode(Constant.PLAYER.SUB_TITLE.DISPLAY.LISTEN_COMPREHENSION_1, model.getPosition());
        handlePlayDifficultWordAtOnceSubtitleList(subtitleIndex);
    }

    private void enterListenComprehensionMode2(DicModel model) {
        showRepeatCountDialog(model, true, Constant.PLAYER.SUB_TITLE.DISPLAY.LISTEN_COMPREHENSION_2);
    }

    private void resetAndResumeListenComprehensionMode2(DicModel dicModel) {
        if (dicModel == null)
            return;

        updateValue_comprehensionRepeatedCount(0);//listComprehensionRepeatedCount = 0;
        setHideSubtitle();
        updateStatusOfListComprehension2(dicModel);
    }

    //TODO : 듣기 연습 1 도중에 "듣기 연습1 처음 부터 다시 시작"을 할려고 하는 코드인데 잘 안된다. 일단 숨기기로 해둘것
    private void handleListenComprehension1StartOver() {
        listComprehension1SubtitleList = CollectionUtil.fetchItems(listComprehension1SubtitleList, listComprehension1PlaySubtitlesAtOnceCount);
        comprehension1PlaySubtitlesPlayPartsCount = listComprehension1SubtitleList.size();
        listComprehension1PlaySubtitlesAtOnceCount = 1;
        updateValue_comprehensionRepeatedCount(0);//listComprehensionRepeatedCount = 0;
        updateValue_listComprehensionCurrentIndex(-1);
        updateValue_listComprehensionModelIndex(0);
        repeatCountOfListen1 = 1;
        updateValue_comprehension1SubtitleIndexInList(0);
        updateValue_comprehension1SubtitleIndexInListFirstIndex(0);

//        listComprehension1PlaySubtitlesAtOnceCount = 0;
//        comprehension1SubtitleStartIndex = 0; //이건 listComprehension1SubtitleList의 첫번째 index에 해당되는 실제 subtitleIndex의 값이다. 이건 듣기연습1을 시작하면 listComprehension1SubtitleList를 다시 반복할때 사용할려고 하는것이다. 이것이 없으면 듣기연습1을 중간부터 해도 반복시 맨처음 자막으로 가게 된다.
//        comprehension1SubtitleIndexInList = 0; //listComprehension1SubtitleList에서의 index, 최초에는 0으로 시작해서 증가한다., 이건 listComprehension1SubtitleList의 값에서 계속 증가됨.
//        comprehension1SubtitleIndexInListFirstIndex = 0; //listComprehension1SubtitleList에서의 index 최초에는 0으로 시작해서 증가한다., 이건 자막 그룹내에서 맨처음 인덱스가 유지됨.
//        repeatCountOfListen1 = 1;
    }
    private void handleExitListenComprehensionMode() {
        exitListenComprehensionModeinAdapater();
        if (isListenComprehensionMode()) {
            if (isDisplayListenComprehension1()) {
                //듣기 연습1을 빠져나오면 자막 건너뛰기는 무조건 OFF시킨다.
                updateValue_isSkipPlayingNoSubtitlePart(false);
            }
//            resetPlayingDifficultWord(true);
            if (isRepeat) {

                ToastUtil.getInstance(activity).show("isRepeat in ivListenComprehensionExit");
                resetRepeat();
                setVisibleRepeatRange(View.GONE);
                handleVisiblityListenComprehensionUI(true);
                //-------
            } else {
                restoreFromDisplayListenComprehension();
            }
        } else {
            resetRepeat();
            setShowSubtitle();
            handleVisiblityListenComprehensionUI(false);
        }

        exitListenComprehensionMode();
        repeatCountOfListen1 = 0;
        activity.playTTS.stop();
        setVisibleIvListenComprehension(View.VISIBLE);
        binding.ivListenComprehension1.setSelected(false);
        comprehension1PlaySubtitlesPlayPartsCount = -1;
        //Without this, play difficult words is not working this case
        //select a subtitle to play difficult words(Works well), exit Listen mode, then select the same subtitle but don't play difficult words because isPlayRecordOrTSS was still true.
        //So I call resetCheckedRepeatAll when I exit Listen mode to make isPlayRecordOrTSS false for all subtitles
        resetCheckedRepeatAll();
        ToastUtil.getInstance(getContext()).show(R.string.messsage_listen_comprehension_mode_ends);
    }

    private void exitListenComprehensionModeinAdapater() {
        subtitleDialogAdapter.setListenComprehensionMode(false);
        subtitleDialogAdapter.setListenComprehension1(false);
        subtitleDialogAdapter.setListenComprehension2(false);
    }

    private void exitListenComprehensionMode() {
        exitAnyRepeatMode();
        setVisibleABRepeatUI(View.VISIBLE);
        setScrollableMenuModeList();
    }

    private int getShowAsteriskAtNormalPlaying() {
        int showAsteriskValue = Constant.SHOW_ASTERISK.SHOW_SENTENCE;
        int tagValue = (int) binding.layoutPlayerSubtitleTable.ivLMSubEyeVertical.getTag();
        if (isFullscreenMode()) {
            tagValue = (int) binding.ivShowButtonsSubtitleEye.getTag();
        }
        switch (tagValue) {
            case R.drawable.ic_new_eye_close:
                showAsteriskValue = Constant.SHOW_ASTERISK.HIDE_SENTENCE;
                break;
            case R.drawable.ic_new_eye_close_half:
                showAsteriskValue = Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY;
                break;
            default:
                showAsteriskValue = Constant.SHOW_ASTERISK.SHOW_SENTENCE;
                break;
        }
        return showAsteriskValue;
    }

    private void handleClickOneHandMode() {
        hideButtonsOnFullScreenWithAnimation();

        activity.runOnUiThread(() -> {
            if (!isRightHandMode || isVerticalMode()) {
                showRightHandModeUI();
            } else {
                showLeftHandModeUI();
            }
            isRightHandMode = !isRightHandMode;
//            playPlayer();
        });
    }

    private void showOneHandModeAfterRotatingScreen() {
        if (isVerticalMode()) {
            //To make sure the playing screen goes top of the screen.
            showRightHandModeUI();
        } else {
            if (isRightHandMode) {
                showRightHandModeUI();
            } else {
                showLeftHandModeUI();
            }
        }
    }


    private void showRightHandModeUI() {
        activity.runOnUiThread(() -> {
            getRoot().removeAllViewsInLayout();
            getRoot().addView(binding.header);
            getRoot().addView(getLLPlayer());
            getRoot().addView(binding.layoutPlayerSubtitleTable.llListMeaning);
            binding.rlShowButtonsOnFullScreen.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.RIGHT));
            binding.rlShowButtonsOnFullScreenLeft.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.LEFT));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.llRepeat.llRepeatStartEnd.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 1);
            binding.llRepeat.llRepeatStartEnd.setLayoutParams(params);
        });
    }

    private void showLeftHandModeUI() {
        activity.runOnUiThread(() -> {
            getRoot().removeAllViewsInLayout();
            getRoot().addView(binding.header);
            getRoot().addView(binding.layoutPlayerSubtitleTable.llListMeaning);
            getRoot().addView(getLLPlayer());
            binding.rlShowButtonsOnFullScreen.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.LEFT));
            binding.rlShowButtonsOnFullScreenLeft.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER | Gravity.RIGHT));

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.llRepeat.llRepeatStartEnd.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 1);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            binding.llRepeat.llRepeatStartEnd.setLayoutParams(params);
        });
    }

    private void hideAraMusicUIForAraPlayer() {
        activity.runOnUiThread(() -> {
            ViewUtil.setViewListVisibility(View.GONE, binding.llPlay.ivNextSong);
        });
    }
    private void showAraMusicUI() {
        activity.runOnUiThread(() -> {
            getRoot().removeAllViewsInLayout();
            getRoot().addView(binding.header);
            getRoot().addView(binding.llPlayTopAraMusic);
            getRoot().addView(binding.layoutPlayerSubtitleTable.llListMeaning);
            getRoot().addView(getLLPlayer());

            getPlayerView().setForeground(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.color_background_music_player)));
            setVisibleSubtitleTable(View.INVISIBLE);
            binding.llPlay.getRoot().removeView(binding.llPlay.layoutBtnLeft);
            ViewUtil.setViewListVisibility(View.GONE, binding.rlShowButtonsOnFullScreenLeft, binding.rlShowButtonsOnFullScreen,
                    binding.llPlay.llPlayTopMenu, binding.llPlay.layoutBtnLeft, binding.llPlay.layoutPlayerPlaySpeed.getRoot(),
                    binding.llPlay.ivAdjustSubtitleFontSize, binding.llPlay.ivLock);
            ButterKnife.bind(this, binding.getRoot());
        });
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        if (isLockAll)
            return false;

        setVisiblePinchZoomClose(isDisplayVideoScale() ? View.GONE : View.VISIBLE);
        updateValue_ShowViewPlayCenter(true);
        if (canScaleWhenPausedPlaying()) {
//            mScaleFactor *= scaleGestureDetector.getScaleFactor();
//            mScaleFactor = Math.max(twoPinchScaleMin, Math.min(mScaleFactor, twoPinchScaleMax));
            setScale(scaleGestureDetector);
            String strScale = (int) (savedVideoScaleRatio * 100) + "%";
            showCenterMessageView(strScale, 0);
            binding.tvPinchZoomClose.setText(strScale);
            return true;
        } else {
            showCenterMessageView(getString(R.string.msg_pause_video_first_to_zoom));
        }
        return false;
    }

    private boolean isDisplayVideoScale() {
        return savedVideoScaleRatio <= videoScaleDefaultRatioToDisplayText;
    }

    private boolean canScaleWhenPausedPlaying() {
        return (canScaleVideoScreen) && (!isBeingPlaying);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (isLockAll) {
            canScaleVideoScreen = false;
            return false;
        }

        dragZoomMode = DRAG_ZOOM_MODE_ZOOM;
        canScaleVideoScreen = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // Problem : Video is played or paused after zoom in/out.
        // Solution :  Call logics in MotionEvent.ACTION_UP after some delay.
        mHandler.postDelayed(() -> {
            canScaleVideoScreen = false;
            setVisiblePinchZoomClose(isDisplayVideoScale() ? View.GONE : View.VISIBLE);
        }, 100);

    }

    private void setScaleToDefault() {
        setVisiblePinchZoomClose(View.GONE);
        matrixVideoScale.reset();
        matrixVideoScale.setScale(videoScaleDefaultRatio, videoScaleDefaultRatio);
        setTransformTextureView(getPlayerView().getVideoSurfaceView());
        savedVideoScaleRatio = videoScaleDefaultRatio;
//        ToastUtil.getInstance(activity).show("Set zoomed scale to 100%");
    }

    public void setScale(ScaleGestureDetector detector) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            final View videoSurfaceView = playerView;
//            videoSurfaceView.setScaleX(scale);
//            videoSurfaceView.setScaleY(scale);
//        }
        View view = getPlayerView().getVideoSurfaceView();
        float scaleFactor = detector.getScaleFactor();
//        DLog.d("setScale", "detector.getFocusX() : " + detector.getFocusX() + ", detector.getFocusY() : " + detector.getFocusY());
        float origScale = savedVideoScaleRatio;
        savedVideoScaleRatio *= scaleFactor;
        isZoomOut = savedVideoScaleRatio <= origScale;
        if (savedVideoScaleRatio > videoScaleMaxRatio) {
            savedVideoScaleRatio = videoScaleMaxRatio;
            scaleFactor = videoScaleMaxRatio / origScale;
        } else if (savedVideoScaleRatio < videoScaleMinRatio) {
            savedVideoScaleRatio = videoScaleMinRatio;
            scaleFactor = videoScaleMinRatio / origScale;
        }
        videoScalePositionRight = view.getWidth() * savedVideoScaleRatio - view.getWidth();
        videoScalePositionBottom = view.getHeight() * savedVideoScaleRatio - view.getHeight();

        if (0 <= view.getWidth() || 0 <= view.getHeight()) {
            matrixVideoScale.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            if (scaleFactor < 1) {
                matrixVideoScale.getValues(mVideoScale);
                float x = mVideoScale[Matrix.MTRANS_X];
                float y = mVideoScale[Matrix.MTRANS_Y];
                if (0 < view.getWidth()) {
                    if (y < -videoScalePositionBottom)
                        matrixVideoScale.postTranslate(0, -(y + videoScalePositionBottom));
                    else if (y > 0)
                        matrixVideoScale.postTranslate(0, -y);
                } else {
                    if (x < -videoScalePositionRight)
                        matrixVideoScale.postTranslate(-(x + videoScalePositionRight), 0);
                    else if (x > 0)
                        matrixVideoScale.postTranslate(-x, 0);
                }
            }
        } else {
            matrixVideoScale.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            matrixVideoScale.getValues(mVideoScale);
            float x = mVideoScale[Matrix.MTRANS_X];
            float y = mVideoScale[Matrix.MTRANS_Y];
            if (scaleFactor < 1) {
                if (x < -videoScalePositionRight)
                    matrixVideoScale.postTranslate(-(x + videoScalePositionRight), 0);
                else if (x > 0)
                    matrixVideoScale.postTranslate(-x, 0);
                if (y < -videoScalePositionBottom)
                    matrixVideoScale.postTranslate(0, -(y + videoScalePositionBottom));
                else if (y > 0)
                    matrixVideoScale.postTranslate(0, -y);
            }
        }

        setTransformTextureView(view);
    }

    private void setTransformTextureView(View view) {
        if (view instanceof TextureView) {
            ((TextureView) view).setTransform(matrixVideoScale);
            view.invalidate();
        }
    }

    private void initRvScrollableMenu() {
//        int collapseWidth = numberOfScrollableMenuDisplayWhenCollapse *
//                getResources().getDimensionPixelSize(R.dimen.scrollable_menu_item_width) +
//                rvScrollableMenu.getPaddingStart() + rvScrollableMenu.getPaddingEnd();
        scrollableMenuAdapter = new ScrollableMenuAdapter(createScrollableMenuList());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) {
            // Don't delete this code.
            // This code is using to prevent scrolling.
//            @Override
//            public boolean canScrollHorizontally() {
//                return adapter.isExpand();
//            }
        };
        layoutManager.setStackFromEnd(true);
        RecyclerView rvScrollableMenu;
        if (FileUtil.isMusicApp()) {
            rvScrollableMenu = binding.llPlay.layoutScrollableMenuAraMusic.rvScrollableMenu;
        } else {
            rvScrollableMenu = binding.llPlay.layoutPlayerPlayScrollableMenu.rvScrollableMenu;
        }
        rvScrollableMenu.setLayoutManager(layoutManager);
        rvScrollableMenu.setAdapter(scrollableMenuAdapter);
        expandScrollableMenu();
//        rvScrollableMenu.scrollToPosition(0);
//        ViewGroup.LayoutParams layoutParams = rvScrollableMenu.getLayoutParams();
//        layoutParams.width = collapseWidth;
        scrollableMenuAdapter.setScrollableMenuClickListener(new OnScrollableMenuClickListener() {
//            @Override
//            public void onClickExpand() {
//                expandScrollableMenu();
////                layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
////                rvScrollableMenu.setLayoutParams(layoutParams);
////                rvScrollableMenu.scrollToPosition(0);
//            }
//
//            @Override
//            public void onClickCollapse() {
//                expandScrollableMenu();
////                layoutParams.width = collapseWidth;
////                rvScrollableMenu.setLayoutParams(layoutParams);
////                rvScrollableMenu.scrollToPosition(0);
//            }

            @Override
            public void onClickMenu(ScrollableMenuModel menu) {
                handleClickScrollableMenu(menu);
            }
        });

    }

    private void expandScrollableMenu() {
        if (!isVisibileLLPlay())
            return;
        LayoutPlayerPlayScrollableMenuBinding scrollableMenuBinding;
        if (FileUtil.isMusicApp()) {
            scrollableMenuBinding = binding.llPlay.layoutScrollableMenuAraMusic;
        } else {
            scrollableMenuBinding = binding.llPlay.layoutPlayerPlayScrollableMenu;
        }
        RecyclerView rvScrollableMenu = scrollableMenuBinding.rvScrollableMenu;
        ImageView ivScollableMenuRightArrow = scrollableMenuBinding.ivScollableMenuRightArrow;

        int numberOfScrollableMenuDisplayWhenCollapse = isVerticalMode() ? numberOfScrollableMenuDisplayForVerticalWhenCollapse : numberOfScrollableMenuDisplayForLandScapeWhenCollapse;
        ViewGroup.LayoutParams layoutParams = rvScrollableMenu.getLayoutParams();
        if (isExpandedScrollableMenu) {
            ivScollableMenuRightArrow.setRotation(0);
            int collapseWidth = numberOfScrollableMenuDisplayWhenCollapse *
                    getResources().getDimensionPixelSize(R.dimen.scrollable_menu_item_width) +
                    rvScrollableMenu.getPaddingStart() + rvScrollableMenu.getPaddingEnd();
            layoutParams.width = collapseWidth;
        } else {
            ivScollableMenuRightArrow.setRotation(180);
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        isExpandedScrollableMenu = !isExpandedScrollableMenu;
        scrollableMenuAdapter.setExpanded(isExpandedScrollableMenu);
        rvScrollableMenu.setLayoutParams(layoutParams);
        rvScrollableMenu.scrollToPosition(0);
    }

    private List<ScrollableMenuModel> createScrollableMenuList() {
        ScrollableMenuModel menuRotateScreen = new ScrollableMenuModel(Constant.MenuButtonInPlayer.ROTATE_SCREEN, R.drawable.ic_screen_rotate, getString(R.string.scrollable_menu_rotate_screen));
        ScrollableMenuModel menuSubtitleGroup = new ScrollableMenuModel(Constant.MenuButtonInPlayer.SUBTITLE_GROUP, R.drawable.ic_subtitle_group, getString(R.string.scrollable_menu_subtitle_group));
        ScrollableMenuModel menuOneHandMode = new ScrollableMenuModel(Constant.MenuButtonInPlayer.ONE_HAND_MODE, R.drawable.ic_one_hand, getString(R.string.scrollable_menu_left_hand_mode));
        ScrollableMenuModel menuDelaySubtitle = new ScrollableMenuModel(Constant.MenuButtonInPlayer.DELAY_SUBTITLE, R.drawable.ic_delay_subtitle, getString(R.string.scrollable_menu_delay_subtitle));
        ScrollableMenuModel menuSleep = new ScrollableMenuModel(Constant.MenuButtonInPlayer.SLEEP, R.drawable.ic_sleep_timer, getString(R.string.scrollable_menu_sleep));
        menuEmbeddedTracks = getMenuEmbeddedTracks();
        ScrollableMenuModel menuNightMode = new ScrollableMenuModel(Constant.MenuButtonInPlayer.NIGHT_MODE, R.drawable.ic_night_mode_new, getString(R.string.scrollable_menu_night_mode));
//        ScrollableMenuModel menuWebDic = new ScrollableMenuModel(Constant.MenuButtonInPlayer.WEB_DICTIONARY, R.drawable.ic_globe, getString(R.string.scrollable_menu_web_dictionary));
        ScrollableMenuModel menuMirrorMode = new ScrollableMenuModel(Constant.MenuButtonInPlayer.MIRROR_MODE, R.drawable.ic_mirror_mode, getString(R.string.scrollable_menu_mirror_mode));
        ScrollableMenuModel menuFlipVertically = new ScrollableMenuModel(Constant.MenuButtonInPlayer.FLIP_VERTICALLY, R.drawable.ic_flip_vertically, getString(R.string.scrollable_menu_flip_vertically));
        ScrollableMenuModel menuPlayerOption = new ScrollableMenuModel(Constant.MenuButtonInPlayer.PLAYER_OPTION, R.drawable.ic_player_option, getString(R.string.scrollable_menu_open_option));
        ScrollableMenuModel menuPlayerSetting = new ScrollableMenuModel(Constant.MenuButtonInPlayer.PLAYER_SETTING, R.drawable.ic_player_setting, getString(R.string.scrollable_menu_open_setting));
        ScrollableMenuModel menuScreenResize = new ScrollableMenuModel(Constant.MenuButtonInPlayer.SCREEN_RESIZE, R.drawable.ic_screen_zoom_out, getString(R.string.scrollable_menu_screen_resize));
        ScrollableMenuModel menuListenComprehensionOption = new ScrollableMenuModel(Constant.MenuButtonInPlayer.LISTEN_COMPREHENSION_OPTION, R.drawable.ic_listen_comprehension, getString(R.string.scrollable_menu_open_listen_comprehension_option_desc));

        List<ScrollableMenuModel> menuList = new ArrayList<>();
        if (isListenComprehensionMode()) {
            menuList.add(menuRotateScreen);
            if (isVerticalMode()) {

            } else {
                if (isShowAdvancedMode()) {
                    menuList.add(menuOneHandMode);
                }
            }
            if (isShowAdvancedMode()) {
                menuList.add(menuSleep);
            }
            menuList.add(menuPlayerOption);
            menuList.add(menuPlayerSetting);
            if (Utils.isDebugOrAdminUser(getContext())) {
                if (isShowAdvancedMode()) {
                    menuList.add(menuListenComprehensionOption);
                }
            }
        } else {
            menuList.add(menuRotateScreen);
            if (isHasSubtitle()) {
                menuList.add(menuSubtitleGroup);
                if (isVerticalMode()) {

                } else {
                    if (isShowAdvancedMode()) {
                        menuList.add(menuOneHandMode);
                        menuList.add(menuDelaySubtitle);
                    }
                }
            } else {
                if (isVerticalMode()) {

                } else {
                    if (isShowAdvancedMode()) {
                        menuList.add(menuOneHandMode);
                    }
                }
            }
            if (isShowAdvancedMode()) {
                menuList.add(menuSleep);
            }
            if (Utils.isDebugOrAdminUser(getContext())) {
                if (isShowAdvancedMode()) {
                    menuList.add(menuNightMode);
                }
            }
            menuEmbeddedTracksPosition = menuList.size();
            if (hasMultipleTracksToSelect()) {
                menuList.add(menuEmbeddedTracks);
            }
            if (isShowAdvancedMode()) {
                menuList.add(menuMirrorMode);
                menuList.add(menuFlipVertically);
                menuList.add(menuScreenResize);
            }
            if (isHasSubtitleTotal()) {
                menuList.add(menuPlayerOption);
            }
            menuList.add(menuPlayerSetting);
            if (isHasSubtitleTotal() && (Utils.isDebugOrAdminUser(getContext()))) {
                if (isShowAdvancedMode()) {
                    menuList.add(menuListenComprehensionOption);
                }
            }
        }
        return menuList;
    }

    private ScrollableMenuModel getMenuEmbeddedTracks() {
        if (menuEmbeddedTracks == null) {
            menuEmbeddedTracks = new ScrollableMenuModel(Constant.MenuButtonInPlayer.EMBEDDED_TRACKS, R.drawable.ic_embedded_tracks, getString(R.string.scrollable_menu_embedded_tracks));
        }
        return menuEmbeddedTracks;
    }

    private void handleClickScrollableMenu(ScrollableMenuModel menu) {
        if (menu.getType() != Constant.MenuButtonInPlayer.SCREEN_RESIZE) {
            hideAllControlsOverPlayingScreen();
        }
        switch (menu.getType()) {
            case ROTATE_SCREEN:
                handleRotateClick();
                break;
            case NIGHT_MODE:
                onClickNightMode(menu);
                break;
            case PLAYER_OPTION:
                pausePlayer();
                openPlayerOptionDialog(activity.playerFileModel);
                break;
            case PLAYER_SETTING:
                pausePlayer();
                openPlayerSettingDialog(activity.playerFileModel);
                break;
            case LISTEN_COMPREHENSION_OPTION:
                pausePlayer();
                openPlayerListenComprehensionOptionDialog();
                break;
            case EMBEDDED_TRACKS:
                pausePlayer();
                openSelectEmbedTracksDialog();
                break;
            case DELAY_SUBTITLE:
                forcePlayPlayer();
                onDelaySubtitles();
                break;
            case SLEEP:
                if (isGoingToSleep) {
                    cancelSleep();
                } else {
                    pausePlayer();
                    sleep();
                }
                break;
            case ONE_HAND_MODE:
                handleClickOneHandMode();
                break;
            case SUBTITLE_GROUP:
                pausePlayer();
                showSubtitleDialogItemClickCaption();
                break;
//            case WEB_DICTIONARY:
//                pausePlayer();
//                openWebDictionary(null);
//                break;
            case MIRROR_MODE:
                handleClickOnMirrorMode();
                break;
            case FLIP_VERTICALLY:
                handleClickOnFlipVertically();
                break;
            case SCREEN_RESIZE:
                handleScreenZoomClick();
                break;
        }
    }

    private void onClickNightMode(ScrollableMenuModel menu) {
        //TODO : Apply Darkmode in the PlayerFragment on the go.
        ToastUtil.getInstance(activity).show(R.string.msg_under_development);
    }

    private void onClickShadowing(DicModel dicModel) {
        if (PermissionUtils.checkRecordAudio(requireActivity(), REQUEST_CODE_RECORD_AUDIO_TYPE_SHADOWING) && PermissionUtils.checkWriteExternalStorage(activity, REQUEST_CODE_RECORD_AUDIO_TYPE_SHADOWING)) {
            isShadowing = true;
            handleLockClick();
            handleShadowing(dicModel);
        }
    }

    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        @Override
        public void onDisplayAdded(int displayId) {
        }

        @Override
        public void onDisplayChanged(int displayId) {
            if (isFullscreenMode() && isVisibleSubtitleTable())
                return;

            if (isVerticalMode()) {
                showHideStatusbar();
                return;
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                deviceRotation = activity.getDisplay().getRotation();
            } else {
                deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            }
            showHideStatusbar();
        }

        @Override
        public void onDisplayRemoved(int displayId) {
        }
    };

    @Override
    protected void initSeekBarPlayListener() {
        getSBPlayer().setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (!isSeekBarPlayChange) return;
                getTvVideoStartTime().setText(getDisplayTimes(progress, isDisplayStartEndTimeInSubtitleView()));
                getTvVideoEndTime().setText(getDisplayTimes(exoPlayer.getDuration() - progress, isDisplayStartEndTimeInSubtitleView()));
                onPlayerVideoPlayRangeChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isSeekBarPlayChange = true;
                pausePlayer();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekToInPlayer(seekBar.getProgress());
                onPlayerVideoPlayRangeChangedStopTracking();
                playPlayer();
            }
        });
    }

    @Override
    public void updateSeekBarPlay(long position) {
        super.updateSeekBarPlay(position);
        getSBPlayer().setProgress((int) position);
    }

    @Override
    protected void setDurationForSeekBar(long duration) {
        getSBPlayer().setMax((int) duration);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initAdjustableBarView() {
        binding.adjustableBarView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_MOVE:
                    rebuildView(event.getRawX());
                    break;

                case MotionEvent.ACTION_UP:
                    rebuildView(event.getRawX());
                    break;

                case MotionEvent.ACTION_CANCEL:
                    break;
            }
            return true;
        });
    }

    private void rebuildView(float draggedToX) {
        // reset weights
        playerWidthPercent = (100 - (100 * (screenWidth - draggedToX) / screenWidth));
        setPanelWeights(playerWidthPercent);

        getRoot().requestLayout();
    }

    @Override
    protected void setPanelWeights(float percentLeft) {
        float percentRight = 100 - percentLeft;

        if (percentLeft < minimumPanelWidthPercent) {
            percentLeft = minimumPanelWidthPercent;
            percentRight = 100 - percentLeft;
        }

        if (percentRight < minimumPanelWidthPercent) {
            percentRight = minimumPanelWidthPercent;
            percentLeft = 100 - percentRight;
        }

        // set weights
        LinearLayout.LayoutParams llPlayerLP = (LinearLayout.LayoutParams) getLLPlayer().getLayoutParams();
        llPlayerLP.width = 0;
        llPlayerLP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        llPlayerLP.weight = percentLeft;
        getLLPlayer().setLayoutParams(llPlayerLP);

        LinearLayout.LayoutParams llListMeaningLP = (LinearLayout.LayoutParams) binding.layoutPlayerSubtitleTable.llListMeaning.getLayoutParams();
        llListMeaningLP.width = 0;
        llListMeaningLP.height = LinearLayout.LayoutParams.MATCH_PARENT;
        llListMeaningLP.weight = percentRight;
        binding.layoutPlayerSubtitleTable.llListMeaning.setLayoutParams(llListMeaningLP);
    }

    private void resetAdjustableBarView() {
        playerWidthPercent = Constant.DEFAULT_PLAYER_WIDTH_PERCENT;
        setPanelWeights(playerWidthPercent);
        closeAdjustableBarView();
    }

    private void closeAdjustableBarView() {
        isEnableChangeTableWidth = false;
        hideAllControlsOverPlayingScreen();
        sharedPreferences.setPlayerWidthPercent(playerWidthPercent);
        playPlayer();
    }

    private void enableChangeTableWidth() {
        isEnableChangeTableWidth = true;
        hideAllControlsOverPlayingScreen();
        binding.adjustableBarView.setVisibility(View.VISIBLE);
    }

    private void handleClickOnMirrorMode() {
        getPlayerView().startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.flip_horizontal));
        if (getPlayerView().getScaleX() == 1) {
            getPlayerView().setScaleX(-1);
        } else {
            getPlayerView().setScaleX(1);
        }
        hideButtonsOnFullScreenWithAnimation();
    }

    private void handleClickOnFlipVertically() {
        getPlayerView().startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.flip_vertical));
        if (getPlayerView().getScaleY() == 1) {
            getPlayerView().setScaleY(-1);
        } else {
            getPlayerView().setScaleY(1);
        }
        hideButtonsOnFullScreenWithAnimation();
    }

    private boolean hasMultipleTracksToSelect() {
        boolean hasMultipleTracks = false;
        DefaultTrackSelector trackSelector = (DefaultTrackSelector) exoPlayer.getTrackSelector();
        if (trackSelector != null) {
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
            if (mappedTrackInfo != null) {
                for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                    int trackType = mappedTrackInfo.getRendererType(rendererIndex);
                    TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex);
                    for (int i = 0; i < trackGroupArray.length; i++) {
                        if (trackType == C.TRACK_TYPE_TEXT) { // has Embedded "SUBTITLE"
                            hasMultipleTracks = true;
                            break;
                        }
                        TrackGroup trackGroup = trackGroupArray.get(i);
                        if (trackType == C.TRACK_TYPE_VIDEO || trackType == C.TRACK_TYPE_AUDIO) {
                            if (trackGroup.length > 1) {
                                // Video has NONE, AUTO and more than One Resolution.
                                // Audio has NONE, AUTO and and more than One Voice.
                                hasMultipleTracks = true;
                                break;
                            }
                        }
                    }
                    if (hasMultipleTracks) break;
                }
            }
            if (hasMultipleTracks) {
                selectSavedTrack(trackSelector, mappedTrackInfo);
            }
        }
        return hasMultipleTracks;
    }

    private void selectSavedTrack(DefaultTrackSelector trackSelector, MappingTrackSelector.MappedTrackInfo mappedTrackInfo) {
        String selectionTrack = activity.playerFileModel.getVideoModel().getTrackSelectionJsonStr();
        if (TextUtils.isEmpty(selectionTrack)) return;
        try {
            Type listType = new TypeToken<List<TrackSelection>>(){}.getType();
            List<TrackSelection> trackSelectionList = new Gson().fromJson(selectionTrack, listType);
            if (trackSelectionList.isEmpty()) return;
            DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
            DefaultTrackSelector.Parameters.Builder builder = parameters.buildUpon();
            for (int i = 0; i < trackSelectionList.size(); i++) {
                TrackSelection trackSelection = trackSelectionList.get(i);
                for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++) {
                    int trackType = mappedTrackInfo.getRendererType(rendererIndex);
                    if (trackSelection.getTrackType() == trackType) {
                        builder.clearSelectionOverrides(rendererIndex).setRendererDisabled(rendererIndex, trackSelection.isDisable());
                        List<DefaultTrackSelector.SelectionOverride> overrides = trackSelection.getSelectionOverrides();
                        if (!overrides.isEmpty()) {
                            builder.setSelectionOverride(
                                    rendererIndex,
                                    mappedTrackInfo.getTrackGroups(rendererIndex),
                                    overrides.get(0));
                        }
                    }
                }
            }
            trackSelector.setParameters(builder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleShadowing(DicModel dicModel) {
        updateValue_SubtitleIndex(subtitleList.indexOf(dicModel));
        handlePlayTheSubtitle(dicModel);
        getMinMaxTime(dicModel);
        binding.ivLockAll.setVisibility(View.GONE);
        LayoutShadowingBinding layoutShadowingBinding = binding.layoutShadowing;

        layoutShadowingBinding.root.setVisibility(View.VISIBLE);
        layoutShadowingBinding.layoutRecordingAndListening.setVisibility(View.VISIBLE);

        layoutShadowingBinding.ivRecording.setVisibility(View.INVISIBLE);
        layoutShadowingBinding.ivListeningMyVoice.setVisibility(View.INVISIBLE);
        layoutShadowingBinding.ivListeningVideoVoice.setVisibility(View.VISIBLE);
        layoutShadowingBinding.pbRecordingCountDown.setVisibility(View.INVISIBLE);
        layoutShadowingBinding.tvRecordingTapToFinish.setVisibility(View.INVISIBLE);
        layoutShadowingBinding.tvTitle.setText(R.string.title_shadowing_listening_video_voice);
    }

    private void initMediaRecorder(File shadowingRecordFile) {
        mediaRecorder = Voca.setupMediaRecorder();
        if (shadowingRecordFile != null) {
            mediaRecorder.setOutputFile(shadowingRecordFile.getPath());
        }
        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    private void recordShadowing(DicModel dicModel) {
        isRecordingOrListeningInShadowing = true;
        LayoutShadowingBinding layoutShadowingBinding = binding.layoutShadowing;
        pausePlayer();
        long subtitlePlayingTime = dicModel.getEndTime() - dicModel.getStartTime();
        float ratioRecordingTime = 2.5f;
        long recordingTime = (long) (subtitlePlayingTime * ratioRecordingTime);
//        layoutShadowingBinding.layoutRecordingAndListening.setVisibility(View.VISIBLE);
        File shadowingRecordFile = generateRecordFile(dicModel);
        initMediaRecorder(shadowingRecordFile);
        updateValue_SubtitleIndex(subtitleList.indexOf(dicModel));
        subtitleDialogAdapter.setLastCheckedPosition(subtitleIndex);
        binding.layoutPlayerSubtitleTable.rvListMeaning.smoothScrollToPosition(subtitleIndex);

        layoutShadowingBinding.tvTitle.setText(R.string.title_shadowing_recording);
        layoutShadowingBinding.ivRecording.setVisibility(View.VISIBLE);
        layoutShadowingBinding.pbRecordingCountDown.setVisibility(View.VISIBLE);
        layoutShadowingBinding.tvRecordingTapToFinish.setVisibility(View.VISIBLE);
        layoutShadowingBinding.pbRecordingCountDown.setProgress(100);
        layoutShadowingBinding.ivListeningMyVoice.setVisibility(View.GONE);
        layoutShadowingBinding.ivListeningVideoVoice.setVisibility(View.GONE);

        try {
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            mediaRecorder.reset();
        }
        shadowingCountDownTimer = new CountDownTimer(recordingTime, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                layoutShadowingBinding.pbRecordingCountDown.setProgress((int) (millisUntilFinished * 100 / recordingTime));
            }

            @Override
            public void onFinish() {
                try {
                    mediaRecorder.stop();
                    dicModel.setRecordedPath(shadowingRecordFile.getPath());
                    subtitleDialogAdapter.notifyItemChanged(subtitleIndex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                releaseMediaRecorder();
                layoutShadowingBinding.ivRecording.setVisibility(View.GONE);
                layoutShadowingBinding.pbRecordingCountDown.setVisibility(View.GONE);
                layoutShadowingBinding.pbRecordingCountDown.setProgress(0);
                layoutShadowingBinding.tvTitle.setText(R.string.title_shadowing_listening_my_voice);
                layoutShadowingBinding.ivListeningMyVoice.setVisibility(View.VISIBLE);
                layoutShadowingBinding.ivListeningVideoVoice.setVisibility(View.GONE);
                layoutShadowingBinding.tvRecordingTapToFinish.setVisibility(View.GONE);
                listenShadowing(shadowingRecordFile);
                shadowingCountDownTimer.cancel();
            }
        };
        shadowingCountDownTimer.start();
    }

    private File generateRecordFile(DicModel dicModel) {
        return Voca.getVoiceFileOnLocal(activity, activity.playerFileModel, Voca.getVoiceFolderOnLocal(activity), dicModel.getVIPath(), dicModel.getVocaId());
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
        }
        mediaRecorder = null;
    }

    private void listenShadowing(File shadowingRecordFile) {
        if (shadowingRecordFile != null && shadowingRecordFile.exists()) {
            initMediaPlayer();
            try {
                setUpMediaPlayer(shadowingRecordFile);
                mediaPlayer.setOnCompletionListener(mp -> {
                    updateValue_SubtitleIndex(BaseCollectionUtil.getFirstIndexIfOutOfIndex(subtitleList, subtitleIndex + 1));
                    DicModel nextSubtitleDicModel = subtitleList.get(subtitleIndex);
                    releaseMediaPlayer();
                    isRecordingOrListeningInShadowing = false;
                    handleShadowing(nextSubtitleDicModel);
                });
            } catch (Exception e) {
                e.printStackTrace();
                closeShadowing(null);
            }
        } else {
            closeShadowing(null);
        }
    }

    private void setUpMediaPlayer(File file) throws IOException {
        Uri uri = Uri.fromFile(file);
        mediaPlayer.setDataSource(requireContext(), uri);
        mediaPlayer.setLooping(false);
        mediaPlayer.prepare();
        mediaPlayer.setOnPreparedListener(mp -> mediaPlayer.start());
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = null;
    }

    private void closeShadowing(DicModel nextSubtitleDicModel) {
        isShadowing = false;
        isRecordingOrListeningInShadowing = false;
        binding.layoutShadowing.layoutRecordingAndListening.setVisibility(View.GONE);
        binding.layoutShadowing.root.setVisibility(View.GONE);
        binding.root.postDelayed(() -> {
            handleLockClick();
            playPlayer();
            if (nextSubtitleDicModel == null) {
//                playPlayer();
            } else {
                handlePlayTheSubtitle(nextSubtitleDicModel);
            }
        }, 100);
    }

    private void mapRecordedPathForSubtitleList(List<DicModel> subtitleList) {
        subtitleList.forEach(item-> {
            addRecordedFileInDicModel(item);
        });
    }

    private void onClickListeningRecorded(DicModel item) {
        releaseMediaPlayer();
        isListeningRecorded = false;
        if (item.isListeningRecord()) {
            listenRecordedList.add(item);
            listenRecordedSubtitleIndex = listenRecordedList.indexOf(item);
            handlePlayTheSubtitle(item);
//            handleVisibilityListenRecordedSubtitleUI(true);
            if (exoPlayer.getVolume() > 0) {
                currentVolume = exoPlayer.getVolume();
            }
            exoPlayer.setVolume(currentVolume);
        } else {

            listenRecordedList.remove(item);
        }


        if (Utils.isEmpty(listenRecordedList)) {
            closeListeningRecorded();
        } else if (listenRecordedList.size() == 1) {
            setVisibleUI_enterListenRecorded();
//            handleVisibilityListenRecordedSubtitleUI(true);
        }
    }

    private void listenRecordedSubtitle() {
//        pausePlayer();
//        if (listenRecordedSubtitleIndex >= listenRecordedList.size()) { // 녹음한걸 2개 듣다가 1개를 취소하면 여서서 전체가 다 빠져나간다. 그래서 주석 처리함.
//            closeListeningRecorded();
//            return;
//        }
        if (Utils.isEmpty(listenRecordedList)) {
            closeListeningRecorded();
            return;
        }
        DicModel selectedSubtitle = Utils.isIndexInsideRange(listenRecordedList, listenRecordedSubtitleIndex) ? listenRecordedList.get(listenRecordedSubtitleIndex) : listenRecordedList.get(0);
        handlePlayTheSubtitle(selectedSubtitle);
        File recordedFile = generateRecordFile(selectedSubtitle);
        if (recordedFile != null && recordedFile.exists()) {
            initMediaPlayer();
            try {
                isListeningRecorded = true;
                exoPlayer.setVolume(0f);
                setUpMediaPlayer(recordedFile);
                mediaPlayer.setOnCompletionListener(mp -> {
                    isListeningRecorded = false;
                    if (listenRecordedList.isEmpty()) {
                        closeListeningRecorded();
                        return;
                    }
                    releaseMediaPlayer();
                    listenRecordedSubtitleIndex++;
                    if (listenRecordedSubtitleIndex >= listenRecordedList.size()) {
                        listenRecordedSubtitleIndex = 0;
                    }
                    DicModel nextListenItem = listenRecordedList.get(listenRecordedSubtitleIndex);
                    handlePlayTheSubtitle(nextListenItem);
                    exoPlayer.setVolume(currentVolume);
                });
            } catch (Exception e) {
                e.printStackTrace();
                closeListeningRecorded();
            }
        } else {
            closeListeningRecorded();
        }
    }

    private void closeListeningRecorded() {
        isListeningRecorded = false;
        if (!listenRecordedList.isEmpty()) {
            for (DicModel dicModel : listenRecordedList) {
                dicModel.setListeningRecord(false);
            }
            subtitleDialogAdapter.notifyDataSetChanged();
        }
        listenRecordedList.clear();
        releaseMediaPlayer();
        setVisibleUI_exitListenRecorded();
//        handleVisibilityListenRecordedSubtitleUI(false);
        exoPlayer.setVolume(currentVolume);
        playPlayer();
    }

    private void manualStopRecording() {
        if (shadowingCountDownTimer != null) {
            shadowingCountDownTimer.onFinish();
        }
    }

    private void deleteRecordingFile(DicModel model) {
        YesNoDialog dialog = new YesNoDialog(
                activity,
                R.string.confirm,
                R.string.msg_delete_recording_file_confirm,
                model,
                new OnYesNoClickListener() {
                    @Override
                    public void onYesClick(View view, Object object) {
                        File recordedFile = generateRecordFile(model);
                        if (recordedFile != null && recordedFile.exists()) {
                            recordedFile.delete();
                        }
                        model.setRecordedPath(null);
                        subtitleDialogAdapter.notifyItemChanged(subtitleList.indexOf(model));
                        playPlayer();
                    }

                    @Override
                    public void onNoClick(View view, Object object) {

                    }
                });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CODE_RECORD_AUDIO_TYPE_SHADOWING) {
                onClickShadowing(subtitleDialogAdapter.getCheckedDicModel());
            } else if (requestCode == REQUEST_CODE_RECORD_AUDIO_TYPE_STT) {
                onClickSubtitleTTS();
            }
        } else {
            if (requestCode == REQUEST_CODE_RECORD_AUDIO_TYPE_STT) {
                handlePlayClick(true, true);
            }
        }
    }

    private void onClickSubtitleTTS() {
        if (PermissionUtils.checkRecordAudio(requireActivity(), REQUEST_CODE_RECORD_AUDIO_TYPE_STT)) {
            initSpeechRecognizer();
            if (isPlayingSTT) {
                updateValue_isPlayingSTT_false();
                editSubtitleDialog.updateMicToReadyToRecordImage();
                stopSTT();
            } else {
                updateValue_isPlayingSTT_true();
                editSubtitleDialog.updateMicToBeingRecordingImage();
                ToastUtil.getInstance(getContext()).show(R.string.stt_start);
                startSTT();
            }
        }
    }

    private void initSpeechRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext());
            speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, BuildConfig.VOICELANG);
            String logTag = getLogTag() + " SpeechRecognizer";
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    DLog.d(logTag, "onReadyForSpeech");
                }

                @Override
                public void onBeginningOfSpeech() {
                    DLog.d(logTag, "onBeginningOfSpeech");
                }

                @Override
                public void onRmsChanged(float rmsdB) {
                    DLog.d(logTag, "onRmsChanged");
                }

                @Override
                public void onBufferReceived(byte[] buffer) {
                    DLog.d(logTag, "onBufferReceived");
                }

                @Override
                public void onEndOfSpeech() {
                    DLog.d(logTag, "onEndOfSpeech");
//                    speechRecognizer.destroy();
//                    speechRecognizer = null;
                }

                @Override
                public void onError(int error) {
                    ToastUtil.getInstance(requireContext()).show(R.string.stt_fail);
                    String errorStr = null;
                    switch (error) {
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT: {
                            errorStr = "ERROR_NETWORK_TIMEOUT";
                            break;
                        }
                        case SpeechRecognizer.ERROR_NETWORK: {
                            errorStr = "ERROR_NETWORK";
                            break;
                        }
                        case SpeechRecognizer.ERROR_AUDIO: {
                            errorStr = "ERROR_AUDIO";
                            break;
                        }
                        case SpeechRecognizer.ERROR_SERVER: {
                            errorStr = "ERROR_SERVER";
                            break;
                        }
                        case SpeechRecognizer.ERROR_CLIENT: {
                            errorStr = "ERROR_CLIENT";
                            break;
                        }
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT: {
                            errorStr = "ERROR_SPEECH_TIMEOUT";
                            break;
                        }
                        case SpeechRecognizer.ERROR_NO_MATCH: {
                            errorStr = "ERROR_NO_MATCH";
                            break;
                        }
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY: {
                            errorStr = "ERROR_RECOGNIZER_BUSY";
                            break;
                        }
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS: {
                            errorStr = "ERROR_INSUFFICIENT_PERMISSIONS";
                            break;
                        }
                    }
                    DLog.d(logTag, "onError: "  + errorStr);
                    editSubtitleDialog.updateMicToReadyToRecordImage();
                    updateValue_isPlayingSTT_false();
                }

                @Override
                public void onResults(Bundle results) {
                    DLog.d(logTag, "onResults");
                    ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    editSubtitleDialog.updateMicToReadyToRecordImage();
                    if (data.isEmpty()) {
                        ToastUtil.getInstance(requireContext()).show(R.string.stt_fail);
                    } else {
                        String sttResult = data.get(0);
                        final YesNoDialog dialog = new YesNoDialog(getContext(),
                                R.string.info,
                                getString(R.string.stt_success, sttResult),
                                null,
                                new OnYesNoClickListener() {
                                    @Override
                                    public void onYesClick(View view, Object object) {
                                        Utils.copyToClipboard(getContext(), sttResult, R.string.copied);
                                    }

                                    @Override
                                    public void onNoClick(View view, Object object) {

                                    }
                                });
                        dialog.show();
                    }
                    updateValue_isPlayingSTT_false();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    DLog.d(logTag, "onPartialResults");
                }

                @Override
                public void onEvent(int eventType, Bundle params) {
                    DLog.d(logTag, "onEvent: " + eventType);
                }
            });
        }
    }

    private void startSTT() {
        if (speechRecognizer != null && speechRecognizerIntent != null) {
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    private void stopSTT() {
        updateValue_isPlayingSTT_false();
        if (speechRecognizer != null && speechRecognizerIntent != null) {
            speechRecognizer.stopListening();
        }
    }

    private void updateValue_isPlayingSTT_false() {
        updateValue_isPlayingSTT(false);
    }

    private void updateValue_isPlayingSTT_true() {
        updateValue_isPlayingSTT(true);
    }

    private void updateValue_isPlayingSTT(boolean value) {
        isPlayingSTT = value;
    }

    private boolean stopTTSWhenPressBack() {
        if (textToSpeechToSpeakMediaTitle.isSpeaking() || activity.playTTS.playTTSHelper.isTTSSpeaking()) {
            textToSpeechToSpeakMediaTitle.stop();
            activity.playTTS.playTTSHelper.stop();
            if (isInitViewFinished) {
                startOverWhenItReachEndOfMediaMain();
            } else {
                startInitViewDelay();
            }
            return true;
        }
        return false;
    }

    private void getMusicPlayList() {
        if (FileUtil.isMusicApp() && musicFilePlayList.isEmpty()) {
            List<VideoModel> playList = sharedPreferences.getListDataFromJson(Constant.SHARE_PREF.KEY_MUSIC_PLAYLIST, VideoModel[].class);
            if (playList != null && !playList.isEmpty()){
                playList.forEach(item -> {
                    if (item.getPath().equals(activity.playerFileModel.getVideoModel().getPath())) {
                        musicFilePlayList.add(activity.playerFileModel);
                    } else {
                        musicFilePlayList.add(new PlayerFileModel(item, FileUtil.isValidMusicExtension(item.getPath())));
                    }
                });
            }
        }
    }

    private boolean isMusicPlayListNotEmpty() {
        return musicFilePlayList != null && !musicFilePlayList.isEmpty();
    }

    private int getCurrentPlaylistIndex() {
        int currentIndex = -1;
        for (int i = 0; i < musicFilePlayList.size(); i++) {
            PlayerFileModel playerFileModel = musicFilePlayList.get(i);
            if (playerFileModel.getPath().equals(activity.playerFileModel.getPath())) {
                currentIndex = i;
                break;
            }
        }
        return currentIndex;
    }

    private void selectNextPlayerFile() {
        int currentIndex = getCurrentPlaylistIndex();
        previousPlaylistIndex = currentIndex;
        if (binding.llPlay.ivShuffle.isSelected()) {
            Random r = new Random();
            currentIndex = r.ints(0, musicFilePlayList.size() - 1).findFirst().getAsInt();
        } else {
            currentIndex++;
        }
        if (currentIndex >= musicFilePlayList.size()) {
            currentIndex = 0;
        }
        getFileFromPlaylistIndexAndPlay(currentIndex);
    }

    private void getFileFromPlaylistIndexAndPlay(int index) {
        PlayerFileModel nextFile = musicFilePlayList.get(index);
        if (nextFile != null) {
            isSettingNextFile = true;
            activity.playerFileModel = nextFile;
            isDurationSet = false;
            initData();
//            Uri videoUri = (new Uri.Builder()).path(activity.playerFileModel.getPath()).build();
//            mediaSource = new ProgressiveMediaSource.Factory(
//                    new DefaultDataSourceFactory(requireContext()),
//                    new DefaultExtractorsFactory()
//            ).createMediaSource(MediaItem.fromUri(videoUri));
//            exoPlayer.setMediaSource(mediaSource);
//            exoPlayer.prepare();
//            setPlayyWhenReady(false);
            Intent intent = new Intent(PlayerService.PLAYER_RECEIVER_ACTION);
            setExtraForPlayerService(intent);
            requireActivity().sendBroadcast(intent);
            if (isPlayTitleByTtsBeforePlaying()) {
                String ttsTitleIntroduce = getTtsTitleToIntroduceBeforePlayingMusic();
                textToSpeechToSpeakMediaTitle.speak(ttsTitleIntroduce, TextToSpeech.QUEUE_FLUSH, null, ttsTitleIntroduce);
            } else {
                isSettingNextFile = false;
            }
        }
    }

    @Override
    protected void startOverWhenItReachEndOfMedia() {
        if (FileUtil.isMusicApp() && isMusicPlayListNotEmpty() && isMusicPlaylist() && !isFinishing
                && !binding.llPlay.ivRepeatSong.isSelected()) {
            selectNextPlayerFile();
        } else {
            super.startOverWhenItReachEndOfMedia();
        }
    }

    @Override
    protected void startOverWhenItReachEndOfMediaMain() {
        if (FileUtil.isMusicApp() && playAllDifficultWordsBeforePlayingMusic()) {
            handlePlayAllDifficultWordBeforePlaying();
        } else {
            super.startOverWhenItReachEndOfMediaMain();
        }
    }

    private boolean isMusicPlaylist() {
        return ((PlayerActivity) activity).isMusicPlaylist;
    }

    private boolean isPlayListNotEmpty() {
        return musicFilePlayList != null && !musicFilePlayList.isEmpty();
    }

    private void handleClickPreviousSong() {
        if (isPlayListNotEmpty()) {
            int currentIndex = getCurrentPlaylistIndex();
            if (previousPlaylistIndex < 0) {
                if (binding.llPlay.ivShuffle.isSelected()) {
                    Random r = new Random();
                    currentIndex = r.ints(0, musicFilePlayList.size() - 1).findFirst().getAsInt();
                } else {
                    currentIndex--;
                }
                if (currentIndex < 0) {
                    currentIndex = musicFilePlayList.size() - 1;
                }
            } else {
                currentIndex = previousPlaylistIndex;
            }
            previousPlaylistIndex = -1;
            getFileFromPlaylistIndexAndPlay(currentIndex);
        }
    }

//    private void handleClickNextSong() {
//        if (isPlayListNotEmpty()) {
//            selectNextPlayerFile();
//        }
//    }

    private void handleClickShuffle() {
        boolean isShuffle = !binding.llPlay.ivShuffle.isSelected();
        binding.llPlay.ivShuffle.setSelected(isShuffle);
    }

    private void handleClickRepeatSong() {
        boolean isRepeat = !binding.llPlay.ivRepeatSong.isSelected();
        binding.llPlay.ivRepeatSong.setSelected(isRepeat);
    }

    private void showTxtLyric(String subTitlePath) {
        String lyric = LyricUtils.parseTextFromFile(new File(subTitlePath), "UTF-8");
        binding.layoutPlayerSubtitleTable.tvLyric.setText(lyric);
    }

    private void handleClickSelectLyric() {
        setPlayyWhenReady(false);
        Intent intent = new Intent(activity, SubtitleFilesPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, activity.playerFileModel);
        startActivity(intent);
    }

    private void makeRubyTextFromServer(Object resultData) {
        if (resultData == null) {
            Loading.hide();
            isAnalyzingLyric = false;
            return;
        }
        //Don't check the subtitle extension, just call the makeRubyTextFromSubtitle API
        makeRubyTextFromSubtitle(resultData);
    }

    private void makeRubyTextFromSubtitle(Object content) {
        application.getAraPlayerApiImpl().makeRubyTextFromSubtitle(requireContext(),
                activity.playerFileModel,
                content,
                new DalApiListener<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        downloadAndUnzipFile(response);
                    }

                    @Override
                    public void onFailure(String error) {
                        parserSubtitleError();
                    }
                });
    }

    private void downloadAndUnzipFile(ResponseBody response) {
        if (response == null) {
            parserSubtitleError();
            return;
        }
        File file = StorageUtil.writeResponseBodyToDisk(requireContext(), response);
        String subtitleDatabasePath = StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(activity, activity.playerFileModel.getPath(), activity.playerFileModel);
        String path = StorageUtil.getFilesStoragePath(requireContext(), activity.playerFileModel);
        StorageUtil.unzip(requireContext(), file, new File(path), subtitleDatabasePath);
        // check sub database
        activity.createSubDatabase(activity.playerFileModel);
        if (activity.playerFileModel.getVideoModel().getTongueLang() != activity.motherTongueLanguage.getIdApi()) {
            activity.playerFileModel.getVideoModel().setTongueLang(activity.motherTongueLanguage.getIdApi());
            activity.updateVideoModel(activity.playerFileModel);
        }
        if (activity.isHasSubRuby()) {
            activity.playerFileModel.getVideoModel().setAnalyzeAgain(Constant.INT_BOOLEAN.FASLE); //Not to display analyze again warning text message
            activity.updateVideoModel(activity.playerFileModel);
            // check translator subtitle when no translation automatically & study language is EN & translator subtitle is ON
            if (sharedPreferences.getTranslateSubtitleFromServer()) {
                ArrayList<DicModel> list = new ArrayList<>();
                list.addAll(activity.getSubDatabase().getNoTranslationSubtitleDialogListByLanguage());
                if (!Utils.isEmptyCollection(list)) {
                    CustomTranslate customTranslate = new CustomTranslate(requireContext());
                    for (DicModel item : list) {
                        customTranslate.translateText(item.getVocaDisplay(), (view, object) -> {
                            DLog.d(getLogTag(), item.getId() + " - " + item.getVocaDisplay() + " translate to =" + object);
                            item.setMeaning((String) object);
                            activity.getSubDatabase().updateTranslate(item);
                        });
                    }
                }
            }
            initData();
        } else {
            ToastUtil.getInstance(requireContext()).show(R.string.error_msg_parser_sub_database_title);
            Loading.hide();
        }
        isAnalyzingLyric = false;
    }

    private void parserSubtitleError() {
        isAnalyzingLyric = false;
        ToastUtil.getInstance(requireContext()).show(R.string.error_msg_parser_sub_title);
        Loading.hide();
    }

    private void initSubtitleFontSize() {
        binding.tvRubyBottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.tvRubyBottom.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private boolean isPinchToZoomSubtitle(View view, MotionEvent event) {
        if (event.getPointerCount() == 2 && isTouchOnSubtitleViewOnFullScreenMode(view)) {
            isZoomingSubtitle = true;
            int action = event.getAction();
            int pureAction = action & MotionEvent.ACTION_MASK;
            if (pureAction == MotionEvent.ACTION_POINTER_DOWN) {
                mSubtitleBaseDist = getDistance(event);
            } else {
                DLog.d("isPinchToZoomSubtitle", "mSubtitleBaseDist : " + mSubtitleBaseDist);
//                int dist = getDistance(event);
                float delta = (getDistance(event) - mSubtitleBaseDist);// / STEP;
//                mSubtitleBaseDist = dist;
//                float deltaSp = Utils.pxToSp(activity, delta);
//                DLog.d("isPinchToZoomSubtitle", "dist : " + dist);
//                DLog.d("isPinchToZoomSubtitle", "delta : " + delta);
//                DLog.d("isPinchToZoomSubtitle", "deltaSp : " + deltaSp);
                resizeSubtitleFontSize(delta > 0, Constant.PLAYER.SETTING.SUBTITLE_FONT_SIZE_ADJUST_BY_PINCH);
                lastTimeZoomingSubtitle = System.currentTimeMillis();
            }
            return true;
        } else if (System.currentTimeMillis() - lastTimeZoomingSubtitle > DELAY_FINISH_ZOOMING_SUBTITLE){
            isZoomingSubtitle = false;
        }
        return false;
    }

    int getDistance(MotionEvent event) {
        int dx = (int) (event.getX(0) - event.getX(1));
        int dy = (int) (event.getY(0) - event.getY(1));
        return (int) (Math.sqrt(dx * dx + dy * dy));
    }
}