package com.dalread.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.YesNoDialog;
import com.dalread.listener.OnYesNoClickListener;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;

public class PointGptUtil {
    public static final int pointForConversation = 10;
    public interface OnRewardPointListener {
        public void onSuccess();
        public void onContinue(); //네트웍이 이상 있어서 안될때는 그냥 하게 한다.
        public void onCancel();
        public void onFail();
    }
    public static int getPoint(Context context) {
        return SharedPreferencesDB.getInstance(context).getPointGpt();
    }
    public static void askToWatchRewardedAd(Activity activity, OnRewardPointListener onRewardPointListener) {
        final YesNoDialog dialog = new YesNoDialog(activity,
                R.string.warning,
                R.string.msg_warning_need_point_for_gpt, null, new OnYesNoClickListener() {
            @Override
            public void onYesClick(View view, Object object) {
                showRewardedAd(activity, onRewardPointListener);
            }

            @Override
            public void onNoClick(View view, Object object) {
                onRewardPointListener.onCancel();
            }
        });
        dialog.show();
    }
    public static boolean needToShowFullAd(Context context) {
        return getPoint(context) < pointForConversation ? true : false;
        //return true;
    }

    public static void showRewardedAd(Activity activity, OnRewardPointListener onRewardPointListener) {
        boolean result = BaseMobileAd.showRewardedAd(activity, new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                DLog.d("", rewardItem.getType());
                PointGptUtil.addPointGptAfterFullAd(activity, rewardItem.getAmount());
                onRewardPointListener.onSuccess();
            }
        });
//        boolean result = BaseMobileAd.showRewardedAd(activity, new RewardedAdCallback() {
//            @Override
//            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
//                DLog.d("", rewardItem.getType());
//                PointGptUtil.addPointGptAfterFullAd(activity, rewardItem.getAmount());
//                onRewardPointListener.onSuccess();
//            }
//            @Override
//            public void onRewardedAdFailedToShow(AdError var1) {
//                onRewardPointListener.onFail();
//            }
//            @Override
//            public void onRewardedAdClosed() {
//                DLog.d("", "onRewardedAdClosed");
//                BaseMobileAd.loadRewardedAd(activity);
//            }
//        });
        //광고를 모종의 이유를 못띄울때는 그냥 한번 하게 해준다.
        if (result == false) {
            onRewardPointListener.onContinue();
        }
    }
    public static void addPointGptAfterFullAd(Context context, int point) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        int currentPoint = sharedPreferences.getPointGpt();
        sharedPreferences.setPointGpt(currentPoint + point);
    }

    public static void consumePointByToken(Context context, int tokens) {
        int pointConsume = 2;
        if (tokens > 500) {
            pointConsume = 4;
        }
        consumePoint(context, pointConsume);
    }
    public static void consumePoint(Context context, int point) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        int currentPoint = sharedPreferences.getPointGpt();
        sharedPreferences.setPointGpt(currentPoint - point);
    }
}
