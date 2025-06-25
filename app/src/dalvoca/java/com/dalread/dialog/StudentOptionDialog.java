package com.dalread.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.base.BaseDialog;
import com.dalread.model.Lesson;
import com.dalread.model.LessonOption;
import com.dalread.util.Constant;

import butterknife.BindArray;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StudentOptionDialog extends BaseDialog {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_lead_lesson)
    TextView tvLeadLesson;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_correct_pronunciation)
    TextView tvCorrectPronunciation;
    @BindView(R.id.tv_speaking_speed)
    TextView tvSpeakingSpeed;
    @BindView(R.id.tv_reading_speed)
    TextView tvReadingSpeed;
    @BindView(R.id.tv_free_talking)
    TextView tvFreeTalking;
    @BindView(R.id.tv_small_talk)
    TextView tvSmallTalk;

    @BindString(R.string.tpl_colon)
    String tplColon;
    @BindString(R.string.lead_lesson)
    String strLeadLesson;
    @BindString(R.string.student)
    String strStudent;
    @BindString(R.string.tutor)
    String strTutor;
    @BindString(R.string.student_level)
    String strLevel;
    @BindString(R.string.correct_pronunciation)
    String strCorrectPronunciation;
    @BindString(R.string.speaking_speed)
    String strSpeakingSpeed;
    @BindString(R.string.reading_speed)
    String strReadingSpeed;
    @BindString(R.string.free_talking)
    String strFreeTalking;
    @BindString(R.string.small_talk)
    String strSmallTalk;
    @BindString(R.string.lesson_do)
    String strDo;
    @BindString(R.string.lesson_donot)
    String strDonot;
    @BindString(R.string.name)
    String strName;

    @BindArray(R.array.level_options)
    String[] levelOptions;
    @BindArray(R.array.correct_pronunciation_options)
    String[] correctPronunciationOptions;
    @BindArray(R.array.speaking_speed_options)
    String[] speakingSpeedOptions;
    @BindArray(R.array.reading_speed_options)
    String[] readingSpeedOptions;

    public StudentOptionDialog(@NonNull Context context) {
        super(context, R.style.TransparentDialog);

        setContentView(R.layout.dialog_tutor_lesson);
        ButterKnife.bind(this);
    }

    public void show(Lesson lesson, LessonOption lessonOption) {
        if (lesson == null || lessonOption == null)
            return;
        String text = strName + " : " + lesson.getStudentName();
        tvTitle.setText(text);
        text = strLeadLesson + String.format(tplColon, lessonOption.getLeadLesson() == Constant.API_VALUE.LEAD_LESSON_YES ? strStudent : strTutor);
        tvLeadLesson.setText(text);
        text = strCorrectPronunciation + String.format(tplColon, correctPronunciationOptions[lessonOption.getCorrectPronunciation()]);
        tvCorrectPronunciation.setText(text);
        text = strFreeTalking + String.format(tplColon, lessonOption.getFreeTalking() == Constant.API_VALUE.FREE_TALKING_YES ? strDo : strDonot);
        tvFreeTalking.setText(text);
        text = strSmallTalk + String.format(tplColon, lessonOption.getSmallTalking() == Constant.API_VALUE.SMALL_TALKING_YES ? strDo : strDonot);
        tvSmallTalk.setText(text);
        if (lessonOption.getLanguageLevel() > 0) {
            text = strLevel + String.format(tplColon, levelOptions[lessonOption.getLanguageLevel() - 1]);
        } else {
            text = strLevel + String.format(tplColon, levelOptions[0]);
        }
        tvLevel.setText(text);
        if (lessonOption.getSpeakingSpeed() > 0) {
            text = strSpeakingSpeed + String.format(tplColon, speakingSpeedOptions[lessonOption.getSpeakingSpeed() - 1]);
        } else {
            text = strSpeakingSpeed + String.format(tplColon, speakingSpeedOptions[0]);
        }
        tvSpeakingSpeed.setText(text);
        if (lessonOption.getReadingSpeed() > 0) {
            text = strReadingSpeed + String.format(tplColon, readingSpeedOptions[lessonOption.getReadingSpeed() - 1]);
        } else {
            text = strReadingSpeed + String.format(tplColon, readingSpeedOptions[0]);
        }
        tvReadingSpeed.setText(text);
        show();
    }

    @OnClick({R.id.tv_ok})
    void onClick() {
        dismiss();
    }
}
