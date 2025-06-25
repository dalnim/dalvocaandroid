package com.dalread.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.dalread.asynctask.LessonNotificationWorker;
import com.dalread.base.BaseVocaActivity;
import com.dalread.model.Lesson;
import com.dalread.model.ReceiveCallModel;
import com.dalread.network.events.BaseEvent;
import com.dalread.network.events.SuccessEvent;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;

public class LessonListActivity extends BaseVocaActivity {

    @BindView(R.id.nav_bottom)
    BottomNavigationView bottomNavigationView;

    private int currentBottomNavigationId;
    private int lessonId; // selected from widget
    private int lessonType;
    private List<Lesson> lessons;
    private ReceiveCallModel receiveCallModel;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lesson_list;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEventBus();
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
        lessonId = getIntent().getIntExtra(Constant.BUNDLE.KEY_LESSON_ID, 0);
        lessonType = getIntent().getIntExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_STUDENT);
        receiveCallModel = (ReceiveCallModel) getIntent().getSerializableExtra(Constant.BUNDLE.KEY_VOICE_DATA);
        getIntent().removeExtra(Constant.BUNDLE.KEY_LESSON_ID);
        getIntent().removeExtra(Constant.BUNDLE.KEY_VOICE_DATA);
    }

    private void initLayout() {
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationClickListener);
        bottomNavigationView.setSelectedItemId(R.id.nav_lessons);
        if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
            bottomNavigationView.getMenu().findItem(R.id.nav_history).setTitle(R.string.student_option_);
            bottomNavigationView.getMenu().findItem(R.id.nav_etc).setTitle(R.string.tutor_option);
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationClickListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id != currentBottomNavigationId) {
                switch (id) {
                    case R.id.nav_lessons:
                        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
                        if (fragment instanceof LessonSettingsFragment) {
                            LessonSettingsFragment lessonSettingsFragment = (LessonSettingsFragment) fragment;
                            if (lessonSettingsFragment.checkDataChanged()) {
                                lessonSettingsFragment.confirmSave(success -> openLessonListScreen());
                                return true;
                            }
                        }
                        openLessonListScreen();
                        return true;
                    case R.id.nav_history:
                        if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
                            openLessonSettingsScreen(Constant.API_VALUE.LIST_LESSON_FOR_STUDENT);
                            currentBottomNavigationId = R.id.nav_history;
                        }
                        return true;
                    case R.id.nav_people:
                        fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
                        if (fragment instanceof LessonSettingsFragment) {
                            LessonSettingsFragment lessonSettingsFragment = (LessonSettingsFragment) fragment;
                            if (lessonSettingsFragment.checkDataChanged()) {
                                lessonSettingsFragment.confirmSave(success -> openPeopleListScreen());
                                return true;
                            }
                        }
                        openPeopleListScreen();
                        return true;
                    case R.id.nav_etc:
                        if (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
                            openLessonSettingsScreen(Constant.API_VALUE.LIST_LESSON_FOR_TUTOR);
                        } else {
                            openLessonSettingsScreen(lessonType);
                        }
                        currentBottomNavigationId = R.id.nav_etc;
                        return true;
                }
            }
            return false;
        }
    };

    private void openLessonListScreen() {
        Bundle bundle = new Bundle();
        if (lessonId > 0) {
            bundle.putInt(Constant.BUNDLE.KEY_LESSON_ID, lessonId);
            lessonId = 0;
        }
        bundle.putInt(Constant.BUNDLE.KEY_LESSON_TYPE, lessonType);
        bundle.putSerializable(Constant.BUNDLE.KEY_VOICE_DATA, receiveCallModel);
        Fragment fragment = new LessonListFragment();
        fragment.setArguments(bundle);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
        currentBottomNavigationId = R.id.nav_lessons;
    }

    private void openPeopleListScreen() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.BUNDLE.KEY_LESSON_TYPE, lessonType);
        Fragment fragment = new PeopleListInLessonFragment();
        fragment.setArguments(bundle);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
        currentBottomNavigationId = R.id.nav_people;
    }

    private void openLessonSettingsScreen(int lessonType) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.BUNDLE.KEY_LESSON_TYPE, lessonType);
        Fragment fragment = new LessonSettingsFragment();
        fragment.setArguments(bundle);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    public void cancelOldNotifications() {
        LessonNotificationWorker.cancelWork(getApplicationContext(), lessonType);
    }

    public void scheduleNotification() {
        /*if (sharedPreferences.getUseLessonNotification()
                && (lessonType == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT || lessonType == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR)
                && lessons != null) {
            for (Lesson lesson : lessons) {
                LessonNotificationWorker.scheduleWork(getApplicationContext(), sharedPreferences, lesson);
            }
        }*/
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment instanceof LessonSettingsFragment) {
            LessonSettingsFragment lessonSettingsFragment = (LessonSettingsFragment) fragment;
            if (lessonSettingsFragment.checkDataChanged()) {
                lessonSettingsFragment.confirmSave(success -> finish());
                return;
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        unRegisterEventBus();
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(final SuccessEvent event) {
        DLog.d(getLogTag(), "SuccessEvent=" + event.getScreen() + " - type=" + event.getEventType());
        if (event.getScreen() == BaseEvent.Screen.LESSON_LIST_ACTIVITY) {
            if (event.getEventType() == BaseEvent.EventType.EXIT) {
                finish();
            }
        }
    }
}
