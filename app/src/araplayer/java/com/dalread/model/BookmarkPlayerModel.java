package com.dalread.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BookmarkPlayerModel extends RealmObject {
    @PrimaryKey
    private long id;
    private String path;
    private int index;
    private long start;
    private long end;
    private String content;
    private boolean isSelected;

    public BookmarkPlayerModel() {
    }

    public BookmarkPlayerModel(String path, int index, long start, long end, String content) {
        this(0, path, index, start, end, content);
    }

    public BookmarkPlayerModel(long id, String path, int index, long start, long end, String content) {
        this.id = id;
        this.path = path;
        this.index = index;
        this.start = start;
        this.end = end;
        this.content = content;
    }

    public BookmarkPlayerModel(BookmarkPlayerModel bookmarkPlayerModel) {
        this(bookmarkPlayerModel, bookmarkPlayerModel.isSelected);
    }

    public BookmarkPlayerModel(BookmarkPlayerModel bookmarkPlayerModel, boolean isSelected) {
        this.id = bookmarkPlayerModel.getId();
        this.path = bookmarkPlayerModel.getPath();
        this.index = bookmarkPlayerModel.getIndex();
        this.start = bookmarkPlayerModel.getStart();
        this.end = bookmarkPlayerModel.getEnd();
        this.content = bookmarkPlayerModel.getContent();
        this.isSelected = isSelected;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "BookmarkPlayerModel{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", index=" + index +
                ", start=" + start +
                ", end=" + end +
                ", content='" + content + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
