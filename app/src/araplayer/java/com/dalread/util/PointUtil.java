package com.dalread.util;

import android.app.Activity;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.helper.point.BasePlayerPointHelper;

public class PointUtil extends AbstractPointUtil {
    private final boolean isMultiPlayer = AppFlavorUtil.isAraMultiPlayerApp();
    public PointUtil(Activity activity) {
        super(activity);
    }
    @Override
    public int getPoint() {
        return sharedPreferences.getPointMultiPlayer(isMultiPlayer);
    }

    @Override
    public void addPoint(int point) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(activity);
        int currentPoint = sharedPreferences.getPointMultiPlayer(isMultiPlayer);
        sharedPreferences.setPointMultiPlayer(currentPoint + point);
        sharedPreferences.setPointAdded(true);
    }

    @Override
    public void consumePoint(int point) {
       SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(activity);
       int updatedPoint = Math.max(0, sharedPreferences.getPointMultiPlayer(isMultiPlayer) - point);
       sharedPreferences.setPointMultiPlayer(updatedPoint);
    }
    public int getPointToAnalyzeSubtitle() {
        return BasePlayerPointHelper.consumePoint5;
    }
    public int getPointToAnalyzeSubtitleAgain() {
        return BasePlayerPointHelper.consumePoint2;
    }
}
