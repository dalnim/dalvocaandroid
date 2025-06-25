package com.dalread.util.arasubtitle;

import java.util.ArrayList;
import java.util.List;

public class SubtitleTextLine implements ISubtitleLine {
    List<SubtitleText> texts;

    public SubtitleTextLine() {
        this.texts = new ArrayList<>();
    }

    public SubtitleTextLine(List<SubtitleText> texts) {
        this.texts = texts;
    }

    public List<SubtitleText> getTexts() {
        return this.texts;
    }

    public void addText(SubtitleText text) {
        this.texts.add(text);
    }

    public boolean isEmpty() {
        return this.toString().isEmpty();
    }

    @Override
    public String toString() {
        String[] texts = new String[this.texts.size()];

        for (int i=0; i<texts.length; i++) {
            texts[i] = this.texts.get(i).toString();
        }

        return String.join("\n", texts);
    }
}

