package com.dalread.util;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnYesNoClickListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;

public abstract class AbstractPointUtil {
    protected Activity activity;
    protected SharedPreferencesDB sharedPreferences;
    protected int minPointToShowRewardButton = 3;
    protected AbstractPointUtil(Activity activity) {
        this.activity = activity;
        sharedPreferences = SharedPreferencesDB.getInstance(activity);
    }
    public interface OnRewardPointListener {
        public void onSuccess();
        public void onContinue(); //네트웍이 이상 있어서 안될때는 그냥 하게 한다.
        public void onCancel();
        public void onFail();
    }
    public abstract int getPoint();
    public abstract void addPoint(int point);
    public abstract void consumePoint(int point);

    public void askToWatchRewardedAd(OnRewardPointListener onRewardPointListener) {
        final YesNoDialog dialog = new YesNoDialog(activity,
                R.string.warning,
                R.string.msg_warning_need_point_for_gpt, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                showRewardedAd(onRewardPointListener);
            }

            @Override
            public void onNoClick(View view, Object object) {
                onRewardPointListener.onCancel();
            }
        });
        dialog.show();
    }
    public boolean needToShowFullAd() {
        return false;
//        return getPoint() <= 0 ? true : false;
        //return true;
    }

    public boolean needToShowRewardButton() {
        return false;
//        return getPoint() <= minPointToShowRewardButton ? true : false;
        //return true;
    }

    public void showRewardedAd(OnRewardPointListener onRewardPointListener) {
        Loading.show(activity, R.string.msg_ads_wait_to_load_ads);
        boolean result = BaseMobileAd.showRewardedAd(activity, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                DLog.d("", rewardItem.getType());
                Loading.hide();
                int points = rewardItem.getAmount();
                addPoint(points);
                ToastUtil.getInstance(activity).show(activity.getString(R.string.snackbar_points_added, points));
                if (onRewardPointListener != null) {
                    onRewardPointListener.onSuccess();
                }
            }
        });
    }


}
