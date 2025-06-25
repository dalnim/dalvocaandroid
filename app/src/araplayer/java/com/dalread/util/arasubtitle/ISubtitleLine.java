package com.dalread.util.arasubtitle;

import java.util.List;

public interface ISubtitleLine {
    public List<SubtitleText> getTexts();

    /**
     *
     * @return true if there is no text
     */
    public boolean isEmpty();
}