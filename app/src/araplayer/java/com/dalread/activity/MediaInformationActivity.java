package com.dalread.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.dalread.AraPlayerApplication;
import com.dalread.R;
import com.dalread.adapter.StudyChatAdapter;
import com.dalread.base.BaseDialog;
import com.dalread.base.BaseDialogListener;
import com.dalread.base.BasePlayerActivity;
import com.dalread.base.EnumType;
import com.dalread.component.Toolbar;
import com.dalread.composition.VocaKnowActivity;
import com.dalread.database.DownloadModelQuery;
import com.dalread.database.VideoModelQuery;
import com.dalread.database.VideoSeasonModelQuery;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.ActivityVideoInformationBinding;
import com.dalread.dialog.PlayerVideoInformationMenuDialog;
import com.dalread.dialog.RecyclerViewDialog;
import com.dalread.dialog.TypeInputDialog;
import com.dalread.dialog.YesNoDialog;
import com.dalread.dialog.ZoomedPhotoDialog;
import com.dalread.helper.point.AraPlayerPointHelper;
import com.dalread.helper.point.BasePlayerPointHelper;
import com.dalread.listener.OnAsyncTaskListenerWithType;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.listener.OnYesNoClickListener;
import com.dalread.model.DownloadModel;
import com.dalread.model.PlayerFileModel;
import com.dalread.model.VideoInformationModel;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.AbstractPointUtil;
import com.dalread.util.DialogUtil;
import com.dalread.util.GlobalActivityRequestCodeUtil;
import com.dalread.util.Constant;
import com.dalread.util.CustomTranslate;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.GuideUtil;
import com.dalread.util.Loading;
import com.dalread.util.NumberUtil;
import com.dalread.util.PointUtil;
import com.dalread.util.StorageUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.SubtitleFormatDetector;
import com.dalread.util.SupportSubtitleFormat;
import com.dalread.util.TimeUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.VideoUtil;
import com.dalread.util.Voca;
import com.dalread.util.arasubtitle.AbstractTranslateFileService;
import com.dalread.util.arasubtitle.MOVIE_ASSService;
import com.dalread.util.arasubtitle.MOVIE_BracketSubtitleService;
import com.dalread.util.arasubtitle.MOVIE_SMIService;
import com.dalread.util.arasubtitle.MOVIE_SQLITEService;
import com.dalread.util.arasubtitle.MOVIE_SRTService;
import com.dalread.util.SubtitleAnalyzer;

import org.apache.commons.io.FileUtils;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.OnClick;
import okhttp3.ResponseBody;

public class MediaInformationActivity extends BasePlayerActivity implements View.OnClickListener, OnAsyncTaskListenerWithType {
    protected final int TYPE_PARSE_SUBTITLE = 0;
    protected final int TYPE_REFRESH_SUBTITLE = TYPE_PARSE_SUBTITLE + 1;
    protected final int TYPE_GET_VIDEO_INFORMATION_FROM_WEB = TYPE_REFRESH_SUBTITLE + 1;

    protected VideoInformationModel videoInformationModel;
    protected final int REQUEST_SEARCH_VIDEO = 1000; //BasePlayerActivity's REQUEST_QUIZ_SCREEN is also 999, so I changed REQUEST_SEARCH_VIDEO to 1000.
    protected final int REQUEST_THUMBNAIL_LIST = REQUEST_SEARCH_VIDEO + 1;
    protected final int REQUEST_OPTION_SCREEN = REQUEST_THUMBNAIL_LIST + 1;


    protected final long INTERSTITIAL_AD_AUTO_CLOSE_TIME = 5000L;
    protected final int INTERSTITIAL_AD_AUTO_CLOSE_MESSAGE = 1;

    protected int searchCount = 0;
    protected double heightRatioByWidth = (double)9/16;
    protected String[] arraySearchName;
    protected boolean isReload = false;
//    protected boolean isSearchVideo = false;
    protected boolean isUseTMDBImage = false;
//    private CastProfileAdapter castProfileAdapter;
    protected TypeInputDialog typeInputDialog;
    protected enum EditDialogType{
        MEMO, TTS_TITLE, DISPLAY_TITLE, TTS_ARTIST, ARTIST, ALBUM;
    }
    protected EditDialogType editDialogType;
    private boolean isRefreshPoint = true; //배터리 최적화를 하고 돌아오면 포인트를 리프레쉬 하면 안된다. 앱 설치시 가이드 안내후 광고 보기 버튼을 그냥 두고 싶은데 포인트를 리프레쉬 하면 광고보기가 날라간다.
    protected RecyclerViewDialog recyclerWordListViewDialog;
    protected List<VocaStudyChat> vocaStudyChatListNotRatedOnly;
    protected StudyChatAdapter studyChatAdapter;
    private PointUtil pointUtil;
    public AraPlayerPointHelper helper;
    protected ActivityVideoInformationBinding binding;
//    protected InterstitialAd mInterstitialAd;
    protected boolean isAdShowing = false;
    protected boolean isAnalyzing = false;
    protected boolean isFirstUpdateUI = true;
    protected Handler adHandler;
    protected boolean isVideoFromNetwork = false;
    protected VocaKnowActivity vocaKnowActivity;

    @Override
    protected View getContentView() {
        binding = ActivityVideoInformationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        return view;
    }

    @Override
    protected Toolbar getToolbar() {
        return binding.header;
    }

    @Override
    protected void setFullscreen() {

    }

    @Override
    public void initView() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        initEventBus();
        initOnClickListener();
        typeInputDialog = new TypeInputDialog(this, onTypeInputListenerForMemo);
       resizeBackdropImageView();
       //Todo : call initData at same place (video app and music app). Should call initData for Music app here.
       if (FileUtil.isMusicApp())
            initData();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void resizeBackdropImageView() {
        binding.ivVideoBackdropImage.post(() -> {
            binding.ivVideoBackdropImage.getLayoutParams().height = (int) (binding.ivVideoBackdropImage.getWidth() * heightRatioByWidth) + getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            binding.ivVideoBackdropImage.requestLayout();
            //Trying to show UI after drawing ivVideoBackdropImage
            //Todo : call initData at same place (video app and music app). Should call initData for Video app here.
            if (FileUtil.isVideoApp())
                initData();

            bounceAnimationOnPlayButton();
        });
    }




