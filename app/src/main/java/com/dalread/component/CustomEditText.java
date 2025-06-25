package com.dalread.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class CustomEditText extends androidx.appcompat.widget.AppCompatEditText {

    private OnKeyListener onKeyPreImeListener;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (onKeyPreImeListener != null
                && onKeyPreImeListener.onKey(this, keyCode, event))
            return true;
        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnKeyPreImeListener(OnKeyListener onKeyPreImeListener) {
        this.onKeyPreImeListener = onKeyPreImeListener;
    }
}
