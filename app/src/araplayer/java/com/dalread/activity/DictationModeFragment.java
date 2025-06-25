package com.dalread.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dalread.R;
import com.dalread.adapter.StudyChatAdapter;
import com.dalread.adapter.StudyWritingAdapter;
import com.dalread.asyntask.OnAsyncTaskListener;
import com.dalread.base.BasePlayerVideoFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.ItemOffsetDecoration;
import com.dalread.database.WebDictionaryQuery;
import com.dalread.database.sqlite.SubDatabase;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.databinding.FragmentPlayerDictationModeBinding;
import com.dalread.dialog.RecyclerViewDialog;
import com.dalread.dialog.RegisterVocaDialog;
import com.dalread.dialog.VocaStudyChatDialog;
import com.dalread.dialog.WebDictionaryDialog;
import com.dalread.interfaces.IVocaBasicItem;
import com.dalread.interfaces.IVocaFullPlayTTSItem;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.listener.OnStudyWritingClickListener;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.model.VocaKnowAndKnowpronounce;
import com.dalread.model.VocaKnowMeaning;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.model.WebDictionaryModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Loading;
import com.dalread.util.MergeUtil;
import com.dalread.util.StringUtils;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.jaygoo.widget.RangeSeekBar;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import butterknife.OnClick;

public class DictationModeFragment extends BasePlayerVideoFragment<FragmentPlayerDictationModeBinding> implements OnAsyncTaskListener {

    private StudyWritingAdapter dictationAdapter;
    private List<VocaKnowMeaning> dictationSplited;
    private List<VocaKnowMeaning> dictationShuffleSplited;
    private int dictationPosition;
    private String dictationText;
    private RegisterVocaDialog registerVocaDialog;

    private final int timeToRefreshQuiz = 2500;
    private final int TYPE_INIT_DATA = 0;
    private final int TYPE_UPDATE_VOCA_KNOW = TYPE_INIT_DATA + 1;
    private final int TYPE_UPDATE_VOCA_KNOW_PRONOUNCE = TYPE_UPDATE_VOCA_KNOW + 1;

    private FragmentPlayerDictationModeBinding binding;

