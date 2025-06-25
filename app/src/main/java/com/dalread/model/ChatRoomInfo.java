package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatRoomInfo implements Serializable {

    @SerializedName("CHATROOM_ID")
    private int firestoreChatRoomId;
    @SerializedName("CHATROOM_NAME")
    private String chatRoomName;
    @SerializedName("USER_LIST_TOTAL")
    private int userListTotal;
    @SerializedName("USER_LIST")
    private ArrayList<User> userList;
    @SerializedName("VOCABOOK")
    private VocaBookInChat vocaBook;
    @SerializedName("IS_FIRST_JOINED_USER")
    private int isFirstJoinedUser;

    public int getFirestoreChatRoomId() {
        return firestoreChatRoomId;
    }

    public void setFirestoreChatRoomId(int firestoreChatRoomId) {
        this.firestoreChatRoomId = firestoreChatRoomId;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public int getUserListTotal() {
        return userListTotal;
    }

    public void setUserListTotal(int userListTotal) {
        this.userListTotal = userListTotal;
    }

    public ArrayList<User> getUserList() {
        if (userList == null) {
            userList = new ArrayList<>();
        }
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public VocaBookInChat getVocaBook() {
        return vocaBook;
    }

    public void setVocaBook(VocaBookInChat vocaBook) {
        this.vocaBook = vocaBook;
    }

    @Override
    public String toString() {
        return "ChatRoomInfo{" +
                "firestoreChatRoomId=" + firestoreChatRoomId +
                ", chatRoomName='" + chatRoomName + '\'' +
                ", userListTotal=" + userListTotal +
                ", userList=" + userList +
                ", vocaBook=" + vocaBook +
                '}';
    }

    public int getIsFirstJoinedUser() {
        return isFirstJoinedUser;
    }

    public void setIsFirstJoinedUser(int isFirstJoinedUser) {
        this.isFirstJoinedUser = isFirstJoinedUser;
    }
}
