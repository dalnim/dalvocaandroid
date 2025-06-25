package com.dalread.asynctask;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.Lesson;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.DateUtils;
import com.dalread.util.Voca;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LessonNotificationWorker extends Worker {

    public LessonNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        sendLessonNotification();
        return Result.success();
    }

    private void sendLessonNotification() {
        Data data = getInputData();
        byte[] bytes = data.getByteArray(Constant.BUNDLE.KEY_LESSON);
        if (bytes == null)
            return;
        Lesson lesson = (Lesson) Voca.restoreObject(bytes);
        if (lesson == null)
            return;
        boolean isStartTime = data.getBoolean(Constant.BUNDLE.KEY_START_TIME, false);
        Voca.createLessonNotification(getApplicationContext(), lesson, isStartTime);
    }

    public static void scheduleWork(Context context, SharedPreferencesDB sharedPreferences, Lesson lesson) {
        if (lesson == null)
            return;
        DLog.i("scheduleWork", "lessonId = " + lesson.getId() + " & lessonType = " + lesson.getLessonType());
        String tag = String.valueOf(lesson.getLessonType());
        byte[] bytes = Voca.backupObject(lesson);
        if (bytes == null)
            return;
        //
        Date lessonStartTime = new Date(DateUtils.secondsToMillis(lesson.getLessonStartTimeTS()));
        long delayMillis = lessonStartTime.getTime()
                - System.currentTimeMillis()
                - TimeUnit.MINUTES.toMillis(sharedPreferences.getTimeToNotifyBeforeLessonStart());
        if (delayMillis > 0) {
            Data data = new Data.Builder()
                    .putByteArray(Constant.BUNDLE.KEY_LESSON, bytes)
                    .putBoolean(Constant.BUNDLE.KEY_START_TIME, true)
                    .build();
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(LessonNotificationWorker.class)
                    .addTag(tag)
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
//                    .setInitialDelay(5, TimeUnit.SECONDS) // testing purpose
                    .setInputData(data)
                    .build();
            WorkManager.getInstance(context).enqueue(request);
        }
        //
        Date lessonFinishTime = new Date(DateUtils.secondsToMillis(lesson.getLessonFinishTimeTS()));
        delayMillis = lessonFinishTime.getTime()
                - System.currentTimeMillis()
                - TimeUnit.MINUTES.toMillis(sharedPreferences.getTimeToNotifyBeforeLessonFinish());
        if (delayMillis > 0) {
            Data data = new Data.Builder()
                    .putByteArray(Constant.BUNDLE.KEY_LESSON, bytes)
                    .putBoolean(Constant.BUNDLE.KEY_START_TIME, false)
                    .build();
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(LessonNotificationWorker.class)
                    .addTag(tag)
                    .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
//                        .setInitialDelay(15, TimeUnit.SECONDS) // testing purpose
                    .setInputData(data)
                    .build();
            WorkManager.getInstance(context).enqueue(request);
        }
    }


    public static void cancelWork(Context context, int lessonType) {
        DLog.i("cancelWork", "lessonType = " + lessonType);
        String tag = String.valueOf(lessonType);
        WorkManager.getInstance(context).cancelAllWorkByTag(tag);
    }
}
