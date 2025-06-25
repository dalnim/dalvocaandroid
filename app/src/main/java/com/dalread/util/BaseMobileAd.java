package com.dalread.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Random;

public class BaseMobileAd {
    private static final String TAG = "AdMobUtil";
    private static RewardedAd mRewardedAd;
    private static InterstitialAd mInterstitialAd;
    private static int interstitialAdCounter = 0;
    private static Random random = new Random();
    public static class Admob {
        public static class Test {
            public static final String BANNER_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
            public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
            public static final String REWARD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
        }

        public static class AraHanja {
            public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/5393350103";
            public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/8032287932";
            public static final String REWARD_UNIT_ID = "ca-app-pub-9625690961966219/1321538457";
        }

        public static class AraConv {
            public static class English {
                public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/5083227133";
                public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/7497974159";
                public static final String REWARD_UNIT_ID = "ca-app-pub-9625690961966219/1036685196";
            }
            //아래는 영어꺼와 동일함. 나중에 각자껄로 바꾸어야 함.
            public static class Japanese {
                public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/5083227133";
                public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/7497974159";
                public static final String REWARD_UNIT_ID = "ca-app-pub-9625690961966219/1036685196";
            }
            public static class Chinese {
                public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/5083227133";
                public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/7497974159";
                public static final String REWARD_UNIT_ID = "ca-app-pub-9625690961966219/1036685196";
            }
            public static class Korean {
                public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/5083227133";
                public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/7497974159";
                public static final String REWARD_UNIT_ID = "ca-app-pub-9625690961966219/1036685196";
            }
        }

        public static class AraPlayer {
            public static class English {
                public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/4690411014";
                public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/4544348383";
                public static final String REWARD_UNIT_ID = "ca-app-pub-9625690961966219/8909552693";
            }
            public static class Japanese {
                public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/6644253330";
                public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/6740456251";
                public static final String REWARD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"; //현재는 테스트용을 그대로 씀
            }
            public static class Chinese {
                public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/4344010169";
                public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/9413531681";
                public static final String REWARD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"; //현재는 테스트용을 그대로 씀
            }
        }

        public static class AraMultiPlayer {
            public static class Lite {
                public static final String BANNER_UNIT_ID = "ca-app-pub-9625690961966219/8353319859";
                public static final String REWARD_UNIT_ID = "ca-app-pub-9625690961966219/9037908220";
                public static final String INTERSTITIAL_UNIT_ID = "ca-app-pub-9625690961966219/1997726612";
            }
        }
    }

    public static AdSize getAdSize(Context context) {
        // Admob Step 2 - Determine the screen width (less decorations) to use for the ad width.
//        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float widthPixels = displayMetrics.widthPixels;
        float density = displayMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Admob Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
    //load하는건 좀 오래걸려서 미리 띄울려고 했는데, 광고보기를 빨리 하면 잘 안될때도 많고, 또 리워드 요청수만 올라가는거 같아서 광고보기를 누르면 showRewardedAd를 바로 보여주는걸로 변경.
    public static void loadRewardedAd(Context context) {
//        RewardedAd.load(context, MobileAd.getAdsRewardId(context), new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
//                @Override
//                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                    super.onAdFailedToLoad(loadAdError);
//                    Loading.hide();
////                    이건 미리 광고를 띄우는거기 때문에 토스트로 에러를 알려주면 안된다. 디버그일때만 사용할려고.
////                    if (loadAdError.getCode() == 3) {
////                        ToastUtil.getInstance(context).show(R.string.snackbar_rewarded_ad_frequency_cap_reached);
////                    } else {
////                        ToastUtil.getInstance(context).show(loadAdError.getCode() + " " + loadAdError.getMessage());
////                    }
////                    ToastUtil.getInstance(context).show(loadAdError.getCode() + " " + loadAdError.getMessage());
//                    DLog.d(TAG, "onRewardedAdFailedToLoad");
//                }
//
//                @Override
//                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
//                    super.onAdLoaded(rewardedAd);
//                    Loading.hide();
//                    mRewardedAd = rewardedAd;
//                    DLog.d(TAG, "onRewardedAdLoaded");
//                }
//            }
//        );
    }

    public static boolean showRewardedAd(Activity activity, OnUserEarnedRewardListener listener) {
        RewardedAd.load(activity, MobileAd.getAdsRewardId(activity), new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Loading.hide();
                    //이건 미리 부르는게 아니고 버튼을 눌렀을때 호출되므로 에러가 나면 이유를 알려준다.
                    if (loadAdError.getCode() == 3) {
                        DialogUtil.showPositiveDialog(activity, activity.getString(R.string.info), activity.getString(R.string.snackbar_rewarded_ad_frequency_cap_reached), activity.getString(R.string.ok), () -> {

                        });
                    } else {
                        ToastUtil.getInstance(activity).show(R.string.snackbar_rewarded_ad_not_ready);
                    }
//                        ToastUtil.getInstance(activity).show(loadAdError.getCode() + " " + loadAdError.getMessage());
                    DLog.d(TAG, "onRewardedAdFailedToLoad");
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    super.onAdLoaded(rewardedAd);
                    Loading.hide();
                    rewardedAd.show(activity, listener);
                    DLog.d(TAG, "onRewardedAdLoaded");
                }
            }
        );
        return false;

//        //다시 mRewardedAd를 받아올려고 부른다.
//        loadRewardedAd(activity);
//        if (mRewardedAd == null) {
//            //SnackbarUtil은 가끔 안뜰때가 있어서 ToastUtil을 사용한다.
//            ToastUtil.getInstance(activity).show(R.string.snackbar_rewarded_ad_not_ready);
//            Loading.hide();
//            return false;
//        }
//        mRewardedAd.show(activity, listener);
//        return true;
    }
//
//    public static boolean showRewardedAd(Activity activity, RewardedAdLoadCallback callback) {
//        if (rewardedAd == null || !rewardedAd.isLoaded()) {
//            loadRewardedAd(activity);
//            SnackbarUtil.getInstance(activity).show("네트웍이 이상합니다. 조금후에 다시 시도해보세요.");
//            return false;
//        }
//        rewardedAd.show(activity, callback);
//        return true;
//    }

//    public static void loadInterstitialAd(Context context) {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        interstitialAd = new InterstitialAd(context);
//        interstitialAd.setAdUnitId(MobileAd.getInterstitialAd(context));
//        interstitialAd.loadAd(adRequest);
//        interstitialAd.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                // Load the next interstitial ad
//                loadInterstitialAd(context);
//            }
//        });
//    }

//    public static void showInterstitialAd(Context context) {
//        if (isShowInterstitialAd()) {
//            if (interstitialAd != null && interstitialAd.isLoaded()) {
//                interstitialAd.show();
//            } else {
//                loadInterstitialAd(context);
//            }
//        }
//    }

//    private static boolean isShowInterstitialAd() {
//        int countToShowAds = 10;
//        interstitialAdCounter++;
//
//        if (interstitialAdCounter % countToShowAds == 0) {
//            return true;
//        }
//        return false;
//    }
    public static boolean isShowAdsAfterTimeSinceLastClick(Context context) {
        SharedPreferencesDB sharedPreferencesDB = SharedPreferencesDB.getInstance(context);
        long timeToShowAdsAgain = 1000 * 60 * 60; //60 minutes
//        timeToShowAdsAgain = 1000 * 60; //1 분
        return Time.isShowAdsAfterTimeSinceLastClick(sharedPreferencesDB.getLastAdsClickedTime(), timeToShowAdsAgain);
    }
}
