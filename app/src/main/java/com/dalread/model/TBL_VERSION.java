package com.dalread.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TBL_VERSION extends RealmObject {

    @PrimaryKey
    private String TBL_NAME;
    private String VERSION;
    private String UPDATE_DATE;

    public String getTBL_NAME() {
        return TBL_NAME;
    }

    public void setTBL_NAME(String TBL_NAME) {
        this.TBL_NAME = TBL_NAME;
    }

    public String getVERSION() {
        return VERSION;
    }

    public void setVERSION(String VERSION) {
        this.VERSION = VERSION;
    }

    public String getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setUPDATE_DATE(String UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public boolean hasUpdate(String VERSION) {
        return this.VERSION == null || !this.VERSION.equals(VERSION);
    }
}
