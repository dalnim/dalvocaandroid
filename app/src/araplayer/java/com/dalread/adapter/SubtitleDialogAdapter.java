package com.dalread.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.component.FuriganaView;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.database.sqlite.model.DicModel;
import com.dalread.helper.DoubleClickHelper;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.model.RubyTextModel;
import com.dalread.model.VideoModel;
import com.dalread.util.Constant;
import com.dalread.util.RepeatUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubtitleDialogAdapter extends BaseAdapter<SubtitleDialogAdapter.ViewHolder> {

    private List<DicModel> items = new ArrayList<>();
    private OnDoubleClickListener listener;
    private DoubleClickHelper doubleClickHelper;
    private Context context;
    private boolean displayPronunciation;
    private int lastCheckedPosition = RecyclerView.NO_POSITION;
    private int currentListenComprehensionCount;
    private int showAsterisk;
    private VideoModel videoModel;
    private boolean isDisplayStartEndTimeAsMilliSeconds;
    private boolean isHideUselessSubtitle;
    private boolean isStudyLang, isTongueLang;
    private boolean isListenComprehensionMode;
    private boolean isListenComprehension1;
    private boolean isListenComprehension2;
    private boolean isCCRepeatMode;
    private boolean isABRepeatMode;
    private boolean isRecordingSubtitle;
    private boolean isHideKnownDialogDuringPlaying; //Dalnim Add
    boolean isDoubleClick = false;
    final Handler mHandler = new Handler(Looper.getMainLooper());
    long numberOfTaps = 0;
    private float playerSubtitleFontSize; //Dalnim add
    private boolean mIsDisplaySubtitleLangStudyFirst; //Dalnim

    private boolean isRightHandMode = true;
    private final int VIEW_TYPE_RIGHT = R.layout.item_player_subtitle_dialog;
    private boolean isLockAll = false;
    private String searchingKeyWord;

    public SubtitleDialogAdapter(Context context,
                                 boolean displayPronunciation,
                                 int showAsterisk,
                                 boolean isHideKnownDialogDuringPlaying,
                                 float playerSubtitleFontSize,
                                 boolean isRightHandMode,
                                 DoubleClickHelper doubleClickHelper,
                                 OnDoubleClickListener listener) {
        super(context);
        this.context = context;
        this.displayPronunciation = displayPronunciation;
        this.showAsterisk = showAsterisk;
        this.isHideKnownDialogDuringPlaying = isHideKnownDialogDuringPlaying;
        this.listener = listener;
        this.doubleClickHelper = doubleClickHelper;
        this.playerSubtitleFontSize = playerSubtitleFontSize;
        this.isRightHandMode = isRightHandMode;
        isDisplayStartEndTimeAsMilliSeconds = SharedPreferencesDB.getInstance(context).getDisplayStartEndTimeInSubtitleView();
//        isHideUselessSubtitle = SharedPreferencesDB.getInstance(context).getHideUselessSubtitles();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_RIGHT;
//        if (isRightHandMode) {
//            return VIEW_TYPE_RIGHT;
//        }
//        return VIEW_TYPE_LEFT;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(getInflater().inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position), position);
    }

    public void setHideKnownDialogDuringPlaying(boolean hideKnownDialogDuringPlaying) {
        isHideKnownDialogDuringPlaying = hideKnownDialogDuringPlaying;
    }

    public void setData(List<DicModel> items) {
        if (items != null) {
            this.items.clear();
            this.items.addAll(items);
//            notifyDataSetChanged();
        }
    }

    public void setPlayerSubtitleFontSize(float playerSubtitleFontSize) {
        if (this.playerSubtitleFontSize == playerSubtitleFontSize) return;
        this.playerSubtitleFontSize = playerSubtitleFontSize;
//        notifyDataSetChanged();
    }

    public void setmIsDisplaySubtitleLangStudyFirst(boolean mIsDisplaySubtitleLangStudyFirst) {
        this.mIsDisplaySubtitleLangStudyFirst = mIsDisplaySubtitleLangStudyFirst;
    }

    public int getLastCheckedPosition() {
        return lastCheckedPosition;
    }

    public DicModel getCheckedDicModel() {
        return items.get(lastCheckedPosition);
    }

    public void setLastCheckedPosition(int newCheckedPosition) {
        if (newCheckedPosition == lastCheckedPosition || newCheckedPosition == RecyclerView.NO_POSITION) return;
        int copyOfLastCheckedPosition = lastCheckedPosition;
        lastCheckedPosition = newCheckedPosition;

        //If I comment these lines AraPlayer doesn't pause short time, but the current subtitle's background color is not set
        notifyItemChanged(copyOfLastCheckedPosition);
        notifyItemChanged(lastCheckedPosition);
    }

    public void resetLastCheckedPosition() {
//        DLog.d("SON", "resetLastCheckedPosition - lastCheckedPosition=" + lastCheckedPosition);
        if (lastCheckedPosition == RecyclerView.NO_POSITION)
            return;
        setLastCheckedPosition(RecyclerView.NO_POSITION);
    }

    @Override
    public void notifyDataSetChanged(List<?> dataList) {

    }

    public void setShowAsterisk(int showAsterisk) {
        this.showAsterisk = showAsterisk;
    }

//    public void setShowAsterisk(int showAsterisk, boolean isRefresh) {
//        this.showAsterisk = showAsterisk;
//        if (isRefresh) {
//            notifyDataSetChanged();
//        }
//    }

    public int getShowAsterisk() {
        return showAsterisk;
    }

    public void setDisplayLanguages(boolean isStudyLang, boolean isTongueLang) {
        this.isStudyLang = isStudyLang;
        this.isTongueLang = isTongueLang;
    }

    public void setListenComprehension1(boolean isListenComprehension1) {
        this.isListenComprehension1 = isListenComprehension1;
    }

    public void setListenComprehension2(boolean isListenComprehension2) {
        this.isListenComprehension2 = isListenComprehension2;
    }

    public void setListenComprehensionMode(boolean isListenComprehensionMode) {
        this.isListenComprehensionMode = isListenComprehensionMode;
    }

    public void setCCRepeatMode(boolean isCCRepeatMode) {
        this.isCCRepeatMode = isCCRepeatMode;
    }

    public void setABRepeatMode(boolean isABRepeatMode) {
        this.isABRepeatMode = isABRepeatMode;
    }

    public void setRecordingSubtitle(boolean recordingSubtitle) {
        isRecordingSubtitle = recordingSubtitle;
    }

    public void setCurrentListenComprehensionCount(int currentListenComprehensionCount) {
        this.currentListenComprehensionCount = currentListenComprehensionCount;
    }

    public void setIsLockAll(boolean lockAll) {
        isLockAll = lockAll;
    }

    public void setSearchingKeyWord(String searchingKeyWord) {
        this.searchingKeyWord = searchingKeyWord;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {
        @BindView(R.id.rl_dialog)
        RelativeLayout rl_dialog;
        @BindView(R.id.llItem)
        View llItem;
        @BindView(R.id.fvSubtitle)
        FuriganaView fvSubtitle;
        @BindView(R.id.tvIndex)
        TextView tvIndex;
        @BindView(R.id.ivCCRepeat)
        ImageView ivCCRepeat;
        @BindView(R.id.ivBookmark)
        ImageView ivBookmark;
        @BindView(R.id.tvKnowValue)
        TextView tvKnowValue;
        @BindView(R.id.ivKnowPronounce)
        ImageView ivKnowPronounce;
        @BindView(R.id.tvRepeatCount)
        TextView tvRepeatCount;
        @BindView(R.id.fvDifficultWord)
        FuriganaView fvDifficultWord;
        @BindView(R.id.tvTimes)
        TextView tvTimes;
//        @BindView(R.id.btnHideDialog)
//        Button btnHideDialog;
//        @BindView(R.id.btnDeleteDialog) Button btnDeleteDialog;
//        @BindView(R.id.btnSwipeCancel) Button btnSwipeCancel;
        @BindView(R.id.layoutListenRecordedSubtitle)
        FrameLayout layoutListenRecordedSubtitle;
        @BindView(R.id.ivListenRecordedSubtitle)
        ImageView ivListenRecordedSubtitle;
        @BindView(R.id.ivRecordingSubtitle)
        ImageView ivRecordingSubtitle;
        private DicModel item;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //Dalnim : Tried to pause player when I swipe the dialog, but this is called only once.
            //I'm not sure addRevealListener is right to use. And R.id.btnReveal is also not sure to be used here. I use it in the listener to pause player.
//            swipe.addRevealListener(R.id.btnReveal, new SwipeLayout.OnRevealListener() {
//                @Override
//                public void onReveal(View child, SwipeLayout.DragEdge edge, float fraction, int distance) {
//                    listener.onClick(child, item);
//                }
//            });
        }

        @OnClick({R.id.llItem, R.id.rl_dialog, R.id.fvDifficultWord, R.id.fvSubtitle, R.id.ivCCRepeat, R.id.llKnowValue,
                R.id.tvKnowValue, R.id.ivBookmark,
//                R.id.btnHideDialog, R.id.btnDeleteDialog, R.id.btnSwipeCancel,
                R.id.ivRecordingSubtitle, R.id.ivListenRecordedSubtitle})
        void onClick(View v) {
            if (listener == null) return;
            switch (v.getId()) {
//                case R.id.btnSwipeCancel:
//                    swipe.close(true);
//                    listener.onClick(v, item);
//                    break;
                case R.id.ivListenRecordedSubtitle:
                    item.setListeningRecord(!item.isListeningRecord());
                    notifyItemChanged(getBindingAdapterPosition());
                    listener.onClick(v, item);
                    break;
                default:
                    listener.onClick(v, item);
                    break;
            }
        }

        public void bind(DicModel model, int position) {
            this.item = model;

            rl_dialog.setOnClickListener(new DoubleClick(listener, item));
            fvDifficultWord.setOnClickListener(new DoubleClick(listener, item));
            fvSubtitle.setOnClickListener(new DoubleClick(listener, item));

            tvKnowValue.setOnClickListener(new DoubleClick(listener, item));
            llItem.setOnClickListener(new DoubleClick(listener, item));

//            model.setPosition(position);
            //Dalnim add
            int showAsteriskToDisplay = showAsterisk;
            if ((showAsterisk != Constant.SHOW_ASTERISK.HIDE_SENTENCE)) {
                //if it's not CC Repeat mode, then handle the KNOWN dialog differently(Show difficult words only)
                showAsteriskToDisplay = Voca.getShowAsteriskWhenHideKnowDialogIsOn(model, isHideKnownDialogDuringPlaying, false, showAsterisk);
            }
            fvSubtitle.setOnTouchListener(this);
            fvSubtitle.settingForSubtitleInTableView(displayPronunciation, showAsteriskToDisplay, playerSubtitleFontSize);

            fvDifficultWord.setOnTouchListener(this);
            fvDifficultWord.settingForDifficutlWords(displayPronunciation, showAsteriskToDisplay, playerSubtitleFontSize);

            final String wordText = model.getTextDisplayByLang(isStudyLang, isTongueLang, mIsDisplaySubtitleLangStudyFirst);

            fvSubtitle.setNormalTextView(false);
            if (Utils.isEmpty(wordText)) {
                fvSubtitle.setVisibility(View.GONE);
            } else {
                fvSubtitle.setListRubyText((HashMap<Integer, RubyTextModel>) model.getListRubyTextModel().clone());
                fvSubtitle.setJText(wordText);
            }

            if (Utils.isEmpty(model.getMeaningWords())) {
                fvDifficultWord.setVisibility(View.GONE);
            } else {
                fvDifficultWord.setJText(Constant.BASE_BLANK, model.getMeaningWords());
            }
            if (isDisplayStartEndTimeAsMilliSeconds) {
                String strTime = model.getStartTimeOriginal() + " --> " + model.getEndTimeOriginal() + "\n" +
                        Voca.getSubtitleTimeWithAllExtraTime(model.getStartTime(), videoModel) + " --> " + Voca.getSubtitleTimeWithAllExtraTime(model.getEndTime(), videoModel)
                        + " (" + videoModel.getPlayBeforeAfterAndDelayTime() + ")";
                tvTimes.setText(strTime);
                tvTimes.setVisibility(View.VISIBLE);
            } else {
                tvTimes.setVisibility(View.GONE);
            }

            ivBookmark.setSelected(model.isBookmark());
            if (model.isBookmark()) {
                ivBookmark.setVisibility(View.VISIBLE);
            } else {
                ivBookmark.setVisibility(View.INVISIBLE);
            }
//            ivBookmark.setSelected(model.isBookmark());
//            ivBookmark.setEnabled(!isLockAll);
//            swipe.setSwipeEnabled(!isLockAll);

//            boolean showKnowPronounceIcon = VocaKnow.showKnowPronounceIcon(SharedPreferencesDB.getInstance(context).getLangStudyCode(), item.getVocaKnow(), item.getVocaKnowPronounce());
//            ivKnowPronounce.setVisibility(showKnowPronounceIcon ? View.VISIBLE : View.INVISIBLE);
            VocaKnow.updateIconVocaKnow(context, tvKnowValue, model.getVocaKnow());
            VocaKnow.updateIconVocaKnowPronounce(context, ivKnowPronounce, item);
//            VocaKnow.updateIconVocaBookmark(ivBookmark, item.isVIBookmark(), true);

            tvIndex.setText(String.valueOf(model.getIndex()));
//            if (model.isPIChecked()) {
            int totalComprehensionCount = 0;
            updateVisible_RecordingSubtitle();


            if (isListenComprehension2) {
                totalComprehensionCount = RepeatUtil.getRepeatValue(model);
                tvRepeatCount.setText(String.valueOf(totalComprehensionCount));
                tvRepeatCount.setVisibility(View.VISIBLE);

            } else {
                tvRepeatCount.setVisibility(View.GONE);
            }
            ivCCRepeat.setSelected(model.isVIChecked());
            updateItemSelect(position == lastCheckedPosition, totalComprehensionCount);
            fvDifficultWord.setVisibility(View.VISIBLE);

            updateVisibilityListenRecrodedSubtitle();

            ivListenRecordedSubtitle.setSelected(item.isListeningRecord());
        }

        private void updateVisibilityListenRecrodedSubtitle() {
            if (isListenComprehensionMode || isABRepeatMode || isCCRepeatMode) {
                layoutListenRecordedSubtitle.setVisibility(View.GONE);
            } else {
                if (item.hasVIVoiceFile()) {
                    layoutListenRecordedSubtitle.setVisibility(View.VISIBLE);
                } else {
                    layoutListenRecordedSubtitle.setVisibility(View.GONE);
                }
            }
        }

        public void updateItemSelect(boolean isSelect, int totalComprehensionCount) {
            llItem.setBackgroundResource(isSelect ? R.color.backgroundCellSubtitleCurrentPlayingColor : R.color.backgroundCellSubtitleNormalColor);
            if (isABRepeatMode) {
                setVisibleCCRepeatUI(View.INVISIBLE);
            } else {
                setVisibleCCRepeatUI(View.VISIBLE);
                if (isSelect) {
                    setVisibleCCRepeatUI(View.VISIBLE);
                    if (isListenComprehension2) {
                        int currentListenComprehensionCountToDisplay = (currentListenComprehensionCount + 1) > totalComprehensionCount ? totalComprehensionCount : (currentListenComprehensionCount + 1);
                        tvRepeatCount.setText(currentListenComprehensionCountToDisplay + "/" + totalComprehensionCount);
                    }
                } else {
                    llItem.setBackgroundResource(R.color.backgroundCellSubtitleNormalColor);
                }

            }
        }

        private void updateVisible_RecordingSubtitle() {
            if (isListenComprehensionMode || isCCRepeatMode || isABRepeatMode || isRecordingSubtitle) {
                setVisibleRecordingSubtitle(View.INVISIBLE);
            } else {
                setVisibleRecordingSubtitle(View.VISIBLE);
            }
        }
        private void setVisibleCCRepeatUI(int visibility) {
            ivCCRepeat.setVisibility(visibility);
        }

        private void setVisibleRecordingSubtitle(int visibility) {
            ivRecordingSubtitle.setVisibility(visibility);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            //이건 아직 안쓴다.
//            if (motionEvent.getAction() != MotionEvent.ACTION_MOVE) {
//                doubleClickHelper.onTouch(view, motionEvent, item);
//                return true;
//            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                handleClickItem(llItem, item);
                return true;
            }
            return false;
        }
    }

    public void setVideoModel(VideoModel videoModel) {
        this.videoModel = videoModel;
    }

    public boolean isDisplayStartEndTimeAsMilliSeconds() {
        return isDisplayStartEndTimeAsMilliSeconds;
    }

    public void setDisplayStartEndTimeAsMilliSeconds(boolean displayStartEndTimeAsMilliSeconds) {
        isDisplayStartEndTimeAsMilliSeconds = displayStartEndTimeAsMilliSeconds;
    }

    public boolean isHideUselessSubtitle() {
        return isHideUselessSubtitle;
    }

    public void setHideUselessSubtitle(boolean hideUselessSubtitle) {
        isHideUselessSubtitle = hideUselessSubtitle;
    }

    private void handleClickItem(View view, DicModel item) {
        if (numberOfTaps == 0) {
            numberOfTaps++;
            mHandler.postDelayed(() -> {
                isDoubleClick = numberOfTaps > 1;
                numberOfTaps = 0;
                if (listener != null) {
                    if (isDoubleClick) {
                        listener.onDoubleClick(view, item);
                    } else {
                        listener.onClick(view, item);
                    }
                }
            }, ViewConfiguration.getDoubleTapTimeout());
        } else {
            numberOfTaps++;
        }
    }

    public void setLayoutType(boolean isLayoutRight) {
        this.isRightHandMode = isLayoutRight;
//        notifyDataSetChanged();
    }

}