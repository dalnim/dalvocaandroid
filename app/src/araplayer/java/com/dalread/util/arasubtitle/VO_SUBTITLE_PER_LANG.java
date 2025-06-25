package com.dalread.util.arasubtitle;

public class VO_SUBTITLE_PER_LANG extends VO_COMMON_MEANING_DETAILED {

    public int ID;
    public String VOCA;
    public String VOCA_WITHOUT_PUNCTUATION;
    public int USED;
    public int UPDATABLE;
    public int SUBTITLE_FILE_ID;
    public int DISP_ORDER;

    private VO_SUBTITLE_PER_LANG(Builder builder) {
        this.ID = builder.ID;
        this.VOCA = builder.VOCA;
        this.VOCA_WITHOUT_PUNCTUATION = builder.VOCA_WITHOUT_PUNCTUATION;
        this.USED = builder.USED;
        this.UPDATABLE = builder.UPDATABLE;
        this.SUBTITLE_FILE_ID = builder.SUBTITLE_FILE_ID;
        this.DISP_ORDER = builder.DISP_ORDER;
    }

    public static class Builder {
        private int ID;
        private String VOCA;
        private String VOCA_WITHOUT_PUNCTUATION;
        private int USED;
        private int UPDATABLE;
        private int SUBTITLE_FILE_ID;
        private int DISP_ORDER;

        public Builder() {}

        public Builder ID(int ID) {
            this.ID = ID;
            return this;
        }

        public Builder VOCA(String VOCA) {
            this.VOCA = VOCA;
            return this;
        }

        public Builder VOCA_WITHOUT_PUNCTUATION(String VOCA_WITHOUT_PUNCTUATION) {
            this.VOCA_WITHOUT_PUNCTUATION = VOCA_WITHOUT_PUNCTUATION;
            return this;
        }

        public Builder USED(int USED) {
            this.USED = USED;
            return this;
        }

        public Builder UPDATABLE(int UPDATABLE) {
            this.UPDATABLE = UPDATABLE;
            return this;
        }

        public Builder SUBTITLE_FILE_ID(int SUBTITLE_FILE_ID) {
            this.SUBTITLE_FILE_ID = SUBTITLE_FILE_ID;
            return this;
        }

        public Builder DISP_ORDER(int DISP_ORDER) {
            this.DISP_ORDER = DISP_ORDER;
            return this;
        }

        public VO_SUBTITLE_PER_LANG build() {
            return new VO_SUBTITLE_PER_LANG(this);
        }
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getVOCA() {
        return VOCA;
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }

    public String getVOCA_WITHOUT_PUNCTUATION() {
        return VOCA_WITHOUT_PUNCTUATION;
    }

    public void setVOCA_WITHOUT_PUNCTUATION(String VOCA_WITHOUT_PUNCTUATION) {
        this.VOCA_WITHOUT_PUNCTUATION = VOCA_WITHOUT_PUNCTUATION;
    }

    public int getUSED() {
        return USED;
    }

    public void setUSED(int USED) {
        this.USED = USED;
    }

    public int getUPDATABLE() {
        return UPDATABLE;
    }

    public void setUPDATABLE(int UPDATABLE) {
        this.UPDATABLE = UPDATABLE;
    }

    public int getSUBTITLE_FILE_ID() {
        return SUBTITLE_FILE_ID;
    }

    public void setSUBTITLE_FILE_ID(int SUBTITLE_FILE_ID) {
        this.SUBTITLE_FILE_ID = SUBTITLE_FILE_ID;
    }

    public int getDISP_ORDER() {
        return DISP_ORDER;
    }

    public void setDISP_ORDER(int DISP_ORDER) {
        this.DISP_ORDER = DISP_ORDER;
    }
}

