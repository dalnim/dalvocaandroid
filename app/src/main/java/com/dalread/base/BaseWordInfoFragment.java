package com.dalread.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.activity.WordInfoActivity;

public abstract class BaseWordInfoFragment extends BaseVocaFragment {

    public abstract void requestGetData();

    protected WordInfoActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof WordInfoActivity) {
            activity = (WordInfoActivity) getActivity();
        }
    }

    @Override
    public void onDestroy() {
        if (activity != null) {
            activity.stopPlayVoca();
        }

        super.onDestroy();
    }

    public boolean onBackPressed() {
        return false;
    }
}
