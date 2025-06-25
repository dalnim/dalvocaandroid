package com.dalread.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.dalread.BaseApplication;
import com.dalread.R;
import com.dalread.util.Utils;

public class CustomFontSizeTextView extends EllipsizingTextView {
    private float defaultTextSize;
    private float defaultWidth;
    private float defaultHeight;
    private float sizeRatio;

    public CustomFontSizeTextView(Context context) {
        this(context, null);
    }

    public CustomFontSizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomFontSizeTextView);
        defaultTextSize = typedArray.getDimension(R.styleable.CustomFontSizeTextView_defaultTextSize, context.getResources().getDimension(R.dimen.font_value));
        defaultWidth = typedArray.getDimension(R.styleable.CustomFontSizeTextView_defaultWidth, -1);
        defaultHeight = typedArray.getDimension(R.styleable.CustomFontSizeTextView_defaultHeight, -1);
        sizeRatio = BaseApplication.getInstance().getSharedPref().getAraHanjaFontSizeRatio(context);
        setTextSize(Utils.pxToSp(context, defaultTextSize * sizeRatio));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMeasure = widthMeasureSpec;
        int heightMeasure = heightMeasureSpec;
        if (defaultWidth > 0) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            widthMeasure = MeasureSpec.makeMeasureSpec((int) (width * sizeRatio), MeasureSpec.EXACTLY);
        }
        if (defaultHeight > 0) {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            heightMeasure = MeasureSpec.makeMeasureSpec((int) (height * sizeRatio), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasure, heightMeasure);
    }

    public void setDefaultTextSize(float defaultTextSize) {
        this.defaultTextSize = defaultTextSize;
        setTextSize(Utils.pxToSp(getContext(), this.defaultTextSize * sizeRatio));
        invalidate();
    }
}
