package com.dalread.activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.BaseVocaFragment;
import com.dalread.databinding.FragmentAppIntroduction1Binding;
import com.dalread.util.AppFlavorUtil;

public class AppIntroductionFragment extends BaseVocaFragment {
    protected FragmentAppIntroduction1Binding binding;

    @Override
    protected int getContentViewId() {
        return -1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAppIntroduction1Binding.bind(view);
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            binding.root.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.multiPrimaryDarkColor));
            binding.tvIntroduction.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.multiPrimaryDarkColor));
            binding.tvIntroduction.setTextColor(ContextCompat.getColor(getContext(), R.color.textPrimaryWhiteColor));
        }
        binding.tvIntroduction.setText(getString(getStringResId()));
    }
    protected int getStringResId() {
        return -1;
    }
}

