package com.dalread.util.arasubtitle;

import java.util.List;
import java.util.Map;

public interface SubtitleObject {
    // Properties
    public enum Property {
        TITLE,
        DESCRIPTION,
        COPYRIGHT,
        FRAME_RATE;
    }

    public boolean hasProperty(Property property);
    public Object getProperty(Property property);
    public Map<Property, Object> getProperties();
    public List<SubtitleCue> getCues();
}