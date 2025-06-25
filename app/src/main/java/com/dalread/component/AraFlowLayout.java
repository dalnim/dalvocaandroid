package com.dalread.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.nex3z.flowlayout.FlowLayout;

public class AraFlowLayout extends FlowLayout {

    private int mMinRows = 1;
    private int initialChildHeight = 0;
    public AraFlowLayout(Context context) {
        super(context);
    }

    public AraFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public AraFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }

    public void setMinRows(int minRows) {
        mMinRows = minRows;
        requestLayout();
    }

    public int getMinRows() {
        return mMinRows;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (initialChildHeight == 0 && getChildCount() > 0) {
            initialChildHeight = getChildAt(0).getMeasuredHeight();
        }

        makeMinRowIsVisible();
    }

    private void makeMinRowIsVisible() {
        int rowCount = calculateRowCount();

        if (rowCount < mMinRows || getChildCount() == 0) {
            int minHeight = initialChildHeight * mMinRows
                    + (int) getRowSpacing() * (mMinRows - 1)
                    + getPaddingTop() + getPaddingBottom();

            setMeasuredDimension(getMeasuredWidth(), minHeight);
        }
    }

    private int calculateRowCount() {
        int rowCount = 1;
        int width = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();
        int currentLineWidth = 0;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int childWidth = child.getMeasuredWidth() + (int) getChildSpacing();

            currentLineWidth += childWidth;
            if (currentLineWidth > width) {
                rowCount++;
                currentLineWidth = childWidth;
            }
        }

        return rowCount;
    }
}
