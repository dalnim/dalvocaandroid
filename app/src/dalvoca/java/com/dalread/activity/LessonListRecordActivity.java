package com.dalread.activity;

import com.dalread.R;
import com.dalread.base.BaseVocaActivity;

public class LessonListRecordActivity extends BaseVocaActivity {

    @Override
    protected int getContentViewId() {
        return R.layout.activity_lesson_list_record;
    }

    @Override
    public void onHeaderLeftClick() {
        finish();
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
