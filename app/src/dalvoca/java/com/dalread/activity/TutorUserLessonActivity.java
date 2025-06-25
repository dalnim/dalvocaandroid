package com.dalread.activity;

import com.dalread.R;
import com.dalread.model.User;
import com.dalread.network.DalApiListener;

import java.util.List;

public class TutorUserLessonActivity extends BaseUserLessonActivity {

    @Override
    protected int getToolbarTitle() {
        return R.string.tutor_list;
    }

    @Override
    protected void getData(int studyLang, int sortType, DalApiListener<List<User>> dalApiListener) {
        application.getDalAiImpl().getAllTutorListForLesson(studyLang, sortType, dalApiListener);
    }

    @Override
    protected String getHeaderText(int size) {
        return getString(R.string.tpl_tutor_list, size);
    }

    @Override
    public void onHeaderIconRightClick() {

    }

    @Override
    public void onHeaderTextRightClick() {

    }
}
