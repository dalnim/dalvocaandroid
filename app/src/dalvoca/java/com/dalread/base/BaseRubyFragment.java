package com.dalread.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.activity.RubyActivity;

public abstract class BaseRubyFragment extends BaseVocaFragment {

    protected RubyActivity activity;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof RubyActivity) {
            activity = (RubyActivity) getActivity();
        }
    }
}