    @Override
    public void initData() {
        playerFileModel = getIntent().getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        isVideoFromNetwork = getIntent().getBooleanExtra(Constant.PLAYER.INTENT.KEY_IS_VIDEO_FROM_NETWORK, false);
        createSubDatabase(playerFileModel);
//        initInterstitialAd();
        vocaKnowActivity = new VocaKnowActivity(this, this.getSubDatabase());
        binding.tvRemainPoint.setOnClickListener(v -> {
            this.startActivity(new Intent(this, InAppPointListActivity.class));
        });
    }

    @Override
    public void onBackPressed() {
        eventBus.post(new SuccessEvent(BaseEvent.Screen.VIDEO_INFORMATION, BaseEvent.EventType.PLAYER_RELOAD, null));
        super.onBackPressed();
    }

    @Override
    public void onHeaderLeftClick() {
        onBackPressed();
    }
    @Override
    public void onHeaderLeft2Click() {
    }
    @Override
    public void onHeaderRightClick() {

    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {
        openVideoInformationMenuDialog();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pointUtil = new PointUtil(this);
        Utils.checkAndAskIgnoreBatteryOptimization(this, new Utils.BatteryOptimizationCallback() {
            @Override
            public void onYes() {
                //Yes를 누르면 시스템 팝업이 떠서 배터리 최적화 할건지 말지 물어보니, 여기서는 아무것도 안하고, onActivityResult에서 Request코드를 봐서 showGuidePointDeduction()를 실행한다.
            }

            @Override
            public void onNo() {
                startShowGuide();
            }
        });
        helper = new AraPlayerPointHelper(this);
//        sharedPreferences.setPointMultiPlayer(3);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isReload) {
            playerFileModel.setVideoModel(VideoModelQuery.getByPath(Voca.getRealm(), playerFileModel.getPath()));
            VideoSeasonModelQuery.addOrUpdate(Voca.getRealm(), playerFileModel);
            createSubDatabase(playerFileModel);
        }
        if (isRefreshPoint)
            refreshRemainPoint(); //보상형 광고를 보고 돌아오면 추가된 점수를 보여주기 위해서.
//        new Handler().postDelayed(() -> {
//            showGuidePointDeduction();
//        }, 1000);
    }

    protected void updatePlayButtonIcon() {
        binding.msgBtnPlay.setVisibility(View.INVISIBLE);
        if (isHasSubRuby()) {
            binding.btnPlay.setImageResource(R.drawable.ic_play_circle_gray);
        } else {
            if ((playerFileModel == null) || (Utils.isEmpty(playerFileModel.getSubPath1())) || FileUtil.isTxtFormat(playerFileModel.getSubPath())) {
                binding.btnPlay.setImageResource(R.drawable.ic_play_circle_gray);
            } else {
                binding.btnPlay.setImageResource(R.drawable.ic_play_circle_green);
                binding.msgBtnPlay.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPTION_SCREEN) {
            switch (resultCode) {
                case RESULT_OK:
                    isReload = true;
                    playerFileModel = data.getParcelableExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
                    break;
            }
        } else if (requestCode == GlobalActivityRequestCodeUtil.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            isRefreshPoint = false;
            startShowGuide();
        }
    }

//    @OnClick({R.id.iv_bookmark, R.id.iv_tmdb_logo, R.id.btn_play, R.id.ll_watch, R.id.llAnalyzeAgain, R.id.iv_trailer, R.id.tvKnowDescription,
//            R.id.iv_poster, R.id.iv_season_poster, R.id.iv_video_backdrop_image, R.id.iv_video_backdrop_image_small,
//            R.id.tl_information, R.id.tvTitleFull, R.id.tvCast, R.id.tv_memo,
//            R.id.tvTtsTitle, R.id.tvDisplayTitle, R.id.tvTtsArtist, R.id.tvArtist, R.id.tvAlbum
//    })
    private void initOnClickListener() {
        binding.ivBookmark.setOnClickListener(this);
        binding.tvKnowDescription.setOnClickListener(this);
        binding.btnPlay.setOnClickListener(this);
        binding.llAnalyzeAgain.setOnClickListener(this);
        binding.tvTitleFull.setOnClickListener(this);
        binding.tvMemo.setOnClickListener(this);
        binding.ivVideoBackdropImage.setOnClickListener(this);
        binding.btnWatchRewardedAd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_bookmark:
                onBookmark();
                break;
            case R.id.tvKnowDescription:
                openWordsList();
                break;
            case R.id.btn_play:
//            case R.id.ll_watch:
                onHasSQLite(TYPE_PARSE_SUBTITLE);
                break;
            case R.id.llAnalyzeAgain:
                onHasSubtitleFile(TYPE_REFRESH_SUBTITLE);
                break;
            case R.id.tvTitleFull:
                Utils.copyToClipboard(this, binding.tvTitleFull.getText().toString(), R.string.copied);
                break;
            case R.id.tv_memo:
                editDialogType = EditDialogType.MEMO;
                showTypeInputDialog();
                break;
            case R.id.ivVideoBackdropImage:
                showZoomImageOfLastDurationInVideo(v);
                break;
            case R.id.btnWatchRewardedAd:
                watchRewardedAd();
                break;
        }
    }
//@OnClick({R.id.iv_bookmark, R.id.btn_play, R.id.llAnalyzeAgain, R.id.tvKnowDescription,
//        R.id.ivVideoBackdropImage, R.id.tvTitleFull, R.id.tv_memo, R.id.btnWatchRewardedAd
//})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.iv_bookmark:
//                onBookmark();
//                break;
//            case R.id.tvKnowDescription:
//                openWordsList();
//                break;
//            case R.id.btn_play:
////            case R.id.ll_watch:
//                onHasSQLite(TYPE_PARSE_SUBTITLE);
//                break;
//            case R.id.llAnalyzeAgain:
//                onHasSubtitleFile(TYPE_REFRESH_SUBTITLE);
//                break;
//            case R.id.tvTitleFull:
//                Utils.copyToClipboard(this, binding.tvTitleFull.getText().toString(), R.string.copied);
//                break;
//            case R.id.tv_memo:
//                editDialogType = EditDialogType.MEMO;
//                showTypeInputDialog();
//                break;
//            case R.id.ivVideoBackdropImage:
//                showZoomImageOfLastDurationInVideo(view);
//                break;
//            case R.id.btnWatchRewardedAd:
//                watchRewardedAd();
//                break;
//        }
//    }

