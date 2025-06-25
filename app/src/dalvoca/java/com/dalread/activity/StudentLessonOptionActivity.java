package com.dalread.activity;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dalread.R;
import com.dalread.base.BaseDalVocaPlayVocaActivity;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

public class StudentLessonOptionActivity extends BaseDalVocaPlayVocaActivity
        implements LessonSettingsFragment.OnSaveFinishListener {

    private int lessonType;
    private int otherUserId;
    private String otherUserName;
    private boolean saveSuccess;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_student_lesson_option;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initLayout();
    }

    @Override
    public void onHeaderLeftClick() {

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
        lessonType = Constant.API_VALUE.LIST_LESSON_FOR_STUDENT;
        otherUserId = getIntent().getIntExtra(Constant.BUNDLE.KEY_OTHER_USER_ID, 0);
        otherUserName = getIntent().getStringExtra(Constant.BUNDLE.KEY_OTHER_USER_NAME);
    }

    private void initLayout() {
        openLessonSettingsScreen();
    }

    private void openLessonSettingsScreen() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.BUNDLE.KEY_LESSON_TYPE, lessonType);
        bundle.putInt(Constant.BUNDLE.KEY_OTHER_USER_ID, otherUserId);
        bundle.putString(Constant.BUNDLE.KEY_OTHER_USER_NAME, otherUserName);
        Fragment fragment = new LessonSettingsFragment();
        fragment.setArguments(bundle);
        Utils.loadFragment(this, fragment, getFragmentContainerId(), false);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(getFragmentContainerId());
        if (fragment instanceof LessonSettingsFragment) {
            LessonSettingsFragment lessonSettingsFragment = (LessonSettingsFragment) fragment;
            if (lessonSettingsFragment.checkDataChanged()) {
                lessonSettingsFragment.confirmSave(success -> {
                    if (success) {
                        setResult(Activity.RESULT_OK);
                    }
                    finish();
                });
                return;
            }
        }
        if (saveSuccess) {
            setResult(Activity.RESULT_OK);
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onFinish(boolean success) {
        saveSuccess = success;
    }
}
