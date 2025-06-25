package com.dalread.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.activity.ChatDetailsActivity;

public abstract class BaseChatDetailsFragment extends BaseVocaFragment {

    protected ChatDetailsActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof ChatDetailsActivity) {
            activity = (ChatDetailsActivity) getActivity();
        }
    }

    @Override
    public void onPause() {
        if (isRemoving() || activity.isFinishing()) {
            activity.stopPlayVoca();
        }

        super.onPause();
    }
}
