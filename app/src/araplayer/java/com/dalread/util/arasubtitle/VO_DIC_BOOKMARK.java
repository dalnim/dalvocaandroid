package com.dalread.util.arasubtitle;

import java.util.Date;

public class VO_DIC_BOOKMARK extends VO_COMMON_VOCA {
    private Integer ID;
    private Integer COUNT;
    private Date REG_DATE;

    public static class Builder {
        private VO_DIC_BOOKMARK instance;

        public Builder() {
            instance = new VO_DIC_BOOKMARK();
        }

        public Builder ID(Integer ID) {
            instance.ID = ID;
            return this;
        }

        public Builder COUNT(Integer COUNT) {
            instance.COUNT = COUNT;
            return this;
        }

        public Builder REG_DATE(Date REG_DATE) {
            instance.REG_DATE = REG_DATE;
            return this;
        }

        public VO_DIC_BOOKMARK build() {
            return instance;
        }
    }

    public void setID(Integer value) {
        ID = (value == null) ? 0 : value;
    }

    public void setCOUNT(Integer value) {
        COUNT = (value == null) ? 0 : value;
    }

    public void setREG_DATE(Date REG_DATE) {
        this.REG_DATE = REG_DATE;
    }

    public Integer getID() {
        return ID;
    }

    public Integer getCOUNT() {
        return COUNT;
    }

    public Date getREG_DATE() {
        return REG_DATE;
    }
}
