package com.dalread.activity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseVocaActivity;

import butterknife.BindView;

public class AboutLessonActivity extends BaseVocaActivity {

    @BindView(R.id.tv_description) TextView tvDescription;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_about_lesson;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
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

    private void initData() {
        tvDescription.setMovementMethod(new ScrollingMovementMethod());
        String description = getString(R.string.about_study_mode_description_normal_mode, getString(R.string.about_study_mode_description));
        description += getString(R.string.about_study_mode_description_role_playing_mode, getString(R.string.about_study_mode_description));
        description += getString(R.string.about_study_mode_description_exam_mode, getString(R.string.about_study_mode_description));
        tvDescription.setText(description);
    }
}
