package com.dalread.model;

import com.dalread.util.Constant;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchVideoModel {
    private int page;
    @SerializedName("total_results")
    private int totalResult;
    @SerializedName("total_pages")
    private int totalPages;
    private List<VideoInformationModel> results;

    public SearchVideoModel() {
        this(0, 0, 0, new ArrayList<>());
    }

    public SearchVideoModel(int page, int totalResult, int totalPages, List<VideoInformationModel> results) {
        this.page = page;
        this.totalResult = totalResult;
        this.totalPages = totalPages;
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResult() {
        return totalResult;
    }

    public void setTotalResult(int totalResult) {
        this.totalResult = totalResult;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<VideoInformationModel> getResults() {
        return results;
    }

    public void setResults(List<VideoInformationModel> results) {
        this.results = results;
    }

    public void add(SearchVideoModel items) {
        add(items, true);
    }

    public void add(List<VideoInformationModel> seasons) {
        this.page = 1;
        this.totalPages = 1;
        results.clear();
        results.addAll(seasons);
    }

    public void add(SearchVideoModel items, boolean isLoadMore) {
        items.setResults(items.getResults().stream().filter(e -> !e.getMediaType().equalsIgnoreCase(Constant.PLAYER.THE_MOVIE_DB.KEY_PERSON)).collect(Collectors.toList()));
        this.page = items.getPage();
        this.totalPages = items.getTotalPages();
        generateData(items.getResults());
        if (isLoadMore) {
            results.addAll(items.getResults());
        } else {
            results.clear();
            results.addAll(items.getResults());
        }
    }

    public void generateData(List<VideoInformationModel> items) {
        if (items != null && !items.isEmpty()) {
            for (VideoInformationModel item : items) {
                item.generateDateVideo();
            }
        }
    }

    @Override
    public String toString() {
        return "SearchVideoModel{" +
                "page=" + page +
                ", totalResult=" + totalResult +
                ", totalPages=" + totalPages +
                ", results=" + results +
                '}';
    }
}
