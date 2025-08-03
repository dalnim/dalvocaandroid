package com.dalread.activity;

import android.content.res.Configuration;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.UtteranceProgressListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.adapter.SubtitleMeaningAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerFragment;
import com.dalread.component.CenterLayoutManager;
import com.dalread.component.FuriganaView;
import com.dalread.component.SeparatorDecoration;
import com.dalread.database.BookmarkPlayerModelQuery;
import com.dalread.database.SubModelQuery;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.FragmentBookmarkPlayerBinding;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnDoubleClickPlayerListener;
import com.dalread.listener.OnKnowChangeListener;
import com.dalread.model.BookmarkPlayerModel;
import com.dalread.model.SubModel;
import com.dalread.model.SubtitleLanguageModel;
import com.dalread.model.SubtitleMeaningModel;
import com.dalread.model.VocaKnowAndKnowpronounce;
import com.dalread.util.BaseBindUtils;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FileUtil;
import com.dalread.util.Loading;
import com.dalread.util.MergeUtil;
import com.dalread.util.ProgressTracker;
import com.dalread.util.SortUtil;
import com.dalread.util.SubtitleUtil;
import com.dalread.util.SupportSubtitleFormat;
import com.dalread.util.TimeUtil;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;

public class BookmarkPlayerFragment extends BasePlayerFragment implements View.OnTouchListener, OnAsyncTaskListener {
    private List<BookmarkPlayerModel> bookmarkPlayerModels;
    private ExoPlayer exoPlayer;
    private ProgressiveMediaSource mediaSource;
    private ProgressTracker progressTracker;
    private boolean isTrackingLeft = true;
    private float repeatTime;
    private float minSub, maxSub;
    private float currentLeft, currentRight;
    private float rangeLeft, rangeRight;
    private boolean isRepeat = true;
    private boolean isUpdatingRangeSeek = false;
    private BookmarkPlayerActivity activity;
    private int subPosition = -1;

    private int typeSwipe = Constant.PLAYER.SWIPE.NONE;
    private int sWidth, sHeight;
    private float downX, downY;
    private float deltaX, deltaY;
    private int positionVolume = 0, positionBrightness = 0;
    private AudioManager mAudioManager;
    // Ruby
    private float rubyY, rubyPosition = -1;
    // Double click
    int numberOfTaps = 0;
    final Handler mHandler = new Handler();

    private boolean isPlay = true;
    private boolean isShowSubtitle = true;
    // show view play center after exoPlayer played
    private boolean isReadyShowViewPlayCenter = false;
    private boolean isShowViewPlayCenter = false;
    private long viewPlayCenterTime = 0;
    // audio speed
    private float speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_DEFAULT;

    private int times = 1;
    private int dialogues = 0;
    private int repeatingTime = Constant.PLAYER.BOOKMARK.REPEAT.TIME_3;
    private List<SubtitleLanguageModel> subtitleLanguageModels;
    private SupportSubtitleFormat subType;

    private SubtitleMeaningAdapter meaningAdapter;
    private ArrayList<SubtitleMeaningModel> subtitleMeaningModels;

    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_UPDATE_VOCA_KNOW = TYPE_INIT_DATA + 1;
    private final int TYPE_UPDATE_VOCA_KNOW_PRONOUNCE = TYPE_UPDATE_VOCA_KNOW + 1;
    private RegisterVocaDialog registerVocaDialog;
    private ArrayList<DicModel> subtitleDialogModels;
    private FragmentBookmarkPlayerBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentBookmarkPlayerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        unregisterObserver();
        super.onDestroy();
    }

    //TODO : 이것 OnClickListener로 옮겨야 한다.
