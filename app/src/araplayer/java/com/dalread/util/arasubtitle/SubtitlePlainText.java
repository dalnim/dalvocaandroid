package com.dalread.util.arasubtitle;

public class SubtitlePlainText implements SubtitleText {
    private String text; // Text to display

    public SubtitlePlainText(String text) {
        this.text = text;
    }

    @Override
    public boolean isEmpty() {
        return this.text.isEmpty();
    }

    @Override
    public String toString() {
        return this.text;
    }
}