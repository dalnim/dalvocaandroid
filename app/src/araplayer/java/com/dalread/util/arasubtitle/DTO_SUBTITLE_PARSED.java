package com.dalread.util.arasubtitle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DTO_SUBTITLE_PARSED {
    protected boolean HAS_STUDY_LANG_DIALOGUE = false;
    protected boolean NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE = false;
    protected MediaInfo MEDIAINFO = new MediaInfo();
    protected List<DTO_DIALOGUE> LIST_DIALOGUE_INFO = new ArrayList<>();
    protected List<String> LIST_DIALOGUE_STUDY_LANG = new ArrayList<>();
    protected List<String> LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION = new ArrayList<>();
    protected Map<String, DTO_DIALOGUE> MAP_DIALOGUE_INFO = new HashMap<>();
    protected Map<String, DTO_DIALOGUE> MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY = new HashMap<>();
    protected Map<String, DTO_DIALOGUE> MAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY = new HashMap<>();

    public boolean isHAS_STUDY_LANG_DIALOGUE() {
        return HAS_STUDY_LANG_DIALOGUE;
    }

    public void setHAS_STUDY_LANG_DIALOGUE(boolean HAS_STUDY_LANG_DIALOGUE) {
        this.HAS_STUDY_LANG_DIALOGUE = HAS_STUDY_LANG_DIALOGUE;
    }

    public boolean isNEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE() {
        return NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE;
    }

    public void setNEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(boolean NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE) {
        this.NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE = NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE;
    }

    public MediaInfo getMEDIAINFO() {
        return MEDIAINFO;
    }

    public void setMEDIAINFO(MediaInfo MEDIAINFO) {
        this.MEDIAINFO = MEDIAINFO;
    }

    public List<DTO_DIALOGUE> getLIST_DIALOGUE_INFO() {
        return LIST_DIALOGUE_INFO;
    }

    public void setLIST_DIALOGUE_INFO(List<DTO_DIALOGUE> LIST_DIALOGUE_INFO) {
        this.LIST_DIALOGUE_INFO = LIST_DIALOGUE_INFO;
    }

    public List<String> getLIST_DIALOGUE_STUDY_LANG() {
        return LIST_DIALOGUE_STUDY_LANG;
    }

    public void setLIST_DIALOGUE_STUDY_LANG(List<String> LIST_DIALOGUE_STUDY_LANG) {
        this.LIST_DIALOGUE_STUDY_LANG = LIST_DIALOGUE_STUDY_LANG;
    }

    public List<String> getLIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION() {
        return LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION;
    }

    public void setLIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION(List<String> LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION) {
        this.LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION = LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION;
    }

    public Map<String, DTO_DIALOGUE> getMAP_DIALOGUE_INFO() {
        return MAP_DIALOGUE_INFO;
    }

    public void setMAP_DIALOGUE_INFO(Map<String, DTO_DIALOGUE> MAP_DIALOGUE_INFO) {
        this.MAP_DIALOGUE_INFO = MAP_DIALOGUE_INFO;
    }

    public Map<String, DTO_DIALOGUE> getMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY() {
        return MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY;
    }

    public void setMAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY(Map<String, DTO_DIALOGUE> MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY) {
        this.MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY = MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY;
    }

    public Map<String, DTO_DIALOGUE> getMAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY() {
        return MAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY;
    }

    public void setMAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY(Map<String, DTO_DIALOGUE> MAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY) {
        this.MAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY = MAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY;
    }

    private DTO_SUBTITLE_PARSED() {
        // private constructor to prevent direct instantiation
    }

    public static class Builder {
        private DTO_SUBTITLE_PARSED parsedSubtitle;

        public Builder() {
            parsedSubtitle = new DTO_SUBTITLE_PARSED();
        }

        public Builder HAS_STUDY_LANG_DIALOGUE(boolean hasStudyLangDialogue) {
            parsedSubtitle.HAS_STUDY_LANG_DIALOGUE = hasStudyLangDialogue;
            return this;
        }

        public Builder NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE(boolean needToSync) {
            parsedSubtitle.NEED_TO_SYNC_DIALOG_STUDY_LANG_AND_MOTHER_TONGUE = needToSync;
            return this;
        }

        public Builder MEDIAINFO(MediaInfo mediaInfo) {
            parsedSubtitle.MEDIAINFO = mediaInfo;
            return this;
        }

        public Builder LIST_DIALOGUE_INFO(List<DTO_DIALOGUE> listDialogueInfo) {
            parsedSubtitle.LIST_DIALOGUE_INFO = listDialogueInfo;
            return this;
        }

        public Builder LIST_DIALOGUE_STUDY_LANG(List<String> listDialogueStudyLang) {
            parsedSubtitle.LIST_DIALOGUE_STUDY_LANG = listDialogueStudyLang;
            return this;
        }

        public Builder LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION(List<String> listDialogueStudyLangWithoutPunctuation) {
            parsedSubtitle.LIST_DIALOGUE_STUDY_LANG_WITHOUT_PUNCTUATION = listDialogueStudyLangWithoutPunctuation;
            return this;
        }

        public Builder MAP_DIALOGUE_INFO(Map<String, DTO_DIALOGUE> mapDialogueInfo) {
            parsedSubtitle.MAP_DIALOGUE_INFO = mapDialogueInfo;
            return this;
        }

        public Builder MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY(Map<String, DTO_DIALOGUE> mapDialogueInfoByVocaTypeIdKey) {
            parsedSubtitle.MAP_DIALOGUE_INFO_BY_VOCA_TYPE_ID_KEY = mapDialogueInfoByVocaTypeIdKey;
            return this;
        }

        public Builder MAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY(Map<String, DTO_DIALOGUE> mapDialogueInfoByBaseVocaTypeIdKey) {
            parsedSubtitle.MAP_DIALOGUE_INFO_BY_BASE_VOCA_TYPE_ID_KEY = mapDialogueInfoByBaseVocaTypeIdKey;
            return this;
        }

        public DTO_SUBTITLE_PARSED build() {
            return parsedSubtitle;
        }
    }
}

