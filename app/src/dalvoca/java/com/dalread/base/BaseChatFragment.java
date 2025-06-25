package com.dalread.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.activity.ChatActivity;

public abstract class BaseChatFragment extends BaseVocaFragment {

    protected ChatActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof ChatActivity) {
            activity = (ChatActivity) getActivity();
        }
    }
}
