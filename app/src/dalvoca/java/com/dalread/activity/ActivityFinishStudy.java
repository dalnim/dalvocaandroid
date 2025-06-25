package com.dalread.activity;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.StudiedBookAdapter;
import com.dalread.base.BaseVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.listener.OnClickListener;
import com.dalread.model.Lesson;
import com.dalread.model.SERVER_VOCABOOKS;
import com.dalread.model.VocaBookInChat;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class ActivityFinishStudy extends BaseVocaActivity {

    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.tv_finish_time)
    TextView tvFinishTime;
    @BindView(R.id.tv_tutor)
    TextView tvTutor;
    @BindView(R.id.tv_student)
    TextView tvStudent;
    @BindView(R.id.tv_last_book)
    TextView tvLastBook;
    @BindView(R.id.rv_book)
    RecyclerView rvBook;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private Context context;
    private Intent intent;
    private Lesson lesson;
    private Calendar startTime;
    private TimePickerDialog startTimePickerDialog;
    private Calendar finishTime;
    private TimePickerDialog finishTimePickerDialog;
    private ArrayList<VocaBookInChat> studiedBooks;
    private StudiedBookAdapter adapter;
    private VocaBookInChat lastBook;
    private EnumLanguage enumStudyLanguage;
    private EnumLanguage enumDisplayLanguage;
    private ArrayList<SERVER_VOCABOOKS> serverVocabooks;
    private AlertDialog alertDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_finish_study;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initEventBus();
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
        sendPushToStudentFinishStudyInStudyMode();
    }

    private void initData() {
        context = this;
        intent = getIntent();
        lesson = (Lesson) intent.getSerializableExtra(Constant.BUNDLE.KEY_LESSON);
        startTime = Calendar.getInstance();
        finishTime = Calendar.getInstance();
        if (lesson != null) {
            startTime.setTimeInMillis(DateUtils.secondsToMillis(lesson.getLessonStartTimeTS()));
            finishTime.setTimeInMillis(DateUtils.secondsToMillis(lesson.getLessonFinishTimeTS()));
        }
        studiedBooks = new ArrayList<>();
        ArrayList<VocaBookInChat> fullStudiedBooks = (ArrayList<VocaBookInChat>) intent.getSerializableExtra(Constant.BUNDLE.KEY_STUDIED_BOOKS);
        if (fullStudiedBooks != null) {
            for (VocaBookInChat studiedBook : fullStudiedBooks) {
                if (studiedBook.getDuration() >= TimeUnit.MINUTES.toMillis(1)) {
                    studiedBook.setChecked(true);
                    studiedBooks.add(studiedBook);
                    lastBook = studiedBook;
                }
            }
        }
        enumStudyLanguage = EnumLanguage.findByFormatApi(lesson == null ? sharedPreferences.getStudyLanguage() : lesson.getStudyLang());
        enumDisplayLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
        serverVocabooks = Voca.getSortedBookList(String.valueOf(enumStudyLanguage.getIdApi()));
    }

    private void initLayout() {
        setStartTimeText();
        startTimePickerDialog = new TimePickerDialog(
                context, onStartTimeSetListener, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true
        );
        setFinishTimeText();
        finishTimePickerDialog = new TimePickerDialog(
                context, onFinishTimeSetListener, finishTime.get(Calendar.HOUR_OF_DAY), finishTime.get(Calendar.MINUTE), true
        );
        if (lesson != null) {
            tvTutor.setText(lesson.getTutorName());
            tvStudent.setText(lesson.getStudentName());
        }
        if (lastBook != null) {
            tvLastBook.setText(lastBook.getBookName());
        }
        rvBook.setLayoutManager(new LinearLayoutManager(context));
        rvBook.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        rvBook.setAdapter(adapter = new StudiedBookAdapter(studiedBooks, onBookClickListener));
        alertDialog = new AlertDialog(context);
    }

    private OnClickListener onBookClickListener = new OnClickListener() {

        @Override
        public void onClick(View view, Object object) {
            if (object instanceof VocaBookInChat) {
                VocaBookInChat book = (VocaBookInChat) object;
                if (book.getVocaBookId() != lastBook.getVocaBookId()) {
                    book.setChecked(!book.isChecked());
                    adapter.notifyItemChanged(book);
                }
            }
        }
    };

    @OnClick({R.id.v_start_time, R.id.v_finish_time, R.id.tv_last_book})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.v_start_time:
                startTimePickerDialog.show();
                break;
            case R.id.v_finish_time:
                finishTimePickerDialog.show();
                break;
            case R.id.tv_last_book:
                openSelectWordbookScreen();
                break;
            default:
                break;
        }
    }

    private TimePickerDialog.OnTimeSetListener onStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startTime.set(Calendar.MINUTE, minute);
            setStartTimeText();
        }
    };

    private void setStartTimeText() {
        String text = DateUtils.getTimeToHourFormat().format(startTime.getTime());
        tvStartTime.setText(text);
    }

    private TimePickerDialog.OnTimeSetListener onFinishTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            finishTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            finishTime.set(Calendar.MINUTE, minute);
            setFinishTimeText();
        }
    };

    private void setFinishTimeText() {
        String text = DateUtils.getTimeToHourFormat().format(finishTime.getTime());
        tvFinishTime.setText(text);
    }

    private void openSelectWordbookScreen() {
        Intent intent = new Intent(context, WordbookByCategoryActivity.class);
        intent.putExtra(Constant.BUNDLE.KEY_STUDY_LANG, enumStudyLanguage.getFormatApi());
        openNewScreenForResult(intent, Constant.REQUEST_CODE.SELECT_BOOK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                int bookId = data.getIntExtra(Constant.BUNDLE.KEY_VOCA_BOOK_ID, 0);
                if (bookId > 0) {
                    String strBookId = String.valueOf(bookId);
                    for (SERVER_VOCABOOKS serverVocabook : serverVocabooks) {
                        if (serverVocabook.getID().equals(strBookId)) {
                            lastBook = new VocaBookInChat();
                            lastBook.setVocaBookId(bookId);
                            lastBook.setVocaBookType(Constant.API_VALUE.VALUE_SERVER_BOOK);
                            lastBook.setBookName(Voca.getHeaderBookName(serverVocabook, enumDisplayLanguage, enumStudyLanguage));
                            lastBook.setLangStudy(enumStudyLanguage.getFormatApi());
                            lastBook.setDuration(TimeUnit.MINUTES.toMillis(1));
                            lastBook.setChecked(true);
                            tvLastBook.setText(lastBook.getBookName());
                            studiedBooks.add(lastBook);
                            adapter.notifyItemInserted(studiedBooks.size() - 1);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void sendPushToStudentFinishStudyInStudyMode() {
        if (lastBook == null)
            return;
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                String bookIds = "";
                String bookTypes = "";
                for (VocaBookInChat studiedBook : studiedBooks) {
                    if (studiedBook.isChecked()) {
                        bookIds += "," + studiedBook.getVocaBookId();
                        bookTypes += "," + studiedBook.getVocaBookType();
                    }
                }
                if (bookIds.isEmpty())
                    return;
                bookIds = bookIds.substring(1);
                bookTypes = bookTypes.substring(1);
                Loading.show(context);
                application.getDalAiImpl().sendPushToStudentFinishStudyInStudyMode(
                        intent.getIntExtra(Constant.BUNDLE.KEY_CHAT_ROOM_INFO, 0),
                        intent.getIntExtra(Constant.BUNDLE.KEY_OPPONENT_ID, 0),
                        lesson == null ? 0 : lesson.getId(),
                        DateUtils.millisToSeconds(startTime.getTimeInMillis()),
                        DateUtils.millisToSeconds(finishTime.getTimeInMillis()),
                        lastBook.getVocaBookId(),
                        lastBook.getVocaBookType(),
                        bookIds,
                        bookTypes,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                Loading.hide();
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
        } else {
            alertDialog.showLogInRequired();
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.ALL) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.NOTIFICATION_RECEIVED) {
                if (successEvent.getModel() instanceof Bundle) {
                    Bundle eventData = (Bundle) successEvent.getModel();
                    String methodName = eventData.getString(Constant.NOTIFICATION_KEY.METHOD_NAME);
                    if (Constant.NOTIFICATION_VALUE.CONFIRM_FINISH_STUDY_IN_STUDY_MODE.equals(methodName)) {
                        String callerUserName = eventData.getString(Constant.NOTIFICATION_KEY.CALLER_USERNAME);
                        String strConfirm = eventData.getString(Constant.NOTIFICATION_KEY.CONFIRM);
                        if ("1".equals(strConfirm)) {
                            final String message = getString(R.string.tpl_confirm_today_lesson, callerUserName);
                            rvBook.post(new Runnable() {

                                @Override
                                public void run() {
                                    alertDialog.show(message, null, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            openNewScreen(MainHomeActivity.class);
                                            finishAffinity();
                                        }
                                    });
                                }
                            });
                        } else {
                            final String message = getString(R.string.tpl_refuse_today_lesson, callerUserName);
                            rvBook.post(new Runnable() {

                                @Override
                                public void run() {
                                    alertDialog.show(message, null, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            alertDialog.show(message, null, null);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }
}
