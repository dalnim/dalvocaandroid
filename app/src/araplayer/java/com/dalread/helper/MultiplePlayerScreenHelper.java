package com.dalread.helper;

// MultiplePlayerScreenHelper.java

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Pair;

import com.dalread.util.DLog;
import com.dalread.util.UnitUtil;

public class MultiplePlayerScreenHelper {
    private Context context;
    public boolean isOrientationVertical;
    public int screenWidth;
    public int screenHeight;
    private int row;
    private int column;

    public MultiplePlayerScreenHelper(Context context, int row, int column) {
        this.context = context;
        this.row = row;
        this.column = column;
        getScreenSize();
    }

    public void setRowColumn(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public void getScreenSize() {
        screenWidth = UnitUtil.getScreenWidth(context);
        screenHeight = UnitUtil.getScreenHeight(context);
        isOrientationVertical = screenHeight > screenWidth;
    }

    public Pair<Integer, Integer> calculateRowsAndColumns() {
        return new Pair<>(row, column);
    }

    public void logCurrentOrientation(Activity activity) {
        int currentOrientation = activity.getRequestedOrientation();
        String orientationString;
        switch (currentOrientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                orientationString = "Portrait";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                orientationString = "Landscape";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                orientationString = "Reverse Portrait";
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                orientationString = "Reverse Landscape";
                break;
            default:
                orientationString = "Unknown";
                break;
        }
        DLog.d("getLayoutParams", "Current orientation: " + orientationString);
    }
}
