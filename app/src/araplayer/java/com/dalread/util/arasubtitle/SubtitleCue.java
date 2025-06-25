package com.dalread.util.arasubtitle;

import java.util.List;

public interface SubtitleCue {
    public String getId();
    public String getLanguage();
    public Integer getLanguageCode();
    public String getLanguageSMIForm();
    public SubtitleTimeCode getStartTime();
    public SubtitleTimeCode getEndTime();
    public List<ISubtitleLine> getLines();
    public String getText();
}
