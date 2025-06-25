package com.dalread.activity;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;

import com.dalread.R;
import com.dalread.base.BaseInit;
import com.dalread.base.BaseSplashActivity;
import com.dalread.util.AppFlavorUtil;
import com.dalread.util.ImageUtils;
import com.dalread.util.AraThemeUtil;

/**
 * Created by JetVHS on 3/4/2017.
 */
public class SplashActivity extends BaseSplashActivity implements BaseInit {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AraThemeUtil.getThemeResId(this));
        super.onCreate(savedInstanceState);
        setBackgroundColor();
    }
    private void setBackgroundColor() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            int color = typedValue.data;
            binding.root.setBackgroundColor(color);
        }
    }
    @Override
    public void initView() {
        super.initView();
        setStudyLangFlag();
    }

    private void setStudyLangFlag() {
        if (AppFlavorUtil.isAraMultiPlayerApp()) {
            binding.ivVideoFolderForStudyLanguage.setVisibility(View.GONE);
        } else {
            binding.ivVideoFolderForStudyLanguage.setBackgroundResource(ImageUtils.getStudyLanguageFlag());
        }
    }

    @Override
    public void onIOnCreate(Bundle savedInstanceState) {
        initView();
    }
}