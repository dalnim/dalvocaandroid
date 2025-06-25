package com.dalread.model;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.dalread.base.EnumMessageAction;
import com.google.firebase.Timestamp;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

public class ChatMessage implements IMessage, MessageContentType.Image, MessageContentType {

    private int action;
    private String content;
    private Timestamp created;
    private String downloadURL;
    private String duration;
    private String senderId;
    private String senderName;
    private String thumbnailURL;
    private int type;
    private Object updated;
    private String messageId;
    private ChatUser chatUser;
    private boolean playing;
    private int playingMillis;
    private int totalMillis;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created.toDate();
    }

    public void setCreated(Date created) {
        this.created = new Timestamp(created);
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getUpdated() {
        return updated;
    }

    public void setUpdated(Object updated) {
        this.updated = updated;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public int getPlayingMillis() {
        return playingMillis;
    }

    public void setPlayingMillis(int playingMillis) {
        this.playingMillis = playingMillis;
    }

    public int getTotalMillis() {
        return totalMillis;
    }

    public void setTotalMillis(int totalMillis) {
        this.totalMillis = totalMillis;
    }

    @Override
    public String getId() {
        return getMessageId();
    }

    @Override
    public String getText() {
        String text = getContent();
        if (getAction() == EnumMessageAction.EDIT.getId()) {
            text += " (Edited)";
        }
        return text;
    }

    @Override
    public IUser getUser() {
        if (chatUser == null) {
            chatUser = new ChatUser(senderId, senderName);
        }
        return chatUser;
    }

    @Override
    public Date getCreatedAt() {
        return getCreated();
    }

    @Nullable
    @Override
    public String getImageUrl() {
        String url = getThumbnailURL();
        return TextUtils.isEmpty(url) ? null : url;
    }
}