//    @OnClick({
//            R.id.ivCenterPlay,
//            R.id.ivAudioSpeedMinus, R.id.tvAudioSpeedValue, R.id.ivAudioSpeedPlus,
//            R.id.ivRepeatPlay, R.id.ivRepeatEye, R.id.ivRepeatClose})
//    void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ivRepeatClose:
//                activity.onBackPressed();
//                break;
//            case R.id.ivRepeatEye:
//                handleSubtitleClick();
//                break;
//            case R.id.ivAudioSpeedMinus:
//                handSpeedMinusClick();
//                break;
//            case R.id.tvAudioSpeedValue:
//                handleSpeedValueClick();
//                break;
//            case R.id.ivAudioSpeedPlus:
//                handleSpeedPlusClick();
//                break;
//            case R.id.ivCenterPlay:
//            case R.id.ivRepeatPlay:
//                handlePlayClick();
//                break;
//        }
//    }

    @Override
    public void initView() {
        binding.llPlayer.setOnTouchListener(this);
        activity = (BookmarkPlayerActivity) getActivity();
        Loading.show(activity);
        registerObserver();
//        mAudioManager = (AudioManager) activity.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        positionVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 100 / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        getScreenSize();

//        llLMView.setVisibility(View.GONE);
        binding.tvRubyBottom.setTextCenterHorizontal(true);
        binding.tvRubyBottom.setTutor(true);
        binding.tvRubyBottom.setShowOutline(true);
        binding.tvRubyBottom.setBaseContent(true);
        binding.tvRubyBottom.setOnTouchListener(this);
        binding.tvSubTitle.setOnTouchListener(this);
        binding.llRepeat.sbRepeatRange.setOnRangeChangedListener(onRepeatRangeRangeChangedListener);
        exoPlayer = new ExoPlayer.Builder(binding.playerView.getContext()).build();
        exoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        exoPlayer.addListener(exoPlayerEventListener);
        progressTracker = new ProgressTracker(exoPlayer, positionListener);

        meaningAdapter = new SubtitleMeaningAdapter(getActivity(), sharedPreferences.getDisplayPronunciation(), meaningAdapterOnClickListener);
        binding.layoutPlayerSubtitleTable.rvListMeaning.setAdapter(meaningAdapter);
        binding.layoutPlayerSubtitleTable.rvListMeaning.setLayoutManager(new CenterLayoutManager(getActivity()));
        binding.layoutPlayerSubtitleTable.rvListMeaning.addItemDecoration(new SeparatorDecoration(getActivity(), BaseBindUtils.getDividerColor(), BaseBindUtils.getDividerHeight(activity)));
        initDialog();
        rotateScreenToLandScape();
        activity.runOnUiThread(() -> initData());
    }

    private void initDialog() {
        registerVocaDialog = new RegisterVocaDialog(activity, onKnowChangeListener);
    }

    @Override
    public void initData() {
        DLog.d(getLogTag(), "initData");
        activity.playerFileModel = getArguments().getParcelable(Constant.PLAYER.INTENT.KEY_VIDEO_FILE);
        repeatingTime = getArguments().getInt(Constant.PLAYER.INTENT.KEY_TIME);
        if (activity.playerFileModel == null) {
            Loading.hide();
            activity.finish();
            return;
        }
        activity.createSubDatabase(activity.playerFileModel);
        getSubtitleLanguage();
    }

    private void updateInitData(Object resultData) {
        subtitleDialogModels = (ArrayList<DicModel>) resultData;
        if (subtitleDialogModels == null && !activity.playerFileModel.isShowRuby()) {
            subtitleDialogModels = getSubtitleFromFile();
        }
        initMeaningAdapter();
        // load bookmark play
        bookmarkPlayerModels = new ArrayList<>();
        bookmarkPlayerModels.addAll(BookmarkPlayerModelQuery.getSelectedByPath(Voca.getRealm(), activity.playerFileModel.getPath()));
        DLog.d(getLogTag(), "activity.playerFileModel=" + activity.playerFileModel.toString());
        binding.playerView.setPlayer(exoPlayer);

        Uri videoUri = Uri.parse(activity.playerFileModel.getPath());
        String userAgent = Util.getUserAgent(binding.playerView.getContext(), binding.playerView.getContext().getString(R.string.app_name));
        mediaSource = new ProgressiveMediaSource.Factory(
                new DefaultDataSourceFactory(binding.playerView.getContext(), userAgent),
                new DefaultExtractorsFactory()
        ).createMediaSource(MediaItem.fromUri(videoUri));

        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

        loadRepeatData();
        updateTitle();
    }

    private void getSubtitleLanguage() {
        subType = FileUtil.getSubtitleExtension(activity.playerFileModel.getSubPath());
        subtitleLanguageModels = SubtitleUtil.getSubtitleLanguageModels(activity, activity.playerFileModel, activity.getSubDatabase());

        activity.callAsyncTask(this, TYPE_INIT_DATA);
    }

    private List<DicModel> getSubtitleData() {
        if (isHasSubtitleLanguage()) {
            if (activity.playerFileModel.isShowRuby()) {
                return getSubtitleFromDB();
            }
        }
        return null;
    }

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

    private ArrayList<DicModel> getSubtitleFromDB() {
        ArrayList<DicModel> list = new ArrayList<>();
        list.addAll(activity.getSubDatabase().getSubtitleDialogList());
        MergeUtil.generateMeaning(list);
        return list;
    }

    private boolean isHasSubtitleLanguage() {
        return subtitleLanguageModels != null && !subtitleLanguageModels.isEmpty();
    }

    private boolean isHasSubtitle() {
        return subtitleDialogModels != null && !subtitleDialogModels.isEmpty();
    }

    private void updateTitle() {
        activity.runOnUiThread(() -> binding.llRepeat.tvTitle.setText(getString(R.string.bookmark_player_display_title, times, repeatingTime, dialogues + 1, bookmarkPlayerModels.size())));
    }

    private void loadRepeatData() {
        if (dialogues >= bookmarkPlayerModels.size()) {
            ToastUtil.getInstance(activity).show(R.string.msg_play_again_started_over);
            dialogues = 0;
//            activity.onBackPressed();
//            return;
        }
        minSub = bookmarkPlayerModels.get(dialogues).getStart();
        maxSub = bookmarkPlayerModels.get(dialogues).getEnd();
        exoPlayer.seekTo((long) minSub);
        updateRangeSeek();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DLog.d(getLogTag(), "onConfigurationChanged");
        getScreenSize();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.playTTS.setIncludeMeaning(false);
        initPlayVocaHelperListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        isPlay = false;
        pausePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        isPlay = false;
        pausePlayer();
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        killPlayer();
    }

    private void killPlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void checkAndShowSubTitle(long position) {
        if (exoPlayer == null || progressTracker == null ||
                activity.playerFileModel == null || !activity.playerFileModel.isShowRuby() ||
                !isHasSubtitleLanguage() || !isHasSubtitle()) {
            binding.tvSubTitle.setVisibility(View.GONE);
            binding.tvRubyBottom.setVisibility(View.GONE);
            return;
        }
        showSubtitle(activity.playerFileModel.isShowRuby() ? binding.tvRubyBottom : binding.tvSubTitle, position);
    }

    private void showSubtitle(TextView textView, long position) {
        if (position > subtitleDialogModels.get(subtitleDialogModels.size() - 1).getEndTime()) {
            textView.setVisibility(View.GONE);
            return;
        }

        String content = Constant.BASE_BLANK;
        for (int i = 0; i < subtitleDialogModels.size(); i++) {
            DicModel item = subtitleDialogModels.get(i);
            if (position >= item.getStartTime() && position <= item.getEndTime()) {
                if (subPosition == i) return;
                content += item.getVocaDisplay(activity.playerFileModel.isShowRuby());
                subPosition = i;
                break;
            }
        }
        if (Utils.isEmpty(content)) {
            textView.setVisibility(View.GONE);
            return;
        }
        if (subPosition < 0) return;
        getSubtitleMeaningData(textView, subtitleDialogModels.get(subPosition).getSubtitleWordlistId(), content);
    }

    /**
     * repeat subtitle
     * find subtitle by time
     *
     * @param position
     */
    private void repeatSubTitle(final long position) {
        if (isRepeat && position >= maxSub && exoPlayer != null) {
            DLog.d(getLogTag(), "repeatSubTitle - position=" + position + " - minSub=" + minSub + " - maxSub=" + maxSub);
            exoPlayer.seekTo((long) minSub);
            times++;
            if (times > repeatingTime) {
                times = 1;
                dialogues++;
                loadRepeatData();
            }
            updateTitle();
        }
        activity.runOnUiThread(() -> checkAndShowSubTitle(position));
    }

    private void updateRangeSeek() {
        boolean isZero = minSub < Constant.PLAYER.TIMER.SECOND;
        if (isZero) {
            minSub = 0;
        }

        binding.llRepeat.tvRepeatMin.setText(TimeUtil.getDisplay(minSub, Constant.PLAYER.TIMER.SECOND));

        if (maxSub < minSub) return;

        binding.llRepeat.tvRepeatMax.setText(TimeUtil.getDisplay(maxSub, Constant.PLAYER.TIMER.SECOND));

        repeatTime = (maxSub - minSub) / Constant.PLAYER.TIMER.SECOND;
        if (isZero) {
            repeatTime += 1;
        } else {
            repeatTime *= 2;
        }
//        sbRange.setRange(0.0f, (repeatTime + 1.0f));
//        currentRight = repeatTime + (minSub<Constant.PLAYER.TIMER.SECOND ? 0.0f : 1.0f);
        currentLeft = isZero ? 0.0f : (minSub / Constant.PLAYER.TIMER.SECOND);
//        currentRight = repeatTime - (repeatTime / 4);
        currentRight = (maxSub / Constant.PLAYER.TIMER.SECOND);

        rangeLeft = currentLeft > 0 ? currentLeft - 1 : 0.0f;
        rangeRight = currentRight + 1;
        binding.llRepeat.sbRepeatRange.setRange(rangeLeft, rangeRight);
        binding.llRepeat.sbRepeatRange.setProgress(currentLeft, currentRight);
        DLog.d(getLogTag(), "minSub=" + minSub + " - maxSub=" + maxSub + " - repeatTime=" + repeatTime + " - currentLeft=" + currentLeft + " - currentRight=" + currentRight);
    }

    private void updateRangeSeekLeft() {
        boolean isZero = minSub < Constant.PLAYER.TIMER.SECOND;
        if (isZero) {
            minSub = 0;
        }

        binding.llRepeat.tvRepeatMin.setText(TimeUtil.getDisplay(minSub, Constant.PLAYER.TIMER.SECOND));
        binding.llRepeat.tvRepeatMax.setText(TimeUtil.getDisplay(maxSub, Constant.PLAYER.TIMER.SECOND));
//        currentLeft = isZero ? 0.0f : (repeatTime / 4);
        currentLeft = isZero ? 0.0f : (minSub / Constant.PLAYER.TIMER.SECOND);
        rangeLeft = currentLeft > 0 ? currentLeft - 1 : 0.0f;
        binding.llRepeat.sbRepeatRange.setRange(rangeLeft, rangeRight);
        binding.llRepeat.sbRepeatRange.setProgress(currentLeft, currentRight);
        DLog.d(getLogTag(), "minSub=" + minSub + " - maxSub=" + maxSub + " - repeatTime=" + repeatTime + " - currentLeft=" + currentLeft + " - currentRight=" + currentRight);
    }

    private void updateRangeSeekRight() {
        try {
            if (Float.compare(currentLeft, 0) == 0) {
                currentRight = rangeRight;
            } else {
                currentRight = (maxSub / Constant.PLAYER.TIMER.SECOND);
                rangeRight = currentRight + 1;
            }

            binding.llRepeat.tvRepeatMin.setText(TimeUtil.getDisplay(minSub, Constant.PLAYER.TIMER.SECOND));
            binding.llRepeat.tvRepeatMax.setText(TimeUtil.getDisplay(maxSub, Constant.PLAYER.TIMER.SECOND));
            binding.llRepeat.sbRepeatRange.setRange(rangeLeft, rangeRight);
            binding.llRepeat.sbRepeatRange.setProgress(currentLeft, currentRight);
            DLog.d(getLogTag(), "minSub=" + minSub + " - maxSub=" + maxSub + " - repeatTime=" + repeatTime + " - currentLeft=" + currentLeft + " - currentRight=" + currentRight);
        } catch (Exception ex) {
            DLog.e(getLogTag(), ex.getMessage());
        }
    }

    private void updateRangeSeekChanged() {
        binding.llRepeat.tvRepeatMin.setText(TimeUtil.getDisplay(minSub, Constant.PLAYER.TIMER.SECOND));
        binding.llRepeat.tvRepeatMax.setText(TimeUtil.getDisplay(maxSub, Constant.PLAYER.TIMER.SECOND));
        DLog.d(getLogTag(), "currentRight=" + currentRight + " - rangeRight=" + rangeRight);
        if (Float.compare(currentRight, rangeRight) == 0) {
            currentRight = rangeRight - 1;
        }
        binding.llRepeat.sbRepeatRange.setProgress(currentLeft, currentRight);
        DLog.d(getLogTag(), "minSub=" + minSub + " - maxSub=" + maxSub + " - repeatTime=" + repeatTime + " - currentLeft=" + currentLeft + " - currentRight=" + currentRight);
    }

    private void handleMoveLeft(final float minValue, final float maxValue) {
        if (Float.compare(minValue, currentLeft) == 0) return;
        isUpdatingRangeSeek = true;
        activity.runOnUiThread(() -> {
            DLog.d(getLogTag(), "handleMoveLeft - minValue=" + minValue + " - maxValue=" + maxValue + " - currentLeft=" + currentLeft + " - minSub=" + minSub);

            minSub = minSub - ((currentLeft - minValue) * Constant.PLAYER.TIMER.SECOND);
            if (minSub <= 0) {
                minSub = 0;
            }
            if (minValue <= rangeLeft) {
                updateRangeSeekLeft();
                binding.llRepeat.sbRepeatRange.setEnabled(false);
                new Handler().postDelayed(() -> {
                    binding.llRepeat.sbRepeatRange.setEnabled(true);
                    isUpdatingRangeSeek = false;
                }, 200);
            } else {
                currentLeft = minValue;
                updateRangeSeekChanged();
                isUpdatingRangeSeek = false;
            }

            // update bookmark player
            if (bookmarkPlayerModels.get(dialogues).getStart() != minSub) {
                bookmarkPlayerModels.get(dialogues).setStart((long) minSub);
                BookmarkPlayerModelQuery.update(Voca.getRealm(), bookmarkPlayerModels.get(dialogues));
            }
            DLog.d(getLogTag(), "handleMoveLeft - minSub=" + minSub + " - maxSub=" + maxSub + " - isUpdatingRangeSeek=" + isUpdatingRangeSeek);
        });
    }

    private void handleMoveRight(final float maxValue) {
        DLog.d(getLogTag(), "handleMoveRight - maxValue=" + maxValue + " - currentRight=" + currentRight);
        if (Float.compare(maxValue, currentRight) == 0) return;
        isUpdatingRangeSeek = true;
        activity.runOnUiThread(() -> {

            if (Float.compare(currentLeft, 0) != 0) {
                maxSub = maxSub - ((currentRight - maxValue) * Constant.PLAYER.TIMER.SECOND);
                if (maxSub >= exoPlayer.getDuration()) {
                    maxSub = exoPlayer.getDuration();
                }
            }

            if (maxValue >= rangeRight) {
                updateRangeSeekRight();
                binding.llRepeat.sbRepeatRange.setEnabled(false);
                new Handler().postDelayed(() -> {
                    binding.llRepeat.sbRepeatRange.setEnabled(true);
                    isUpdatingRangeSeek = false;
                }, 200);
            } else {
                currentRight = maxValue;
                updateRangeSeekChanged();
                isUpdatingRangeSeek = false;
            }

            // update bookmark player
            if (bookmarkPlayerModels.get(dialogues).getEnd() != maxSub) {
                bookmarkPlayerModels.get(dialogues).setEnd((long) maxSub);
                BookmarkPlayerModelQuery.update(Voca.getRealm(), bookmarkPlayerModels.get(dialogues));
            }
        });
    }

    private void playPlayer() {
        activity.runOnUiThread(() -> {
            DLog.d(getLogTag(), "playPlayer");
            if (exoPlayer != null && !exoPlayer.getPlayWhenReady() && isPlay) {
                exoPlayer.setPlayWhenReady(true);
            }
        });
    }

    private void pausePlayer() {
        activity.runOnUiThread(() -> {
            DLog.d(getLogTag(), "pausePlayer");
            if (exoPlayer != null && exoPlayer.getPlayWhenReady()) {
                exoPlayer.setPlayWhenReady(false);
            }
        });
    }

    /**
     * Max allowed distance to move during a "click", in DP.
     */
    private static final int MAX_CLICK_DISTANCE = 15;
    /**
     * Max allowed duration for a "click", in milliseconds.
     */
    private static final int MAX_CLICK_DURATION = 200;
    private long pressStartTime;
    private boolean stayedWithinClickDistance;

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
        final int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pressStartTime = System.currentTimeMillis();
                stayedWithinClickDistance = true;
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                //touch is start
                downX = event.getX();
                downY = event.getY();
                // move ruby text
                rubyY = view.getY() - event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (stayedWithinClickDistance && distance(downX, downY, event.getX(), event.getY()) > MAX_CLICK_DISTANCE) {
                    stayedWithinClickDistance = false;
                }
                if (!stayedWithinClickDistance && (view.getId() == R.id.tvRubyBottom || view.getId() == R.id.tvSubTitle)) {
                    handleMoveRubyText(event.getRawY() + rubyY, view);
                } else {
                    //finger move to screen
                    final float x2 = event.getX();
                    final float y2 = event.getY();
                    deltaX = downX - x2;
                    deltaY = downY - y2;

                    //HORIZONTAL SCROLL
                    if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                        if (Math.abs(deltaX) < Math.abs(deltaY)) {
                            if (Math.abs(deltaY) > Constant.PLAYER.SWIPE.DISTANCE_MIN) {
                                downY = event.getY();
                                if (downX < (sWidth / 2)) {
                                    typeSwipe = Constant.PLAYER.SWIPE.VERTICALLY_LEFT_SCREEN;
                                } else if (downX > (sWidth / 2)) {
                                    typeSwipe = Constant.PLAYER.SWIPE.VERTICALLY_RIGHT_SCREEN;
                                }
                            }
                        }
                    } else {
                        try {
                            if (typeSwipe == Constant.PLAYER.SWIPE.VERTICALLY_LEFT_SCREEN) {
                                handleSwipeLeft(event.getHistoricalY(0, 0), y2);
                            } else if (typeSwipe == Constant.PLAYER.SWIPE.VERTICALLY_RIGHT_SCREEN) {
                                handSwipeRight(event.getHistoricalY(0, 0), y2, mAudioManager);
                            }
                        } catch (Exception ex) {
                            DLog.e(getLogTag(), ex.getMessage());
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                    if (numberOfTaps == 0) {
                        numberOfTaps++;
                        mHandler.postDelayed(() -> {
                            if (view.getId() != R.id.tvRubyBottom) {
                                if (numberOfTaps > 1) {
                                    handleDoubleClick();
                                } else if (typeSwipe == Constant.PLAYER.SWIPE.NONE) {
                                    binding.llRepeat.ivRepeatPlay.performClick();
                                }
                            }
                            numberOfTaps = 0;
                        }, ViewConfiguration.getDoubleTapTimeout());
                    } else {
                        numberOfTaps++;
                    }
                    // click
                    long pressDuration = System.currentTimeMillis() - pressStartTime;
                    if (pressDuration < MAX_CLICK_DURATION) {
                        if (view.getId() == R.id.tvRubyBottom) {
                            view.onTouchEvent(event);
                        }
                    }
                }
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                break;
            case MotionEvent.ACTION_CANCEL:
                numberOfTaps = 0;
                typeSwipe = Constant.PLAYER.SWIPE.NONE;
                break;
        }
        return true;
    }

    private void handleSwipeLeft(float Y, float y) {
        activity.runOnUiThread(() -> {
            isShowViewPlayCenter = true;
            int distance = Math.abs(Math.round((deltaY / sHeight) * 100)) + 1;
            if (distance > 0) {
                if (y < Y) {
                    positionBrightness += distance;
                } else if (y > Y) {
                    positionBrightness -= distance;
                }
                Utils.changeBrightness(activity, positionBrightness);
                int percent = (int) (((float) positionBrightness / Constant.PLAYER.SWIPE.BRIGHTNESS.MAX) * 100);
                DLog.e(getLogTag(), "handleSwipeLeft - positionBrightness=" + positionBrightness + " - percent=" + percent);
                String valueDisplay = getString(R.string.display_brightness_percent, percent + "%");
                showViewCenter(valueDisplay);
                downY = y;
            }
        });
    }

    private void handSwipeRight(float Y, float y, AudioManager audioManager) {
        activity.runOnUiThread(() -> {
            isShowViewPlayCenter = true;
            int distance = Math.abs(Math.round((deltaY / sHeight) * 100));
            if (distance > 0) {
                if (y < Y) {
                    positionVolume += distance;
                } else if (y > Y) {
                    positionVolume -= distance;
                }
                positionVolume = Utils.checkVolumeRange(positionVolume);
                int value = Math.round((positionVolume * audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) / 100);
                DLog.e(getLogTag(), "handSwipeRight - positionVolume=" + positionVolume + " - value=" + value);
                Utils.changeVolume(audioManager, value);
                String valueDisplay = getString(R.string.display_volume_percent, String.valueOf(positionVolume));
                showViewCenter(valueDisplay);
                downY = y;
            }
        });
    }

    private void getScreenSize() {
        getScreenSize(null);
    }

    private void getScreenSize(Configuration newConfig) {
        DLog.d(getLogTag(), "getScreenSize");
        binding.llPlayer.post(() -> {
            sWidth = binding.llPlayer.getWidth();
            sHeight = binding.llPlayer.getHeight();
            DLog.d(getLogTag(), "getScreenSize - sWidth=" + sWidth + " - sHeight=" + sHeight);

            if (newConfig == null) return;

            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.root.setOrientation(LinearLayout.HORIZONTAL);
                final LinearLayout.LayoutParams playerParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                binding.llPlayer.setLayoutParams(playerParam);
                final LinearLayout.LayoutParams meaningParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 2.0f);
                binding.layoutPlayerSubtitleTable.llListMeaning.setLayoutParams(meaningParam);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                binding.root.setOrientation(LinearLayout.VERTICAL);
                final LinearLayout.LayoutParams playerParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 2.0f);
                binding.llPlayer.setLayoutParams(playerParam);
                final LinearLayout.LayoutParams meaningParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
                binding.layoutPlayerSubtitleTable.llListMeaning.setLayoutParams(meaningParam);
            }
        });
    }

    private ContentObserver contentBrightness = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            positionBrightness = Utils.changeBrightness(activity, Settings.System.getInt(
                    activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 0));
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
        activity.getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, contentVolume);
    }

    private void unregisterObserver() {
        activity.getContentResolver().unregisterContentObserver(contentBrightness);
        activity.getContentResolver().unregisterContentObserver(contentVolume);
    }

    private void showViewCenter(String value) {
        showViewCenter(value, 0);
    }

    private void showViewCenter(int resId) {
        showViewCenter(Constant.BASE_BLANK, resId);
    }

    private void showViewCenter(String value, int resId) {
        if (!isShowViewPlayCenter) return;
        viewPlayCenterTime = System.currentTimeMillis();

        binding.tvCenterText.setText(value);
        binding.tvCenterText.setVisibility(Utils.isEmpty(value) ? View.GONE : View.VISIBLE);

        binding.ivCenterPlay.setImageResource(resId);
        binding.ivCenterPlay.setVisibility(resId <= 0 ? View.GONE : View.VISIBLE);

        binding.llCenter.setVisibility(View.VISIBLE);
        isShowViewPlayCenter = false;
        mHandler.post(onViewCenterRunnable);
    }

    private void handleMoveRubyText(float y, View view) {
        activity.runOnUiThread(() -> {
            rubyPosition = y;
            if (rubyPosition <= Utils.getStatusBarHeight(activity)) {
                rubyPosition = Utils.getStatusBarHeight(activity);
            } else {
                final float tmp = sHeight - view.getHeight() - Utils.getStatusBarHeight(activity);
                if (rubyPosition > tmp) {
                    rubyPosition = tmp;
                }
            }
            updateRubyViewPosition(view);
        });
    }

    private void updateRubyViewPosition(View view) {
        DLog.e(getLogTag(), "updateRubyViewPosition rubyPosition=" + rubyPosition);
        if (rubyPosition < 0) return;
        view.animate()
                .y(rubyPosition)
                .setDuration(0)
                .start();
    }

    /**
     * positionListener
     * return when activity is null, have not duration from exoPlayer
     * update seekBar play
     * update subtitle
     */
    private ProgressTracker.PositionListener positionListener = new ProgressTracker.PositionListener() {
        @Override
        public void progress(long position) {
            if (activity == null) {
                DLog.d(getLogTag(), "getActivity is null");
                return;
            }
            repeatSubTitle(position);
        }
    };

    /**
     * exoPlayerEventListener
     */
    private final Player.Listener exoPlayerEventListener = new Player.Listener() {
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            DLog.d(getLogTag(), "onTimelineChanged - timeline=" + timeline.toString() + " - reason=" + reason);
        }

        @Override
        public void onTracksChanged(Tracks tracks) {
            DLog.d(getLogTag(), "onTracksChanged - tracks=" + tracks.toString());
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            DLog.d(getLogTag(), "onPlayerStateChanged - playWhenReady=" + playWhenReady + " - playbackState=" + playbackState);
            activity.runOnUiThread(() -> {
                if (playbackState == ExoPlayer.STATE_READY) {
                    Loading.hide();
                }
                handlePlayWhenReady(playWhenReady, playbackState);
            });
        }

        @Override
        public void onPlaybackSuppressionReasonChanged(int playbackSuppressionReason) {
            DLog.d(getLogTag(), "onPlaybackSuppressionReasonChanged - playbackSuppressionReason=" + playbackSuppressionReason);
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            DLog.d(getLogTag(), "onIsPlayingChanged - isPlaying=" + isPlaying);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {
            DLog.d(getLogTag(), "onRepeatModeChanged - repeatMode=" + repeatMode);
        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            DLog.d(getLogTag(), "onShuffleModeEnabledChanged - shuffleModeEnabled=" + shuffleModeEnabled);
        }

        // onPlayerError was removed in ExoPlayer 2.19.1
        // Error handling is now done through onPlaybackStateChanged when state is STATE_IDLE

        @Override
        public void onPositionDiscontinuity(Player.PositionInfo oldPosition, Player.PositionInfo newPosition, int reason) {
            DLog.d(getLogTag(), "onPositionDiscontinuity - reason=" + reason);
        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            DLog.d(getLogTag(), "onPlaybackParametersChanged - playbackParameters=" + playbackParameters.toString());
        }


    };

    /**
     * true is playing
     * false is pause
     *
     * @param playWhenReady
     */
    private void handlePlayWhenReady(boolean playWhenReady, int playbackState) {
        try {
            if (playWhenReady) {
                binding.llRepeat.ivRepeatPlay.setImageResource(R.drawable.ic_new_pause);
                progressTracker.startHandle();
            } else {
                binding.llRepeat.ivRepeatPlay.setImageResource(R.drawable.ic_new_play);
                progressTracker.stopHandler();
            }
            handleViewPlayCenterClick(playWhenReady, playbackState);
        } catch (Exception ex) {
            DLog.e(getLogTag(), ex.getMessage());
        }
    }

    private void handleSubtitleClick() {
        isShowSubtitle = !isShowSubtitle;
        if (isShowSubtitle) {
            binding.llRepeat.ivRepeatEye.setImageResource(R.drawable.ic_new_eye_open);
            binding.tvRubyBottom.setVisibility(View.VISIBLE);
            binding.tvSubTitle.setVisibility(View.VISIBLE);
        } else {
            binding.llRepeat.ivRepeatEye.setImageResource(R.drawable.ic_new_eye_close);
            binding.tvRubyBottom.setVisibility(View.GONE);
            binding.tvSubTitle.setVisibility(View.GONE);
        }
    }

    /**
     * Sub - Audio Speed
     * Adjust: 0.1F
     */
    private void handSpeedMinusClick() {
        speedAudio -= Constant.PLAYER.AUDIO.AUDIO_SPEED_ADJUST;
        updateAudioSpeed();
    }

    /**
     * set default audio speed to 1.0f
     */
    private void handleSpeedValueClick() {
        speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_DEFAULT;
        updateAudioSpeed();
    }

    /**
     * Add - Audio Speed
     * Adjust: 0.1F
     */
    private void handleSpeedPlusClick() {
        speedAudio += Constant.PLAYER.AUDIO.AUDIO_SPEED_ADJUST;
        updateAudioSpeed();
    }

    private void handlePlayClick() {
        handlePlayClick(!isPlay, true);
    }

    private void handlePlayClick(boolean isPlay, boolean isShowViewPlayCenter) {
        DLog.e(getLogTag(), "handlePlayClick - isPlay=" + isPlay);
        this.isPlay = isPlay;
        this.isShowViewPlayCenter = isShowViewPlayCenter;
        if (activity.playTTS.playTTSHelper.isPlaying()) {
            activity.playTTS.stop();
        }
        exoPlayer.setPlayWhenReady(isPlay);
    }

    /**
     * show view play center after exoPlayer played
     *
     * @param isPlay
     */
    private void handleViewPlayCenterClick(boolean isPlay, int playbackState) {
        if (!isReadyShowViewPlayCenter) {
            if (playbackState == Player.STATE_READY) {
                isReadyShowViewPlayCenter = true;
            }
            return;
        }
        showViewCenter(isPlay ? R.drawable.exo_icon_play : R.drawable.exo_icon_pause);
    }

    /**
     * check after 0.5 seconds > hide view play center
     */
    private Runnable onViewCenterRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                long currentTime = System.currentTimeMillis();
                if (currentTime - viewPlayCenterTime >= Constant.PLAYER.TIMER.HIDE_TEXT_CENTER) {
                    binding.ivCenterPlay.setVisibility(View.GONE);
                    binding.ivCenterPlay.setImageResource(0);
                    binding.tvCenterText.setVisibility(View.GONE);
                    binding.tvCenterText.setText(Constant.BASE_BLANK);
                    binding.llCenter.setVisibility(View.GONE);
                    isShowViewPlayCenter = false;
                } else {
                    mHandler.postDelayed(this, Constant.PLAYER.TIMER.HIDE_TEXT_CENTER);
                }
            } catch (Exception ex) {
                DLog.e(getLogTag(), ex.getMessage());
            }
        }
    };

    private void updateAudioSpeed() {
        isShowViewPlayCenter = true;
        if (speedAudio < Constant.PLAYER.AUDIO.AUDIO_SPEED_MIN) {
            speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_MIN;
        } else if (speedAudio > Constant.PLAYER.AUDIO.AUDIO_SPEED_MAX) {
            speedAudio = Constant.PLAYER.AUDIO.AUDIO_SPEED_MAX;
        }

        exoPlayer.setPlaybackParameters(new PlaybackParameters(speedAudio));
        final String value = String.format("%.01f", speedAudio);
        showViewCenter(getString(R.string.display_audio_speed, value));
        binding.llRepeat.tvAudioSpeedValue.setText(getString(R.string.audio_speed_text, value));
    }

    private OnRangeChangedListener onRepeatRangeRangeChangedListener = new OnRangeChangedListener() {
        @Override
        public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            DLog.d(getLogTag(), "isUpdatingRangeSeek=" + isUpdatingRangeSeek);
            if (isUpdatingRangeSeek) return;
            DLog.d(getLogTag(), "sbRange - onRangeChanged - leftValue=" + leftValue + " - currentLeft=" + currentLeft + " - rightValue=" + rightValue + " - isFromUser=" + isFromUser);
            if (isTrackingLeft) {
                handleMoveLeft(leftValue, rightValue);
            } else {
                handleMoveRight(rightValue);
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
            DLog.d(getLogTag(), "sbRange - onStopTrackingTouch - isLeft=" + isLeft);
            playPlayer();
        }
    };

    private void handleDoubleClick() {
        if (binding.llRepeat.llControl.getVisibility() == View.VISIBLE) {
            binding.llRepeat.llControl.setVisibility(View.GONE);
            binding.llRepeat.llSpeed.setVisibility(View.GONE);
        } else {
            binding.llRepeat.llControl.setVisibility(View.VISIBLE);
            binding.llRepeat.llSpeed.setVisibility(View.VISIBLE);
        }
    }

    private void initMeaningAdapter() {
        if (activity.playerFileModel.isShowRuby() && isHasSubtitle()) {
            binding.layoutPlayerSubtitleTable.llListMeaning.setVisibility(View.VISIBLE);
            getSubtitleRightTable(String.valueOf(subtitleDialogModels.get(0).getSubtitleWordlistId()));
        } else {
            binding.layoutPlayerSubtitleTable.llListMeaning.setVisibility(View.GONE);
        }
        getScreenSize();
    }

    private void getSubtitleMeaningData(TextView textView, String listId, String content) {
        if (Utils.isEmpty(listId)) {
            textView.setVisibility(View.GONE);
        } else {
            getSubtitleRightTable(listId);
            if (textView instanceof FuriganaView) {
                ((FuriganaView) textView).resetText();
                ((FuriganaView) textView).setJText(content);
            } else {
                textView.setText(content);
            }
            if (isShowSubtitle) {
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }
        }
    }

    private OnDoubleClickPlayerListener meaningAdapterOnClickListener = new OnDoubleClickPlayerListener() {
        @Override
        public void onClick(View view, Object object) {
            switch (view.getId()) {
                case R.id.llItem:
                    showDialogPlayerRegisterSubtitle((Integer) object);
                    break;
                case R.id.icPlay:
                    handlePlay(((SubtitleMeaningModel) object).getDicModel());
                    break;
            }
        }

        @Override
        public void onDoubleClick(View view, Object object) {
            final DicModel dicModel = subtitleMeaningModels.get((Integer) object).getDicModel();
            int vocaKnow;
            if (dicModel.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                vocaKnow = Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN;
            } else {
                vocaKnow = Constant.VOCA_KNOW.VOCA_KNOW_KNOWN;
            }
            sendAmkiGrade(dicModel, vocaKnow, true);
        }
    };

    private void showDialogPlayerRegisterSubtitle(int position) {
        pausePlayer();
//        registerVocaDialog.show(null, subtitleMeaningModels.get(position).getDicModel(), false);
        registerVocaDialog.show(null, false);
    }

    private void sendAmkiGrade(DicModel model, int amkiGrade) {
        sendAmkiGrade(model, amkiGrade, false);
    }

    private void sendAmkiGrade(DicModel model, int amkiGrade, boolean isDoubleClick) {
//        int knownPronounce;
//        if (isDoubleClick) {
//            knownPronounce = Constant.KNOW.KNOWN;
//        }else {
//            if (amkiGrade == Constant.AMKI_GRADE.VALUE_KNOWN) {
//                knownPronounce = Constant.KNOW.KNOWN;
//            } else if (amkiGrade == Constant.AMKI_GRADE.VALUE_UNKNOWN) {
//                knownPronounce = Constant.KNOW.UNKNOWN;
//            } else {
//                knownPronounce = model.getAmkiKnowPronounce();
//            }
//        }
//        if (model.getKnow() == Voca.getKnowByGrade(amkiGrade) &&
//                model.getAmkiGrade() == amkiGrade && model.getKnowPronounce() == knownPronounce) {
//            registerVocaDialog.setUpdateData(false);
//            return;
//        }
//        if (!activity.isLoggedIn()) {
//            activity.alertDialog.showLogInRequired();
//            return;
//        }
//        if (!activity.isNetwork()) {
//            activity.alertDialog.showNoInternet();
//            return;
//        }
//        onAmkiGradeChanged(model, amkiGrade, knownPronounce);
//        application.getDalAiImpl().changeAmkiGrade(
//                String.valueOf(amkiGrade),
//                String.valueOf(model.getAmkiId()),
//                String.valueOf(model.getAmkiType()),
//                null);
    }

