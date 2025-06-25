package com.dalread.util.arasubtitle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SamiObject implements SubtitleObject{
    private List<SubtitleCue> cues;
    private Set<String> languages;
    private Map<Property, Object> properties;
    private String contentsInStyleTag;
    public SamiObject() {
        this.cues = new ArrayList<SubtitleCue>();
        this.properties = new HashMap<>();
        this.contentsInStyleTag = "";
        this.languages = new HashSet<String>();
    }

    public void addContentsInStyleTag(String contentsInStyleTag) {
        this.contentsInStyleTag = contentsInStyleTag;
    }

    public String getContentsInStyleTag() {
        return this.contentsInStyleTag;
    }

    public void addCue(SubtitleCue cue) {
        this.cues.add(cue);
    }

    public List<SubtitleCue> getCues() {
        return this.cues;
    }


//	    public void addLanguages(String language) {
//	        this.languages.add(language);
//	    }

    public void addLanguages(String language) {
        this.languages.add(language);
    }

    public Set<String> getLanguages() {
        return this.languages;
    }
    public void setCues(List<SubtitleCue> cues) {
        this.cues = cues;
    }

    @Override
    public Object getProperty(Property property) {
        return this.properties.get(property);
    }

    @Override
    public boolean hasProperty(Property property) {
        return (this.getProperty(property) != null);
    }

    @Override
    public Map<Property, Object> getProperties() {
        return this.properties;
    }
    public void setProperty(Property property, Object value) {
        this.properties.put(property, value);
    }


    public void setProperties(Map<Property, Object> properties) {
        this.properties = properties;
    }


}