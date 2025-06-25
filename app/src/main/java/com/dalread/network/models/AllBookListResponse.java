package com.dalread.network.models;

import com.dalread.model.VocaBook;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllBookListResponse {

    @SerializedName("USER_VOCA_BOOKLIST")
    private ArrayList<VocaBook> userBooks;
    @SerializedName("SERVER_VOCA_BOOKLIST")
    private ArrayList<VocaBook> serverBooks;

    public ArrayList<VocaBook> getUserBooks() {
        if (userBooks == null) {
            setUserBooks(new ArrayList<VocaBook>());
        }
        return userBooks;
    }

    public void setUserBooks(ArrayList<VocaBook> userBooks) {
        this.userBooks = userBooks;
    }

    public ArrayList<VocaBook> getServerBooks() {
        if (serverBooks == null) {
            setServerBooks(new ArrayList<VocaBook>());
        }
        return serverBooks;
    }

    public void setServerBooks(ArrayList<VocaBook> serverBooks) {
        this.serverBooks = serverBooks;
    }
}
