package com.dalread.database.sqlite.model;

import com.dalread.model.IPlaylistDisplay;

/**
 * 멀티플레이어 전용 플레이리스트 POJO. SQLite playlist / playlist_item 테이블 매핑.
 * Realm 미사용.
 */
public class MultiPlayerPlaylistModel implements IPlaylistDisplay {
    private long id;
    private String name;
    private int filePathCount;

    public MultiPlayerPlaylistModel() {
        this.name = "";
    }

    public MultiPlayerPlaylistModel(long id, String name, int filePathCount) {
        this.id = id;
        this.name = name != null ? name : "";
        this.filePathCount = filePathCount;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name : "";
    }

    public int getFilePathCount() {
        return filePathCount;
    }

    public void setFilePathCount(int filePathCount) {
        this.filePathCount = filePathCount;
    }

    @Override
    public long getPlayListId() {
        return id;
    }
}
