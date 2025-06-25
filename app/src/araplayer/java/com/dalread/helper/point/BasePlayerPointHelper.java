package com.dalread.helper.point;

import android.app.Activity;

import com.dalread.util.PointUtil;

public abstract class BasePlayerPointHelper {
    public static int consumePoint1 = 1;
    public static int consumePoint2 = 2;
    public static int consumePoint3 = 3;
    public static int consumePoint5 = 5;
    protected Activity activity;
//    public abstract void consumePoint(int point);
    public BasePlayerPointHelper(Activity activity) {
        this.activity = activity;
    }
    public void consumePoint(int point) {
        (new PointUtil(activity)).consumePoint(point);
    }
}

