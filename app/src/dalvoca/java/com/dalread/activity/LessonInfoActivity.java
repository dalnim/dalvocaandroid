package com.dalread.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.model.Lesson;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.OnClick;

public class LessonInfoActivity extends BaseVocaActivity {

    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_lesson_minutes)
    TextView tvLessonMinutes;
    @BindView(R.id.tv_tutor)
    TextView tvTutor;
    @BindView(R.id.tv_student)
    TextView tvStudent;
    @BindView(R.id.tv_study_lang)
    TextView tvStudyLang;
    @BindView(R.id.tv_lesson_type)
    TextView tvLessonType;

    @BindArray(R.array.lesson_type_options)
    String[] lessonTypeValues;

    private Context context;
    private Lesson lesson;
    private AlertDialog alertDialog;
    private SingleChoiceDialog singleChoiceDialog;
    private Calendar startTime;
    private DatePickerDialog startDatePickerDialog;
    private TimePickerDialog startTimePickerDialog;
    private String[] lessonMinutesValues;
    private int lessonMinutes;
    private int selectedLessonMinutesPos;
    private int studentUid;
    private String studentName;
    private int tutorUid;
    private String tutorName;
    private String[] studyLangValues;
    private int studyLang;
    private int selectedStudyLangPos;
    private int lessonType;
    private int selectedLessonTypePos;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lesson_info;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initLayout();
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

    }

    public void initData() {
        context = this;
        lesson = (Lesson) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_LESSON);
        //
        startTime = Calendar.getInstance();
        if (lesson == null) {
            startTime.set(Calendar.SECOND, 0);
            startTime.set(Calendar.MILLISECOND, 0);
        } else {
            startTime.setTimeInMillis(DateUtils.secondsToMillis(lesson.getLessonStartTimeTS()));
        }
        //
        lessonMinutesValues = Voca.getMinuteValues();
        if (lesson == null) {
            lessonMinutes = Constant.LESSON_MINUTES_DEFAULT;
        } else {
            lessonMinutes = (int) TimeUnit.SECONDS.toMinutes(lesson.getLessonFinishTimeTS() - lesson.getLessonStartTimeTS());
        }
        selectedLessonMinutesPos = Arrays.asList(lessonMinutesValues).indexOf(String.valueOf(lessonMinutes));
        //
        if (lesson != null) {
            studentUid = lesson.getStudentId();
            studentName = lesson.getStudentName();
            tutorUid = lesson.getTutorId();
            tutorName = lesson.getTutorName();
        }
        //
        studyLangValues = EnumLanguage.getStudyLanguages();
        if (lesson == null) {
            studyLang = sharedPreferences.getLangStudyCode();
        } else {
            studyLang = lesson.getStudyLangCode();
        }
        selectedStudyLangPos = -1;
        String studyLangFormatUser = EnumLanguage.findByIdApi(studyLang).getFormatUser();
        for (int i = 0; i < studyLangValues.length; i++) {
            if (studyLangValues[i].contains(studyLangFormatUser)) {
                selectedStudyLangPos = i;
                break;
            }
        }
        //
        if (lesson == null) {
            lessonType = Constant.API_VALUE.LESSON_TYPE_PHONE;
            selectedLessonTypePos = 0;
        } else {
            lessonType = lesson.getLessonTypeApi();
            selectedLessonTypePos = lessonType == Constant.API_VALUE.LESSON_TYPE_TEXT_CHAT ? 1 : 0;
        }
    }

    private void initLayout() {
        //
        alertDialog = new AlertDialog(context);
        singleChoiceDialog = new SingleChoiceDialog(context);
        //
        setStartTimeText();
        startDatePickerDialog = new DatePickerDialog(
                context, onStartDateSetListener, startTime.get(Calendar.YEAR), startTime.get(Calendar.MONTH), startTime.get(Calendar.DAY_OF_MONTH)
        );
        startTimePickerDialog = new TimePickerDialog(
                context, onStartTimeSetListener, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true
        );
        //
        setLessonMinutesText();
        //
        setStudentText();
        //
        setTutorText();
        //
        setStudyText();
        //
        setLessonTypeText();
    }

    private void setStartTimeText() {
        String text = DateUtils.getDateFullNoSecondFormat().format(startTime.getTime());
        tvStartTime.setText(text);
    }

    private void setLessonMinutesText() {
        String text = String.valueOf(lessonMinutes);
        tvLessonMinutes.setText(text);
    }

    private void setStudentText() {
        tvStudent.setText(studentName);
    }

    private void setTutorText() {
        tvTutor.setText(tutorName);
    }

    private void setStudyText() {
        tvStudyLang.setText(studyLangValues[selectedStudyLangPos]);
    }

    private void setLessonTypeText() {
        tvLessonType.setText(lessonTypeValues[selectedLessonTypePos]);
    }

    @OnClick({R.id.v_start_time, R.id.v_lesson_minutes, R.id.v_student, R.id.v_tutor, R.id.v_study, R.id.v_lesson_type,
            R.id.btn_join, R.id.btn_save, R.id.btn_duplicate})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.v_start_time:
                startDatePickerDialog.show();
                break;
            case R.id.v_lesson_minutes:
                singleChoiceDialog.show(
                        R.string.lesson_minutes, lessonMinutesValues, selectedLessonMinutesPos,
                        R.string.ok, R.string.cancel, onLessonMinutesClickListener
                );
                break;
            case R.id.v_student:
                openUserLessonScreen(false);
                break;
            case R.id.v_tutor:
                openUserLessonScreen(true);
                break;
            case R.id.v_study:
                singleChoiceDialog.show(
                        R.string.study, studyLangValues, selectedStudyLangPos,
                        R.string.ok, R.string.cancel, onStudyClickListener
                );
                break;
            case R.id.v_lesson_type:
                singleChoiceDialog.show(
                        R.string.lesson_type, lessonTypeValues, selectedLessonTypePos,
                        R.string.ok, R.string.cancel, onLessonTypeClickListener
                );
                break;
            case R.id.btn_join:
                openLesson();
                break;
            case R.id.btn_save:
                saveLesson();
                break;
            case R.id.btn_duplicate:
                duplicateLesson();
                break;
            default:
                break;
        }
    }

    private void openUserLessonScreen(boolean isTutor) {
        Intent intent = new Intent(context, isTutor ? TutorUserLessonActivity.class : StudentUserLessonActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_STUDY_LANG, studyLang);
        startActivityForResult(intent, isTutor ? Constant.REQUEST_CODE.SELECT_TUTOR : Constant.REQUEST_CODE.SELECT_STUDENT);
    }

    private void openLesson() {
        if (lesson == null) return;
        Intent i = new Intent(context, ChatDetailsActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_LESSON, lesson);
        openNewScreen(i);
    }

    private void saveLesson() {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            long startTimeTS = DateUtils.millisToSeconds(startTime.getTimeInMillis());
            application.getDalAiImpl().updateOrMakeNewLesson(
                    String.valueOf(lesson == null ? 0 : lesson.getId()),
                    String.valueOf(studentUid),
                    String.valueOf(tutorUid),
                    String.valueOf(startTimeTS),
                    String.valueOf(startTimeTS + TimeUnit.MINUTES.toSeconds(lessonMinutes)),
                    studyLang,
                    String.valueOf(lessonType),
                    new DalApiListener<Boolean>() {

                        @Override
                        public void onSuccess(Boolean response) {
                            Loading.hide();
                            if (response) {
                                ToastUtil.getInstance(context).show(R.string.saved);
                                application.getEventBus().post(new SuccessEvent(BaseEvent.Screen.MAIN, BaseEvent.EventType.DATA_CHANGED, true));
                            } else {
                                ToastUtil.getInstance(context).show(R.string.failed_to_save);
                            }
                        }

                        @Override
                        public void onFailure(String error) {
                            Loading.hide();
                        }
                    }
            );
        } else {
            alertDialog.showNoInternet();
        }
    }

    private void duplicateLesson() {
        lesson = null;
        ToastUtil.getInstance(context).show(R.string.duplicated);
    }

    private DatePickerDialog.OnDateSetListener onStartDateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            startTime.set(Calendar.YEAR, year);
            startTime.set(Calendar.MONTH, month);
            startTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setStartTimeText();
            startTimePickerDialog.show();
        }
    };

    private TimePickerDialog.OnTimeSetListener onStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startTime.set(Calendar.MINUTE, minute);
            setStartTimeText();
        }
    };

    private OnClickDialogListener onLessonMinutesClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedLessonMinutesPos) {
                lessonMinutes = Utils.parseInt(lessonMinutesValues[selectedLessonMinutesPos = which]);
                setLessonMinutesText();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private OnClickDialogListener onStudyClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedStudyLangPos) {
                studyLang = EnumLanguage.getStudyLanguagesIdApi()[selectedStudyLangPos = which];
                setStudyText();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    private OnClickDialogListener onLessonTypeClickListener = new OnClickDialogListener() {

        @Override
        public void onClick(View view, Object object) {
            final int which = (int) object;
            if (which != selectedLessonTypePos) {
                lessonType = (selectedLessonTypePos = which) + 1;
                setLessonTypeText();
            }
        }

        @Override
        public void onDismiss(View view, Object object) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == Constant.REQUEST_CODE.SELECT_STUDENT) {
                studentUid = data.getIntExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, 0);
                studentName = data.getStringExtra(Constant.BUNDLE.KEY_OTHER_USER_NAME);
                setStudentText();
            } else if (requestCode == Constant.REQUEST_CODE.SELECT_TUTOR) {
                tutorUid = data.getIntExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, 0);
                tutorName = data.getStringExtra(Constant.BUNDLE.KEY_OTHER_USER_NAME);
                setTutorText();
            }
        }
    }
}
