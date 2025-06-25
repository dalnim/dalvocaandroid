package com.dalread.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Lyric {
    private static final String TAG = Lyric.class.getSimpleName();

    public String title;
    public String titleTts;
    public String artist;
    public String artistTts;
    public String album;
    public String by;
    public String author;
    public int offset;
    public long length;
    public List<Sentence> sentenceList = new ArrayList<Sentence>(100);

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Title: " + title + "\n")
                .append("TitleTts: " + titleTts + "\n")
                .append("Artist: " + artist + "\n")
                .append("ArtistTts: " + artistTts + "\n")
                .append("Album: " + album + "\n")
                .append("By: " + by + "\n")
                .append("Author: " + author + "\n")
                .append("Length: " + length + "\n")
                .append("Offset: " + offset + "\n");
        if (sentenceList != null) {
            for (Sentence sentence : sentenceList) {
                stringBuilder.append(sentence.toString() + "\n");
            }
        }
        return stringBuilder.toString();
    }

    public void addSentence(String content, long time) {
        sentenceList.add(new Sentence(content, time));
    }

    public static class SentenceComparator implements Comparator<Sentence> {
        @Override
        public int compare(Sentence sent1, Sentence sent2) {
            return (int) (sent1.fromTime - sent2.fromTime);
        }
    }

    public class Sentence {
        public String content;
        public long fromTime;

        public Sentence(String content, long fromTime) {
            this.content = content;
            this.fromTime = fromTime;
        }

        public String toString() {
            return String.valueOf(fromTime) + ": " + content;
        }
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public String getTitleTts() {
        return titleTts == null ? "" : titleTts;
    }

    public String getArtist() {
        return artist == null ? "" : artist;
    }

    public String getArtistTts() {
        return artistTts == null ? "" : artistTts;
    }

    public String getAlbum() {
        return album == null ? "" : album;
    }

    public String getBy() {
        return by == null ? "" : by;
    }

    public String getAuthor() {
        return author == null ? "" : author;
    }

    public int getOffset() {
        return offset;
    }

    public long getLength() {
        return length;
    }

    public List<Sentence> getSentenceList() {
        return sentenceList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleTts(String titleTts) {
        this.titleTts = titleTts;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setArtistTts(String artistTts) {
        this.artistTts = artistTts;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setLength(long length) {
        this.length = length;
    }
}