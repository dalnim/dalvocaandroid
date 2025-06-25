package com.dalread.adapter.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.listener.OnVocaStudyChatExamClickListener;
import com.dalread.model.VocaStudyChatExam;
import com.dalread.util.Constant;
import com.dalread.util.Voca;
import com.dalread.util.VocaKnow;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StudyChatExamItemHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.v_item)
    View vItem;
    @BindView(R.id.tv_index)
    TextView tvIndex;
    @BindView(R.id.tv_study_count)
    TextView tvStudyCount;
    @BindView(R.id.tv_word)
    TextView tvWord;
    @BindView(R.id.tv_meaning)
    TextView tvMeaning;
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

    private VocaStudyChatExam voca;
    private int studyRole;
    private OnVocaStudyChatExamClickListener listener;
    private Context context;
    public StudyChatExamItemHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void bind(Context context, VocaStudyChatExam voca, int studyRole, boolean displayPronunciation, boolean isStudentAndTutorJoined,
                     boolean showStudentMeaning, OnVocaStudyChatExamClickListener listener) {
        this.context = context;
        this.voca = voca;
        this.studyRole = studyRole;
        this.listener = listener;

        String text = String.valueOf(voca.getIndex());
        if (!TextUtils.isEmpty(voca.getPersonAB())) {
            text += " " + voca.getPersonAB();
        }
        tvIndex.setText(text);

        text = String.valueOf(voca.getStudyCount());
        text = String.format(tplStudyCount, text);
        tvStudyCount.setText(text);

        if (studyRole == Constant.STUDY_ROLE_STUDENT) {
            if (voca.getExamType() == Constant.EXAM_TYPE_QUESTION_MEANING) {
                text = "";
            } else if (voca.getExamType() == Constant.EXAM_TYPE_QUESTION_WORD) {
                text = Voca.getVocaDisplay(voca);
                if ((voca.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                        && displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
                    text += " [" + voca.getPronounce() + "]";
                }
            } else {
                text = Voca.changeVocaWithSmartAsterisk(Voca.getVocaDisplay(voca), voca.getVocaKnow());
            }
        } else {
            text = Voca.getVocaDisplay(voca);
//            if (studyRole == Constant.STUDY_ROLE_OBSERVER
//                    && (voca.getAmkiGrade() != Constant.AMKI_GRADE.VALUE_KNOWN || voca.getKnow() != Constant.KNOW.KNOWN)
//                    && displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {
            if ((voca.getVocaKnow() != Constant.VOCA_KNOW.VOCA_KNOW_KNOWN)
                    && displayPronunciation && !TextUtils.isEmpty(voca.getPronounce())) {

                text += " [" + voca.getPronounce() + "]";
            }
        }
        tvWord.setText(text);

        if (studyRole == Constant.STUDY_ROLE_STUDENT) {
            if (voca.getExamType() == Constant.EXAM_TYPE_QUESTION_WORD) {
                text = "";
            } else {
                if (showStudentMeaning) {
                    text = voca.getMeaning();
                } else {
                    text = "";
                }
            }
        } else {
            text = voca.getMeaning();
            if (!showStudentMeaning) {
                if (TextUtils.isEmpty(text)) {
                    text = strStudentMeaningIsHided;
                } else {
                    text = strStudentMeaningIsHided + "\n" + text;
                }
            }
        }
        tvMeaning.setText(text);

        bindGrade();

        if (isStudentAndTutorJoined) {
            icPlay.setColorFilter(null);
            icPlay.setImageResource(R.drawable.ic_sync);
        } else {
            Voca.updateIconSpeaker(icPlay, voca);
        }

        if (voca.getExamType() == Constant.EXAM_TYPE_NORMAL) {
            vBtn1.setVisibility(View.GONE);
            vBtn2.setVisibility(View.GONE);
        } else {
            bindButtons();
            vBtn1.setVisibility(View.VISIBLE);
            vBtn2.setVisibility(View.VISIBLE);
        }

        if (voca.isVIChecked()) { // use key checked for blink mode
            vItem.setBackgroundResource(R.color.color_blink);
            vItem.postDelayed(normalItemRunnable, Constant.STOP_BLINK_DELAY_TIME);
        } else {
            vItem.setBackgroundResource(R.drawable.btn_button);
        }
    }

    private void bindGrade() {
        if (studyRole == Constant.STUDY_ROLE_STUDENT) {
            Voca.updateTextViewEvaluateGradeStyle(
                    ivSmall, tvSmall,
                    voca.isDisplayEvaluation() ? voca.getEvaluateVocaGrade() : "",
                    voca.isDisplayEvaluation() ? voca.getEvaluateVocaGradeTutors() : ""
            );
            VocaKnow.updateImageViewStyleByVocaKnow(context, ivBig, tvBig, voca.getVocaKnow());
        } else {
            tvBig.setVisibility(View.VISIBLE);
            Voca.updateTextViewEvaluateGradeStyle(
                    ivBig, tvBig,
                    voca.isDisplayEvaluation() ? voca.getEvaluateVocaGrade() : "",
                    voca.isDisplayEvaluation() ? voca.getEvaluateVocaGradeTutors() : ""
            );
            VocaKnow.updateImageViewStyleByVocaKnow(context, ivSmall, tvSmall, voca.getVocaKnow());
        }
        v3Buttons.setVisibility(studyRole == Constant.STUDY_ROLE_TUTOR ? View.VISIBLE : View.GONE);
    }

    private void bindButtons() {
        btn1.setBackgroundResource(
                voca.isAnswer1Selected()
                        ? (voca.getCorrectAnswerNumber() == 1 ? R.drawable.btn_no_border_blue_background : R.drawable.btn_no_border_red_background)
                        : R.drawable.btn_no_border_grey_background
        );
        btn1.setText(voca.getAnswer1());
        btn2.setBackgroundResource(
                voca.isAnswer2Selected()
                        ? (voca.getCorrectAnswerNumber() == 2 ? R.drawable.btn_no_border_blue_background : R.drawable.btn_no_border_red_background)
                        : R.drawable.btn_no_border_grey_background
        );
        btn2.setText(voca.getAnswer2());
        btn3.setBackgroundResource(
                voca.isAnswer3Selected()
                        ? (voca.getCorrectAnswerNumber() == 3 ? R.drawable.btn_no_border_blue_background : R.drawable.btn_no_border_red_background)
                        : R.drawable.btn_no_border_grey_background
        );
        btn3.setText(voca.getAnswer3());
        btn4.setBackgroundResource(
                voca.isAnswer4Selected()
                        ? (voca.getCorrectAnswerNumber() == 4 ? R.drawable.btn_no_border_blue_background : R.drawable.btn_no_border_red_background)
                        : R.drawable.btn_no_border_grey_background
        );
        btn4.setText(voca.getAnswer4());
    }

    @OnClick({R.id.ic_play, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.tv_big, R.id.iv_big,
            R.id.btn_a, R.id.btn_b, R.id.btn_c})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_play:
                if (listener != null) {
                    listener.onPlayClick(voca);
                }
                break;
            case R.id.btn1:
                if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                    voca.setAnswer1Selected(true);
                    voca.setLastSelectedAnswer(1);
                    bindButtons();
                    if (listener != null) {
                        listener.onButtonClick(voca);
                    }
                }
                break;
            case R.id.btn2:
                if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                    voca.setAnswer2Selected(true);
                    voca.setLastSelectedAnswer(2);
                    bindButtons();
                    if (listener != null) {
                        listener.onButtonClick(voca);
                    }
                }
                break;
            case R.id.btn3:
                if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                    voca.setAnswer3Selected(true);
                    voca.setLastSelectedAnswer(3);
                    bindButtons();
                    if (listener != null) {
                        listener.onButtonClick(voca);
                    }
                }
                break;
            case R.id.btn4:
                if (studyRole == Constant.STUDY_ROLE_STUDENT) {
                    voca.setAnswer4Selected(true);
                    voca.setLastSelectedAnswer(4);
                    bindButtons();
                    if (listener != null) {
                        listener.onButtonClick(voca);
                    }
                }
                break;
            case R.id.tv_big:
            case R.id.iv_big:
                if (listener != null) {
                    listener.onBigIconClick(voca);
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

    private Runnable normalItemRunnable = new Runnable() {

        @Override
        public void run() {
            vItem.setBackgroundResource(R.drawable.btn_button);
            voca.setVIChecked(false);
        }
    };
}
