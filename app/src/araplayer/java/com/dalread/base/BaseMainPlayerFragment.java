package com.dalread.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dalread.activity.MainHomeActivity;

public abstract class BaseMainPlayerFragment extends BasePlayerFragment {

    protected MainHomeActivity activity;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = (MainHomeActivity) getActivity();
        super.onViewCreated(view, savedInstanceState);
    }
}
