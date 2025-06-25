package com.dalread.network.models;

import com.dalread.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserListResponse {

    @SerializedName("USER_LIST_TOTAL")
    private int userListTotal;
    @SerializedName("USER_LIST")
    private ArrayList<User> users;

    public int getUserListTotal() {
        return userListTotal;
    }

    public void setUserListTotal(int userListTotal) {
        this.userListTotal = userListTotal;
    }

    public ArrayList<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
}
