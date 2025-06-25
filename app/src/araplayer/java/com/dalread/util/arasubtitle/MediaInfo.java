package com.dalread.util.arasubtitle;

import org.apache.commons.lang3.math.NumberUtils;

public class MediaInfo  {
    private static String title;
    private static String titleTts;
    private static String artist;
    private static String artistTts;
    private static String album;
    private static String lyricCreator;
    private static String lyricFileCreator;
    private static String lyricFileEditor;
    private static int length;
    private static String version;

    public MediaInfo() {
    }
    public void parseMediaHeaderLine(String line) {
        if  (line.toLowerCase().startsWith( "[ti:" )) {  //타이틀
            String headerInfo = line.substring ( 4 , line.length ()  -1 );
            setTitle(headerInfo);
        } else if (line.toLowerCase().startsWith( "[titts:" )) {
            String headerInfo = line.substring ( 7 , line.length ()  -1 );
            setTitleTts(headerInfo);
        } else if (line.toLowerCase().startsWith( "[ar:" )) {  //아티스트
            String headerInfo = line.substring ( 4 , line.length ()  -1 );
            setArtist(headerInfo);
        } else if (line.toLowerCase().startsWith( "[artts:" )) {
            String headerInfo = line.substring ( 7 , line.length ()  -1 );
            setArtistTts(headerInfo);
        } else if (line.toLowerCase().startsWith( "[au:" )) {  //가사 작성자
            String headerInfo = line.substring ( 4 , line.length ()  -1 );
            setLyricCreator(headerInfo);
        } else if (line.toLowerCase().startsWith( "[by:" )) {  //LRC파일의 작성자
            String headerInfo = line.substring ( 4 , line.length ()  -1 );
            setLyricFileCreator(headerInfo);
        } else if (line.toLowerCase().startsWith( "[ve:" )) {  //프로그램 버전
            String headerInfo = line.substring ( 4 , line.length ()  -1 );
            setVersion(headerInfo);
        } else if (line.toLowerCase().startsWith( "[re:" )) {  //LRC를 작성한 플레이어나 편집기
            String headerInfo = line.substring ( 4 , line.length ()  -1 );
            setLyricFileEditor(headerInfo);
        } else if (line.toLowerCase().startsWith( "[length:" )) {  //음악의 길이
            String headerInfo = line.substring ( 8 , line.length ()  -1 );
            if (NumberUtils.isNumber(headerInfo))
                setLength(Integer.parseInt(headerInfo));
        }

    }

    public String getTitle() {
        return title == null ? "" : title.trim();
    }

    public void setTitle(String title) {
        MediaInfo.title = title;
    }

    public String getTitleTts() {
        return titleTts == null ? "" : titleTts.trim();
    }

    public void setTitleTts(String titleTts) {
        MediaInfo.titleTts = titleTts;
    }

    public String getArtist() {
        return artist == null ? "" : artist.trim();
    }

    public void setArtist(String artist) {
        MediaInfo.artist = artist;
    }

    public String getArtistTts() {
        return artistTts == null ? "" : artistTts.trim();
    }

    public void setArtistTts(String artistTts) {
        MediaInfo.artistTts = artistTts;
    }

    public String getAlbum() {
        return album == null ? "" : album.trim();
    }

    public void setAlbum(String album) {
        MediaInfo.album = album;
    }

    public String getLyricCreator() {
        return lyricCreator == null ? "" : lyricCreator.trim();
    }

    public void setLyricCreator(String lyricCreator) {
        MediaInfo.lyricCreator = lyricCreator;
    }

    public String getLyricFileCreator() {
        return lyricFileCreator == null ? "" : lyricFileCreator.trim();
    }

    public void setLyricFileCreator(String lyricFileCreator) {
        MediaInfo.lyricFileCreator = lyricFileCreator;
    }

    public String getLyricFileEditor() {
        return lyricFileEditor == null ? "" : lyricFileEditor.trim();
    }

    public void setLyricFileEditor(String lyricFileEditor) {
        MediaInfo.lyricFileEditor = lyricFileEditor;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        MediaInfo.length = length;
    }

    public String getVersion() {
        return version == null ? "" : version.trim();
    }

    public void setVersion(String version) {
        MediaInfo.version = version;
    }
}