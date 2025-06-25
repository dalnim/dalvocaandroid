package com.dalread.util.arasubtitle;

public class DTO_INDEX_SUPER_BUILDER {
    protected Integer INDEX = 0;

    // Builder class for DTO_INDEX_SUPER_BUILDER
    public static class Builder<T extends Builder<T>> {
        protected DTO_INDEX_SUPER_BUILDER instance;

        public Builder() {
            instance = new DTO_INDEX_SUPER_BUILDER();
        }

        @SuppressWarnings("unchecked")
        public T INDEX(Integer INDEX) {
            instance.INDEX = INDEX;
            return (T) this;
        }

        public DTO_INDEX_SUPER_BUILDER build() {
            return instance;
        }
    }

    public Integer getINDEX() {
        return INDEX;
    }

    public void setINDEX(Integer INDEX) {
        this.INDEX = INDEX;
    }
}
