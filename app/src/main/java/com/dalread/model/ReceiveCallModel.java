package com.dalread.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dalread.DalFlavor;
import com.dalread.util.Constant;

import java.io.Serializable;
import java.util.HashMap;

public class ReceiveCallModel implements Serializable {

    private String id;
    private String name;
    private String roomId;
    private String roomName;
    private String lessonId;
    private String studyRole;
    private String body;
    private String replyCallType;
    private String method;
    private int screen = Constant.JITSI.SCREEN_RECEIVE_DATA.NONE;
    private String callType;

    public ReceiveCallModel() {
        this(Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK,
                Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK);
    }

    public ReceiveCallModel(String id, String name, String roomId, String roomName, String lessonId, String studyRole, String body, String replyCallType, String method, String callType) {
        this.id = id;
        this.name = name;
        this.roomId = roomId;
        this.roomName = roomName;
        this.lessonId = lessonId;
        this.studyRole = studyRole;
        this.body = body;
        this.replyCallType = replyCallType;
        this.method = method;
        this.callType = callType;
    }

    public ReceiveCallModel(Bundle bundle) {
        id = bundle.getString(Constant.NOTIFICATION_KEY.CALLER_UID, Constant.BASE_BLANK);
        name = bundle.getString(Constant.NOTIFICATION_KEY.CALLER_USERNAME, Constant.BASE_BLANK);
        roomId = bundle.getString(Constant.NOTIFICATION_KEY.CHATROOM_ID, Constant.BASE_BLANK);
        roomName = bundle.getString(Constant.NOTIFICATION_KEY.CALL_ROOMNAME, Constant.BASE_BLANK);
        lessonId = bundle.getString(Constant.NOTIFICATION_KEY.LESSON_ID, Constant.BASE_BLANK);
        studyRole = bundle.getString(Constant.NOTIFICATION_KEY.OPPONENT_STUDY_ROLE, Constant.BASE_BLANK);
        body = bundle.getString(Constant.NOTIFICATION_KEY.NOTIFICATION_BODY, Constant.BASE_BLANK);
        replyCallType = bundle.getString(Constant.NOTIFICATION_KEY.REPLY_CALL_TYPE, Constant.BASE_BLANK);
        method = bundle.getString(Constant.NOTIFICATION_KEY.METHOD_NAME, Constant.BASE_BLANK);
        callType = bundle.getString(Constant.NOTIFICATION_KEY.CALL_TYPE, Constant.BASE_BLANK);
    }

    public ReceiveCallModel(HashMap<String, String> handle) {
        id = handle.get(Constant.NOTIFICATION_KEY.CALLER_UID);
        name = handle.get(Constant.NOTIFICATION_KEY.CALLER_USERNAME);
        lessonId = handle.get(Constant.NOTIFICATION_KEY.LESSON_ID);
        studyRole = handle.get(Constant.NOTIFICATION_KEY.OPPONENT_STUDY_ROLE);
    }

    public ReceiveCallModel(Intent intent) {
        id = intent.getStringExtra(Constant.NOTIFICATION_KEY.CALLER_UID);
        name = intent.getStringExtra(Constant.NOTIFICATION_KEY.CALLER_USERNAME);
        lessonId = intent.getStringExtra(Constant.NOTIFICATION_KEY.LESSON_ID);
        studyRole = intent.getStringExtra(Constant.NOTIFICATION_KEY.OPPONENT_STUDY_ROLE);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getStudyRole() {
        return studyRole;
    }

    public void setStudyRole(String studyRole) {
        this.studyRole = studyRole;
    }

    public int getScreen() {
        return screen;
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public void setScreen(Activity currentActivity) {
        setScreen(currentActivity, null);
    }

    public void setScreen(Activity currentActivity, ReceiveCallModel receiveCallModel) {
        screen = DalFlavor.getReceiveDataScreen(currentActivity, receiveCallModel, roomId);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getReplyCallType() {
        return replyCallType;
    }

    public void setReplyCallType(String replyCallType) {
        this.replyCallType = replyCallType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    @Override
    public String toString() {
        return "ReceiveCallModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", roomId='" + roomId + '\'' +
                ", roomName='" + roomName + '\'' +
                ", lessonId='" + lessonId + '\'' +
                ", studyRole='" + studyRole + '\'' +
                ", body='" + body + '\'' +
                ", replyCallType='" + replyCallType + '\'' +
                ", method='" + method + '\'' +
                ", screen=" + screen +
                ", callType='" + callType + '\'' +
                '}';
    }
}
