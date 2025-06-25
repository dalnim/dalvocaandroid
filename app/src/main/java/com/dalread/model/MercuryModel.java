package com.dalread.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by JetVHS on 3/4/2017.
 */
public class MercuryModel {
    private String title;
    private String author;
    @SerializedName("date_published")
    private String datePublished;
    private String dek;
    @SerializedName("lead_image_url")
    private String leadImageUrl;
    private String content;
    @SerializedName("next_page_url")
    private String nextPageUrl;
    private String url;
    private String domain;
    private String excerpt;
    @SerializedName("word_count")
    private String wordCount;
    private String direction;
    @SerializedName("total_pages")
    private String totalPages;
    @SerializedName("rendered_pages")
    private String renderedPages;
    private String errorMessage;

    public MercuryModel() {

    }

    public MercuryModel(String title, String author, String datePublished, String dek, String leadImageUrl, String content, String nextPageUrl, String url, String domain, String excerpt, String wordCount, String direction, String totalPages, String renderedPages) {
        this.title = title;
        this.author = author;
        this.datePublished = datePublished;
        this.dek = dek;
        this.leadImageUrl = leadImageUrl;
        this.content = content;
        this.nextPageUrl = nextPageUrl;
        this.url = url;
        this.domain = domain;
        this.excerpt = excerpt;
        this.wordCount = wordCount;
        this.direction = direction;
        this.totalPages = totalPages;
        this.renderedPages = renderedPages;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getDek() {
        return dek;
    }

    public void setDek(String dek) {
        this.dek = dek;
    }

    public String getLeadImageUrl() {
        return leadImageUrl;
    }

    public void setLeadImageUrl(String leadImageUrl) {
        this.leadImageUrl = leadImageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getWordCount() {
        return wordCount;
    }

    public void setWordCount(String wordCount) {
        this.wordCount = wordCount;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public String getRenderedPages() {
        return renderedPages;
    }

    public void setRenderedPages(String renderedPages) {
        this.renderedPages = renderedPages;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "MercuryModel{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", datePublished='" + datePublished + '\'' +
                ", dek='" + dek + '\'' +
                ", leadImageUrl='" + leadImageUrl + '\'' +
                ", content='" + content + '\'' +
                ", nextPageUrl='" + nextPageUrl + '\'' +
                ", url='" + url + '\'' +
                ", domain='" + domain + '\'' +
                ", excerpt='" + excerpt + '\'' +
                ", wordCount='" + wordCount + '\'' +
                ", direction='" + direction + '\'' +
                ", totalPages='" + totalPages + '\'' +
                ", renderedPages='" + renderedPages + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
