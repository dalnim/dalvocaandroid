package com.dalread.base;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;

import com.dalread.R;

//@SuppressLint("NonConstantResourceId")
public abstract class BasePlayerDialog extends BaseDialog {
    protected View view;
    protected abstract void initOnClickListener();
    public BasePlayerDialog(@NonNull Context context) {
        this(context, R.style.TransparentDialog);
    }

    public BasePlayerDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        setContentView(getContentView());
        initOnClickListener();
    }

    protected View getContentView() {
        return null;
    }
}
