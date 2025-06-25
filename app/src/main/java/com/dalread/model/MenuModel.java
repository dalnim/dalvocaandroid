package com.dalread.model;

/**
 * Created by vuson on 3/3/17.
 */

public class MenuModel {
    private int id;
    private int icon;
    private String title;
    private boolean isShow = true;

    public MenuModel() {
    }

    public MenuModel(int id, int icon, String title) {
        this(id, icon, title, false);
    }

    public MenuModel(int id, int icon, String title, boolean isShow) {
        this.id = id;
        this.icon = icon;
        this.title = title;
        this.isShow = isShow;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    @Override
    public String toString() {
        return "MenuModel{" +
                "id=" + id +
                ", icon=" + icon +
                ", title='" + title + '\'' +
                ", isShow=" + isShow +
                '}';
    }
}