//    private void onAmkiGradeChanged(DicModel model, int amkiGrade, int knownPronounce) {
//        int oldKnow = model.getVocaKnow();
//        int know = Voca.getKnowByGrade(amkiGrade);
//        int oldKnowPronounce = model.getVocaKnowPronounce();
//        DLog.d(getLogTag(), "onAmkiGradeChanged - oldKnow=" + oldKnow + " - know=" + know);
//        if (oldKnow == know && oldKnowPronounce == knownPronounce) {
////            registerVocaDialog.setUpdateData(false);
//            return;
//        }
////        registerVocaDialog.setUpdateData(true);
//        model.setVocaKnow(know);
//        model.setVocaKnowPronounce(knownPronounce);
//        activity.getSubDatabase().updateVocaKnowAndVocaKnowPronounce(model);
//        rvListMeaning.post(() -> {
//            meaningAdapter.notifyItemChanged(model.getPosition());
//        });
//        activity.callAsyncTask(this, new int[]{oldKnow, know, oldKnowPronounce, knownPronounce, model.getVocaId()}, TYPE_UPDATE_VOCA_KNOW, false);
//    }

    @Override
    public void onInitAsyncTask() {
        Loading.show(activity);
    }

    @Override
    public void onPrevExecuteAsyncTask() {

    }

    @Override
    public Object onPostExecuteAsyncTask(int searchType, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                return getSubtitleData();
            case TYPE_UPDATE_VOCA_KNOW:
//                updateAmkiGradeChanged(data);
//                break;
            case TYPE_UPDATE_VOCA_KNOW_PRONOUNCE:
                VocaKnowAndKnowpronounce vocaTypeId_know = (VocaKnowAndKnowpronounce) data;
                updateKnownPronounceChanged(vocaTypeId_know);
                break;
        }
        return null;
    }

    @Override
    public void onFinishAsyncTask(int searchType, Object resultData, Object data) {
        switch (searchType) {
            case TYPE_INIT_DATA:
                updateInitData(data);
                break;
            case TYPE_UPDATE_VOCA_KNOW:
            case TYPE_UPDATE_VOCA_KNOW_PRONOUNCE:
                subPosition = -1;
                showSubtitle(activity.playerFileModel.isShowRuby() ? binding.tvRubyBottom : binding.tvSubTitle, exoPlayer.getCurrentPosition());
                playPlayer();
                break;
        }
        Loading.hide();
    }

    private OnKnowChangeListener onKnowChangeListener = new OnKnowChangeListener() {

        @Override
        public void onVocaKnowChange(IVocaBasicItem iVocaBasicItem, int newVocaKnow) {

        }

        @Override
        public void onVocaKnowPronounceChange(IVocaBasicItem iVocaBasicItem, int newVocaKnowPronounce) {

        }

        @Override
        public void onAddToWordbook(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onAddToBookmark(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onDeleteFromBookmark(IVocaBasicItem iVocaBasicItem) {

        }

        @Override
        public void onDismiss() {

        }
    };

//    private OnGradeChangeListener onAmkiGradeChangeListener = (voca, dicModel, amkiGrade) -> {
//        sendAmkiGrade(dicModel, amkiGrade);
//    };
//
//    private OnKnowChangeListener onKnownPronounceChangeListener = (voca, dicModel, knownPronounce)
//            -> sendKnownPronounce(dicModel, knownPronounce);

    private OnClickListener dismissListener = (view, object) -> {
        if (!(Boolean) object) {
            playPlayer();
        }
    };

    private void sendKnownPronounce(DicModel dicModel, int vocaKnowPronounce) {
//        DLog.d(getLogTag(), "onChangeKnownPronounce - vocaKnowPronounce=" + vocaKnowPronounce);
////        registerVocaDialog.setUpdateData(true);
//        int vocaKnowpronounceOld = dicModel.getAmkiKnowPronounce();
//        dicModel.setVocaKnowPronounce(vocaKnowPronounce);
//        activity.getSubDatabase().updateVocaKnowAndVocaKnowPronounce(dicModel);
//        rvListMeaning.post(() -> {
//            meaningAdapter.notifyItemChanged(dicModel.getPosition());
//            DLog.d(getLogTag(), "dicModel=" + dicModel.toString());
//        });
//        if (vocaKnowPronounce == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
//            application.getDalAiImpl().setPronounceKnown(dicModel.getAmki(), String.valueOf(dicModel.getAmkiId()), null);
//        } else {
//            application.getDalAiImpl().setPronounceUnknown(dicModel.getAmki(), String.valueOf(dicModel.getAmkiId()), null);
//        }
//        int vocaType = Constant.API_VALUE.VALUE_VOCA_TYPE_WORD;
//        VocaTypeId_KnowWithOldValue vocaTypeId_knowWithOld = new VocaTypeId_KnowWithOldValue(dicModel.getVocaId(), vocaType, -1, vocaKnowPronounce, -1, vocaKnowpronounceOld);
//        activity.callAsyncTask(this, new int[]{vocaKnowpronounceOld, vocaKnowPronounce, dicModel.getVocaId()}, TYPE_UPDATE_VOCA_KNOW_PRONOUNCE);
    }

    private void handlePlay(IVocaFullPlayTTSItem model) {
        DLog.d(getLogTag(), "handlePlay");
        boolean isPlaying = model.isVIPlaying();
        activity.playTTS.stop();
        if (!isPlaying) {
            handlePlayClick(false, false);
            activity.playTTS.preparePlayVocaSingle(model);
        } else {
            handlePlayClick(true, false);
        }
    }

    private void getSubtitleRightTable(String listId) {
        if (!activity.playerFileModel.isShowRuby())
            return;
        activity.runOnUiThread(() -> {
            subtitleMeaningModels = SortUtil.sortSubtitleMeaningModel(activity, listId);
            meaningAdapter.setData(subtitleMeaningModels);
            binding.layoutPlayerSubtitleTable.rvListMeaning.scrollToPosition(0);
        });
    }

//    private void updateAmkiGradeChanged(Object data) {
//        DLog.d(getLogTag(), "updateAmkiGradeChanged");
//        final String ids = activity.getSubDatabase().updateVocaKnowChangedForSubModel(data);
//        updateToSubModel(ids);
//
//    }

    private void updateKnownPronounceChanged(VocaKnowAndKnowpronounce vocaTypeId_know) {
        DLog.d(getLogTag(), "updateKnownPronounceChanged");
        final String ids = activity.getSubDatabase().getSubtitleIdListToUpdateVocaKnow(vocaTypeId_know);
        updateToSubModel(ids);
    }

    private void updateToSubModel(String id) {
        if (Utils.isEmpty(id)) return;
        if (!isHasSubtitle()) return;
        ArrayList<DicModel> dicModels = new ArrayList<>();
        dicModels.addAll(activity.getSubDatabase().getSubtitleDialogListById(id));
        MergeUtil.generateMeaning(dicModels);
        for (DicModel d1 : dicModels) {
            for (int i = 0; i < subtitleDialogModels.size(); i++) {
                DicModel d2 = subtitleDialogModels.get(i);
                if (d1.getId() == d2.getId()) {
                    d1.setPlayRecordOrTSS(d2.isPlayRecordOrTSS());
                    d1.setVIChecked(d2.isVIChecked());
                    DLog.d(getLogTag(), "d1=" + d1.getId() + " ||| d2=" + d2.getId());
                    subtitleDialogModels.set(i, d1);
                    break;
                }
            }
        }
    }

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
        for (SubtitleMeaningModel item : subtitleMeaningModels) {
            if (utteranceId.equals(String.valueOf(item.getDicModel().getVIId()))) {
                item.getDicModel().setVIPlaying(playing);
                binding.layoutPlayerSubtitleTable.rvListMeaning.post(() -> meaningAdapter.notifyItemChanged(subtitleMeaningModels.indexOf(item)));
                break;
            }
        }
    }
}
