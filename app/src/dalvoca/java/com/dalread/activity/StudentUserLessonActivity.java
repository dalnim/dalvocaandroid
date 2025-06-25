package com.dalread.activity;

import com.dalread.R;
import com.dalread.model.User;
import com.dalread.network.DalApiListener;

import java.util.List;

public class StudentUserLessonActivity extends BaseUserLessonActivity {

    @Override
    protected int getToolbarTitle() {
        return R.string.student_list;
    }

    @Override
    protected void getData(int studyLang, int sortType, DalApiListener<List<User>> dalApiListener) {
        application.getDalAiImpl().getAllStudentListForLesson(studyLang, sortType, dalApiListener);
    }

    @Override
    protected String getHeaderText(int size) {
        return getString(R.string.tpl_student_list, size);
    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }
}
