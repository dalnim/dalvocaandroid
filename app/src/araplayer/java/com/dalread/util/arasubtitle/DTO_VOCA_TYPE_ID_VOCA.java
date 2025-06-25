package com.dalread.util.arasubtitle;

public class DTO_VOCA_TYPE_ID_VOCA extends DTO_VOCA_TYPE_ID {
    protected String VOCA = "";

    public static class Builder {
        private DTO_VOCA_TYPE_ID_VOCA instance;

        public Builder() {
            instance = new DTO_VOCA_TYPE_ID_VOCA();
        }

        public Builder VOCA(String VOCA) {
            instance.VOCA = VOCA;
            return this;
        }

        public Builder VOCA_ID(Integer VOCA_ID) {
            instance.VOCA_ID = VOCA_ID;
            return this;
        }

        public Builder VOCA_TYPE(Integer VOCA_TYPE) {
            instance.VOCA_TYPE = VOCA_TYPE;
            return this;
        }

        public DTO_VOCA_TYPE_ID_VOCA build() {
            return instance;
        }
    }

    public String getVOCA() {
        return VOCA;
    }

    public void setVOCA(String VOCA) {
        this.VOCA = VOCA;
    }
}


