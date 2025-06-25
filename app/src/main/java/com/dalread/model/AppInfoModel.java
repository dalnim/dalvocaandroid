package com.dalread.model;

import android.graphics.drawable.Drawable;

public class AppInfoModel {
    private String title;
    private String description;
    private Drawable icon;
    private String googlePlayUrl;

    public AppInfoModel(String title, String description, Drawable icon, String googlePlayPath) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.googlePlayUrl = googlePlayPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getGooglePlayUrl() {
        return googlePlayUrl;
    }

    public void setGooglePlayUrl(String googlePlayUrl) {
        this.googlePlayUrl = googlePlayUrl;
    }
}
