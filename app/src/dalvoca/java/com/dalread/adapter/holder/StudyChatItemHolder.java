package com.dalread.adapter.holder;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.component.FuriganaView;
import com.dalread.listener.OnVocaStudyChatClickListener;
import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.util.Constant;
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
    @BindView(R.id.ic_unknown_pronounce)
    ImageView icUnknownPronounce;
    @BindView(R.id.ic_play)
    ImageView icPlay;
    @BindView(R.id.v_4_buttons)
    View v4Buttons;
    @BindView(R.id.v_3_buttons)
    View v3Buttons;
    @BindView(R.id.btn1)
    Button btn1;
    @BindView(R.id.btn2)
    Button btn2;
    @BindView(R.id.btn3)
    Button btn3;
    @BindView(R.id.btn4)
    Button btn4;
    @BindView(R.id.v_btn_1)
    View vBtn1;
    @BindView(R.id.v_btn_2)
    View vBtn2;

    @BindString(R.string.tpl_study_count)
    String tplStudyCount;
    @BindString(R.string.student_meaning_is_hided)
    String strStudentMeaningIsHided;

    @BindColor(R.color.color_ruby_text_normal)
    int clNormal;
    @BindColor(R.color.color_ruby_text_unknown_pronounce)
    int clUnknownPronounce;
    @BindColor(R.color.color_ruby_text_unknown_meaning)
    int clUnknownMeaning;

    private boolean isDialog;
    private VocaStudyChat voca;
    private int studyRole;
    private int showAsterisk;
    private boolean displayPronunciation;
    private boolean isStudentAndTutorJoined;
    private int studyLang;
    private boolean hasBookName;
    private boolean hasHeader;
    private boolean showStudentMeaning;
    private OnVocaStudyChatClickListener listener;
    private boolean isShow4Buttons;
    private String originalText;
    private String asteriskText;

    boolean isDoubleClick = false;
    final Handler mHandler = new Handler(Looper.getMainLooper());
    long numberOfTaps = 0;
    private Context context;
    public StudyChatItemHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public StudyChatItemHolder(@NonNull View itemView, boolean isDialog) {
        this(itemView);
        this.isDialog = isDialog;
    }

    public void bind(Context context, VocaStudyChat voca, int studyRole, int showAsterisk, boolean displayPronunciation,
                     boolean isStudentAndTutorJoined, int studyLang, int highlightIndex, boolean hasBookName,
                     boolean hasHeader, boolean showStudentMeaning, OnVocaStudyChatClickListener listener) {
        this.context = context;
        this.voca = voca;
        this.studyRole = studyRole;
        this.showAsterisk = showAsterisk;
        this.displayPronunciation = displayPronunciation;
        this.isStudentAndTutorJoined = isStudentAndTutorJoined;
        this.studyLang = studyLang;
        this.hasBookName = hasBookName;
        this.hasHeader = hasHeader;
        this.showStudentMeaning = showStudentMeaning;
        this.listener = listener;
        isShow4Buttons = Voca.isShow4Buttons(voca.getVocaKnow());

        bindIndex();
        bindStudyCount();
        bindWord();
        bindMeaning();
        bindFeedback();
        bindGrade();
        bindKnownPronounce();
        bindIconPlay();
        bindAnswer();
        bindBackground(highlightIndex);
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
            if (voca.getType() == 1) {
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
            if (!isDialog && voca.getVocaId() > 0 && showAsterisk != Constant.SHOW_ASTERISK.SHOW_SENTENCE) {
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
            tvWord.setText(strWord);
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

    private void bindMeaning() {
        if (voca.getVocaId() > 0) {
            String strMeaning;
            if (voca instanceof VocaStudyChatExam) {
                if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                    tvMeaning.setText("");
                    tvMeaning.setVisibility(View.GONE);
                } else {
                    strMeaning = voca.getMeaning();
                    tvMeaning.setText(strMeaning);
                    tvMeaning.setVisibility(View.VISIBLE);
                }
            } else {
                strMeaning = showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE ? voca.getMeaningForHideAll() : voca.getMeaning();
                if (!showStudentMeaning) {
                    if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                        strMeaning = strStudentMeaningIsHided;
                    } else {
                        if (TextUtils.isEmpty(strMeaning)) {
                            strMeaning = strStudentMeaningIsHided;
                        } else {
                            strMeaning = strStudentMeaningIsHided + "\n" + strMeaning;
                        }
                    }
                }
                tvMeaning.setText(strMeaning);
                tvMeaning.setVisibility(View.VISIBLE);
            }
        } else {
            tvMeaning.setText("");
            tvMeaning.setVisibility(View.GONE);
        }
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
        if (voca.getVocaId() > 0) {
            if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                Voca.updateTextViewEvaluateGradeStyle(
                        ivSmall, tvSmall,
                        voca.isDisplayEvaluation() ? voca.getEvaluateVocaGrade() : "",
                        voca.isDisplayEvaluation() ? voca.getEvaluateVocaGradeTutors() : ""
                );
                v4Buttons.setVisibility(isShow4Buttons ? View.VISIBLE : View.GONE);
                VocaKnow.updateImageViewStyleByVocaKnow(context, ivBig, tvBig, voca.getVocaKnow());
            } else {
                v4Buttons.setVisibility(View.GONE);
                tvBig.setVisibility(View.VISIBLE);
                Voca.updateTextViewEvaluateGradeStyle(
                        ivBig, tvBig,
                        voca.isDisplayEvaluation() ? voca.getEvaluateVocaGrade() : "",
                        voca.isDisplayEvaluation() ? voca.getEvaluateVocaGradeTutors() : ""
                );
                VocaKnow.updateImageViewStyleByVocaKnow(context, ivSmall, tvSmall, voca.getVocaKnow());
            }
            v3Buttons.setVisibility(studyRole == Constant.STUDY_ROLE_TUTOR ? View.VISIBLE : View.GONE);
            tvSmall.setVisibility(View.VISIBLE);
        } else {
            v4Buttons.setVisibility(View.GONE);
            v3Buttons.setVisibility(View.GONE);
            tvBig.setVisibility(View.GONE);
            tvSmall.setVisibility(View.GONE);
        }
    }

    private void bindKnownPronounce() {
        if (voca.getVocaKnow() == Constant.VOCA_KNOW.VOCA_KNOW_KNOWN && voca.getVocaKnowPronounce() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN) {
            icUnknownPronounce.setVisibility(View.VISIBLE);
        } else {
            icUnknownPronounce.setVisibility(View.GONE);
        }
    }

    private void bindIconPlay() {
        if (isStudentAndTutorJoined) {
            icPlay.setColorFilter(null);
            icPlay.setImageResource(R.drawable.ic_sync);
        } else {
            Voca.updateIconSpeaker(icPlay, voca);
        }
    }

    private void bindAnswer() {
        if (voca instanceof VocaStudyChatExam) {
            VocaStudyChatExam vocaExam = (VocaStudyChatExam) voca;
            if (vocaExam.getExamType() == Constant.EXAM_TYPE_NORMAL) {
                vBtn1.setVisibility(View.GONE);
                vBtn2.setVisibility(View.GONE);
            } else {
                vBtn1.setVisibility(View.VISIBLE);
                vBtn2.setVisibility(View.VISIBLE);
                bindAnswerButtons(vocaExam);
            }
        } else {
            vBtn1.setVisibility(View.GONE);
            vBtn2.setVisibility(View.GONE);
        }
    }

    private void bindAnswerButtons(VocaStudyChatExam vocaExam) {
        btn1.setBackgroundResource(
                vocaExam.isAnswer1Selected()
                        ? (vocaExam.getCorrectAnswerNumber() == 1 ? R.drawable.btn_no_border_blue_background : R.drawable.btn_no_border_red_background)
                        : R.drawable.btn_no_border_grey_background
        );
        btn1.setText(vocaExam.getAnswer1());
        btn2.setBackgroundResource(
                vocaExam.isAnswer2Selected()
                        ? (vocaExam.getCorrectAnswerNumber() == 2 ? R.drawable.btn_no_border_blue_background : R.drawable.btn_no_border_red_background)
                        : R.drawable.btn_no_border_grey_background
        );
        btn2.setText(vocaExam.getAnswer2());
        btn3.setBackgroundResource(
                vocaExam.isAnswer3Selected()
                        ? (vocaExam.getCorrectAnswerNumber() == 3 ? R.drawable.btn_no_border_blue_background : R.drawable.btn_no_border_red_background)
                        : R.drawable.btn_no_border_grey_background
        );
        btn3.setText(vocaExam.getAnswer3());
        btn4.setBackgroundResource(
                vocaExam.isAnswer4Selected()
                        ? (vocaExam.getCorrectAnswerNumber() == 4 ? R.drawable.btn_no_border_blue_background : R.drawable.btn_no_border_red_background)
                        : R.drawable.btn_no_border_grey_background
        );
        btn4.setText(vocaExam.getAnswer4());
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

    @OnClick({R.id.v_item, R.id.ic_play, R.id.tv_big, R.id.iv_big, R.id.fv_word, R.id.tv_word,
            R.id.btn_known, R.id.btn_grade_1, R.id.btn_grade_2, R.id.btn_exclude,
            R.id.btn_a, R.id.btn_b, R.id.btn_c, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4})
    void onClick(View view) {
        if (listener != null && !hasHeader) {
            voca.setTblSection(0);
            voca.setTblRow(getAdapterPosition() - (hasBookName ? 1 : 0));
        }
        int id = view.getId();
        switch (id) {
            case R.id.v_item:
            case R.id.fv_word:
                handleClickItem();
                break;
            case R.id.ic_play:
                if (listener != null) {
                    listener.onPlayClick(voca);
                }
                break;
            case R.id.tv_big:
            case R.id.iv_big:
                if (listener != null) {
                    listener.onBigIconClick(voca);
                }
                break;
            case R.id.tv_word:
                if (studyRole == Constant.STUDY_ROLE_STUDENT && showAsterisk == Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY) {
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
            case R.id.btn1:
                if (studyRole == Constant.STUDY_ROLE_STUDENT && voca instanceof VocaStudyChatExam) {
                    VocaStudyChatExam vocaExam = (VocaStudyChatExam) voca;
                    vocaExam.setAnswer1Selected(true);
                    vocaExam.setLastSelectedAnswer(1);
                    bindAnswerButtons(vocaExam);
                    if (listener != null) {
                        listener.onAnswerClick(vocaExam);
                    }
                }
                break;
            case R.id.btn2:
                if (studyRole == Constant.STUDY_ROLE_STUDENT && voca instanceof VocaStudyChatExam) {
                    VocaStudyChatExam vocaExam = (VocaStudyChatExam) voca;
                    vocaExam.setAnswer2Selected(true);
                    vocaExam.setLastSelectedAnswer(2);
                    bindAnswerButtons(vocaExam);
                    if (listener != null) {
                        listener.onAnswerClick(vocaExam);
                    }
                }
                break;
            case R.id.btn3:
                if (studyRole == Constant.STUDY_ROLE_STUDENT && voca instanceof VocaStudyChatExam) {
                    VocaStudyChatExam vocaExam = (VocaStudyChatExam) voca;
                    vocaExam.setAnswer3Selected(true);
                    vocaExam.setLastSelectedAnswer(3);
                    bindAnswerButtons(vocaExam);
                    if (listener != null) {
                        listener.onAnswerClick(vocaExam);
                    }
                }
                break;
            case R.id.btn4:
                if (studyRole == Constant.STUDY_ROLE_STUDENT && voca instanceof VocaStudyChatExam) {
                    VocaStudyChatExam vocaExam = (VocaStudyChatExam) voca;
                    vocaExam.setAnswer4Selected(true);
                    vocaExam.setLastSelectedAnswer(4);
                    bindAnswerButtons(vocaExam);
                    if (listener != null) {
                        listener.onAnswerClick(vocaExam);
                    }
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
}
