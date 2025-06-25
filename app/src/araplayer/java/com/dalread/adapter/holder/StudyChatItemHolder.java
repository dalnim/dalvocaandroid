package com.dalread.adapter.holder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.component.FuriganaView;
import com.dalread.listener.DoubleClick;
import com.dalread.listener.OnDoubleClickListener;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.listener.OnVocaStudyChatSpeakerClickListener;
import com.dalread.model.VocaStudyChat;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.StringUtils;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyChatItemHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

    @BindView(R.id.v_item)
    View vItem;
    @BindView(R.id.tv_index)
    TextView tvIndex;
    @BindView(R.id.tv_study_count)
    TextView tvStudyCount;
    @BindView(R.id.fv_word)
    FuriganaView fvWord;
    @BindView(R.id.tv_word)
    TextView tvWord;
    @BindView(R.id.tv_meaning)
    TextView tvMeaning;
    @BindView(R.id.tv_feedback)
    TextView tvFeedback;
    @BindView(R.id.iv_small)
    ImageView ivSmall;
    @BindView(R.id.tv_small)
    TextView tvSmall;
    @BindView(R.id.iv_big)
    ImageView ivBig;
    @BindView(R.id.tv_big)
    TextView tvBig;
    @BindView(R.id.ic_play)
    ImageView icPlay;
    @BindView(R.id.v_4_buttons)
    LinearLayout v4Buttons;
    @BindView(R.id.v_3_buttons)
    View v3Buttons;
    @BindView(R.id.ic_unknown_pronounce)
    ImageView icUnknownPronounce;

    @BindString(R.string.tpl_study_count)
    String tplStudyCount;

    @BindColor(R.color.textPrimaryColor)
    int clNormal;
    @BindColor(R.color.rubyTextUnknownPronounceColor)
    int clUnknownPronounce;
    @BindColor(R.color.rubyTextUnknownColor)
    int clUnknownMeaning;

    private VocaStudyChat voca;
    private int studyRole;
    private int showAsterisk;
    private boolean displayPronunciation;
    private boolean isStudentAndTutorJoined;
    private int studyLang;
    private boolean hasBookName;
    private boolean hasHeader;
    private OnVocaStudyChatClickListener listener;
    private OnVocaStudyChatSpeakerClickListener onVocaStudyChatSpeakerClickListener;
    private OnDoubleClickListener onDoubleClickListener;
    private OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow;
    private boolean isShow4Buttons;
    private String originalText;
    private String asteriskText;
    private boolean isRightHandMode = true;
    boolean isDoubleClick = false;
    final Handler mHandler = new Handler(Looper.getMainLooper());
    long numberOfTaps = 0;
    private Context context;
    public StudyChatItemHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        fvWord.setOnTouchListener(this);
    }


    public void setOnDoubleClickListener(OnDoubleClickListener onDoubleClickListener) {
        this.onDoubleClickListener = onDoubleClickListener;
    }

    public void setOnDoubleClickListenerOnBaseVocaKnow(OnDoubleClickListener onDoubleClickListenerOnBaseVocaKnow) {
        this.onDoubleClickListenerOnBaseVocaKnow = onDoubleClickListenerOnBaseVocaKnow;
    }

    public void setOnVocaStudyChatSpeakerClickListener(OnVocaStudyChatSpeakerClickListener onVocaStudyChatSpeakerClickListener) {
        this.onVocaStudyChatSpeakerClickListener = onVocaStudyChatSpeakerClickListener;
    }

    public void bind(Context context, VocaStudyChat voca, int studyRole, int showAsterisk, boolean displayPronunciation,
                     boolean isStudentAndTutorJoined, int studyLang, int highlightIndex, boolean hasBookName,
                     boolean hasHeader, OnVocaStudyChatClickListener listener) {
        this.context = context;
        this.voca = voca;
        this.studyRole = studyRole;
        this.showAsterisk = showAsterisk;
        this.displayPronunciation = displayPronunciation;
        this.isStudentAndTutorJoined = isStudentAndTutorJoined;
        this.studyLang = studyLang;
        this.hasBookName = hasBookName;
        this.hasHeader = hasHeader;
        this.listener = listener;
        isShow4Buttons = Voca.isShow4Buttons(voca.getVocaKnow());
        if (isRightHandMode) {
            v4Buttons.setGravity(Gravity.END);
        } else {
            v4Buttons.setGravity(Gravity.START);
        }

        ivSmall.setOnClickListener(new DoubleClick(onDoubleClickListener, voca));
        tvSmall.setOnClickListener(new DoubleClick(onDoubleClickListener, voca));
        tvBig.setOnClickListener(new DoubleClick(onDoubleClickListener, voca));

        bindIndex();
        bindStudyCount();
        bindWord();
        bindMeaning();
        bindFeedback();
        bindGrade();
        bindIconPlay();
        bindKnownPronounce();
        btnBookmark();
//        bindBackground(highlightIndex);
    }

    private void btnBookmark() {
        ivSmall.setImageResource(voca.isBookmark() ? R.drawable.ic_favorite_new_on : R.drawable.ic_favorite_new_off);
        tvSmall.setVisibility(View.GONE);
    }

    private void bindIndex() {
        String strIndex = String.valueOf(voca.getIndex());
        if (!TextUtils.isEmpty(voca.getPersonAB())) {
            strIndex += " " + voca.getPersonAB();
        }
        tvIndex.setText(strIndex);
    }

    private void bindStudyCount() {
        if (voca.getVocaId() > 0) {
            String strStudyCount = String.valueOf(voca.getStudyCount());
            strStudyCount = String.format(tplStudyCount, strStudyCount);
            tvStudyCount.setText(strStudyCount);
            tvStudyCount.setVisibility(View.VISIBLE);
        } else {
            tvStudyCount.setVisibility(View.GONE);
        }
        if (!Utils.isDebug()) {
            tvStudyCount.setVisibility(View.GONE);
        }
    }

    private void bindWord() {
        fvWord.setIsKnownPronounceMeaning(Voca.checkStudyLanguageJPCN(String.valueOf(studyLang)));
        fvWord.resetText();
        originalText = "";
        asteriskText = "";
        if (Constant.MAKE_RUBY_TEXT && !TextUtils.isEmpty(voca.getVocaDisplayRubyText())
                && showAsterisk == Constant.SHOW_ASTERISK.SHOW_SENTENCE) {
            if (studyLang == EnumLanguage.ENGLISH.getIdApi()) {
                fvWord.setTutor(true);
            } else {
                fvWord.setTutor(studyRole == Constant.STUDY_ROLE_TUTOR);
            }
            fvWord.setTutor(true);
            fvWord.setShowMeaningJPCN(!Voca.checkStudyLanguageJPCN(String.valueOf(studyLang)));
            fvWord.setDisplayPronunciation(displayPronunciation);
            fvWord.setJText(voca.getVocaDisplayRubyText());
            fvWord.setVisibility(View.VISIBLE);
            tvWord.setText(originalText);
            tvWord.setVisibility(View.GONE);
            voca.setRubyVocaIdsAndTypes(fvWord.getListId(), fvWord.getListType());
        } else {
            fvWord.setVisibility(View.GONE);
            String strWord = Voca.getVocaDisplay(voca);
            if (voca.getType() == Constant.API_VALUE.VALUE_VOCA_TYPE_WORD) {
                if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                    if (voca.getVocaKnowPronounce() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                        tvWord.setTextColor(clNormal);
                    } else {
                        tvWord.setTextColor(clUnknownPronounce);
                        strWord = combinePronounce(strWord);
                    }
                } else {
                    tvWord.setTextColor(clUnknownMeaning);
                    strWord = combinePronounce(strWord);
                }
            } else {
                tvWord.setTextColor(clNormal);
                strWord = combinePronounce(strWord);
            }
            originalText = strWord;
            if (voca.getVocaId() > 0 && showAsterisk != Constant.SHOW_ASTERISK.SHOW_SENTENCE) {
                strWord = strWord.replace(" [" + voca.getPronounce() + "]", "");
                asteriskText = showAsterisk == Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY
                        ? Voca.changeVocaWithSmartAsterisk(strWord, voca.getVocaKnow())
                        : Voca.changeVocaWithAllAsterisk(strWord);
                if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                    strWord = asteriskText;
                } else {
                    strWord += "\n[" + asteriskText + "]";
                }
            }

            if (voca.getVocaId() > 0) {
                tvWord.setText(strWord);
            } else {
                if (!Utils.isEmpty(voca.getHanja())) {
                    tvWord.setText(strWord + " -> " + voca.getHanja());
                } else {
                    tvWord.setText(strWord);
                }
            }
            tvWord.setVisibility(View.VISIBLE);
        }
    }

    private String combinePronounce(String strWord) {
        if (displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())
                && (voca.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN || voca.getVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)) {
            strWord += " [" + voca.getPronounce() + "]";
        }
        return strWord;
    }

    private String combineHanja(String string) {
        if (!Utils.isEmpty(voca.getHanja())) {
            string += " (" + voca.getHanja() + ")";
        }
        return string;
    }
    private void bindMeaning() {
        if (voca.getVocaId() > 0) {
//            String strMeaning = showAsterisk == Constant.SHOW_ASTERISK_ALL ? voca.getMeaningForHideAll() : voca.getMeaning();
            String strMeaning = showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE ? voca.getMeaningForHideAll() : getEnglishMeaningIfEmptyMeaning();
            tvMeaning.setText(combineHanja(strMeaning));
        } else {
            tvMeaning.setText(R.string.this_word_is_not_in_the_dictionary);
        }
        tvMeaning.setVisibility(View.VISIBLE);
    }

    private String getEnglishMeaningIfEmptyMeaning() {
        String strMeaning = "";
        if (Utils.isEmpty(voca.getMeaning())) {
            strMeaning = getEnglishMeaningIfExists(strMeaning);
            strMeaning = getJmdtMeaningForJapaneseIfMeaningIsNotExist(strMeaning);
        } else {
            strMeaning = voca.getMeaning();
        }
        return strMeaning;
    }

    private String getEnglishMeaningIfExists(String strMeaning) {
        if (!Utils.isEmpty(voca.getMeaningEnglish())) {
            strMeaning = "(" + StringUtils.cutMeaningIfTooLongInWordListView(voca.getMeaningEnglish()) + ")";
        }
        return strMeaning;
    }

    private String getJmdtMeaningForJapaneseIfMeaningIsNotExist(String strMeaning) {
        if (studyLang == EnumLanguage.JAPANESE.getIdApi() && Utils.isEmpty(strMeaning)) {
            if (!Utils.isEmpty(voca.getJmdictMeaning())) {
                strMeaning = "(" + StringUtils.cutMeaningIfTooLongInWordListView(voca.getJmdictMeaning()) + ")";
            } else if (!Utils.isEmpty(voca.getJmdictMeaningEng())) {
                strMeaning = "(" + StringUtils.cutMeaningIfTooLongInWordListView(voca.getJmdictMeaningEng()) + ")";
            }
        }
        return strMeaning;
    }

    private void bindFeedback() {
        if (voca.getVocaId() > 0) {
            String strFeedback = voca.getFeedbackMessage();
            tvFeedback.setText(strFeedback);
            tvFeedback.setVisibility(View.VISIBLE);
        } else {
            tvFeedback.setVisibility(View.GONE);
        }
    }

    private void bindGrade() {
        if (voca.getVocaId() < 0) {
            tvBig.setVisibility(View.GONE);
            ivSmall.setVisibility(View.GONE);
            v4Buttons.setVisibility(View.GONE);
            v3Buttons.setVisibility(View.GONE);
            return;
        }
        if (isShow4Buttons) {
            v4Buttons.setVisibility(View.VISIBLE);
            tvBig.setVisibility(View.GONE);
        } else {
            v4Buttons.setVisibility(View.GONE);
            tvBig.setVisibility(View.VISIBLE);
            VocaKnow.updateImageViewStyleByVocaKnow(context, ivBig, tvBig, voca.getVocaKnow());
        }
        v3Buttons.setVisibility(View.GONE);
    }

    private void bindIconPlay() {
        if (isStudentAndTutorJoined) {
            icPlay.setColorFilter(null);
            icPlay.setImageResource(R.drawable.ic_sync);
        } else {
            Voca.updateIconSpeaker(icPlay, voca);
        }
    }

    private void bindKnownPronounce() {
        if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN && voca.getVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
            icUnknownPronounce.setVisibility(View.VISIBLE);
        } else {
            icUnknownPronounce.setVisibility(View.GONE);
        }
    }

    private void bindBackground(int highlightIndex) {
        if (highlightIndex > 0 && highlightIndex == voca.getIndex()) {
            vItem.setBackgroundResource(R.color.color_blink);
        } else if (voca.isVIChecked()) { // use key checked for blink mode
            vItem.setBackgroundResource(R.color.color_blink);
            vItem.postDelayed(normalItemRunnable, Constant.STOP_BLINK_DELAY_TIME);
        } else {
            vItem.setBackgroundResource(R.drawable.btn_button);
        }
    }

    @OnClick({R.id.v_item, R.id.ic_play, R.id.iv_small, R.id.tv_big, R.id.tv_word,
            R.id.btn_known, R.id.btn_grade_1, R.id.btn_grade_2, R.id.btn_exclude,
            R.id.btn_a, R.id.btn_b, R.id.btn_c})
    void onClick(View view) {
        if (listener != null && !hasHeader) {
            voca.setTblSection(0);
            voca.setTblRow(getAdapterPosition() - (hasBookName ? 1 : 0));
        }

        BaseVoca.updateSpeakerIconStop(icPlay);
        int id = view.getId();
        switch (id) {
            case R.id.v_item:
                handleClickItem();
                break;
            case R.id.ic_play:
                if (listener != null) {
                    listener.onPlayClick(voca);
                }
                //Don't change code order (This code should be called later than listener)
                if (onVocaStudyChatSpeakerClickListener != null) {
                    voca.setVIPlaying(!voca.isVIPlaying());
                    onVocaStudyChatSpeakerClickListener.onSpeakerClick(voca);
                }
                break;
//            case R.id.iv_small:
//            case R.id.tv_big:
//                if (listener != null) {
//                    listener.onBigIconClick(voca);
//                }
//                break;
            case R.id.tv_word:
                if (studyRole == Constant.STUDY_ROLE_STUDENT && showAsterisk != Constant.SHOW_ASTERISK.SHOW_SENTENCE) {
                    if (!isShow4Buttons) {
                        tvWord.setText(originalText);
                        tvWord.removeCallbacks(displayAsteriskRunnable);
                        vItem.setBackgroundResource(R.color.color_blink);
                        voca.setVIChecked(false);
                        if (voca.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
                            tvWord.postDelayed(displayAsteriskRunnable, Constant.SELF_PRACTICE_DISPLAY_VOCA_TIME);
                            tvWord.postDelayed(normalItemRunnable, Constant.STOP_BLINK_DELAY_TIME);
                        }
                    }
                    if (listener != null) {
                        listener.onAsteriskSentenceClick(voca);
                    }
                }
                break;
            case R.id.btn_known:
                if (listener != null) {
                    listener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_KNOWN);
                }
                break;
            case R.id.btn_grade_1:
                if (listener != null) {
                    listener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_1);
                }
                break;
            case R.id.btn_grade_2:
                if (listener != null) {
                    listener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_AMKI_GRADE_2);
                }
                break;
            case R.id.btn_exclude:
                if (listener != null) {
                    listener.onVocaKnowClick(voca, Constant.VOCA_KNOW.VOCA_KNOW_UNKNOWN);
                }
                break;
            case R.id.btn_a:
                if (listener != null) {
                    listener.onEvaluateGradeClick(voca, Constant.EVALUATE.GRADE_A);
                }
                break;
            case R.id.btn_b:
                if (listener != null) {
                    listener.onEvaluateGradeClick(voca, Constant.EVALUATE.GRADE_B);
                }
                break;
            case R.id.btn_c:
                if (listener != null) {
                    listener.onEvaluateGradeClick(voca, Constant.EVALUATE.GRADE_C);
                }
                break;
            default:
                break;
        }
    }

    private Runnable displayAsteriskRunnable = new Runnable() {

        @Override
        public void run() {
            tvWord.setText(asteriskText);
        }
    };

    private Runnable normalItemRunnable = new Runnable() {

        @Override
        public void run() {
            vItem.setBackgroundResource(R.drawable.btn_button);
            voca.setVIChecked(false);
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            handleClickItem();
            return true;
        }
        return false;
    }

    private void handleClickItem() {
        if (listener == null)
            return;

        if (numberOfTaps == 0) {
            numberOfTaps++;
            mHandler.postDelayed(() -> {
                isDoubleClick = numberOfTaps > 1;
                numberOfTaps = 0;
                if (isDoubleClick) {
                    listener.onDoubleItemClick(voca);
                } else {
                    listener.onItemClick(voca);
                }
            }, ViewConfiguration.getDoubleTapTimeout());
        } else {
            numberOfTaps++;
        }
    }

    public void setRightHandMode(boolean rightHandMode) {
        isRightHandMode = rightHandMode;
    }
}
