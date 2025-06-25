package com.dalread.util.arasubtitle;

import java.util.ArrayList;
import java.util.List;

public class SamiCue implements SubtitleCue{
    private String id; // Id of cue. 1 or c1
    private String language;   //DalMovie에서 사용할폼. ENGLISH, KOREAN...
    private Integer languageCode; //English = 13, Korean = 3 etc..
    private String languageSMIForm;   //smi에서 사용됨. KRCC, ENCC
    private SubtitleTimeCode startTime; // Start displaying the cue at this time code
    private SubtitleTimeCode endTime; // Stop displaying the cue at this time code
    private List<ISubtitleLine> lines; // Lines composed of texts

    protected SamiCue(SubtitleCue cue) {
        this.id = cue.getId();
        this.startTime = cue.getStartTime();
        this.endTime = cue.getEndTime();
        this.lines = new ArrayList<>(cue.getLines());
    }

    protected SamiCue() {
        this.lines = new ArrayList<>();
    }

    protected SamiCue(SubtitleTimeCode startTime, SubtitleTimeCode endTime) {
        this.lines = new ArrayList<>();
        this.startTime = startTime;
        this.endTime = endTime;
    }

    protected SamiCue(SubtitleTimeCode startTime, SubtitleTimeCode endTime, List<ISubtitleLine> lines) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.lines = lines;
    }

    protected SamiCue(SubtitleTimeCode startTime, SubtitleTimeCode endTime, List<ISubtitleLine> lines, String language) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.lines = lines;
        this.language = language;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SubtitleTimeCode getStartTime() {
        return this.startTime;
    }

    public void setStartTime(SubtitleTimeCode startTime) {
        this.startTime = startTime;
    }

    public SubtitleTimeCode getEndTime() {
        return this.endTime;
    }

    public void setEndTime(SubtitleTimeCode endTime) {
        this.endTime = endTime;
    }

    public List<ISubtitleLine> getLines() {
        return this.lines;
    }

    public void setLines(List<ISubtitleLine> lines) {
        this.lines = lines;
    }

    public void addLine(ISubtitleLine line) {
        this.lines.add(line);
    }

    public void subtractTime(SubtitleTimeCode toSubtract) {
        this.setStartTime(this.getStartTime().subtract(toSubtract));
        this.setEndTime(this.getEndTime().subtract(toSubtract));
    }

    public String getText() {
        String[] texts = new String[this.lines.size()];

        for (int i=0; i<texts.length; i++) {
            texts[i] = this.lines.get(i).toString();
        }

        return String.join("\n", texts);
    }

    @Override
    public String toString() {
        return this.getText();
    }


    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String getLanguage() {
        // TODO Auto-generated method stub
        return this.language;
    }

    public void setLanguageSMIForm(String languageSMIForm) {
        this.languageSMIForm = languageSMIForm;
    }

    public String getLanguageSMIForm() {
        // TODO Auto-generated method stub
        return this.languageSMIForm;
    }
    public void setLanguageCode(Integer languageCode) {
        this.languageCode = languageCode;
    }

    public Integer getLanguageCode() {
        // TODO Auto-generated method stub
        return this.languageCode;
    }

}