    protected void showThumbnailFromPath(String path, Drawable thumbnailDrawable) {
        ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(this, playerFileModel);
        zoomedPhotoDialog.loadThumbnailFromPath(path, thumbnailDrawable);
    }

    private void showZoomImageOfLastDurationInVideo(View imageView) {
        if (sharedPreferences.isFirstShowVideoBackDropImage()) {
            sharedPreferences.setFirstShowVideoBackDropImage();
            DialogUtil.showPositiveDialog(this, R.string.info, R.string.dialog_message_first_show_back_drop_image, R.string.ok, () -> {
                showZoomImageOfLastDurationInVideoMain(imageView);
            });
        } else {
            showZoomImageOfLastDurationInVideoMain(imageView);
        }
    }

    private void showZoomImageOfLastDurationInVideoMain(View view) {
        ZoomedPhotoDialog zoomedPhotoDialog = new ZoomedPhotoDialog(this, playerFileModel);
        zoomedPhotoDialog.loadThumbnailFromFileAndShow(playerFileModel, ((ImageView) view).getDrawable());
    }

    protected void showZoomImageOfBackDrop() {
        if ((playerFileModel == null) || (playerFileModel.getVideoModel() == null) || (videoInformationModel == null))
            return;

        String imagePath = "";
        if (!Utils.isEmpty(playerFileModel.getVideoModel().getTmdbEpisodePath())) {
            imagePath = playerFileModel.getVideoModel().getImagePathOriginalSizeOnTmdbWebsite(playerFileModel.getVideoModel().getTmdbEpisodePath());
        } else if (!Utils.isEmpty(videoInformationModel.getBackdropPath())) {
            imagePath = videoInformationModel.getBackdropURLOriginalSize();
        }
        showThumbnailFromPath(imagePath, binding.ivVideoBackdropImageSmall.getDrawable());
    }

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.EDIT_SUBTITLE) {
            switch (event.getEventType()) {
                case PLAYER_EDIT_SUBTITLE:
                    isReload = true; //(boolean) event.getModel();
                    break;
            }
        } else if (event.getScreen() == BaseEvent.Screen.WORD_LIST) {
            switch (event.getEventType()) {
                case DATA_CHANGED:
                    isReload = true;
                    break;
            }
        }
    }

    @Override
    public void onInitAsyncTask() {
        DLog.d("", "onInitAsyncTask");

    }

    @Override
    public void onInitAsyncTask(int searchType) {
        DLog.d("", "onInitAsyncTask searchType");
        switch (searchType) {
            case TYPE_GET_VIDEO_INFORMATION_FROM_WEB:
                Loading.show(this);
                break;
            case TYPE_PARSE_SUBTITLE:
            case TYPE_REFRESH_SUBTITLE:
                isAnalyzing = true;
                Loading.show(this, R.string.msg_analyzing_title, R.string.msg_analyzing_message);
                break;
        }
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_GET_VIDEO_INFORMATION_FROM_WEB:
                checkSearchVideoInformationByFilename();
                break;
            case TYPE_PARSE_SUBTITLE:
            case TYPE_REFRESH_SUBTITLE:
                return parserSubtitleFile(searchType);
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
//            case TYPE_GET_VIDEO_INFORMATION:
//                break;
            case TYPE_PARSE_SUBTITLE:
            case TYPE_REFRESH_SUBTITLE:
                makeRubyTextFromServer(resultData);
                break;
        }
    }

    protected void checkSearchVideoInformationByFilename() {

    }

    protected void resetUI() {
        binding.ivVideoBackdropImage.setImageBitmap(null);
    }

    @SuppressLint("StringFormatInvalid")
    protected void updateUI() {
        if (isDestroyed() || isFinishing()) return;

        searchCount = 0;
        runOnUiThread(() -> {
            if (VideoUtil.isNeedToSetVideoDurationWhenZero(playerFileModel)) {
                VideoUtil.setVideoDurationWhenZero(playerFileModel.getVideoModel());
            }

            String nameDisplay = playerFileModel.getName();
            binding.tvTitleFull.setText(nameDisplay);
            binding.scrollView.setVisibility(View.VISIBLE);
            if (isHasSubRuby()) {
                binding.llAnalyzeAgain.setVisibility(View.VISIBLE);
//                binding.tvWatch.setText(R.string.watch_now);
            } else {
                binding.llAnalyzeAgain.setVisibility(View.GONE);
//                binding.tvWatch.setText(playerFileModel.getVideoModel().hasSubPath1() ? R.string.analyze_and_watch_now : R.string.watch_now);
            }

            final int knowAll = playerFileModel.getVideoModel() != null ? playerFileModel.getVideoModel().getVocaKnowAll() : 0;
            if (knowAll > 0) {
                // SQLite
                final int knowCount = playerFileModel.getVideoModel().getVocaKnowCount();
                Voca.updateTextViewDifficultLabelForSubtitle(this, binding.tvDifficult, knowCount, knowAll);
                binding.tvKnowDescription.setText(getString(R.string.video_information_msg_you_know_words,
                        NumberUtil.formatNumber(knowCount), NumberUtil.formatNumber(knowAll), NumberUtil.percentageString(knowCount, knowAll)));
                binding.tvDifficult.setVisibility(View.VISIBLE);
                binding.tvKnowDescription.setVisibility(View.VISIBLE);
            } else {
                binding.tvDifficult.setVisibility(View.GONE);
                binding.tvKnowDescription.setVisibility(View.GONE);
            }

            binding.tvNeedToAnalyzeAgain.setVisibility(View.GONE);
            if (playerFileModel.getVideoModel() != null) {
                Voca.updateTextViewSubtitleExtension(this, binding.tvSubtitleExt, playerFileModel.getVideoModel().getSubPath1());
                Voca.updateTextViewSubtitleExtension(this, binding.tvSubtitleExt2, playerFileModel.getVideoModel().getSubPath2());
                binding.tvDate.setVisibility(Utils.isEmpty(playerFileModel.getVideoModel().getSubPath2()) ? View.VISIBLE : View.GONE);
                binding.ivBookmark.setSelected(playerFileModel.getVideoModel().isBookmark());
                if (playerFileModel.getVideoModel().isAnalyzeAgain() && isHasSubRuby()) {
                    binding.tvNeedToAnalyzeAgain.setVisibility(View.VISIBLE);
                }
                binding.tvMemo.setVisibility(Utils.isEmpty(playerFileModel.getVideoModel().getMemo()) ? View.GONE : View.VISIBLE);
                binding.tvMemo.setText(playerFileModel.getVideoModel().getMemo());
            } else {
                binding.tvSubtitleExt.setVisibility(View.GONE);
                binding.ivBookmark.setVisibility(View.GONE);
            }

            binding.tvSize.setText(StorageUtil.getDynamicSpace(playerFileModel.getSize()));
            binding.tvDate.setText(DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.SHORT).format(playerFileModel.getCreatedDate())); //https://docs.oracle.com/javase/tutorial/i18n/format/dateFormat.html

            resetUI();
            binding.tvVideoDuration.setText(playerFileModel.isDuration() ? TimeUtil.getDisplay(playerFileModel.getDuration()) : Constant.BASE_BLANK);
            updatePlayButtonIcon();
        });
    }

    protected String getInputDialogInputValue() {
        String result = "";
        if (editDialogType == EditDialogType.MEMO) {
            result = getMemoFromDB();
        }
        return result;
    }

    protected String getMemoFromDB() {
        return playerFileModel.getVideoModel().getMemo();
    }


    protected void bounceAnimationOnPlayButton() {
        //Don't delete this code.
//        if (isFirstUpdateUI) {
//            final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
//            BounceInterpolator interpolator = new BounceInterpolator(0.7, 5);
//            myAnim.setInterpolator(interpolator);
//            binding.btnPlay.setVisibility(View.VISIBLE);
//            binding.btnPlay.startAnimation(myAnim);
//            isFirstUpdateUI = false;
//        }
    }

    private void onBookmark() {
        if (playerFileModel.getVideoModel().isBookmark() == true) {
            binding.ivBookmark.setSelected(false);
            playerFileModel.getVideoModel().setBookmark(Constant.INT_BOOLEAN.FASLE);
        } else {
            binding.ivBookmark.setSelected(true);
            playerFileModel.getVideoModel().setBookmark(Constant.INT_BOOLEAN.TRUE);
        }
        updateVideoModel(playerFileModel);
    }

    private void onHasSQLite(int type) {
        if (!isHasSubRuby() && pointUtil.needToShowFullAd()) {
            final YesNoDialog dialog = new YesNoDialog(this,
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
//            ToastUtil.getInstance(this).show(R.string.msg_warning_not_enough_point);
        } else {
            if (askToLogInByPlayMediaCount()) {
                alertDialog.showLogInRequired();
                return;
            }

            if (isHasSubRuby()) {
                openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
//            openDalPlayer();
            } else {
                onHasSubtitleFile(type);
            }
        }
    }

    private void onHasSubtitleFile(int type) {
        if (pointUtil.needToShowFullAd()) {
            ToastUtil.getInstance(this).show(R.string.msg_warning_not_enough_point_watch_ads_get_points);
            return;
        }
        checkAndCreateVideoModel();

        if (openMediaWithoutAnalzing()) {
            openDalPlayer();
        } else {
//            if (NetworkUtil.isNetworkConnetedIfNotShowWarningAsToast(this)) {
                callAsyncTaskPlay(type, true);
//                showInterstitialAd();
//            }
        }
    }

    private boolean openMediaWithoutAnalzing() {
        return Utils.isEmpty(playerFileModel.getSubPath1()) && !isHasSubRuby() || FileUtil.isTxtFormat(playerFileModel.getSubPath());
    }

    private void checkAndCreateVideoModel() {
//        VideoModel videoModel = VideoModelQuery.getByPath(Voca.getRealm(), playerFileModel.getPath());
//        if (videoModel == null) {
//            videoModel = new VideoModel(playerFileModel.getPath());
//        }
    }

    private void openDalPlayer() {
        //Don't delete this code. Sometimes openDalPlayer is never called again when Ads is related.
        if (isAnalyzing || isAdShowing) return;
//        closeInterstitialAd();
        if (!isVideoFromNetwork) {
            File f = new File(playerFileModel.getPath());
            if(!f.exists()) {
                ToastUtil.getInstance(this).show(R.string.file_is_not_existed);
                return;
            }
        }

        increaseCountOfPlayMedia();
        DLog.d(getLogTag(), "openDalPlayer");
        isReload = true;
        playerFileModel.getVideoModel().setNewFile(Constant.INT_BOOLEAN.FASLE);
        updateVideoModel(playerFileModel);
        eventBus.post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.PLAYER_RELOAD_VIDEO, playerFileModel.getVideoModel()));
        startPlayer();
    }

    private void increaseCountOfPlayMedia() {
        int countOfWatchVideo = sharedPreferences.getCountOfWatchVideo();
        sharedPreferences.setCountOfWatchVideo(countOfWatchVideo + 1);
    }

    private boolean askToLogInByPlayMediaCount() {
        //Don't delete this. I'll use this later.
//        int countOfWatchVideo = sharedPreferences.getCountOfWatchVideo();
//        int maxCountOfWatchVideoToShowLogInPopup = 10;
//        if (countOfWatchVideo >= maxCountOfWatchVideoToShowLogInPopup) {
//            return UserUtil.isDebugOrAdminUser(this) ? false : true;
//        }

        return false;
    }

    private void startPlayer() {
        if (!isHasSubRuby()) {
            helper.consumePoint(BasePlayerPointHelper.consumePoint1);
        }
        isRefreshPoint = true;
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        Loading.hide();
        startActivity(intent);
    }

    private void callAsyncTaskPlay(int type, boolean isLoading) {
        callAsyncTask(this, type, isLoading);
    }

    private void confirmDownloadNetwork(PlayerFileModel data) {
        if (!Utils.hasWifiConnected(this)) {
            final YesNoDialog dialog = new YesNoDialog(this,
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
        DLog.d(getLogTag(), "checkDownloadFile - file=" + data.toString());
        File localFile = new File(StorageUtil.getVideoPath(this), data.getPath());
        final DownloadModel downloadModel = DownloadModelQuery.getById(Voca.getRealm(), data.getPath());
        if (downloadModel != null || localFile.length() > 0) {
            showDownloadWarningSameFileDialog(data);
        } else {
            downloadFile(data);
        }
    }

    private void showDownloadWarningSameFileDialog(Object data) {
        final YesNoDialog dialog = new YesNoDialog(this,
                R.string.warning,
                R.string.msg_download_overwrite_same_file,
                data,
                onDownloadWarningSameFileListener);
        dialog.show();
    }

    private OnYesNoClickListener onDownloadWarningSameFileListener = new OnYesNoClickListener() {
        @Override
        public void onYesClick(View view, Object object) {
            downloadFile(object);
        }

        @Override
        public void onNoClick(View view, Object object) {
        }
    };

    private void downloadFile(Object data) {
        final PlayerFileModel file = (PlayerFileModel) data;
        final DownloadModel checkSameModel = DownloadModelQuery.getById(Voca.getRealm(), file.getPath());
        // set currentSize is 0 when downloadModel is exist and isOverwrite is true
        if (checkSameModel != null) {
            if (!checkSameModel.isCompleted()) {
                checkSameModel.setStatus(Constant.PLAYER.SERVER.DOWNLOAD.STATUS.WAIT);
                DownloadModelQuery.update(Voca.getRealm(), checkSameModel);
//                addDownload(checkSameModel);
            }
            return;
        }
        final DownloadModel model = new DownloadModel();
        model.setId(file.getPath());
        model.setName(file.getName());
        model.setPath(file.getPath());
        model.setIdServer(playerFileModel.getServerModel().getId());
        model.setSize(file.getSize());
        DownloadModelQuery.add(Voca.getRealm(), model);
//        addDownload(model);
    }

    private Object parserSubtitleFile(int type) {
        if (type == TYPE_PARSE_SUBTITLE) {
            return new File(playerFileModel.getVideoModel().getSubPath1());
        }
        return new File(StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(this, playerFileModel.getPath(), playerFileModel));
    }

    private void makeRubyTextFromServer(Object resultData) {
        if (resultData == null) {
            isAnalyzing = false;
            openDalPlayer();
            return;
        }
        //Don't check the subtitle extension, just call the makeRubyTextFromSubtitle API
//        makeRubyTextFromSubtitle(resultData);
        makeRubyTextFromSubtitleOnLocal(resultData);
    }

    private void makeRubyTextFromSubtitleOnLocal(Object resultData) {
        SubtitleAnalyzer subtitleAnalyzer = new SubtitleAnalyzer(
            this, dicDatabase, subDatabase, playerFileModel);
        
        subtitleAnalyzer.analyzeSubtitle(resultData, new SubtitleAnalyzer.OnAnalysisCompleteListener() {
            @Override
            public void onAnalysisStarted() {
                Loading.show(MediaInformationActivity.this, R.string.msg_analyzing_title, R.string.msg_analyzing_message);
            }

            @Override
            public void onAnalysisSuccess(AbstractTranslateFileService fileService) {
                playerFileModel.getVideoModel().setAnalyzeAgain(Constant.INT_BOOLEAN.FASLE); //Not to display analyze again warning text message
                updateVideoModel(playerFileModel);
                if (fileService instanceof MOVIE_SQLITEService) {
                    helper.consumePoint(pointUtil.getPointToAnalyzeSubtitleAgain());
                } else {
                    helper.consumePoint(pointUtil.getPointToAnalyzeSubtitle());
                }

                Loading.hide();
                openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
                subtitleAnalyzer.shutdown();
            }

            @Override
            public void onAnalysisError(String errorMessage) {
                subtitleAnalyzer.shutdown();
            }
        });
    }
    //이건 서버로 부터 자막 분석하는것임. 로컬에서 테스트용으로 사용하니 지우지 말것.
    private void makeRubyTextFromSubtitle(Object content) {
        ((AraPlayerApplication) application).getAraPlayerApiImpl().makeRubyTextFromSubtitle(this,
                playerFileModel,
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
        File file = StorageUtil.writeResponseBodyToDisk(this, response);
        String subtitleDatabasePath = StorageUtil.generateSubtitleSQLitePathUnderAndroidFolder(this, playerFileModel.getPath(), playerFileModel);
        String path = StorageUtil.getFilesStoragePath(this, playerFileModel);
        StorageUtil.unzip(this, file, new File(path), subtitleDatabasePath);
        // check sub database
        createSubDatabase(playerFileModel);
        checkAndCreateVideoModel();
        if (playerFileModel.getVideoModel().getTongueLang() != motherTongueLanguage.getIdApi()) {
            playerFileModel.getVideoModel().setTongueLang(motherTongueLanguage.getIdApi());
            updateVideoModel(playerFileModel);
        }
        if (isHasSubRuby()) {
            playerFileModel.getVideoModel().setAnalyzeAgain(Constant.INT_BOOLEAN.FASLE); //Not to display analyze again warning text message
            updateVideoModel(playerFileModel);
            // check translator subtitle when no translation automatically & study language is EN & translator subtitle is ON
            if (sharedPreferences.getTranslateSubtitleFromServer()) {
                ArrayList<DicModel> list = new ArrayList<>();
                list.addAll(getSubDatabase().getNoTranslationSubtitleDialogListByLanguage());
                if (Utils.isEmptyCollection(list)) {
                    openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
                } else {
                    CustomTranslate customTranslate = new CustomTranslate(this);
                    for (DicModel item : list) {
                        customTranslate.translateText(item.getVocaDisplay(), (view, object) -> {
                            DLog.d(getLogTag(), item.getId() + " - " + item.getVocaDisplay() + " translate to =" + object);
                            item.setMeaning((String) object);
                            getSubDatabase().updateTranslate(item);
                            if (item == list.get(list.size() - 1)) {
                                openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
                            }
                        });
                    }
                }
            } else {
                openNotRatedOnlyWordsListPopupViewBeforePlayVideo();
            }
        } else {
            ToastUtil.getInstance(this).show(R.string.error_msg_parser_sub_database_title);
            Loading.hide();
        }
    }

    private void parserSubtitleError() {
//        ToastUtil.getInstance(this).show(R.string.error_msg_parser_sub_title);
        Loading.hide();
        //아래는 자막 분석이 실패해도 플레이어를 열수 있게 해준다.
        isAnalyzing = false;
        openDalPlayer();
    }

    private void openVideoInformationMenuDialog() {
        final PlayerVideoInformationMenuDialog dialog = new PlayerVideoInformationMenuDialog(this, playerFileModel, videoInformationModel, (view, object) -> {
            switch (view.getId()) {
                case R.id.tvChooseMetadata:
                    openVideoSearchScreen();
                    break;
                case R.id.tv_clear_metadata:
                    onClearMetadata();
                    break;
                case R.id.tv_word_list:
                    openWordsList();
                    break;
                case R.id.tv_subtitle_list:
                    openDialogList();
                    break;
                case R.id.llEditSubtitle:
                    openEditSubtitle();
                    break;
                case R.id.tv_bookmark_list:
                    openBookmarkList();
                    break;
                case R.id.tv_thumbnail_list:
                    openThumbnailListScreen();
                    break;
                case R.id.tv_quiz:
                    openQuizPlayerScreenWithLocalData();
                    break;
                case R.id.ll_memo:
                    editDialogType = EditDialogType.MEMO;
                    showTypeInputDialog();
                    break;
                case R.id.llTtsTitle:
                    editDialogType = EditDialogType.TTS_TITLE;
                    showTypeInputDialog();
                    break;
                case R.id.llDisplayTitle:
                    editDialogType = EditDialogType.DISPLAY_TITLE;
                    showTypeInputDialog();
                    break;
                case R.id.llTtsArtist:
                    editDialogType = EditDialogType.TTS_ARTIST;
                    showTypeInputDialog();
                    break;
                case R.id.llArtist:
                    editDialogType = EditDialogType.ARTIST;
                    showTypeInputDialog();
                    break;
                case R.id.llAlbum:
                    editDialogType = EditDialogType.ALBUM;
                    showTypeInputDialog();
                    break;
                case R.id.tv_option:
                    openOptionPlayer();
                    break;
            }
        });
        dialog.show();
    }

    private void openVideoSearchScreen() {
        Intent intent = new Intent(this, VideoSearchActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivityForResult(intent, REQUEST_SEARCH_VIDEO);
    }

    private void openVideoCreditsScreen(VideoInformationModel data) {
        //Will delete VideoCreditsActivity later.
//        Intent intent = new Intent(this, VideoCreditsActivity.class);
//        intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, data);
//        startActivity(intent);
    }

    private String generateInformation(List<VideoInformationModel.People> items) {
        return generateInformation(items, -1);
    }

    private String generateInformation(List<VideoInformationModel.People> items, int maxCount) {
        int count = 0;
        String content = Constant.BASE_BLANK;
        for (VideoInformationModel.People p : items) {
            content = StringUtils.addString(content, p.getName());
            count++;
            if (maxCount > 0 && count >= maxCount)
                break;
        }
        return content;
    }

    private void openOptionPlayer() {
        isReload = true;
        Intent intent = new Intent(this, OptionPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivityForResult(intent, REQUEST_OPTION_SCREEN);
    }

    private void openWordsList() {
        openWordsList(true);
    }

    private void openWordsList(boolean isWordList) {
        openNewScreen(
                WordListPlayerActivity.createIntent(this, playerFileModel)
        );
    }

    private void openDialogList() {
        Intent intent = new Intent(this, DialogueListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivity(intent);
    }

    private void openBookmarkList() {
        Intent intent = new Intent(this, BookmarkListActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivity(intent);
    }

    private void openThumbnailListScreen() {
        Intent intent = new Intent(this, ThumbnailListPlayerActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        startActivityForResult(intent, REQUEST_THUMBNAIL_LIST);
    }

    private List<VideoInformationModel.People> generateCastProfileData() {
        final List<VideoInformationModel.People> list = new ArrayList<>();
        if (videoInformationModel != null) {
            if (!videoInformationModel.getCredits().getCast().isEmpty()) {
                list.addAll(videoInformationModel.getCredits().getCast());
            }
            if (!videoInformationModel.getCredits().getGuestStars().isEmpty()) {
                list.addAll(videoInformationModel.getCredits().getGuestStars());
            }
        }
        return list;
    }

//    private OnClickListener onCastProfileClickListener = (view, object) -> {
//        final VideoInformationModel.People item = (VideoInformationModel.People) object;
//        switch (view.getId()) {
//            case R.id.izb_profile:
//                if (!Utils.isEmpty(item.getPath())) {
////                    binding.ivZoomedPhoto.setVisibility(View.VISIBLE);
////                    UtilImage.getThumbnailFullsize(this, binding.ivZoomedPhoto, item.getOriginalSizeImagePath());
//                    showThumbnailFromPath(item.getOriginalSizeImagePath(), ((ImageView) view).getDrawable());
//                }
//                break;
//            case R.id.ll_cast_name:
//            case R.id.tv_real_name:
//            case R.id.tv_character_name:
//                Utils.openWeb(this, item.getPersonURL());
//                break;
//        }
//    };

    private void onClearMetadata() {
        // delete files
        try {
            FileUtils.deleteDirectory(new File(StorageUtil.getTMDBPath(this, videoInformationModel)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        playerFileModel.getVideoModel().clearTMDB();
        updateVideoModel(playerFileModel);
        videoInformationModel = null;
        updateUI();
    }

    private void openEditSubtitle() {
        final List<DicModel> subtitleDialogModels = getSubDatabase().getSubtitleDialogList();
        String ids = subtitleDialogModels.stream().map(e -> e.getSubtitleWordlistId()).collect(Collectors.joining(","));
        Intent intent = new Intent(this, EditSubtitleActivity.class);
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, playerFileModel);
        if (ids.length() > 0) {
            intent.putExtra(Constant.PLAYER.INTENT.KEY_DATA, ids);
        }
        startActivity(intent);
    }

    private BaseDialogListener onTypeInputListenerForMemo = new BaseDialogListener() {

        @Override
        public void onBaseDialogListenerShow(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            ToastUtil.getInstance(MediaInformationActivity.this).show("need to show the keyboard");
        }

        @Override
        public void onBaseDialogListenerOk(EnumType type, BaseDialog dialog, View v, int position, Object data) {
            if (data instanceof String) {
                String value = (String) data;
                if (editDialogType == EditDialogType.MEMO) {
                    binding.tvMemo.setText(value);
                    binding.tvMemo.setVisibility(View.VISIBLE);
                    playerFileModel.getVideoModel().setMemo(value);
                } else {
                    updateMediaInfoValue(value);
                }
                updateVideoModel(playerFileModel);
            }
            typeInputDialog.dismiss();
        }

        @Override
        public void onBaseDialogListenerCancel(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClick(EnumType type, BaseDialog dialog, View v, int position, Object data) {
        }

        @Override
        public void onBaseDialogListenerClickMulti(EnumType type, BaseDialog dialog, View v, int position, Object[] data) {
        }
    };

    protected void updateMediaInfoValue(String value) {

    }

    protected void showTypeInputDialog() {
        Utils.showSoftKeyboard(this);
        typeInputDialog.setTitle(R.string.info);
        typeInputDialog.setSubTitle(getInputDialogSubtitle());
        typeInputDialog.setInput(getInputDialogInputValue());
        typeInputDialog.show();
    }

    private int getInputDialogSubtitle() {
        int stringId = R.string.write_memo_for_video;
        if (editDialogType == EditDialogType.MEMO) {
            stringId = R.string.write_memo_for_video;
            if (FileUtil.isMusicApp())
                stringId = R.string.write_memo_for_music;
        } else if (editDialogType == EditDialogType.TTS_TITLE) {
            stringId = R.string.write_title_to_play_by_tts;
        } else if (editDialogType == EditDialogType.DISPLAY_TITLE) {
            stringId = R.string.write_title_to_display;
        } else if (editDialogType == EditDialogType.TTS_ARTIST) {
            stringId = R.string.write_artist_name_to_play_by_tts;
        } else if (editDialogType == EditDialogType.ARTIST) {
            stringId = R.string.write_artist_name;
        } else if (editDialogType == EditDialogType.ALBUM) {
            stringId = R.string.write_album_name;
        }
        return stringId;
    }

    private void openNotRatedOnlyWordsListPopupViewBeforePlayVideo() {
        isAnalyzing = false;
        vocaStudyChatListNotRatedOnly = this.getSubDatabase().getVocaStudyChatListNotRatedOnly();
        if (Utils.isEmptyCollection(vocaStudyChatListNotRatedOnly)) {
            openDalPlayer();
        } else {
            recyclerWordListViewDialog = new RecyclerViewDialog(this, (view, object) -> {
                if (Utils.isEmptyCollection(vocaStudyChatListNotRatedOnly)) {
                    openDalPlayer();
                } else {
                    Loading.hide();
                    updateUI();
                }
            });
            studyChatAdapter = new StudyChatAdapter(this);

            studyChatAdapter.setDataNoLoop(vocaStudyChatListNotRatedOnly);
            setRecyclerViewDataDialog(getString(R.string.title_not_rated_words_only_in_subtitle), studyChatAdapter, null);
        }
    }

    private void setRecyclerViewDataDialog(String title, StudyChatAdapter adapter, OnClickListener callback) {
        adapter.setDisplayPronunciation(false);//sharedPreferences.getDisplayPronunciation());
        adapter.setHighlightIndex(0);
        adapter.setHasHeader(true);
        adapter.setNotRatedOnlyWordsList(true);
        adapter.setListener(onVocaStudyChatClickListener);
        adapter.setDoubleClickListener(onVocaStudyChatDoubleClickListener);
        adapter.setOnDoubleClickListenerOnBaseVocaKnow(vocaKnowActivity.onDoubleClickListenerOnBaseVocaKnow);

        recyclerWordListViewDialog.setCanceledOnTouchOutside(false);
        recyclerWordListViewDialog.showCloseButton(false);
        recyclerWordListViewDialog.setAdapter(adapter, title);
        recyclerWordListViewDialog.show();
    }

    private OnVocaStudyChatClickListener onVocaStudyChatClickListener = new OnVocaStudyChatClickListener() {

        @Override
        public void onItemClick(VocaStudyChat voca) {

        }

        @Override
        public void onDoubleItemClick(VocaStudyChat voca) {
            removeVocaInList(voca);
        }

        @Override
        public void onPlayClick(final VocaStudyChat voca) {

        }

        @Override
        public void onBigIconClick(VocaStudyChat voca) {

        }

        @Override
        public void onAsteriskSentenceClick(final VocaStudyChat voca) {
        }

        @Override
        public void onVocaKnowClick(VocaStudyChat voca, int vocaKnow) {
            removeVocaInList(voca);
        }

        @Override
        public void onEvaluateGradeClick(VocaStudyChat voca, String grade) {

        }

        @Override
        public void onAnswerClick(VocaStudyChatExam voca) {

        }
    };

    private OnDoubleClickListener onVocaStudyChatDoubleClickListener = new OnDoubleClickListener() {
        @Override
        public void onClick(View view, Object data) {

        }

        @Override
        public void onDoubleClick(View view, Object data) {
            if (data instanceof VocaStudyChat) {
                VocaStudyChat voca = (VocaStudyChat) data;
//                onChangeVocaKnow(voca, null, VocaKnow.switchVocaKnow(voca.getVocaKnow()), true);
                removeVocaInList(voca);
            }
        }
    };

    private void removeVocaInList(VocaStudyChat voca) {
        vocaStudyChatListNotRatedOnly.remove(voca);
        if (Utils.isEmptyCollection(vocaStudyChatListNotRatedOnly)) {
            recyclerWordListViewDialog.dismiss();
        } else {
            studyChatAdapter.setDataNoLoop(vocaStudyChatListNotRatedOnly);
            studyChatAdapter.notifyDataSetChanged();
        }
    }

    private void showInterstitialAd() {
        //TODO : Don't delete this code. Will use later
//        Integer maxCountOfAnalyzeVideoToDisplayAds = 10;
//        Integer countOfAnalyzeVideo = sharedPreferences.getCountOfAnalyzeVideo();
//        if (countOfAnalyzeVideo > maxCountOfAnalyzeVideoToDisplayAds) {
//            isAdShowing = true;
//            if (mInterstitialAd.isLoaded()) {
//                mInterstitialAd.show();
//                adHandler.sendEmptyMessageDelayed(INTERSTITIAL_AD_AUTO_CLOSE_MESSAGE, INTERSTITIAL_AD_AUTO_CLOSE_TIME);
//            } else if (!mInterstitialAd.isLoaded() && !mInterstitialAd.isLoading()) {
//                mInterstitialAd.loadAd(new AdRequest.Builder().build());
//            }
//        } else {
//            sharedPreferences.setCountOfAnalyzeVideo(countOfAnalyzeVideo + 1);
//        }
    }
    private void watchRewardedAd() {
        binding.btnWatchRewardedAd.setVisibility(View.GONE);
        pointUtil.showRewardedAd(createRewardPointListener());
    }
    private PointUtil.OnRewardPointListener createRewardPointListener() {
        return new PointUtil.OnRewardPointListener() {
            @Override
            public void onSuccess() {
//                refreshRemainPoint();
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

    private void refreshRemainPoint() {
        int point = pointUtil.getPoint();
        new Handler().postDelayed(() -> {
//            showRewardButton();
            if (sharedPreferences.isPointAdded()) {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.text_scale_anim);
                binding.tvRemainPoint.startAnimation(animation);
                sharedPreferences.setPointAdded(false);
            }
            binding.tvRemainPoint.setText(getResources().getQuantityString(R.plurals.point, point, point));
        }, 1000);
    }

//    private void showRewardButton() {
//        if (sharedPreferences.isFirstShowGuidePointDeduction() || pointUtil.needToShowRewardButton()) {
//            binding.btnWatchRewardedAd.setVisibility(View.VISIBLE);
//        } else {
//            binding.btnWatchRewardedAd.setVisibility(View.GONE);
//        }
//    }

    private void startShowGuide() {
        showGuidePointDeduction();
    }
    private void showGuidePointDeduction() {
        if (sharedPreferences.isFirstShowGuidePointDeduction()) {
            sharedPreferences.setFirstShowGuidePointDeduction();
            String title = getString(R.string.guide_ara_player_point_deduction);
            this.runOnUiThread(() -> {
                GuideUtil.showGuideView(this, title, binding.tvRemainPoint, view -> showGuideWatchAd());
            });
        }
    }

    private void showGuideWatchAd() {
        if (sharedPreferences.isFirstShowGuideWatchAd()) {
            sharedPreferences.setFirstShowGuideWatchAd();
            String title = getString(R.string.guide_watch_ad_to_get_point);
            GuideUtil.showGuideView(this, title, binding.btnWatchRewardedAd, view -> showGuidePlayAndAnalyzeButton());
        }
    }

    private void showGuidePlayAndAnalyzeButton() {
        if (sharedPreferences.isFirstShowGuidePlayButton()) {
            sharedPreferences.setFirstShowGuidePlayButton();
//            showRewardButton(); 가이드가 끝나도 굳이 숨기지는 말자. 이뷰로 다시 들어오며 포인트 많으면 어짜비 숨겨진다.
            String title = getString(R.string.guide_video_play_and_analyze_button);
            GuideUtil.showGuideView(this, title, binding.btnPlay, view -> {});
        }
    }

//    private void initInterstitialAd() {
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId(MobileAd.getInterstateId(this));
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        mInterstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                if (isAnalyzing) {
//                    showInterstitialAd();
//                }
//            }
//
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//                isAdShowing = false;
//                adHandler.removeMessages(INTERSTITIAL_AD_AUTO_CLOSE_MESSAGE);
//            }
//        });
//
//        adHandler = new Handler(getMainLooper()) {
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == INTERSTITIAL_AD_AUTO_CLOSE_MESSAGE) {
//                    isAdShowing = false;
//                    if (!isAnalyzing) {
//                        closeInterstitialAd();
//                        openDalPlayer();
//                    }
//                }
//            }
//        };
//    }
//
//    private void closeInterstitialAd() {
//        Activity currentActivity = BaseApplication.getInstance().getCurrentActivity();
//        if (currentActivity instanceof AdActivity) {
//            isAdShowing = false;
//            currentActivity.finish();
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        }
//    }
}