    @Override
    protected View getContentView() {
        binding = FragmentPlayerDictationModeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initView() {
        super.initView();
        final Bundle bundle = getArguments();
        subtitleIndex = bundle.getInt(Constant.PLAYER.INTENT.KEY_INDEX, 0);
//        subtitleGroupType = bundle.getInt(Constant.PLAYER.INTENT.KEY_TYPE);
//        subtitleDifficultArray = bundle.getIntArray(Constant.PLAYER.INTENT.KEY_DATA);
//        vocaKnowGroupSelect = bundle.getParcelable(Constant.PLAYER.INTENT.KEY_DATA);
        if (activity.playerFileModel == null) {
            activity.onBackPressed();
            return;
        }
        initEventBus();
        dictationSplited = new ArrayList<>();
        dictationShuffleSplited = new ArrayList<>();
        if (dictationAdapter == null) {
            dictationAdapter = new StudyWritingAdapter(activity, dictationShuffleSplited, onDictationWordClickListener);
            getRoot().post(() -> {
                int smallBoxWidth = requireContext().getResources().getDimensionPixelSize(R.dimen.study_english_small_box_width);
                int smallBoxOffset = requireContext().getResources().getDimensionPixelSize(R.dimen.study_english_small_box_offset);
                int noOfColumns = getRoot().getWidth() / (smallBoxWidth + smallBoxOffset);
                DLog.d(getLogTag(), "sWidth=" + getRoot().getWidth() + " - noOfColumns=" + noOfColumns);
                binding.layoutChooseWord.rvDictationList.setLayoutManager(new GridLayoutManager(activity, noOfColumns));
                binding.layoutChooseWord.rvDictationList.addItemDecoration(new ItemOffsetDecoration(smallBoxOffset));
            });
        } else {
            dictationAdapter.setData(dictationShuffleSplited);
        }
        binding.layoutChooseWord.rvDictationList.setAdapter(dictationAdapter);
        binding.layoutChooseWord.llDictationMeaningDisplayScroll.setListener(onDictationMeaningClickListener);
        initDialog();
        initData();
        activity.callAsyncTask(this, TYPE_INIT_DATA);
    }

    private void initDialog() {
        registerVocaDialog = new RegisterVocaDialog(activity, onKnowChangeListenerForVocaWord);
        recyclerSubtitleViewDialog = new RecyclerViewDialog(activity, (view, object) -> {
            if ((Boolean) object) {
                recyclerSubtitleViewDialog.setUpdateData(false);
            }
            activity.playTTS.stop();
            activity.playTTS.playTTSHelper.resetPlayPlayer();

            refreshDifficultWordsInTextView();

            playPlayer();
        });
    }

    private void refreshDifficultWordsInTextView() {
        dictationSplited = refreshVocaKnow(dictationShuffleSplited, activity.getSubDatabase());
        refreshDictationLayout();
        final DicModel dicModel = getDicModel(subtitleIndex);
        updateDictationMeaning(dicModel);
    }

    @Override
    public void initData() {
        resizePlayerView(getLLPlayer());
        setDictationMode();
        initExoPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.runOnUiThread(() -> {
            if (isDataChangeFromPhraseInfor) {
                if (!Utils.isEmpty(idsDataChangeFromPhraseInfor)) {
                    updateToSubModel(idsDataChangeFromPhraseInfor);
                    updateDictationData();
                }
                isDataChangeFromPhraseInfor = false;
                idsDataChangeFromPhraseInfor = Constant.BASE_BLANK;
            }
        });
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
        return binding.llRepeat;
    }

    @Override
    protected Button getBtnCCRepeatReset() {
        return binding.btnCCRepeatReset;
    }

    @Override
    protected RangeSeekBar getSbRepeatRange() {
        return binding.sbRepeatRange;
    }

    @Override
    @Nullable
    protected LinearLayout getLLRepeatRange() {
        return null;
    }

    @Override
    protected TextView getTvRepeatMin() {
        return binding.tvRepeatMin;
    }

    @Override
    protected TextView getTvRepeatMax() {
        return binding.tvRepeatMax;
    }

    @Override
    protected View getLLPlay() {
        return binding.llPlay;
    }

    @Override
    protected RangeSeekBar getSbPlay() {
        return binding.sbPlay;
    }

    @Override
    protected TextView getTvVideoStartTime() {
        return binding.tvVideoStartTime;
    }

    @Override
    protected TextView getTvVideoEndTime() {
        return binding.tvVideoEndTime;
    }

    @Override
    protected FragmentPlayerDictationModeBinding inflateViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPlayerDictationModeBinding.inflate(inflater, container, false);
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
        for (int i = 0; i < subtitleList.size(); i++) {
            if (getDicModel(i).checkTime((long) leftValue, (long) leftValue, getDicModel(i + 1))) {
                if (subtitleIndex != i) {
                    subtitleIndex = i;
                    updateDictationData();
                }
                break;
            }
        }
    }

    @Override
    protected void onPlayerVideoPlayRangeChangedStopTracking() {

    }

    @Override
    protected void onPlayerVideoUpdateRangeSeekProgress() {

    }

    @Override
    protected void onPlayerVideoPlayClick() {

    }

    @Override
    protected void onPlayerVideoPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    protected void onPlayerVideoPositionDiscontinuity(int reason) {

    }

    @Override
    protected void onPlayerVideoRepeatSubtitle(long position) {
        updateRangeSeek();
        if ((isSubtitleEndTimeExceedDelaySubtitle(position))) {
            seekToInPlayer_MinSubWithDelayTime();
        }
    }

