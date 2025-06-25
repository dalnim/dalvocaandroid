package com.dalread.model.roleplaying;

import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Content implements Serializable {

    @SerializedName("CONTENT_VIEW_ID")
    private int contentViewId;
    @SerializedName("CONTENT_TITLE")
    private String contentTitle;
    @SerializedName("CONTENT_TITLE_RUBY_TEXT")
    private String contentTitleRubyText;
    @SerializedName("CONTENT_MAIN")
    private List<ContentMain> contentMainList;

    public int getContentViewId() {
        return contentViewId;
    }

    public void setContentViewId(int contentViewId) {
        this.contentViewId = contentViewId;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentTitleRubyText() {
        return contentTitleRubyText;
    }

    public void setContentTitleRubyText(String contentTitleRubyText) {
        this.contentTitleRubyText = contentTitleRubyText;
    }

    public List<ContentMain> getContentMainList() {
        if (contentMainList == null) {
            setContentMainList(new ArrayList<>());
        }
        return contentMainList;
    }

    public void setContentMainList(List<ContentMain> contentMainList) {
        this.contentMainList = contentMainList;
    }

    public boolean updateVocaDisplayRubyText(int vocaId, String key, int newValue) {
        boolean result = Utils.updateRubyText(getContentTitleRubyText(), vocaId, key, newValue, this::setContentTitleRubyText);
        for (ContentMain contentMain : getContentMainList()) {
            result |= contentMain.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        return result;
    }
}
