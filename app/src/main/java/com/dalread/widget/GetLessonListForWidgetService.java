package com.dalread.widget;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.dalread.BaseApplication;
import com.dalread.base.EnumUserType;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.Lesson;
import com.dalread.network.DalApiListener;
import com.dalread.util.BaseVoca;
import com.dalread.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class GetLessonListForWidgetService extends JobIntentService {

    private static final int JOB_ID = 1567931547;

    public static void getLessonList(Context context) {
        Intent intent = new Intent(context, GetLessonListForWidgetService.class);
        enqueueWork(context, GetLessonListForWidgetService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Application application = getApplication();
        if (application instanceof BaseApplication) {
            BaseApplication dalApplication = (BaseApplication) application;
            List<LessonListWidgetItem> result = new ArrayList<>();
            SharedPreferencesDB sharedPreferences = dalApplication.getSharedPref();
            int uid = sharedPreferences.getRealUid();
            if (uid > 0) {
                if (sharedPreferences.getUserType() == EnumUserType.SYSTEM_MANAGER.getType()) {
                    getLessonListFor(dalApplication, Constant.API_VALUE.LIST_LESSON_FOR_ADMIN, result);
                } else {
                    getLessonListFor(dalApplication, Constant.API_VALUE.LIST_LESSON_FOR_STUDENT, result);
                }
            } else {
                saveResultAndBroadcast(result, Constant.API_VALUE.LIST_LESSON_FOR_STUDENT);
            }
        }
    }

    private void getLessonListFor(final BaseApplication dalApplication, final int whom, final List<LessonListWidgetItem> result) {
        dalApplication.getDalAiImpl().getListOfLesson(
                Constant.API_VALUE.APP_TYPE_WIDGET,
                whom,
                new DalApiListener<List<Lesson>>() {

                    @Override
                    public void onSuccess(List<Lesson> response) {
                        if (response != null) {
                            addLessonToResult(response, result, whom);
                        }
                        if (whom == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
                            getLessonListFor(dalApplication, Constant.API_VALUE.LIST_LESSON_FOR_TUTOR, result);
                        } else {
                            saveResultAndBroadcast(result, whom);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                    }
                }
        );
    }

    private void addLessonToResult(List<Lesson> lessons, List<LessonListWidgetItem> items, int whom) {
        for (Lesson lesson : lessons) {
            lesson.setLessonType(whom);
            items.add(new LessonListWidgetItem(lesson));
        }
    }

    private void saveResultAndBroadcast(final List<LessonListWidgetItem> result, int whom) {
        BaseVoca.executeRealmTransaction(realm -> {
            realm.delete(LessonListWidgetItem.class);
            realm.copyToRealm(result);
        });
        Intent intent = new Intent(this, LessonListWidget.class);
        intent.setAction(Constant.BUNDLE.KEY_LESSON_LIST_ACTION);
        intent.putExtra(Constant.BUNDLE.KEY_LESSON_TYPE, whom);
        getApplicationContext().sendBroadcast(intent);
    }
}
