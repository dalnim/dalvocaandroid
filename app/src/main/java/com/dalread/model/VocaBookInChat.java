package com.dalread.model;

import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class VocaBookInChat implements Serializable {

    @SerializedName("LANG_STUDY")
    private String langStudy;
    @SerializedName("VOCABOOK_TYPE")
    private int vocaBookType;
    @SerializedName("VOCABOOKS_ID")
    private int vocaBookId;
    @SerializedName("VOCABOOKS_CELL_INDEX")
    private int vocaBookCellIndex;
    @SerializedName(Constant.API_KEY.KEY_TOPIC_BEGIN_INDEX)
    private int topicBeginIndex;
    @SerializedName(Constant.API_KEY.KEY_TOPIC_REPEATED_COUNT)
    private int topicRepeatedCount;
    private long startTime;
    private long finishTime;
    private long duration;
    private String bookName;
    private boolean checked;

    public String getLangStudy() {
        return langStudy;
    }

    public void setLangStudy(String langStudy) {
        this.langStudy = langStudy;
    }

    public int getVocaBookType() {
        return vocaBookType;
    }

    public void setVocaBookType(int vocaBookType) {
        this.vocaBookType = vocaBookType;
    }

    public int getVocaBookId() {
        return vocaBookId;
    }

    public void setVocaBookId(int vocaBookId) {
        this.vocaBookId = vocaBookId;
    }

    public int getVocaBookCellIndex() {
        return vocaBookCellIndex;
    }

    public void setVocaBookCellIndex(int vocaBookCellIndex) {
        this.vocaBookCellIndex = vocaBookCellIndex;
    }

    public int getTopicBeginIndex() {
        return topicBeginIndex;
    }

    public void setTopicBeginIndex(int topicBeginIndex) {
        this.topicBeginIndex = topicBeginIndex;
    }

    public int getTopicRepeatedCount() {
        return topicRepeatedCount;
    }

    public void setTopicRepeatedCount(int topicRepeatedCount) {
        this.topicRepeatedCount = topicRepeatedCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void calculateDuration() {
        duration += (finishTime - startTime);
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "VocaBookInChat{" +
                "langStudy='" + langStudy + '\'' +
                ", vocaBookType=" + vocaBookType +
                ", vocaBookId=" + vocaBookId +
                ", vocaBookCellIndex=" + vocaBookCellIndex +
                ", topicBeginIndex=" + topicBeginIndex +
                ", topicRepeatedCount=" + topicRepeatedCount +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", duration=" + duration +
                ", bookName='" + bookName + '\'' +
                ", checked=" + checked +
                '}';
    }
}
