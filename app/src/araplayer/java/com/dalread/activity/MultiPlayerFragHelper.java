package com.dalread.activity;

import android.content.Context;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.util.ToastUtil;

public class MultiPlayerFragHelper {
    private Context context;
    SharedPreferencesDB pref;
    public MultiPlayerFragHelper(Context context, SharedPreferencesDB pref) {
        this.context = context;
        this.pref = pref;
    }
    public void showABRepeatGuide() {
        if (pref.isFirstABRepeatUse()) {
            ToastUtil.getInstance(context).show(R.string.toast_guide_exit_ab_repeat_mode);
            pref.setFirstABRepeatUse();
        }
    }
}
