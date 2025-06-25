package com.dalread.model;

import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.Utils;

import java.util.Objects;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class SubModel extends RealmObject {
    @PrimaryKey
    private long id;
    private long start;
    private long end;
    private String content;
    private String contentRuby;
    private String path;
    private int language;
    @Ignore private String ids;
    @Ignore private String meaningWords;
    @Ignore private int position;
    @Ignore private long startTimeOriginal;
    @Ignore private long endTimeOriginal;


    public SubModel() {
        this(0, 0, Constant.BASE_BLANK);
    }

    public SubModel(long start, long end, String path) {
        this(start, end, Constant.BASE_BLANK, path, 0);
    }

    public SubModel(long start, long end, String content, String path, int language) {
        this(start, end, content, Constant.BASE_BLANK, path, language);
    }

    public SubModel(long start, long end, String content, String contentRuby, String path, int language) {
        this.start = start;
        this.end = end;
        this.content = content;
        this.contentRuby = contentRuby;
        this.path = path;
        this.language = language;
    }

    public SubModel(SubModel subModel) {
        this.id = subModel.getId();
        this.start = subModel.getStart();
        this.end = subModel.getEnd();
        this.content = subModel.getContent();
        this.contentRuby = subModel.getContentRuby();
        this.path = subModel.getPath();
        this.language = subModel.getLanguage();
        this.ids = subModel.getIds();
        this.meaningWords = subModel.getMeaningWords();
        this.startTimeOriginal = subModel.getStartTimeOriginal();
        this.endTimeOriginal = subModel.getEndTimeOriginal();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
    }

    public String getContentRuby() {
        return contentRuby;
    }

    public void setContentRuby(String contentRuby) {
        this.contentRuby = contentRuby;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubModel that = (SubModel) o;
        return Objects.equals(start, that.start);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start);
    }

    public SubModel merge(SubModel other) {
        assert(this.equals(other));
        String displayRuby = getContentRuby();
        if (!Utils.isEmpty(other.getContentRuby())) {
            if (!Utils.isEmpty(displayRuby)) {
                displayRuby += Constant.RUBY.KEY.BREAK_BR_START;
            }
            displayRuby += other.getContentRuby();
        }
        other.setContentRuby(displayRuby);

        String display = getContent();
        if (!Utils.isEmpty(other.getContent())) {
            if (!Utils.isEmpty(display)) {
                display += "\n";
            }
            display += other.getContent();
        }
        other.setContent(display);
        other.setIds(getId() + "," + other.getId());

        return other;
    }

    public boolean updateDisplayRubyText(int vocaId, String key, int oldValue, int newValue) {
        String displayRubyText = getContentRuby();
        if (!Utils.isEmpty(displayRubyText)) {
            try {
                String wordId = Constant.RUBY.KEY.VOCA_ID + "=" + vocaId + " ";
                int startIndex = displayRubyText.indexOf(wordId);
                int endIndex = displayRubyText.indexOf(key, startIndex);
                endIndex = displayRubyText.indexOf(" ", endIndex);
                if (startIndex >= 0 && endIndex > 0) {
                    String oldString = displayRubyText.substring(startIndex, endIndex);
                    String oldValueString = oldString.substring(oldString.indexOf(key + "="));
                    String newValueString = key + "=" + newValue;
                    String newString = oldString.replace(oldValueString, newValueString);
                    setContentRuby(displayRubyText.replace(oldString, newString));
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean updateDisplayRubyText(int vocaId, String key, String oldValue, String newValue) {
        String displayRubyText = getContentRuby();
        if (!Utils.isEmpty(displayRubyText)) {
            try {
                String wordId = Constant.RUBY.KEY.VOCA_ID + "=" + vocaId + " ";
                int startIndex = displayRubyText.indexOf(wordId);
                int endIndex = displayRubyText.indexOf("<ruby>", startIndex);
                if (startIndex >= 0 && endIndex > 0) {
                    String oldString = displayRubyText.substring(startIndex, endIndex);
                    String oldValueString = key + "=\"" + oldValue + "\"";
                    String newValueString = key + "=\"" + newValue + "\"";
                    String newString = oldString.replace(oldValueString, newValueString);
                    setContentRuby(displayRubyText.replace(oldString, newString));
                    DLog.d("", "updateDisplayRubyText - oldValueString=" + oldValueString + " - newValueString=" + newValueString);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getMeaningWords() {
        return meaningWords;
    }

    public void setMeaningWords(String meaningWords) {
        this.meaningWords = meaningWords;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public long getStartTimeOriginal() {
        return startTimeOriginal;
    }

    public void setStartTimeOriginal(long startTimeOriginal) {
        this.startTimeOriginal = startTimeOriginal;
    }

    public long getEndTimeOriginal() {
        return endTimeOriginal;
    }

    public void setEndTimeOriginal(long endTimeOriginal) {
        this.endTimeOriginal = endTimeOriginal;
    }

    @Override
    public String toString() {
        return "SubModel{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", content='" + content + '\'' +
                ", contentRuby='" + contentRuby + '\'' +
                ", path='" + path + '\'' +
                ", language='" + language + '\'' +
                ", ids='" + ids + '\'' +
                '}';
    }
}
