package com.dalread.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.activity.HomeworkActivity;

public abstract class BaseHomeworkFragment extends BaseVocaFragment {

    protected HomeworkActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof HomeworkActivity) {
            activity = (HomeworkActivity) getActivity();
        }
    }

    @Override
    public void onDestroy() {
        if (activity != null) {
            activity.stopPlayVoca();
        }

        super.onDestroy();
    }
}
