package com.dalread.component;

import android.content.Context;
import android.util.AttributeSet;

import com.dalread.util.Constant;

/**
 * Created by Akira on 2016/06/24.
 */
public class FuriganaView extends BaseFuriganaView {
    public FuriganaView(Context context) {
        super(context);
        initialize(context, null);
    }

    public FuriganaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public FuriganaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }



    protected void setTextSizeOfmFuriganaTextPaint(float size) {
        mFuriganaTextPaint.setTextSize(size * 0.8f);
    }

    protected int getFuriganaView_line_spacing() {
        if (isShowFurigana == Constant.SHOW_FURIGANA_OFF) {
            return Constant.RUBY.DEFAULT_LINE_SPACE_FOR_HIDE_RUBY;
        } else {
            return Constant.RUBY.DEFAULT_LINE_SPACE_FOR_HANJA;
        }
    }
}