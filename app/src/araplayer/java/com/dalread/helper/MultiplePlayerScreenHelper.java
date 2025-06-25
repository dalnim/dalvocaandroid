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
    private int numberOfScreens;
    public MultiplePlayerScreenHelper(Context context, int numberOfScreens) {
        this.context = context;
        this.numberOfScreens = numberOfScreens;
        getScreenSize();
    }

    public void setNumberOfScreens(int numberOfScreens) {
        this.numberOfScreens = numberOfScreens;
    }

    public void getScreenSize() {
        screenWidth = UnitUtil.getScreenWidth(context);
        screenHeight = UnitUtil.getScreenHeight(context);
        isOrientationVertical = screenHeight > screenWidth;
    }

    public Pair<Integer, Integer> calculateRowsAndColumns() {
        int rows, columns;
        switch (numberOfScreens) {
            case 1:
                rows = 1;
                columns = 1;
                break;
            case 2:
                if (isOrientationVertical) {
                    rows = 2;
                    columns = 1;
                } else {
                    rows = 1;
                    columns = 2;
                }
                break;
            case 3:
                if (isOrientationVertical) {
                    rows = 3;
                    columns = 1;
                } else {
                    rows = 1;
                    columns = 3;
                }
                break;
            case 4:
                if (isOrientationVertical) {
                    rows = 4;
                    columns = 1;
                } else {
                    rows = 2;
                    columns = 2;
                }
                break;
            case 6:
                if (isOrientationVertical) {
                    rows = 3;
                    columns = 2;
                } else {
                    rows = 2;
                    columns = 3;
                }
                break;
            case 8:
                if (isOrientationVertical) {
                    rows = 4;
                    columns = 2;
                } else {
                    rows = 2;
                    columns = 4;
                }
                break;
            case 9:
                rows = 3;
                columns = 3;
                break;
            case 12:
                if (isOrientationVertical) {
                    rows = 4;
                    columns = 3;
                } else {
                    rows = 3;
                    columns = 4;
                }
                break;
            case 15:
                if (isOrientationVertical) {
                    rows = 5;
                    columns = 3;
                } else {
                    rows = 3;
                    columns = 5;
                }
                break;
            case 16:
                if (isOrientationVertical) {
                    rows = 5;
                    columns = 3;
                } else {
                    rows = 4;
                    columns = 4;
                }
                break;
            case 18:
                if (isOrientationVertical) {
                    rows = 6;
                    columns = 3;
                } else {
                    rows = 3;
                    columns = 6;
                }
                break;
            case 20:
                if (isOrientationVertical) {
                    rows = 6;
                    columns = 3;
                } else {
                    rows = 4;
                    columns = 5;
                }
                break;
            case 21:
                if (isOrientationVertical) {
                    rows = 7;
                    columns = 3;
                } else {
                    rows = 3;
                    columns = 7;
                }
                break;
            case 24:
                if (isOrientationVertical) {
                    rows = 8;
                    columns = 3;
                } else {
                    rows = 4;
                    columns = 6;
                }
                break;
            case 27:
                if (isOrientationVertical) {
                    rows = 9;
                    columns = 3;
                } else {
                    rows = 3;
                    columns = 9;
                }
                break;
            case 28:
                if (isOrientationVertical) {
                    rows = 7;
                    columns = 4;
                } else {
                    rows = 4;
                    columns = 7;
                }
                break;
            case 30:
                if (isOrientationVertical) {
                    rows = 10;
                    columns = 3;
                } else {
                    rows = 3;
                    columns = 10;
                }
                break;
            case 32:
                if (isOrientationVertical) {
                    rows = 8;
                    columns = 4;
                } else {
                    rows = 4;
                    columns = 8;
                }
                break;
            case 36:
                if (isOrientationVertical) {
                    rows = 9;
                    columns = 4;
                } else {
                    rows = 4;
                    columns = 9;
                }
                break;
            case 40:
                if (isOrientationVertical) {
                    rows = 10;
                    columns = 4;
                } else {
                    rows = 4;
                    columns = 10;
                }
                break;
            default:
                rows = 3;
                columns = 1;
                break;
        }
        return new Pair<>(rows, columns);
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
