package com.dalread.model.roleplaying;

import android.text.TextUtils;

import com.dalread.model.VocaStudyChat;
import com.dalread.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conversation implements Serializable {

    @SerializedName("CONVERSATION_ID")
    private int conversationId;
    @SerializedName("TITLE")
    private String title;
    @SerializedName("TITLE_RUBY_TEXT")
    private String titleRubyText;
    @SerializedName("PERSON_AB")
    private String personAB;
    @SerializedName("DEFAULT_PHRASE_FOR_STUDENT")
    private String defaultPhraseForStudent;
    @SerializedName("DEFAULT_PHRASE_FOR_STUDENT_RUBY_TEXT")
    private String defaultPhraseForStudentRubyText;
    @SerializedName("DEFAULT_PHRASE_LANGUAGE_LEVEL")
    private int defaultPhraseLanguageLevel;
    @SerializedName("VALUE")
    private String value;
    @SerializedName("VALUE_RUBY_TEXT")
    private String valueRubyText;
    @SerializedName("INDEX")
    private int index;
    @SerializedName("SENTENCES")
    private List<VocaStudyChat> sentences;
    private Integer tblSection;
    private Integer tblRow;
    private boolean checked;
    @SerializedName("BRANCH")
    private String branch;
    private List<String> branches;
    @SerializedName("BRANCH_SELECTED")
    private String branchSelected;
    @SerializedName("DISPLAY_CONVERSATION")
    private String displayConversation;
    private String rubyVocaIds;
    private String rubyVocaTypes;

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleRubyText() {
        return titleRubyText;
    }

    public void setTitleRubyText(String titleRubyText) {
        this.titleRubyText = titleRubyText;
    }

    public String getPersonAB() {
        return personAB;
    }

    public void setPersonAB(String personAB) {
        this.personAB = personAB;
    }

    public String getDefaultPhraseForStudent() {
        return defaultPhraseForStudent;
    }

    public void setDefaultPhraseForStudent(String defaultPhraseForStudent) {
        this.defaultPhraseForStudent = defaultPhraseForStudent;
    }

    public String getDefaultPhraseForStudentRubyText() {
        return defaultPhraseForStudentRubyText;
    }

    public void setDefaultPhraseForStudentRubyText(String defaultPhraseForStudentRubyText) {
        this.defaultPhraseForStudentRubyText = defaultPhraseForStudentRubyText;
    }

    public int getDefaultPhraseLanguageLevel() {
        return defaultPhraseLanguageLevel;
    }

    public void setDefaultPhraseLanguageLevel(int defaultPhraseLanguageLevel) {
        this.defaultPhraseLanguageLevel = defaultPhraseLanguageLevel;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueRubyText() {
        return valueRubyText;
    }

    public void setValueRubyText(String valueRubyText) {
        this.valueRubyText = valueRubyText;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<VocaStudyChat> getSentences() {
        if (sentences == null) {
            setSentences(new ArrayList<>());
        }
        return sentences;
    }

    public void setSentences(List<VocaStudyChat> sentences) {
        this.sentences = sentences;
    }

    public Integer getTblSection() {
        return tblSection;
    }

    public void setTblSection(Integer tblSection) {
        this.tblSection = tblSection;
    }

    public Integer getTblRow() {
        return tblRow;
    }

    public void setTblRow(Integer tblRow) {
        this.tblRow = tblRow;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public List<String> getBranches() {
        if (branches == null) {
            setBranches(new ArrayList<>());
        }
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
    }

    public String getBranchSelected() {
        return branchSelected;
    }

    public void setBranchSelected(String branchSelected) {
        this.branchSelected = branchSelected;
    }

    public String getDisplayConversation() {
        return displayConversation;
    }

    public void setDisplayConversation(String displayConversation) {
        this.displayConversation = displayConversation;
    }

    public boolean isDisplayConversation() {
        return "DISPLAY".equals(displayConversation);
    }

    public String getRubyVocaIds() {
        return rubyVocaIds;
    }

    public String getRubyVocaTypes() {
        return rubyVocaTypes;
    }

    public void setRubyVocaIdsAndTypes(String rubyVocaIds, String rubyVocaTypes) {
        if (TextUtils.isEmpty(this.rubyVocaIds)) {
            this.rubyVocaIds = rubyVocaIds;
            this.rubyVocaTypes = rubyVocaTypes;
        } else if (!TextUtils.isEmpty(rubyVocaIds)) {
            rubyVocaIds = this.rubyVocaIds + "," + rubyVocaIds;
            rubyVocaTypes = this.rubyVocaTypes + "," + rubyVocaTypes;
            StringBuilder ids = new StringBuilder();
            StringBuilder types = new StringBuilder();
            List<String> list = new ArrayList<>();
            String[] idArray = rubyVocaIds.split(",");
            String[] typeArray = rubyVocaTypes.split(",");
            for (int i = 0; i < idArray.length; i++) {
                if (!list.contains(idArray[i])) {
                    list.add(idArray[i]);
                    ids.append(",").append(idArray[i]);
                    types.append(",").append(typeArray[i]);
                }
            }
            this.rubyVocaIds = ids.substring(1);
            this.rubyVocaTypes = types.substring(1);
        }
    }

    public boolean updateRubyText(int vocaId, String key, int newValue) {
        return Utils.updateRubyText(getTitleRubyText(), vocaId, key, newValue, this::setTitleRubyText)
                || Utils.updateRubyText(getDefaultPhraseForStudentRubyText(), vocaId, key, newValue, this::setDefaultPhraseForStudentRubyText)
                || Utils.updateRubyText(getValueRubyText(), vocaId, key, newValue, this::setValueRubyText);
    }
}
