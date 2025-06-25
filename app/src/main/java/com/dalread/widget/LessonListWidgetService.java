package com.dalread.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.dalread.R;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;

import java.util.Date;
import java.util.List;

import io.realm.RealmResults;

public class LessonListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new LessonListWidgetFactory(getApplicationContext(), intent);
    }

    class LessonListWidgetFactory implements RemoteViewsFactory {

        private Context context;
        private List<LessonListWidgetItem> widgetItems;

        LessonListWidgetFactory(Context context, Intent intent) {
            this.context = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            BaseVoca.executeRealmTransaction(realm -> {
                RealmResults<LessonListWidgetItem> items = realm.where(LessonListWidgetItem.class).sort("startTime").findAll();
                widgetItems = realm.copyFromRealm(items);
            });
        }

        @Override
        public void onDestroy() {
            if (widgetItems != null) {
                widgetItems.clear();
            }
        }

        @Override
        public int getCount() {
            return widgetItems == null ? 0 : widgetItems.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            LessonListWidgetItem item = widgetItems.get(position);
            //
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.lesson_list_widget_item);
            int bg = BaseVoca.getLessonWidgetBackground(
                    item.getStartTime(),
                    item.getFinishTime(),
                    item.isStarted(),
                    item.isFinished()
            );
            rv.setImageViewResource(R.id.iv_square, bg);
            rv.setTextViewText(R.id.tv_lang, BaseVoca.localeToEmoji(item.getStudyLangCode()));
            rv.setViewVisibility(R.id.tv_keyboard, item.getLessonTypeApi() == Constant.API_VALUE.LESSON_TYPE_TEXT_CHAT ? View.VISIBLE : View.GONE);
            Date date = new Date(DateUtils.secondsToMillis(item.getStartTime()));
            int textColor = bg == R.drawable.bg_border_16dp_orange ? Color.WHITE : Color.BLACK;
            rv.setTextColor(R.id.tv_time, textColor);
            rv.setTextViewText(R.id.tv_time, DateUtils.getTimeWidgetFormat().format(date));
            rv.setTextColor(R.id.tv_date_day, textColor);
            rv.setTextViewText(R.id.tv_date_day, DateUtils.getDateDayWidgetFormat().format(date));
            switch (item.getLessonType()) {
                case Constant.API_VALUE.LIST_LESSON_FOR_ADMIN:
                    showStudent(rv, item);
                    showTutor(rv, item);
                    break;
                case Constant.API_VALUE.LIST_LESSON_FOR_TUTOR:
                    showStudent(rv, item);
                    hideTutor(rv);
                    break;
                default: // Constant.API_VALUE.LIST_LESSON_FOR_STUDENT
                    hideStudent(rv);
                    showTutor(rv, item);
                    break;
            }
            //
            Bundle extras = new Bundle();
            extras.putInt(Constant.BUNDLE.KEY_LESSON_ID, item.getId());
            extras.putInt(Constant.BUNDLE.KEY_LESSON_TYPE, item.getLessonType());
            Intent fillInIntent = new Intent();
            fillInIntent.putExtras(extras);
            rv.setOnClickFillInIntent(R.id.widget_item, fillInIntent);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return widgetItems.get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        private void hideStudent(RemoteViews rv) {
            rv.setViewVisibility(R.id.tv_student, View.GONE);
        }

        private void showStudent(RemoteViews rv, LessonListWidgetItem item) {
            rv.setTextViewText(R.id.tv_student, item.getStudentName());
            rv.setTextViewCompoundDrawables(R.id.tv_student,
                    item.isStudentJoined() ? R.drawable.ic_round_blue_8dp : R.drawable.ic_round_grey_8dp, 0, 0, 0
            );
            rv.setViewVisibility(R.id.tv_student, View.VISIBLE);
        }

        private void hideTutor(RemoteViews rv) {
            rv.setViewVisibility(R.id.tv_tutor, View.GONE);
        }

        private void showTutor(RemoteViews rv, LessonListWidgetItem item) {
            rv.setTextViewText(R.id.tv_tutor, item.getTutorName());
            rv.setTextViewCompoundDrawables(R.id.tv_tutor,
                    item.isTutorJoined() ? R.drawable.ic_round_blue_8dp : R.drawable.ic_round_grey_8dp, 0, 0, 0
            );
            rv.setViewVisibility(R.id.tv_tutor, View.VISIBLE);
        }
    }
}
