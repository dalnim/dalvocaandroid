package com.dalread.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.activity.StudyHistoryActivity;

public abstract class BaseVocaHistoryFragment extends BaseVocaFragment {

    protected StudyHistoryActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof StudyHistoryActivity) {
            activity = (StudyHistoryActivity) getActivity();
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
