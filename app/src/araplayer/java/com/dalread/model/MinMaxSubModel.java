package com.dalread.model;

public class MinMaxSubModel {
    private long minSub;
    private long maxSub;
    private long minSubNoExtraTime;
    private long maxSubNoExtraTime;
    private long minSubWithAllExtraTime;
    private long maxSubWithAllExtraTime;

    public MinMaxSubModel(long minSub, long maxSub, long minSubNoExtraTime, long maxSubNoExtraTime, long minSubWithAllExtraTime, long maxSubWithAllExtraTime) {
        this.minSub = minSub;
        this.maxSub = maxSub;
        this.minSubNoExtraTime = minSubNoExtraTime;
        this.maxSubNoExtraTime = maxSubNoExtraTime;
        this.minSubWithAllExtraTime = minSubWithAllExtraTime;
        this.maxSubWithAllExtraTime = maxSubWithAllExtraTime;
    }

    public long getMinSub() {
        return minSub;
    }

    public void setMinSub(long minSub) {
        this.minSub = minSub;
    }

    public long getMaxSub() {
        return maxSub;
    }

    public void setMaxSub(long maxSub) {
        this.maxSub = maxSub;
    }

    public long getMinSubNoExtraTime() {
        return minSubNoExtraTime;
    }

    public void setMinSubNoExtraTime(long minSubNoExtraTime) {
        this.minSubNoExtraTime = minSubNoExtraTime;
    }

    public long getMaxSubNoExtraTime() {
        return maxSubNoExtraTime;
    }

    public void setMaxSubNoExtraTime(long maxSubNoExtraTime) {
        this.maxSubNoExtraTime = maxSubNoExtraTime;
    }

    public long getMinSubWithAllExtraTime() {
        return minSubWithAllExtraTime;
    }

    public void setMinSubWithAllExtraTime(long minSubWithAllExtraTime) {
        this.minSubWithAllExtraTime = minSubWithAllExtraTime;
    }

    public long getMaxSubWithAllExtraTime() {
        return maxSubWithAllExtraTime;
    }

    public void setMaxSubWithAllExtraTime(long maxSubWithAllExtraTime) {
        this.maxSubWithAllExtraTime = maxSubWithAllExtraTime;
    }
}
