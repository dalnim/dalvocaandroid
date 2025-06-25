package com.dalread.model;

import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MultipleWordMeaningWithIDModel {
    @SerializedName(Constant.API_KEY.KEY_UID)
    private int uid;
    @SerializedName(Constant.API_KEY.KEY_LANG_STUDY_CODE)
    private int studyCode;
    @SerializedName(Constant.API_KEY.KEY_LANG_MEANING_CODE)
    private int meaningCode;
    @SerializedName("VOCA_BASIC_INFO_LIST")
    private List<InfoListModel> infoList;

    public MultipleWordMeaningWithIDModel() {
        this(0, 0, 0, new ArrayList<>());
    }

    public MultipleWordMeaningWithIDModel(int uid, int studyCode, int meaningCode, List<InfoListModel> infoList) {
        this.uid = uid;
        this.studyCode = studyCode;
        this.meaningCode = meaningCode;
        this.infoList = infoList;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getStudyCode() {
        return studyCode;
    }

    public void setStudyCode(int studyCode) {
        this.studyCode = studyCode;
    }

    public int getMeaningCode() {
        return meaningCode;
    }

    public void setMeaningCode(int meaningCode) {
        this.meaningCode = meaningCode;
    }

    public List<InfoListModel> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<InfoListModel> infoList) {
        this.infoList = infoList;
    }

    public static class InfoListModel {
        @SerializedName(Constant.API_KEY.KEY_INDEX)
        private int index;
        @SerializedName(Constant.API_KEY.KEY_VOCA_TYPE)
        private int vocaType;
        @SerializedName(Constant.API_KEY.KEY_VOCA_ID)
        private int vocaId;
        @SerializedName(Constant.API_KEY.KEY_MEANING)
        private String meaning;
        @SerializedName(Constant.API_KEY.KEY_MEANING_DETAILED)
        private String meaningDetailed;
        @SerializedName(Constant.API_KEY.KEY_PRONOUNCE)
        private String pronounce;
        @SerializedName(Constant.API_KEY.KEY_VOCA)
        private String voca;
        @SerializedName(Constant.API_KEY.KEY_UPDATE_RESULT)
        private int updateResult;
        @SerializedName(Constant.API_KEY.KEY_POSALL)
        private String posAll;

        public InfoListModel() {
            this(0, 0, 0, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, 0, "");
        }

        public InfoListModel(int index, int vocaType, int vocaId, String meaning, String meaningDetailed, String pronounce, String voca, int updateResult, String posAll) {
            this.index = index;
            this.vocaType = vocaType;
            this.vocaId = vocaId;
            this.meaning = meaning;
            this.meaningDetailed = meaningDetailed;
            this.pronounce = pronounce;
            this.voca = voca;
            this.updateResult = updateResult;
            this.posAll = posAll;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getVocaType() {
            return vocaType;
        }

        public void setVocaType(int vocaType) {
            this.vocaType = vocaType;
        }

        public int getVocaId() {
            return vocaId;
        }

        public void setVocaId(int vocaId) {
            this.vocaId = vocaId;
        }

        public String getMeaning() {
            return meaning;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public String getMeaningDetailed() {
            return meaningDetailed;
        }

        public void setMeaningDetailed(String meaningDetailed) {
            this.meaningDetailed = meaningDetailed;
        }

        public String getPronounce() {
            return pronounce;
        }

        public void setPronounce(String pronounce) {
            this.pronounce = pronounce;
        }

        public String getVoca() {
            return voca;
        }

        public void setVoca(String voca) {
            this.voca = voca;
        }

        public int getUpdateResult() {
            return updateResult;
        }

        public void setUpdateResult(int updateResult) {
            this.updateResult = updateResult;
        }

        public String getPosAll() {
            return posAll;
        }

        public void setPosAll(String posAll) {
            this.posAll = posAll;
        }
    }

    public static class ResponseModel {
        @SerializedName(Constant.API_KEY.KEY_NEW_VOCA_ID_LIST)
        private List<InfoListModel> newVocaIdList;
        @SerializedName(Constant.API_KEY.KEY_UPDATE_RESULT_LIST)
        private List<InfoListModel> updateResultList;

        public ResponseModel() {
        }

        public ResponseModel(List<InfoListModel> newVocaIdList, List<InfoListModel> updateResultList) {
            this.newVocaIdList = newVocaIdList;
            this.updateResultList = updateResultList;
        }

        public List<InfoListModel> getNewVocaIdList() {
            return newVocaIdList;
        }

        public void setNewVocaIdList(List<InfoListModel> newVocaIdList) {
            this.newVocaIdList = newVocaIdList;
        }

        public List<InfoListModel> getUpdateResultList() {
            return updateResultList;
        }

        public void setUpdateResultList(List<InfoListModel> updateResultList) {
            this.updateResultList = updateResultList;
        }

        public boolean isHasNewVocaIdList() {
            return newVocaIdList != null && !newVocaIdList.isEmpty();
        }

        public int getVocaId() {
            return newVocaIdList.get(0).getVocaId();
        }
    }
}
