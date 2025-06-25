package com.dalread.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.activity.LessonListActivity;

public abstract class BaseLessonListFragment extends BaseVocaFragment {

    protected LessonListActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof LessonListActivity) {
            activity = (LessonListActivity) getActivity();
        }
    }
}
