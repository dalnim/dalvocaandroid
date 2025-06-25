package com.dalread.util.arasubtitle;

public class DTO_VOCA_POS extends DTO_VOCA_TYPE_ID_VOCA {
    protected String POSALL = "";

    public static class Builder {
        private DTO_VOCA_POS instance;

        public Builder() {
            instance = new DTO_VOCA_POS();
        }

        public Builder VOCA(String VOCA) {
            instance.VOCA = VOCA;
            return this;
        }

        public Builder POSALL(String POSALL) {
            instance.POSALL = POSALL;
            return this;
        }

        public DTO_VOCA_POS build() {
            return instance;
        }
    }

    public String getPOSALL() {
        return POSALL;
    }

    public void setPOSALL(String POSALL) {
        this.POSALL = POSALL;
    }
}



