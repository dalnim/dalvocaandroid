package com.dalread.model.roleplaying;

import com.dalread.model.VocaStudyChat;
import com.dalread.model.VocaStudyChatAllWords;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RolePlayingContent implements Serializable {

    @SerializedName("MISSION")
    private Mission mission;
    @SerializedName("CONVERSATION")
    private List<Conversation> conversationList;
    @SerializedName("ROLE_PLAYING_CONTENT_TYPE")
    private int rolePlayingContentType;
    @SerializedName("CONTENT")
    private Content content;
    @SerializedName("ROLE_PLAYING_CATEGORY_ID")
    private int rolePlayingCategoryId;
    @SerializedName("ROLE_PLAYING_TYPE")
    private int rolePlayingType;
    @SerializedName("LANGUAGE_LEVEL")
    private int languageLevel;
    @SerializedName("VOCA_LIST_BY_CATEGORY")
    private List<VocaStudyChatAllWords> vocaListByCategory;
    @SerializedName("ALL_SENTENCE_LIST")
    private List<VocaStudyChat> allSentenceList;
    @SerializedName("STUDY_SENTENCE_LIST")
    private List<VocaStudyChat> studySentenceList;
    @SerializedName("ALL_VOCA_LIST")
    private List<VocaStudyChat> allVocaList;
    @SerializedName("STUDY_VOCA_LIST")
    private List<VocaStudyChat> studyVocaList;

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public List<Conversation> getConversationList() {
        if (conversationList == null) {
            setConversationList(new ArrayList<>());
        }
        return conversationList;
    }

    public void setConversationList(List<Conversation> conversationList) {
        this.conversationList = conversationList;
    }

    public int getRolePlayingContentType() {
        return rolePlayingContentType;
    }

    public void setRolePlayingContentType(int rolePlayingContentType) {
        this.rolePlayingContentType = rolePlayingContentType;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public int getRolePlayingCategoryId() {
        return rolePlayingCategoryId;
    }

    public void setRolePlayingCategoryId(int rolePlayingCategoryId) {
        this.rolePlayingCategoryId = rolePlayingCategoryId;
    }

    public int getRolePlayingType() {
        return rolePlayingType;
    }

    public void setRolePlayingType(int rolePlayingType) {
        this.rolePlayingType = rolePlayingType;
    }

    public int getLanguageLevel() {
        return languageLevel;
    }

    public void setLanguageLevel(int languageLevel) {
        this.languageLevel = languageLevel;
    }

    public List<VocaStudyChatAllWords> getVocaListByCategory() {
        if (vocaListByCategory == null) {
            setVocaListByCategory(new ArrayList<>());
        }
        return vocaListByCategory;
    }

    public void setVocaListByCategory(List<VocaStudyChatAllWords> vocaListByCategory) {
        this.vocaListByCategory = vocaListByCategory;
    }

    public List<VocaStudyChat> getAllSentenceList() {
        if (allSentenceList == null) {
            setAllSentenceList(new ArrayList<>());
        }
        return allSentenceList;
    }

    public void setAllSentenceList(List<VocaStudyChat> allSentenceList) {
        this.allSentenceList = allSentenceList;
    }

    public List<VocaStudyChat> getStudySentenceList() {
        if (studySentenceList == null) {
            setStudySentenceList(new ArrayList<>());
        }
        return studySentenceList;
    }

    public void setStudySentenceList(List<VocaStudyChat> studySentenceList) {
        this.studySentenceList = studySentenceList;
    }

    public int getAllSentenceListShowGuide() {
        return getStudySentenceList().isEmpty() ? 0 : 1;
    }

    public List<VocaStudyChat> getAllVocaList() {
        if (allVocaList == null) {
            setAllVocaList(new ArrayList<>());
        }
        return allVocaList;
    }

    public void setAllVocaList(List<VocaStudyChat> allVocaList) {
        this.allVocaList = allVocaList;
    }

    public List<VocaStudyChat> getStudyVocaList() {
        if (studyVocaList == null) {
            setStudyVocaList(new ArrayList<>());
        }
        return studyVocaList;
    }

    public void setStudyVocaList(List<VocaStudyChat> studyVocaList) {
        this.studyVocaList = studyVocaList;
    }

    public int getAllVocaListShowGuide() {
        return getStudyVocaList().isEmpty() ? 0 : 1;
    }

    public boolean updateVocaDisplayRubyText(int vocaId, String key, int newValue) {
        boolean result = false;
        for (Conversation conversation : getConversationList()) {
            result |= conversation.updateRubyText(vocaId, key, newValue);
        }
        if (content != null) {
            result |= content.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        for (VocaStudyChatAllWords voca : getVocaListByCategory()) {
            result |= voca.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        for (VocaStudyChat voca : getAllSentenceList()) {
            result |= voca.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        for (VocaStudyChat voca : getStudySentenceList()) {
            result |= voca.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        for (VocaStudyChat voca : getAllVocaList()) {
            result |= voca.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        for (VocaStudyChat voca : getStudyVocaList()) {
            result |= voca.updateVocaDisplayRubyText(vocaId, key, newValue);
        }
        return result;
    }
}
