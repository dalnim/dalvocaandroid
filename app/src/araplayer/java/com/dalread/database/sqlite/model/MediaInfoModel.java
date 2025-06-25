package com.dalread.database.sqlite.model;

public class MediaInfoModel {
    private String title;
    private String titleTts;
    private String artist;
    private String artistTts;
    private String album;
    private String lyricCreator;
    private String lyricFileCreator;
    private String lyricFileEditor;
    private int length;
    private String version;

    public MediaInfoModel() {
    }

    public MediaInfoModel(String title, String titleTts, String artist, String artistTts, String album, String lyricCreator, String lyricFileCreator, String lyricFileEditor, int length, String version) {
        this.title = title;
        this.titleTts = titleTts;
        this.artist = artist;
        this.artistTts = artistTts;
        this.album = album;
        this.lyricCreator = lyricCreator;
        this.lyricFileCreator = lyricFileCreator;
        this.lyricFileEditor = lyricFileEditor;
        this.length = length;
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleTts() {
        return titleTts;
    }

    public void setTitleTts(String titleTts) {
        this.titleTts = titleTts;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistTts() {
        return artistTts;
    }

    public void setArtistTts(String artistTts) {
        this.artistTts = artistTts;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLyricCreator() {
        return lyricCreator;
    }

    public void setLyricCreator(String lyricCreator) {
        this.lyricCreator = lyricCreator;
    }

    public String getLyricFileCreator() {
        return lyricFileCreator;
    }

    public void setLyricFileCreator(String lyricFileCreator) {
        this.lyricFileCreator = lyricFileCreator;
    }

    public String getLyricFileEditor() {
        return lyricFileEditor;
    }

    public void setLyricFileEditor(String lyricFileEditor) {
        this.lyricFileEditor = lyricFileEditor;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