    @OnClick({R.id.iconLeft,
            R.id.llPlayer, R.id.ivCenterPlay,
            R.id.btnCCRepeatReset,
            R.id.ivAudioSpeedMinus, R.id.ivAudioSpeedPlus,
            R.id.ivDictationPrevious, R.id.ivDictationBookmark, R.id.ivDictationNext,
            R.id.icDictationRefresh})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.iconLeft:
                activity.onBackPressed();
                break;
            case R.id.llPlayer:
                onPlayerClick();
                break;
            case R.id.ivCenterPlay:
                handlePlayClick((Integer) getIvCenterPlay().getTag() == R.drawable.ic_new_play, true);
                break;
            case R.id.btnCCRepeatReset:
                onResetButton();
                break;
            case R.id.ivAudioSpeedMinus:
                handSpeedMinusClick();
                break;
            case R.id.ivAudioSpeedPlus:
                handleSpeedPlusClick();
                break;
            case R.id.ivDictationPrevious:
                handleDictationPrevious();
                break;
            case R.id.ivDictationBookmark:
                handleDictationBookmark();
                break;
            case R.id.ivDictationNext:
                handleDictationNext();
                break;
            case R.id.icDictationRefresh:
                handleDictationRefresh();
                break;
        }
    }

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
                return getDictationData();
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
                subtitleList = (ArrayList<DicModel>) resultData;
                if (subtitleList.size() == 1) {
                    binding.ivDictationPrevious.setVisibility(View.GONE);
                    binding.ivDictationNext.setVisibility(View.GONE);
                } else {
                    binding.ivDictationPrevious.setVisibility(View.VISIBLE);
                    binding.ivDictationNext.setVisibility(View.VISIBLE);
                }
                updateDictationData();
                break;
            case TYPE_UPDATE_VOCA_KNOW:
            case TYPE_UPDATE_VOCA_KNOW_PRONOUNCE:
                updateDictationData();
                Loading.hide();
                break;
        }
        Loading.hide();
    }

    private List<DicModel> getDictationData() {
        try {
            if (Utils.isEmptyCollection(subtitleListTotal)) {
                if (activity.getSubDatabase() == null) {
                    activity.createSubDatabase(activity.playerFileModel);
                }
                subtitleListTotal = new ArrayList<>();
                subtitleListTotal.addAll(activity.getSubDatabase().getSubtitleDialogList());
                rubyTextModels = activity.getSubDatabase().getWordRubyTag();
                MergeUtil.generateMeaning(subtitleListTotal, rubyTextModels);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return generateSubtitleDialogByType();
    }

    private List<DicModel> generateSubtitleDialogByType() {
        final List<DicModel> items = new ArrayList<>();
        items.addAll(subtitleListTotal);

        //쓰는 자막막 남겨둔다.
        for (Iterator<DicModel> iterator = items.iterator(); iterator.hasNext(); ) {
            DicModel item = iterator.next();
            if (!item.isShowUsed()) {
                iterator.remove();
            }
        }

        return items;
    }

    private void onPlayerClick() {
        if (numberOfTaps == 0) {
            numberOfTaps++;
            mHandler.postDelayed(() -> {
                if (numberOfTaps > 1) {
                    int visibility = getLLRepeat().getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
                    getLLRepeat().setVisibility(visibility);
//                    ll_listen_comprehension.setVisibility(visibility);
                    getLLPlay().setVisibility(visibility);
                } else {
                    getIvCenterPlay().performClick();
                }
                numberOfTaps = 0;
            }, ViewConfiguration.getDoubleTapTimeout());
        } else {
            numberOfTaps++;
        }
    }

    private OnClickListener dismissListener = (view, object) -> {
        if (!(Boolean) object && !recyclerSubtitleViewDialog.isShowing()) {
            playPlayer();
        }
    };

    @Subscribe
    public void onEvent(SuccessEvent event) {
        if (event.getScreen() == BaseEvent.Screen.DICTATION_MODE) {
            switch (event.getEventType()) {
                case DATA_CHANGED:
                    String[] data = (String[]) event.getModel();
                    this.idsDataChangeFromPhraseInfor = data[0];
                    this.vocaIdDataChangeFromPhraseInfor = data[1];
                    this.isDataChangeFromPhraseInfor = true;
                    break;
            }
        }
    }

    private void updateDictationData() {
        DicModel dicModel = getDicModel(subtitleIndex);
        if (dicModel == null) {
            if (!Utils.isEmpty(subtitleList)) {
                dicModel = getDicModel(0);
            }
            if (dicModel == null) {
                binding.llContent.setVisibility(View.GONE);
                return;
            }
        }
        binding.llContent.setVisibility(View.VISIBLE);
        getMinMaxTime(dicModel);
        updateRangeSeek();

        String dictationWord = dicModel.getVocaDisplay();
        String dictationWordRuby = dicModel.getVocaDisplayRuby();
        String dictationMeaning = dicModel.getMeaning();

        dictationSplited = MergeUtil.generateStudyWritingModel(dictationWord, dictationWordRuby);
        dictationSplited = refreshVocaKnow(dictationSplited, activity.getSubDatabase());
        binding.layoutChooseWord.tvDictationMeaning.setText(StringUtils.removeLineBreaks(dictationMeaning));
        binding.layoutChooseWord.tvDictationMeaning.setVisibility(Utils.isEmpty(dictationMeaning) ? View.INVISIBLE : View.VISIBLE);
//        Collections.shuffle(dictationShuffleSplited = new ArrayList<>(dictationSplited));
        refreshDictationData();
        refreshDictationLayout();
        playPlayer();
        binding.tvDictationSubtitleCount.setText((subtitleIndex + 1) + "/" + subtitleList.size());
//        exoPlayer.seekTo((long) minSub);
        seekToInPlayer_MinSubWithDelayTime();
        binding.ivDictationBookmark.setSelected(dicModel.isBookmark());
        updateDictationMeaning(dicModel);
        binding.layoutChooseWord.svMain.fullScroll(ScrollView.FOCUS_UP);
    }

    private List<VocaKnowMeaning> refreshVocaKnow(List<VocaKnowMeaning> list, SubDatabase subDatabase) {
        String strWordIdList = list.stream()
                .filter(e -> e.getVocaType() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD)
                .map(e -> String.valueOf(e.getVocaId()))
                .collect(Collectors.joining(","));
        List<DicModel> dicModels = subDatabase.getDicModelByVocaIds(strWordIdList);
        Map<String, DicModel> mapDicModelWordList = dicModels.stream()
                .collect(Collectors.toMap(e -> e.getVocaType() + "_" + e.getVocaId(), e -> e));

        for(VocaKnowMeaning aModel : list) {
            String vocaTypeID = aModel.getVocaType() + "_" + aModel.getVocaId();
            if (mapDicModelWordList.containsKey(vocaTypeID)) {
                DicModel dicModel = mapDicModelWordList.get(vocaTypeID);
                aModel.setVocaKnow(dicModel.getVocaKnow());
            }
        }
        return list;
    }
    private void updateDictationMeaning(DicModel dicModel) {
        String difficultWordsWithMeaning = dictationShuffleSplited.stream()
                .filter( e -> e.getVocaKnow() < Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                .map(e -> e.getVoca() + ":" + e.getMeaning())
                .collect(Collectors.joining(","));
        binding.layoutChooseWord.tvDictationMeaningDisplay.setText(difficultWordsWithMeaning);
        binding.layoutChooseWord.llDictationMeaningDisplayScroll.fullScroll(ScrollView.FOCUS_LEFT);
    }

    private void handleDictationPrevious() {
        subtitleIndex--;
        if (subtitleIndex < 0) {
            subtitleIndex = subtitleList.size() - 1;
        }
        updateDictationData();
    }

    private void handleDictationNext() {
        subtitleIndex++;
        if (subtitleIndex >= subtitleList.size()) {
            subtitleIndex = 0;
        }
        updateDictationData();
    }

    private void handleDictationBookmark() {
        activity.runOnUiThread(() -> {
            final DicModel dicModel = getDicModel(subtitleIndex);
            updateBookmark(dicModel);
            binding.ivDictationBookmark.setSelected(dicModel.isBookmark());
            eventBus.post(new SuccessEvent(BaseEvent.Screen.DICTATION_MODE, BaseEvent.EventType.REAOAD_SUBTITLE_AGAIN, dicModel));
        });
    }

    private void handleDictationRefresh() {
        refreshDictationData();
        refreshDictationLayout();
        binding.layoutChooseWord.tvDictationText.setText(Constant.BASE_BLANK);
    }

    private void handleDictationMeaning() {
        DLog.d(getLogTag(), "handleDictationMeaning");
        pausePlayer();
        getRubyList(getDicModel(subtitleIndex), null);
    }

    private void getRubyList(DicModel model, OnClickListener callback) {
        DLog.d(getLogTag(), "getRubyList - model=" + model.toString());
        StudyChatAdapter adapter = new StudyChatAdapter(activity);
        adapter.setDataNoLoop(activity.getSubDatabase().getVocaStudyChat(model.getId()));
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
        recyclerSubtitleViewDialog.setAdapter(adapter, title);
        recyclerSubtitleViewDialog.show();
        recyclerSubtitleViewDialog.getAdapter();
        activity.playTTS.stop();
        activity.playTTS.playTTSHelper.resetPlayPlayer();
        if (callback != null) {
            callback.onClick(null, adapter);
        }
    }

    @Override
    protected void updateVocaKnow(IVocaBasicItem iVocaBasicItem) {
        super.updateVocaKnow(iVocaBasicItem);
    }

    private OnVocaStudyChatClickListener onVocaStudyChatClickListener = new OnVocaStudyChatClickListener() {

        @Override
        public void onItemClick(VocaStudyChat voca) {
            //Don't delete this, I'll show the this menu later.
            openVocaStudyChatDialog(voca);
        }

        @Override
        public void onDoubleItemClick(VocaStudyChat voca) {
            updateKnowWhenDoubleClick(voca);
            eventBus.post(new SuccessEvent(BaseEvent.Screen.DICTATION_MODE, BaseEvent.EventType.REAOAD_SUBTITLE_AGAIN, voca));
        }

        @Override
        public void onPlayClick(final VocaStudyChat voca) {
            handlePlay(voca, false);
        }

        @Override
        public void onBigIconClick(VocaStudyChat voca) {
            registerVocaDialog.show(voca, true);
        }

        @Override
        public void onAsteriskSentenceClick(final VocaStudyChat voca) {
        }

        @Override
        public void onVocaKnowClick(VocaStudyChat voca, int vocaKnow) {
            onKnowChangeListenerForVocaWord.onVocaKnowChange(voca, vocaKnow);
        }

        @Override
        public void onEvaluateGradeClick(VocaStudyChat voca, String grade) {

        }

        @Override
        public void onAnswerClick(VocaStudyChatExam voca) {

        }
    };

    private OnStudyWritingClickListener onDictationWordClickListener = new OnStudyWritingClickListener() {
        @Override
        public void onClick(Object data, int pos) {
            VocaKnowMeaning model = (VocaKnowMeaning) data;
            DLog.d(getLogTag(), "onDictationWordClickListener model=" + model.toString());
            if (dictationPosition >= dictationSplited.size()) {
                return;
            }
            if (model.getVoca().equals(dictationSplited.get(dictationPosition).getVoca())) {
//                if (!dictationText.isEmpty() && dictationWord.contains(" ")) {
                if (!dictationText.isEmpty()) {
                    dictationText += " ";
                }
                dictationText += model.getVoca();
                binding.layoutChooseWord.tvDictationText.setText(dictationText);
//                dictationShuffleSplited.set(pos, new VocaKnowMeaning());
                dictationAdapter.notifyItemChanged(pos);
                if (++dictationPosition == dictationSplited.size()) {
                    binding.layoutChooseWord.svMain.fullScroll(ScrollView.FOCUS_UP);
                    binding.layoutChooseWord.rvDictationList.postDelayed(() -> {
                        refreshDictationData();
                        refreshDictationLayout();
                    }, timeToRefreshQuiz);
                }
            } else {
                //runVibrator();
                ToastUtil.getInstance(activity).show("Try again");
            }
        }
    };

    private void handlePlay(IVocaFullPlayTTSItem model, boolean isAutoPlay) {
        DLog.d(getLogTag(), "handlePlay");
        boolean isPlaying = model.isVIPlaying();
        activity.playTTS.stop();
        if (!isPlaying) {
            if (isAutoPlay) {
                pausePlayer();
            }
            activity.playTTS.preparePlayVocaSingle(model);
        } else {
            if (isAutoPlay) {
                playPlayer();
            }
        }
    }

    private void refreshDictationData() {
        Collections.shuffle(dictationShuffleSplited = new ArrayList<>(dictationSplited));
        dictationText = "";
        dictationPosition = 0;
    }

    private void refreshDictationLayout() {
        binding.layoutChooseWord.tvDictationText.setText(R.string.msg_making_sentence);
        dictationAdapter.setData(dictationShuffleSplited);
        dictationAdapter.notifyDataSetChanged();
    }

    private OnClickListener onDictationMeaningClickListener = (view, object) -> handleDictationMeaning();

    private void updateDictationStudyWritingModel(Object data) {
        if (dictationSplited == null || dictationShuffleSplited == null) return;
        final int[] arrayData = (int[]) data;
        for (VocaKnowMeaning i : dictationSplited) {
            if (i.getVocaId() == arrayData[4]) {
                i.setVocaKnow(arrayData[1]);
                break;
            }
        }
        for (VocaKnowMeaning i : dictationShuffleSplited) {
            if (i.getVocaId() == arrayData[4]) {
                i.setVocaKnow(arrayData[1]);
                break;
            }
        }
    }

    // https://github.com/dalnim/IssueOnly/issues/141
    private void openVocaStudyChatDialog(VocaStudyChat voca) {
        final VocaStudyChatDialog dialog = new VocaStudyChatDialog(activity, voca, new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
                switch (view.getId()) {
                    case R.id.llOpenPhraseInformation:
                        openPhraseInformation(object);
                        break;
                    case R.id.tv_edit_phrase:
                        openEditMeaningScreen(object);
                        break;
                    case R.id.tv_play_this_phrase:
                        break;
                    case R.id.ivWebDictionary:
                        openWebDictionary(object);
                        break;
                    case R.id.tv_copy_this_phrase:
                        onCopyThisPhrase(object);
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
        if (data instanceof VocaStudyChat) {
            final VocaStudyChat item = (VocaStudyChat) data;
            final WebDictionaryModel webDictionary = WebDictionaryQuery.getFirst(Voca.getRealm(),
                    activity.studyLanguage.getIdApi(), activity.motherTongueLanguage.getIdApi());
            String url;
            if (webDictionary == null) {
                if (activity.motherTongueLanguage.getIdApi() == EnumLanguage.KOREAN.getIdApi()) {
                    url = Constant.PLAYER.WEB_DICTIONARY.URL_DEFAULT + item.getVocaDisplay();
                } else {
                    openWebDictionary();
                    return;
                }
            } else {
                url = webDictionary.getUrl().replace(Constant.PLAYER.WEB_DICTIONARY.WORD_REPLACE, item.getVocaDisplay());
            }
            final WebDictionaryDialog dialog = new WebDictionaryDialog(activity, url, new OnClickDialogListener() {
                @Override
                public void onClick(View view, Object object) {

                }

                @Override
                public void onDismiss(View view, Object object) {

                }
            });
            dialog.show();
        }
    }

    private void openPhraseInformation(Object data) {
        final VocaStudyChat item = (VocaStudyChat) data;
        Intent intent = new Intent(activity, WordInfoActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_ID, item.getVocaId());
        intent.putExtra(Constant.BUNDLE.KEY_VOCA_TYPE, item.getType());
        intent.putExtra(Constant.PLAYER.INTENT.KEY_VIDEO_FILE, activity.playerFileModel);
        startActivity(intent);
    }

    private void openEditMeaningScreen(Object data) {
        ToastUtil.getInstance(activity).show(R.string.msg_under_development);
    }

    private void openWebDictionary() {
        Intent intent = new Intent(activity, WebDictionaryActivity.class);
        startActivity(intent);
    }

    private void onCopyThisPhrase(Object data) {
        final VocaStudyChat item = (VocaStudyChat) data;
        Utils.copyToClipboard(activity, item.getVocaDisplay(), R.string.copied);
    }

//    private void updateAmkiGradeChanged(Object data) {
//        DLog.d(getLogTag(), "updateAmkiGradeChanged");
//        final String ids = activity.getSubDatabase().updateVocaKnowChangedForSubModel(data);
//        updateToSubModel(ids);
//        updateDictationStudyWritingModel(data);
//    }

    private void updateKnownPronounceChanged(VocaKnowAndKnowpronounce vocaTypeId_know) {
        DLog.d(getLogTag(), "updateKnownPronounceChanged");
        final String ids = activity.getSubDatabase().getSubtitleIdListToUpdateVocaKnow(vocaTypeId_know);
        updateToSubModel(ids);
    }

    private void updateToSubModel(String id) {
        if (Utils.isEmpty(id)) return;
        ArrayList<DicModel> dicModels = new ArrayList<>();
        dicModels.addAll(activity.getSubDatabase().getSubtitleDialogListById(id));
        rubyTextModels = activity.getSubDatabase().getWordRubyTag();
        MergeUtil.generateMeaning(dicModels, rubyTextModels);
        for (DicModel d1 : dicModels) {
            for (int i = 0; i < subtitleList.size(); i++) {
                DicModel d2 = getDicModel(i);
                if (d1.getId() == d2.getId()) {
                    d1.setPlayRecordOrTSS(d2.isPlayRecordOrTSS());
                    d1.setVIChecked(d2.isVIChecked());
                    DLog.d(getLogTag(), "total d1=" + d1.getId() + " ||| d2=" + d2.getId());
                    subtitleList.set(i, d1);
                    break;
                }
            }
        }

        // check StudyChatAdapter when changed data from phrase information
        if (recyclerSubtitleViewDialog != null && recyclerSubtitleViewDialog.isShowing()
                && !Utils.isEmpty(vocaIdDataChangeFromPhraseInfor)) {
            final DicModel item = activity.getSubDatabase().getDicModelByVocaId(vocaIdDataChangeFromPhraseInfor);
            if (item != null) {
                StudyChatAdapter studyChatAdapter = (StudyChatAdapter) recyclerSubtitleViewDialog.getAdapter();
                for (Object obj : studyChatAdapter.getData()) {
                    if (obj instanceof VocaStudyChat) {
                        VocaStudyChat voca = (VocaStudyChat) obj;
                        if (voca.getVocaId() == item.getVocaId()) {
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
        }
    }


}
