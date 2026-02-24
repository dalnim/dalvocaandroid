package com.dalread.model;

/**
 * Created by vuson on 3/3/17.
 */

public class MenuModel {
    private int id;
    private int icon;
    private String title;
    private boolean isShow = true;
    /** 드로어 "무료로 앱 다운받기" 섹션 헤더 행 여부 */
    private boolean sectionHeader;
    /** 앱 다운로드 행일 때 Play Store URL. null이면 일반 메뉴 클릭(id 사용) */
    private String playStoreUrl;
    /** 앱 다운로드 행일 때 앱 ID. 0이 아니면 "무료 앱들" 화면을 이 앱 선택 상태로 연다. */
    private int appIdForFreeApps;

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

    /** 섹션 헤더용 (드로어 "무료로 앱을 다운받으세요" 등). id/icon 무시. */
    public static MenuModel createSectionHeader(String title) {
        MenuModel m = new MenuModel(0, 0, title, true);
        m.sectionHeader = true;
        return m;
    }

    /** 다른 앱 다운로드 행용. 클릭 시 "무료 앱들" 화면을 selectedAppId 선택 상태로 연다. */
    public static MenuModel createAppDownloadRow(int iconResId, String title, int selectedAppId, String playStoreUrl) {
        MenuModel m = new MenuModel(0, iconResId, title, true);
        m.appIdForFreeApps = selectedAppId;
        m.playStoreUrl = playStoreUrl;
        return m;
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

    public boolean isSectionHeader() {
        return sectionHeader;
    }

    public void setSectionHeader(boolean sectionHeader) {
        this.sectionHeader = sectionHeader;
    }

    public String getPlayStoreUrl() {
        return playStoreUrl;
    }

    public void setPlayStoreUrl(String playStoreUrl) {
        this.playStoreUrl = playStoreUrl;
    }

    public int getAppIdForFreeApps() {
        return appIdForFreeApps;
    }

    public void setAppIdForFreeApps(int appIdForFreeApps) {
        this.appIdForFreeApps = appIdForFreeApps;
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
