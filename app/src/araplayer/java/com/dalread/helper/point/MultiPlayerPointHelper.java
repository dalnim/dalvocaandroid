package com.dalread.helper.point;

import android.app.Activity;

public class MultiPlayerPointHelper extends BasePlayerPointHelper {
    private boolean isConsumePoint = false; //멀티플레이어는 화면이 여러개 있으므로 여러개 동영상을 재생해도 한번만 포인트를 차감시킬려고
    public MultiPlayerPointHelper(Activity activity) {
        super(activity);
    }
    @Override
    public void consumePoint(int point) {
        if (!isConsumePoint) {
            isConsumePoint = true;
            super.consumePoint(point);
        }
    }
}

