package com.dalread.util;

import android.app.Activity;

public class PointUtil extends AbstractPointUtil {
    public PointUtil(Activity activity) {
        super(activity);
        minPointToShowRewardButton = 30;
    }
    @Override
    public int getPoint() {
        // return sharedPreferences.getPointAraHanja();
        // 아라한자에서는 항상 충분한 포인트가 있는 것처럼 표시
        return 999;
        // return sharedPreferences.getPointAraHanja();
    }

    @Override
    public void addPoint(int point) {
        // 아라한자에서는 포인트 추가 비활성화
        // int currentPoint = sharedPreferences.getPointAraHanja();
        // sharedPreferences.setPointAraHanja(currentPoint + point);
        // sharedPreferences.setPointAdded(true);
    }

    @Override
    public void consumePoint(int point) {
        // 아라한자에서는 포인트 소모 비활성화
        // int updatedPoint = Math.max(0, sharedPreferences.getPointAraHanja() - point);
        // sharedPreferences.setPointAraHanja(updatedPoint);
    }

    @Override
    public boolean needToShowFullAd() {
        // 아라한자에서는 항상 광고를 보여주지 않음
        return false;
    }
}
