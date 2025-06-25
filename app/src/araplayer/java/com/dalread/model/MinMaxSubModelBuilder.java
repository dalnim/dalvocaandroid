package com.dalread.model;

public final class MinMaxSubModelBuilder {
    private long minSub;
    private long maxSub;
    private long minSubNoExtraTime;
    private long maxSubNoExtraTime;
    private long minSubWithAllExtraTime;
    private long maxSubWithAllExtraTime;

    private MinMaxSubModelBuilder() {
    }

    public static MinMaxSubModelBuilder aMinMaxSubModel() {
        return new MinMaxSubModelBuilder();
    }

    public MinMaxSubModelBuilder withMinSub(long minSub) {
        this.minSub = minSub;
        return this;
    }

    public MinMaxSubModelBuilder withMaxSub(long maxSub) {
        this.maxSub = maxSub;
        return this;
    }

    public MinMaxSubModelBuilder withMinSubNoExtraTime(long minSubNoExtraTime) {
        this.minSubNoExtraTime = minSubNoExtraTime;
        return this;
    }

    public MinMaxSubModelBuilder withMaxSubNoExtraTime(long maxSubNoExtraTime) {
        this.maxSubNoExtraTime = maxSubNoExtraTime;
        return this;
    }

    public MinMaxSubModelBuilder withMinSubWithAllExtraTime(long minSubWithAllExtraTime) {
        this.minSubWithAllExtraTime = minSubWithAllExtraTime;
        return this;
    }

    public MinMaxSubModelBuilder withMaxSubWithAllExtraTime(long maxSubWithAllExtraTime) {
        this.maxSubWithAllExtraTime = maxSubWithAllExtraTime;
        return this;
    }

    public MinMaxSubModel build() {
        return new MinMaxSubModel(minSub, maxSub, minSubNoExtraTime, maxSubNoExtraTime, minSubWithAllExtraTime, maxSubWithAllExtraTime);
    }
}
