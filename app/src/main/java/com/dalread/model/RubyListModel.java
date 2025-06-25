package com.dalread.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RubyListModel {
    @SerializedName("LIST_RUBY_TEXT")
    private List<RubyTextModel> rubyTextModels;

    public RubyListModel() {
        this(new ArrayList<>());
    }

    public RubyListModel(List<RubyTextModel> rubyTextModels) {
        this.rubyTextModels = rubyTextModels;
    }

    public List<RubyTextModel> getRubyTextModels() {
        return rubyTextModels;
    }

    public void setRubyTextModels(List<RubyTextModel> rubyTextModels) {
        this.rubyTextModels = rubyTextModels;
    }
}
