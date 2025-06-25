package com.dalread.util;

import com.dalread.BaseApplication;
import com.dalread.model.Lesson;
import com.dalread.network.DalApiListener;

public class ApiManager {
    public static final String TAG = "ApiManager";

    public static void replyCallFromOpponent(BaseApplication application,
                                             Lesson lesson,
                                             int typeCall,
                                             int chatRoomId,
                                             DalApiListener<Boolean> listener) {
        replyCallFromOpponent(application, lesson, typeCall, String.valueOf(chatRoomId), listener);
    }

    public static void replyCallFromOpponent(BaseApplication application,
                                             Lesson lesson,
                                             int typeCall,
                                             String chatRoomId,
                                             DalApiListener<Boolean> listener) {
        DLog.d(TAG, "replyCallFromOpponent - typeCall=" + typeCall);
        if (lesson == null) {
            DLog.d(TAG, "lesson is null");
            return;
        }
        int uid, opponentId;
        if (lesson.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_STUDENT) {
            uid = lesson.getStudentId();
            opponentId = lesson.getTutorId();
        } else if (lesson.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR) {
            uid = lesson.getTutorId();
            opponentId = lesson.getStudentId();
        } else {
            uid = application.getSharedPref().getRealUid();
            opponentId = 0;
        }
        application.getDalAiImpl().replyCallFromOpponent(Constant.BASE_BLANK,
                uid,
                opponentId,
                typeCall,
                chatRoomId,
                lesson.getId(),
                listener);
    }
}
