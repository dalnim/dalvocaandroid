package com.dalread.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GestureDetectorCompat;

import com.dalread.util.DLog;

/**
 *
 * Created by ericbhatti on 11/24/15.
 *
 * @author Eric Bhatti
 * @since 24 November, 2015
 *
 */
//TODO : 어디에 쓰는거지?
public class Floaty {
    private final View head;
    private final View body;
    private final Context context;
    private static Floaty floaty;
    private final FloatyOrientationListener floatyOrientationListener;
    private float ratioY = 0;
    private float oldWidth = 0;
    private float oldX = 0;
    private boolean confChange = false;
    private boolean isStart = false;
    private String textValue;
    private static final int defaultSize = 200;

    private static final String LOG_TAG = "Floaty";


    /**
     *
     * @return The body of the floaty which is assigned through the {@link #createInstance} method.
     */

    public View getBody() {
        return floaty.body;
    }


    /**
     *
     * @return The head of the floaty which is assigned through the {@link #createInstance} method.
     */

    public View getHead() {
        return floaty.head;
    }

    /**
     *
     * Creates a Singleton of the Floating Window
     *
     * @param context The application context
     * @param head The head View, upon clicking it the body is to be opened
     * @param body The body View
     * @param floatyOrientationListener The {@link FloatyOrientationListener} interface with callbacks which are called when orientation changes.
     * @return A Floating Window
     *
     */

    public static synchronized Floaty createInstance(Context context, View head, View body, FloatyOrientationListener
            floatyOrientationListener) {
        if (floaty == null) {
            floaty = new Floaty(context, head, body, floatyOrientationListener);
        }
        return floaty;
    }

    /**
     *
     * Creates a Singleton of the Floating Window
     *
     * @param context The application context
     * @param head The head View, upon clicking it the body is to be opened
     * @param body The body View
     * @return A Floating Window
     *
     */
    public static synchronized Floaty createInstance(Context context, View head, View body) {
        if (floaty == null) {
            floaty = new Floaty(context, head, body, new FloatyOrientationListener() {
                @Override
                public void beforeOrientationChange(Floaty floaty) {
                    DLog.d(LOG_TAG, "beforeOrientationChange");
                }

                @Override
                public void afterOrientationChange(Floaty floaty) {
                    DLog.d(LOG_TAG, "afterOrientationChange");
                }

                @Override
                public void onDestroy() {
                    DLog.d(LOG_TAG, "onDestroy");
                }
            });
        }
        return floaty;
    }

    /**
     * @return The same instance of Floating Window, which has been created through {@link #createInstance}. Don't call this method before createInstance
     */
    public static synchronized Floaty getInstance() {
        if (floaty == null) {
            throw new NullPointerException("Floaty not initialized! First call createInstance method, then to access Floaty in any other class call getInstance()");
        }
        return floaty;
    }

    private Floaty(Context context, View head, View body, FloatyOrientationListener floatyOrientationListener) {
        this.head = head;
        this.body = body;
        this.context = context;
        this.floatyOrientationListener = floatyOrientationListener;
    }


    /**
     * Starts the service and adds it to the screen
     */
    public void startService() {
        isStart = true;
        Intent intent = new Intent(context, FloatHeadService.class);
        context.startService(intent);
    }

    public void stopService(boolean isAction) {
        isStart = false;
        Intent intent = new Intent(context, FloatHeadService.class);
        context.stopService(intent);
        if (isAction) {
            if (floatyOrientationListener != null) {
                floatyOrientationListener.onDestroy();
            }
        }
    }

    /**
     * Stops the service and removes it from the screen
     */
    public void stopService() {
        stopService(false);
    }


    /**
     *
     * Helper method for notification creation.
     *
     * @param context
     * @param contentTitle
     * @param contentText
     * @param notificationIcon
     * @param contentIntent
     * @return Notification for the Service
     */
    public static Notification createNotification(Context context, String contentTitle, String contentText, int notificationIcon, PendingIntent contentIntent) {
        return new NotificationCompat.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(notificationIcon)
                .setContentIntent(contentIntent).build();

    }

    public static class FloatHeadService extends Service {

        private WindowManager windowManager;
        private WindowManager.LayoutParams params;
        private LinearLayout mLinearLayout;
        GestureDetectorCompat gestureDetectorCompat;
        DisplayMetrics metrics;
        private boolean didFling;
        private int[] clickLocation = new int[2];


        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

