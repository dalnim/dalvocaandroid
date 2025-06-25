package com.dalread.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.dalread.R;
import com.dalread.base.BaseLessonListFragment;
import com.dalread.component.Toolbar;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.LessonOption;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.Arrays;
import java.util.List;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class LessonSettingsFragment extends BaseLessonListFragment {

    @BindView(R.id.header)
    Toolbar toolbar;
    @BindView(R.id.sc_enable_notification)
    SwitchCompat scEnableNotification;
    @BindView(R.id.tv_before_start)
    TextView tvBeforeStart;
    @BindView(R.id.tv_before_finish)
    TextView tvBeforeFinish;
    @BindView(R.id.v_my_info)
    View vMyInfo;
    @BindView(R.id.sc_lead_lesson)
    SwitchCompat scLeadLesson;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.sc_free_talking)
    SwitchCompat scFreeTalking;
    @BindView(R.id.sc_small_talk)
    SwitchCompat scSmallTalk;
    @BindView(R.id.tv_correct_pronunciation)
    TextView tvCorrectPronunciation;
    @BindView(R.id.tv_speaking_speed)
    TextView tvSpeakingSpeed;
    @BindView(R.id.tv_reading_speed)
    TextView tvReadingSpeed;
    @BindView(R.id.tv_repeat_topics)
    TextView tvRepeatTopics;
    @BindView(R.id.tv_repeat_count)
    TextView tvRepeatCount;
    @BindView(R.id.tv_exam_type)
    TextView tvExamType;
    @BindView(R.id.sc_record_lesson)
    SwitchCompat scRecordLesson;
    @BindView(R.id.v_study_order)
    View vStudyOrder;
    @BindView(R.id.tv_phrases_to_study)
    TextView tvPhrasesToStudy;
    @BindView(R.id.sc_hide_parts_of_words)
    SwitchCompat scHidePartsOfWords;
    @BindView(R.id.sc_hide_all_phrases)
    SwitchCompat scHideAllPhrases;
    @BindView(R.id.sc_random_questions)
    SwitchCompat scRandomQuestions;

    @BindArray(R.array.level_options)
    String[] levelOptions;
    @BindArray(R.array.correct_pronunciation_options)
    String[] correctPronunciationOptions;
    @BindArray(R.array.speaking_speed_options)
    String[] speakingSpeedOptions;
    @BindArray(R.array.reading_speed_options)
    String[] readingSpeedOptions;
    @BindArray(R.array.exam_type_options)
    String[] examTypeOptions;

    private Context context;
    private String[] minuteValues, repeatTopicsValues, repeatCountValues, phrasesToStudyValues;
    private int selectedStartPos, selectedFinishPos, selectedLevelPos, selectedCorrectPronunciationPos,
            selectedSpeakingSpeedPos, selectedReadingSpeedPos, selectedRepeatTopicsPos,
            selectedRepeatCountPos, selectedExamTypePos, selectedPhrasesToStudyPos;
    private SingleChoiceDialog singleChoiceDialog;
    private int lessonType;
    private AlertDialog alertDialog;
    private LessonOption lessonOption;
    private byte[] backupBytes;
    private int otherUserId;
    private String otherUserName;


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_lesson_settings;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initLayout();

        getLessonOption();
    }

    private void initData() {
        context = getContext();
        minuteValues = Voca.getMinuteValues();
        repeatTopicsValues = Voca.getRepeatTopicsValues();
        repeatCountValues = Voca.getRepeatCountValues();
        phrasesToStudyValues = Voca.getPhrasesToStudyValues();
        selectedStartPos = selectedFinishPos = selectedLevelPos = selectedCorrectPronunciationPos
                = selectedSpeakingSpeedPos = selectedReadingSpeedPos = selectedRepeatTopicsPos
                = selectedRepeatCountPos = selectedExamTypePos = selectedPhrasesToStudyPos = -1;
        lessonType = getArguments().getInt(Constant.BUNDLE.KEY_LESSON_TYPE);
        otherUserId = getArguments().getInt(Constant.BUNDLE.KEY_OTHER_USER_ID);
        otherUserName = getArguments().getString(Constant.BUNDLE.KEY_OTHER_USER_NAME);
    }

    private void initLayout() {
        if (!TextUtils.isEmpty(otherUserName)) {
            toolbar.setTitle(otherUserName);
        }
        toolbar.hideTvRight();
        singleChoiceDialog = new SingleChoiceDialog(context);
        vMyInfo.setVisibility(lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT ? View.VISIBLE : View.GONE);
        vStudyOrder.setVisibility(lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT ? View.VISIBLE : View.GONE);
        alertDialog = new AlertDialog(context);
    }

    private void getLessonOption() {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            application.getDalAiImpl().getLessonOption(
                    otherUserId > 0 ? otherUserId : sharedPreferences.getRealUid(),
                    lessonType,
                    new DalApiListener<LessonOption>() {

                        @Override
                        public void onSuccess(LessonOption response) {
                            lessonOption = response;
                            backupBytes = Voca.backupObject(lessonOption);
                            bindData();
                            Loading.hide();
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                        }
                    });
        } else {
            alertDialog.showNoInternet();
        }
    }

    private void bindData() {
        if (lessonOption != null) {
            scEnableNotification.setChecked(lessonOption.getEnableLessonPN() == Constant.API_VALUE.ENABLE_LESSON_PN_YES);
            List minuteList = Arrays.asList(minuteValues);
            selectedStartPos = minuteList.indexOf(String.valueOf(lessonOption.getPnMinutesBeforeBeginLesson()));
            setBeforeStartText();
            selectedFinishPos = minuteList.indexOf(String.valueOf(lessonOption.getPnMinutesBeforeFinishLesson()));
            setBeforeFinishText();
            if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
                scLeadLesson.setChecked(lessonOption.getLeadLesson() == Constant.API_VALUE.LEAD_LESSON_YES);
                selectedLevelPos = lessonOption.getLanguageLevel() - 1;
                setLevelText();
                scFreeTalking.setChecked(lessonOption.getFreeTalking() == Constant.API_VALUE.FREE_TALKING_YES);
                scSmallTalk.setChecked(lessonOption.getSmallTalking() == Constant.API_VALUE.SMALL_TALKING_YES);
                selectedCorrectPronunciationPos = lessonOption.getCorrectPronunciation();
                setCorrectPronunciationText();
                selectedSpeakingSpeedPos = lessonOption.getSpeakingSpeed() - 1;
                setSpeakingSpeedText();
                selectedReadingSpeedPos = lessonOption.getReadingSpeed() - 1;
                setReadingSpeedText();
                selectedRepeatTopicsPos = Arrays.asList(repeatTopicsValues).indexOf(String.valueOf(lessonOption.getRepeatTopics()));
                setRepeatTopicsText();
                selectedRepeatCountPos = Arrays.asList(repeatCountValues).indexOf(String.valueOf(lessonOption.getRepeatCount()));
                setRepeatCountText();
                selectedExamTypePos = lessonOption.getUserWordExamType() - 1;
                setExamTypeText();
                scRecordLesson.setChecked(lessonOption.isRecordLesson());
                selectedPhrasesToStudyPos = Arrays.asList(phrasesToStudyValues).indexOf(String.valueOf(lessonOption.getCountOfPhrasesToStudyAtOnce()));
                setPhrasesToStudyText();
                scHidePartsOfWords.setChecked(lessonOption.isStudyOrderHidePartOfWords());
                scHideAllPhrases.setChecked(lessonOption.isStudyOrderHideAllPhrases());
                scRandomQuestions.setChecked(lessonOption.isStudyOrderRandomQuestions());
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }

    @OnCheckedChanged({
            R.id.sc_enable_notification, R.id.sc_lead_lesson, R.id.sc_free_talking, R.id.sc_small_talk, R.id.sc_record_lesson,
            R.id.sc_hide_parts_of_words, R.id.sc_hide_all_phrases, R.id.sc_random_questions
    })
    void onCheckedChanged(CompoundButton button, boolean checked) {
        int id = button.getId();
        switch (id) {
            case R.id.sc_enable_notification:
                lessonOption.setEnableLessonPN(checked ? Constant.API_VALUE.ENABLE_LESSON_PN_YES : Constant.API_VALUE.ENABLE_LESSON_PN_NO);
                if (activity != null) {
                    if (checked) {
                        activity.scheduleNotification();
                    } else {
                        activity.cancelOldNotifications();
                    }
                }
                break;
            case R.id.sc_lead_lesson:
                lessonOption.setLeadLesson(checked ? Constant.API_VALUE.LEAD_LESSON_YES : Constant.API_VALUE.LEAD_LESSON_NO);
                break;
            case R.id.sc_free_talking:
                lessonOption.setFreeTalking(checked ? Constant.API_VALUE.FREE_TALKING_YES : Constant.API_VALUE.FREE_TALKING_NO);
                break;
            case R.id.sc_small_talk:
                lessonOption.setSmallTalking(checked ? Constant.API_VALUE.SMALL_TALKING_YES : Constant.API_VALUE.SMALL_TALKING_NO);
                break;
            case R.id.sc_record_lesson:
                lessonOption.setRecordLesson(checked ? Constant.API_VALUE.RECORD_LESSON_YES : Constant.API_VALUE.RECORD_LESSON_NO);
                break;
            case R.id.sc_hide_parts_of_words:
                lessonOption.setStudyOrderHidePartOfWords(checked ? Constant.API_VALUE.IS_YES : Constant.API_VALUE.IS_NO);
                break;
            case R.id.sc_hide_all_phrases:
                lessonOption.setStudyOrderHideAllPhrases(checked ? Constant.API_VALUE.IS_YES : Constant.API_VALUE.IS_NO);
                break;
            case R.id.sc_random_questions:
                lessonOption.setStudyOrderRandomQuestions(checked ? Constant.API_VALUE.IS_YES : Constant.API_VALUE.IS_NO);
                break;
            default:
                break;
        }
        checkDataChangedAndShowHideSaveButton();
    }

    @OnClick({
            R.id.ic_left, R.id.tv_right, R.id.v_before_start, R.id.v_before_finish,
            R.id.v_level, R.id.v_correct_pronunciation, R.id.v_speaking_speed, R.id.v_reading_speed,
            R.id.v_repeat_topics, R.id.v_exam_type, R.id.v_repeat_count, R.id.v_phrases_to_study
    })
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_left:
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                break;
            case R.id.tv_right:
                setLessonOption();
                break;
            case R.id.v_before_start:
                singleChoiceDialog.show(
                        R.string.before_begin_class, minuteValues, selectedStartPos, R.string.ok, R.string.cancel, onTimeToNotifyBeforeLessonStartClickListener
                );
                break;
            case R.id.v_before_finish:
                singleChoiceDialog.show(
                        R.string.before_finish_class, minuteValues, selectedFinishPos, R.string.ok, R.string.cancel, onTimeToNotifyBeforeLessonFinishClickListener
                );
                break;
            case R.id.v_level:
                singleChoiceDialog.show(
                        R.string.student_level, levelOptions, selectedLevelPos, R.string.ok, R.string.cancel, onLevelClickListener
                );
                break;
            case R.id.v_correct_pronunciation:
                singleChoiceDialog.show(
                        R.string.correct_pronunciation, correctPronunciationOptions, selectedCorrectPronunciationPos, R.string.ok, R.string.cancel, onCorrectPronunciationClickListener
                );
                break;
            case R.id.v_speaking_speed:
                singleChoiceDialog.show(
                        R.string.speaking_speed, speakingSpeedOptions, selectedSpeakingSpeedPos, R.string.ok, R.string.cancel, onSpeakingSpeedClickListener
                );
                break;
            case R.id.v_reading_speed:
                singleChoiceDialog.show(
                        R.string.reading_speed, readingSpeedOptions, selectedReadingSpeedPos, R.string.ok, R.string.cancel, onReadingSpeedClickListener
                );
                break;
            case R.id.v_repeat_topics:
                singleChoiceDialog.show(
                        R.string.repeat_topics, repeatTopicsValues, selectedRepeatTopicsPos, R.string.ok, R.string.cancel, onRepeatTopicsClickListener
                );
                break;
            case R.id.v_repeat_count:
                singleChoiceDialog.show(
                        R.string.repeat_count, repeatCountValues, selectedRepeatCountPos, R.string.ok, R.string.cancel, onRepeatCountClickListener
                );
                break;
            case R.id.v_exam_type:
                singleChoiceDialog.show(
                        R.string.exam_type, examTypeOptions, selectedExamTypePos, R.string.ok, R.string.cancel, onExamTypeClickListener
                );
                break;
            case R.id.v_phrases_to_study:
                singleChoiceDialog.show(
                        R.string.phrases_to_study, phrasesToStudyValues, selectedPhrasesToStudyPos, R.string.ok, R.string.cancel, onPhrasesToStudyClickListener
                );
                break;
            default:
                break;
        }
    }

    private OnClickDialogListener onTimeToNotifyBeforeLessonStartClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedStartPos) {
                lessonOption.setPnMinutesBeforeBeginLesson(Utils.parseInt(minuteValues[selectedStartPos = which]));
                setBeforeStartText();
                if (activity != null) {
                    activity.cancelOldNotifications();
                    activity.scheduleNotification();
                }
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setBeforeStartText() {
        int minutes = lessonOption.getPnMinutesBeforeBeginLesson();
        String text = minutes + " " + getResources().getQuantityString(R.plurals.minute, minutes);
        tvBeforeStart.setText(text);
    }

    private OnClickDialogListener onTimeToNotifyBeforeLessonFinishClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedFinishPos) {
                lessonOption.setPnMinutesBeforeFinishLesson(Utils.parseInt(minuteValues[selectedFinishPos = which]));
                setBeforeFinishText();
                if (activity != null) {
                    activity.cancelOldNotifications();
                    activity.scheduleNotification();
                }
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setBeforeFinishText() {
        int minutes = lessonOption.getPnMinutesBeforeFinishLesson();
        String text = minutes + " " + getResources().getQuantityString(R.plurals.minute, minutes);
        tvBeforeFinish.setText(text);
    }

    private OnClickDialogListener onLevelClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedLevelPos) {
                selectedLevelPos = which;
                lessonOption.setLanguageLevel(selectedLevelPos + 1);
                setLevelText();
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setLevelText() {
        String text;
        try {
            text = levelOptions[selectedLevelPos];
        } catch (Exception e) {
            text = "";
        }
        tvLevel.setText(text);
    }

    private OnClickDialogListener onCorrectPronunciationClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedCorrectPronunciationPos) {
                selectedCorrectPronunciationPos = which;
                lessonOption.setCorrectPronunciation(selectedCorrectPronunciationPos);
                setCorrectPronunciationText();
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setCorrectPronunciationText() {
        String text;
        try {
            text = correctPronunciationOptions[selectedCorrectPronunciationPos];
        } catch (Exception e) {
            text = "";
        }
        tvCorrectPronunciation.setText(text);
    }

    private OnClickDialogListener onSpeakingSpeedClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedSpeakingSpeedPos) {
                selectedSpeakingSpeedPos = which;
                lessonOption.setSpeakingSpeed(selectedSpeakingSpeedPos + 1);
                setSpeakingSpeedText();
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setSpeakingSpeedText() {
        String text;
        try {
            text = speakingSpeedOptions[selectedSpeakingSpeedPos];
        } catch (Exception e) {
            text = "";
        }
        tvSpeakingSpeed.setText(text);
    }

    private OnClickDialogListener onReadingSpeedClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedReadingSpeedPos) {
                selectedReadingSpeedPos = which;
                lessonOption.setReadingSpeed(selectedReadingSpeedPos + 1);
                setReadingSpeedText();
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setReadingSpeedText() {
        String text;
        try {
            text = readingSpeedOptions[selectedReadingSpeedPos];
        } catch (Exception e) {
            text = "";
        }
        tvReadingSpeed.setText(text);
    }

    private OnClickDialogListener onRepeatTopicsClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedRepeatTopicsPos) {
                lessonOption.setRepeatTopics(Utils.parseInt(repeatTopicsValues[selectedRepeatTopicsPos = which]));
                setRepeatTopicsText();
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setRepeatTopicsText() {
        int repeatTopics = lessonOption.getRepeatTopics();
        String text = getResources().getQuantityString(R.plurals.tpl_topics, repeatTopics, repeatTopics);
        tvRepeatTopics.setText(text);
    }

    private OnClickDialogListener onRepeatCountClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedRepeatCountPos) {
                lessonOption.setRepeatCount(Utils.parseInt(repeatCountValues[selectedRepeatCountPos = which]));
                setRepeatCountText();
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setRepeatCountText() {
        int repeatCount = lessonOption.getRepeatCount();
        String text = getResources().getQuantityString(R.plurals.tpl_times, repeatCount, repeatCount);
        tvRepeatCount.setText(text);
    }

    private OnClickDialogListener onExamTypeClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedExamTypePos) {
                selectedExamTypePos = which;
                lessonOption.setUserWordExamType(selectedExamTypePos + 1);
                setExamTypeText();
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setExamTypeText() {
        String text = examTypeOptions[selectedExamTypePos];
        tvExamType.setText(text);
    }

    private OnClickDialogListener onPhrasesToStudyClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedPhrasesToStudyPos) {
                lessonOption.setCountOfPhrasesToStudyAtOnce(Utils.parseInt(phrasesToStudyValues[selectedPhrasesToStudyPos = which]));
                setPhrasesToStudyText();
                checkDataChangedAndShowHideSaveButton();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private void setPhrasesToStudyText() {
        String text = phrasesToStudyValues[selectedPhrasesToStudyPos];
        tvPhrasesToStudy.setText(text);
    }

    public boolean checkDataChanged() {
        return !Arrays.equals(backupBytes, Voca.backupObject(lessonOption));
    }

    private void checkDataChangedAndShowHideSaveButton() {
        toolbar.getTvRight().setVisibility(checkDataChanged() ? View.VISIBLE : View.GONE);
    }

    private void setLessonOption() {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            application.getDalAiImpl().setLessonOption(otherUserId, lessonType, lessonOption, new DalApiListener<Boolean>() {

                @Override
                public void onSuccess(Boolean response) {
                    if (response) {
                        backupBytes = Voca.backupObject(lessonOption);
                        toolbar.hideTvRight();
                        if (getActivity() instanceof OnSaveFinishListener) {
                            ((OnSaveFinishListener) getActivity()).onFinish(true);
                        }
                    }
                    Loading.hide();
                }

                @Override
                public void onFailure(String error) {
                    Loading.hide();
                }
            });
        } else {
            alertDialog.showNoInternet();
        }
    }

    public void confirmSave(OnSaveFinishListener listener) {
        new ConfirmationDialog(context, R.string.warning, R.string.msg_warning_save, R.string.yes, R.string.no, new ConfirmationDialog.OnDialogClickListener() {

            @Override
            public void onPositive(DialogInterface dialog) {
                dialog.dismiss();
                if (Utils.isConnected(context)) {
                    Loading.show(context);
                    application.getDalAiImpl().setLessonOption(otherUserId, lessonType, lessonOption, new DalApiListener<Boolean>() {

                        @Override
                        public void onSuccess(Boolean response) {
                            Loading.hide();
                            if (listener != null) {
                                listener.onFinish(response);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                            if (listener != null) {
                                listener.onFinish(false);
                            }
                        }
                    });
                } else {
                    alertDialog.showNoInternet();
                }
            }

            @Override
            public void onNegative(DialogInterface dialog) {
                dialog.dismiss();
                if (listener != null) {
                    listener.onFinish(false);
                }
            }
        }).show();
    }

    public interface OnSaveFinishListener {

        void onFinish(boolean success);
    }
}
