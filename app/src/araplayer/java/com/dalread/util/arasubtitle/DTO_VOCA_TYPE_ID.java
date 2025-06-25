package com.dalread.util.arasubtitle;

public class DTO_VOCA_TYPE_ID {
    protected Integer VOCA_ID = -1;
    protected Integer VOCA_TYPE = Constants.VOCA_TYPE_NONE;

    public static class Builder {
        protected DTO_VOCA_TYPE_ID instance;

        public Builder() {
            instance = new DTO_VOCA_TYPE_ID();
        }

        public Builder VOCA_ID(Integer VOCA_ID) {
            instance.VOCA_ID = VOCA_ID;
            return this;
        }

        public Builder VOCA_TYPE(Integer VOCA_TYPE) {
            instance.VOCA_TYPE = VOCA_TYPE;
            return this;
        }

        public DTO_VOCA_TYPE_ID build() {
            return instance;
        }
    }

    public Integer getVOCA_ID() {
        return VOCA_ID;
    }

    public void setVOCA_ID(Integer VOCA_ID) {
        this.VOCA_ID = VOCA_ID;
    }

    public Integer getVOCA_TYPE() {
        return VOCA_TYPE;
    }

    public void setVOCA_TYPE(Integer VOCA_TYPE) {
        this.VOCA_TYPE = VOCA_TYPE;
    }
}


