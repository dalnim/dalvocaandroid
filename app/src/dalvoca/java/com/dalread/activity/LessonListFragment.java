package com.dalread.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.adapter.LessonListAdapter;
import com.dalread.base.BaseLessonListFragment;
import com.dalread.base.EnumLanguage;
import com.dalread.component.SeparatorDecoration;
import com.dalread.component.Toolbar;
import com.dalread.dialog.AlertDialog;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.listener.OnClickDialogListener;
import com.dalread.listener.OnClickListener;
import com.dalread.model.CurrentLesson;
import com.dalread.model.Lesson;
import com.dalread.model.LessonOption;
import com.dalread.model.LessonPreviewReviewModel;
import com.dalread.model.ReceiveCallModel;
import com.dalread.network.DalApiListener;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;
import com.dalread.util.Loading;
import com.dalread.util.Utils;
import com.dalread.util.Voca;
import com.nikhilpanju.recyclerviewenhanced.RecyclerTouchListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class LessonListFragment extends BaseLessonListFragment {

    @BindView(R.id.header)
    Toolbar toolbar;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;

    @BindColor(R.color.color_divider)
    int clDivider;

    @BindDimen(R.dimen.divider_height)
    float dividerHeight;

    private Context context;
    private int lessonId; // selected from widget
    private int lessonType;
    private AlertDialog alertDialog;
    private List<Lesson> fullLessons;
    private List<Lesson> lessons;
    private LessonListAdapter adapter;
    private ReceiveCallModel receiveCallModel;
    private LinearLayoutManager linearLayoutManager;
    private PopupMenu popupMenu;
    private EventBus eventBus;
    private boolean dataChanged;
    private RecyclerTouchListener recyclerTouchListener;
    private SingleChoiceDialog singleChoiceDialog;
    private String[] duplicateLessonDays;
    private int selectedDuplicateLessonDayPos;
    private boolean searchByStudentName;
    private boolean searchByTutorName;
    private String searchText;
    private LessonPreviewReviewModel lessonPreviewReviewModel;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_toolbar_and_recycler_view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
        initLayout();
        initEventBus();

        getLessonOption();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (dataChanged) {
            dataChanged = false;
            getData();
        }
        if (isLessonAdmin()) {
            rvContent.addOnItemTouchListener(recyclerTouchListener);
        }
    }

    @Override
    public void onPause() {
        if (isLessonAdmin()) {
            rvContent.removeOnItemTouchListener(recyclerTouchListener);
        }

        super.onPause();
    }

    private void initData() {
        context = getContext();
        Bundle bundle = getArguments();
        if (bundle != null) {
            lessonId = bundle.getInt(Constant.BUNDLE.KEY_LESSON_ID);
            lessonType = bundle.getInt(Constant.BUNDLE.KEY_LESSON_TYPE);
            receiveCallModel = (ReceiveCallModel) bundle.getSerializable(Constant.BUNDLE.KEY_VOICE_DATA);

        }
        duplicateLessonDays = Voca.getDuplicateLessonDays();
        selectedDuplicateLessonDayPos = Arrays.asList(duplicateLessonDays).indexOf(String.valueOf(Constant.DUPLICATE_LESSON_DAY_DEFAULT));
        searchByStudentName = true;
        searchByTutorName = true;
        searchText = "";
    }

    private void initLayout() {
        if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
            toolbar.setTitle(R.string.tb_lessons_admin);
            toolbar.setIconRight(R.drawable.ic_more_vert_white_24dp);
            View popupView = getLayoutInflater().inflate(R.layout.item_search_lesson, null);
            CompoundButton cb = popupView.findViewById(R.id.cb_student_name);
            cb.setOnCheckedChangeListener(onCheckedChangeListener);
            cb = popupView.findViewById(R.id.cb_tutor_name);
            cb.setOnCheckedChangeListener(onCheckedChangeListener);
            PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            toolbar.setPopupWindow(popupWindow);
            toolbar.setSearchListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    searchText = s;
                    searchLesson();
                    return true;
                }
            }, () -> {
                searchText = "";
                searchLesson();
                return true;
            });
            toolbar.showSearchView();
        } else if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR) {
            toolbar.setTitle(R.string.tb_lessons_tutor);
        } else {
            toolbar.setTitle(R.string.tb_lessons_student);
        }
        alertDialog = new AlertDialog(context);
        rvContent.setLayoutManager(linearLayoutManager = new LinearLayoutManager(context));
        rvContent.addItemDecoration(BaseBindUtils.getSeparatorDecoration(context));
        if (isLessonAdmin()) {
            recyclerTouchListener = new RecyclerTouchListener(activity, rvContent)
                    .setSwipeOptionViews(R.id.tv_delete)
                    .setSwipeable(R.id.v_foreground, R.id.v_background, (viewId, position) -> {
                        Lesson lesson = adapter.notifyLessonRemoved(position);
                        if (lesson != null) {
                            lessons.remove(lesson);
                            fullLessons.remove(lesson);
                            deleteLesson(lesson);
                        }
                    });
        }
        popupMenu = new PopupMenu(context, toolbar.getIconRight());
        popupMenu.getMenuInflater().inflate(R.menu.menu_lesson_admin, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            openLessonInfo(null);
            return true;
        });
        singleChoiceDialog = new SingleChoiceDialog(context);
    }

    private void initEventBus() {
        eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    private void getData() {
        if (Utils.isConnected(context)) {
            Loading.show(context);

            application.getDalAiImpl().getListOfLesson(Constant.API_VALUE.APP_TYPE_APP, lessonType, new DalApiListener<List<Lesson>>() {

                @Override
                public void onSuccess(List<Lesson> response) {
                    fullLessons = response;
                    setLessonType();
                    activity.setLessons(fullLessons);
                    activity.cancelOldNotifications();
                    activity.scheduleNotification();
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

    private void setLessonType() {
        lessons = new ArrayList<>();
        if (fullLessons != null) {
            for (Lesson lesson : fullLessons) {
                lesson.setLessonType(lessonType);
                if (lessonId > 0 && lesson.getId() == lessonId) {
                    openLesson(lesson);
                }
                if (isLessonAdmin() && !TextUtils.isEmpty(searchText)) {
                    if (searchByStudentName && lesson.getStudentName().toLowerCase().contains(searchText.toLowerCase())) {
                        lessons.add(lesson);
                        continue;
                    }
                    if (searchByTutorName && lesson.getTutorName().toLowerCase().contains(searchText.toLowerCase())) {
                        lessons.add(lesson);
                    }
                } else {
                    lessons.add(lesson);
                }
            }
            lessonId = 0;
        }
        bindData();
    }

    private void bindData() {
        if (rvContent == null || lessons == null) return;
        int focusPos = 0;
        if (adapter == null) {
            adapter = new LessonListAdapter(
                    activity,
                    EnumLanguage.findByFormatApi(sharedPreferences.getMotherTongueLanguage()),
                    EnumLanguage.findByFormatApi(sharedPreferences.getStudyLanguage()),
                    lessonPreviewReviewModel);
            focusPos = adapter.setData(lessons);
            adapter.setListener(onLessonClickListener);
            rvContent.setAdapter(adapter);
        } else {
            adapter.setData(lessons);
            adapter.notifyDataSetChanged();
        }
        if (isLessonAdmin()) {
            setUnswipeableRows();
            if (focusPos > 0) {
                linearLayoutManager.scrollToPosition(focusPos);
            }
        }
    }

    private void setUnswipeableRows() {
        ArrayList<Integer> unswipeableRows = new ArrayList<>();
        int count = adapter.getItemCount();
        for (int i = 0; i < count; i++) {
            if (!adapter.isSwipeable(i)) {
                unswipeableRows.add(i);
            }
        }
        recyclerTouchListener.setUnSwipeableRows(unswipeableRows.toArray(new Integer[0]));
    }

    private OnClickListener onLessonClickListener = (view, object) -> {
        if (object instanceof Lesson) {
            Lesson lesson = (Lesson) object;
            int id = view.getId();
            if (id == R.id.tv_header) {
                showDayRangeDialog(lesson);
            } else if (id == R.id.ic_left) {
                openUserProfile(lesson.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR ? lesson.getStudentId() : lesson.getTutorId());
            } else if (id == R.id.tv_student) {
                openUserProfile(lesson.getStudentId());
            } else if (id == R.id.tv_tutor) {
                openUserProfile(lesson.getTutorId());
            } else if (id == R.id.v_foreground) {
                openLessonInfo(lesson);
            } else if (id == R.id.v_item) {
                openLesson(lesson);
            }
        } else if (view.getId() == R.id.v_preview_today_lesson) {
            getCurrentLessonTopicOfUser();
        } else if (view.getId() == R.id.v_record_lessons) {
            openLessonListRecord();
        }
    };

    private void showDayRangeDialog(Lesson firstLesson) {
        singleChoiceDialog.show(
                R.string.choose,
                duplicateLessonDays,
                selectedDuplicateLessonDayPos,
                R.string.ok,
                R.string.cancel,
                new OnClickDialogListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        final int which = (int) object;
                        makeNewLessons(firstLesson, Utils.parseInt(duplicateLessonDays[selectedDuplicateLessonDayPos = which]));
                    }

                    @Override
                    public void onDismiss(View view, Object object) {

                    }
                });
    }

    private void makeNewLessons(Lesson lesson, int days) {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            StringBuilder ids = new StringBuilder();
            StringBuilder studentIds = new StringBuilder();
            StringBuilder tutorIds = new StringBuilder();
            StringBuilder startTimes = new StringBuilder();
            StringBuilder finishTimes = new StringBuilder();
            StringBuilder studyLangs = new StringBuilder();
            StringBuilder lessonTypes = new StringBuilder();
            int i = lessons.indexOf(lesson);
            do {
                ids.append(",0");
                studentIds.append(",").append(lesson.getStudentId());
                tutorIds.append(",").append(lesson.getTutorId());
                startTimes.append(",").append(lesson.getLessonStartTimeTS() + TimeUnit.DAYS.toSeconds(days));
                finishTimes.append(",").append(lesson.getLessonFinishTimeTS() + TimeUnit.DAYS.toSeconds(days));
                studyLangs.append(",").append(lesson.getStudyLangCode());
                lessonTypes.append(",").append(lesson.getLessonTypeApi());
                if (++i < lessons.size() && DateUtils.isSameDate(
                        new Date(DateUtils.secondsToMillis(lessons.get(i).getLessonStartTimeTS())),
                        new Date(DateUtils.secondsToMillis(lesson.getLessonStartTimeTS()))
                )) {
                    lesson = lessons.get(i);
                } else {
                    lesson = null;
                }
            } while (lesson != null);
            application.getDalAiImpl().updateOrMakeNewLesson(
                    ids.substring(1),
                    studentIds.substring(1),
                    tutorIds.substring(1),
                    startTimes.substring(1),
                    finishTimes.substring(1),
                    Utils.parseInt(studyLangs.substring(1)),
                    lessonTypes.substring(1),
                    new DalApiListener<Boolean>() {

                        @Override
                        public void onSuccess(Boolean response) {
                            if (response) {
                                getData();
                            } else {
                                Loading.hide();
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

    private void openUserProfile(int uid) {
        if (uid == sharedPreferences.getRealUid()) {
            activity.openNewScreen(MyProfileActivity.class);
        } else {
            Intent i = new Intent(activity, OtherProfileActivity.class);
            i.putExtra(Constant.BUNDLE.KEY_OPPONENT_ID, uid);
            activity.openNewScreen(i);
        }
    }

    private void openLesson(Lesson lesson) {
        Intent i = new Intent(context, ChatDetailsActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_LESSON, lesson);
        i.putExtra(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
        activity.openNewScreen(i);
        receiveCallModel = null;
    }

    private void openLessonInfo(Lesson lesson) {
        Intent i = new Intent(context, LessonInfoActivity.class);
        i.putExtra(Constant.BUNDLE.KEY_LESSON, lesson);
        activity.openNewScreen(i);
    }

    private void deleteLesson(Lesson lesson) {
        if (Utils.isConnected(context)) {
            application.getDalAiImpl().deleteLessons(String.valueOf(lesson.getId()), null);
        } else {
            alertDialog.showNoInternet();
        }
    }

    private boolean isLessonAdmin() {
        return lessonType == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN;
    }

    @OnClick({R.id.ic_left, R.id.ic_right})
    void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ic_left:
                activity.onBackPressed();
                break;
            case R.id.ic_right:
                popupMenu.show();
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onEvent(SuccessEvent successEvent) {
        if (successEvent.getScreen() == BaseEvent.Screen.MAIN) {
            BaseEvent.EventType type = successEvent.getEventType();
            if (type == BaseEvent.EventType.DATA_CHANGED) {
                dataChanged = true;
            }
        }
    }

    @Override
    public void onDestroyView() {
        destroyEventBus();

        super.onDestroyView();
    }

    private void destroyEventBus() {
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            switch (id) {
                case R.id.cb_student_name:
                    searchByStudentName = isChecked;
                    searchLesson();
                    break;
                case R.id.cb_tutor_name:
                    searchByTutorName = isChecked;
                    searchLesson();
                    break;
                default:
                    break;
            }
        }
    };

    private void searchLesson() {
        lessons = new ArrayList<>();
        if (fullLessons != null) {
            if (isLessonAdmin() && !TextUtils.isEmpty(searchText)) {
                for (Lesson lesson : fullLessons) {
                    if (searchByStudentName && lesson.getStudentName().toLowerCase().contains(searchText.toLowerCase())) {
                        lessons.add(lesson);
                        continue;
                    }
                    if (searchByTutorName && lesson.getTutorName().toLowerCase().contains(searchText.toLowerCase())) {
                        lessons.add(lesson);
                    }
                }
            } else {
                lessons.addAll(fullLessons);
            }
        }
        bindData();
    }

    private void getCurrentLessonTopicOfUser() {
        if (Utils.isConnected(context)) {
            Loading.show(context);
            application.getDalAiImpl().getCurrentLessonTopicOfUser(
                    sharedPreferences.getUid(),
                    sharedPreferences.getLangStudyCode(),
                    new DalApiListener<CurrentLesson>() {

                        @Override
                        public void onSuccess(CurrentLesson response) {
                            Loading.hide();
                            if (response != null) {
                                Lesson lesson = new Lesson();
                                lesson.setBookId(response.getBookId());
                                if (!TextUtils.isEmpty(response.getStudyLang())) {
                                    lesson.setStudyLang(response.getStudyLang().toUpperCase());
                                }
                                lesson.setStudentId(sharedPreferences.getRealUid());
                                lesson.setLessonType(Constant.API_VALUE.LIST_LESSON_FOR_STUDENT);
                                openLesson(lesson);
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

    private void getLessonOption() {
        if (Utils.isConnected(context)) {
            Loading.show(context);

            application.getDalAiImpl().getLessonOption(
                    sharedPreferences.getRealUid(),
                    lessonType,
                    new DalApiListener<LessonOption>() {

                        @Override
                        public void onSuccess(LessonOption response) {
                            lessonPreviewReviewModel = new LessonPreviewReviewModel(lessonType, response.isRecordLesson());
                            Loading.hide();
                            getData();
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

    private void openLessonListRecord() {
        Intent intent = new Intent(this.getContext(), LessonListRecordActivity.class);
        startActivity(intent);
    }
}