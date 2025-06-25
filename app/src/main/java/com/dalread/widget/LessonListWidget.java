package com.dalread.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.RemoteViews;

import com.dalread.R;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;

public class LessonListWidget extends AppWidgetProvider {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Constant.BUNDLE.KEY_LESSON_LIST_ACTION.equals(action)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.v_grid);
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.lesson_list_widget);
            boolean isAdmin = intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN;
            if (isAdmin) {
                rv.setViewVisibility(R.id.v_header, View.VISIBLE);
                rv.setViewVisibility(R.id.btn_schedule, View.VISIBLE);
            } else {
                rv.setViewVisibility(R.id.v_header, View.GONE);
                rv.setViewVisibility(R.id.btn_schedule, View.GONE);
            }
            rv.setViewVisibility(R.id.pb, View.GONE);
            rv.setTextViewText(R.id.tv, context.getString(R.string.no_lesson));
            appWidgetManager.updateAppWidget(appWidgetIds, rv);
            return;
        }
        if (Constant.BUNDLE.KEY_ITEM_CLICK_ACTION.equals(action)) {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (i != null) {
                int lessonId = intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_ID, 0);
                if (lessonId > 0) {
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra(Constant.BUNDLE.KEY_LESSON_ID, lessonId);
                    i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_STUDENT));
                    context.startActivity(i);
                }
            }
            return;
        }
        if (Constant.BUNDLE.KEY_REFRESH_CLICK_ACTION.equals(action)) {
            // delete current data from Realm
            BaseVoca.executeRealmTransaction(realm -> realm.delete(LessonListWidgetItem.class));
            // notify to remote view
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.v_grid);
            // show loading view
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.lesson_list_widget);
            rv.setViewVisibility(R.id.pb, View.VISIBLE);
            rv.setTextViewText(R.id.tv, context.getString(R.string.loading));
            appWidgetManager.updateAppWidget(appWidgetIds, rv);
            // get new data from API
            GetLessonListForWidgetService.getLessonList(context);
            return;
        }
        if (Constant.BUNDLE.KEY_SCHEDULE_CLICK_ACTION.equals(action)) {
            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            if (i != null) {
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, intent.getIntExtra(Constant.BUNDLE.KEY_LESSON_TYPE, Constant.API_VALUE.LIST_LESSON_FOR_ADMIN));
                context.startActivity(i);
            }
            return;
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            //
            Intent intent1 = new Intent(context, LessonListWidgetService.class);
            intent1.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent1.setData(Uri.parse(intent1.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.lesson_list_widget);
            rv.setRemoteAdapter(R.id.v_grid, intent1);
            rv.setEmptyView(R.id.v_grid, R.id.v_empty);
            //
            Intent intent2 = new Intent(context, LessonListWidget.class);
            intent2.setAction(Constant.BUNDLE.KEY_ITEM_CLICK_ACTION);
            intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent2.setData(Uri.parse(intent2.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.v_grid, pendingIntent2);
            //
            Intent intent3 = new Intent(context, LessonListWidget.class);
            intent3.setAction(Constant.BUNDLE.KEY_REFRESH_CLICK_ACTION);
            intent3.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent3.setData(Uri.parse(intent3.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent3 = PendingIntent.getBroadcast(context, 0, intent3, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.ic_refresh, pendingIntent3);
            //
            Intent intent4 = new Intent(context, LessonListWidget.class);
            intent4.setAction(Constant.BUNDLE.KEY_SCHEDULE_CLICK_ACTION);
            intent4.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent4.setData(Uri.parse(intent4.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent pendingIntent4 = PendingIntent.getBroadcast(context, 0, intent4, PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setOnClickPendingIntent(R.id.btn_schedule, pendingIntent4);
            //
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
        GetLessonListForWidgetService.getLessonList(context);
    }
}
