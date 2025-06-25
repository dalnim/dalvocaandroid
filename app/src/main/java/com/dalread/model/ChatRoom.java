package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatRoom implements Serializable {

    @SerializedName("ID")
    private int id;
    @SerializedName("CHATROOM_NAME")
    private String chatRoomName;
    @SerializedName("CHATROOM_TYPE")
    private int chatRoomType;
    @SerializedName("OPPONENT_UID")
    private int opponentUid;
    @SerializedName("USER_LIST_TOTAL")
    private int userListTotal;
    @SerializedName("LAST_ACCESS_DATE")
    private String lastAccessDate; // TODO: @dalnim do we have timestamp here?
    @SerializedName("LAST_MESSAGE")
    private String lastMessage;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public int getChatRoomType() {
        return chatRoomType;
    }

    public void setChatRoomType(int chatRoomType) {
        this.chatRoomType = chatRoomType;
    }

    public int getOpponentUid() {
        return opponentUid;
    }

    public void setOpponentUid(int opponentUid) {
        this.opponentUid = opponentUid;
    }

    public int getUserListTotal() {
        return userListTotal;
    }

    public void setUserListTotal(int userListTotal) {
        this.userListTotal = userListTotal;
    }

    public String getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(String lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
