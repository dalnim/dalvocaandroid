package com.dalread.model;

import com.dalread.R;
import com.dalread.util.Constant;
import com.dalread.util.Utils;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by JetVHS on 6/21/17.
 */

public class ReadingListModel extends RealmObject {
    @PrimaryKey
    private long id;
    private String uId;
    private String title;
    private String content;
    private String md5;
    private String allWordsCount;
    private String knownWordsCount;
    private String unknownWordsCount;
    private String bookmarkedWordsCount;
    private String modDate;
    private String langStudy;

    @Ignore
    private int coverImage;
    @Ignore
    private int levelImage;

    public ReadingListModel() {
        this(0, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK, Constant.BASE_BLANK);
    }

    public ReadingListModel(long id, String uId, String title, String content, String md5, String allWordsCount, String knownWordsCount, String unknownWordsCount, String bookmarkedWordsCount, String modDate, String langStudy) {
        this.id = id;
        this.uId = uId;
        this.title = title;
        this.content = content;
        this.md5 = md5;
        this.allWordsCount = allWordsCount;
        this.knownWordsCount = knownWordsCount;
        this.unknownWordsCount = unknownWordsCount;
        this.bookmarkedWordsCount = bookmarkedWordsCount;
        this.modDate = modDate;
        this.langStudy = langStudy;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getAllWordsCount() {
        return allWordsCount;
    }

    public int getAllWordsCountInt() {
        return Utils.parseInt(allWordsCount);
    }

    public void setAllWordsCount(String allWordsCount) {
        this.allWordsCount = allWordsCount;
    }

    public String getKnownWordsCount() {
        return knownWordsCount;
    }

    public int getKnownWordsCountInt() {
        return Utils.parseInt(knownWordsCount);
    }

    public void setKnownWordsCount(String knownWordsCount) {
        this.knownWordsCount = knownWordsCount;
    }

    public String getUnknownWordsCount() {
        return unknownWordsCount;
    }

    public int getUnknownWordsCountInt() {
        return Utils.parseInt(unknownWordsCount);
    }

    public void setUnknownWordsCount(String unknownWordsCount) {
        this.unknownWordsCount = unknownWordsCount;
    }

    public String getBookmarkedWordsCount() {
        return bookmarkedWordsCount;
    }

    public int getBookmarkedWordsCountInt() {
        return Utils.parseInt(bookmarkedWordsCount);
    }

    public void setBookmarkedWordsCount(String bookmarkedWordsCount) {
        this.bookmarkedWordsCount = bookmarkedWordsCount;
    }

    public String getModDate() {
        return modDate;
    }

    public void setModDate(String modDate) {
        this.modDate = modDate;
    }

    public String getLangStudy() {
        return langStudy;
    }

    public void setLangStudy(String langStudy) {
        this.langStudy = langStudy;
    }

    public int getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(int coverImage) {
        this.coverImage = coverImage;
    }

    public int getLevelImage() {
        return levelImage;
    }

    public void setLevelImage(int levelImage) {
        this.levelImage = levelImage;
    }

    public int parserLevelImage() {
        final int difficulty = (int) (((float) getKnownWordsCountInt() / getAllWordsCountInt()) * 100);
        if (difficulty > 90) {
            levelImage = R.mipmap.book_very_easy_level;
        } else if (difficulty > 80) {
            levelImage = R.mipmap.book_easy_level;
        } else if (difficulty > 70) {
            levelImage = R.mipmap.book_good_level;
        } else if (difficulty > 60) {
            levelImage = R.mipmap.book_hard_level;
        } else if (difficulty > 0) {
            levelImage = R.mipmap.book_very_hard_level;
        } else {
            levelImage = R.mipmap.book_unknown_level;
        }
        return levelImage;
    }

    public int parserCoverImage() {
        final int difficulty = (int) (((float) getKnownWordsCountInt() / getAllWordsCountInt()) * 100);
        if (difficulty > 90) {
            coverImage = R.mipmap.book_very_easy;
        } else if (difficulty > 80) {
            coverImage = R.mipmap.book_easy;
        } else if (difficulty > 70) {
            coverImage = R.mipmap.book_good;
        } else if (difficulty > 60) {
            coverImage = R.mipmap.book_hard;
        } else if (difficulty > 0) {
            coverImage = R.mipmap.book_very_hard;
        } else {
            coverImage = R.mipmap.book_unknown;
        }
        return coverImage;
    }

    public String getWordsString() {
        String value = "";
        value += knownWordsCount + "/" + allWordsCount + "(" + (int) (((float) getKnownWordsCountInt() / getAllWordsCountInt()) * 100) + "%)";
        return value;
    }

  @Override
    public String toString() {
        return "ReadingListModel{" +
                "id=" + id +
                ", uId='" + uId + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", md5='" + md5 + '\'' +
                ", allWordsCount='" + allWordsCount + '\'' +
                ", knownWordsCount='" + knownWordsCount + '\'' +
                ", unknownWordsCount='" + unknownWordsCount + '\'' +
                ", bookmarkedWordsCount='" + bookmarkedWordsCount + '\'' +
                ", modDate='" + modDate + '\'' +
                ", langStudy='" + langStudy + '\'' +
                '}';
    }
}
