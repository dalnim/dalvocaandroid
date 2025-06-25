package com.dalread.util.arasubtitle;

public class VO_COMMON_VOCA {
    private Integer VOCA_TYPE;
    private Integer VOCA_ID;
    private String VOCA;

    public static class Builder {
        private VO_COMMON_VOCA instance;

        public Builder() {
            instance = new VO_COMMON_VOCA();
        }

        public Builder VOCA_TYPE(Integer VOCA_TYPE) {
            instance.VOCA_TYPE = VOCA_TYPE;
            return this;
        }

        public Builder VOCA_ID(Integer VOCA_ID) {
            instance.VOCA_ID = VOCA_ID;
            return this;
        }

        public Builder VOCA(String VOCA) {
            instance.VOCA = VOCA;
            return this;
        }

        public VO_COMMON_VOCA build() {
            return instance;
        }
    }

    public void setVOCA_TYPE(Integer value) {
        VOCA_TYPE = (value == null) ? Constants.VOCA_TYPE_NONE : value;
    }

    public void setVOCA_ID(Integer value) {
        VOCA_ID = (value == null) ? -1 : value;
    }

    public void setVOCA(String value) {
        VOCA = (value == null) ? "" : value;
    }

    @Override
    public String toString() {
        return "VO_COMMON_VOCA{" +
                "VOCA_TYPE=" + VOCA_TYPE +
                ", VOCA_ID=" + VOCA_ID +
                ", VOCA='" + VOCA + '\'' +
                '}';
    }

    public Integer getVOCA_TYPE() {
        return VOCA_TYPE;
    }

    public Integer getVOCA_ID() {
        return VOCA_ID;
    }

    public String getVOCA() {
        return VOCA;
    }
}
