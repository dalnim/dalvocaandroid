package com.dalread.model.roleplaying;

import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ContentMain {

    @SerializedName("CONTENT_SUB_TITLE")
    private String contentSubTitle;
    @SerializedName("CONTENT_SUB_TITLE_RUBY_TEXT")
    private String contentSubTitleRubyText;
    @SerializedName("CONTENT_SUB_DETAILS")
    private List<ContentSubDetails> contentSubDetails;

    public String getContentSubTitle() {
        return contentSubTitle;
    }

    public void setContentSubTitle(String contentSubTitle) {
        this.contentSubTitle = contentSubTitle;
    }

    public String getContentSubTitleRubyText() {
        return contentSubTitleRubyText;
    }

    public void setContentSubTitleRubyText(String contentSubTitleRubyText) {
        this.contentSubTitleRubyText = contentSubTitleRubyText;
    }

    public List<ContentSubDetails> getContentSubDetails() {
        if (contentSubDetails == null) {
            setContentSubDetails(new ArrayList<>());
        }
        return contentSubDetails;
    }

    public void setContentSubDetails(List<ContentSubDetails> contentSubDetails) {
        this.contentSubDetails = contentSubDetails;
    }

    public boolean updateVocaDisplayRubyText(int vocaId, String key, int newValue) {
        boolean result = Utils.updateRubyText(getContentSubTitleRubyText(), vocaId, key, newValue, this::setContentSubTitleRubyText);
        for (ContentSubDetails contentSubDetails : getContentSubDetails()) {
            result |= contentSubDetails.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        return result;
    }
}
