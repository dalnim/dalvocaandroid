package com.dalread.util;

import android.app.Activity;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;

public class InAppReviewUtil {
    public static void requestReview(Activity activity) {
        ReviewManager reviewManager = ReviewManagerFactory.create(activity);
        Task<ReviewInfo> reviewInfoTask = reviewManager.requestReviewFlow();

        reviewInfoTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                launchReviewFlowAndExit(activity, reviewManager, reviewInfo);
            } else {
                DLog.d("ReviewFlow", "Error requesting review flow", task.getException());
            }
        });
    }

    private static void launchReviewFlowAndExit(Activity activity, ReviewManager reviewManager, ReviewInfo reviewInfo) {
        Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);
        flow.addOnCompleteListener(task -> {
            // 리뷰 플로우 완료 후 앱 종료
            DLog.d("ReviewFlow", "Review flow completed.");
        });
    }
}
