package com.dalread.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.dalread.R;
import com.dalread.base.BaseChatFragment;

public class PeopleFragment extends BaseChatFragment {

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_people;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
