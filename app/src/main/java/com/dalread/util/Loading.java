package com.dalread.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.dalread.R;
import com.dalread.component.kprogresshud.KProgressHUD;

/**
 * Created by JetVHS on 2/28/2017.
 */
public class Loading {
    private static final String TAG = "@@Loading";
    private static KProgressHUD dialog;
    private static final Handler handler = new Handler(Looper.getMainLooper());
    private static Runnable showDialogRunnable;
    private static final long defaultDelayMillis_0 = 0L;
    private static final long defaultDelayMillis = 1000L;
    private static boolean isCancelable = true;
    public static void show(Context context) {
        show(context, R.string.loading);
    }
    //Not complicated
    public static void showWithProgress(Context context) {
        showWithProgress(context, R.string.loading, 0);
    }

    public static void showWithProgress(Context context, int maxProgress) {
        showWithProgress(context, R.string.loading, maxProgress);
    }

    public static void show(Context context, int title) {
        show(context, context.getString(title), "", defaultDelayMillis_0);
    }
    //Not complicated
    public static void showWithProgress(Context context, int title, int maxProgress) {
        showWithProgressMain(context, context.getString(title), "", KProgressHUD.Style.PIE_DETERMINATE, maxProgress, defaultDelayMillis_0);
    }

    public static void show(Context context, int title, int detailsLabel) {
        show(context, context.getString(title), context.getString(detailsLabel), defaultDelayMillis_0);
    }

    public static void show(Context context, String title) {
        show(context, title, "", defaultDelayMillis_0);
    }

    public static void show(Context context, String title, String detailsLabel) {
        show(context, title, detailsLabel, defaultDelayMillis_0);
    }

    public static void showWithoutMessage(Context context) {
        show(context, null);
    }

    //Dalnim added
    public static void showDelayWithoutMessage(Context context) {
        show(context, "", "", defaultDelayMillis);
    }

    //Dalnim added
    public static void showDelay(Context context) {
        show(context, context.getString(R.string.loading), "", defaultDelayMillis);
    }

    //Dalnim added
    public static void showDelay(Context context, int title) {
        show(context, context.getString(title), "", defaultDelayMillis);
    }

    //Dalnim added
    public static void showDelay(Context context, long delayMillis) {
        show(context, context.getString(R.string.loading), "", delayMillis);
    }
    //Dalnim added
    public static void showDelay(Context context, int title, long delayMillis) {
        show(context, context.getString(title), "", delayMillis);
    }

    //Dalnim added
    public static void showDelay(Context context, int title, int detailsLabel, long delayMillis) {
        show(context, context.getString(title), context.getString(detailsLabel), delayMillis);
    }

    //Dalnim added
    public static void showDelay(Context context, String title, long delayMillis) {
        show(context, title, "", delayMillis);
    }

    //Dalnim added
    public static void showDelay(Context context, String title, String detailsLabel, long delayMillis) {
        show(context, title, detailsLabel, delayMillis);
    }

//    public static void show(Context context, int title, long delayMillis) {
//        show(context, context.getString(title), delayMillis);
//    }

    private static void show(Context context, String title, String detailsLabel, long delayMillis) {
        if (showDialogRunnable == null) {
            showDialogRunnable = () -> {
                try {
                    DLog.d(TAG, "show()");
                    if (context == null) {
                        return;
                    }
                    dialog = KProgressHUD.create(context).setLabel(title);
                    dialog.setDetailsLabel(detailsLabel);
                    dialog.setCancellable(isCancelable);
                    dialog.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            };
            handler.postDelayed(showDialogRunnable, delayMillis);
        } else if (isShowing()) {
            handler.post(() -> dialog.setLabel(title));
        }
    }
    //Not complicated
    private static void showWithProgressMain(Context context, String title, String detailsLabel, KProgressHUD.Style style, int maxProgress, long delayMillis) {
        if (showDialogRunnable == null) {
            showDialogRunnable = () -> {
                try {
                    DLog.d(TAG, "show()");
                    if (context == null) {
                        return;
                    }
                    dialog = KProgressHUD.create(context).setLabel(title);
                    dialog.setDetailsLabel(detailsLabel);
                    dialog.setCancellable(isCancelable);
                    if (style != null) {
                        dialog.setStyle(style);
                        if (maxProgress > 0)
                            dialog.setMaxProgress(maxProgress);
                    }
                    dialog.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            };
            handler.postDelayed(showDialogRunnable, delayMillis);
        } else if (isShowing()) {
            handler.post(() -> dialog.setLabel(title));
        }
    }
    //Not complicated
    public static void setProgress(int progress) {
        dialog.setProgress(progress);
    }
    //Not complicated
    public static void setMaxProgress(int maxProgress) {
        dialog.setMaxProgress(maxProgress);
    }

    public static void hide() {
        hide(0);
    }

    private static void hide(int delay) {
        //Dalnim : if this code is here, the delay value is useless.
        //But if I move this code into handler.postDelayed, then "handler.post(() -> dialog.setMessage(title));" in above "show" method causes null exception. So I let this code stay here.
        //error message : java.lang.NullPointerException: Attempt to invoke virtual method 'void android.app.ProgressDialog.setMessage(java.lang.CharSequence)' on a null object reference
        //It happens when I try to open "OpenSubtitles" view from AraPlayer's VideoInformation view.
        handler.removeCallbacks(showDialogRunnable);
        showDialogRunnable = null;
        handler.postDelayed(() -> {
            try {
                DLog.d(TAG, "hide()");
//                handler.removeCallbacks(showDialogRunnable);
//                showDialogRunnable = null;

                if (isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, delay);
        setCancelable(false);
    }

    public static void hideDelay() {
        hide(500);
    }

    public static void hideDelay(int delay) {
        hide(delay);
    }

    public static boolean isShowing() {
        return dialog != null && dialog.isShowing();
    }

    public static void setCancelable(boolean cancelable) {
        isCancelable = cancelable;
    }
}