                int[] location = new int[2];
                mLinearLayout.getLocationOnScreen(location);
                floaty.oldWidth = metrics.widthPixels;
                floaty.confChange = true;
                if (floaty.getBody().getVisibility() == View.VISIBLE) {
                    floaty.oldX = clickLocation[0];
                    floaty.ratioY = (float) (clickLocation[1]) / (float) metrics.heightPixels;
                } else {
                    floaty.oldX = location[0];
                    floaty.ratioY = (float) (location[1]) / (float) metrics.heightPixels;
                }
                if (floaty.floatyOrientationListener != null) {
                    floaty.floatyOrientationListener.beforeOrientationChange(floaty);
                }
                floaty.stopService();
                floaty.startService();
                if (floaty.floatyOrientationListener != null) {
                    floaty.floatyOrientationListener.afterOrientationChange(floaty);
                }
            }
        }

        public int onStartCommand(Intent intent, int flags, int startId) {
            DLog.d(LOG_TAG, "onStartCommand");
            metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            return START_STICKY;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            DLog.d(LOG_TAG, "onCreate");
            mLinearLayout = new LinearLayout(getApplicationContext()) {
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_HOME) {
                        if (floaty.body != null) {
                            floaty.body.setVisibility(View.GONE);
                        }
                        params.x = clickLocation[0];
                        params.y = clickLocation[1];
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        params.width = defaultSize;
                        params.height = defaultSize;
                        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                        windowManager.updateViewLayout(mLinearLayout, params);
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                }
            };

            gestureDetectorCompat = new GestureDetectorCompat(floaty.context, new GestureDetector.SimpleOnGestureListener() {
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onDown(MotionEvent event) {
                    DLog.d(LOG_TAG, "onDown");
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    didFling = false;
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {
                    DLog.d(LOG_TAG, "onShowPress");
                    if (floaty.head != null) {
                        floaty.head.setAlpha(0.8f);
                    }
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    if (floaty.body != null && floaty.body.getVisibility() == View.VISIBLE) {
                        floaty.body.setVisibility(View.GONE);
                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        params.width = Floaty.defaultSize;
                        params.height = Floaty.defaultSize;
                        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
                    }
                    params.x = (initialX + (int) ((e2.getRawX() - initialTouchX)));
                    params.y = (initialY + (int) ((e2.getRawY() - initialTouchY)));
                    windowManager.updateViewLayout(mLinearLayout, params);
                    return false;
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    DLog.d(LOG_TAG, "onSingleTapConfirmed");
//                    if (floaty.body != null && floaty.body.getVisibility() == View.GONE) {
//                        params.x = metrics.widthPixels;
//                        params.y = 0;
//                        params.flags = params.flags & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                        if (floaty.head != null) {
//                            floaty.head.getLocationOnScreen(clickLocation);
//                        }
//                        floaty.body.setVisibility(View.VISIBLE);
//                        mLinearLayout.setBackgroundColor(Color.argb(200, 50, 50, 50));
//                    } else {
//                        if (floaty.body != null) {
//                            floaty.body.setVisibility(View.GONE);
//                        }
//                        params.x = clickLocation[0];
//                        params.y = clickLocation[1] - 36;
//                        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//                        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                        mLinearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0));
//                    }
//                    windowManager.updateViewLayout(mLinearLayout, params);
                    floaty.stopService(true);
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    DLog.d(LOG_TAG, "onFling");
                    didFling = true;
                    int newX = params.x;
                    if (newX > (metrics.widthPixels / 2))
                        params.x = metrics.widthPixels;
                    else
                        params.x = 0;
                    windowManager.updateViewLayout(mLinearLayout, params);
                    return false;
                }
            });

            mLinearLayout.setOrientation(LinearLayout.VERTICAL);
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            metrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(metrics);
            params = new WindowManager.LayoutParams(
                    Floaty.defaultSize, Floaty.defaultSize,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.LEFT;


//            if (floaty.confChange) {
//                floaty.confChange = false;
//                if (floaty.oldX < (floaty.oldWidth / 2)) {
//                    params.x = 0;
//                } else {
//                    params.x = metrics.widthPixels;
//                }
//                params.y = (int) (metrics.heightPixels * floaty.ratioY);
//            } else {
//                params.x = metrics.widthPixels;
//                params.y = 0;
//            }
            params.x = metrics.widthPixels;
            params.y = (int) (metrics.heightPixels * floaty.ratioY);
            if (floaty.body != null) {
                floaty.body.setVisibility(View.GONE);
            }
            if (floaty.head != null) {
                floaty.head.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        gestureDetectorCompat.onTouchEvent(event);
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            floaty.head.setAlpha(1.0f);
                            if (!didFling) {
                                Log.d(LOG_TAG, "ACTION_UP");
                                int newX = params.x;
                                if (newX > (metrics.widthPixels / 2))
                                    params.x = metrics.widthPixels;
                                else
                                    params.x = 0;
                                windowManager.updateViewLayout(mLinearLayout, params);
                            }
                        }
                        return true;
                    }
                });
            }
            windowManager.addView(mLinearLayout, params);
            if (floaty.body != null && floaty.body.getParent() != null) {
                ((ViewGroup) floaty.body.getParent()).removeView(floaty.body);
            }
            mLinearLayout.setFocusable(true);
            LinearLayout.LayoutParams headParams = new LinearLayout.LayoutParams(Floaty.defaultSize, Floaty.defaultSize);
//            LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            headParams.gravity = Gravity.CENTER | Gravity.RIGHT;
//            bodyParams.gravity = Gravity.TOP;
            if (floaty.head != null) {
                mLinearLayout.addView(floaty.head, headParams);
            }
//            if (floaty.body != null) {
//                mLinearLayout.addView(floaty.body, bodyParams);
//            }
        }

        @Override
        public void onDestroy() {
            DLog.d(LOG_TAG, "onDestroy");
            super.onDestroy();
            onCloseLayout();
            stopForeground(true);
        }

        public void onCloseLayout() {
            if (mLinearLayout != null) {
                mLinearLayout.removeAllViews();
                windowManager.removeView(mLinearLayout);
            }
        }
    }

    public boolean isStartService() {
        return isStart;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }
}
