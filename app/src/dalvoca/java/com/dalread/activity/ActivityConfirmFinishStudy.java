package com.dalread.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.StudiedBookAdapter;
import com.dalread.base.BaseVocaActivity;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.ConfirmationDialog;
import com.dalread.model.Lesson;
import com.dalread.model.SERVER_VOCABOOKS;
import com.dalread.model.VocaBookInChat;
import com.dalread.network.DalApiListener;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;
import com.dalread.util.Loading;
import com.dalread.util.ToastUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;

public class ActivityConfirmFinishStudy extends BaseVocaActivity {

    @BindView(R.id.tv_start_time)
    TextView tvStartTime;
    @BindView(R.id.ic_arrow_start_time)
    ImageView icArrowStartTime;
    @BindView(R.id.tv_finish_time)
    TextView tvFinishTime;
    @BindView(R.id.ic_arrow_finish_time)
    ImageView icArrowFinishTime;
    @BindView(R.id.tv_tutor)
    TextView tvTutor;
    @BindView(R.id.tv_student)
    TextView tvStudent;
    @BindView(R.id.tv_last_book)
    TextView tvLastBook;
    @BindView(R.id.ic_arrow_last_book)
    ImageView icArrowLastBook;
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
    private Calendar finishTime;
    private String bookIds;
    private String bookTypes;
    private ArrayList<VocaBookInChat> studiedBooks;
    private VocaBookInChat lastBook;
    private AlertDialog alertDialog;
    private ConfirmationDialog confirmationDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_finish_study;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        confirmationDialog.show();
    }

    private void initData() {
        context = this;
        intent = getIntent();
        lesson = (Lesson) intent.getSerializableExtra(Constant.BUNDLE.KEY_LESSON);
        Bundle eventData = intent.getBundleExtra(Constant.BUNDLE.KEY_EVENT_DATA);
        startTime = Calendar.getInstance();
        try {
            startTime.setTimeInMillis(DateUtils.secondsToMillis(Long.parseLong(eventData.getString(Constant.NOTIFICATION_KEY.START_TIME))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        finishTime = Calendar.getInstance();
        try {
            finishTime.setTimeInMillis(DateUtils.secondsToMillis(Long.parseLong(eventData.getString(Constant.NOTIFICATION_KEY.FINISH_TIME))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        bookIds = eventData.getString(Constant.NOTIFICATION_KEY.VOCABOOKS_ID);
        bookTypes = eventData.getString(Constant.NOTIFICATION_KEY.VOCABOOK_TYPE);
        String lastBookId = eventData.getString(Constant.NOTIFICATION_KEY.LAST_VOCABOOKS_ID);
        EnumLanguage enumStudyLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage());
        EnumLanguage enumDisplayLanguage = EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage());
        ArrayList<SERVER_VOCABOOKS> serverVocabooks = Voca.getSortedBookList(String.valueOf(enumStudyLanguage.getIdApi()));
        studiedBooks = new ArrayList<>();
        for (String bookId : bookIds.split(",")) {
            for (SERVER_VOCABOOKS serverVocabook : serverVocabooks) {
                if (serverVocabook.getID().equals(bookId)) {
                    VocaBookInChat studiedBook = new VocaBookInChat();
                    studiedBook.setVocaBookId(Integer.parseInt(bookId));
                    studiedBook.setVocaBookType(Constant.API_VALUE.VALUE_SERVER_BOOK);
                    studiedBook.setBookName(Voca.getHeaderBookName(serverVocabook, enumDisplayLanguage, enumStudyLanguage));
                    studiedBook.setLangStudy(enumStudyLanguage.getFormatApi());
                    studiedBook.setChecked(false);
                    studiedBooks.add(studiedBook);
                    if (bookId.equals(lastBookId)) {
                        lastBook = studiedBook;
                    }
                    break;
                }
            }
        }
    }

    private void initLayout() {
        toolbar.setTextRight(R.string.confirm);
        setStartTimeText();
        icArrowStartTime.setVisibility(View.INVISIBLE);
        setFinishTimeText();
        icArrowFinishTime.setVisibility(View.INVISIBLE);
        if (lesson != null) {
            tvTutor.setText(lesson.getTutorName());
            tvStudent.setText(lesson.getStudentName());
        }
        if (lastBook != null) {
            tvLastBook.setText(lastBook.getBookName());
        }
        icArrowLastBook.setVisibility(View.INVISIBLE);
        rvBook.setLayoutManager(new LinearLayoutManager(context));
        rvBook.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        rvBook.setAdapter(new StudiedBookAdapter(studiedBooks, null));
        alertDialog = new AlertDialog(context);
        confirmationDialog = new ConfirmationDialog(context, onConfirmClickListener);
        confirmationDialog.setMyTitle(R.string.confirm);
        confirmationDialog.setMessage(R.string.msg_confirm_today_lesson);
        confirmationDialog.setPositiveText(R.string.yes);
        confirmationDialog.setNegativeText(R.string.no);
        ToastUtil.getInstance(context).show(R.string.check_today_lesson);
    }

    private ConfirmationDialog.OnDialogClickListener onConfirmClickListener = new ConfirmationDialog.OnDialogClickListener() {

        @Override
        public void onPositive(DialogInterface dialog) {
            dialog.dismiss();
            confirmFinishStudyInStudyMode(true);
        }

        @Override
        public void onNegative(DialogInterface dialog) {
            dialog.dismiss();
            confirmFinishStudyInStudyMode(false);
        }
    };

    private void setStartTimeText() {
        String text = DateUtils.getTimeToHourFormat().format(startTime.getTime());
        tvStartTime.setText(text);
    }

    private void setFinishTimeText() {
        String text = DateUtils.getTimeToHourFormat().format(finishTime.getTime());
        tvFinishTime.setText(text);
    }

    private void confirmFinishStudyInStudyMode(boolean confirm) {
        int uid = getUserID();
        if (uid > 0) {
            if (Utils.isConnected(context)) {
                Loading.show(context);
                application.getDalAiImpl().confirmFinishStudyInStudyMode(
                        intent.getIntExtra(Constant.BUNDLE.KEY_CHAT_ROOM_INFO, 0),
                        intent.getIntExtra(Constant.BUNDLE.KEY_STUDENT_ID, 0),
                        intent.getIntExtra(Constant.BUNDLE.KEY_TUTOR_ID, 0),
                        lesson == null ? 0 : lesson.getId(),
                        DateUtils.millisToSeconds(startTime.getTimeInMillis()),
                        DateUtils.millisToSeconds(finishTime.getTimeInMillis()),
                        lastBook.getVocaBookId(),
                        lastBook.getVocaBookType(),
                        bookIds,
                        bookTypes,
                        confirm,
                        new DalApiListener<Boolean>() {

                            @Override
                            public void onSuccess(Boolean response) {
                                Loading.hide();
                                if (response) {
                                    openNewScreen(MainHomeActivity.class);
                                    finishAffinity();
                                } else {
                                    onBackPressed();
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
        } else {
            alertDialog.showLogInRequired();
        }
    }
}
