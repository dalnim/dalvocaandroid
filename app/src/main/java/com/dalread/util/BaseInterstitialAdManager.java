package com.dalread.util;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class BaseInterstitialAdManager {
  private static InterstitialAd mInterstitialAd;
  private static boolean isLoading = false;
  private static boolean isLoadingFailed = false;
  private static int adAttemptCount = 0;  // 광고 시도 횟수 추적 변수
  private static final int MAX_AD_ATTEMPTS = 14;  // 최대 광고 시도 횟수
  private static final int AD_RETRY_DELAY_MS = 500;  // 광고 재시도 지연 시간


  public interface AdDismissListener {
    void onAdDismissed();
  }

  // 이건Interstitial 광고를 미리 로드해둠. (보여주지는 않음)
  public static void loadInterstitialAd(Context context, String adUnitId) {
    if (mInterstitialAd == null) {
      AdRequest adRequest = new AdRequest.Builder().build();
      isLoading = true;
      isLoadingFailed = false;
      InterstitialAd.load(context, adUnitId, adRequest, new InterstitialAdLoadCallback() {
        @Override
        public void onAdLoaded(InterstitialAd interstitialAd) {
          mInterstitialAd = interstitialAd;
          isLoading = false;
          adAttemptCount = 0;  // 광고가 로드되면 시도 횟수 초기화
        }

        @Override
        public void onAdFailedToLoad(LoadAdError adError) {
          mInterstitialAd = null;
          isLoadingFailed = true;
          isLoading = false;
          adAttemptCount = 0;  // 광고가 로드되면 시도 횟수 초기화
        }
      });
    }
  }

  // Interstitial 광고를 보여주는 메서드
  public static void showInterstitialAd(Activity activity, String adUnitId, AdDismissListener dismissListener) {
    adAttemptCount++;  // 광고 시도 횟수 증가

    // 시도 횟수가 최대 횟수를 초과하면 종료
    if (adAttemptCount > MAX_AD_ATTEMPTS) {
      if (dismissListener != null) {
        dismissListener.onAdDismissed();
      }
      return;
    }

    if (isLoading) {
      //아직 광고를 로딩중이면 잠시뒤에 다시 보여주길 시도한다.
      new Handler().postDelayed(() -> {
        showInterstitialAd(activity, adUnitId, dismissListener);
      }, AD_RETRY_DELAY_MS);
    } else if (isLoadingFailed) {
      if (dismissListener != null) {
        dismissListener.onAdDismissed();
      }
    } else {
      if (mInterstitialAd != null) {
        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
          @Override
          public void onAdDismissedFullScreenContent() {
            if (dismissListener != null) {
              dismissListener.onAdDismissed();
            }
          }

          @Override
          public void onAdFailedToShowFullScreenContent(AdError adError) {
            if (dismissListener != null) {
              dismissListener.onAdDismissed();
            }
          }

          @Override
          public void onAdShowedFullScreenContent() {
            //광고를 재활용할려면 광고를 보여준뒤에 미리 또 로드해줘야한다. (이건 앱 인스톨시 한번만 하기에 안한다)
//            loadInterstitialAd(activity, adUnitId);
          }
        });
        mInterstitialAd.show(activity);
      } else {
        if (dismissListener != null) {
          dismissListener.onAdDismissed();
        }
      }
    }
  }

  public static void setNullToInterstitialAd() {
    mInterstitialAd = null;
  }
}

