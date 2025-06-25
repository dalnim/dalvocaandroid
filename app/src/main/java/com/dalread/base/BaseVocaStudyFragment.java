package com.dalread.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.activity.StudyActivity;

public abstract class BaseVocaStudyFragment extends BaseVocaFragment {

    protected StudyActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof StudyActivity) {
            activity = (StudyActivity) getActivity();
        }
    }
}
