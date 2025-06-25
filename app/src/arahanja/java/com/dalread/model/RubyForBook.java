package com.dalread.model;

public class RubyForBook {
    private String voca;
    private String difficultWords;
    private String meaning;
    private String meaningDetailed;

    public RubyForBook(String voca, String difficultWords, String meaning, String meaningDetailed) {
        this.voca = voca;
        this.difficultWords = difficultWords;
        this.meaning = meaning;
        this.meaningDetailed = meaningDetailed;
    }

    public String getVoca() {
        return voca;
    }

    public String getDifficultWords() {
        return difficultWords;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getMeaningDetailed() {
        return meaningDetailed;
    }
}
